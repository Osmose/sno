package edu.fit.cs.sno.snes.ppu;

import edu.fit.cs.sno.util.Util;

public class OAM {
	static int oam[] = new int[544]; // 544 bytes for the OAM
	static Sprite spriteTable[] = new Sprite[128];
	
	static int lastWriteAddress;
	static int internalOAMAddress;
	static boolean lastPriority;
	static boolean priority;
	
	private static int objectSize;
	private static int nameSelect;
	private static int nameBaseSelect;
	
	public static boolean userEnabled = true;
	
	// Windows
	public static boolean windowMaskMain = false;
	public static boolean windowMaskSub = false;
	public static boolean window1Enabled = false;
	public static boolean window2Enabled = false;
	public static boolean window1Invert = false;
	public static boolean window2Invert = false;
	public static int windowOp;
	
	// Main/sub screen drawing enables
	public static boolean mainScreen;
	public static boolean subScreen;
	
	// Stores the 4 possible priorities for sprites
	public static int[] priorityMap = new int[4];
	
	// curSprites stores all 32 of the currently loaded sprites
	private static Sprite[] curSprites = new Sprite[32];
	private static int numSprites;
	
	// curTiles stores the 34 possible tiles being drawn
	private static SpriteTile[] curTiles = new SpriteTile[34];
	private static int numTiles;
	
	static {
		for (int i=0;i<128;i++) {
			spriteTable[i] = new Sprite();	
		}
		for (int k = 0; k < 32; k++) {
			curSprites[k] = new Sprite();
		}
		for (int k = 0; k < 34; k++) {
			curTiles[k] = new SpriteTile();
		}
	}
	
	/**
	 * Cycles through each active tile and checks if it has a pixel at the 
	 * current x/y position on the screen, and outputs if one is found. Called
	 * once per on-screen pixel by PPU
	 */
	public static void loadPixel() {
		SpriteTile curTile;
		
		// For each active tile
		for (int k = 0; k < numTiles; k++) {
			curTile = curTiles[k];
			
			// Check if the tile overlaps the current pixel. Subtract 22 to handle offset
			// of 22 pixels at start of scanline
			if (Util.inRange(PPU.x - 22, curTile.x, curTile.x + 7)) {
				int color = curTile.getPixel((PPU.x - 22) - curTile.x, PPU.y - 1 - curTile.y);
				
				// Don't draw transparent pixels or when we're disabled
				if (color == 0 || !userEnabled) continue;
				
				// Masking check
				boolean masked = Window.checkSpriteMask();
				boolean mainMask = windowMaskMain && masked;
				boolean subMask = windowMaskSub && masked;
				
				if (mainScreen && !mainMask && curTile.priority > PPU.priorityMain) {
					PPU.colorMain = color + curTile.paletteOffset;
					PPU.priorityMain = curTile.priority;
					PPU.sourceMain = PPU.SRC_OAM;
				}
				
				if (subScreen && !subMask && curTile.priority > PPU.prioritySub) {
					PPU.colorSub = color + curTile.paletteOffset;
					PPU.prioritySub = curTile.priority;
					PPU.sourceSub = PPU.SRC_OAM;
				}
				
				// Break out if both layers have been written
				if ((!mainScreen || PPU.sourceMain == PPU.SRC_OAM) && (!subScreen || PPU.sourceSub == PPU.SRC_OAM)) break;
			}
		}
	}
	
	/**
	 * Loads the first 32 sprites found on the current scanline. From these, load
	 * the first 34 tiles from the sprites (IE only 17 16x16 sprites can be displayed
	 * on a scanline).
	 * 
	 * @param y Current y value of PPU (AKA current scanline)
	 */
	public static void scanline(int y) {
		int firstSprite;
		if (priority) {
			// TODO: Handle odd case of writing to last byte in sprite. See anomie's doc.
			firstSprite = (internalOAMAddress & 0xFE) << 1;
		} else {
			firstSprite = 0;
		}
		
		// Loop through each sprite, finding the first 32 on the scanline
		numSprites = 0;
		for (int k = 0; k < 127; k++) {
			curSprites[numSprites].loadSprite((firstSprite + k) % 128);
			if (onScanline(curSprites[numSprites], y)) {
				numSprites++;
				if (numSprites >= 32) break; // TODO: Mark register 0x213E if >32 sprites on line
			}
		}
		
		numTiles = 0;
		SpriteTile curTile = curTiles[0];
		Sprite curSprite;
		
		// Loop through the sprites in reverse and load up to 34 tiles
		loadTiles:
		for (int k = 0; k < numSprites; k++) {
			curSprite = curSprites[k];
			
			// yOffset is the pixel row we want within the sprite
			int yOffset = (y - curSprite.y);
			if (curSprite.vflip) {
				yOffset = curSprite.getHeight() - 1 - yOffset;
			}
			
			// yTileOffset is the tile row we want within the sprite (0 = first
			// row of tiles, 1 = second row, and so on)
			int yTileOffset = (yOffset / 8);
			
			// For every tile in this sprite on the scanline
			for (int m = 0; m < curSprites[k].getWidth(); m += 8) {
				// Load tile data
				curTile.x = curSprite.x + m;
				curTile.priority = priorityMap[curSprite.priority];
				curTile.paletteOffset = 128 + (curSprite.paletteNum << 4);
				curTile.vflip = curSprite.vflip;
				
				// 512 = number of bytes per row of tiles
				curTile.addr = curSprite.getCharacterAddr() + (512 * yTileOffset) + (m * 4);
				
				// Handle vertical flip
				if (curSprite.vflip) {
					curTile.y = curSprite.y + (curSprite.getWidth() - 8 - (yTileOffset * 8));
				} else {
					curTile.y = curSprite.y + (yTileOffset * 8);
				}
				
				// TODO: Ensure tiles are loaded left to right after flipping
				// Handle horizontal flip
				if (curSprite.hflip) {
					curTile.hflip = true;
					curTile.x = curSprite.x + (curSprite.getWidth() - 8 - m);
				} else {
					curTile.hflip = false;
				}
				
				// Move to the next open spot of tiles to load
				numTiles++;
				if (numTiles >= 34) {
					break loadTiles;
				} else {
					curTile = curTiles[numTiles];
				}
			}
		}
	}
	
	/**
	 * Resets the OAM address and loads scanline 0
	 */
	public static void vBlank() {
		resetOAMAddress();
	}
	
	/**
	 * Returns true if sprite t overlaps the scanline
	 * @param t
	 * @param scanline
	 * @return
	 */
	public static boolean onScanline(Sprite t, int scanline) {
		if (t.x + t.getWidth() <= 0) return false;
		if (t.x > PPU.screenWidth) return false;
		if (t.y + t.getHeight() <= scanline) return false;
		if (t.y > scanline) return false;
		
		return true;
	}
	
	/**
	 * Loads information about the 128 sprites from RAM
	 */
	public static void parseOAM() {
		int s = 0;
		for (int i=0;i<512;i+=4) {
			spriteTable[s].x = oam[i];
			spriteTable[s].y = oam[i+1];
			spriteTable[s].tileNumber = oam[i+2];
			int lastByte = oam[i+3];
			spriteTable[s].vflip = (lastByte & 0x80) == 0x80;
			spriteTable[s].hflip = (lastByte & 0x40) == 0x40;
			spriteTable[s].priority = (lastByte >> 4) & 0x03;
			spriteTable[s].paletteNum = ((lastByte >> 1) & 0x07);
			spriteTable[s].name = (lastByte & 0x01) == 1;
			s++;
		}
		s=0;
		for (int i = 512; i < 544; i++) {
			int data = oam[i];
			for(int j = 0; j < 8; j += 2) {
				int info = (data >> j) & 0x03;
				spriteTable[s].sizeToggle = (((info>>1) & 0x01) == 0x01);
				spriteTable[s].x = (spriteTable[s].x) | ((info & 0x01)<<8);
				s++;
			}
		}
	}
	
	public static void dumpTiles() {
		for (int k = 0; k < numTiles; k++) {
			System.out.println(curTiles[k].toString());
		}
	}
	
	public static void resetOAMAddress() {
		internalOAMAddress = lastWriteAddress;
		priority = lastPriority;
	}

	public static void updateAddress(int addr, boolean pri) {
		lastPriority = pri;
		priority = pri;
		lastWriteAddress = addr;
		internalOAMAddress = addr;
	}
	
	// TODO: enforce writing only when vblank or forced blank period
	public static void writeOAM(int value) {
		oam[internalOAMAddress] = value & 0xFF;
		internalOAMAddress++;
		// TODO: error/bounds checking
		// only updates the table when the high byte is written...so you can write low bytes without affecting the table
	}
	public static int readOAM() {
		int ret = oam[internalOAMAddress];
		internalOAMAddress++;
		return ret;
	}
	
	public static int getObjectSize() { return objectSize; }
	public static int getNameSelect() { return nameSelect; }
	public static int getNameBaseSelect() { return nameBaseSelect;}

	public static void setNameSelect(int ns) { nameSelect = ns; }
	public static void setObjectSize(int os) { objectSize = os; }
	public static void setNameBaseSelect(int nbs) { nameBaseSelect = nbs; }

}
