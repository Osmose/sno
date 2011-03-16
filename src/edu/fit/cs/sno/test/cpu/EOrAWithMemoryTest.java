package edu.fit.cs.sno.test.cpu;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.cpu.instructions.EOrAWithMemory;
import edu.fit.cs.sno.snes.mem.LoROMMemory;
import edu.fit.cs.sno.util.Settings;

public class EOrAWithMemoryTest {
	
	private static final int BASEADDR = 0x18E4;
	private static final int A8BIT    = 0x7F;
	private static final int VAL8BIT  = 0x8F;
	private static final int R8BIT    = 0xF0;
	
	private static final int A16BIT   = 0x7FFF;
	private static final int VAL16BIT = 0x80F0;
	private static final int R16BIT   = 0xFF0F;

	@Before
	public void setUp() throws Exception {
		new EOrAWithMemory(); // For 100% coverage
		Settings.init();
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
	}

	// 0x41
	@Test
	public void eorAMemDPIndirectX16Bit() {
		int addr = BASEADDR;
		int iaddr = 0x1378;
		int x = 0x1024;
		int arg = 0x71;
		int dp = iaddr -x - arg;
		int bank = 5;
		
		int a = A16BIT;
		int val = VAL16BIT;
		
		Core.mem.set(Size.BYTE, bank, iaddr-1, 1);
		Core.mem.set(Size.SHORT, bank, iaddr, addr);
		Core.mem.set(Size.BYTE, bank, iaddr+2, 1);
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x41, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(R16BIT, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		val = 0x0000;
		CPU.resetCPU();
		CPU.x.setValue(x);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.a.setValue(0x0000);
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.doOp(0x41, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(0x0000, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x41
	@Test
	public void eorAMemDPIndirectX8Bit() {
		int addr = BASEADDR;
		int iaddr = 0x1378;
		int x = 0x1024;
		int arg = 0x71;
		int dp = iaddr -x - arg;
		int bank = 5;
		
		int a = A8BIT;
		int val = VAL8BIT;
		
		Core.mem.set(Size.BYTE, bank, iaddr-1, 1);
		Core.mem.set(Size.SHORT, bank, iaddr, addr);
		Core.mem.set(Size.BYTE, bank, iaddr+2, 1);
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		
		CPU.doOp(0x41, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(R8BIT, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		val = 0x00;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.x.setValue(x);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.a.setValue(0x00);
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.doOp(0x41, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(0x00, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x43
	@Test
	public void eorAMemStackRelative16Bit() {
		int addr = BASEADDR;
		int arg = 0x71;
		int sp = addr - arg;
		int bank = 5;
		
		int a = A16BIT;
		int val = VAL16BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.a.setValue(a);
		CPU.sp.setValue(sp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x43, new int[]{arg});
		
		assertEquals(R16BIT, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		val = 0x0000;
		CPU.resetCPU();
		CPU.sp.setValue(sp);
		CPU.dbr.setValue(bank);
		CPU.a.setValue(0x0000);
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.doOp(0x43, new int[]{arg});
		assertEquals(0x0000, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}
	// 0x43
	@Test
	public void eorAMemStackRelative8Bit() {
		int addr = BASEADDR;
		int arg = 0x71;
		int sp = addr - arg;
		int bank = 5;
		
		int a = A8BIT;
		int val = VAL8BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.sp.setValue(sp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x43, new int[]{arg});
		
		assertEquals(R8BIT, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		val = 0x00;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.sp.setValue(sp);
		CPU.dbr.setValue(bank);
		CPU.a.setValue(0x00);
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.doOp(0x43, new int[]{arg});
		assertEquals(0x00, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}
	

	// 0x45
	@Test
	public void eorAMemDP16Bit() {
		int addr = BASEADDR;
		int arg = 0x71;
		int dp = addr - arg;
		int bank = 5;
		
		int a = A16BIT;
		int val = VAL16BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x45, new int[]{arg});
		
		assertEquals(R16BIT, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		val = 0x0000;
		CPU.resetCPU();
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.a.setValue(0x0000);
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.doOp(0x45, new int[]{arg});
		assertEquals(0x0000, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}
	// 0x45
	@Test
	public void eorAMemDP8Bit() {
		int addr = BASEADDR;
		int arg = 0x71;
		int dp = addr - arg;
		int bank = 5;
		
		int a = A8BIT;
		int val = VAL8BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x45, new int[]{arg});
		
		assertEquals(R8BIT, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		val = 0x00;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.a.setValue(0x00);
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.doOp(0x45, new int[]{arg});
		assertEquals(0x00, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x47
	@Test
	public void eorAMemDPIndirectLong16bit() {
		int addr = BASEADDR;
		int iaddr = 0x1378;
		int arg = 0x71;
		int dp = iaddr - arg;
		int ibank = 0;
		int bank = 5;
		
		int a = A16BIT;
		int val = VAL16BIT;
		
		Core.mem.set(Size.BYTE, ibank, iaddr-1, 1);
		Core.mem.set(Size.SHORT, ibank, iaddr, addr);
		Core.mem.set(Size.BYTE, ibank, iaddr+2, bank);
		Core.mem.set(Size.BYTE, ibank, iaddr+3, 1);
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(ibank);
		
		CPU.doOp(0x47, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(R16BIT, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		val = 0x0000;
		CPU.resetCPU();
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(ibank);
		CPU.a.setValue(0x0000);
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.doOp(0x47, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(0x0000, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}
	// 0x47
	@Test
	public void eorAMemDPIndirectLong8bit() {
		int addr = BASEADDR;
		int iaddr = 0x1378;
		int arg = 0x71;
		int dp = iaddr - arg;
		int ibank = 0;
		int bank = 5;
		
		int a = A8BIT;
		int val = VAL8BIT;
		
		Core.mem.set(Size.BYTE, ibank, iaddr-1, 1);
		Core.mem.set(Size.SHORT, ibank, iaddr, addr);
		Core.mem.set(Size.BYTE, ibank, iaddr+2, bank);
		Core.mem.set(Size.BYTE, ibank, iaddr+3, 1);
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(ibank);
		
		CPU.doOp(0x47, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(R8BIT, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		val = 0x00;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(ibank);
		CPU.a.setValue(0x00);
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.doOp(0x47, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(0x00, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x49
	@Test
	public void eorAMemImmediate16Bit() {
		int[] args = new int[]{VAL16BIT & 0xFF, (VAL16BIT >>8) & 0xFF};
		int a = A16BIT;
		
		CPU.a.setValue(a);
		CPU.doOp(0x49, args);
		
		assertEquals(R16BIT, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		args = new int[]{0x00,0x00};
		CPU.resetCPU();
		CPU.a.setValue(0x0000);
		CPU.doOp(0x49, args);
		
		assertEquals(0x0000, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x49
	@Test
	public void eorAMemImmediate8Bit() {
		int[] args = new int[]{VAL8BIT};
		int a = A8BIT;
		
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.doOp(0x49, args);
		
		assertEquals(R8BIT, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		args = new int[]{0x00};
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(0x00);
		CPU.doOp(0x49, args);
		
		assertEquals(0x00, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x4D
	@Test
	public void eorAMemAbsolute16Bit() {
		int addr = BASEADDR;
		int args[] = new int[]{addr & 0xFF, (addr >> 8) & 0xFF};
		int bank = 5;
		
		int a = A16BIT;
		int val = VAL16BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x4D, args);
		
		assertEquals(R16BIT, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		val = 0x0000;
		CPU.resetCPU();
		CPU.dbr.setValue(bank);
		CPU.a.setValue(0x0000);
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.doOp(0x4D, args);
		assertEquals(0x0000, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x4D
	@Test
	public void eorAMemAbsolute8Bit() {
		int addr = BASEADDR;
		int args[] = new int[]{addr & 0xFF, (addr >> 8) & 0xFF};
		int bank = 5;
		
		int a = A8BIT;
		int val = VAL8BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x4D, args);
		
		assertEquals(R8BIT, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		val = 0x00;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.dbr.setValue(bank);
		CPU.a.setValue(0x00);
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.doOp(0x4D, args);
		assertEquals(0x00, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x4F
	@Test
	public void eorAMemAbsoluteLong16Bit() {
		int addr = BASEADDR;
		int bank = 5;
		int args[] = new int[]{addr & 0xFF, (addr >> 8) & 0xFF, bank};
		
		int a = A16BIT;
		int val = VAL16BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.a.setValue(a);
		CPU.dbr.setValue(0);
		CPU.doOp(0x4F, args);
		
		assertEquals(R16BIT, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		val = 0x0000;
		CPU.resetCPU();
		CPU.dbr.setValue(0);
		CPU.a.setValue(0x0000);
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.doOp(0x4F, args);
		assertEquals(0x0000, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}

	// 0x4F
	@Test
	public void eorAMemAbsoluteLong8Bit() {
		int addr = BASEADDR;
		int bank = 5;
		int args[] = new int[]{addr & 0xFF, (addr >> 8) & 0xFF, bank};
		
		int a = A8BIT;
		int val = VAL8BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.dbr.setValue(0);
		CPU.doOp(0x4F, args);
		
		assertEquals(R8BIT, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		val = 0x00;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.dbr.setValue(0);
		CPU.a.setValue(0x00);
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.doOp(0x4F, args);
		assertEquals(0x00, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x51
	@Test
	public void eorAMemDPIndirectY16Bit() {
		int addr = BASEADDR;
		int iaddr = 0x1378;
		int arg = 0x71;
		int dp = iaddr - arg;
		int bank = 5;
		int ibank = 0;
		int y = 0x123;
		
		int a = A16BIT;
		int val = VAL16BIT;
		
		Core.mem.set(Size.BYTE, ibank, iaddr-1, 1);
		Core.mem.set(Size.SHORT, ibank, iaddr, addr - y);
		Core.mem.set(Size.BYTE, ibank, iaddr+2, 1);
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		
		CPU.doOp(0x51, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(R16BIT, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		val = 0x0000;
		CPU.resetCPU();
		CPU.y.setValue(y);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.a.setValue(0x0000);
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.doOp(0x51, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(0x0000, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x51
	@Test
	public void eorAMemDPIndirectY8Bit() {
		int addr = BASEADDR;
		int iaddr = 0x1378;
		int arg = 0x71;
		int dp = iaddr - arg;
		int bank = 5;
		int ibank = 0;
		int y = 0x123;
		
		int a = A8BIT;
		int val = VAL8BIT;
		
		Core.mem.set(Size.BYTE, ibank, iaddr-1, 1);
		Core.mem.set(Size.SHORT, ibank, iaddr, addr - y);
		Core.mem.set(Size.BYTE, ibank, iaddr+2, 1);
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		
		CPU.doOp(0x51, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(R8BIT, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		val = 0x00;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.y.setValue(y);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.a.setValue(0x00);
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.doOp(0x51, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(0x00, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x52
	@Test
	public void eorAMemDPIndirect16Bit() {
		int addr = BASEADDR;
		int iaddr = 0x1378;
		int arg = 0x71;
		int dp = iaddr - arg;
		int ibank = 0;
		int bank = 5;
		
		int a = A16BIT;
		int val = VAL16BIT;
		
		Core.mem.set(Size.BYTE, ibank, iaddr-1, 1);
		Core.mem.set(Size.SHORT, ibank, iaddr, addr);
		Core.mem.set(Size.BYTE, ibank, iaddr+2, 1);
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		
		CPU.doOp(0x52, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(R16BIT, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		val = 0x0000;
		CPU.resetCPU();
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.a.setValue(0x0000);
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.doOp(0x52, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(0x0000, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}

	// 0x52
	@Test
	public void eorAMemDPIndirect8Bit() {
		int addr = BASEADDR;
		int iaddr = 0x1378;
		int arg = 0x71;
		int dp = iaddr - arg;
		int bank = 0;
		
		int a = A8BIT;
		int val = VAL8BIT;
		
		Core.mem.set(Size.BYTE, bank, iaddr-1, 1);
		Core.mem.set(Size.SHORT, bank, iaddr, addr);
		Core.mem.set(Size.BYTE, bank, iaddr+2, 1);
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		
		CPU.doOp(0x52, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(R8BIT, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		val = 0x00;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.a.setValue(0x00);
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.doOp(0x52, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(0x00, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x53
	@Test
	public void eorAMemSRIndirectY16Bit() {
		int addr = BASEADDR;
		int iaddr = 0x1378;
		int arg = 0x71;
		int sp = iaddr - arg;
		int ibank = 0;
		int bank = 5;
		int y = 20;
		
		int a = A16BIT;
		int val = VAL16BIT;
		
		Core.mem.set(Size.BYTE, ibank, iaddr-1, 1);
		Core.mem.set(Size.SHORT, ibank, iaddr, addr - y);
		Core.mem.set(Size.BYTE, ibank, iaddr+2, 1);
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.sp.setValue(sp);
		CPU.dbr.setValue(bank);
		
		CPU.doOp(0x53, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(R16BIT, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		val = 0x0000;
		CPU.resetCPU();
		CPU.sp.setValue(sp);
		CPU.y.setValue(y);
		CPU.dbr.setValue(bank);
		CPU.a.setValue(0x0000);
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.doOp(0x53, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(0x0000, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x53
	@Test
	public void eorAMemSRIndirectY8Bit() {
		int addr = BASEADDR;
		int iaddr = 0x1378;
		int arg = 0x71;
		int sp = iaddr - arg;
		int ibank = 0;
		int bank = 5;
		int y = 20;
		
		int a = A8BIT;
		int val = VAL8BIT;
		
		Core.mem.set(Size.BYTE, ibank, iaddr-1, 1);
		Core.mem.set(Size.SHORT, ibank, iaddr, addr - y);
		Core.mem.set(Size.BYTE, ibank, iaddr+2, 1);
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.sp.setValue(sp);
		CPU.dbr.setValue(bank);
		
		CPU.doOp(0x53, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(R8BIT, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		val = 0x00;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.sp.setValue(sp);
		CPU.y.setValue(y);
		CPU.dbr.setValue(bank);
		CPU.a.setValue(0x00);
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.doOp(0x53, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(0x00, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}

	// 0x55
	@Test
	public void eorAMemDPIndexedX16Bit() {
		int addr = BASEADDR;
		int arg = 0x71;
		int x = 0x10;
		int dp = addr - arg - x;
		int bank = 5;
		
		int a = A16BIT;
		int val = VAL16BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.x.setValue(x);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x55, new int[]{arg});
		
		assertEquals(R16BIT, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		val = 0x0000;
		CPU.resetCPU();
		CPU.x.setValue(x);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.a.setValue(0x0000);
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.doOp(0x55, new int[]{arg});
		assertEquals(0x0000, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x55
	@Test
	public void eorAMemDPIndexedX8Bit() {
		int addr = BASEADDR;
		int arg = 0x71;
		int x = 0x10;
		int dp = addr - arg - x;
		int bank = 5;
		
		int a = A8BIT;
		int val = VAL8BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.status.setMemoryAccess(true);
		CPU.x.setValue(x);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x55, new int[]{arg});
		
		assertEquals(R8BIT, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		val = 0x00;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.x.setValue(x);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.a.setValue(0x00);
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.doOp(0x55, new int[]{arg});
		
		assertEquals(0x00, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x57
	@Test
	public void eorAMemDPIndirectLongY16Bit() {
		int addr = BASEADDR;
		int iaddr = 0x1378;
		int arg = 0x71;
		int dp = iaddr - arg;
		int bank = 5;
		int ibank = 0;
		int y = 0x44;
		
		int a = A16BIT;
		int val = VAL16BIT;
		
		Core.mem.set(Size.BYTE, ibank, iaddr-1, 1);
		Core.mem.set(Size.SHORT, ibank, iaddr, addr - y);
		Core.mem.set(Size.BYTE, ibank, iaddr+2, bank);
		Core.mem.set(Size.BYTE, ibank, iaddr+3, 1);
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(ibank);
		CPU.doOp(0x57, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(R16BIT, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		val = 0x0000;
		CPU.resetCPU();
		CPU.y.setValue(y);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(ibank);
		CPU.a.setValue(0x0000);
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.doOp(0x57, new int[]{arg});
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(0x0000, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x57
	@Test
	public void eorAMemDPIndirectLongY8Bit() {
		int addr = BASEADDR;
		int iaddr = 0x1378;
		int arg = 0x71;
		int dp = iaddr - arg;
		int bank = 5;
		int ibank = 0;
		int y = 0x44;
		
		int a = A8BIT;
		int val = VAL8BIT;
		
		Core.mem.set(Size.BYTE, ibank, iaddr-1, 1);
		Core.mem.set(Size.SHORT, ibank, iaddr, addr - y);
		Core.mem.set(Size.BYTE, ibank, iaddr+2, bank);
		Core.mem.set(Size.BYTE, ibank, iaddr+3, 1);
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(ibank);
		CPU.status.setMemoryAccess(true);
		
		CPU.doOp(0x57, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(R8BIT, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		val = 0x00;
		CPU.resetCPU();
		CPU.y.setValue(y);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(ibank);
		CPU.a.setValue(0x00);
		CPU.status.setMemoryAccess(true);
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.doOp(0x57, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(0x00, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}

	// 0x59
	@Test
	public void eorAMemAbsoluteY16Bit() {
		int addr = BASEADDR;
		
		int y = 0x1010;
		int taddr = addr - y;
		int args[] = new int[]{taddr & 0xFF, (taddr >> 8) & 0xFF};
		int bank = 5;
		
		
		int a = A16BIT;
		int val = VAL16BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x59, args);
		
		assertEquals(R16BIT, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		val = 0x0000;
		CPU.resetCPU();
		CPU.dbr.setValue(bank);
		CPU.a.setValue(0x0000);
		CPU.y.setValue(y);
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.doOp(0x59, args);
		
		assertEquals(0x0000, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x59
	@Test
	public void eorAMemAbsoluteY8Bit() {
		int addr = BASEADDR;
		
		int y = 0x10;
		int taddr = addr - y;
		int args[] = new int[]{taddr & 0xFF, (taddr >> 8) & 0xFF};
		int bank = 5;
		
		
		int a = A8BIT;
		int val = VAL8BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x59, args);
		
		assertEquals(R8BIT, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());

		val = 0x00;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.dbr.setValue(bank);
		CPU.a.setValue(0x00);
		CPU.y.setValue(y);
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.doOp(0x59, args);
		
		assertEquals(0x00, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x5D
	@Test
	public void eorAMemAbsoluteX16Bit() {
		int addr = BASEADDR;
		
		int x = 0x1010;
		int taddr = addr - x;
		int args[] = new int[]{taddr & 0xFF, (taddr >> 8) & 0xFF};
		int bank = 5;
		
		
		int a = A16BIT;
		int val = VAL16BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x5D, args);
		
		assertEquals(R16BIT, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		val = 0x0000;
		CPU.resetCPU();
		CPU.dbr.setValue(bank);
		CPU.a.setValue(0x0000);
		CPU.x.setValue(x);
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.doOp(0x5D, args);
		
		assertEquals(0x0000, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x5D
	@Test
	public void eorAMemAbsoluteX8Bit() {
		int addr = BASEADDR;
		
		int x = 0x10;
		int taddr = addr - x;
		int args[] = new int[]{taddr & 0xFF, (taddr >> 8) & 0xFF};
		int bank = 5;
		
		int a = A8BIT;
		int val = VAL8BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x5D, args);
		
		assertEquals(R8BIT, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());

		val = 0x00;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.dbr.setValue(bank);
		CPU.a.setValue(0x00);
		CPU.x.setValue(x);
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.doOp(0x5D, args);
		
		assertEquals(0x00, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x5F
	@Test
	public void eorAMemAbsoluteLongX16Bit() {
		int addr = BASEADDR;
		int x = 0x10;
		int taddr = addr - x;
		int bank = 5;
		int args[] = new int[]{taddr & 0xFF, (taddr >> 8) & 0xFF, bank};
		
		int a = A16BIT;
		int val = VAL16BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x5F, args);
		
		assertEquals(R16BIT, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		val = 0x0000;
		CPU.resetCPU();
		CPU.dbr.setValue(bank);
		CPU.a.setValue(0x0000);
		CPU.x.setValue(x);
		Core.mem.set(Size.SHORT, bank, addr, val);
		
		CPU.doOp(0x5F, args);
		
		assertEquals(0x0000, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}
	
	// 0x5F
	@Test
	public void eorAMemAbsoluteLongX8Bit() {
		int addr = BASEADDR;
		int x = 0x10;
		int taddr = addr - x;
		int bank = 5;
		int args[] = new int[]{taddr & 0xFF, (taddr >> 8) & 0xFF, bank};
		
		int a = A8BIT;
		int val = VAL8BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x5F, args);
		
		assertEquals(R8BIT, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		val = 0x00;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.dbr.setValue(bank);
		CPU.a.setValue(0x00);
		CPU.x.setValue(x);
		Core.mem.set(Size.BYTE, bank, addr, val);
		
		CPU.doOp(0x5F, args);
		
		assertEquals(0x00, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}
}
