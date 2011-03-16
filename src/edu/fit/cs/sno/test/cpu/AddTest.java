package edu.fit.cs.sno.test.cpu;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.cpu.instructions.Add;
import edu.fit.cs.sno.snes.mem.LoROMMemory;
import edu.fit.cs.sno.util.Settings;
import edu.fit.cs.sno.util.Util;

public class AddTest {
	// XXX: Does not attempt to test 8bit anything
	private static final int BASEADDR = 0x12DA;
	private static final int IBASEADDR = 0x1378;
	
	private static final int VAL16BIT[] = {0x80F0, 0x8000, 0x7FFF, 0x0000, 0x1000, 0x7FFF, 0xFFFE};
	private static final int ADD16BIT[] = {0x1234, 0x8000, 0x0005, 0x5000, 0xFFFF, 0x0000, 0x0001};
	
	private static final int BCDVAL16BIT[] = {0x1112, 0x8000, 0x0000, 0x9999, 0x0005, 0x9998};
	private static final int BCDADD16BIT[] = {0x8888, 0x8000, 0x0000, 0x0004, 0x9999, 0x0001};
	
	@Before
	public void setUp() throws Exception {
		new Add(); // For 100% coverage
		Settings.init();
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
	}

	public void verifyAdd16Bit(int origValue, int additive, int oldFlags, int newFlags) {
		String tstr = String.format("Adding 0x%04X to 0x%04X%s: ",additive, origValue, ((oldFlags & 0x08) == 0x08)? " in BCD mode":"");
		if ((oldFlags & 0x08) != 0x08) { // Binary mode
			int oracle = origValue + additive + (oldFlags & 0x01); // add original value, carry flag, and additive
			int carry = (oracle >> 16) & 0x01;
			oracle &= 0xFFFF;
			assertEquals(tstr, oracle, CPU.a.getValue());
			
			assertEquals(tstr, (CPU.a.getValue() & 0x8000) == 0x8000, (newFlags & 0x80) == 0x80);
			assertEquals(tstr, CPU.a.getValue() == 0x0000, (newFlags & 0x02) == 0x02);
			
			// Determine if signed overflow occurred
			boolean signedOverflow = false;
			if (((origValue & 0x8000) == 0x8000) && ((additive & 0x8000) == 0x8000)) {
				if ((oracle & 0x8000) == 0x8000) { // result is still negative, no overflow
					signedOverflow = false;
				} else { // result was positive, sign overflow
					signedOverflow = true;
				}
			} else if (((origValue & 0x8000) != 0x8000) && ((additive & 0x8000) != 0x8000)) {
				// If both were positive, but summed to a negative, signed overflow occured
				if ((oracle & 0x8000) == 0x8000) { // result was negative, sign overflow
					signedOverflow = true;
				} else { // result is still positive, no sign overflow
					signedOverflow = false;
				}
			}
			
			// Determine if unsigned overflow occurred
			boolean unsignedOverflow = false;
			if (carry == 1)
				unsignedOverflow = true;
			
			assertEquals(tstr, signedOverflow, (newFlags & 0x40)==0x40);
			assertEquals(tstr, unsignedOverflow, (newFlags & 0x01)==0x01);
			
		} else { // Decimal mode
			//fail("BCD Test not yet implemented");
			
			
			int a = Util.bcdAdjustAdd(Size.SHORT, origValue);
			int b = Util.bcdAdjustAdd(Size.SHORT, additive);
			int carry = 0;
			if (a+b+(oldFlags & 0x01) > 0x9999) carry = 1;
			
			int oracle = Util.bcdAdjustAdd(Size.SHORT, origValue + additive + (oldFlags & 0x01))&0xFFFF; // add original value, carry flag, and additive
			assertEquals(tstr, oracle, CPU.a.getValue());
			
			assertEquals(tstr, (CPU.a.getValue() & 0x8000) == 0x8000, (newFlags & 0x80) == 0x80);
			assertEquals(tstr, CPU.a.getValue() == 0x0000, (newFlags & 0x02) == 0x02);
			
			boolean signedOverflow = false; // Always False with BCD math
			
			// Determine if unsigned overflow occurred
			boolean unsignedOverflow = false;
			if (carry == 1)
				unsignedOverflow = true;
			
			assertEquals(tstr, signedOverflow, (newFlags & 0x40)==0x40);
			assertEquals(tstr, unsignedOverflow, (newFlags & 0x01)==0x01);
		}
	}
	
	// 0x69
	@Test
	public void addImmediate16Bit() {
		int opcode = 0x69;
		
		int bank = 5;
		int oldFlags;
		
		// Regular test
		for (int i=0; i<VAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			
			int a = VAL16BIT[i];
			int additive = ADD16BIT[i];
			int args[] = {additive & 0xFF, (additive >>8)&0xFF};
		
			CPU.a.setValue(a);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		
		// Regular test with carry
		for (int i=0; i<VAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setCarry(true);
			
			int a = VAL16BIT[i];
			int additive = ADD16BIT[i];
			int args[] = {additive & 0xFF, (additive >>8)&0xFF};
			
			CPU.a.setValue(a);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		
		// Binary coded decimal test
		for (int i=0; i<BCDVAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setDecimalMode(true);
			
			int a = BCDVAL16BIT[i];
			int additive = BCDADD16BIT[i];
			int args[] = {additive & 0xFF, (additive >>8)&0xFF};
			
			CPU.a.setValue(a);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		// Binary coded decimal test with carry
		for (int i=0; i<BCDVAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setDecimalMode(true);
			CPU.status.setCarry(true);
			
			int a = BCDVAL16BIT[i];
			int additive = BCDADD16BIT[i];
			int args[] = {additive & 0xFF, (additive >>8)&0xFF};
			
			CPU.a.setValue(a);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
	}
	
	// 0x61
	@Test
	public void addDPIndirectX16Bit() {
		int opcode = 0x61;
		int addr = BASEADDR;
		int iaddr = IBASEADDR;
		int x = 0x1024;
		
		int arg = 0x71;
		int args[] = {0x71};
		int dp = iaddr - x - arg;
		int bank = 5;
		
		int oldFlags;
		
		// Regular test
		for (int i=0; i<VAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			
			int a = VAL16BIT[i];
			int additive = ADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, iaddr-1, 1);
			Core.mem.set(Size.SHORT, bank, iaddr, addr);
			Core.mem.set(Size.BYTE, bank, iaddr+2, 1);
			
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.x.setValue(x);
			CPU.dp.setValue(dp);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		
		// Regular test with carry
		for (int i=0; i<VAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setCarry(true);
			
			int a = VAL16BIT[i];
			int additive = ADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, iaddr-1, 1);
			Core.mem.set(Size.SHORT, bank, iaddr, addr);
			Core.mem.set(Size.BYTE, bank, iaddr+2, 1);
			
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.x.setValue(x);
			CPU.dp.setValue(dp);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		
		// Binary coded decimal test
		for (int i=0; i<BCDVAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setDecimalMode(true);
			
			int a = BCDVAL16BIT[i];
			int additive = BCDADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, iaddr-1, 1);
			Core.mem.set(Size.SHORT, bank, iaddr, addr);
			Core.mem.set(Size.BYTE, bank, iaddr+2, 1);
			
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.x.setValue(x);
			CPU.dp.setValue(dp);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		// Binary coded decimal test with carry
		for (int i=0; i<BCDVAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setDecimalMode(true);
			CPU.status.setCarry(true);
			
			int a = BCDVAL16BIT[i];
			int additive = BCDADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, iaddr-1, 1);
			Core.mem.set(Size.SHORT, bank, iaddr, addr);
			Core.mem.set(Size.BYTE, bank, iaddr+2, 1);
			
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.x.setValue(x);
			CPU.dp.setValue(dp);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
	}
	
	
	// 0x63
	@Test
	public void addStackRelative16Bit() {
		int opcode = 0x63;
		int addr = BASEADDR;
		
		int arg = 0x71;
		int args[] = {0x71};
		int sp = addr - arg;
		int bank = 0; // only bank 0
		
		int oldFlags;
		
		// Regular test
		for (int i=0; i<VAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			
			int a = VAL16BIT[i];
			int additive = ADD16BIT[i];
			
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.sp.setValue(sp);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		
		// Regular test with carry
		for (int i=0; i<VAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setCarry(true);
			
			int a = VAL16BIT[i];
			int additive = ADD16BIT[i];
			
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.sp.setValue(sp);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		
		// Binary coded decimal test
		for (int i=0; i<BCDVAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setDecimalMode(true);
			
			int a = BCDVAL16BIT[i];
			int additive = BCDADD16BIT[i];

			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.sp.setValue(sp);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		// Binary coded decimal test with carry
		for (int i=0; i<BCDVAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setDecimalMode(true);
			CPU.status.setCarry(true);
			
			int a = BCDVAL16BIT[i];
			int additive = BCDADD16BIT[i];
			
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.sp.setValue(sp);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
	}
	
	// 0x65
	@Test
	public void addDP16Bit() {
		int opcode = 0x65;
		int addr = BASEADDR;
		
		int arg = 0x71;
		int args[] = {0x71};
		int dp = addr - arg;
		int bank = 0; // only in bank 0
		
		int oldFlags;
		
		// Regular test
		for (int i=0; i<VAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			
			int a = VAL16BIT[i];
			int additive = ADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.dp.setValue(dp);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		
		// Regular test with carry
		for (int i=0; i<VAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setCarry(true);
			
			int a = VAL16BIT[i];
			int additive = ADD16BIT[i];
			
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.dp.setValue(dp);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		
		// Binary coded decimal test
		for (int i=0; i<BCDVAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setDecimalMode(true);
			
			int a = BCDVAL16BIT[i];
			int additive = BCDADD16BIT[i];
			
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.dp.setValue(dp);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		// Binary coded decimal test with carry
		for (int i=0; i<BCDVAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setDecimalMode(true);
			CPU.status.setCarry(true);
			
			int a = BCDVAL16BIT[i];
			int additive = BCDADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.dp.setValue(dp);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
	}
	
	// 0x67
	@Test
	public void addDPIndirectLong16Bit() {
		int opcode = 0x67;
		int addr = BASEADDR;
		int iaddr = IBASEADDR;
		
		int bank = 0x05;
		int ibank = 0;// must be zero
		
		int arg = 0x71;
		int args[] = {0x71};
		int dp = iaddr - arg;
		
		
		int oldFlags;
		
		// Regular test
		for (int i=0; i<VAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			
			int a = VAL16BIT[i];
			int additive = ADD16BIT[i];
		
			Core.mem.set(Size.BYTE, ibank, iaddr-1, 1);
			Core.mem.set(Size.SHORT, ibank, iaddr, addr);
			Core.mem.set(Size.BYTE, ibank, iaddr+2, bank);
			Core.mem.set(Size.BYTE, ibank, iaddr+3, 1);
			
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.dp.setValue(dp);
			CPU.dbr.setValue(ibank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		
		// Regular test with carry
		for (int i=0; i<VAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setCarry(true);
			
			int a = VAL16BIT[i];
			int additive = ADD16BIT[i];
		
			Core.mem.set(Size.BYTE, ibank, iaddr-1, 1);
			Core.mem.set(Size.SHORT, ibank, iaddr, addr);
			Core.mem.set(Size.BYTE, ibank, iaddr+2, bank);
			Core.mem.set(Size.BYTE, ibank, iaddr+3, 1);
			
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.dp.setValue(dp);
			CPU.dbr.setValue(ibank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		
		// Binary coded decimal test
		for (int i=0; i<BCDVAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setDecimalMode(true);
			
			int a = BCDVAL16BIT[i];
			int additive = BCDADD16BIT[i];
		
			Core.mem.set(Size.BYTE, ibank, iaddr-1, 1);
			Core.mem.set(Size.SHORT, ibank, iaddr, addr);
			Core.mem.set(Size.BYTE, ibank, iaddr+2, bank);
			Core.mem.set(Size.BYTE, ibank, iaddr+3, 1);
			
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.dp.setValue(dp);
			CPU.dbr.setValue(ibank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		// Binary coded decimal test with carry
		for (int i=0; i<BCDVAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setDecimalMode(true);
			CPU.status.setCarry(true);
			
			int a = BCDVAL16BIT[i];
			int additive = BCDADD16BIT[i];
		
			Core.mem.set(Size.BYTE, ibank, iaddr-1, 1);
			Core.mem.set(Size.SHORT, ibank, iaddr, addr);
			Core.mem.set(Size.BYTE, ibank, iaddr+2, bank);
			Core.mem.set(Size.BYTE, ibank, iaddr+3, 1);
			
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.dp.setValue(dp);
			CPU.dbr.setValue(ibank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
	}
	
	// 0x6D
	@Test
	public void addAbsolute16Bit() {
		int opcode = 0x6D;
		
		int addr = BASEADDR;
		
		int args[] = {addr & 0xFF, (addr >>8)&0xFF};
		int bank = 5;
		
		int oldFlags;
		
		// Regular test
		for (int i=0; i<VAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			
			int a = VAL16BIT[i];
			int additive = ADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		
		// Regular test with carry
		for (int i=0; i<VAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setCarry(true);
			
			int a = VAL16BIT[i];
			int additive = ADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		
		// Binary coded decimal test
		for (int i=0; i<BCDVAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setDecimalMode(true);
			
			int a = BCDVAL16BIT[i];
			int additive = BCDADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		// Binary coded decimal test with carry
		for (int i=0; i<BCDVAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setDecimalMode(true);
			CPU.status.setCarry(true);
			
			int a = BCDVAL16BIT[i];
			int additive = BCDADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
	}
	
	// 0x6F
	@Test
	public void addAbsoluteLong16Bit() {
		int opcode = 0x6F;
		
		int addr = BASEADDR;
		
		int bank = 5;
		int args[] = {addr & 0xFF, (addr >>8)&0xFF, bank};
		
		
		int oldFlags;
		
		// Regular test
		for (int i=0; i<VAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			
			int a = VAL16BIT[i];
			int additive = ADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.dbr.setValue(0);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		
		// Regular test with carry
		for (int i=0; i<VAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setCarry(true);
			
			int a = VAL16BIT[i];
			int additive = ADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.dbr.setValue(0);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		
		// Binary coded decimal test
		for (int i=0; i<BCDVAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setDecimalMode(true);
			
			int a = BCDVAL16BIT[i];
			int additive = BCDADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.dbr.setValue(0);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		// Binary coded decimal test with carry
		for (int i=0; i<BCDVAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setDecimalMode(true);
			CPU.status.setCarry(true);
			
			int a = BCDVAL16BIT[i];
			int additive = BCDADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.dbr.setValue(0);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
	}

	// 0x71
	@Test
	public void addDPIndirectY16Bit() {
		int opcode = 0x71;
		int addr = BASEADDR;
		int iaddr = IBASEADDR;
		int y = 0x1024;
		
		int arg = 0x71;
		int args[] = {0x71};
		int dp = iaddr - arg;
		int bank = 5;
		
		int oldFlags;
		
		// Regular test
		for (int i=0; i<VAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			
			int a = VAL16BIT[i];
			int additive = ADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, iaddr-1, 1);
			Core.mem.set(Size.SHORT, bank, iaddr, addr-y);
			Core.mem.set(Size.BYTE, bank, iaddr+2, 1);
			
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.y.setValue(y);
			CPU.dp.setValue(dp);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		
		// Regular test with carry
		for (int i=0; i<VAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setCarry(true);
			
			int a = VAL16BIT[i];
			int additive = ADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, iaddr-1, 1);
			Core.mem.set(Size.SHORT, bank, iaddr, addr-y);
			Core.mem.set(Size.BYTE, bank, iaddr+2, 1);
			
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.y.setValue(y);
			CPU.dp.setValue(dp);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		
		// Binary coded decimal test
		for (int i=0; i<BCDVAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setDecimalMode(true);
			
			int a = BCDVAL16BIT[i];
			int additive = BCDADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, iaddr-1, 1);
			Core.mem.set(Size.SHORT, bank, iaddr, addr-y);
			Core.mem.set(Size.BYTE, bank, iaddr+2, 1);
			
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.y.setValue(y);
			CPU.dp.setValue(dp);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		// Binary coded decimal test with carry
		for (int i=0; i<BCDVAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setDecimalMode(true);
			CPU.status.setCarry(true);
			
			int a = BCDVAL16BIT[i];
			int additive = BCDADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, iaddr-1, 1);
			Core.mem.set(Size.SHORT, bank, iaddr, addr-y);
			Core.mem.set(Size.BYTE, bank, iaddr+2, 1);
			
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.y.setValue(y);
			CPU.dp.setValue(dp);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
	}

	// 0x72
	@Test
	public void addDPIndirect16Bit() {
		int opcode = 0x72;
		int addr = BASEADDR;
		int iaddr = IBASEADDR;
		
		int arg = 0x71;
		int args[] = {0x71};
		int dp = iaddr - arg;
		int bank = 5;
		
		int oldFlags;
		
		// Regular test
		for (int i=0; i<VAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			
			int a = VAL16BIT[i];
			int additive = ADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, iaddr-1, 1);
			Core.mem.set(Size.SHORT, bank, iaddr, addr);
			Core.mem.set(Size.BYTE, bank, iaddr+2, 1);
			
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.dp.setValue(dp);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		
		// Regular test with carry
		for (int i=0; i<VAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setCarry(true);
			
			int a = VAL16BIT[i];
			int additive = ADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, iaddr-1, 1);
			Core.mem.set(Size.SHORT, bank, iaddr, addr);
			Core.mem.set(Size.BYTE, bank, iaddr+2, 1);
			
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.dp.setValue(dp);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		
		// Binary coded decimal test
		for (int i=0; i<BCDVAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setDecimalMode(true);
			
			int a = BCDVAL16BIT[i];
			int additive = BCDADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, iaddr-1, 1);
			Core.mem.set(Size.SHORT, bank, iaddr, addr);
			Core.mem.set(Size.BYTE, bank, iaddr+2, 1);
			
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.dp.setValue(dp);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		// Binary coded decimal test with carry
		for (int i=0; i<BCDVAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setDecimalMode(true);
			CPU.status.setCarry(true);
			
			int a = BCDVAL16BIT[i];
			int additive = BCDADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, iaddr-1, 1);
			Core.mem.set(Size.SHORT, bank, iaddr, addr);
			Core.mem.set(Size.BYTE, bank, iaddr+2, 1);
			
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.dp.setValue(dp);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
	}
	
	// 0x73
	@Test
	public void addSRIndirectY16Bit() {
		int opcode = 0x73;
		int addr = BASEADDR;
		int iaddr = IBASEADDR;
		int y = 0x1024;
		
		int arg = 0x71;
		int args[] = {0x71};
		int sp = iaddr - arg;
		int bank = 0; // bank must be zero
		
		int oldFlags;
		
		// Regular test
		for (int i=0; i<VAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			
			int a = VAL16BIT[i];
			int additive = ADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, iaddr-1, 1);
			Core.mem.set(Size.SHORT, bank, iaddr, addr-y);
			Core.mem.set(Size.BYTE, bank, iaddr+2, 1);
			
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.y.setValue(y);
			CPU.sp.setValue(sp);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		
		// Regular test with carry
		for (int i=0; i<VAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setCarry(true);
			
			int a = VAL16BIT[i];
			int additive = ADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, iaddr-1, 1);
			Core.mem.set(Size.SHORT, bank, iaddr, addr-y);
			Core.mem.set(Size.BYTE, bank, iaddr+2, 1);
			
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.y.setValue(y);
			CPU.sp.setValue(sp);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		
		// Binary coded decimal test
		for (int i=0; i<BCDVAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setDecimalMode(true);
			
			int a = BCDVAL16BIT[i];
			int additive = BCDADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, iaddr-1, 1);
			Core.mem.set(Size.SHORT, bank, iaddr, addr-y);
			Core.mem.set(Size.BYTE, bank, iaddr+2, 1);
			
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.y.setValue(y);
			CPU.sp.setValue(sp);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		// Binary coded decimal test with carry
		for (int i=0; i<BCDVAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setDecimalMode(true);
			CPU.status.setCarry(true);
			
			int a = BCDVAL16BIT[i];
			int additive = BCDADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, iaddr-1, 1);
			Core.mem.set(Size.SHORT, bank, iaddr, addr-y);
			Core.mem.set(Size.BYTE, bank, iaddr+2, 1);
			
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.y.setValue(y);
			CPU.sp.setValue(sp);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
	}
	
	// 0x75
	@Test
	public void addDPIndexedX16Bit() {
		int opcode = 0x75;
		int addr = BASEADDR;
		int x = 0x1024;
		
		int arg = 0x71;
		int args[] = {0x71};
		int dp = addr - x - arg;
		int bank = 0; // must be zero
		
		int oldFlags;
		
		// Regular test
		for (int i=0; i<VAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			
			int a = VAL16BIT[i];
			int additive = ADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.x.setValue(x);
			CPU.dp.setValue(dp);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		
		// Regular test with carry
		for (int i=0; i<VAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setCarry(true);
			
			int a = VAL16BIT[i];
			int additive = ADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.x.setValue(x);
			CPU.dp.setValue(dp);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		
		// Binary coded decimal test
		for (int i=0; i<BCDVAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setDecimalMode(true);
			
			int a = BCDVAL16BIT[i];
			int additive = BCDADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.x.setValue(x);
			CPU.dp.setValue(dp);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		// Binary coded decimal test with carry
		for (int i=0; i<BCDVAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setDecimalMode(true);
			CPU.status.setCarry(true);
			
			int a = BCDVAL16BIT[i];
			int additive = BCDADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.x.setValue(x);
			CPU.dp.setValue(dp);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
	}
	
	// 0x77
	@Test
	public void addDPIndirectLongY16Bit() {
		int opcode = 0x77;
		int addr = BASEADDR;
		int iaddr = IBASEADDR;
		
		int bank = 0x05;
		int ibank = 0;// must be zero
		
		int arg = 0x71;
		int args[] = {0x71};
		int dp = iaddr - arg;
		
		int y = 0x035E;
		
		
		int oldFlags;
		
		// Regular test
		for (int i=0; i<VAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			
			int a = VAL16BIT[i];
			int additive = ADD16BIT[i];
		
			Core.mem.set(Size.BYTE, ibank, iaddr-1, 1);
			Core.mem.set(Size.SHORT, ibank, iaddr, addr-y);
			Core.mem.set(Size.BYTE, ibank, iaddr+2, bank);
			Core.mem.set(Size.BYTE, ibank, iaddr+3, 1);
			
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.y.setValue(y);
			CPU.dp.setValue(dp);
			CPU.dbr.setValue(ibank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		
		// Regular test with carry
		for (int i=0; i<VAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setCarry(true);
			
			int a = VAL16BIT[i];
			int additive = ADD16BIT[i];
		
			Core.mem.set(Size.BYTE, ibank, iaddr-1, 1);
			Core.mem.set(Size.SHORT, ibank, iaddr, addr-y);
			Core.mem.set(Size.BYTE, ibank, iaddr+2, bank);
			Core.mem.set(Size.BYTE, ibank, iaddr+3, 1);
			
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.y.setValue(y);
			CPU.dp.setValue(dp);
			CPU.dbr.setValue(ibank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		
		// Binary coded decimal test
		for (int i=0; i<BCDVAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setDecimalMode(true);
			
			int a = BCDVAL16BIT[i];
			int additive = BCDADD16BIT[i];
		
			Core.mem.set(Size.BYTE, ibank, iaddr-1, 1);
			Core.mem.set(Size.SHORT, ibank, iaddr, addr-y);
			Core.mem.set(Size.BYTE, ibank, iaddr+2, bank);
			Core.mem.set(Size.BYTE, ibank, iaddr+3, 1);
			
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.y.setValue(y);
			CPU.dp.setValue(dp);
			CPU.dbr.setValue(ibank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		// Binary coded decimal test with carry
		for (int i=0; i<BCDVAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setDecimalMode(true);
			CPU.status.setCarry(true);
			
			int a = BCDVAL16BIT[i];
			int additive = BCDADD16BIT[i];
		
			Core.mem.set(Size.BYTE, ibank, iaddr-1, 1);
			Core.mem.set(Size.SHORT, ibank, iaddr, addr-y);
			Core.mem.set(Size.BYTE, ibank, iaddr+2, bank);
			Core.mem.set(Size.BYTE, ibank, iaddr+3, 1);
			
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.y.setValue(y);
			CPU.dp.setValue(dp);
			CPU.dbr.setValue(ibank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
	}
	
	// 0x79
	@Test
	public void addAbsoluteY16Bit() {
		int opcode = 0x79;
		
		int addr = BASEADDR;
		int y = 0x03DE;
		int taddr = addr -y;
		int args[] = {taddr & 0xFF, (taddr >>8)&0xFF};
		int bank = 5;
		
		int oldFlags;
		
		// Regular test
		for (int i=0; i<VAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			
			int a = VAL16BIT[i];
			int additive = ADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.y.setValue(y);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		
		// Regular test with carry
		for (int i=0; i<VAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setCarry(true);
			
			int a = VAL16BIT[i];
			int additive = ADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.y.setValue(y);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		
		// Binary coded decimal test
		for (int i=0; i<BCDVAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setDecimalMode(true);
			
			int a = BCDVAL16BIT[i];
			int additive = BCDADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.y.setValue(y);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		// Binary coded decimal test with carry
		for (int i=0; i<BCDVAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setDecimalMode(true);
			CPU.status.setCarry(true);
			
			int a = BCDVAL16BIT[i];
			int additive = BCDADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.y.setValue(y);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
	}
	
	// 0x7D
	@Test
	public void addAbsoluteX16Bit() {
		int opcode = 0x7D;
		
		int addr = BASEADDR;
		int x = 0x03DE;
		int taddr = addr - x;
		int args[] = {taddr & 0xFF, (taddr >>8)&0xFF};
		int bank = 5;
		
		int oldFlags;
		
		// Regular test
		for (int i=0; i<VAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			
			int a = VAL16BIT[i];
			int additive = ADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.x.setValue(x);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		
		// Regular test with carry
		for (int i=0; i<VAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setCarry(true);
			
			int a = VAL16BIT[i];
			int additive = ADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.x.setValue(x);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		
		// Binary coded decimal test
		for (int i=0; i<BCDVAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setDecimalMode(true);
			
			int a = BCDVAL16BIT[i];
			int additive = BCDADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.x.setValue(x);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		// Binary coded decimal test with carry
		for (int i=0; i<BCDVAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setDecimalMode(true);
			CPU.status.setCarry(true);
			
			int a = BCDVAL16BIT[i];
			int additive = BCDADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.x.setValue(x);
			CPU.dbr.setValue(bank);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
	}
	
	// 0x7F
	@Test
	public void addAbsoluteLongX16Bit() {
		int opcode = 0x7F;
		
		int addr = BASEADDR;
		
		int bank = 5;
		int x = 0x0CEF;
		int taddr = addr - x;
		int args[] = {taddr & 0xFF, (taddr >>8)&0xFF, bank};
		
		int oldFlags;
		
		// Regular test
		for (int i=0; i<VAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			
			int a = VAL16BIT[i];
			int additive = ADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.x.setValue(x);
			CPU.dbr.setValue(0);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		
		// Regular test with carry
		for (int i=0; i<VAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setCarry(true);
			
			int a = VAL16BIT[i];
			int additive = ADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.x.setValue(x);
			CPU.dbr.setValue(0);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		
		// Binary coded decimal test
		for (int i=0; i<BCDVAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setDecimalMode(true);
			
			int a = BCDVAL16BIT[i];
			int additive = BCDADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.x.setValue(x);
			CPU.dbr.setValue(0);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
		// Binary coded decimal test with carry
		for (int i=0; i<BCDVAL16BIT.length; i++) {
			Core.mem = new LoROMMemory();
			CPU.resetCPU();
			CPU.status.setDecimalMode(true);
			CPU.status.setCarry(true);
			
			int a = BCDVAL16BIT[i];
			int additive = BCDADD16BIT[i];
		
			Core.mem.set(Size.BYTE, bank, addr-1, 1);
			Core.mem.set(Size.SHORT, bank, addr, additive);
			Core.mem.set(Size.BYTE, bank, addr+2, 1);
			
			CPU.a.setValue(a);
			CPU.x.setValue(x);
			CPU.dbr.setValue(0);
			oldFlags = CPU.status.getValue();
			CPU.doOp(opcode, args);
			
			assertEquals(bank, CPU.dataBank);
			assertEquals(addr, CPU.dataAddr);
			verifyAdd16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
	}
}
