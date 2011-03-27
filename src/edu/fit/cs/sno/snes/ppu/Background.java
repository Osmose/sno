package edu.fit.cs.sno.snes.ppu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import edu.fit.cs.sno.snes.ppu.hwregs.CGRAM;
import edu.fit.cs.sno.util.Settings;

public class Background {
	public int num;
	public boolean tile16px;
	public int tileWidth = 8;
	public int tileHeight = 8;
	public int baseAddress;
	
	public boolean mosaic;
	
	public BGSize size;
	public ColorMode colorMode = ColorMode.Color4;
	public int tileMapAddress;
	
	public boolean enabled = true;
	public boolean userEnabled = true; // User-controlled
	
	public int hscroll = 0;
	public int vscroll = 0;
	public boolean mainScreen;
	public boolean subScreen;
	
	// Windows
	public boolean windowMaskMain = false;
	public boolean windowMaskSub = false;
	public boolean window1Enabled = false;
	public boolean window2Enabled = false;
	public boolean window1Invert = false;
	public boolean window2Invert = false;
	public int windowOp;
	
	// x/y position of the pixel we're currently drawing
	private int x;
	private int y;
	private int baseY;
	
	private int tile;		// Stores the content of the current tile entry
	private int tileAddr;	// Address of current tile
	private int tilePaletteOffset;	// Palette offset for the current tile
	
	private int characterAddr;	// Address of current character data
	
	// Priority values
	public int priority0;
	public int priority1;
	public int curPriority;	// Priority of the current tile
	
	// Which background number this is
	public Background(int number) {
		num = number;
		
		size = BGSize.bg32x32;
	}
	
	/**
	 * Calculates the offset of the palette colors for the current tile
	 * 
	 * @return
	 */
	private int getPaletteOffset() {
		switch(colorMode) {
			case Color4:
				return ((PPU.vram[tileAddr+1] >> 2) & 0x7) * 4;
			case Color16:
				return ((PPU.vram[tileAddr+1] >> 2) & 0x7) * 16;
			default:
				return ((PPU.vram[tileAddr+1] >> 2) & 0x7);
		}
	}
	
	public void setTileSize(boolean tileSize) {
		tile16px = tileSize;
		tileWidth = tile16px ? 16 : 8;
		tileHeight = tileWidth;
	}

	public void setHScroll(int scrollvalue) {
		hscroll = scrollvalue;
	}
	public void setVScroll(int scrollvalue) {
		vscroll = scrollvalue;
	}
	
	/**
	 * Resets x and y to the beginning of the next scanline and loads
	 * the first tile in the scanline
	 */
	public void nextScanline() {
		// Mod to wrap scrolling
		baseY = (baseY + 1);
		y = (baseY + getVScroll())  % (size.height * tileHeight);
		x = getHScroll();
		loadTile();
	}
	
	/**
	 * Calculates the address of the current tile using x and y and loads
	 * it into tile. Also processes some attributes of the current tile.
	 */
	public void loadTile() {
		// Get x/y position of the tile we want
		int xTilePos = x / tileWidth;
		int yTilePos = y / tileHeight;
		
		// Tile is relative to base address
		tileAddr = tileMapAddress;
		
		// Add 2 bytes per x tile 
		tileAddr += ((xTilePos % 32) * 2);
		
		// If we are on the right 32 tiles, add 0x800 bytes
		tileAddr += (xTilePos >= 32 ? 0x800 : 0);
		
		// Add 64 bytes per y tile 
		tileAddr += ((yTilePos % 32) * 64);
		
		// Add extra if we are on the bottom half
		if (yTilePos >= 32) {
			// 32x64 uses B as the bottom half, 64x64 uses C/D
			if (size == BGSize.bg32x64) {
				tileAddr += 0x800;
			} else {
				tileAddr += 0x1000;
			}
		}
		
		// Load the tile
		tile = PPU.vram[tileAddr] | (PPU.vram[tileAddr + 1] << 8);

		// yOffset is the number of bytes we have to skip to get to the correct
		// line for rendering a tile. Each horizontal line in a character is 2 bytes long
		int yOffset = ((y % tileHeight) * 2);
		yOffset = ((tile & 0x8000) != 0 ? ((tileHeight * 2) - yOffset - 2) : yOffset);	// Vertical flip
		
		// Base address for character data
		characterAddr = baseAddress + (8 * colorMode.bitDepth * (tile & 0x3FF));
		characterAddr += yOffset; // Mod for y offset within sprite
		
		// If we are in the bottom half of a 16x16 tile, add another row of 16 tiles
		// to get to the bottom half
		if (tile16px && (y % 16 >= 8)) {
			characterAddr += 16 * (8 * colorMode.bitDepth);
		}
		
		tilePaletteOffset = getPaletteOffset();
		
		// Tiles can choose between one of two priorities
		curPriority = (((tile >> 13) & 1) != 0 ? priority1 : priority0);
	}
	
	/**
	 * Loads and outputs the current pixel and increments x to move to the next
	 * pixel in the scanline.
	 */
	public int loadPixel() {
		int index = 0;
		
		// Determine x offset for pixel we want
		int pixelX = x % tileWidth;
		if ((tile & 0x4000) != 0) { // Horizontal flip
			pixelX = tileWidth - pixelX -1;
		}
		
		// Grab the pixel
		int xMask = 0x80 >> (pixelX % 8);
		switch (colorMode) {
			case Color4:
				index |= ((PPU.vram[characterAddr] & xMask) != 0) ? 0x1 : 0;
				index |= ((PPU.vram[characterAddr + 1] & xMask) != 0) ? 0x2 : 0;
				break;
			case Color16:
				index |= ((PPU.vram[characterAddr] & xMask) != 0) ? 0x1 : 0;
				index |= ((PPU.vram[characterAddr + 1] & xMask) != 0) ? 0x2 : 0;
				index |= ((PPU.vram[characterAddr + 16] & xMask) != 0) ? 0x4 : 0;
				index |= ((PPU.vram[characterAddr + 17] & xMask) != 0) ? 0x8 : 0;
				break;
			case Color256:
				index |= ((PPU.vram[characterAddr] & xMask) != 0) ? 0x1 : 0;
				index |= ((PPU.vram[characterAddr + 1] & xMask) != 0) ? 0x2 : 0;
				index |= ((PPU.vram[characterAddr + 16] & xMask) != 0) ? 0x4 : 0;
				index |= ((PPU.vram[characterAddr + 17] & xMask) != 0) ? 0x8 : 0;
				index |= ((PPU.vram[characterAddr + 32] & xMask) != 0) ? 0x10 : 0;
				index |= ((PPU.vram[characterAddr + 33] & xMask) != 0) ? 0x20 : 0;
				index |= ((PPU.vram[characterAddr + 48] & xMask) != 0) ? 0x40 : 0;
				index |= ((PPU.vram[characterAddr + 49] & xMask) != 0) ? 0x80 : 0;
				break;
		}
		
		// Don't output transparent
		if (index != 0) {
			// Output on main screen
			if (mainScreen && curPriority > PPU.priorityMain) {
				PPU.priorityMain = curPriority;
				PPU.colorMain = index + tilePaletteOffset;
				PPU.sourceMain = num;
			}
			
			// Output on subscreen
			if (subScreen && curPriority > PPU.prioritySub) {
				PPU.prioritySub = curPriority;
				PPU.colorSub = index + tilePaletteOffset;
				PPU.sourceSub = num;
			}
		}
		
		// Move to next pixel, wrapping in case we scrolled off the edge of the screen
		x = (x + 1) % (size.width*tileWidth);
		
		// If we have processed 8 pixels (or 16 for a 16x16 tile),
		// move to the next tile.
		if (((x % 8) == 0 && !tile16px) || (x % 16) == 0) {
			loadTile();
		} else if ((x % 16) == 8 && tile16px) {	// Move to the right half of a 16x16 tile
			characterAddr += 8 * colorMode.bitDepth;
		}
		return (index==0 ? 0: index + tilePaletteOffset);
	}
	
	/**
	 * Resets the x and y values to 0 (handling scrolling)
	 * and load the first tile of scanline 0
	 */
	public void vBlank() {
		x = getHScroll();
		baseY = 0;
		y = baseY + getVScroll() % (size.height*tileHeight);
		
		loadTile();
	}
	
	// Scrolls a value for display on the screen
	private int getHScroll() {
		return (this.hscroll & 0x03FF) % (size.width*tileWidth);// only 10 bits count
	}
	
	private int getVScroll() {
		return vscroll & 0x03FF % (size.height*tileHeight);// only 10 bits count
	}
	
	public boolean enabled() {
		return enabled && userEnabled;
	}
	
	public void dumpBGImage() {
		String baseDir = Settings.get(Settings.DEBUG_PPU_DIR);
		int oldx = x;
		int oldy = y;
		
		BufferedImage img = new BufferedImage(size.width*tileWidth, size.height*tileHeight, BufferedImage.TYPE_INT_ARGB);
		
		for (y=0;y<size.height*tileHeight; y++) {
			for (x=0;x<size.width*tileWidth; x++) {
				int ox=x;
				int oy=y;
				loadTile();
				int c = CGRAM.getColor(loadPixel());
				
				x=ox;
				y=oy;
				
				
				int r, g, b, realColor;
				r = ((int) SNESColor.getColor(c, SNESColor.RED) & 0x1F) << 19;
				g = ((int) SNESColor.getColor(c, SNESColor.GREEN) & 0x1F) << 11;
				b = ((int) SNESColor.getColor(c, SNESColor.BLUE) & 0x1F) << 3;
				realColor = (0xFF << 24) | r | g | b;
				
				// Write to the screenbuffer, adjusting for the 22 unused pixels at the start of the scanline
				img.setRGB(x, y, realColor);
			}
		}
		
		try {
			ImageIO.write(img, "PNG", new File(baseDir + "/bg" + num + "_img.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		x = oldx;
		y = oldy;
		loadTile();
	}
	
	public void dumpBGGraphics() {
		String baseDir = Settings.get(Settings.DEBUG_PPU_DIR);
		Color[] colors = new Color[] {
			new Color(0, 0, 0),
			new Color(0, 0, 127),
			new Color(0, 0, 255),
			new Color(0, 127, 0),
			new Color(0, 127, 127),
			new Color(0, 127, 255),
			new Color(0, 255, 0),
			new Color(0, 255, 127),
			new Color(0, 255, 255),
			new Color(127, 0, 0),
			new Color(127, 0, 127),
			new Color(127, 0, 255),
			new Color(127, 127, 0),
			new Color(127, 127, 127),
			new Color(127, 127, 255),
			new Color(127, 255, 0)
		};
		
		BufferedImage img = new BufferedImage(128, 512, BufferedImage.TYPE_INT_RGB);
		for (int k = 0; k < 64; k++) {
			for (int m = 0; m < 16; m++) {
				int spriteNum = (k * 16) + m;
				for (int y = 0; y < 8; y++) {
					for (int x = 0; x < 8; x++) {
						int addr = baseAddress + (spriteNum * 8 * colorMode.bitDepth) + (y * 2);
						int xMask = 0x80 >> x;
						int index = 0;
						switch (colorMode) {
							case Color4:
								index |= ((PPU.vram[addr] & xMask) != 0) ? 0x1 : 0;
								index |= ((PPU.vram[addr + 1] & xMask) != 0) ? 0x2 : 0;
								break;
							case Color16:
								index |= ((PPU.vram[addr] & xMask) != 0) ? 0x1 : 0;
								index |= ((PPU.vram[addr + 1] & xMask) != 0) ? 0x2 : 0;
								index |= ((PPU.vram[addr + 16] & xMask) != 0) ? 0x4 : 0;
								index |= ((PPU.vram[addr + 17] & xMask) != 0) ? 0x8 : 0;
								break;
							case Color256:
								index |= ((PPU.vram[addr] & xMask) != 0) ? 0x1 : 0;
								index |= ((PPU.vram[addr + 1] & xMask) != 0) ? 0x2 : 0;
								index |= ((PPU.vram[addr + 16] & xMask) != 0) ? 0x4 : 0;
								index |= ((PPU.vram[addr + 17] & xMask) != 0) ? 0x8 : 0;
								index |= ((PPU.vram[addr + 32] & xMask) != 0) ? 0x10 : 0;
								index |= ((PPU.vram[addr + 33] & xMask) != 0) ? 0x20 : 0;
								index |= ((PPU.vram[addr + 48] & xMask) != 0) ? 0x40 : 0;
								index |= ((PPU.vram[addr + 49] & xMask) != 0) ? 0x80 : 0;
								break;
						}
						if (colorMode != ColorMode.Color256) {
							img.setRGB(x + (m * 8), y + (k * 8), colors[index].getRGB());
						} else {
							img.setRGB(x + (m * 8), y + (k * 8), CGRAM.getColor(index));
						}
					}
				}
			}
		}

		try {
			ImageIO.write(img, "PNG", new File(baseDir + "/bg" + num + "Dump.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		StringBuffer ret = new StringBuffer();
		ret.append("Background " + num + "\n");
		ret.append(String.format("  Map Address:  0x%04X\n", tileMapAddress));
		ret.append(String.format("  Char Address: 0x%04X\n", baseAddress));
		ret.append(String.format("  HOffset: %d\n", getHScroll()));
		ret.append(String.format("  VOffset: %d\n", getVScroll()));
		ret.append(String.format("  Tile size:    %s\n", (tile16px?"16x16":"8x8")));
		ret.append(String.format("  Mosaic Mode:  %s\n", (mosaic?"true":"false")));
		ret.append(String.format("  TileMap Size: %s\n", size));
		ret.append(String.format("  Colors:       %s\n", colorMode));
		ret.append(String.format("  Main screen:  %s\n", (mainScreen?"true":"false")));
		ret.append(String.format("  Sub screen:   %s\n", (subScreen?"true":"false")));
		return ret.toString();
	}
}