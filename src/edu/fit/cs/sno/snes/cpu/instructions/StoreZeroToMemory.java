package edu.fit.cs.sno.snes.cpu.instructions;

import edu.fit.cs.sno.snes.common.Instruction;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.AddressingMode;
import edu.fit.cs.sno.snes.cpu.CPU;

public class StoreZeroToMemory {
	public static final String mnemonic = "STZ";
	/**
     * Store Zero to Memory (Direct Page)
     * 0x64
     */ 
	public static Instruction storeZeroDirectPage = new Instruction(AddressingMode.DIRECT_PAGE, Size.MEMORY_A) {
		{this.name = "Store Zero to Memory (Direct Page)";}
        public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
            CPU.dataReg.setValue(0);
			CPU.saveDataReg();
			
			int cycles = 3;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
        }
    };
    
    /**
     * Store Zero to Memory (DP Indexed X)
     * 0x74
     */ 
	public static Instruction storeZeroDPIndexedX = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_X, Size.MEMORY_A) {
		{this.name = "Store Zero to Memory (DP Indexed X)";}
        public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
            CPU.dataReg.setValue(0);
			CPU.saveDataReg();
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
        }
    };
    
    /**
     * Store Zero to Memory (Absolute)
     * 0x9C
     */ 
	public static Instruction storeZeroAbsolute = new Instruction(AddressingMode.ABSOLUTE, Size.MEMORY_A) {
		{this.name = "Store Zero to Memory (Absolute)";}
        public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
            CPU.dataReg.setValue(0);
			CPU.saveDataReg();
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
        }
    };
    
    /**
     * Store Zero to Memory (Absolute Indexed X)
     * 0x9E
     */ 
	public static Instruction storeZeroAbsoluteIndexedX = new Instruction(AddressingMode.ABSOLUTE_INDEXED_X, Size.MEMORY_A) {
		{this.name = "Store Zero to Memory (Absolute Indexed X)";}
        public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
            CPU.dataReg.setValue(0);
			CPU.saveDataReg();
			
			int cycles = 5;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
        }
    };
    
}
