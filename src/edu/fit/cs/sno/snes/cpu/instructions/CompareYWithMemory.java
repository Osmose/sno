package edu.fit.cs.sno.snes.cpu.instructions;

import edu.fit.cs.sno.snes.common.Instruction;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.AddressingMode;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.util.Util;

public class CompareYWithMemory {
	public static final String mnemonic = "CPY";

	/**
	 * Compare Y with Memory Immediate
	 * 0xC0
	 */ 
	public static Instruction cmpYImmediate = new Instruction(AddressingMode.IMMEDIATE_INDEX, Size.INDEX) {
		{this.name = "Compare Y with Memory Immediate";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int newVal = Util.limit(size.getRealSize(), CPU.y.getValue() - CPU.dataReg.getValue());
			
			CPU.status.setNegative((newVal & size.getRealSize().topBitMask) != 0);
			CPU.status.setZero(newVal == 0);
			CPU.status.setCarry(CPU.y.getValue() >= CPU.dataReg.getValue());
			
			int cycles = 2;
			if (!CPU.status.isIndexRegister())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Compare Y with Memory Direct Page
	 * 0xC4
	 */ 
	public static Instruction cmpYDP = new Instruction(AddressingMode.DIRECT_PAGE, Size.INDEX) {
		{this.name = "Compare Y with Memory Direct Page";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int newVal = Util.limit(size.getRealSize(), CPU.y.getValue() - CPU.dataReg.getValue());
			
			CPU.status.setNegative((newVal & size.getRealSize().topBitMask) != 0);
			CPU.status.setZero(newVal == 0);
			CPU.status.setCarry(CPU.y.getValue() >= CPU.dataReg.getValue());
			
			int cycles = 3;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			if (!CPU.status.isIndexRegister())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Compare Y with Memory Absolute
	 * 0xCC
	 */ 
	public static Instruction cmpYAbsolute = new Instruction(AddressingMode.ABSOLUTE, Size.INDEX) {
		{this.name = "Compare Y with Memory Absolute";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int newVal = Util.limit(size.getRealSize(), CPU.y.getValue() - CPU.dataReg.getValue());
			
			CPU.status.setNegative((newVal & size.getRealSize().topBitMask) != 0);
			CPU.status.setZero(newVal == 0);
			CPU.status.setCarry(CPU.y.getValue() >= CPU.dataReg.getValue());
			
			int cycles = 4;
			if (!CPU.status.isIndexRegister())
				cycles++;
			return cycles;
		}
	};
	
}
