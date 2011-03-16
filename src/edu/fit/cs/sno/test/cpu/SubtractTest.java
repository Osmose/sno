package edu.fit.cs.sno.test.cpu;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.cpu.instructions.Subtract;
import edu.fit.cs.sno.snes.mem.LoROMMemory;
import edu.fit.cs.sno.util.Settings;
import edu.fit.cs.sno.util.Util;

public class SubtractTest {
	// XXX: Does not attempt to test 8bit anything
	private static final int BASEADDR = 0x12DA;
	private static final int IBASEADDR = 0x1378;
	
	private static final int VAL16BIT[] = {0x80F0, 0x8000, 0x7FFF, 0x0000, 0x1000, 0x7FFF, 0xFFFE};
	private static final int ADD16BIT[] = {0x1234, 0x8000, 0x0005, 0x5000, 0xFFFF, 0x0000, 0x0001};
	
	private static final int BCDVAL16BIT[] = {0x1112, 0x8000, 0x0000, 0x9999, 0x0005, 0x9998};
	private static final int BCDADD16BIT[] = {0x8888, 0x8000, 0x0000, 0x0004, 0x9999, 0x0001};
	
	@Before
	public void setUp() throws Exception {
		new Subtract(); // For 100% coverage
		Settings.init();
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
	}

	public void verifySub16Bit(int origValue, int subtractive, int oldFlags, int newFlags) {
		boolean carryClear = (oldFlags & 0x01) == 0x00;
		subtractive += (carryClear?1:0);
		
		String tstr = String.format("Subtracting 0x%04X from 0x%04X%s: ", subtractive, origValue, ((oldFlags & 0x08) == 0x08)? " in BCD mode":"");
		
		if ((oldFlags & 0x08) != 0x08) { // Binary mode
			int oracle = origValue - subtractive; // add original value, carry flag, and additive
			oracle &= 0xFFFF;
			assertEquals(tstr, oracle, CPU.a.getValue());
			
			assertEquals(tstr, (CPU.a.getValue() & 0x8000) == 0x8000, (newFlags & 0x80) == 0x80);
			assertEquals(tstr, CPU.a.getValue() == 0x0000, (newFlags & 0x02) == 0x02);
			
			// Determine if signed overflow occurred
			boolean signedOverflow = false;
			if (((origValue & 0x8000) == 0x8000) && ((subtractive & 0x8000) != 0x8000)) {
				if ((oracle & 0x8000) == 0x8000) { // result is still negative, no overflow
					signedOverflow = false;
				} else { // result was positive, sign overflow
					signedOverflow = true;
				}
			} else if (((origValue & 0x8000) != 0x8000) && ((subtractive & 0x8000) == 0x8000)) {
				// If subtracting a negative from a positive
				if ((oracle & 0x8000) == 0x8000) { // result was negative, sign overflow
					signedOverflow = true;
				} else { // result is still positive, no sign overflow
					signedOverflow = false;
				}
			}
			
			// Determine if unsigned overflow occurred
			boolean carryFlag = true;
			if (subtractive > origValue)
				carryFlag = false;
			
			assertEquals(tstr, signedOverflow, (newFlags & 0x40)==0x40);
			assertEquals(tstr, carryFlag, (newFlags & 0x01)==0x01);
			
		} else { // Decimal mode
			int a = Util.bcdAdjustSubtract(Size.SHORT, origValue);
			int b = Util.bcdAdjustSubtract(Size.SHORT, subtractive);
			int carry = 1;
			if (a-b < 0) carry = 0;
			
			int oracle = Util.bcdAdjustSubtract(Size.SHORT, origValue - subtractive)&0xFFFF; // add original value, carry flag, and additive
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
	
	// 0xE9
	@Test
	public void subImmediate16Bit() {
		int opcode = 0xE9;
		
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
			
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
	}
	
	// 0xE1
	@Test
	public void subDPIndirectX16Bit() {
		int opcode = 0xE1;
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
	}
	
	
	// 0xE3
	@Test
	public void subStackRelative16Bit() {
		int opcode = 0xE3;
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
	}
	
	// 0xE5
	@Test
	public void subDP16Bit() {
		int opcode = 0xE5;
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
	}
	
	// 0xE7
	@Test
	public void subDPIndirectLong16Bit() {
		int opcode = 0xE7;
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
	}
	
	// 0xED
	@Test
	public void subAbsolute16Bit() {
		int opcode = 0xED;
		
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
	}
	
	// 0xEF
	@Test
	public void subAbsoluteLong16Bit() {
		int opcode = 0xEF;
		
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
	}

	// 0xF1
	@Test
	public void subDPIndirectY16Bit() {
		int opcode = 0xF1;
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
	}

	// 0xF2
	@Test
	public void subDPIndirect16Bit() {
		int opcode = 0xF2;
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
	}
	
	// 0xF3
	@Test
	public void subSRIndirectY16Bit() {
		int opcode = 0xF3;
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
	}
	
	// 0xF5
	@Test
	public void subDPIndexedX16Bit() {
		int opcode = 0xF5;
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
	}
	
	// 0xF7
	@Test
	public void subDPIndirectLongY16Bit() {
		int opcode = 0xF7;
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
	}
	
	// 0xF9
	@Test
	public void subAbsoluteY16Bit() {
		int opcode = 0xF9;
		
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
	}
	
	// 0xFD
	@Test
	public void subAbsoluteX16Bit() {
		int opcode = 0xFD;
		
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
	}
	
	// 0xFF
	@Test
	public void subAbsoluteLongX16Bit() {
		int opcode = 0xFF;
		
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
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
			verifySub16Bit(a, additive, oldFlags, CPU.status.getValue());
		}
	}
}
