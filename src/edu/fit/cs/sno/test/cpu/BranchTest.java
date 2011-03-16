package edu.fit.cs.sno.test.cpu;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.cpu.instructions.Branching;
import edu.fit.cs.sno.snes.mem.LoROMMemory;
import edu.fit.cs.sno.util.Settings;

public class BranchTest {

	@Before
	public void setUp() throws Exception {
		new Branching(); // For 100% coverage
		Settings.init();
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
	}

	@Test
	public void branchAlways() {
		// Positive offset
		int pc = 0x0085;
		int offset = 0x78;
		int addr = pc + (offset);

		CPU.pc.setValue(pc);
		CPU.doOp(0x80, new int[] { offset });

		assertEquals(addr, CPU.pc.getValue());

		// Negative offset
		CPU.resetCPU();
		offset = 0x80; // 0x80 is minus 128, 0xFF is -1
		addr = pc - 128;

		CPU.pc.setValue(pc);
		CPU.doOp(0x80, new int[] { offset });

		assertEquals(addr, CPU.pc.getValue());
	}

	@Test
	public void branchAlwaysLong() {
		// 0x82
		// Positive offset
		final int pc = 0x0085;
		int offset = 0x78AF;
		int addr = pc + offset;
		int args[] = { 0xAF, 0x78 };

		CPU.pc.setValue(pc);
		CPU.doOp(0x82, args);

		assertEquals(addr, CPU.pc.getValue());

		// Negative offset
		CPU.resetCPU();
		offset = 0xFFFF; // -1
		args = new int[] { 0xFF, 0xFF };
		addr = pc - 1;

		CPU.pc.setValue(pc);
		CPU.doOp(0x82, args);

		assertEquals(addr, CPU.pc.getValue());
	}

	@Test
	public void branchCarryClear() {
		// 0x90
		// Positive offset
		int pc = 0x0085;
		int offset = 0x78;
		int addr = pc + (offset);

		CPU.pc.setValue(pc);
		CPU.status.setCarry(false);
		CPU.doOp(0x90, new int[] { offset });

		assertEquals(addr, CPU.pc.getValue());

		// Negative offset
		CPU.resetCPU();
		offset = 0x80; // 0x80 is minus 128, 0xFF is -1
		addr = pc - 128;

		CPU.pc.setValue(pc);
		CPU.status.setCarry(false);
		CPU.doOp(0x90, new int[] { offset });

		assertEquals(addr, CPU.pc.getValue());

		// Carry set(i.e. don't branch)
		CPU.resetCPU();
		offset = 0x57;
		CPU.pc.setValue(pc);
		CPU.status.setCarry(true);
		CPU.doOp(0x90, new int[] { offset });

		assertEquals(pc, CPU.pc.getValue());
	}

	@Test
	public void branchCarrySet() {
		// 0xB0
		// Positive offset
		int pc = 0x0085;
		int offset = 0x78;
		int addr = pc + (offset);

		CPU.pc.setValue(pc);
		CPU.status.setCarry(true);
		CPU.doOp(0xB0, new int[] { offset });

		assertEquals(addr, CPU.pc.getValue());

		// Negative offset
		CPU.resetCPU();
		offset = 0x80; // 0x80 is minus 128, 0xFF is -1
		addr = pc - 128;

		CPU.pc.setValue(pc);
		CPU.status.setCarry(true);
		CPU.doOp(0xB0, new int[] { offset });

		assertEquals(addr, CPU.pc.getValue());

		// Carry set(i.e. don't branch)
		CPU.resetCPU();
		offset = 0x57;
		CPU.pc.setValue(pc);
		CPU.status.setCarry(false);
		CPU.doOp(0xB0, new int[] { offset });

		assertEquals(pc, CPU.pc.getValue());
	}

	@Test
	public void branchEqual() {
		// 0xF0
		// Positive offset
		int pc = 0x0085;
		int offset = 0x78;
		int addr = pc + (offset);

		CPU.pc.setValue(pc);
		CPU.status.setZero(true);
		CPU.doOp(0xF0, new int[] { offset });

		assertEquals(addr, CPU.pc.getValue());

		// Negative offset
		CPU.resetCPU();
		offset = 0x80; // 0x80 is minus 128, 0xFF is -1
		addr = pc - 128;

		CPU.pc.setValue(pc);
		CPU.status.setZero(true);
		CPU.doOp(0xF0, new int[] { offset });

		assertEquals(addr, CPU.pc.getValue());

		// Carry set(i.e. don't branch)
		CPU.resetCPU();
		offset = 0x57;
		CPU.pc.setValue(pc);
		CPU.status.setZero(false);
		CPU.doOp(0xF0, new int[] { offset });

		assertEquals(pc, CPU.pc.getValue());
	}

	@Test
	public void branchMinus() {
		// 0x30
		// Positive offset
		int pc = 0x0085;
		int offset = 0x78;
		int addr = pc + (offset);

		CPU.pc.setValue(pc);
		CPU.status.setNegative(true);
		CPU.doOp(0x30, new int[] { offset });

		assertEquals(addr, CPU.pc.getValue());

		// Negative offset
		CPU.resetCPU();
		offset = 0x80; // 0x80 is minus 128, 0xFF is -1
		addr = pc - 128;

		CPU.pc.setValue(pc);
		CPU.status.setNegative(true);
		CPU.doOp(0x30, new int[] { offset });

		assertEquals(addr, CPU.pc.getValue());

		// Carry set(i.e. don't branch)
		CPU.resetCPU();
		offset = 0x57;
		CPU.pc.setValue(pc);
		CPU.status.setNegative(false);
		CPU.doOp(0x30, new int[] { offset });

		assertEquals(pc, CPU.pc.getValue());
	}

	@Test
	public void branchNotEqual() {
		// 0xD0
		// Positive offset
		int pc = 0x0085;
		int offset = 0x78;
		int addr = pc + (offset);

		CPU.pc.setValue(pc);
		CPU.status.setZero(false);
		CPU.doOp(0xD0, new int[] { offset });

		assertEquals(addr, CPU.pc.getValue());

		// Negative offset
		CPU.resetCPU();
		offset = 0x80; // 0x80 is minus 128, 0xFF is -1
		addr = pc - 128;

		CPU.pc.setValue(pc);
		CPU.status.setZero(false);
		CPU.doOp(0xD0, new int[] { offset });

		assertEquals(addr, CPU.pc.getValue());

		// Carry set(i.e. don't branch)
		CPU.resetCPU();
		offset = 0x57;
		CPU.pc.setValue(pc);
		CPU.status.setZero(true);
		CPU.doOp(0xD0, new int[] { offset });

		assertEquals(pc, CPU.pc.getValue());
	}

	@Test
	public void branchOverflowClear() {
		// 0x50
		// Positive offset
		int pc = 0x0085;
		int offset = 0x78;
		int addr = pc + (offset);

		CPU.pc.setValue(pc);
		CPU.status.setOverflow(false);
		CPU.doOp(0x50, new int[] { offset });

		assertEquals(addr, CPU.pc.getValue());

		// Negative offset
		CPU.resetCPU();
		offset = 0x80; // 0x80 is minus 128, 0xFF is -1
		addr = pc - 128;

		CPU.pc.setValue(pc);
		CPU.status.setOverflow(false);
		CPU.doOp(0x50, new int[] { offset });

		assertEquals(addr, CPU.pc.getValue());

		// Carry set(i.e. don't branch)
		CPU.resetCPU();
		offset = 0x57;
		CPU.pc.setValue(pc);
		CPU.status.setOverflow(true);
		CPU.doOp(0x50, new int[] { offset });

		assertEquals(pc, CPU.pc.getValue());
	}

	@Test
	public void branchOverflowSet() {
		// 0x70
		// Positive offset
		int pc = 0x0085;
		int offset = 0x78;
		int addr = pc + (offset);

		CPU.pc.setValue(pc);
		CPU.status.setOverflow(true);
		CPU.doOp(0x70, new int[] { offset });

		assertEquals(addr, CPU.pc.getValue());

		// Negative offset
		CPU.resetCPU();
		offset = 0x80; // 0x80 is minus 128, 0xFF is -1
		addr = pc - 128;

		CPU.pc.setValue(pc);
		CPU.status.setOverflow(true);
		CPU.doOp(0x70, new int[] { offset });

		assertEquals(addr, CPU.pc.getValue());

		// Carry set(i.e. don't branch)
		CPU.resetCPU();
		offset = 0x57;
		CPU.pc.setValue(pc);
		CPU.status.setOverflow(false);
		CPU.doOp(0x70, new int[] { offset });

		assertEquals(pc, CPU.pc.getValue());
	}

	@Test
	public void branchPlus() {
		// 0x10
		// Positive offset
		int pc = 0x0085;
		int offset = 0x78;
		int addr = pc + (offset);

		CPU.pc.setValue(pc);
		CPU.status.setNegative(false);
		CPU.doOp(0x10, new int[] { offset });

		assertEquals(addr, CPU.pc.getValue());

		// Negative offset
		CPU.resetCPU();
		offset = 0x80; // 0x80 is minus 128, 0xFF is -1
		addr = pc - 128;

		CPU.pc.setValue(pc);
		CPU.status.setNegative(false);
		CPU.doOp(0x10, new int[] { offset });

		assertEquals(addr, CPU.pc.getValue());

		// Carry set(i.e. don't branch)
		CPU.resetCPU();
		offset = 0x57;
		CPU.pc.setValue(pc);
		CPU.status.setNegative(true);
		CPU.doOp(0x10, new int[] { offset });

		assertEquals(pc, CPU.pc.getValue());
	}
}
