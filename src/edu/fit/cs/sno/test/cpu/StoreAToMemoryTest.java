package edu.fit.cs.sno.test.cpu;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.cpu.instructions.StoreAToMemory;
import edu.fit.cs.sno.snes.mem.LoROMMemory;
import edu.fit.cs.sno.util.Settings;

public class StoreAToMemoryTest {

	@Before
	public void setUp() {
		new StoreAToMemory();
		Settings.init();
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
	}

	// Store A to Memory
	// jmp[0x81] = StoreAToMemory.saveADPIndexedIndirectX;
	// jmp[0x83] = StoreAToMemory.saveAStackRelative;
	// jmp[0x85] = StoreAToMemory.saveADirectPage;
	// jmp[0x87] = StoreAToMemory.saveADPIndirectLong;
	// jmp[0x8D] = StoreAToMemory.saveAAbsolute;
	// jmp[0x8F] = StoreAToMemory.saveAAbsoluteLong;
	// jmp[0x91] = StoreAToMemory.saveADPIndexedIndirectY;
	// jmp[0x92] = StoreAToMemory.saveADPIndirect;
	// jmp[0x93] = StoreAToMemory.saveASRIndirectIndexedY;
	// jmp[0x95] = StoreAToMemory.saveADirectPageX;
	// jmp[0x97] = StoreAToMemory.saveADPIndirectLongIndexedY;
	// jmp[0x99] = StoreAToMemory.saveAAbsoluteIndexedY;
	// jmp[0x9D] = StoreAToMemory.saveAAbsoluteIndexedX;
	// jmp[0x9F] = StoreAToMemory.saveAAbsoluteLongIndexedX;
	@Test
	public void saveADPIndexedIndirectX() {
		int bank = 3;
		int iaddr = 0xCE;
		int addr = 0xAD;
		int a = 0x34AF;
		int x = 10;
		int dp = 8;
		int args[] = { iaddr - x - dp };
		Core.mem.set(Size.SHORT, bank, iaddr, addr);
		Core.mem.set(Size.BYTE, bank, addr - 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 0, 1);
		Core.mem.set(Size.BYTE, bank, addr + 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 2, 1);

		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.doOp(0x81, args);

		assertEquals(bank, CPU.dataBank);
		assertEquals(bank, CPU.dbr.getValue());
		assertEquals(addr, CPU.dataAddr);

		assertEquals(a, CPU.a.getValue());
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr - 1));
		assertEquals(0xAF, Core.mem.get(Size.BYTE, bank, addr + 0));
		assertEquals(0x34, Core.mem.get(Size.BYTE, bank, addr + 1));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 2));

		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		a = 0xAF;
		Core.mem.set(Size.SHORT, bank, iaddr, addr);
		Core.mem.set(Size.BYTE, bank, addr - 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 0, 1);
		Core.mem.set(Size.BYTE, bank, addr + 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 2, 1);

		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.status.setMemoryAccess(true);
		CPU.doOp(0x81, args);

		assertEquals(bank, CPU.dataBank);
		assertEquals(bank, CPU.dbr.getValue());
		assertEquals(addr, CPU.dataAddr);

		assertEquals(a, CPU.a.getValue());
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr - 1));
		assertEquals(0xAF, Core.mem.get(Size.BYTE, bank, addr + 0));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 1));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 2));
	}

	@Test
	public void saveAStackRelative() {
		int a = 0x34AF;
		int bank = 0; // stack is always bank 0
		int addr = 0x4F;
		int offset = 10;
		int args[] = { offset };
		Core.mem.set(Size.BYTE, bank, addr - 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 0, 1);
		Core.mem.set(Size.BYTE, bank, addr + 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 2, 1);

		CPU.sp.setValue(addr - offset);
		CPU.a.setValue(a);
		CPU.doOp(0x83, args);

		assertEquals(addr, CPU.dataAddr);
		assertEquals(bank, CPU.dataBank);

		assertEquals(a, CPU.a.getValue());
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr - 1));
		assertEquals(0xAF, Core.mem.get(Size.BYTE, bank, addr + 0));
		assertEquals(0x34, Core.mem.get(Size.BYTE, bank, addr + 1));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 2));

		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		a = 0xAF;
		Core.mem.set(Size.BYTE, bank, addr - 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 0, 1);
		Core.mem.set(Size.BYTE, bank, addr + 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 2, 1);

		CPU.sp.setValue(addr - offset);
		CPU.a.setValue(a);
		CPU.status.setMemoryAccess(true);
		CPU.doOp(0x83, args);

		assertEquals(addr, CPU.dataAddr);
		assertEquals(bank, CPU.dataBank);

		assertEquals(a, CPU.a.getValue());
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr - 1));
		assertEquals(0xAF, Core.mem.get(Size.BYTE, bank, addr + 0));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 1));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 2));
	}

	@Test
	public void saveADirectPage() {
		int addr = 0xAF;
		int bank = 0; // Direct page bank is zero
		int a = 0xACDC;
		int dp = 35;
		int args[] = { addr - dp };
		Core.mem.set(Size.BYTE, bank, addr - 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 0, 1);
		Core.mem.set(Size.BYTE, bank, addr + 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 2, 1);

		CPU.dp.setValue(dp);
		CPU.a.setValue(a);
		CPU.doOp(0x85, args);

		assertEquals(addr, CPU.dataAddr);
		assertEquals(bank, CPU.dataBank);

		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr - 1));
		assertEquals(0xDC, Core.mem.get(Size.BYTE, bank, addr + 0));
		assertEquals(0xAC, Core.mem.get(Size.BYTE, bank, addr + 1));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 2));

		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		a = 0xAC;
		Core.mem.set(Size.BYTE, bank, addr - 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 0, 1);
		Core.mem.set(Size.BYTE, bank, addr + 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 2, 1);

		CPU.dp.setValue(dp);
		CPU.a.setValue(a);
		CPU.status.setMemoryAccess(true);
		CPU.doOp(0x85, args);

		assertEquals(addr, CPU.dataAddr);
		assertEquals(bank, CPU.dataBank);

		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr - 1));
		assertEquals(0xAC, Core.mem.get(Size.BYTE, bank, addr + 0));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 1));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 2));
	}

	@Test
	public void saveADPIndirectLong() {
		int a = 0xACDC;
		int dp = 35;
		int ibank = 0;
		int bank = 5;
		int addr = 0x1D3F;
		int iaddr = 0x005F;
		int args[] = { iaddr - dp };
		Core.mem.set(Size.SHORT, ibank, iaddr, addr);
		Core.mem.set(Size.SHORT, ibank, iaddr + 2, bank);
		Core.mem.set(Size.BYTE, bank, addr - 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 0, 1);
		Core.mem.set(Size.BYTE, bank, addr + 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 2, 1);

		CPU.dp.setValue(dp);
		CPU.a.setValue(a);
		CPU.doOp(0x87, args);

		assertEquals(addr, CPU.dataAddr);
		assertEquals(bank, CPU.dataBank);

		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr - 1));
		assertEquals(0xDC, Core.mem.get(Size.BYTE, bank, addr + 0));
		assertEquals(0xAC, Core.mem.get(Size.BYTE, bank, addr + 1));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 2));

		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		a = 0xAC;
		Core.mem.set(Size.SHORT, ibank, iaddr, addr);
		Core.mem.set(Size.SHORT, ibank, iaddr + 2, bank);
		Core.mem.set(Size.BYTE, bank, addr - 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 0, 1);
		Core.mem.set(Size.BYTE, bank, addr + 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 2, 1);

		CPU.dp.setValue(dp);
		CPU.a.setValue(a);
		CPU.status.setMemoryAccess(true);
		CPU.doOp(0x87, args);

		assertEquals(addr, CPU.dataAddr);
		assertEquals(bank, CPU.dataBank);

		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr - 1));
		assertEquals(0xAC, Core.mem.get(Size.BYTE, bank, addr + 0));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 1));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 2));

	}

	@Test
	public void saveAAbsolute() {
		int a = 0xACDC;
		int bank = 5;
		int addr = 0x1D3F;
		int args[] = { 0x3F, 0x1D };
		Core.mem.set(Size.BYTE, bank, addr - 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 0, 1);
		Core.mem.set(Size.BYTE, bank, addr + 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 2, 1);

		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x8D, args);

		assertEquals(addr, CPU.dataAddr);
		assertEquals(bank, CPU.dataBank);
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr - 1));
		assertEquals(0xDC, Core.mem.get(Size.BYTE, bank, addr + 0));
		assertEquals(0xAC, Core.mem.get(Size.BYTE, bank, addr + 1));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 2));

		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		a = 0xAC;
		Core.mem.set(Size.BYTE, bank, addr - 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 0, 1);
		Core.mem.set(Size.BYTE, bank, addr + 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 2, 1);

		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.status.setMemoryAccess(true);
		CPU.doOp(0x8D, args);

		assertEquals(addr, CPU.dataAddr);
		assertEquals(bank, CPU.dataBank);
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr - 1));
		assertEquals(0xAC, Core.mem.get(Size.BYTE, bank, addr + 0));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 1));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 2));
	}

	@Test
	public void saveAAbsoluteLong() {
		int a = 0xACDC;
		int bank = 5;
		int addr = 0x1D3F;
		int args[] = { 0x3F, 0x1D, bank };
		Core.mem.set(Size.BYTE, bank, addr - 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 0, 1);
		Core.mem.set(Size.BYTE, bank, addr + 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 2, 1);

		CPU.a.setValue(a);
		CPU.doOp(0x8F, args);

		assertEquals(addr, CPU.dataAddr);
		assertEquals(bank, CPU.dataBank);
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr - 1));
		assertEquals(0xDC, Core.mem.get(Size.BYTE, bank, addr + 0));
		assertEquals(0xAC, Core.mem.get(Size.BYTE, bank, addr + 1));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 2));

		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		a = 0xAC;
		Core.mem.set(Size.BYTE, bank, addr - 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 0, 1);
		Core.mem.set(Size.BYTE, bank, addr + 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 2, 1);

		CPU.a.setValue(a);
		CPU.status.setMemoryAccess(true);
		CPU.doOp(0x8F, args);

		assertEquals(addr, CPU.dataAddr);
		assertEquals(bank, CPU.dataBank);
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr - 1));
		assertEquals(0xAC, Core.mem.get(Size.BYTE, bank, addr + 0));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 1));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 2));
	}

	@Test
	// 0x91
	public void saveADPIndirectIndexedY() {
		int bank = 3;
		int iaddr = 0xCE;
		int addr = 0xAD;
		int a = 0x34AF;
		int y = 10;
		int dp = 8;
		int args[] = { iaddr - dp };
		Core.mem.set(Size.SHORT, 0, iaddr, addr - y);
		Core.mem.set(Size.BYTE, bank, addr - 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 0, 1);
		Core.mem.set(Size.BYTE, bank, addr + 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 2, 1);

		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.doOp(0x91, args);

		assertEquals(bank, CPU.dataBank);
		assertEquals(bank, CPU.dbr.getValue());
		assertEquals(addr, CPU.dataAddr);

		assertEquals(a, CPU.a.getValue());
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr - 1));
		assertEquals(0xAF, Core.mem.get(Size.BYTE, bank, addr + 0));
		assertEquals(0x34, Core.mem.get(Size.BYTE, bank, addr + 1));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 2));

		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		a = 0xAF;
		Core.mem.set(Size.SHORT, bank, iaddr, addr - y);
		Core.mem.set(Size.BYTE, bank, addr - 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 0, 1);
		Core.mem.set(Size.BYTE, bank, addr + 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 2, 1);

		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.status.setMemoryAccess(true);
		CPU.doOp(0x91, args);

		assertEquals(bank, CPU.dataBank);
		assertEquals(bank, CPU.dbr.getValue());
		assertEquals(addr, CPU.dataAddr);

		assertEquals(a, CPU.a.getValue());
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr - 1));
		assertEquals(0xAF, Core.mem.get(Size.BYTE, bank, addr + 0));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 1));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 2));
	}

	@Test
	public void saveADPIndirect() {
		int a = 0xACDC;
		int dp = 35;
		int bank = 0;
		int addr = 0x1D3F;
		int iaddr = 0x005F;
		int args[] = { iaddr - dp };
		Core.mem.set(Size.SHORT, bank, iaddr, addr);
		Core.mem.set(Size.BYTE, bank, addr - 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 0, 1);
		Core.mem.set(Size.BYTE, bank, addr + 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 2, 1);

		CPU.dp.setValue(dp);
		CPU.a.setValue(a);
		CPU.doOp(0x92, args);

		assertEquals(addr, CPU.dataAddr);
		assertEquals(bank, CPU.dataBank);

		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr - 1));
		assertEquals(0xDC, Core.mem.get(Size.BYTE, bank, addr + 0));
		assertEquals(0xAC, Core.mem.get(Size.BYTE, bank, addr + 1));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 2));

		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		a = 0xAC;
		Core.mem.set(Size.SHORT, bank, iaddr, addr);
		Core.mem.set(Size.BYTE, bank, addr - 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 0, 1);
		Core.mem.set(Size.BYTE, bank, addr + 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 2, 1);

		CPU.dp.setValue(dp);
		CPU.a.setValue(a);
		CPU.status.setMemoryAccess(true);
		CPU.doOp(0x92, args);

		assertEquals(addr, CPU.dataAddr);
		assertEquals(bank, CPU.dataBank);

		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr - 1));
		assertEquals(0xAC, Core.mem.get(Size.BYTE, bank, addr + 0));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 1));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 2));
	}

	@Test
	public void saveASRIndirectIndexedY() {
		int bank = 3;
		int iaddr = 0xCE;
		int addr = 0xAD;
		int a = 0x34AF;
		int offset = 25;
		int y = 10;
		int args[] = { offset };
		Core.mem.set(Size.SHORT, bank, iaddr, addr - y);
		Core.mem.set(Size.BYTE, bank, addr - 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 0, 1);
		Core.mem.set(Size.BYTE, bank, addr + 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 2, 1);

		CPU.sp.setValue(iaddr-offset);
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.dbr.setValue(bank);
		CPU.doOp(0x93, args);

		assertEquals(bank, CPU.dataBank);
		assertEquals(bank, CPU.dbr.getValue());
		assertEquals(addr, CPU.dataAddr);

		assertEquals(a, CPU.a.getValue());
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr - 1));
		assertEquals(0xAF, Core.mem.get(Size.BYTE, bank, addr + 0));
		assertEquals(0x34, Core.mem.get(Size.BYTE, bank, addr + 1));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 2));
	}

	@Test
	public void saveADirectPageX() {
		int addr = 0xAF;
		int bank = 0; // Direct page bank is zero
		int a = 0xACDC;
		int x = 10;
		int dp = 35;
		int args[] = { addr - dp - x };
		Core.mem.set(Size.BYTE, bank, addr - 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 0, 1);
		Core.mem.set(Size.BYTE, bank, addr + 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 2, 1);

		CPU.dp.setValue(dp);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.doOp(0x95, args);

		assertEquals(addr, CPU.dataAddr);
		assertEquals(bank, CPU.dataBank);

		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr - 1));
		assertEquals(0xDC, Core.mem.get(Size.BYTE, bank, addr + 0));
		assertEquals(0xAC, Core.mem.get(Size.BYTE, bank, addr + 1));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 2));

		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		a = 0xAC;
		Core.mem.set(Size.BYTE, bank, addr - 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 0, 1);
		Core.mem.set(Size.BYTE, bank, addr + 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 2, 1);

		CPU.dp.setValue(dp);
		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.status.setMemoryAccess(true);
		CPU.doOp(0x95, args);

		assertEquals(addr, CPU.dataAddr);
		assertEquals(bank, CPU.dataBank);

		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr - 1));
		assertEquals(0xAC, Core.mem.get(Size.BYTE, bank, addr + 0));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 1));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 2));
	}

	@Test
	public void saveADPIndirectLongIndexedY() {
		int ibank = 3;
		int bank = 8;
		int iaddr = 0xCE;
		int addr = 0xAD;
		int a = 0x34AF;
		int y = 10;
		int dp = 8;
		int args[] = { iaddr - dp };
		Core.mem.set(Size.SHORT, ibank, iaddr, addr - y);
		Core.mem.set(Size.SHORT, ibank, iaddr + 2, bank);
		Core.mem.set(Size.BYTE, bank, addr - 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 0, 1);
		Core.mem.set(Size.BYTE, bank, addr + 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 2, 1);

		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.doOp(0x97, args);

		assertEquals(bank, CPU.dataBank);
		assertEquals(bank, CPU.dbr.getValue());
		assertEquals(addr, CPU.dataAddr);

		assertEquals(a, CPU.a.getValue());
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr - 1));
		assertEquals(0xAF, Core.mem.get(Size.BYTE, bank, addr + 0));
		assertEquals(0x34, Core.mem.get(Size.BYTE, bank, addr + 1));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 2));

		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		a = 0xAF;
		Core.mem.set(Size.SHORT, ibank, iaddr, addr - y);
		Core.mem.set(Size.SHORT, ibank, iaddr + 2, bank);
		Core.mem.set(Size.BYTE, bank, addr - 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 0, 1);
		Core.mem.set(Size.BYTE, bank, addr + 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 2, 1);

		CPU.dp.setValue(dp);
		CPU.dbr.setValue(bank);
		CPU.a.setValue(a);
		CPU.y.setValue(y);
		CPU.status.setMemoryAccess(true);
		CPU.doOp(0x97, args);

		assertEquals(bank, CPU.dataBank);
		assertEquals(bank, CPU.dbr.getValue());
		assertEquals(addr, CPU.dataAddr);

		assertEquals(a, CPU.a.getValue());
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr - 1));
		assertEquals(0xAF, Core.mem.get(Size.BYTE, bank, addr + 0));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 1));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 2));
	}

	@Test
	public void saveAAbsoluteIndexedY() {
		int a = 0xACDC;
		int bank = 5;
		int addr = 0x1D3F;
		int y = 15;
		int args[] = { 0x3F - y, 0x1D };
		Core.mem.set(Size.BYTE, bank, addr - 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 0, 1);
		Core.mem.set(Size.BYTE, bank, addr + 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 2, 1);

		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.y.setValue(y);
		CPU.doOp(0x99, args);

		assertEquals(addr, CPU.dataAddr);
		assertEquals(bank, CPU.dataBank);
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr - 1));
		assertEquals(0xDC, Core.mem.get(Size.BYTE, bank, addr + 0));
		assertEquals(0xAC, Core.mem.get(Size.BYTE, bank, addr + 1));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 2));

		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		a = 0xAC;
		Core.mem.set(Size.BYTE, bank, addr - 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 0, 1);
		Core.mem.set(Size.BYTE, bank, addr + 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 2, 1);

		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.y.setValue(y);
		CPU.status.setMemoryAccess(true);
		CPU.doOp(0x99, args);

		assertEquals(addr, CPU.dataAddr);
		assertEquals(bank, CPU.dataBank);
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr - 1));
		assertEquals(0xAC, Core.mem.get(Size.BYTE, bank, addr + 0));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 1));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 2));
	}

	@Test
	public void saveAAbsoluteIndexedX() {
		int a = 0xACDC;
		int bank = 5;
		int addr = 0x1D3F;
		int x = 15;
		int args[] = { 0x3F - x, 0x1D };
		Core.mem.set(Size.BYTE, bank, addr - 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 0, 1);
		Core.mem.set(Size.BYTE, bank, addr + 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 2, 1);

		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.x.setValue(x);
		CPU.doOp(0x9D, args);

		assertEquals(addr, CPU.dataAddr);
		assertEquals(bank, CPU.dataBank);
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr - 1));
		assertEquals(0xDC, Core.mem.get(Size.BYTE, bank, addr + 0));
		assertEquals(0xAC, Core.mem.get(Size.BYTE, bank, addr + 1));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 2));

		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		a = 0xAC;
		Core.mem.set(Size.BYTE, bank, addr - 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 0, 1);
		Core.mem.set(Size.BYTE, bank, addr + 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 2, 1);

		CPU.a.setValue(a);
		CPU.dbr.setValue(bank);
		CPU.x.setValue(x);
		CPU.status.setMemoryAccess(true);
		CPU.doOp(0x9D, args);

		assertEquals(addr, CPU.dataAddr);
		assertEquals(bank, CPU.dataBank);
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr - 1));
		assertEquals(0xAC, Core.mem.get(Size.BYTE, bank, addr + 0));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 1));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 2));
	}

	@Test
	public void saveAAbsoluteLongIndexedX() {
		int a = 0xACDC;
		int bank = 5;
		int addr = 0x1D3F;
		int x = 15;
		int args[] = { 0x3F - x, 0x1D, bank };
		Core.mem.set(Size.BYTE, bank, addr - 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 0, 1);
		Core.mem.set(Size.BYTE, bank, addr + 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 2, 1);

		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.doOp(0x9F, args);

		assertEquals(addr, CPU.dataAddr);
		assertEquals(bank, CPU.dataBank);
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr - 1));
		assertEquals(0xDC, Core.mem.get(Size.BYTE, bank, addr + 0));
		assertEquals(0xAC, Core.mem.get(Size.BYTE, bank, addr + 1));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 2));

		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		a = 0xAC;
		Core.mem.set(Size.BYTE, bank, addr - 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 0, 1);
		Core.mem.set(Size.BYTE, bank, addr + 1, 1);
		Core.mem.set(Size.BYTE, bank, addr + 2, 1);

		CPU.a.setValue(a);
		CPU.x.setValue(x);
		CPU.status.setMemoryAccess(true);
		CPU.doOp(0x9F, args);

		assertEquals(addr, CPU.dataAddr);
		assertEquals(bank, CPU.dataBank);
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr - 1));
		assertEquals(0xAC, Core.mem.get(Size.BYTE, bank, addr + 0));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 1));
		assertEquals(1, Core.mem.get(Size.BYTE, bank, addr + 2));
	}
}
