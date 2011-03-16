package edu.fit.cs.sno.snes.ppu;

public enum BGSize {
	bg32x32(32, 32),
	bg64x32(64, 32),
	bg32x64(32, 64),
	bg64x64(64, 64);
	
	public int width, height;
	
	private BGSize(int w, int h) {
		width = w;
		height = h;
	}
	
	public static BGSize toBGSize(int num) {
		switch (num) {
			case 0: return bg32x32;
			case 1: return bg64x32;
			case 2: return bg32x64;
			case 3: return bg64x64;
			default: return bg32x32;
		}
	}
	
	public String toString() {
		switch (this) {
			case bg32x32: return "32x32";
			case bg64x32: return "64x32";
			case bg32x64: return "32x64";
			case bg64x64: return "64x64";
			default: return "";
		}
	}
}
