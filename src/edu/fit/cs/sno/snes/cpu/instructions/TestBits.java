package edu.fit.cs.sno.snes.cpu.instructions;

import edu.fit.cs.sno.snes.common.Instruction;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.AddressingMode;
import edu.fit.cs.sno.snes.cpu.CPU;

public class TestBits {
	public static final String mnemonic = "BIT";

	/**
	 * Test Memory Bits against Accumulator Immediate
	 * 0x89
	 */ 
	public static Instruction testImmediate = new Instruction(AddressingMode.IMMEDIATE_MEMORY) {
		{this.name = "Test Memory Bits against Accumulator Immediate";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.status.setZero((CPU.a.getValue() & CPU.dataReg.getValue()) == 0);
			
			int cycles = 2;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};

	/**
	 * Test Memory Bits against Accumulator Absolute
	 * 0x2C
	 */ 
	public static Instruction testAbsolute = new Instruction(AddressingMode.ABSOLUTE, Size.MEMORY_A) {
		{this.name = "Test Memory Bits against Accumulator Absolute";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.status.setNegative(CPU.dataReg.isNegative());
			if (this.size.getRealSize() == Size.SHORT)
				CPU.status.setOverflow((CPU.dataReg.getValue() & 0x4000) != 0);
			else
				CPU.status.setOverflow((CPU.dataReg.getValue() & 0x40) != 0);
			CPU.status.setZero((CPU.a.getValue() & CPU.dataReg.getValue()) == 0);
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Test Memory Bits against Accumulator Direct Page
	 * 0x24
	 */ 
	public static Instruction testDP = new Instruction(AddressingMode.DIRECT_PAGE, Size.MEMORY_A) {
		{this.name = "Test Memory Bits against Accumulator Direct Page";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.status.setNegative(CPU.dataReg.isNegative());
			if (this.size.getRealSize() == Size.SHORT)
				CPU.status.setOverflow((CPU.dataReg.getValue() & 0x4000) != 0);
			else
				CPU.status.setOverflow((CPU.dataReg.getValue() & 0x40) != 0);
			CPU.status.setZero((CPU.a.getValue() & CPU.dataReg.getValue()) == 0);
			
			int cycles = 3;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Test Memory Bits against Accumulator Absolute Indexed X
	 * 0x3C
	 */ 
	public static Instruction testAbsoluteX = new Instruction(AddressingMode.ABSOLUTE_INDEXED_X, Size.MEMORY_A) {
		{this.name = "Test Memory Bits against Accumulator Absolute Indexed X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.status.setNegative(CPU.dataReg.isNegative());
			if (this.size.getRealSize() == Size.SHORT)
				CPU.status.setOverflow((CPU.dataReg.getValue() & 0x4000) != 0);
			else
				CPU.status.setOverflow((CPU.dataReg.getValue() & 0x40) != 0);
			CPU.status.setZero((CPU.a.getValue() & CPU.dataReg.getValue()) == 0);
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if (CPU.indexCrossedPageBoundary)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Test Memory Bits against Accumulator Direct Page Indexed X
	 * 0x34
	 */ 
	public static Instruction testDPX = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_X, Size.MEMORY_A) {
		{this.name = "Test Memory Bits against Accumulator Direct Page Indexed X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.status.setNegative(CPU.dataReg.isNegative());
			if (this.size.getRealSize() == Size.SHORT)
				CPU.status.setOverflow((CPU.dataReg.getValue() & 0x4000) != 0);
			else
				CPU.status.setOverflow((CPU.dataReg.getValue() & 0x40) != 0);
			CPU.status.setZero((CPU.a.getValue() & CPU.dataReg.getValue()) == 0);
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};

	/**
	 * Test and Set Memory Bits against Accumulator Absolute
	 * 0x0C
	 */ 
	public static Instruction testSetAbsolute = new Instruction(AddressingMode.ABSOLUTE, Size.MEMORY_A) {
		{this.name = "Test and Set Memory Bits against Accumulator Absolute";
		 this.mnemonic = "TSB";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.status.setZero((CPU.a.getValue() & CPU.dataReg.getValue()) == 0);
			CPU.dataReg.setValue(CPU.a.getValue() | CPU.dataReg.getValue());
			CPU.saveDataReg();
			
			int cycles = 6;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Test and Set Memory Bits against Accumulator Direct Page
	 * 0x04
	 */ 
	public static Instruction testSetDP = new Instruction(AddressingMode.DIRECT_PAGE, Size.MEMORY_A) {
		{this.name = "Test and Set Memory Bits against Accumulator Direct Page";
		 this.mnemonic = "TSB";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.status.setZero((CPU.a.getValue() & CPU.dataReg.getValue()) == 0);
			CPU.dataReg.setValue(CPU.a.getValue() | CPU.dataReg.getValue());
			CPU.saveDataReg();
			
			int cycles = 5;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 *  Test and Reset Memory Bits against Accumulator Direct Page
	 *  0x14
	 */
	public static Instruction testResetDP = new Instruction(AddressingMode.DIRECT_PAGE, Size.MEMORY_A) {

		{this.name = "Test and Reset Memory Bits against Accumulator Direct Page";
		 this.mnemonic = "TRB";}
		public int run(int[] args) {
			int cycles = 5;
			int complement = 0xFF;
			if (!CPU.status.isMemoryAccess()) {
				cycles+=2;
				complement = 0xFFFF;
			}
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.status.setZero((CPU.a.getValue() & CPU.dataReg.getValue()) == 0);
			complement = CPU.a.getValue() ^ complement;
			CPU.dataReg.setValue(complement & CPU.dataReg.getValue());
			CPU.saveDataReg();
			
			return cycles;
		}
	};
	
	/**
	 *  Test and Reset Memory Bits against Accumulator Absolute
	 *  0x1C
	 */
	public static Instruction testResetAbsolute = new Instruction(AddressingMode.ABSOLUTE, Size.MEMORY_A) {

		{this.name = "Test and Reset Memory Bits against Accumulator Absolute";
		 this.mnemonic = "TRB";}
		public int run(int[] args) {
			int cycles = 6;
			int complement = 0xFF;
			if (!CPU.status.isMemoryAccess()) {
				cycles+=2;
				complement = 0xFFFF;
			}
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.status.setZero((CPU.a.getValue() & CPU.dataReg.getValue()) == 0);
			complement = CPU.a.getValue() ^ complement;
			CPU.dataReg.setValue(complement & CPU.dataReg.getValue());
			CPU.saveDataReg();
			
			return cycles;
		}
	};
}
