package fiji.plugin.SPTAnalysis.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;

import fiji.plugin.SPTAnalysis.DataController;
import fiji.plugin.SPTAnalysis.estimators.WellEstimatorParameters;
import fiji.plugin.SPTAnalysis.wellDetection.HybridWellDetectionMultiscale;

public class HybridWellDetectionMultiscalePanel extends HybridWellDetectionPanel
{
	private static final long serialVersionUID = 1L;

	public static final double defMinBinWidth = 0.1;
	public static final double defMaxBinWidth = 0.2;
	public static final double defStepBinWidth = 0.02;

	private JFormattedTextField minBinWidthPanel;
	private JFormattedTextField maxBinWidthPanel;
	private JFormattedTextField stepBinWidthPanel;
	
	public HybridWellDetectionMultiscalePanel(final JFrame frame, DataController dctrl)
	{
		super(frame, dctrl, false);

		this.minBinWidthPanel = new JFormattedTextField(new DecimalFormat("0.000"));
		this.minBinWidthPanel.setValue(defMinBinWidth);

		this.maxBinWidthPanel = new JFormattedTextField(new DecimalFormat("0.000"));
		this.maxBinWidthPanel.setValue(defMaxBinWidth);

		this.stepBinWidthPanel = new JFormattedTextField(new DecimalFormat("0.000"));
		this.stepBinWidthPanel.setValue(defStepBinWidth);

		initGui();
	}

	protected void initGui()
	{
		this.setLayout(new GridBagLayout());

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

		cLabel.gridy = 0;
		cData.gridy = 0;
		this.add(new JLabel("Min. it. bins width (µm)"), cLabel);
		this.add(this.minBinWidthPanel, cData);

		cLabel.gridy = 1;
		cData.gridy = 1;
		this.add(new JLabel("Max. it. bins width (µm)"), cLabel);
		this.add(this.maxBinWidthPanel, cData);

		cLabel.gridy = 2;
		cData.gridy = 2;
		this.add(new JLabel("Step it. bins width (µm)"), cLabel);
		this.add(this.stepBinWidthPanel, cData);

		this.ymin = 3;

		super.initGui(false);
	}

	@Override
	public HybridWellDetectionMultiscale.Parameters detectionParameters()
	{
		WellEstimatorParameters estPs = this.estParamsPanelMaps.get(selectedEst).parameters();
		//TODO: rethink
		return new HybridWellDetectionMultiscale.Parameters(
				"bb",
				((Number) this.dxSeedsPanel.getValue()).doubleValue(),
				Integer.parseInt(this.filtSeedsChoice.getSelectedItem()),
				((Number) this.minBinWidthPanel.getValue()).doubleValue(),
				((Number) this.maxBinWidthPanel.getValue()).doubleValue(),
				((Number) this.stepBinWidthPanel.getValue()).doubleValue(),
				((Number) this.densityThPanel.getValue()).doubleValue(),
				((Number) this.seedDistPanel.getValue()).intValue(),
				((Number) this.maxSizePanel.getValue()).doubleValue(),
				((Number) this.minWellPtsPanel.getValue()).intValue(),
				Integer.parseInt(this.confEllPercChoice.getSelectedItem()),
				estPs,
				(int) this.bestItShiftPanel.getValue(),
				this.itChoosePanelMaps.get(selectedItChoose).parameters(estPs));
	}

	@Override
	public void freeze()
	{
		this.minBinWidthPanel.setEnabled(false);
		this.maxBinWidthPanel.setEnabled(false);
		this.stepBinWidthPanel.setEnabled(false);
		super.freeze();
	}

	@Override
	public void reset()
	{
		this.minBinWidthPanel.setEnabled(true);
		this.minBinWidthPanel.setValue(defMinBinWidth);
		this.maxBinWidthPanel.setEnabled(true);
		this.maxBinWidthPanel.setValue(defMaxBinWidth);
		this.stepBinWidthPanel.setEnabled(true);
		this.stepBinWidthPanel.setValue(defStepBinWidth);
		super.reset();
	}
}
