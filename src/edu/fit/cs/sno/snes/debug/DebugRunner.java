package edu.fit.cs.sno.snes.debug;

import edu.fit.cs.sno.snes.Core;

public class DebugRunner implements Runnable {
	
	private static final int UPDATECOUNT = 10;
	volatile boolean infloop = true;
	volatile boolean paused = true;
	volatile boolean done = false;
	volatile int count = 0;
	volatile int updatecount = UPDATECOUNT;
	private TraceViewer tv;
	
	public DebugRunner(String file, TraceViewer traceViewer) {
		//Core.init(file);
		paused = true;
		infloop = true;
		done = false;
		tv = traceViewer;
		updatecount = UPDATECOUNT;
	}
	
	@Override
	public synchronized void run() {
		while (!done) {
			try {
				while (!paused) {
					if (count > 0) {
						Core.cycle(1);
						count--;
						updatecount--;
					} else if (infloop) {
						Core.cycle(1);
						updatecount--;
					} else {
						tv.updateDisplay(); // update when we pause/stop running code
						paused = true;
					}
					if (updatecount<=0) {
						tv.updateDisplay();
						updatecount=UPDATECOUNT;
					}
					Thread.yield();
				}
				wait();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void donotify() {
		notify();
	}
	
	public void stop() {
		done = true;
	}
	
	public void togglePaused() {
		paused = !paused;
	}

	public void cycle(int i) {
		count = i;
		paused = false;
		infloop = false;
		donotify();
	}
	
	public void cycle() {
		paused = false;
		infloop = true;
		donotify();
	}

	public void pause() {
		paused = true;
	}

}
