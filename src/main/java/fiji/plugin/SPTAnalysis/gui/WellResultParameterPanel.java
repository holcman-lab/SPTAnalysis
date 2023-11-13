package fiji.plugin.SPTAnalysis.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.scijava.ui.UIService;

import fiji.plugin.SPTAnalysis.estimators.GridEstimatorParameters;
import fiji.plugin.SPTAnalysis.estimators.WellEstimatorParameters;
import fiji.plugin.SPTAnalysis.wellDetection.DensityWellDetection;
import fiji.plugin.SPTAnalysis.wellDetection.HybridWellDetection;
import fiji.plugin.SPTAnalysis.wellDetection.HybridWellDetectionMultiscale;
import fiji.plugin.SPTAnalysis.wellDetection.IterationChooser;
import fiji.plugin.SPTAnalysis.wellDetection.WellDetectionParameters;


public class WellResultParameterPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private final UIService UIServ;

	public WellResultParameterPanel(UIService uis, WellDetectionParameters ps)
	{
		this.UIServ = uis;

		if (ps instanceof HybridWellDetectionMultiscale.Parameters)
			initGUI((HybridWellDetectionMultiscale.Parameters) ps);
		else if (ps instanceof HybridWellDetection.Parameters)
			initGUI((HybridWellDetection.Parameters) ps);
		else if (ps instanceof DensityWellDetection.Parameters)
			initGUI((DensityWellDetection.Parameters) ps);
		else
			assert(false);
	}

	public void initGUI(HybridWellDetectionMultiscale.Parameters ps)
	{
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);

		GridBagConstraints cLabel = new GridBagConstraints();
		cLabel.gridx = 0;
		cLabel.weightx = 1;
		cLabel.anchor = GridBagConstraints.WEST;
		cLabel.insets = new Insets(10, 15, 0, 0);

		GridBagConstraints cData = new GridBagConstraints();
		cData.gridx = 1;
		cData.weightx = 0.5;
		cData.fill = GridBagConstraints.HORIZONTAL;
		cData.gridwidth = GridBagConstraints.REMAINDER;
		cData.insets = new Insets(10, 15, 0, 0);

		cLabel.gridy = 0;
		cData.gridy = 0;
		this.add(new JLabel("Algorithm: "), cLabel);
		this.add(new JLabel("Hyb. multiscale"), cData);

		cLabel.gridy = 1;
		cData.gridy = 1;
		this.add(new JLabel("Dens bins width (µm)"), cLabel);
		this.add(new JLabel(String.valueOf(ps.dxSeeds)), cData);

		cLabel.gridy = 2;
		cData.gridy = 2;
		this.add(new JLabel("bins width min (µm):"), cLabel);
		this.add(new JLabel(String.valueOf(ps.dxMin)), cData);

		cLabel.gridy = 3;
		cData.gridy = 3;
		this.add(new JLabel("bins width max (µm):"), cLabel);
		this.add(new JLabel(String.valueOf(ps.dxMax)), cData);

		cLabel.gridy = 4;
		cData.gridy = 4;
		this.add(new JLabel("bins width step (µm):"), cLabel);
		this.add(new JLabel(String.valueOf(ps.dxStep)), cData);

		cLabel.gridy = 5;
		cData.gridy = 5;
		this.add(new JLabel("Density threshold (%):"), cLabel);
		this.add(new JLabel(String.valueOf(ps.densityTh)), cData);

		cLabel.gridy = 6;
		cData.gridy = 6;
		this.add(new JLabel("Min. seed distance (cells):"), cLabel);
		this.add(new JLabel(String.valueOf(ps.seedDist)), cData);

		cLabel.gridy = 7;
		cData.gridy = 7;
		this.add(new JLabel("Max area size (µm):"), cLabel);
		this.add(new JLabel(String.valueOf(ps.maxSize)), cData);

		cLabel.gridy = 8;
		cData.gridy = 8;
		this.add(new JLabel("Min. well pts:"), cLabel);
		this.add(new JLabel(String.valueOf(ps.minPtsTh)), cData);

		cLabel.gridy = 9;
		cData.gridy = 9;
		this.add(new JLabel("Confidence ellipse (%):"), cLabel);
		this.add(new JLabel(String.valueOf(ps.confEllPerc)), cData);

		cLabel.gridy = 10;
		cData.gridy = 10;
		this.add(new JLabel("Best it. shift:"), cLabel);
		this.add(new JLabel(String.valueOf(ps.bestItShift)), cData);

		this.initEstAndChooserParams(ps.estPs, ps.itChooserPs, cLabel, cData, 11);
	}

	public void initGUI(HybridWellDetection.Parameters ps)
	{
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);

		GridBagConstraints cLabel = new GridBagConstraints();
		cLabel.gridx = 0;
		cLabel.weightx = 1;
		cLabel.anchor = GridBagConstraints.WEST;
		cLabel.insets = new Insets(10, 15, 0, 0);
	
		GridBagConstraints cData = new GridBagConstraints();
		cData.gridx = 1;
		cData.weightx = 0.5;
		cData.fill = GridBagConstraints.HORIZONTAL;
		cData.gridwidth = GridBagConstraints.REMAINDER;
		cData.insets = new Insets(10, 15, 0, 0);

		cLabel.gridy = 0;
		cData.gridy = 0;
		this.add(new JLabel("Algorithm: "), cLabel);
		this.add(new JLabel("Hybrid"), cData);

		cLabel.gridy = 1;
		cData.gridy = 1;
		this.add(new JLabel("Seeds bins width (µm):"), cLabel);
		this.add(new JLabel(String.valueOf(ps.dxSeeds)), cData);

		cLabel.gridy = 2;
		cData.gridy = 2;
		this.add(new JLabel("Density threshold (%):"), cLabel);
		this.add(new JLabel(String.valueOf(ps.densityTh)), cData);

		cLabel.gridy = 3;
		cData.gridy = 3;
		this.add(new JLabel("Min. seed distance (cells):"), cLabel);
		this.add(new JLabel(String.valueOf(ps.seedDist)), cData);

		cLabel.gridy = 4;
		cData.gridy = 4;
		this.add(new JLabel("Grid bins width (µm):"), cLabel);
		this.add(new JLabel(String.valueOf(ps.dx)), cData);

		cLabel.gridy = 5;
		cData.gridy = 5;
		this.add(new JLabel("Max area size (µm):"), cLabel);
		this.add(new JLabel(String.valueOf(ps.maxSize)), cData);

		cLabel.gridy = 6;
		cData.gridy = 6;
		this.add(new JLabel("Min. well pts:"), cLabel);
		this.add(new JLabel(String.valueOf(ps.minPtsTh)), cData);

		cLabel.gridy = 7;
		cData.gridy = 7;
		this.add(new JLabel("Confidence ellipse:"), cLabel);
		this.add(new JLabel(String.valueOf(ps.confEllPerc)), cData);

		cLabel.gridy = 8;
		cData.gridy = 8;
		this.add(new JLabel("Best it shift:"), cLabel);
		this.add(new JLabel(String.valueOf(ps.bestItShift)), cData);

		this.initEstAndChooserParams(ps.estPs, ps.itChooserPs, cLabel, cData, 9);
	}

	public void initGUI(DensityWellDetection.Parameters ps)
	{
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);

		GridBagConstraints cLabel = new GridBagConstraints();
		cLabel.gridx = 0;
		cLabel.weightx = 1;
		cLabel.anchor = GridBagConstraints.WEST;
		cLabel.insets = new Insets(10, 15, 0, 0);
	
		GridBagConstraints cData = new GridBagConstraints();
		cData.gridx = 1;
		cData.weightx = 0.5;
		cData.fill = GridBagConstraints.HORIZONTAL;
		cData.gridwidth = GridBagConstraints.REMAINDER;
		cData.insets = new Insets(10, 15, 0, 0);

		cLabel.gridy = 0;
		cData.gridy = 0;
		this.add(new JLabel("Algorithm: "), cLabel);
		this.add(new JLabel("Density"), cData);

		cLabel.gridy = 1;
		cData.gridy = 1;
		this.add(new JLabel("Seeds bins width (µm):"), cLabel);
		this.add(new JLabel(String.valueOf(ps.dx)), cData);

		cLabel.gridy = 2;
		cData.gridy = 2;
		this.add(new JLabel("Density threshold (%):"), cLabel);
		this.add(new JLabel(String.valueOf(ps.densityTh)), cData);

		cLabel.gridy = 3;
		cData.gridy = 3;
		this.add(new JLabel("Min. seed distance (cells):"), cLabel);
		this.add(new JLabel(String.valueOf(ps.seedDist)), cData);

		cLabel.gridy = 4;
		cData.gridy = 4;
		this.add(new JLabel("Local grid dx (µm):"), cLabel);
		this.add(new JLabel(String.valueOf(ps.localGridDx)), cData);

		cLabel.gridy = 5;
		cData.gridy = 5;
		this.add(new JLabel("Local grid size (cells):"), cLabel);
		this.add(new JLabel(String.valueOf(ps.localGridSize)), cData);

		cLabel.gridy = 6;
		cData.gridy = 6;
		this.add(new JLabel("dr (µm):"), cLabel);
		this.add(new JLabel(String.valueOf(ps.dr)), cData);

		cLabel.gridy = 7;
		cData.gridy = 7;
		this.add(new JLabel("r min (µm):"), cLabel);
		this.add(new JLabel(String.valueOf(ps.rMin)), cData);

		cLabel.gridy = 8;
		cData.gridy = 8;
		this.add(new JLabel("r max (µm):"), cLabel);
		this.add(new JLabel(String.valueOf(ps.rMax)), cData);

		cLabel.gridy = 9;
		cData.gridy = 9;
		this.add(new JLabel("ratio max dist. (µm):"), cLabel);
		this.add(new JLabel(String.valueOf(ps.ratMaxDist)), cData);

		cLabel.gridy = 10;
		cData.gridy = 10;
		this.add(new JLabel("r max (µm):"), cLabel);
		this.add(new JLabel(String.valueOf(ps.rMax)), cData);

		this.initEstAndChooserParams(ps.estPs, ps.itChooserPs, cLabel, cData, 11);
	}

	public void initEstAndChooserParams(final WellEstimatorParameters estPs,
			final IterationChooser.Parameters itChoosePs,
			GridBagConstraints cLabel, GridBagConstraints cData, int cury)
	{
		if (estPs instanceof GridEstimatorParameters)
			cury += this.initGUIEstimatorParams((GridEstimatorParameters) estPs,
					cLabel, cData, cury);

		if (itChoosePs instanceof IterationChooser.BestParabScore.Parameters)
			cury += initGUIItChooserParams((IterationChooser.BestParabScore.Parameters) itChoosePs,
					cLabel, cData, cury);
		else if (itChoosePs instanceof IterationChooser.BestMLEDeltaScore.Parameters)
			cury += initGUIItChooserParams((IterationChooser.BestMLEDeltaScore.Parameters) itChoosePs,
					cLabel, cData, cury);
	}

	public int initGUIEstimatorParams(GridEstimatorParameters estPs,
			GridBagConstraints cLabel, GridBagConstraints cData, int starty)
	{
		cLabel.gridy = starty;
		cData.gridy = starty;
		this.add(new JLabel("Estimator type:"), cLabel);
		this.add(new JLabel(String.valueOf(estPs.estType.name())), cData);

		cLabel.gridy = starty + 1;
		cData.gridy = starty + 1;
		this.add(new JLabel("Min. drift bin pts:"), cLabel);
		this.add(new JLabel(String.valueOf(estPs.driftNptsTh)), cData);

		cLabel.gridy = starty + 2;
		cData.gridy = starty + 2;
		this.add(new JLabel("Min. well bins:"), cLabel);
		this.add(new JLabel(String.valueOf(estPs.minCellsTh)), cData);

		cLabel.gridy = starty + 3;
		cData.gridy = starty + 3;
		this.add(new JLabel("Constr. diff in well:"), cLabel);
		this.add(new JLabel(String.valueOf(estPs.diffInWell)), cData);

		cLabel.gridy = starty + 4;
		cData.gridy = starty + 4;
		this.add(new JLabel("Correct Field:"), cLabel);
		this.add(new JLabel(String.valueOf(estPs.correctField)), cData);

		return 5;
	}

	public int initGUIItChooserParams(IterationChooser.BestParabScore.Parameters itChoosePs,
			GridBagConstraints cLabel, GridBagConstraints cData, int starty)
	{
		cLabel.gridy = starty;
		cData.gridy = starty;
		this.add(new JLabel("It. chooser:"), cLabel);
		this.add(new JLabel("BestParabScore"), cData);

		cLabel.gridy = starty+1;
		cData.gridy = starty+1;
		this.add(new JLabel("Ang. sim. threshold:"), cLabel);
		this.add(new JLabel(String.valueOf(itChoosePs.angSimTh)), cData);

		cLabel.gridy = starty + 2;
		cData.gridy = starty + 2;
		this.add(new JLabel("Min. bins coverage ratio:"), cLabel);
		this.add(new JLabel(String.valueOf(itChoosePs.sampledRatioTh)), cData);

		return 3;
	}
	
	public int initGUIItChooserParams(IterationChooser.BestMLEDeltaScore.Parameters itChoosePs,
			GridBagConstraints cLabel, GridBagConstraints cData, int starty)
	{
		cLabel.gridy = starty;
		cData.gridy = starty;
		this.add(new JLabel("It. chooser:"), cLabel);
		this.add(new JLabel("BestMLEDelta"), cData);
		
		cLabel.gridy = starty + 1;
		cData.gridy = starty + 1;
		this.add(new JLabel("Min. slice pts:"), cLabel);
		this.add(new JLabel(String.valueOf(itChoosePs.minSlicePtsTh)), cData);

		return 2;
	}
}
