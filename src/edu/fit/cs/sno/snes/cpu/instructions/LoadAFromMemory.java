package edu.fit.cs.sno.snes.cpu.instructions;

import edu.fit.cs.sno.snes.common.Instruction;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.AddressingMode;
import edu.fit.cs.sno.snes.cpu.CPU;

public class LoadAFromMemory {
	public static final String mnemonic = "LDA";

	/**
	 * Load Accumulator from Memory Direct Page Indexed Indirect X
	 * 0xA1
	 */ 
	public static Instruction loadADPIndexedIndirectX = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_INDIRECT_X, Size.MEMORY_A) {
		{this.name = "Load Accumulator from Memory Direct Page Indexed Indirect X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 6;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Load Accumulator from Memory Stack Relative
	 * 0xA3
	 */ 
	public static Instruction loadAStackRelative = new Instruction(AddressingMode.STACK_RELATIVE, Size.MEMORY_A) {
		{this.name = "Load Accumulator from Memory Stack Relative";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Load Accumulator from Memory Direct Page
	 * 0xA5
	 */ 
	public static Instruction loadADirectPage = new Instruction(AddressingMode.DIRECT_PAGE, Size.MEMORY_A) {
		{this.name = "Load Accumulator from Memory Direct Page";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 3;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Load Accumulator from Memory Direct Page Indirect Long
	 * 0xA7
	 */ 
	public static Instruction loadADPIndirectLong = new Instruction(AddressingMode.DIRECT_PAGE_INDIRECT_LONG, Size.MEMORY_A) {
		{this.name = "Load Accumulator from Memory Direct Page Indirect Long";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 6;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Load Accumulator from Memory Immediate
	 * 0xA9
	 */ 
	public static Instruction loadAImmediate = new Instruction(AddressingMode.IMMEDIATE_MEMORY, Size.MEMORY_A) {
		{this.name = "Load Accumulator from Memory Immediate";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 2;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Load Accumulator from Memory Absolute
	 * 0xAD
	 */ 
	public static Instruction loadAAbsolute = new Instruction(AddressingMode.ABSOLUTE, Size.MEMORY_A) {
		{this.name = "Load Accumulator from Memory Absolute";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Load Accumulator from Memory Absolute Long
	 * 0xAF
	 */ 
	public static Instruction loadAAbsoluteLong = new Instruction(AddressingMode.ABSOLUTE_LONG, Size.MEMORY_A) {
		{this.name = "Load Accumulator from Memory Absolute Long";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 5;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Load Accumulator from Memory Direct Page Indirect Indexed Y
	 * 0xB1
	 */ 
	public static Instruction loadADPIndirectIndexedY = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_INDIRECT_Y, Size.MEMORY_A) {
		{this.name = "Load Accumulator from Memory Direct Page Indirect Indexed Y";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 6;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			if (CPU.indexCrossedPageBoundary)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Load Accumulator from Memory Direct Page Indirect
	 * 0xB2
	 */ 
	public static Instruction loadADPIndirect = new Instruction(AddressingMode.DIRECT_PAGE_INDIRECT, Size.MEMORY_A) {
		{this.name = "Load Accumulator from Memory Direct Page Indirect";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 5;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Load Accumulator from Memory Stack Relative Indirect Indexed Y
	 * 0xB3
	 */ 
	public static Instruction loadASRIndirectIndexedY = new Instruction(AddressingMode.STACK_RELATIVE_INDIRECT_INDEXED_Y, Size.MEMORY_A) {
		{this.name = "Load Accumulator from Memory Stack Relative Indirect Indexed Y";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 7;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Load Accumulator from Memory Direct Page Indexed X
	 * 0xB5
	 */ 
	public static Instruction loadADirectPageX = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_X, Size.MEMORY_A) {
		{this.name = "Load Accumulator from Memory Direct Page Indexed X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Load Accumulator from Memory Direct Page Indirect Long Indexed Y
	 * 0xB7
	 */ 
	public static Instruction loadADPIndirectLongIndexedY = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_INDIRECT_LONG_Y, Size.MEMORY_A) {
		{this.name = "Load Accumulator from Memory Direct Page Indirect Long Indexed Y";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 6;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Load Accumulator from Memory Absolute Indexed Y
	 * 0xB9
	 */ 
	public static Instruction loadAAbsoluteIndexedY = new Instruction(AddressingMode.ABSOLUTE_INDEXED_Y, Size.MEMORY_A) {
		{this.name = "Load Accumulator from Memory Absolute Indexed Y";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if (CPU.indexCrossedPageBoundary)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Load Accumulator from Memory Absolute Indexed X
	 * 0xBD
	 */ 
	public static Instruction loadAAbsoluteIndexedX = new Instruction(AddressingMode.ABSOLUTE_INDEXED_X, Size.MEMORY_A) {
		{this.name = "Load Accumulator from Memory Absolute Indexed X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if (CPU.indexCrossedPageBoundary)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Load Accumulator from Memory Absolute Long Indexed X
	 * 0xBF
	 */ 
	public static Instruction loadAAbsoluteLongIndexedX = new Instruction(AddressingMode.ABSOLUTE_LONG_INDEXED_X, Size.MEMORY_A) {
		{this.name = "Load Accumulator from Memory Absolute Long Indexed X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 5;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
}
