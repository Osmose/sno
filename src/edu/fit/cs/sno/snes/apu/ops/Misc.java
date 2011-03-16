package edu.fit.cs.sno.snes.apu.ops;

import edu.fit.cs.sno.snes.apu.APU;
import edu.fit.cs.sno.snes.apu.APUMemory;
import edu.fit.cs.sno.snes.common.Register;

public class Misc {

	public static void daa() {
		if (APU.psw.isCarry() || APU.a.getValue() > 0x99) {
			APU.a.add(0x60);
		}
		
		if (APU.psw.isHalfCarry() || (APU.a.getValue() & 0xF) > 0x09) {
			APU.a.add(0x06);
		}
		
		APU.psw.setNegative((APU.a.getValue() & 0x80) != 0);
		APU.psw.setZero(APU.a.getValue() == 0);
	}
	
	public static void das() {
		if (!APU.psw.isCarry() || APU.a.getValue() > 0x99) {
			APU.a.subtract(0x60);
		}
		
		if (!APU.psw.isHalfCarry() || (APU.a.getValue() & 0xF) > 0x09) {
			APU.a.subtract(0x06);
		}
		
		APU.psw.setNegative((APU.a.getValue() & 0x80) != 0);
		APU.psw.setZero(APU.a.getValue() == 0);
	}
	
	// Subroutines / Interrupts
	public static void call(int addr) {
		push(APU.pc.getValue() >> 8);
		push(APU.pc.getValue());
		APU.pc.setValue(addr);
	}
	
	public static void pcall(int addr) {
		push(APU.pc.getValue() >> 8);
		push(APU.pc.getValue());
		APU.pc.setValue(addr & 0xFF00);
	}
	
	public static void tcall(int n) {
		int addr = 0xFFDE - (n << 1);
		int jmpTo = APUMemory.getShort(addr);
		
		call(jmpTo);
	}
	
	public static void brk() {
		push(APU.pc.getValue() >> 8);
		push(APU.pc.getValue());
		push(APU.psw.getValue());
		
		APU.pc.setValue(APUMemory.getShort(0xFFDE));
		APU.psw.setBreakFlag(true);
		APU.psw.setIndirectMaster(false);
	}
	
	public static void ret() {
		int addr = pop();
		addr |= pop() << 8;
		
		APU.pc.setValue(addr);
	}
	
	public static void reti() {
		APU.psw.setValue(pop());
		ret();
	}
	
	// Stack
	public static void push(int val) {
		APUMemory.set(0x0100 | APU.sp.getValue(), val);
		APU.sp.subtract(1);
	}
	
	public static void pushReg(Register r) {
		push(r.getValue());
	}
	
	public static int pop() {
		APU.sp.add(1);
		return APUMemory.get(0x0100 | APU.sp.getValue());
	}
	
	public static void popReg(Register r) {
		r.setValue(pop());
	}
	
	public static void stop() {
		while (true) {
			APU.cycle(2);
		}
	}
}
