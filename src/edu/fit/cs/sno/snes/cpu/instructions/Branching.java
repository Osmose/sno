package edu.fit.cs.sno.snes.cpu.instructions;

import edu.fit.cs.sno.snes.common.Instruction;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.AddressingMode;
import edu.fit.cs.sno.snes.cpu.CPU;

public class Branching {

	/**
	 * Branch if Plus 0x10
	 */ 
	public static Instruction branchPlus = new Instruction(AddressingMode.PROGRAM_COUNTER_RELATIVE, Size.BYTE) {
		{this.name = "Branch if Plus ";
		 this.mnemonic = "BPL";}
		public int run(int[] args) {
			int cycles = 2;
			if (!CPU.status.isNegative()) {
				CPU.loadDataRegister(addrMode, size.getRealSize(), args);
				int origPC = CPU.pc.getValue();
				CPU.pc.add(CPU.dataReg.getValue());
				
				cycles++;
				if (CPU.emulationMode && (origPC/CPU.PAGE_SIZE != CPU.pc.getValue() / CPU.PAGE_SIZE)) {
					cycles++;
				}
			}
			return cycles;
		}
	};

	/**
	 * Branch if Minus 0x30
	 */ 
	public static Instruction branchMinus = new Instruction(AddressingMode.PROGRAM_COUNTER_RELATIVE, Size.BYTE) {
		{this.name = "Branch if Minus ";
		 this.mnemonic = "BMI";}
		public int run(int[] args) {
			int cycles = 2;
			if (CPU.status.isNegative()) {
				CPU.loadDataRegister(addrMode, size.getRealSize(), args);
				int origPC = CPU.pc.getValue();
				CPU.pc.add(CPU.dataReg.getValue());
				
				cycles++;
				if (CPU.emulationMode && (origPC/CPU.PAGE_SIZE != CPU.pc.getValue() / CPU.PAGE_SIZE)) {
					cycles++;
				}
			}
			return cycles;
		}
	};

	/**
	 * Branch Overflow Clear 0x50
	 */ 
	public static Instruction branchOverflowClear = new Instruction(AddressingMode.PROGRAM_COUNTER_RELATIVE, Size.BYTE) {
		{this.name = "Branch Overflow Clear ";
		 this.mnemonic = "BVC";}
		public int run(int[] args) {
			int cycles = 2;
			if (!CPU.status.isOverflow()) {
				CPU.loadDataRegister(addrMode, size.getRealSize(), args);
				int origPC = CPU.pc.getValue();
				CPU.pc.add(CPU.dataReg.getValue());
				
				cycles++;
				if (CPU.emulationMode && (origPC/CPU.PAGE_SIZE != CPU.pc.getValue() / CPU.PAGE_SIZE)) {
					cycles++;
				}
			}
			return cycles;
		}
	};

	/**
	 * Branch Overflow Set 0x70
	 */ 
	public static Instruction branchOverflowSet = new Instruction(AddressingMode.PROGRAM_COUNTER_RELATIVE, Size.BYTE) {
		{this.name = "Branch Overflow Set ";
		 this.mnemonic = "BVS";}
		public int run(int[] args) {
			int cycles = 2;
			if (CPU.status.isOverflow()) {
				CPU.loadDataRegister(addrMode, size.getRealSize(), args);
				int origPC = CPU.pc.getValue();
				CPU.pc.add(CPU.dataReg.getValue());
				
				cycles++;
				if (CPU.emulationMode && (origPC/CPU.PAGE_SIZE != CPU.pc.getValue() / CPU.PAGE_SIZE)) {
					cycles++;
				}
			}
			return cycles;
		}
	};

	/**
	 * Branch Always 0x80
	 */ 
	public static Instruction branchAlways = new Instruction(AddressingMode.PROGRAM_COUNTER_RELATIVE, Size.BYTE) {
		{this.name = "Branch Always ";
		 this.mnemonic = "BRA";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int cycles = 3;
			int origPC = CPU.pc.getValue();
			CPU.pc.add(CPU.dataReg.getValue());
			
			if (CPU.emulationMode && (origPC/CPU.PAGE_SIZE != CPU.pc.getValue() / CPU.PAGE_SIZE)) {
				cycles++;
			}
			return cycles;
		}
	};

	/**
	 * Branch Always (Long) 0x82
	 */ 
	public static Instruction branchAlwaysLong = new Instruction(AddressingMode.PROGRAM_COUNTER_RELATIVE_LONG, Size.SHORT) {
		{this.name = "Branch Always ";
		 this.mnemonic = "BRL";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int cycles = 4;
			CPU.pc.add(CPU.dataReg.getValue());
			return cycles;
		}
	};
	/**
	 * Branch if Carry Clear 0x90
	 */ 
	public static Instruction branchCarryClear = new Instruction(AddressingMode.PROGRAM_COUNTER_RELATIVE, Size.BYTE) {
		{this.name = "Branch if Carry Clear ";
		 this.mnemonic = "BCC";}
		public int run(int[] args) {
			int cycles = 2;
			if (!CPU.status.isCarry()) {
				CPU.loadDataRegister(addrMode, size.getRealSize(), args);
				int origPC = CPU.pc.getValue();
				CPU.pc.add(CPU.dataReg.getValue());
				
				cycles++;
				if (CPU.emulationMode && (origPC/CPU.PAGE_SIZE != CPU.pc.getValue() / CPU.PAGE_SIZE)) {
					cycles++;
				}
			}
			return cycles;
		}
	};

	/**
	 * Branch if Carry Set 0xB0
	 */ 
	public static Instruction branchCarrySet = new Instruction(AddressingMode.PROGRAM_COUNTER_RELATIVE, Size.BYTE) {
		{this.name = "Branch if Carry Set ";

		 this.mnemonic = "BCS";}
		public int run(int[] args) {
			int cycles = 2;
			if (CPU.status.isCarry()) {
				CPU.loadDataRegister(addrMode, size.getRealSize(), args);
				int origPC = CPU.pc.getValue();
				CPU.pc.add(CPU.dataReg.getValue());
				
				cycles++;
				if (CPU.emulationMode && (origPC/CPU.PAGE_SIZE != CPU.pc.getValue() / CPU.PAGE_SIZE)) {
					cycles++;
				}
			}
			return cycles;
		}
	};

	/**
	 * Branch if Not Equal 0xD0
	 */ 
	public static Instruction branchNotEqual = new Instruction(AddressingMode.PROGRAM_COUNTER_RELATIVE, Size.BYTE) {
		{this.name = "Branch if Not Equal ";
		 this.mnemonic = "BNE";}
		public int run(int[] args) {
			int cycles = 2;
			if (!CPU.status.isZero()) {
				CPU.loadDataRegister(addrMode, size.getRealSize(), args);
				int origPC = CPU.pc.getValue();
				CPU.pc.add(CPU.dataReg.getValue());
				
				cycles++;
				if (CPU.emulationMode && (origPC/CPU.PAGE_SIZE != CPU.pc.getValue() / CPU.PAGE_SIZE)) {
					cycles++;
				}
			}
			return cycles;
		}
	};

	/**
	 * Branch if Equal 0xF0
	 */ 
	public static Instruction branchEqual = new Instruction(AddressingMode.PROGRAM_COUNTER_RELATIVE, Size.BYTE) {
		{this.name = "Branch if Equal ";
		 this.mnemonic = "BEQ";}
		public int run(int[] args) {
			int cycles = 2;
			if (CPU.status.isZero()) {
				CPU.loadDataRegister(addrMode, size.getRealSize(), args);
				int origPC = CPU.pc.getValue();
				CPU.pc.add(CPU.dataReg.getValue());
				
				cycles++;
				if (CPU.emulationMode && (origPC/CPU.PAGE_SIZE != CPU.pc.getValue() / CPU.PAGE_SIZE)) {
					cycles++;
				}
			}
			return cycles;
		}
	};

}
