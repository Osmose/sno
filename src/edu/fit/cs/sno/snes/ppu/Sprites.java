package edu.fit.cs.sno.snes.ppu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import edu.fit.cs.sno.snes.ppu.hwregs.CGRAM;
import edu.fit.cs.sno.util.Settings;
import edu.fit.cs.sno.util.Util;

public class Sprites {

	public static void dumpOBJ() {
		OAM.parseOAM(); // Update the sprite object table
		String baseDir = Settings.get(Settings.DEBUG_PPU_DIR);
		BufferedImage asprites = new BufferedImage(16*16*8, 16*16*8, BufferedImage.TYPE_INT_RGB);
		Graphics allSprites = asprites.getGraphics();
		for (int i=0; i<128; i++) {
			Sprite t = OAM.spriteTable[i];
			String fname = baseDir + String.format("/%03d.png", i);
			BufferedImage s = new BufferedImage(t.getWidth()*8, t.getHeight()*8, BufferedImage.TYPE_INT_RGB);
			Graphics g = s.getGraphics();
			// Render the sprite...
			int numTilesX = t.getWidth() / 8;
			int numTilesY = t.getHeight() / 8;
			
			if (t.vflip) {
				g.setColor(Color.RED);
				g.fillRect(0, 0,s.getWidth(),s.getHeight());
			} else {
			
				for (int y=0;y<t.getHeight();y++) {
					int charAddr = (OAM.getNameBaseSelect() << 14) + 32*t.tileNumber+2;// 4 color 8x8 tile = 32bytes
					charAddr += 32*16*(y/8);
					int tileYOffset = (y%8)*2;
					for (int k=0;k<numTilesX;k++) {
						int x = k*8;
						for (int pixelX=0; pixelX<8; pixelX++) {
							int index = 0;
							int xMask = 0x80 >> pixelX;
							index |= ((PPU.vram[charAddr + tileYOffset] & xMask) != 0) ? 0x1 : 0;
							index |= ((PPU.vram[charAddr + tileYOffset + 1] & xMask) != 0) ? 0x2 : 0;
							index |= ((PPU.vram[charAddr + tileYOffset + 16] & xMask) != 0) ? 0x4 : 0;
							index |= ((PPU.vram[charAddr + tileYOffset + 17] & xMask) != 0) ? 0x8 : 0;
							if (index != 0) {
								Color color = new Color(CGRAM.getColor(index + 128+16*t.paletteNum));
								g.setColor(color);
								g.fillRect((x + pixelX)*8, y*8,8,8);
								
								allSprites.setColor(color);
								allSprites.fillRect((x + pixelX)*8 + (i%16)*16*8, y*8 + (i/16)*16*8,8,8);
							}
						}
						charAddr += 32;
					}
				}
			}
			
			try {
				ImageIO.write(s, "PNG", new File(fname));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			ImageIO.write(asprites, "PNG", new File(baseDir + "/allSprites.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void dumpSpriteData() {
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
		
		int base = OAM.getNameBaseSelect() << 14;
		BufferedImage img = new BufferedImage(128, 256, BufferedImage.TYPE_INT_RGB);
		for (int k = 0; k < 16; k++) {
			for (int m = 0; m < 16; m++) {
				int spriteNum = (k * 16) + m;
				for (int y = 0; y < 8; y++) {
					for (int x = 0; x < 8; x++) {
						int addr = base + (spriteNum * 32) + (y * 2);
						int xMask = 0x80 >> x;
						int index = 0;
						index |= ((PPU.vram[addr] & xMask) != 0) ? 0x1 : 0;
						index |= ((PPU.vram[addr + 1] & xMask) != 0) ? 0x2 : 0;
						index |= ((PPU.vram[addr + 16] & xMask) != 0) ? 0x4 : 0;
						index |= ((PPU.vram[addr + 17] & xMask) != 0) ? 0x8 : 0;
						img.setRGB(x + (m * 8), y + (k * 8), colors[index].getRGB());
					}
				}
			}
		}
		
		// Do again for the offset table
		base += (256 * 32) + (OAM.getNameSelect() << 13);
		for (int k = 0; k < 16; k++) {
			for (int m = 0; m < 16; m++) {
				int spriteNum = (k * 16) + m;
				for (int y = 0; y < 8; y++) {
					for (int x = 0; x < 8; x++) {
						int addr = base + (spriteNum * 32) + (y * 2);
						int xMask = 0x80 >> x;
						int index = 0;
						index |= ((PPU.vram[addr] & xMask) != 0) ? 0x1 : 0;
						index |= ((PPU.vram[addr + 1] & xMask) != 0) ? 0x2 : 0;
						index |= ((PPU.vram[addr + 16] & xMask) != 0) ? 0x4 : 0;
						index |= ((PPU.vram[addr + 17] & xMask) != 0) ? 0x8 : 0;
						img.setRGB(x + (m * 8), y + (k * 8) + 128, colors[index].getRGB());
					}
				}
			}
		}

		try {
			ImageIO.write(img, "PNG", new File(baseDir + "/spriteDump.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void renderSprites(int scanline, int priority) {
		OAM.parseOAM(); // Update the sprite object table
		
		// Disable sprites if not on a screen
		if (!OAM.mainScreen && !OAM.subScreen) return;
		
		// Render each sprite to a file...
		for (int i=127; i>=0;i--) {// Reverse draw for priorityness
			Sprite t = OAM.spriteTable[i];
			if (t.priority != priority) continue;
	
			int tileYPos = scanline - t.y;
			
			if (t.x>0 && t.x < PPU.screenWidth - t.getWidth() && scanline >= t.y && scanline < t.y + t.getHeight()) { // Are we on the right scanline to draw this tile
				int numTilesX = t.getWidth() / 8;
				int charAddr = (OAM.getNameBaseSelect() << 14) + 32*t.tileNumber;// 4 color 8x8 tile = 32bytes
				if (t.name) {
					charAddr += (256 * 32) + (OAM.getNameSelect() << 13);
				}
				
				// Vertical flip
				if (t.vflip) {
					tileYPos = t.getHeight() - 1 - tileYPos;
				}
				
				int tileYOffset = (tileYPos%8)*2;
				charAddr += 32*16*(tileYPos/8);
				
				for (int k=0;k<numTilesX;k++) {
					for (int pixelX=0; pixelX<8; pixelX++) {
						int pos;
						if (t.hflip) { // Horizontal flip
							pos = t.x + (t.getWidth() - 1 - (pixelX + (k * 8)));
						} else {
							pos = t.x + pixelX + (k * 8);
						}
						
						// Check if this pixel will be masked
						boolean pixelMasked = Window.checkSpriteMask(pos);
						
						// Check if we're masked or if another pixel is in our spot
						// If either is true, we won't draw on that screen
						boolean drawOnMain = false;//OAM.mainScreen && !PPU.mainScreen.isDrawn(pos, scanline) && !(OAM.windowMaskMain && pixelMasked);
						boolean drawOnSub = false;//OAM.subScreen && !PPU.subScreen.isDrawn(pos, scanline) && !(OAM.windowMaskSub && pixelMasked);
						
						// Quit early if we can't be drawn
						if (!drawOnMain && !drawOnSub) {
							continue;
						}
						
						int index = 0;
						int addr = charAddr + tileYOffset;
						int xMask = 0x80 >> pixelX;
						index |= ((PPU.vram[addr] & xMask) != 0) ? 0x1 : 0;
						index |= ((PPU.vram[addr + 1] & xMask) != 0) ? 0x2 : 0;
						index |= ((PPU.vram[addr + 16] & xMask) != 0) ? 0x4 : 0;
						index |= ((PPU.vram[addr + 17] & xMask) != 0) ? 0x8 : 0;
						if (index != 0) {
							int color = CGRAM.getColor(index + 128+16*t.paletteNum);
							
							if (drawOnMain) {
								//PPU.mainScreen.setPixel(pos, scanline, color, OAM.colorMathEnabled && Util.inRange(t.paletteNum, 4, 7), true);
							}
							
							if (drawOnSub) {
								//PPU.subScreen.setPixel(pos, scanline, color, OAM.colorMathEnabled && Util.inRange(t.paletteNum, 4, 7), true);
							}
						}
					}
					charAddr += 32;
				}
			}
		}
	}
}
