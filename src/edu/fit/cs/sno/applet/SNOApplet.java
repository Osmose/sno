package edu.fit.cs.sno.applet;

import java.awt.CardLayout;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JApplet;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.CoreRunnable;
import edu.fit.cs.sno.snes.apu.APU;
import edu.fit.cs.sno.snes.apu.APUMemory;
import edu.fit.cs.sno.snes.apu.APURunnable;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.input.Input;
import edu.fit.cs.sno.snes.ppu.OAM;
import edu.fit.cs.sno.snes.ppu.PPU;
import edu.fit.cs.sno.snes.ppu.Sprites;
import edu.fit.cs.sno.snes.ppu.hwregs.CGRAM;
import edu.fit.cs.sno.util.Log;
import edu.fit.cs.sno.util.Settings;
import edu.fit.cs.sno.util.Util;

public class SNOApplet extends JApplet implements ActionListener {
	private static final long serialVersionUID = 1L;

	public static SNOApplet instance;

	private JTextArea jta = null;
	public CardLayout layout;
	
	public VideoDisplay screen;
	public Thread coreThread;
	public Thread apuThread;
	
	public SNOApplet() {
		super();

		instance = this;
	}

	@Override
	public void init() {
		try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					layout = new CardLayout();
					getContentPane().setLayout(layout);
					initGui();
					
					checkForGame();
				}

			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks if a ROM url has been specified by an applet tag or in the
	 * properties file. If so, loads and starts the rom
	 */
	private void checkForGame() {
		String loc = Settings.get(Settings.ROM_URL);
		if (loc != null) {
			System.out.println("Loading from url: " + loc);
			
			// Attempt to load from url
			InputStream is = Util.getStreamFromUrl(loc);
			boolean isZip = loc.endsWith(".zip");
			
			if (is != null) {
				runGame(is, isZip);
			}
		}
	}
	
	private void runGame(InputStream is, boolean isZip) {
		try {
			coreThread = new Thread(new CoreRunnable(is, isZip));
			apuThread = new Thread(new APURunnable());
			coreThread.start();
			apuThread.start();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	private void initGameGui() {
		// Window default size
		try {
			setSize(Settings.getInt(Settings.APPLET_WINDOW_WIDTH), Settings.getInt(Settings.APPLET_WINDOW_HEIGHT));
		} catch (NumberFormatException err) {
			setSize(256,240);
		}
		
		// Create video component
		screen = new VideoDisplay();
		getContentPane().add(screen, "Game");
		getContentPane().add(new SettingsPanel(), "Options");
		getContentPane().add(new InputSettingsPanel(), "Input");
		
		// Hook into keyboard events
		this.setFocusable(true);
		this.addKeyListener(Input.keyListener);
	}

	private void initGui() {

		JMenuBar menubar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
		JMenuItem fileOpen = new JMenuItem("Open");
		fileOpen.addActionListener(this);
		fileMenu.add(fileOpen);
		JMenu helpMenu = new JMenu("Help");
		JMenuItem helpAbout = new JMenuItem("About");
		helpAbout.addActionListener(this);
		helpMenu.add(helpAbout);

		JMenu settingsMenu = new JMenu("Settings");
		JMenuItem settingsOptions = new JMenuItem("Options");
		settingsOptions.addActionListener(this);
		settingsMenu.add(settingsOptions);
		JMenuItem settingsInput = new JMenuItem("Input");
		settingsInput.addActionListener(this);
		settingsMenu.add(settingsInput);
		
		JMenu debugMenu = new JMenu("Debug");
		JMenuItem debugDumpAPU = new JMenuItem("Dump APU RAM");
		debugDumpAPU.addActionListener(this);
		debugMenu.add(debugDumpAPU);
		JMenuItem debugResetApu = new JMenuItem("Reset APU");
		debugResetApu.addActionListener(this);
		debugMenu.add(debugResetApu);
		JMenuItem debugDumpVRAM = new JMenuItem("Dump VRAM");
		debugDumpVRAM.addActionListener(this);
		debugMenu.add(debugDumpVRAM);
		JMenuItem debugDumpWRAM = new JMenuItem("Dump WRAM");
		debugDumpWRAM.addActionListener(this);
		debugMenu.add(debugDumpWRAM);
		JMenuItem debugDumpSprites = new JMenuItem("Dump Sprites");
		debugDumpSprites.addActionListener(this);
		debugMenu.add(debugDumpSprites);
		JMenuItem debugDumpOBJs = new JMenuItem("Dump OBJs");
		debugDumpOBJs.addActionListener(this);
		debugMenu.add(debugDumpOBJs);
		JMenuItem debugBGStatus = new JMenuItem("BG Status");
		debugBGStatus.addActionListener(this);
		debugMenu.add(debugBGStatus);
		JMenuItem debugDumpPalette = new JMenuItem("Dump Palette");
		debugDumpPalette.addActionListener(this);
		debugMenu.add(debugDumpPalette);
		
		JMenuItem debugDumpBGs = new JMenuItem("Dump BGs");
		debugDumpBGs.addActionListener(this);
		debugMenu.add(debugDumpBGs);
		JMenuItem debugOutHexColors = new JMenuItem("Output Hex Colors");
		debugOutHexColors.addActionListener(this);
		debugMenu.add(debugOutHexColors);
		JMenuItem debugToggleLog = new JMenuItem("Toggle Logging");
		debugToggleLog.addActionListener(this);
		debugMenu.add(debugToggleLog);
		
		JMenuItem debugDumpTiles = new JMenuItem("Dump Tiles");
		debugDumpTiles.addActionListener(this);
		debugMenu.add(debugDumpTiles);
		JMenuItem debugDisableIRQ = new JMenuItem("Toggle IRQ");
		debugDisableIRQ.addActionListener(this);
		debugMenu.add(debugDisableIRQ);

		menubar.add(fileMenu);
		menubar.add(debugMenu);
		menubar.add(helpMenu);
		menubar.add(settingsMenu);

		setJMenuBar(menubar);

		initGameGui();
		validate();
		setVisible(true);
	}

	

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getActionCommand());
		if (e.getActionCommand().equals("Open")) {
			FileDialog fd = new FileDialog(new Frame(), "Choose a rom file",
					FileDialog.LOAD);
			fd.setVisible(true);

			String file = fd.getFile();
			String dir = fd.getDirectory();

			if (file != null) {
				if (dir != null) {
					file = dir.concat(file);
				}
				
				InputStream is = null;
				boolean isZip = file.endsWith(".zip");
				try {
					is = new FileInputStream(file);
				} catch (FileNotFoundException e1) {
					
				}
				
				if (is != null) {
					runGame(is, isZip);
				} else {
					System.out.println("File '" + file + "' not found!");
				}
			}
		}
		
		  if (e.getActionCommand().equals("About")) { 
			  JOptionPane.showMessageDialog(getContentPane(), "Created By: Keith Johnson, Mike Kelly, and Eric Wells\r\nhttps://cs.fit.edu/proxy/proj/sno/","About us!",JOptionPane.INFORMATION_MESSAGE);
		 }
		if (e.getActionCommand().equals("Options")) {
			Core.pause = true;
			layout.show(getContentPane(), "Options");
		} else if (e.getActionCommand().equals("Input")) {
			Core.pause = true;
			layout.show(getContentPane(), "Input");
		}
		
		
		if (e.getActionCommand().equals("Dump APU RAM")) {
			APUMemory.dump();
		} else if (e.getActionCommand().equals("Reset APU")) {
			APU.debugReset();
		} else if (e.getActionCommand().equals("Dump VRAM")) {
			PPU.dumpVRAM();
		} else if (e.getActionCommand().equals("Dump WRAM")) {
			Core.mem.dumpWRAM();
		} else if (e.getActionCommand().equals("Dump Sprites")) {
			Sprites.dumpSpriteData();
		} else if (e.getActionCommand().equals("Dump OBJs")) {
			Sprites.dumpOBJ();
		}  else if (e.getActionCommand().equals("BG Status")) {
			System.out.println(PPU.bg[0].toString());
			System.out.println(PPU.bg[1].toString());
			System.out.println(PPU.bg[2].toString());
			System.out.println(PPU.bg[3].toString());
		}  else if (e.getActionCommand().equals("Dump Palette")) {
			CGRAM.dumpCGRAM();
			CGRAM.testColors();
		} else if (e.getActionCommand().equals("Dump BGs")) {
			PPU.bg[0].dumpBGGraphics();
			PPU.bg[1].dumpBGGraphics();
			PPU.bg[2].dumpBGGraphics();
			PPU.bg[3].dumpBGGraphics();
		} else if (e.getActionCommand().equals("Output Hex Colors")) {
			CGRAM.outputHexColors();
		} else if (e.getActionCommand().equals("Toggle Logging")) {
			Log.setLogEnabled(!Log.enabled);
		} else if (e.getActionCommand().equals("Dump Tiles")) {
			OAM.dumpTiles();
		} else if (e.getActionCommand().equals("Toggle IRQ")) {
			CPU.userDisableIRQ = !CPU.userDisableIRQ;
		}

	}

}
