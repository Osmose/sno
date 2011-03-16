package edu.fit.cs.sno.snes.cpu.instructions;

import edu.fit.cs.sno.snes.common.Instruction;
import edu.fit.cs.sno.snes.cpu.AddressingMode;
import edu.fit.cs.sno.snes.cpu.CPU;

/**
 * Decrement CPU instructions
 */
public class Decrement {
	public static final String mnemonic = "DEC";

	/**
	 * Decrement Accumulator
	 * 0x3A
	 */ 
	public static Instruction decAccumulator = new Instruction(AddressingMode.ACCUMULATOR) {
		{this.name = "Decrement Accumulator";}
		public int run(int[] args) {
			CPU.a.subtract(1);
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 2;
			return cycles;
		}
	};
	
	/**
	 * Decrement Direct Page
	 * 0xC6
	 */ 
	public static Instruction decDirectPage = new Instruction(AddressingMode.DIRECT_PAGE) {
		{this.name = "Decrement Direct Page";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.dataReg.subtract(1);
			CPU.saveDataReg();
			
			CPU.status.setNegative(CPU.dataReg.isNegative());
			CPU.status.setZero(CPU.dataReg.getValue() == 0);
			
			int cycles = 5;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			if(!CPU.status.isMemoryAccess())
				cycles += 2;
			return cycles;
		}
	};
	
	/**
	 * Decrement Absolute
	 * 0xCE
	 */ 
	public static Instruction decAbsolute = new Instruction(AddressingMode.ABSOLUTE) {
		{this.name = "Decrement Absolute";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.dataReg.subtract(1);
			CPU.saveDataReg();
			
			CPU.status.setNegative(CPU.dataReg.isNegative());
			CPU.status.setZero(CPU.dataReg.getValue() == 0);
			
			int cycles = 6;
			if(!CPU.status.isMemoryAccess())
				cycles += 2;
			return cycles;
		}
	};
	
	/**
	 * Decrement Direct Page Indexed, X
	 * 0xD6
	 */
	public static Instruction decDirectPageX = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_X) {
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.dataReg.subtract(1);
			CPU.saveDataReg();
			
			CPU.status.setNegative(CPU.dataReg.isNegative());
			CPU.status.setZero(CPU.dataReg.getValue() == 0);
			
			int cycles = 6;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			if(!CPU.status.isMemoryAccess())
				cycles += 2;
			return cycles;
		}
	};
	
	/**
	 * Decrement Absolute Indexed, X
	 * 0xDE
	 */
	public static Instruction decAbsoluteX = new Instruction(AddressingMode.ABSOLUTE_INDEXED_X) {
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.dataReg.subtract(1);
			CPU.saveDataReg();
			
			CPU.status.setNegative(CPU.dataReg.isNegative());
			CPU.status.setZero(CPU.dataReg.getValue() == 0);
			
			int cycles = 7;
			if(!CPU.status.isMemoryAccess())
				cycles += 2;
			return cycles;
		}
	};
	
	/**
	 * Decrement Index Register X
	 * 0xCA
	 */ 
	public static Instruction decX = new Instruction() {
		{
			this.name = "Decrement Index Register X";
			this.mnemonic = "DEX";
		}
		public int run(int[] args) {
			CPU.x.subtract(1);
			
			CPU.status.setNegative(CPU.x.isNegative());
			CPU.status.setZero(CPU.x.getValue() == 0);
			
			int cycles = 2;
			return cycles;
		}
	};
	
	/**
	 * Decrement Index Register Y
	 * 0x88
	 */ 
	public static Instruction decY = new Instruction() {
		{
			this.name = "Decrement Index Register Y";
			this.mnemonic = "DEY";
		}
		public int run(int[] args) {
			CPU.y.subtract(1);
			
			CPU.status.setNegative(CPU.y.isNegative());
			CPU.status.setZero(CPU.y.getValue() == 0);
			
			int cycles = 2;
			return cycles;
		}
	};
	
}
