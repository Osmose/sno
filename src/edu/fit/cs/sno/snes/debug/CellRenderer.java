package edu.fit.cs.sno.snes.debug;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class CellRenderer extends JLabel implements TableCellRenderer {

	public CellRenderer() {
		setOpaque(true);
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Color changed = new Color(135,206,250);
		setBackground(changed);
		setText(value.toString());
		return this;
	}

}
