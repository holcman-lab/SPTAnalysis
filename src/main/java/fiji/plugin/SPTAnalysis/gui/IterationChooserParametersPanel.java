package fiji.plugin.SPTAnalysis.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fiji.plugin.SPTAnalysis.estimators.WellEstimatorParameters;
import fiji.plugin.SPTAnalysis.struct.Freezable;
import fiji.plugin.SPTAnalysis.wellDetection.IterationChooser;
import fiji.plugin.SPTAnalysis.wellDetection.IterationChooser.Parameters;

public abstract class IterationChooserParametersPanel extends JPanel implements Freezable
{
	private static final long serialVersionUID = 1L;
	
	public abstract IterationChooser.Parameters parameters(final WellEstimatorParameters estPs);

	public static class BestParabScorePanel extends IterationChooserParametersPanel
	{
		private static final long serialVersionUID = 1L;

		public static final double defAngSimTh = 0.7;
		public static final double defBinCoverRatio = 0.5;

		protected JFormattedTextField angSimThPanel;
		protected JFormattedTextField binCoverRatioPanel;

		public BestParabScorePanel()
		{
			this.angSimThPanel = new JFormattedTextField(new DecimalFormat("0.00"));
			this.angSimThPanel.setValue(defAngSimTh);

			this.binCoverRatioPanel = new JFormattedTextField(new DecimalFormat("0.00"));
			this.binCoverRatioPanel.setValue(defBinCoverRatio);

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
			this.add(new JLabel("Ang. sim. threshold ([0, 1])"), cLabel);
			this.add(this.angSimThPanel, cData);

			cLabel.gridy = 1;
			cData.gridy = 1;
			this.add(new JLabel("Min. bins coverage ratio ([0, 1])"), cLabel);
			this.add(this.binCoverRatioPanel, cData);
		}

		@Override
		public void freeze()
		{
			this.angSimThPanel.setEnabled(false);
			this.binCoverRatioPanel.setEnabled(false);
		}

		@Override
		public void reset()
		{
			this.angSimThPanel.setValue(defAngSimTh);
			this.angSimThPanel.setEnabled(true);
			this.binCoverRatioPanel.setValue(defBinCoverRatio);
			this.binCoverRatioPanel.setEnabled(true);
		}

		@Override
		public IterationChooser.Parameters parameters(final WellEstimatorParameters estPs)
		{
			return new IterationChooser.BestParabScore.Parameters(
					IterationChooser.chooser.BestParabScore,
					estPs,
					((Number) this.angSimThPanel.getValue()).doubleValue(),
					((Number) this.binCoverRatioPanel.getValue()).doubleValue());
		}
	}

	public static class BestMLEScorePanel extends IterationChooserParametersPanel
	{
		private static final long serialVersionUID = 1L;

		public BestMLEScorePanel()
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
		public Parameters parameters(WellEstimatorParameters estPs)
		{
			return new IterationChooser.BestMLEScore.Parameters(
					IterationChooser.chooser.BestMLEScore,
					estPs);
		}
	}

	public static class BestMLEDeltaPanel extends IterationChooserParametersPanel
	{
		private static final long serialVersionUID = 1L;

		public static final int defMinSlicePts= 5;

		protected JFormattedTextField minSlicePtsPanel;

		public BestMLEDeltaPanel()
		{
			this.minSlicePtsPanel = new JFormattedTextField(new DecimalFormat("0"));
			this.minSlicePtsPanel.setValue(defMinSlicePts);

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
			this.add(new JLabel("Min. pts. per ring"), cLabel);
			this.add(this.minSlicePtsPanel, cData);
		}

		@Override
		public void freeze()
		{
			this.minSlicePtsPanel.setEnabled(false);
		}

		@Override
		public void reset()
		{
			this.minSlicePtsPanel.setValue(defMinSlicePts);
			this.minSlicePtsPanel.setEnabled(true);
		}

		@Override
		public Parameters parameters(WellEstimatorParameters estPs)
		{
			return new IterationChooser.BestMLEDeltaScore.Parameters(
					IterationChooser.chooser.BestMLEDelta,
					estPs,
					((Number) this.minSlicePtsPanel.getValue()).intValue());
		}
	}

	public static class MaxDensPercPanel extends IterationChooserParametersPanel
	{
		private static final long serialVersionUID = 1L;

		public static final double defMaxDensPerc = 0.3;

		protected JFormattedTextField maxDensPercPanel;

		public MaxDensPercPanel()
		{
			this.maxDensPercPanel = new JFormattedTextField(new DecimalFormat("0.00"));
			this.maxDensPercPanel.setValue(defMaxDensPerc);

			this.setLayout(new GridBagLayout());
			this.initGui();
		}

		public void initGui()
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
			this.add(new JLabel("% of max. density [0,1]"), cLabel);
			this.add(this.maxDensPercPanel, cData);
		}

		@Override
		public void freeze()
		{
			this.maxDensPercPanel.setEnabled(false);
		}

		@Override
		public void reset()
		{
			this.maxDensPercPanel.setEnabled(true);
			this.maxDensPercPanel.setValue(defMaxDensPerc);
		}

		@Override
		public Parameters parameters(WellEstimatorParameters estPs)
		{
			return new IterationChooser.MaxDensPerc.Parameters(
					IterationChooser.chooser.MaxDensPerc,
					estPs,
					((Number) this.maxDensPercPanel.getValue()).doubleValue());
		}
	}

	public static class MinDensPanel extends IterationChooserParametersPanel
	{
		private static final long serialVersionUID = 1L;

		public MinDensPanel()
		{
			this.setLayout(new GridBagLayout());
			this.initGui();
		}

		public void initGui()
		{
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
		public Parameters parameters(WellEstimatorParameters estPs)
		{
			return new IterationChooser.MinDens.Parameters(
					IterationChooser.chooser.MinDens,
					estPs);
		}
	}
}
