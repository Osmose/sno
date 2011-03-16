package edu.fit.cs.sno.snes.apu.ops;

import edu.fit.cs.sno.snes.apu.APU;
import edu.fit.cs.sno.snes.apu.APUAddrMode;
import edu.fit.cs.sno.snes.apu.APUMemory;
import edu.fit.cs.sno.snes.common.Register;

public class Move {

	public static void movMemToReg(Register r) {
		r.setValue(APU.data);
		
		APU.psw.setNegative((APU.data & 0x80) != 0);
		APU.psw.setZero(APU.data == 0);
	}
	
	public static void movRegToMem(Register r) {
		APUMemory.set(APU.dataAddr, r.getValue());
	}
	
	public static void movRegToReg(Register src, Register dst, boolean setFlags) {
		dst.setValue(src.getValue());
		
		APU.psw.setNegative((src.getValue() & 0x80) != 0);
		APU.psw.setZero(src.getValue() == 0);
	}
	
	public static void movDPToDP() {
		int src = APU.loadDataAddr(APUAddrMode.DIRECT_PAGE);
		int dst = APU.loadDataAddr(APUAddrMode.DIRECT_PAGE);
		APUMemory.set(dst, APUMemory.get(src));
	}
	
	public static void movConstToDP() {
		int src = APU.loadDataAddr(APUAddrMode.IMMEDIATE);
		int dst = APU.loadDataAddr(APUAddrMode.DIRECT_PAGE);
		APUMemory.set(dst, APUMemory.get(src));
	}
}
