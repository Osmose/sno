package edu.fit.cs.sno.snes.ppu.background;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import edu.fit.cs.sno.snes.mem.MemoryObserver;
import edu.fit.cs.sno.snes.ppu.ColorMode;
import edu.fit.cs.sno.snes.ppu.PPU;
import edu.fit.cs.sno.snes.ppu.SNESColor;
import edu.fit.cs.sno.snes.ppu.Window;
import edu.fit.cs.sno.snes.ppu.hwregs.BGRegisters;
import edu.fit.cs.sno.snes.ppu.hwregs.CGRAM;
import edu.fit.cs.sno.util.Settings;
import edu.fit.cs.sno.util.Util;

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
	//private int tileAddr;	// Address of current tile
	private int tilePaletteOffset;	// Palette offset for the current tile
	
	//private int characterAddr;	// Address of current character data
	
	
	private int xTilePos=0;
	private int yTilePos=0;
	private int pixelY = 0;
	private int cacheTilemap[][];
	private int cacheChardata[][][];
	
	// Priority values
	public int priority0;
	public int priority1;
	public int curPriority;	// Priority of the current tile
	
	private BGTilemapMemoryObserver tilemapMemoryObserver;
	private BGCharDataMemoryObserver charDataMemoryObserver;
	
	// Which background number this is
	public Background(int number) {
		num = number;
		
		size = BGSize.bg32x32;
		
		cacheTilemap = new int[64][64];
		for (int i=0;i<64;i++)
			for(int j=0;j<64;j++)
				cacheTilemap[i][j] = 0;
		cacheChardata = new int[4096][16][16]; // 1024 "tiles", size 16x16max
		for (int i=0;i<4096;i++)
			for(int j=0;j<16;j++)
				for(int k=0;k<16;k++)
					cacheChardata[i][j][k]=0;
		
		tilemapMemoryObserver = new BGTilemapMemoryObserver(this);
		charDataMemoryObserver = new BGCharDataMemoryObserver(this);
		
		MemoryObserver.addObserver(tilemapMemoryObserver);
		MemoryObserver.addObserver(charDataMemoryObserver);
	}
	
	/**
	 * Calculates the offset of the palette colors for the current tile
	 * 
	 * @return
	 */
	private int getPaletteOffset() {
		switch(colorMode) {
			case Color4:
				return ((tile >> 10) & 0x7) * 4;
			case Color16:
				return ((tile >> 10) & 0x7) * 16;
			default:
				return ((tile >> 10) & 0x7);
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
		xTilePos = x / tileWidth;
		yTilePos = y / tileHeight;
		
		// Load the tile(from cache)
		tile = cacheTilemap[xTilePos][yTilePos];
		
		if (tile==0)
			tile = cacheTilemap[xTilePos][yTilePos] = PPU.vram[tileMapAddress] | (PPU.vram[tileMapAddress+1]<<8);
		
		pixelY = y%tileHeight;
		if ((tile & 0x8000) != 0) { // Vertical flip
			pixelY = tileHeight - pixelY - 1;
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
		
		if (colorMode == ColorMode.Mode7) return mode7Run();
		
		// Determine x offset for pixel we want
		int pixelX = x % tileWidth;
		if ((tile & 0x4000) != 0) { // Horizontal flip
			pixelX = tileWidth - pixelX -1;
		}
		
		// Get the pixel color
		index = cacheChardata[(tile & 0x3FF)][pixelX][pixelY];
		
		// Don't output transparent or when we're disabled
		if (index != 0 && enabled()) {
			// Masking check
			boolean masked = Window.checkBackgroundMask(this);
			boolean mainMask = windowMaskMain && masked;
			boolean subMask = windowMaskSub && masked;
			
			// Output on main screen
			if (mainScreen && !mainMask && curPriority > PPU.priorityMain) {
				PPU.priorityMain = curPriority;
				PPU.colorMain = index + tilePaletteOffset;
				PPU.sourceMain = num - 1;
			}
			
			// Output on subscreen
			if (subScreen && !subMask &&  curPriority > PPU.prioritySub) {
				PPU.prioritySub = curPriority;
				PPU.colorSub = index + tilePaletteOffset;
				PPU.sourceSub = num - 1;
			}
		}
		
		// Move to next pixel, wrapping in case we scrolled off the edge of the screen
		x = (x + 1) % (size.width*tileWidth);

		// If we have processed 8 pixels (or 16 for a 16x16 tile),
		// move to the next tile.
		if (((x % 8) == 0 && !tile16px) || (x % 16) == 0) {
			loadTile();
		}
		return (index==0 ? 0: index + tilePaletteOffset);
	}
	
	/**
	 * Ported from bsnes
	 */
	public int mode7Run() {
		int a = Util.sclip(16, BGRegisters.m7a.val);
		int b = Util.sclip(16, BGRegisters.m7b.val);
		int c = Util.sclip(16, BGRegisters.m7c.val);
		int d = Util.sclip(16, BGRegisters.m7d.val);
		
		int cx = Util.sclip(13, BGRegisters.m7x.val);
		int cy = Util.sclip(13, BGRegisters.m7y.val);
		int hoffset = Util.sclip(13, PPU.m7HOffset);
		int voffset = Util.sclip(13, PPU.m7VOffset);
		
		int tempX = PPU.x - 22;
		int tempY = PPU.y;
		
		if (PPU.m7XFlip) tempX = 255 - tempX;
		if (PPU.m7YFlip) tempY = 255 - tempY;
		
		int psx = ((a * m7Clip(hoffset - cx)) & (~63)) + ((b * m7Clip(voffset - cy)) & (~63)) + ((b * tempY) & (~63)) + (cx << 8);
		int psy = ((c * m7Clip(hoffset - cx)) & (~63)) + ((d * m7Clip(voffset - cy)) & (~63)) + ((d * tempY) & (~63)) + (cy << 8);
		
		int px = (psx + (a * tempX)) >> 8;
		int py = (psy + (c * tempX)) >> 8;
		
		int tile = 0, palette = 0, priority = 0;
		switch (PPU.m7Repeat) {
			// Screen repetition outside of screen area
			case 0:
			case 1:
				px &= 1023;
				py &= 1023;
				tile = PPU.vram[((py >> 3) * 128 + (px >> 3)) << 1];
				palette = PPU.vram[(((tile << 6) + ((py & 7) << 3) + (px & 7)) << 1) + 1];
				break;
				
			// Palette color 0 outside of screen area
			case 2:
				if(((px | py) & (~1023)) != 0) {
					palette = 0;
				} else {
					px &= 1023;
					py &= 1023;
					tile = PPU.vram[((py >> 3) * 128 + (px >> 3)) << 1];
					palette = PPU.vram[(((tile << 6) + ((py & 7) << 3) + (px & 7)) << 1) + 1];
				}
				break;
				
			// Character 0 repetition outside of screen area
			case 3:
				if(((px | py) & (~1023)) != 0) {
					tile = 0;
				} else {
					px &= 1023;
					py &= 1023;
					tile = PPU.vram[((py >> 3) * 128 + (px >> 3)) << 1];
				}
				palette = PPU.vram[(((tile << 6) + ((py & 7) << 3) + (px & 7)) << 1) + 1];
				break;
		}
		
		if (num == 1) {
			priority = priority0;
		} else if (num == 2) {
			priority = ((palette & 0x80) != 0 ? priority1 : priority0);
		    palette &= 0x7f;
		}
		
		// Don't output transparent or when we're disabled
		if (palette != 0 && enabled()) {
			// Masking check
			boolean masked = Window.checkBackgroundMask(this);
			boolean mainMask = windowMaskMain && masked;
			boolean subMask = windowMaskSub && masked;
			
			// Output on main screen
			if (mainScreen && !mainMask && priority > PPU.priorityMain) {
				PPU.priorityMain = priority;
				PPU.colorMain = palette;
				PPU.sourceMain = num - 1;
			}
			
			// Output on subscreen
			if (subScreen && !subMask &&  priority > PPU.prioritySub) {
				PPU.prioritySub = priority;
				PPU.colorSub = palette;
				PPU.sourceSub = num - 1;
			}
		}
		
		// Move to next pixel
		x++;
		
		return palette;
	}
	
	/**
	 * 13 bit sign extend; ported from bsnes
	 */
	private int m7Clip(int a) {
		if ((a & 0x2000) != 0) {
			return a | (~0x3ff);
		} else {
			return a & 0x3ff;
		}
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

	// Rebuild the cachedata for the actual tilemap
	void rebuildTilemap(int addr) {
		// Make sure we only operate on addresses in our range(to prevent errors)
		if (addr >= tileMapAddress+0x2000){
			return;
		}
		int origAddr = addr;
		addr -= (addr%2);
		int taddr = addr;
		
		// Figure out which x/y tile this is(so we know where to place the data in the cache)
		int tileX = 0;
		int tileY = 0;
		addr -= tileMapAddress;
		switch(size) {
			case bg32x32: // No adjustment
				break;
			case bg32x64:
				if (addr >= 0x800) { tileY += 32; addr -= 0x0800;}
				break;
			case bg64x32:
				if (addr >= 0x800) { tileX += 32; addr -= 0x0800;}
				break;
			case bg64x64:
				if (addr >= 0x1800)      { tileX += 32; tileY += 32; addr -= 0x1800;}
				else if (addr >= 0x1000) { tileY += 32; addr -= 0x1000;}
				else if (addr >= 0x0800) { tileX += 32; addr -= 0x0800;}
				break;
		}
		
		tileY += addr/64;
		addr %= 64;
		tileX += addr/2;
		
		cacheTilemap[tileX][tileY] = PPU.vram[taddr] | (PPU.vram[taddr + 1] << 8);
	}
	// Repopulates the cachedata for the actual tiles
	void rebuildChardata(int addr) {
		// Figure out which character tile this will affect
		int characterNumber = (addr - baseAddress);
		int charOffset = characterNumber % (8 * colorMode.bitDepth);
		
		
		int characterBaseAddress = addr - charOffset;
		characterNumber /= (8*colorMode.bitDepth);
		
		int charY = charOffset / 2;
		int charX = charOffset % 8;
		
		// Re-render the entire tile
		for(int pixelY=0; pixelY<tileHeight; pixelY++) {
			if (pixelY==8)
				characterBaseAddress += 16*8*colorMode.bitDepth;
			for (int pixelX=0; pixelX<tileWidth; pixelX++) {
				if (pixelX==8)
					characterBaseAddress += 8*colorMode.bitDepth;
				
				int cIndex = 0;
				
				// Grab the pixel
				int xMask = 0x80 >> (pixelX % 8);
				switch (colorMode) {
					case Color4:
						cIndex |= ((PPU.vram[characterBaseAddress] & xMask) != 0) ? 0x1 : 0;
						cIndex |= ((PPU.vram[characterBaseAddress + 1] & xMask) != 0) ? 0x2 : 0;
						break;
					case Color16:
						cIndex |= ((PPU.vram[characterBaseAddress] & xMask) != 0) ? 0x1 : 0;
						cIndex |= ((PPU.vram[characterBaseAddress + 1] & xMask) != 0) ? 0x2 : 0;
						cIndex |= ((PPU.vram[characterBaseAddress + 16] & xMask) != 0) ? 0x4 : 0;
						cIndex |= ((PPU.vram[characterBaseAddress + 17] & xMask) != 0) ? 0x8 : 0;
						break;
					case Color256:
						cIndex |= ((PPU.vram[characterBaseAddress] & xMask) != 0) ? 0x1 : 0;
						cIndex |= ((PPU.vram[characterBaseAddress + 1] & xMask) != 0) ? 0x2 : 0;
						cIndex |= ((PPU.vram[characterBaseAddress + 16] & xMask) != 0) ? 0x4 : 0;
						cIndex |= ((PPU.vram[characterBaseAddress + 17] & xMask) != 0) ? 0x8 : 0;
						cIndex |= ((PPU.vram[characterBaseAddress + 32] & xMask) != 0) ? 0x10 : 0;
						cIndex |= ((PPU.vram[characterBaseAddress + 33] & xMask) != 0) ? 0x20 : 0;
						cIndex |= ((PPU.vram[characterBaseAddress + 48] & xMask) != 0) ? 0x40 : 0;
						cIndex |= ((PPU.vram[characterBaseAddress + 49] & xMask) != 0) ? 0x80 : 0;
						break;
				}
				cacheChardata[characterNumber][pixelX][pixelY] = cIndex;
			}
			characterBaseAddress+=2;
		}
	}
}