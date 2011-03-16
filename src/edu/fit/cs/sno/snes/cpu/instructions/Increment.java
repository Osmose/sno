package edu.fit.cs.sno.snes.cpu.instructions;

import edu.fit.cs.sno.snes.common.Instruction;
import edu.fit.cs.sno.snes.cpu.AddressingMode;
import edu.fit.cs.sno.snes.cpu.CPU;

/**
 * Increment CPU instructions
 */
public class Increment {
	public static final String mnemonic = "INC";

	/**
	 * Increment Accumulator
	 * 0x1A
	 */ 
	public static Instruction incAccumulator = new Instruction(AddressingMode.ACCUMULATOR) {
		{this.name = "Increment Accumulator";}
		public int run(int[] args) {
			CPU.a.add(1);
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 2;
            return cycles;
		}
	};
	
	/**
	 * Increment Direct Page
	 * 0xE6
	 */ 
	public static Instruction incDirectPage = new Instruction(AddressingMode.DIRECT_PAGE) {
		{this.name = "Increment Direct Page";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.dataReg.add(1);
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
	 * Increment Absolute
	 * 0xEE
	 */ 
	public static Instruction incAbsolute = new Instruction(AddressingMode.ABSOLUTE) {
		{this.name = "Increment Absolute";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.dataReg.add(1);
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
	 * Increment Direct Page Indexed, X
	 * 0xF6
	 */
	public static Instruction incDirectPageX = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_X) {
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.dataReg.add(1);
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
	 * Increment Absolute Indexed, X
	 * 0xFE
	 */
	public static Instruction incAbsoluteX = new Instruction(AddressingMode.ABSOLUTE_INDEXED_X) {
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.dataReg.add(1);
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
	 * Increment Index Register X
	 * 0xE8
	 */ 
	public static Instruction incX = new Instruction() {
		{this.name = "Increment Index Register X";
		 this.mnemonic = "INX";}
		public int run(int[] args) {
			CPU.x.add(1);
			
			CPU.status.setNegative(CPU.x.isNegative());
			CPU.status.setZero(CPU.x.getValue() == 0);
			int cycles = 2;
			return cycles;
		}
	};
	
	/**
	 * Increment Index Register Y
	 * 0xC8
	 */ 
	public static Instruction incY = new Instruction() {
		{this.name = "Increment Index Register Y";
		 this.mnemonic = "INY";}
		public int run(int[] args) {
			CPU.y.add(1);
			
			CPU.status.setNegative(CPU.y.isNegative());
			CPU.status.setZero(CPU.y.getValue() == 0);
			
			int cycles = 2;
			return cycles;
		}
	};
	
}
