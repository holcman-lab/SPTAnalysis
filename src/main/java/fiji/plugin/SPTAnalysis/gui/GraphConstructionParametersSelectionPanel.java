package fiji.plugin.SPTAnalysis.gui;

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
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.SwingWorker;

import fiji.plugin.SPTAnalysis.DataController;
import fiji.plugin.SPTAnalysis.GUIController;
import fiji.plugin.SPTAnalysis.SPTAnalysis;
import fiji.plugin.SPTAnalysis.graphConstruction.GraphConstruction;
import fiji.plugin.SPTAnalysis.graphConstruction.GraphConstructionDBScan;
import fiji.plugin.SPTAnalysis.graphConstruction.GraphConstructionDBScanRecursive;
import fiji.plugin.SPTAnalysis.graphConstruction.GraphConstructionParameters;
import fiji.plugin.SPTAnalysis.struct.Freezable;
import fiji.plugin.SPTAnalysis.struct.Point;

public class GraphConstructionParametersSelectionPanel extends AnalysisParametersPanel
{
	public static class MinVolEllParamPanel extends JPanel implements Freezable
	{
		private static final long serialVersionUID = 1L;

		public final JLabel label;
		public final JFormattedTextField minVolEpsPanel;

		public MinVolEllParamPanel()
		{
			this.label = new JLabel("Min. area epsilon");
			this.minVolEpsPanel = new JFormattedTextField(new DecimalFormat("0.000"));
			this.minVolEpsPanel.setValue(defMinVolEps);

			this.setLayout(new GridBagLayout());
			GridBagConstraints cLabel = new GridBagConstraints();
			cLabel.gridx = 0;
			cLabel.gridy = 0;
			cLabel.weightx = 1;
			cLabel.anchor = GridBagConstraints.WEST;
			cLabel.insets = new Insets(10, 10, 0, 0);

			GridBagConstraints cData = new GridBagConstraints();
			cData.gridx = 1;
			cData.gridy = 0;
			cData.weightx = 0.5;
			cData.fill = GridBagConstraints.HORIZONTAL;
			cData.gridwidth = GridBagConstraints.REMAINDER;
			cData.insets = new Insets(10, 5, 0, 10);

			add(this.label, cLabel);
			add(this.minVolEpsPanel, cData);
		}

		@Override
		public void freeze()
		{
			this.minVolEpsPanel.setEnabled(false);
		}

		@Override
		public void reset()
		{
			this.minVolEpsPanel.setEnabled(true);
			this.minVolEpsPanel.setValue(defMinVolEps);
		}

		public double getValue()
		{
			return (double) this.minVolEpsPanel.getValue();
		}
	}
	
	private static final long serialVersionUID = 1L;

	public static final double defLowVelTh = 10.0;
	public static final String defAlgoChoice = GraphConstructionDBScan.name;
	public static final String defNodeTypeChoice = GraphConstructionParameters.NodeType.POLY.name();

	public static final double defMinArea = 0;
	public static final String defMaxArea = "inf";
	public static final double defMinVolEps = 0.001;

	@SuppressWarnings("unused")
	private final JFrame frame;

	private final DataController dctrl;
	private final GUIController gctrl;

	private JFormattedTextField lowVelThPanel;

	private final Choice algoChoicePanel;
	private final HashMap<String, GraphConstructionParametersPanel> graphParamsPanel;
	String selected;

	private final JCheckBox showDbscanClustersBox;

	private final JButton computeBut;
	private final JProgressBar progrBar;

	private final HashMap<String, GraphConstructionParameters.NodeType> nodeTypeMap;
	private final Choice nodeTypeChoicePanel;

	private JFormattedTextField minAreaPanel;
	private JFormattedTextField maxAreaPanel;
	private MinVolEllParamPanel minVolEllParamPanel;
	private JPanel emptyPanel;

	public GraphConstructionParametersSelectionPanel(final JFrame frame, final DataController dctrl, final GUIController gctrl)
	{
		this.frame = frame;
		this.dctrl = dctrl;
		this.gctrl = gctrl;

		this.lowVelThPanel = new JFormattedTextField(new DecimalFormat("0.000"));
		this.lowVelThPanel.setValue(defLowVelTh);

		this.graphParamsPanel = new HashMap<> ();
		this.graphParamsPanel.put(GraphConstructionDBScan.name,
				new GraphConstructionDBScanPanel(this.dctrl, this.gctrl));
		this.graphParamsPanel.put(GraphConstructionDBScanRecursive.name,
				new GraphConstructionDBScanRecursivePanel(this.dctrl, this.gctrl));
		this.selected = GraphConstructionDBScan.name;

		this.algoChoicePanel = new Choice();
		this.algoChoicePanel.add(GraphConstructionDBScan.name);
		this.algoChoicePanel.add(GraphConstructionDBScanRecursive.name);
		this.algoChoicePanel.select(defAlgoChoice);
		this.algoChoicePanel.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				remove(graphParamsPanel.get(selected));
				selected = algoChoicePanel.getSelectedItem();
				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.BOTH;
				c.gridx = 0;
				c.gridy = 5;
				c.gridwidth = 2;
				add(graphParamsPanel.get(algoChoicePanel.getSelectedItem()), c);
				revalidate();
				repaint();
				frame.pack();
			}
		});

		this.nodeTypeChoicePanel = new Choice();
		this.nodeTypeChoicePanel.add(GraphConstructionParameters.NodeType.POLY.name());
		this.nodeTypeChoicePanel.add(GraphConstructionParameters.NodeType.ELLIPSE.name());
		this.nodeTypeChoicePanel.select(defNodeTypeChoice);
		this.nodeTypeChoicePanel.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.BOTH;
				c.gridx = 0;
				c.gridy = 8;
				c.gridwidth = 2;

				if (nodeTypeChoicePanel.getSelectedItem().equals(GraphConstructionParameters.NodeType.ELLIPSE.name()))
				{
					remove(emptyPanel);
					add(minVolEllParamPanel, c);
				}
				else
				{
					remove(minVolEllParamPanel);
					add(emptyPanel, c);
				}
				revalidate();
				repaint();
				frame.pack();
			}
		});

		this.nodeTypeMap = new HashMap<> ();
		this.nodeTypeMap.put(GraphConstructionParameters.NodeType.ELLIPSE.name(),
				GraphConstructionParameters.NodeType.ELLIPSE);
		this.nodeTypeMap.put(GraphConstructionParameters.NodeType.POLY.name(),
				GraphConstructionParameters.NodeType.POLY);

		this.minAreaPanel = new JFormattedTextField(new DecimalFormat("0.000"));
		this.minAreaPanel.setValue(defMinArea);

		this.maxAreaPanel = new JFormattedTextField();
		this.maxAreaPanel.setValue(defMaxArea);

		this.minVolEllParamPanel = new MinVolEllParamPanel();
		this.emptyPanel = new JPanel();

		this.showDbscanClustersBox = new JCheckBox();
		this.showDbscanClustersBox.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent arg0)
			{
				gctrl.display();
			}
		});

		this.computeBut = new JButton("Compute");
		this.computeBut.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				progrBar.setValue(0);
				computeBut.setEnabled(false);
				freeze();

				ProgressBarPlugLogger pBarLog = new ProgressBarPlugLogger(progrBar);

				GraphConstructionParameters ps = getParams();
				final SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>()
				{
					@Override
					protected Void doInBackground() throws Exception
					{
						String algoName = algoChoicePanel.getSelectedItem();

						if (algoName.equals(GraphConstructionDBScan.name))
							SPTAnalysis.constructGraph(dctrl, gctrl, new GraphConstructionDBScan(ps), pBarLog);
						else if (algoName.equals(GraphConstructionDBScanRecursive.name))
							SPTAnalysis.constructGraph(dctrl, gctrl, new GraphConstructionDBScanRecursive(ps), pBarLog);
						return null;
					}

					@Override
					protected void done()
					{
						try
						{
							this.get();
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}

						progrBar.setIndeterminate(false);
						progrBar.setValue(100);

						 if (ps.expName().equals(dctrl.nextAvailableAnalysisName()))
							 dctrl.incAvailableAnalysisName();
					}
				};

				worker.execute();
			}
		});

		this.progrBar = new JProgressBar(0, 100);

		initGui();
	}

	public void initGui()
	{
		this.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(10, 10, 0, 10);

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

		cLabel.gridy = 0;
		cData.gridy = 0;
		this.add(new JLabel("Analysis name"), cLabel);
		this.add(this.analysisNamePanel, cData);

		c.gridy = 1;
		this.add(new JSeparator(JSeparator.HORIZONTAL), c);

		cLabel.gridy = 2;
		cData.gridy = 2;
		this.add(new JLabel("Velocity threshold (µm/s)"), cLabel);
		this.add(this.lowVelThPanel, cData);

		c.gridy = 3;
		this.add(new JSeparator(JSeparator.HORIZONTAL), c);

		cLabel.gridy = 4;
		cData.gridy = 4;
		this.add(GUIController.newBoldLabel("Construction algorithm"), cLabel);
		this.add(this.algoChoicePanel, cData);

		c.gridy = 5;
		add(graphParamsPanel.get(algoChoicePanel.getSelectedItem()), c);

		c.gridy = 6;
		this.add(new JSeparator(JSeparator.HORIZONTAL), c);

		cLabel.gridy = 7;
		cData.gridy = 7;
		this.add(GUIController.newBoldLabel("Nodes shape"), cLabel);
		this.add(this.nodeTypeChoicePanel, cData);

		c.gridy = 8;
		c.insets = new Insets(0, 0, 0, 0);
		add(this.emptyPanel, c);

		cLabel.gridy = 9;
		cData.gridy = 9;
		this.add(new JLabel("Min. node area (µm²)"), cLabel);
		this.add(this.minAreaPanel, cData);

		cLabel.gridy = 10;
		cData.gridy = 10;
		this.add(new JLabel("Max. node area (µm²)"), cLabel);
		this.add(this.maxAreaPanel, cData);

		c.gridy = 11;
		c.insets = new Insets(10, 10, 0, 10);
		this.add(new JSeparator(JSeparator.HORIZONTAL), c);

		c.gridy = 12;
		this.add(this.computeBut, c);

		c.gridy = 13;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(this.progrBar, c);
	}
	
	public double maxArea()
	{
		Double res = Double.NaN;
		if (((String) this.maxAreaPanel.getValue()).equals("inf"))
			res = Double.MAX_VALUE;
		else
			res = Double.parseDouble((String) this.maxAreaPanel.getValue());

		return res;
	}

	public GraphConstructionParameters getParams()
	{
		GraphConstructionParameters res = graphParamsPanel.get(selected).params(
				(String) analysisNamePanel.getValue(),
				((Number) this.lowVelThPanel.getValue()).doubleValue(),
				((Number) this.minAreaPanel.getValue()).doubleValue(), this.maxArea(),
				((Number) this.minVolEllParamPanel.getValue()).doubleValue(),
				this.nodeTypeMap.get(this.nodeTypeChoicePanel.getSelectedItem()));

		return res;
	}

	public void updateAnalysisName()
	{
		this.analysisNamePanel.setValue(dctrl.nextAvailableAnalysisName());
	}

	@Override
	public void freeze()
	{
		super.freeze();
		this.lowVelThPanel.setEnabled(false);
		algoChoicePanel.setEnabled(false);
		this.graphParamsPanel.get(this.selected).freeze();
		this.minAreaPanel.setEnabled(false);
		this.maxAreaPanel.setEnabled(false);
		this.nodeTypeChoicePanel.setEnabled(false);
	}

	@Override
	public void reset()
	{
		super.reset();
		this.updateAnalysisName();

		this.lowVelThPanel.setEnabled(true);
		this.lowVelThPanel.setValue(defLowVelTh);

		this.algoChoicePanel.select(defAlgoChoice);
		this.algoChoicePanel.setEnabled(true);
		this.algoChoicePanel.getItemListeners()[0].itemStateChanged(null);
		
		for (final GraphConstructionParametersPanel paramP: this.graphParamsPanel.values())
			paramP.reset();

		this.minAreaPanel.setEnabled(true);
		this.minAreaPanel.setValue(defMinArea);
		this.maxAreaPanel.setEnabled(true);
		this.maxAreaPanel.setValue(defMaxArea);
		this.nodeTypeChoicePanel.setEnabled(true);
		this.nodeTypeChoicePanel.select(defNodeTypeChoice);

		this.computeBut.setEnabled(true);
		this.progrBar.setValue(0);
	}

	public boolean showClusters()
	{
		return this.showDbscanClustersBox.isSelected();
	}
	
	public HashMap<Integer, ArrayList<Point>> clusters()
	{
		String algoName = algoChoicePanel.getSelectedItem();

		GraphConstructionParameters ps = getParams();
		GraphConstruction gConstr = null;
		 if (algoName.equals(GraphConstructionDBScan.name))
			 gConstr = new GraphConstructionDBScan(ps);
		 else if (algoName.equals(GraphConstructionDBScanRecursive.name))
			 gConstr = new GraphConstructionDBScanRecursive(ps);

		 ArrayList<Point> lowVelPts = GraphConstruction.extractLowVelPts(this.dctrl.trajs().wins.get(gctrl.curFrame()), ps);
		 return gConstr.computeClusters(lowVelPts);
	}
}
