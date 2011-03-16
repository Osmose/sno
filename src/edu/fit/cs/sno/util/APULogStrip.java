package edu.fit.cs.sno.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class APULogStrip {

	public static void main(String[] args) throws Exception {
		Scanner in = new Scanner(new FileInputStream(args[0]));
		PrintStream out = new PrintStream(new FileOutputStream(args[1]));
		
		String str;
		while(in.hasNextLine()) {
			str = in.nextLine();
			out.println(str.substring(0, 8) + str.substring(36, 66));
		}
		
		in.close();
		out.close();
	}
	
}
