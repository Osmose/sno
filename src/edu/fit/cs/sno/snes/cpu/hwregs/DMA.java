package edu.fit.cs.sno.snes.cpu.hwregs;

import edu.fit.cs.sno.snes.mem.HWRegister;
import edu.fit.cs.sno.util.Log;

public class DMA {
	
	protected static DMAChannel channels[] = new DMAChannel[8];
	public static DMAChannelRegisterGroup dmaReg[] = new DMAChannelRegisterGroup[8];
	static {
		for(int i=0;i<8;i++) {
			channels[i] = new DMAChannel();
			dmaReg[i] = new DMAChannelRegisterGroup(i);
		}
	}
	
	/**
	 * Initialize the HDMA channels if they are enabled
	 * This happens after every vblank(on scanline 0)
	 */
	// TODO: should take some cycles on the cpu
	public static void HDMAInit() {
		for (int i=0;i<8;i++) {
			channels[i].initHDMA();
		}
	}
	
	// Perform the HDMA transfer for the current scanline	
	public static void HDMARun() {
		for (int i=0; i<8; i++) {
			channels[i].doHDMA();
		}
	}
	
	private static void startDMA(int channel) {
		//Log.debug("[DMA] Starting DMA on channel: " + channel);
		//Log.debug(channels[channel].toString());
		channels[channel].start();
	}
	private static void startHDMA(int channel) {
		//Log.debug("[HDMA] Starting HDMA channel: " + channel);
		//Log.debug(channels[channel].toString());
		channels[channel].hdmaEnabled = true;
	}
	private static void stopHDMA(int channel) {
		//if (channels[channel].hdmaEnabled) Log.debug("[HDMA] Stopping HDMA channel: " + channel);
		channels[channel].hdmaEnabled = false;
	}
	
	/**
	 * Enables and disables DMA channels
	 * 0x420B
	 */
	public static HWRegister mdmaen = new HWRegister() {
		@Override
		public void onWrite(int value) {
			// Check each channel, and start the dma transfer if necessary
			for(int i=0;i<8;i++) {
				if (((value >> i) & 0x01) == 0x01) {
					startDMA(i);
				}
			}
		}
	};
	
	/**
	 * Enables and disables HDMA channels
	 * 0x420C
	 */
	public static HWRegister hdmaen = new HWRegister() {
		@Override
		public void onWrite(int value) {
			// Check each channel, and start the hdma transfer if necessary
			for(int i=0;i<8;i++) {
				if (((value >> i) & 0x01) == 0x01) {
					startHDMA(i);
				} else {
					stopHDMA(i);
				}
			}
		}
	};
	
	
}
