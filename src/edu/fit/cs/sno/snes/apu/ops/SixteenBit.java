package edu.fit.cs.sno.snes.apu.ops;

import edu.fit.cs.sno.snes.apu.APU;
import edu.fit.cs.sno.snes.apu.APUMemory;

public class SixteenBit {

	public static void movwMemToReg(int addr) {
		int r = APUMemory.getShort(addr);
		
		APU.y.setValue((r & 0xFF00) >> 8);
		APU.a.setValue(r & 0xFF);
		
		APU.psw.setNegative((r & 0x8000) != 0);
		APU.psw.setZero(r == 0);
	}
	
	public static void movwRegToMem(int addr) {
		APUMemory.set(addr, APU.a.getValue());
		APUMemory.set(addr + 1, APU.y.getValue());
	}
	
	public static void incw(int addr) {
		int r = (APUMemory.getShort(addr) + 1) & 0xFFFF;
		
		APU.psw.setNegative((r & 0x8000) != 0);
		APU.psw.setZero(r == 0);
		
		APUMemory.setShort(addr, r);
	}
	
	public static void decw(int addr) {
		int r = (APUMemory.getShort(addr) - 1) & 0xFFFF;
		
		APU.psw.setNegative((r & 0x8000) != 0);
		APU.psw.setZero(r == 0);
		
		APUMemory.setShort(addr, r);
	}
	
	public static void addw(int addr) {
		int x = APU.getYA();
		int y = APUMemory.getShort(addr);
		int r = x + y;
		
		APU.psw.setNegative((r & 0x8000) != 0);
		APU.psw.setZero((r & 0xFFFF) == 0);
		APU.psw.setCarry(r > 0xFFFF);
		APU.psw.setHalfCarry(((x ^ y ^ r) & 0x1000) != 0);
		APU.psw.setOverflow((~(x ^ y) & (x ^ r) & 0x8000) != 0);
		
		APU.setYA(r);
	}
	
	public static void subw(int addr) {
		int x = APU.getYA();
		int y = APUMemory.getShort(addr);
		int r = x - y;
		
		APU.psw.setNegative((r & 0x8000) != 0);
		APU.psw.setZero((r & 0xFFFF) == 0);
		APU.psw.setCarry((r & 0xFFFF) >= 0);
		APU.psw.setHalfCarry(((x ^ y ^ r) & 0x1000) == 0);
		APU.psw.setOverflow(((x ^ y) & (x ^ r) & 0x8000) != 0);
		
		APU.setYA(r);
	}
	
	public static void cmpw(int addr) {
		int x = APU.getYA();
		int y = APUMemory.getShort(addr);
		int r = x - y;
		
		APU.psw.setNegative((r & 0x8000) != 0);
		APU.psw.setZero((r & 0xFFFF) == 0);
	}
}
