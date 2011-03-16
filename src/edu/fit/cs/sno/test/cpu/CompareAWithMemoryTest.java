package edu.fit.cs.sno.test.cpu;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.cpu.instructions.CompareAWithMemory;
import edu.fit.cs.sno.snes.mem.LoROMMemory;
import edu.fit.cs.sno.util.Settings;

public class CompareAWithMemoryTest {


	private static final int BASEADDR = 0x1BEF;
	private static final int VAL8BIT = 0x78;
	private static final int VAL16BIT = 0x7FF8;

	@Before
	public void setUp() throws Exception {
		new CompareAWithMemory(); // For 100% coverage
		Settings.init();
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
	}

	public void verify16Bit(int a, int val, int flags) {
		assertEquals(a,    CPU.a.getValue());
		assertEquals(val,  CPU.dataReg.getValue());
		
		int cmp = (a & 0xFFFF) - (val &0xFFFF);
		assertEquals((cmp < 0), ((flags & 0x80) == 0x80));  // Test Negative
		assertEquals((cmp == 0), ((flags & 0x02) == 0x02)); // Test Zero
		assertEquals((cmp >= 0), ((flags & 0x01) == 0x01)); // Test Carry
	}
	
	public void verify8Bit(int a, int val, int flags) {
		assertEquals(a,    CPU.a.getValue());
		assertEquals(val,  CPU.dataReg.getValue());
		
		int cmp = (a & 0xFF) - (val &0xFF);
		assertEquals((cmp < 0), ((flags & 0x80) == 0x80));  // Test Negative
		assertEquals((cmp == 0), ((flags & 0x02) == 0x02)); // Test Zero
		assertEquals((cmp >= 0), ((flags & 0x01) == 0x01)); // Test Carry
	}
	
	
	// 0xC1
	@Test
	public void cmpADPIndirectX16Bit() {
		int addr = BASEADDR;
		int iaddr = 0x1378;
		int x = 0x1024;
		int arg = 0x71;
		int dp = iaddr -x - arg;
		int bank = 5;
		
		int a = VAL16BIT;
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
		CPU.doOp(0xC1, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xC1, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xC1, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
	}
	
	// 0xC1
	@Test
	public void cmpADPIndirectX8Bit() {
		int addr = BASEADDR;
		int iaddr = 0x1378;
		int x = 0x1024;
		int arg = 0x71;
		int dp = iaddr -x - arg;
		int bank = 5;
		
		int a = VAL8BIT;
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
		
		CPU.doOp(0xC1, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xC1, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xC1, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());

	}
	
	// 0xC3
	@Test
	public void cmpAStackRelative16Bit() {
		int addr = BASEADDR;
		int arg = 0x71;
		int sp = addr - arg;
		int bank = 0; // must be zero
		
		int a = VAL16BIT;
		int val = VAL16BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.a.setValue(a);
		CPU.sp.setValue(sp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xC3, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.sp.setValue(sp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xC3, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.sp.setValue(sp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xC3, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
	}
	// 0xC3
	@Test
	public void cmpAMemStackRelative8Bit() {
		int addr = BASEADDR;
		int arg = 0x71;
		int sp = addr - arg;
		int bank = 0; // must be zero
		
		int a = VAL8BIT;
		int val = VAL8BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.sp.setValue(sp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xC3, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.sp.setValue(sp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xC3, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.sp.setValue(sp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xC3, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
	}
	

	// 0xC5
	@Test
	public void cmpAMemDP16Bit() {
		int addr = BASEADDR;
		int arg = 0x71;
		int dp = addr - arg;
		int bank = 0; // must be zero
		
		int a = VAL16BIT;
		int val = VAL16BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xC5, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xC5, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xC5, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
	}
	// 0xC5
	@Test
	public void cmpAMemDP8Bit() {
		int addr = BASEADDR;
		int arg = 0x71;
		int dp = addr - arg;
		int bank = 0; // must be zero
		
		int a = VAL8BIT;
		int val = VAL8BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xC5, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xC5, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xC5, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
	}
	
	// 0xC7
	@Test
	public void cmpAMemDPIndirectLong16bit() {
		int addr = BASEADDR;
		int iaddr = 0x1378;
		int arg = 0x71;
		int dp = iaddr - arg;
		int ibank = 0;
		int bank = 5;
		
		int a = VAL16BIT;
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
		CPU.doOp(0xC7, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xC7, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xC7, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
	}
	// 0xC7
	@Test
	public void cmpAMemDPIndirectLong8bit() {
		int addr = BASEADDR;
		int iaddr = 0x1378;
		int arg = 0x71;
		int dp = iaddr - arg;
		int ibank = 0;
		int bank = 5;
		
		int a = VAL8BIT;
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
		
		CPU.doOp(0xC7, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xC7, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xC7, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
	}
	
	// 0xC9
	@Test
	public void cmpAMemImmediate16Bit() {
		int[] args = new int[]{VAL16BIT & 0xFF, (VAL16BIT >>8) & 0xFF};
		int a = VAL16BIT;
		
		CPU.a.setValue(a);
		CPU.doOp(0xC9, args);
		
		verify16Bit(a, VAL16BIT, CPU.status.getValue());
		
		a = VAL16BIT - 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.doOp(0xC9, args);
		
		verify16Bit(a, VAL16BIT, CPU.status.getValue());
		
		
		a = VAL16BIT + 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.doOp(0xC9, args);
		
		verify16Bit(a, VAL16BIT, CPU.status.getValue());
	}
	
	// 0xC9
	@Test
	public void cmpAMemImmediate8Bit() {
		int[] args = new int[]{VAL8BIT};
		int a = VAL8BIT;
		
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.doOp(0xC9, args);
		
		
		a = VAL8BIT - 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.doOp(0xC9, args);
		
		verify8Bit(a, VAL8BIT, CPU.status.getValue());
		
		
		a = VAL8BIT + 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.doOp(0xC9, args);
		
		verify8Bit(a, VAL8BIT, CPU.status.getValue());
	}
	
	// 0xCD
	@Test
	public void cmpAMemAbsolute16Bit() {
		int addr = BASEADDR;
		int args[] = new int[]{addr & 0xFF, (addr >> 8) & 0xFF};
		int bank = 5;
		
		int a = VAL16BIT;
		int val = VAL16BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xCD, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xCD, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xCD, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
	}
	
	// 0xCD
	@Test
	public void cmpAMemAbsolute8Bit() {
		int addr = BASEADDR;
		int args[] = new int[]{addr & 0xFF, (addr >> 8) & 0xFF};
		int bank = 5;
		
		int a = VAL8BIT;
		int val = VAL8BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xCD, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xCD, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xCD, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
	}
	
	// 0xCF
	@Test
	public void cmpAMemAbsoluteLong16Bit() {
		int addr = BASEADDR;
		int bank = 5;
		int args[] = new int[]{addr & 0xFF, (addr >> 8) & 0xFF, bank};
		
		int a = VAL16BIT;
		int val = VAL16BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.a.setValue(a);
		CPU.dbr.setValue(0);
		CPU.doOp(0xCF, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xCF, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xCF, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
	}

	// 0xCF
	@Test
	public void cmpAMemAbsoluteLong8Bit() {
		int addr = BASEADDR;
		int bank = 5;
		int args[] = new int[]{addr & 0xFF, (addr >> 8) & 0xFF, bank};
		
		int a = VAL8BIT;
		int val = VAL8BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.dbr.setValue(0);
		CPU.doOp(0xCF, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xCF, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xCF, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
	}
	
	// 0xD1
	@Test
	public void cmpAMemDPIndirectY16Bit() {
		int addr = BASEADDR;
		int iaddr = 0x1378;
		int arg = 0x71;
		int dp = iaddr - arg;
		int bank = 5;
		int ibank = 0;
		int y = 0x123;
		
		int a = VAL16BIT;
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
		CPU.doOp(0xD1, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xD1, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xD1, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
	}
	
	// 0xD1
	@Test
	public void cmpAMemDPIndirectY8Bit() {
		int addr = BASEADDR;
		int iaddr = 0x1378;
		int arg = 0x71;
		int dp = iaddr - arg;
		int bank = 5;
		int ibank = 0;
		int y = 0x44;
		
		int a = VAL8BIT;
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
		
		CPU.doOp(0xD1, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xD1, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xD1, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
	}
	
	// 0xD2
	@Test
	public void cmpAMemDPIndirect16Bit() {
		int addr = BASEADDR;
		int iaddr = 0x1378;
		int arg = 0x71;
		int dp = iaddr - arg;
		int ibank = 0;
		int bank = 5;
		
		int a = VAL16BIT;
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
		
		CPU.doOp(0xD2, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xD2, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xD2, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
	}

	// 0xD2
	@Test
	public void cmpAMemDPIndirect8Bit() {
		int addr = BASEADDR;
		int iaddr = 0x1378;
		int arg = 0x71;
		int dp = iaddr - arg;
		int bank = 0;
		
		int a = VAL8BIT;
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
		
		CPU.doOp(0xD2, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xD2, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xD2, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
	}
	
	// 0xD3
	@Test
	public void cmpAMemSRIndirectY16Bit() {
		int addr = BASEADDR;
		int iaddr = 0x1378;
		int arg = 0x71;
		int sp = iaddr - arg;
		int ibank = 0;
		int bank = 5;
		int y = 20;
		
		int a = VAL16BIT;
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
		
		CPU.doOp(0xD3, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.sp.setValue(sp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xD3, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.sp.setValue(sp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xD3, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
	}
	
	// 0xD3
	@Test
	public void cmpAMemSRIndirectY8Bit() {
		int addr = BASEADDR;
		int iaddr = 0x1378;
		int arg = 0x71;
		int sp = iaddr - arg;
		int ibank = 0;
		int bank = 5;
		int y = 20;
		
		int a = VAL8BIT;
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
		
		CPU.doOp(0xD3, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.sp.setValue(sp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xD3, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.sp.setValue(sp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xD3, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
	}

	// 0xD5
	@Test
	public void cmpAMemDPIndexedX16Bit() {
		int addr = BASEADDR;
		int arg = 0x71;
		int x = 0x10;
		int dp = addr - arg - x;
		int bank = 0; // must be zero
		
		int a = VAL16BIT;
		int val = VAL16BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.x.setValue(x);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xD5, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xD5, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xD5, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
	}
	
	// 0xD5
	@Test
	public void cmpAMemDPIndexedX8Bit() {
		int addr = BASEADDR;
		int arg = 0x71;
		int x = 0x10;
		int dp = addr - arg - x;
		int bank = 0; // Must be zero
		
		int a = VAL8BIT;
		int val = VAL8BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.status.setMemoryAccess(true);
		CPU.x.setValue(x);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xD5, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xD5, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xD5, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
	}
	
	// 0xD7
	@Test
	public void cmpAMemDPIndirectLongY16Bit() {
		int addr = BASEADDR;
		int iaddr = 0x1378;
		int arg = 0x71;
		int dp = iaddr - arg;
		int bank = 5;
		int ibank = 0;
		int y = 0x44;
		
		int a = VAL16BIT;
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
		CPU.doOp(0xD7, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xD7, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xD7, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
	}
	
	// 0xD7
	@Test
	public void cmpAMemDPIndirectLongY8Bit() {
		int addr = BASEADDR;
		int iaddr = 0x1378;
		int arg = 0x71;
		int dp = iaddr - arg;
		int bank = 5;
		int ibank = 0;
		int y = 0x44;
		
		int a = VAL8BIT;
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
		
		CPU.doOp(0xD7, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xD7, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xD7, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
	}

	// 0xD9
	@Test
	public void cmpAMemAbsoluteY16Bit() {
		int addr = BASEADDR;
		
		int y = 0x1010;
		int taddr = addr - y;
		int args[] = new int[]{taddr & 0xFF, (taddr >> 8) & 0xFF};
		int bank = 5;
		
		
		int a = VAL16BIT;
		int val = VAL16BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xD9, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xD9, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xD9, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
	}
	
	// 0xD9
	@Test
	public void cmpAMemAbsoluteY8Bit() {
		int addr = BASEADDR;
		
		int y = 0x10;
		int taddr = addr - y;
		int args[] = new int[]{taddr & 0xFF, (taddr >> 8) & 0xFF};
		int bank = 5;
		
		
		int a = VAL8BIT;
		int val = VAL8BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xD9, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xD9, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xD9, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
	}
	
	// 0xDD
	@Test
	public void cmpAMemAbsoluteX16Bit() {
		int addr = BASEADDR;
		
		int x = 0x1010;
		int taddr = addr - x;
		int args[] = new int[]{taddr & 0xFF, (taddr >> 8) & 0xFF};
		int bank = 5;
		
		
		int a = VAL16BIT;
		int val = VAL16BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xDD, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xDD, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xDD, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
	}
	
	// 0xDD
	@Test
	public void cmpAMemAbsoluteX8Bit() {
		int addr = BASEADDR;
		
		int x = 0x10;
		int taddr = addr - x;
		int args[] = new int[]{taddr & 0xFF, (taddr >> 8) & 0xFF};
		int bank = 5;
		
		
		int a = VAL8BIT;
		int val = VAL8BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xDD, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xDD, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xDD, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
	}
	
	// 0xDF
	@Test
	public void cmpAMemAbsoluteLongX16Bit() {
		int addr = BASEADDR;
		int x = 0x10;
		int taddr = addr - x;
		int bank = 5;
		int args[] = new int[]{taddr & 0xFF, (taddr >> 8) & 0xFF, bank};
		
		int a = VAL16BIT;
		int val = VAL16BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xDF, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xDF, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xDF, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
	}
	
	// 0xDF
	@Test
	public void cmpAMemAbsoluteLongX8Bit() {
		int addr = BASEADDR;
		int x = 0x10;
		int taddr = addr - x;
		int bank = 5;
		int args[] = new int[]{taddr & 0xFF, (taddr >> 8) & 0xFF, bank};
		
		int a = VAL8BIT;
		int val = VAL8BIT;
		
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xDF, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xDF, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0xDF, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
	}
}
