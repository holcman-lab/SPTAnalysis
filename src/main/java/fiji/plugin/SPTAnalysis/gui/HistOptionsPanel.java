package fiji.plugin.SPTAnalysis.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;

import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

public class HistOptionsPanel extends JPanel
{
	public class HistOptions
	{
		public final boolean mergeWins;
		public final boolean restrictToReg;
		public final boolean autoBins;
		public final boolean fixedBins;
		public final int nBins;
		public final boolean rangeBins;
		public final double minBin;
		public final double maxBin;
		public final double stepBin;

		public HistOptions(boolean mergeWins, boolean restrictToReg, boolean autoBins, boolean fixedBins,
				int nBins, boolean rangeBins, double minBin, double maxBin, double stepBin)
		{
			this.mergeWins = mergeWins;
			this.restrictToReg = restrictToReg;
			this.autoBins = autoBins;
			this.fixedBins = fixedBins;
			this.nBins = nBins;
			this.rangeBins = rangeBins;
			this.minBin = minBin;
			this.maxBin = maxBin;
			this.stepBin = stepBin;
		}
	}

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private final HistogramPanel histPan;

	private final JCheckBox timeWindowBox;
	private final JCheckBox regBox;

	private final JCheckBox autoBinsBox;

	private final JCheckBox nBinsBox;
	private final JFormattedTextField nBinsPanel;

	private final JCheckBox rangeBinsBox;
	private final JFormattedTextField minBinPanel;
	private final JFormattedTextField maxBinPanel;
	private final JFormattedTextField stepBinPanel;

	public HistOptions generateOpts()
	{
		return new HistOptions(this.timeWindowBox.isSelected(),
							   this.regBox.isSelected(),
							   this.autoBinsBox.isSelected(),
							   this.nBinsBox.isSelected(),
							   ((Number) this.nBinsPanel.getValue()).intValue(),
							   this.rangeBinsBox.isSelected(),
							   ((Number) this.minBinPanel.getValue()).doubleValue(),
							   ((Number) this.maxBinPanel.getValue()).doubleValue(),
							   ((Number) this.stepBinPanel.getValue()).doubleValue());
	}

	public HistOptionsPanel(final HistogramPanel histPan)
	{
		this.histPan = histPan;

		this.timeWindowBox = new JCheckBox("Flatten time windows", false);
		this.timeWindowBox.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent arg0)
			{
				histPan.updateHist(generateOpts());
			}
		});

		this.regBox = new JCheckBox("Restrict to region", false);
		this.regBox.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent arg0)
			{
				histPan.updateHist(generateOpts());
			}
		});

		this.autoBinsBox = new JCheckBox("Automatic (sqrt(n))", true);
		this.autoBinsBox.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent arg0)
			{
				if (arg0.getStateChange() == ItemEvent.DESELECTED &&
						!nBinsBox.isSelected() && !rangeBinsBox.isSelected())
					autoBinsBox.setSelected(true);
				else if (arg0.getStateChange() == ItemEvent.SELECTED)
				{
					nBinsBox.setSelected(false);
					rangeBinsBox.setSelected(false);
					nBinsPanel.setEnabled(false);
					minBinPanel.setEnabled(false);
					maxBinPanel.setEnabled(false);
					stepBinPanel.setEnabled(false);
					histPan.updateHist(generateOpts());
				}
			}
		});

		this.nBinsBox = new JCheckBox("Fixed number", false);
		this.nBinsBox.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent arg0)
			{
				if (arg0.getStateChange() == ItemEvent.DESELECTED &&
						!autoBinsBox.isSelected() && !rangeBinsBox.isSelected())
					nBinsBox.setSelected(true);
				else if (arg0.getStateChange() == ItemEvent.SELECTED)
				{
					autoBinsBox.setSelected(false);
					rangeBinsBox.setSelected(false);
					nBinsPanel.setEnabled(true);
					minBinPanel.setEnabled(false);
					maxBinPanel.setEnabled(false);
					stepBinPanel.setEnabled(false);
					histPan.updateHist(generateOpts());
				}
			}
		});

		this.nBinsPanel = new JFormattedTextField(new DecimalFormat("0"));
		this.nBinsPanel.setValue(new Integer(100));
		this.nBinsPanel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				histPan.updateHist(generateOpts());
			}
		});

		this.rangeBinsBox = new JCheckBox("Range", false);
		this.rangeBinsBox.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent arg0)
			{
				if (arg0.getStateChange() == ItemEvent.DESELECTED &&
						!autoBinsBox.isSelected() && !nBinsBox.isSelected())
					rangeBinsBox.setSelected(true);
				else if (arg0.getStateChange() == ItemEvent.SELECTED)
				{
					autoBinsBox.setSelected(false);
					nBinsBox.setSelected(false);
					nBinsPanel.setEnabled(false);
					minBinPanel.setEnabled(true);
					maxBinPanel.setEnabled(true);
					stepBinPanel.setEnabled(true);
					histPan.updateHist(generateOpts());
				}
			}
		});
		this.minBinPanel = new JFormattedTextField(new DecimalFormat("0.000"));
		this.minBinPanel.setValue(new Double(0.0));
		this.minBinPanel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				histPan.updateHist(generateOpts());
			}
		});
		this.maxBinPanel = new JFormattedTextField(new DecimalFormat("0.000"));
		this.maxBinPanel.setValue(new Double(1.0));
		this.maxBinPanel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				histPan.updateHist(generateOpts());
			}
		});
		this.stepBinPanel = new JFormattedTextField(new DecimalFormat("0.000"));
		this.stepBinPanel.setValue(new Double(0.1));
		this.stepBinPanel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				histPan.updateHist(generateOpts());
			}
		});

		nBinsPanel.setEnabled(false);
		minBinPanel.setEnabled(false);
		maxBinPanel.setEnabled(false);
		stepBinPanel.setEnabled(false);

		this.initGui();
	}

	protected void initGui()
	{
		this.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridwidth = 7;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(10, 10, 0, 10);

		c.gridy = 1;
		this.add(this.timeWindowBox, c);

		c.gridy = 2;
		this.add(this.regBox, c);

		c.gridy = 3;
		c.gridwidth = GridBagConstraints.REMAINDER;
		add(new JSeparator(JSeparator.HORIZONTAL), c);

		c.gridy = 4;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		this.add(new JLabel("Binning:"), c);
		
		c.gridy = 5;
		this.add(this.autoBinsBox, c);

		c.gridy = 6;
		this.add(this.nBinsBox, c);
		c.gridx = 1;
		this.nBinsPanel.setPreferredSize(new Dimension(60, 30));
		this.add(this.nBinsPanel, c);

		c.gridy = 7;
		c.gridx = 0;
		c.anchor = GridBagConstraints.WEST;
		this.add(this.rangeBinsBox, c);

		c.gridx = 1;
		c.insets = new Insets(10, 10, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		this.add(new JLabel("Min:"), c);
		c.gridx = 2;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(10, 5, 0, 0);
		this.minBinPanel.setPreferredSize(new Dimension(80, 30));
		this.add(this.minBinPanel, c);

		c.gridx = 3;
		c.insets = new Insets(10, 10, 0, 0);
		this.add(new JLabel("Max:"), c);
		c.gridx = 4;
		c.insets = new Insets(10, 5, 0, 0);
		this.maxBinPanel.setPreferredSize(new Dimension(80, 30));
		this.add(this.maxBinPanel, c);

		c.gridx = 5;
		c.insets = new Insets(10, 10, 10, 0);
		this.add(new JLabel("Step:"), c);
		c.gridx = 6;
		c.insets = new Insets(10, 5, 10, 10);
		this.stepBinPanel.setPreferredSize(new Dimension(80, 30));
		this.add(this.stepBinPanel, c);
	}
}
