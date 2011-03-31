package edu.fit.cs.sno.applet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import edu.fit.cs.sno.snes.ppu.PPU;

public class VideoDisplay extends JComponent {

	int k = 0;
	
	private BufferedImage backbuffer = new BufferedImage(256, 240, BufferedImage.TYPE_INT_ARGB);
	
	public VideoDisplay(int width, int height) {
		setBackground(Color.BLACK);
		
		setDoubleBuffered(true);
		setSize(width, height);
		setPreferredSize(new Dimension(width, height));
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.drawImage(backbuffer,0,0,this.getWidth(),this.getHeight(),0,0,256,240,this);
		//g.drawImage(backbuffer,0,0,this.getWidth(),this.getHeight(),0,0,512,480,this);
	}
	
	public void drawFrame() {
		backbuffer.getGraphics().drawImage(PPU.screenBuffer, 0, 0, null);
		repaint();
	}

}
