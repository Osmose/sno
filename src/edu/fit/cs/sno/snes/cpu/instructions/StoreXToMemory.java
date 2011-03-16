
package edu.fit.cs.sno.snes.cpu.instructions;

import edu.fit.cs.sno.snes.common.Instruction;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.AddressingMode;
import edu.fit.cs.sno.snes.cpu.CPU;

public class StoreXToMemory {
	public static final String mnemonic = "STX";

    /**
     * Store X to Memory (Direct Page)
     * 0x86
     */ 
	public static Instruction storeXDirectPage = new Instruction(AddressingMode.DIRECT_PAGE, Size.INDEX) {
		{this.name = "Store X to Memory (Direct Page)";}
        public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
            CPU.dataReg.setValue(CPU.x.getValue());
			CPU.saveDataReg();
			
			int cycles = 3;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			if (!CPU.status.isIndexRegister())
				cycles++;
			return cycles;
        }
    };
    
    /**
     * Store X to Memory (Absolute)
     * 0x8E
     */ 
	public static Instruction storeXAbsolute = new Instruction(AddressingMode.ABSOLUTE, Size.INDEX) {
		{this.name = "Store X to Memory (Absolute)";}
        public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
            CPU.dataReg.setValue(CPU.x.getValue());
			CPU.saveDataReg();
			
			int cycles = 4;
			if (!CPU.status.isIndexRegister())
				cycles++;
			return cycles;
        }
    };
    
    /**
     * Store X to Memory (DP Indexed Y)
     * 0x96
     */ 
	public static Instruction storeXDPIndexedY = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_Y, Size.INDEX) {
		{this.name = "Store X to Memory (DP Indexed Y)";}
        public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
            CPU.dataReg.setValue(CPU.x.getValue());
			CPU.saveDataReg();
			
			int cycles = 4;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			if (!CPU.status.isIndexRegister())
				cycles++;
			return cycles;
        }
    };
    
}