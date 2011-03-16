package edu.fit.cs.sno.snes.cpu.instructions;

import edu.fit.cs.sno.snes.common.Instruction;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.CPU;

public class Pull {

	/**
	 * Pull Accumulator
	 * 0x68
	 */ 
	public static Instruction pullAccumulator = new Instruction() {
		{this.name = "Pull Accumulator";
		 this.mnemonic = "PLA";}
		public int run(int[] args) {
			CPU.a.setValue(CPU.stackPull(Size.MEMORY_A.getRealSize()));
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Pull Data Bank Register
	 * 0xAB
	 */ 
	public static Instruction pullDataBank = new Instruction() {
		{this.name = "Pull Data Bank Register";
		 this.mnemonic = "PLB";}
		public int run(int[] args) {
			CPU.dbr.setValue(CPU.stackPull(Size.BYTE));
			
			CPU.status.setNegative(CPU.dbr.isNegative());
			CPU.status.setZero(CPU.dbr.getValue() == 0);
			
			int cycles = 4;
			return cycles;
		}
	};
	
	/**
	 * Pull Direct Page Register
	 * 0x2B
	 */ 
	public static Instruction pullDirectPage = new Instruction() {
		{this.name = "Pull Direct Page Register";
		 this.mnemonic = "PLD";}
		public int run(int[] args) {
			CPU.dp.setValue(CPU.stackPull(Size.SHORT));
			
			CPU.status.setNegative(CPU.dp.isNegative());
			CPU.status.setZero(CPU.dp.getValue() == 0);
			
			int cycles = 5;
			return cycles;
		}
	};
	
	/**
	 * Pull Processor Status Register
	 * 0x28
	 */ 
	public static Instruction pullStatus = new Instruction() {
		{this.name = "Pull Processor Status Register";
		 this.mnemonic = "PLP";}
		public int run(int[] args) {
			CPU.status.setValue(CPU.stackPull(Size.BYTE));
			
			int cycles = 4;
			return cycles;
		}
	};
	
	/**
	 * Pull Index Register X
	 * 0xFA
	 */ 
	public static Instruction pullX = new Instruction() {
		{this.name = "Pull Index Register X";
		 this.mnemonic = "PLX";}
		public int run(int[] args) {
			CPU.x.setValue(CPU.stackPull(Size.INDEX.getRealSize()));
			
			CPU.status.setNegative(CPU.x.isNegative());
			CPU.status.setZero(CPU.x.getValue() == 0);
			
			int cycles = 4;
			if (!CPU.status.isIndexRegister())
				cycles+=2;
			return cycles;
		}
	};
	
	/**
	 * Pull Index Register Y
	 * 0x7A
	 */ 
	public static Instruction pullY = new Instruction() {
		{this.name = "Pull Index Register Y";
		 this.mnemonic = "PLY";}
		public int run(int[] args) {
			CPU.y.setValue(CPU.stackPull(Size.INDEX.getRealSize()));
			
			CPU.status.setNegative(CPU.y.isNegative());
			CPU.status.setZero(CPU.y.getValue() == 0);
			
			int cycles = 4;
			if (!CPU.status.isIndexRegister())
				cycles+=2;
			return cycles;
		}
	};
}
