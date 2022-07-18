package fiji.plugin.SPTAnalysis.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fiji.plugin.SPTAnalysis.struct.Freezable;

public class WellLinkerPanel extends JPanel implements Freezable
{
	private static final long serialVersionUID = 1L;

	public static final double defMaxDist = 0.1;
	public static final int defMaxFrameGap = 1;
	
	private JFormattedTextField maxDistPanel;
	private JFormattedTextField maxFrameGapPanel;

	public WellLinkerPanel()
	{
		this.maxDistPanel = new JFormattedTextField(new DecimalFormat("0.000"));
		this.maxDistPanel.setValue(defMaxDist);
		this.maxFrameGapPanel = new JFormattedTextField(new DecimalFormat("0"));
		this.maxFrameGapPanel.setValue(defMaxFrameGap);

		initGUI();
	}

	public double maxDist()
	{
		return ((Number) this.maxDistPanel.getValue()).doubleValue();
	}

	public int maxFrameGap()
	{
		return ((Number) this.maxFrameGapPanel.getValue()).intValue();
	}

	public void initGUI()
	{
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);

		GridBagConstraints cLabel = new GridBagConstraints();
		cLabel.fill = GridBagConstraints.HORIZONTAL;
		cLabel.weightx = 0.5;
		cLabel.insets = new Insets(10, 10, 0, 0);
		GridBagConstraints cData = new GridBagConstraints();
		cData.fill = GridBagConstraints.HORIZONTAL;
		cData.weightx = 0.5;
		cData.gridwidth = GridBagConstraints.REMAINDER;
		cData.insets = new Insets(10, 5, 0, 10);

		cLabel.gridy = 0;
		cData.gridy = 0;
		this.add(new JLabel("Max. linking distance (µm)"), cLabel);
		this.add(this.maxDistPanel, cData);

		cLabel.gridy = 1;
		cData.gridy = 1;
		this.add(new JLabel("Max. windows gap"), cLabel);
		this.add(this.maxFrameGapPanel, cData);
	}

	@Override
	public void freeze()
	{
		this.maxDistPanel.setEnabled(false);
		this.maxFrameGapPanel.setEnabled(false);
	}

	@Override
	public void reset()
	{
		this.maxDistPanel.setValue(defMaxDist);
		this.maxDistPanel.setEnabled(true);
		this.maxFrameGapPanel.setValue(defMaxFrameGap);
		this.maxFrameGapPanel.setEnabled(true);
	}
}
