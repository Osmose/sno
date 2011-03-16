package edu.fit.cs.sno.test.cpu;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.cpu.instructions.Return;
import edu.fit.cs.sno.snes.mem.LoROMMemory;
import edu.fit.cs.sno.util.Settings;

public class ReturnTest {

	@Before
	public void setUp() throws Exception {
		new Return();
		Settings.init();
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
	}
	
	// TODO: verify the order of items on the stack
	
	// 0x40
	@Test
	public void returnFromInterrupt() {
		int bank = 0x3B;
		int addr = 0x348A;
		int status = 0x7F;

		
		// Test when not in emulation mode
		CPU.sp.setValue(0xFF);
		CPU.stackPush(Size.BYTE, status);
		CPU.stackPush(Size.BYTE, bank);
		CPU.stackPush(Size.SHORT, addr);
		CPU.doOp(0x40, null);
		
		assertEquals(addr, CPU.pc.getValue());
		assertEquals(bank, CPU.pbr.getValue());
		assertEquals(status, CPU.status.getValue());
		assertEquals(0xFF,CPU.sp.getValue());
		
		// Test when in emulation mode(the program bank is not pushed when this is the case)
		CPU.resetCPU();
		CPU.emulationMode = true;
		CPU.sp.setValue(0xFF);
		CPU.pbr.setValue(bank);
		CPU.stackPush(Size.BYTE, status);
		CPU.stackPush(Size.SHORT, addr);
		CPU.doOp(0x40, null);
		
		assertEquals(addr, CPU.pc.getValue());
		assertEquals(bank, CPU.pbr.getValue());
		assertEquals(status, CPU.status.getValue());
		assertEquals(0xFF,CPU.sp.getValue());
	}
	
	// 0x6B
	@Test
	public void returnFromSubroutineLong() {
		int bank = 0x3B;
		int addr = 0x348A;
		
		// Test when not in emulation mode
		CPU.sp.setValue(0xFF);
		CPU.stackPush(Size.BYTE, bank);
		CPU.stackPush(Size.SHORT, addr-1);
		CPU.doOp(0x6B, null);
		
		assertEquals(addr, CPU.pc.getValue());
		assertEquals(bank, CPU.pbr.getValue());
		assertEquals(0xFF,CPU.sp.getValue());
		
		// Test when in emulation mode(should function the same)
		CPU.resetCPU();
		CPU.emulationMode = true;
		CPU.sp.setValue(0xFF);
		CPU.stackPush(Size.BYTE, bank);
		CPU.stackPush(Size.SHORT, addr-1);
		CPU.doOp(0x6B, null);
		
		assertEquals(addr, CPU.pc.getValue());
		assertEquals(bank, CPU.pbr.getValue());
		assertEquals(0xFF,CPU.sp.getValue());
	}

	// 0x60
	@Test
	public void returnFromSubroutine() {
		int addr = 0x348A;
		
		// Test when not in emulation mode
		CPU.sp.setValue(0xFF);
		CPU.stackPush(Size.SHORT, addr-1);
		CPU.doOp(0x60, null);
		
		assertEquals(addr, CPU.pc.getValue());
		assertEquals(0xFF,CPU.sp.getValue());
		
		// Test when in emulation mode(should function the same)
		CPU.resetCPU();
		CPU.emulationMode = true;
		CPU.sp.setValue(0xFF);
		CPU.stackPush(Size.SHORT, addr-1);
		CPU.doOp(0x60, null);
		
		assertEquals(addr, CPU.pc.getValue());
		assertEquals(0xFF,CPU.sp.getValue());
	}

}
