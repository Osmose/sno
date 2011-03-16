package edu.fit.cs.sno.snes.cpu.instructions;

import edu.fit.cs.sno.snes.common.Instruction;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.AddressingMode;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.util.Util;

public class CompareAWithMemory {
	public static final String mnemonic = "CMP";

	/**
	 * Compare A with Memory Direct Page Indexed Indirect X
	 * 0xC1
	 */ 
	public static Instruction cmpADPIndirectX = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_INDIRECT_X, Size.MEMORY_A) {
		{this.name = "Compare A with Memory Direct Page Indexed Indirect X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int newVal = Util.limit(size.getRealSize(), CPU.a.getValue() - CPU.dataReg.getValue());
			
			CPU.status.setNegative((newVal & size.getRealSize().topBitMask) != 0);
			CPU.status.setZero(newVal == 0);
			CPU.status.setCarry(CPU.a.getValue() >= CPU.dataReg.getValue());
			
			int cycles = 6;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Compare A with Memory Stack Relative
	 * 0xC3
	 */ 
	public static Instruction cmpAStackRelative = new Instruction(AddressingMode.STACK_RELATIVE, Size.MEMORY_A) {
		{this.name = "Compare A with Memory Stack Relative";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int newVal = Util.limit(size.getRealSize(), CPU.a.getValue() - CPU.dataReg.getValue());
			
			CPU.status.setNegative((newVal & size.getRealSize().topBitMask) != 0);
			CPU.status.setZero(newVal == 0);
			CPU.status.setCarry(CPU.a.getValue() >= CPU.dataReg.getValue());
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Compare A with Memory Direct Page
	 * 0xC5
	 */ 
	public static Instruction cmpADP = new Instruction(AddressingMode.DIRECT_PAGE, Size.MEMORY_A) {
		{this.name = "Compare A with Memory Direct Page";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int newVal = Util.limit(size.getRealSize(), CPU.a.getValue() - CPU.dataReg.getValue());
			
			CPU.status.setNegative((newVal & size.getRealSize().topBitMask) != 0);
			CPU.status.setZero(newVal == 0);
			CPU.status.setCarry(CPU.a.getValue() >= CPU.dataReg.getValue());
			
			int cycles = 3;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Compare A with Memory Direct Page Indirect Long
	 * 0xC7
	 */ 
	public static Instruction cmpADPIndirectLong = new Instruction(AddressingMode.DIRECT_PAGE_INDIRECT_LONG, Size.MEMORY_A) {
		{this.name = "Compare A with Memory Direct Page Indirect Long";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int newVal = Util.limit(size.getRealSize(), CPU.a.getValue() - CPU.dataReg.getValue());
			
			CPU.status.setNegative((newVal & size.getRealSize().topBitMask) != 0);
			CPU.status.setZero(newVal == 0);
			CPU.status.setCarry(CPU.a.getValue() >= CPU.dataReg.getValue());
			
			int cycles = 6;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Compare A with Memory Immediate
	 * 0xC9
	 */ 
	public static Instruction cmpAImmediate = new Instruction(AddressingMode.IMMEDIATE_MEMORY, Size.MEMORY_A) {
		{this.name = "Compare A with Memory Immediate";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int newVal = Util.limit(size.getRealSize(), CPU.a.getValue() - CPU.dataReg.getValue());
			
			CPU.status.setNegative((newVal & size.getRealSize().topBitMask) != 0);
			CPU.status.setZero(newVal == 0);
			CPU.status.setCarry(CPU.a.getValue() >= CPU.dataReg.getValue());
			
			int cycles = 2;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Compare A with Memory Absolute
	 * 0xCD
	 */ 
	public static Instruction cmpAAbsolute = new Instruction(AddressingMode.ABSOLUTE, Size.MEMORY_A) {
		{this.name = "Compare A with Memory Absolute";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int newVal = Util.limit(size.getRealSize(), CPU.a.getValue() - CPU.dataReg.getValue());
			
			CPU.status.setNegative((newVal & size.getRealSize().topBitMask) != 0);
			CPU.status.setZero(newVal == 0);
			CPU.status.setCarry(CPU.a.getValue() >= CPU.dataReg.getValue());
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Compare A with Memory Absolute Long
	 * 0xCF
	 */ 
	public static Instruction cmpAAbsoluteLong = new Instruction(AddressingMode.ABSOLUTE_LONG, Size.MEMORY_A) {
		{this.name = "Compare A with Memory Absolute Long";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int newVal = Util.limit(size.getRealSize(), CPU.a.getValue() - CPU.dataReg.getValue());
			
			CPU.status.setNegative((newVal & size.getRealSize().topBitMask) != 0);
			CPU.status.setZero(newVal == 0);
			CPU.status.setCarry(CPU.a.getValue() >= CPU.dataReg.getValue());
			
			int cycles = 5;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Compare A with Memory Direct Page Indirect Indexed Y
	 * 0xD1
	 */ 
	public static Instruction cmpADPIndirectY = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_INDIRECT_Y, Size.MEMORY_A) {
		{this.name = "Compare A with Memory Direct Page Indirect Indexed Y";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int newVal = Util.limit(size.getRealSize(), CPU.a.getValue() - CPU.dataReg.getValue());
			
			CPU.status.setNegative((newVal & size.getRealSize().topBitMask) != 0);
			CPU.status.setZero(newVal == 0);
			CPU.status.setCarry(CPU.a.getValue() >= CPU.dataReg.getValue());
			
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
	 * Compare A with Memory Direct Page Indirect
	 * 0xD2
	 */ 
	public static Instruction cmpADPIndirect = new Instruction(AddressingMode.DIRECT_PAGE_INDIRECT, Size.MEMORY_A) {
		{this.name = "Compare A with Memory Direct Page Indirect";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int newVal = Util.limit(size.getRealSize(), CPU.a.getValue() - CPU.dataReg.getValue());
			
			CPU.status.setNegative((newVal & size.getRealSize().topBitMask) != 0);
			CPU.status.setZero(newVal == 0);
			CPU.status.setCarry(CPU.a.getValue() >= CPU.dataReg.getValue());
			
			int cycles = 5;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Compare A with Memory Stack Relative Indirect Indexed Y
	 * 0xD3
	 */ 
	public static Instruction cmpASRIndirectY = new Instruction(AddressingMode.STACK_RELATIVE_INDIRECT_INDEXED_Y, Size.MEMORY_A) {
		{this.name = "Compare A with Memory Stack Relative Indirect Indexed Y";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int newVal = Util.limit(size.getRealSize(), CPU.a.getValue() - CPU.dataReg.getValue());
			
			CPU.status.setNegative((newVal & size.getRealSize().topBitMask) != 0);
			CPU.status.setZero(newVal == 0);
			CPU.status.setCarry(CPU.a.getValue() >= CPU.dataReg.getValue());
			
			int cycles = 7;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Compare A with Memory Direct Page Indexed X
	 * 0xD5
	 */ 
	public static Instruction cmpADPX = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_X, Size.MEMORY_A) {
		{this.name = "Compare A with Memory Direct Page Indexed X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int newVal = Util.limit(size.getRealSize(), CPU.a.getValue() - CPU.dataReg.getValue());
			
			CPU.status.setNegative((newVal & size.getRealSize().topBitMask) != 0);
			CPU.status.setZero(newVal == 0);
			CPU.status.setCarry(CPU.a.getValue() >= CPU.dataReg.getValue());
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Compare A with Memory Direct Page Indirect Long Indexed Y
	 * 0xD7
	 */ 
	public static Instruction cmpADPIndirectLongY = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_INDIRECT_LONG_Y, Size.MEMORY_A) {
		{this.name = "Compare A with Memory Direct Page Indirect Long Indexed Y";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int newVal = Util.limit(size.getRealSize(), CPU.a.getValue() - CPU.dataReg.getValue());
			
			CPU.status.setNegative((newVal & size.getRealSize().topBitMask) != 0);
			CPU.status.setZero(newVal == 0);
			CPU.status.setCarry(CPU.a.getValue() >= CPU.dataReg.getValue());
			
			int cycles = 6;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Compare A with Memory Absolute Indexed Y
	 * 0xD9
	 */ 
	public static Instruction cmpAAbsoluteY = new Instruction(AddressingMode.ABSOLUTE_INDEXED_Y, Size.MEMORY_A) {
		{this.name = "Compare A with Memory Absolute Indexed Y";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int newVal = Util.limit(size.getRealSize(), CPU.a.getValue() - CPU.dataReg.getValue());
			
			CPU.status.setNegative((newVal & size.getRealSize().topBitMask) != 0);
			CPU.status.setZero(newVal == 0);
			CPU.status.setCarry(CPU.a.getValue() >= CPU.dataReg.getValue());
			
			int cycles = 6;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if (CPU.indexCrossedPageBoundary)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Compare A with Memory Absolute Indexed X
	 * 0xDD
	 */ 
	public static Instruction cmpAAbsoluteX = new Instruction(AddressingMode.ABSOLUTE_INDEXED_X, Size.MEMORY_A) {
		{this.name = "Compare A with Memory Absolute Indexed X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int newVal = Util.limit(size.getRealSize(), CPU.a.getValue() - CPU.dataReg.getValue());
			
			CPU.status.setNegative((newVal & size.getRealSize().topBitMask) != 0);
			CPU.status.setZero(newVal == 0);
			CPU.status.setCarry(CPU.a.getValue() >= CPU.dataReg.getValue());
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if (CPU.indexCrossedPageBoundary)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Compare A with Memory Absolute Long Indexed X
	 * 0xDF
	 */ 
	public static Instruction cmpAAbsoluteLongX = new Instruction(AddressingMode.ABSOLUTE_LONG_INDEXED_X, Size.MEMORY_A) {
		{this.name = "Compare A with Memory Absolute Long Indexed X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int newVal = Util.limit(size.getRealSize(), CPU.a.getValue() - CPU.dataReg.getValue());
			
			CPU.status.setNegative((newVal & size.getRealSize().topBitMask) != 0);
			CPU.status.setZero(newVal == 0);
			CPU.status.setCarry(CPU.a.getValue() >= CPU.dataReg.getValue());
			
			int cycles = 5;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
}
