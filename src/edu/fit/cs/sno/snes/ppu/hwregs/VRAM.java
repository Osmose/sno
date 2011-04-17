package edu.fit.cs.sno.snes.ppu.hwregs;

import edu.fit.cs.sno.snes.mem.HWRegister;
import edu.fit.cs.sno.snes.mem.MemoryObserver;
import edu.fit.cs.sno.snes.ppu.PPU;
import edu.fit.cs.sno.util.Log;

public class VRAM {
	static int vramAddress;
	static boolean incMode = false;
	static int incVal = 1;
	static int mapMode = 0;
	static boolean readDummy = true;
	
	/**
	 * 0x2115 - Video Port Control
	 */
	public static HWRegister vmainc = new HWRegister() {
		@Override
		public void onWrite(int value) {
			incMode = (value & 0x80) == 0x80;
			mapMode = (value >> 2) & 3;
			switch (value & 3) {
				case 0: incVal = 1; break;
				case 1: incVal = 32; break;
				case 2: incVal = 128; break;
				case 3: incVal = 128; break;
			}
		}
	};
	
	/**
	 * 0x2116 - VRAM Address low
	 */
	public static HWRegister vmaddl = new HWRegister() {
		@Override
		public void onWrite(int value) {
			vramAddress = (vramAddress & 0xFF00) | value;
			//readDummy = true;
			//Log.debug(String.format("Updating vramaddress to 0x%x", vramAddress));
		}
	};
	
	/**
	 * 0x2117 - VRAM Address high
	 */
	public static HWRegister vmaddh = new HWRegister() {
		@Override
		public void onWrite(int value) {
			vramAddress = (vramAddress & 0x00FF) | (value << 8);
			//readDummy = true;
			//Log.debug(String.format("Updating vramaddress to 0x%x", vramAddress));
		}
	};
	
	/**
	 * 0x2118 - VRAM Data write low
	 */
	public static HWRegister vmwdatal = new HWRegister() {
		@Override
		public void onWrite(int value) {
			val = value & 0xFF;
			PPU.vram[getVramAddress()] = val;
			MemoryObserver.notifyObservers(getVramAddress());
			if (!incMode) vramAddress += incVal;
		}
	};
	
	/**
	 * 0x2119 - VRAM Data write high
	 */
	public static HWRegister vmwdatah = new HWRegister() {
		@Override
		public void onWrite(int value) {
			val = value & 0xFF;
			PPU.vram[getVramAddress()+1] = val;
			MemoryObserver.notifyObservers(getVramAddress()+1);
			if (incMode) vramAddress += incVal;
		}
	};
	
	/**
	 * 0x2139 - VRAM Data read low
	 */
	public static HWRegister vmrdatal = new HWRegister() {
		@Override
		public void onRead() {
			/*if (readDummy) {
				val = 0x00;
				readDummy = false;
			} else {
				val = PPU.vram[getVramAddress()];
				if (!incMode) vramAddress += incVal;
			}*/
			val = PPU.vram[getVramAddress()];
			if (!incMode) vramAddress += incVal;
		}
	};
	/**
	 * 0x213A - VRAM Data read high
	 */
	public static HWRegister vmrdatah = new HWRegister() {
		@Override
		public void onRead() {
			/*if (readDummy) {
				val = 0x00;
				readDummy = false;
			} else {
				val = PPU.vram[getVramAddress()+1];
				if (incMode) vramAddress += incVal;
			}*/
			val = PPU.vram[getVramAddress()+1];
			if (incMode) vramAddress += incVal;
		}
	};
	
	/**
	 * Maps the current internal vramAddress to the effective address
	 * Adapted from byuu's bsnes vram code
	 * 
	 * @return Effective vram address
	 */
	private static int getVramAddress() {
		int addr = vramAddress;
		
		// Each mapping takes 3 bytes from somewhere in the middle
		// and moves them to the bottom 3 bytes
		switch (mapMode) {
			case 0: break;	// Direct mapping
			case 1: addr = (addr & 0xff00) | ((addr & 0x001f) << 3) | ((addr >> 5) & 7); break;
			case 2: addr = (addr & 0xfe00) | ((addr & 0x003f) << 3) | ((addr >> 6) & 7); break;
			case 3: addr = (addr & 0xfc00) | ((addr & 0x007f) << 3) | ((addr >> 7) & 7); break;
		}
		
		return (addr << 1) & 0xFFFF;
	}
}
