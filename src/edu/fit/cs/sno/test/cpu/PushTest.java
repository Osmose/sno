package edu.fit.cs.sno.test.cpu;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.cpu.instructions.Push;
import edu.fit.cs.sno.snes.mem.LoROMMemory;
import edu.fit.cs.sno.util.Settings;

public class PushTest {
	private final int bank = 0;
	@Before
	public void setUp() throws Exception {
		new Push(); // For 100% coverage
		Settings.init();
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
	}
	
	@Test
	public void pushEffectiveAbsolute() {
		// 0xF4
		int args[] = {0xFC, 0xDB};
		int addr = 100;

		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr+0, 1);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		Core.mem.set(Size.BYTE, bank, addr+4, 1);
		
		CPU.sp.setValue(addr+2);
		CPU.doOp(0xF4, args);
		
		assertEquals(addr, CPU.sp.getValue());
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr-1));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+0));
		assertEquals(0xFC, Core.mem.get(Size.BYTE, bank, addr+1));
		assertEquals(0xDB, Core.mem.get(Size.BYTE, bank, addr+2));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+3));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+4));
	}
	
	@Test
	public void pushEffectiveIndirect() {
		int addr = 0x1B4C;
		int iaddr = 0x10AC;
		int dp = 34;
		int args[] = {iaddr - dp};
		
		// Set the indirect data
		Core.mem.set(Size.BYTE, bank, iaddr-1, 1);
		Core.mem.set(Size.BYTE, bank, iaddr+0, 0xAC);
		Core.mem.set(Size.BYTE, bank, iaddr+1, 0xDC);
		Core.mem.set(Size.BYTE, bank, iaddr+2, 1);
		
		// Set the stack to ones
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr+0, 1);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		Core.mem.set(Size.BYTE, bank, addr+4, 1);
		
		CPU.sp.setValue(addr+2);
		CPU.dp.setValue(dp);
		CPU.status.setMemoryAccess(false);
		CPU.status.setIndexRegister(false);
		CPU.doOp(0xD4, args);
		
		assertEquals(iaddr, CPU.dataAddr);
		assertEquals(bank, CPU.dataBank);
		
		assertEquals(addr, CPU.sp.getValue());
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr-1));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+0));
		assertEquals(0xAC, Core.mem.get(Size.BYTE, bank, addr+1));
		assertEquals(0xDC, Core.mem.get(Size.BYTE, bank, addr+2));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+3));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+4));
		
		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		// Set the indirect data
		Core.mem.set(Size.BYTE, bank, iaddr-1, 1);
		Core.mem.set(Size.BYTE, bank, iaddr+0, 0xAC);
		Core.mem.set(Size.BYTE, bank, iaddr+1, 0xDC);
		Core.mem.set(Size.BYTE, bank, iaddr+2, 1);
		
		// Set the stack to ones
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr+0, 1);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		Core.mem.set(Size.BYTE, bank, addr+4, 1);
		
		CPU.sp.setValue(addr+2);
		CPU.dp.setValue(dp);
		CPU.status.setMemoryAccess(true); // should function identically regardless of x/m flags
		CPU.status.setIndexRegister(true);
		CPU.doOp(0xD4, args);
		
		assertEquals(iaddr, CPU.dataAddr);
		assertEquals(bank, CPU.dataBank);
		
		assertEquals(addr, CPU.sp.getValue());
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr-1));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+0));
		assertEquals(0xAC, Core.mem.get(Size.BYTE, bank, addr+1));
		assertEquals(0xDC, Core.mem.get(Size.BYTE, bank, addr+2));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+3));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+4));
	}
	
	@Test
	public void pushEffectivePC() {
		// 0x62
		
		int addr = 0x1B4C;
		int data = 0x3454;
		int offset = 0x1122;
		int args[] = {0x22, 0x11};
		
		// Set the stack to ones
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr+0, 1);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		Core.mem.set(Size.BYTE, bank, addr+4, 1);
		
		CPU.sp.setValue(addr+2);
		CPU.pc.setValue(data-offset);
		CPU.status.setMemoryAccess(false);
		CPU.status.setIndexRegister(false);
		CPU.doOp(0x62, args);
		
		assertEquals(addr, CPU.sp.getValue());
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr-1));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+0));
		assertEquals(0x54, Core.mem.get(Size.BYTE, bank, addr+1));
		assertEquals(0x34, Core.mem.get(Size.BYTE, bank, addr+2));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+3));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+4));
		
		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		// Set the stack to ones
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr+0, 1);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		Core.mem.set(Size.BYTE, bank, addr+4, 1);
		
		CPU.sp.setValue(addr+2);
		CPU.pc.setValue(data-offset);
		CPU.status.setMemoryAccess(true);
		CPU.status.setIndexRegister(true);
		CPU.doOp(0x62, args);
		
		assertEquals(addr, CPU.sp.getValue());
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr-1));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+0));
		assertEquals(0x54, Core.mem.get(Size.BYTE, bank, addr+1));
		assertEquals(0x34, Core.mem.get(Size.BYTE, bank, addr+2));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+3));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+4));
	}
	
	@Test
	public void pushAccumulator() {
		// 0x48
		int a = 0xACDC;
		int addr = 0x1B4C;
		int args[] = null;
		// Set the stack to ones
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr+0, 1);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		Core.mem.set(Size.BYTE, bank, addr+4, 1);
		
		CPU.sp.setValue(addr+2);
		CPU.a.setValue(a);
		CPU.status.setMemoryAccess(false);
		CPU.doOp(0x48, args);
		
		assertEquals(addr, CPU.sp.getValue());
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr-1));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+0));
		assertEquals(0xDC, Core.mem.get(Size.BYTE, bank, addr+1));
		assertEquals(0xAC, Core.mem.get(Size.BYTE, bank, addr+2));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+3));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+4));
		
		a = 0xDC;
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		// Set the stack to ones
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr+0, 1);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		Core.mem.set(Size.BYTE, bank, addr+4, 1);
		
		CPU.sp.setValue(addr+1);
		CPU.status.setMemoryAccess(true);
		CPU.a.setValue(a);
		CPU.doOp(0x48, args);
		
		assertEquals(addr, CPU.sp.getValue());
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr-1));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+0));
		assertEquals(0xDC, Core.mem.get(Size.BYTE, bank, addr+1));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+2));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+3));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+4));
	}
	
	@Test
	public void pushDataBank() {
		// 0x8B
		int dbr = 0xAC;
		int addr = 0x1B4C;
		int args[] = null;
		
		// Set the stack to ones
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr+0, 1);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		Core.mem.set(Size.BYTE, bank, addr+4, 1);
		
		CPU.sp.setValue(addr+1);
		CPU.dbr.setValue(dbr);
		CPU.status.setMemoryAccess(false);
		CPU.status.setIndexRegister(false);
		CPU.doOp(0x8B, args);
		
		assertEquals(addr, CPU.sp.getValue());
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr-1));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+0));
		assertEquals(0xAC, Core.mem.get(Size.BYTE, bank, addr+1));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+2));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+3));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+4));
		
		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		// Set the stack to ones
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr+0, 1);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		Core.mem.set(Size.BYTE, bank, addr+4, 1);
		
		CPU.sp.setValue(addr+1);
		CPU.dbr.setValue(dbr);
		CPU.status.setMemoryAccess(true);
		CPU.status.setIndexRegister(true);
		CPU.doOp(0x8B, args);
		
		assertEquals(addr, CPU.sp.getValue());
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr-1));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+0));
		assertEquals(0xAC, Core.mem.get(Size.BYTE, bank, addr+1));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+2));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+3));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+4));
	}
	
	@Test
	public void pushDirectPage() {
		int addr = 0x1B4C;
		int dp = 0x3454;
		int offset = 0x1122;
		int args[] = {0x22, 0x11};
		
		// Set the stack to ones
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr+0, 1);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		Core.mem.set(Size.BYTE, bank, addr+4, 1);
		
		CPU.sp.setValue(addr+2);
		CPU.dp.setValue(dp);
		CPU.status.setMemoryAccess(false);
		CPU.status.setIndexRegister(false);
		CPU.doOp(0x0B, args);
		
		assertEquals(addr, CPU.sp.getValue());
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr-1));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+0));
		assertEquals(0x54, Core.mem.get(Size.BYTE, bank, addr+1));
		assertEquals(0x34, Core.mem.get(Size.BYTE, bank, addr+2));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+3));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+4));
		
		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		// Set the stack to ones
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr+0, 1);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		Core.mem.set(Size.BYTE, bank, addr+4, 1);
		
		CPU.sp.setValue(addr+2);
		CPU.dp.setValue(dp);
		CPU.status.setMemoryAccess(true);
		CPU.status.setIndexRegister(true);
		CPU.doOp(0x0B, args);
		
		assertEquals(addr, CPU.sp.getValue());
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr-1));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+0));
		assertEquals(0x54, Core.mem.get(Size.BYTE, bank, addr+1));
		assertEquals(0x34, Core.mem.get(Size.BYTE, bank, addr+2));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+3));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+4));
	}
	
	@Test
	public void pushProgramBank() {
		// 0x4b
		int pbr = 0xAC;
		int addr = 0x1B4C;
		int args[] = null;
		
		// Set the stack to ones
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr+0, 1);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		Core.mem.set(Size.BYTE, bank, addr+4, 1);
		
		CPU.sp.setValue(addr+1);
		CPU.pbr.setValue(pbr);
		CPU.status.setMemoryAccess(false);
		CPU.status.setIndexRegister(false);
		CPU.doOp(0x4B, args);
		
		assertEquals(addr, CPU.sp.getValue());
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr-1));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+0));
		assertEquals(0xAC, Core.mem.get(Size.BYTE, bank, addr+1));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+2));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+3));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+4));
		
		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		// Set the stack to ones
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr+0, 1);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		Core.mem.set(Size.BYTE, bank, addr+4, 1);
		
		CPU.sp.setValue(addr+1);
		CPU.pbr.setValue(pbr);
		CPU.status.setMemoryAccess(true);
		CPU.status.setIndexRegister(true);
		CPU.doOp(0x4B, args);
		
		assertEquals(addr, CPU.sp.getValue());
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr-1));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+0));
		assertEquals(0xAC, Core.mem.get(Size.BYTE, bank, addr+1));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+2));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+3));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+4));
	}
	
	@Test
	public void pushStatus() {
		// 0x08
		int stat = 0xFF;
		int addr = 0x1B4C;
		int args[] = null;
		
		// Set the stack to ones
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr+0, 1);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		Core.mem.set(Size.BYTE, bank, addr+4, 1);
		
		CPU.sp.setValue(addr+1);
		CPU.status.setValue(stat);
		CPU.doOp(0x08, args);
		
		assertEquals(stat, CPU.status.getValue());
		assertEquals(addr, CPU.sp.getValue());
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr-1));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+0));
		assertEquals(stat, Core.mem.get(Size.BYTE, bank, addr+1));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+2));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+3));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+4));
		
		stat = 0x00;
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		// Set the stack to ones
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr+0, 1);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		Core.mem.set(Size.BYTE, bank, addr+4, 1);
		
		CPU.sp.setValue(addr+1);
		CPU.status.setValue(stat);
		CPU.doOp(0x08, args);
		
		assertEquals(addr, CPU.sp.getValue());
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr-1));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+0));
		assertEquals(stat, Core.mem.get(Size.BYTE, bank, addr+1));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+2));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+3));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+4));
	}
	
	@Test
	public void pushX() {
		// 0xDA
		int x = 0xACDC;
		int addr = 0x1B4C;
		int args[] = null;
		// Set the stack to ones
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr+0, 1);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		Core.mem.set(Size.BYTE, bank, addr+4, 1);
		
		CPU.sp.setValue(addr+2);
		CPU.x.setValue(x);
		CPU.status.setIndexRegister(false);
		CPU.doOp(0xDA, args);
		
		assertEquals(addr, CPU.sp.getValue());
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr-1));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+0));
		assertEquals(0xDC, Core.mem.get(Size.BYTE, bank, addr+1));
		assertEquals(0xAC, Core.mem.get(Size.BYTE, bank, addr+2));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+3));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+4));
		
		x = 0xDC;
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		// Set the stack to ones
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr+0, 1);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		Core.mem.set(Size.BYTE, bank, addr+4, 1);
		
		CPU.sp.setValue(addr+1);
		CPU.status.setIndexRegister(true);
		CPU.x.setValue(x);
		CPU.doOp(0xDA, args);
		
		assertEquals(addr, CPU.sp.getValue());
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr-1));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+0));
		assertEquals(0xDC, Core.mem.get(Size.BYTE, bank, addr+1));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+2));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+3));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+4));
	}
	
	@Test
	public void pushY() {
		// 0x5A
		int y = 0xACDC;
		int addr = 0x1B4C;
		int args[] = null;
		// Set the stack to ones
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr+0, 1);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		Core.mem.set(Size.BYTE, bank, addr+4, 1);
		
		CPU.sp.setValue(addr+2);
		CPU.y.setValue(y);
		CPU.status.setIndexRegister(false);
		CPU.doOp(0x5A, args);
		
		assertEquals(addr, CPU.sp.getValue());
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr-1));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+0));
		assertEquals(0xDC, Core.mem.get(Size.BYTE, bank, addr+1));
		assertEquals(0xAC, Core.mem.get(Size.BYTE, bank, addr+2));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+3));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+4));
		
		y = 0xDC;
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		// Set the stack to ones
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr+0, 1);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		Core.mem.set(Size.BYTE, bank, addr+4, 1);
		
		CPU.sp.setValue(addr+1);
		CPU.status.setIndexRegister(true);
		CPU.y.setValue(y);
		CPU.doOp(0x5A, args);
		
		assertEquals(addr, CPU.sp.getValue());
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr-1));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+0));
		assertEquals(0xDC, Core.mem.get(Size.BYTE, bank, addr+1));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+2));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+3));
		assertEquals(1,    Core.mem.get(Size.BYTE, bank, addr+4));
	}
}
