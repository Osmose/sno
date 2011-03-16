package edu.fit.cs.sno.snes.common;

import java.lang.reflect.Field;

import edu.fit.cs.sno.snes.cpu.AddressingMode;

public abstract class Instruction {

	public int argCount;
	public Size size;
	public AddressingMode addrMode;
	public String name = "UNKNOWN";
	public String mnemonic = "XXX";
	
	public Instruction() {
		argCount = 0;
		size = Size.MEMORY_A;
		addrMode = AddressingMode.IMPLIED;
		updateMnemonic();
	}
	
	public Instruction(AddressingMode addrMode, int argCount, Size size) {
		this.argCount = argCount;
		this.size = size;
		this.addrMode = addrMode;
		updateMnemonic();
	}
	
	public Instruction(AddressingMode addrMode, int argCount) {
		this.argCount = argCount;
		this.addrMode = addrMode;
		this.size = Size.MEMORY_A;
		updateMnemonic();
	}
	
	public Instruction(AddressingMode addrMode, Size size) {
		this.argCount = 0;
		this.addrMode = addrMode;
		this.size = size;
		updateMnemonic();
	}
	
	public Instruction(AddressingMode addrMode) {
		this.argCount = 0;
		this.addrMode = addrMode;
		this.size = Size.MEMORY_A;
		updateMnemonic();
	}
	
	public void updateMnemonic() {
		try {
			Class x = this.getClass().getEnclosingClass();
			if (x != null) {
				Field f = x.getField("mnemonic");
				this.mnemonic = (String)f.get(null);
			}
		}catch(Exception e) {
			this.mnemonic = "xxx";
		}
	}
	
	
	public abstract int run(int[] args);
	
}
