package edu.fit.cs.sno.snes.cpu.instructions;

import edu.fit.cs.sno.snes.common.Instruction;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.CPU;

public class Return {

	/**
	 * Return from Interrupt
	 * 0x40
	 */ 
	public static Instruction returnFromInterrupt = new Instruction() {
		{this.name = "Return from Interrupt";
		 this.mnemonic = "RTI";}
		public int run(int[] args) {
			CPU.status.setValue(CPU.stackPull(Size.BYTE));
			CPU.pc.setValue(CPU.stackPull(Size.SHORT));
			if (!CPU.emulationMode)
				CPU.pbr.setValue(CPU.stackPull(Size.BYTE));
			
			int cycles = 6;
			if (!CPU.emulationMode)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Return from Subroutine Long
	 * 0x6B
	 */ 
	public static Instruction returnFromSubroutineLong = new Instruction() {
		{this.name = "Return from Subroutine Long";
		 this.mnemonic = "RTL";}
		public int run(int[] args) {
			CPU.pc.setValue(CPU.stackPull(Size.SHORT) + 1);
			CPU.pbr.setValue(CPU.stackPull(Size.BYTE));
			
			int cycles = 6;
			return cycles;
		}
	};
	
	/**
	 * Return from Subroutine
	 * 0x60
	 */ 
	public static Instruction returnFromSubroutine = new Instruction() {
		{this.name = "Return from Subroutine";
		 this.mnemonic = "RTS";}
		public int run(int[] args) {
			CPU.pc.setValue(CPU.stackPull(Size.SHORT) + 1);
			
			int cycles = 6;
			return cycles;
		}
	};
}
