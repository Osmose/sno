package edu.fit.cs.sno.snes.apu;

import edu.fit.cs.sno.snes.apu.ops.Arithmetic;
import edu.fit.cs.sno.snes.apu.ops.Bitwise;
import edu.fit.cs.sno.snes.apu.ops.Branch;
import edu.fit.cs.sno.snes.apu.ops.Logic;
import edu.fit.cs.sno.snes.apu.ops.Misc;
import edu.fit.cs.sno.snes.apu.ops.Move;
import edu.fit.cs.sno.snes.apu.ops.SixteenBit;
import edu.fit.cs.sno.snes.common.Register;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.Timing;
import edu.fit.cs.sno.util.Log;
import edu.fit.cs.sno.util.Settings;

public class APU {
	// Init CPU
	static {
		init();
	}

	public static long masterCyclesToRun = 0;
	
	public static Register pc;
	public static Register a;
	public static Register x;
	public static Register y;
	public static Register sp;
	public static StatusRegister psw;
	
	// For making pretty logs
	private static int[] args = new int[3];
	private static int anum = 0;
	
	public static int data;
	public static int dataAddr;
	
	public static Timer t0 = new Timer(125);
	public static Timer t1 = new Timer(125);
	public static Timer t2 = new Timer(16);
	
	private static long totalCycles = 0;
	private static long lastTime;
	private static final long targetSpeed = 1000*1000; // Clocked at ~1MhZ
	private static final long cycleTimeNS = (long)((1.0/targetSpeed) * 1000000000.0f);
	
	public static boolean limitSpeed = Settings.isTrue(Settings.CPU_LIMIT_SPEED);
	
	public static void init() {
		pc = new Register(Size.SHORT, 0);
		a = new Register(Size.BYTE, 0);
		x = new Register(Size.BYTE, 0);
		y = new Register(Size.BYTE, 0);
		sp = new Register(Size.BYTE, 0);
		psw = new StatusRegister();
		
		reset();
	}
	
	private static void reset() {
		pc.setValue(0xFFC0);
	}
	
	public static void step() {
		// Read opcode
		int opAddr = pc.getValue();
		int opcode = APUMemory.get(pc.getValue());
		pc.add(1);
		
		anum = 0;
		args[0] = -1;
		args[1] = -1;
		args[2] = -1;
		
		boolean b; // Used for branching mostly
		switch (opcode) {
			// ADC
			case 0x88: loadDataAddr(APUAddrMode.IMMEDIATE);		Arithmetic.addMemToReg(a, dataAddr); cycle(2); break;
			case 0x86: loadDataAddr(APUAddrMode.INDIRECT_X);	Arithmetic.addMemToReg(a, dataAddr); cycle(3); break;
			case 0x84: loadDataAddr(APUAddrMode.DIRECT_PAGE);	Arithmetic.addMemToReg(a, dataAddr); cycle(3); break;
			case 0x94: loadDataAddr(APUAddrMode.DIRECT_PAGE_X);	Arithmetic.addMemToReg(a, dataAddr); cycle(4); break;
			case 0x85: loadDataAddr(APUAddrMode.ABSOLUTE);		Arithmetic.addMemToReg(a, dataAddr); cycle(4); break;
			case 0x95: loadDataAddr(APUAddrMode.ABSOLUTE_X);	Arithmetic.addMemToReg(a, dataAddr); cycle(5); break;
			case 0x96: loadDataAddr(APUAddrMode.ABSOLUTE_Y);	Arithmetic.addMemToReg(a, dataAddr); cycle(5); break;
			case 0x87: loadDataAddr(APUAddrMode.ABSOLUTE_DP_X);	Arithmetic.addMemToReg(a, dataAddr); cycle(6); break;
			case 0x97: loadDataAddr(APUAddrMode.ABSOLUTE_DP_Y);	Arithmetic.addMemToReg(a, dataAddr); cycle(6); break;
			
			case 0x99: Arithmetic.addIYToIX();		cycle(5); break;
			case 0x89: Arithmetic.addDPToDP();		cycle(6); break;
			case 0x98: Arithmetic.addConstToDP();	cycle(5); break;
			
			// AND
			case 0x28: loadDataAddr(APUAddrMode.IMMEDIATE);		Logic.andMemToReg(a, dataAddr); cycle(2); break;
			case 0x26: loadDataAddr(APUAddrMode.INDIRECT_X);	Logic.andMemToReg(a, dataAddr); cycle(3); break;
			case 0x24: loadDataAddr(APUAddrMode.DIRECT_PAGE);	Logic.andMemToReg(a, dataAddr); cycle(3); break;
			case 0x34: loadDataAddr(APUAddrMode.DIRECT_PAGE_X);	Logic.andMemToReg(a, dataAddr); cycle(4); break;
			case 0x25: loadDataAddr(APUAddrMode.ABSOLUTE);		Logic.andMemToReg(a, dataAddr); cycle(4); break;
			case 0x35: loadDataAddr(APUAddrMode.ABSOLUTE_X);	Logic.andMemToReg(a, dataAddr); cycle(5); break;
			case 0x36: loadDataAddr(APUAddrMode.ABSOLUTE_Y);	Logic.andMemToReg(a, dataAddr); cycle(5); break;
			case 0x27: loadDataAddr(APUAddrMode.ABSOLUTE_DP_X);	Logic.andMemToReg(a, dataAddr); cycle(6); break;
			case 0x37: loadDataAddr(APUAddrMode.ABSOLUTE_DP_Y);	Logic.andMemToReg(a, dataAddr); cycle(6); break;
			
			case 0x39: Logic.andIYToIX();		cycle(5); break;
			case 0x29: Logic.andDPToDP();		cycle(6); break;
			case 0x38: Logic.andConstToDP();	cycle(5); break;
			
			// CMP
			case 0x68: loadDataAddr(APUAddrMode.IMMEDIATE);		Arithmetic.cmpMemToReg(a, dataAddr); cycle(2); break;
			case 0x66: loadDataAddr(APUAddrMode.INDIRECT_X);	Arithmetic.cmpMemToReg(a, dataAddr); cycle(3); break;
			case 0x64: loadDataAddr(APUAddrMode.DIRECT_PAGE);	Arithmetic.cmpMemToReg(a, dataAddr); cycle(3); break;
			case 0x74: loadDataAddr(APUAddrMode.DIRECT_PAGE_X);	Arithmetic.cmpMemToReg(a, dataAddr); cycle(4); break;
			case 0x65: loadDataAddr(APUAddrMode.ABSOLUTE);		Arithmetic.cmpMemToReg(a, dataAddr); cycle(4); break;
			case 0x75: loadDataAddr(APUAddrMode.ABSOLUTE_X);	Arithmetic.cmpMemToReg(a, dataAddr); cycle(5); break;
			case 0x76: loadDataAddr(APUAddrMode.ABSOLUTE_Y);	Arithmetic.cmpMemToReg(a, dataAddr); cycle(5); break;
			case 0x67: loadDataAddr(APUAddrMode.ABSOLUTE_DP_X);	Arithmetic.cmpMemToReg(a, dataAddr); cycle(6); break;
			case 0x77: loadDataAddr(APUAddrMode.ABSOLUTE_DP_Y);	Arithmetic.cmpMemToReg(a, dataAddr); cycle(6); break;
			
			case 0x79: Arithmetic.cmpIYToIX();		cycle(5); break;
			case 0x69: Arithmetic.cmpDPToDP();		cycle(6); break;
			case 0x78: Arithmetic.cmpConstToDP();	cycle(5); break;
			
			case 0xC8: loadDataAddr(APUAddrMode.IMMEDIATE);		Arithmetic.cmpMemToReg(x, dataAddr); cycle(2); break;
			case 0x3E: loadDataAddr(APUAddrMode.DIRECT_PAGE);	Arithmetic.cmpMemToReg(x, dataAddr); cycle(3); break;
			case 0x1E: loadDataAddr(APUAddrMode.ABSOLUTE);		Arithmetic.cmpMemToReg(x, dataAddr); cycle(4); break;
			
			case 0xAD: loadDataAddr(APUAddrMode.IMMEDIATE);		Arithmetic.cmpMemToReg(y, dataAddr); cycle(2); break;
			case 0x7E: loadDataAddr(APUAddrMode.DIRECT_PAGE);	Arithmetic.cmpMemToReg(y, dataAddr); cycle(3); break;
			case 0x5E: loadDataAddr(APUAddrMode.ABSOLUTE);		Arithmetic.cmpMemToReg(y, dataAddr); cycle(4); break;
			
			// DEC
			case 0x9C: Arithmetic.decReg(a); cycle(2); break;
			case 0x1D: Arithmetic.decReg(x); cycle(2); break;
			case 0xDC: Arithmetic.decReg(y); cycle(2); break;
			case 0x8B: loadDataAddr(APUAddrMode.DIRECT_PAGE);	Arithmetic.decMem(dataAddr); cycle(4); break;
			case 0x9B: loadDataAddr(APUAddrMode.DIRECT_PAGE_X);	Arithmetic.decMem(dataAddr); cycle(5); break;
			case 0x8C: loadDataAddr(APUAddrMode.ABSOLUTE);		Arithmetic.decMem(dataAddr); cycle(5); break;
			
			// INC
			case 0xBC: Arithmetic.incReg(a); cycle(2); break;
			case 0x3D: Arithmetic.incReg(x); cycle(2); break;
			case 0xFC: Arithmetic.incReg(y); cycle(2); break;
			case 0xAB: loadDataAddr(APUAddrMode.DIRECT_PAGE);	Arithmetic.incMem(dataAddr); cycle(4); break;
			case 0xBB: loadDataAddr(APUAddrMode.DIRECT_PAGE_X);	Arithmetic.incMem(dataAddr); cycle(5); break;
			case 0xAC: loadDataAddr(APUAddrMode.ABSOLUTE);		Arithmetic.incMem(dataAddr); cycle(5); break;
			
			// MOV
			case 0xE8: loadData(APUAddrMode.IMMEDIATE);			Move.movMemToReg(a); cycle(2); break;
			case 0xE6: loadData(APUAddrMode.INDIRECT_X);		Move.movMemToReg(a); cycle(3); break;
			case 0xBF: loadData(APUAddrMode.INDIRECT_X_INC);	Move.movMemToReg(a); cycle(4); break;
			case 0xE4: loadData(APUAddrMode.DIRECT_PAGE);		Move.movMemToReg(a); cycle(3); break;
			case 0xF4: loadData(APUAddrMode.DIRECT_PAGE_X);		Move.movMemToReg(a); cycle(4); break;
			case 0xE5: loadData(APUAddrMode.ABSOLUTE);			Move.movMemToReg(a); cycle(4); break;
			case 0xF5: loadData(APUAddrMode.ABSOLUTE_X);		Move.movMemToReg(a); cycle(5); break;
			case 0xF6: loadData(APUAddrMode.ABSOLUTE_Y);		Move.movMemToReg(a); cycle(5); break;
			case 0xE7: loadData(APUAddrMode.ABSOLUTE_DP_X);		Move.movMemToReg(a); cycle(6); break;
			case 0xF7: loadData(APUAddrMode.ABSOLUTE_DP_Y);		Move.movMemToReg(a); cycle(6); break;
			
			case 0xCD: loadData(APUAddrMode.IMMEDIATE);			Move.movMemToReg(x); cycle(2); break;
			case 0xF8: loadData(APUAddrMode.DIRECT_PAGE);		Move.movMemToReg(x); cycle(3); break;
			case 0xF9: loadData(APUAddrMode.DIRECT_PAGE_Y);		Move.movMemToReg(x); cycle(4); break;
			case 0xE9: loadData(APUAddrMode.ABSOLUTE);			Move.movMemToReg(x); cycle(4); break;
			
			case 0x8D: loadData(APUAddrMode.IMMEDIATE);			Move.movMemToReg(y); cycle(2); break;
			case 0xEB: loadData(APUAddrMode.DIRECT_PAGE);		Move.movMemToReg(y); cycle(3); break;
			case 0xFB: loadData(APUAddrMode.DIRECT_PAGE_Y);		Move.movMemToReg(y); cycle(4); break;
			case 0xEC: loadData(APUAddrMode.ABSOLUTE);			Move.movMemToReg(y); cycle(4); break;
			
			case 0xC6: loadDataAddr(APUAddrMode.INDIRECT_X);		Move.movRegToMem(a); cycle(4); break;
			case 0xAF: loadDataAddr(APUAddrMode.INDIRECT_X_INC);	Move.movRegToMem(a); cycle(4); break;
			case 0xC4: loadDataAddr(APUAddrMode.DIRECT_PAGE);		Move.movRegToMem(a); cycle(4); break;
			case 0xD4: loadDataAddr(APUAddrMode.DIRECT_PAGE_X);		Move.movRegToMem(a); cycle(5); break;
			case 0xC5: loadDataAddr(APUAddrMode.ABSOLUTE);			Move.movRegToMem(a); cycle(5); break;
			case 0xD5: loadDataAddr(APUAddrMode.ABSOLUTE_X);		Move.movRegToMem(a); cycle(6); break;
			case 0xD6: loadDataAddr(APUAddrMode.ABSOLUTE_Y);		Move.movRegToMem(a); cycle(6); break;
			case 0xC7: loadDataAddr(APUAddrMode.ABSOLUTE_DP_X);		Move.movRegToMem(a); cycle(7); break;
			case 0xD7: loadDataAddr(APUAddrMode.ABSOLUTE_DP_Y);		Move.movRegToMem(a); cycle(7); break;
			
			case 0xD8: loadDataAddr(APUAddrMode.DIRECT_PAGE);		Move.movRegToMem(x); cycle(4); break;
			case 0xD9: loadDataAddr(APUAddrMode.DIRECT_PAGE_X);		Move.movRegToMem(x); cycle(5); break;
			case 0xC9: loadDataAddr(APUAddrMode.ABSOLUTE);			Move.movRegToMem(x); cycle(5); break;
			
			case 0xCB: loadDataAddr(APUAddrMode.DIRECT_PAGE);		Move.movRegToMem(y); cycle(4); break;
			case 0xDB: loadDataAddr(APUAddrMode.DIRECT_PAGE_X);		Move.movRegToMem(y); cycle(5); break;
			case 0xCC: loadDataAddr(APUAddrMode.ABSOLUTE);			Move.movRegToMem(y); cycle(5); break;
			
			case 0x7D: Move.movRegToReg(x, a, true); cycle(2); break;
			case 0xDD: Move.movRegToReg(y, a, true); cycle(2); break;
			case 0x5D: Move.movRegToReg(a, x, true); cycle(2); break;
			case 0xFD: Move.movRegToReg(a, y, true); cycle(2); break;
			case 0x9D: Move.movRegToReg(sp, x, true); cycle(2); break;
			case 0xBD: Move.movRegToReg(x, sp, true); cycle(2); break;
			
			case 0xFA: Move.movDPToDP(); cycle(5); break;
			case 0x8F: Move.movConstToDP(); cycle(5); break;
			
			// OR
			case 0x08: loadDataAddr(APUAddrMode.IMMEDIATE);		Logic.orMemToReg(a, dataAddr); cycle(2); break;
			case 0x06: loadDataAddr(APUAddrMode.INDIRECT_X);	Logic.orMemToReg(a, dataAddr); cycle(3); break;
			case 0x04: loadDataAddr(APUAddrMode.DIRECT_PAGE);	Logic.orMemToReg(a, dataAddr); cycle(3); break;
			case 0x14: loadDataAddr(APUAddrMode.DIRECT_PAGE_X);	Logic.orMemToReg(a, dataAddr); cycle(4); break;
			case 0x05: loadDataAddr(APUAddrMode.ABSOLUTE);		Logic.orMemToReg(a, dataAddr); cycle(4); break;
			case 0x15: loadDataAddr(APUAddrMode.ABSOLUTE_X);	Logic.orMemToReg(a, dataAddr); cycle(5); break;
			case 0x16: loadDataAddr(APUAddrMode.ABSOLUTE_Y);	Logic.orMemToReg(a, dataAddr); cycle(5); break;
			case 0x07: loadDataAddr(APUAddrMode.ABSOLUTE_DP_X);	Logic.orMemToReg(a, dataAddr); cycle(6); break;
			case 0x17: loadDataAddr(APUAddrMode.ABSOLUTE_DP_Y);	Logic.orMemToReg(a, dataAddr); cycle(6); break;
			
			case 0x19: Logic.orIYToIX();		cycle(5); break;
			case 0x09: Logic.orDPToDP();		cycle(6); break;
			case 0x18: Logic.orConstToDP();	cycle(5); break;
			
			// SBC
			case 0xA8: loadDataAddr(APUAddrMode.IMMEDIATE);		Arithmetic.subMemToReg(a, dataAddr); cycle(2); break;
			case 0xA6: loadDataAddr(APUAddrMode.INDIRECT_X);	Arithmetic.subMemToReg(a, dataAddr); cycle(3); break;
			case 0xA4: loadDataAddr(APUAddrMode.DIRECT_PAGE);	Arithmetic.subMemToReg(a, dataAddr); cycle(3); break;
			case 0xB4: loadDataAddr(APUAddrMode.DIRECT_PAGE_X);	Arithmetic.subMemToReg(a, dataAddr); cycle(4); break;
			case 0xA5: loadDataAddr(APUAddrMode.ABSOLUTE);		Arithmetic.subMemToReg(a, dataAddr); cycle(4); break;
			case 0xB5: loadDataAddr(APUAddrMode.ABSOLUTE_X);	Arithmetic.subMemToReg(a, dataAddr); cycle(5); break;
			case 0xB6: loadDataAddr(APUAddrMode.ABSOLUTE_Y);	Arithmetic.subMemToReg(a, dataAddr); cycle(5); break;
			case 0xA7: loadDataAddr(APUAddrMode.ABSOLUTE_DP_X);	Arithmetic.subMemToReg(a, dataAddr); cycle(6); break;
			case 0xB7: loadDataAddr(APUAddrMode.ABSOLUTE_DP_Y);	Arithmetic.subMemToReg(a, dataAddr); cycle(6); break;
			
			case 0xB9: Arithmetic.subIYToIX();		cycle(5); break;
			case 0xA9: Arithmetic.subDPToDP();		cycle(6); break;
			case 0xB8: Arithmetic.subConstToDP();	cycle(5); break;
			
			// XOR
			case 0x48: loadDataAddr(APUAddrMode.IMMEDIATE);		Logic.xorMemToReg(a, dataAddr); cycle(2); break;
			case 0x46: loadDataAddr(APUAddrMode.INDIRECT_X);	Logic.xorMemToReg(a, dataAddr); cycle(3); break;
			case 0x44: loadDataAddr(APUAddrMode.DIRECT_PAGE);	Logic.xorMemToReg(a, dataAddr); cycle(3); break;
			case 0x54: loadDataAddr(APUAddrMode.DIRECT_PAGE_X);	Logic.xorMemToReg(a, dataAddr); cycle(4); break;
			case 0x45: loadDataAddr(APUAddrMode.ABSOLUTE);		Logic.xorMemToReg(a, dataAddr); cycle(4); break;
			case 0x55: loadDataAddr(APUAddrMode.ABSOLUTE_X);	Logic.xorMemToReg(a, dataAddr); cycle(5); break;
			case 0x56: loadDataAddr(APUAddrMode.ABSOLUTE_Y);	Logic.xorMemToReg(a, dataAddr); cycle(5); break;
			case 0x47: loadDataAddr(APUAddrMode.ABSOLUTE_DP_X);	Logic.xorMemToReg(a, dataAddr); cycle(6); break;
			case 0x57: loadDataAddr(APUAddrMode.ABSOLUTE_DP_Y);	Logic.xorMemToReg(a, dataAddr); cycle(6); break;
			
			case 0x59: Logic.xorIYToIX();		cycle(5); break;
			case 0x49: Logic.xorDPToDP();		cycle(6); break;
			case 0x58: Logic.xorConstToDP();	cycle(5); break;
			
			// ASL
			case 0x1C: Bitwise.aslReg(a); cycle(2); break;
			case 0x0B: loadDataAddr(APUAddrMode.DIRECT_PAGE);	Bitwise.aslMem(dataAddr); cycle(4); break;
			case 0x1B: loadDataAddr(APUAddrMode.DIRECT_PAGE_X);	Bitwise.aslMem(dataAddr); cycle(5); break;
			case 0x0C: loadDataAddr(APUAddrMode.ABSOLUTE);		Bitwise.aslMem(dataAddr); cycle(5); break;
			
			// LSR
			case 0x5C: Bitwise.lsrReg(a); cycle(2); break;
			case 0x4B: loadDataAddr(APUAddrMode.DIRECT_PAGE);	Bitwise.lsrMem(dataAddr); cycle(4); break;
			case 0x5B: loadDataAddr(APUAddrMode.DIRECT_PAGE_X);	Bitwise.lsrMem(dataAddr); cycle(5); break;
			case 0x4C: loadDataAddr(APUAddrMode.ABSOLUTE);		Bitwise.lsrMem(dataAddr); cycle(5); break;
			
			// ROL
			case 0x3C: Bitwise.rolReg(a); cycle(2); break;
			case 0x2B: loadDataAddr(APUAddrMode.DIRECT_PAGE);	Bitwise.rolMem(dataAddr); cycle(4); break;
			case 0x3B: loadDataAddr(APUAddrMode.DIRECT_PAGE_X);	Bitwise.rolMem(dataAddr); cycle(5); break;
			case 0x2C: loadDataAddr(APUAddrMode.ABSOLUTE);		Bitwise.rolMem(dataAddr); cycle(5); break;
			
			// ROR
			case 0x7C: Bitwise.rorReg(a); cycle(2); break;
			case 0x6B: loadDataAddr(APUAddrMode.DIRECT_PAGE);	Bitwise.rorMem(dataAddr); cycle(4); break;
			case 0x7B: loadDataAddr(APUAddrMode.DIRECT_PAGE_X);	Bitwise.rorMem(dataAddr); cycle(5); break;
			case 0x6C: loadDataAddr(APUAddrMode.ABSOLUTE);		Bitwise.rorMem(dataAddr); cycle(5); break;
			
			// 16-bit
			case 0xBA: loadDataAddr(APUAddrMode.DIRECT_PAGE);	SixteenBit.movwMemToReg(dataAddr); cycle(5); break;
			case 0xDA: loadDataAddr(APUAddrMode.DIRECT_PAGE);	SixteenBit.movwRegToMem(dataAddr); cycle(5); break;
			case 0x3A: loadDataAddr(APUAddrMode.DIRECT_PAGE);	SixteenBit.incw(dataAddr); cycle(6); break;
			case 0x1A: loadDataAddr(APUAddrMode.DIRECT_PAGE);	SixteenBit.decw(dataAddr); cycle(6); break;
			case 0x7A: loadDataAddr(APUAddrMode.DIRECT_PAGE);	SixteenBit.addw(dataAddr); cycle(5); break;
			case 0x9A: loadDataAddr(APUAddrMode.DIRECT_PAGE);	SixteenBit.subw(dataAddr); cycle(5); break;
			case 0x5A: loadDataAddr(APUAddrMode.DIRECT_PAGE);	SixteenBit.cmpw(dataAddr); cycle(4); break;
			
			// DIV / MUL
			case 0xCF: Arithmetic.mul(); cycle(9); break;
			case 0x9E: Arithmetic.div(); cycle(12); break;
			
			// DAA / DAS
			case 0xDF: Misc.daa(); cycle(3); break;
			case 0xBE: Misc.das(); cycle(3); break;
			
			// Branch / Jump
			case 0x2F: loadData(APUAddrMode.IMMEDIATE);	b = Branch.bra(data); cycle(b ? 4 : 2); break;
			case 0xF0: loadData(APUAddrMode.IMMEDIATE);	b = Branch.beq(data); cycle(b ? 4 : 2); break;
			case 0xD0: loadData(APUAddrMode.IMMEDIATE);	b = Branch.bne(data); cycle(b ? 4 : 2); break;
			case 0xB0: loadData(APUAddrMode.IMMEDIATE);	b = Branch.bcs(data); cycle(b ? 4 : 2); break;
			case 0x90: loadData(APUAddrMode.IMMEDIATE);	b = Branch.bcc(data); cycle(b ? 4 : 2); break;
			case 0x70: loadData(APUAddrMode.IMMEDIATE);	b = Branch.bvs(data); cycle(b ? 4 : 2); break;
			case 0x50: loadData(APUAddrMode.IMMEDIATE);	b = Branch.bvc(data); cycle(b ? 4 : 2); break;
			case 0x30: loadData(APUAddrMode.IMMEDIATE);	b = Branch.bmi(data); cycle(b ? 4 : 2); break;
			case 0x10: loadData(APUAddrMode.IMMEDIATE);	b = Branch.bpl(data); cycle(b ? 4 : 2); break;
			
			case 0x2E: loadDataAddr(APUAddrMode.DIRECT_PAGE);	b = Branch.cbne(dataAddr); cycle(b ? 7 : 5); break;
			case 0xDE: loadDataAddr(APUAddrMode.DIRECT_PAGE_X);	b = Branch.cbne(dataAddr); cycle(b ? 8 : 6); break;
			case 0x6E: b = Branch.dbnzMem(); cycle(b ? 7 : 5); break;
			case 0xFE: b = Branch.dbnzReg(); cycle(b ? 6 : 4); break;
			
			case 0x5F: loadDataAddr(APUAddrMode.ABSOLUTE);		Branch.jmp(dataAddr); 						cycle(3); break;
			case 0x1F: loadDataAddr(APUAddrMode.ABSOLUTE_X);	Branch.jmp(APUMemory.getShort(dataAddr)); 	cycle(6); break;
			
			case 0x03: loadDataAddr(APUAddrMode.DIRECT_PAGE);	b = Branch.bbs(dataAddr, 0x01); cycle(b ? 7 : 5); break;
			case 0x23: loadDataAddr(APUAddrMode.DIRECT_PAGE);	b = Branch.bbs(dataAddr, 0x02); cycle(b ? 7 : 5); break;
			case 0x43: loadDataAddr(APUAddrMode.DIRECT_PAGE);	b = Branch.bbs(dataAddr, 0x04); cycle(b ? 7 : 5); break;
			case 0x63: loadDataAddr(APUAddrMode.DIRECT_PAGE);	b = Branch.bbs(dataAddr, 0x08); cycle(b ? 7 : 5); break;
			case 0x83: loadDataAddr(APUAddrMode.DIRECT_PAGE);	b = Branch.bbs(dataAddr, 0x10); cycle(b ? 7 : 5); break;
			case 0xA3: loadDataAddr(APUAddrMode.DIRECT_PAGE);	b = Branch.bbs(dataAddr, 0x20); cycle(b ? 7 : 5); break;
			case 0xC3: loadDataAddr(APUAddrMode.DIRECT_PAGE);	b = Branch.bbs(dataAddr, 0x40); cycle(b ? 7 : 5); break;
			case 0xE3: loadDataAddr(APUAddrMode.DIRECT_PAGE);	b = Branch.bbs(dataAddr, 0x80); cycle(b ? 7 : 5); break;
			
			case 0x13: loadDataAddr(APUAddrMode.DIRECT_PAGE);	b = Branch.bbc(dataAddr, 0x01); cycle(b ? 7 : 5); break;
			case 0x33: loadDataAddr(APUAddrMode.DIRECT_PAGE);	b = Branch.bbc(dataAddr, 0x02); cycle(b ? 7 : 5); break;
			case 0x53: loadDataAddr(APUAddrMode.DIRECT_PAGE);	b = Branch.bbc(dataAddr, 0x04); cycle(b ? 7 : 5); break;
			case 0x73: loadDataAddr(APUAddrMode.DIRECT_PAGE);	b = Branch.bbc(dataAddr, 0x08); cycle(b ? 7 : 5); break;
			case 0x93: loadDataAddr(APUAddrMode.DIRECT_PAGE);	b = Branch.bbc(dataAddr, 0x10); cycle(b ? 7 : 5); break;
			case 0xB3: loadDataAddr(APUAddrMode.DIRECT_PAGE);	b = Branch.bbc(dataAddr, 0x20); cycle(b ? 7 : 5); break;
			case 0xD3: loadDataAddr(APUAddrMode.DIRECT_PAGE);	b = Branch.bbc(dataAddr, 0x40); cycle(b ? 7 : 5); break;
			case 0xF3: loadDataAddr(APUAddrMode.DIRECT_PAGE);	b = Branch.bbc(dataAddr, 0x80); cycle(b ? 7 : 5); break;
			
			// Subroutines / Interrupts
			case 0x3F: loadDataAddr(APUAddrMode.ABSOLUTE);		Misc.call(dataAddr);	cycle(8); break;
			case 0x4F: loadDataAddr(APUAddrMode.ABSOLUTE_BYTE);	Misc.pcall(dataAddr);	cycle(6); break;
			case 0x0F: Misc.brk();	cycle(8); break;
			case 0x6F: Misc.ret();	cycle(5); break;
			case 0x7F: Misc.reti();	cycle(6); break;
			
			case 0x01: Misc.tcall(0x0); cycle(8); break;
			case 0x11: Misc.tcall(0x1); cycle(8); break;
			case 0x21: Misc.tcall(0x2); cycle(8); break;
			case 0x31: Misc.tcall(0x3); cycle(8); break;
			case 0x41: Misc.tcall(0x4); cycle(8); break;
			case 0x51: Misc.tcall(0x5); cycle(8); break;
			case 0x61: Misc.tcall(0x6); cycle(8); break;
			case 0x71: Misc.tcall(0x7); cycle(8); break;
			case 0x81: Misc.tcall(0x8); cycle(8); break;
			case 0x91: Misc.tcall(0x9); cycle(8); break;
			case 0xA1: Misc.tcall(0xA); cycle(8); break;
			case 0xB1: Misc.tcall(0xB); cycle(8); break;
			case 0xC1: Misc.tcall(0xC); cycle(8); break;
			case 0xD1: Misc.tcall(0xD); cycle(8); break;
			case 0xE1: Misc.tcall(0xE); cycle(8); break;
			case 0xF1: Misc.tcall(0xF); cycle(8); break;
			
			// Stack
			case 0x2D: Misc.pushReg(a);				cycle(4); break;
			case 0x4D: Misc.pushReg(x);				cycle(4); break;
			case 0x6D: Misc.pushReg(y);				cycle(4); break;
			case 0x0D: Misc.push(psw.getValue());	cycle(4); break;
			
			case 0xAE: Misc.popReg(a);				cycle(4); break;
			case 0xCE: Misc.popReg(x);				cycle(4); break;
			case 0xEE: Misc.popReg(y);				cycle(4); break;
			case 0x8E: psw.setValue(Misc.pop());	cycle(4); break;
			
			// Single-bit
			case 0x02: loadDataAddr(APUAddrMode.DIRECT_PAGE);	Bitwise.setBit(dataAddr, 0x01); cycle(4); break;
			case 0x22: loadDataAddr(APUAddrMode.DIRECT_PAGE);	Bitwise.setBit(dataAddr, 0x02); cycle(4); break;
			case 0x42: loadDataAddr(APUAddrMode.DIRECT_PAGE);	Bitwise.setBit(dataAddr, 0x04); cycle(4); break;
			case 0x62: loadDataAddr(APUAddrMode.DIRECT_PAGE);	Bitwise.setBit(dataAddr, 0x08); cycle(4); break;
			case 0x82: loadDataAddr(APUAddrMode.DIRECT_PAGE);	Bitwise.setBit(dataAddr, 0x10); cycle(4); break;
			case 0xA2: loadDataAddr(APUAddrMode.DIRECT_PAGE);	Bitwise.setBit(dataAddr, 0x20); cycle(4); break;
			case 0xC2: loadDataAddr(APUAddrMode.DIRECT_PAGE);	Bitwise.setBit(dataAddr, 0x40); cycle(4); break;
			case 0xE2: loadDataAddr(APUAddrMode.DIRECT_PAGE);	Bitwise.setBit(dataAddr, 0x80); cycle(4); break;
			
			case 0x12: loadDataAddr(APUAddrMode.DIRECT_PAGE);	Bitwise.clearBit(dataAddr, 0x01); cycle(4); break;
			case 0x32: loadDataAddr(APUAddrMode.DIRECT_PAGE);	Bitwise.clearBit(dataAddr, 0x02); cycle(4); break;
			case 0x52: loadDataAddr(APUAddrMode.DIRECT_PAGE);	Bitwise.clearBit(dataAddr, 0x04); cycle(4); break;
			case 0x72: loadDataAddr(APUAddrMode.DIRECT_PAGE);	Bitwise.clearBit(dataAddr, 0x08); cycle(4); break;
			case 0x92: loadDataAddr(APUAddrMode.DIRECT_PAGE);	Bitwise.clearBit(dataAddr, 0x10); cycle(4); break;
			case 0xB2: loadDataAddr(APUAddrMode.DIRECT_PAGE);	Bitwise.clearBit(dataAddr, 0x20); cycle(4); break;
			case 0xD2: loadDataAddr(APUAddrMode.DIRECT_PAGE);	Bitwise.clearBit(dataAddr, 0x40); cycle(4); break;
			case 0xF2: loadDataAddr(APUAddrMode.DIRECT_PAGE);	Bitwise.clearBit(dataAddr, 0x80); cycle(4); break;
			
			case 0x0E: loadDataAddr(APUAddrMode.ABSOLUTE);	Bitwise.tset1(dataAddr); cycle(6); break;
			case 0x4E: loadDataAddr(APUAddrMode.ABSOLUTE);	Bitwise.tclr1(dataAddr); cycle(6); break;
			
			case 0x4A: loadDataAddr(APUAddrMode.ABSOLUTE);	Bitwise.and1(dataAddr, false);	cycle(4); break;
			case 0x6A: loadDataAddr(APUAddrMode.ABSOLUTE);	Bitwise.and1(dataAddr, true);	cycle(4); break;
			case 0x0A: loadDataAddr(APUAddrMode.ABSOLUTE);	Bitwise.or1(dataAddr, false);	cycle(5); break;
			case 0x2A: loadDataAddr(APUAddrMode.ABSOLUTE);	Bitwise.or1(dataAddr, true);	cycle(5); break;
			case 0x8A: loadDataAddr(APUAddrMode.ABSOLUTE);	Bitwise.xor1(dataAddr);			cycle(5); break;
			case 0xEA: loadDataAddr(APUAddrMode.ABSOLUTE);	Bitwise.not1(dataAddr);			cycle(5); break;
			case 0xAA: loadDataAddr(APUAddrMode.ABSOLUTE);	Bitwise.mov1ToCarry(dataAddr);	cycle(4); break;
			case 0xCA: loadDataAddr(APUAddrMode.ABSOLUTE);	Bitwise.mov1ToMem(dataAddr);	cycle(6); break;
			
			// Status Register Bits
			case 0x60: psw.setCarry(false);								cycle(2); break;
			case 0x80: psw.setCarry(true);								cycle(2); break;
			case 0xED: psw.setCarry(!psw.isCarry());					cycle(3); break;
			case 0xE0: psw.setOverflow(false); psw.setHalfCarry(false);	cycle(2); break;
			case 0x20: psw.setDirectPage(false);						cycle(2); break;
			case 0x40: psw.setDirectPage(true);							cycle(2); break;
			case 0xA0: psw.setIndirectMaster(true);						cycle(3); break;
			case 0xC0: psw.setIndirectMaster(false);					cycle(3); break;
			
			// Misc
			case 0x9F: Bitwise.xcn(); cycle(5); break; // XCN
			case 0x00: cycle(2); break;
			case 0xEF: Misc.stop(); break;
			case 0xFF: Misc.stop(); break;
		}
		
		// Pretty logs!
		StringBuffer argStr = new StringBuffer();
		for (int i=0; i<3; i++) {
			if (args[i] != -1)
				argStr.append(String.format("%02X ",args[i]));
			else
				argStr.append("   ");
		}
		
		if (Log.apu.enabled()) {
			Log.apu(String.format("%04X %02X %s A:%02X X:%02X Y:%02X S:%02X P:%8s DataAddr:%04X Data:%02X",
				opAddr,
				opcode,
				argStr,
				a.getValue(),
				x.getValue(),
				y.getValue(),
				sp.getValue(),
				psw.toString(),
				dataAddr,
				data
			));
		}
	}
	
	public static void loadData(APUAddrMode mode) {
		loadDataAddr(mode);
		data = APUMemory.get(dataAddr);
	}
	
	public static int loadDataAddr(APUAddrMode mode) {
		switch (mode) {
			case IMMEDIATE:
				args[anum++] = APUMemory.get(pc.getValue());
				dataAddr = pc.getValue();
				pc.add(1);
				break;
			case INDIRECT_X:
				dataAddr = dpAddr(x.getValue());
				break;
			case INDIRECT_Y:
				dataAddr = dpAddr(y.getValue());
				break;
			case INDIRECT_X_INC:
				dataAddr = dpAddr(x.getValue());
				x.add(1);
				break;
			case DIRECT_PAGE:
				args[anum++] = APUMemory.get(pc.getValue());
				dataAddr = dpAddr(APUMemory.get(pc.getValue()));
				pc.add(1);
				break;
			case DIRECT_PAGE_X:
				args[anum++] = APUMemory.get(pc.getValue());
				dataAddr = dpAddr(APUMemory.get(pc.getValue()) + x.getValue());
				pc.add(1);
				break;
			case DIRECT_PAGE_Y:
				args[anum++] = APUMemory.get(pc.getValue());
				dataAddr = dpAddr(APUMemory.get(pc.getValue()) + y.getValue());
				pc.add(1);
				break;
			case ABSOLUTE:
				args[anum++] = APUMemory.get(pc.getValue());
				args[anum++] = APUMemory.get(pc.getValue()+1);
				dataAddr = APUMemory.getShort(pc.getValue());
				pc.add(2);
				break;
			case ABSOLUTE_BYTE:
				args[anum++] = APUMemory.get(pc.getValue());
				dataAddr = APUMemory.get(pc.getValue());
				pc.add(1);
				break;
			case ABSOLUTE_X:
				args[anum++] = APUMemory.get(pc.getValue());
				args[anum++] = APUMemory.get(pc.getValue()+1);
				dataAddr = APUMemory.getShort(pc.getValue()) + x.getValue();
				pc.add(2);
				break;
			case ABSOLUTE_Y:
				args[anum++] = APUMemory.get(pc.getValue());
				args[anum++] = APUMemory.get(pc.getValue()+1);
				dataAddr = APUMemory.getShort(pc.getValue()) + y.getValue();
				pc.add(2);
				break;
			case ABSOLUTE_DP_X:
				args[anum++] = APUMemory.get(pc.getValue());
				dataAddr = APUMemory.getShort(dpAddr(APUMemory.get(pc.getValue()) + x.getValue()));
				pc.add(1);
				break;
			case ABSOLUTE_DP_Y:
				args[anum++] = APUMemory.get(pc.getValue());
				dataAddr = APUMemory.getShort(dpAddr(APUMemory.get(pc.getValue()))) + y.getValue();
				pc.add(1);
				break;
		}
		
		return dataAddr;
	}
	
	private static int dpAddr(int addr) {
		return (psw.isDirectPage() ? 0x0100 : 0) + addr;
	}
	
	public static void processCycles(long masterCycles) {
		masterCyclesToRun += masterCycles;
		while(masterCyclesToRun >= 24) {
			step();
			masterCyclesToRun -= 24;
		}
	}
	
	public static void cycle(int cycles) {
		// Each cycle appears to be 24 master cycles - NOT SURE, BUT GOING WITH IT
		// int cyclesPassed = 24 * cycles;
		int cyclesPassed = cycles;
		totalCycles += cyclesPassed;
		
		// Process timers
		t0.passCycles(cyclesPassed);
		t1.passCycles(cyclesPassed);
		t2.passCycles(cyclesPassed);
		
		if (limitSpeed) {
			long sleep, elapsed;
			do {
				elapsed = System.nanoTime() - lastTime;
				sleep = (cycles * cycleTimeNS) - elapsed;
			} while(sleep > cycleTimeNS);
			lastTime = System.nanoTime();
		}
		
		while(totalCycles*cycleTimeNS > Timing.getCycles()*Timing.cycleTimeNS) {
			
		}
	}
	
	public static int getYA() {
		return (y.getValue() << 8) | a.getValue();
	}
	
	public static void setYA(int val) {
		y.setValue((val & 0xFF00) >> 8);
		a.setValue(val & 0xFF);
	}

	public static void debugReset() {
		// TODO: put the apu back into the, hey i'm waiting for data more
		System.out.println("Resetting APU");
		APUMemory.reset();
		init();
	}
	
}
