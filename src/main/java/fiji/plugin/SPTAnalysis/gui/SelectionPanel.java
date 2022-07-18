package fiji.plugin.SPTAnalysis.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.scijava.widget.FileWidget;

import fiji.plugin.SPTAnalysis.DataController;
import fiji.plugin.SPTAnalysis.GUIController;

public class SelectionPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private final DataController dctrl;
	@SuppressWarnings("unused")
	private final GUIController gctrl;

	private GridBagLayout layout;

	private final JFrame infoFrame;
	private final InfoPanel infoPan;

	private final JFrame trajFiltFrame;
	private final TrajectoryFilteringPanel trajFiltPan;
	
	private final JFrame wellDetectionFrame;
	private final WellDetectionParametersSelectionPanel wellDetectionPan;

	private final JFrame graphConstructionFrame;
	private final GraphConstructionParametersSelectionPanel graphConstructionPan;

	private final JFrame displayFrame;
	private final DisplayPanel displayPan;

	private final JFrame saveFrame;
	private final SavePanel savePan;

	private JButton butInfoPan;
	private JButton butTrajFiltPan;
	private JButton butwellPan;
	private JButton butGraphPan;
	private JButton butDisplayPan;
	private JButton butExportPan;
	private JButton butSavePan;

	public SelectionPanel(DataController dctrl, GUIController gctrl)
	{
		this.dctrl = dctrl;
		this.gctrl = gctrl;

		this.infoFrame = new JFrame();
		this.infoFrame.setTitle("Trajectories info.");
		this.infoFrame.setSize(500, 300);
		this.infoFrame.setVisible(false);

		this.infoPan = new InfoPanel(dctrl);
		this.infoFrame.add(new JScrollPane(this.infoPan));
		this.infoFrame.pack();

		this.trajFiltFrame = new JFrame();
		this.trajFiltFrame.setTitle("Trajectories filtering");
		this.trajFiltFrame.setSize(500, 300);
		this.trajFiltFrame.setVisible(false);

		this.trajFiltPan = new TrajectoryFilteringPanel(dctrl, gctrl);
		this.trajFiltFrame.add(new JScrollPane(this.trajFiltPan));
		this.trajFiltFrame.pack();


		this.wellDetectionFrame = new JFrame();
		this.wellDetectionFrame.setTitle("Well detection");
		this.wellDetectionFrame.setSize(500, 300);
		this.wellDetectionFrame.setVisible(false);

		this.wellDetectionPan = new WellDetectionParametersSelectionPanel(wellDetectionFrame, dctrl, gctrl);
		this.wellDetectionFrame.add(new JScrollPane(this.wellDetectionPan));
		this.wellDetectionFrame.pack();


		this.graphConstructionFrame = new JFrame();
		this.graphConstructionFrame.setTitle("Graph detection");
		this.graphConstructionFrame.setSize(500, 300);
		this.graphConstructionFrame.setVisible(false);

		this.graphConstructionPan = new GraphConstructionParametersSelectionPanel(
				this.graphConstructionFrame, dctrl, gctrl);
		this.graphConstructionFrame.add(new JScrollPane(this.graphConstructionPan));
		this.graphConstructionFrame.pack();


		this.displayFrame = new JFrame();
		this.displayFrame.setTitle("Display options");
		this.displayFrame.setSize(500, 300);
		this.displayFrame.setVisible(false);

		this.displayPan = new DisplayPanel(dctrl, gctrl);
		this.displayFrame.add(new JScrollPane(this.displayPan));
		this.displayFrame.pack();


		this.saveFrame = new JFrame();
		this.saveFrame.setTitle("Export");
		this.saveFrame.setSize(500, 300);
		this.saveFrame.setVisible(false);

		this.savePan = new SavePanel(saveFrame, dctrl, gctrl);
		this.saveFrame.add(new JScrollPane(this.savePan));
		this.saveFrame.pack();

		this.butInfoPan = new JButton("Info");
		this.butInfoPan.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				infoFrame.setVisible(!infoFrame.isVisible());
			}
		});

		this.butTrajFiltPan = new JButton("Traj. filters");
		this.butTrajFiltPan.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				trajFiltFrame.setVisible(!trajFiltFrame.isVisible());
			}
		});

		this.butwellPan = new JButton("Well detection");
		this.butwellPan.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				wellDetectionFrame.setVisible(!wellDetectionFrame.isVisible());
				if (wellDetectionFrame.isVisible())
					wellDetectionPan.reset();
			}
		});

		this.butGraphPan = new JButton("Graph construction");
		this.butGraphPan.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				graphConstructionFrame.setVisible(!graphConstructionFrame.isVisible());
				if (graphConstructionFrame.isVisible())
					graphConstructionPan.reset();
			}
		});

		this.butDisplayPan = new JButton("Display options");
		this.butDisplayPan.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				displayFrame.setVisible(!displayFrame.isVisible());
			}
		});

		this.butExportPan = new JButton("Export");
		this.butExportPan.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				saveFrame.setVisible(!saveFrame.isVisible());
			}
		});

		this.butSavePan = new JButton("Save plugin");
		this.butSavePan.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				File selFile = gctrl.UIServ().chooseFile(null, FileWidget.SAVE_STYLE);
				if (selFile == null)
					return;

				dctrl.marshall(selFile.getAbsolutePath());
			}
		});

		this.initGUI();
	}

	protected void initGUI()
	{
		this.layout = new GridBagLayout();
		setLayout(layout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridx = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(5, 0, 0, 0);

		c.gridy = 0;
		this.add(this.butInfoPan, c);

		c.gridy = 1;
		this.add(this.butTrajFiltPan, c);

		c.gridy = 2;
		this.add(this.butwellPan, c);

		c.gridy = 3;
		this.add(this.butGraphPan, c);

		c.gridy = 4;
		this.add(this.butDisplayPan, c);

		c.gridy = 5;
		this.add(this.butExportPan, c);

		c.gridy = 6;
		this.add(this.butSavePan, c);
	}

	public WellDetectionParametersSelectionPanel wellDetectionParametersSelectionPanel()
	{
		return this.wellDetectionPan;
	}

	public GraphConstructionParametersSelectionPanel graphConstructionParametersSelectionPanel()
	{
		return this.graphConstructionPan;
	}

	public DisplayPanel displayPanel()
	{
		return this.displayPan;
	}

	public SavePanel savePanel()
	{
		return this.savePan;
	}
}
