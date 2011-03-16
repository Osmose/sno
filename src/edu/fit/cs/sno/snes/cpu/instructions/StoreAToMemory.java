package edu.fit.cs.sno.snes.cpu.instructions;

import edu.fit.cs.sno.snes.common.Instruction;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.AddressingMode;
import edu.fit.cs.sno.snes.cpu.CPU;

public class StoreAToMemory {
	public static final String mnemonic = "STA";
	/**
	 * Save Accumulator To Memory Direct Page Indexed Indirect X
	 * 0x81
	 */ 
	public static Instruction saveADPIndexedIndirectX = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_INDIRECT_X, Size.MEMORY_A) {
		{this.name = "Save Accumulator To Memory Direct Page Indexed Indirect X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
            CPU.dataReg.setValue(CPU.a.getValue());
			CPU.saveDataReg();
			
			int cycles = 6;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Save Accumulator to Memory Stack Relative
	 * 0x83
	 */ 
	public static Instruction saveAStackRelative = new Instruction(AddressingMode.STACK_RELATIVE, Size.MEMORY_A) {
		{this.name = "Save Accumulator to Memory Stack Relative";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
            CPU.dataReg.setValue(CPU.a.getValue());
			CPU.saveDataReg();
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Save Accumulator to Memory Direct Page
	 * 0x85
	 */ 
	public static Instruction saveADirectPage = new Instruction(AddressingMode.DIRECT_PAGE, Size.MEMORY_A) {
		{this.name = "Save Accumulator to Memory Direct Page";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
            CPU.dataReg.setValue(CPU.a.getValue());
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
	 * Save Accumulator to Memory Direct Page Indirect Long
	 * 0x87
	 */ 
	public static Instruction saveADPIndirectLong = new Instruction(AddressingMode.DIRECT_PAGE_INDIRECT_LONG, Size.MEMORY_A) {
		{this.name = "Save Accumulator to Memory Direct Page Indirect Long";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
            CPU.dataReg.setValue(CPU.a.getValue());
			CPU.saveDataReg();
			
			int cycles = 6;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Save Accumulator to Memory Absolute
	 * 0x8D
	 */ 
	public static Instruction saveAAbsolute = new Instruction(AddressingMode.ABSOLUTE, Size.MEMORY_A) {
		{this.name = "Save Accumulator to Memory Absolute";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
            CPU.dataReg.setValue(CPU.a.getValue());
			CPU.saveDataReg();
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Save Accumulator to Memory Absolute Long
	 * 0x8F
	 */ 
	public static Instruction saveAAbsoluteLong = new Instruction(AddressingMode.ABSOLUTE_LONG, Size.MEMORY_A) {
		{this.name = "Save Accumulator to Memory Absolute Long";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
            CPU.dataReg.setValue(CPU.a.getValue());
			CPU.saveDataReg();
			
			int cycles = 5;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Save Accumulator to Memory Direct Page Indirect Indexed Y
	 * 0x91
	 */ 
	public static Instruction saveADPIndirectIndexedY = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_INDIRECT_Y, Size.MEMORY_A) {
		{this.name = "Save Accumulator to Memory Direct Page Indirect Indexed Y";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
            CPU.dataReg.setValue(CPU.a.getValue());
			CPU.saveDataReg();
			
			int cycles = 6;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Save Accumulator to Memory Direct Page Indirect
	 * 0x92
	 */ 
	public static Instruction saveADPIndirect = new Instruction(AddressingMode.DIRECT_PAGE_INDIRECT, Size.MEMORY_A) {
		{this.name = "Save Accumulator to Memory Direct Page Indirect";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
            CPU.dataReg.setValue(CPU.a.getValue());
			CPU.saveDataReg();
			
			int cycles = 5;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Save Accumulator to Memory Stack Relative Indirect Indexed Y
	 * 0x93
	 */ 
	public static Instruction saveASRIndirectIndexedY = new Instruction(AddressingMode.STACK_RELATIVE_INDIRECT_INDEXED_Y, Size.MEMORY_A) {
		{this.name = "Save Accumulator to Memory Stack Relative Indirect Indexed Y";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
            CPU.dataReg.setValue(CPU.a.getValue());
			CPU.saveDataReg();
			
			int cycles = 7;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Save Accumulator to Memory Direct Page Indexed X
	 * 0x95
	 */ 
	public static Instruction saveADirectPageX = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_X, Size.MEMORY_A) {
		{this.name = "Save Accumulator to Memory Direct Page Indexed X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
            CPU.dataReg.setValue(CPU.a.getValue());
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
	 * Save Accumulator to Memory Direct Page Indirect Long Indexed Y
	 * 0x97
	 */ 
	public static Instruction saveADPIndirectLongIndexedY = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_INDIRECT_LONG_Y, Size.MEMORY_A) {
		{this.name = "Save Accumulator to Memory Direct Page Indirect Long Indexed Y";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
            CPU.dataReg.setValue(CPU.a.getValue());
			CPU.saveDataReg();
			
			int cycles = 6;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Save Accumulator to Memory Absolute Indexed Y
	 * 0x99
	 */ 
	public static Instruction saveAAbsoluteIndexedY = new Instruction(AddressingMode.ABSOLUTE_INDEXED_Y, Size.MEMORY_A) {
		{this.name = "Save Accumulator to Memory Absolute Indexed Y";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
            CPU.dataReg.setValue(CPU.a.getValue());
			CPU.saveDataReg();
			
			int cycles = 5;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Save Accumulator to Memory Absolute Indexed X
	 * 0x9D
	 */ 
	public static Instruction saveAAbsoluteIndexedX = new Instruction(AddressingMode.ABSOLUTE_INDEXED_X, Size.MEMORY_A) {
		{this.name = "Save Accumulator to Memory Absolute Indexed X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
            CPU.dataReg.setValue(CPU.a.getValue());
			CPU.saveDataReg();
			
			int cycles = 5;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Save Accumulator to Memory Absolute Long Indexed X
	 * 0x9F
	 */ 
	public static Instruction saveAAbsoluteLongIndexedX = new Instruction(AddressingMode.ABSOLUTE_LONG_INDEXED_X, Size.MEMORY_A) {
		{this.name = "Save Accumulator to Memory Absolute Long Indexed X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
            CPU.dataReg.setValue(CPU.a.getValue());
			CPU.saveDataReg();
			
			int cycles = 5;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
}
