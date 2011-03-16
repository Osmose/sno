package edu.fit.cs.sno.snes.apu.ops;

import edu.fit.cs.sno.snes.apu.APU;
import edu.fit.cs.sno.snes.apu.APUAddrMode;
import edu.fit.cs.sno.snes.apu.APUMemory;
import edu.fit.cs.sno.snes.common.Register;

public class Logic {

	// AND
	
	private static int and(int x, int y) {
		int r = x & y;
		
		APU.psw.setZero(r == 0);
		APU.psw.setNegative((r & 0x80) != 0);
		
		return r;
	}
	
	public static void andMemToReg(Register reg, int addr) {
		reg.setValue(and(reg.getValue(), APUMemory.get(addr)));
	}
	
	public static void andMemToMem(int srcAddr, int dstAddr) {
		APUMemory.set(dstAddr, and(APUMemory.get(srcAddr), APUMemory.get(dstAddr)));
	}
	
	public static void andIYToIX() {
		int srcAddr = APU.loadDataAddr(APUAddrMode.INDIRECT_Y);
		int dstAddr = APU.loadDataAddr(APUAddrMode.INDIRECT_X);
		
		andMemToMem(srcAddr, dstAddr);
	}
	
	public static void andDPToDP() {
		int srcAddr = APU.loadDataAddr(APUAddrMode.DIRECT_PAGE);
		int dstAddr = APU.loadDataAddr(APUAddrMode.DIRECT_PAGE);
		
		andMemToMem(srcAddr, dstAddr);
	}
	
	public static void andConstToDP() {
		int srcAddr = APU.loadDataAddr(APUAddrMode.IMMEDIATE);
		int dstAddr = APU.loadDataAddr(APUAddrMode.DIRECT_PAGE);
		
		andMemToMem(srcAddr, dstAddr);
	}
	
	// OR
	
	private static int or(int x, int y) {
		int r = x | y;
		
		APU.psw.setZero(r == 0);
		APU.psw.setNegative((r & 0x80) != 0);
		
		return r;
	}
	
	public static void orMemToReg(Register reg, int addr) {
		reg.setValue(or(reg.getValue(), APUMemory.get(addr)));
	}
	
	public static void orMemToMem(int srcAddr, int dstAddr) {
		APUMemory.set(dstAddr, or(APUMemory.get(srcAddr), APUMemory.get(dstAddr)));
	}
	
	public static void orIYToIX() {
		int srcAddr = APU.loadDataAddr(APUAddrMode.INDIRECT_Y);
		int dstAddr = APU.loadDataAddr(APUAddrMode.INDIRECT_X);
		
		orMemToMem(srcAddr, dstAddr);
	}
	
	public static void orDPToDP() {
		int srcAddr = APU.loadDataAddr(APUAddrMode.DIRECT_PAGE);
		int dstAddr = APU.loadDataAddr(APUAddrMode.DIRECT_PAGE);
		
		orMemToMem(srcAddr, dstAddr);
	}
	
	public static void orConstToDP() {
		int srcAddr = APU.loadDataAddr(APUAddrMode.IMMEDIATE);
		int dstAddr = APU.loadDataAddr(APUAddrMode.DIRECT_PAGE);
		
		orMemToMem(srcAddr, dstAddr);
	}
	
	// XOR
	
	private static int xor(int x, int y) {
		int r = x ^ y;
		
		APU.psw.setZero(r == 0);
		APU.psw.setNegative((r & 0x80) != 0);
		
		return r;
	}
	
	public static void xorMemToReg(Register reg, int addr) {
		reg.setValue(xor(reg.getValue(), APUMemory.get(addr)));
	}
	
	public static void xorMemToMem(int srcAddr, int dstAddr) {
		APUMemory.set(dstAddr, xor(APUMemory.get(srcAddr), APUMemory.get(dstAddr)));
	}
	
	public static void xorIYToIX() {
		int srcAddr = APU.loadDataAddr(APUAddrMode.INDIRECT_Y);
		int dstAddr = APU.loadDataAddr(APUAddrMode.INDIRECT_X);
		
		xorMemToMem(srcAddr, dstAddr);
	}
	
	public static void xorDPToDP() {
		int srcAddr = APU.loadDataAddr(APUAddrMode.DIRECT_PAGE);
		int dstAddr = APU.loadDataAddr(APUAddrMode.DIRECT_PAGE);
		
		xorMemToMem(srcAddr, dstAddr);
	}
	
	public static void xorConstToDP() {
		int srcAddr = APU.loadDataAddr(APUAddrMode.IMMEDIATE);
		int dstAddr = APU.loadDataAddr(APUAddrMode.DIRECT_PAGE);
		
		xorMemToMem(srcAddr, dstAddr);
	}
	
}
