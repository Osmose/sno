package edu.fit.cs.sno.snes.ppu;

public enum ColorMode {
	Color4(2),
	Color16(4),
	Color256(8),
	Mode7(0);
	
	public int bitDepth;
	
	ColorMode(int bitDepth) {
		this.bitDepth = bitDepth;
	}
	
	@Override
	public String toString() {
		return (int)Math.pow(2,bitDepth) + " colors";
	}
}
