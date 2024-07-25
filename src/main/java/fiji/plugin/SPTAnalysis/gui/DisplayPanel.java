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
	public static final Integer DEFAULT_ANODIFF_MIN_TR_PTS = 4;
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
	private final JFormattedTextField anoDiffBinWidthPanel;
	private final JFormattedTextField anoDiffNptsThPanel;
	private final JFormattedTextField anoDiffNptsFitPanel;
	private final JFormattedTextField driftBinWidthPanel;
	private final JFormattedTextField driftNptsThPanel;
	private final JFormattedTextField driftSizeMultPanel;

	private final Checkbox trajsDispPan;
	private final JButton trajsDispHist;
	private final Checkbox densDispPan;
	private final JButton densDispHist;
	private final Checkbox anoDiffDispPan;
	private final JButton anoDiffDispHist;
	private final Checkbox anoAlphaDispPan;
	private final JButton anoAlphaDispHist;
	private final Checkbox diffDispPan;
	private final JButton diffDispHist;
	private final Checkbox driftDispPan;

	private Choice DensFilterSizeChoice;

	private JCheckBox mergeWindows;
	private JCheckBox colorByFamily;
	private JCheckBox filteredDiffBox;
	private JCheckBox filteredAnoDiffBox;
	private JCheckBox filteredDriftBox;

	private final JFormattedTextField diffFilterSizePan;
	private final JFormattedTextField anoDiffFilterSizePan;
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

		this.anoDiffDispPan = new Checkbox("Anomalous Diffusion", false);
		this.anoDiffDispPan.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent arg0)
			{
				gCntrl.display();
			}
		});

		this.anoDiffDispHist = new JButton("Hist");
		this.anoDiffDispHist.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				gCntrl.displayAnoDiffHist(getAnoDiffusionParams());
			}
		});


		this.anoAlphaDispPan = new Checkbox("Anomalous alpha", false);
		this.anoAlphaDispPan.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent arg0)
			{
				gCntrl.display();
			}
		});

		this.anoAlphaDispHist = new JButton("Hist");
		this.anoAlphaDispHist.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				gCntrl.displayAnoAlphaHist(getAnoDiffusionParams());
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
	
		this.anoDiffBinWidthPanel = new JFormattedTextField(new DecimalFormat("0.000"));
		this.anoDiffBinWidthPanel.setValue(DEFAULT_DIFF_BIN_W);
		this.anoDiffBinWidthPanel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				gCntrl.display();
			}
		});

		this.anoDiffNptsThPanel = new JFormattedTextField(new DecimalFormat("0"));
		this.anoDiffNptsThPanel.setValue(DEFAULT_DIFF_MIN_NPTS);
		this.anoDiffNptsThPanel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				gCntrl.display();
			}
		});
	
		this.anoDiffNptsFitPanel = new JFormattedTextField(new DecimalFormat("0"));
		this.anoDiffNptsFitPanel.setValue(DEFAULT_ANODIFF_MIN_TR_PTS);
		this.anoDiffNptsFitPanel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				gCntrl.display();
			}
		});

		this.filteredAnoDiffBox = new JCheckBox();
		this.filteredAnoDiffBox.addItemListener(new ItemListener()
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

		this.anoDiffFilterSizePan = new JFormattedTextField(new DecimalFormat("0.000"));
		this.anoDiffFilterSizePan.setValue(DEFAULT_DIFF_FILTER_SIZE);
		this.anoDiffFilterSizePan.addActionListener(new ActionListener()
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
		if (this.anoDiffDispPan .getState())
			res.add("AnoDiffusion");
		if (this.anoAlphaDispPan .getState())
			res.add("AnoAlpha");
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
		this.add(GUIController.newBoldLabel("Show"), cLabel);

		cLabel.gridy++;
		this.add(this.trajsDispPan, cLabel);
		cData.gridy = cLabel.gridy;
		this.add(this.trajsDispHist, cData);

		cLabel.gridy++;
		this.add(this.densDispPan, cLabel);
		cData.gridy = cLabel.gridy;
		this.add(this.densDispHist, cData);

		cLabel.gridy++;
		this.add(this.diffDispPan, cLabel);
		cData.gridy = cLabel.gridy;
		this.add(this.diffDispHist, cData);

		cLabel.gridy++;
		this.add(this.anoDiffDispPan, cLabel);
		cData.gridy = cLabel.gridy;
		this.add(this.anoDiffDispHist, cData);

		cLabel.gridy++;
		this.add(this.anoAlphaDispPan, cLabel);
		cData.gridy = cLabel.gridy;
		this.add(this.anoAlphaDispHist, cData);

		cLabel.gridy++;
		this.add(this.driftDispPan, cLabel);

		cLabel.gridy++;
		c.gridy = cLabel.gridy;
		this.add(new JSeparator(JSeparator.HORIZONTAL), c);

		cLabel.gridy++;
		this.add(GUIController.newBoldLabel("Trajectory Options"), cLabel);

		cLabel.gridy++;
		cData.gridy = cLabel.gridy;
		this.add(new JLabel("Color"), cLabel);
		this.add(this.trajectoryColor, cData);

		cLabel.gridy++;
		cData.gridy = cLabel.gridy;
		this.add(new JLabel("Max. inst. vel. (µm/s)"), cLabel);
		this.add(this.trajMaxInstVelPanel, cData);


		cLabel.gridy++;
		c.gridy = cLabel.gridy;
		this.add(new JSeparator(JSeparator.HORIZONTAL), c);

		cLabel.gridy++;
		this.add(GUIController.newBoldLabel("Density Map Options"), cLabel);

		cLabel.gridy++;
		cData.gridy = cLabel.gridy;
		this.add(new JLabel("Density bins width (µm)"), cLabel);
		this.add(this.densBinWidthPanel, cData);

		cLabel.gridy++;
		cData.gridy = cLabel.gridy;
		this.add(new JLabel("Log density"), cLabel);
		this.add(this.logDens, cData);

		cLabel.gridy++;
		cData.gridy = cLabel.gridy;
		this.add(new JLabel("Filter size (NxN)"), cLabel);
		this.add(this.DensFilterSizeChoice, cData);


		cLabel.gridy++;
		c.gridy = cLabel.gridy;
		this.add(new JSeparator(JSeparator.HORIZONTAL), c);

		cLabel.gridy++;
		this.add(GUIController.newBoldLabel("Diffusion map options"), cLabel);

		cLabel.gridy++;
		cData.gridy = cLabel.gridy;
		this.add(new JLabel("Diffusion bins width (µm)"), cLabel);
		this.add(this.diffBinWidthPanel, cData);

		cLabel.gridy++;
		cData.gridy = cLabel.gridy;
		this.add(new JLabel("Diffusion min. num. pts."), cLabel);
		this.add(this.diffNptsThPanel, cData);

		cLabel.gridy++;
		cData.gridy = cLabel.gridy;
		this.add(new JLabel("Apply cosine filter"), cLabel);
		this.add(this.filteredDiffBox, cData);

		cLabel.gridy++;
		cData.gridy = cLabel.gridy;
		this.add(new JLabel("Cosine filter radius (µm)"), cLabel);
		this.add(this.diffFilterSizePan, cData);

		cLabel.gridy++;
		c.gridy = cLabel.gridy;
		this.add(new JSeparator(JSeparator.HORIZONTAL), c);

		cLabel.gridy++;
		this.add(GUIController.newBoldLabel("Anomalous Diffusion map options"), cLabel);

		cLabel.gridy++;
		cData.gridy = cLabel.gridy;
		this.add(new JLabel("Ano. Diffusion bins width (µm)"), cLabel);
		this.add(this.anoDiffBinWidthPanel, cData);

		cLabel.gridy++;
		cData.gridy = cLabel.gridy;
		this.add(new JLabel("Ano. Diffusion min. num. pts."), cLabel);
		this.add(this.anoDiffNptsThPanel, cData);

		cLabel.gridy++;
		cData.gridy = cLabel.gridy;
		this.add(new JLabel("Ano. Diffusion min. tr. fit"), cLabel);
		this.add(this.anoDiffNptsFitPanel, cData);

		cLabel.gridy++;
		cData.gridy = cLabel.gridy;
		this.add(new JLabel("Apply cosine filter"), cLabel);
		this.add(this.filteredAnoDiffBox, cData);

		cLabel.gridy++;
		cData.gridy = cLabel.gridy;
		this.add(new JLabel("Cosine filter radius (µm)"), cLabel);
		this.add(this.anoDiffFilterSizePan, cData);

		cLabel.gridy++;
		c.gridy = cLabel.gridy;
		this.add(new JSeparator(JSeparator.HORIZONTAL), c);

		cLabel.gridy++;
		c.gridy = cLabel.gridy;
		this.add(GUIController.newBoldLabel("Drift map options"), cLabel);

		cLabel.gridy++;
		cData.gridy = cLabel.gridy;
		this.add(new JLabel("Drift bins width (μm)"), cLabel);
		this.add(this.driftBinWidthPanel, cData);

		cLabel.gridy++;
		cData.gridy = cLabel.gridy;
		this.add(new JLabel("Drift min. num. pts."), cLabel);
		this.add(this.driftNptsThPanel, cData);

		cLabel.gridy++;
		cData.gridy = cLabel.gridy;
		this.add(new JLabel("Apply cosine filter"), cLabel);
		this.add(this.filteredDriftBox, cData);

		cLabel.gridy++;
		cData.gridy = cLabel.gridy;
		this.add(new JLabel("Cosine filter radius (μm)"), cLabel);
		this.add(this.driftFilterSizePan, cData);

		cLabel.gridy++;
		cData.gridy = cLabel.gridy;
		this.add(new JLabel("Size multiplier"), cLabel);
		this.add(this.driftSizeMultPanel, cData);

		cLabel.gridy++;
		c.gridy = cLabel.gridy;
		this.add(new JSeparator(JSeparator.HORIZONTAL), c);

		cLabel.gridy++;
		this.add(GUIController.newBoldLabel("Time-windows options"), cLabel);

		cLabel.gridy++;
		cData.gridy = cLabel.gridy;
		this.add(new JLabel("Merge windows"), cLabel);
		this.add(this.mergeWindows, cData);

		cLabel.gridy++;
		c.gridy = cLabel.gridy;
		this.add(new JSeparator(JSeparator.HORIZONTAL), c);

		cLabel.gridy++;
		this.add(GUIController.newBoldLabel("Potential wells options"), cLabel);

		cLabel.gridy++;
		cData.gridy = cLabel.gridy;
		this.add(new JLabel("Color by family"), cLabel);
		this.add(this.colorByFamily, cData);

		cLabel.gridy++;
		c.gridy = cLabel.gridy;
		this.add(new JSeparator(JSeparator.HORIZONTAL), c);

		cLabel.gridy++;
		this.add(GUIController.newBoldLabel("Scale bar options"), cLabel);

		cLabel.gridy++;
		cData.gridy = cLabel.gridy;
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

	public MapParameters.AnomalousDiffusionParameters getAnoDiffusionParams()
	{
		return new MapParameters.AnomalousDiffusionParameters(((Number) this.anoDiffBinWidthPanel.getValue()).doubleValue(),
												 ((Number) this.anoDiffNptsThPanel.getValue()).intValue(),
												 ((Number) this.anoDiffNptsFitPanel.getValue()).intValue(),
												 this.filteredAnoDiffBox.isSelected(),
												 ((Number) this.anoDiffFilterSizePan.getValue()).doubleValue());
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
