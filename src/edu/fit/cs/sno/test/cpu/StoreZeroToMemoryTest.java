package edu.fit.cs.sno.test.cpu;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.cpu.instructions.StoreZeroToMemory;
import edu.fit.cs.sno.snes.mem.LoROMMemory;
import edu.fit.cs.sno.util.Settings;

public class StoreZeroToMemoryTest {

	@Before
	public void setUp() throws Exception {
		new StoreZeroToMemory(); // For 100% coverage
		Settings.init();
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
	}
	
	// Store Zero to Memory
    //jmp[0x64] = StoreZeroToMemory.storeZeroDirectPage;
    //jmp[0x74] = StoreZeroToMemory.storeZeroDPIndexedX;
    //jmp[0x9C] = StoreZeroToMemory.storeZeroAbsolute;
    //jmp[0x9E] = StoreZeroToMemory.storeZeroAbsoluteIndexedX;
	
	@Test
	public void storeZeroAbsoluteIndexedX16bit() {
		int x = 6;
		int dbr=5;
		int addr = 0x1CDC;
		int[] args = {0xDC-x, 0x1C};
		Core.mem.set(Size.BYTE, dbr, addr-1, 1);
		Core.mem.set(Size.BYTE, dbr, addr+0, 1);
		Core.mem.set(Size.BYTE, dbr, addr+1, 1);
		Core.mem.set(Size.BYTE, dbr, addr+2, 1);
		
		CPU.x.setValue(x);
		CPU.dbr.setValue(dbr);
		CPU.doOp(0x9E, args);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(dbr, CPU.dataBank);
		assertEquals(0, CPU.dataReg.getValue());

		assertEquals(1, Core.mem.get(Size.BYTE, dbr, addr-1));
		assertEquals(0, Core.mem.get(Size.BYTE, dbr, addr+0));
		assertEquals(0, Core.mem.get(Size.BYTE, dbr, addr+1));
		assertEquals(1, Core.mem.get(Size.BYTE, dbr, addr+2));
	}
	
	@Test
	public void storeZeroAbsoluteIndexedX8bit() {
		int x = 6;
		int dbr=5;
		int addr = 0x1CDC;
		int[] args = {0xDC-x, 0x1C};
		Core.mem.set(Size.BYTE, dbr, addr - 1, 1);
		Core.mem.set(Size.BYTE, dbr, addr + 0, 1);
		Core.mem.set(Size.BYTE, dbr, addr + 1, 1);
		Core.mem.set(Size.BYTE, dbr, addr + 2, 1);
		
		CPU.status.setMemoryAccess(true);//8bit mode
		CPU.x.setValue(x);
		CPU.dbr.setValue(dbr);
		CPU.doOp(0x9E, args);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(dbr, CPU.dataBank);
		assertEquals(0, CPU.dataReg.getValue());

		assertEquals(1, Core.mem.get(Size.BYTE, dbr, addr-1));
		assertEquals(0, Core.mem.get(Size.BYTE, dbr, addr+0));
		assertEquals(1, Core.mem.get(Size.BYTE, dbr, addr+1));
		assertEquals(1, Core.mem.get(Size.BYTE, dbr, addr+2));
	}
	
	
	@Test
	public void storeZeroDPIndexedX16bit() {
		int x=6;
		int dp = 10;
		int addr = 12+x+dp;
		int[] args = {addr-x-dp};
		Core.mem.set(Size.BYTE, 0, addr-1, 1);
		Core.mem.set(Size.BYTE, 0, addr+0, 1);
		Core.mem.set(Size.BYTE, 0, addr+1, 1);
		Core.mem.set(Size.BYTE, 0, addr+2, 1);
		
		CPU.x.setValue(x);
		CPU.dp.setValue(dp);
		CPU.doOp(0x74, args);
		assertEquals(addr, CPU.dataAddr);
		assertEquals(0, CPU.dataBank);
		assertEquals(0, CPU.dataReg.getValue());
		
		assertEquals(CPU.dataReg.getValue(), 0);
		assertEquals(1, Core.mem.get(Size.BYTE, 0, addr-1));
		assertEquals(0, Core.mem.get(Size.BYTE, 0, addr));
		assertEquals(0, Core.mem.get(Size.BYTE, 0, addr+1));
		assertEquals(1, Core.mem.get(Size.BYTE, 0, addr+2));
	}
	
	@Test
	public void storeZeroDPIndexedX8bit() {
		int x=6;
		int dp = 10;
		int addr = 12+x+dp;
		int[] args = {addr-x-dp};
		Core.mem.set(Size.BYTE, 0, addr-1, 1);
		Core.mem.set(Size.BYTE, 0, addr+0, 1);
		Core.mem.set(Size.BYTE, 0, addr+1, 1);
		Core.mem.set(Size.BYTE, 0, addr+2, 1);
		
		CPU.status.setMemoryAccess(true);//8bit mode
		CPU.x.setValue(x);
		CPU.dp.setValue(dp);
		CPU.doOp(0x74, args);
		
		assertEquals(addr, CPU.dataAddr);
		assertEquals(0, CPU.dataBank);
		assertEquals(0, CPU.dataReg.getValue());
		
		assertEquals(CPU.dataReg.getValue(), 0);
		assertEquals(1, Core.mem.get(Size.BYTE, 0, addr-1));
		assertEquals(0, Core.mem.get(Size.BYTE, 0, addr));
		assertEquals(1, Core.mem.get(Size.BYTE, 0, addr+1));
		assertEquals(1, Core.mem.get(Size.BYTE, 0, addr+2));
	}
	
	@Test
	public void storeZeroAbsolute16bit() {
		int addr = 0x1CDC;
		int dbr=3;
		int [] args = {0xDC, 0x1C};
		Core.mem.set(Size.BYTE, dbr, addr - 1, 1);
		Core.mem.set(Size.BYTE, dbr, addr + 0, 1);
		Core.mem.set(Size.BYTE, dbr, addr + 1, 1);
		Core.mem.set(Size.BYTE, dbr, addr + 2, 1);

		CPU.dbr.setValue(dbr);
		CPU.doOp(0x9C, args);
		
		assertEquals(0, CPU.dataReg.getValue());
		assertEquals(addr, CPU.dataAddr);
		assertEquals(dbr, CPU.dataBank);

		assertEquals(1, Core.mem.get(Size.BYTE, dbr, addr-1));
		assertEquals(0, Core.mem.get(Size.BYTE, dbr, addr+0));
		assertEquals(0, Core.mem.get(Size.BYTE, dbr, addr+1));
		assertEquals(1, Core.mem.get(Size.BYTE, dbr, addr+2));
	}
	@Test
	public void storeZeroAbsolute8bit() {
		int addr = 0x1CDC;
		int dbr=3;
		int [] args = {0xDC, 0x1C};
		Core.mem.set(Size.BYTE, dbr, addr - 1, 1);
		Core.mem.set(Size.BYTE, dbr, addr + 0, 1);
		Core.mem.set(Size.BYTE, dbr, addr + 1, 1);
		Core.mem.set(Size.BYTE, dbr, addr + 2, 1);

		CPU.dbr.setValue(dbr);
		CPU.status.setMemoryAccess(true);//8bitmode
		CPU.doOp(0x9C, args);
		
		assertEquals(0, CPU.dataReg.getValue());
		assertEquals(addr, CPU.dataAddr);
		assertEquals(dbr, CPU.dataBank);

		assertEquals(1, Core.mem.get(Size.BYTE, dbr, addr-1));
		assertEquals(0, Core.mem.get(Size.BYTE, dbr, addr+0));
		assertEquals(1, Core.mem.get(Size.BYTE, dbr, addr+1));
		assertEquals(1, Core.mem.get(Size.BYTE, dbr, addr+2));
	}
	
	
	@Test
	public void storeZeroDirectPage16bit() {
		int addr = 12;
		int dp = 3;
		int[] args = {addr - dp};
		Core.mem.set(Size.BYTE, 0, addr-1, 1);
		Core.mem.set(Size.BYTE, 0, addr+0, 1);
		Core.mem.set(Size.BYTE, 0, addr+1, 1);
		Core.mem.set(Size.BYTE, 0, addr+2, 1);
		
		CPU.dp.setValue(dp);
		CPU.doOp(0x64, args);
		
		assertEquals(addr, CPU.dataAddr);
		assertEquals(0, CPU.dataBank);
		assertEquals(0, CPU.dataReg.getValue());
		
		assertEquals(CPU.dataReg.getValue(), 0);
		assertEquals(1, Core.mem.get(Size.BYTE, 0, addr-1));
		assertEquals(0, Core.mem.get(Size.BYTE, 0, addr));
		assertEquals(0, Core.mem.get(Size.BYTE, 0, addr+1));
		assertEquals(1, Core.mem.get(Size.BYTE, 0, addr+2));
		
		dp = 0;
		args = new int[]{addr};
		CPU.dp.setValue(dp);
		CPU.doOp(0x64, args);
		
		assertEquals(addr, CPU.dataAddr);
		assertEquals(0, CPU.dataBank);
		assertEquals(0, CPU.dataReg.getValue());
		
		assertEquals(CPU.dataReg.getValue(), 0);
		assertEquals(1, Core.mem.get(Size.BYTE, 0, addr-1));
		assertEquals(0, Core.mem.get(Size.BYTE, 0, addr));
		assertEquals(0, Core.mem.get(Size.BYTE, 0, addr+1));
		assertEquals(1, Core.mem.get(Size.BYTE, 0, addr+2));
	}
	@Test
	public void storeZeroDirectPage8bit() {
		int addr = 12;
		int dp = 5;
		int[] args = {addr-dp};
		Core.mem.set(Size.BYTE, 0, addr-1, 1);
		Core.mem.set(Size.BYTE, 0, addr+0, 1);
		Core.mem.set(Size.BYTE, 0, addr+1, 1);
		
		CPU.status.setMemoryAccess(true);
		CPU.dp.setValue(dp);
		CPU.doOp(0x64, args);
		
		assertEquals(addr, CPU.dataAddr);
		assertEquals(0, CPU.dataBank);
		assertEquals(0, CPU.dataReg.getValue());
		
		assertEquals(CPU.dataReg.getValue(), 0);
		assertEquals(1, Core.mem.get(Size.BYTE, 0, addr-1));
		assertEquals(0, Core.mem.get(Size.BYTE, 0, addr));
		assertEquals(1, Core.mem.get(Size.BYTE, 0, addr+1));
	}
}
