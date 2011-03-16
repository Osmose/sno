package edu.fit.cs.sno.snes.apu.ops;

import edu.fit.cs.sno.snes.apu.APU;
import edu.fit.cs.sno.snes.apu.APUAddrMode;
import edu.fit.cs.sno.snes.apu.APUMemory;
import edu.fit.cs.sno.snes.common.Register;

public class Branch {

	private static boolean branch(int rel, boolean branch) {
		if (branch) {
			// Odd bug; when branching negatives, will overflow to next page
			// Detect negatives and handle
			if ((rel & 0x80) != 0) {
				APU.pc.subtract((~rel + 1) & 0xFF);
			} else {
				APU.pc.add(rel);
			}
		}
		
		return branch;
	}
	
	public static boolean bra(int rel) {
		return branch(rel, true);
	}
	
	public static boolean beq(int rel) {
		return branch(rel, APU.psw.isZero());
	}
	
	public static boolean bne(int rel) {
		return branch(rel, !APU.psw.isZero());
	}
	
	public static boolean bcs(int rel) {
		return branch(rel, APU.psw.isCarry());
	}
	
	public static boolean bcc(int rel) {
		return branch(rel, !APU.psw.isCarry());
	}
	
	public static boolean bvs(int rel) {
		return branch(rel, APU.psw.isOverflow());
	}
	
	public static boolean bvc(int rel) {
		return branch(rel, !APU.psw.isOverflow());
	}
	
	public static boolean bmi(int rel) {
		return branch(rel, APU.psw.isNegative());
	}
	
	public static boolean bpl(int rel) {
		return branch(rel, !APU.psw.isNegative());
	}
	
	public static boolean bbs(int addr, int mask) {
		int rel = APUMemory.get(APU.loadDataAddr(APUAddrMode.IMMEDIATE));
		
		return branch(rel, (APUMemory.get(addr) & mask) != 0);
	}
	
	public static boolean bbc(int addr, int mask) {
		int rel = APUMemory.get(APU.loadDataAddr(APUAddrMode.IMMEDIATE));
		
		return branch(rel, (APUMemory.get(addr) & mask) == 0);
	}
	
	// cbne does not change the processor flags with its compare
	public static boolean cbne(int addr) {
		int rel = APUMemory.get(APU.loadDataAddr(APUAddrMode.IMMEDIATE));
		
		return branch(rel, ((APU.a.getValue() - APUMemory.get(addr)) & 0xFF) == 0);
	}
	
	// dbnz changes the value in the register or memory, but does not change the flags
	public static boolean dbnzMem() {
		int addr = APU.loadDataAddr(APUAddrMode.DIRECT_PAGE);
		int rel = APUMemory.get(APU.loadDataAddr(APUAddrMode.IMMEDIATE));
		
		int r = APUMemory.get(addr) - 1;
		APUMemory.set(addr, r);
		return branch(rel, (r & 0xFF) != 0);
	}
	
	public static boolean dbnzReg() {
		int rel = APUMemory.get(APU.loadDataAddr(APUAddrMode.IMMEDIATE));
		
		APU.y.subtract(1);
		return branch(rel, (APU.y.getValue() & 0xFF) != 0);
	}
	
	public static void jmp(int addr) {
		APU.pc.setValue(addr);
	}
}
