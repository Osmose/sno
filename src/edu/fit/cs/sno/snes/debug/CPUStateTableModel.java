package edu.fit.cs.sno.snes.debug;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class CPUStateTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -6475349977195130346L;
	CPUState s = null;

	ArrayList<Integer> updateRows = new ArrayList<Integer>();
	public void updateTable(CPUState state) {
		updateRows.clear();
		if (s==null) {
			s = state;
			fireTableDataChanged();
			return;
		}
		// Compare the two states, and only fire changed events for the changed rows
		if (s.a != state.a)	updateRows.add(0);
		if (s.b != state.b)	updateRows.add(1);
		if (s.x != state.x)	updateRows.add(2);
		if (s.y != state.y)	updateRows.add(3);
		
		if (s.pbr != state.pbr) updateRows.add(4);
		if (s.pc != state.pc)   updateRows.add(5);
		if (s.dbr != state.dbr) updateRows.add(6);
		if (s.dp != state.dp)   updateRows.add(7);
		
		if (s.sp != state.sp)           updateRows.add(8);
		if (s.opcode != state.opcode)   updateRows.add(9);
		
		if ((s.status & 0x01) != (state.status & 0x01)) updateRows.add(10); // Carry
		if ((s.status & 0x40) != (state.status & 0x40)) updateRows.add(11); // Overflow
		if ((s.status & 0x02) != (state.status & 0x02)) updateRows.add(12); // Zero
		
		s = state;
		for(int x: updateRows) {
			fireTableRowsUpdated(x, x);
		}
	}

	private Object[] rowHeaders = new Object[] { "A", "B", "X", "Y", "PBR",
			"PC", "DBR", "DP", "Stack Pointer", "OpCode", "Carry", "Overflow", "Zero"};
	
	@Override
	public int getColumnCount() { return 4; }

	@Override
	public int getRowCount() { return rowHeaders.length; }

	@Override
	public Object getValueAt(int row, int col) {
		if (col==0) {
			return rowHeaders[row];
		} else {
			if (s==null)return "";
			switch(row) {
				case  0: return formatNumber(s.a, col);
				case  1: return formatNumber(s.b, col);
				case  2: return formatNumber(s.x, col);
				case  3: return formatNumber(s.y, col);
				case  4: return formatNumberPositive(s.pbr, col);
				case  5: return formatNumberPositive(s.pc, col);
				case  6: return formatNumberPositive(s.dbr, col);
				case  7: return formatNumberPositive(s.dp, col);
				case  8: return formatNumberPositive(s.sp, col);
				case  9: return formatNumberPositive(s.opcode, col);
				case 10: return (col == 1 ? (((s.status & 0x01) != 0) ? 1 : 0) : "");
				case 11: return (col == 1 ? (((s.status & 0x40) != 0) ? 1 : 0) : "");
				case 12: return (col == 1 ? (((s.status & 0x02) != 0) ? 1 : 0) : "");
			}
		}
		return "";
	}
	
	private String formatNumberPositive(int n, int col) {
		if (col==1)	return String.format("0x%04X",n);
		if (col==2) return String.format("%6d",n);
		return "";
	}
	
	private String formatNumber(int n, int col) {
		if (col==1)	return String.format("0x%04X",n);
		if (col==2) return String.format("%6d",n);
		if (col==3) return String.format("%+6d", (short)n );
		return "";
	}
}
