package edu.fit.cs.sno.snes.cpu;

import java.util.ArrayList;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.cpu.hwregs.CPURegisters;
import edu.fit.cs.sno.snes.cpu.hwregs.DMA;
import edu.fit.cs.sno.snes.input.Input;
import edu.fit.cs.sno.snes.ppu.PPU;
import edu.fit.cs.sno.util.Settings;

public class Timing {
	static final long TARGET_SPEED = 21477272; // 21.477MHz Master Clock
	public static final long cycleTimeNS = (long)((1.0/TARGET_SPEED) * 1000000000.0f);
	
	static long totalCycles = 0;        // Total master cycles that have been run
	public static boolean limitSpeed = false;  // Whether or not to limit the speed of execution
	static long lastTime = 0;           // The last time in nanoseconds we last did a cycle
	
	public static boolean autoFrameSkip = false;
	static {
		limitSpeed = Settings.isTrue(Settings.CPU_LIMIT_SPEED);
		autoFrameSkip = Settings.isTrue(Settings.AUTO_FRAME_SKIP);
	}

	static long sinceLastScanline = 0;
	static long cyclesPerScanLine = 1364;
	static int scanlines = 262;
	public static int currentScanline = 0;
	static boolean wramRefreshed = false;
	static boolean hdmaStarted = false;
	
	static int cyclesToCatchup = 0;
	
	static ArrayList<TimerCallback> callbacks = new ArrayList<TimerCallback>();
	static ArrayList<TimerCallback> toremove = new ArrayList<TimerCallback>();
	
	private static boolean irqOnCurrentLine = false; // Tracks if an IRQ occurred on the current scanline
	
	public static volatile long apuCyclesToRun = 0;
	
	public static void cycle(long numCycles) {
		totalCycles += numCycles;
		sinceLastScanline += numCycles;
		// WRAM Refresh Period
		if (sinceLastScanline >= 536 && !wramRefreshed) {
			numCycles += 40;
			totalCycles +=40;
			wramRefreshed = true;
		}
		// HDMA Update(happens during the 0th scanline)
		if (currentScanline == 0 && !hdmaStarted) {
			DMA.HDMAInit();
			hdmaStarted = true;
		}
		
		apuCyclesToRun += numCycles;
		
		// Run PPU
		PPU.renderCycles(numCycles);
		
		// Handle IRQ check
		checkIRQ();
		
		
		// Reset the cycles into this scanline counter, incrementing
		// the current scanline
		while (sinceLastScanline > cyclesPerScanLine) {
			sinceLastScanline -= cyclesPerScanLine;
			currentScanline++;
			irqOnCurrentLine = false;
			DMA.HDMARun();
			
			// Call VBlank only immediately after switching to the right scanline
			if (currentScanline == 0xE1) {
				CPU.triggerVBlank();
				PPU.vBlank();
				
				// Set advanceFrameOnce to pause after finishing out the current frame
				Core.advanceFrameOnce = false;
				break;
			}
			
			// Handle auto-joypad read
			if (currentScanline == 0xE3 && CPU.standardControllerRead) {
				Input.autoRead();
			}
		}
		// Reset back to the top of the screen
		if (currentScanline >= scanlines) {
			currentScanline = 0;
			wramRefreshed = false;
			PPU.vBlanking = false;
			hdmaStarted = false;
		}
		
		// VBlank related stuff
		if (currentScanline >= 0xE1) { // we are vblanking
			CPURegisters.rdnmi.val |= 0x80;
		} else {
			CPURegisters.rdnmi.val &= 0x7F;
		}
		
		// TODO: HBlank related stuff
		
		
		// Check for callbacks
		toremove.clear();
		for(TimerCallback t: callbacks) {
			if (t.callbackTime <= totalCycles) {
				System.out.println("Running callback: " + t);
				t.callback();
				toremove.add(t);
			}
		}
		for(TimerCallback t: toremove)
			callbacks.remove(t);
		
		// Check for timers

		// Waste time to match MHz setting(but only do it once per frame...when scanline==1)
		cyclesToCatchup += numCycles;
		if (limitSpeed && currentScanline==1) {
			long sleep, elapsed;
			do {
				elapsed = System.nanoTime() - lastTime;
				sleep = cyclesToCatchup*cycleTimeNS - elapsed;
			} while(sleep>cycleTimeNS);
			lastTime = System.nanoTime();
			cyclesToCatchup = 0;
		}
		
		//How long should this have taken
		if (autoFrameSkip) {
			long duration = numCycles*cycleTimeNS;
			long actualDuration = System.nanoTime() - lastTime;
			if (actualDuration > duration) {
				PPU.renderFrames = false;
			} else {
				PPU.renderFrames = true;
			}
		}
	}

	public static long getCycles() {
		return totalCycles;
	}
	
	public static void checkIRQ() {
		if (!irqOnCurrentLine) {
			boolean doIRQ = false;
			switch (CPU.irqEnable) {
				case CPU.IRQ_V:
					doIRQ = currentScanline == CPU.vtime;
					break;
				case CPU.IRQ_H:
					doIRQ = PPU.x >= CPU.htime;
					break;
				case CPU.IRQ_VH:
					doIRQ = PPU.x >= CPU.htime && currentScanline == CPU.vtime;
					break;
			}
			
			if (doIRQ) {
				irqOnCurrentLine = true;
				CPU.triggerIRQ();
			}
		}
	}
	
	public static void addTimer(long repeatEvery) {
		// Adds a callback to be executed every repeatEvery cycles
	}

	// Adds a callback to be executed in fromNow cycles
	public static void addCallback(long fromNow, TimerCallback t) {
		t.callbackTime = totalCycles + fromNow;
		callbacks.add(t);
	}

}
