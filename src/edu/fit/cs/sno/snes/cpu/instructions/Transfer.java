package edu.fit.cs.sno.snes.cpu.instructions;

import edu.fit.cs.sno.snes.common.Instruction;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.CPU;

public class Transfer {

	/**
	 * Transfer Accumulator to X
	 * 0xAA
	 */ 
	public static Instruction transferAtoX = new Instruction() {
		{this.name = "Transfer Accumulator to X";
		 this.mnemonic = "TAX";}
		public int run(int[] args) {
			CPU.x.setValue(CPU.a.getValue(CPU.x.getSize()));
			
			CPU.status.setNegative(CPU.x.isNegative());
			CPU.status.setZero(CPU.x.getValue() == 0);
			
			int cycles = 2;
			return cycles;
		}
	};
	
	/**
	 * Transfer Accumulator to Y
	 * 0xA8
	 */ 
	public static Instruction transferAtoY = new Instruction() {
		{this.name = "Transfer Accumulator to Y";
		 this.mnemonic = "TAY";}
		public int run(int[] args) {
			CPU.y.setValue(CPU.a.getValue(CPU.y.getSize()));
			
			CPU.status.setNegative(CPU.y.isNegative());
			CPU.status.setZero(CPU.y.getValue() == 0);
			
			int cycles = 2;
			return cycles;
		}
	};
	
	/**
	 * Transfer Accumulator to Direct Page
	 * 0x5B
	 */ 
	public static Instruction transferAtoDP = new Instruction() {
		{this.name = "Transfer Accumulator to Direct Page";
		 this.mnemonic = "TCD";}
		public int run(int[] args) {
			CPU.dp.setValue(CPU.a.getValue(Size.SHORT));
			
			CPU.status.setNegative(CPU.dp.isNegative());
			CPU.status.setZero(CPU.dp.getValue() == 0);
			
			int cycles = 2;
			return cycles;
		}
	};
	
	/**
	 * Transfer Accumulator to Stack Pointer
	 * 0x1B
	 */ 
	public static Instruction transferAtoSP = new Instruction() {
		{this.name = "Transfer Accumulator to Stack Pointer";
		 this.mnemonic = "TCS";}
		public int run(int[] args) {
			CPU.sp.setValue(CPU.a.getValue(Size.SHORT));
			
			int cycles = 2;
			return cycles;
		}
	};
	
	/**
	 * Transfer Direct Page to Accumulator
	 * 0x7B
	 */ 
	public static Instruction transferDPtoA = new Instruction() {
		{this.name = "Transfer Direct Page to Accumulator";
		 this.mnemonic = "TDC";}
		public int run(int[] args) {
			CPU.a.setRealValue(CPU.dp.getValue());
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 2;
			return cycles;
		}
	};
	
	/**
	 * Transfer Stack Pointer to Accumulator
	 * 0x3B
	 */ 
	public static Instruction transferSPtoA = new Instruction() {
		{this.name = "Transfer Stack Pointer to Accumulator";
		 this.mnemonic = "TSC";}
		public int run(int[] args) {
			CPU.a.setRealValue(CPU.sp.getValue());
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 2;
			return cycles;
		}
	};
	
	/**
	 * Transfer Stack Pointer to X
	 * 0xBA
	 */ 
	public static Instruction transferSPtoX = new Instruction() {
		{this.name = "Transfer Stack Pointer to X";
		 this.mnemonic = "TSX";}
		public int run(int[] args) {
			CPU.x.setValue(CPU.sp.getValue());
			
			CPU.status.setNegative(CPU.x.isNegative());
			CPU.status.setZero(CPU.x.getValue() == 0);
			
			int cycles = 2;
			return cycles;
		}
	};
	
	/**
	 * Transfer X to Accumulator
	 * 0x8A
	 */ 
	public static Instruction transferXtoA = new Instruction() {
		{this.name = "Transfer X to Accumulator";
		 this.mnemonic = "TXA";}
		public int run(int[] args) {
			if (CPU.status.isIndexRegister()) {
				CPU.a.setValue(CPU.x.getValue());
			} else {
				if (CPU.status.isMemoryAccess()) {
					CPU.a.setValue(CPU.x.getValue());
				} else {
					CPU.a.setRealValue(CPU.x.getValue());
				}
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 2;
			return cycles;
		}
	};
	
	/**
	 * Transfer X to Stack Pointer
	 * 0x9A
	 */ 
	public static Instruction transferXtoSP = new Instruction() {
		{this.name = "Transfer X to Stack Pointer";
		 this.mnemonic = "TXS";}
		public int run(int[] args) {
			CPU.sp.setValue(CPU.x.getValue());
			
			int cycles = 2;
			return cycles;
		}
	};
	
	/**
	 * Transfer X to Y
	 * 0x9B
	 */ 
	public static Instruction transferXtoY = new Instruction() {
		{this.name = "Transfer X to Y";
		 this.mnemonic = "TXY";}
		public int run(int[] args) {
			CPU.y.setValue(CPU.x.getValue());
			
			CPU.status.setNegative(CPU.y.isNegative());
			CPU.status.setZero(CPU.y.getValue() == 0);
			
			int cycles = 2;
			return cycles;
		}
	};
	
	/**
	 * Transfer Y to Accumulator
	 * 0x98
	 */ 
	public static Instruction transferYtoA = new Instruction() {
		{this.name = "Transfer Y to Accumulator";
		 this.mnemonic = "TYA";}
		public int run(int[] args) {
			if (CPU.status.isIndexRegister()) {
				CPU.a.setValue(CPU.y.getValue());
			} else {
				CPU.a.setRealValue(CPU.y.getValue());
			}
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			
			int cycles = 2;
			return cycles;
		}
	};
	
	/**
	 * Transfer Y to X
	 * 0xBB
	 */ 
	public static Instruction transferYtoX = new Instruction() {
		{this.name = "Transfer Y to X";
		 this.mnemonic = "TYX";}
		public int run(int[] args) {
			CPU.x.setValue(CPU.y.getValue());
			
			CPU.status.setNegative(CPU.x.isNegative());
			CPU.status.setZero(CPU.x.getValue() == 0);
			
			int cycles = 2;
			return cycles;
		}
	};
}
