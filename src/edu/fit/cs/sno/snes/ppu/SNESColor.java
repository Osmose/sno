package edu.fit.cs.sno.snes.ppu;

public enum SNESColor {
	RED(0x1F, 0),
	GREEN(0x3E0, 5),
	BLUE(0x7C00, 10);
	
	private int mask, shift;
	
	private SNESColor(int mask, int shift) {
		this.mask = mask;
		this.shift = shift;
	}
	
	public static int getColor(int value, SNESColor color) {
		return (value & color.mask) >> color.shift;
	}
}
