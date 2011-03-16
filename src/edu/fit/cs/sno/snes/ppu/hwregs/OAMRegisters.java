package edu.fit.cs.sno.snes.ppu.hwregs;

import edu.fit.cs.sno.snes.mem.HWRegister;
import edu.fit.cs.sno.snes.ppu.OAM;

public class OAMRegisters {

	/**
	 * 0x2101 - Set object size and character select
	 */
	public static HWRegister OAMSize = new HWRegister() {
		@Override
		public void onWrite(int value) {
			val = value & 0xFF;
			OAM.setObjectSize((val >> 5) & 0x07);
			OAM.setNameSelect((val >> 3) & 0x03);
			OAM.setNameBaseSelect(val & 0x07);
		}
	};
	
	/**
	 * 0x2102 - OAM Address low byte
	 */
	public static HWRegister OAMAddrLow = new HWRegister() {
		@Override
		public void onWrite(int value) {
			val = value & 0xFF;
		}
	};
	
	/**
	 * 0x2103 - OAM Address high byte
	 */
	public static HWRegister OAMAddrHigh = new HWRegister() {
		public int OAMAddress = 0;
		boolean objPriority = false;
		@Override
		public void onWrite(int value) {
			objPriority = ((value & 0x80) == 0x80);
			OAMAddress = ((value & 0x01) << 8);
			
			OAM.updateAddress(OAMAddress | OAMAddrLow.val, objPriority);
		}
	};
	
	/**
	 * 0x2104 - OAM Data Write
	 */
	public static HWRegister OAMWrite = new HWRegister() {
		@Override
		public void onWrite(int value) {
			super.onWrite(value);
			OAM.writeOAM(value & 0xFF);
		}
	};
	
	
	/**
	 * 0x2138 - OAM Data Read
	 */
	public static HWRegister OAMRead = new HWRegister() {
		@Override
		public void onRead() {
			val = OAM.readOAM();
		}
	};
	

}
