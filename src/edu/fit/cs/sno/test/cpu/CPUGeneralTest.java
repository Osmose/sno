package edu.fit.cs.sno.test.cpu;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.mem.LoROMMemory;
import edu.fit.cs.sno.util.Settings;

public class CPUGeneralTest extends TestCase {

	@Before
	public void setUp() {
		Settings.init();
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
	}
	
	@Test
	public void testStack() {
		CPU.sp.setValue(0xFF);
		
		// Test 8-bit
		int[] values = new int[]{4, 6, 3, 7, 8, 53, 16, 73};
		for (int k = 0; k < values.length; k++) {
			CPU.stackPush(Size.BYTE, values[k]);
		}
		
		for (int k = values.length - 1; k >= 0; k--) {
			assertEquals(values[k], CPU.stackPull(Size.BYTE));
		}
		
		// Test 16-bit
		int[] shortValues = new int[]{0x5E63, 0x76BB, 0x98AA, 0xFFFF, 0x0000, 0x4286, 0x16, 73};
		for (int k = 0; k < shortValues.length; k++) {
			CPU.stackPush(Size.SHORT, shortValues[k]);
		}
		
		for (int k = shortValues.length - 1; k >= 0; k--) {
			assertEquals(shortValues[k], CPU.stackPull(Size.SHORT));
		}
	}
	
}
