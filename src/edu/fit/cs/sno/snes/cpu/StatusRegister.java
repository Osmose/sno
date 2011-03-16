package edu.fit.cs.sno.snes.cpu;

import edu.fit.cs.sno.snes.common.Size;

public class StatusRegister {
	/**
	 * N
	 * True = Top Bit Set, False = Top Bit Unset
	 */
	private boolean negative = false;
	
	/**
	 * V
	 * True = Overflow, False = No Overflow
	 */
	private boolean overflow = false;
	
	/**
	 * M
	 * True = 8-bit, False = 16-bit
	 */
	private boolean memory_access = false;
	
	/**
	 * X
	 * True = 8-bit, False = 16-bit
	 */
	private boolean index_register = false;
	
	/**
	 * D
	 * True = Decimal, False = Binary
	 */
	private boolean decimal_mode = false;
	
	/**
	 * I
	 * True = Disabled, False = Enableds
	 */
	private boolean irq_disable = false;
	
	/**
	 * Z
	 * True = Zero, False = Nonzero
	 */
	private boolean zero = false;
	
	/**
	 * C
	 * Addition: True = Carry, False = No Carry
	 * Subtraction: True = No Borrow, False = Borrow
	 */
	private boolean carry = false;
	
	public int getValue() {
		int status = 0;
		status |= (negative ? 0x80 : 0);
		status |= (overflow ? 0x40 : 0);
		status |= (memory_access ? 0x20 : 0);
		status |= (index_register ? 0x10 : 0);
		status |= (decimal_mode ? 0x8 : 0);
		status |= (irq_disable ? 0x4 : 0);
		status |= (zero ? 0x2 : 0);
		status |= (carry ? 0x1 : 0);
		
		return status;
	}
	
	public void setValue(int status) {
		negative = (status & 0x80) != 0;
		overflow = (status & 0x40) != 0;
		setMemoryAccess((status & 0x20) != 0);
		setIndexRegister((status & 0x10) != 0);
		decimal_mode = (status & 0x8) != 0;
		irq_disable = (status & 0x4) != 0;
		zero = (status & 0x2) != 0;
		carry = (status & 0x1) != 0;
	}

	public void setNegative(boolean negative) {
		this.negative = negative;
	}

	public boolean isNegative() {
		return negative;
	}

	public void setOverflow(boolean overflow) {
		this.overflow = overflow;
	}

	public boolean isOverflow() {
		return overflow;
	}

	public void setMemoryAccess(boolean memory_access) {
		this.memory_access = memory_access;
		
		if (memory_access) {
			CPU.a.setSize(Size.BYTE);
		} else {
			CPU.a.setSize(Size.SHORT);
		}
	}

	public boolean isMemoryAccess() {
		return memory_access;
	}

	public void setIndexRegister(boolean index_register) {
		this.index_register = index_register;
		
		if (index_register) {
			CPU.x.setSize(Size.BYTE);
			CPU.x.setValue(CPU.x.getValue() & 0xFF);
			
			CPU.y.setSize(Size.BYTE);
			CPU.y.setValue(CPU.y.getValue() & 0xFF);
		} else {
			CPU.x.setSize(Size.SHORT);
			CPU.y.setSize(Size.SHORT);
		}
	}

	public boolean isIndexRegister() {
		return index_register;
	}

	public void setDecimalMode(boolean decimal_mode) {
		this.decimal_mode = decimal_mode;
	}

	public boolean isDecimalMode() {
		return decimal_mode;
	}

	public void setIrqDisable(boolean irq_disable) {
		this.irq_disable = irq_disable;
	}

	public boolean isIrqDisable() {
		return irq_disable;
	}

	public void setZero(boolean zero) {
		this.zero = zero;
	}

	public boolean isZero() {
		return zero;
	}

	public void setCarry(boolean carry) {
		this.carry = carry;
	}

	public boolean isCarry() {
		return carry;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(negative 			? "N" : "n");
		sb.append(overflow 			? "V" : "v");
		sb.append(memory_access 	? "M" : "m");
		sb.append(index_register 	? "X" : "x");
		sb.append(decimal_mode 		? "D" : "d");
		sb.append(irq_disable 		? "I" : "i");
		sb.append(zero 				? "Z" : "z");
		sb.append(carry 			? "C" : "c");
		
		return sb.toString();
	}
}
