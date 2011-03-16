package edu.fit.cs.sno.snes.cpu.instructions;

import edu.fit.cs.sno.snes.common.Instruction;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.AddressingMode;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.util.Util;

public class CompareXWithMemory {
	public static final String mnemonic = "CPX";

	/**
	 * Compare X with Memory Immediate
	 * 0xE0
	 */ 
	public static Instruction cmpXImmediate = new Instruction(AddressingMode.IMMEDIATE_INDEX, Size.INDEX) {
		{this.name = "Compare X with Memory Immediate";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int newVal = Util.limit(size.getRealSize(), CPU.x.getValue() - CPU.dataReg.getValue());
			
			CPU.status.setNegative((newVal & size.getRealSize().topBitMask) != 0);
			CPU.status.setZero(newVal == 0);
			CPU.status.setCarry(CPU.x.getValue() >= CPU.dataReg.getValue());
			
			int cycles = 2;
			if (!CPU.status.isIndexRegister())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Compare X with Memory Direct Page
	 * 0xE4
	 */ 
	public static Instruction cmpXDP = new Instruction(AddressingMode.DIRECT_PAGE, Size.INDEX) {
		{this.name = "Compare X with Memory Direct Page";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int newVal = Util.limit(size.getRealSize(), CPU.x.getValue() - CPU.dataReg.getValue());
			
			CPU.status.setNegative((newVal & size.getRealSize().topBitMask) != 0);
			CPU.status.setZero(newVal == 0);
			CPU.status.setCarry(CPU.x.getValue() >= CPU.dataReg.getValue());
			
			int cycles = 3;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			if (!CPU.status.isIndexRegister())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Compare X with Memory Absolute
	 * 0xEC
	 */ 
	public static Instruction cmpXAbsolute = new Instruction(AddressingMode.ABSOLUTE, Size.INDEX) {
		{this.name = "Compare X with Memory Absolute";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int newVal = Util.limit(size.getRealSize(), CPU.x.getValue() - CPU.dataReg.getValue());
			
			CPU.status.setNegative((newVal & size.getRealSize().topBitMask) != 0);
			CPU.status.setZero(newVal == 0);
			CPU.status.setCarry(CPU.x.getValue() >= CPU.dataReg.getValue());
			
			int cycles = 4;
			if (!CPU.status.isIndexRegister())
				cycles++;
			return cycles;
		}
	};
	
}
