package edu.fit.cs.sno.snes.common;

import edu.fit.cs.sno.util.Util;

public class Register {

	private int val;
	private Size size;
	
	public Register(Size size, int val) {
		this.val = val;
		this.size = size.getRealSize();
	}
	
	public int getValue() {
		return getValue(size);
	}
	
	public int getValue(Size size) {
		if (size == Size.SHORT) {
			return val & 0xFFFF;
		} else {
			return val & 0xFF;
		}
	}
	
	public int getRealValue() {
		return val;
	}
	
	public void setRealValue(int val) {
		this.val = val & 0xFFFF;
	}
	
	public void setValue(Size size, int val) {
		this.size = size.getRealSize();
		
		setValue(val);
	}
	
	public void setValue(int val) {
		if (size == Size.BYTE) {
			this.val = (this.val & 0xFF00) + (val & 0x00FF);
		} else {
			this.val = val;
		}
	}
	
	public Size getSize() {
		return size;
	}
	
	public void setSize(Size size) {
		this.size = size.getRealSize();
	}
	
	public void add(int add) {
		setValue(val + add);
	}
	
	public void subtract(int sub) {
		setValue(val - sub);
	}
	
	public boolean isNegative() {
		return (val & size.topBitMask) > 0;
	}
}
