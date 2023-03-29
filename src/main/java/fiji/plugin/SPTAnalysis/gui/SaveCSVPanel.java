package fiji.plugin.SPTAnalysis.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.scijava.widget.FileWidget;

import fiji.plugin.SPTAnalysis.DataController;
import fiji.plugin.SPTAnalysis.GUIController;
import fiji.plugin.SPTAnalysis.Utils;
import fiji.plugin.SPTAnalysis.struct.MapParameters;
import fiji.plugin.SPTAnalysis.struct.Rectangle;
import fiji.plugin.SPTAnalysis.struct.ScalarMap;
import fiji.plugin.SPTAnalysis.struct.ScalarMapWindows;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsembleWindows;
import fiji.plugin.SPTAnalysis.struct.VectorMap;
import fiji.plugin.SPTAnalysis.struct.VectorMapWindows;
import fiji.plugin.SPTAnalysis.writers.CSVScalarMapWriter;
import fiji.plugin.SPTAnalysis.writers.CSVTrajectoriesWriter;
import fiji.plugin.SPTAnalysis.writers.CSVVectorMapWriter;
import fiji.plugin.SPTAnalysis.writers.CSVWriter;

public class SaveCSVPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	enum SaveType {TRAJ, DENS, DIFF, DRIFT, ALL};

	private final DataController dctrl;
	private final GUIController gctrl;

	private JCheckBox regBox;
	private JLabel sepLabel;
	private JFormattedTextField sepPanel;
	private JButton saveTrajsBut;
	private JButton saveDensBut;
	private JButton saveDiffBut;
	private JButton saveDriftBut;
	private JButton saveAllBut;

	private void saveAction(SaveType todo)
	{
		File selDir = this.gctrl.UIServ().chooseFile(null, FileWidget.DIRECTORY_STYLE);

		if (selDir == null)
			return;

		if (!selDir.exists())
			selDir.mkdirs();

		Rectangle selection = dctrl.default_selection();
		if (regBox.isSelected())
		{
			selection = gctrl.selectedRegion();
			SavePanel.saveSelection(selDir, selection);
		}

		boolean mergeWin = this.gctrl.displayPanel().mergeWindows();

		if (todo == SaveType.TRAJ || todo == SaveType.ALL)
		{
			TrajectoryEnsembleWindows tew = this.dctrl.trajs();
			if (mergeWin)
			{
				tew = new TrajectoryEnsembleWindows();
				tew.wins.add(this.dctrl.trajsFlat());
			}

			int cpt = 0;
			for (TrajectoryEnsemble te: tew.wins)
			{
				try
				{
					CSVWriter.saveCSV(selDir.getAbsolutePath() + String.format("/trajectories_%d.csv", cpt),
						new CSVTrajectoriesWriter(this.gctrl.csvSeparator(), Utils.trajsInShape(te, selection)));
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
				cpt = cpt + 1;
			}
		}

		if (todo == SaveType.DENS || todo == SaveType.ALL)
		{
			MapParameters.DensityParameters dp = this.gctrl.displayPanel().getDensityParams();
			ScalarMapWindows densWins = dctrl.densMapWindows(dp, mergeWin);
			int cpt = 0;
			for (final ScalarMap dens: densWins.wins)
			{
				try {
					CSVWriter.saveCSV(selDir.getAbsolutePath() + String.format("/density_%d_%s.csv", cpt, dp.toString()),
							new CSVScalarMapWriter(this.gctrl.csvSeparator(), dens,
									Utils.squaresInReg(dens.grid(), selection)));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				cpt = cpt + 1;
			}
		}

		if (todo == SaveType.DIFF || todo == SaveType.ALL)
		{
			MapParameters.DiffusionParameters dp = this.gctrl.displayPanel().getDiffusionParams();
			ScalarMapWindows diffWins = dctrl.diffMapWindows(dp, mergeWin);
			int cpt = 0;
			for (final ScalarMap diff: diffWins.wins)
			{
				try {
					CSVWriter.saveCSV(selDir.getAbsolutePath() + String.format("/diffusion_%d_%s.csv", cpt, dp.toString()),
							new CSVScalarMapWriter(this.gctrl.csvSeparator(), diff,
									Utils.squaresInReg(diff.grid(), selection)));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				cpt = cpt + 1;
			}
		}

		if (todo == SaveType.DRIFT || todo == SaveType.ALL)
		{
			MapParameters.DriftParameters dp = this.gctrl.displayPanel().getDriftParams();
			VectorMapWindows driftWins = dctrl.driftMapWindows(dp, mergeWin);
			int cpt = 0;
			for (final VectorMap drift: driftWins.wins)
			{
				try {
					CSVWriter.saveCSV(selDir.getAbsolutePath() + String.format("/drift_%d_%s.csv", cpt, dp.toString()),
							new CSVVectorMapWriter(this.gctrl.csvSeparator(), drift,
									Utils.squaresInReg(drift.grid(), selection)));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				cpt = cpt + 1;
			}
		}
	}

	public SaveCSVPanel(DataController dcntrl, GUIController gcntrl)
	{
		this.dctrl = dcntrl;
		this.gctrl = gcntrl;

		this.saveTrajsBut = new JButton("Save trajs.");
		this.saveTrajsBut.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				saveAction(SaveType.TRAJ);
			}
		});

		this.saveDensBut = new JButton("Save dens.");
		this.saveDensBut.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				saveAction(SaveType.DENS);
			}
		});

		this.saveDiffBut = new JButton("Save diff.");
		this.saveDiffBut.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				saveAction(SaveType.DIFF);
			}
		});

		this.saveDriftBut = new JButton("Save drift");
		this.saveDriftBut.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				saveAction(SaveType.DRIFT);
			}
		});

		this.saveAllBut = new JButton("Save all");
		this.saveAllBut.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				saveAction(SaveType.ALL);
			}
		});

		this.regBox = new JCheckBox("Restrict to region", false);

		this.sepLabel = new JLabel(String.format("Separator (%s)",
				this.gctrl.csvSeparator()));

		this.sepPanel = new JFormattedTextField();
		this.sepPanel.setValue(gctrl.csvSeparator());
		this.sepPanel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				gctrl.setCsvSeparator((String) sepPanel.getValue());
				sepLabel.setText(String.format("Separator (%s)",
						gctrl.csvSeparator()));
			}
		});

		initGUI();
	}

	public void initGUI()
	{
		setLayout(new GridBagLayout());


		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.weightx = 1;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 2;
		c.insets = new Insets(0, 10, 10, 10);

		GridBagConstraints cLabel = new GridBagConstraints();
		cLabel.gridx = 0;
		cLabel.weightx = 1;
		cLabel.anchor = GridBagConstraints.WEST;
		cLabel.insets = new Insets(0, 10, 10, 0);

		GridBagConstraints cData = new GridBagConstraints();
		cData.gridx = 1;
		cData.weightx = 0.5;
		cData.fill = GridBagConstraints.HORIZONTAL;
		cData.gridwidth = GridBagConstraints.REMAINDER;
		cData.insets = new Insets(0, 15, 10, 10);

		c.gridy = 0;
		this.add(this.regBox, c);

		cLabel.gridy = 1;
		this.add(this.sepLabel, cLabel);
		cData.gridy = 1;
		this.add(this.sepPanel, cData);

		c.gridy = 2;
		this.add(this.saveTrajsBut, c);

		c.gridy = 3;
		this.add(this.saveDensBut, c);

		c.gridy = 4;
		this.add(this.saveDiffBut, c);

		c.gridy = 5;
		this.add(this.saveDriftBut, c);

		c.gridy = 6;
		this.add(this.saveAllBut, c);
	}

	public void reset()
	{
		this.sepPanel.setValue(gctrl.csvSeparator());
	}
}
