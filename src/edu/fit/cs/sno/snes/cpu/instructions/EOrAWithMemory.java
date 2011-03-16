package edu.fit.cs.sno.snes.cpu.instructions;

import edu.fit.cs.sno.snes.common.Instruction;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.AddressingMode;
import edu.fit.cs.sno.snes.cpu.CPU;

public class EOrAWithMemory {
	public static final String mnemonic = "EOR";
	/**
	 * EOR Accumulator with Memory DP Indexed Indirect, X
	 * 0x41
	 */
	public static Instruction eorAMemDPIndirectX = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_INDIRECT_X, Size.MEMORY_A) {
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.a.getValue() ^ CPU.dataReg.getValue());
			
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
	 * EOR Accumulator with Memory Stack Relative
	 * 0x43
	 */ 
	public static Instruction eorAMemStackRelative = new Instruction(AddressingMode.STACK_RELATIVE, Size.MEMORY_A) {
		{this.name = "EOR Accumulator with Memory Stack Relative";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.a.getValue() ^ CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * EOR Accumulator with Memory Direct Page
	 * 0x45
	 */ 
	public static Instruction eorAMemDP = new Instruction(AddressingMode.DIRECT_PAGE, Size.MEMORY_A) {
		{this.name = "EOR Accumulator with Memory Direct Page";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.a.getValue() ^ CPU.dataReg.getValue());
			
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
	 * EOR Accumulator with Memory DP Indirect Long
	 * 0x47
	 */ 
	public static Instruction eorAMemDPIndirectLong = new Instruction(AddressingMode.DIRECT_PAGE_INDIRECT_LONG, Size.MEMORY_A) {
		{this.name = "EOR Accumulator with Memory DP Indirect Long";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.a.getValue() ^ CPU.dataReg.getValue());
			
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
	 * EOR Accumulator with Memory Immediate
	 * 0x49
	 */ 
	public static Instruction eorAMemImmediate = new Instruction(AddressingMode.IMMEDIATE_MEMORY) {
		{this.name = "EOR Accumulator with Memory Immediate";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.a.getValue() ^ CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 2;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * EOR Accumulator with Memory Absolute
	 * 0x4D
	 */ 
	public static Instruction eorAMemAbsolute = new Instruction(AddressingMode.ABSOLUTE, Size.MEMORY_A) {
		{this.name = "EOR Accumulator with Memory Absolute";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.a.getValue() ^ CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * EOR Accumulator with Memory Absolute Long
	 * 0x4F
	 */ 
	public static Instruction eorAMemAbsoluteLong = new Instruction(AddressingMode.ABSOLUTE_LONG, Size.MEMORY_A) {
		{this.name = "EOR Accumulator with Memory Absolute Long";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.a.getValue() ^ CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 5;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * EOR Accumulator with Memory Direct Page Indirect Indexed Y
	 * 0x51
	 */ 
	public static Instruction eorAMemDPIndirectY = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_INDIRECT_Y, Size.MEMORY_A) {
		{this.name = "EOR Accumulator with Memory Direct Page Indirect Indexed Y";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.a.getValue() ^ CPU.dataReg.getValue());
			
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
	 * EOR Accumulator with Memory Direct Page Indirect
	 * 0x52
	 */ 
	public static Instruction eorAMemDPIndirect = new Instruction(AddressingMode.DIRECT_PAGE_INDIRECT, Size.MEMORY_A) {
		{this.name = "EOR Accumulator with Memory Direct Page Indirect";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.a.getValue() ^ CPU.dataReg.getValue());
			
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
	 * EOR Accumulator with Memory Stack Relative Indirect Indexed Y
	 * 0x53
	 */ 
	public static Instruction eorAMemSRIndirectY = new Instruction(AddressingMode.STACK_RELATIVE_INDIRECT_INDEXED_Y, Size.MEMORY_A) {
		{this.name = "EOR Accumulator with Memory Stack Relative Indirect Indexed Y";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.a.getValue() ^ CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 7;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * EOR Accumulator with Memory Direct Page Indexed X
	 * 0x55
	 */ 
	public static Instruction eorAMemDPIndexedX = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_X, Size.MEMORY_A) {
		{this.name = "EOR Accumulator with Memory Direct Page Indexed X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.a.getValue() ^ CPU.dataReg.getValue());
			
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
	 * EOR Accumulator with Memory Direct Page Indirect Long Indexed Y
	 * 0x57
	 */ 
	public static Instruction eorAMemDPIndirectLongY = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_INDIRECT_LONG_Y, Size.MEMORY_A) {
		{this.name = "EOR Accumulator with Memory Direct Page Indirect Long Indexed Y";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.a.getValue() ^ CPU.dataReg.getValue());
			
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
	 * EOR Accumulator with Memory Absolute Indexed Y
	 * 0x59
	 */ 
	public static Instruction eorAMemAbsoluteY = new Instruction(AddressingMode.ABSOLUTE_INDEXED_Y, Size.MEMORY_A) {
		{this.name = "EOR Accumulator with Memory Absolute Indexed Y";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.a.getValue() ^ CPU.dataReg.getValue());
			
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
	 * EOR Accumulator with Memory Absolute Indexed X
	 * 0x5D
	 */ 
	public static Instruction eorAMemAbsoluteX = new Instruction(AddressingMode.ABSOLUTE_INDEXED_X, Size.MEMORY_A) {
		{this.name = "EOR Accumulator with Memory Absolute Indexed X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.a.getValue() ^ CPU.dataReg.getValue());
			
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
	 * EOR Accumulator with Memory Absolute Long Indexed X
	 * 0x5F
	 */ 
	public static Instruction eorAMemAbsoluteLongX = new Instruction(AddressingMode.ABSOLUTE_LONG_INDEXED_X, Size.MEMORY_A) {
		{this.name = "EOR Accumulator with Memory Absolute Long Indexed X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.a.setValue(CPU.a.getValue() ^ CPU.dataReg.getValue());
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 5;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
}
