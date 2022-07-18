package fiji.plugin.SPTAnalysis.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.scijava.ui.UIService;

import fiji.plugin.SPTAnalysis.graphConstruction.GraphConstructionDBScanParameters;
import fiji.plugin.SPTAnalysis.graphConstruction.GraphConstructionDBScanRecursiveParameters;
import fiji.plugin.SPTAnalysis.graphConstruction.GraphConstructionParameters;

public class GraphResultParameterPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private final UIService UIServ;

	public GraphResultParameterPanel(UIService uis, GraphConstructionParameters ps)
	{
		this.UIServ = uis;

		if (ps instanceof GraphConstructionDBScanParameters)
			initGUI((GraphConstructionDBScanParameters) ps);
		else if (ps instanceof GraphConstructionDBScanRecursiveParameters)
			initGUI((GraphConstructionDBScanRecursiveParameters) ps);
		else
			assert(false);
	}

	public void initGUI(GraphConstructionDBScanParameters ps)
	{
		this.setLayout(new GridBagLayout());

		GridBagConstraints cLabel = new GridBagConstraints();
		cLabel.gridx = 0;
		cLabel.anchor = GridBagConstraints.WEST;
		cLabel.insets = new Insets(10, 10, 0, 0);

		GridBagConstraints cData = new GridBagConstraints();
		cData.gridx = 1;
		cData.fill = GridBagConstraints.HORIZONTAL;
		cData.gridwidth = GridBagConstraints.REMAINDER;
		cData.insets = new Insets(10, 5, 0, 10);

		cLabel.gridy = 0;
		cData.gridy = 0;
		this.add(new JLabel("Algorithm:"), cLabel);
		this.add(new JLabel("DBScan"), cData);

		cLabel.gridy = 1;
		cData.gridy = 1;
		this.add(new JLabel("Low velocity th (µm/s):"), cLabel);
		this.add(new JLabel(String.valueOf(ps.lowVelTh)), cData);

		cLabel.gridy = 2;
		cData.gridy = 2;
		this.add(new JLabel("Max. pts dist (µm):"), cLabel);
		this.add(new JLabel(String.valueOf(ps.R)), cData);

		cLabel.gridy = 3;
		cData.gridy = 3;
		this.add(new JLabel("Num neighb pts:"), cLabel);
		this.add(new JLabel(String.valueOf(ps.N)), cData);

		cLabel.gridy = 4;
		cData.gridy = 4;
		this.add(new JLabel("Min node area (µm²):"), cLabel);
		this.add(new JLabel(String.valueOf(ps.minArea)), cData);

		cLabel.gridy = 5;
		cData.gridy = 5;
		cLabel.insets = new Insets(10, 10, 10, 0);
		cData.insets = new Insets(10, 5, 10, 10);
		this.add(new JLabel("Max node area (µm²):"), cLabel);
		if (ps.maxArea == Double.MAX_VALUE)
			this.add(new JLabel("inf"), cData);
		else
			this.add(new JLabel(String.valueOf(ps.maxArea)), cData);
	}
	
	public void initGUI(GraphConstructionDBScanRecursiveParameters ps)
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
		this.add(new JLabel("Algorithm:"), cLabel);
		this.add(new JLabel("DBScanRecursive"), cData);

		cLabel.gridy = 1;
		cData.gridy = 1;
		this.add(new JLabel("Low velocity th (µm/s):"), cLabel);
		this.add(new JLabel(String.valueOf(ps.lowVelTh)), cData);

		cLabel.gridy = 2;
		cData.gridy = 2;
		this.add(new JLabel("Max.clust npts:"), cLabel);
		this.add(new JLabel(String.valueOf(ps.maxClustNpts)), cData);

		cLabel.gridy = 3;
		cData.gridy = 3;
		this.add(new JLabel("R Max. (µm):"), cLabel);
		this.add(new JLabel(String.valueOf(ps.RMax)), cData);

		cLabel.gridy = 4;
		cData.gridy = 4;
		this.add(new JLabel("R Step (µm):"), cLabel);
		this.add(new JLabel(String.valueOf(ps.RStep)), cData);

		cLabel.gridy = 5;
		cData.gridy = 5;
		this.add(new JLabel("R Min. (µm):"), cLabel);
		this.add(new JLabel(String.valueOf(ps.RMin)), cData);

		cLabel.gridy = 6;
		cData.gridy = 6;
		this.add(new JLabel("N min:"), cLabel);
		this.add(new JLabel(String.valueOf(ps.NMin)), cData);

		cLabel.gridy = 7;
		cData.gridy = 7;
		this.add(new JLabel("N step:"), cLabel);
		this.add(new JLabel(String.valueOf(ps.NStep)), cData);

		cLabel.gridy = 8;
		cData.gridy = 8;
		this.add(new JLabel("N Max:"), cLabel);
		this.add(new JLabel(String.valueOf(ps.NMax)), cData);

		cLabel.gridy = 9;
		cData.gridy = 9;
		this.add(new JLabel("Min node area (µm²):"), cLabel);
		this.add(new JLabel(String.valueOf(ps.minArea)), cData);

		cLabel.gridy = 10;
		cData.gridy = 10;
		cLabel.insets = new Insets(10, 10, 10, 0);
		cData.insets = new Insets(10, 5, 10, 10);
		this.add(new JLabel("Max node area (µm²):"), cLabel);
		if (ps.maxArea == Double.MAX_VALUE)
			this.add(new JLabel("inf"), cData);
		else
			this.add(new JLabel(String.valueOf(ps.maxArea)), cData);
	}
}
