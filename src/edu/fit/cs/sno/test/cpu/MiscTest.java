package edu.fit.cs.sno.test.cpu;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.cpu.instructions.Misc;
import edu.fit.cs.sno.snes.mem.LoROMMemory;
import edu.fit.cs.sno.util.Settings;

public class MiscTest {

	@Before
	public void setUp() throws Exception {
		new Misc(); // For 100% coverage
		Settings.init();
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
	}

	// 0xEA
	@Test
	public void nop() {
		CPU.doOp(0xEA, null);
		assertEquals(0, CPU.a.getValue());
		assertEquals(0, CPU.x.getValue());
		assertEquals(0, CPU.y.getValue());
		assertEquals(0, CPU.dataAddr);
		assertEquals(0, CPU.dataBank);
		assertEquals(0, CPU.dataReg.getValue());
		assertEquals(0, CPU.dbr.getValue());
		assertEquals(0, CPU.dp.getValue());
		assertFalse(CPU.emulationMode);
		assertEquals(0, CPU.pbr.getValue());
		assertEquals(0, CPU.pc.getValue());
		assertEquals(0x01FF, CPU.sp.getValue());
		assertEquals(0, CPU.status.getValue());
		assertFalse(CPU.indexCrossedPageBoundary);
	}
	
	//0x00
	//@Test
	public void softwareBreak() {
		fail("Not yet tested");
	}
	
	// 0xEB
	@Test
	public void exchangeBA() {
		int a = 0x0000;
		CPU.a.setValue(a);
		CPU.doOp(0xEB, null);
		
		assertEquals(((a & 0xFF) << 8) | ((a >> 8) & 0xFF), CPU.a.getValue());
		assertTrue(CPU.status.isZero());
		assertFalse(CPU.status.isNegative());
		
		a = 0xFF00;
		CPU.a.setValue(a);
		CPU.doOp(0xEB, null);
		
		assertEquals(((a & 0xFF) << 8) | ((a >> 8) & 0xFF), CPU.a.getValue());
		assertFalse(CPU.status.isZero());
		assertFalse(CPU.status.isNegative());
		
		a = 0x00FF;
		CPU.a.setValue(a);
		CPU.doOp(0xEB, null);
		
		assertEquals(((a & 0xFF) << 8) | ((a >> 8) & 0xFF), CPU.a.getValue());
		assertFalse(CPU.status.isZero());
		assertTrue(CPU.status.isNegative());
	}
}

