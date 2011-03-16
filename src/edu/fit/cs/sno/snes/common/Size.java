package edu.fit.cs.sno.snes.common;

import edu.fit.cs.sno.snes.cpu.CPU;

public enum Size {
	BYTE(0x80, 0xFF),		// 1 Byte value
	SHORT(0x8000, 0xFFFF),	// 2 Byte value
	MEMORY_A(0, 0),	// Determined by memory access flag
	INDEX(0, 0);
	
	public int topBitMask;
	public int sizeMask;
	
	private Size(int topBitMask, int sizeMask) {
		this.topBitMask = topBitMask;
		this.sizeMask = sizeMask;
	}
	
	public int getNumBits() {
		Size s = this.getRealSize();
		if(s == BYTE) {
			return 8;
		} else if (s==SHORT) {
			return 16;
		} else {
			throw new RuntimeException("Unknown size");
		}
	}
	
	public Size getRealSize() {
		if (this == MEMORY_A) {
			if (CPU.status.isMemoryAccess()) {
				return BYTE;
			} else {
				return SHORT;
			}
		} else if (this == INDEX) {
			if (CPU.status.isIndexRegister()) {
				return BYTE;
			} else {
				return SHORT;
			}
		}
		
		return this;
	}
}
