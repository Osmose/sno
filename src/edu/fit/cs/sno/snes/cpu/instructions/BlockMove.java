package edu.fit.cs.sno.snes.cpu.instructions;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.common.Instruction;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.AddressingMode;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.cpu.Timing;
import edu.fit.cs.sno.util.Log;

public class BlockMove {

	/**
	 * Block Move Negative 0x54
	 */ 
	public static Instruction blockMoveNegative = new Instruction(AddressingMode.BLOCK_MOVE, Size.BYTE) {

		{this.name = "Block Move Negative";
		 this.mnemonic = "MVN";}
		public int run(int[] args) {
			CPU.dbr.setRealValue(args[0]);
			do {
				Core.mem.set(Size.BYTE, args[0], CPU.y.getValue(), Core.mem.read(Size.BYTE, args[1], CPU.x.getValue()));
				CPU.a.subtract(1);
				CPU.x.add(1);
				CPU.y.add(1);
				
				Timing.cycle(42);
				if (CPU.checkInterrupts()) break;
			} while (CPU.a.getValue() != (CPU.a.getSize()==Size.SHORT?0xFFFF:0xFF));
			return 0;
		}
	};
	
	/**
	 * Block Move Positive 0x44
	 */ 
	public static Instruction blockMovePositive = new Instruction(AddressingMode.BLOCK_MOVE, Size.BYTE) {

		{this.name = "Block Move Positive";
		 this.mnemonic = "MVP";}
		public int run(int[] args) {
			CPU.dbr.setRealValue(args[0]);
			do {
				Core.mem.set(Size.BYTE, args[0], CPU.y.getValue(), Core.mem.read(Size.BYTE, args[1], CPU.x.getValue()));
				CPU.a.subtract(1);
				CPU.x.subtract(1);
				CPU.y.subtract(1);
				
				Timing.cycle(42);
				if (CPU.checkInterrupts()) break;
			} while (CPU.a.getValue() != (CPU.a.getSize()==Size.SHORT?0xFFFF:0xFF));
			return 0;
		}
	};


}
