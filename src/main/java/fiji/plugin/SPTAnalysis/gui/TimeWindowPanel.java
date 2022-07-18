package fiji.plugin.SPTAnalysis.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;

import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TimeWindowPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private JCheckBox useTimeWindowField;
	private JFormattedTextField timeWindowDurationField;
	private JFormattedTextField timeWindowOverlapField;

	private GridBagLayout layout;

	public TimeWindowPanel()
	{
		this.useTimeWindowField = new JCheckBox();

		this.timeWindowDurationField = new JFormattedTextField(new DecimalFormat("0.000"));
		this.timeWindowDurationField.setValue(new Double(20));
		this.timeWindowDurationField.setEnabled(false);

		this.timeWindowOverlapField = new JFormattedTextField(new DecimalFormat("0.0"));
		this.timeWindowOverlapField.setValue(new Double(0.0));
		//this.timeWindowOverlapField.setFormat("%.1f");
		this.timeWindowOverlapField.setEnabled(false);

		this.useTimeWindowField.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				timeWindowDurationField.setEnabled(useTimeWindowField.isSelected());
				timeWindowOverlapField.setEnabled(useTimeWindowField.isSelected());
			}
		});

		initGUI();
	}

	private void initGUI()
	{
		this.layout = new GridBagLayout();
		setLayout(layout);

		this.setLayout(new GridBagLayout());
		GridBagConstraints cLabel = new GridBagConstraints();
		cLabel.gridx = 0;
		cLabel.gridy = 0;
		cLabel.weightx = 1;
		cLabel.anchor = GridBagConstraints.WEST;
		cLabel.insets = new Insets(5, 0, 0, 0);

		GridBagConstraints cData = new GridBagConstraints();
		cData.gridx = 1;
		cData.gridy = 0;
		cData.weightx = 0.5;
		cData.fill = GridBagConstraints.HORIZONTAL;
		cData.gridwidth = GridBagConstraints.REMAINDER;
		cData.insets = new Insets(5, 10, 0, 0);

		cLabel.gridy = 0;
		cData.gridy = 0;
		this.add(new JLabel("Use time-windows?"), cLabel);
		this.add(this.useTimeWindowField, cData);

		cLabel.gridy = 1;
		cData.gridy = 1;
		this.add(new JLabel("Time-windows duration (sec)"), cLabel);
		this.add(this.timeWindowDurationField, cData);

		cLabel.gridy = 2;
		cData.gridy = 2;
		this.add(new JLabel("Time-windows overlap [0-1]"), cLabel);
		this.add(this.timeWindowOverlapField, cData);
	}

	public boolean useTimeWindow()
	{
		return this.useTimeWindowField.isSelected();
	}

	public double timeWindowDuration()
	{
		return ((Number) this.timeWindowDurationField.getValue()).doubleValue();
	}

	public double timeWindowOverlap()
	{
		return ((Number) this.timeWindowOverlapField.getValue()).doubleValue();
	}
}
