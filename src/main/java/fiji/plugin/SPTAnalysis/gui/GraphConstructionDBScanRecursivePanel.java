package fiji.plugin.SPTAnalysis.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;

import fiji.plugin.SPTAnalysis.DataController;
import fiji.plugin.SPTAnalysis.GUIController;
import fiji.plugin.SPTAnalysis.graphConstruction.GraphConstructionDBScanRecursiveParameters;
import fiji.plugin.SPTAnalysis.graphConstruction.GraphConstructionParameters;

public class GraphConstructionDBScanRecursivePanel extends GraphConstructionParametersPanel
{
	private static final long serialVersionUID = 1L;

	public static final int defMaxClustNpts = 100;
	public static final double defRMax = 0.2;
	public static final double defRStep = 0.05;
	public static final double defRMin = 0.0;
	public static final int defNMin = 10;
	public static final int defNStep = 2;
	public static final int defNMax = 50;

	private final DataController dctrl;
	@SuppressWarnings("unused")
	private final GUIController gctrl;

	private JFormattedTextField maxClustNptsPanel;
	private JFormattedTextField RMaxPanel;
	private JFormattedTextField RStepPanel;
	private JFormattedTextField RMinPanel;
	private JFormattedTextField NMinPanel;
	private JFormattedTextField NStepPanel;
	private JFormattedTextField NMaxPanel;

	public GraphConstructionDBScanRecursivePanel(DataController dctrl, GUIController gctrl)
	{
		super();
		this.dctrl = dctrl;
		this.gctrl = gctrl;

		this.updateAnalysisName();

		this.maxClustNptsPanel = new JFormattedTextField();
		this.maxClustNptsPanel.setValue(defMaxClustNpts);

		this.RMaxPanel = new JFormattedTextField(new DecimalFormat("0.000"));
		this.RMaxPanel.setValue(defRMax);

		this.RStepPanel = new JFormattedTextField(new DecimalFormat("0.000"));
		this.RStepPanel.setValue(defRStep);

		this.RMinPanel = new JFormattedTextField(new DecimalFormat("0.000"));
		this.RMinPanel.setValue(defRMin);

		this.NMinPanel = new JFormattedTextField();
		this.NMinPanel.setValue(defNMin);

		this.NStepPanel = new JFormattedTextField();
		this.NStepPanel.setValue(defNStep);

		this.NMaxPanel = new JFormattedTextField();
		this.NMaxPanel.setValue(defNMax);

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

		cLabel.gridy = 0;
		cData.gridy = 0;
		this.add(new JLabel("Max. cluster pts."), cLabel);
		this.add(this.maxClustNptsPanel, cData);

		cLabel.gridy = 1;
		cData.gridy = 1;
		this.add(new JLabel("Max. core dist. (R, µm)"), cLabel);
		this.add(this.RMaxPanel, cData);

		cLabel.gridy = 2;
		cData.gridy = 2;
		this.add(new JLabel("Step core dist. (R, µm)"), cLabel);
		this.add(this.RStepPanel, cData);

		cLabel.gridy = 3;
		cData.gridy = 3;
		this.add(new JLabel("Min. core dist. (R, µm)"), cLabel);
		this.add(this.RMinPanel, cData);

		cLabel.gridy = 4;
		cData.gridy = 4;
		this.add(new JLabel("Min. num. neighb. pts. (N)"), cLabel);
		this.add(this.NMinPanel, cData);

		cLabel.gridy = 5;
		cData.gridy = 5;
		this.add(new JLabel("Step num. neighb. pts. (N)"), cLabel);
		this.add(this.NStepPanel, cData);

		cLabel.gridy = 6;
		cData.gridy = 6;
		this.add(new JLabel("Max. num. neighb. pts. (N)"), cLabel);
		this.add(this.NMaxPanel, cData);
	}

	public void updateAnalysisName()
	{
		this.setAnalysisName(this.dctrl.nextAvailableAnalysisName());
	}

	@Override
	public void freeze()
	{
		super.freeze();
		this.maxClustNptsPanel.setEnabled(false);
		this.RMaxPanel.setEnabled(false);
		this.RStepPanel.setEnabled(false);
		this.RMinPanel.setEnabled(false);
		this.NMinPanel.setEnabled(false);
		this.NStepPanel.setEnabled(false);
		this.NMaxPanel.setEnabled(false);
	}

	@Override
	public void reset()
	{
		super.reset();
		this.updateAnalysisName();

		this.maxClustNptsPanel.setEnabled(true);
		this.maxClustNptsPanel.setValue(defMaxClustNpts);
		this.RMaxPanel.setEnabled(true);
		this.RMaxPanel.setValue(defRMax);
		this.RStepPanel.setEnabled(true);
		this.RStepPanel.setValue(defRStep);
		this.RMinPanel.setEnabled(true);
		this.RMinPanel.setValue(defRMin);
		this.NMinPanel.setEnabled(true);
		this.NMinPanel.setValue(defNMin);
		this.NStepPanel.setEnabled(true);
		this.NStepPanel.setValue(defNStep);
		this.NMaxPanel.setEnabled(true);
		this.NMaxPanel.setValue(defNMax);
	}

	@Override
	public GraphConstructionParameters params(String expName, double instVelTh, double minAreaTh, double maxAreaTh,
			double ellEps, GraphConstructionParameters.NodeType nodeT)
	{
		return new GraphConstructionDBScanRecursiveParameters(
				expName, instVelTh,
				(int) this.maxClustNptsPanel.getValue(),
				((Number) this.RMaxPanel.getValue()).doubleValue(),
				((Number) this.RStepPanel.getValue()).doubleValue(),
				((Number) this.RMinPanel.getValue()).doubleValue(),
				(int) this.NMinPanel.getValue(),
				(int) this.NStepPanel.getValue(),
				(int) this.NMaxPanel.getValue(),
				minAreaTh, maxAreaTh, ellEps, nodeT);
	}
}
