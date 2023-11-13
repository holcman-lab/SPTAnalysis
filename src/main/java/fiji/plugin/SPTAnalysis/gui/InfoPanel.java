package fiji.plugin.SPTAnalysis.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import fiji.plugin.SPTAnalysis.DataController;
import fiji.plugin.SPTAnalysis.GUIController;
import fiji.plugin.SPTAnalysis.readers.CSVReaderOptions;

public class InfoPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private final DataController dctrl;

	public InfoPanel(final DataController dctrl)
	{
		this.dctrl = dctrl;

		this.setLayout(new GridBagLayout());
		initGui();
	}
	
	protected void initGui()
	{
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
		cData.insets = new Insets(10, 15, 0, 15);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(10, 0, 0, 0);

		cLabel.gridy = 0;
		cData.gridy = 0;
		this.add(new JLabel("File"), cLabel);
		String fname = "None";
		if (!dctrl.filename().isEmpty())
			fname = dctrl.filename();
		this.add(new JLabel(fname), cData);

		c.gridy = 1;
		this.add(new JSeparator(JSeparator.HORIZONTAL), c);

		cLabel.gridy = 2;
		this.add(GUIController.newBoldLabel("Time-windows"), cLabel);

		cLabel.gridy = 3;
		cData.gridy = 3;
		this.add(new JLabel("Duration (s)"), cLabel);
		this.add(new JLabel(String.valueOf(dctrl.timeWindows().duration())), cData);

		cLabel.gridy = 4;
		cData.gridy = 4;
		this.add(new JLabel("Overlap ([0, 1])"), cLabel);
		this.add(new JLabel(String.valueOf(dctrl.timeWindows().overlap())), cData);

		c.gridy = 5;
		this.add(new JSeparator(JSeparator.HORIZONTAL), c);

		cLabel.gridy = 6;
		this.add(GUIController.newBoldLabel("Acquisition setup"), cLabel);

		cLabel.gridy = 7;
		cData.gridy = 7;
		this.add(new JLabel("Pixel Size (μm)"), cLabel);
		if (Double.isNaN(this.dctrl.pxsize()))
			this.add(new JLabel("?"), cData);
		else
			this.add(new JLabel(String.format("%g", this.dctrl.pxsize())), cData);

		cLabel.gridy = 8;
		cData.gridy = 8;
		this.add(new JLabel("Acquisition time (s)"), cLabel);
		this.add(new JLabel(String.format("%g", dctrl.trajs().wins.get(0).acqDT())), cData);

		c.gridy = 9;
		this.add(new JLabel(""), c);
	}
}
