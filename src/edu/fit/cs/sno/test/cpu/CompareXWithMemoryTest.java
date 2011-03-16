package edu.fit.cs.sno.test.cpu;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.cpu.instructions.CompareXWithMemory;
import edu.fit.cs.sno.snes.mem.LoROMMemory;
import edu.fit.cs.sno.util.Settings;

public class CompareXWithMemoryTest {

	@Before
	public void setUp() throws Exception {
		new CompareXWithMemory(); // For 100% coverage
		Settings.init();
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
	}
	
	// 0xE0
	@Test
	public void cmpXImmediate() {
		int val = 0xACDC;
		int args[] = {0xDC, 0xAC};
		
		// Test x equal to value
		CPU.x.setValue(val);
		assertEquals(CPU.x.getValue(), val);
		CPU.doOp(0xE0, args);
		assertTrue(CPU.status.isZero());
		assertTrue(CPU.status.isCarry());
		assertFalse(CPU.status.isNegative());
		
		// Test with x less than value
		CPU.resetCPU();
		CPU.x.setValue(val - 5);
		CPU.doOp(0xE0, args);
		
		assertFalse(CPU.status.isZero());
		assertFalse(CPU.status.isCarry());
		assertTrue(CPU.status.isNegative());
		
		// Test with x greater than value
		CPU.resetCPU();
		CPU.x.setValue(val+5);
		CPU.doOp(0xE0, args);
		
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
		CPU.x.setValue(val);
		assertEquals(CPU.x.getValue(), val);
		CPU.doOp(0xE0, args);
		assertTrue(CPU.status.isZero());
		assertTrue(CPU.status.isCarry());
		assertFalse(CPU.status.isNegative());
		
		// Test with x less than value
		CPU.resetCPU();
		CPU.status.setIndexRegister(true);
		CPU.x.setValue(val - 5);
		CPU.doOp(0xE0, args);
		
		assertFalse(CPU.status.isZero());
		assertFalse(CPU.status.isCarry());
		assertTrue(CPU.status.isNegative());
		
		// Test with x greater than value
		CPU.resetCPU();
		CPU.status.setIndexRegister(true);
		CPU.x.setValue(val+5);
		CPU.doOp(0xE0, args);
		
		assertFalse(CPU.status.isZero());
		assertTrue(CPU.status.isCarry());
		assertFalse(CPU.status.isNegative());
	}
	
	// 0xE4
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
		CPU.x.setValue(val);
		CPU.dp.setValue(dp);
		assertEquals(CPU.x.getValue(), val);
		CPU.doOp(0xE4, args);
		assertTrue(CPU.status.isZero());
		assertTrue(CPU.status.isCarry());
		assertFalse(CPU.status.isNegative());
		
		// Test with x less than value
		CPU.resetCPU();
		CPU.x.setValue(val - 5);
		CPU.dp.setValue(dp);
		CPU.doOp(0xE4, args);
		
		assertFalse(CPU.status.isZero());
		assertFalse(CPU.status.isCarry());
		assertTrue(CPU.status.isNegative());
		
		// Test with x greater than value
		CPU.resetCPU();
		CPU.x.setValue(val+5);
		CPU.dp.setValue(dp);
		CPU.doOp(0xE4, args);
		
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
		CPU.x.setValue(val);
		CPU.dp.setValue(dp);
		assertEquals(CPU.x.getValue(), val);
		CPU.doOp(0xE4, args);
		assertTrue(CPU.status.isZero());
		assertTrue(CPU.status.isCarry());
		assertFalse(CPU.status.isNegative());
		
		// Test with x less than value
		CPU.resetCPU();
		CPU.status.setIndexRegister(true);
		CPU.x.setValue(val - 5);
		CPU.dp.setValue(dp);
		CPU.doOp(0xE4, args);
		
		assertFalse(CPU.status.isZero());
		assertFalse(CPU.status.isCarry());
		assertTrue(CPU.status.isNegative());
		
		// Test with x greater than value
		CPU.resetCPU();
		CPU.status.setIndexRegister(true);
		CPU.x.setValue(val+5);
		CPU.dp.setValue(dp);
		CPU.doOp(0xE4, args);
		
		assertFalse(CPU.status.isZero());
		assertTrue(CPU.status.isCarry());
		assertFalse(CPU.status.isNegative());
	}
	
	// 0xEC
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
		CPU.x.setValue(val);
		assertEquals(CPU.x.getValue(), val);
		CPU.doOp(0xEC, args);
		assertTrue(CPU.status.isZero());
		assertTrue(CPU.status.isCarry());
		assertFalse(CPU.status.isNegative());
		
		// Test with x less than value
		CPU.resetCPU();
		CPU.x.setValue(val - 5);
		CPU.doOp(0xEC, args);
		
		assertFalse(CPU.status.isZero());
		assertFalse(CPU.status.isCarry());
		assertTrue(CPU.status.isNegative());
		
		// Test with x greater than value
		CPU.resetCPU();
		CPU.x.setValue(val+5);
		CPU.doOp(0xEC, args);
		
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
		CPU.x.setValue(val);
		assertEquals(CPU.x.getValue(), val);
		CPU.doOp(0xEC, args);
		assertTrue(CPU.status.isZero());
		assertTrue(CPU.status.isCarry());
		assertFalse(CPU.status.isNegative());
		
		// Test with x less than value
		CPU.resetCPU();
		CPU.status.setIndexRegister(true);
		CPU.x.setValue(val - 5);
		CPU.doOp(0xEC, args);
		
		assertFalse(CPU.status.isZero());
		assertFalse(CPU.status.isCarry());
		assertTrue(CPU.status.isNegative());
		
		// Test with x greater than value
		CPU.resetCPU();
		CPU.status.setIndexRegister(true);
		CPU.x.setValue(val+5);
		CPU.doOp(0xEC, args);
		
		assertFalse(CPU.status.isZero());
		assertTrue(CPU.status.isCarry());
		assertFalse(CPU.status.isNegative());
	}
}
