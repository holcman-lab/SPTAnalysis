package fiji.plugin.SPTAnalysis.gui;

import java.awt.Choice;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.SwingWorker;

import fiji.plugin.SPTAnalysis.DataController;
import fiji.plugin.SPTAnalysis.GUIController;
import fiji.plugin.SPTAnalysis.SPTAnalysis;
import fiji.plugin.SPTAnalysis.wellDetection.DensityWellDetection;
import fiji.plugin.SPTAnalysis.wellDetection.HybridWellDetection;
import fiji.plugin.SPTAnalysis.wellDetection.HybridWellDetectionMultiscale;
import fiji.plugin.SPTAnalysis.wellDetection.WellDetectionParameters;
import fiji.plugin.SPTAnalysis.wellLinker.DistanceWellLinker;


public class WellDetectionParametersSelectionPanel extends AnalysisParametersPanel
{
	private static final long serialVersionUID = 1L;

	public static final String defAlgoChoice = HybridWellDetection.name;

	private final JFrame frame;

	private final DataController dctrl;
	@SuppressWarnings("unused")
	private final GUIController gctrl;

	private final Choice algoChoicePanel;
	private final HashMap<String, WellDetectionParametersPanel> wellParamsPanel;
	String selected;

	private final WellLinkerPanel linkPan;

	private final JProgressBar progrBar;

	private final JButton computeBut;

	public WellDetectionParametersSelectionPanel(final JFrame frame, final DataController dctrl, final GUIController gctrl)
	{
		super();
		this.frame = frame;
		this.dctrl = dctrl;
		this.gctrl = gctrl;

		updateAnalysisName();

		this.wellParamsPanel = new HashMap<>();
		this.wellParamsPanel.put(HybridWellDetection.name,
				new HybridWellDetectionPanel(this.frame, this.dctrl, true));
		this.wellParamsPanel.put(HybridWellDetectionMultiscale.name,
				new HybridWellDetectionMultiscalePanel(this.frame, this.dctrl));
		this.wellParamsPanel.put(DensityWellDetection.name,
				new DensityWellDetectionPanel(this.frame, this.dctrl));
		this.selected = HybridWellDetection.name;

		this.algoChoicePanel = new Choice();
		this.algoChoicePanel.add(HybridWellDetection.name);
		this.algoChoicePanel.add(HybridWellDetectionMultiscale.name);
		this.algoChoicePanel.add(DensityWellDetection.name);
		this.algoChoicePanel.select(defAlgoChoice);
		this.algoChoicePanel.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				remove(wellParamsPanel.get(selected));
				selected = algoChoicePanel.getSelectedItem();
				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.BOTH;
				c.gridx = 0;
				c.gridy = 3;
				c.gridwidth = 2;
				add(wellParamsPanel.get(algoChoicePanel.getSelectedItem()), c);
				revalidate();
				repaint();
				frame.pack();
			}
		});

		this.linkPan = new WellLinkerPanel();

		this.progrBar = new JProgressBar(0, 100);

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

				WellDetectionParameters ps = getParams();
				final SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>()
				{
					@Override
					protected Void doInBackground() throws Exception
					{
						String algoName = algoChoicePanel.getSelectedItem();

						DistanceWellLinker dwl = new DistanceWellLinker(linkPan.maxFrameGap(),
								linkPan.maxDist());

						if (algoName.equals(HybridWellDetection.name))
							SPTAnalysis.detectWells(dctrl, gctrl, new HybridWellDetection(ps), dwl, pBarLog);
						else if (algoName.equals(HybridWellDetectionMultiscale.name))
							SPTAnalysis.detectWells(dctrl, gctrl, new HybridWellDetectionMultiscale(ps), dwl, pBarLog);
						else if (algoName.equals(DensityWellDetection.name))
							SPTAnalysis.detectWells(dctrl, gctrl, new DensityWellDetection(ps), dwl, pBarLog);
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

		initGui();
	}

	public void initGui()
	{
		this.setLayout(new GridBagLayout());

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

		{
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.weightx = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.gridy = 1;
			c.insets = new Insets(10, 10, 0, 10);
			this.add(new JSeparator(JSeparator.HORIZONTAL), c);
		}

		cLabel.gridy = 2;
		cData.gridy = 2;
		this.add(GUIController.newBoldLabel("Detection algorithm"), cLabel);

		this.add(this.algoChoicePanel, cData);

		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 0;
			c.gridy = 3;
			c.gridwidth = 2;
			add(wellParamsPanel.get(algoChoicePanel.getSelectedItem()), c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.weightx = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.gridy = 4;
			c.insets = new Insets(10, 10, 0, 10);
			this.add(new JSeparator(JSeparator.HORIZONTAL), c);
		}

		cLabel.gridy = 5;
		this.add(GUIController.newBoldLabel("Well linking"), cLabel);
		
		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridy = 6;
			c.gridwidth = 2;
			c.insets = new Insets(0, 0, 10, 0);
			this.add(this.linkPan, c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.weightx = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.gridy = 7;
			c.insets = new Insets(0, 10, 0, 10);
			this.add(new JSeparator(JSeparator.HORIZONTAL), c);
		}

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridy = 8;
		c.insets = new Insets(10, 10, 0, 10);
		this.add(this.computeBut, c);


		c.gridy = 9;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(this.progrBar, c);
	}

	public WellDetectionParameters getParams()
	{
		WellDetectionParameters res = wellParamsPanel.get(selected).detectionParameters();
		res.expName((String) analysisNamePanel.getValue());
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
		algoChoicePanel.setEnabled(false);
		this.wellParamsPanel.get(this.selected).freeze();
		this.linkPan.freeze();
	}

	@Override
	public void reset()
	{
		super.reset();
		this.updateAnalysisName();

		for (final WellDetectionParametersPanel pan: this.wellParamsPanel.values())
			pan.reset();
		this.algoChoicePanel.select(defAlgoChoice);
		this.algoChoicePanel.setEnabled(true);
		this.algoChoicePanel.getItemListeners()[0].itemStateChanged(null);
		this.linkPan.reset();

		this.computeBut.setEnabled(true);
		this.progrBar.setValue(0);
	}
}
