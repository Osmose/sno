package edu.fit.cs.sno.snes.apu;

public class Timer {
	private boolean enabled;
	
	private int cyclesPerTick;
	private int cyclesPassed = 0;
	
	private int target=256;
	private int lowCounter;		// 8 bits
	private int highCounter;	// 4 bits
	
	public Timer(int cycles) {
		enabled = false;
		cyclesPerTick = cycles;
	}
	
	public void passCycles(int cycles) {
		cyclesPassed += cycles;
			
		if (cyclesPassed >= cyclesPerTick) {
			if (enabled) {
				incCounter(cyclesPassed / cyclesPerTick);
			}
			
			cyclesPassed %= cyclesPerTick;
		}
	}
	
	private void incCounter(int val) {
		lowCounter += val;

		if (lowCounter >= target) {
			// Some silly math in the rare case more than one timer tick has occurred
			highCounter = ((int)(highCounter + Math.floor(lowCounter / target))) & 0x0F;
			lowCounter %= target;
		}
		
		lowCounter &= 0xFF;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getTarget() {
		return target;
	}
	
	public void setTarget(int target) {
		this.target = (target == 0 ? 256 : target);	// 0 = 256 for target
	}

	public int getCounter() {
		return highCounter;
	}

	public void setCounter(int counter) {
		this.highCounter = counter;
	}
}
