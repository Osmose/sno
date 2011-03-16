package edu.fit.cs.sno.test.cpu;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.cpu.instructions.LoadYFromMemory;
import edu.fit.cs.sno.snes.mem.LoROMMemory;
import edu.fit.cs.sno.util.Settings;

/**
 * Load Y From Memory CPU Instruction tests
 */
public class LoadYFromMemoryTest extends TestCase {

	@Before
	public void setUp() {
		new LoadYFromMemory();// For 100% coverage
		Settings.init();
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
	}
	
	@Test 
	public void testLoadYImmediate() {
		// Test operation
		CPU.y.setValue(0);
		CPU.doOp(0xA0, new int[]{0x3A, 0xFF});
		assertEquals(CPU.y.getValue(), 0xFF3A);
		
		// Test Zero Flag
		CPU.y.setValue(1);
		CPU.doOp(0xA0, new int[]{0, 0});
		assertEquals(CPU.y.getValue(), 0);
		assertTrue(CPU.status.isZero());
		
		// Test Negative Flag
		CPU.y.setValue(0);
		CPU.doOp(0xA0, new int[]{0, 0x80});
		assertEquals(CPU.y.getValue(), 0x8000);
		assertTrue(CPU.status.isNegative());
	}
	
	@Test 
	public void testLoadYAbsolute() {
		// Test operation
		CPU.dbr.setValue(0);
		CPU.y.setValue(0);
		Core.mem.set(Size.SHORT, 0, 0x1337, 5);
		CPU.doOp(0xAC, new int[]{0x37, 0x13});
		assertEquals(CPU.y.getValue(), 5);
		
		// Test Zero Flag
		CPU.dbr.setValue(0);
		CPU.y.setValue(1);
		Core.mem.set(Size.SHORT, 0, 0x1337, 0);
		CPU.doOp(0xAC, new int[]{0x37, 0x13});
		assertEquals(CPU.y.getValue(), 0);
		assertTrue(CPU.status.isZero());
		
		// Test Negative Flag
		CPU.dbr.setValue(0);
		CPU.y.setValue(0);
		Core.mem.set(Size.SHORT, 0, 0x1337, 0x8000);
		CPU.doOp(0xAC, new int[]{0x37, 0x13});
		assertEquals(CPU.y.getValue(), 0x8000);
		assertTrue(CPU.status.isNegative());
	}
	
	@Test 
	public void testLoadYDirectPage() {
		// Test operation
		CPU.y.setValue(0);
		CPU.dp.setValue(6);
		Core.mem.set(Size.SHORT, 0, 16, 5);
		CPU.doOp(0xA4, new int[]{10});
		assertEquals(CPU.y.getValue(), 5);
		
		// Test Zero Flag
		CPU.y.setValue(1);
		CPU.dp.setValue(6);
		Core.mem.set(Size.SHORT, 0, 16, 0);
		CPU.doOp(0xA4, new int[]{10});
		assertEquals(CPU.y.getValue(), 0);
		assertTrue(CPU.status.isZero());
		
		// Test Negative Flag
		CPU.y.setValue(0);
		CPU.dp.setValue(6);
		Core.mem.set(Size.SHORT, 0, 16, 0x8000);
		CPU.doOp(0xA4, new int[]{10});
		assertEquals(CPU.y.getValue(), 0x8000);
		assertTrue(CPU.status.isNegative());
	}
	
	@Test 
	public void testLoadYAbsoluteX() {
		// Test operation
		CPU.dbr.setValue(0);
		CPU.y.setValue(0);
		CPU.x.setValue(3);
		Core.mem.set(Size.SHORT, 0, 0x133A, 5);
		CPU.doOp(0xBC, new int[]{0x37, 0x13});
		assertEquals(CPU.y.getValue(), 5);
		
		// Test Zero Flag
		CPU.dbr.setValue(0);
		CPU.y.setValue(1);
		CPU.x.setValue(3);
		Core.mem.set(Size.SHORT, 0, 0x133A, 0);
		CPU.doOp(0xBC, new int[]{0x37, 0x13});
		assertEquals(CPU.y.getValue(), 0);
		assertTrue(CPU.status.isZero());
		
		// Test Negative Flag
		CPU.dbr.setValue(0);
		CPU.y.setValue(0);
		CPU.x.setValue(3);
		Core.mem.set(Size.SHORT, 0, 0x133A, 0x8000);
		CPU.doOp(0xBC, new int[]{0x37, 0x13});
		assertEquals(CPU.y.getValue(), 0x8000);
		assertTrue(CPU.status.isNegative());
	}
	
	@Test 
	public void testLoadYDirectPageX() {
		// Test operation
		CPU.y.setValue(0);
		CPU.dp.setValue(6);
		CPU.x.setValue(3);
		Core.mem.set(Size.SHORT, 0, 19, 5);
		CPU.doOp(0xB4, new int[]{10});
		assertEquals(CPU.y.getValue(), 5);
		
		// Test Zero Flag
		CPU.y.setValue(1);
		CPU.dp.setValue(6);
		CPU.x.setValue(3);
		Core.mem.set(Size.SHORT, 0, 19, 0);
		CPU.doOp(0xB4, new int[]{10});
		assertEquals(CPU.y.getValue(), 0);
		assertTrue(CPU.status.isZero());
		
		// Test Negative Flag
		CPU.y.setValue(0);
		CPU.dp.setValue(6);
		CPU.x.setValue(3);
		Core.mem.set(Size.SHORT, 0, 19, 0x8000);
		CPU.doOp(0xB4, new int[]{10});
		assertEquals(CPU.y.getValue(), 0x8000);
		assertTrue(CPU.status.isNegative());
	}
	
}
