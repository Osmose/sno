package edu.fit.cs.sno.snes.ppu;

import edu.fit.cs.sno.util.Util;

public class Window {

	public static final int MAIN_SCREEN = 0;
	public static final int SUB_SCREEN = 1;
	
	public static int window1Left = 0;
	public static int window1Right = 0;
	public static int window2Left = 0;
	public static int window2Right = 0;
	
	public static void maskPixel(int x) {
		boolean spriteMask = checkSpriteMask(x);
		
		if (maskBySource(MAIN_SCREEN, PPU.sourceMain, x, spriteMask)) {
			PPU.colorMain = 0;
		}
		if (maskBySource(SUB_SCREEN, PPU.sourceMain, x, spriteMask)) {
			PPU.colorSub = 0;
		}
		
		Screen.inColorWindow = checkColorWindow(x);
	}
	
	private static boolean maskBySource(int screen, int source, int x, boolean spriteMask) {
		// Check if the source is masked
		switch (source) {
			case PPU.SRC_BG1:
			case PPU.SRC_BG2:
			case PPU.SRC_BG3:
			case PPU.SRC_BG4:
				boolean bgWindowMask = (source == SUB_SCREEN ? PPU.bg[source].windowMaskSub : PPU.bg[source].windowMaskMain);
				if (bgWindowMask && checkBackgroundMask(source, x)) {
					return true;
				}
				break;
			case PPU.SRC_OAM:
				boolean spriteWindowMask = (source == SUB_SCREEN ? OAM.windowMaskSub : OAM.windowMaskMain);
				if (spriteWindowMask && spriteMask) {
					return true;
				}
				break;
		}
		
		return false;
	}
	
	/**
	 * Checks if a given pixel in a BG is masked
	 * 
	 * @param index Index of background in PPU array
	 * @param screen 0 = main screen, 1 = sub screen
	 * @param x X value of pixel
	 * @return True if masked, false otherwise
	 */
	public static boolean checkBackgroundMask(int index, int x) {
		Background bg = PPU.bg[index];
		
		return checkMask(x, bg.window1Enabled, bg.window2Enabled, bg.window1Invert, bg.window2Invert, bg.windowOp);
	}
	
	public static boolean checkSpriteMask(int x) {
		return checkMask(x, OAM.window1Enabled, OAM.window2Enabled, OAM.window1Invert, OAM.window2Invert, OAM.windowOp);
	}
	
	public static boolean checkColorWindow(int x) {
		return checkMask(x, Screen.window1Enabled, Screen.window2Enabled, Screen.window1Invert, Screen.window2Invert, Screen.windowOp);
	}
	
	public static boolean checkMask(int x, boolean w1Enabled, boolean w2Enabled, boolean w1Invert, boolean w2Invert, int op) {
		if (!(w1Enabled || w2Enabled)) { // Neither enabled
			return false;
		} else if (w1Enabled && !w2Enabled) {
			return Util.inRange(x, window1Left, window1Right) ^ w1Invert;
		} else if (!w1Enabled && w2Enabled) {
			return Util.inRange(x, window2Left, window2Right) ^ w2Invert;
		} else {
			boolean in1 = Util.inRange(x, window1Left, window1Right) ^ w1Invert;
			boolean in2 = Util.inRange(x, window2Left, window2Right) ^ w2Invert;
			switch (op) {
				case 0:
					return in1 || in2;
				case 1:
					return in1 && in2;
				case 2:
					return in1 ^ in2;
				case 3:
					return !(in1 ^ in2);
			}
		}
		
		return false;
	}
	
}
