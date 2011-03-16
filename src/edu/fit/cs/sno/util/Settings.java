package edu.fit.cs.sno.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import edu.fit.cs.sno.applet.SNOApplet;

public class Settings {

	private static Properties props = new Properties();
	private static boolean loaded = false;
	
	public static void init() {
		if (loaded) return;
		try {
			props.load(new FileInputStream("sno.properties"));
			loaded = true;
		} catch (Exception err) {
			// Do nothing for now
		}
	}
	
	public static void initFromJar() {
		if (loaded) return;
		try {
			props.load(Settings.class.getResourceAsStream("/sno.properties"));
			loaded = true;
		} catch (Exception e) {
			// Do nothing for now
		}
	}
	
	public static String get(String str) {
		if (!loaded) init();
		
		if (SNOApplet.instance != null && SNOApplet.instance.getParameter(str) != null) {
			if (SNOApplet.instance.getParameter(str).equals("null")) {
				return null;
			} else {
				return SNOApplet.instance.getParameter(str);
			}
		} else {
			return props.getProperty(str);
		}
	}
	
	public static void set(String str, String value) {
		if (!loaded) init();
		props.setProperty(str, value);
	}
	
	public static boolean isTrue(String str) {
		return get(str) != null && get(str).equals("true");
	}
	
	public static int getInt(String str) {
		return Integer.parseInt(get(str));
	}
	
	public static boolean isSet(String str) {
		String val = get(str);
		
		return !(val == null || val.equals("") || val.equals("null"));
	}
	
	public static final String DEBUG_DIR = "sno.debug.dir";
	public static final String DEBUG_PPU_DIR = "sno.debug.gfx.dir";
	
	public static final String LOG_ENABLED = "sno.log.enabled";
	public static final String LOG_DEBUG_OUT = "sno.log.debugOut";
	public static final String LOG_ERR_OUT = "sno.log.errOut";
	public static final String LOG_INSTRUCTIONS = "sno.log.instructionsOut";
	public static final String LOG_APU = "sno.log.apuOut";
	
	public static final String MEM_THROW_INVALID_ADDR = "sno.mem.throwInvalidAddr";
	public static final String CPU_LIMIT_SPEED = "sno.cpu.limitSpeed";
	public static final String CPU_DEBUG_TRACE = "sno.cpu.debugTrace";
	public static final String CPU_ALT_DEBUG = "sno.cpu.snes9xDebugFormat";
	public static final String CORE_MAX_INSTRUCTIONS = "sno.core.maxInstructions";
	
	public static final String APPLET_FULLSCREEN = "sno.applet.fullscreen";
	public static final String APPLET_WINDOW_WIDTH = "sno.applet.width";
	public static final String APPLET_WINDOW_HEIGHT = "sno.applet.height";
	public static final String AUTO_FRAME_SKIP = "sno.ppu.autoFrameSkip";
	public static final String FRAMES_TO_SKIP  = "sno.ppu.framesToSkip";
	public static final String SOUND_EMULATION = "sno.apu.soundEmulation";
	public static final String MUTE_SOUND = "sno.applet.mute";
	public static final String DEBUG_OUT = "sno.cpu.debugOut";
	public static final String ROM_URL = "sno.rom.url";
	
	
 
	
}
