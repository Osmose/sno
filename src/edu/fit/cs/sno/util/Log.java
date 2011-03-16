package edu.fit.cs.sno.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class Log {
	
	public static boolean enabled = false;
	
	public static Log debug = new Log(null);
	public static Log err = new Log(null);
	public static Log instruction = new Log(null);
	public static Log apu = new Log(null);
	
	static {
		setLogEnabled(Settings.isTrue(Settings.LOG_ENABLED));
	}
	
	private PrintStream stream;
	
	public Log(String destination) {
		stream = getStream(destination);
	}
	
	public void setStream(PrintStream st) {
		stream = st;
	}
	
	public void log(String str) {
		if (enabled()) stream.println(str);
	}
	
	public boolean enabled() {
		return stream != null;
	}
	
	public static void debug(String str) {
		debug.log(str);
	}
	
	public static void err(String str) {
		err.log(str);
	}
	
	public static void instruction(String str) {
		instruction.log(str);
	}
	
	public static void apu(String str) {
		apu.log(str);
	}
	
	public static void setLogEnabled(boolean enabled) {
		Log.enabled = enabled;
		if (!enabled) {
			debug.setStream(null);
			err.setStream(null);
			instruction.setStream(null);
			apu.setStream(null);
			
			System.out.println("Logging disabled.");
		} else {
			debug.setStream(getStream(Settings.get(Settings.LOG_DEBUG_OUT)));
			err.setStream(getStream(Settings.get(Settings.LOG_ERR_OUT)));
			instruction.setStream(getStream(Settings.get(Settings.LOG_INSTRUCTIONS)));
			apu.setStream(getStream(Settings.get(Settings.LOG_APU)));
			
			System.out.println("Logging enabled.");
		}
	}
	
	private static PrintStream getStream(String type) {
		if (type == null) return null;
		
		if (type.equals("stdout")) {
			return System.out;
		} else if (type.equals("stderr")) {
			return System.err;
		} else if (type.startsWith("file:")) {
			String relativeDir = Settings.get(Settings.DEBUG_DIR);
			try {
				return new PrintStream(new FileOutputStream(relativeDir + "/" + type.substring(5)));
			} catch (FileNotFoundException e) {
				System.out.println("WARNING: Cannot log to file: " + relativeDir + "/" + type.substring(5) + ", Redirecting to sysout");
				return System.out;
			}
		}
		
		return null;
	}
	
}
