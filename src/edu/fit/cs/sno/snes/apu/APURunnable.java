package edu.fit.cs.sno.snes.apu;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.cpu.Timing;

public class APURunnable implements Runnable {

	@Override
	public void run() {
		while(Core.running) {
			if (Timing.apuCyclesToRun > (24 * 21)) {
				APU.processCycles(24);
				Timing.apuCyclesToRun -= (24 * 21);
			}
		}
	}

}
