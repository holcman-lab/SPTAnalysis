package fiji.plugin.SPTAnalysis.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import fiji.plugin.SPTAnalysis.DataController;
import fiji.plugin.SPTAnalysis.GUIController;
import fiji.plugin.SPTAnalysis.struct.MyPolygon;

public class TrajectoryFilteringPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private final DataController dctrl;
	@SuppressWarnings("unused")
	private final GUIController gctrl;

	protected JCheckBox numPtsBox;
	protected JFormattedTextField minNumPtsField;

	protected MyPolygon poly;

	protected JCheckBox regionBox;
	protected JButton updatePolyBut;

	public TrajectoryFilteringPanel(final DataController dctrl, final GUIController gctrl)
	{
		this.dctrl = dctrl;
		this.gctrl = gctrl;

		this.poly = this.dctrl.traj_region();

		this.minNumPtsField = new JFormattedTextField(new DecimalFormat("0"));
		this.minNumPtsField.setValue(dctrl.traj_min_size());
		this.minNumPtsField.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				dctrl.updateTrajFilteringSize(((Number) minNumPtsField.getValue()).intValue());
				gctrl.regenerateTrajsOverlay();
				gctrl.display();
			}
		});

		this.regionBox = new JCheckBox();
		this.regionBox.setSelected(this.poly != null);
		this.regionBox.addItemListener(new ItemListener()
		{
			//TODO: Add a button to show polygon ?
			@Override
			public void itemStateChanged(ItemEvent arg0)
			{
				if (regionBox.isSelected())
					dctrl.updateTrajFilteringRegion(poly);
				else
					dctrl.updateTrajFilteringRegion(null);
				gctrl.regenerateTrajsOverlay();
				gctrl.display();
			}
		});

		this.updatePolyBut = new JButton("go");
		updatePolyBut.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				poly = gctrl.getPolygon();
				if (regionBox.isSelected())
				{
					dctrl.updateTrajFilteringRegion(poly);
					gctrl.regenerateTrajsOverlay();
					gctrl.display();
				}
			}
		});

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

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(5, 10, 0, 10);

		cLabel.gridy = 0;
		cData.gridy = 0;
		this.add(new JLabel("Only trajs. with >= N points"), cLabel);
		this.add(this.minNumPtsField, cData);

		c.gridy = 1;
		this.add(new JSeparator(JSeparator.HORIZONTAL), c);

		cLabel.gridy = 2;
		cData.gridy = 2;
		this.add(new JLabel("Only trajs. in region"), cLabel);
		this.add(this.regionBox, cData);

		cLabel.gridy = 3;
		cData.gridy = 3;
		cLabel.insets = new Insets(10, 10, 10, 10);
		cData.insets = new Insets(10, 5, 10, 10);
		this.add(new JLabel("Update polygon"), cLabel);
		this.add(this.updatePolyBut, cData);
	}
}
