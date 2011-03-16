package edu.fit.cs.sno.snes;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import edu.fit.cs.sno.util.Log;

public class CoreRunnable implements Runnable {

	private InputStream is;
	private boolean isZip;
	public boolean done;
	
	public CoreRunnable(InputStream is, boolean isZip) {
		this.is = is;
		this.isZip = isZip;
	}
	
	@Override
	public void run() {
		try {
			Core.run(is, isZip);
		} catch (Exception err) {
			StringWriter sw = new StringWriter();
			err.printStackTrace(new PrintWriter(sw));
			
			Log.err(err.toString());
			Log.err(sw.toString());
		}
		done = true;
	}

}
