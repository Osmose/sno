package edu.fit.cs.sno.snes.debug;

import java.util.Arrays;

import edu.fit.cs.sno.snes.common.Instruction;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.util.Util;


public class CPUState {
	int a = 0;	// Accumulator
	int b = 0;	// "B" Register (Stores top byte of A in 8-bit mode)
	int dbr = 0;	// Data Bank Register
	int x = 0;	// X Index Register
	int y = 0;	// Y Index Register
	int dp = 0;	// Direct Page Register
	int sp = 0;	// Stack Pointer
	int pbr = 0;	// Program Bank Register
	int pc = 0;	// Program Counter
	int status = 0;		// Processor Status Register
	boolean emulationMode = false;
	Instruction inst;
	int[] args;
	int opcode;
	String instStr;
	String argStr;
	
	public static final int MAXSTATES = 1000;
	
	private static int head = 0;
	private static int currentIndex = 0;
	private static int size = 0;
	
	//public static ArrayList<CPUState> states = new ArrayList<CPUState>(MAXSTATES);
	public static CPUState states[] = new CPUState[MAXSTATES];
	static {
		for (int i=0;i<MAXSTATES;i++)
			states[i] = new CPUState();
	}

	public static void saveState(int opcode, int []args) {
		CPUState s = states[currentIndex];
		s.a      = CPU.a.getValue();
		s.b      = CPU.b.getValue();
		s.dbr    = CPU.dbr.getValue();
		s.x      = CPU.x.getValue();
		s.y      = CPU.y.getValue();
		s.dp     = CPU.dp.getValue();
		s.sp     = CPU.sp.getValue();
		s.pbr    = CPU.pbr.getValue();
		s.pc     = CPU.pc.getValue();
		s.status = CPU.status.getValue();
		s.emulationMode = CPU.emulationMode;
		
		s.inst = CPU.jmp[opcode];
		s.args = Arrays.copyOf(args, args.length);
		s.opcode = opcode;
		
		s.instStr = s.inst.name;
		
		String argStr = "[";
		if (s.args.length > 0) {
			for (int i = 0; i < s.args.length-1; i++) {
				argStr += Integer.toHexString(s.args[i]) + ", ";
			}
			argStr += Integer.toHexString(s.args[s.args.length-1]) + "]";
		} else {
			argStr += "]";
		}
		s.argStr = argStr;
		
		currentIndex = (currentIndex+1)%MAXSTATES;
		if (currentIndex == head) {head = (head+1) % MAXSTATES;}
		
		size++;
		if (size>=MAXSTATES) size = MAXSTATES-1;
	}

	@Override
	public String toString() {
		String[] strArgs = new String[args.length];
		if (args.length > 0) {
			for (int k = 0; k < args.length; k++) {
				strArgs[k] = Integer.toHexString(args[k]);
			}
		}
		
		int length = 1 + args.length;
		
		return String.format("%-6x %02x:%04x  %02x   %-18s %s", 
				((pbr * 0x8000) + pc - 0x8000 - length),  
				pbr, 
				pc - length, 
				opcode, 
				Util.implode(strArgs, ", "),
				inst.name
			);
	}

	public static CPUState getState(int index) {
		return states[(head + index) % MAXSTATES];
	}

	public static CPUState[] getArray() {
		CPUState s[] = new CPUState[size];
		for(int i=0; i<size; i++) {
			s[i] = getState(i);
		}
		return s;
	}
}
