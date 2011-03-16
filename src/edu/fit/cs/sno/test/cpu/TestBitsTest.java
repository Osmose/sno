package edu.fit.cs.sno.test.cpu;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.cpu.instructions.TestBits;
import edu.fit.cs.sno.snes.mem.LoROMMemory;
import edu.fit.cs.sno.util.Settings;

public class TestBitsTest {
	private static final int BASE_ADDR = 0x127D;

	@Before
	public void setUp() throws Exception {
		new TestBits(); // For 100% coverage
		Settings.init();
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
	}
	
	// 0x89
	@Test
	public void testImmediate() {
		int a   = 0x000F;
		int val = 0x0F0F;
		int args[];
				
		args = new int[]{val & 0xFF, (val >> 8) & 0xFF};
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.a.setValue(a);
		CPU.doOp(0x89, args);
		
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertFalse(CPU.status.isZero());
		
		a = 0x0F0F;
		val = 0xF0F0;
		args = new int[]{val & 0xFF, (val >> 8) & 0xFF};
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.a.setValue(a);
		CPU.doOp(0x89, args);
		
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertTrue(CPU.status.isZero());
		
		a = 0x7FFF;
		val = 0x800F;
		args = new int[]{val & 0xFF, (val >> 8) & 0xFF};
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.a.setValue(a);
		CPU.doOp(0x89, args);
		
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertFalse(CPU.status.isZero());
		
		a = 0x7FFF;
		val = 0x7F00;
		args = new int[]{val & 0xFF, (val >> 8) & 0xFF};
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.a.setValue(a);
		CPU.doOp(0x89, args);
		
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertFalse(CPU.status.isZero());
		
		a = 0xFFFF;
		val = 0x0000;
		args = new int[]{val & 0xFF, (val >> 8) & 0xFF};
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.a.setValue(a);
		CPU.doOp(0x89, args);
		
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x2C
	@Test
	public void testAbsolute() {
		int a   = 0x000F;
		int val = 0x0F0F;
		
		int bank= 0x05;
		int addr = BASE_ADDR;
		int args[] = new int[]{addr & 0xFF, (addr >> 8) & 0xFF};
		
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x2C, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertFalse(CPU.status.isOverflow());
		assertFalse(CPU.status.isZero());
		
		a = 0x0F0F;
		val = 0xF0F0;
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x2C, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertTrue(CPU.status.isOverflow());
		assertTrue(CPU.status.isZero());
		
		a = 0x7FFF;
		val = 0x800F;
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x2C, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isOverflow());
		assertFalse(CPU.status.isZero());
		
		a = 0x7FFF;
		val = 0x7F00;
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x2C, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isOverflow());
		assertFalse(CPU.status.isZero());
		
		a = 0xFFFF;
		val = 0x0000;
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x2C, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertFalse(CPU.status.isOverflow());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x24
	@Test
	public void testDP() {
		int a   = 0x000F;
		int val = 0x0F0F;
		
		int bank = 0;
		int offset = 0x8E;
		int addr = BASE_ADDR;
		int dp = addr - offset;
		int args[] = new int[]{offset};
		
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x24, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertFalse(CPU.status.isOverflow());
		assertFalse(CPU.status.isZero());
		
		a = 0x0F0F;
		val = 0xF0F0;
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x24, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertTrue(CPU.status.isOverflow());
		assertTrue(CPU.status.isZero());
		
		a = 0x7FFF;
		val = 0x800F;
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x24, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isOverflow());
		assertFalse(CPU.status.isZero());
		
		a = 0x7FFF;
		val = 0x7F00;
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x24, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isOverflow());
		assertFalse(CPU.status.isZero());
		
		a = 0xFFFF;
		val = 0x0000;
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x24, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertFalse(CPU.status.isOverflow());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x3C
	@Test
	public void testAbsoluteX() {
		int a   = 0x000F;
		int val = 0x0F0F;
		
		int bank= 0x05;
		int addr = BASE_ADDR;
		int x = 0x1234;
		int taddr = addr - x;
		int args[] = new int[]{taddr & 0xFF, (taddr >> 8) & 0xFF};
		
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.status.setIndexRegister(false);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x3C, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertFalse(CPU.status.isOverflow());
		assertFalse(CPU.status.isZero());
		
		a = 0x0F0F;
		val = 0xF0F0;
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.status.setIndexRegister(false);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x3C, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertTrue(CPU.status.isOverflow());
		assertTrue(CPU.status.isZero());
		
		a = 0x7FFF;
		val = 0x800F;
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.status.setIndexRegister(false);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x3C, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isOverflow());
		assertFalse(CPU.status.isZero());
		
		a = 0x7FFF;
		val = 0x7F00;
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.status.setIndexRegister(false);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x3C, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isOverflow());
		assertFalse(CPU.status.isZero());
		
		a = 0xFFFF;
		val = 0x0000;
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.status.setIndexRegister(false);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x3C, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertFalse(CPU.status.isOverflow());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x34
	@Test
	public void testDPX() {
		int a   = 0x000F;
		int val = 0x0F0F;
		
		int bank = 0;
		int offset = 0x8E;
		int addr = BASE_ADDR;
		int x = 0x1234;
		int dp = addr - offset - x;
		int args[] = new int[]{offset};
		
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.status.setIndexRegister(false);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x34, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertFalse(CPU.status.isOverflow());
		assertFalse(CPU.status.isZero());
		
		a = 0x0F0F;
		val = 0xF0F0;
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.status.setIndexRegister(false);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x34, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertTrue(CPU.status.isOverflow());
		assertTrue(CPU.status.isZero());
		
		a = 0x7FFF;
		val = 0x800F;
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.status.setIndexRegister(false);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x34, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isOverflow());
		assertFalse(CPU.status.isZero());
		
		a = 0x7FFF;
		val = 0x7F00;
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.status.setIndexRegister(false);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x34, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isOverflow());
		assertFalse(CPU.status.isZero());
		
		a = 0xFFFF;
		val = 0x0000;
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.status.setIndexRegister(false);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x34, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertFalse(CPU.status.isOverflow());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x0C
	@Test
	public void testSetAbsolute() {
		int a   = 0x000F;
		int val = 0x0F0F;
		
		int bank= 0x05;
		int addr = BASE_ADDR;
		int args[] = new int[]{addr & 0xFF, (addr >> 8) & 0xFF};
		
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x0C, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(a, CPU.a.getValue());
		assertEquals(a | val, Core.mem.get(Size.SHORT, bank, addr));
		assertFalse(CPU.status.isZero());
		
		a   = 0x0F0F;
		val = 0xF0F0;
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x0C, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(a, CPU.a.getValue());
		assertEquals(a | val, Core.mem.get(Size.SHORT, bank, addr));
		assertTrue(CPU.status.isZero());
		
		a = 0x7FFF;
		val = 0x800F;
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x0C, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(a, CPU.a.getValue());
		assertEquals(a | val, Core.mem.get(Size.SHORT, bank, addr));
		assertFalse(CPU.status.isZero());
		
		a = 0x7FFF;
		val = 0x7F00;
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x0C, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(a, CPU.a.getValue());
		assertEquals(a | val, Core.mem.get(Size.SHORT, bank, addr));
		assertFalse(CPU.status.isZero());
		
		a = 0xFFFF;
		val = 0x0000;
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x0C, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(a, CPU.a.getValue());
		assertEquals(a | val, Core.mem.get(Size.SHORT, bank, addr));
		assertTrue(CPU.status.isZero());
	}
	
	// 0x04
	@Test
	public void testSetDP() {
		int a   = 0x000F;
		int val = 0x0F0F;
		
		int bank = 0;
		int offset = 0x8E;
		int addr = BASE_ADDR;
		int dp = addr - offset;
		int args[] = new int[]{offset};
		
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x04, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(a, CPU.a.getValue());
		assertEquals(a | val, Core.mem.get(Size.SHORT, bank, addr));
		assertFalse(CPU.status.isZero());
		
		a   = 0x0F0F;
		val = 0xF0F0;
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x04, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(a, CPU.a.getValue());
		assertEquals(a | val, Core.mem.get(Size.SHORT, bank, addr));
		assertTrue(CPU.status.isZero());
		
		a = 0x7FFF;
		val = 0x800F;
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x04, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(a, CPU.a.getValue());
		assertEquals(a | val, Core.mem.get(Size.SHORT, bank, addr));
		assertFalse(CPU.status.isZero());
		
		a = 0x7FFF;
		val = 0x7F00;
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x04, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(a, CPU.a.getValue());
		assertEquals(a | val, Core.mem.get(Size.SHORT, bank, addr));
		assertFalse(CPU.status.isZero());
		
		a = 0xFFFF;
		val = 0x0000;
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.status.setMemoryAccess(false);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x04, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(a, CPU.a.getValue());
		assertEquals(a | val, Core.mem.get(Size.SHORT, bank, addr));
		assertTrue(CPU.status.isZero());
	}

	/**************************************************************************
	 * 8Bit Tests Below
	 *************************************************************************/
	// 0x89
	@Test
	public void testImmediate8Bit() {
		int a   = 0x0F;
		int val = 0x1F;
		int args[];
				
		args = new int[]{val & 0xFF};
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.doOp(0x89, args);
		
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertFalse(CPU.status.isZero());
		
		a = 0x0F;
		val = 0xF0;
		args = new int[]{val & 0xFF};
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.doOp(0x89, args);
		
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertTrue(CPU.status.isZero());
		
		a = 0x7F;
		val = 0x81;
		args = new int[]{val & 0xFF};
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.doOp(0x89, args);
		
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertFalse(CPU.status.isZero());
		
		a = 0x7F;
		val = 0x7D;
		args = new int[]{val & 0xFF};
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.doOp(0x89, args);
		
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertFalse(CPU.status.isZero());
		
		a = 0xFF;
		val = 0x00;
		args = new int[]{val & 0xFF};
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.doOp(0x89, args);
		
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x2C
	@Test
	public void testAbsolute8Bit() {
		int a   = 0x0F;
		int val = 0x1F;
		
		int bank= 0x05;
		int addr = BASE_ADDR;
		int args[] = new int[]{addr & 0xFF, (addr >> 8) & 0xFF};
		
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x2C, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertFalse(CPU.status.isOverflow());
		assertFalse(CPU.status.isZero());
		
		a = 0x0F;
		val = 0xF0;
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x2C, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertTrue(CPU.status.isOverflow());
		assertTrue(CPU.status.isZero());
		
		a = 0x7F;
		val = 0x81;
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x2C, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isOverflow());
		assertFalse(CPU.status.isZero());
		
		a = 0x7F;
		val = 0x7D;
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x2C, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isOverflow());
		assertFalse(CPU.status.isZero());
		
		a = 0xFF;
		val = 0x00;
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x2C, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertFalse(CPU.status.isOverflow());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x24
	@Test
	public void testDP8Bit() {
		int a   = 0x0F;
		int val = 0x1F;
		
		int bank = 0;
		int offset = 0x8E;
		int addr = BASE_ADDR;
		int dp = addr - offset;
		int args[] = new int[]{offset};
		
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x24, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertFalse(CPU.status.isOverflow());
		assertFalse(CPU.status.isZero());
		
		a = 0x0F;
		val = 0xF0;
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x24, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertTrue(CPU.status.isOverflow());
		assertTrue(CPU.status.isZero());
		
		a = 0x7F;
		val = 0x81;
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x24, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isOverflow());
		assertFalse(CPU.status.isZero());
		
		a = 0x7F;
		val = 0x7D;
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x24, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isOverflow());
		assertFalse(CPU.status.isZero());
		
		a = 0xFF;
		val = 0x00;
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x24, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertFalse(CPU.status.isOverflow());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x3C
	@Test
	public void testAbsoluteX8Bit() {
		int a   = 0x0F;
		int val = 0x1F;
		
		int bank= 0x05;
		int addr = BASE_ADDR;
		int x = 0xEC;
		int taddr = addr - x;
		int args[] = new int[]{taddr & 0xFF, (taddr >> 8) & 0xFF};
		
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x3C, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertFalse(CPU.status.isOverflow());
		assertFalse(CPU.status.isZero());
		
		a = 0x0F;
		val = 0xF0;
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x3C, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertTrue(CPU.status.isOverflow());
		assertTrue(CPU.status.isZero());
		
		a = 0x7F;
		val = 0x81;
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x3C, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isOverflow());
		assertFalse(CPU.status.isZero());
		
		a = 0x7F;
		val = 0x7D;
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x3C, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isOverflow());
		assertFalse(CPU.status.isZero());
		
		a = 0xFF;
		val = 0x00;
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x3C, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertFalse(CPU.status.isOverflow());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x34
	@Test
	public void testDPX8Bit() {
		int a   = 0x0F;
		int val = 0x1F;
		
		int bank = 0;
		int offset = 0x8E;
		int addr = BASE_ADDR;
		int x = 0xEC;
		int dp = addr - offset - x;
		int args[] = new int[]{offset};
		
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x34, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertFalse(CPU.status.isOverflow());
		assertFalse(CPU.status.isZero());
		
		a = 0x0F;
		val = 0xF0;
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x34, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertTrue(CPU.status.isOverflow());
		assertTrue(CPU.status.isZero());
		
		a = 0x7F;
		val = 0x81;
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x34, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isOverflow());
		assertFalse(CPU.status.isZero());
		
		a = 0x7F;
		val = 0x7D;
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x34, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isOverflow());
		assertFalse(CPU.status.isZero());
		
		a = 0xFF;
		val = 0x00;
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x34, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(val, CPU.dataReg.getValue());
		assertEquals(a, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertFalse(CPU.status.isOverflow());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x0C
	@Test
	public void testSetAbsolute8Bit() {
		int a   = 0x0F;
		int val = 0x1F;
		
		int bank= 0x05;
		int addr = BASE_ADDR;
		int args[] = new int[]{addr & 0xFF, (addr >> 8) & 0xFF};
		
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x0C, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(a, CPU.a.getValue());
		assertEquals(a | val, Core.mem.get(Size.BYTE, bank, addr));
		assertFalse(CPU.status.isZero());
		
		a   = 0x0F;
		val = 0xF0;
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x0C, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(a, CPU.a.getValue());
		assertEquals(a | val, Core.mem.get(Size.BYTE, bank, addr));
		assertTrue(CPU.status.isZero());
		
		a = 0x7F;
		val = 0x81;
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x0C, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(a, CPU.a.getValue());
		assertEquals(a | val, Core.mem.get(Size.BYTE, bank, addr));
		assertFalse(CPU.status.isZero());
		
		a = 0x7F;
		val = 0x7D;
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x0C, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(a, CPU.a.getValue());
		assertEquals(a | val, Core.mem.get(Size.BYTE, bank, addr));
		assertFalse(CPU.status.isZero());
		
		a = 0xFF;
		val = 0x00;
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x0C, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(a, CPU.a.getValue());
		assertEquals(a | val, Core.mem.get(Size.BYTE, bank, addr));
		assertTrue(CPU.status.isZero());
	}
	
	// 0x04
	@Test
	public void testSetDP8Bit() {
		int a   = 0x0F;
		int val = 0x1F;
		
		int bank = 0;
		int offset = 0x8E;
		int addr = BASE_ADDR;
		int dp = addr - offset;
		int args[] = new int[]{offset};
		
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x04, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(a, CPU.a.getValue());
		assertEquals(a | val, Core.mem.get(Size.BYTE, bank, addr));
		assertFalse(CPU.status.isZero());
		
		a   = 0x0F;
		val = 0xF0;
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x04, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(a, CPU.a.getValue());
		assertEquals(a | val, Core.mem.get(Size.BYTE, bank, addr));
		assertTrue(CPU.status.isZero());
		
		a = 0x7F;
		val = 0x81;
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x04, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(a, CPU.a.getValue());
		assertEquals(a | val, Core.mem.get(Size.BYTE, bank, addr));
		assertFalse(CPU.status.isZero());
		
		a = 0x7F;
		val = 0x7D;
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x04, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(a, CPU.a.getValue());
		assertEquals(a | val, Core.mem.get(Size.BYTE, bank, addr));
		assertFalse(CPU.status.isZero());
		
		a = 0xFF;
		val = 0x00;
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.resetCPU();
		CPU.status.setValue(0xFF);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x04, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(a, CPU.a.getValue());
		assertEquals(a | val, Core.mem.get(Size.BYTE, bank, addr));
		assertTrue(CPU.status.isZero());
	}
}
