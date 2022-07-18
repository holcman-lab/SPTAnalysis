package fiji.plugin.SPTAnalysis.gui;

import java.awt.Choice;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;

import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fiji.plugin.SPTAnalysis.estimators.DensityEstimatorParameters;
import fiji.plugin.SPTAnalysis.estimators.GridEstimatorParameters;
import fiji.plugin.SPTAnalysis.estimators.MLEEstimatorParameters;
import fiji.plugin.SPTAnalysis.estimators.WellEstimator;
import fiji.plugin.SPTAnalysis.estimators.WellEstimatorParameters;
import fiji.plugin.SPTAnalysis.struct.Freezable;

public abstract class WellEstimatorParametersPanel extends JPanel implements Freezable
{
	private static final long serialVersionUID = 1L;

	public abstract WellEstimatorParameters parameters();

	public static class GridEstimatorPanel extends WellEstimatorParametersPanel
	{
		private static final long serialVersionUID = 1L;

		public static final String defEstimatorType = WellEstimator.type.LSQELL.name();
		public static final double defDx = 0.1;
		public static final int defMinDriftPts = 10;
		public static final int defMinWellBins = 5;
		public static final boolean defDiffInWell = false;
		public static final boolean defCorrectField = false;

		protected Choice estimatorTypeChoicePanel;
		protected JFormattedTextField dxPanel;
		protected JFormattedTextField minDriftPtsPanel;
		protected JFormattedTextField minWellBinsPanel;
		protected JCheckBox diffInWellBox;
		protected JCheckBox correctFieldBox;

		public GridEstimatorPanel()
		{
			this.minDriftPtsPanel = new JFormattedTextField(new DecimalFormat("0"));
			this.minDriftPtsPanel.setValue(defMinDriftPts);

			this.dxPanel = new JFormattedTextField(new DecimalFormat("0.000"));
			this.dxPanel.setValue(defDx);

			this.minWellBinsPanel = new JFormattedTextField(new DecimalFormat("0"));
			this.minWellBinsPanel.setValue(defMinWellBins);

			this.diffInWellBox = new JCheckBox();
			this.diffInWellBox.setSelected(defDiffInWell);

			this.correctFieldBox = new JCheckBox();
			this.correctFieldBox.setSelected(defCorrectField);

			this.estimatorTypeChoicePanel = new Choice();
			this.estimatorTypeChoicePanel.add(WellEstimator.type.LSQCIRC.name());
			this.estimatorTypeChoicePanel.add(WellEstimator.type.LSQELL.name());
			this.estimatorTypeChoicePanel.select(defEstimatorType);

			this.setLayout(new GridBagLayout());
			this.initGui();
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

			cLabel.gridy = 0;
			cData.gridy = 0;
			this.add(new JLabel("Type"), cLabel);
			this.add(this.estimatorTypeChoicePanel, cData);

			cLabel.gridy = 1;
			cData.gridy = 1;
			this.add(new JLabel("Drift bins width (µm)"), cLabel);
			this.add(this.dxPanel, cData);

			cLabel.gridy = 2;
			cData.gridy = 2;
			this.add(new JLabel("Min. drift bin pts"), cLabel);
			this.add(this.minDriftPtsPanel, cData);

			cLabel.gridy = 3;
			cData.gridy = 3;
			this.add(new JLabel("Min. well bins"), cLabel);
			this.add(this.minWellBinsPanel, cData);

			cLabel.gridy = 4;
			cData.gridy = 4;
			this.add(new JLabel("Constr. diff. in well"), cLabel);
			this.add(this.diffInWellBox, cData);

			cLabel.gridy = 5;
			cData.gridy = 5;
			this.add(new JLabel("Correct drift"), cLabel);
			this.add(this.correctFieldBox, cData);
		}

		@Override
		public void freeze()
		{
			this.estimatorTypeChoicePanel.setEnabled(false);
			this.dxPanel.setEnabled(false);
			this.minDriftPtsPanel.setEnabled(false);
			this.minWellBinsPanel.setEnabled(false);
			this.diffInWellBox.setEnabled(false);
			this.correctFieldBox.setEnabled(false);
		}

		@Override
		public void reset()
		{
			this.estimatorTypeChoicePanel.select(defEstimatorType);
			this.estimatorTypeChoicePanel.setEnabled(true);
			this.dxPanel.setValue(defDx);
			this.dxPanel.setEnabled(true);
			this.minDriftPtsPanel.setValue(defMinDriftPts);
			this.minDriftPtsPanel.setEnabled(true);
			this.minWellBinsPanel.setValue(defMinWellBins);
			this.minWellBinsPanel.setEnabled(true);
			this.diffInWellBox.setSelected(defDiffInWell);
			this.diffInWellBox.setEnabled(true);
			this.correctFieldBox.setSelected(defCorrectField);
			this.correctFieldBox.setEnabled(true);
		}

		@Override
		public WellEstimatorParameters parameters()
		{
			return new GridEstimatorParameters(
					WellEstimator.type.valueOf(this.estimatorTypeChoicePanel.getSelectedItem()),
					((Number) this.dxPanel.getValue()).doubleValue(),
					((Number) this.minDriftPtsPanel.getValue()).intValue(),
					((Number) this.minWellBinsPanel.getValue()).intValue(),
					this.diffInWellBox.isSelected(),
					this.correctFieldBox.isSelected());
		}
	}

	public static class DensityEstimatorPanel extends WellEstimatorParametersPanel
	{
		private static final long serialVersionUID = 1L;

		public static final boolean defDiffInWell = false;


		protected JCheckBox diffInWellBox;

		public DensityEstimatorPanel()
		{
			this.diffInWellBox = new JCheckBox();
			this.diffInWellBox.setSelected(defDiffInWell);

			this.setLayout(new GridBagLayout());
			this.initGui();
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

			cLabel.gridy = 0;
			cData.gridy = 0;
			this.add(new JLabel("Constr. diff. in well"), cLabel);
			this.add(this.diffInWellBox, cData);
		}

		@Override
		public void freeze()
		{
			this.diffInWellBox.setEnabled(false);
		}

		@Override
		public void reset()
		{
			this.diffInWellBox.setSelected(defDiffInWell);
			this.diffInWellBox.setEnabled(true);
		}

		@Override
		public WellEstimatorParameters parameters()
		{
			return new DensityEstimatorParameters(WellEstimator.type.DENS,
					this.diffInWellBox.isSelected());
		}
	}

	public static class MLEEstimatorPanel extends WellEstimatorParametersPanel
	{
		private static final long serialVersionUID = 1L;

		public MLEEstimatorPanel()
		{
			this.setLayout(new GridBagLayout());
		}

		@Override
		public void freeze()
		{
		}

		@Override
		public void reset()
		{
		}

		@Override
		public WellEstimatorParameters parameters()
		{
			return new MLEEstimatorParameters(WellEstimator.type.MLE);
		}
	}
}
