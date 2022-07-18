package fiji.plugin.SPTAnalysis.gui;

import java.awt.Choice;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import fiji.plugin.SPTAnalysis.DataController;
import fiji.plugin.SPTAnalysis.GUIController;
import fiji.plugin.SPTAnalysis.struct.Rectangle;

public class SavePanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private final JFrame frame;

	@SuppressWarnings("unused")
	private final DataController dcntrl;
	@SuppressWarnings("unused")
	private final GUIController gcntrl;

	private GridBagLayout layout;

	private final SaveCSVPanel CSVPan;
	private final SaveSVGPanel SVGPan;

	private String selected;
	private final Choice saveChoice;

	
	public static void saveSelection(final File selDir, final Rectangle sel)
	{
		BufferedWriter writer;
		try
		{
			writer = new BufferedWriter(new FileWriter(selDir.getAbsolutePath() + "/region.csv"));
			writer.write(String.format("lowerleft %.3f %.3f\n", sel.ll()[0], sel.ll()[1]));
			writer.write(String.format("topright %.3f %.3f\n", sel.tr()[0], sel.tr()[1]));
			writer.close();
		}
		catch (IOException e2)
		{
			e2.printStackTrace();
		}
	}

	public SavePanel(JFrame frame, DataController dcntrl, GUIController gcntrl)
	{
		this.frame = frame;
		this.dcntrl = dcntrl;
		this.gcntrl = gcntrl;

		this.CSVPan = new SaveCSVPanel(dcntrl, gcntrl);
		this.SVGPan = new SaveSVGPanel(dcntrl, gcntrl);

		this.selected = "SVG";
		this.saveChoice = new Choice();
		this.saveChoice.add("SVG");
		this.saveChoice.add("CSV");
		this.saveChoice.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				if (selected.equals("SVG"))
					remove(SVGPan);
				else
					remove(CSVPan);
				selected = saveChoice.getSelectedItem();

				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridwidth = GridBagConstraints.REMAINDER;
				c.weightx = 1;
				c.gridx = 0;
				c.gridy = 0;
				c.insets = new Insets(0, 10, 10, 0);

				if (selected.equals("SVG"))
					add(SVGPan);
				else
					add(CSVPan);

				revalidate();
				repaint();
				frame.pack();
			}
		});


		initGUI();
	}

	public void initGUI()
	{
		this.layout = new GridBagLayout();
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

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1;
		c.gridx = 0;
		c.insets = new Insets(10, 10, 10, 10);

		c.gridy = 0;
		this.add(this.saveChoice, c);

		c.gridy = 1;
		this.add(this.SVGPan, c);
	}
}
