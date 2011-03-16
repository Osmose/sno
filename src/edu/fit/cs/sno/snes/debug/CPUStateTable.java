package edu.fit.cs.sno.snes.debug;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public class CPUStateTable extends JTable {

	CellRenderer cr = new CellRenderer();
	public CPUStateTable(TableModel csm) {
		super(csm);
	}
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		if (((CPUStateTableModel)getModel()).updateRows.contains(row)) {
			return cr;
		}
		return super.getCellRenderer(row, column);
	}

}
