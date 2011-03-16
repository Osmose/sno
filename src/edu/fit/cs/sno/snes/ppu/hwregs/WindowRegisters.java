package edu.fit.cs.sno.snes.ppu.hwregs;

import edu.fit.cs.sno.snes.mem.HWRegister;
import edu.fit.cs.sno.snes.ppu.Screen;
import edu.fit.cs.sno.snes.ppu.OAM;
import edu.fit.cs.sno.snes.ppu.PPU;
import edu.fit.cs.sno.snes.ppu.Window;

public class WindowRegisters {	
	/**
	 * 0x2124 - Window Mask Settings for BG1/BG2
	 */
	public static HWRegister w12sel = new HWRegister() {
		@Override
		public void onWrite(int value) {
			PPU.bg[0].window1Enabled = (value & 0x02) != 0;
			PPU.bg[0].window2Enabled = (value & 0x08) != 0;
			PPU.bg[1].window1Enabled = (value & 0x20) != 0;
			PPU.bg[1].window2Enabled = (value & 0x80) != 0;
			
			PPU.bg[0].window1Invert = (value & 0x01) != 0;
			PPU.bg[0].window2Invert = (value & 0x04) != 0;
			PPU.bg[1].window1Invert = (value & 0x10) != 0;
			PPU.bg[1].window2Invert = (value & 0x40) != 0;
		}
	};
	/**
	 * 0x2124 - Window Mask Settings for BG3/BG4
	 */
	public static HWRegister w34sel = new HWRegister() {
		@Override
		public void onWrite(int value) {
			PPU.bg[2].window1Enabled = (value & 0x02) != 0;
			PPU.bg[2].window2Enabled = (value & 0x08) != 0;
			PPU.bg[3].window1Enabled = (value & 0x20) != 0;
			PPU.bg[3].window2Enabled = (value & 0x80) != 0;
			
			PPU.bg[2].window1Invert = (value & 0x01) != 0;
			PPU.bg[2].window2Invert = (value & 0x04) != 0;
			PPU.bg[3].window1Invert = (value & 0x10) != 0;
			PPU.bg[3].window2Invert = (value & 0x40) != 0;
		}
	};
	/**
	 * 0x2125 - Window Mask Settings for Obj/Color Window
	 */
	public static HWRegister wobjsel = new HWRegister() {
		@Override
		public void onWrite(int value) {
			OAM.window1Enabled = (value & 0x02) != 0;
			OAM.window2Enabled = (value & 0x08) != 0;
			Screen.window1Enabled = (value & 0x20) != 0;
			Screen.window2Enabled = (value & 0x80) != 0;
			
			OAM.window1Invert = (value & 0x01) != 0;
			OAM.window2Invert = (value & 0x04) != 0;
			Screen.window1Invert = (value & 0x10) != 0;
			Screen.window2Invert = (value & 0x40) != 0;
		}
	};
	
	/**
	 * 0x2126 - Window 1 Left Position
	 */
	public static HWRegister wh0 = new HWRegister() {
		@Override
		public void onWrite(int value) {
			Window.window1Left = value;
		}
	};
	/**
	 * 0x2127 - Window 1 Right Position
	 */
	public static HWRegister wh1 = new HWRegister() {
		@Override
		public void onWrite(int value) {
			Window.window1Right = value;
		}
	};
	/**
	 * 0x2128 - Window 2 Left Position
	 */
	public static HWRegister wh2 = new HWRegister() {
		@Override
		public void onWrite(int value) {
			Window.window2Left = value;
		}
	};
	/**
	 * 0x2129 - Window 2 Right Position
	 */
	public static HWRegister wh3 = new HWRegister() {
		@Override
		public void onWrite(int value) {
			Window.window2Right = value;
		}
	};
	
	
	/**
	 * 0x212a Window mask logic for BGs
	 */
	public static HWRegister wbglog = new HWRegister() {
		@Override
		public void onWrite(int value) {
			PPU.bg[0].windowOp = value & 0x03;
			PPU.bg[1].windowOp = (value >> 2) & 0x03;
			PPU.bg[2].windowOp = (value >> 4) & 0x03;
			PPU.bg[3].windowOp = (value >> 6) & 0x03;
		}
	};
	/**
	 * 0x212b Window mask logic for OBJs and Color window
	 */
	public static HWRegister wobjlog = new HWRegister() {
		@Override
		public void onWrite(int value) {
			OAM.windowOp = value & 0x03;
			Screen.windowOp = (value >> 2) & 0x03;
		}
	};
	
	/**
	 * 0x212e Window mask designation for the main screen
	 */
	public static HWRegister tmw = new HWRegister() {
		@Override
		public void onWrite(int value) {
			PPU.bg[0].windowMaskMain = (value & 0x01) != 0;
			PPU.bg[1].windowMaskMain = (value & 0x02) != 0;
			PPU.bg[2].windowMaskMain = (value & 0x04) != 0;
			PPU.bg[3].windowMaskMain = (value & 0x08) != 0;
			OAM.windowMaskMain = (value & 0x10) != 0;
		}
	};
	
	/**
	 * 0x212f Window mask designation for the subscreen
	 */
	public static HWRegister tsw = new HWRegister() {
		@Override
		public void onWrite(int value) {
			PPU.bg[0].windowMaskSub = (value & 0x01) != 0;
			PPU.bg[1].windowMaskSub = (value & 0x02) != 0;
			PPU.bg[2].windowMaskSub = (value & 0x04) != 0;
			PPU.bg[3].windowMaskSub = (value & 0x08) != 0;
			OAM.windowMaskSub = (value & 0x10) != 0;
		}
	};
}
