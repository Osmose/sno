package edu.fit.cs.sno.snes.ppu;

public class Sprite {
	public int x;
	public int y;
	public int tileNumber;
	public boolean vflip;
	public boolean hflip;
	public int priority;
	public int paletteNum;
	public boolean name;
	public boolean sizeToggle;
	
	public void loadSprite(int num) {
		int tileAddr = num * 4;
		
		x = OAM.oam[tileAddr];
		y = OAM.oam[tileAddr + 1];
		tileNumber = OAM.oam[tileAddr + 2];
		
		int lastByte = OAM.oam[tileAddr + 3];
		vflip = (lastByte & 0x80) == 0x80;
		hflip = (lastByte & 0x40) == 0x40;
		priority = (lastByte >> 4) & 0x03;
		paletteNum = ((lastByte >> 1) & 0x07);
		name = (lastByte & 0x01) == 1;
		
		// Hightable starts 512 bytes into OAM. Each byte represents 4 objs,
		// so we take the corresponding byte and shift it down based on which
		// sprite in the byte we want (IE obj 15 is in byte (15 / 4) = 3 in 
		// bits 7 and 8, so we shift down by (15 % 4) * 2 = 6 bits to get them.)
		int highTableData = OAM.oam[512 + (num / 4)] >> ((num % 4) * 2);
		sizeToggle = (highTableData & 0x02) != 0;
		x |= (highTableData & 0x01) << 8;
	}
	
	public int getCharacterAddr() {
		int addr = (OAM.getNameBaseSelect() << 14) + 32 * tileNumber;
		if (name) {
			addr += (256 * 32) + (OAM.getNameSelect() << 13);
		}
		
		return addr;
	}
	
	public int getWidth() {
		switch (OAM.getObjectSize()) {
			case 0: return (sizeToggle?16:8);
			case 1: return (sizeToggle?32:8);
			case 2: return (sizeToggle?64:8);
			case 3: return (sizeToggle?32:16);
			case 4: return (sizeToggle?64:16);
			case 5: return (sizeToggle?64:32);
			case 6: return (sizeToggle?32:16);
			case 7: return (sizeToggle?32:16);
		}
		return 8;
	}
	public int getHeight() {
		switch (OAM.getObjectSize()) {
			case 0: return (sizeToggle?16:8);
			case 1: return (sizeToggle?32:8);
			case 2: return (sizeToggle?64:8);
			case 3: return (sizeToggle?32:16);
			case 4: return (sizeToggle?64:16);
			case 5: return (sizeToggle?64:32);
			case 6: return (sizeToggle?64:32);
			case 7: return (sizeToggle?32:32);
		}
		return 8;
	}
}
