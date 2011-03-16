package edu.fit.cs.sno.test.cpu;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.cpu.instructions.Jump;
import edu.fit.cs.sno.snes.mem.LoROMMemory;
import edu.fit.cs.sno.util.Settings;

public class JumpTest {

	@Before
	public void setUp() throws Exception {
		new Jump(); // For 100% coverage
		Settings.init();
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
	}

	@Test
	public void jumpAbsolute() {
		int addr = 0x1345;
		int args[] = { addr & 0xFF, (addr >> 8) & 0xFF };

		CPU.pc.setValue(0x4FDC);
		CPU.pbr.setValue(5);
		CPU.doOp(0x4C, args);

		assertEquals(addr, CPU.pc.getValue());
		assertEquals(5, CPU.pbr.getValue());
	}

	@Test
	public void jumpAbsoluteIndexedIndirect() {
		int iaddr = 0x1345;
		int addr = 0x084F;
		int dbr = 5;
		int x = 0x44;
		int args[] = { iaddr & 0xFF-x, (iaddr >> 8) & 0xFF };

		Core.mem.set(Size.BYTE, dbr, iaddr - 1, 1);
		Core.mem.set(Size.SHORT, dbr, iaddr, addr);
		Core.mem.set(Size.BYTE, dbr, iaddr + 2, 1);

		CPU.pc.setValue(0x4FDC);
		CPU.pbr.setValue(5);
		CPU.x.setValue(x);
		CPU.doOp(0x7C, args);

		assertEquals(addr, CPU.pc.getValue());
		assertEquals(5, CPU.pbr.getValue());
	}

	@Test
	public void jumpAbsoluteIndirect() {
		// 0x6C
		int iaddr = 0x1345;
		int addr = 0x084F;
		int pbr = 5;
		int x = 0x44;
		int args[] = { iaddr & 0xFF, (iaddr >> 8) & 0xFF };

		Core.mem.set(Size.BYTE, pbr, iaddr - 1, 1);
		Core.mem.set(Size.SHORT, pbr, iaddr, addr);
		Core.mem.set(Size.BYTE, pbr, iaddr + 2, 1);

		CPU.pc.setValue(0x4FDC);
		CPU.pbr.setValue(pbr);
		CPU.doOp(0x6C, args);

		assertEquals(addr, CPU.pc.getValue());
		assertEquals(pbr, CPU.pbr.getValue());
	}

	@Test
	public void jumpAbsoluteIndirectLong() {
		// 0xDC
		int iaddr = 0x1345;
		int addr = 0x084F;
		int pbr = 0x05;
		int bank = 0x22;
		int x = 0x44;
		int args[] = { iaddr & 0xFF, (iaddr >> 8) & 0xFF };

		// Has to be bank 0 for indirect addressing
		Core.mem.set(Size.BYTE, 0, iaddr - 1, 1);
		Core.mem.set(Size.SHORT, 0, iaddr, addr);
		Core.mem.set(Size.BYTE, 0, iaddr + 2, bank);
		Core.mem.set(Size.BYTE, 0, iaddr + 3, 1);

		CPU.pc.setValue(0x4FDC);
		CPU.pbr.setValue(pbr);
		CPU.doOp(0xDC, args);

		assertEquals(addr, CPU.pc.getValue());
		assertEquals(bank, CPU.pbr.getValue());
	}

	@Test
	public void jumpAbsoluteLong() {
		// 0x5C
		int addr = 0x1345;
		int pbr = 0x10;
		int args[] = { addr & 0xFF, (addr >> 8) & 0xFF, pbr };

		CPU.pc.setValue(0x4FDC);
		CPU.pbr.setValue(5);
		CPU.doOp(0x5C, args);

		assertEquals(addr, CPU.pc.getValue());
		assertEquals(pbr, CPU.pbr.getValue());
	}
	
	@Test
	public void jumpSubAbsolute() {
		// 0x20
		int addr = 0x1345;
		int pbr = 0x10;
		int sp = 0x84;
		int pc = 0x4FDC;
		int args[] = { addr & 0xFF, (addr >> 8) & 0xFF };

		// Put some stuff on the stack...
		Core.mem.set(Size.BYTE, 0, sp-4, 1);
		Core.mem.set(Size.BYTE, 0, sp-3, 1);
		Core.mem.set(Size.BYTE, 0, sp-2, 1);
		Core.mem.set(Size.BYTE, 0, sp-1, 1);
		Core.mem.set(Size.BYTE, 0, sp+0, 1);
		Core.mem.set(Size.BYTE, 0, sp+1, 1);
		Core.mem.set(Size.BYTE, 0, sp+2, 1);
		Core.mem.set(Size.BYTE, 0, sp+3, 1);
		
		CPU.pc.setValue(0x4FDC);
		CPU.pbr.setValue(pbr);
		CPU.sp.setValue(sp);
		CPU.doOp(0x20, args);

		assertEquals(addr, CPU.pc.getValue());
		assertEquals(pbr,  CPU.pbr.getValue());
		assertEquals(sp-2, CPU.sp.getValue()); // 16bits pushed
		
		pc -= 1; // Per the spec
		assertEquals(1,                Core.mem.get(Size.BYTE, 0, sp-4));
		assertEquals(1,                Core.mem.get(Size.BYTE, 0, sp-3));
		assertEquals(1,                Core.mem.get(Size.BYTE, 0, sp-2));
		assertEquals(pc & 0xFF,        Core.mem.get(Size.BYTE, 0, sp-1));
		assertEquals((pc >> 8) & 0xFF, Core.mem.get(Size.BYTE, 0, sp+0));
		assertEquals(1,                Core.mem.get(Size.BYTE, 0, sp+1));
		assertEquals(1,                Core.mem.get(Size.BYTE, 0, sp+2));
	}
	
	@Test
	public void jumpSubAbsoluteIndexedIndirect() {
		// 0xFC
		int iaddr = 0x1345;
		int addr = 0x084F;
		int pbr = 5;
		int sp = 0x84;
		int pc = 0x4FDC;
		int x = 0x05;
		int args[] = { iaddr & 0xFF - x, (iaddr >> 8) & 0xFF };

		Core.mem.set(Size.BYTE, pbr, iaddr - 1, 1);
		Core.mem.set(Size.SHORT, pbr, iaddr, addr);
		Core.mem.set(Size.BYTE, pbr, iaddr + 2, 1);

		// Put some stuff on the stack...
		Core.mem.set(Size.BYTE, 0, sp-4, 1);
		Core.mem.set(Size.BYTE, 0, sp-3, 1);
		Core.mem.set(Size.BYTE, 0, sp-2, 1);
		Core.mem.set(Size.BYTE, 0, sp-1, 1);
		Core.mem.set(Size.BYTE, 0, sp+0, 1);
		Core.mem.set(Size.BYTE, 0, sp+1, 1);
		Core.mem.set(Size.BYTE, 0, sp+2, 1);
		Core.mem.set(Size.BYTE, 0, sp+3, 1);
		
		CPU.pc.setValue(0x4FDC);
		CPU.pbr.setValue(pbr);
		CPU.sp.setValue(sp);
		CPU.x.setValue(x);
		CPU.doOp(0xFC, args);

		assertEquals(addr, CPU.pc.getValue());
		assertEquals(pbr, CPU.pbr.getValue());
		
		pc -= 1; // Per the spec
		assertEquals(1,                Core.mem.get(Size.BYTE, 0, sp-4));
		assertEquals(1,                Core.mem.get(Size.BYTE, 0, sp-3));
		assertEquals(1,                Core.mem.get(Size.BYTE, 0, sp-2));
		assertEquals(pc & 0xFF,        Core.mem.get(Size.BYTE, 0, sp-1));
		assertEquals((pc >> 8) & 0xFF, Core.mem.get(Size.BYTE, 0, sp+0));
		assertEquals(1,                Core.mem.get(Size.BYTE, 0, sp+1));
		assertEquals(1,                Core.mem.get(Size.BYTE, 0, sp+2));
	}
	
	@Test
	public void jumpSubLong() {
		// 0x22
		int addr = 0x1345;
		int pbr = 0x10;
		int old_pbr = 0x05;
		int sp = 0x84;
		int pc = 0x4FDC;
		int bank = 0;
		int args[] = { addr & 0xFF, (addr >> 8) & 0xFF, pbr };

		// Put some stuff on the stack...
		Core.mem.set(Size.BYTE, 0, sp-4, 1);
		Core.mem.set(Size.BYTE, 0, sp-3, 1);
		Core.mem.set(Size.BYTE, 0, sp-2, 1);
		Core.mem.set(Size.BYTE, 0, sp-1, 1);
		Core.mem.set(Size.BYTE, 0, sp+0, 1);
		Core.mem.set(Size.BYTE, 0, sp+1, 1);
		Core.mem.set(Size.BYTE, 0, sp+2, 1);
		Core.mem.set(Size.BYTE, 0, sp+3, 1);
		
		CPU.pc.setValue(0x4FDC);
		CPU.pbr.setValue(old_pbr);
		CPU.sp.setValue(sp);
		CPU.doOp(0x22, args);

		assertEquals(addr, CPU.pc.getValue());
		assertEquals(pbr,  CPU.pbr.getValue());
		assertEquals(sp-3, CPU.sp.getValue()); // 24bits pushed
		
		pc -= 1; // Per the spec
		assertEquals(1,                Core.mem.get(Size.BYTE, 0, sp-4));
		assertEquals(1,                Core.mem.get(Size.BYTE, 0, sp-3));
		assertEquals(pc & 0xFF,        Core.mem.get(Size.BYTE, 0, sp-2));
		assertEquals((pc >> 8) & 0xFF, Core.mem.get(Size.BYTE, 0, sp-1));
		assertEquals(old_pbr,          Core.mem.get(Size.BYTE, 0, sp+0));
		assertEquals(1,                Core.mem.get(Size.BYTE, 0, sp+1));
		assertEquals(1,                Core.mem.get(Size.BYTE, 0, sp+2));
	}
}
