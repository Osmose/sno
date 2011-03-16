package edu.fit.cs.sno.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.util.Util;

public class UtilTest {

	@Test
	public void testLimit() {
		assertEquals(0x00, Util.limit(Size.BYTE, 0x00));
		assertEquals(0xFF, Util.limit(Size.BYTE, 0xFF));
		assertEquals(0x00, Util.limit(Size.BYTE, 0xFF+1));
		assertEquals(0xFF, Util.limit(Size.BYTE, 0xFFFF));
		assertEquals(0xCA, Util.limit(Size.BYTE, 0xFDCA));
		
		assertEquals(0x0000, Util.limit(Size.SHORT, 0x0000));
		assertEquals(0xFFFF, Util.limit(Size.SHORT, 0xFFFF));
		assertEquals(0x0000, Util.limit(Size.SHORT, 0xFFFF+1));
		assertEquals(0xFFFF, Util.limit(Size.SHORT,  0xCBFFFF));
		assertEquals(0xFDCA, Util.limit(Size.SHORT,  0xADFDCA));
	}

	@Test
	public void testLimitShort() {
		assertEquals(0x0000, Util.limitShort(0x0000));
		assertEquals(0xFFFF, Util.limitShort(0xFFFF));
		assertEquals(0x0000, Util.limitShort(0xFFFF+1));
	}
	
	@Test
	public void testBCDToInt() {
		assertEquals(1342, Util.bcdToInt(0x1342));
	}
	
	@Test
	public void testBCDAdjustAdd() {
		assertEquals(0x31, Util.bcdAdjustAdd(Size.BYTE, 0x13 + 0x18));
		assertEquals(0x10, Util.bcdAdjustAdd(Size.BYTE, 0x70 + 0x40) & 0xFF);
		assertEquals(0x00, Util.bcdAdjustAdd(Size.BYTE, 0x70 + 0x30) & 0xFF);
		
		assertEquals(0x3000, Util.bcdAdjustAdd(Size.SHORT, 0x2999 + 0x1));
		assertEquals(0x0003, Util.bcdAdjustAdd(Size.SHORT, 0x9999 + 0x4) & 0xFFFF);
		assertEquals(0x0000, Util.bcdAdjustAdd(Size.SHORT, 0x9999 + 0x0001) & 0xFFFF);
		assertEquals(0x0000, Util.bcdAdjustAdd(Size.SHORT, 0x8888 + 0x1112) & 0xFFFF);
	}
	
	@Test
	public void testBCDAdjustSubtract() {
		assertEquals(0x05, Util.bcdAdjustSubtract(Size.BYTE, 0x13 - 0x8));
		assertEquals(0x90, Util.bcdAdjustSubtract(Size.BYTE, 0x10 - 0x20) & 0xFF);
		
		assertEquals(0x1999, Util.bcdAdjustSubtract(Size.SHORT, 0x2000 - 0x1));
		assertEquals(0x9999, Util.bcdAdjustSubtract(Size.SHORT, 0x0 - 0x1) & 0xFFFF);
	}

}
