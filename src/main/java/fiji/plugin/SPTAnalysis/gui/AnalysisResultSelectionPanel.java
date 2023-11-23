package fiji.plugin.SPTAnalysis.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import fiji.plugin.SPTAnalysis.DataController;
import fiji.plugin.SPTAnalysis.GUIController;
import fiji.plugin.SPTAnalysis.graphConstruction.GraphConstructionParameters;
import fiji.plugin.SPTAnalysis.struct.AnalysisParameters;
import fiji.plugin.SPTAnalysis.wellDetection.WellDetectionParameters;

public class AnalysisResultSelectionPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	public class CbRenderer extends DefaultTableCellRenderer
	{
		private static final long serialVersionUID = 1L;
		private JCheckBox cb;

		public CbRenderer()
		{
			this.cb = null;
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column)
		{
			if (this.cb == null)
			{
				this.cb = new JCheckBox();
				this.cb.setSelected(false);
			}

			if (value == null)
				return this.cb;

			if (value.equals("false"))
				this.cb.setSelected(false);
			else
				this.cb.setSelected(true);
			return this.cb;
		}
	}

	public class ButtonRenderer extends DefaultTableCellRenderer
	{
		private static final long serialVersionUID = 1L;
		private JButton but;

		public ButtonRenderer()
		{
			this.but = null;
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column)
		{
			if (this.but == null)
				this.but = new JButton("Show");

			return this.but;
		}
	}

	private final DataController dcntrl;
	@SuppressWarnings("unused")
	private final GUIController pcntrl;

	private BorderLayout layout;
	private JTable tab;
	private ArrayList<AnalysisParameters> params;

	private String[] tableRowFromParams(AnalysisParameters ps)
	{
		String[] data = new String[4];
		data[0] = ps.expName();
		if (ps.analysisType() == AnalysisParameters.Type.WELL)
			data[1] = String.format("%d",
					this.dcntrl.numberOfWells((WellDetectionParameters) ps));
		else if (ps.analysisType() == AnalysisParameters.Type.GRAPH)
			data[1] = String.format("%d",
					this.dcntrl.numberOfNodes((GraphConstructionParameters) ps));
		data[2] = "false";
		data[3] = "false";

		return data;
	}

	public AnalysisResultSelectionPanel(DataController dcntrl, GUIController pcntrl)
	{
		this.dcntrl = dcntrl;
		this.pcntrl = pcntrl;
		this.layout = new BorderLayout();
		this.params = new ArrayList<> ();

		setLayout(layout);

		String[] head = {"Name", "Num. elts", "Parameters", "View"};

		DefaultTableModel dtm = new DefaultTableModel()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};

		for (String h: head)
			dtm.addColumn(h);

		for (String algoName: dcntrl.wells().keySet())
		{
			for (WellDetectionParameters ps: dcntrl.wells().get(algoName).keySet())
			{
				dtm.addRow(this.tableRowFromParams(ps));
				this.params.add(ps);
			}
		}

		for (String algoName: dcntrl.graphs().keySet())
		{
			for (GraphConstructionParameters ps: dcntrl.graphs().get(algoName).keySet())
			{
				dtm.addRow(this.tableRowFromParams(ps));
				this.params.add(ps);
			}
		}

		this.tab = new JTable(dtm);
		this.tab.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
		this.tab.getColumnModel().getColumn(3).setCellRenderer(new CbRenderer());

		this.tab.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent event)
			{
				int r = tab.getSelectedRow();
				int c = tab.getSelectedColumn();

				if (r < 0 || r >= tab.getRowCount() || c < 0 || c >= tab.getColumnCount())
					return;
				if (c == 2 || c == 3)
				{
					if (tab.getValueAt(r, c).equals("true"))
						tab.setValueAt("false", r, c);
					else if (tab.getValueAt(r, c).equals("false"))
						tab.setValueAt("true", r, c);
				}

				if (c == 2)
					pcntrl.AnalysisResultParameterPanels(params.get(r));
				if (c == 3)
					pcntrl.selectWellResultDisplay(params.get(r));
				dtm.fireTableDataChanged();
			}
		});

		this.tab.setPreferredScrollableViewportSize(this.tab.getPreferredSize());
		this.tab.setFillsViewportHeight(true);
		//this.tab.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		JScrollPane jsp = new JScrollPane(this.tab);
		this.add(jsp);
	}

	public void addNewResults(AnalysisParameters ps)
	{
		String[] trow = this.tableRowFromParams(ps);
		DefaultTableModel dtm = (DefaultTableModel) this.tab.getModel();
		dtm.addRow(trow);
		dtm.fireTableDataChanged();
		this.params.add(ps);
		this.tab.repaint();
		this.repaint();
	}
}
