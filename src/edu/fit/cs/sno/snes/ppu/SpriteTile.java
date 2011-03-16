package edu.fit.cs.sno.snes.ppu;


public class SpriteTile {
	public int addr;
	public int x;
	public int y;
	public int priority;
	public int paletteOffset;
	public boolean hflip;
	public boolean vflip;
	public boolean enabled;
	
	public int getPixel(int nx, int ny) {
		if (hflip) nx = 7 - nx;
		if (vflip) ny = 7 - ny;
		
		int val = 0;
		int rowAddr = addr + (2 * ny);
		int xMask = 0x80 >> nx;
		val |= ((PPU.vram[rowAddr] & xMask) != 0) ? 0x01 : 0;
		val |= ((PPU.vram[rowAddr + 1] & xMask) != 0) ? 0x02 : 0;
		val |= ((PPU.vram[rowAddr + 16] & xMask) != 0) ? 0x04 : 0;
		val |= ((PPU.vram[rowAddr + 17] & xMask) != 0) ? 0x08 : 0;
		
		return val;
	}
}
