package edu.fit.cs.sno.snes.ppu.background;

import edu.fit.cs.sno.snes.mem.MemoryObserver;

public class BGCharDataMemoryObserver extends MemoryObserver {

	private Background bg;
	
	public BGCharDataMemoryObserver(Background bg) {
		this.bg = bg;
	}
	
	@Override
	public int[] getRange() {
		int start = bg.baseAddress;
		int end = start + 1024 * 8 * bg.colorMode.bitDepth;
		
		return new int[]{start, end};
	}

	@Override
	public void onInvalidate(int addr) {
		bg.rebuildChardata(addr);
	}

}
