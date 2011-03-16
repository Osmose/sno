package edu.fit.cs.sno.test.romloader;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.mem.LoROMMemory;
import edu.fit.cs.sno.snes.rom.RomLoader;

@RunWith(Parameterized.class)
public class RomLoaderTest {
	private static String romPath = "roms";
	
	@Parameters
	public static Collection getAllRoms() {
		ArrayList<Object[]> allRoms = new ArrayList<Object[]>();
		File dir = new File(romPath);
		String items[] = dir.list();
		for (String filename: items) {
			if (!filename.endsWith(".zip") && !filename.endsWith(".smc"))
				continue;
			allRoms.add(new Object[]{romPath + "/" + filename});
		}
		return allRoms;
	}

	private String filename;
	public RomLoaderTest(String filename){
		this.filename = filename;
		Core.mem = new LoROMMemory();
	}

	@Test
	public void testRomLoader() {
		//RomLoader rl = new RomLoader(filename);
		//assertEquals(rl.getRomChecksum(), rl.getRomInfo().checksum);
		//rl.loadMemory(new LoROMMemory());
	}
}
