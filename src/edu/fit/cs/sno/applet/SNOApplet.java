package edu.fit.cs.sno.applet;

import java.awt.CardLayout;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

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
	private CardLayout layout;
	
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
					initComponents();
					setOptions();
					
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
		
		// Hook into keyboard events
		this.setFocusable(true);
		this.addKeyListener(Input.keyListener);
	}

	private void setOptions() {

		if (Settings.get(Settings.AUTO_FRAME_SKIP) != null) {
			if (Settings.get(Settings.AUTO_FRAME_SKIP).equalsIgnoreCase("true"))
				optionAutoFrameskip.setSelected(true);
			else if (Settings.get(Settings.AUTO_FRAME_SKIP).equalsIgnoreCase(
					"false"))
				optionAutoFrameskip.setSelected(false);
		}

		if (Settings.get(Settings.APPLET_FULLSCREEN) != null)
			if (Settings.get(Settings.APPLET_FULLSCREEN).equalsIgnoreCase(
					"true"))
				optionFullScreen.setSelected(true);
			else if (Settings.get(Settings.APPLET_FULLSCREEN).equalsIgnoreCase(
					"false"))
				optionFullScreen.setSelected(false);

		if (Settings.get(Settings.CPU_DEBUG_TRACE) != null) {
			if (Settings.get(Settings.CPU_DEBUG_TRACE).equalsIgnoreCase("true"))
				optionsDebugTrace.setSelected(true);
			else if (Settings.get(Settings.CPU_DEBUG_TRACE).equalsIgnoreCase(
					"false"))
				optionsDebugTrace.setSelected(false);
		}

		if (Settings.get(Settings.CPU_LIMIT_SPEED) != null) {
			if (Settings.get(Settings.CPU_LIMIT_SPEED).equalsIgnoreCase("true")) {
				optionLimitSpeed.setSelected(true);
			} else if (Settings.get(Settings.CPU_LIMIT_SPEED).equalsIgnoreCase(
					"false")) {
				optionLimitSpeed.setSelected(false);
			}
		}

		if (Settings.get(Settings.DEBUG_OUT) != null) {
			if (Settings.get(Settings.DEBUG_OUT).equalsIgnoreCase("true"))
				optionDebugOut.setSelected(true);
			else if (Settings.get(Settings.DEBUG_OUT).equalsIgnoreCase("false"))
				optionDebugOut.setSelected(false);
		}

		if (Settings.get(Settings.MUTE_SOUND) != null) {
			if (Settings.get(Settings.MUTE_SOUND).equalsIgnoreCase("true"))
				optionMuteSound.setSelected(true);
			else if (Settings.get(Settings.MUTE_SOUND)
					.equalsIgnoreCase("false"))
				optionMuteSound.setSelected(false);
		}

		if (Settings.get(Settings.SOUND_EMULATION) != null) {
			if (Settings.get(Settings.SOUND_EMULATION).equalsIgnoreCase("true"))
				optionSoundEmulation.setSelected(true);
			else if (Settings.get(Settings.SOUND_EMULATION).equalsIgnoreCase(
					"false"))
				optionSoundEmulation.setSelected(false);
		}

	}

	// Made using the netbeans design editor
	private void initComponents() {

		settingsPanel = new javax.swing.JTabbedPane();
		videoSettingsPanel = new javax.swing.JPanel();
		frameSkipOptionsPane = new javax.swing.JPanel();
		optionAutoFrameskip = new javax.swing.JCheckBox();
		optionFramesToSkip = new javax.swing.JTextField();
		framesToSkipLable = new javax.swing.JLabel();
		windowOptionsPane = new javax.swing.JPanel();
		optionFullScreen = new javax.swing.JCheckBox();
		acceptVideoOptions = new javax.swing.JButton();
		audioSettingsPane = new javax.swing.JPanel();
		audioOptionsPane = new javax.swing.JPanel();
		optionSoundEmulation = new javax.swing.JCheckBox();
		optionMuteSound = new javax.swing.JCheckBox();
		acceptAudioOptions = new javax.swing.JButton();
		emulatorSettingsPane = new javax.swing.JPanel();
		emulatorOptionsPane = new javax.swing.JPanel();
		optionEAnOption = new javax.swing.JCheckBox();
		optionEAnotherOption = new javax.swing.JCheckBox();
		acceptEmulatorOptions = new javax.swing.JButton();
		controllerSettingsPane = new javax.swing.JPanel();
		acceptControllerOptions = new javax.swing.JButton();
		controllerTableScrollPane = new javax.swing.JScrollPane();
		controllerConfigTable = new javax.swing.JTable();

		optionsDebugTrace = new javax.swing.JCheckBox();
		optionLimitSpeed = new javax.swing.JCheckBox();
		optionDebugOut = new javax.swing.JCheckBox();

		frameSkipOptionsPane.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Frameskip"));

		optionAutoFrameskip.setText("Enable Automatic Frameskip");
		optionAutoFrameskip.addActionListener(this);
		optionFramesToSkip.setText("0");
		optionFramesToSkip.addActionListener(this);

		framesToSkipLable.setText("Frames to Skip");

		javax.swing.GroupLayout frameSkipOptionsPaneLayout = new javax.swing.GroupLayout(
				frameSkipOptionsPane);
		frameSkipOptionsPane.setLayout(frameSkipOptionsPaneLayout);
		frameSkipOptionsPaneLayout
				.setHorizontalGroup(frameSkipOptionsPaneLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								frameSkipOptionsPaneLayout
										.createSequentialGroup()
										.addGroup(
												frameSkipOptionsPaneLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																optionAutoFrameskip)
														.addGroup(
																frameSkipOptionsPaneLayout
																		.createSequentialGroup()
																		.addComponent(
																				optionFramesToSkip,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				26,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				framesToSkipLable,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				129,
																				Short.MAX_VALUE)))
										.addContainerGap()));
		frameSkipOptionsPaneLayout
				.setVerticalGroup(frameSkipOptionsPaneLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								frameSkipOptionsPaneLayout
										.createSequentialGroup()
										.addComponent(optionAutoFrameskip)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												frameSkipOptionsPaneLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																optionFramesToSkip,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																framesToSkipLable))
										.addContainerGap(82, Short.MAX_VALUE)));

		windowOptionsPane.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Window Settings"));

		optionFullScreen.setText("Full Screen");

		javax.swing.GroupLayout windowOptionsPaneLayout = new javax.swing.GroupLayout(
				windowOptionsPane);
		windowOptionsPane.setLayout(windowOptionsPaneLayout);
		windowOptionsPaneLayout.setHorizontalGroup(windowOptionsPaneLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						windowOptionsPaneLayout.createSequentialGroup()
								.addContainerGap()
								.addComponent(optionFullScreen)
								.addContainerGap(64, Short.MAX_VALUE)));
		windowOptionsPaneLayout.setVerticalGroup(windowOptionsPaneLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						windowOptionsPaneLayout.createSequentialGroup()
								.addComponent(optionFullScreen)
								.addContainerGap(104, Short.MAX_VALUE)));

		acceptVideoOptions.setText("Accept");
		acceptVideoOptions.addActionListener(this);

		javax.swing.GroupLayout videoSettingsPanelLayout = new javax.swing.GroupLayout(
				videoSettingsPanel);
		videoSettingsPanel.setLayout(videoSettingsPanelLayout);
		videoSettingsPanelLayout
				.setHorizontalGroup(videoSettingsPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								videoSettingsPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												videoSettingsPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																javax.swing.GroupLayout.Alignment.TRAILING,
																videoSettingsPanelLayout
																		.createSequentialGroup()
																		.addComponent(
																				frameSkipOptionsPane,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				177,
																				Short.MAX_VALUE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																		.addComponent(
																				windowOptionsPane,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addGap(110,
																				110,
																				110))
														.addGroup(
																javax.swing.GroupLayout.Alignment.TRAILING,
																videoSettingsPanelLayout
																		.createSequentialGroup()
																		.addComponent(
																				acceptVideoOptions)
																		.addContainerGap()))));
		videoSettingsPanelLayout
				.setVerticalGroup(videoSettingsPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								videoSettingsPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												videoSettingsPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING,
																false)
														.addComponent(
																frameSkipOptionsPane,
																javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																windowOptionsPane,
																javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												98, Short.MAX_VALUE)
										.addComponent(acceptVideoOptions)
										.addContainerGap()));

		settingsPanel.addTab("Video", videoSettingsPanel);

		audioOptionsPane.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Sound Settings"));

		optionSoundEmulation.setText("Enable Sound Emulation");
		optionSoundEmulation.addActionListener(this);

		optionMuteSound.setText("Mute Sound");
		optionMuteSound.addActionListener(this);

		javax.swing.GroupLayout audioOptionsPaneLayout = new javax.swing.GroupLayout(
				audioOptionsPane);
		audioOptionsPane.setLayout(audioOptionsPaneLayout);
		audioOptionsPaneLayout
				.setHorizontalGroup(audioOptionsPaneLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								audioOptionsPaneLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												audioOptionsPaneLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																optionSoundEmulation)
														.addComponent(
																optionMuteSound))
										.addContainerGap(39, Short.MAX_VALUE)));
		audioOptionsPaneLayout
				.setVerticalGroup(audioOptionsPaneLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								audioOptionsPaneLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(optionSoundEmulation)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(optionMuteSound)
										.addContainerGap(47, Short.MAX_VALUE)));

		acceptAudioOptions.setText("Accept");
		acceptAudioOptions.addActionListener(this);

		javax.swing.GroupLayout audioSettingsPaneLayout = new javax.swing.GroupLayout(
				audioSettingsPane);
		audioSettingsPane.setLayout(audioSettingsPaneLayout);
		audioSettingsPaneLayout.setHorizontalGroup(audioSettingsPaneLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						audioSettingsPaneLayout
								.createSequentialGroup()
								.addContainerGap()
								.addComponent(audioOptionsPane,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addContainerGap(260, Short.MAX_VALUE))
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						audioSettingsPaneLayout.createSequentialGroup()
								.addContainerGap(391, Short.MAX_VALUE)
								.addComponent(acceptAudioOptions)
								.addContainerGap()));
		audioSettingsPaneLayout
				.setVerticalGroup(audioSettingsPaneLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								audioSettingsPaneLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(
												audioOptionsPane,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												125, Short.MAX_VALUE)
										.addComponent(acceptAudioOptions)
										.addContainerGap()));

		settingsPanel.addTab("Audio", audioSettingsPane);

		emulatorOptionsPane.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Emulator Settings"));

		optionEAnOption.setText("An Option");
		optionEAnOption.addActionListener(this);

		optionsDebugTrace.setText("Debug Tracer");
		optionsDebugTrace.addActionListener(this);

		optionLimitSpeed.setText("Limit CPU Speed");
		optionLimitSpeed.addActionListener(this);

		optionEAnotherOption.setText("Another Option");

		javax.swing.GroupLayout emulatorOptionsPaneLayout = new javax.swing.GroupLayout(
				emulatorOptionsPane);
		emulatorOptionsPane.setLayout(emulatorOptionsPaneLayout);
		emulatorOptionsPaneLayout
				.setHorizontalGroup(emulatorOptionsPaneLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								emulatorOptionsPaneLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												emulatorOptionsPaneLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																optionsDebugTrace)
														.addComponent(
																optionLimitSpeed))
										.addContainerGap(39, Short.MAX_VALUE)));
		emulatorOptionsPaneLayout
				.setVerticalGroup(emulatorOptionsPaneLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								emulatorOptionsPaneLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(optionsDebugTrace)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(optionLimitSpeed)
										.addContainerGap(47, Short.MAX_VALUE)));

		acceptEmulatorOptions.setText("Accept");
		acceptEmulatorOptions.addActionListener(this);

		javax.swing.GroupLayout emulatorSettingsPaneLayout = new javax.swing.GroupLayout(
				emulatorSettingsPane);
		emulatorSettingsPane.setLayout(emulatorSettingsPaneLayout);
		emulatorSettingsPaneLayout
				.setHorizontalGroup(emulatorSettingsPaneLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								emulatorSettingsPaneLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(
												emulatorOptionsPane,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addContainerGap(284, Short.MAX_VALUE))
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								emulatorSettingsPaneLayout
										.createSequentialGroup()
										.addContainerGap(391, Short.MAX_VALUE)
										.addComponent(acceptEmulatorOptions)
										.addContainerGap()));
		emulatorSettingsPaneLayout
				.setVerticalGroup(emulatorSettingsPaneLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								emulatorSettingsPaneLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(
												emulatorOptionsPane,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												120, Short.MAX_VALUE)
										.addComponent(acceptEmulatorOptions)
										.addContainerGap()));

		settingsPanel.addTab("Emulator", emulatorSettingsPane);

		acceptControllerOptions.setText("Accept");
		acceptControllerOptions.addActionListener(this);

		controllerConfigTable.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][] { { "Up", null }, { "Left", null },
						{ "Down", null }, { "Right", null }, { "A", null },
						{ "B", null }, { "X", null }, { "Y", null },
						{ "L", null }, { "R", null }, { "Start", null },
						{ "Select", null } }, new String[] { "\"Action\"",
						"\"Key Bind\"" }) {
			Class[] types = new Class[] { java.lang.String.class,
					java.lang.String.class };
			boolean[] canEdit = new boolean[] { false, true };

			public Class getColumnClass(int columnIndex) {
				return types[columnIndex];
			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
			}
		});
		controllerTableScrollPane.setViewportView(controllerConfigTable);
		controllerConfigTable.getColumnModel().getColumn(0).setResizable(false);
		controllerConfigTable.getColumnModel().getColumn(1).setResizable(false);

		javax.swing.GroupLayout controllerSettingsPaneLayout = new javax.swing.GroupLayout(
				controllerSettingsPane);
		controllerSettingsPane.setLayout(controllerSettingsPaneLayout);
		controllerSettingsPaneLayout
				.setHorizontalGroup(controllerSettingsPaneLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								controllerSettingsPaneLayout
										.createSequentialGroup()
										.addContainerGap(391, Short.MAX_VALUE)
										.addComponent(acceptControllerOptions)
										.addContainerGap())
						.addGroup(
								controllerSettingsPaneLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(
												controllerTableScrollPane,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												329,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addContainerGap(127, Short.MAX_VALUE)));
		controllerSettingsPaneLayout
				.setVerticalGroup(controllerSettingsPaneLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								controllerSettingsPaneLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(
												controllerTableScrollPane,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												219,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												33, Short.MAX_VALUE)
										.addComponent(acceptControllerOptions)
										.addContainerGap()));

		settingsPanel.addTab("Controller", controllerSettingsPane);

		getContentPane().add(settingsPanel, "Settings");
	}// </editor-fold>

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
		JMenuItem settingsVideo = new JMenuItem("Video");
		settingsVideo.addActionListener(this);
		settingsMenu.add(settingsVideo);
		JMenuItem settingsAudio = new JMenuItem("Audio");
		settingsAudio.addActionListener(this);
		settingsMenu.add(settingsAudio);
		JMenuItem settingsEmulator = new JMenuItem("Emulator");
		settingsEmulator.addActionListener(this);
		settingsMenu.add(settingsEmulator);
		JMenuItem settingsController = new JMenuItem("Controller");
		settingsController.addActionListener(this);
		settingsMenu.add(settingsController);
		
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
		if (e.getActionCommand().equals("Video")) {
			layout.show(getContentPane(), "Settings");
			settingsPanel.setSelectedIndex(0);
		} else if (e.getActionCommand().equals("Audio")) {
			layout.show(getContentPane(), "Settings");
			settingsPanel.setSelectedIndex(1);
		} else if (e.getActionCommand().equals("Emulator")) {
			layout.show(getContentPane(), "Settings");
			settingsPanel.setSelectedIndex(2);
		} else if (e.getActionCommand().equals("Controller")) {
			layout.show(getContentPane(), "Settings");
			settingsPanel.setSelectedIndex(3);
		}
		if (e.getActionCommand().equals("Accept")) {
			layout.show(getContentPane(), "Game");
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
		}

	}

	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();
		String selected = "false";
		if (e.getStateChange() == ItemEvent.SELECTED) {
			selected = "true";
		}
		if (source == optionAutoFrameskip) {
			Settings.set(Settings.AUTO_FRAME_SKIP, selected);
		} else if (source == optionFullScreen) {
			Settings.set(Settings.APPLET_FULLSCREEN, selected);
		} else if (source == optionSoundEmulation) {
			Settings.set(Settings.SOUND_EMULATION, selected);
		} else if (source == optionMuteSound) {
			Settings.set(Settings.MUTE_SOUND, selected);
		} else if (source == optionLimitSpeed) {
			Settings.set(Settings.CPU_LIMIT_SPEED, selected);
			System.out.println("setting cpulimitspeed to" + selected);
		} else if (source == optionsDebugTrace) {
			Settings.set(Settings.CPU_DEBUG_TRACE, selected);
		}
	}

	// Variables declaration - do not modify
	private javax.swing.JButton acceptAudioOptions;
	private javax.swing.JButton acceptControllerOptions;
	private javax.swing.JButton acceptEmulatorOptions;
	private javax.swing.JButton acceptVideoOptions;
	private javax.swing.JPanel audioOptionsPane;
	private javax.swing.JPanel audioSettingsPane;
	private javax.swing.JTable controllerConfigTable;
	private javax.swing.JPanel controllerSettingsPane;
	private javax.swing.JScrollPane controllerTableScrollPane;
	private javax.swing.JPanel emulatorOptionsPane;
	private javax.swing.JPanel emulatorSettingsPane;
	private javax.swing.JPanel frameSkipOptionsPane;
	private javax.swing.JLabel framesToSkipLable;
	private javax.swing.JCheckBox optionAutoFrameskip;
	private javax.swing.JCheckBox optionsDebugTrace;
	private javax.swing.JCheckBox optionLimitSpeed;
	private javax.swing.JCheckBox optionDebugOut;
	private javax.swing.JCheckBox optionEAnOption;
	private javax.swing.JCheckBox optionEAnotherOption;
	private javax.swing.JTextField optionFramesToSkip;
	private javax.swing.JCheckBox optionFullScreen;
	private javax.swing.JCheckBox optionMuteSound;
	private javax.swing.JCheckBox optionSoundEmulation;
	private javax.swing.JTabbedPane settingsPanel;
	private javax.swing.JPanel videoSettingsPanel;
	private javax.swing.JPanel windowOptionsPane;
	// End of variables declaration

}
