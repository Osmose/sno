package edu.fit.cs.sno.snes.apu.hwregs;

import edu.fit.cs.sno.snes.apu.APUMemory;
import edu.fit.cs.sno.snes.mem.HWRegister;

public class APURegisters {

	/**
	 * 0x2140 - Audio IO Port 0
	 */
	public static HWRegister apuio0 = new HWRegister() {
		public void onWrite(int val) {
			APUMemory.apuio0_in = val;
		}
		
		public int getValue() {
			return APUMemory.apuio0_out;
		}
	};
	
	/**
	 * 0x2141 - Audio IO Port 1
	 */
	public static HWRegister apuio1 = new HWRegister() {
		public void onWrite(int val) {
			APUMemory.apuio1_in = val;
		}
		
		public int getValue() {
			return APUMemory.apuio1_out;
		}
	};
	
	/**
	 * 0x2142 - Audio IO Port 2
	 */
	public static HWRegister apuio2 = new HWRegister() {
		public void onWrite(int val) {
			APUMemory.apuio2_in = val;
		}
		
		public int getValue() {
			return APUMemory.apuio2_out;
		}
	};
	
	/**
	 * 0x2143 - Audio IO Port 3
	 */
	public static HWRegister apuio3 = new HWRegister() {
		public void onWrite(int val) {
			APUMemory.apuio3_in = val;
		}
		
		public int getValue() {
			return APUMemory.apuio3_out;
		}
	};
}
