package edu.fit.cs.sno.test.cpu;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.cpu.instructions.Shift;
import edu.fit.cs.sno.snes.mem.LoROMMemory;
import edu.fit.cs.sno.util.Settings;

public class ShiftTest {

	private static final int VAL16BIT1 = 0x0000;
	private static final int VAL16BIT2 = 0xFFFF;
	private static final int VAL16BIT3 = 0x7FFF;
	
	private static final int VAL8BIT1 = 0x00;
	private static final int VAL8BIT2 = 0xFF;
	private static final int VAL8BIT3 = 0x7F;
	private static final int BASEADDR = 0x182E;

	@Before
	public void setUp() throws Exception {
		new Shift();
		Settings.init();
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
	}

	public void verifyLeft16(int origValue, int finalValue,int newFlags) {
		int oracle, carry;
		oracle = (origValue << 1);
		carry = (oracle >> 16) & 0x01;
		oracle = oracle & 0xFFFF;
		
		assertEquals(oracle, finalValue);
		assertEquals((oracle & 0x8000) == 0x8000, (newFlags & 0x80)==0x80); // Negative flag
		assertEquals(oracle == 0, (newFlags & 0x02)==0x02); // Zero flag
		assertEquals(carry == 0x01, (newFlags & 0x01)==0x01); // Carry flag
	}
	
	public void verifyRight16(int origValue, int finalValue,int newFlags) {
		int oracle, carry;
		carry = origValue & 0x01;
		oracle = (origValue >> 1) & 0xFFFF;
		
		assertEquals(oracle, finalValue);
		assertEquals(false, (newFlags & 0x80)==0x80); // Negative flag(Must be false)
		assertEquals(oracle == 0, (newFlags & 0x02)==0x02); // Zero flag
		assertEquals(carry == 0x01, (newFlags & 0x01)==0x01); // Carry flag
	}
	
	public void verifyLeft8(int origValue, int finalValue,int newFlags) {
		int oracle, carry;
		oracle = (origValue << 1);
		carry = (oracle >> 8) & 0x01;
		oracle = oracle & 0xFF;
		
		assertEquals(oracle, finalValue);
		assertEquals((oracle & 0x80) == 0x80, (newFlags & 0x80)==0x80); // Negative flag
		assertEquals(oracle == 0, (newFlags & 0x02)==0x02); // Zero flag
		assertEquals(carry == 0x01, (newFlags & 0x01)==0x01); // Carry flag
	}
	
	public void verifyRight8(int origValue, int finalValue,int newFlags) {
		int oracle, carry;
		carry = origValue & 0x01;
		oracle = (origValue >> 1) & 0xFF;
		
		assertEquals(oracle, finalValue);
		assertEquals(false, (newFlags & 0x80)==0x80); // Negative flag(Must be false)
		assertEquals(oracle == 0, (newFlags & 0x02)==0x02); // Zero flag
		assertEquals(carry == 0x01, (newFlags & 0x01)==0x01); // Carry flag
	}
	
	// 0x0E
	@Test
	public void shiftLeftAbsolute() {
		int opcode = 0x0E;
		int addr = BASEADDR;
		int bank = 3;
		int testVal;
		int args[] = new int[]{addr & 0xFF, (addr >> 8) & 0xFF};		

		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT1;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.dbr.setValue(bank);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyLeft16(testVal, Core.mem.get(Size.SHORT, bank, addr), CPU.status.getValue());

		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT2;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.dbr.setValue(bank);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyLeft16(testVal, Core.mem.get(Size.SHORT, bank, addr), CPU.status.getValue());
		
		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT3;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.dbr.setValue(bank);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyLeft16(testVal, Core.mem.get(Size.SHORT, bank, addr), CPU.status.getValue());
	}
	
	// 0x1E
	@Test
	public void shiftLeftAbsoluteX() {
		int opcode = 0x1E;
		int addr = BASEADDR;
		int bank = 3;
		int x = 0x1234;
		int testVal;
		int argaddr = addr - x;
		int args[] = new int[]{argaddr & 0xFF, (argaddr >> 8) & 0xFF};		

		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT1;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.dbr.setValue(bank);
		CPU.x.setValue(x);
		
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyLeft16(testVal, Core.mem.get(Size.SHORT, bank, addr), CPU.status.getValue());

		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT2;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.dbr.setValue(bank);
		CPU.x.setValue(x);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyLeft16(testVal, Core.mem.get(Size.SHORT, bank, addr), CPU.status.getValue());
		
		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT3;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.dbr.setValue(bank);
		CPU.x.setValue(x);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyLeft16(testVal, Core.mem.get(Size.SHORT, bank, addr), CPU.status.getValue());
	}
	
	// 0x0A
	@Test
	public void shiftLeftAccumulator() {
		int opcode = 0x0A;
		int testVal;
		int args[] = null;

		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT1;
		CPU.a.setValue(testVal);
		
		CPU.doOp(opcode, args);
		
		verifyLeft16(testVal, CPU.a.getValue(), CPU.status.getValue());

		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT2;
		CPU.a.setValue(testVal);
		CPU.doOp(opcode, args);
		
		verifyLeft16(testVal, CPU.a.getValue(), CPU.status.getValue());
		
		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT3;
		CPU.a.setValue(testVal);	
		CPU.doOp(opcode, args);
		
		verifyLeft16(testVal, CPU.a.getValue(), CPU.status.getValue());
	}
	
	// 0x06
	@Test
	public void shiftLeftDP() {
		int opcode = 0x06;
		int addr = BASEADDR;
		int bank = 0; // Direct page, must be zero
		int offset = 0x88;
		int dp = addr - offset;
		int testVal;
		int args[] = new int[]{offset & 0xFF};		

		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT1;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.dbr.setValue(bank);
		CPU.dp.setValue(dp);
		
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyLeft16(testVal, Core.mem.get(Size.SHORT, bank, addr), CPU.status.getValue());

		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT2;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.dbr.setValue(bank);
		CPU.dp.setValue(dp);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyLeft16(testVal, Core.mem.get(Size.SHORT, bank, addr), CPU.status.getValue());
		
		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT3;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.dbr.setValue(bank);
		CPU.dp.setValue(dp);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyLeft16(testVal, Core.mem.get(Size.SHORT, bank, addr), CPU.status.getValue());
	}
	
	// 0x16
	@Test
	public void shiftLeftDPX() {
		int opcode = 0x16;
		int addr = BASEADDR;
		int bank = 0; // Direct page, must be zero
		int offset = 0x88;
		int x = 0x1234;
		int dp = addr - offset - x;
		int testVal;
		int args[] = new int[]{offset & 0xFF};		

		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT1;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.dbr.setValue(bank);
		CPU.dp.setValue(dp);
		CPU.x.setValue(x);
		
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyLeft16(testVal, Core.mem.get(Size.SHORT, bank, addr), CPU.status.getValue());

		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT2;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.dbr.setValue(bank);
		CPU.dp.setValue(dp);
		CPU.x.setValue(x);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyLeft16(testVal, Core.mem.get(Size.SHORT, bank, addr), CPU.status.getValue());
		
		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT3;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.dbr.setValue(bank);
		CPU.dp.setValue(dp);
		CPU.x.setValue(x);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyLeft16(testVal, Core.mem.get(Size.SHORT, bank, addr), CPU.status.getValue());
	}
	
	// 0x4E
	@Test
	public void shiftRightAbsolute() {
		int opcode = 0x4E;
		int addr = BASEADDR;
		int bank = 3;
		int testVal;
		int args[] = new int[]{addr & 0xFF, (addr >> 8) & 0xFF};		

		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT1;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.dbr.setValue(bank);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyRight16(testVal, Core.mem.get(Size.SHORT, bank, addr), CPU.status.getValue());

		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT2;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.dbr.setValue(bank);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyRight16(testVal, Core.mem.get(Size.SHORT, bank, addr), CPU.status.getValue());
		
		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT3;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.dbr.setValue(bank);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyRight16(testVal, Core.mem.get(Size.SHORT, bank, addr), CPU.status.getValue());
	}
	
	// 0x5E
	@Test
	public void shiftRightAbsoluteX() {
		int opcode = 0x5E;
		int addr = BASEADDR;
		int bank = 3;
		int x = 0x1234;
		int testVal;
		int argaddr = addr - x;
		int args[] = new int[]{argaddr & 0xFF, (argaddr >> 8) & 0xFF};		

		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT1;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.dbr.setValue(bank);
		CPU.x.setValue(x);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyRight16(testVal, Core.mem.get(Size.SHORT, bank, addr), CPU.status.getValue());

		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT2;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.dbr.setValue(bank);
		CPU.x.setValue(x);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyRight16(testVal, Core.mem.get(Size.SHORT, bank, addr), CPU.status.getValue());
		
		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT3;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.dbr.setValue(bank);
		CPU.x.setValue(x);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyRight16(testVal, Core.mem.get(Size.SHORT, bank, addr), CPU.status.getValue());
	}
	
	// 0x4A
	@Test
	public void shiftRightAccumulator() {
		int opcode = 0x4A;
		int testVal;
		int args[] = null;

		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT1;
		CPU.a.setValue(testVal);
		CPU.doOp(opcode, args);
		
		verifyRight16(testVal, CPU.a.getValue(), CPU.status.getValue());

		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT2;
		CPU.a.setValue(testVal);
		CPU.doOp(opcode, args);
		
		verifyRight16(testVal, CPU.a.getValue(), CPU.status.getValue());
		
		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT3;
		CPU.a.setValue(testVal);	
		CPU.doOp(opcode, args);
		
		verifyRight16(testVal, CPU.a.getValue(), CPU.status.getValue());
	}
	
	// 0x46
	@Test
	public void shiftRightDP() {
		int opcode = 0x46;
		int addr = BASEADDR;
		int bank = 0; // Direct page, must be zero
		int offset = 0x88;
		int dp = addr - offset;
		int testVal;
		int args[] = new int[]{offset & 0xFF};		

		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT1;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.dbr.setValue(bank);
		CPU.dp.setValue(dp);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyRight16(testVal, Core.mem.get(Size.SHORT, bank, addr), CPU.status.getValue());

		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT2;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.dbr.setValue(bank);
		CPU.dp.setValue(dp);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyRight16(testVal, Core.mem.get(Size.SHORT, bank, addr), CPU.status.getValue());
		
		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT3;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.dbr.setValue(bank);
		CPU.dp.setValue(dp);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyRight16(testVal, Core.mem.get(Size.SHORT, bank, addr), CPU.status.getValue());
	}
	
	// 0x56
	@Test
	public void shiftRightDPX() {
		int opcode = 0x56;
		int addr = BASEADDR;
		int bank = 0; // Direct page, must be zero
		int offset = 0x88;
		int x = 0x1234;
		int dp = addr - offset - x;
		int testVal;
		int args[] = new int[]{offset & 0xFF};		

		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT1;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.dbr.setValue(bank);
		CPU.dp.setValue(dp);
		CPU.x.setValue(x);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyRight16(testVal, Core.mem.get(Size.SHORT, bank, addr), CPU.status.getValue());

		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT2;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.dbr.setValue(bank);
		CPU.dp.setValue(dp);
		CPU.x.setValue(x);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyRight16(testVal, Core.mem.get(Size.SHORT, bank, addr), CPU.status.getValue());
		
		
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT3;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.dbr.setValue(bank);
		CPU.dp.setValue(dp);
		CPU.x.setValue(x);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyRight16(testVal, Core.mem.get(Size.SHORT, bank, addr), CPU.status.getValue());
	}
	
	
	// 0x0E
	@Test
	public void shiftLeftAbsolute8() {
		int opcode = 0x0E;
		int addr = BASEADDR;
		int bank = 3;
		int testVal;
		int args[] = new int[]{addr & 0xFF, (addr >> 8) & 0xFF};		

		
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT1;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.dbr.setValue(bank);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyLeft8(testVal, Core.mem.get(Size.BYTE, bank, addr), CPU.status.getValue());

		
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT2;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.dbr.setValue(bank);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyLeft8(testVal, Core.mem.get(Size.BYTE, bank, addr), CPU.status.getValue());
		
		
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT3;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.dbr.setValue(bank);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyLeft8(testVal, Core.mem.get(Size.BYTE, bank, addr), CPU.status.getValue());
	}
	
	// 0x1E
	@Test
	public void shiftLeftAbsoluteX8() {
		int opcode = 0x1E;
		int addr = BASEADDR;
		int bank = 3;
		int x = 0x1234;
		int testVal;
		int argaddr = addr - x;
		int args[] = new int[]{argaddr & 0xFF, (argaddr >> 8) & 0xFF};		

		
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT1;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.dbr.setValue(bank);
		CPU.x.setValue(x);
		
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyLeft8(testVal, Core.mem.get(Size.BYTE, bank, addr), CPU.status.getValue());

		
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT2;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.dbr.setValue(bank);
		CPU.x.setValue(x);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyLeft8(testVal, Core.mem.get(Size.BYTE, bank, addr), CPU.status.getValue());
		
		
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT3;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.dbr.setValue(bank);
		CPU.x.setValue(x);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyLeft8(testVal, Core.mem.get(Size.BYTE, bank, addr), CPU.status.getValue());
	}
	
	// 0x0A
	@Test
	public void shiftLeftAccumulator8() {
		int opcode = 0x0A;
		int testVal;
		int args[] = null;

		
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT1;
		CPU.a.setValue(testVal);
		
		CPU.doOp(opcode, args);
		
		verifyLeft8(testVal, CPU.a.getValue(), CPU.status.getValue());

		
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT2;
		CPU.a.setValue(testVal);
		CPU.doOp(opcode, args);
		
		verifyLeft8(testVal, CPU.a.getValue(), CPU.status.getValue());
		
		
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT3;
		CPU.a.setValue(testVal);	
		CPU.doOp(opcode, args);
		
		verifyLeft8(testVal, CPU.a.getValue(), CPU.status.getValue());
	}
	
	// 0x06
	@Test
	public void shiftLeftDP8() {
		int opcode = 0x06;
		int addr = BASEADDR;
		int bank = 0; // Direct page, must be zero
		int offset = 0x88;
		int dp = addr - offset;
		int testVal;
		int args[] = new int[]{offset & 0xFF};		

		
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT1;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.dbr.setValue(bank);
		CPU.dp.setValue(dp);
		
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyLeft8(testVal, Core.mem.get(Size.BYTE, bank, addr), CPU.status.getValue());

		
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT2;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.dbr.setValue(bank);
		CPU.dp.setValue(dp);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyLeft8(testVal, Core.mem.get(Size.BYTE, bank, addr), CPU.status.getValue());
		
		
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT3;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.dbr.setValue(bank);
		CPU.dp.setValue(dp);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyLeft8(testVal, Core.mem.get(Size.BYTE, bank, addr), CPU.status.getValue());
	}
	
	// 0x16
	@Test
	public void shiftLeftDPX8() {
		int opcode = 0x16;
		int addr = BASEADDR;
		int bank = 0; // Direct page, must be zero
		int offset = 0x88;
		int x = 0x1234;
		int dp = addr - offset - x;
		int testVal;
		int args[] = new int[]{offset & 0xFF};		

		
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT1;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.dbr.setValue(bank);
		CPU.dp.setValue(dp);
		CPU.x.setValue(x);
		
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyLeft8(testVal, Core.mem.get(Size.BYTE, bank, addr), CPU.status.getValue());

		
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT2;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.dbr.setValue(bank);
		CPU.dp.setValue(dp);
		CPU.x.setValue(x);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyLeft8(testVal, Core.mem.get(Size.BYTE, bank, addr), CPU.status.getValue());
		
		
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT3;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.dbr.setValue(bank);
		CPU.dp.setValue(dp);
		CPU.x.setValue(x);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyLeft8(testVal, Core.mem.get(Size.BYTE, bank, addr), CPU.status.getValue());
	}
	
	// 0x4E
	@Test
	public void shiftRightAbsolute8() {
		int opcode = 0x4E;
		int addr = BASEADDR;
		int bank = 3;
		int testVal;
		int args[] = new int[]{addr & 0xFF, (addr >> 8) & 0xFF};		

		
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT1;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.dbr.setValue(bank);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyRight8(testVal, Core.mem.get(Size.BYTE, bank, addr), CPU.status.getValue());

		
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT2;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.dbr.setValue(bank);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyRight8(testVal, Core.mem.get(Size.BYTE, bank, addr), CPU.status.getValue());
		
		
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT3;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.dbr.setValue(bank);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyRight8(testVal, Core.mem.get(Size.BYTE, bank, addr), CPU.status.getValue());
	}
	
	// 0x5E
	@Test
	public void shiftRightAbsoluteX8() {
		int opcode = 0x5E;
		int addr = BASEADDR;
		int bank = 3;
		int x = 0x1234;
		int testVal;
		int argaddr = addr - x;
		int args[] = new int[]{argaddr & 0xFF, (argaddr >> 8) & 0xFF};		

		
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT1;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.dbr.setValue(bank);
		CPU.x.setValue(x);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyRight8(testVal, Core.mem.get(Size.BYTE, bank, addr), CPU.status.getValue());

		
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT2;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.dbr.setValue(bank);
		CPU.x.setValue(x);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyRight8(testVal, Core.mem.get(Size.BYTE, bank, addr), CPU.status.getValue());
		
		
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT3;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.dbr.setValue(bank);
		CPU.x.setValue(x);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyRight8(testVal, Core.mem.get(Size.BYTE, bank, addr), CPU.status.getValue());
	}
	
	// 0x4A
	@Test
	public void shiftRightAccumulator8() {
		int opcode = 0x4A;
		int testVal;
		int args[] = null;

		
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT1;
		CPU.a.setValue(testVal);
		CPU.doOp(opcode, args);
		
		verifyRight8(testVal, CPU.a.getValue(), CPU.status.getValue());

		
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT2;
		CPU.a.setValue(testVal);
		CPU.doOp(opcode, args);
		
		verifyRight8(testVal, CPU.a.getValue(), CPU.status.getValue());
		
		
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT3;
		CPU.a.setValue(testVal);	
		CPU.doOp(opcode, args);
		
		verifyRight8(testVal, CPU.a.getValue(), CPU.status.getValue());
	}
	
	// 0x46
	@Test
	public void shiftRightDP8() {
		int opcode = 0x46;
		int addr = BASEADDR;
		int bank = 0; // Direct page, must be zero
		int offset = 0x88;
		int dp = addr - offset;
		int testVal;
		int args[] = new int[]{offset & 0xFF};		

		
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT1;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.dbr.setValue(bank);
		CPU.dp.setValue(dp);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyRight8(testVal, Core.mem.get(Size.BYTE, bank, addr), CPU.status.getValue());

		
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT2;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.dbr.setValue(bank);
		CPU.dp.setValue(dp);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyRight8(testVal, Core.mem.get(Size.BYTE, bank, addr), CPU.status.getValue());
		
		
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT3;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.dbr.setValue(bank);
		CPU.dp.setValue(dp);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyRight8(testVal, Core.mem.get(Size.BYTE, bank, addr), CPU.status.getValue());
	}
	
	// 0x56
	@Test
	public void shiftRightDPX8() {
		int opcode = 0x56;
		int addr = BASEADDR;
		int bank = 0; // Direct page, must be zero
		int offset = 0x88;
		int x = 0x1234;
		int dp = addr - offset - x;
		int testVal;
		int args[] = new int[]{offset & 0xFF};		

		
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT1;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.dbr.setValue(bank);
		CPU.dp.setValue(dp);
		CPU.x.setValue(x);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyRight8(testVal, Core.mem.get(Size.BYTE, bank, addr), CPU.status.getValue());

		
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT2;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.dbr.setValue(bank);
		CPU.dp.setValue(dp);
		CPU.x.setValue(x);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyRight8(testVal, Core.mem.get(Size.BYTE, bank, addr), CPU.status.getValue());
		
		
		CPU.resetCPU();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT3;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.BYTE, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+1, 1);
		
		CPU.dbr.setValue(bank);
		CPU.dp.setValue(dp);
		CPU.x.setValue(x);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyRight8(testVal, Core.mem.get(Size.BYTE, bank, addr), CPU.status.getValue());
	}
}
