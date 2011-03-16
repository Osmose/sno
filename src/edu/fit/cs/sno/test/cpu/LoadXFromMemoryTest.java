package edu.fit.cs.sno.test.cpu;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.cpu.instructions.LoadXFromMemory;
import edu.fit.cs.sno.snes.mem.LoROMMemory;
import edu.fit.cs.sno.util.Settings;

/**
 * Load X From Memory CPU Instruction tests
 */
public class LoadXFromMemoryTest extends TestCase {

	@Before
	public void setUp() {
		new LoadXFromMemory();// For 100% coverage
		Settings.init();
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
	}
	
	@Test 
	public void testLoadXImmediate() {
		// Test operation
		CPU.x.setValue(0);
		CPU.doOp(0xA2, new int[]{0x3A, 0xFF});
		assertEquals(CPU.x.getValue(), 0xFF3A);
		
		// Test Zero Flag
		CPU.x.setValue(1);
		CPU.doOp(0xA2, new int[]{0, 0});
		assertEquals(CPU.x.getValue(), 0);
		assertTrue(CPU.status.isZero());
		
		// Test Negative Flag
		CPU.x.setValue(0);
		CPU.doOp(0xA2, new int[]{0, 0x80});
		assertEquals(CPU.x.getValue(), 0x8000);
		assertTrue(CPU.status.isNegative());
	}
	
	@Test 
	public void testLoadXImmediate2() {
		CPU.status.setIndexRegister(true);
		// Test operation
		CPU.x.setValue(0);
		CPU.doOp(0xA2, new int[]{0x3A});
		assertEquals(CPU.x.getValue(), 0x3A);
		
		// Test Zero Flag
		CPU.x.setValue(1);
		CPU.doOp(0xA2, new int[]{0});
		assertEquals(CPU.x.getValue(), 0);
		assertTrue(CPU.status.isZero());
		
		// Test Negative Flag
		CPU.x.setValue(0);
		CPU.doOp(0xA2, new int[]{0x80});
		assertEquals(CPU.x.getValue(), 0x80);
		assertTrue(CPU.status.isNegative());
	}
	
	@Test 
	public void testLoadXAbsolute() {
		// Test operation
		CPU.dbr.setValue(0);
		CPU.x.setValue(0);
		Core.mem.set(Size.SHORT, 0, 0x1337, 5);
		CPU.doOp(0xAE, new int[]{0x37, 0x13});
		assertEquals(CPU.x.getValue(), 5);
		
		// Test Zero Flag
		CPU.dbr.setValue(0);
		CPU.x.setValue(1);
		Core.mem.set(Size.SHORT, 0, 0x1337, 0);
		CPU.doOp(0xAE, new int[]{0x37, 0x13});
		assertEquals(CPU.x.getValue(), 0);
		assertTrue(CPU.status.isZero());
		
		// Test Negative Flag
		CPU.dbr.setValue(0);
		CPU.x.setValue(0);
		Core.mem.set(Size.SHORT, 0, 0x1337, 0x8000);
		CPU.doOp(0xAE, new int[]{0x37, 0x13});
		assertEquals(CPU.x.getValue(), 0x8000);
		assertTrue(CPU.status.isNegative());
	}
	
	@Test 
	public void testLoadXDirectPage() {
		// Test operation
		CPU.x.setValue(0);
		CPU.dp.setValue(6);
		Core.mem.set(Size.SHORT, 0, 16, 5);
		CPU.doOp(0xA6, new int[]{10});
		assertEquals(CPU.x.getValue(), 5);
		
		// Test Zero Flag
		CPU.x.setValue(1);
		CPU.dp.setValue(6);
		Core.mem.set(Size.SHORT, 0, 16, 0);
		CPU.doOp(0xA6, new int[]{10});
		assertEquals(CPU.x.getValue(), 0);
		assertTrue(CPU.status.isZero());
		
		// Test Negative Flag
		CPU.x.setValue(0);
		CPU.dp.setValue(6);
		Core.mem.set(Size.SHORT, 0, 16, 0x8000);
		CPU.doOp(0xA6, new int[]{10});
		assertEquals(CPU.x.getValue(), 0x8000);
		assertTrue(CPU.status.isNegative());
	}
	
	@Test 
	public void testLoadXAbsoluteY() {
		// Test operation
		CPU.dbr.setValue(0);
		CPU.x.setValue(0);
		CPU.y.setValue(3);
		Core.mem.set(Size.SHORT, 0, 0x133A, 5);
		CPU.doOp(0xBE, new int[]{0x37, 0x13});
		assertEquals(CPU.x.getValue(), 5);
		
		// Test Zero Flag
		CPU.dbr.setValue(0);
		CPU.x.setValue(1);
		CPU.y.setValue(3);
		Core.mem.set(Size.SHORT, 0, 0x133A, 0);
		CPU.doOp(0xBE, new int[]{0x37, 0x13});
		assertEquals(CPU.x.getValue(), 0);
		assertTrue(CPU.status.isZero());
		
		// Test Negative Flag
		CPU.dbr.setValue(0);
		CPU.x.setValue(0);
		CPU.y.setValue(3);
		Core.mem.set(Size.SHORT, 0, 0x133A, 0x8000);
		CPU.doOp(0xBE, new int[]{0x37, 0x13});
		assertEquals(CPU.x.getValue(), 0x8000);
		assertTrue(CPU.status.isNegative());
	}
	
	@Test 
	public void testLoadXDirectPageY() {
		// Test operation
		CPU.x.setValue(0);
		CPU.dp.setValue(6);
		CPU.y.setValue(3);
		Core.mem.set(Size.SHORT, 0, 19, 5);
		CPU.doOp(0xB6, new int[]{10});
		assertEquals(CPU.x.getValue(), 5);
		
		// Test Zero Flag
		CPU.x.setValue(1);
		CPU.dp.setValue(6);
		CPU.y.setValue(3);
		Core.mem.set(Size.SHORT, 0, 19, 0);
		CPU.doOp(0xB6, new int[]{10});
		assertEquals(CPU.x.getValue(), 0);
		assertTrue(CPU.status.isZero());
		
		// Test Negative Flag
		CPU.x.setValue(0);
		CPU.dp.setValue(6);
		CPU.y.setValue(3);
		Core.mem.set(Size.SHORT, 0, 19, 0x8000);
		CPU.doOp(0xB6, new int[]{10});
		assertEquals(CPU.x.getValue(), 0x8000);
		assertTrue(CPU.status.isNegative());
	}
	
}
