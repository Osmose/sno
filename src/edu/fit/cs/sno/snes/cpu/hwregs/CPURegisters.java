package edu.fit.cs.sno.snes.cpu.hwregs;

import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.input.Input;
import edu.fit.cs.sno.snes.input.SNESController;
import edu.fit.cs.sno.snes.mem.HWRegister;
import edu.fit.cs.sno.util.Log;
import edu.fit.cs.sno.util.Settings;

public class CPURegisters {

	/**
	 * 0x4200 - Enable/Disable interrupts
	 */
	public static HWRegister interruptEnable = new HWRegister() {
		
		@Override
		public void onWrite(int value) {
			// Non Maskable Interrupt Enable/Disable(high bit of the register)
			boolean temp = (value & 0x80) == 0x80;
			if (temp != CPU.NMIEnable) {
				CPU.NMIEnable = temp;
				if (!Settings.isTrue(Settings.CPU_ALT_DEBUG)) {
					if(CPU.NMIEnable)
						Log.debug("Enabling NMI interrupts");
					else
						Log.debug("Disabling NMI interrupts");
				}
			}
			
			// IRQEnable
			CPU.irqEnable = (val >> 4) & 0x03;
			Log.debug("IRQ Enable set to " + CPU.irqEnable);
			
			// Standard Controller Enable
			temp = (value & 0x01) == 0x01;
			if (temp != CPU.standardControllerRead) {
				CPU.standardControllerRead = temp;
				if (!Settings.isTrue(Settings.CPU_ALT_DEBUG)) {
					if(CPU.standardControllerRead)
						Log.debug("Enabling automatic reading of standard controller");
					else
						Log.debug("Disabling automatic reading of standard controller");
				}
			}
			
			// Timer Enable
			boolean vtemp = ((value >> 5) & 0x01) == 0x01;
			boolean htemp = ((value >> 4) & 0x01) == 0x01;
			if (vtemp != CPU.timerV) {
				CPU.timerV = vtemp;
				if (!Settings.isTrue(Settings.CPU_ALT_DEBUG)) {
					if(CPU.timerV) Log.debug("Enabling V-Count timer");
					else Log.debug("Disabling V-Count timer");
				}
			}
			if (htemp != CPU.timerH) {
				CPU.timerH = htemp;
				if (!Settings.isTrue(Settings.CPU_ALT_DEBUG)) {
					if(CPU.timerH) Log.debug("Enabling H-Count timer");
					else Log.debug("Disabling H-Count timer");
				}
			}
			// No need to save the value into val.
		};
		@Override
		public void onRead() {
			if (!Settings.isTrue(Settings.CPU_ALT_DEBUG)) Log.err("Game trying to read from 0x4200.  Bad coding practice.");
		};
	};
	
	/**
	 * 0x4207 - H Timer Low Byte
	 */
	public static HWRegister htimel = new HWRegister() {
		public void onWrite(int value) {
			CPU.htime = (CPU.htime & 0x100) | (value & 0xFF);
		}
	};
	
	/**
	 * 0x4208 - H Timer High Byte
	 */
	public static HWRegister htimeh = new HWRegister() {
		public void onWrite(int value) {
			CPU.htime = (CPU.htime & 0xFF) | ((value << 8) & 0x01);
		}
	};
	
	/**
	 * 0x4209 - V Timer Low Byte
	 */
	public static HWRegister vtimel = new HWRegister() {
		public void onWrite(int value) {
			CPU.vtime = (CPU.vtime & 0x100) | (value & 0xFF);
		}
	};
	
	/**
	 * 0x420A - V Timer High Byte
	 */
	public static HWRegister vtimeh = new HWRegister() {
		public void onWrite(int value) {
			CPU.vtime = (CPU.vtime & 0xFF) | ((value << 8) & 0x01);
		}
	};
	
	/**
	 * 0x420D - ROM Access Speed Register(MEMSEL)
	 */
	public static HWRegister memsel = new HWRegister() {
		{this.val = 0x00;}
		@Override
		public void onWrite(int value) {
			if (!Settings.isTrue(Settings.CPU_ALT_DEBUG)) {
				if ((value & 0x01) != 0) {
					System.out.println("Enabling FastRom");
				} else {
					System.out.println("Disabling FastRom");
				}
			}
			// TODO: actually do what it says it's doing
		}
	};
	
	/**
	 * 0x4210 - NMI Register
	 * Contents: x000vvvv
	 * x = Bitflag for if v-blank generates an NMI
	 * v = 5a22 Version number
	 */
	public static HWRegister rdnmi = new HWRegister() {
		@Override
		public void onWrite(int value) {
			this.val = value;
		}
		@Override
		public int getValue() {
			int temp = this.val;
			this.val &= 0x7F;
			return temp;
		}
	};
	
	/**
	 * 0x4211 - IRQ Flag
	 */
	public static HWRegister timeup = new HWRegister() {
		public void onWrite(int value) {
			CPU.irqFlag = false;
		}
		
		public int getValue() {
			int t = (CPU.irqFlag ? 0x80 : 0);
			CPU.irqFlag = false;
			return t;
		}
	};
	
	/**
	 * 0x4016 - NES-Style Joypad Access Port 1
	 * Read: ------ca
	 * Write: -------l
	 * 
	 * First, the CPU writes a 1 to l to "latch" the controller. Then, the
	 * controller data can be read out of 4016 and 4017, one button at a time.
	 * Controller 1's data is in a/b, 2 is in c/d in joyser1
	 * 
	 */
	private static int curButton = 16;
	public static HWRegister joyser0 = new HWRegister() {
		// TODO: Support multiple controllers
		public void onWrite(int value) {
			if ((value & 1) != 0) {
				curButton = 0;
			}
		}
		
		public int getValue() {
			int val = 0;
			val |= (Input.readButton(curButton) ? 1 : 0);
			
			curButton++;
			return val;
		}
	};
	
	/**
	 * 0x4017 - NES-Style Joypad Access Port 2
	 * Read: ---111db
	 */
	public static HWRegister joyser1 = new HWRegister() {
		public int getValue() {
			int val = 0x1C;
			val |= (Input.readButton(curButton) ? 1 : 0);
			
			return val;
		}
	};
	
	public static HWRegister joy1l = new HWRegister() {};
	public static HWRegister joy1h = new HWRegister() {};
	public static HWRegister joy2l = new HWRegister() {};
	public static HWRegister joy2h = new HWRegister() {};
	public static HWRegister joy3l = new HWRegister() {};
	public static HWRegister joy3h = new HWRegister() {};
	public static HWRegister joy4l = new HWRegister() {};
	public static HWRegister joy5h = new HWRegister() {};
}
