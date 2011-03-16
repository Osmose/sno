package edu.fit.cs.sno.test.cpu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.cpu.instructions.CompareYWithMemory;
import edu.fit.cs.sno.snes.mem.LoROMMemory;
import edu.fit.cs.sno.util.Settings;


public class CompareYWithMemoryTest {
	@Before
	public void setUp() throws Exception {
		new CompareYWithMemory(); // For 100% coverage
		Settings.init();
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
	}
	
	// 0xC0
	@Test
	public void cmpXImmediate() {
		int val = 0xACDC;
		int args[] = {0xDC, 0xAC};
		
		// Test x equal to value
		CPU.y.setValue(val);
		assertEquals(CPU.y.getValue(), val);
		CPU.doOp(0xC0, args);
		assertTrue(CPU.status.isZero());
		assertTrue(CPU.status.isCarry());
		assertFalse(CPU.status.isNegative());
		
		// Test with x less than value
		CPU.resetCPU();
		CPU.y.setValue(val - 5);
		CPU.doOp(0xC0, args);
		
		assertFalse(CPU.status.isZero());
		assertFalse(CPU.status.isCarry());
		assertTrue(CPU.status.isNegative());
		
		// Test with x greater than value
		CPU.resetCPU();
		CPU.y.setValue(val+5);
		CPU.doOp(0xC0, args);
		
		assertFalse(CPU.status.isZero());
		assertTrue(CPU.status.isCarry());
		assertFalse(CPU.status.isNegative());

		//======================
		// Test the 8bit version
		val = 0xCD;
		args = new int[]{0xCD};
		
		// Test x equal to value
		CPU.resetCPU();
		CPU.status.setIndexRegister(true);
		CPU.y.setValue(val);
		assertEquals(CPU.y.getValue(), val);
		CPU.doOp(0xC0, args);
		assertTrue(CPU.status.isZero());
		assertTrue(CPU.status.isCarry());
		assertFalse(CPU.status.isNegative());
		
		// Test with x less than value
		CPU.resetCPU();
		CPU.status.setIndexRegister(true);
		CPU.y.setValue(val - 5);
		CPU.doOp(0xC0, args);
		
		assertFalse(CPU.status.isZero());
		assertFalse(CPU.status.isCarry());
		assertTrue(CPU.status.isNegative());
		
		// Test with x greater than value
		CPU.resetCPU();
		CPU.status.setIndexRegister(true);
		CPU.y.setValue(val+5);
		CPU.doOp(0xC0, args);
		
		assertFalse(CPU.status.isZero());
		assertTrue(CPU.status.isCarry());
		assertFalse(CPU.status.isNegative());
	}
	
	// 0xC4
	@Test
	public void cmpXDP() {
		int val = 0xACDC;
		
		int addr = 0x1234;
		int offset = 0x57;
		int dp = addr - offset;
		int args[] = {offset};
		int bank = 3;
		Core.mem.set(Size.BYTE, bank, addr -1, 1);
		Core.mem.set(Size.SHORT, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr +2, 1);
		
		// Test x equal to value
		CPU.y.setValue(val);
		CPU.dp.setValue(dp);
		assertEquals(CPU.y.getValue(), val);
		CPU.doOp(0xC4, args);
		assertTrue(CPU.status.isZero());
		assertTrue(CPU.status.isCarry());
		assertFalse(CPU.status.isNegative());
		
		// Test with x less than value
		CPU.resetCPU();
		CPU.y.setValue(val - 5);
		CPU.dp.setValue(dp);
		CPU.doOp(0xC4, args);
		
		assertFalse(CPU.status.isZero());
		assertFalse(CPU.status.isCarry());
		assertTrue(CPU.status.isNegative());
		
		// Test with x greater than value
		CPU.resetCPU();
		CPU.y.setValue(val+5);
		CPU.dp.setValue(dp);
		CPU.doOp(0xC4, args);
		
		assertFalse(CPU.status.isZero());
		assertTrue(CPU.status.isCarry());
		assertFalse(CPU.status.isNegative());
		
		//======================
		// Test the 8bit version
		val = 0xCD;
		Core.mem = new LoROMMemory();
		Core.mem.set(Size.BYTE, bank, addr -1, 1);
		Core.mem.set(Size.BYTE, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr +1, 1);
		
		// Test x equal to value
		CPU.resetCPU();
		CPU.status.setIndexRegister(true);
		CPU.y.setValue(val);
		CPU.dp.setValue(dp);
		assertEquals(CPU.y.getValue(), val);
		CPU.doOp(0xC4, args);
		assertTrue(CPU.status.isZero());
		assertTrue(CPU.status.isCarry());
		assertFalse(CPU.status.isNegative());
		
		// Test with x less than value
		CPU.resetCPU();
		CPU.status.setIndexRegister(true);
		CPU.y.setValue(val - 5);
		CPU.dp.setValue(dp);
		CPU.doOp(0xC4, args);
		
		assertFalse(CPU.status.isZero());
		assertFalse(CPU.status.isCarry());
		assertTrue(CPU.status.isNegative());
		
		// Test with x greater than value
		CPU.resetCPU();
		CPU.status.setIndexRegister(true);
		CPU.y.setValue(val+5);
		CPU.dp.setValue(dp);
		CPU.doOp(0xC4, args);
		
		assertFalse(CPU.status.isZero());
		assertTrue(CPU.status.isCarry());
		assertFalse(CPU.status.isNegative());
	}
	
	// 0xCC
	@Test
	public void cmpXAbsolute() {
		int val = 0xACDC;
		int addr = 0x1234;
		int args[] = {0x34, 0x12};
		int bank = 3;
		Core.mem.set(Size.BYTE, bank, addr -1, 1);
		Core.mem.set(Size.SHORT, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr +2, 1);
		
		// Test x equal to value
		CPU.y.setValue(val);
		assertEquals(CPU.y.getValue(), val);
		CPU.doOp(0xCC, args);
		assertTrue(CPU.status.isZero());
		assertTrue(CPU.status.isCarry());
		assertFalse(CPU.status.isNegative());
		
		// Test with x less than value
		CPU.resetCPU();
		CPU.y.setValue(val - 5);
		CPU.doOp(0xCC, args);
		
		assertFalse(CPU.status.isZero());
		assertFalse(CPU.status.isCarry());
		assertTrue(CPU.status.isNegative());
		
		// Test with x greater than value
		CPU.resetCPU();
		CPU.y.setValue(val+5);
		CPU.doOp(0xCC, args);
		
		assertFalse(CPU.status.isZero());
		assertTrue(CPU.status.isCarry());
		assertFalse(CPU.status.isNegative());
		
		//======================
		// Test the 8bit version
		val = 0xCD;
		Core.mem = new LoROMMemory();
		Core.mem.set(Size.BYTE, bank, addr -1, 1);
		Core.mem.set(Size.BYTE, bank, addr, val);
		Core.mem.set(Size.BYTE, bank, addr +1, 1);
		
		// Test x equal to value
		CPU.resetCPU();
		CPU.status.setIndexRegister(true);
		CPU.y.setValue(val);
		assertEquals(CPU.y.getValue(), val);
		CPU.doOp(0xCC, args);
		assertTrue(CPU.status.isZero());
		assertTrue(CPU.status.isCarry());
		assertFalse(CPU.status.isNegative());
		
		// Test with x less than value
		CPU.resetCPU();
		CPU.status.setIndexRegister(true);
		CPU.y.setValue(val - 5);
		CPU.doOp(0xCC, args);
		
		assertFalse(CPU.status.isZero());
		assertFalse(CPU.status.isCarry());
		assertTrue(CPU.status.isNegative());
		
		// Test with x greater than value
		CPU.resetCPU();
		CPU.status.setIndexRegister(true);
		CPU.y.setValue(val+5);
		CPU.doOp(0xCC, args);
		
		assertFalse(CPU.status.isZero());
		assertTrue(CPU.status.isCarry());
		assertFalse(CPU.status.isNegative());
	}
}
