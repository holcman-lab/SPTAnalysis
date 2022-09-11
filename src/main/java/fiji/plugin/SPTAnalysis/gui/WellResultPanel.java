package fiji.plugin.SPTAnalysis.gui;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
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

import org.scijava.ui.UIService;
import org.scijava.widget.FileWidget;

import fiji.plugin.SPTAnalysis.GUIController;
import fiji.plugin.SPTAnalysis.DataController;
import fiji.plugin.SPTAnalysis.Utils;
import fiji.plugin.SPTAnalysis.struct.PotWell;
import fiji.plugin.SPTAnalysis.struct.PotWellsWindows;
import fiji.plugin.SPTAnalysis.struct.Rectangle;
import fiji.plugin.SPTAnalysis.wellLinker.WellLinker;
import fiji.plugin.SPTAnalysis.writers.WellWriter;


public class WellResultPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private final UIService UIServ;
	private final GUIController gctrl;
	@SuppressWarnings("unused")
	private final DataController dctrl;

	private PotWellsWindows wells;

	private DefaultTableModel dtm;
	private JTable tab;
	private JScrollPane jsp;

	private JButton exportStats;
	private JButton exportIndv;
	private JCheckBox regBox;
	private boolean allWells;

	private void updateDtm(boolean use_reg)
	{
		if (!this.gctrl.hasSelection())
		{
			if (this.allWells)
				return;
			else
				this.allWells = true;
		}
		else
			this.allWells = false;

		for (int i = this.dtm.getRowCount() - 1; i >= 0; --i)
			this.dtm.removeRow(i);

		ArrayList<Double> as = new ArrayList<> ();
		ArrayList<Double> bs = new ArrayList<> ();
		ArrayList<Double> As = new ArrayList<> ();
		ArrayList<Double> Ds = new ArrayList<> ();
		ArrayList<Double> Es = new ArrayList<> ();
		ArrayList<Double> scores = new ArrayList<> ();
		ArrayList<Double> resTimes = new ArrayList<> ();

		int cpt = 0;
		for (int i = 0; i < this.wells.wins.size(); ++i)
		{
			for (int j = 0; j < this.wells.wins.get(i).wells.size(); ++j)
			{
				PotWell w = this.wells.wins.get(i).wells.get(j);
				if (!use_reg || (this.gctrl.selectedRegion() != null &&
						this.gctrl.selectedRegion().inside(w.ell().mu())))
				{
					String[] data = new String[13];
					data[0] = String.format("%d", cpt);
					data[1] = String.format("%d", i);
					data[2] = String.format("%.2f", w.ell().mu()[0]);
					data[3] = String.format("%.2f", w.ell().mu()[1]);
					data[4] = String.format("%.2f", w.ell().rad()[0]);
					data[5] = String.format("%.2f", w.ell().rad()[1]);
					data[6] = String.format("%.2f", w.ell().phi());
					data[7] = String.format("%.2f", w.A());
					data[8] = String.format("%.2f", w.D());
					data[9] = String.format("%.2f", w.A() / w.D());
					data[10] = String.format("%.2f", w.score().value());
					data[11] = String.format("%.2f", w.residence_time());
	
					if (this.wells.links() == null)
						data[12] = "-1";
					else
						data[12] = String.format("%d", WellLinker.findFamily(new WellLinker.WindowIndex(i,j),
								this.wells.links()));

					as.add(w.ell().rad()[0]);
					bs.add(w.ell().rad()[1]);
					As.add(w.A());
					Ds.add(w.D());
					Es.add(w.A() / w.D());
					scores.add(w.score().value());
					resTimes.add(w.residence_time());
	
					this.dtm.addRow(data);
				}
				++cpt;
			}
		}

		String[] data = new String[13];
		data[0] = "AVG ± SD";
		data[1] = "";
		data[2] = "";
		data[3] = "";
		data[4] = String.format("%.2f ± %.2f", Utils.arrayAVG(as), Utils.arraySD(as));
		data[5] = String.format("%.2f ± %.2f", Utils.arrayAVG(bs), Utils.arraySD(bs));
		data[6] = "";
		data[7] = String.format("%.2f ± %.2f", Utils.arrayAVG(As), Utils.arraySD(As));
		data[8] = String.format("%.2f ± %.2f", Utils.arrayAVG(Ds), Utils.arraySD(Ds));
		data[9] = String.format("%.2f ± %.2f", Utils.arrayAVG(Es), Utils.arraySD(Es));
		data[10] = String.format("%.2f ± %.2f", Utils.arrayAVG(scores), Utils.arraySD(scores));
		data[11] = String.format("%.3f ± %.3f", Utils.arrayAVG(resTimes), Utils.arraySD(resTimes));
		data[12] = String.format("N=%d", As.size());

		dtm.insertRow(0, data);
	}

	public WellResultPanel(UIService uis, GUIController gctrl, DataController dctrl, PotWellsWindows wells)
	{
		this.UIServ = uis;
		this.gctrl = gctrl;
		this.dctrl = dctrl;
		this.wells = wells;

		String[] head = {"ID", "Frame", "x (µm)", "y (µm)", "a (µm)",
				 "b (µm)", "φ", "A (µm²/s)", "D (µm²/s)", "E (kT)", "score",
				 "Res. time (s)", "Family"};

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

		this.allWells = false;
		this.updateDtm(false);
		this.tab = new JTable(dtm);

		this.tab.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent event)
			{
				//either invalid row or AVG row
				if (tab.getSelectedRow() < 1)
					return;

				highlightCoord(tab.getSelectedRow());

				int r = Integer.valueOf((String) tab.getModel().getValueAt(tab.getSelectedRow(), 0));
				int cpt = 0;
				for (int fr = 0; fr < wells.wins.size(); ++fr)
				{
					for (int i = 0; i < wells.wins.get(fr).wells.size(); ++i)
					{
						if (cpt == r)
						{
							gctrl.setSelectedWell(wells.wins.get(fr).wells.get(i));
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

		this.exportStats = new JButton("Export Stats");
		this.exportStats.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					File selFile = UIServ.chooseFile(null, FileWidget.SAVE_STYLE);
					if (selFile == null)
						return;

					Rectangle selection = dctrl.default_selection();
					if (regBox.isSelected())
						selection = gctrl.selectedRegion();
					wells.toFile(selFile.getAbsolutePath(), true, selection);
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
			}
		});

		this.exportIndv = new JButton("Export Indiv");
		this.exportIndv.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					File selDir = UIServ.chooseFile(null, FileWidget.DIRECTORY_STYLE);
					if (selDir == null)
						return;

					Rectangle selection = dctrl.default_selection();
					if (regBox.isSelected())
					{
						selection = gctrl.selectedRegion();
						SavePanel.saveSelection(selDir, selection);
					}

					int cpt = 0;
					for (int i = 0; i < wells.wins.size(); ++i)
					{
						for (int j = 0; j < wells.wins.get(i).wells.size(); ++j)
						{
							final PotWell w = wells.wins.get(i).wells.get(j);
							if (gctrl.selectedRegion().inside(w.ell().mu()))
							{
								File curDir = new File(selDir.getAbsolutePath() + String.format("/well_%d", cpt));
								if (!curDir.exists())
									curDir.mkdirs();
								WellWriter ww = new WellWriter(w, dctrl.trajs().wins.get(i),
										gctrl.trajsOverlay().colorScheme().wins.get(i), 1.5,
										curDir.getAbsolutePath());
								ww.generate();
							}
							++cpt;
						}
					}
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
			}
		});

		this.regBox = new JCheckBox("Wells in region", false);
		this.regBox.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent arg0)
			{
				updateDtm(arg0.getStateChange() == ItemEvent.SELECTED);
				tab.revalidate();
				tab.repaint();
			}
		});

		initGUI();
	}

	public void initGUI()
	{
		this.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 0.9;
		this.add(this.jsp, c);

		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		this.add(this.exportStats, c);

		c.gridx = 1;
		c.gridy = 1;
		this.add(this.exportIndv, c);

		c.gridx = 2;
		c.gridy = 1;
		this.add(this.regBox, c);
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

	public void highlight(PotWell we)
	{
		int highlightedRow = -1;
		int cpt = 1;
		for (int k = 0; k < this.wells.wins.size(); ++k)
		{
			for (PotWell w: this.wells.wins.get(k).wells)
			{
				if (w == we)
				{
					highlightedRow = cpt;
					break;
				}
				cpt += 1;
			}
		}

		this.highlightCoord(highlightedRow);
	}
}
