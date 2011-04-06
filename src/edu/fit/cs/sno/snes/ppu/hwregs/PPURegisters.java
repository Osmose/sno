package edu.fit.cs.sno.snes.ppu.hwregs;

import edu.fit.cs.sno.snes.mem.HWRegister;
import edu.fit.cs.sno.snes.ppu.Screen;
import edu.fit.cs.sno.snes.ppu.OAM;
import edu.fit.cs.sno.snes.ppu.PPU;
import edu.fit.cs.sno.util.Log;

public class PPURegisters {
	/**
	 * 0x2100
	 */
	public static HWRegister screenDisplay = new HWRegister() {
		@Override
		public void onWrite(int value) {
			int oldval = val;
			super.onWrite(value);
			PPU.blankScreen((val & 0x80) == 0x80);
			PPU.setBrightness(val & 0x0F);
			
			//TODO: This is wrong, check anomie's doc
			// The internal OAM address is reset on a change from 1->0 of bit 7
			if ((oldval & 0x80)==0x80 && (val & 0x80)==0x00)
				OAM.resetOAMAddress();
		}
	};
	
	/**
	 * 0x212C - Main screen designation(enables/disables backgrounds/sprites)
	 */
	public static HWRegister tm = new HWRegister() {
		@Override
		public void onWrite(int value) {
			PPU.bg[0].mainScreen = (value & 0x01) != 0;
			PPU.bg[1].mainScreen = (value & 0x02) != 0;
			PPU.bg[2].mainScreen = (value & 0x04) != 0;
			PPU.bg[3].mainScreen = (value & 0x08) != 0;
			OAM.mainScreen = (value & 0x10) != 0;
		};
	};
	
	/**
	 * 0x212D - Sub screen designation(added/subtracted from the main screen)
	 */
	public static HWRegister ts = new HWRegister() {
		@Override
		public void onWrite(int value) {
			PPU.bg[0].subScreen = (value & 0x01) != 0;
			PPU.bg[1].subScreen = (value & 0x02) != 0;
			PPU.bg[2].subScreen = (value & 0x04) != 0;
			PPU.bg[3].subScreen = (value & 0x08) != 0;
			OAM.subScreen = (value & 0x10) != 0;
		};
	};
	
	// Not implemented(Mode 7 related)
	public static HWRegister m7 = new HWRegister() {
		@Override
		public void onWrite(int value) {
			//Log.err("[Unimplemented] Mode 7 registers not implemented");
		}
	};
	
	
	// Color Math Related Registers
	/**
	 * 0x2130 - Color Addition Select
	 */
	public static HWRegister cgwsel = new HWRegister() {
		@Override
		public void onWrite(int value) {
			// Clip to black?
			Screen.clipBlack = (value >> 6) & 0x03;
			Screen.preventMath = (value >> 4) & 0x03;
			Screen.addSubscreen = (value & 0x02) != 0;
			Screen.directColor = (value & 0x01) != 0;
		}
	};
	
	/**
	 * 0x2131 - Color math designation
	 */
	public static HWRegister cgadsub = new HWRegister() {
		@Override
		public void onWrite(int value) {
			Screen.colorEnable[PPU.SRC_BG1] = (value & 0x01) != 0;
			Screen.colorEnable[PPU.SRC_BG2] = (value & 0x02) != 0;
			Screen.colorEnable[PPU.SRC_BG3] = (value & 0x04) != 0;
			Screen.colorEnable[PPU.SRC_BG4] = (value & 0x08) != 0;
			Screen.colorEnable[PPU.SRC_OAM] = (value & 0x10) != 0;
			Screen.colorEnable[PPU.SRC_BACK] = (value & 0x20) != 0;
			
			Screen.halfMath = (value & 0x40) != 0;
			Screen.addSub = (value & 0x80) != 0;
		}
	};
	
	/**
	 * 0x2132 - Fixed Color Data
	 */
	public static HWRegister coldata = new HWRegister() {
		public void onWrite(int value) {
			CGRAM.modFixedColor(value & 0x1F, (value & 0x20) != 0, (value & 0x40) != 0, (value & 0x80) != 0);
		}
	};
	
	
	/**
	 * 0x2133 - SETINI
	 */
	public static HWRegister setini = new HWRegister() {
		@Override
		public void onWrite(int value) {
			PPU.m7EXTBG = (value & 0x40) != 0;
			if ((value & 0x08) == 0x08)	System.out.println("Enabling pseudo-hires mode");
			if ((value & 0x04) == 0x04) System.out.println("Overscan mode enabled");
			if ((value & 0x02) == 0x02) System.out.println("OBJ interlace enabled");
			if ((value & 0x01) == 0x01) System.out.println("Screen interlace enabled");
		}
	};
	/**
	 * 0x4212 - PPU Status
	 */
	public static HWRegister hvbjoy = new HWRegister() {
		@Override
		public int getValue() {
			int val = 0;
			val |= (PPU.vBlanking ? 0x80 : 0);
			val |= ((PPU.x>274 || PPU.x<1) ? 0x40 : 0);
			// TODO: hblank status
			// TODO: auto-joypad read status
			
			return val;
		}
	};
}
