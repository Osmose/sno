package edu.fit.cs.sno.snes.cpu.instructions;

import edu.fit.cs.sno.snes.common.Instruction;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.AddressingMode;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.util.Util;

public class Add {
	public static final String mnemonic = "ADC";
	/**
	 * Add with Carry from Accumulator Immediate
	 * 0x69
	 */ 
	public static Instruction addImmediate = new Instruction(AddressingMode.IMMEDIATE_MEMORY) {
		{this.name = "Add with Carry from Accumulator Immediate";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int oldA = CPU.a.getValue();
			boolean oldNeg = CPU.a.isNegative();
			
			CPU.a.add(CPU.dataReg.getValue() + (CPU.status.isCarry() ? 1 : 0));
			
			// BCD adjust
			if (CPU.status.isDecimalMode()) {
				CPU.a.setValue(Util.bcdAdjustAdd(Size.MEMORY_A.getRealSize(), CPU.a.getValue()));
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry((CPU.a.getValue() < oldA) || (CPU.status.isCarry() && oldA == CPU.a.getValue()));
			if (CPU.status.isDecimalMode()) CPU.status.setOverflow(false);
			else CPU.status.setOverflow(oldNeg == CPU.dataReg.isNegative() && oldNeg != CPU.a.isNegative());
			
			int cycles = 2;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Add with Carry from Accumulator Direct Page Indexed Indirect X
	 * 0x61
	 */ 
	public static Instruction addDPIndirectX = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_INDIRECT_X, Size.MEMORY_A) {
		{this.name = "Add with Carry from Accumulator Direct Page Indexed Indirect X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int oldA = CPU.a.getValue();
			boolean oldNeg = CPU.a.isNegative();
			
			CPU.a.add(CPU.dataReg.getValue() + (CPU.status.isCarry() ? 1 : 0));
			
			// BCD adjust
			if (CPU.status.isDecimalMode()) {
				CPU.a.setValue(Util.bcdAdjustAdd(Size.MEMORY_A.getRealSize(), CPU.a.getValue()));
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry((CPU.a.getValue() < oldA) || (CPU.status.isCarry() && oldA == CPU.a.getValue()));
			if (CPU.status.isDecimalMode()) CPU.status.setOverflow(false);
			else CPU.status.setOverflow(oldNeg == CPU.dataReg.isNegative() && oldNeg != CPU.a.isNegative());
			
			int cycles = 6;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Add with Carry from Accumulator Stack Relative
	 * 0x63
	 */ 
	public static Instruction addStackRelative = new Instruction(AddressingMode.STACK_RELATIVE, Size.MEMORY_A) {
		{this.name = "Add with Carry from Accumulator Stack Relative";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int oldA = CPU.a.getValue();
			boolean oldNeg = CPU.a.isNegative();
			
			CPU.a.add(CPU.dataReg.getValue() + (CPU.status.isCarry() ? 1 : 0));
			
			// BCD adjust
			if (CPU.status.isDecimalMode()) {
				CPU.a.setValue(Util.bcdAdjustAdd(Size.MEMORY_A.getRealSize(), CPU.a.getValue()));
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry((CPU.a.getValue() < oldA) || (CPU.status.isCarry() && oldA == CPU.a.getValue()));
			if (CPU.status.isDecimalMode()) CPU.status.setOverflow(false);
			else CPU.status.setOverflow(oldNeg == CPU.dataReg.isNegative() && oldNeg != CPU.a.isNegative());
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Add with Carry from Accumulator Direct Page
	 * 0x65
	 */ 
	public static Instruction addDP = new Instruction(AddressingMode.DIRECT_PAGE, Size.MEMORY_A) {
		{this.name = "Add with Carry from Accumulator Direct Page";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int oldA = CPU.a.getValue();
			boolean oldNeg = CPU.a.isNegative();
			
			CPU.a.add(CPU.dataReg.getValue() + (CPU.status.isCarry() ? 1 : 0));
			
			// BCD adjust
			if (CPU.status.isDecimalMode()) {
				CPU.a.setValue(Util.bcdAdjustAdd(Size.MEMORY_A.getRealSize(), CPU.a.getValue()));
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry((CPU.a.getValue() < oldA) || (CPU.status.isCarry() && oldA == CPU.a.getValue()));
			if (CPU.status.isDecimalMode()) CPU.status.setOverflow(false);
			else CPU.status.setOverflow(oldNeg == CPU.dataReg.isNegative() && oldNeg != CPU.a.isNegative());
			
			int cycles = 3;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Add with Carry from Accumulator Direct Page Indirect Long
	 * 0x67
	 */ 
	public static Instruction addDPIndirectLong = new Instruction(AddressingMode.DIRECT_PAGE_INDIRECT_LONG, Size.MEMORY_A) {
		{this.name = "Add with Carry from Accumulator Direct Page Indirect Long";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int oldA = CPU.a.getValue();
			boolean oldNeg = CPU.a.isNegative();
			
			CPU.a.add(CPU.dataReg.getValue() + (CPU.status.isCarry() ? 1 : 0));
			
			// BCD adjust
			if (CPU.status.isDecimalMode()) {
				CPU.a.setValue(Util.bcdAdjustAdd(Size.MEMORY_A.getRealSize(), CPU.a.getValue()));
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry((CPU.a.getValue() < oldA) || (CPU.status.isCarry() && oldA == CPU.a.getValue()));
			if (CPU.status.isDecimalMode()) CPU.status.setOverflow(false);
			else CPU.status.setOverflow(oldNeg == CPU.dataReg.isNegative() && oldNeg != CPU.a.isNegative());
			
			int cycles = 6;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Add with Carry from Accumulator Absolute
	 * 0x6D
	 */ 
	public static Instruction addAbsolute = new Instruction(AddressingMode.ABSOLUTE, Size.MEMORY_A) {
		{this.name = "Add with Carry from Accumulator Absolute";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int oldA = CPU.a.getValue();
			boolean oldNeg = CPU.a.isNegative();
			
			CPU.a.add(CPU.dataReg.getValue() + (CPU.status.isCarry() ? 1 : 0));
			
			// BCD adjust
			if (CPU.status.isDecimalMode()) {
				CPU.a.setValue(Util.bcdAdjustAdd(Size.MEMORY_A.getRealSize(), CPU.a.getValue()));
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry((CPU.a.getValue() < oldA) || (CPU.status.isCarry() && oldA == CPU.a.getValue()));
			if (CPU.status.isDecimalMode()) CPU.status.setOverflow(false);
			else CPU.status.setOverflow(oldNeg == CPU.dataReg.isNegative() && oldNeg != CPU.a.isNegative());
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Add with Carry from Accumulator Absolute Long
	 * 0x6F
	 */ 
	public static Instruction addAbsoluteLong = new Instruction(AddressingMode.ABSOLUTE_LONG, Size.MEMORY_A) {
		{this.name = "Add with Carry from Accumulator Absolute Long";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int oldA = CPU.a.getValue();
			boolean oldNeg = CPU.a.isNegative();
			
			CPU.a.add(CPU.dataReg.getValue() + (CPU.status.isCarry() ? 1 : 0));
			
			// BCD adjust
			if (CPU.status.isDecimalMode()) {
				CPU.a.setValue(Util.bcdAdjustAdd(Size.MEMORY_A.getRealSize(), CPU.a.getValue()));
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry((CPU.a.getValue() < oldA) || (CPU.status.isCarry() && oldA == CPU.a.getValue()));
			if (CPU.status.isDecimalMode()) CPU.status.setOverflow(false);
			else CPU.status.setOverflow(oldNeg == CPU.dataReg.isNegative() && oldNeg != CPU.a.isNegative());
			
			int cycles = 5;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Add with Carry from Accumulator Direct Page Indirect Indexed Y
	 * 0x71
	 */ 
	public static Instruction addDPIndirectY = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_INDIRECT_Y, Size.MEMORY_A) {
		{this.name = "Add with Carry from Accumulator Direct Page Indirect Indexed Y";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int oldA = CPU.a.getValue();
			boolean oldNeg = CPU.a.isNegative();
			
			CPU.a.add(CPU.dataReg.getValue() + (CPU.status.isCarry() ? 1 : 0));
			
			// BCD adjust
			if (CPU.status.isDecimalMode()) {
				CPU.a.setValue(Util.bcdAdjustAdd(Size.MEMORY_A.getRealSize(), CPU.a.getValue()));
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry((CPU.a.getValue() < oldA) || (CPU.status.isCarry() && oldA == CPU.a.getValue()));
			if (CPU.status.isDecimalMode()) CPU.status.setOverflow(false);
			else CPU.status.setOverflow(oldNeg == CPU.dataReg.isNegative() && oldNeg != CPU.a.isNegative());
			
			int cycles = 5;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			if (CPU.indexCrossedPageBoundary)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Add with Carry from Accumulator Direct Page Indirect
	 * 0x72
	 */ 
	public static Instruction addDPIndirect = new Instruction(AddressingMode.DIRECT_PAGE_INDIRECT, Size.MEMORY_A) {
		{this.name = "Add with Carry from Accumulator Direct Page Indirect";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int oldA = CPU.a.getValue();
			boolean oldNeg = CPU.a.isNegative();
			
			CPU.a.add(CPU.dataReg.getValue() + (CPU.status.isCarry() ? 1 : 0));
			
			// BCD adjust
			if (CPU.status.isDecimalMode()) {
				CPU.a.setValue(Util.bcdAdjustAdd(Size.MEMORY_A.getRealSize(), CPU.a.getValue()));
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry((CPU.a.getValue() < oldA) || (CPU.status.isCarry() && oldA == CPU.a.getValue()));
			if (CPU.status.isDecimalMode()) CPU.status.setOverflow(false);
			else CPU.status.setOverflow(oldNeg == CPU.dataReg.isNegative() && oldNeg != CPU.a.isNegative());
			
			int cycles = 5;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Add with Carry from Accumulator Stack Relative Indirect Indexed Y
	 * 0x73
	 */ 
	public static Instruction addSRIndirectY = new Instruction(AddressingMode.STACK_RELATIVE_INDIRECT_INDEXED_Y, Size.MEMORY_A) {
		{this.name = "Add with Carry from Accumulator Stack Relative Indirect Indexed Y";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int oldA = CPU.a.getValue();
			boolean oldNeg = CPU.a.isNegative();
			
			CPU.a.add(CPU.dataReg.getValue() + (CPU.status.isCarry() ? 1 : 0));
			
			// BCD adjust
			if (CPU.status.isDecimalMode()) {
				CPU.a.setValue(Util.bcdAdjustAdd(Size.MEMORY_A.getRealSize(), CPU.a.getValue()));
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry((CPU.a.getValue() < oldA) || (CPU.status.isCarry() && oldA == CPU.a.getValue()));
			if (CPU.status.isDecimalMode()) CPU.status.setOverflow(false);
			else CPU.status.setOverflow(oldNeg == CPU.dataReg.isNegative() && oldNeg != CPU.a.isNegative());

			int cycles = 7;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Add with Carry from Accumulator Direct Page Indexed X
	 * 0x75
	 */ 
	public static Instruction addDPX = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_X, Size.MEMORY_A) {
		{this.name = "Add with Carry from Accumulator Direct Page Indexed X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int oldA = CPU.a.getValue();
			boolean oldNeg = CPU.a.isNegative();
			
			CPU.a.add(CPU.dataReg.getValue() + (CPU.status.isCarry() ? 1 : 0));
			
			// BCD adjust
			if (CPU.status.isDecimalMode()) {
				CPU.a.setValue(Util.bcdAdjustAdd(Size.MEMORY_A.getRealSize(), CPU.a.getValue()));
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry((CPU.a.getValue() < oldA) || (CPU.status.isCarry() && oldA == CPU.a.getValue()));
			if (CPU.status.isDecimalMode()) CPU.status.setOverflow(false);
			else CPU.status.setOverflow(oldNeg == CPU.dataReg.isNegative() && oldNeg != CPU.a.isNegative());
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Add with Carry from Accumulator Direct Page Indirect Long Indexed Y
	 * 0x77
	 */ 
	public static Instruction addDPIndirectLongY = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_INDIRECT_LONG_Y, Size.MEMORY_A) {
		{this.name = "Add with Carry from Accumulator Direct Page Indirect Long Indexed Y";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int oldA = CPU.a.getValue();
			boolean oldNeg = CPU.a.isNegative();
			
			CPU.a.add(CPU.dataReg.getValue() + (CPU.status.isCarry() ? 1 : 0));
			
			// BCD adjust
			if (CPU.status.isDecimalMode()) {
				CPU.a.setValue(Util.bcdAdjustAdd(Size.MEMORY_A.getRealSize(), CPU.a.getValue()));
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry((CPU.a.getValue() < oldA) || (CPU.status.isCarry() && oldA == CPU.a.getValue()));
			if (CPU.status.isDecimalMode()) CPU.status.setOverflow(false);
			else CPU.status.setOverflow(oldNeg == CPU.dataReg.isNegative() && oldNeg != CPU.a.isNegative());
			
			int cycles = 6;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Add with Carry from Accumulator Absolute Indexed Y
	 * 0x79
	 */ 
	public static Instruction addAbsoluteY = new Instruction(AddressingMode.ABSOLUTE_INDEXED_Y, Size.MEMORY_A) {
		{this.name = "Add with Carry from Accumulator Absolute Indexed Y";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int oldA = CPU.a.getValue();
			boolean oldNeg = CPU.a.isNegative();
			
			CPU.a.add(CPU.dataReg.getValue() + (CPU.status.isCarry() ? 1 : 0));
			
			// BCD adjust
			if (CPU.status.isDecimalMode()) {
				CPU.a.setValue(Util.bcdAdjustAdd(Size.MEMORY_A.getRealSize(), CPU.a.getValue()));
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry((CPU.a.getValue() < oldA) || (CPU.status.isCarry() && oldA == CPU.a.getValue()));
			if (CPU.status.isDecimalMode()) CPU.status.setOverflow(false);
			else CPU.status.setOverflow(oldNeg == CPU.dataReg.isNegative() && oldNeg != CPU.a.isNegative());
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if (CPU.indexCrossedPageBoundary)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Add with Carry from Accumulator Absolute Indexed X
	 * 0x7D
	 */ 
	public static Instruction addAbsoluteX = new Instruction(AddressingMode.ABSOLUTE_INDEXED_X, Size.MEMORY_A) {
		{this.name = "Add with Carry from Accumulator Absolute Indexed X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int oldA = CPU.a.getValue();
			boolean oldNeg = CPU.a.isNegative();
			
			CPU.a.add(CPU.dataReg.getValue() + (CPU.status.isCarry() ? 1 : 0));
			
			// BCD adjust
			if (CPU.status.isDecimalMode()) {
				CPU.a.setValue(Util.bcdAdjustAdd(Size.MEMORY_A.getRealSize(), CPU.a.getValue()));
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry((CPU.a.getValue() < oldA) || (CPU.status.isCarry() && oldA == CPU.a.getValue()));
			if (CPU.status.isDecimalMode()) CPU.status.setOverflow(false);
			else CPU.status.setOverflow(oldNeg == CPU.dataReg.isNegative() && oldNeg != CPU.a.isNegative());
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if (CPU.indexCrossedPageBoundary)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Add with Carry from Accumulator Absolute Long Indexed X
	 * 0x7F
	 */ 
	public static Instruction addAbsoluteLongX = new Instruction(AddressingMode.ABSOLUTE_LONG_INDEXED_X, Size.MEMORY_A) {
		{this.name = "Add with Carry from Accumulator Absolute Long Indexed X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int oldA = CPU.a.getValue();
			boolean oldNeg = CPU.a.isNegative();
			
			CPU.a.add(CPU.dataReg.getValue() + (CPU.status.isCarry() ? 1 : 0));
			
			// BCD adjust
			if (CPU.status.isDecimalMode()) {
				CPU.a.setValue(Util.bcdAdjustAdd(Size.MEMORY_A.getRealSize(), CPU.a.getValue()));
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry((CPU.a.getValue() < oldA) || (CPU.status.isCarry() && oldA == CPU.a.getValue()));
			if (CPU.status.isDecimalMode()) CPU.status.setOverflow(false);
			else CPU.status.setOverflow(oldNeg == CPU.dataReg.isNegative() && oldNeg != CPU.a.isNegative());
			
			int cycles = 5;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
}
