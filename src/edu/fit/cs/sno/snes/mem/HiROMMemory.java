package edu.fit.cs.sno.snes.mem;

import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.util.Log;
import edu.fit.cs.sno.util.Settings;
import edu.fit.cs.sno.util.Util;

public class HiROMMemory extends Memory {

	public HiROMMemory(){
		super();
		isHiROM = true;
	}
	public int get(Size size, int bank, int addr) {
		// Which "bank set" do you want?
		if (Util.inRange(bank, 0x0, 0x3F)) {	// WRAM, hardware registers, game pak RAM, SRAM, or ROM chunk
			// SRAM
			if (bank == 0x20 && Util.inRange(addr, 0x6000, 0x7FFF)) {
				return 0;
			}
			
			if (addr < 0x2000) { // WRAM
				return getFromArray(size, wram, addr);
			} else if (addr >=0x2000 && addr < 0x6000) { // Hardware registers
				return readHWReg(size, addr);
			} else if (addr >= 0x8000) { // ROM Chunks
				return getFromArray(size, rom, 0x8000+ (bank*0x10000) + (addr-0x8000));
			}
		} else if (Util.inRange(bank, 0x40, 0x7D)) {	// ROM chunks
			return getFromArray(size, rom, 0x8000+((bank - 0x40)*0x10000) + addr);
		} else if (bank == 0x7E) {	// WRAM, System RAM 1
			return getFromArray(size, wram, addr);
		} else if (bank == 0x7F) {	// Moar wram
			return getFromArray(size, wram, (addr + 0x10000));
		} else if (Util.inRange(bank, 0x80, 0xBF)) {
			// SRAM
			if (bank == 0xA0 && Util.inRange(addr, 0x6000, 0x7FFF)) {
				return 0;
			}
			
			if (addr < 0x2000) { // WRAM
				return getFromArray(size, wram, addr);
			} else if (addr >=0x2000 && addr < 0x6000) { // Hardware registers
				return readHWReg(size, addr);
			} else if (addr >= 0x8000) { // ROM Chunks
				return getFromArray(size, rom, 0x8000+ ((bank - 0x80) * 0x10000) + (addr-0x8000));
			}
		} else if (Util.inRange(bank, 0xC0, 0xFF)) {
			return getFromArray(size, rom, ((bank - 0xC0) * 0x10000) + addr);
		}
		
		if (Settings.get(Settings.MEM_THROW_INVALID_ADDR).equals("true"))
			throw new RuntimeException(String.format("Invalid memory address: 0x%02x:%04x",bank,addr));
		return 0;
	}

	public void set(Size size, int bank, int addr, int val) {
		// Which "bank set" do you want?
		if (Util.inRange(bank, 0x0, 0x3F)) {	// WRAM, hardware registers, game pak RAM, SRAM, or ROM chunk			
			if (addr < 0x2000) { // WRAM
				setInArray(size, wram, addr, val);
				return;
			} else if (addr >=0x2000 && addr < 0x6000) { // Hardware registers
				writeHWReg(size, addr, val);
				return;
			} else if (addr >= 0x8000) { // ROM Chunks
				invalidMemoryWrite("ROM", bank, addr);

			}
		} else if (Util.inRange(bank, 0x40, 0x7D)) {	// ROM chunks
			invalidMemoryWrite("ROM", bank, addr);
		} else if (bank == 0x7E) {	// WRAM, System RAM 1
			setInArray(size, wram, addr, val);
			return;
		} else if (bank == 0x7F) {	// Moar WRAM
			setInArray(size, wram, (addr + 0x10000), val);
			return;
		} else if (Util.inRange(bank, 0x80, 0xBF)) {			
			if (addr < 0x2000) { // WRAM
				setInArray(size, wram, addr, val);
				return;
			} else if (addr >=0x2000 && addr < 0x6000) { // Hardware registers
				writeHWReg(size, addr, val);
				return;
			} else if (addr >= 0x8000) { // ROM Chunks
				invalidMemoryWrite("ROM", bank, addr);
			}
		} else if (Util.inRange(bank, 0xC0, 0xFF)) {
			Log.err(String.format("Invalid memory address: 0x%02x:%04x",bank,addr));
			invalidMemoryWrite("ROM", bank, addr);
		}
		
		if (Settings.get(Settings.MEM_THROW_INVALID_ADDR).equals("true"))
			throw new RuntimeException(String.format("Invalid memory address: 0x%02x:%04x",bank,addr));
	}
	
}
