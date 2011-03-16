package edu.fit.cs.sno.snes.cpu.instructions;

import edu.fit.cs.sno.snes.common.Instruction;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.AddressingMode;
import edu.fit.cs.sno.snes.cpu.CPU;

public class LoadXFromMemory {
	public static final String mnemonic = "LDX";

	/**
	 * Load Index Register X from Memory Immediate
	 * 0xA2
	 */ 
	public static Instruction loadXImmediate = new Instruction(AddressingMode.IMMEDIATE_INDEX, Size.INDEX) {
		{this.name = "Load Index Register X from Memory Immediate";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.x.setValue(CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.x.isNegative());
			CPU.status.setZero(CPU.x.getValue() == 0);
			
			int cycles = 2;
			if (!CPU.status.isIndexRegister())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Load Index Register X from Memory Absolute
	 * 0xAE
	 */ 
	public static Instruction loadXAbsolute = new Instruction(AddressingMode.ABSOLUTE, Size.INDEX) {
		{this.name = "Load Index Register X from Memory Absolute";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.x.setValue(CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.x.isNegative());
			CPU.status.setZero(CPU.x.getValue() == 0);
			
			int cycles = 4;
			if (!CPU.status.isIndexRegister())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Load Index Register X from Memory Direct Page
	 * 0xA6
	 */ 
	public static Instruction loadXDirectPage = new Instruction(AddressingMode.DIRECT_PAGE, Size.INDEX) {
		{this.name = "Load Index Register X from Memory Direct Page";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.x.setValue(CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.x.isNegative());
			CPU.status.setZero(CPU.x.getValue() == 0);
			
			int cycles = 3;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			if (!CPU.status.isIndexRegister())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Load Index Register X from Memory Absolute Indexed Y
	 * 0xBE
	 */ 
	public static Instruction loadXAbsoluteY = new Instruction(AddressingMode.ABSOLUTE_INDEXED_Y, Size.INDEX) {
		{this.name = "Load Index Register X from Memory Absolute Indexed Y";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.x.setValue(CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.x.isNegative());
			CPU.status.setZero(CPU.x.getValue() == 0);
			
			int cycles = 4;
			if (CPU.indexCrossedPageBoundary)
				cycles++;
			if (!CPU.status.isIndexRegister())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Load Index Register X from Memory Direct Page Indexed Y
	 * 0xB6
	 */ 
	public static Instruction loadXDirectPageY = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_Y, Size.INDEX) {
		{this.name = "Load Index Register X from Memory Direct Page Indexed Y";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.x.setValue(CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.x.isNegative());
			CPU.status.setZero(CPU.x.getValue() == 0);
			
			int cycles = 4;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			if (!CPU.status.isIndexRegister())
				cycles++;
			return cycles;
		}
	};
	
}
