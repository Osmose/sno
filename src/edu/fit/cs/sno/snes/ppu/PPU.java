package edu.fit.cs.sno.snes.ppu;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;

import edu.fit.cs.sno.applet.SNOApplet;
import edu.fit.cs.sno.snes.ppu.background.Background;
import edu.fit.cs.sno.snes.ppu.hwregs.CGRAM;
import edu.fit.cs.sno.util.Log;
import edu.fit.cs.sno.util.Settings;
import edu.fit.cs.sno.util.Util;

public class PPU {
	// Output pixel source constants
	public static final int SRC_BG1 = 0;
	public static final int SRC_BG2 = 1;
	public static final int SRC_BG3 = 2;
	public static final int SRC_BG4 = 3;
	public static final int SRC_OAM = 4;
	public static final int SRC_BACK = 5;
	
	// Memory declarations
	public static int vram[] = new int[64*1024]; // Video RAM
	public static Background bg[] = new Background[4];
	static {
		bg[0] = new Background(1);
		bg[1] = new Background(2);
		bg[2] = new Background(3);
		bg[3] = new Background(4);
		
		init();
		
		if (Settings.get(Settings.FRAMES_TO_SKIP) != null)
			skipLimit = Settings.getInt(Settings.FRAMES_TO_SKIP);
	}
	
	public static boolean screenBlank = false;
	public static int brightness = 1;
	public static boolean vBlanking = false;
	
	public static int mode = 0;
	
	public static BufferedImage screenBuffer;
	public static boolean mode1BG3Priority = false;

	public static int screenWidth = 256;
	public static int screenHeight = 240;
	
	public static boolean drawWindow1 = false;
	public static boolean drawWindow2 = false;
	public static Color window1Color = new Color(0, 255, 0);
	public static Color window2Color = new Color(255, 0, 255);
	
	public static long unprocessedCycles;
	public static int x;
	public static int y;
	
	// Color value that will be output for the next pixel
	public static int colorMain;
	public static int colorSub;
	
	// Priority of main and subscreen colors
	public static int priorityMain;
	public static int prioritySub;
	
	// Source of the colors
	public static int sourceMain;
	public static int sourceSub;
	
	// Whether or not to actually render the frames
	public static boolean renderFrames = true;
	public static int skipCount = 0;
	public static int skipLimit = 5; // draw 1 out of 30 frames
	
	// Mode 7 data
	public static boolean m7EXTBG = false;	// Toggle BG2 in Mode 7
	public static int m7HOffset;
	public static int m7VOffset;
	public static int m7Repeat;
	public static boolean m7XFlip;
	public static boolean m7YFlip;
	
	public static void setMode(int mode) {
		PPU.mode = mode;
		
		// Backgrounds and OAM output a priority value that Screen uses to
		// determine which pixel is on top; For BGs, priority is a single bit
		// in each tile, while objs have 2 bits to select from 4 different
		// priorities per sprite
		switch(mode) {
			case 0:
				bg[0].enabled = true; bg[0].colorMode = ColorMode.Color4;
				bg[1].enabled = true; bg[1].colorMode = ColorMode.Color4;
				bg[2].enabled = true; bg[2].colorMode = ColorMode.Color4;
				bg[3].enabled = true; bg[3].colorMode = ColorMode.Color4;
				
				bg[0].priority0 = 8; bg[0].priority1 = 11;
				bg[1].priority0 = 7; bg[1].priority1 = 10; 
				bg[2].priority0 = 2; bg[2].priority1 = 5;
				bg[3].priority0 = 1; bg[3].priority1 = 4;
				OAM.priorityMap[0] = 3; OAM.priorityMap[1] = 6; OAM.priorityMap[2] = 7; OAM.priorityMap[3] = 12;
				break;
			case 1: 
				bg[0].enabled = true; bg[0].colorMode = ColorMode.Color16;
				bg[1].enabled = true; bg[1].colorMode = ColorMode.Color16;
				bg[2].enabled = true; bg[2].colorMode = ColorMode.Color4;
				bg[3].enabled = false;
				if (mode1BG3Priority) {
					bg[0].priority0 = 5; bg[0].priority1 = 8;
					bg[1].priority0 = 4; bg[1].priority1 = 7;
					bg[2].priority0 = 1; bg[2].priority1 = 10;
					OAM.priorityMap[0] = 2; OAM.priorityMap[1] = 3; OAM.priorityMap[2] = 6; OAM.priorityMap[3] = 9;
				} else {
					bg[0].priority0 = 6; bg[0].priority1 = 9;
					bg[1].priority0 = 5; bg[1].priority1 = 8;
					bg[2].priority0 = 1; bg[2].priority1 = 3;
					OAM.priorityMap[0] = 2; OAM.priorityMap[1] = 4; OAM.priorityMap[2] = 7; OAM.priorityMap[3] = 10;
				}
				break;
			case 2:
				bg[0].enabled = true; bg[0].colorMode = ColorMode.Color16;
				bg[1].enabled = true; bg[1].colorMode = ColorMode.Color16;
				bg[2].enabled = false;
				bg[3].enabled = false;
				
				bg[0].priority0 = 3; bg[0].priority1 = 7;
				bg[1].priority0 = 1; bg[1].priority1 = 5; 
				OAM.priorityMap[0] = 2; OAM.priorityMap[1] = 4; OAM.priorityMap[2] = 6; OAM.priorityMap[3] = 8;
				break;
			case 3:
				bg[0].enabled = true; bg[0].colorMode = ColorMode.Color256;
				bg[1].enabled = true; bg[1].colorMode = ColorMode.Color4;
				bg[2].enabled = false;
				bg[3].enabled = false;
				
				bg[0].priority0 = 3; bg[0].priority1 = 7;
				bg[1].priority0 = 1; bg[1].priority1 = 5; 
				OAM.priorityMap[0] = 2; OAM.priorityMap[1] = 4; OAM.priorityMap[2] = 6; OAM.priorityMap[3] = 8;
				break;
			case 4:
				bg[0].enabled = true; bg[0].colorMode = ColorMode.Color256;
				bg[1].enabled = true; bg[1].colorMode = ColorMode.Color4;
				bg[2].enabled = false;
				bg[3].enabled = false;
				
				bg[0].priority0 = 3; bg[0].priority1 = 7;
				bg[1].priority0 = 1; bg[1].priority1 = 5; 
				OAM.priorityMap[0] = 2; OAM.priorityMap[1] = 4; OAM.priorityMap[2] = 6; OAM.priorityMap[3] = 8;
				break;
			case 5:
				bg[0].enabled = true; bg[0].colorMode = ColorMode.Color16;
				bg[1].enabled = true; bg[1].colorMode = ColorMode.Color4;
				bg[2].enabled = false;
				bg[3].enabled = false;
				
				bg[0].priority0 = 3; bg[0].priority1 = 7;
				bg[1].priority0 = 1; bg[1].priority1 = 5; 
				OAM.priorityMap[0] = 2; OAM.priorityMap[1] = 4; OAM.priorityMap[2] = 6; OAM.priorityMap[3] = 8;
				break;
			case 6:
				bg[0].enabled = true; bg[0].colorMode = ColorMode.Color16;
				bg[1].enabled = false;
				bg[2].enabled = false;
				bg[3].enabled = false;
				
				bg[0].priority0 = 2; bg[0].priority1 = 5;
				OAM.priorityMap[0] = 1; OAM.priorityMap[1] = 3; OAM.priorityMap[2] = 4; OAM.priorityMap[3] = 6;
			case 7:
				if (m7EXTBG) {
					bg[0].enabled = true; bg[0].colorMode = ColorMode.Mode7;
					bg[1].enabled = true; bg[1].colorMode = ColorMode.Mode7;
					bg[2].enabled = false;
					bg[3].enabled = false;
					
					bg[0].priority0 = 3; bg[0].priority1 = 3;
					bg[1].priority0 = 1; bg[1].priority1 = 5;
					OAM.priorityMap[0] = 2; OAM.priorityMap[1] = 4; OAM.priorityMap[2] = 6; OAM.priorityMap[3] = 7;
				} else {
					bg[0].enabled = true; bg[0].colorMode = ColorMode.Mode7;
					bg[1].enabled = false;
					bg[2].enabled = false;
					bg[3].enabled = false;
					
					bg[0].priority0 = 2; bg[0].priority1 = 2;
					OAM.priorityMap[0] = 1; OAM.priorityMap[1] = 3; OAM.priorityMap[2] = 4; OAM.priorityMap[3] = 6;
				}
				break;
		}
	}
	
	public static void blankScreen(boolean blank) {
		screenBlank = blank;
	}

	public static void setBrightness(int i) {
		brightness = i;
	}
	
	/**
	 * vBlank is called by Timing when vBlank first occurs. It outputs the current
	 * frame and resets everything for the next frame.
	 */
	public static void vBlank() {
		// Draw out current frame
		if (SNOApplet.instance != null) SNOApplet.instance.screen.drawFrame();
		
		// Activate vblank and reset to the top of the screen
		vBlanking = true;		
		x = 0;
		y = 0;
		
		// Reset each bg and oam
		bg[0].vBlank();
		bg[1].vBlank();
		bg[2].vBlank();
		bg[3].vBlank();
		OAM.vBlank();
		
		if (!renderFrames) {
			skipCount++;
			if(skipCount>=skipLimit){
				skipCount = 0;
			} else {
				return;
			}
		}
		
		// Draw color 0 as the base color if the screen is completely transparent
		screenBuffer.getGraphics().setColor(new Color(CGRAM.getColor(0)));
		screenBuffer.getGraphics().fillRect(0, 0, screenBuffer.getWidth(), screenBuffer.getHeight());
	}
	
	/**
	 * Init is called once to initialize the PPU. This probably isn't needed.
	 */
	public static void init() {
		screenBuffer = new BufferedImage(256, 240, BufferedImage.TYPE_INT_ARGB);
	}
	
	/**
	 * Render cycles runs the PPU for the amount of cycles passed in. 
	 * 
	 * The PPU outputs one pixel every 4 cycles, with the exception of 
	 * pixels 323 and 327, which take 6 cycles, for a total of 1364 cycles
	 * per scanline.
	 * 
	 * There are always 340 pixels per scanline. But only pixels 22-277 are
	 * displayed for a total of 256 displayed pixels. renderCycles calls loadPixel()
	 * for each of these 256 pixels, and writes the final output to the screenBuffer.
	 * 
	 * @param cycles Number of cycles to process
	 */
	public static void renderCycles(long cycles) {
		// Don't render during vBlank
		if (!vBlanking) {
			// Loop until we run out of 4-cycle pixels to process
			unprocessedCycles += cycles;
			while (unprocessedCycles > 4 && x < 340) {
				// TODO: Refactor so x is the x value on the visible screen
				
				// Dots 323 and 327 take 6 cycles
				if (x == 323 || x == 327) {
					if (unprocessedCycles >= 6) {
						unprocessedCycles -= 6;
					} else {
						break;
					}
				} else {
					unprocessedCycles -= 4;
				}
				
				// Only draw pixels 22 - 277
				if (Util.inRange(x, 22, 277) && (renderFrames || (!renderFrames && skipCount==0))) {
					// Init output to the background color
					colorMain = 0;
					priorityMain = 0;
					sourceMain = SRC_BACK;
					
					colorSub = 0;
					prioritySub = 0;
					sourceSub = SRC_BACK;
					
					// loadPixel processes the current pixel and sets the output to the correct pixel
					bg[0].loadPixel();
					bg[1].loadPixel();
					bg[2].loadPixel();
					bg[3].loadPixel();
					OAM.loadPixel();
					
					// Screen then combines the output into a single color
					int color = Screen.doPixel(x - 22);
					
					// Write to the screenbuffer, adjusting for the 22 unused pixels at the start of the scanline
					screenBuffer.setRGB(x - 22, y, CGRAM.snesColorToARGB(color, brightness));
				}

				x++;
			}
			if (unprocessedCycles>4) unprocessedCycles = 0;
		}
	}
	
	/**
	 * scanline() is called once per scanline by Timing. It resets each BG and OAM
	 * to prepare for rendering the next scanline;
	 */
	public static void scanline() {
		// No scanlines during vBlank
		if (!vBlanking) {
			bg[0].nextScanline();
			bg[1].nextScanline();
			bg[2].nextScanline();
			bg[3].nextScanline();
			OAM.scanline(y);
			x = 0;
			y++;
		}
	}
	
	public static void dumpVRAM() {
		if (Settings.get(Settings.DEBUG_DIR) != null) {
			try {
				String fname = Settings.get(Settings.DEBUG_DIR) + "/vram.bin";
				FileOutputStream fos = new FileOutputStream(fname);
				for(int i=0; i<vram.length; i++)
					fos.write(vram[i]);
				fos.close();
			} catch (IOException e) {
				System.out.println("Unable to dump vram");
				e.printStackTrace();
			}
		}
		Log.debug(bg[0].toString());
		Log.debug(bg[1].toString());
		Log.debug(bg[2].toString());
		Log.debug(bg[3].toString());
	}

}
