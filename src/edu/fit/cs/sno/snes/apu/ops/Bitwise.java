package edu.fit.cs.sno.snes.apu.ops;

import edu.fit.cs.sno.snes.apu.APU;
import edu.fit.cs.sno.snes.apu.APUMemory;
import edu.fit.cs.sno.snes.common.Register;

public class Bitwise {

	// ASL
	
	private static int asl(int x) {
		int r = x << 1;
		
		APU.psw.setZero((r & 0xFF) == 0);
		APU.psw.setNegative((r & 0x80) != 0);
		APU.psw.setCarry((x & 0x80) != 0);
		
		return r;
	}
	
	public static void aslReg(Register r) {
		r.setValue(asl(r.getValue()));
	}
	
	public static void aslMem(int addr) {
		APUMemory.set(addr, asl(APUMemory.get(addr)));
	}
	
	// LSR
	
	private static int lsr(int x) {
		int r = x >> 1;
		
		APU.psw.setZero(r == 0);
		APU.psw.setNegative((r & 0x80) != 0);
		APU.psw.setCarry((x & 0x01) != 0);
		
		return r;
	}
	
	public static void lsrReg(Register r) {
		r.setValue(lsr(r.getValue()));
	}
	
	public static void lsrMem(int addr) {
		APUMemory.set(addr, lsr(APUMemory.get(addr)));
	}
	
	// ROL
	
	private static int rol(int x) {
		int r = (x << 1) | (APU.psw.isCarry() ? 0x01 : 0);
		
		APU.psw.setZero((r & 0xFF) == 0);
		APU.psw.setNegative((r & 0x80) != 0);
		APU.psw.setCarry((x & 0x80) != 0);
		
		return r;
	}
	
	public static void rolReg(Register r) {
		r.setValue(rol(r.getValue()));
	}
	
	public static void rolMem(int addr) {
		APUMemory.set(addr, rol(APUMemory.get(addr)));
	}
	
	// ROR
	
	private static int ror(int x) {
		int r = (x >> 1) | (APU.psw.isCarry() ? 0x80 : 0);
		
		APU.psw.setZero(r == 0);
		APU.psw.setNegative((r & 0x80) != 0);
		APU.psw.setCarry((x & 0x01) != 0);
		
		return r;
	}
	
	public static void rorReg(Register r) {
		r.setValue(ror(r.getValue()));
	}
	
	public static void rorMem(int addr) {
		APUMemory.set(addr, ror(APUMemory.get(addr)));
	}
	
	// XCN
	
	public static void xcn() {
		APU.a.setValue((APU.a.getValue() >> 4) | (APU.a.getValue() << 4));
		
		APU.psw.setZero(APU.a.getValue() == 0);
		APU.psw.setNegative((APU.a.getValue() & 0x80) != 0);
	}
	
	// Set / Clear Bits
	public static void setBit(int addr, int mask) {
		APUMemory.set(addr, APUMemory.get(addr) | mask);
	}
	
	public static void clearBit(int addr, int mask) {
		APUMemory.set(addr, APUMemory.get(addr) & ~mask);
	}
	
	public static void tset1(int addr) {
		int val = APUMemory.get(addr);
		
		APU.psw.setZero((APU.a.getValue() - val) == 0);
		APU.psw.setNegative(((APU.a.getValue() - val) & 0x80) != 0);
		
		APUMemory.set(addr, val | APU.a.getValue());
	}
	
	public static void tclr1(int addr) {
		int val = APUMemory.get(addr);
		
		APU.psw.setZero((APU.a.getValue() - val) == 0);
		APU.psw.setNegative(((APU.a.getValue() - val) & 0x80) != 0);
		
		APUMemory.set(addr, val | ~APU.a.getValue());
	}
	
	// Single bit operations
	private static int getBitOperand(int addr, boolean flip) {
		int bit = addr >> 13;
		addr &= 0x1FFF;
		
		int r = ((APUMemory.get(addr) & (1 << bit)) != 0) ? 1 : 0;
		return r ^ (flip ? 1 : 0);
	}
	
	public static void and1(int addr, boolean flip) {
		APU.psw.setCarry((APU.a.getValue() & getBitOperand(addr, flip)) != 0);
	}
	
	public static void or1(int addr, boolean flip) {
		APU.psw.setCarry((APU.a.getValue() | getBitOperand(addr, flip)) != 0);
	}
	
	public static void xor1(int addr) {
		APU.psw.setCarry((APU.a.getValue() ^ getBitOperand(addr, false)) != 0);
	}
	
	public static void not1(int addr) {
		int bit = addr >> 13;
		addr &= 0x1FFF;
		
		APUMemory.set(addr, APUMemory.get(addr) ^ (1 << bit));
	}
	
	public static void mov1ToCarry(int addr) {
		APU.psw.setCarry(getBitOperand(addr, false) != 0);
	}
	
	public static void mov1ToMem(int addr) {
		int mask = 1 << (addr >> 13);
		addr &= 0x1FFF;
		int val = APUMemory.get(addr);
		
		if ((val & mask) != 0) {
			APUMemory.set(addr, val & ~mask);
		} else {
			APUMemory.set(addr, val | mask);
		}
	}
	
}
