package edu.fit.cs.sno.snes.debug;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

public class InstructionViewer extends JPanel {
	private static final long serialVersionUID = 1L;

	JTable jt;
	JLabel instruction,arguments;
	CPUStateTableModel csm = new CPUStateTableModel();
	public InstructionViewer(CPUState s) {
		jt = new CPUStateTable(csm);
		updateState(s);
		instruction = new JLabel("Instruction:");
		arguments = new JLabel("Arguments:");

		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top,BoxLayout.PAGE_AXIS));
		top.add(instruction);
		top.add(arguments);
		
		setLayout(new BorderLayout());
		add(top, BorderLayout.NORTH);
		add(jt, BorderLayout.CENTER);
	}

	public void updateState(CPUState newState) {
		csm.updateTable(newState);
		if (newState != null) {
			instruction.setText("Instruction:  " + newState.instStr);
			arguments.setText("Arguments: " + newState.argStr);
		}
	}
}
