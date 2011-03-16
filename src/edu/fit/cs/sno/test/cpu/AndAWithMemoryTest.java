package edu.fit.cs.sno.test.cpu;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.cpu.instructions.AndAWithMemory;
import edu.fit.cs.sno.snes.mem.LoROMMemory;
import edu.fit.cs.sno.util.Settings;

public class AndAWithMemoryTest {


	private static final int BASEADDR = 0x127D;
	private static final int VAL8BIT = 0x7F;
	private static final int VAL16BIT = 0x7FF8;

	@Before
	public void setUp() throws Exception {
		new AndAWithMemory(); // For 100% coverage
		Settings.init();
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
	}

	public void verify16Bit(int a, int val, int flags) {
		assertEquals(a & val,    CPU.a.getValue());
		assertEquals(val,  CPU.dataReg.getValue());
		
		int cmp = (a & 0xFFFF) & (val &0xFFFF);
		assertEquals(((cmp & 0x8000) == 0x8000), ((flags & 0x80) == 0x80));  // Test Negative
		assertEquals((cmp == 0), ((flags & 0x02) == 0x02)); // Test Zero
	}
	
	public void verify8Bit(int a, int val, int flags) {
		assertEquals(a & val,    CPU.a.getValue());
		assertEquals(val,  CPU.dataReg.getValue());
		
		int cmp = (a & 0xFF) & (val &0xFF);
		assertEquals(((cmp & 0x80) == 0x80), ((flags & 0x80) == 0x80));  // Test Negative
		assertEquals((cmp == 0), ((flags & 0x02) == 0x02)); // Test Zero
	}
	
	
	// 0x21
	@Test
	public void andADPIndirectX16Bit() {
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
		CPU.doOp(0x21, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x21, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x21, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
	}
	
	// 0x21
	@Test
	public void andADPIndirectX8Bit() {
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
		
		CPU.doOp(0x21, new int[]{arg});
		
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
		CPU.doOp(0x21, new int[]{arg});
		
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
		CPU.doOp(0x21, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());

	}
	
	// 0x23
	@Test
	public void andAStackRelative16Bit() {
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
		CPU.doOp(0x23, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.sp.setValue(sp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x23, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.sp.setValue(sp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x23, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
	}
	// 0x23
	@Test
	public void andAMemStackRelative8Bit() {
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
		CPU.doOp(0x23, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.sp.setValue(sp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x23, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.sp.setValue(sp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x23, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
	}
	

	// 0x25
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
		CPU.doOp(0x25, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x25, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x25, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
	}
	// 0x25
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
		CPU.doOp(0x25, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x25, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x25, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
	}
	
	// 0x27
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
		CPU.doOp(0x27, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x27, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x27, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
	}
	// 0x27
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
		
		CPU.doOp(0x27, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x27, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x27, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
	}
	
	// 0x29
	@Test
	public void cmpAMemImmediate16Bit() {
		int[] args = new int[]{VAL16BIT & 0xFF, (VAL16BIT >>8) & 0xFF};
		int a = VAL16BIT;
		
		CPU.a.setValue(a);
		CPU.doOp(0x29, args);
		
		verify16Bit(a, VAL16BIT, CPU.status.getValue());
		
		a = VAL16BIT - 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.doOp(0x29, args);
		
		verify16Bit(a, VAL16BIT, CPU.status.getValue());
		
		
		a = VAL16BIT + 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.doOp(0x29, args);
		
		verify16Bit(a, VAL16BIT, CPU.status.getValue());
	}
	
	// 0x29
	@Test
	public void cmpAMemImmediate8Bit() {
		int[] args = new int[]{VAL8BIT};
		int a = VAL8BIT;
		
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.doOp(0x29, args);
		
		
		a = VAL8BIT - 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.doOp(0x29, args);
		
		verify8Bit(a, VAL8BIT, CPU.status.getValue());
		
		
		a = VAL8BIT + 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.doOp(0x29, args);
		
		verify8Bit(a, VAL8BIT, CPU.status.getValue());
	}
	
	// 0x2D
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
		CPU.doOp(0x2D, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x2D, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x2D, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
	}
	
	// 0x2D
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
		CPU.doOp(0x2D, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x2D, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x2D, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
	}
	
	// 0x2F
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
		CPU.doOp(0x2F, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x2F, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x2F, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
	}

	// 0x2F
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
		CPU.doOp(0x2F, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x2F, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x2F, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
	}
	
	// 0x31
	@Test
	public void cmpAMemDPIndirectY16Bit() {
		int addr = BASEADDR;
		int iaddr = 0x1378;
		int arg = 0x71;
		int dp = iaddr - arg;
		int bank = 5;
		int ibank = 0;
		int y = 0x1234;
		
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
		CPU.doOp(0x31, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x31, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x31, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
	}
	
	// 0x31
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
		
		CPU.doOp(0x31, new int[]{arg});
		
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
		CPU.doOp(0x31, new int[]{arg});
		
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
		CPU.doOp(0x31, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
	}
	
	// 0x32
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
		
		CPU.doOp(0x32, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x32, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x32, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
	}

	// 0x32
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
		
		CPU.doOp(0x32, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x32, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x32, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
	}
	
	// 0x33
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
		
		CPU.doOp(0x33, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.sp.setValue(sp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x33, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.sp.setValue(sp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x33, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
	}
	
	// 0x33
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
		
		CPU.doOp(0x33, new int[]{arg});
		
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
		CPU.doOp(0x33, new int[]{arg});
		
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
		CPU.doOp(0x33, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
	}

	// 0x35
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
		CPU.doOp(0x35, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x35, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x35, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
	}
	
	// 0x35
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
		CPU.doOp(0x35, new int[]{arg});
		
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
		CPU.doOp(0x35, new int[]{arg});
		
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
		CPU.doOp(0x35, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
	}
	
	// 0x37
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
		CPU.doOp(0x37, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x37, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x37, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
	}
	
	// 0x37
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
		
		CPU.doOp(0x37, new int[]{arg});
		
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
		CPU.doOp(0x37, new int[]{arg});
		
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
		CPU.doOp(0x37, new int[]{arg});
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
	}

	// 0x39
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
		CPU.doOp(0x39, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x39, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x39, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
	}
	
	// 0x39
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
		CPU.doOp(0x39, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x39, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x39, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
	}
	
	// 0x3D
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
		CPU.doOp(0x3D, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x3D, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x3D, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
	}
	
	// 0x3D
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
		CPU.doOp(0x3D, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x3D, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x3D, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
	}
	
	// 0x3F
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
		CPU.doOp(0x3F, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x3F, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x3F, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify16Bit(a, val, CPU.status.getValue());
	}
	
	// 0x3F
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
		CPU.doOp(0x3F, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val - 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x3F, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
		
		
		a = val + 10;
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x3F, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verify8Bit(a, val, CPU.status.getValue());
	}
}
