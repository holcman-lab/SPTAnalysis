package fiji.plugin.SPTAnalysis.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

import fiji.plugin.SPTAnalysis.DataController;
import fiji.plugin.SPTAnalysis.GUIController;

public class MainPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private final GUIController gctrl;
	@SuppressWarnings("unused")
	private final DataController dctrl;

	private final SelectionPanel selPan;
	private final AnalysisResultSelectionPanel anaResPan;

	private GridBagLayout layout;

	public MainPanel(DataController dctrl, GUIController gctrl, JFrame frame)
	{
		this.gctrl = gctrl;
		this.dctrl = dctrl;

		this.selPan = new SelectionPanel(dctrl, gctrl);
		this.anaResPan = new AnalysisResultSelectionPanel(dctrl, gctrl);

		this.layout = new GridBagLayout();
		setLayout(layout);

		Border bl = BorderFactory.createLineBorder(Color.black);

		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 0;
			c.gridy = 0;
			this.selPan.setBorder(bl);
			this.add(this.selPan, c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			c.weightx = 1;
			c.weighty = 1;
			c.gridx = 1;
			c.gridy = 0;
			this.anaResPan.setBorder(bl);
			this.add(this.anaResPan, c);
		}
	}

	public AnalysisResultSelectionPanel analysisResultSelectionPanel()
	{
		return this.anaResPan;
	}

	public SelectionPanel selectionPanel()
	{
		return this.selPan;
	}
}
