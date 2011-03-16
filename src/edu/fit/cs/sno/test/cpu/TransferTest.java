package edu.fit.cs.sno.test.cpu;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.common.Register;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.cpu.instructions.Transfer;
import edu.fit.cs.sno.snes.mem.LoROMMemory;
import edu.fit.cs.sno.util.Settings;

/**
 * Decrement CPU Instruction tests
 */
public class TransferTest extends TestCase {

	@Before
	public void setUp() {
		new Transfer();// For 100% coverage
		Settings.init();
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
	}
	
	private void transferTestGeneral(Register from, Register to, int opCode) {
		// Test operation
		to.setValue(0);
		from.setValue(2);
		CPU.doOp(opCode, null);
		assertEquals(to.getValue(), 2);
		
		// Test Zero Flag
		to.setValue(2);
		from.setValue(0);
		CPU.doOp(opCode, null);
		assertEquals(to.getValue(), 0);
		assertTrue(CPU.status.isZero());
		
		// Test Negative Flag
		to.setValue(0);
		from.setValue(0x8000);
		CPU.doOp(opCode, null);
		assertEquals(to.getValue(), 0x8000);
		assertTrue(CPU.status.isNegative());
	}
	
	@Test 
	public void testTransferAtoX() {
		transferTestGeneral(CPU.a, CPU.x, 0xAA);
	}
	
	@Test 
	public void testTransferAtoY() {
		transferTestGeneral(CPU.a, CPU.y, 0xA8);
	}
	
	@Test 
	public void testTransferAtoDP() {
		transferTestGeneral(CPU.a, CPU.dp, 0x5B);
	}
	
	@Test 
	public void testTransferAtoSP() {
		// Test operation
		CPU.sp.setValue(0);
		CPU.a.setValue(2);
		CPU.doOp(0x1B, null);
		assertEquals(CPU.sp.getValue(), 2);
	}
	
	@Test 
	public void testTransferDPtoA() {
		transferTestGeneral(CPU.dp, CPU.a, 0x7B);
	}
	
	@Test 
	public void testTransferSPtoA() {
		transferTestGeneral(CPU.sp, CPU.a, 0x3B);
	}
	
	@Test 
	public void testTransferSPtoX() {
		transferTestGeneral(CPU.sp, CPU.x, 0xBA);
	}
	
	@Test 
	public void testTransferXtoA() {
		transferTestGeneral(CPU.x, CPU.a, 0x8A);
	}
	
	@Test 
	public void testTransferXtoSP() {
		// Test operation
		CPU.sp.setValue(0);
		CPU.x.setValue(2);
		CPU.doOp(0x9A, null);
		assertEquals(CPU.sp.getValue(), 2);
	}
	
	@Test 
	public void testTransferXtoY() {
		transferTestGeneral(CPU.x, CPU.y, 0x9B);
	}
	
	@Test 
	public void testTransferYtoA() {
		transferTestGeneral(CPU.y, CPU.a, 0x98);
	}
	
	@Test 
	public void testTransferYtoX() {
		transferTestGeneral(CPU.y, CPU.x, 0xBB);
	}
}
