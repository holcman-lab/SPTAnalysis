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
import fiji.plugin.SPTAnalysis.estimators.WellEstimatorParameters;
import fiji.plugin.SPTAnalysis.wellDetection.HybridWellDetection;
import fiji.plugin.SPTAnalysis.wellDetection.IterationChooser;

public class HybridWellDetectionPanel extends WellDetectionParametersPanel
{
	private static final long serialVersionUID = 1L;

	public static final double defDxSeeds = 0.2;
	public static final int defFiltSeedsItem = 0;
	public static final double defDensityTh = 5.0;
	public static final int defSeedDist = 3;
	public static final double defDx = 0.05;
	public static final double defMaxSize = 1.0;
	public static final int defMinWellPts = 20;
	public static final String defConfEllPerc = "95";
	public static final int defBestItShift = 0;
	public static final String defEstimator = estTypeCategories[0];
	public static final String defItChooser = IterationChooser.chooser.BestParabScore.name();

	@SuppressWarnings("unused")
	private final JFrame frame;

	protected HashMap<String, WellEstimatorParametersPanel> estParamsPanelMaps;
	protected String selectedEst;
	protected HashMap<String, IterationChooserParametersPanel> itChoosePanelMaps;
	protected String selectedItChoose;

	protected JFormattedTextField dxSeedsPanel;
	protected Choice filtSeedsChoice;
	protected JFormattedTextField densityThPanel;
	protected JFormattedTextField seedDistPanel;
	protected JFormattedTextField dxPanel;
	protected JFormattedTextField maxSizePanel;
	protected JFormattedTextField minWellPtsPanel;
	protected JFormattedTextField bestItShiftPanel;

	protected Choice confEllPercChoice;
	protected Choice estimatorChoice;
	protected Choice itChooserChoice;

	int ymin;

	public HybridWellDetectionPanel(final JFrame frame, DataController dctrl, boolean init)
	{
		if (init)
			ymin = 1;
		else
			ymin = 0;

		this.frame = frame;

		this.estParamsPanelMaps = new HashMap<> ();
		this.estParamsPanelMaps.put("Grid",
				new WellEstimatorParametersPanel.GridEstimatorPanel());
		this.estParamsPanelMaps.put("MLE",
				new WellEstimatorParametersPanel.MLEEstimatorPanel());
		this.estParamsPanelMaps.put("Dens",
				new WellEstimatorParametersPanel.DensityEstimatorPanel());
		this.estParamsPanelMaps.put("DensMLE",
				new WellEstimatorParametersPanel.MLEEstimatorPanel());

		this.itChoosePanelMaps = new HashMap<> ();
		this.itChoosePanelMaps.put(IterationChooser.chooser.BestParabScore.name(),
				new IterationChooserParametersPanel.BestParabScorePanel());
		this.itChoosePanelMaps.put(IterationChooser.chooser.BestMLEScore.name(),
				new IterationChooserParametersPanel.BestMLEScorePanel());
		this.itChoosePanelMaps.put(IterationChooser.chooser.BestMLEDelta.name(),
				new IterationChooserParametersPanel.BestMLEDeltaPanel());

		this.dxSeedsPanel = new JFormattedTextField(new DecimalFormat("0.000"));
		this.dxSeedsPanel.setValue(defDx);
		
		this.filtSeedsChoice = new Choice();
		this.filtSeedsChoice.add("1");
		this.filtSeedsChoice.add("3");
		this.filtSeedsChoice.add("5");
		this.filtSeedsChoice.add("7");
		this.filtSeedsChoice.select(defFiltSeedsItem);

		this.densityThPanel = new JFormattedTextField(new DecimalFormat("0.00"));
		this.densityThPanel.setValue(defDensityTh);

		this.seedDistPanel = new JFormattedTextField(new DecimalFormat("0"));
		this.seedDistPanel.setValue(defSeedDist);

		this.dxPanel = new JFormattedTextField(new DecimalFormat("0.000"));
		this.dxPanel.setValue(defDx);

		this.maxSizePanel = new JFormattedTextField(new DecimalFormat("0.000"));
		this.maxSizePanel.setValue(defMaxSize);

		this.minWellPtsPanel = new JFormattedTextField(new DecimalFormat("0"));
		this.minWellPtsPanel.setValue(defMinWellPts);

		this.bestItShiftPanel = new JFormattedTextField(new DecimalFormat("0"));
		this.bestItShiftPanel.setValue(defBestItShift);

		this.confEllPercChoice = new Choice();
		this.confEllPercChoice.add("90");
		this.confEllPercChoice.add("95");
		this.confEllPercChoice.add("99");
		this.confEllPercChoice.select(defConfEllPerc);

		this.estimatorChoice = new Choice();
		for (final String estType: estTypeCategories)
			this.estimatorChoice.add(estType);
		this.estimatorChoice.select(defEstimator);
		this.selectedEst = defEstimator;
		this.estimatorChoice.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				remove(estParamsPanelMaps.get(selectedEst));
				selectedEst = estimatorChoice.getSelectedItem();

				GridBagConstraints c = new GridBagConstraints();
				c.gridx = 0;
				c.gridy = ymin + 11;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridwidth = GridBagConstraints.REMAINDER;

				add(estParamsPanelMaps.get(selectedEst), c);
				revalidate();
				repaint();
				frame.pack();
			}
		});

		this.itChooserChoice = new Choice();
		this.itChooserChoice.add(IterationChooser.chooser.BestParabScore.name());
		this.itChooserChoice.add(IterationChooser.chooser.BestMLEScore.name());
		this.itChooserChoice.add(IterationChooser.chooser.BestMLEDelta.name());
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
				c.gridy = ymin + 14;
				c.weightx = 1;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridwidth = GridBagConstraints.REMAINDER;

				add(itChoosePanelMaps.get(selectedItChoose), c);
				revalidate();
				repaint();
				frame.pack();
			}
		});

		if (init)
		{
			this.setLayout(new GridBagLayout());
			initGui(true);
		}
	}

	protected void initGui(boolean showDx)
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
		c.insets = new Insets(0, 10, 0, 10);

		if (showDx)
		{
			cLabel.gridy = 0;
			cData.gridy = 0;
			this.add(new JLabel("Iteration bins width (µm)"), cLabel);
			this.add(this.dxPanel, cData);
		}

		cLabel.gridy = ymin + 1;
		cData.gridy = ymin + 1;
		this.add(new JLabel("Density bins width (µm)"), cLabel);
		this.add(this.dxSeedsPanel, cData);
		
		cLabel.gridy = ymin + 2;
		cData.gridy = ymin + 2;
		this.add(new JLabel("Density filt"), cLabel);
		this.add(this.filtSeedsChoice, cData);

		cLabel.gridy = ymin + 3;
		cData.gridy = ymin + 3;
		this.add(new JLabel("Density threshold (%)"), cLabel);
		this.add(this.densityThPanel, cData);

		cLabel.gridy = ymin + 4;
		cData.gridy = ymin + 4;
		this.add(new JLabel("Min. seed distance (cells)"), cLabel);
		this.add(this.seedDistPanel, cData);

		cLabel.gridy = ymin + 5;
		cData.gridy = ymin + 5;
		this.add(new JLabel("Max. well area (µm)"), cLabel);
		this.add(this.maxSizePanel, cData);

		cLabel.gridy = ymin + 6;
		cData.gridy = ymin + 6;
		this.add(new JLabel("Min. well pts"), cLabel);
		this.add(this.minWellPtsPanel, cData);

		cLabel.gridy = ymin + 7;
		cData.gridy = ymin + 7;
		this.add(new JLabel("Conf. ell. perc."), cLabel);
		this.add(this.confEllPercChoice, cData);

		cLabel.gridy = ymin + 8;
		cData.gridy = ymin + 8;
		this.add(new JLabel("Best it. shift"), cLabel);
		this.add(this.bestItShiftPanel, cData);

		c.gridy = ymin + 9;
		c.insets = new Insets(10, 10, 0, 10);
		this.add(new JSeparator(JSeparator.HORIZONTAL), c);
		c.insets = new Insets(0, 10, 0, 10);

		cLabel.gridy = ymin + 10;
		cData.gridy = ymin + 10;
		this.add(new JLabel("Estimator"), cLabel);
		this.add(this.estimatorChoice, cData);

		c.gridy = ymin + 11;
		this.add(this.estParamsPanelMaps.get(selectedEst), c);

		c.gridy = ymin + 12;
		c.insets = new Insets(10, 10, 0, 10);
		this.add(new JSeparator(JSeparator.HORIZONTAL), c);
		c.insets = new Insets(0, 10, 0, 10);
		
		cLabel.gridy = ymin + 13;
		cData.gridy = ymin + 13;
		this.add(new JLabel("Iteration scoring"), cLabel);
		this.add(this.itChooserChoice, cData);

		c.gridy = ymin + 14;
		this.add(this.itChoosePanelMaps.get(selectedItChoose), c);
	}

	@Override
	public HybridWellDetection.Parameters detectionParameters()
	{
		WellEstimatorParameters estPs = this.estParamsPanelMaps.get(selectedEst).parameters();
		//TODO: rethink
		return new HybridWellDetection.Parameters(
				"aa",
				((Number) this.dxSeedsPanel.getValue()).doubleValue(),
				Integer.parseInt(this.filtSeedsChoice.getSelectedItem()),
				((Number) this.densityThPanel.getValue()).doubleValue(),
				((Number) this.seedDistPanel.getValue()).intValue(),
				((Number) this.dxPanel.getValue()).doubleValue(),
				((Number) this.maxSizePanel.getValue()).doubleValue(),
				((Number) this.minWellPtsPanel.getValue()).intValue(),
				Integer.parseInt(this.confEllPercChoice.getSelectedItem()),
				estPs,
				((Number) this.bestItShiftPanel.getValue()).intValue(),
				this.itChoosePanelMaps.get(selectedItChoose).parameters(estPs));
	}

	@Override
	public void freeze()
	{
		this.dxSeedsPanel.setEnabled(false);
		this.filtSeedsChoice.setEnabled(false);
		this.densityThPanel.setEnabled(false);
		this.seedDistPanel.setEnabled(false);
		this.dxPanel.setEnabled(false);
		this.maxSizePanel.setEnabled(false);
		this.minWellPtsPanel.setEnabled(false);
		this.estimatorChoice.setEnabled(false);
		this.confEllPercChoice.setEnabled(false);
		this.bestItShiftPanel.setEnabled(false);
		this.estParamsPanelMaps.get(selectedEst).freeze();
		this.itChooserChoice.setEnabled(false);
		this.itChoosePanelMaps.get(selectedItChoose).freeze();
	}

	@Override
	public void reset()
	{
		this.dxSeedsPanel.setValue(defDxSeeds);
		this.dxSeedsPanel.setEnabled(true);
		this.filtSeedsChoice.setEnabled(true);
		this.filtSeedsChoice.select(defFiltSeedsItem);
		this.densityThPanel.setValue(defDensityTh);
		this.densityThPanel.setEnabled(true);
		this.seedDistPanel.setValue(defSeedDist);
		this.seedDistPanel.setEnabled(true);
		this.dxPanel.setValue(defDx);
		this.dxPanel.setEnabled(true);
		this.maxSizePanel.setValue(defMaxSize);
		this.maxSizePanel.setEnabled(true);
		this.minWellPtsPanel.setValue(defMinWellPts);
		this.minWellPtsPanel.setEnabled(true);

		this.confEllPercChoice.setEnabled(true);
		this.confEllPercChoice.select(defConfEllPerc);
		this.bestItShiftPanel.setEnabled(true);

		for (final WellEstimatorParametersPanel pan: this.estParamsPanelMaps.values())
			pan.reset();
		this.estimatorChoice.select(defEstimator);
		this.estimatorChoice.setEnabled(true);
		this.estimatorChoice.getItemListeners()[0].itemStateChanged(null);

		for (final IterationChooserParametersPanel pan: this.itChoosePanelMaps.values())
			pan.reset();
		this.itChooserChoice.select(defItChooser);
		this.itChooserChoice.setEnabled(true);
		this.itChooserChoice.getItemListeners()[0].itemStateChanged(null);
	}
}
