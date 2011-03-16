package edu.fit.cs.sno.snes.cpu;


public enum AddressingMode {
	ACCUMULATOR(0, false, false),
	ABSOLUTE(2),
	ABSOLUTE_INDEXED_X(2),
	ABSOLUTE_INDEXED_Y(2),
	ABSOLUTE_INDEXED_INDIRECT(2),
	ABSOLUTE_INDIRECT(2),
	ABSOLUTE_INDIRECT_LONG(2),
	ABSOLUTE_LONG(3),
	ABSOLUTE_LONG_INDEXED_X(3),
	DIRECT_PAGE(1),
	DIRECT_PAGE_INDEXED_X(1),
	DIRECT_PAGE_INDEXED_Y(1),
	DIRECT_PAGE_INDEXED_INDIRECT_X(1),
	DIRECT_PAGE_INDEXED_INDIRECT_Y(1),
	DIRECT_PAGE_INDIRECT(1),
	DIRECT_PAGE_INDIRECT_LONG(1),
	DIRECT_PAGE_INDEXED_INDIRECT_LONG_Y(1),
	IMPLIED(0, false, false),
	IMMEDIATE_MEMORY(-1, true, false),
	IMMEDIATE_INDEX(-1, true, false),
	PROGRAM_COUNTER_RELATIVE(1),
	PROGRAM_COUNTER_RELATIVE_LONG(2),
	STACK_RELATIVE(1),
	STACK_RELATIVE_INDIRECT_INDEXED_Y(1),
	BLOCK_MOVE(2);
	
	private int numArgs;
	public boolean load;
	
	private AddressingMode(int numArgs) {
		this.numArgs = numArgs;
		this.load = true;
	}
	
	private AddressingMode(int numArgs, boolean load, boolean save) {
		this.numArgs = numArgs;
		this.load = load;
	}

	public int getNumArgs() {
		if (this == IMMEDIATE_MEMORY) {
			return (CPU.status.isMemoryAccess() ? 1 : 2);
		} else if (this == IMMEDIATE_INDEX) {
			return (CPU.status.isIndexRegister() ? 1 : 2);
		}
		
		return numArgs;
	}
}
