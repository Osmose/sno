package edu.fit.cs.sno.snes.cpu.instructions;

import edu.fit.cs.sno.snes.common.Instruction;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.AddressingMode;
import edu.fit.cs.sno.snes.cpu.CPU;

public class Jump {
	public static final String mnemonic = "JMP";
	 /**
     * Jump Absolute
     * 0x4C
     */ 
	public static Instruction jumpAbsolute = new Instruction(AddressingMode.ABSOLUTE) {
		{this.name = "Jump Absolute";}
        public int run(int[] args) {
        	CPU.loadDataRegister(addrMode, size.getRealSize(), args);
            CPU.pc.setValue(CPU.dataAddr);
            int cycles = 3;
            return cycles;
        }
    };
    
    /**
     * Jump Absolute Indirect
     * 0x6C
     */ 
	public static Instruction jumpAbsoluteIndirect = new Instruction(AddressingMode.ABSOLUTE_INDIRECT) {
		{this.name = "Jump Absolute Indirect";}
        public int run(int[] args) {
        	CPU.loadDataRegister(addrMode, size.getRealSize(), args);
            CPU.pc.setValue(CPU.dataAddr);
            int cycles = 5;
            return cycles;
        }
    };
    
    /**
     * Jump Absolute Indexed Indirect
     * 0x7C
     */ 
	public static Instruction jumpAbsoluteIndexedIndirect = new Instruction(AddressingMode.ABSOLUTE_INDEXED_INDIRECT) {
		{this.name = "Jump Absolute Indexed Indirect";}
        public int run(int[] args) {
        	CPU.loadDataRegister(addrMode, size.getRealSize(), args);
            CPU.pc.setValue(CPU.dataAddr);
            int cycles = 6;
            return cycles;
        }
    };
    
    /**
     * Jump Absolute Long
     * 0x5C
     */ 
	public static Instruction jumpAbsoluteLong = new Instruction(AddressingMode.ABSOLUTE_LONG) {
		{this.name = "Jump Absolute Long";}
        public int run(int[] args) {
        	CPU.loadDataRegister(addrMode, size.getRealSize(), args);
            CPU.pc.setValue(CPU.dataAddr);
            CPU.pbr.setValue(CPU.dataBank);
            
            int cycles = 4;
            return cycles;
        }
    };
    
    /**
     * Jump Absolute Indirect Long
     * 0xDC
     */ 
	public static Instruction jumpAbsoluteIndirectLong = new Instruction(AddressingMode.ABSOLUTE_INDIRECT_LONG) {
		{this.name = "Jump Absolute Indirect Long";}
        public int run(int[] args) {
        	CPU.loadDataRegister(addrMode, size.getRealSize(), args);
            CPU.pc.setValue(CPU.dataAddr);
            CPU.pbr.setValue(CPU.dataBank);
            
            int cycles = 6;
            return cycles;
        }
    };
    
    /**
     * Jump to Subroutine Absolute
     * 0x20
     */ 
	public static Instruction jumpSubAbsolute = new Instruction(AddressingMode.ABSOLUTE) {
		{this.name = "Jump to Subroutine Absolute";
		 this.mnemonic = "JSR";}
        public int run(int[] args) {
        	CPU.loadDataRegister(addrMode, size.getRealSize(), args);
        	// Spec says to put the address of the last byte in this instruction
        	// PC points to the next instruction, so subtract one
        	CPU.stackPush(Size.SHORT, CPU.pc.getValue() - 1);
        	
            CPU.pc.setValue(CPU.dataAddr);

            int cycles = 6;
            return cycles;
        }
    };
    
    /**
     * Jump to Subroutine Absolute Indexed Indirect
     * 0xFC
     */ 
	public static Instruction jumpSubAbsoluteIndexedIndirect = new Instruction(AddressingMode.ABSOLUTE_INDEXED_INDIRECT) {
		{this.name = "Jump to Subroutine Absolute Indexed Indirect";
		 this.mnemonic = "JSR";}
        public int run(int[] args) {
        	CPU.loadDataRegister(addrMode, size.getRealSize(), args);
        	// Spec says to put the address of the last byte in this instruction
        	// PC points to the next instruction, so subtract one
        	CPU.stackPush(Size.SHORT, CPU.pc.getValue() - 1);
        	
            CPU.pc.setValue(CPU.dataAddr);
            CPU.pbr.setValue(CPU.dataBank);

            int cycles = 8;
            return cycles;
        }
    };
    
    /**
     * Jump to Subroutine Long
     * 0x22
     */ 
	public static Instruction jumpSubLong = new Instruction(AddressingMode.ABSOLUTE_LONG) {
		{this.name = "Jump to Subroutine Long";
		 this.mnemonic = "JSL";}
        public int run(int[] args) {
        	CPU.loadDataRegister(addrMode, size.getRealSize(), args);
        	CPU.stackPush(Size.BYTE, CPU.pbr.getValue());
        	
        	// Spec says to put the address of the last byte in this instruction
        	// PC points to the next instruction, so subtract one
        	CPU.stackPush(Size.SHORT, CPU.pc.getValue() - 1);
        	
            CPU.pc.setValue(CPU.dataAddr);
            CPU.pbr.setValue(CPU.dataBank);
            
            int cycles = 8;
            return cycles;
        }
    };
}
