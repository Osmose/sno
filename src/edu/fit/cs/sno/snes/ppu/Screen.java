package edu.fit.cs.sno.snes.ppu;

import edu.fit.cs.sno.snes.ppu.hwregs.CGRAM;

public class Screen {
	public static final int NEVER = 0;
	public static final int OUTSIDE = 1;
	public static final int INSIDE = 2;
	public static final int ALWAYS = 3;
	
	public static final int SRC_BG1 = 0;
	public static final int SRC_BG2 = 1;
	public static final int SRC_BG3 = 2;
	public static final int SRC_BG4 = 3;
	public static final int SRC_OAM = 4;
	public static final int SRC_BACK = 5;
	
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
		int priorityMain = 0;
		int colorMain = 0;
		int sourceMain = 0;
		
		if (PPU.bg[0].enabled() && PPU.bg[0].outMainPriority > priorityMain) {
			priorityMain = PPU.bg[0].outMainPriority;
			colorMain = CGRAM.getColor(PPU.bg[0].outMainPixel);
			sourceMain = SRC_BG1;
		}
		
		if (PPU.bg[1].enabled() && PPU.bg[1].outMainPriority > priorityMain) {
			priorityMain = PPU.bg[1].outMainPriority;
			colorMain = CGRAM.getColor(PPU.bg[1].outMainPixel);
			sourceMain = SRC_BG2;
		}
		
		if (PPU.bg[2].enabled() && PPU.bg[2].outMainPriority > priorityMain) {
			priorityMain = PPU.bg[2].outMainPriority;
			colorMain = CGRAM.getColor(PPU.bg[2].outMainPixel);
			sourceMain = SRC_BG3;
		}
		
		if (PPU.bg[3].enabled() && PPU.bg[3].outMainPriority > priorityMain) {
			priorityMain = PPU.bg[31].outMainPriority;
			colorMain = CGRAM.getColor(PPU.bg[3].outMainPixel);
			sourceMain = SRC_BG4;
		}
		
		if (OAM.userEnabled && OAM.outMainPriority > priorityMain) {
			priorityMain = OAM.outMainPriority;
			colorMain = CGRAM.getColor(OAM.outMainPixel);
			sourceMain = SRC_OAM;
		}
		
		if (priorityMain == 0) {
			colorMain = CGRAM.getColor(0);
			sourceMain = SRC_BACK;
		}
		
		int prioritySub = 0;
		int colorSub = 0;
		int sourceSub = 0;
		
		if (PPU.bg[0].enabled() && PPU.bg[0].outSubPriority > prioritySub) {
			prioritySub = PPU.bg[0].outSubPriority;
			colorSub = CGRAM.getColor(PPU.bg[0].outSubPixel);
			sourceSub = SRC_BG1;
		}
		
		if (PPU.bg[1].enabled() && PPU.bg[1].outSubPriority > prioritySub) {
			prioritySub = PPU.bg[1].outSubPriority;
			colorSub = CGRAM.getColor(PPU.bg[1].outSubPixel);
			sourceSub = SRC_BG2;
		}

		if (PPU.bg[2].enabled() && PPU.bg[2].outSubPriority > prioritySub) {
			prioritySub = PPU.bg[2].outSubPriority;
			colorSub = CGRAM.getColor(PPU.bg[2].outSubPixel);
			sourceSub = SRC_BG3;
		}
		
		if (PPU.bg[3].enabled() && PPU.bg[3].outSubPriority > prioritySub) {
			prioritySub = PPU.bg[3].outSubPriority;
			colorSub = CGRAM.getColor(PPU.bg[3].outSubPixel);
			sourceSub = SRC_BG4;
		}
		
		if (OAM.userEnabled && OAM.outSubPriority > prioritySub) {
			prioritySub = OAM.outSubPriority;
			colorSub = CGRAM.getColor(OAM.outSubPixel);
			sourceSub = SRC_OAM;
		}
		
		if (prioritySub == 0 || !addSubscreen) {
			colorSub = CGRAM.fixedColor;
			sourceSub = SRC_BACK;
		}
		
		boolean clipMain = checkWindowEffect(clipBlack);
		boolean preventSubMath = checkWindowEffect(preventMath);
		
		if (clipMain) {
			if (preventSubMath) {
				return 0;
			}
			colorMain = 0;
		}
		
		boolean colorExempt = sourceMain == SRC_OAM && OAM.outMainPixel < 192;
		if (!colorExempt && colorEnable[sourceMain] && !preventSubMath) {
			boolean halve = false;
			if (halfMath) {
				halve = !addSub || sourceSub == SRC_BACK;
			}
			
			return addSub(colorMain, colorSub, halve);
		} else {
			return colorMain;
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
