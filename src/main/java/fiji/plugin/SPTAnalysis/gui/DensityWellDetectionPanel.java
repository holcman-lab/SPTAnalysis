package fiji.plugin.SPTAnalysis.gui;

import java.awt.Choice;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.util.HashMap;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;

import fiji.plugin.SPTAnalysis.DataController;
import fiji.plugin.SPTAnalysis.GUIController;
import fiji.plugin.SPTAnalysis.estimators.WellEstimatorParameters;
import fiji.plugin.SPTAnalysis.wellDetection.DensityWellDetection;
import fiji.plugin.SPTAnalysis.wellDetection.IterationChooser;

public class DensityWellDetectionPanel extends WellDetectionParametersPanel
{
	private static final long serialVersionUID = 1L;

	public static final double defBinWidth = 0.2;
	public static final double defDensityTh = 5.0;
	public static final int defSeedDist = 3;
	public static final double defLocalGridDx = 0.05;
	public static final int defLocalGridSize = 5;
	public static final double defDr = 0.05;
	public static final double defRMin = 0.1;
	public static final double defRMax = 1;
	public static final double defRatMaxDist = 0.5;
	public static final int defMinWellPts = 20;
	public static final String defItChooser = IterationChooser.chooser.MinDens.name();

	@SuppressWarnings("unused")
	private final JFrame frame;


	protected JFormattedTextField binWidthPanel;
	protected JFormattedTextField densityThPanel;
	protected JFormattedTextField seedDistPanel;
	protected JFormattedTextField localGridDxPanel;
	protected JFormattedTextField localGridSizePanel;
	protected JFormattedTextField drPanel;
	protected JFormattedTextField rMinPanel;
	protected JFormattedTextField rMaxPanel;
	protected JFormattedTextField ratMaxDistPanel;

	protected WellEstimatorParametersPanel estParamsPanel;

	protected HashMap<String, IterationChooserParametersPanel> itChoosePanelMaps;
	protected String selectedItChoose;

	protected Choice itChooserChoice;

	public DensityWellDetectionPanel(final JFrame frame, DataController dctrl)
	{
		this.frame = frame;

		this.binWidthPanel = new JFormattedTextField(new DecimalFormat("0.000"));
		this.binWidthPanel.setValue(defBinWidth);

		this.densityThPanel = new JFormattedTextField(new DecimalFormat("0.00"));
		this.densityThPanel.setValue(defDensityTh);

		this.seedDistPanel = new JFormattedTextField(new DecimalFormat("0"));
		this.seedDistPanel.setValue(defSeedDist);

		this.localGridDxPanel = new JFormattedTextField(new DecimalFormat("0.000"));
		this.localGridDxPanel.setValue(defLocalGridDx);

		this.localGridSizePanel = new JFormattedTextField(new DecimalFormat("0"));
		this.localGridSizePanel.setValue(defLocalGridSize);

		this.drPanel = new JFormattedTextField(new DecimalFormat("0.000"));
		this.drPanel.setValue(defDr);

		this.rMinPanel = new JFormattedTextField(new DecimalFormat("0.000"));
		this.rMinPanel.setValue(defRMin);

		this.rMaxPanel = new JFormattedTextField(new DecimalFormat("0.000"));
		this.rMaxPanel.setValue(defRMax);

		this.ratMaxDistPanel = new JFormattedTextField(new DecimalFormat("0.000"));
		this.ratMaxDistPanel.setValue(defRatMaxDist);

		this.estParamsPanel = new WellEstimatorParametersPanel.DensityEstimatorPanel();

		this.itChoosePanelMaps = new HashMap<> ();
		this.itChoosePanelMaps.put(IterationChooser.chooser.MinDens.name(),
				new IterationChooserParametersPanel.MinDensPanel());
		this.itChoosePanelMaps.put(IterationChooser.chooser.MaxDensPerc.name(),
				new IterationChooserParametersPanel.MaxDensPercPanel());

		this.itChooserChoice = new Choice();
		this.itChooserChoice.add(IterationChooser.chooser.MinDens.name());
		this.itChooserChoice.add(IterationChooser.chooser.MaxDensPerc.name());
		this.itChooserChoice.select(defItChooser);
		this.selectedItChoose = defItChooser;
		this.itChooserChoice.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				remove(itChoosePanelMaps.get(selectedItChoose));
				selectedItChoose = itChooserChoice.getSelectedItem();

				GridBagConstraints c = new GridBagConstraints();
				c.gridx = 0;
				c.gridy = 13;
				c.weightx = 1;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridwidth = GridBagConstraints.REMAINDER;

				add(itChoosePanelMaps.get(selectedItChoose), c);
				revalidate();
				repaint();
				frame.pack();
			}
		});
		
		this.setLayout(new GridBagLayout());
		initGui();
	}

	protected void initGui()
	{
		GridBagConstraints cLabel = new GridBagConstraints();
		cLabel.gridx = 0;
		cLabel.weightx = 1;
		cLabel.anchor = GridBagConstraints.WEST;
		cLabel.insets = new Insets(10, 10, 0, 0);

		GridBagConstraints cData = new GridBagConstraints();
		cData.gridx = 1;
		cData.weightx = 0.5;
		cData.fill = GridBagConstraints.HORIZONTAL;
		cData.gridwidth = GridBagConstraints.REMAINDER;
		cData.insets = new Insets(10, 5, 0, 10);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = GridBagConstraints.REMAINDER;

		cLabel.gridy = 0;
		cData.gridy = 0;
		this.add(new JLabel("Dens. grid bins width (µm)"), cLabel);
		this.add(this.binWidthPanel, cData);

		cLabel.gridy = 1;
		cData.gridy = 1;
		this.add(new JLabel("Dens. threshold (%)"), cLabel);
		this.add(this.densityThPanel, cData);

		cLabel.gridy = 2;
		cData.gridy = 2;
		this.add(new JLabel("Min. seed distance (cells)"), cLabel);
		this.add(this.seedDistPanel, cData);

		cLabel.gridy = 3;
		cData.gridy = 3;
		this.add(new JLabel("Local grid bins width (µm)"), cLabel);
		this.add(this.localGridDxPanel, cData);

		cLabel.gridy = 4;
		cData.gridy = 4;
		this.add(new JLabel("Local grid size"), cLabel);
		this.add(this.localGridSizePanel, cData);

		cLabel.gridy = 5;
		cData.gridy = 5;
		this.add(new JLabel("Ring width (µm)"), cLabel);
		this.add(this.drPanel, cData);

		cLabel.gridy = 6;
		cData.gridy = 6;
		this.add(new JLabel("Min. ring radius (µm)"), cLabel);
		this.add(this.rMinPanel, cData);

		cLabel.gridy = 7;
		cData.gridy = 7;
		this.add(new JLabel("Max. ring radius (µm)"), cLabel);
		this.add(this.rMaxPanel, cData);

		cLabel.gridy = 8;
		cData.gridy = 8;
		this.add(new JLabel("Max. cov. ratio dist (µm)"), cLabel);
		this.add(this.ratMaxDistPanel, cData);

		c.gridy = 9;
		c.insets = new Insets(10, 10, 0, 10);
		this.add(new JSeparator(JSeparator.HORIZONTAL), c);
		c.insets = new Insets(0, 10, 0, 10);

		cLabel.gridy = 10;
		this.add(GUIController.newBoldLabel("Estimator parameters"), cLabel);

		c.gridy = 11;
		this.add(this.estParamsPanel, c);

		cLabel.gridy = 12;
		cData.gridy = 12;
		this.add(new JLabel("Iteration scoring"), cLabel);
		this.add(this.itChooserChoice, cData);
		
		c.gridy = 13;
		this.add(this.itChoosePanelMaps.get(selectedItChoose), c);
	}

	@Override
	public DensityWellDetection.Parameters detectionParameters()
	{
		WellEstimatorParameters estPs = this.estParamsPanel.parameters();
		//TODO: rethink
		return new DensityWellDetection.Parameters(
				"aa",
				((Number) this.binWidthPanel.getValue()).doubleValue(),
				((Number) this.densityThPanel.getValue()).doubleValue(),
				((Number) this.seedDistPanel.getValue()).intValue(),
				((Number) this.localGridDxPanel.getValue()).doubleValue(),
				((Number) this.localGridSizePanel.getValue()).intValue(),
				((Number) this.drPanel.getValue()).doubleValue(),
				((Number) this.rMinPanel.getValue()).doubleValue(),
				((Number) this.rMaxPanel.getValue()).doubleValue(),
				((Number) this.ratMaxDistPanel.getValue()).doubleValue(),
				estPs,
				this.itChoosePanelMaps.get(selectedItChoose).parameters(estPs));
	}

	@Override
	public void freeze()
	{
		this.binWidthPanel.setEnabled(false);
		this.densityThPanel.setEnabled(false);
		this.seedDistPanel.setEnabled(false);
		this.localGridDxPanel.setEnabled(false);
		this.localGridSizePanel.setEnabled(false);
		this.drPanel.setEnabled(false);
		this.rMinPanel.setEnabled(false);
		this.rMaxPanel.setEnabled(false);
		this.ratMaxDistPanel.setEnabled(false);
		this.estParamsPanel.freeze();
		this.itChooserChoice.setEnabled(false);
		this.itChoosePanelMaps.get(selectedItChoose).freeze();
	}

	@Override
	public void reset()
	{
		this.binWidthPanel.setValue(defBinWidth);
		this.binWidthPanel.setEnabled(true);
		this.densityThPanel.setValue(defDensityTh);
		this.densityThPanel.setEnabled(true);
		this.seedDistPanel.setValue(defSeedDist);
		this.seedDistPanel.setEnabled(true);
		this.localGridDxPanel.setEnabled(true);
		this.localGridDxPanel.setValue(defLocalGridDx);
		this.localGridSizePanel.setEnabled(true);
		this.localGridSizePanel.setValue(defLocalGridSize);
		this.drPanel.setEnabled(true);
		this.drPanel.setValue(defDr);
		this.rMinPanel.setEnabled(true);
		this.rMinPanel.setValue(defRMin);
		this.rMaxPanel.setEnabled(true);
		this.rMaxPanel.setValue(defRMax);
		this.ratMaxDistPanel.setEnabled(true);
		this.ratMaxDistPanel.setValue(defRatMaxDist);

		this.estParamsPanel.setEnabled(true);
		this.estParamsPanel.reset();

		for (final IterationChooserParametersPanel pan: this.itChoosePanelMaps.values())
			pan.reset();
		this.itChooserChoice.select(defItChooser);
		this.itChooserChoice.setEnabled(true);
		this.itChooserChoice.getItemListeners()[0].itemStateChanged(null);
	}
}
