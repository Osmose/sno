package edu.fit.cs.sno.util;

import java.io.InputStream;
import java.net.URL;

import edu.fit.cs.sno.applet.SNOApplet;
import edu.fit.cs.sno.snes.common.Size;


/**
 * General utility functions not specific to any one component
 */
public class Util {

	/**
	 * Mask a value based on its intended size
	 * @param size Size of value
	 * @param val Value to limit
	 * @return Limited value
	 */
	public static int limit(Size size, int val) {
		if (size.sizeMask == 0)
			throw new RuntimeException("Invalid sizeMask being used; code should probably read size.getRealSize()");
		return val & size.sizeMask;
	}
	
	public static int limitShort(int val) {
		return limit(Size.SHORT, val);
	}
	
	/**
	 * Converts a negative byte in  two's complement to a negative java integer of the same magnitude
	 * @param val
	 * @return sign extended value
	 */
	public static int signExtendByte(int val) {
		if ((val & Size.BYTE.topBitMask) != 0) {
			return val + 0xFF00;
		} else {
			return val;
		}
	}
	
	/**
	 * Fixes any invalid digits in a binary-coded decimal after addition
	 * @param size Size of number to adjust
	 * @param val Value to adjust
	 */
	public static int bcdAdjustAdd(Size size, int val) {
		int nibbleCount = 2;
		if (size.getRealSize() == Size.SHORT) {
			nibbleCount = 4;
		}
		
		// For each 4-bit digit...
		for (int k = 0; k < nibbleCount; k++) {
			// Are those 4-bits larger than 9?
			int digit = ((val & (0xF << (k * 4))) >> (k * 4));
			if (digit > 9) {
				// If so, add 6 to the digit
				val += (6 << (k * 4)); 
			}
		}
		
		return val;
	}
	
	/**
	 * Fixes any invalid digits in a binary-coded decimal after subtraction
	 * @param size Size of number to adjust
	 * @param val Value to adjust
	 */
	public static int bcdAdjustSubtract(Size size, int val) {
		int nibbleCount = 2;
		if (size.getRealSize() == Size.SHORT) {
			nibbleCount = 4;
		}
		
		// For each 4-bit digit...
		for (int k = 0; k < nibbleCount; k++) {
			// Are those 4-bits larger than 9?
			int digit = ((val & (0xF << (k * 4))) >> (k * 4));
			if (digit > 9) {
				// If so, subtract 6 from the digit
				val -= (6 << (k * 4)); 
			}
		}
		
		return val;
	}
	
	/**
	 * Converts a BCD integer to a binary integer (unsigned only)
	 * @param val BCD int to convert
	 * @return Java integer representing value of BCD int
	 */
	public static int bcdToInt(int val) {
		int returnVal = 0;
		
		for (int k = 0; k < 8; k++) {
			int digit = ((val & (0xF << (k * 4))) >> (k * 4));
			returnVal += (digit * Math.pow(10, k));
		}
		
		return returnVal;
	}
	
	/**
	 * Join array elements with a string
	 * @param strs Array to join
	 * @param glue String to join with 
	 * @return string containing the array elements
	 */
	public static String implode(Object[] strs, String glue) {
		StringBuilder sb = new StringBuilder();
		
		for (int k = 0; k < strs.length; k++) {
			if (k != 0) sb.append(glue);
			sb.append(strs[k].toString());
		}
		
		return sb.toString();
	}
	
	/**
	 * Join array elements with a string
	 * @param strs Array to join
	 * @param glue String to join with 
	 * @return string containing the array elements
	 */
	public static String implode(int[] strs, String glue) {
		StringBuilder sb = new StringBuilder();
		
		for (int k = 0; k < strs.length; k++) {
			if (k != 0) sb.append(glue);
			sb.append(strs[k]);
		}
		
		return sb.toString();
	}
	
	/**
	 * Checks if a value is within a range (inclusive)
	 * @param val Value to check
	 * @param high High bound
	 * @param low Low bound
	 * @return True if within range, false otherwise
	 */
	public static boolean inRange(int val, int low, int high) {
		return (val >= low && val <= high);
	}
	
	public static int reverseBits(int val, int digits) {
		int newVal = 0;
		for (int k = 0; k < digits; k++) {
			newVal = (newVal << 1) | (val & 1);
			val = val >> 1;
		}
		
		return newVal;
	}
	
	public static InputStream getStreamFromUrl(String loc) {
		InputStream is = null;
		try {
			URL url;
			if (loc.startsWith("http://")) { // Check for absolute URL
				url = new URL(loc);
			} else if (SNOApplet.instance != null) {
				url = new URL(SNOApplet.instance.getDocumentBase(), loc);
			} else {
				return null;
			}
			
			is = url.openStream();
		} catch (Exception err) {
			System.out.println("Failed loading from url: " + loc);
		}
		
		return is;
	}

	@SuppressWarnings("unused")
	public static void brk() {
		int a = 1;
	}
	@SuppressWarnings("unused")
	public static void brk() {
		int a = 1;
	}
}
