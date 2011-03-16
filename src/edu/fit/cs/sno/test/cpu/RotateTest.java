package edu.fit.cs.sno.test.cpu;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.cpu.instructions.Rotate;
import edu.fit.cs.sno.snes.mem.LoROMMemory;
import edu.fit.cs.sno.util.Settings;

public class RotateTest {

	private static final int VAL16BIT1 = 0x0000;
	private static final int VAL16BIT2 = 0xFFFF;
	private static final int VAL16BIT3 = 0x7FFF;
	
	private static final int VAL8BIT1 = 0x00;
	private static final int VAL8BIT2 = 0xFF;
	private static final int VAL8BIT3 = 0x7F;
	private static final int BASEADDR = 0x182E;

	@Before
	public void setUp() throws Exception {
		new Rotate();
		Settings.init();
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
	}

	public void verifyLeft16(int origValue, int finalValue, int oldFlags, int newFlags) {
		int oracle, carry;
		if ((oldFlags & 0x01) == 0x01)
			oracle = (origValue << 1) | 0x01;
		else
			oracle = (origValue << 1);
		carry = (oracle >> 16) & 0x01;
		oracle = oracle & 0xFFFF;
		
		assertEquals(oracle, finalValue);
		assertEquals((oracle & 0x8000) == 0x8000, (newFlags & 0x80)==0x80); // Negative flag
		assertEquals(oracle == 0, (newFlags & 0x02)==0x02); // Zero flag
		assertEquals(carry == 0x01, (newFlags & 0x01)==0x01); // Carry flag
	}
	
	public void verifyRight16(int origValue, int finalValue, int oldFlags, int newFlags) {
		int oracle, carry;
		carry = origValue & 0x01;
		if ((oldFlags & 0x01) == 0x01)
			oracle = (origValue >> 1) | 0x8000;
		else
			oracle = (origValue >> 1);
		oracle = oracle & 0xFFFF;
		
		assertEquals(oracle, finalValue);
		assertEquals((oracle & 0x8000) == 0x8000, (newFlags & 0x80)==0x80); // Negative flag
		assertEquals(oracle == 0, (newFlags & 0x02)==0x02); // Zero flag
		assertEquals(carry == 0x01, (newFlags & 0x01)==0x01); // Carry flag
	}
	
	public void verifyLeft8(int origValue, int finalValue, int oldFlags, int newFlags) {
		int oracle, carry;
		if ((oldFlags & 0x01) == 0x01)
			oracle = (origValue << 1) | 0x01;
		else
			oracle = (origValue << 1);
		carry = (oracle >> 8) & 0x01;
		oracle = oracle & 0xFF;
		
		assertEquals(oracle, finalValue);
		assertEquals((oracle & 0x80) == 0x80, (newFlags & 0x80)==0x80); // Negative flag
		assertEquals(oracle == 0, (newFlags & 0x02)==0x02); // Zero flag
		assertEquals(carry == 0x01, (newFlags & 0x01)==0x01); // Carry flag
	}
	
	public void verifyRight8(int origValue, int finalValue, int oldFlags, int newFlags) {
		int oracle, carry;
		carry = origValue & 0x01;
		if ((oldFlags & 0x01) == 0x01)
			oracle = (origValue >> 1) | 0x80;
		else
			oracle = (origValue >> 1);
		oracle = oracle & 0xFF;
		
		assertEquals(oracle, finalValue);
		assertEquals((oracle & 0x80) == 0x80, (newFlags & 0x80)==0x80); // Negative flag(Must be false)
		assertEquals(oracle == 0, (newFlags & 0x02)==0x02); // Zero flag
		assertEquals(carry == 0x01, (newFlags & 0x01)==0x01); // Carry flag
	}
	
	// 0x2E
	@Test
	public void rotateLeftAbsolute() {
		int opcode = 0x2E;
		int addr = BASEADDR;
		int bank = 3;
		int testVal;
		int args[] = new int[]{addr & 0xFF, (addr >> 8) & 0xFF};				
		int oldFlags;

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT1;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.dbr.setValue(bank);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyLeft16(testVal, Core.mem.get(Size.SHORT, bank, addr), oldFlags, CPU.status.getValue());

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT2;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.dbr.setValue(bank);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyLeft16(testVal, Core.mem.get(Size.SHORT, bank, addr), oldFlags, CPU.status.getValue());
		
		oldFlags = CPU.status.getValue();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT3;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.dbr.setValue(bank);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyLeft16(testVal, Core.mem.get(Size.SHORT, bank, addr), oldFlags, CPU.status.getValue());
	}
	
	// 0x3E
	@Test
	public void rotateLeftAbsoluteX() {
		int opcode = 0x3E;
		int addr = BASEADDR;
		int bank = 3;
		int x = 0x1234;
		int testVal;
		int argaddr = addr - x;
		int args[] = new int[]{argaddr & 0xFF, (argaddr >> 8) & 0xFF};				
		int oldFlags;

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
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
		verifyLeft16(testVal, Core.mem.get(Size.SHORT, bank, addr), oldFlags, CPU.status.getValue());

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
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
		verifyLeft16(testVal, Core.mem.get(Size.SHORT, bank, addr), oldFlags, CPU.status.getValue());
		

		oldFlags = CPU.status.getValue();
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
		verifyLeft16(testVal, Core.mem.get(Size.SHORT, bank, addr), oldFlags, CPU.status.getValue());
	}
	
	// 0x2A
	@Test
	public void rotateLeftAccumulator() {
		int opcode = 0x2A;
		int testVal;
		int args[] = null;		
		int oldFlags;

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT1;
		CPU.a.setValue(testVal);
		
		CPU.doOp(opcode, args);
		
		verifyLeft16(testVal, CPU.a.getValue(), oldFlags, CPU.status.getValue());

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT2;
		CPU.a.setValue(testVal);
		CPU.doOp(opcode, args);
		
		verifyLeft16(testVal, CPU.a.getValue(), oldFlags, CPU.status.getValue());
		
		oldFlags = CPU.status.getValue();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT3;
		CPU.a.setValue(testVal);	
		CPU.doOp(opcode, args);
		
		verifyLeft16(testVal, CPU.a.getValue(), oldFlags, CPU.status.getValue());
	}
	
	// 0x26
	@Test
	public void rotateLeftDP() {
		int opcode = 0x26;
		int addr = BASEADDR;
		int bank = 0; // Direct page, must be zero
		int offset = 0x88;
		int dp = addr - offset;
		int testVal;
		int args[] = new int[]{offset & 0xFF};				
		int oldFlags;

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
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
		verifyLeft16(testVal, Core.mem.get(Size.SHORT, bank, addr), oldFlags, CPU.status.getValue());

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
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
		verifyLeft16(testVal, Core.mem.get(Size.SHORT, bank, addr), oldFlags, CPU.status.getValue());
		
		
		oldFlags = CPU.status.getValue();
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
		verifyLeft16(testVal, Core.mem.get(Size.SHORT, bank, addr), oldFlags, CPU.status.getValue());
	}
	
	// 0x36
	@Test
	public void rotateLeftDPX() {
		int opcode = 0x36;
		int addr = BASEADDR;
		int bank = 0; // Direct page, must be zero
		int offset = 0x88;
		int x = 0x1234;
		int dp = addr - offset - x;
		int testVal;
		int args[] = new int[]{offset & 0xFF};				
		int oldFlags;

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
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
		verifyLeft16(testVal, Core.mem.get(Size.SHORT, bank, addr), oldFlags, CPU.status.getValue());

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
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
		verifyLeft16(testVal, Core.mem.get(Size.SHORT, bank, addr), oldFlags, CPU.status.getValue());
		
		
		oldFlags = CPU.status.getValue();
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
		verifyLeft16(testVal, Core.mem.get(Size.SHORT, bank, addr), oldFlags, CPU.status.getValue());
	}
	
	// 0x6E
	@Test
	public void rotateRightAbsolute() {
		int opcode = 0x6E;
		int addr = BASEADDR;
		int bank = 3;
		int testVal;
		int args[] = new int[]{addr & 0xFF, (addr >> 8) & 0xFF};				
		int oldFlags;

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT1;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.dbr.setValue(bank);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyRight16(testVal, Core.mem.get(Size.SHORT, bank, addr), oldFlags, CPU.status.getValue());

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT2;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.dbr.setValue(bank);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyRight16(testVal, Core.mem.get(Size.SHORT, bank, addr), oldFlags, CPU.status.getValue());
		
		
		oldFlags = CPU.status.getValue();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT3;
		Core.mem.set(Size.BYTE, bank, addr-1, 1);
		Core.mem.set(Size.SHORT, bank, addr, testVal);
		Core.mem.set(Size.BYTE, bank, addr+3, 1);
		
		CPU.dbr.setValue(bank);
		CPU.doOp(opcode, args);
		
		assertEquals(bank, CPU.dataBank);
		assertEquals(addr, CPU.dataAddr);
		verifyRight16(testVal, Core.mem.get(Size.SHORT, bank, addr), oldFlags, CPU.status.getValue());
	}
	
	// 0x7E
	@Test
	public void rotateRightAbsoluteX() {
		int opcode = 0x7E;
		int addr = BASEADDR;
		int bank = 3;
		int x = 0x1234;
		int testVal;
		int argaddr = addr - x;
		int args[] = new int[]{argaddr & 0xFF, (argaddr >> 8) & 0xFF};				
		int oldFlags;

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
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
		verifyRight16(testVal, Core.mem.get(Size.SHORT, bank, addr), oldFlags, CPU.status.getValue());

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
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
		verifyRight16(testVal, Core.mem.get(Size.SHORT, bank, addr), oldFlags, CPU.status.getValue());
		
		
		oldFlags = CPU.status.getValue();
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
		verifyRight16(testVal, Core.mem.get(Size.SHORT, bank, addr), oldFlags, CPU.status.getValue());
	}
	
	// 0x6A
	@Test
	public void rotateRightAccumulator() {
		int opcode = 0x6A;
		int testVal;
		int args[] = null;		
		int oldFlags;

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT1;
		CPU.a.setValue(testVal);
		CPU.doOp(opcode, args);
		
		verifyRight16(testVal, CPU.a.getValue(), oldFlags, CPU.status.getValue());

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT2;
		CPU.a.setValue(testVal);
		CPU.doOp(opcode, args);
		
		verifyRight16(testVal, CPU.a.getValue(), oldFlags, CPU.status.getValue());
		
		
		oldFlags = CPU.status.getValue();
		Core.mem = new LoROMMemory();
		testVal = VAL16BIT3;
		CPU.a.setValue(testVal);	
		CPU.doOp(opcode, args);
		
		verifyRight16(testVal, CPU.a.getValue(), oldFlags, CPU.status.getValue());
	}
	
	// 0x66
	@Test
	public void rotateRightDP() {
		int opcode = 0x66;
		int addr = BASEADDR;
		int bank = 0; // Direct page, must be zero
		int offset = 0x88;
		int dp = addr - offset;
		int testVal;
		int args[] = new int[]{offset & 0xFF};				
		int oldFlags;

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
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
		verifyRight16(testVal, Core.mem.get(Size.SHORT, bank, addr), oldFlags, CPU.status.getValue());

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
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
		verifyRight16(testVal, Core.mem.get(Size.SHORT, bank, addr), oldFlags, CPU.status.getValue());
		
		
		oldFlags = CPU.status.getValue();
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
		verifyRight16(testVal, Core.mem.get(Size.SHORT, bank, addr), oldFlags, CPU.status.getValue());
	}
	
	// 0x76
	@Test
	public void rotateRightDPX() {
		int opcode = 0x76;
		int addr = BASEADDR;
		int bank = 0; // Direct page, must be zero
		int offset = 0x88;
		int x = 0x1234;
		int dp = addr - offset - x;
		int testVal;
		int args[] = new int[]{offset & 0xFF};				
		int oldFlags;

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
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
		verifyRight16(testVal, Core.mem.get(Size.SHORT, bank, addr), oldFlags, CPU.status.getValue());

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
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
		verifyRight16(testVal, Core.mem.get(Size.SHORT, bank, addr), oldFlags, CPU.status.getValue());
		
		
		oldFlags = CPU.status.getValue();
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
		verifyRight16(testVal, Core.mem.get(Size.SHORT, bank, addr), oldFlags, CPU.status.getValue());
	}
	
	
	// 0x2E
	@Test
	public void rotateLeftAbsolute8() {
		int opcode = 0x2E;
		int addr = BASEADDR;
		int bank = 3;
		int testVal;
		int args[] = new int[]{addr & 0xFF, (addr >> 8) & 0xFF};			
		int oldFlags;	

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
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
		verifyLeft8(testVal, Core.mem.get(Size.BYTE, bank, addr), oldFlags, CPU.status.getValue());

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
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
		verifyLeft8(testVal, Core.mem.get(Size.BYTE, bank, addr), oldFlags, CPU.status.getValue());
		
		
		oldFlags = CPU.status.getValue();
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
		verifyLeft8(testVal, Core.mem.get(Size.BYTE, bank, addr), oldFlags, CPU.status.getValue());
	}
	
	// 0x3E
	@Test
	public void rotateLeftAbsoluteX8() {
		int opcode = 0x3E;
		int addr = BASEADDR;
		int bank = 3;
		int x = 0x1234;
		int testVal;
		int argaddr = addr - x;
		int args[] = new int[]{argaddr & 0xFF, (argaddr >> 8) & 0xFF};			
		int oldFlags;	

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
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
		verifyLeft8(testVal, Core.mem.get(Size.BYTE, bank, addr), oldFlags, CPU.status.getValue());

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
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
		verifyLeft8(testVal, Core.mem.get(Size.BYTE, bank, addr), oldFlags, CPU.status.getValue());
		
		
		oldFlags = CPU.status.getValue();
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
		verifyLeft8(testVal, Core.mem.get(Size.BYTE, bank, addr), oldFlags, CPU.status.getValue());
	}
	
	// 0x2A
	@Test
	public void rotateLeftAccumulator8() {
		int opcode = 0x2A;
		int testVal;
		int args[] = null;		
		int oldFlags;

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT1;
		CPU.a.setValue(testVal);
		
		CPU.doOp(opcode, args);
		
		verifyLeft8(testVal, CPU.a.getValue(), oldFlags, CPU.status.getValue());

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT2;
		CPU.a.setValue(testVal);
		CPU.doOp(opcode, args);
		
		verifyLeft8(testVal, CPU.a.getValue(), oldFlags, CPU.status.getValue());
		
		
		oldFlags = CPU.status.getValue();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT3;
		CPU.a.setValue(testVal);	
		CPU.doOp(opcode, args);
		
		verifyLeft8(testVal, CPU.a.getValue(), oldFlags, CPU.status.getValue());
	}
	
	// 0x26
	@Test
	public void rotateLeftDP8() {
		int opcode = 0x26;
		int addr = BASEADDR;
		int bank = 0; // Direct page, must be zero
		int offset = 0x88;
		int dp = addr - offset;
		int testVal;
		int args[] = new int[]{offset & 0xFF};				
		int oldFlags;

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
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
		verifyLeft8(testVal, Core.mem.get(Size.BYTE, bank, addr), oldFlags, CPU.status.getValue());

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
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
		verifyLeft8(testVal, Core.mem.get(Size.BYTE, bank, addr), oldFlags, CPU.status.getValue());
		
		
		oldFlags = CPU.status.getValue();
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
		verifyLeft8(testVal, Core.mem.get(Size.BYTE, bank, addr), oldFlags, CPU.status.getValue());
	}
	
	// 0x36
	@Test
	public void rotateLeftDPX8() {
		int opcode = 0x36;
		int addr = BASEADDR;
		int bank = 0; // Direct page, must be zero
		int offset = 0x88;
		int x = 0x1234;
		int dp = addr - offset - x;
		int testVal;
		int args[] = new int[]{offset & 0xFF};				
		int oldFlags;

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
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
		verifyLeft8(testVal, Core.mem.get(Size.BYTE, bank, addr), oldFlags, CPU.status.getValue());

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
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
		verifyLeft8(testVal, Core.mem.get(Size.BYTE, bank, addr), oldFlags, CPU.status.getValue());
		
		
		oldFlags = CPU.status.getValue();
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
		verifyLeft8(testVal, Core.mem.get(Size.BYTE, bank, addr), oldFlags, CPU.status.getValue());
	}
	
	// 0x6E
	@Test
	public void rotateRightAbsolute8() {
		int opcode = 0x6E;
		int addr = BASEADDR;
		int bank = 3;
		int testVal;
		int args[] = new int[]{addr & 0xFF, (addr >> 8) & 0xFF};			
		int oldFlags;	

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
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
		verifyRight8(testVal, Core.mem.get(Size.BYTE, bank, addr), oldFlags, CPU.status.getValue());

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
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
		verifyRight8(testVal, Core.mem.get(Size.BYTE, bank, addr), oldFlags, CPU.status.getValue());
		
		
		oldFlags = CPU.status.getValue();
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
		verifyRight8(testVal, Core.mem.get(Size.BYTE, bank, addr), oldFlags, CPU.status.getValue());
	}
	
	// 0x7E
	@Test
	public void rotateRightAbsoluteX8() {
		int opcode = 0x7E;
		int addr = BASEADDR;
		int bank = 3;
		int x = 0x1234;
		int testVal;
		int argaddr = addr - x;
		int args[] = new int[]{argaddr & 0xFF, (argaddr >> 8) & 0xFF};				
		int oldFlags;

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
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
		verifyRight8(testVal, Core.mem.get(Size.BYTE, bank, addr), oldFlags, CPU.status.getValue());

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
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
		verifyRight8(testVal, Core.mem.get(Size.BYTE, bank, addr), oldFlags, CPU.status.getValue());
		
		
		oldFlags = CPU.status.getValue();
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
		verifyRight8(testVal, Core.mem.get(Size.BYTE, bank, addr), oldFlags, CPU.status.getValue());
	}
	
	// 0x6A
	@Test
	public void rotateRightAccumulator8() {
		int opcode = 0x6A;
		int testVal;
		int args[] = null;		
		int oldFlags;

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT1;
		CPU.a.setValue(testVal);
		CPU.doOp(opcode, args);
		
		verifyRight8(testVal, CPU.a.getValue(), oldFlags, CPU.status.getValue());

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT2;
		CPU.a.setValue(testVal);
		CPU.doOp(opcode, args);
		
		verifyRight8(testVal, CPU.a.getValue(), oldFlags, CPU.status.getValue());
		
		
		oldFlags = CPU.status.getValue();
		CPU.status.setMemoryAccess(true);
		Core.mem = new LoROMMemory();
		testVal = VAL8BIT3;
		CPU.a.setValue(testVal);	
		CPU.doOp(opcode, args);
		
		verifyRight8(testVal, CPU.a.getValue(), oldFlags, CPU.status.getValue());
	}
	
	// 0x66
	@Test
	public void rotateRightDP8() {
		int opcode = 0x66;
		int addr = BASEADDR;
		int bank = 0; // Direct page, must be zero
		int offset = 0x88;
		int dp = addr - offset;
		int testVal;
		int args[] = new int[]{offset & 0xFF};			
		int oldFlags;	

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
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
		verifyRight8(testVal, Core.mem.get(Size.BYTE, bank, addr), oldFlags, CPU.status.getValue());

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
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
		verifyRight8(testVal, Core.mem.get(Size.BYTE, bank, addr), oldFlags, CPU.status.getValue());
		
		
		oldFlags = CPU.status.getValue();
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
		verifyRight8(testVal, Core.mem.get(Size.BYTE, bank, addr), oldFlags, CPU.status.getValue());
	}
	
	// 0x76
	@Test
	public void rotateRightDPX8() {
		int opcode = 0x76;
		int addr = BASEADDR;
		int bank = 0; // Direct page, must be zero
		int offset = 0x88;
		int x = 0x1234;
		int dp = addr - offset - x;
		int testVal;
		int args[] = new int[]{offset & 0xFF};		
		int oldFlags;
		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
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
		verifyRight8(testVal, Core.mem.get(Size.BYTE, bank, addr), oldFlags, CPU.status.getValue());

		
		CPU.resetCPU();
		oldFlags = CPU.status.getValue();
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
		verifyRight8(testVal, Core.mem.get(Size.BYTE, bank, addr), oldFlags, CPU.status.getValue());
		
		
		oldFlags = CPU.status.getValue();
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
		verifyRight8(testVal, Core.mem.get(Size.BYTE, bank, addr), oldFlags, CPU.status.getValue());
	}
}
