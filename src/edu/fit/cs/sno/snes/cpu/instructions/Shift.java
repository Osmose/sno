package edu.fit.cs.sno.snes.cpu.instructions;

import edu.fit.cs.sno.snes.common.Instruction;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.AddressingMode;
import edu.fit.cs.sno.snes.cpu.CPU;

public class Shift {
	public static final String mnemonic = "ASL";

	/**
	 * Shift Memory or Accumulator Left Accumulator
	 * 0x0A
	 */ 
	public static Instruction shiftLeftAccumulator = new Instruction(AddressingMode.ACCUMULATOR) {
		{this.name = "Shift Memory or Accumulator Left Accumulator";}
		public int run(int[] args) {
			// Check the top bit to set the carry flag
			boolean newCarry = CPU.a.isNegative();
			
			CPU.a.setValue(CPU.a.getValue() << 1);
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry(newCarry);
			
			int cycles = 2;
			return cycles;
		}
	};
	
	/**
	 * Shift Memory or Accumulator Left Direct Page
	 * 0x06
	 */ 
	public static Instruction shiftLeftDP = new Instruction(AddressingMode.DIRECT_PAGE, Size.MEMORY_A) {
		{this.name = "Shift Memory or Accumulator Left Direct Page";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			// Check the top bit to set the carry flag
			boolean newCarry = CPU.dataReg.isNegative();
			CPU.dataReg.setValue(CPU.dataReg.getValue() << 1);
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
	 * Shift Memory or Accumulator Left Absolute
	 * 0x0E
	 */ 
	public static Instruction shiftLeftAbsolute = new Instruction(AddressingMode.ABSOLUTE, Size.MEMORY_A) {
		{this.name = "Shift Memory or Accumulator Left Absolute";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			// Check the top bit to set the carry flag
			boolean newCarry = CPU.dataReg.isNegative();
			CPU.dataReg.setValue(CPU.dataReg.getValue() << 1);
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
	 * Shift Memory or Accumulator Left Absolute Indexed X
	 * 0x1E
	 */ 
	public static Instruction shiftLeftAbsoluteX = new Instruction(AddressingMode.ABSOLUTE_INDEXED_X, Size.MEMORY_A) {
		{this.name = "Shift Memory or Accumulator Left Absolute Indexed X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			// Check the top bit to set the carry flag
			boolean newCarry = CPU.dataReg.isNegative();
			CPU.dataReg.setValue(CPU.dataReg.getValue() << 1);
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
	 * Shift Memory or Accumulator Left Direct Page Indexed X
	 * 0x16
	 */ 
	public static Instruction shiftLeftDPX = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_X, Size.MEMORY_A) {
		{this.name = "Shift Memory or Accumulator Left Direct Page Indexed X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			// Check the top bit to set the carry flag
			boolean newCarry = CPU.dataReg.isNegative();
			CPU.dataReg.setValue(CPU.dataReg.getValue() << 1);
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
	 * Logical Shift Memory or Accumulator Right Accumulator
	 * 0x4A
	 */ 
	public static Instruction shiftRightAccumulator = new Instruction() {
		{this.name = "Logical Shift Memory or Accumulator Right Accumulator";
		 this.mnemonic = "LSR";}
		public int run(int[] args) {
			// Check top bit
			boolean newCarry = (CPU.a.getValue() & 0x1) != 0;
			CPU.a.setValue(CPU.a.getValue() >> 1);
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry(newCarry);
			
			int cycles = 2;
			return cycles;
		}
	};
	
	/**
	 * Logical Shift Memory or Accumulator Right Absolute
	 * 0x4E
	 */ 
	public static Instruction shiftRightAbsolute = new Instruction(AddressingMode.ABSOLUTE, Size.MEMORY_A) {
		{this.name = "Logical Shift Memory or Accumulator Right Absolute";
		 this.mnemonic = "LSR";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			// Check top bit
			boolean newCarry = (CPU.dataReg.getValue() & 0x1) != 0;
			CPU.dataReg.setValue(CPU.dataReg.getValue() >> 1);
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
	 * Logical Shift Memory or Accumulator Right Direct Page
	 * 0x46
	 */ 
	public static Instruction shiftRightDP = new Instruction(AddressingMode.DIRECT_PAGE, Size.MEMORY_A) {
		{this.name = "Logical Shift Memory or Accumulator Right Direct Page";
		 this.mnemonic = "LSR";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			// Check top bit
			boolean newCarry = (CPU.dataReg.getValue() & 0x1) != 0;
			CPU.dataReg.setValue(CPU.dataReg.getValue() >> 1);
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
	 * Logical Shift Memory or Accumulator Right Absolute Indexed X
	 * 0x5E
	 */ 
	public static Instruction shiftRightAbsoluteX = new Instruction(AddressingMode.ABSOLUTE_INDEXED_X, Size.MEMORY_A) {
		{this.name = "Logical Shift Memory or Accumulator Right Absolute Indexed X";
		 this.mnemonic = "LSR";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			// Check top bit
			boolean newCarry = (CPU.dataReg.getValue() & 0x1) != 0;
			CPU.dataReg.setValue(CPU.dataReg.getValue() >> 1);
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
	 * Logical Shift Memory or Accumulator Right Direct Page Indexed X
	 * 0x56
	 */ 
	public static Instruction shiftRightDPX = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_X, Size.MEMORY_A) {
		{this.name = "Logical Shift Memory or Accumulator Right Direct Page Indexed X";
		 this.mnemonic = "LSR";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			// Check top bit
			boolean newCarry = (CPU.dataReg.getValue() & 0x1) != 0;
			CPU.dataReg.setValue(CPU.dataReg.getValue() >> 1);
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
