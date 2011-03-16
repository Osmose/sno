package edu.fit.cs.sno.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.fit.cs.sno.snes.common.Size;

public class SizeTest {

	@Before
	public void setUp() throws Exception {
		Size b = Size.BYTE;
	}
	
	@Test
	public void testSize() {
		assertEquals(Size.BYTE, Size.valueOf("BYTE"));
		assertEquals(Size.SHORT, Size.valueOf("SHORT"));
		Size.values();
	}

	@Test
	public void testGetRealSize() {
		assertEquals(Size.BYTE, Size.BYTE.getRealSize());
	}

}
