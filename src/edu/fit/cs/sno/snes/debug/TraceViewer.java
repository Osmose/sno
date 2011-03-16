package edu.fit.cs.sno.snes.debug;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.fit.cs.sno.snes.Core;

public class TraceViewer extends JFrame implements ListSelectionListener, ActionListener {

	private static final long serialVersionUID = 1L;
	private JList jl;
	private InstructionViewer iv;
	private MemoryViewer mv;
	private DebugRunner dr;
	private Thread thread;
	
	public static void main(String args[]) {
		new TraceViewer();
	}
	
	public TraceViewer() {
		setTitle("CPU Trace");
		
		JPanel center = new JPanel();
		center.setLayout(new FlowLayout(FlowLayout.LEFT));
		getContentPane().add(center, BorderLayout.CENTER);
		
		
		iv = new InstructionViewer(null);
		iv.setPreferredSize(new Dimension(500,250));
		center.add(iv);
		
		mv = new MemoryViewer(Core.mem);
		JScrollPane mvsp = new JScrollPane(mv);
		center.add(mvsp);
		
		jl = new JList(CPUState.getArray());
		jl.setFont(new Font("Courier New", Font.PLAIN, 11));
		jl.addListSelectionListener(this);
		jl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane instructions = new JScrollPane(jl);
		instructions.setPreferredSize(new Dimension(250,200));
		getContentPane().add(instructions, BorderLayout.SOUTH);
		
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		getContentPane().add(initToolBar(), BorderLayout.PAGE_START);
		
		setJMenuBar(initMenu());
		pack();
	}
	
	public JMenuItem createItem(String text) {
		JMenuItem jmi = new JMenuItem(text);
		jmi.addActionListener(this);
		return jmi;
	}
	
	public JMenuBar initMenu() {
		JMenu jmFile = new JMenu("File");
		jmFile.add(createItem("Load Rom"));
		jmFile.add(createItem("Exit"));
		
		JMenu jmDebug = new JMenu("Debug");
		jmDebug.add(createItem("Run"));
		jmDebug.add(createItem("Step"));
		jmDebug.add(createItem("Step(10)"));
		jmDebug.add(createItem("Continue"));
		jmDebug.add(createItem("Pause"));
		
		JMenuBar jmb = new JMenuBar();
		jmb.add(jmFile);
		jmb.add(jmDebug);
		return jmb;
	}
	
	public JToolBar initToolBar() {
		JToolBar jtb = new JToolBar("Debug");
		jtb.add(makeButton("Run"));
		jtb.addSeparator();
		jtb.add(makeButton("Step"));
		jtb.add(makeButton("Step(10)"));
		jtb.add(makeButton("Continue"));
		jtb.add(makeButton("Pause"));
		return jtb;
	}
	public JButton makeButton(String name) {
		JButton jb = new JButton();
		jb.setActionCommand(name);
		jb.addActionListener(this);
		
		jb.setText(name);
		jb.setToolTipText("tooltips!");
		return jb;
	}



	private void updateTraceInfo(int index) {
		CPUState s = CPUState.getState(index);
		iv.updateState(s);
		iv.repaint();
		System.out.println(s);
	}
	
	/**
	 * Event handler is fired when the selection in the list changes
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting())
			return;
		if (jl.getSelectedIndex()!=-1)
			updateTraceInfo(jl.getSelectedIndex());
	}

	/**
	 * Used for handling input from the menu
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Load Rom")) {
			FileDialog fd = new FileDialog(new Frame(), "Choose a rom file", FileDialog.LOAD);
			fd.setVisible(true);

			String file = fd.getFile();
			String dir = fd.getDirectory();

			// Kill the old thread first before starting a new one
			if (thread != null) {
				dr.stop();
				try {
					thread.join();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			if (file != null) {
				if (dir != null)
					file = dir.concat(file);
				dr = new DebugRunner(file, this);
			} else {
				dr = new DebugRunner("roms/spaceinvaders.smc", this);
			}
			thread = new Thread(dr);
			thread.start();
			mv.updateMem(Core.mem);
			mv.repaint();
		} else if (e.getActionCommand().equals("Step(10)")) {
			try {
				dr.cycle(10);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else if (e.getActionCommand().equals("Step")) {
			try {
				dr.cycle(1);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else if (e.getActionCommand().equals("Continue") || e.getActionCommand().equals("Run")) {
			dr.cycle();
		} else if (e.getActionCommand().equals("Pause")) {
			dr.pause();
		} else if (e.getActionCommand().equals("Exit")) {
			dispose();
		}
	}
	
	public void updateDisplay() {
		jl.setListData(CPUState.getArray());
		jl.setSelectedIndex(jl.getModel().getSize() -1);
		jl.ensureIndexIsVisible(jl.getModel().getSize() -1);
		jl.repaint();
		mv.repaint();
	}
}
