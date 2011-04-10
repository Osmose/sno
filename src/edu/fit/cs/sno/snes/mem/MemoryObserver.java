package edu.fit.cs.sno.snes.mem;

import java.util.HashSet;

public abstract class MemoryObserver {
	public static HashSet<MemoryObserver> observers = new HashSet<MemoryObserver>();
	public static boolean addObserver(MemoryObserver o) {
		return observers.add(o);
	}
	public static boolean delObserver(MemoryObserver o) {
		return observers.remove(o);
	}
	public static void notifyObservers(int address) {
		for(MemoryObserver o: observers) {
			int[] range = o.getRange();
			for(int i=0;i<range.length; i+=2) {
				int start = range[i];
				int end = range[i+1];
				if (address >= start && address < end) {
					o.onInvalidate(address);
					break; // Move on to the next observer
				}
			}
		}
	}

	/**
	 * Declares the range of memory addresses we want to watch and be notified when they change
	 * @return list of start/end values for memory ranges
	 */
	public abstract int[] getRange();
	
	/**
	 * Called when the memory is changed(passed a list of addresses that have been modified)
	 * @param addr - A list of addresses that changed
	 */
	public abstract void onInvalidate(int addr);
	
}
