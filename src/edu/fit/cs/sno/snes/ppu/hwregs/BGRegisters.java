package edu.fit.cs.sno.snes.ppu.hwregs;

import edu.fit.cs.sno.snes.mem.HWRegister;
import edu.fit.cs.sno.snes.ppu.BGSize;
import edu.fit.cs.sno.snes.ppu.Background;
import edu.fit.cs.sno.snes.ppu.PPU;
import edu.fit.cs.sno.util.Log;

public class BGRegisters {
	/**
	 * 0x2105 - Screen Mode Register
	 */
	public static HWRegister screenMode = new HWRegister() {
		@Override
		public void onWrite(int value) {
			super.onWrite(value);
			boolean bg1Size = (((value>>4) & 0x01) == 0x01);
			boolean bg2Size = (((value>>5) & 0x01) == 0x01);
			boolean bg3Size = (((value>>6) & 0x01) == 0x01);
			boolean bg4Size = (((value>>7) & 0x01) == 0x01);
			
			boolean mode1BG3Priority = (((value >> 3) & 0x01) == 0x01);
			PPU.mode1BG3Priority = mode1BG3Priority;
			//if (mode1BG3Priority) Debug.Log("BG3 Priority");
			//else Debug.log("BG1 Priority");
			
			PPU.bg[0].setTileSize(bg1Size);
			PPU.bg[1].setTileSize(bg2Size);
			PPU.bg[2].setTileSize(bg3Size);
			PPU.bg[3].setTileSize(bg4Size);
			
			if (bg1Size) Log.debug("Setting BG1 Size to 16x16");
			else         Log.debug("Setting BG1 Size to 8x8");
			if (bg2Size) Log.debug("Setting BG2 Size to 16x16");
			else         Log.debug("Setting BG2 Size to 8x8");
			if (bg3Size) Log.debug("Setting BG3 Size to 16x16");
			else         Log.debug("Setting BG3 Size to 8x8");
			if (bg4Size) Log.debug("Setting BG4 Size to 16x16");
			else         Log.debug("Setting BG4 Size to 8x8");
			
			PPU.setMode(value & 0x07);
			Log.debug("Screen mode is: " + PPU.mode);
			if (PPU.mode == 7) {
				Log.err("====MODE 7 ENABLED====");
			}
		}
	};
	
	/**
	 * 0x2106 - Mosaic
	 */
	public static HWRegister mosaic = new HWRegister() {
		@Override
		public void onWrite(int value) {
			super.onWrite(value);
			boolean bg1Mosaic = (((value >> 0) & 0x01) == 0x01);
			boolean bg2Mosaic = (((value >> 1) & 0x01) == 0x01);
			boolean bg3Mosaic = (((value >> 2) & 0x01) == 0x01);
			boolean bg4Mosaic = (((value >> 3) & 0x01) == 0x01);
			
			if (bg1Mosaic) Log.debug("Enabling BG1 Mosaic");
			else           Log.debug("Disabling BG1 Mosaic");
			if (bg2Mosaic) Log.debug("Enabling BG1 Mosaic");
			else           Log.debug("Disabling BG1 Mosaic");
			if (bg3Mosaic) Log.debug("Enabling BG1 Mosaic");
			else           Log.debug("Disabling BG1 Mosaic");
			if (bg4Mosaic) Log.debug("Enabling BG1 Mosaic");
			else           Log.debug("Disabling BG1 Mosaic");
			
			PPU.bg[0].mosaic = bg1Mosaic;
			PPU.bg[1].mosaic = bg2Mosaic;
			PPU.bg[2].mosaic = bg3Mosaic;
			PPU.bg[3].mosaic = bg4Mosaic;
			
			int mosaicModeSize = (value >> 4)  & 0x0F;
			Log.debug("Mosaic size set to " + mosaicModeSize);
		}
	};
	
	/**
	 * 0x2107 - BG1 Tilemap address and size
	 */
	public static HWRegister bg1sc = new HWRegister() {
		@Override
		public void onWrite(int value) {
			PPU.bg[0].tileMapAddress = ((value >> 2) & 0x3F) << 11;
			PPU.bg[0].size = BGSize.toBGSize(value & 3);
		}
	};
	
	/**
	 * 0x2108 - BG2 Tilemap address and size
	 */
	public static HWRegister bg2sc = new HWRegister() {
		@Override
		public void onWrite(int value) {
			PPU.bg[1].tileMapAddress = ((value >> 2) & 0x3F) << 11;
			PPU.bg[1].size = BGSize.toBGSize(value & 3);
		}
	};
	
	/**
	 * 0x2109 - BG3 Tilemap address and size
	 */
	public static HWRegister bg3sc = new HWRegister() {
		@Override
		public void onWrite(int value) {
			PPU.bg[2].tileMapAddress = ((value >> 2) & 0x3F) << 11;
			PPU.bg[2].size = BGSize.toBGSize(value & 3);
		}
	};
	
	/**
	 * 0x210A - BG4 Tilemap address and size
	 */
	public static HWRegister bg4sc = new HWRegister() {
		@Override
		public void onWrite(int value) {
			PPU.bg[3].tileMapAddress = ((value >> 2) & 0x3F) << 11;
			PPU.bg[3].size = BGSize.toBGSize(value & 3);
		}
	};
	
	/**
	 * 0x210B - Background 1/2 Character Address
	 */
	public static HWRegister bg12nba = new HWRegister() {
		@Override
		public void onWrite(int value) {
			PPU.bg[0].baseAddress = (value & 0x07) << 13;
			PPU.bg[1].baseAddress = (value & 0x70) << 9; 
		}
	};
	
	/**
	 * 0x210C - Background 3/4 Character Address
	 */
	public static HWRegister bg34nba = new HWRegister() {
		@Override
		public void onWrite(int value) {
			PPU.bg[2].baseAddress = (value & 0x07) << 13;
			PPU.bg[3].baseAddress = (value & 0x70) << 9; 
		}
	};

	
	/**
	 * Background scroll registers, 0x210D - 0x2114
	 */
	private static int bgScrollPrev;
	static class BGHScrollReg extends HWRegister {
		Background bg = null;
		public BGHScrollReg(int bgnum) {
			bg = PPU.bg[bgnum];
		}
		@Override
		public void onWrite(int value) {
			bg.hscroll = (value << 8) | (bgScrollPrev& ~7) | ((bg.hscroll>>8)&7);
			bgScrollPrev = value;
		}
	}
	static class BGVScrollReg extends HWRegister {
		Background bg = null;
		public BGVScrollReg(int bgnum) {
			bg = PPU.bg[bgnum];
		}
		@Override
		public void onWrite(int value) {
			bg.vscroll = (value << 8) | bgScrollPrev;
			bgScrollPrev = value;
		}
	}
	
	public static HWRegister bg1hofs = new BGHScrollReg(0);
	public static HWRegister bg1vofs = new BGVScrollReg(0);
	
	public static HWRegister bg2hofs = new BGHScrollReg(1);
	public static HWRegister bg2vofs = new BGVScrollReg(1);
	
	public static HWRegister bg3hofs = new BGHScrollReg(2);
	public static HWRegister bg3vofs = new BGVScrollReg(2);
	
	public static HWRegister bg4hofs = new BGHScrollReg(3);
	public static HWRegister bg4vofs = new BGVScrollReg(3);
}
