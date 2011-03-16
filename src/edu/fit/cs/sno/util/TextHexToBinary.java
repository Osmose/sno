package edu.fit.cs.sno.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Scanner;

/**
 * Small tool to convert a text file containing hex numbers in text
 * format to a binary file of those hex values. Useful for converting
 * hex dumps from snes9xdbg to binary dumps.
 */
public class TextHexToBinary {
	
	public static void main(String[] args) throws Exception {
		Scanner in = new Scanner(new FileInputStream(args[0]));
		FileOutputStream out = new FileOutputStream(args[1]);
		
		while(in.hasNext()) {
			out.write(Integer.parseInt(in.next(), 16));
		}
		
		in.close();
		out.close();
	}

}
