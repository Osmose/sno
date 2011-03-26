package edu.fit.cs.sno.snes;

import java.io.File;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import edu.fit.cs.sno.applet.SNOApplet;
import edu.fit.cs.sno.snes.apu.APUMemory;
import edu.fit.cs.sno.snes.apu.APURunnable;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.cpu.Timing;
import edu.fit.cs.sno.snes.mem.HiROMMemory;
import edu.fit.cs.sno.snes.mem.LoROMMemory;
import edu.fit.cs.sno.snes.mem.Memory;
import edu.fit.cs.sno.snes.mem.UnimplementedHardwareRegister;
import edu.fit.cs.sno.snes.ppu.PPU;
import edu.fit.cs.sno.snes.ppu.Sprites;
import edu.fit.cs.sno.snes.ppu.hwregs.CGRAM;
import edu.fit.cs.sno.snes.rom.RomLoader;
import edu.fit.cs.sno.util.Log;
import edu.fit.cs.sno.util.Settings;
import edu.fit.cs.sno.util.Util;

public class Core {

	public static Memory mem;
	public static long instCount;
	public static long timeBegin;
	public static long timeEnd;
	
	public static boolean pause = false;
	public static boolean advanceFrameOnce = false;
	
	public static long maxInstructions;
	public static boolean running = true;
	
	public static Thread coreThread;
	public static Thread apuThread;
	
	public static void main(String args[]) {
		System.out.println("Loading rom: " + args[0]);
		
		try {
			InputStream is = new FileInputStream(args[0]);
			boolean isZip = args[0].endsWith(".zip");
			Core.run(is, isZip);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void cycle(long count) throws Exception {
		long instCount = 0;
		
		// Do as many instructions as set in properties file
		// Will execute indefinitely if instruction count is negative
		try {
			while ((instCount < count || count < 0) && running) {
				if (pause && !advanceFrameOnce) continue;
				CPU.cycle();
				instCount++;
			}
		} catch (Exception err) {
			// Finish timing and print stats before throwing error up
			timeEnd = new Date().getTime();
			System.out.println("Total time: " + ((timeEnd - timeBegin) / 1000) + " seconds");
			System.out.println("Instructions performed: " + instCount);
			System.out.println("Cycles performed: " + Timing.getCycles());
			System.out.println("Average speed: " + (((Timing.getCycles() + 0f) / ((timeEnd - timeBegin + 0f) / 1000f)) / (1024f * 1024f)) + " MHz");
			System.out.println("Average speed: " + (((Timing.getCycles() + 0f) / ((timeEnd - timeBegin + 0f) / 1000f))) + " Hz");
			printMMaps();
			
			throw err;
		}
		
		Core.instCount += instCount;
	}
	
	public static void init(InputStream is, boolean isZip) {
		CPU.resetCPU();
		PPU.init();
		printStats();
		Log.debug("====Starting SNO====");
		RomLoader rl = new RomLoader(is, isZip);
		if (rl.getRomInfo().isHiROM())
			mem = new HiROMMemory();
		else
			mem = new LoROMMemory();
		rl.loadMemory(mem);
		instCount = 0;
		
		// Initiate Reset Vector
		CPU.resetVectorInit();
	}
	
	public static void run(InputStream is, boolean isZip) throws Exception {
		init(is, isZip);
		instCount = 0;
		maxInstructions = Long.parseLong(Settings.get(Settings.CORE_MAX_INSTRUCTIONS));
		
		// Load save file if found
		if (Settings.isSet(Settings.SAVE_PATH)) {
			InputStream saveStream = Util.getStreamFromUrl(Settings.get(Settings.SAVE_PATH));
			if (saveStream != null) {
				mem.loadSram(saveStream);
			}
		}
		
		// Load save file if found
		if (Settings.get(Settings.SAVE_PATH) != null) {
			File save = new File(Settings.get(Settings.SAVE_PATH));
			if (save != null) {
				mem.loadSram(new FileInputStream(save));
			}
		}
		
		// Execute and time game
		if (Log.instruction.enabled() && Settings.get(Settings.CPU_ALT_DEBUG)=="false") {
			Log.instruction("====CPU Execution====");
			Log.instruction("romAdr pbr:pc   op   CPU Status                      args              Instruction");
			Log.instruction("------ -------  --   ------------------------------- ---------------   -----------");
		}
		timeBegin = new Date().getTime();
		cycle(maxInstructions);
		timeEnd = new Date().getTime();
		running = false;
		
		// Print run stats
		System.out.println("Total time: " + ((timeEnd - timeBegin) / 1000) + " seconds");
		System.out.println("Instructions performed: " + instCount);
		System.out.println("Cycles performed: " + Timing.getCycles());
		System.out.println("Average speed: " + (((Timing.getCycles() + 0f) / ((timeEnd - timeBegin + 0f) / 1000f)) / (1024f * 1024f)) + " MHz");
		System.out.println("Average speed: " + (((Timing.getCycles() + 0f) / ((timeEnd - timeBegin + 0f) / 1000f))) + " Hz");
		printMMaps();
		
		PPU.dumpVRAM();
		mem.dumpWRAM();
		APUMemory.dump();
		renderScreen();
	}
	
	public static void printStats() {
		Log.debug("====SNO Information====");
		
		// Count number of implemented instructions
		int instCount = 0;
		for (int k = 0; k < CPU.jmp.length; k++) {
			if (CPU.jmp[k] != null) instCount++;
		}
		Log.debug("Implemented Instructions: " + instCount + "/255");
	}
	
	public static void printMMaps() {
		System.out.println("List of unimplemented but used hardware registers: ");
		for (int i=0; i<0x4000; i++) {
			if ((/*(LoROMMemory)*/mem).mmap[i] instanceof UnimplementedHardwareRegister) {
				System.out.format("  0x%04x\n", i+0x2000);
			}
		}
	}
	
	public static void renderScreen() {
		CGRAM.readColors();
		CGRAM.testColors();
		Sprites.dumpOBJ();
	}
	
}
