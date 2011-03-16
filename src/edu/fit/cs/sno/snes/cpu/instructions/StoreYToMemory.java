
package edu.fit.cs.sno.snes.cpu.instructions;

import edu.fit.cs.sno.snes.common.Instruction;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.AddressingMode;
import edu.fit.cs.sno.snes.cpu.CPU;

public class StoreYToMemory {
	public static final String mnemonic = "STY";

    /**
     * Store Y to Memory (Direct Page)
     * 0x84
     */ 
	public static Instruction storeYDirectPage = new Instruction(AddressingMode.DIRECT_PAGE, Size.INDEX) {
		{this.name = "Store Y to Memory (Direct Page)";}
        public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
            CPU.dataReg.setValue(CPU.y.getValue());
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
     * Store Y to Memory (Absolute)
     * 0x8C
     */ 
	public static Instruction storeYAbsolute = new Instruction(AddressingMode.ABSOLUTE, Size.INDEX) {
		{this.name = "Store Y to Memory (Absolute)";}
        public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
            CPU.dataReg.setValue(CPU.y.getValue());
			CPU.saveDataReg();
			
			int cycles = 4;
			if (!CPU.status.isIndexRegister())
				cycles++;
			return cycles;
        }
    };
    
    /**
     * Store Y to Memory (DP Indexed Y)
     * 0x94
     */ 
	public static Instruction storeYDPIndexedX = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_X, Size.INDEX) {
		{this.name = "Store Y to Memory (DP Indexed Y)";}
        public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
            CPU.dataReg.setValue(CPU.y.getValue());
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