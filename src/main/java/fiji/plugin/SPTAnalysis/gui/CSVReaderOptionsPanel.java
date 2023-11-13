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
import javax.swing.JTextField;

import fiji.plugin.SPTAnalysis.readers.CSVReaderOptions;

public class CSVReaderOptionsPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	JTextField delimField;

	JFormattedTextField idPosField;
	JFormattedTextField tPosField;
	JFormattedTextField xPosField;
	JFormattedTextField yPosField;

	JCheckBox hasZField;
	JFormattedTextField zPosField;

	JFormattedTextField skipHeadLinesField;
	JCheckBox unitIsPxField;
	JFormattedTextField pxSizeField;

	JCheckBox unitIsFrameField;
	JFormattedTextField dtField;

	public CSVReaderOptionsPanel()
	{
		this.delimField = new JTextField("\\t");

		this.idPosField = new JFormattedTextField(new DecimalFormat("0"));
		this.idPosField.setValue(new Integer(0));

		this.tPosField = new JFormattedTextField(new DecimalFormat("0"));
		this.tPosField.setValue(new Integer(1));

		this.xPosField = new JFormattedTextField(new DecimalFormat("0"));
		this.xPosField.setValue(new Integer(2));
		this.yPosField = new JFormattedTextField(new DecimalFormat("0"));
		this.yPosField.setValue(new Integer(3));

		this.hasZField = new JCheckBox();

		this.zPosField = new JFormattedTextField(new DecimalFormat("0"));
		this.zPosField.setValue(new Integer(4));
		this.zPosField.setEnabled(false);

		this.skipHeadLinesField = new JFormattedTextField(new DecimalFormat("0"));
		this.skipHeadLinesField.setValue(new Integer(0));

		this.unitIsPxField = new JCheckBox();

		this.pxSizeField = new JFormattedTextField(new DecimalFormat("0.000"));
		this.pxSizeField.setValue(Double.NaN);
		this.pxSizeField.setEnabled(false);

		this.unitIsFrameField = new JCheckBox();
		
		this.dtField = new JFormattedTextField(new DecimalFormat("0.000"));
		this.dtField.setValue(new Double(0.0));
		this.dtField.setEnabled(false);

		this.hasZField.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				zPosField.setEnabled(hasZField.isSelected());
			}
		});

		this.unitIsPxField.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				pxSizeField.setEnabled(unitIsPxField.isSelected());
			}
		});

		this.unitIsFrameField.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				dtField.setEnabled(unitIsFrameField.isSelected());
			}
		});

		initGUI();
	}

	private void initGUI()
	{
		GridBagLayout layout = new GridBagLayout();
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
		cData.insets = new Insets(5, 5, 0, 0);

		cLabel.gridy = 0;
		cData.gridy = 0;
		this.add(new JLabel("Delimiter"), cLabel);
		this.add(this.delimField, cData);

		cLabel.gridy = 1;
		cData.gridy = 1;
		this.add(new JLabel("Traj. id column"), cLabel);
		this.add(this.idPosField, cData);

		cLabel.gridy = 2;
		cData.gridy = 2;
		this.add(new JLabel("Time column"), cLabel);
		this.add(this.tPosField, cData);

		cLabel.gridy = 3;
		cData.gridy = 3;
		this.add(new JLabel("X coord. column"), cLabel);
		this.add(this.xPosField, cData);

		cLabel.gridy = 4;
		cData.gridy = 4;
		this.add(new JLabel("Y coord. column"), cLabel);
		this.add(this.yPosField, cData);

		cLabel.gridy = 5;
		cData.gridy = 5;
		this.add(new JLabel("Has Z"), cLabel);
		this.add(this.hasZField, cData);

		cLabel.gridy = 6;
		cData.gridy = 6;
		this.add(new JLabel("Z column"), cLabel);
		this.add(this.zPosField, cData);

		cLabel.gridy = 7;
		cData.gridy = 7;
		this.add(new JLabel("Skip header line(s)"), cLabel);
		this.add(this.skipHeadLinesField, cData);

		cLabel.gridy = 8;
		cData.gridy = 8;
		this.add(new JLabel("Unit is pixel"), cLabel);
		this.add(this.unitIsPxField, cData);

		cLabel.gridy = 9;
		cData.gridy = 9;
		this.add(new JLabel("Pixel size (µm)"), cLabel);
		this.add(this.pxSizeField, cData);

		cLabel.gridy = 10;
		cData.gridy = 10;
		this.add(new JLabel("Unit is frame"), cLabel);
		this.add(this.unitIsFrameField, cData);

		cLabel.gridy = 11;
		cData.gridy = 11;
		this.add(new JLabel("Acquisition time (sec)"), cLabel);
		this.add(this.dtField, cData);
	}

	public CSVReaderOptions getOptions()
	{
		return new CSVReaderOptions(this.delimField.getText(),
				((Number) this.idPosField.getValue()).intValue(),
				((Number) this.tPosField.getValue()).intValue(),
				((Number) this.xPosField.getValue()).intValue(),
				((Number) this.yPosField.getValue()).intValue(),
				this.hasZField.isSelected(),
				((Number) this.zPosField.getValue()).intValue(),
				((Number) this.skipHeadLinesField.getValue()).intValue(),
				this.unitIsPxField.isSelected(),
				((Number) this.pxSizeField.getValue()).doubleValue(),
				this.unitIsFrameField.isSelected(),
				((Number) this.dtField.getValue()).doubleValue());
	}
}
