package edu.fit.cs.sno.snes.cpu.instructions;

import edu.fit.cs.sno.snes.common.Instruction;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.AddressingMode;
import edu.fit.cs.sno.snes.cpu.CPU;

public class LoadYFromMemory {
	public static final String mnemonic = "LDY";

	/**
	 * Load Index Register Y from Memory Immediate
	 * 0xA0
	 */ 
	public static Instruction loadYImmediate = new Instruction(AddressingMode.IMMEDIATE_INDEX, Size.INDEX) {
		{this.name = "Load Index Register Y from Memory Immediate";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.y.setValue(CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.y.isNegative());
			CPU.status.setZero(CPU.y.getValue() == 0);

			int cycles = 2;
			if (!CPU.status.isIndexRegister())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Load Index Register Y from Memory Absolute
	 * 0xAC
	 */ 
	public static Instruction loadYAbsolute = new Instruction(AddressingMode.ABSOLUTE, Size.INDEX) {
		{this.name = "Load Index Register Y from Memory Absolute";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.y.setValue(CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.y.isNegative());
			CPU.status.setZero(CPU.y.getValue() == 0);

			int cycles = 4;
			if (!CPU.status.isIndexRegister())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Load Index Register Y from Memory Direct Page
	 * 0xA4
	 */ 
	public static Instruction loadYDirectPage = new Instruction(AddressingMode.DIRECT_PAGE, Size.INDEX) {
		{this.name = "Load Index Register Y from Memory Direct Page";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.y.setValue(CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.y.isNegative());
			CPU.status.setZero(CPU.y.getValue() == 0);

			int cycles = 3;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			if (!CPU.status.isIndexRegister())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Load Index Register Y from Memory Absolute Indexed X
	 * 0xBC
	 */ 
	public static Instruction loadYAbsoluteX = new Instruction(AddressingMode.ABSOLUTE_INDEXED_X, Size.INDEX) {
		{this.name = "Load Index Register Y from Memory Absolute Indexed X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.y.setValue(CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.y.isNegative());
			CPU.status.setZero(CPU.y.getValue() == 0);
			
			int cycles = 4;
			if (CPU.indexCrossedPageBoundary)
				cycles++;
			if (!CPU.status.isIndexRegister())
				cycles++;
			return cycles;
			
		}
	};
	
	/**
	 * Load Index Register Y from Memory Direct Page Indexed X
	 * 0xB4
	 */ 
	public static Instruction loadYDirectPageX = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_X, Size.INDEX) {
		{this.name = "Load Index Register Y from Memory Direct Page Indexed X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.y.setValue(CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.y.isNegative());
			CPU.status.setZero(CPU.y.getValue() == 0);
			
			int cycles = 4;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			if (!CPU.status.isIndexRegister())
				cycles++;
			return cycles;
		}
	};
	
}
