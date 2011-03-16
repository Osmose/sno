package edu.fit.cs.sno.test.cpu;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.cpu.instructions.BlockMove;
import edu.fit.cs.sno.snes.mem.LoROMMemory;
import edu.fit.cs.sno.util.Settings;

public class BlockMoveTest {

	@Before
	public void setUp() throws Exception {
		new BlockMove(); // For 100% coverage
		Settings.init();
		CPU.resetCPU();
		Core.mem = new LoROMMemory();
	}

	@Test
	public void BlockMovePositive() {
		final int sourceAddress = 1000;
		final int sourceBank = 0x00;
		final int destAddress = 1010;
		final int destBank = 0x00;
		final int numBytes = 6;
		
		CPU.a.setRealValue(numBytes-1);
		CPU.x.setRealValue(sourceAddress);
		CPU.y.setRealValue(destAddress);
		int []args = {sourceBank, destBank};
		final int []values = {1,2,3,4,5,6};
		Core.mem.set(Size.BYTE, args[0], sourceAddress, values[0]);
		Core.mem.set(Size.BYTE, args[0], sourceAddress+1, values[1]);
		Core.mem.set(Size.BYTE, args[0], sourceAddress+2, values[2]);
		Core.mem.set(Size.BYTE, args[0], sourceAddress+3, values[3]);
		Core.mem.set(Size.BYTE, args[0], sourceAddress+4, values[4]);
		Core.mem.set(Size.BYTE, args[0], sourceAddress+5, values[5]);
		CPU.doOp(0x54, args);
		
		for( int i=0;i<numBytes;i++) {
			assertEquals(values[i], Core.mem.get(Size.BYTE, args[1], destAddress+i));
		}
		assertEquals(CPU.a.getValue(Size.SHORT), 0xFFFF);
		assertEquals(sourceAddress+numBytes, CPU.x.getValue());
		assertEquals(CPU.dbr.getRealValue(), destBank);
	}
	
	@Test
	public void BlockMoveNegative() {
		final int sourceAddress = 1000;
		final int sourceBank = 0x00;
		final int destAddress = 1010;
		final int destBank = 0x00;
		final int numBytes = 6;
		
		CPU.a.setRealValue(numBytes-1);
		CPU.x.setRealValue(sourceAddress);
		CPU.y.setRealValue(destAddress);
		int []args = {sourceBank, destBank};
		final int []values = {1,2,3,4,5,6};
		Core.mem.set(Size.BYTE, args[0], sourceAddress, values[0]);
		Core.mem.set(Size.BYTE, args[0], sourceAddress-1, values[1]);
		Core.mem.set(Size.BYTE, args[0], sourceAddress-2, values[2]);
		Core.mem.set(Size.BYTE, args[0], sourceAddress-3, values[3]);
		Core.mem.set(Size.BYTE, args[0], sourceAddress-4, values[4]);
		Core.mem.set(Size.BYTE, args[0], sourceAddress-5, values[5]);
		CPU.doOp(0x44, args);
		
		for( int i=0;i<numBytes;i++) {
			assertEquals(values[i], Core.mem.get(Size.BYTE, args[1], destAddress-i));
		}
		assertEquals(CPU.a.getValue(Size.SHORT), 0xFFFF);
		assertEquals(sourceAddress-numBytes, CPU.x.getValue());
		assertEquals(destAddress-numBytes, CPU.y.getValue());
		assertEquals(CPU.dbr.getRealValue(), destBank);
	}
}

