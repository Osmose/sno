package edu.fit.cs.sno.snes.rom;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import edu.fit.cs.sno.snes.mem.Memory;
import edu.fit.cs.sno.util.Log;


public class RomLoader {
	private byte romData[];
	private int filepos;

	private RomInfo ri = new RomInfo();
	private int romChecksum;
	
	public RomLoader(InputStream is, boolean isZip)
	{
		try {
			loadData(is, isZip);
		} catch (IOException e) {
			Log.debug("Error loading file");
		}
		parseRom();
	}

	// Only valid for a LORom
	public void loadMemory(Memory m) {
//		if (!(m instanceof LoROMMemory) || !ri.lorom) {
//			throw new RuntimeException("Cannot load HiRoms yet...");
//		}
		
		//int[] rom = new int[0x7c*0x8000];
		int[] rom = new int[0x80*0x8000];
		for (int k = 0; k < romData.length; k++) {
			rom[k] = romData[k] & 0xFF;
		}
		
		m.setRom(rom);
	}
	
	private void parseRom() {
		romChecksum = computeChecksum();
		ri.lorom=false;
		try {
			if (parseRom(65472)==false) { // Try to parse as hirom, but if not, assume its lorom
				ri.lorom=true;
				parseRom(32704); // Assume its lorom
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			ri.lorom=true;
			parseRom(32704); // Assume its lorom
		}

		//if (ri.mbits > 16) // Does not seem to be true.
		//	ri.lorom = false;
		
		Log.debug(String.format("Calculated checksum: 0x%x\n", romChecksum));
	}
	
	private boolean parseRom(int offset) {
		filepos = offset;

		// Game Title
		byte name[] = new byte[21];
		read(name);
		ri.gameTitle = new String(name);
		
		// Rom Speed/Bank Size
		int temp = read();
		ri.bankSize = (byte) (temp & 0xF);
		ri.romSpeed = (byte) ((temp & 0xF0) >> 4);
		
		// Various other fields
		ri.romType  = read();
		ri.romSize  = read();
		ri.sramSize = read();
		ri.country  = read();
		ri.license  = read();
		ri.gameVersion = read();
		ri.inverseChecksum = (read()&0xFF | (read() <<8 & 0xFF00)) & 0xFFFF;
		ri.checksum = (read()&0xFF | (read() << 8 & 0xFF00)) & 0xFFFF;
		
		
		if (!ri.lorom && (~ri.checksum & 0xFFFF) != (ri.inverseChecksum & 0xFFFF)) {
			Log.debug("Error validating checksum/inverse checksum.");
			Log.debug("This means it could be a LoRom");
			return false;
		}
		if(romChecksum != ri.checksum) {
			Log.debug("Calculated checksum does not match stored checksum.");
		}
		RomInfo.printRomInfo(ri);
		
		return true;
	}

	public int computeChecksum()
	{
		int chunkSize = 128*1024; // 1mbit chunk
		int checksum = 0;
		byte chunk[];
		int power_two = 0;
		for(int i=0; i<6; i++) {
			if ((1<<i) >= ri.mbits) {
				power_two = i;
				break;
			}
		}
		filepos = 0;
		int half = (1<<(power_two-1));

		chunk = new byte[chunkSize*half];
		read(chunk);
		for(int i=0; i<chunk.length; i++) {
			checksum += chunk[i] & 0xFF;
		}
		int numRepeats = ri.mbits - half;
		int repeatchunks = ((1<<power_two) - half) / numRepeats;
		chunk = new byte[numRepeats * chunkSize];
		read(chunk);
		for(int i=0; i<repeatchunks; i++) {
			Log.debug("Adding repeat chunk " + i);
			for(int j=0; j<chunk.length; j++) {
				checksum += chunk[j] & 0xFF;
			}
		}
		return checksum & 0xFFFF;
	}
	
	private void loadData(InputStream is, boolean isZip) throws IOException {
		if(isZip) {
			ZipInputStream zis = new ZipInputStream(is);
			ZipEntry ze = zis.getNextEntry();
			Log.debug("Loading rom from zip: " + ze.getName());
			romData = new byte[(int) ze.getSize()];
			filepos = 0;
			while(true) {
				int read = zis.read(romData, filepos, romData.length-filepos);
				if(read<=0)break;
				filepos+=read;
			}
			zis.close();
			is.close();
		} else {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			int b;
			while ((b = is.read()) != -1) {
				os.write(b);
			}
			romData = os.toByteArray();
			is.close();
		}
		// Check for File header, remove if present
		if (romData.length % 1024 != 0) {
			romData = Arrays.copyOfRange(romData, 0x200, romData.length);
			Log.debug("Removing File Header");
		}
		Log.debug("Rom Loaded - size: " + romData.length);
		
		ri.fileSize = romData.length;
		
		ri.mbits = ri.fileSize / 131072;
		Log.debug("Rom size mbits: " + ri.mbits);
	}
	private void read(byte[] b) {
		System.arraycopy(Arrays.copyOfRange(romData, filepos, filepos+b.length), 0, b, 0, b.length);
		filepos += b.length;
	}

	private byte read() {
		if(filepos >= romData.length)
			throw new RuntimeException("Error reading rom");
		return romData[filepos++];
	}

	public int getRomChecksum() {
		return romChecksum;
	}

	public RomInfo getRomInfo() {
		return ri;
	}
}
