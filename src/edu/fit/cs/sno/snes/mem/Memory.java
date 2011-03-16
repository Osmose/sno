package edu.fit.cs.sno.snes.mem;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import edu.fit.cs.sno.snes.apu.hwregs.APURegisters;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.Timing;
import edu.fit.cs.sno.snes.cpu.hwregs.CPURegisters;
import edu.fit.cs.sno.snes.cpu.hwregs.CPUMath;
import edu.fit.cs.sno.snes.cpu.hwregs.DMA;
import edu.fit.cs.sno.snes.ppu.hwregs.BGRegisters;
import edu.fit.cs.sno.snes.ppu.hwregs.CGRAM;
import edu.fit.cs.sno.snes.ppu.hwregs.OAMRegisters;
import edu.fit.cs.sno.snes.ppu.hwregs.PPURegisters;
import edu.fit.cs.sno.snes.ppu.hwregs.VRAM;
import edu.fit.cs.sno.snes.ppu.hwregs.WindowRegisters;
import edu.fit.cs.sno.util.Log;
import edu.fit.cs.sno.util.Settings;

public abstract class Memory {
	protected Boolean isHiROM;
	protected int[] wram = new int[128 * 1024];
	protected int[] rom; // 0x80 chunks of 32k
	
	public abstract int get(Size size, int bank, int addr);
	public abstract void set(Size size, int bank, int addr, int val);
	
	// TODO: fix so the +1 isn't needed when trying to read a short from address 0x6000(last possible hwregister location)
	public HWRegister[] mmap = new HWRegister[0x4000+1]; // TODO: change back to private(used by core to display unimplemented registers)
	public Memory(){
		// CPU related hwregs
		mmap[0x4200 - 0x2000] = CPURegisters.interruptEnable;
		mmap[0x4207 - 0x2000] = CPURegisters.htimel;
		mmap[0x4208 - 0x2000] = CPURegisters.htimeh;
		mmap[0x4209 - 0x2000] = CPURegisters.vtimel;
		mmap[0x420A - 0x2000] = CPURegisters.vtimeh;
		mmap[0x420D - 0x2000] = CPURegisters.memsel;
		mmap[0x4210 - 0x2000] = CPURegisters.rdnmi;
		mmap[0x4211 - 0x2000] = CPURegisters.timeup;
		
		
		// CPU math registers(for multiplication/division)
		mmap[0x4202 - 0x2000] = CPUMath.wrmpya;
		mmap[0x4203 - 0x2000] = CPUMath.wrmpyb;
		mmap[0x4204 - 0x2000] = CPUMath.wrdivl;
		mmap[0x4205 - 0x2000] = CPUMath.wrdivh;
		mmap[0x4206 - 0x2000] = CPUMath.wrdivb;
		mmap[0x4214 - 0x2000] = CPUMath.rddivl;
		mmap[0x4215 - 0x2000] = CPUMath.rddivh;
		mmap[0x4216 - 0x2000] = CPUMath.rdmpyl;
		mmap[0x4217 - 0x2000] = CPUMath.rdmpyh;
		
		// DMA
		mmap[0x420B - 0x2000] = DMA.mdmaen;
		mmap[0x420C - 0x2000] = DMA.hdmaen;
		
		// DMA Channel x Registers
		for (int i = 0; i<8;i++) {
			mmap[0x4300 - 0x2000 + 0x10*i] = DMA.dmaReg[i].dmapx;
			mmap[0x4301 - 0x2000 + 0x10*i] = DMA.dmaReg[i].bbadx;
			mmap[0x4302 - 0x2000 + 0x10*i] = DMA.dmaReg[i].a1txl;
			mmap[0x4303 - 0x2000 + 0x10*i] = DMA.dmaReg[i].a1txh;
			mmap[0x4304 - 0x2000 + 0x10*i] = DMA.dmaReg[i].a1bx;
			mmap[0x4305 - 0x2000 + 0x10*i] = DMA.dmaReg[i].dasxl;
			mmap[0x4306 - 0x2000 + 0x10*i] = DMA.dmaReg[i].dasxh;
			mmap[0x4307 - 0x2000 + 0x10*i] = DMA.dmaReg[i].dasbh;
			mmap[0x4308 - 0x2000 + 0x10*i] = DMA.dmaReg[i].a2axl;
			mmap[0x4309 - 0x2000 + 0x10*i] = DMA.dmaReg[i].a2axh;
			mmap[0x430a - 0x2000 + 0x10*i] = DMA.dmaReg[i].nltrx;
		}
		
		// Video
		mmap[0x2100 - 0x2000] = PPURegisters.screenDisplay;
		
		// Background
		mmap[0x2105 - 0x2000] = BGRegisters.screenMode;
		mmap[0x2106 - 0x2000] = BGRegisters.mosaic;
		mmap[0x2107 - 0x2000] = BGRegisters.bg1sc;
		mmap[0x2108 - 0x2000] = BGRegisters.bg2sc;
		mmap[0x2109 - 0x2000] = BGRegisters.bg3sc;
		mmap[0x210A - 0x2000] = BGRegisters.bg4sc;
		mmap[0x210B - 0x2000] = BGRegisters.bg12nba;
		mmap[0x210C - 0x2000] = BGRegisters.bg34nba;
		
		// Background scrolling
		mmap[0x210D - 0x2000] = BGRegisters.bg1hofs;
		mmap[0x210E - 0x2000] = BGRegisters.bg1vofs;
		mmap[0x210F - 0x2000] = BGRegisters.bg2hofs;
		mmap[0x2110 - 0x2000] = BGRegisters.bg2vofs;
		mmap[0x2111 - 0x2000] = BGRegisters.bg3hofs;
		mmap[0x2112 - 0x2000] = BGRegisters.bg3vofs;
		mmap[0x2113 - 0x2000] = BGRegisters.bg4hofs;
		mmap[0x2114 - 0x2000] = BGRegisters.bg4vofs;
		
		// Mode 7 TODO: Unimplemented
		mmap[0x211A - 0x2000] = PPURegisters.m7;
		mmap[0x211B - 0x2000] = PPURegisters.m7;
		mmap[0x211C - 0x2000] = PPURegisters.m7;
		mmap[0x211D - 0x2000] = PPURegisters.m7;
		mmap[0x211E - 0x2000] = PPURegisters.m7;
		mmap[0x211F - 0x2000] = PPURegisters.m7;
		mmap[0x2120 - 0x2000] = PPURegisters.m7;
		
		// Window Registers TODO: unimplemented
		mmap[0x2123 - 0x2000] = WindowRegisters.w12sel;
		mmap[0x2124 - 0x2000] = WindowRegisters.w34sel;
		mmap[0x2125 - 0x2000] = WindowRegisters.wobjsel;
		mmap[0x2126 - 0x2000] = WindowRegisters.wh0;
		mmap[0x2127 - 0x2000] = WindowRegisters.wh1;
		mmap[0x2128 - 0x2000] = WindowRegisters.wh2;
		mmap[0x2129 - 0x2000] = WindowRegisters.wh3;
		mmap[0x212A - 0x2000] = WindowRegisters.wbglog;
		mmap[0x212B - 0x2000] = WindowRegisters.wobjlog;
		mmap[0x212E - 0x2000] = WindowRegisters.tmw;
		mmap[0x212F - 0x2000] = WindowRegisters.tsw;
		
		
		// OAM registers
		mmap[0x2101 - 0x2000] = OAMRegisters.OAMSize;
		mmap[0x2102 - 0x2000] = OAMRegisters.OAMAddrLow;
		mmap[0x2103 - 0x2000] = OAMRegisters.OAMAddrHigh;
        mmap[0x2104 - 0x2000] = OAMRegisters.OAMWrite;
        mmap[0x2138 - 0x2000] = OAMRegisters.OAMRead;
        
        // VRAM registers
        mmap[0x2115 - 0x2000] = VRAM.vmainc;
        mmap[0x2116 - 0x2000] = VRAM.vmaddl;
        mmap[0x2117 - 0x2000] = VRAM.vmaddh;
        mmap[0x2118 - 0x2000] = VRAM.vmwdatal;
        mmap[0x2119 - 0x2000] = VRAM.vmwdatah;
        mmap[0x2139 - 0x2000] = VRAM.vmrdatal;
        mmap[0x213A - 0x2000] = VRAM.vmrdatah;
        
		
        // PPU Stuff
		mmap[0x212C - 0x2000] = PPURegisters.tm;
		mmap[0x212D - 0x2000] = PPURegisters.ts;
		mmap[0x2133 - 0x2000] = PPURegisters.setini;
		
		// PPU Status
		mmap[0x4212 - 0x2000] = PPURegisters.hvbjoy;

		// Color Math Registers
		mmap[0x2130 - 0x2000] = PPURegisters.cgwsel;
		mmap[0x2131 - 0x2000] = PPURegisters.cgadsub;
		mmap[0x2132 - 0x2000] = PPURegisters.coldata;

		// CGRam
		mmap[0x2121 - 0x2000] = CGRAM.cgadd;
		mmap[0x2122 - 0x2000] = CGRAM.cgdata;
		mmap[0x213B - 0x2000] = CGRAM.cgdataread;
		
		// Audio IO Ports
		mmap[0x2140 - 0x2000] = APURegisters.apuio0;
		mmap[0x2141 - 0x2000] = APURegisters.apuio1;
		mmap[0x2142 - 0x2000] = APURegisters.apuio2;
		mmap[0x2143 - 0x2000] = APURegisters.apuio3;
		
		// Joypad
		mmap[0x4016 - 0x2000] = CPURegisters.joyser0;
		mmap[0x4017 - 0x2000] = CPURegisters.joyser1;
		mmap[0x4218 - 0x2000] = CPURegisters.joy1l;
		mmap[0x4219 - 0x2000] = CPURegisters.joy1h;
		
		// Initialize memory
		Arrays.fill(wram, 0x55);	// WRAM is initialized with 0x55
	};
	
	public Boolean isHiROM() {
		return isHiROM;
	}
	public int read(Size size, int bank, int addr) {
		boolean fastrom = false;
		
		// Determine speed by bank
		if (bank >= 0x80) {
			fastrom = true;
		}
		
		if ((bank == 0x00 || bank == 0x80) && addr >= 0x4000  && addr <= 0x41FF) {
			Timing.cycle(12); // 12 master cycles to access these
			return get(size,bank,addr);
		}
		
		if (fastrom){ // 6 cycles
			Timing.cycle(6);
			return get(size,bank,addr);
		} else { // 8 cycles
			Timing.cycle(8);
			return get(size,bank,addr);
		}
	}
	public void write(Size size, int bank, int addr, int val) {
		boolean fastrom = false;
		
		// Determine speed by bank
		if (bank >= 0x80) {
			fastrom = true;
		}
		
		if ((bank == 0x00 || bank == 0x80) && addr >= 0x4000  && addr <= 0x41FF) {
			Timing.cycle(12); // 12 master cycles to access these
			set(size,bank,addr,val);
			return;
		}
		
		if (fastrom){ // 6 cycles
			Timing.cycle(6);
			set(size,bank,addr,val);
			return;
		} else { // 8 cycles
			Timing.cycle(8);
			set(size,bank,addr,val);
			return;
		}
	}
	
	protected int getFromArray(Size size, int[] data, int index) {
		if (size == Size.SHORT && index != data.length-1) {
			return data[index + 1]<<8 | data[index];
		} else {
			return data[index];
		}
	}
	
	protected void setInArray(Size size, int[] data, int index, int value) {
		if (size == Size.SHORT && index != data.length-1) {
			data[index] = value & 0x00FF;
			data[index + 1] = (value & 0xFF00) >> 8;
		} else {
			data[index] = value;
		}
	}
	
	protected void writeHWReg(Size size, int addr, int value) {
		int reg = addr - 0x2000;
		
		if(mmap[reg] == null) {
			mmap[reg] = new UnimplementedHardwareRegister();
			Log.err(String.format("Write Memory Mapped register 0x%04x not implemented.", addr));
			if (Settings.get(Settings.MEM_THROW_INVALID_ADDR).equals("true"))
				throw new RuntimeException(String.format("Write Memory Mapped register 0x%04x not implemented.", addr));
		}
		
		if (size == Size.SHORT) {
			if(mmap[reg+1] == null) {
				mmap[reg+1] = new UnimplementedHardwareRegister();
				Log.err(String.format("Write Memory Mapped register 0x%04x not implemented.", addr+1));
				if (Settings.get(Settings.MEM_THROW_INVALID_ADDR).equals("true"))
					throw new RuntimeException(String.format("Write Memory Mapped register 0x%04x not implemented.", addr+1));
			}

			/*
			 * Low goes first unless you want games to look like they're being 
			 * played through Kanye's shutter shades.
			 */
			mmap[reg].setValue(value & 0x00FF);
			mmap[reg+1].setValue((value & 0xFF00) >> 8);
		} else {
			mmap[reg].setValue(value);
		}
	}
	
	protected int readHWReg(Size size, int addr) {
		int reg = addr - 0x2000;
		if (mmap[reg] == null) {
			//Log.err(String.format("Read Memory Mapped register 0x%04x not implemented.", addr));
			if (Settings.get(Settings.MEM_THROW_INVALID_ADDR).equals("true"))
				throw new RuntimeException(String.format("Read Memory Mapped register 0x%04x not implemented.", addr));
			return 0;
		}
		if (size == Size.SHORT) {
			if (mmap[reg+1] == null) {
				//mmap[addr-0x2000] = new UnimplementedHardwareRegister();
				//Log.err(String.format("Read Memory Mapped register 0x%04x not implemented.", addr+1));
				if (Settings.get(Settings.MEM_THROW_INVALID_ADDR).equals("true"))
					throw new RuntimeException(String.format("Read Memory Mapped register 0x%04x not implemented.", addr+1));
				return 0;
			}
			
			// LOW ALWAYS GOES FIRST
			int val = mmap[reg].getValue();
			val |= mmap[reg+1].getValue() << 8;
			return val;
		} else {
			return mmap[reg].getValue();
		}
	}

	public void setRom(int[] rom) {
		this.rom = rom;
	}
	
	protected void invalidMemoryWrite(String type, int bank, int addr) throws RuntimeException {
		System.out.println(String.format("Writing to invalid memory address (" + type + "):: 0x%02x:%04x",bank,addr));
		throw new RuntimeException(String.format("Writing to invalid memory address (" + type + "):: 0x%02x:%04x",bank,addr));
	}
	
	public void dumpWRAM() {
		if (Settings.get(Settings.DEBUG_DIR) != null) {
			try {
				String fname = Settings.get(Settings.DEBUG_DIR) + "/wram.bin";
				FileOutputStream fos = new FileOutputStream(fname);
				for(int i=0; i<wram.length; i++)
					fos.write(wram[i]);
				fos.close();
			} catch (IOException e) {
				System.out.println("Unable to dump wram");
				e.printStackTrace();
			}
		}
	}
}
