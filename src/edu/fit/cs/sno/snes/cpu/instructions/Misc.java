package edu.fit.cs.sno.snes.cpu.instructions;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.common.Instruction;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.AddressingMode;
import edu.fit.cs.sno.snes.cpu.CPU;

public class Misc {
	public static String mnemonic = "hello world";
	/**
	 * Software Break
	 * 0x00
	 */ 
	public static Instruction softwareBreak = new Instruction() {
		{this.name = "Software Break";
		 this.mnemonic = "BRK";}
		public int run(int[] args) {
			CPU.stackPush(Size.BYTE, CPU.pbr.getValue());
			CPU.pc.add(2);
			CPU.stackPush(Size.SHORT, CPU.pc.getValue());
			CPU.stackPush(Size.BYTE, CPU.status.getValue());
			CPU.status.setIrqDisable(true);
			CPU.pbr.setValue(0);
			CPU.pc.setValue(Core.mem.get(Size.SHORT, 0, 0xFFE6));
			
			int cycles = 9;
			if (!CPU.emulationMode)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * NOP - No operation
	 * 0xEA
	 */
	public static Instruction nop = new Instruction() {
		{this.mnemonic = "NOP";}
		@Override
		public int run(int[] args) {
			// Does nothing
			int cycles = 2;
			return cycles;
		}
	};
	
	/**
	 * WDM - Reserved for Future Expansion
	 * 0x42
	 */
	public static Instruction wdm = new Instruction() {
		{this.mnemonic = "WDM";}
		@Override
		public int run(int[] args) {
			return 0;
		}
	};
	
	/**
	 * Exchange the B and A accumulators
	 * 0xEB
	 */ 
	public static Instruction exchangeBA = new Instruction() {
		{this.name = "Exchange the B and A accumulators";
		 this.mnemonic = "XBA";}
		public int run(int[] args) {
			int b = (CPU.a.getRealValue() & 0xFF00) >> 8;
			int a = (CPU.a.getRealValue() & 0x00FF);
			CPU.a.setRealValue((a << 8) + b);
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue()==0);
			int cycles = 3;
			return cycles;
		}
	};
	
	/**
	 * Co-Processor Enable
	 * 0x02
	 */
	public static Instruction coprocessorEnable = new Instruction(AddressingMode.IMPLIED,1) {
		{this.name = "Enable Co-Processor";
		 this.mnemonic = "COP";}
		public int run(int[] args) {
			//int signatureByte = args[0];
			int cycles;
			if (CPU.emulationMode) {
				CPU.pc.add(2);
				CPU.stackPush(CPU.pc.getSize(), CPU.pc.getValue());
				CPU.stackPush(Size.BYTE,CPU.status.getValue());
				CPU.status.setIrqDisable(true);
				CPU.pc.setValue(Core.mem.read(Size.SHORT, 0x00, 0xFFF4));
				CPU.status.setDecimalMode(false);
				cycles = 7;
			} else {
				CPU.stackPush(CPU.pbr.getSize(), CPU.pbr.getValue());
				CPU.pc.add(2);
				CPU.stackPush(CPU.pc.getSize(), CPU.pc.getValue());
				CPU.stackPush(Size.BYTE,CPU.status.getValue());
				CPU.status.setIrqDisable(true);
				CPU.pbr.setValue(0x00);
				CPU.pc.setValue(Core.mem.read(Size.SHORT, 0x00, 0xFFE4));
				CPU.status.setDecimalMode(false);
				cycles = 8;
			}
			// Code to start a co-processor
			return cycles;
		}
	};
	
	/**
	 * Stop Processor
	 * 0xDB
	 */
	public static Instruction stopProcessor = new Instruction(AddressingMode.IMPLIED) {
			{this.name = "Stop Processor";
			 this.mnemonic = "STP";}
			public int run(int[] args) {
				CPU.emulationMode = true;
				CPU.dp.setValue(0);
				//stack high set to 1?
				CPU.status.setIndexRegister(true);
				Core.running = false;
				return 3;
			}
	};
}
