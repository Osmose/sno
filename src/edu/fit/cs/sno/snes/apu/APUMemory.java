package edu.fit.cs.sno.snes.apu;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import edu.fit.cs.sno.util.Settings;
import edu.fit.cs.sno.util.Util;

public class APUMemory {

	private static int[] mem = new int[0x8000];
	public static volatile int apuio0_in = 0;
	public static volatile int apuio0_out = 0;
	public static volatile int apuio1_in = 0;
	public static volatile int apuio1_out = 0;
	public static volatile int apuio2_in = 0;
	public static volatile int apuio2_out = 0;
	public static volatile int apuio3_in = 0;
	public static volatile int apuio3_out = 0;
	
	private static boolean iplRomEnable = true;
	
	/**
	 * Initial program loaded into APU
	 * Courtesy of anomie's SPC-700 doc
	 */
	private static int[] iplRom = new int[] {
		0xCD, 0xEF, 0xBD, 0xE8, 0x00, 0xC6, 0x1D, 0xD0, 0xFC, 0x8F, 0xAA, 0xF4, 0x8F, 0xBB, 0xF5, 0x78,
		0xCC, 0xF4, 0xD0, 0xFB, 0x2F, 0x19, 0xEB, 0xF4, 0xD0, 0xFC, 0x7E, 0xF4, 0xD0, 0x0B, 0xE4, 0xF5,
		0xCB, 0xF4, 0xD7, 0x00, 0xFC, 0xD0, 0xF3, 0xAB, 0x01, 0x10, 0xEF, 0x7E, 0xF4, 0x10, 0xEB, 0xBA,
		0xF6, 0xDA, 0x00, 0xBA, 0xF4, 0xC4, 0xF4, 0xDD, 0x5D, 0xD0, 0xDB, 0x1F, 0x00, 0x00, 0xC0, 0xFF
	};
	
	private static int[] iplRam = new int[64];
	
	public static int get(int addr) {
		if (Util.inRange(addr, 0, 0x00EF)) {	// Zero page
			return mem[addr];
		} else if (Util.inRange(addr, 0x00F0, 0x00FF)) {	// Function registers
			return readReg(addr);
		} else if (Util.inRange(addr, 0x0100, 0x7FFF)) {	// Sound RAM
			return mem[addr];
		} else if (Util.inRange(addr, 0xFFC0, 0xFFFF)) {	// IPL-ROM
			if (iplRomEnable) {
				return iplRom[addr - 0xFFC0];
			} else {
				return iplRam[addr - 0xFFC0];
			}
		}
		
		if (Settings.isTrue(Settings.MEM_THROW_INVALID_ADDR))
			throw new RuntimeException(String.format("APU: Read invalid memory address: 0x%04x",addr));
		
		return 0;
	}
	
	public static int getShort(int addr) {
		return get(addr) + (get(addr+1) << 8);
	}
	
	public static void set(int addr, int val) {
		val &= 0xFF;
		if (Util.inRange(addr, 0, 0x00EF)) {	// Zero page
			mem[addr] = val;
			return;
		} else if (Util.inRange(addr, 0x00F0, 0x00FF)) {	// Function registers
			writeReg(addr, val);
			return;
		} else if (Util.inRange(addr, 0x0100, 0x7FFF)) {	// Sound RAM
			mem[addr] = val;
			return;
		} else if (Util.inRange(addr, 0xFFC0, 0xFFFF)) {	// IPL-ROM
			if (!iplRomEnable) {
				iplRam[addr - 0xFFC0] = val;
			}
			return;
		}
		
		if (Settings.isTrue(Settings.MEM_THROW_INVALID_ADDR))
			throw new RuntimeException(String.format("APU: Write invalid memory address: 0x%04x",addr));
	}
	
	public static void setShort(int addr, int val) {
		set(addr, val);
		set(addr + 1, val >> 8);
	}
	
	private static int readReg(int addr) {
		int t; // Used by T#OUT
		switch (addr) {
			case 0xF0:	// TEST
				return 0;
			case 0xF1:	// CONTROL
				return 0; // Can't read!
			case 0xF2:	// DSPADDR
				return 0;
			case 0xF3:	// DSPDATA
				return 0;
			case 0xF4:	// CPUIO0
				return apuio0_in;
			case 0xF5:	// CPUIO1
				return apuio1_in;
			case 0xF6:	// CPUIO2
				return apuio2_in;
			case 0xF7:	// CPUIO3
				return apuio3_in;
			case 0xF8:	// Memory
			case 0xF9:	// Memory
				return mem[addr];
			case 0xFA:	// T0TARGET
				return 0;	// Can't read!
			case 0xFB:	// T1TARGET
				return 0;	// Can't read!
			case 0xFC:	// T2TARGET
				return 0;	// Can't read!
			case 0xFD:	// T0OUT
				t = APU.t0.getCounter();
				APU.t0.setCounter(0);
				return t;
			case 0xFE:	// T1OUT
				t = APU.t1.getCounter();
				APU.t1.setCounter(0);
				return t;
			case 0xFF:	// T2OUT
				t = APU.t2.getCounter();
				APU.t2.setCounter(0);
				return t;
		}
		
		if (Settings.isTrue(Settings.MEM_THROW_INVALID_ADDR))
			throw new RuntimeException(String.format("APU: Read invalid memory register: 0x%04x",addr));
		return 0;
	}
	
	private static void writeReg(int addr, int val) {
		switch (addr) {
			case 0xF0:	// TEST
				break;
			case 0xF1:	// CONTROL
				iplRomEnable = ((val & 0x80) != 0 ? true : false);
				APU.t2.setEnabled((val & 0x04) != 0 ? true : false);
				APU.t1.setEnabled((val & 0x02) != 0 ? true : false);
				APU.t0.setEnabled((val & 0x01) != 0 ? true : false);
				
				// Reset output ports
				if ((val & 0x20) != 0) {
					apuio0_out = 0;
					apuio1_out = 0;
				}
				if ((val & 0x10) != 0) {
					apuio2_out = 0;
					apuio3_out = 0;
				}
				break;
			case 0xF2:	// DSPADDR
				break;
			case 0xF3:	// DSPDATA
				break;
			case 0xF4:	// CPUIO0
				apuio0_out = val;
				break;
			case 0xF5:	// CPUIO1
				apuio1_out = val;
				break;
			case 0xF6:	// CPUIO2
				apuio2_out = val;
				break;
			case 0xF7:	// CPUIO3
				apuio3_out = val;
				break;
			case 0xFA:	// T0TARGET
				APU.t0.setTarget(val);
				break;
			case 0xFB:	// T1TARGET
				APU.t1.setTarget(val);
				break;
			case 0xFC:	// T2TARGET
				APU.t2.setTarget(val);
				break;
			case 0xFD:	// T0OUT
				break;
			case 0xFE:	// T1OUT
				break;
			case 0xFF:	// T2OUT
				break;
		}
		
		// Mapped registers still write to memory
		//mem[addr] = val & 0xFF;
	}
	
	public static void reset() {
		System.out.println("Resetting APUMemory");
		Arrays.fill(mem, 0);
		
		apuio0_in = 0;
		apuio0_out = 0;
		apuio1_in = 0;
		apuio1_out = 0;
		apuio2_in = 0;
		apuio2_out = 0;
		apuio3_in = 0;
		apuio3_out = 0;
		
		iplRomEnable = true;
	}
	
	public static void dump() {
		if (Settings.get(Settings.DEBUG_DIR) != null) {
			try {
				String fname = Settings.get(Settings.DEBUG_DIR) + "/apuRam.bin";
				FileOutputStream fos = new FileOutputStream(fname);
				for(int i = 0; i < mem.length; i++)
					fos.write(mem[i]);
				fos.close();
			} catch (IOException e) {
				System.out.println("Unable to dump apuRam");
				e.printStackTrace();
			}
		}
	}
}
