package edu.fit.cs.sno.snes.cpu.hwregs;

import edu.fit.cs.sno.snes.mem.HWRegister;
import edu.fit.cs.sno.util.Log;

public class CPUMath {

	/**
	 * 0x4202 - The multiplicand
	 */
	public static HWRegister wrmpya = new HWRegister() {
		{val = 0xFF;} // Instance initializer, this register defaults to 0xFF on power on/reset
		// This register does nothing special by itself
		@Override
		public void onRead() {
			//Log.err("Cannot read from register 0x4202");
		}
	};
	
	/**
	 * 0x4203 - The multiplier
	 */
	public static HWRegister wrmpyb = new HWRegister() {
		@Override
		public void onWrite(int value) {
			super.onWrite(value);
			int result = wrmpya.getValue() * wrmpyb.getValue();
			rdmpyl.val = (result & 0xFF);
			rdmpyh.val = ((result>>8) & 0xFF);
		}
		@Override
		public void onRead() {
			//Log.err("Cannot read from register 0x4203");
		}
	};
	
	/**
	 * 0x4204 - Dividend, low byte
	 */
	public static HWRegister wrdivl = new HWRegister() {
		@Override
		public void onRead() {
			//Log.err("Cannot read from register 0x4204");
		}
	};
	
	/**
	 * 0x4205 - Dividend, high byte
	 */
	public static HWRegister wrdivh = new HWRegister() {
		@Override
		public void onRead() {
			//Log.err("Cannot read from register 0x4205");
		}
	};
	
	/**
	 * 0x4206 - Divisor
	 */
	public static HWRegister wrdivb = new HWRegister() {
		@Override
		public void onRead() {
			//Log.err("Cannot read from register 0x4206");
		}
		@Override
		public void onWrite(int value) {
			super.onWrite(value);
			int divisor = val & 0xFF;
			int dividend = ((wrdivh.val & 0xFF) <<8) | (wrdivl.val & 0xFF);
			
			int result=0, remainder =0;
			if (divisor != 0) {
				result = (int)(dividend / divisor);
				remainder = dividend % divisor;
			}
			
			
			// Update the registers for the result
			rddivl.val = result & 0xFF;
			rddivh.val = (result >> 8) & 0xFF;
			
			// Update the registers for the remainder
			rdmpyl.val = remainder & 0xFF;
			rdmpyh.val = (remainder >> 8) & 0xFF;
		}
	};
	
	/**
	 * 0x4214 - Low byte of the quotient
	 */
	public static HWRegister rddivl = new HWRegister() {
		@Override
		public void onWrite(int value) {
			Log.err("Cannot write to register 0x4214");
		}
	};
	
	/**
	 * 0x4215 - High byte of the quotient
	 */
	public static HWRegister rddivh = new HWRegister() {
		@Override
		public void onWrite(int value) {
			Log.err("Cannot write to register 0x4215");
		}
	};
	
	/**
	 * 0x4216 - Low byte of the multiply result(or the remainder from a divide)
	 */
	public static HWRegister rdmpyl = new HWRegister() {
		@Override
		public void onWrite(int value) {
			Log.err("Cannot write from register 0x4216");
		}
	};
	
	/**
	 * 0x4217 - High byte of the multiply result(or the remainder from a divide)
	 */
	public static HWRegister rdmpyh = new HWRegister() {
		@Override
		public void onWrite(int value) {
			Log.err("Cannot write from register 0x4217");
		}
	};
}
