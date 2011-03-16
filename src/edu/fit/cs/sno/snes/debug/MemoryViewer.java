package edu.fit.cs.sno.snes.debug;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.mem.Memory;

@SuppressWarnings("serial")
public class MemoryViewer extends JPanel{
	Memory mem = null;
	int zoomlevel = 3;
	int pixelSize = 3;
	int cols = 100;
	int rows = (int)Math.ceil((8*1024)/cols);

	public MemoryViewer(Memory m) {
		mem = m;
	}
	public void updateMem(Memory m) {
		mem = m;
	}

	@Override
	protected void paintComponent(Graphics g) {
		for(int i=0; i<rows; i++) {
			for (int j=0; j<cols; j++) {
				if (mem != null && i*cols+j < 8*1024)
					g.setColor(new Color(mem.get(Size.BYTE, 0, i*cols+j)));
				else
					g.setColor(Color.WHITE);
				g.fillRect(j*pixelSize, i*pixelSize, pixelSize, pixelSize);
			}
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(cols*pixelSize, rows*pixelSize);
	}
}
