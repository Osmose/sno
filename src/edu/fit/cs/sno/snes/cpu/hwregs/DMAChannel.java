package edu.fit.cs.sno.snes.cpu.hwregs;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.Timing;
import edu.fit.cs.sno.util.Log;
import edu.fit.cs.sno.util.Util;

public class DMAChannel {
	boolean hdmaEnabled;

	boolean doTransfer = false;      // Custom variable for whether hdma is enabled for this or not

	boolean direction = true;        // True = Read PPU, false = Write PPU
	boolean addressMode = true;      // HDMA only(true=indirect, false=direct)
	boolean addressIncrement = true; // True=decrement, False=increment
	boolean fixedTransfer = true;    // Do we allow addressIncrement to have an effect?
	int transferMode = 0x7;          // TransferMode
	
	int srcAddress = 0xFFFF;
	int srcBank = 0xFF;
	int dstRegister = 0x21FF;
	
	int indirectBank = 0xFF;
	int transferSize = 0xFFFF; // Also known as indirect address

	// Updated at the beginning of a frame(copied from 42x2/3)
	int tableAddr = 0xFFFF;

	int rlc = 0xFF; // repeat/line counter

	boolean frameDisabled=true;//is hdma disabled for this frame?

	private boolean isRepeat() {
		return (rlc & 0x80) == 0x80;
	}
	private int getLineCounter() {
		return rlc & 0x7F;
	}
	
	
	public void start() {
		
		if (transferSize == 0x0000) {
			transferSize = 0x10000;
		}
		int cycleCount = transferSize *8 + 24; // 8 cyles per byte + 24overhead
		
		if (direction == false) dmaWritePPU();
		else                    dmaReadPPU();
		
		Timing.cycle(cycleCount);
	};
	
	/**
	 * Happens every frame(on scanline 0)
	 */
	public void initHDMA() {
		if (!hdmaEnabled) return;
		
		// Copy AAddress into TableAddress
		// Load repeat/line counter from table
		// load indirect address
		// set enable transfer
		tableAddr = srcAddress;
		rlc = Core.mem.get(Size.BYTE, srcBank, tableAddr);
		tableAddr++;
		if (rlc==0) {
			frameDisabled = true;
			doTransfer = false;
			return;
		}
		
		if (addressMode == true) { // Indirect
			transferSize = Core.mem.get(Size.SHORT, srcBank, tableAddr);
			tableAddr += 2;
		}
		frameDisabled = false;
		doTransfer = true;
	}
	
	public void doHDMA() {
		if (frameDisabled || !hdmaEnabled) return;
		
		if (doTransfer) {
			if (direction == false) hdmaWritePPU();
			else                    hdmaReadPPU();
		}
		rlc = Util.limit(Size.BYTE, rlc-1);
		doTransfer = isRepeat();
		if (getLineCounter() == 0) {
			rlc = Core.mem.get(Size.BYTE, srcBank, tableAddr);
			tableAddr++;
			if (addressMode == true) { // Indirect
				transferSize = Core.mem.get(Size.SHORT, srcBank, tableAddr);
				tableAddr += 2;
			}
			// Handle special case if rlc == 0(rlc is $43xA)
			// SEE: http://wiki.superfamicom.org/snes/show/DMA+%26+HDMA
			if (rlc == 0) {
				frameDisabled = true;
			}
			doTransfer = true;
		}
	}
	
	private void hdmaReadPPU() {
		System.out.println("HDMA Read: Not implemented");
	}
	private void dmaReadPPU() {
		while(transferSize>0) {
			int size = dmaTransferPPUOnce(0, dstRegister, srcBank, srcAddress);
			adjustSrcAddress(size);
			transferSize -= size;
		}
	}
	
	private void hdmaWritePPU() {
		if (addressMode) { // Indirect address
			int size = dmaTransferPPUOnce(indirectBank, transferSize, 0, dstRegister);
			transferSize += size;
		} else {
			int size = dmaTransferPPUOnce(srcBank, tableAddr, 0, dstRegister);
			tableAddr += size;
		}
	}
	
	private void dmaWritePPU() {
		while(transferSize>0) {
			int size = dmaTransferPPUOnce(srcBank, srcAddress, 0, dstRegister);
			adjustSrcAddress(size);
			transferSize -= size;
		}
	}
	
	private int dmaTransferPPUOnce(int fromBank, int fromAddress, int toBank, int toAddress) {
		// TODO: for normal dma transfers, only write to at most $transferSize registers, even if we are supposed to write to multiple ones this call
		// TODO: i.e. break out of the statement if transfersize == 0 at any point
		int size = 0;
		int tmp = 0;
		if (transferMode == 0x0) { // 1 register, write once(1byte)
			tmp = Core.mem.get(Size.BYTE, fromBank, fromAddress);
			Core.mem.set(Size.BYTE, toBank, toAddress, tmp);
			size = 1;
		} else if (transferMode == 0x01) { // 2 Register write once(2bytes)
			tmp = Core.mem.get(Size.BYTE, fromBank, fromAddress+0);
			Core.mem.set(Size.BYTE, toBank, toAddress, tmp);
			
			// Don't fetch the next byte if it is a fixed transfer
			if (!fixedTransfer)	tmp = Core.mem.get(Size.BYTE, fromBank, fromAddress+1);
			Core.mem.set(Size.BYTE, toBank, toAddress+1, tmp);
			size = 2;
		} else  if (transferMode == 0x02 || transferMode == 0x06) { // 1 Register Write Twice(2bytes)
			tmp = Core.mem.get(Size.BYTE, fromBank, fromAddress+0);
			Core.mem.set(Size.BYTE, toBank, toAddress, tmp);
			
			if (!fixedTransfer)	tmp = Core.mem.get(Size.BYTE, fromBank, fromAddress+1);
			Core.mem.set(Size.BYTE, toBank, toAddress, tmp);
			size = 2;
		} else if (transferMode == 0x03 || transferMode == 0x07) { // 2 Register Write Twice(4bytes)
			tmp = Core.mem.get(Size.BYTE, fromBank, fromAddress+0);
			Core.mem.set(Size.BYTE, toBank, toAddress, tmp);
			
			if (!fixedTransfer)	tmp = Core.mem.get(Size.BYTE, fromBank, fromAddress+1);
			Core.mem.set(Size.BYTE, toBank, toAddress, tmp);
			
			if (!fixedTransfer)	tmp = Core.mem.get(Size.BYTE, fromBank, fromAddress+2);
			Core.mem.set(Size.BYTE, toBank, toAddress+1, tmp);
			
			if (!fixedTransfer)	tmp = Core.mem.get(Size.BYTE, fromBank, fromAddress+3);
			Core.mem.set(Size.BYTE, toBank, toAddress+1, tmp);
			size = 4;
		} else if (transferMode == 0x04) { // 4 Registers Write once(4bytes)
			tmp = Core.mem.get(Size.BYTE, fromBank, fromAddress+0);
			Core.mem.set(Size.BYTE, toBank, toAddress, tmp);
			
			if (!fixedTransfer)	tmp = Core.mem.get(Size.BYTE, fromBank, fromAddress+1);
			Core.mem.set(Size.BYTE, toBank, toAddress+1, tmp);
			
			if (!fixedTransfer)	tmp = Core.mem.get(Size.BYTE, fromBank, fromAddress+2);
			Core.mem.set(Size.BYTE, toBank, toAddress+2, tmp);
			
			if (!fixedTransfer)	tmp = Core.mem.get(Size.BYTE, fromBank, fromAddress+3);
			Core.mem.set(Size.BYTE, toBank, toAddress+3, tmp);
			size = 4;
		} else {
			System.out.println("Unknown transfer mode");
			return 0;
		}
		return size;
	}
	
	private void adjustSrcAddress(int size) {
		if (!fixedTransfer) {
			if (addressIncrement) // Decrease srcAddress
				srcAddress-=size;
			else
				srcAddress+=size; // Increase srcAddress
		}
	}
	
	@Override
	public String toString() {
		String r = "DMA Settings: \n";
		r += "  Direction:         " + (direction?"Read PPU":"Write PPU") + "\n";
		r += "  HDMA Address Mode: " + (addressMode?"table=pointer":"table=data") + "\n";
		r += "  Address Increment: " + (addressIncrement?"Decrement":"Increment") + "\n";
		r += "  Fixed Transfer:    " + fixedTransfer + "\n";
		r += "  Transfer Mode:     0b" + Integer.toBinaryString(transferMode)+"\n";
		r += String.format("  Source Address:    %02X:%04X\n",srcBank, srcAddress);
		r += String.format("  Destination Reg:   %04X\n", dstRegister);
		r += String.format("  Size/IndirectAddr: %04X\n", transferSize);
		r += String.format("  Indirect Bank:     %02X\n", indirectBank);
		return r;
	}
}
