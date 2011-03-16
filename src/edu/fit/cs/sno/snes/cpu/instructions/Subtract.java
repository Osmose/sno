package edu.fit.cs.sno.snes.cpu.instructions;

import edu.fit.cs.sno.snes.common.Instruction;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.AddressingMode;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.util.Util;

public class Subtract {
	public static final String mnemonic = "SBC";
	/**
	 * Subtract with Borrow from Accumulator Immediate
	 * 0xE9
	 */ 
	public static Instruction subFromAImmediate = new Instruction(AddressingMode.IMMEDIATE_MEMORY) {
		{this.name = "Subtract with Borrow from Accumulator Immediate";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int oldA = CPU.a.getValue();
			boolean oldNeg = CPU.a.isNegative();
			
			int subtractand = CPU.dataReg.getValue() + (!CPU.status.isCarry() ? 1 : 0); 
			CPU.a.subtract(subtractand);
			
			// BCD adjust
			if (CPU.status.isDecimalMode()) {
				CPU.a.setValue(Util.bcdAdjustSubtract(Size.MEMORY_A.getRealSize(), CPU.a.getValue()));
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry(subtractand<=oldA);
			if (!CPU.status.isDecimalMode()) CPU.status.setOverflow(oldNeg != CPU.dataReg.isNegative() && oldNeg != CPU.a.isNegative());
			else CPU.status.setOverflow(false);
			
			int cycles = 2;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Subtract with Borrow from Accumulator Direct Page Indexed Indirect X
	 * 0xE1
	 */ 
	public static Instruction subFromADPIndirectX = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_INDIRECT_X, Size.MEMORY_A) {
		{this.name = "Subtract with Borrow from Accumulator Direct Page Indexed Indirect X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int oldA = CPU.a.getValue();
			boolean oldNeg = CPU.a.isNegative();
			
			int subtractand = CPU.dataReg.getValue() + (!CPU.status.isCarry() ? 1 : 0); 
			CPU.a.subtract(subtractand);
			
			// BCD adjust
			if (CPU.status.isDecimalMode()) {
				CPU.a.setValue(Util.bcdAdjustSubtract(Size.MEMORY_A.getRealSize(), CPU.a.getValue()));
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry(subtractand<=oldA);
			if (!CPU.status.isDecimalMode()) CPU.status.setOverflow(oldNeg != CPU.dataReg.isNegative() && oldNeg != CPU.a.isNegative());
			else CPU.status.setOverflow(false);
			
			int cycles = 6;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Subtract with Borrow from Accumulator Stack Relative
	 * 0xE3
	 */ 
	public static Instruction subFromAStackRelative = new Instruction(AddressingMode.STACK_RELATIVE, Size.MEMORY_A) {
		{this.name = "Subtract with Borrow from Accumulator Stack Relative";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int oldA = CPU.a.getValue();
			boolean oldNeg = CPU.a.isNegative();

			int subtractand = CPU.dataReg.getValue() + (!CPU.status.isCarry() ? 1 : 0); 
			CPU.a.subtract(subtractand);
			
			// BCD adjust
			if (CPU.status.isDecimalMode()) {
				CPU.a.setValue(Util.bcdAdjustSubtract(Size.MEMORY_A.getRealSize(), CPU.a.getValue()));
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry(subtractand<=oldA);
			if (!CPU.status.isDecimalMode()) CPU.status.setOverflow(oldNeg != CPU.dataReg.isNegative() && oldNeg != CPU.a.isNegative());
			else CPU.status.setOverflow(false);
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Subtract with Borrow from Accumulator Direct Page
	 * 0xE5
	 */ 
	public static Instruction subFromADP = new Instruction(AddressingMode.DIRECT_PAGE, Size.MEMORY_A) {
		{this.name = "Subtract with Borrow from Accumulator Direct Page";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int oldA = CPU.a.getValue();
			boolean oldNeg = CPU.a.isNegative();

			int subtractand = CPU.dataReg.getValue() + (!CPU.status.isCarry() ? 1 : 0); 
			CPU.a.subtract(subtractand);
			
			// BCD adjust
			if (CPU.status.isDecimalMode()) {
				CPU.a.setValue(Util.bcdAdjustSubtract(Size.MEMORY_A.getRealSize(), CPU.a.getValue()));
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry(subtractand<=oldA);
			if (!CPU.status.isDecimalMode()) CPU.status.setOverflow(oldNeg != CPU.dataReg.isNegative() && oldNeg != CPU.a.isNegative());
			else CPU.status.setOverflow(false);
			
			int cycles = 3;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Subtract with Borrow from Accumulator Direct Page Indirect Long
	 * 0xE7
	 */ 
	public static Instruction subFromADPIndirectLong = new Instruction(AddressingMode.DIRECT_PAGE_INDIRECT_LONG, Size.MEMORY_A) {
		{this.name = "Subtract with Borrow from Accumulator Direct Page Indirect Long";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int oldA = CPU.a.getValue();
			boolean oldNeg = CPU.a.isNegative();

			int subtractand = CPU.dataReg.getValue() + (!CPU.status.isCarry() ? 1 : 0); 
			CPU.a.subtract(subtractand);
			
			// BCD adjust
			if (CPU.status.isDecimalMode()) {
				CPU.a.setValue(Util.bcdAdjustSubtract(Size.MEMORY_A.getRealSize(), CPU.a.getValue()));
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry(subtractand<=oldA);
			if (!CPU.status.isDecimalMode()) CPU.status.setOverflow(oldNeg != CPU.dataReg.isNegative() && oldNeg != CPU.a.isNegative());
			else CPU.status.setOverflow(false);
			
			int cycles = 6;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Subtract with Borrow from Accumulator Absolute
	 * 0xED
	 */ 
	public static Instruction subFromAAbsolute = new Instruction(AddressingMode.ABSOLUTE, Size.MEMORY_A) {
		{this.name = "Subtract with Borrow from Accumulator Absolute";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int oldA = CPU.a.getValue();
			boolean oldNeg = CPU.a.isNegative();

			int subtractand = CPU.dataReg.getValue() + (!CPU.status.isCarry() ? 1 : 0); 
			CPU.a.subtract(subtractand);
			
			// BCD adjust
			if (CPU.status.isDecimalMode()) {
				CPU.a.setValue(Util.bcdAdjustSubtract(Size.MEMORY_A.getRealSize(), CPU.a.getValue()));
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry(subtractand<=oldA);
			if (!CPU.status.isDecimalMode()) CPU.status.setOverflow(oldNeg != CPU.dataReg.isNegative() && oldNeg != CPU.a.isNegative());
			else CPU.status.setOverflow(false);
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Subtract with Borrow from Accumulator Absolute Long
	 * 0xEF
	 */ 
	public static Instruction subFromAAbsoluteLong = new Instruction(AddressingMode.ABSOLUTE_LONG, Size.MEMORY_A) {
		{this.name = "Subtract with Borrow from Accumulator Absolute Long";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int oldA = CPU.a.getValue();
			boolean oldNeg = CPU.a.isNegative();

			int subtractand = CPU.dataReg.getValue() + (!CPU.status.isCarry() ? 1 : 0); 
			CPU.a.subtract(subtractand);
			
			// BCD adjust
			if (CPU.status.isDecimalMode()) {
				CPU.a.setValue(Util.bcdAdjustSubtract(Size.MEMORY_A.getRealSize(), CPU.a.getValue()));
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry(subtractand<=oldA);
			if (!CPU.status.isDecimalMode()) CPU.status.setOverflow(oldNeg != CPU.dataReg.isNegative() && oldNeg != CPU.a.isNegative());
			else CPU.status.setOverflow(false);
			
			int cycles = 5;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Subtract with Borrow from Accumulator Direct Page Indirect Indexed Y
	 * 0xF1
	 */ 
	public static Instruction subFromADPIndirectY = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_INDIRECT_Y, Size.MEMORY_A) {
		{this.name = "Subtract with Borrow from Accumulator Direct Page Indirect Indexed Y";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int oldA = CPU.a.getValue();
			boolean oldNeg = CPU.a.isNegative();

			int subtractand = CPU.dataReg.getValue() + (!CPU.status.isCarry() ? 1 : 0); 
			CPU.a.subtract(subtractand);
			
			// BCD adjust
			if (CPU.status.isDecimalMode()) {
				CPU.a.setValue(Util.bcdAdjustSubtract(Size.MEMORY_A.getRealSize(), CPU.a.getValue()));
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry(subtractand<=oldA);
			if (!CPU.status.isDecimalMode()) CPU.status.setOverflow(oldNeg != CPU.dataReg.isNegative() && oldNeg != CPU.a.isNegative());
			else CPU.status.setOverflow(false);
			
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
	 * Subtract with Borrow from Accumulator Direct Page Indirect
	 * 0xF2
	 */ 
	public static Instruction subFromADPIndirect = new Instruction(AddressingMode.DIRECT_PAGE_INDIRECT, Size.MEMORY_A) {
		{this.name = "Subtract with Borrow from Accumulator Direct Page Indirect";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int oldA = CPU.a.getValue();
			boolean oldNeg = CPU.a.isNegative();

			int subtractand = CPU.dataReg.getValue() + (!CPU.status.isCarry() ? 1 : 0); 
			CPU.a.subtract(subtractand);
			
			// BCD adjust
			if (CPU.status.isDecimalMode()) {
				CPU.a.setValue(Util.bcdAdjustSubtract(Size.MEMORY_A.getRealSize(), CPU.a.getValue()));
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry(subtractand<=oldA);
			if (!CPU.status.isDecimalMode()) CPU.status.setOverflow(oldNeg != CPU.dataReg.isNegative() && oldNeg != CPU.a.isNegative());
			else CPU.status.setOverflow(false);
			
			int cycles = 5;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Subtract with Borrow from Accumulator Stack Relative Indirect Indexed Y
	 * 0xF3
	 */ 
	public static Instruction subFromASRIndirectY = new Instruction(AddressingMode.STACK_RELATIVE_INDIRECT_INDEXED_Y, Size.MEMORY_A) {
		{this.name = "Subtract with Borrow from Accumulator Stack Relative Indirect Indexed Y";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int oldA = CPU.a.getValue();
			boolean oldNeg = CPU.a.isNegative();

			int subtractand = CPU.dataReg.getValue() + (!CPU.status.isCarry() ? 1 : 0); 
			CPU.a.subtract(subtractand);
			
			// BCD adjust
			if (CPU.status.isDecimalMode()) {
				CPU.a.setValue(Util.bcdAdjustSubtract(Size.MEMORY_A.getRealSize(), CPU.a.getValue()));
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry(subtractand<=oldA);
			if (!CPU.status.isDecimalMode()) CPU.status.setOverflow(oldNeg != CPU.dataReg.isNegative() && oldNeg != CPU.a.isNegative());
			else CPU.status.setOverflow(false);
			
			int cycles = 7;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Subtract with Borrow from Accumulator Direct Page Indexed X
	 * 0xF5
	 */ 
	public static Instruction subFromADPX = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_X, Size.MEMORY_A) {
		{this.name = "Subtract with Borrow from Accumulator Direct Page Indexed X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int oldA = CPU.a.getValue();
			boolean oldNeg = CPU.a.isNegative();

			int subtractand = CPU.dataReg.getValue() + (!CPU.status.isCarry() ? 1 : 0); 
			CPU.a.subtract(subtractand);
			
			// BCD adjust
			if (CPU.status.isDecimalMode()) {
				CPU.a.setValue(Util.bcdAdjustSubtract(Size.MEMORY_A.getRealSize(), CPU.a.getValue()));
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry(subtractand<=oldA);
			if (!CPU.status.isDecimalMode()) CPU.status.setOverflow(oldNeg != CPU.dataReg.isNegative() && oldNeg != CPU.a.isNegative());
			else CPU.status.setOverflow(false);
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Subtract with Borrow from Accumulator Direct Page Indirect Long Indexed Y
	 * 0xF7
	 */ 
	public static Instruction subFromADPIndirectLongY = new Instruction(AddressingMode.DIRECT_PAGE_INDEXED_INDIRECT_LONG_Y, Size.MEMORY_A) {
		{this.name = "Subtract with Borrow from Accumulator Direct Page Indirect Long Indexed Y";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int oldA = CPU.a.getValue();
			boolean oldNeg = CPU.a.isNegative();

			int subtractand = CPU.dataReg.getValue() + (!CPU.status.isCarry() ? 1 : 0); 
			CPU.a.subtract(subtractand);
			
			// BCD adjust
			if (CPU.status.isDecimalMode()) {
				CPU.a.setValue(Util.bcdAdjustSubtract(Size.MEMORY_A.getRealSize(), CPU.a.getValue()));
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry(subtractand<=oldA);
			if (!CPU.status.isDecimalMode()) CPU.status.setOverflow(oldNeg != CPU.dataReg.isNegative() && oldNeg != CPU.a.isNegative());
			else CPU.status.setOverflow(false);
			
			int cycles = 6;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if ((CPU.dp.getValue() & 0xFF) != 0)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Subtract with Borrow from Accumulator Absolute Indexed Y
	 * 0xF9
	 */ 
	public static Instruction subFromAAbsoluteY = new Instruction(AddressingMode.ABSOLUTE_INDEXED_Y, Size.MEMORY_A) {
		{this.name = "Subtract with Borrow from Accumulator Absolute Indexed Y";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int oldA = CPU.a.getValue();
			boolean oldNeg = CPU.a.isNegative();

			int subtractand = CPU.dataReg.getValue() + (!CPU.status.isCarry() ? 1 : 0); 
			CPU.a.subtract(subtractand);
			
			// BCD adjust
			if (CPU.status.isDecimalMode()) {
				CPU.a.setValue(Util.bcdAdjustSubtract(Size.MEMORY_A.getRealSize(), CPU.a.getValue()));
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry(subtractand<=oldA);
			if (!CPU.status.isDecimalMode()) CPU.status.setOverflow(oldNeg != CPU.dataReg.isNegative() && oldNeg != CPU.a.isNegative());
			else CPU.status.setOverflow(false);
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if (CPU.indexCrossedPageBoundary)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Subtract with Borrow from Accumulator Absolute Indexed X
	 * 0xFD
	 */ 
	public static Instruction subFromAAbsoluteX = new Instruction(AddressingMode.ABSOLUTE_INDEXED_X, Size.MEMORY_A) {
		{this.name = "Subtract with Borrow from Accumulator Absolute Indexed X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int oldA = CPU.a.getValue();
			boolean oldNeg = CPU.a.isNegative();

			int subtractand = CPU.dataReg.getValue() + (!CPU.status.isCarry() ? 1 : 0); 
			CPU.a.subtract(subtractand);
			
			// BCD adjust
			if (CPU.status.isDecimalMode()) {
				CPU.a.setValue(Util.bcdAdjustSubtract(Size.MEMORY_A.getRealSize(), CPU.a.getValue()));
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry(subtractand<=oldA);
			if (!CPU.status.isDecimalMode()) CPU.status.setOverflow(oldNeg != CPU.dataReg.isNegative() && oldNeg != CPU.a.isNegative());
			else CPU.status.setOverflow(false);
			
			int cycles = 4;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			if (CPU.indexCrossedPageBoundary)
				cycles++;
			return cycles;
		}
	};
	
	/**
	 * Subtract with Borrow from Accumulator Absolute Long Indexed X
	 * 0xFF
	 */ 
	public static Instruction subFromAAbsoluteLongX = new Instruction(AddressingMode.ABSOLUTE_LONG_INDEXED_X, Size.MEMORY_A) {
		{this.name = "Subtract with Borrow from Accumulator Absolute Long Indexed X";}
		public int run(int[] args) {
			CPU.loadDataRegister(addrMode, size.getRealSize(), args);
			int oldA = CPU.a.getValue();
			boolean oldNeg = CPU.a.isNegative();

			int subtractand = CPU.dataReg.getValue() + (!CPU.status.isCarry() ? 1 : 0); 
			CPU.a.subtract(subtractand);
			
			// BCD adjust
			if (CPU.status.isDecimalMode()) {
				CPU.a.setValue(Util.bcdAdjustSubtract(Size.MEMORY_A.getRealSize(), CPU.a.getValue()));
			}
			
			CPU.status.setNegative(CPU.a.isNegative());
			CPU.status.setZero(CPU.a.getValue() == 0);
			CPU.status.setCarry(subtractand<=oldA);
			if (!CPU.status.isDecimalMode()) CPU.status.setOverflow(oldNeg != CPU.dataReg.isNegative() && oldNeg != CPU.a.isNegative());
			else CPU.status.setOverflow(false);
			
			int cycles = 5;
			if (!CPU.status.isMemoryAccess())
				cycles++;
			return cycles;
		}
	};
}
