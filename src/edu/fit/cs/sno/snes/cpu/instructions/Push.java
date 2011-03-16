package edu.fit.cs.sno.snes.cpu.instructions;

import edu.fit.cs.sno.snes.common.Instruction;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.AddressingMode;
import edu.fit.cs.sno.snes.cpu.CPU;

public class Push {

	/**
	 * Push Effective Absolute Address
	 * 0xF4
	 */ 
	public static Instruction pushEffectiveAbsolute = new Instruction(AddressingMode.IMPLIED, 2, Size.SHORT) {
		{this.name = "Push Effective Absolute Address";
		 this.mnemonic = "PEA";}
		public int run(int[] args) {
			CPU.stackPush(Size.SHORT, (args[1] << 8) + args[0]);
			
			int cycles = 5;
			return cycles;
		}
	};
	
	/**
	 * Push Effective Indirect Address
	 * 0xD4
	 */ 
	public static Instruction pushEffectiveIndirect = new Instruction(AddressingMode.DIRECT_PAGE, Size.SHORT) {
		{this.name = "Push Effective Indirect Address";
		 this.mnemonic = "PEI";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			CPU.stackPush(Size.SHORT, CPU.dataReg.getValue());
			
			int cycles = 6;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Push Effective PC Relative Indirect Address
	 * 0x62
	 */ 
	public static Instruction pushEffectivePC = new Instruction(AddressingMode.IMPLIED, 2, Size.SHORT) {
		{this.name = "Push Effective PC Relative Indirect Address";
		 this.mnemonic = "PER";}
		public int run(int[] args) {
			CPU.stackPush(Size.SHORT, CPU.pc.getValue() + ((args[1] << 8) + args[0]));
			
			int cycles = 6;
			return cycles;
		}
	};
	
	/**
	 * Push Accumulator
	 * 0x48
	 */ 
	public static Instruction pushAccumulator = new Instruction() {
		{this.name = "Push Accumulator";
		 this.mnemonic = "PHA";}
		public int run(int[] args) {
			CPU.stackPush(Size.MEMORY_A, CPU.a.getValue());
			
			int cycles = 3;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Push Data Bank Register
	 * 0x8B
	 */ 
	public static Instruction pushDataBank = new Instruction() {
		{this.name = "Push Data Bank Register";
		 this.mnemonic = "PHB";}
		public int run(int[] args) {
			CPU.stackPush(Size.BYTE, CPU.dbr.getValue());
			
			int cycles = 3;
			return cycles;
		}
	};
	
	/**
	 * Push Direct Page Register
	 * 0x0B
	 */ 
	public static Instruction pushDirectPage = new Instruction() {
		{this.name = "Push Direct Page Register";
		 this.mnemonic = "PHD";}
		public int run(int[] args) {
			CPU.stackPush(Size.SHORT, CPU.dp.getValue());
			
			int cycles = 4;
			return cycles;
		}
	};
	
	/**
	 * Push Program Bank Register
	 * 0x4B
	 */ 
	public static Instruction pushProgramBank = new Instruction() {
		{this.name = "Push Program Bank Register";
		 this.mnemonic = "PHK";}
		public int run(int[] args) {
			CPU.stackPush(Size.BYTE, CPU.pbr.getValue());
			
			int cycles = 3;
			return cycles;
		}
	};
	
	/**
	 * Push Processor Status Register
	 * 0x08
	 */ 
	public static Instruction pushStatus = new Instruction() {
		{this.name = "Push Processor Status Register";
		 this.mnemonic = "PHP";}
		public int run(int[] args) {
			CPU.stackPush(Size.BYTE, CPU.status.getValue());
			
			int cycles = 3;
			return cycles;
		}
	};
	
	/**
	 * Push Index Register X
	 * 0xDA
	 */ 
	public static Instruction pushX = new Instruction() {
		{this.name = "Push Index Register X";
		 this.mnemonic = "PHX";}
		public int run(int[] args) {
			CPU.stackPush(Size.INDEX, CPU.x.getValue());
			
			int cycles = 3;
			if (!CPU.status.isIndexRegister())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Push Index Register Y
	 * 0x5A
	 */ 
	public static Instruction pushY = new Instruction() {
		{this.name = "Push Index Register Y";
		 this.mnemonic = "PHY";}
		public int run(int[] args) {
			CPU.stackPush(Size.INDEX, CPU.y.getValue());
			
			int cycles = 3;
			if (!CPU.status.isIndexRegister())
				cycles++;
			return cycles;
		}
	};
}
