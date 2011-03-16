package edu.fit.cs.sno.snes.cpu.instructions;

import edu.fit.cs.sno.snes.common.Instruction;
import edu.fit.cs.sno.snes.cpu.AddressingMode;
import edu.fit.cs.sno.snes.cpu.CPU;

public class FlagOps {
    
    /* Clear Carry Flag
     * 0x18
     */ 
	public static Instruction clearCarryFlag = new Instruction() {
		{this.name = "Clear Carry Flag";
		 this.mnemonic = "CLC";}
        public int run(int[] args) {
            CPU.status.setCarry(false);
            int cycles = 2;
            return cycles;
        }
    };
    
    /* Set Carry Flag
     * 0x38
     */ 
	public static Instruction setCarryFlag = new Instruction() {
		{this.name = "Set Carry Flag";
		 this.mnemonic = "SEC";}
        public int run(int[] args) {
            CPU.status.setCarry(true);
            int cycles = 2;
            return cycles;
        }
    };    
    
    /* Clear Decimal Mode Flag
     * 0xD8
     */ 
	public static Instruction clearDecimalModeFlag = new Instruction() {
		{this.name = "Clear Decimal Mode Flag";
		 this.mnemonic = "CLD";}
        public int run(int[] args) {
            CPU.status.setDecimalMode(false);
            int cycles = 2;
            return cycles;
        }
    };
    
    /* Set Decimal Mode Flag
     * 0xF8
     */ 
	public static Instruction setDecimalModeFlag = new Instruction() {
		{this.name = "Set Decimal Mode Flag";
		 this.mnemonic = "SED";}
        public int run(int[] args) {
            CPU.status.setDecimalMode(true);
            int cycles = 2;
            return cycles;
        }
    };
    
    /* Clear Interrupt Disable Flag
     * 0x58
     */ 
	public static Instruction clearInterruptDisableFlag = new Instruction() {
		{this.name = "Clear Interrupt Disable Flag";
		 this.mnemonic = "CLI";}
       public int run(int[] args) {
            CPU.status.setIrqDisable(false);
            int cycles = 2;
            return cycles;
        }
    };
    
    /* Set Interrupt Disable Flag
     * 0x78
     */ 
	public static Instruction setInterruptDisableFlag = new Instruction() {
		{this.name = "Set Interrupt Disable Flag";
		 this.mnemonic = "SEI";}
       public int run(int[] args) {
            CPU.status.setIrqDisable(true);
            int cycles = 2;
            return cycles;
        }
    };
    
    /* Set Processor Status Bits
     * 0xE2
     */ 
	public static Instruction setProcessorStatusBits = new Instruction(AddressingMode.IMPLIED, 1) {
		{this.name = "Set Processor Status Bits";
		 this.mnemonic = "SEP";}

        public int run(int[] args) {
            CPU.status.setCarry((args[0] & 0x01)  == 0x01?true:CPU.status.isCarry());
            CPU.status.setZero((args[0] & 0x02)  == 0x02?true:CPU.status.isZero());
            CPU.status.setIrqDisable((args[0] & 0x04) == 0x04?true:CPU.status.isIrqDisable());
            CPU.status.setDecimalMode((args[0] & 0x08)  == 0x08?true:CPU.status.isDecimalMode());
            CPU.status.setIndexRegister((args[0] & 0x10)  == 0x10?true:CPU.status.isIndexRegister());
            CPU.status.setMemoryAccess((args[0] & 0x20)  == 0x20?true:CPU.status.isMemoryAccess());
            CPU.status.setOverflow((args[0] & 0x40)  == 0x40?true:CPU.status.isOverflow());
            CPU.status.setNegative((args[0] & 0x80) == 0x80?true:CPU.status.isNegative());
            int cycles = 3;
            return cycles;
        }
    };
    
    /* Clear Processor Status Bits
     * 0xC2
     */ 
	public static Instruction resetProcessorStatusBits = new Instruction(AddressingMode.IMPLIED, 1) {
		{this.name = "Clear Processor Status Bits";
		 this.mnemonic = "REP";}

        public int run(int[] args) {

        	CPU.status.setCarry((args[0] & 0x01)  == 0x01?false:CPU.status.isCarry());
            CPU.status.setZero((args[0] & 0x02)  == 0x02?false:CPU.status.isZero());
            CPU.status.setIrqDisable((args[0] & 0x04) == 0x04?false:CPU.status.isIrqDisable());
            CPU.status.setDecimalMode((args[0] & 0x08)  == 0x08?false:CPU.status.isDecimalMode());
            CPU.status.setIndexRegister((args[0] & 0x10)  == 0x10?false:CPU.status.isIndexRegister());
            CPU.status.setMemoryAccess((args[0] & 0x20)  == 0x20?false:CPU.status.isMemoryAccess());
            CPU.status.setOverflow((args[0] & 0x40)  == 0x40?false:CPU.status.isOverflow());
            CPU.status.setNegative((args[0] & 0x80) == 0x80?false:CPU.status.isNegative());
            int cycles = 3;
            return cycles;
        }
    };
    
    /* Clear Overflow Flag
     * 0xB8
     */ 
	public static Instruction clearOverflowFlag = new Instruction() {
		{this.name = "Clear Overflow Flag";
		 this.mnemonic = "CLV";}
        public int run(int[] args) {
            CPU.status.setOverflow(false);
            int cycles = 2;
            return cycles;
        }
    };
    
    /* Exchange Carry and Emulation Flags
     * 0xFB
     * This might also have some interaction with Index and Accumulator size
     */
    public static Instruction exchangeCarryEmulationFlag = new Instruction() {
    	{this.name = "Exchange Carry and Emulation Flags";
		 this.mnemonic = "XCE";
    	}
        public int run(int[] args) {
        	if(CPU.status.isCarry() ^ CPU.emulationMode) {
        		if( CPU.status.isCarry()) {					// Change to Emulation mode
        			CPU.status.setCarry(false);
        			CPU.emulationMode = true;
        			CPU.status.setIndexRegister(false);
        			CPU.status.setMemoryAccess(false);
        		} else
        		{
        			CPU.status.setCarry(true);				// Change to Native mode
        			CPU.emulationMode = false;
        			CPU.status.setIndexRegister(true);
        			CPU.status.setMemoryAccess(true);
        		}
        	}
        	int cycles = 2;
            return cycles;
        }
    };

}