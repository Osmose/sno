#!/usr/bin/env python
# -*- coding: iso-8859-1 -*-
import sys
_ORACLE_ = "o"
_TEST_ = "t"
printWarnings = True

warnIsError = True

# Set to -1 to get all warnings
warnLimit = 100
exactMatch = False
limitDiff = 50
contextSize = 15


### not for you to touch ###
warnCount = 0;

class RingBuffer:
    def __init__(self, size):
        self.data = [None for i in xrange(size)]

    def append(self, x):
        self.data.pop(0)
        self.data.append(x)

    def get(self):
        return self.data

context = RingBuffer(contextSize)

class LogLine():
    def __init__(self, line, lineNumber):
	self.lineItems = {}
	self.lineItems['bank'] = None
	self.lineItems['address'] = None
	self.lineItems['opcode'] = None
	self.lineItems['args'] = []
	self.lineItems['mnemonic'] = None
	self.lineItems['a'] = None
	self.lineItems['x'] = None
	self.lineItems['y'] = None
	self.lineItems['dp'] = None
	self.lineItems['dbr'] = None
	self.lineItems['sp'] = None

	self.lineItems['flags'] = None
	self.wholeLine = line
	self.lineNumber = lineNumber

    def getLine(self):
	return self.wholeLine

    def getLineNumber(self):
	return str(self.lineNumber)

    def parseString(self):
	pass

    @staticmethod
    def compareLines(a, b):
	error = []
	warn = []
	if a.lineItems['bank']   != b.lineItems['bank']:   error.append('Bank');
	if a.lineItems['addr']   != b.lineItems['addr']:   error.append('Address');
	if a.lineItems['opcode'] != b.lineItems['opcode']: error.append('Opcode');

	# Compare the arguments
	aArgs = a.lineItems['args']
	bArgs = b.lineItems['args']
	if len(aArgs) != len(bArgs):
	    error.append('Argument Length');
	else:
	    for x in range(len(aArgs)):
		if aArgs[x] != bArgs[x]: error.append("Argument " + str(x));

	if a.lineItems['mnemonic'] != b.lineItems['mnemonic']: warn.append('Mnemonic');
	if a.lineItems['a'] != b.lineItems['a']:     error.append('Register A');
	if a.lineItems['x'] != b.lineItems['x']:     error.append('Register X');
	if a.lineItems['y'] != b.lineItems['y']:     error.append('Register Y');
	if a.lineItems['dp'] != b.lineItems['dp']:   error.append('Direct Page');
	if a.lineItems['dbr'] != b.lineItems['dbr']: error.append('Data Bank');
	if a.lineItems['sp'] != b.lineItems['sp']:   error.append('Stack Pointer');
	if a.lineItems['flags'] != b.lineItems['flags']: error.append('Flags Register');

	if len(error) > 0:
	    for x in warn: error.append(x);
	    return (False, error)
	else:
	    return (True, warn)


class OracleLogLine(LogLine):
    def __init__(self, string, lineNumber):
	LogLine.__init__(self, string, lineNumber)
	self.parseLine(string)

    def parseLine(self, string):
	#print string
	self.lineItems['bank'] = int(string[1:3],16)
	self.lineItems['addr'] = int(string[4:8],16)

	self.lineItems['opcode'] = int(string[9:11],16)

	# Parse out the arguments
	targ = string[12:14].strip()
	if len(targ) >0: self.lineItems['args'].append(targ);

	targ = string[15:17].strip()
	if len(targ) >0: self.lineItems['args'].append(targ);

	targ = string[19:21].strip()
	if len(targ) >0: self.lineItems['args'].append(targ);

	# Get the mnemonic
	self.lineItems['mnemonic'] = string[21:43]
	self.lineItems['a'] = int(string[47:51],16)
	self.lineItems['x'] = int(string[54:59],16)
	self.lineItems['y'] = int(string[61:65],16)
	self.lineItems['dp'] = int(string[68:72],16)
	self.lineItems['dbr'] = int(string[76:78],16)
	self.lineItems['sp'] = int(string[81:85],16)
	self.lineItems['flags'] = string[88:97]

class TestLogLine(LogLine):
    def __init__(self, string, lineNumber):
	LogLine.__init__(self, string, lineNumber)
	self.parseLine(string)

    def parseLine(self, string):
	self.lineItems['bank'] = int(string[1:3],16)
	self.lineItems['addr'] = int(string[4:8],16)

	self.lineItems['opcode'] = int(string[9:11],16)

	# Parse out the arguments
	targ = string[12:14].strip()
	if len(targ) >0: self.lineItems['args'].append(targ);

	targ = string[15:17].strip()
	if len(targ) >0: self.lineItems['args'].append(targ);

	targ = string[19:21].strip()
	if len(targ) >0: self.lineItems['args'].append(targ);

	# Get the mnemonic
	self.lineItems['mnemonic'] = string[21:43]
	self.lineItems['a'] = int(string[47:51],16)
	self.lineItems['x'] = int(string[54:59],16)
	self.lineItems['y'] = int(string[61:65],16)
	self.lineItems['dp'] = int(string[68:72],16)
	self.lineItems['dbr'] = int(string[76:78],16)
	self.lineItems['sp'] = int(string[81:85],16)
	self.lineItems['flags'] = string[88:97]


def printHeader(outfile):
    html =  '<html><head><title>Comparison</title>\n<style>' +\
	    '  td {\n'+\
	    '    font-size: 11px;\n'+\
	    '    font-family: courier new;\n'+\
	    '    border: 1px solid gray;'+\
	    '  }\n'+\
	    '  tr.warn {\n'+\
	    '    background-color: lightblue;\n'+\
	    '  }\n'+\
	    '  tr.err {\n'+\
	    '    background-color: pink;\n'+\
	    '  }\n'+\
	    '  pre {\n'+\
	    '    margin: 0\n'+\
	    '  }\n'+\
	    '</style>\n</head><body>\n'+\
	    '<table cellspacing="0">\n'
    outfile.write(html)

def printFooter(outfile):
    html = '</table></body></html>'
    outfile.write(html)

def printWarning(outfile, oracle, test, warnings):
    global warnCount, printWarnings, warnLimit
    warnCount += 1
    html = '<tr class="warn">\n' +\
	   '  <td>#'+oracle.getLineNumber()+'</td>\n'+\
	   '  <td><pre>'+oracle.getLine()+'</pre></td>\n' +\
	   '  <td>#'+test.getLineNumber()+'</td>\n'+\
	   '  <td><pre>'+test.getLine()+'</pre></td>\n'

    html += '  <td>\n'
    for x in warnings:
	if x:
	    html += '    <span class="warn">' + x + '</span>\n'
    html += '  </td>\n'
    html += '</tr>'


    if warnCount > warnLimit and printWarnings:
	printWarnings = False
	html += '<tr class="warn">\n'+\
		'  <td colspan="5"><center><b>Max warning limit('+str(warnLimit)+') reached!</b></center></td>\n'+\
		'</tr>'
    outfile.write(html)

def printError(outfile, oracle, test, errors):
    # Print out the context leading up to this(the last line of context is the
    # line with the error on it
    c = context.get()
    for i in range(len(c)-1):
	if c[i] is None:
	    continue;
	(a,b) = c[i]
	html = '<tr class="context">'+\
	       '  <td>#'+a.getLineNumber()+'</td>\n'+\
	       '  <td><pre>'+a.getLine()+'</pre></td>\n' +\
	       '  <td>#'+b.getLineNumber()+'</td>\n'+\
	       '  <td><pre>'+b.getLine()+'</pre></td>\n'+\
	       '  <td><span> </span></td>'+\
	       '</tr>\n'
	outfile.write(html)
    (a,b) = c[len(c)-1]
    html = '<tr class="err">\n' +\
	   '  <td>#'+a.getLineNumber()+'</td>\n'+\
	   '  <td><pre>'+a.getLine()+'</pre></td>\n' +\
	   '  <td>#'+b.getLineNumber()+'</td>\n'+\
	   '  <td><pre>'+b.getLine()+'</pre></td>\n'

    html += '  <td>\n'
    for x in errors:
	html += '    <span class="err">' + x + '</span>\n'
    html += '  </td>\n'
    html += '</tr>'
    outfile.write(html)

def skipMVN(oracle, o, olnum):
    # check for MVN in the mnemonic of the oracle(snes9x prints a lot more than it should for this)
    testmvn = o
    if o.lineItems['mnemonic'].startswith('MVN'):
	mvnEnd = oracle.tell()
	print "MVN Starting line " + str(olnum)
	while testmvn.lineItems['mnemonic'].startswith('MVN'):
	    mvnEnd = oracle.tell()
	    lineTemp='\n'
	    while len(lineTemp) < 5:
		lineTemp = oracle.readline()
		olnum += 1
	    if len(lineTemp) == 0:
		break;
	    testmvn = OracleLogLine(lineTemp.strip(), olnum)
	oracle.seek(mvnEnd)
	print "MVN ends line: " + str(olnum)
    return (o,olnum)

def skipNMI(f, line, linenum):
    if line.startswith("*** NMI"):
	nmiEnd = f.tell()
	print "NMI Starts: " + str(linenum)
	line = f.readline()
	linenum += 1
	while len(line)>24 and line[21:24] != "RTI":
	    nmiEnd = f.tell()
	    line = f.readline()
	    linenum += 1
	    while len(line) < 10:
		nmiEnd = f.tell()
		line = f.readline()
		linenum += 1
	line = f.readline()
	linenum += 1
	print "NMI Ends:   " + str(linenum)
	print line
	#f.seek(nmiEnd)
    return (line,linenum)

def compareFiles(oracle, test):
    global printWarnings, exactMatch, limitDiff, warnIsError
    outfile = open('/dev/shm/results.html','wb')
    printHeader(outfile)

    olnum = 0;
    tlnum = 0;
    printline = False
    while True:
	# Give some indication of progress
	if olnum % 5000 == 0:
	    print olnum

	oldPos = test.tell()

	# Read a line from each file, ignoring empty lines
	ol = '\n'
	tl = '\n'
	while len(ol) <5:
	    ol = oracle.readline()
	    olnum += 1
	    if len(ol)==0:

		break
	while len(tl) <5:
	    tl = test.readline()
	    tlnum += 1
	if len(ol) == 0 or len(tl) == 0:
	    break;

	# Skip NMI interrupts
	(ol,olnum) = skipNMI(oracle, ol, olnum)
	(tl,tlnum) = skipNMI(test, tl, tlnum)

	o = OracleLogLine(ol.strip(), olnum)

	# Skip extra block move instructions from the oracle
	(o,olnum) = skipMVN(oracle, o, olnum)



	t = TestLogLine(tl.strip(), tlnum)
	context.append((o,t))



	# Compare the lines
	error = []
	(equal, error) = LogLine.compareLines(o,t)

	# If they are equal, move onto the next pair of lines
	if (equal):
	    # Warnings are added to the error array
	    if len(error)>0:
		if warnIsError:
		    printError(outfile, o, t, error)
		    break;
		elif printWarnings:
		    printWarning(outfile, o, t, error)
	    continue;

	# If we require the lines match exactly, we print an error and stop
	if (exactMatch):
	    printError(outfile, o, t, error)
	    continue;

	# Now is where things can get tricky
	# If we want to see if 'test' ever gets back in sync with oracle

	error.append('Skipping line')
	printWarning(outfile, o, t, error)

	for i in range(limitDiff):
	    # read a line, skip blank lines, and end at eof
	    tl = '\n'
	    while len(tl)<20:
		tl = test.readline()
		tlnum += 1
	    if len(tl)==0: # eof?
		printError(outfile, o, t, error)
		break
	    tx = TestLogLine(tl.strip(), tlnum)

	    printWarning(outfile, LogLine("",0), tx,['Skipping line'])
	    #context.append((LogLine("",0),tx))
	    (equal, errorx) = LogLine.compareLines(o,tx)
	    if (equal):
		test.seek(oldPos,0)
		errorx.append('Skipped ' + str(i) + ' lines')
		printWarning(outfile, o, tx, errorx)
		break
	else: # They never get back in sync
	    error.append('No matching line found within ' + str(limitDiff) + ' lines')
	    #context.append((o,LogLine("",0)))
	    printError(outfile,o,t,error)
	    break
    print "Done! writing out file..."
    printFooter(outfile)
    outfile.close()

def readArgs():
    global _TEST_, _ORACLE_
    oracle = None
    test = None
    if len(sys.argv) > 2:
        oracle = open(sys.argv[1],'r')
        test =  open(sys.argv[2],'r')
    else:
	oracle = open(_ORACLE_,'r')
	test = open(_TEST_,'r')
    return (oracle, test)

if __name__ == '__main__':
    (oracle, test) = readArgs();
    print oracle, test

    compareFiles(oracle, test);

    if oracle:
        oracle.close()
    if test:
        test.close()
