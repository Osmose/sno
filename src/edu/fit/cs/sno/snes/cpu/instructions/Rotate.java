package edu.fit.cs.sno.snes.cpu.instructions;

import edu.fit.cs.sno.snes.common.Instruction;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.AddressingMode;
import edu.fit.cs.sno.snes.cpu.CPU;

public class Rotate {
	public static final String mnemonic = "ROL";

	/**
	 * Rotate Memory or Accumulator Left Accumulator
	 * 0x2A
	 */ 
	public static Instruction rotateLeftAccumulator = new Instruction(AddressingMode.ACCUMULATOR) {
		{this.name = "Rotate Memory or Accumulator Left Accumulator";}
		public int run(int[] args) {
			// Check top bit
			boolean newCarry = CPU.a.isNegative();
			CPU.a.setValue((CPU.a.getValue() << 1) + (CPU.status.isCarry() ? 1 : 0));
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry(newCarry);
			
			int cycles = 2;
			return cycles;
		}
	};
	
	/**
	 * Rotate Memory or Accumulator Left Absolute
	 * 0x2E
	 */ 
	public static Instruction rotateLeftAbsolute = new Instruction(AddressingMode.ABSOLUTE, Size.MEMORY_A) {
		{this.name = "Rotate Memory or Accumulator Left Absolute";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			// Check top bit
			boolean newCarry = CPU.dataReg.isNegative();
			CPU.dataReg.setValue((CPU.dataReg.getValue() << 1) + (CPU.status.isCarry() ? 1 : 0));
			CPU.saveDataReg();
			
			CPU.status.setNegative(CPU.dataReg.isNegative());
			CPU.status.setZero(CPU.dataReg.getValue() == 0);
			CPU.status.setCarry(newCarry);
			
			int cycles = 6;
			if (!CPU.status.isMemoryAccess())
				cycles += 2;
			return cycles;
		}
	};
	
	/**
	 * Rotate Memory or Accumulator Left Direct Page
	 * 0x26
	 */ 
	public static Instruction rotateLeftDP = new Instruction(AddressingMode.DIRECT_PAGE, Size.MEMORY_A) {
		{this.name = "Rotate Memory or Accumulator Left Direct Page";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			// Check top bit
			boolean newCarry = CPU.dataReg.isNegative();
			CPU.dataReg.setValue((CPU.dataReg.getValue() << 1) + (CPU.status.isCarry() ? 1 : 0));
			CPU.saveDataReg();
			
			CPU.status.setNegative(CPU.dataReg.isNegative());
			CPU.status.setZero(CPU.dataReg.getValue() == 0);
			CPU.status.setCarry(newCarry);
			
			int cycles = 5;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			if (!CPU.status.isMemoryAccess())
				cycles += 2;
			return cycles;
		}
	};
	
	/**
	 * Rotate Memory or Accumulator Left Absolute Indexed X
	 * 0x3E
	 */ 
	public static Instruction rotateLeftAbsoluteX = new Instruction(AddressingMode.ABSOLUTE_INDEXED_X, Size.MEMORY_A) {
		{this.name = "Rotate Memory or Accumulator Left Absolute Indexed X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			// Check top bit
			boolean newCarry = CPU.dataReg.isNegative();
			CPU.dataReg.setValue((CPU.dataReg.getValue() << 1) + (CPU.status.isCarry() ? 1 : 0));
			CPU.saveDataReg();
			
			CPU.status.setNegative(CPU.dataReg.isNegative());
			CPU.status.setZero(CPU.dataReg.getValue() == 0);
			CPU.status.setCarry(newCarry);
			
			int cycles = 7;
			if (!CPU.status.isMemoryAccess())
				cycles += 2;
			return cycles;
		}
	};
	
	/**
	 * Rotate Memory or Accumulator Left Direct Page Indexed X
	 * 0x36
	 */ 
	public static Instruction rotateLeftDPX = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_X, Size.MEMORY_A) {
		{this.name = "Rotate Memory or Accumulator Left Direct Page Indexed X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			// Check top bit
			boolean newCarry = CPU.dataReg.isNegative();
			CPU.dataReg.setValue((CPU.dataReg.getValue() << 1) + (CPU.status.isCarry() ? 1 : 0));
			CPU.saveDataReg();
			
			CPU.status.setNegative(CPU.dataReg.isNegative());
			CPU.status.setZero(CPU.dataReg.getValue() == 0);
			CPU.status.setCarry(newCarry);
			
			int cycles = 6;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			if (!CPU.status.isMemoryAccess())
				cycles += 2;
			return cycles;
		}
	};
	
	/**
	 * Rotate Memory or Accumulator Right Accumulator
	 * 0x6A
	 */ 
	public static Instruction rotateRightAccumulator = new Instruction(AddressingMode.ACCUMULATOR) {
		{this.name = "Rotate Memory or Accumulator Right Accumulator";
		 this.mnemonic = "ROR";}
		public int run(int[] args) {
			// Check top bit
			boolean newCarry = (CPU.a.getValue() & 0x1) != 0;
			CPU.a.setValue((CPU.a.getValue() >> 1) + (CPU.status.isCarry() ? size.getRealSize().topBitMask : 0));
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry(newCarry);
			
			int cycles = 2;
			return cycles;
		}
	};
	
	/**
	 * Rotate Memory or Accumulator Right Absolute
	 * 0x6E
	 */ 
	public static Instruction rotateRightAbsolute = new Instruction(AddressingMode.ABSOLUTE, Size.MEMORY_A) {
		{this.name = "Rotate Memory or Accumulator Right Absolute";
		 this.mnemonic = "ROR";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			// Check top bit
			boolean newCarry = (CPU.dataReg.getValue() & 0x1) != 0;
			CPU.dataReg.setValue((CPU.dataReg.getValue() >> 1) + (CPU.status.isCarry() ? size.getRealSize().topBitMask : 0));
			CPU.saveDataReg();
			
			CPU.status.setNegative(CPU.dataReg.isNegative());
			CPU.status.setZero(CPU.dataReg.getValue() == 0);
			CPU.status.setCarry(newCarry);
			
			int cycles = 6;
			if (!CPU.status.isMemoryAccess())
				cycles += 2;
			return cycles;
		}
	};
	
	/**
	 * Rotate Memory or Accumulator Right Direct Page
	 * 0x66
	 */ 
	public static Instruction rotateRightDP = new Instruction(AddressingMode.DIRECT_PAGE, Size.MEMORY_A) {
		{this.name = "Rotate Memory or Accumulator Right Direct Page";
		 this.mnemonic = "ROR";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			// Check top bit
			boolean newCarry = (CPU.dataReg.getValue() & 0x1) != 0;
			CPU.dataReg.setValue((CPU.dataReg.getValue() >> 1) + (CPU.status.isCarry() ? size.getRealSize().topBitMask : 0));
			CPU.saveDataReg();
			
			CPU.status.setNegative(CPU.dataReg.isNegative());
			CPU.status.setZero(CPU.dataReg.getValue() == 0);
			CPU.status.setCarry(newCarry);
			
			int cycles = 5;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			if (!CPU.status.isMemoryAccess())
				cycles += 2;
			return cycles;
		}
	};
	
	/**
	 * Rotate Memory or Accumulator Right Absolute Indexed X
	 * 0x7E
	 */ 
	public static Instruction rotateRightAbsoluteX = new Instruction(AddressingMode.ABSOLUTE_INDEXED_X, Size.MEMORY_A) {
		{this.name = "Rotate Memory or Accumulator Right Absolute Indexed X";
		 this.mnemonic = "ROR";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			// Check top bit
			boolean newCarry = (CPU.dataReg.getValue() & 0x1) != 0;
			CPU.dataReg.setValue((CPU.dataReg.getValue() >> 1) + (CPU.status.isCarry() ? size.getRealSize().topBitMask : 0));
			CPU.saveDataReg();
			
			CPU.status.setNegative(CPU.dataReg.isNegative());
			CPU.status.setZero(CPU.dataReg.getValue() == 0);
			CPU.status.setCarry(newCarry);
			
			int cycles = 7;
			if (!CPU.status.isMemoryAccess())
				cycles += 2;
			return cycles;
		}
	};
	
	/**
	 * Rotate Memory or Accumulator Right Direct Page Indexed X
	 * 0x76
	 */ 
	public static Instruction rotateRightDPX = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_X, Size.MEMORY_A) {
		{this.name = "Rotate Memory or Accumulator Right Direct Page Indexed X";
		 this.mnemonic = "ROR";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			// Check top bit
			boolean newCarry = (CPU.dataReg.getValue() & 0x1) != 0;
			CPU.dataReg.setValue((CPU.dataReg.getValue() >> 1) + (CPU.status.isCarry() ? size.getRealSize().topBitMask : 0));
			CPU.saveDataReg();
			
			CPU.status.setNegative(CPU.dataReg.isNegative());
			CPU.status.setZero(CPU.dataReg.getValue() == 0);
			CPU.status.setCarry(newCarry);
			
			int cycles = 6;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			if (!CPU.status.isMemoryAccess())
				cycles += 2;
			return cycles;
		}
	};
}
