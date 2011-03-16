package edu.fit.cs.sno.snes.mem;

import edu.fit.cs.sno.snes.common.Size;

public abstract class HWRegister {
	public int val;
	
	public HWRegister() {
		val = 0;
	}
	
	public HWRegister(Size size, int val) {
		this.val = val;
	}
	
	public int getValue() {
		onRead();
		return val & 0xFF;
	}
	public void onRead() {
		// Do nothing by default
	}
	public void setValue(int val) {
		onWrite(val);
	}
	public void onWrite(int value) {
		this.val = value;
	}

}
