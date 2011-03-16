package edu.fit.cs.sno.snes.apu.ops;

import edu.fit.cs.sno.snes.apu.APU;
import edu.fit.cs.sno.snes.apu.APUAddrMode;
import edu.fit.cs.sno.snes.apu.APUMemory;
import edu.fit.cs.sno.snes.common.Register;

public class Arithmetic {

	// Addition
	
	public static void addMemToReg(Register reg, int addr) {
		reg.setValue(add(reg.getValue(), APUMemory.get(addr)));
	}
	
	public static void addMemToMem(int srcAddr, int dstAddr) {
		APUMemory.set(dstAddr, add(APUMemory.get(srcAddr), APUMemory.get(dstAddr)));
	}
	
	public static void addIYToIX() {
		int srcAddr = APU.loadDataAddr(APUAddrMode.INDIRECT_Y);
		int dstAddr = APU.loadDataAddr(APUAddrMode.INDIRECT_X);
		
		addMemToMem(srcAddr, dstAddr);
	}
	
	public static void addDPToDP() {
		int srcAddr = APU.loadDataAddr(APUAddrMode.DIRECT_PAGE);
		int dstAddr = APU.loadDataAddr(APUAddrMode.DIRECT_PAGE);
		
		addMemToMem(srcAddr, dstAddr);
	}
	
	public static void addConstToDP() {
		int srcAddr = APU.loadDataAddr(APUAddrMode.IMMEDIATE);
		int dstAddr = APU.loadDataAddr(APUAddrMode.DIRECT_PAGE);
		
		addMemToMem(srcAddr, dstAddr);
	}
	
	private static int add(int x, int y) {
		int r = x + y + (APU.psw.isCarry() ? 1 : 0);
		
		APU.psw.setNegative((r & 0x80) != 0);
		APU.psw.setZero((r & 0xFF) == 0);
		APU.psw.setCarry(r > 0xFF);
		APU.psw.setHalfCarry(((x ^ y ^ r) & 0x10) != 0);
		APU.psw.setOverflow((~(x ^ y) & (x ^ r) & 0x80) != 0);
		
		return r;
	}
	
	// Subtraction
	
	public static void subMemToReg(Register reg, int addr) {
		reg.setValue(subtract(reg.getValue(), APUMemory.get(addr), true, true));
	}
	
	public static void subMemToMem(int srcAddr, int dstAddr) {
		APUMemory.set(dstAddr, subtract(APUMemory.get(srcAddr), APUMemory.get(dstAddr), true, true));
	}
	
	public static void subIYToIX() {
		int srcAddr = APU.loadDataAddr(APUAddrMode.INDIRECT_Y);
		int dstAddr = APU.loadDataAddr(APUAddrMode.INDIRECT_X);
		
		subMemToMem(srcAddr, dstAddr);
	}
	
	public static void subDPToDP() {
		int srcAddr = APU.loadDataAddr(APUAddrMode.DIRECT_PAGE);
		int dstAddr = APU.loadDataAddr(APUAddrMode.DIRECT_PAGE);
		
		subMemToMem(srcAddr, dstAddr);
	}
	
	public static void subConstToDP() {
		int srcAddr = APU.loadDataAddr(APUAddrMode.IMMEDIATE);
		int dstAddr = APU.loadDataAddr(APUAddrMode.DIRECT_PAGE);
		
		subMemToMem(srcAddr, dstAddr);
	}
	
	private static int subtract(int x, int y, boolean extFlags, boolean useCarry) {
		int r = x - y - (useCarry && !APU.psw.isCarry() ? 1 : 0);
		
		APU.psw.setNegative((r & 0x80) != 0);
		APU.psw.setZero((r & 0xFF) == 0);
		APU.psw.setCarry(y <= x);
		
		if (extFlags) {
			APU.psw.setHalfCarry(((x ^ y ^ r) & 0x10) == 0);
			APU.psw.setOverflow(((x ^ y) & (x ^ r) & 0x80) != 0);
		}
			
		return r;
	}
	
	// Compare
	
	public static void cmpMemToReg(Register reg, int addr) {
		subtract(reg.getValue(), APUMemory.get(addr), false, false);
	}
	
	public static void cmpMemToMem(int srcAddr, int dstAddr) {
		subtract(APUMemory.get(srcAddr), APUMemory.get(dstAddr), false, false);
	}
	
	public static void cmpIYToIX() {
		int srcAddr = APU.loadDataAddr(APUAddrMode.INDIRECT_Y);
		int dstAddr = APU.loadDataAddr(APUAddrMode.INDIRECT_X);
		
		cmpMemToMem(srcAddr, dstAddr);
	}
	
	public static void cmpDPToDP() {
		int srcAddr = APU.loadDataAddr(APUAddrMode.DIRECT_PAGE);
		int dstAddr = APU.loadDataAddr(APUAddrMode.DIRECT_PAGE);
		
		cmpMemToMem(srcAddr, dstAddr);
	}
	
	public static void cmpConstToDP() {
		int srcAddr = APU.loadDataAddr(APUAddrMode.IMMEDIATE);
		int dstAddr = APU.loadDataAddr(APUAddrMode.DIRECT_PAGE);
		
		cmpMemToMem(srcAddr, dstAddr);
	}
	
	// Increment
	
	private static int inc(int x) {
		int r = x + 1;
		
		APU.psw.setNegative((r & 0x80) != 0);
		APU.psw.setZero((r & 0xFF) == 0);
		
		return r;
	}
	
	public static void incReg(Register r) {
		r.setValue(inc(r.getValue()));
	}
	
	public static void incMem(int addr) {
		APUMemory.set(addr, inc(APUMemory.get(addr)));
	}
	
	// Decrement
	
	private static int dec(int x) {
		int r = x - 1;
		
		APU.psw.setNegative((r & 0x80) != 0);
		APU.psw.setZero((r & 0xFF) == 0);
		
		return r;
	}
	
	public static void decReg(Register r) {
		r.setValue(dec(r.getValue()));
	}
	
	public static void decMem(int addr) {
		APUMemory.set(addr, dec(APUMemory.get(addr)));
	}
	
	// Multiply
	
	public static void mul() {
		int r = APU.a.getValue() * APU.y.getValue();
		
		APU.psw.setNegative((APU.y.getValue() & 0x80) != 0);
		APU.psw.setZero(APU.y.getValue() == 0);
		
		APU.setYA(r);
	}
	
	// Divide
	
	/**
	 * Many thanks to bsnes for clarifying things, most of this code is ported from it
	 */
	public static void div() {
		int ya = APU.getYA();
		
		// Overflow set if quotient >= 256
		APU.psw.setOverflow(APU.y.getValue() >= APU.x.getValue());
		APU.psw.setHalfCarry((APU.y.getValue() & 0xF) >= (APU.x.getValue() & 0xF));
		
		if (APU.y.getValue() < (APU.x.getValue() << 1)) {
			// If quotient is <= 511 (fits in 9-bit result)
			APU.a.setValue(ya / APU.x.getValue());
			APU.y.setValue(ya % APU.x.getValue());			
		} else {
			// Otherwise quotient won't fit in A and psw.V
			// Emulate odd behavior of APU
			APU.a.setValue(255 - (ya - (APU.x.getValue() << 9)) / (256 - APU.x.getValue()));
			APU.y.setValue(255 - (ya - (APU.x.getValue() << 9)) % (256 - APU.x.getValue()));
		}
		
		APU.psw.setNegative((APU.a.getValue() & 0x80) != 0);
		APU.psw.setZero(APU.a.getValue() == 0);
	}
}
