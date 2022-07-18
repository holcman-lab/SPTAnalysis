package fiji.plugin.SPTAnalysis.gui;

import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JButton;
import javax.swing.JCheckBox;


import org.scijava.ui.UIService;
import fiji.plugin.SPTAnalysis.DataController;
import fiji.plugin.SPTAnalysis.GUIController;
import fiji.plugin.SPTAnalysis.struct.MapParameters;
import fiji.plugin.SPTAnalysis.struct.ScalarMap;
import fiji.plugin.SPTAnalysis.struct.TrajectoriesColorScheme;


public class DisplayPanel  extends JPanel
{
	private static final long serialVersionUID = 1L;

	public static final boolean DEFAULT_DENS_LOG = false;

	public static final String DEFAULT_TRAJ_MAX_INST_VEL = "inf";
	public static final Double DEFAULT_DENS_BIN_W = 0.2;
	public static final Double DEFAULT_DIFF_BIN_W = 0.2;
	public static final Integer DEFAULT_DIFF_MIN_NPTS = 5;
	public static final Double DEFAULT_DIFF_FILTER_SIZE = 0.2;
	public static final Double DEFAULT_DRIFT_BIN_W = 0.2;
	public static final Integer DEFAULT_DRIFT_MIN_NPTS = 5;
	public static final Double DEFAULT_DRIFT_FILTER_SIZE = 0.2;

	public static final Double DEFAULT_SCALEBAR_LENGTH = 5.0; //µm


	private final GUIController gCntrl;
	@SuppressWarnings("unused")
	private final DataController dCntrl;
	@SuppressWarnings("unused")
	private final UIService UIServ;

	private Choice trajectoryColor;
	private Checkbox logDens;

	private final JFormattedTextField trajMaxInstVelPanel;
	private final JFormattedTextField densBinWidthPanel;
	private final JFormattedTextField diffBinWidthPanel;
	private final JFormattedTextField diffNptsThPanel;
	private final JFormattedTextField driftBinWidthPanel;
	private final JFormattedTextField driftNptsThPanel;
	private final JFormattedTextField driftSizeMultPanel;

	private final Checkbox trajsDispPan;
	private final JButton trajsDispHist;
	private final Checkbox densDispPan;
	private final JButton densDispHist;
	private final Checkbox diffDispPan;
	private final JButton diffDispHist;
	private final Checkbox driftDispPan;

	private Choice DensFilterSizeChoice;

	private JCheckBox mergeWindows;
	private JCheckBox colorByFamily;
	private JCheckBox filteredDiffBox;
	private JCheckBox filteredDriftBox;

	private final JFormattedTextField diffFilterSizePan;
	private final JFormattedTextField driftFilterSizePan;

	private JFormattedTextField scaleBarLength;

	private GridBagLayout layout;

	public DisplayPanel(DataController dataCntrl, GUIController GUICntrl)
	{
		this.gCntrl = GUICntrl;
		this.dCntrl = dataCntrl;
		this.UIServ = GUICntrl.UIServ();

		this.trajectoryColor = new Choice();
		for (TrajectoriesColorScheme.ColoringType trCol: TrajectoriesColorScheme.ColoringType.values())
			this.trajectoryColor.add(trCol.name());
		this.trajectoryColor.select(TrajectoriesColorScheme.ColoringType.Random.name());
		this.trajectoryColor.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				gCntrl.regenerateTrajsOverlay();
				gCntrl.display();
			}
		});

		this.trajsDispPan = new Checkbox("Trajectories", true);
		this.trajsDispPan.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent arg0)
			{
				gCntrl.display();
			}
		});

		this.trajsDispHist = new JButton("Hist");
		this.trajsDispHist.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				gCntrl.displayInstVelHist();
			}
		});

		this.densDispPan = new Checkbox("Density", false);
		this.densDispPan.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent arg0)
			{
				gCntrl.display();
			}
		});

		this.densDispHist = new JButton("Hist");
		this.densDispHist.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				gCntrl.displayDensHist(getDensityParams());
			}
		});

		this.diffDispPan = new Checkbox("Diffusion", false);
		this.diffDispPan.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent arg0)
			{
				gCntrl.display();
			}
		});

		this.diffDispHist = new JButton("Hist");
		this.diffDispHist.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				gCntrl.displayDiffHist(getDiffusionParams());
			}
		});

		this.driftDispPan = new Checkbox("Drift", false);
		this.driftDispPan.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent arg0)
			{
				gCntrl.display();
			}
		});

		this.DensFilterSizeChoice = new Choice();
		this.DensFilterSizeChoice.add("0");
		this.DensFilterSizeChoice.add("3");
		this.DensFilterSizeChoice.add("5");
		this.DensFilterSizeChoice.add("7");
		this.DensFilterSizeChoice.add("9");
		this.DensFilterSizeChoice.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				gCntrl.display();
			}
		});

		this.logDens = new Checkbox("", DEFAULT_DENS_LOG);
		this.logDens.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				gCntrl.display();
			}
		});

		this.trajMaxInstVelPanel = new JFormattedTextField();
		this.trajMaxInstVelPanel.setValue(DEFAULT_TRAJ_MAX_INST_VEL);
		this.trajMaxInstVelPanel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				gCntrl.regenerateTrajsOverlay();
				gCntrl.display();
			}
		});


		this.densBinWidthPanel = new JFormattedTextField(new DecimalFormat("0.000"));
		this.densBinWidthPanel.setValue(DEFAULT_DENS_BIN_W);
		this.densBinWidthPanel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				gCntrl.display();
			}
		});

		this.diffBinWidthPanel = new JFormattedTextField(new DecimalFormat("0.000"));
		this.diffBinWidthPanel.setValue(DEFAULT_DIFF_BIN_W);
		this.diffBinWidthPanel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				gCntrl.display();
			}
		});

		this.diffNptsThPanel = new JFormattedTextField(new DecimalFormat("0"));
		this.diffNptsThPanel.setValue(DEFAULT_DIFF_MIN_NPTS);
		this.diffNptsThPanel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				gCntrl.display();
			}
		});

		this.filteredDiffBox = new JCheckBox();
		this.filteredDiffBox.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				gCntrl.display();
			}
		});

		this.filteredDriftBox = new JCheckBox();
		this.filteredDriftBox.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				gCntrl.display();
			}
		});

		this.diffFilterSizePan = new JFormattedTextField(new DecimalFormat("0.000"));
		this.diffFilterSizePan.setValue(DEFAULT_DIFF_FILTER_SIZE);
		this.diffFilterSizePan.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				gCntrl.display();
			}
		});

		this.driftFilterSizePan = new JFormattedTextField(new DecimalFormat("0.000"));
		this.driftFilterSizePan.setValue(DEFAULT_DRIFT_FILTER_SIZE);
		this.driftFilterSizePan.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				gCntrl.display();
			}
		});

		this.driftBinWidthPanel = new JFormattedTextField(new DecimalFormat("0.000"));
		this.driftBinWidthPanel.setValue(DEFAULT_DRIFT_BIN_W);
		this.driftBinWidthPanel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				gCntrl.display();
			}
		});

		this.driftNptsThPanel = new JFormattedTextField(new DecimalFormat("0"));
		this.driftNptsThPanel.setValue(DEFAULT_DRIFT_MIN_NPTS);
		this.driftNptsThPanel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				gCntrl.display();
			}
		});

		this.driftSizeMultPanel = new JFormattedTextField(new DecimalFormat("0.00"));
		this.driftSizeMultPanel.setValue(new Double(0.2));
		this.driftSizeMultPanel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				gCntrl.display();
			}
		});

		this.mergeWindows = new JCheckBox();
		this.mergeWindows.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				gCntrl.regenerateTrajsOverlay();
				gCntrl.display();
			}
		});

		this.colorByFamily = new JCheckBox();
		this.colorByFamily.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				gCntrl.display();
			}
		});

		this.scaleBarLength = new JFormattedTextField(new DecimalFormat("0.000"));
		this.scaleBarLength.setValue(DEFAULT_SCALEBAR_LENGTH);
		this.scaleBarLength.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				gCntrl.display();
			}
		});


		this.initGUI();
	}

	public TrajectoriesColorScheme.ColoringType trajectoryColor()
	{
		for (TrajectoriesColorScheme.ColoringType trCol: TrajectoriesColorScheme.ColoringType.values())
			if (trCol.name().equals(this.trajectoryColor.getSelectedItem()))
				return trCol;
		return null;
	}

	public boolean mergeWindows()
	{
		return this.mergeWindows.isSelected();
	}
	
	public double trajMaxInstVel()
	{
		if (((String) this.trajMaxInstVelPanel.getValue()).equals("inf"))
			return Double.POSITIVE_INFINITY;
		else
			return Double.parseDouble((String) this.trajMaxInstVelPanel.getValue());
	}

	public boolean colorByFamily()
	{
		return this.colorByFamily.isSelected();
	}

	public void updateGUI()
	{
		this.removeAll();
		this.initGUI();
	}

	public ArrayList<String> toDisplay()
	{
		ArrayList<String> res = new ArrayList<> ();

		if (this.densDispPan .getState())
			res.add("Density");
		if (this.diffDispPan .getState())
			res.add("Diffusion");
		if (this.trajsDispPan.getState())
			res.add("Trajectories");
		if (this.driftDispPan.getState())
			res.add("Drift");

		return res;
	}

	protected void initGUI()
	{
		this.layout = new GridBagLayout();
		setLayout(layout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.insets = new Insets(5, 10, 0, 10);

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
		cData.insets = new Insets(10, 15, 0, 10);

		cLabel.gridy = 0;
		this.add(new JLabel("Show"), cLabel);

		cLabel.gridy = 1;
		this.add(this.trajsDispPan, cLabel);
		cData.gridy = 1;
		this.add(this.trajsDispHist, cData);

		cLabel.gridy = 2;
		this.add(this.densDispPan, cLabel);
		cData.gridy = 2;
		this.add(this.densDispHist, cData);

		cLabel.gridy = 3;
		this.add(this.diffDispPan, cLabel);
		cData.gridy = 3;
		this.add(this.diffDispHist, cData);

		cLabel.gridy = 4;
		this.add(this.driftDispPan, cLabel);

		c.gridy = 5;
		this.add(new JSeparator(JSeparator.HORIZONTAL), c);

		cLabel.gridy = 6;
		this.add(new JLabel("Trajectory Options"), cLabel);

		cLabel.gridy = 7;
		cData.gridy = 7;
		this.add(new JLabel("Color"), cLabel);
		this.add(this.trajectoryColor, cData);

		cLabel.gridy = 8;
		cData.gridy = 8;
		this.add(new JLabel("Max. inst. vel. (µm/s)"), cLabel);
		this.add(this.trajMaxInstVelPanel, cData);



		c.gridy = 9;
		this.add(new JSeparator(JSeparator.HORIZONTAL), c);

		cLabel.gridy = 10;
		this.add(new JLabel("Density Map Options"), cLabel);

		cLabel.gridy = 11;
		cData.gridy = 11;
		this.add(new JLabel("Density bins width (µm)"), cLabel);
		this.add(this.densBinWidthPanel, cData);

		cLabel.gridy = 12;
		cData.gridy = 12;
		this.add(new JLabel("Log density"), cLabel);
		this.add(this.logDens, cData);

		cLabel.gridy = 13;
		cData.gridy = 13;
		this.add(new JLabel("Filter size (NxN)"), cLabel);
		this.add(this.DensFilterSizeChoice, cData);


		c.gridy = 14;
		this.add(new JSeparator(JSeparator.HORIZONTAL), c);

		cLabel.gridy = 15;
		this.add(new JLabel("Diffusion map options"), cLabel);

		cLabel.gridy = 16;
		cData.gridy = 16;
		this.add(new JLabel("Diffusion bins width (µm)"), cLabel);
		this.add(this.diffBinWidthPanel, cData);

		cLabel.gridy = 17;
		cData.gridy = 17;
		this.add(new JLabel("Diffusion min. num. pts."), cLabel);
		this.add(this.diffNptsThPanel, cData);

		cLabel.gridy = 18;
		cData.gridy = 18;
		this.add(new JLabel("Apply cosine filter"), cLabel);
		this.add(this.filteredDiffBox, cData);

		cLabel.gridy = 19;
		cData.gridy = 19;
		this.add(new JLabel("Cosine filter radius (µm)"), cLabel);
		this.add(this.diffFilterSizePan, cData);

		c.gridy = 20;
		this.add(new JSeparator(JSeparator.HORIZONTAL), c);

		cLabel.gridy = 21;
		this.add(new JLabel("Drift map options"), cLabel);

		cLabel.gridy = 22;
		cData.gridy = 22;
		this.add(new JLabel("Drift bins width (μm)"), cLabel);
		this.add(this.driftBinWidthPanel, cData);

		cLabel.gridy = 23;
		cData.gridy = 23;
		this.add(new JLabel("Drift min. num. pts."), cLabel);
		this.add(this.driftNptsThPanel, cData);
		
		cLabel.gridy = 24;
		cData.gridy = 24;
		this.add(new JLabel("Apply cosine filter"), cLabel);
		this.add(this.filteredDriftBox, cData);

		cLabel.gridy = 25;
		cData.gridy = 25;
		this.add(new JLabel("Cosine filter radius (μm)"), cLabel);
		this.add(this.driftFilterSizePan, cData);

		cLabel.gridy = 26;
		cData.gridy = 26;
		this.add(new JLabel("Size multiplier"), cLabel);
		this.add(this.driftSizeMultPanel, cData);


		c.gridy = 27;
		this.add(new JSeparator(JSeparator.HORIZONTAL), c);

		cLabel.gridy = 28;
		this.add(new JLabel("Time-windows options"), cLabel);

		cLabel.gridy = 29;
		cData.gridy = 29;
		this.add(new JLabel("Merge windows"), cLabel);
		this.add(this.mergeWindows, cData);

		c.gridy = 30;
		this.add(new JSeparator(JSeparator.HORIZONTAL), c);

		cLabel.gridy = 31;
		this.add(new JLabel("Potential wells options"), cLabel);

		cLabel.gridy = 32;
		cData.gridy = 32;
		this.add(new JLabel("Color by family"), cLabel);
		this.add(this.colorByFamily, cData);

		c.gridy = 33;
		this.add(new JSeparator(JSeparator.HORIZONTAL), c);

		cLabel.gridy = 34;
		this.add(new JLabel("Scale bar options"), cLabel);

		cLabel.gridy = 35;
		cData.gridy = 35;
		cLabel.insets = new Insets(10, 15, 10, 10);
		cData.insets = new Insets(10, 15, 10, 10);
		this.add(new JLabel("Length (µm)"), cLabel);
		this.add(this.scaleBarLength, cData);
	}

	public ScalarMap.DensityOption logDens()
	{
		if (this.logDens.getState())
			return ScalarMap.DensityOption.LOGDENS;
		else
			return ScalarMap.DensityOption.DENS;
	}

	public double scaleBarLength()
	{
		return ((Number) this.scaleBarLength.getValue()).doubleValue();
	}

	public double dxDens()
	{
		return ((Number) this.densBinWidthPanel.getValue()).doubleValue();
	}

	public MapParameters.DensityParameters getDensityParams()
	{
		return new MapParameters.DensityParameters(this.dxDens(), this.logDens(),
				Integer.parseInt(this.DensFilterSizeChoice.getSelectedItem()));
	}

	public MapParameters.DiffusionParameters getDiffusionParams()
	{
		return new MapParameters.DiffusionParameters(((Number) this.diffBinWidthPanel.getValue()).doubleValue(),
												 ((Number) this.diffNptsThPanel.getValue()).intValue(),
												 this.filteredDiffBox.isSelected(),
												 ((Number) this.diffFilterSizePan.getValue()).doubleValue());
	}

	public MapParameters.DriftParameters getDriftParams()
	{
		return new MapParameters.DriftParameters(((Number) this.driftBinWidthPanel.getValue()).doubleValue(),
											 ((Number) this.driftNptsThPanel.getValue()).intValue(),
											 this.filteredDriftBox.isSelected(),
											 ((Number) this.driftFilterSizePan.getValue()).doubleValue());
	}

	public double driftSizeMult()
	{
		return ((Number) this.driftSizeMultPanel.getValue()).doubleValue();
	}
}
