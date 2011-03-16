package edu.fit.cs.sno.snes.apu;


public class StatusRegister {
	/**
	 * N
	 * True = Top Bit Set, False = Top Bit Unset
	 */
	private boolean negative = false;
	
	/**
	 * V
	 * True = Overflow/Underflow, False = No Overflow
	 */
	private boolean overflow = false;
	
	/**
	 * P
	 * True = Direct Page at 0100-01FF, False = 0000-00FF
	 */
	private boolean direct_page = false;
	
	/**
	 * B
	 * True = Break Set, False = Not Break
	 */
	private boolean break_flag = false;
	
	/**
	 * H
	 * True = Carry from bit 3 to 4, False = No Carry
	 * True = No Borrow, False = Borrow
	 */
	private boolean half_carry = false;
	
	/**
	 * I
	 * True = Enabled, False = Disabled
	 */
	private boolean indirect_master = false;
	
	/**
	 * Z
	 * True = Zero, False = Nonzero
	 */
	private boolean zero = false;
	
	/**
	 * C
	 * True = Carry, False = No Carry
	 * True = No Borrow, False = Borrow
	 */
	private boolean carry = false;
	
	public int getValue() {
		int status = 0;
		status |= (negative ? 0x80 : 0);
		status |= (overflow ? 0x40 : 0);
		status |= (direct_page ? 0x20 : 0);
		status |= (break_flag ? 0x10 : 0);
		status |= (half_carry ? 0x8 : 0);
		status |= (indirect_master ? 0x4 : 0);
		status |= (zero ? 0x2 : 0);
		status |= (carry ? 0x1 : 0);
		
		return status;
	}
	
	public void setValue(int status) {
		negative = (status & 0x80) != 0;
		overflow = (status & 0x40) != 0;
		direct_page = (status & 0x20) != 0;
		break_flag = (status & 0x10) != 0;
		half_carry = (status & 0x8) != 0;
		indirect_master = (status & 0x4) != 0;
		zero = (status & 0x2) != 0;
		carry = (status & 0x1) != 0;
	}

	
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(negative 			? "N" : "n");
		sb.append(overflow 			? "V" : "v");
		sb.append(direct_page	 	? "P" : "p");
		sb.append(break_flag	 	? "B" : "b");
		sb.append(half_carry 		? "H" : "h");
		sb.append(indirect_master 	? "I" : "i");
		sb.append(zero 				? "Z" : "z");
		sb.append(carry 			? "C" : "c");
		
		return sb.toString();
	}

	public boolean isNegative() {
		return negative;
	}

	public void setNegative(boolean negative) {
		this.negative = negative;
	}

	public boolean isOverflow() {
		return overflow;
	}

	public void setOverflow(boolean overflow) {
		this.overflow = overflow;
	}

	public boolean isDirectPage() {
		return direct_page;
	}

	public void setDirectPage(boolean direct_page) {
		this.direct_page = direct_page;
	}

	public boolean isHalfCarry() {
		return half_carry;
	}

	public void setHalfCarry(boolean half_carry) {
		this.half_carry = half_carry;
	}

	public boolean isZero() {
		return zero;
	}

	public void setZero(boolean zero) {
		this.zero = zero;
	}

	public boolean isCarry() {
		return carry;
	}

	public void setCarry(boolean carry) {
		this.carry = carry;
	}

	public boolean isBreakFlag() {
		return break_flag;
	}

	public void setBreakFlag(boolean break_flag) {
		this.break_flag = break_flag;
	}

	public boolean isIndirectMaster() {
		return indirect_master;
	}

	public void setIndirectMaster(boolean indirect_master) {
		this.indirect_master = indirect_master;
	}
}
