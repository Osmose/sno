package edu.fit.cs.sno.snes.ppu;

import edu.fit.cs.sno.snes.ppu.hwregs.CGRAM;

public class Screen {
	public static final int NEVER = 0;
	public static final int OUTSIDE = 1;
	public static final int INSIDE = 2;
	public static final int ALWAYS = 3;
	
	public static boolean window1Enabled;
	public static boolean window2Enabled;
	public static boolean window1Invert;
	public static boolean window2Invert;
	public static int windowOp;
	
	public static boolean addSub; // True = subtract, false = add
	public static boolean halfMath;
	public static boolean addSubscreen;
	public static boolean directColor;
	public static int clipBlack;
	public static int preventMath;
	
	public static boolean[] colorEnable = new boolean[6]; 
	
	public static boolean inColorWindow = false;
	
	public static int doPixel(int x) {
		inColorWindow = Window.checkColorWindow();
		
		int mainColor = CGRAM.getColor(PPU.colorMain);
		int subColor = (PPU.prioritySub == 0 ? CGRAM.fixedColor : CGRAM.getColor(PPU.colorSub));
		
		boolean clipMain = checkWindowEffect(clipBlack);
		boolean preventSubMath = checkWindowEffect(preventMath);
		
		if (clipMain) {
			if (preventSubMath) {
				return 0;
			}
			mainColor = 0;
		}
		
		boolean colorExempt = PPU.sourceMain == PPU.SRC_OAM && PPU.colorMain < 192;
		if (!colorExempt && colorEnable[PPU.sourceMain] && !preventSubMath) {
			boolean halve = false;
			if (halfMath) {
				halve = !addSub || PPU.sourceSub == PPU.SRC_BACK;
			}
			
			return addSub(mainColor, subColor, halve);
		} else {
			return mainColor;
		}
	}
	
	public static int addSub(int colorMain, int colorSub, boolean halve) {
		int newColor;
		if (!addSub) { // Add
			if (!halve) {
				int sum = colorMain + colorSub;
				int carry = (sum - ((colorMain ^ colorSub) & 0x0421)) & 0x8420;
				newColor = (sum - carry) | (carry - (carry >> 5));
			} else {
				newColor = (colorMain + colorSub - ((colorMain ^ colorSub) & 0x0421)) >> 1;
			}
		} else { // Subtract
			int diff = colorMain - colorSub + 0x8420;
			int borrow = (diff - ((colorMain ^ colorSub) & 0x8420)) & 0x8420;
			if(!halve) {
				newColor = (diff - borrow) & (borrow - (borrow >> 5));
			} else {
				newColor = (((diff - borrow) & (borrow - (borrow >> 5))) & 0x7bde) >> 1;
			}
		}
		
		return newColor;
	}
	
	public static boolean checkWindowEffect(int status) {
		switch (status) {
			case NEVER:
				return false;
			case OUTSIDE:
				return !inColorWindow;
			case INSIDE: 
				return inColorWindow;
			case ALWAYS:
				return true;
			default:
				return false;
		}
	}
}
