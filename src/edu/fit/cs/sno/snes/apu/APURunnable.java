package edu.fit.cs.sno.snes.apu;

import edu.fit.cs.sno.snes.Core;

public class APURunnable implements Runnable {

	@Override
	public void run() {
		while(Core.running) {
			APU.step();
		}
	}

}
