package edu.fit.cs.sno.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.util.Settings;


public class MiscTest {
	@Before
	public void setUp() {
		Settings.init();
		CPU.resetCPU();
	}

	@Test
	public void testCPUStatic() {
		CPU.dataBank = 5;
		assertEquals(CPU.dataBank, 5);
	}
	
	@Test
	public void testCPUStatic02() {
		assertEquals(0, CPU.dataBank);
	}
}
