package edu.fit.cs.sno.snes.cpu.hwregs;

import edu.fit.cs.sno.snes.mem.HWRegister;

public class DMAChannelRegisterGroup {
	DMAChannel channels[] = DMA.channels;
	private int channel;
	public DMAChannelRegisterGroup(int channel) {
		this.channel = channel;
	}
	/**
	 * DMA Channel x
	 * 0x43x0 - 0x43x9
	 */
	public HWRegister dmapx = new HWRegister() { /** 0x4300 - DMA Control*/
		{val = 0xFF;}
		@Override
		public void onWrite(int value) {
			super.onWrite(value);
			channels[channel].direction = ((value & 0x80) == 0x80); 
			channels[channel].addressMode = ((value & 0x40) == 0x40);
			channels[channel].addressIncrement = ((value & 0x10) == 0x10);
			channels[channel].fixedTransfer = ((value & 0x08) == 0x08);
			channels[channel].transferMode = (value & 0x07);
		}
	};
	public HWRegister bbadx = new HWRegister() { /** 0x43x1 - DMA Destination Register*/
		{val = 0xFF;}
		@Override
		public void onWrite(int value) {
			super.onWrite(value);
			channels[channel].dstRegister = 0x2100 + (value&0xFF);
		}
	};
	public HWRegister a1txl = new HWRegister() { /** 0x43x2 - DMA Source Address(low)*/
		{val = 0xFF;}
		@Override
		public void onWrite(int value) {
			channels[channel].srcAddress = (channels[channel].srcAddress & 0xFF00) | (value&0xFF);
		}
		@Override
		public void onRead() {
			val = (channels[channel].srcAddress) & 0xFF;
		}
	};
	public HWRegister a1txh = new HWRegister() { /** 0x43x3 - DMA Source Address(high)*/
		{val = 0xFF;}
		@Override
		public void onWrite(int value) {
			channels[channel].srcAddress = ((value<<8) & 0xFF00) | (channels[channel].srcAddress & 0xFF);
		}
		@Override
		public void onRead() {
			val = (channels[channel].srcAddress >> 8) & 0xFF;
		}
	};
	public HWRegister a1bx = new HWRegister() {  /** 0x43x4 - DMA Source Address(bank)*/
		{val = 0xFF;}
		@Override
		public void onWrite(int value) {
			super.onWrite(value);
			channels[channel].srcBank = value & 0xFF;
		}
	};
	public HWRegister dasxl = new HWRegister() { /** 0x43x5 - DMA Size(low)*/
		{val = 0xFF;}
		@Override
		public void onWrite(int value) {
			channels[channel].transferSize = (channels[channel].transferSize & 0xFF00) | (value&0xFF);
		}
		@Override
		public void onRead() {
			val = channels[channel].transferSize & 0xFF;
		}
	};
	public HWRegister dasxh = new HWRegister() { /** 0x43x6 - DMA Size(high)*/
		{val = 0xFF;}
		@Override
		public void onWrite(int value) {
			channels[channel].transferSize = ((value<<8) & 0xFF00) | (channels[channel].transferSize & 0xFF);
		}
		@Override
		public void onRead() {
			val = (channels[channel].transferSize>>8) & 0xFF;
		}
	};
	public HWRegister dasbh = new HWRegister() { /** 0x43x7 - HDMA Indirect Address bank */
		{val = 0xFF;}
		@Override
		public void onWrite(int value) {
			super.onWrite(value);
			channels[channel].indirectBank = (value & 0xFF);
		}
	};
	
	public HWRegister a2axl = new HWRegister() { /** 0x43x8 - HDMA Table Address(low)*/
		{val = 0xFF;}
		@Override
		public void onWrite(int value) {
			channels[channel].tableAddr = (channels[channel].tableAddr & 0xFF00) | (value&0xFF);
		}
		@Override
		public void onRead() {
			val = (channels[channel].tableAddr) & 0xFF;
		}
	};
	public HWRegister a2axh = new HWRegister() { /** 0x43x9 - HDMA Table Address(high)*/
		{val = 0xFF;}
		@Override
		public void onWrite(int value) {
			channels[channel].tableAddr = ((value<<8) & 0xFF00) | (channels[channel].tableAddr & 0xFF);
		}
		@Override
		public void onRead() {
			val = (channels[channel].tableAddr >> 8) & 0xFF;
		}
	};
	
	public HWRegister nltrx = new HWRegister() { /** 0x43xA - HDMA Line Counter */
		{val = 0xFF;}
		@Override
		public void onWrite(int value) {
			super.onWrite(value);
			channels[channel].rlc = (value & 0xFF);
		}
	};
}
