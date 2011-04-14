package edu.fit.cs.sno.snes.ppu.hwregs;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import edu.fit.cs.sno.snes.mem.HWRegister;
import edu.fit.cs.sno.snes.ppu.PPU;
import edu.fit.cs.sno.snes.ppu.SNESColor;
import edu.fit.cs.sno.util.Settings;

public class CGRAM {
	public static int cgram[] = new int[512]; // Color Pallete Memory
	public static Color colors[] = new Color[256];
	public static Color transparent = new Color(0, 0, 0, 0);
	public static int fixedColor = 0;
	
	// Caches colors; First index is brightness, second is SNES color
	public static int[][] cachedColors = new int[16][0x8000];
	
	private static boolean lowByte = true;
	
	public static int snesColorToARGB(int snesColor, int brightness) {
		int argbColor = cachedColors[brightness][snesColor];
		if (argbColor == 0) {
			// Convert the SNES-format color (integer in the form bbbbbgggggrrrrr, b = blue bits, g = green bits,
			// r = red bits) to an ARGB format color
			int r, g, b;
			r = ((int) (SNESColor.getColor(snesColor, SNESColor.RED) * (brightness / 15f)) & 0x1F) << 19;
			g = ((int) (SNESColor.getColor(snesColor, SNESColor.GREEN) * (brightness / 15f)) & 0x1F) << 11;
			b = ((int) (SNESColor.getColor(snesColor, SNESColor.BLUE) * (brightness / 15f)) & 0x1F) << 3;
			argbColor = (0xFF << 24) | r | g | b;
			
			cachedColors[brightness][snesColor] = argbColor;
		}
		
		return argbColor;
	}
	
	public static int getColor(int index) {
		index <<= 1;
		return cgram[index] | (cgram[index + 1] << 8);
	}
	
	public static void modFixedColor(int val, boolean modRed, boolean modGreen, boolean modBlue) {
		int r = (int)(modRed ? val : (fixedColor & 0x1F));
		int g = (int)(modGreen ? (val << 5) : (fixedColor & 0x3E0));
		int b = (int)(modBlue ? (val << 10) : (fixedColor & 0x7C00));
		
		fixedColor = r | g | b;
	}
	
	/**
	 * Reads the color palette from ram into a set of Color objects
	 */
	public static void readColors() {
		for (int i=0;i<512;i += 2) {
			int color = cgram[i] | (cgram[i+1]<<8);
			int r = (color & 0x1F);
			int g = ((color >> 5) & 0x1F);
			int b = ((color >> 10) & 0x1F);
			colors[i/2] = new Color(r, g, b);
		}
	}
	
	public static void testColors() {
		if (Settings.get(Settings.DEBUG_PPU_DIR) != null) {
			readColors();
			BufferedImage colorTest = new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB);
			Graphics g = colorTest.getGraphics();
			for(int i=0;i<16;i++) {
				for (int j=0;j<16;j++) {
					g.setColor(colors[i*16+j]);
					g.fillRect(i*32,j*32, 32, 32);
				}
			}
			try {
				ImageIO.write(colorTest, "PNG", new File(Settings.get(Settings.DEBUG_PPU_DIR) + "/colortest.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		dumpCGRAM();
	}
	
	public static void dumpCGRAM() {
		if (Settings.get(Settings.DEBUG_DIR) != null) {
			try {
				FileOutputStream fos = new FileOutputStream(Settings.get(Settings.DEBUG_DIR) + "/cgram.bin");
				for(int i=0; i<cgram.length; i++)
					fos.write(cgram[i]);
				fos.close();
			} catch (IOException e) {
				System.out.println("Unable to dump CGRam");
				e.printStackTrace();
			}
		}
	}
	
	public static void outputHexColors() {
		readColors();
		for (int k = 0; k < colors.length; k++) {
			if (k == colors.length / 2) System.out.println();
			System.out.format("%06x ", colors[k].getRGB() & 0x00ffffff);
		}
	}
	
	public static String getHexColor(int i) {
		i <<= 1;
		int color = cgram[i] | (cgram[i+1]<<8);
		int r = (color & 0x1F);
		int g = ((color >> 5) & 0x1F);
		int b = ((color >> 10) & 0x1F);
		return String.format("%02x%02x%02x", r, g, b);
	}
	
	/**
	 * 0x2121 - CGRam Address
	 */
	public static HWRegister cgadd = new HWRegister() {
		public void onWrite(int value) {
			super.onWrite((value << 1) % 512);
		}
	};
	
	/**
	 * 0x2122 - Data write to CGRam
	 */
	public static HWRegister cgdata = new HWRegister() {
		int lowVal;
		public void onWrite(int value) {
			if (lowByte) {
				lowVal = value;
			} else {
				cgram[cgadd.val] = lowVal;
				cgram[cgadd.val + 1] = value & 0x7F;
				cgadd.val = ((cgadd.val + 2) % 512);
			}
			
			lowByte = !lowByte;
		}
	};
	
	/**
	 * 0x213B - Data read from CGRam
	 */
	public static HWRegister cgdataread = new HWRegister() {
		int retVal = 0;
		public int getValue() {
			if (lowByte) {
				retVal = cgram[cgadd.val];
			} else {
				retVal &= 0x80;
				retVal |= cgram[cgadd.val + 1];
				cgadd.val = ((cgadd.val + 2) % 512);
			}
			
			lowByte = !lowByte;
			
			return retVal;
		}
	};
}
