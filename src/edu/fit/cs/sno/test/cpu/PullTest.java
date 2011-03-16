package edu.fit.cs.sno.test.cpu;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.cpu.instructions.Pull;
import edu.fit.cs.sno.snes.mem.LoROMMemory;
import edu.fit.cs.sno.util.Settings;

public class PullTest {

	@Before
	public void setUp() throws Exception {
		new Pull(); // For 100% coverage
		Settings.init();
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
	}
	
	@Test
	public void pullAccumulator() {
		int data = 0x6CDC;
		int addr = 0x13AC;
		int bank = 3;
		int args[] = null;
		Core.mem.set(Size.BYTE, bank, addr-2, 1);
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, data);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.sp.setValue(addr-1);
		CPU.a.setValue(1);
		CPU.doOp(0x68, args);
		
		assertEquals(addr+1, CPU.sp.getValue());
		assertEquals(data, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		// Test Negative
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		data = 0x85AF;
		Core.mem.set(Size.BYTE, bank, addr-2, 1);
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, data);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.sp.setValue(addr-1);
		CPU.a.setValue(1);
		CPU.doOp(0x68, args);
		
		assertEquals(addr+1, CPU.sp.getValue());
		assertEquals(data, CPU.a.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		// Test zero
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		data = 0;
		Core.mem.set(Size.BYTE, bank, addr-2, 1);
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, data);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.sp.setValue(addr-1);
		CPU.a.setValue(1);
		CPU.doOp(0x68, args);
		
		assertEquals(addr+1, CPU.sp.getValue());
		assertEquals(data, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
		
		// Test 8bit
		data = 0x4F;
		Core.mem.set(Size.BYTE, bank, addr-2, 1);
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, data);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.sp.setValue(addr-1);
		CPU.a.setValue(1);
		CPU.status.setMemoryAccess(true);
		CPU.doOp(0x68, args);
		
		assertEquals(addr, CPU.sp.getValue());
		assertEquals(data, CPU.a.getValue());
		assertFalse(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
	}
	
	@Test
	public void pullDataBank() {
		int data = 0x73;
		int addr = 0x13AC;
		int bank = 3;
		int args[] = null;
		Core.mem.set(Size.BYTE, bank, addr-2, 1);
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, data);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.sp.setValue(addr-1);
		CPU.dbr.setValue(1);
		CPU.doOp(0xAB, args);
		
		assertEquals(addr, CPU.sp.getValue());
		assertEquals(data, CPU.dbr.getValue());
		assertFalse(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		// Test Negative
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		data = 0xF8;
		Core.mem.set(Size.BYTE, bank, addr-2, 1);
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, data);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.sp.setValue(addr-1);
		CPU.dbr.setValue(1);
		CPU.doOp(0xAB, args);
		
		assertEquals(addr, CPU.sp.getValue());
		assertEquals(data, CPU.dbr.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		// Test zero
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		data = 0x00;
		Core.mem.set(Size.BYTE, bank, addr-2, 1);
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, data);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.sp.setValue(addr-1);
		CPU.dbr.setValue(1);
		CPU.doOp(0xAB, args);
		
		assertEquals(addr, CPU.sp.getValue());
		assertEquals(data, CPU.dbr.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}
	
	@Test
	public void pullDirectPage() {
		int data = 0x6CDC;
		int addr = 0x13AC;
		int bank = 3;
		int args[] = null;
		Core.mem.set(Size.BYTE, bank, addr-2, 1);
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, data);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.sp.setValue(addr-1);
		CPU.dp.setValue(1);
		CPU.doOp(0x2B, args);
		
		assertEquals(addr+1, CPU.sp.getValue());
		assertEquals(data, CPU.dp.getValue());
		assertFalse(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		// Test Negative
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		data = 0x85AF;
		Core.mem.set(Size.BYTE, bank, addr-2, 1);
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, data);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.sp.setValue(addr-1);
		CPU.dp.setValue(1);
		CPU.doOp(0x2B, args);
		
		assertEquals(addr+1, CPU.sp.getValue());
		assertEquals(data, CPU.dp.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		// Test zero
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		data = 0x00;
		Core.mem.set(Size.BYTE, bank, addr-2, 1);
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, data);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.sp.setValue(addr-1);
		CPU.dp.setValue(1);
		CPU.doOp(0x2B, args);
		
		assertEquals(addr+1, CPU.sp.getValue());
		assertEquals(data, CPU.dp.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
	}
	
	@Test
	public void pullStatus() {
		int data = 0x73;
		int addr = 0x13AC;
		int bank = 3;
		int args[] = null;
		Core.mem.set(Size.BYTE, bank, addr-2, 1);
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, data);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.sp.setValue(addr-1);
		CPU.status.setValue(1);
		CPU.doOp(0x28, args);
		
		assertEquals(addr, CPU.sp.getValue());
		assertEquals(data, CPU.status.getValue());
		
		// A second test
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		data = 0xF8;
		Core.mem.set(Size.BYTE, bank, addr-2, 1);
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, data);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.sp.setValue(addr-1);
		CPU.status.setValue(1);
		CPU.doOp(0x28, args);
		
		assertEquals(addr, CPU.sp.getValue());
		assertEquals(data, CPU.status.getValue());
		
		
		// A third test
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		data = 0x00;
		Core.mem.set(Size.BYTE, bank, addr-2, 1);
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, data);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.sp.setValue(addr-1);
		CPU.status.setValue(1);
		CPU.doOp(0x28, args);
		
		assertEquals(addr, CPU.sp.getValue());
		assertEquals(data, CPU.status.getValue());
	}
	
	@Test
	public void pullX() {
		int data = 0x6CDC;
		int addr = 0x13AC;
		int bank = 3;
		int args[] = null;
		Core.mem.set(Size.BYTE, bank, addr-2, 1);
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, data);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.sp.setValue(addr-1);
		CPU.x.setValue(1);
		CPU.doOp(0xFA, args);
		
		assertEquals(addr+1, CPU.sp.getValue());
		assertEquals(data, CPU.x.getValue());
		assertFalse(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		// Test Negative
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		data = 0x85AF;
		Core.mem.set(Size.BYTE, bank, addr-2, 1);
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, data);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.sp.setValue(addr-1);
		CPU.x.setValue(1);
		CPU.doOp(0xFA, args);
		
		assertEquals(addr+1, CPU.sp.getValue());
		assertEquals(data, CPU.x.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		// Test zero
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		data = 0;
		Core.mem.set(Size.BYTE, bank, addr-2, 1);
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, data);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.sp.setValue(addr-1);
		CPU.x.setValue(1);
		CPU.doOp(0xFA, args);
		
		assertEquals(addr+1, CPU.sp.getValue());
		assertEquals(data, CPU.x.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
		
		// Test 8bit
		data = 0x4F;
		Core.mem.set(Size.BYTE, bank, addr-2, 1);
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, data);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.sp.setValue(addr-1);
		CPU.x.setValue(1);
		CPU.status.setIndexRegister(true);
		CPU.doOp(0xFA, args);
		
		assertEquals(addr, CPU.sp.getValue());
		assertEquals(data, CPU.x.getValue());
		assertFalse(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
	}
	
	@Test
	public void pullY() {
		int data = 0x6CDC;
		int addr = 0x13AC;
		int bank = 3;
		int args[] = null;
		Core.mem.set(Size.BYTE, bank, addr-2, 1);
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, data);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.sp.setValue(addr-1);
		CPU.y.setValue(1);
		CPU.doOp(0x7A, args);
		
		assertEquals(addr+1, CPU.sp.getValue());
		assertEquals(data, CPU.y.getValue());
		assertFalse(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		// Test Negative
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		data = 0x85AF;
		Core.mem.set(Size.BYTE, bank, addr-2, 1);
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, data);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.sp.setValue(addr-1);
		CPU.y.setValue(1);
		CPU.doOp(0x7A, args);
		
		assertEquals(addr+1, CPU.sp.getValue());
		assertEquals(data, CPU.y.getValue());
		assertTrue(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
		
		// Test zero
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		data = 0;
		Core.mem.set(Size.BYTE, bank, addr-2, 1);
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, data);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.sp.setValue(addr-1);
		CPU.y.setValue(1);
		CPU.doOp(0x7A, args);
		
		assertEquals(addr+1, CPU.sp.getValue());
		assertEquals(data, CPU.y.getValue());
		assertFalse(CPU.status.isNegative());
		assertTrue(CPU.status.isZero());
		
		// Test 8bit
		data = 0x4F;
		Core.mem.set(Size.BYTE, bank, addr-2, 1);
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, data);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		Core.mem.set(Size.BYTE, bank, addr+2, 1);
		
		CPU.sp.setValue(addr-1);
		CPU.y.setValue(1);
		CPU.status.setIndexRegister(true);
		CPU.doOp(0x7A, args);
		
		assertEquals(addr, CPU.sp.getValue());
		assertEquals(data, CPU.y.getValue());
		assertFalse(CPU.status.isNegative());
		assertFalse(CPU.status.isZero());
	}
}
