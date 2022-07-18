package fiji.plugin.SPTAnalysis.gui;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.scijava.ui.UIService;
import org.scijava.widget.FileWidget;

import fiji.plugin.SPTAnalysis.GUIController;
import fiji.plugin.SPTAnalysis.Utils;
import fiji.plugin.SPTAnalysis.struct.Graph;
import fiji.plugin.SPTAnalysis.struct.GraphWindows;
import fiji.plugin.SPTAnalysis.struct.Shape;

public class GraphResultPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	public static final int defMinLinkDisp = 0;

	private final UIService UIServ;
	@SuppressWarnings("unused")
	private final GUIController gctrl;

	private final GraphWindows graphs;

	private DefaultTableModel dtm;
	private JTable tab;
	private JScrollPane jsp;

	private final JFormattedTextField minLinkDispPanel;
	private final JButton exportBut;

	private void updateDtm()
	{
		for (int i = this.dtm.getRowCount() - 1; i >= 0; --i)
			this.dtm.removeRow(i);

		ArrayList<Double> areas = new ArrayList<> ();
		ArrayList<Double> Nlinks = new ArrayList<> ();

		for (int i = 0; i < this.graphs.wins.size(); ++i)
		{
			for (Integer k: this.graphs.wins.get(i).nodes().keySet())
			{
				Shape node = this.graphs.wins.get(i).nodes().get(k);

				String[] data = new String[6];
				data[0] = String.format("%d", k);
				data[1] = String.format("%d", i);
				data[2] = String.format("%.2f", node.center()[0]);
				data[3] = String.format("%.2f", node.center()[1]);
				data[4] = String.format("%.2f", node.area());

				int nls = 0;
				for (Integer l: this.graphs.wins.get(i).nodes().keySet())
				{
					if (k != l && this.graphs.wins.get(i).connect(k, l) > 0.0)
						nls += 1;
				}
				data[5] = String.format("%d", nls);

				areas.add(node.area());
				Nlinks.add((double) nls);

				this.dtm.addRow(data);
			}
		}

		String[] data = new String[12];
		data[0] = "AVG ± SD";
		data[1] = String.format("N=%d", areas.size());;
		data[2] = "";
		data[3] = "";
		data[4] = String.format("%.2f ± %.2f", Utils.arrayAVG(areas), Utils.arraySD(areas));
		data[5] = String.format("%.2f ± %.2f", Utils.arrayAVG(Nlinks), Utils.arraySD(Nlinks));
		dtm.insertRow(0, data);
	}

	private void highlightCoord(final int coord)
	{
		this.tab.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus, int row,
					int column)
			{
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
						row, column);
				if (row == 0)
					c.setFont(this.getFont().deriveFont(Font.BOLD));
				if (row == coord)
				{
					c.setBackground(new java.awt.Color(255, 72, 72));
				}
				else
					c.setBackground(null);

				return c;
			}
		});

		this.tab.repaint();
		this.tab.scrollRectToVisible(this.tab.getCellRect(coord, 0, true));
	}

	public void highlight(final Shape s)
	{
		int highlightedRow = -1;
		int cpt = 1;
		for (int k = 0; k < this.graphs.wins.size(); ++k)
		{
			for (Shape s2: this.graphs.wins.get(k).nodes().values())
			{
				if (s == s2)
				{
					highlightedRow = cpt;
					break;
				}
				cpt += 1;
			}
		}

		this.highlightCoord(highlightedRow);
	}

	public GraphResultPanel(UIService uis, GUIController gctrl, final GraphWindows graphs)
	{
		this.UIServ = uis;
		this.gctrl = gctrl;
		this.graphs = graphs;

		String[] head = {"ID", "Frame", "x (µm)", "y (µm)", "Area (µm²)"};

		this.dtm = new DefaultTableModel()
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

		this.updateDtm();
		this.tab = new JTable(dtm);

		this.tab.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent event)
			{
				int r = tab.getSelectedRow();

				highlightCoord(r);

				int cpt = 0;
				for (int fr = 0; fr < graphs.wins.size(); ++fr)
				{
					for (final Shape s: graphs.wins.get(fr).nodes().values())
					{
						if (cpt == r)
						{
							gctrl.setSelectedNode(s);
							return;
						}
						++cpt;
					}
				}
			}
		});

		this.tab.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus, int row,
					int column)
			{
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
						row, column);
				if (row == 0)
					c.setFont(this.getFont().deriveFont(Font.BOLD));
				return c;
			}
		});

		this.jsp = new JScrollPane(this.tab);

		this.exportBut = new JButton("Export");
		exportBut.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					File selDir = UIServ.chooseFile(null, FileWidget.DIRECTORY_STYLE);
					if (selDir == null)
						return;

					int cpt = 0;
					for (final Graph g: graphs.wins)
					{
						g.toFile(selDir.getAbsolutePath() + String.format("/graph_%d.txt", cpt));
						++cpt;
					}
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
			}
		});
		
		this.minLinkDispPanel = new JFormattedTextField();
		this.minLinkDispPanel.setValue(defMinLinkDisp);
		this.minLinkDispPanel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				gctrl.modifyGraphOverlay(graphs, (int) minLinkDispPanel.getValue());
				gctrl.display();
			}
		});

		initGUI();
	}

	public void initGUI()
	{
		this.setLayout(new GridBagLayout());

		GridBagConstraints cLabel = new GridBagConstraints();
		cLabel.gridx = 0;
		cLabel.anchor = GridBagConstraints.WEST;
		cLabel.insets = new Insets(10, 10, 10, 0);

		GridBagConstraints cData = new GridBagConstraints();
		cData.gridx = 1;
		cData.insets = new Insets(10, 5, 10, 10);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 0.9;
		this.add(this.jsp, c);

		cLabel.gridy = 1;
		cData.gridy = 1;
		this.add(new JLabel("Num. disps. for links:"), cLabel);

		cData.gridx = 1;
		cData.fill = GridBagConstraints.HORIZONTAL;
		cData.weightx = 1;
		this.add(this.minLinkDispPanel, cData);

		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 1;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(this.exportBut, c);
	}
}
