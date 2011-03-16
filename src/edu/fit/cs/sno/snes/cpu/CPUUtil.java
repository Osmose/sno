package edu.fit.cs.sno.snes.cpu;


/**
 * Utility functions relating to the CPU
 */
public class CPUUtil {
	
	public static void clearFlags() {

        CPU.status.setCarry(false);
        CPU.status.setZero(false);
        CPU.status.setIrqDisable(false);
        CPU.status.setDecimalMode(false);
        CPU.status.setIndexRegister(false);
        CPU.status.setMemoryAccess(false);
        CPU.status.setOverflow(false);
        CPU.status.setNegative(false);
    }
	
}
