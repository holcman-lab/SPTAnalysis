package fiji.plugin.SPTAnalysis.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import fiji.plugin.SPTAnalysis.DataController;
import fiji.plugin.SPTAnalysis.GUIController;
import fiji.plugin.SPTAnalysis.graphConstruction.GraphConstructionDBScanParameters;
import fiji.plugin.SPTAnalysis.graphConstruction.GraphConstructionParameters;

public class GraphConstructionDBScanPanel extends GraphConstructionParametersPanel
{
	private static final long serialVersionUID = 1L;

	public static final double defR = 0.2;
	public static final int defN = 10;

	private final DataController dctrl;
	@SuppressWarnings("unused")
	private final GUIController gctrl;

	private JFormattedTextField RPanel;
	private JFormattedTextField NPanel;

	public GraphConstructionDBScanPanel(DataController dctrl, GUIController gctrl)
	{
		super();
		this.dctrl = dctrl;
		this.gctrl = gctrl;

		this.updateAnalysisName();

		this.RPanel = new JFormattedTextField(new DecimalFormat("0.000"));
		this.RPanel.setValue(defR);

		this.NPanel = new JFormattedTextField(new DecimalFormat("0"));
		this.NPanel.setValue(defN);

		initGui();
	}

	private void initGui()
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

		cLabel.gridy = 1;
		cData.gridy = 1;
		this.add(new JLabel("Core distance (R, µm)"), cLabel);
		this.add(this.RPanel, cData);

		cLabel.gridy = 2;
		cData.gridy = 2;
		this.add(new JLabel("Num. neighbor pts. (N)"), cLabel);
		this.add(this.NPanel, cData);
	}

	public void updateAnalysisName()
	{
		this.setAnalysisName(this.dctrl.nextAvailableAnalysisName());
	}

	@Override
	public void freeze()
	{
		super.freeze();
		this.RPanel.setEnabled(false);
		this.NPanel.setEnabled(false);
	}

	@Override
	public void reset()
	{
		super.reset();
		this.updateAnalysisName();

		this.RPanel.setEnabled(true);
		this.RPanel.setValue(defR);
		this.NPanel.setEnabled(true);
		this.NPanel.setValue(defN);
	}

	@Override
	public GraphConstructionParameters params(String expName, double instVelTh, double minAreaTh, double maxAreaTh,
			double ellEps, GraphConstructionParameters.NodeType nodeT)
	{
		return new GraphConstructionDBScanParameters(
				expName, instVelTh,
				((Number) this.RPanel.getValue()).doubleValue(),
				((Number) this.NPanel.getValue()).intValue(),
				minAreaTh, maxAreaTh, ellEps, nodeT);
	}
}
