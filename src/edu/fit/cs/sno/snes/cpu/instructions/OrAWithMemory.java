package edu.fit.cs.sno.snes.cpu.instructions;

import edu.fit.cs.sno.snes.common.Instruction;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.AddressingMode;
import edu.fit.cs.sno.snes.cpu.CPU;

public class OrAWithMemory {
	public static final String mnemonic = "ORA";

	/**
	 * OR Accumulator with Memory DP Indexed Indirect, X
	 * 0x01
	 */
	public static Instruction orAMemDPIndirectX = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_INDIRECT_X, Size.MEMORY_A) {
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.a.getValue() | CPU.dataReg.getValue());
			
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
	 * OR Accumulator with Memory Stack Relative
	 * 0x03
	 */ 
	public static Instruction orAMemStackRelative = new Instruction(AddressingMode.STACK_RELATIVE, Size.MEMORY_A) {
		{this.name = "OR Accumulator with Memory Stack Relative";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.a.getValue() | CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * OR Accumulator with Memory Direct Page
	 * 0x05
	 */ 
	public static Instruction orAMemDP = new Instruction(AddressingMode.DIRECT_PAGE, Size.MEMORY_A) {
		{this.name = "OR Accumulator with Memory Direct Page";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.a.getValue() | CPU.dataReg.getValue());
			
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
	 * OR Accumulator with Memory DP Indirect Long
	 * 0x07
	 */ 
	public static Instruction orAMemDPIndirectLong = new Instruction(AddressingMode.DIRECT_PAGE_INDIRECT_LONG, Size.MEMORY_A) {
		{this.name = "OR Accumulator with Memory DP Indirect Long";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.a.getValue() | CPU.dataReg.getValue());
			
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
	 * OR Accumulator with Memory Immediate
	 * 0x09
	 */ 
	public static Instruction orAMemImmediate = new Instruction(AddressingMode.IMMEDIATE_MEMORY) {
		{this.name = "OR Accumulator with Memory Immediate";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.a.getValue() | CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 2;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * OR Accumulator with Memory Absolute
	 * 0x0D
	 */ 
	public static Instruction orAMemAbsolute = new Instruction(AddressingMode.ABSOLUTE, Size.MEMORY_A) {
		{this.name = "OR Accumulator with Memory Absolute";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.a.getValue() | CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * OR Accumulator with Memory Absolute Long
	 * 0x0F
	 */ 
	public static Instruction orAMemAbsoluteLong = new Instruction(AddressingMode.ABSOLUTE_LONG, Size.MEMORY_A) {
		{this.name = "OR Accumulator with Memory Absolute Long";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.a.getValue() | CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 5;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * OR Accumulator with Memory Direct Page Indirect Indexed Y
	 * 0x11
	 */ 
	public static Instruction orAMemDPIndirectY = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_INDIRECT_Y, Size.MEMORY_A) {
		{this.name = "OR Accumulator with Memory Direct Page Indirect Indexed Y";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.a.getValue() | CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 5;
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
	 * OR Accumulator with Memory Direct Page Indirect
	 * 0x12
	 */ 
	public static Instruction orAMemDPIndirect = new Instruction(AddressingMode.DIRECT_PAGE_INDIRECT, Size.MEMORY_A) {
		{this.name = "OR Accumulator with Memory Direct Page Indirect";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.a.getValue() | CPU.dataReg.getValue());
			
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
	 * OR Accumulator with Memory Stack Relative Indirect Indexed Y
	 * 0x13
	 */ 
	public static Instruction orAMemSRIndirectY = new Instruction(AddressingMode.STACK_RELATIVE_INDIRECT_INDEXED_Y, Size.MEMORY_A) {
		{this.name = "OR Accumulator with Memory Stack Relative Indirect Indexed Y";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.a.getValue() | CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 7;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * OR Accumulator with Memory Direct Page Indexed X
	 * 0x15
	 */ 
	public static Instruction orAMemDPIndexedX = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_X, Size.MEMORY_A) {
		{this.name = "OR Accumulator with Memory Direct Page Indexed X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.a.getValue() | CPU.dataReg.getValue());
			
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
	 * OR Accumulator with Memory Direct Page Indirect Long Indexed Y
	 * 0x17
	 */ 
	public static Instruction orAMemDPIndirectLongY = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_INDIRECT_LONG_Y, Size.MEMORY_A) {
		{this.name = "OR Accumulator with Memory Direct Page Indirect Long Indexed Y";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.a.getValue() | CPU.dataReg.getValue());
			
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
	 * OR Accumulator with Memory Absolute Indexed Y
	 * 0x19
	 */ 
	public static Instruction orAMemAbsoluteY = new Instruction(AddressingMode.ABSOLUTE_INDEXED_Y, Size.MEMORY_A) {
		{this.name = "OR Accumulator with Memory Absolute Indexed Y";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.a.getValue() | CPU.dataReg.getValue());
			
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
	 * OR Accumulator with Memory Absolute Indexed X
	 * 0x1D
	 */ 
	public static Instruction orAMemAbsoluteX = new Instruction(AddressingMode.ABSOLUTE_INDEXED_X, Size.MEMORY_A) {
		{this.name = "OR Accumulator with Memory Absolute Indexed X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.a.getValue() | CPU.dataReg.getValue());
			
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
	 * OR Accumulator with Memory Absolute Long Indexed X
	 * 0x1F
	 */ 
	public static Instruction orAMemAbsoluteLongX = new Instruction(AddressingMode.ABSOLUTE_LONG_INDEXED_X, Size.MEMORY_A) {
		{this.name = "OR Accumulator with Memory Absolute Long Indexed X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.a.getValue() | CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 5;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
}
