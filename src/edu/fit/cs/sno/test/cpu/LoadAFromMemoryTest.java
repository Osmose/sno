package edu.fit.cs.sno.test.cpu;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.cpu.instructions.LoadAFromMemory;
import edu.fit.cs.sno.snes.mem.LoROMMemory;
import edu.fit.cs.sno.util.Settings;

/**
 * Load A From Memory CPU Instruction tests
 */
public class LoadAFromMemoryTest extends TestCase {

	@Before
	public void setUp() {
		new LoadAFromMemory();// For 100% coverage
		Settings.init();
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
	}
	
	@Test
	public void testLoadADPIndexedIndirectX() {
		// Test operation
		CPU.a.setValue(0);
		CPU.dp.setValue(4);
		CPU.x.setValue(0x1000);
		Core.mem.set(Size.SHORT, 0, 0x1006, 0x1010);
		Core.mem.set(Size.SHORT, 0, 0x1010, 0xFF3A);
		CPU.doOp(0xA1, new int[]{2});
		assertEquals(CPU.a.getValue(), 0xFF3A);
		
		// Test Zero Flag
		CPU.a.setValue(1);
		CPU.dp.setValue(4);
		CPU.x.setValue(0x1000);
		Core.mem.set(Size.SHORT, 0, 0x1006, 0x1010);
		Core.mem.set(Size.SHORT, 0, 0x1010, 0);
		CPU.doOp(0xA1, new int[]{2});
		assertEquals(CPU.a.getValue(), 0);
		assertTrue(CPU.status.isZero());
		
		// Test Negative Flag
		CPU.a.setValue(0);
		CPU.dp.setValue(4);
		CPU.x.setValue(0x1000);
		Core.mem.set(Size.SHORT, 0, 0x1006, 0x1010);
		Core.mem.set(Size.SHORT, 0, 0x1010, 0x8000);
		CPU.doOp(0xA1, new int[]{2});
		assertEquals(CPU.a.getValue(), 0x8000);
		assertTrue(CPU.status.isNegative());
	}
	
	@Test 
	public void testLoadAStackRelative() {
		// Test operation
		CPU.a.setValue(0);
		CPU.sp.setValue(0x1006);
		Core.mem.set(Size.SHORT, 0, 0x100A, 0xFF3A);
		CPU.doOp(0xA3, new int[]{0x4});
		assertEquals(CPU.a.getValue(), 0xFF3A);
		
		// Test Zero Flag
		CPU.a.setValue(1);
		CPU.sp.setValue(0x1006);
		Core.mem.set(Size.SHORT, 0, 0x100A, 0);
		CPU.doOp(0xA3, new int[]{0x4});
		assertEquals(CPU.a.getValue(), 0);
		assertTrue(CPU.status.isZero());
		
		// Test Negative Flag
		CPU.a.setValue(0);
		CPU.sp.setValue(0x1006);
		Core.mem.set(Size.SHORT, 0, 0x100A, 0xFF3A);
		CPU.doOp(0xA3, new int[]{0x4});
		assertEquals(CPU.a.getValue(), 0xFF3A);
		assertTrue(CPU.status.isNegative());
	}
	
	@Test 
	public void testLoadADirectPage() {
		// Test operation
		CPU.a.setValue(0);
		CPU.dp.setValue(6);
		Core.mem.set(Size.SHORT, 0, 16, 5);
		CPU.doOp(0xA5, new int[]{10});
		assertEquals(CPU.a.getValue(), 5);
		
		// Test Zero Flag
		CPU.a.setValue(1);
		CPU.dp.setValue(6);
		Core.mem.set(Size.SHORT, 0, 16, 0);
		CPU.doOp(0xA5, new int[]{10});
		assertEquals(CPU.a.getValue(), 0);
		assertTrue(CPU.status.isZero());
		
		// Test Negative Flag
		CPU.a.setValue(0);
		CPU.dp.setValue(6);
		Core.mem.set(Size.SHORT, 0, 16, 0x8000);
		CPU.doOp(0xA5, new int[]{10});
		assertEquals(CPU.a.getValue(), 0x8000);
		assertTrue(CPU.status.isNegative());
	}
	
	@Test 
	public void testLoadADirectPageIndirectLong() {
		// Test operation
		CPU.a.setValue(0);
		CPU.dp.setValue(6);
		Core.mem.set(Size.BYTE, 0, 18, 0);
		Core.mem.set(Size.SHORT, 0, 16, 4);
		Core.mem.set(Size.SHORT, 0, 4, 5);
		CPU.doOp(0xA7, new int[]{10});
		assertEquals(CPU.a.getValue(), 5);
		
		// Test Zero Flag
		CPU.a.setValue(1);
		CPU.dp.setValue(6);
		Core.mem.set(Size.BYTE, 0, 18, 0);
		Core.mem.set(Size.SHORT, 0, 16, 4);
		Core.mem.set(Size.SHORT, 0, 4, 0);
		CPU.doOp(0xA7, new int[]{10});
		assertEquals(CPU.a.getValue(), 0);
		assertTrue(CPU.status.isZero());
		
		// Test Negative Flag
		CPU.a.setValue(0);
		CPU.dp.setValue(6);
		Core.mem.set(Size.BYTE, 0, 18, 0);
		Core.mem.set(Size.SHORT, 0, 16, 4);
		Core.mem.set(Size.SHORT, 0, 4, 0x8000);
		CPU.doOp(0xA7, new int[]{10});
		assertEquals(CPU.a.getValue(), 0x8000);
		assertTrue(CPU.status.isNegative());
	}
	
	@Test 
	public void testLoadAImmediate() {
		// Test operation
		CPU.a.setValue(0);
		CPU.doOp(0xA9, new int[]{0x3A, 0xFF});
		assertEquals(CPU.a.getValue(), 0xFF3A);
		
		// Test Zero Flag
		CPU.a.setValue(1);
		CPU.doOp(0xA9, new int[]{0, 0});
		assertEquals(CPU.a.getValue(), 0);
		assertTrue(CPU.status.isZero());
		
		// Test Negative Flag
		CPU.a.setValue(0);
		CPU.doOp(0xA9, new int[]{0, 0x80});
		assertEquals(CPU.a.getValue(), 0x8000);
		assertTrue(CPU.status.isNegative());
	}
	
	@Test 
	public void testLoadAAbsolute() {
		// Test operation
		CPU.dbr.setValue(0);
		CPU.a.setValue(0);
		Core.mem.set(Size.SHORT, 0, 0x1337, 5);
		CPU.doOp(0xAD, new int[]{0x37, 0x13});
		assertEquals(CPU.a.getValue(), 5);
		
		// Test Zero Flag
		CPU.dbr.setValue(0);
		CPU.a.setValue(1);
		Core.mem.set(Size.SHORT, 0, 0x1337, 0);
		CPU.doOp(0xAD, new int[]{0x37, 0x13});
		assertEquals(CPU.a.getValue(), 0);
		assertTrue(CPU.status.isZero());
		
		// Test Negative Flag
		CPU.dbr.setValue(0);
		CPU.a.setValue(0);
		Core.mem.set(Size.SHORT, 0, 0x1337, 0x8000);
		CPU.doOp(0xAD, new int[]{0x37, 0x13});
		assertEquals(CPU.a.getValue(), 0x8000);
		assertTrue(CPU.status.isNegative());
	}
	
	@Test 
	public void testLoadAAbsoluteLong() {
		// Test operation
		CPU.dbr.setValue(0);
		CPU.a.setValue(0);
		Core.mem.set(Size.SHORT, 0, 0x1337, 5);
		CPU.doOp(0xAF, new int[]{0x37, 0x13, 0});
		assertEquals(CPU.a.getValue(), 5);
		
		// Test Zero Flag
		CPU.dbr.setValue(0);
		CPU.a.setValue(1);
		Core.mem.set(Size.SHORT, 0, 0x1337, 0);
		CPU.doOp(0xAF, new int[]{0x37, 0x13, 0});
		assertEquals(CPU.a.getValue(), 0);
		assertTrue(CPU.status.isZero());
		
		// Test Negative Flag
		CPU.dbr.setValue(0);
		CPU.a.setValue(0);
		Core.mem.set(Size.SHORT, 0, 0x1337, 0x8000);
		CPU.doOp(0xAF, new int[]{0x37, 0x13, 0});
		assertEquals(CPU.a.getValue(), 0x8000);
		assertTrue(CPU.status.isNegative());
	}
	
	@Test
	public void testLoadADPIndexedIndirectY() {
		int iaddr = 0x1006;
		int addr = 0x1010;
		int y = 0x800;
		int dp = 4;
		// Test operation
		CPU.a.setValue(0);
		CPU.dp.setValue(dp);
		CPU.y.setValue(y);
		Core.mem.set(Size.SHORT, 0, iaddr, addr-y);
		Core.mem.set(Size.SHORT, 0, addr, 0xFF3A);
		CPU.doOp(0xB1, new int[]{iaddr-dp});
		assertEquals(0xFF3A, CPU.a.getValue());
		
		// Test Zero Flag
		CPU.a.setValue(1);
		CPU.dp.setValue(dp);
		CPU.y.setValue(y);
		Core.mem.set(Size.SHORT, 0, iaddr, addr-y);
		Core.mem.set(Size.SHORT, 0, addr, 0);
		CPU.doOp(0xB1, new int[]{iaddr-dp});
		assertEquals(CPU.a.getValue(), 0);
		assertTrue(CPU.status.isZero());
		
		// Test Negative Flag
		CPU.a.setValue(0);
		CPU.dp.setValue(dp);
		CPU.y.setValue(y);
		Core.mem.set(Size.SHORT, 0, iaddr, addr-y);
		Core.mem.set(Size.SHORT, 0, addr, 0x8000);
		CPU.doOp(0xB1, new int[]{iaddr-dp});
		assertEquals(CPU.a.getValue(), 0x8000);
		assertTrue(CPU.status.isNegative());
	}
	
	@Test
	public void testLoadADPIndirect() {
		// Test operation
		CPU.a.setValue(0);
		CPU.dp.setValue(4);
		Core.mem.set(Size.SHORT, 0, 0x1006, 0x1010);
		Core.mem.set(Size.SHORT, 0, 0x1010, 0xFF3A);
		CPU.doOp(0xB2, new int[]{0x1002});
		assertEquals(CPU.a.getValue(), 0xFF3A);
		
		// Test Zero Flag
		CPU.a.setValue(1);
		CPU.dp.setValue(4);
		Core.mem.set(Size.SHORT, 0, 0x1006, 0x1010);
		Core.mem.set(Size.SHORT, 0, 0x1010, 0);
		CPU.doOp(0xB2, new int[]{0x1002});
		assertEquals(CPU.a.getValue(), 0);
		assertTrue(CPU.status.isZero());
		
		// Test Negative Flag
		CPU.a.setValue(0);
		CPU.dp.setValue(4);
		Core.mem.set(Size.SHORT, 0, 0x1006, 0x1010);
		Core.mem.set(Size.SHORT, 0, 0x1010, 0x8000);
		CPU.doOp(0xB2, new int[]{0x1002});
		assertEquals(CPU.a.getValue(), 0x8000);
		assertTrue(CPU.status.isNegative());
	}
	
	@Test
	public void testLoadASRIndexedIndirectY() {
		// Test operation
		CPU.a.setValue(0);
		CPU.sp.setValue(4);
		CPU.dbr.setValue(0);
		CPU.y.setValue(0x1000);
		Core.mem.set(Size.SHORT, 0, 6, 0x10);
		Core.mem.set(Size.SHORT, 0, 0x1010, 0xFF3A);
		CPU.doOp(0xB3, new int[]{2});
		assertEquals(CPU.a.getValue(), 0xFF3A);
		
		// Test Zero Flag
		CPU.a.setValue(1);
		CPU.sp.setValue(4);
		CPU.dbr.setValue(0);
		CPU.y.setValue(0x1000);
		Core.mem.set(Size.SHORT, 0, 6, 0x10);
		Core.mem.set(Size.SHORT, 0, 0x1010, 0);
		CPU.doOp(0xB3, new int[]{2});
		assertEquals(CPU.a.getValue(), 0);
		assertTrue(CPU.status.isZero());
		
		// Test Negative Flag
		CPU.a.setValue(0);
		CPU.sp.setValue(4);
		CPU.dbr.setValue(0);
		CPU.y.setValue(0x1000);
		Core.mem.set(Size.SHORT, 0, 6, 0x10);
		Core.mem.set(Size.SHORT, 0, 0x1010, 0x8000);
		CPU.doOp(0xB3, new int[]{2});
		assertEquals(CPU.a.getValue(), 0x8000);
		assertTrue(CPU.status.isNegative());
	}
	
	@Test 
	public void testLoadADirectPageX() {
		// Test operation
		CPU.a.setValue(0);
		CPU.dp.setValue(6);
		CPU.x.setValue(3);
		Core.mem.set(Size.SHORT, 0, 19, 5);
		CPU.doOp(0xB5, new int[]{10});
		assertEquals(CPU.a.getValue(), 5);
		
		// Test Zero Flag
		CPU.a.setValue(1);
		CPU.dp.setValue(6);
		CPU.x.setValue(3);
		Core.mem.set(Size.SHORT, 0, 19, 0);
		CPU.doOp(0xB5, new int[]{10});
		assertEquals(CPU.a.getValue(), 0);
		assertTrue(CPU.status.isZero());
		
		// Test Negative Flag
		CPU.a.setValue(0);
		CPU.dp.setValue(6);
		CPU.x.setValue(3);
		Core.mem.set(Size.SHORT, 0, 19, 0x8000);
		CPU.doOp(0xB5, new int[]{10});
		assertEquals(CPU.a.getValue(), 0x8000);
		assertTrue(CPU.status.isNegative());
	}
	
	@Test
	public void testLoadADPIndirectLongIndexedY() {
		// Test operation
		CPU.a.setValue(0);
		CPU.dp.setValue(4);
		CPU.y.setValue(0x1000);
		Core.mem.set(Size.BYTE, 0, 8, 0);
		Core.mem.set(Size.SHORT, 0, 6, 0x10);
		Core.mem.set(Size.SHORT, 0, 0x1010, 0xFF3A);
		CPU.doOp(0xB7, new int[]{2});
		assertEquals(CPU.a.getValue(), 0xFF3A);
		
		// Test Zero Flag
		CPU.a.setValue(1);
		CPU.dp.setValue(4);
		CPU.y.setValue(0x1000);
		Core.mem.set(Size.BYTE, 0, 8, 0);
		Core.mem.set(Size.SHORT, 0, 6, 0x10);
		Core.mem.set(Size.SHORT, 0, 0x1010, 0);
		CPU.doOp(0xB7, new int[]{2});
		assertEquals(CPU.a.getValue(), 0);
		assertTrue(CPU.status.isZero());
		
		// Test Negative Flag
		CPU.a.setValue(0);
		CPU.dp.setValue(4);
		CPU.y.setValue(0x1000);
		Core.mem.set(Size.BYTE, 0, 8, 0);
		Core.mem.set(Size.SHORT, 0, 6, 0x10);
		Core.mem.set(Size.SHORT, 0, 0x1010, 0x8000);
		CPU.doOp(0xB7, new int[]{2});
		assertEquals(CPU.a.getValue(), 0x8000);
		assertTrue(CPU.status.isNegative());
	}
	
	@Test 
	public void testLoadAAbsoluteY() {
		// Test operation
		CPU.dbr.setValue(0);
		CPU.a.setValue(0);
		CPU.y.setValue(3);
		Core.mem.set(Size.SHORT, 0, 0x133A, 5);
		CPU.doOp(0xB9, new int[]{0x37, 0x13});
		assertEquals(CPU.a.getValue(), 5);
		
		// Test Zero Flag
		CPU.dbr.setValue(0);
		CPU.a.setValue(1);
		CPU.y.setValue(3);
		Core.mem.set(Size.SHORT, 0, 0x133A, 0);
		CPU.doOp(0xB9, new int[]{0x37, 0x13});
		assertEquals(CPU.a.getValue(), 0);
		assertTrue(CPU.status.isZero());
		
		// Test Negative Flag
		CPU.dbr.setValue(0);
		CPU.a.setValue(0);
		CPU.y.setValue(3);
		Core.mem.set(Size.SHORT, 0, 0x133A, 0x8000);
		CPU.doOp(0xB9, new int[]{0x37, 0x13});
		assertEquals(CPU.a.getValue(), 0x8000);
		assertTrue(CPU.status.isNegative());
	}
	
	@Test 
	public void testLoadAAbsoluteX() {
		// Test operation
		CPU.dbr.setValue(0);
		CPU.a.setValue(0);
		CPU.x.setValue(3);
		Core.mem.set(Size.SHORT, 0, 0x133A, 5);
		CPU.doOp(0xBD, new int[]{0x37, 0x13});
		assertEquals(CPU.a.getValue(), 5);
		
		// Test Zero Flag
		CPU.dbr.setValue(0);
		CPU.a.setValue(1);
		CPU.x.setValue(3);
		Core.mem.set(Size.SHORT, 0, 0x133A, 0);
		CPU.doOp(0xBD, new int[]{0x37, 0x13});
		assertEquals(CPU.a.getValue(), 0);
		assertTrue(CPU.status.isZero());
		
		// Test Negative Flag
		CPU.dbr.setValue(0);
		CPU.a.setValue(0);
		CPU.x.setValue(3);
		Core.mem.set(Size.SHORT, 0, 0x133A, 0x8000);
		CPU.doOp(0xBD, new int[]{0x37, 0x13});
		assertEquals(CPU.a.getValue(), 0x8000);
		assertTrue(CPU.status.isNegative());
	}
	
	@Test 
	public void testLoadAAbsoluteLongIndexedX() {
		// Test operation
		CPU.dbr.setValue(0);
		CPU.a.setValue(0);
		CPU.x.setValue(3);
		Core.mem.set(Size.SHORT, 1, 0x133A, 5);
		CPU.doOp(0xBF, new int[]{0x37, 0x13, 1});
		assertEquals(CPU.a.getValue(), 5);
		
		// Test Zero Flag
		CPU.dbr.setValue(0);
		CPU.a.setValue(1);
		CPU.x.setValue(3);
		Core.mem.set(Size.SHORT, 1, 0x133A, 0);
		CPU.doOp(0xBF, new int[]{0x37, 0x13, 1});
		assertEquals(CPU.a.getValue(), 0);
		assertTrue(CPU.status.isZero());
		
		// Test Negative Flag
		CPU.dbr.setValue(0);
		CPU.a.setValue(0);
		CPU.x.setValue(3);
		Core.mem.set(Size.SHORT, 1, 0x133A, 0x8000);
		CPU.doOp(0xBF, new int[]{0x37, 0x13, 1});
		assertEquals(CPU.a.getValue(), 0x8000);
		assertTrue(CPU.status.isNegative());
	}
}
