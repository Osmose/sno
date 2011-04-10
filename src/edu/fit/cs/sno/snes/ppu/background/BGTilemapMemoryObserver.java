package edu.fit.cs.sno.snes.ppu.background;

import edu.fit.cs.sno.snes.mem.MemoryObserver;

public class BGTilemapMemoryObserver extends MemoryObserver {

	private Background bg;
	
	public BGTilemapMemoryObserver(Background bg) {
		this.bg = bg;
	}
	
	@Override
	public int[] getRange() {
		int start = bg.tileMapAddress;
		int end = start + 0x800;
		switch (bg.size) {
			case bg64x32:
			case bg32x64:
				end += 0x800;
				break;
			case bg64x64:
				end += 0x1800;
				break;
		}
		
		return new int[]{start, end};
	}

	@Override
	public void onInvalidate(int addr) {
		bg.rebuildTilemap(addr);
	}

}
