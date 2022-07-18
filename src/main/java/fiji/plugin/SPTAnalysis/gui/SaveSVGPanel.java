package fiji.plugin.SPTAnalysis.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.scijava.widget.FileWidget;

import fiji.plugin.SPTAnalysis.DataController;
import fiji.plugin.SPTAnalysis.GUIController;
import fiji.plugin.SPTAnalysis.Utils;
import fiji.plugin.SPTAnalysis.graphConstruction.GraphConstructionParameters;
import fiji.plugin.SPTAnalysis.struct.AnalysisParameters;
import fiji.plugin.SPTAnalysis.struct.Graph;
import fiji.plugin.SPTAnalysis.struct.GraphWindows;
import fiji.plugin.SPTAnalysis.struct.MapParameters;
import fiji.plugin.SPTAnalysis.struct.PotWell;
import fiji.plugin.SPTAnalysis.struct.PotWells;
import fiji.plugin.SPTAnalysis.struct.PotWellsWindows;
import fiji.plugin.SPTAnalysis.struct.Rectangle;
import fiji.plugin.SPTAnalysis.struct.ScalarMap;
import fiji.plugin.SPTAnalysis.struct.ScalarMapWindows;
import fiji.plugin.SPTAnalysis.struct.TrajectoriesColorScheme;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsembleWindows;
import fiji.plugin.SPTAnalysis.struct.VectorMap;
import fiji.plugin.SPTAnalysis.struct.VectorMapWindows;
import fiji.plugin.SPTAnalysis.wellDetection.WellDetectionParameters;
import fiji.plugin.SPTAnalysis.writers.SVGGraphWriter;
import fiji.plugin.SPTAnalysis.writers.SVGScalarMapWriter;
import fiji.plugin.SPTAnalysis.writers.SVGScaleBarWriter;
import fiji.plugin.SPTAnalysis.writers.SVGTrajectoriesWriter;
import fiji.plugin.SPTAnalysis.writers.SVGVectorMapWriter;
import fiji.plugin.SPTAnalysis.writers.SVGWellsWriter;
import fiji.plugin.SPTAnalysis.writers.SVGWriter;

public class SaveSVGPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private HashMap<String, JCheckBox> SVGPanels;

	private JButton SVGSaveIndividualButton;
	private JButton SVGSaveOverlayButton;
	private JCheckBox regBox;

	public SaveSVGPanel(DataController dcntrl, GUIController gcntrl)
	{
		this.SVGPanels = new HashMap<> ();
		this.SVGPanels.put("TRAJ", new JCheckBox());
		this.SVGPanels.put("DENS", new JCheckBox());
		this.SVGPanels.put("DIFF", new JCheckBox());
		this.SVGPanels.put("DRIFT", new JCheckBox());
		this.SVGPanels.put("WELL", new JCheckBox());
		this.SVGPanels.put("GRAPH", new JCheckBox());
		this.SVGPanels.put("REGION", new JCheckBox());

		this.SVGSaveIndividualButton = new JButton("Save individual");
		this.SVGSaveIndividualButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				File selDir = gcntrl.UIServ().chooseFile(null, FileWidget.DIRECTORY_STYLE);

				if (selDir == null || (selDir.exists() && !selDir.isDirectory()))
					return;

				if (!selDir.exists())
					selDir.mkdirs();

				Rectangle selection = dcntrl.default_selection();
				if (regBox.isSelected())
				{
					selection = gcntrl.selectedRegion();
					SavePanel.saveSelection(selDir, selection);
				}

				boolean mergeWin = gcntrl.displayPanel().mergeWindows();
				try
				{
					if (SVGPanels.get("TRAJ").isSelected())
					{
						TrajectoryEnsembleWindows tew = dcntrl.trajs();
						if (mergeWin)
						{
							tew = new TrajectoryEnsembleWindows();
							tew.wins.add(dcntrl.trajsFlat());
						}

						int cpt = 0;
						for (TrajectoryEnsemble te: tew.wins)
						{
							TrajectoriesColorScheme coloring = gcntrl.trajsOverlay().colorScheme().wins.get(cpt);
							if (regBox.isSelected())
							{
								te = Utils.trajsInShape(te, selection);
								coloring = new TrajectoriesColorScheme(te, coloring.coloringType());
							}

							SVGTrajectoriesWriter tw = new SVGTrajectoriesWriter(te, 100, 1, coloring);
							SVGWriter.saveSVG(selDir.getAbsolutePath() + String.format("/trajectories_%d.svg", cpt),
									new SVGWriter[] {tw, new SVGScaleBarWriter(tw.zoomFactor(), 1, Color.BLACK)}, tw.minp());
							cpt = cpt + 1;
						}
					}
					if (SVGPanels.get("DENS").isSelected())
					{
						MapParameters.DensityParameters dp = gcntrl.displayPanel().getDensityParams();

						ScalarMapWindows densWins = dcntrl.densMapWindows(dp, mergeWin);
						int cpt = 0;
						for (final ScalarMap dens: densWins.wins)
						{
							SVGScalarMapWriter tw = new SVGScalarMapWriter(dens, 100,
									Utils.squaresInReg(dens.grid(), selection));
							SVGWriter.saveSVG(selDir.getAbsolutePath() + String.format("/density_%d.svg", cpt),
								new SVGWriter[] {tw, new SVGScaleBarWriter(tw.zoomFactor(), 1, Color.BLACK)}, tw.minp());
							cpt = cpt + 1;
						}
					}
					if (SVGPanels.get("DIFF").isSelected())
					{
						MapParameters.DiffusionParameters dp = gcntrl.displayPanel().getDiffusionParams();

						ScalarMapWindows diffWins = dcntrl.diffMapWindows(dp, mergeWin);
						int cpt = 0;
						for (final ScalarMap diff: diffWins.wins)
						{
							SVGScalarMapWriter tw = new SVGScalarMapWriter(diff, 100,
									Utils.squaresInReg(diff.grid(), selection));
							SVGWriter.saveSVG(selDir.getAbsolutePath() + String.format("/diffusion_%d.svg", cpt),
								new SVGWriter[] {tw, new SVGScaleBarWriter(tw.zoomFactor(), 1, Color.BLACK)}, tw.minp());
							cpt = cpt + 1;
						}
					}
					if (SVGPanels.get("DRIFT").isSelected())
					{
						MapParameters.DriftParameters dp = gcntrl.displayPanel().getDriftParams();

						VectorMapWindows driftWins = dcntrl.driftMapWindows(dp, mergeWin);
						int cpt = 0;
						for (final VectorMap drift: driftWins.wins)
						{
							SVGVectorMapWriter tw = new SVGVectorMapWriter(drift, dp.dx/2, 100,
									Utils.squaresInReg(drift.grid(), selection));
							SVGWriter.saveSVG(selDir.getAbsolutePath() + String.format("/drift_%d.svg", cpt),
								new SVGWriter[] {tw, new SVGScaleBarWriter(tw.zoomFactor(), 1, Color.BLACK)}, tw.minp());
							cpt = cpt + 1;
						}
					}


					if (SVGPanels.get("WELL").isSelected())
					{
						AnalysisParameters ap = gcntrl.curAnalysisParameters();

						if (ap != null && ap.analysisType() == AnalysisParameters.Type.WELL)
						{
							WellDetectionParameters wp = (WellDetectionParameters) ap;

							int cpt = 0;
							for (final PotWells wells: dcntrl.wells().get(wp.algoName()).get(wp).wins)
							{
								ArrayList<PotWell> tmp1 = Utils.wellsInReg(wells.wells, selection);
								PotWell[] tmp = new PotWell[tmp1.size()];
								tmp1.toArray(tmp);
								SVGWellsWriter tw = new SVGWellsWriter(tmp, 100);
								SVGWriter.saveSVG(selDir.getAbsolutePath() + String.format("/wells_%d.svg", cpt),
									new SVGWriter[] {tw, new SVGScaleBarWriter(tw.zoomFactor(), 1, Color.BLACK)}, tw.minp());
								++cpt;
							}
						}
					}

					if (SVGPanels.get("GRAPH").isSelected())
					{
						AnalysisParameters ap = gcntrl.curAnalysisParameters();
						if (ap != null && ap.analysisType() == AnalysisParameters.Type.GRAPH)
						{
							GraphConstructionParameters gp = (GraphConstructionParameters) ap;
							if (SVGPanels.get("GRAPH").isSelected() && gp != null)
							{
								int cpt = 0;
								for (final Graph g: dcntrl.graphs().get(gp.algoName()).get(gp).wins)
								{
									final Graph gReg = Graph.GraphInRegion(g, selection);
									SVGGraphWriter tw = new SVGGraphWriter(gReg, 100);
									SVGWriter.saveSVG(selDir.getAbsolutePath() + String.format("/graph_%d.svg", cpt),
											new SVGWriter[] {tw, new SVGScaleBarWriter(tw.zoomFactor(), 1, Color.BLACK)}, tw.minp());
									++cpt;
								}
							}
						}
					}
					else
						assert(false);
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
		});

		this.SVGSaveOverlayButton = new JButton("Save overlay");
		this.SVGSaveOverlayButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				File selDir = gcntrl.UIServ().chooseFile(null, FileWidget.DIRECTORY_STYLE);

				if (selDir == null || (selDir.exists() && !selDir.isDirectory()))
					return;

				if (!selDir.exists())
					selDir.mkdirs();

				Rectangle selection = dcntrl.default_selection();
				if (regBox.isSelected())
				{
					selection = gcntrl.selectedRegion();
					SavePanel.saveSelection(selDir, selection);
				}

				boolean mergeWin = gcntrl.displayPanel().mergeWindows();

				TrajectoryEnsembleWindows trajsW = dcntrl.trajs();
				if (mergeWin)
				{
					trajsW = new TrajectoryEnsembleWindows();
					trajsW.wins.add(dcntrl.trajsFlat());
				}

				ScalarMapWindows densW = null;
				ScalarMapWindows diffW = null;
				VectorMapWindows driftW = null;
				double dxDrift = Double.NaN;
				PotWellsWindows wellsW = null;
				GraphWindows graphsW = null;

				ArrayList<String> overlayCats = new ArrayList<> ();

				if (SVGPanels.get("TRAJ").isSelected())
					overlayCats.add("traj");
				if (SVGPanels.get("DENS").isSelected())
				{
					MapParameters.DensityParameters dp = gcntrl.displayPanel().getDensityParams();
					densW = dcntrl.densMapWindows(dp, mergeWin);
					overlayCats.add("dens");
				}
				if (SVGPanels.get("DIFF").isSelected())
				{
					MapParameters.DiffusionParameters dp = gcntrl.displayPanel().getDiffusionParams();
					diffW = dcntrl.diffMapWindows(dp, mergeWin);
					overlayCats.add("diff");
				}
				if (SVGPanels.get("DRIFT").isSelected())
				{
					MapParameters.DriftParameters dp = gcntrl.displayPanel().getDriftParams();
					driftW = dcntrl.driftMapWindows(dp, mergeWin);
					dxDrift = dp.dx;
					overlayCats.add("drift");
				}
				if (SVGPanels.get("WELL").isSelected())
				{
					AnalysisParameters ap = gcntrl.curAnalysisParameters();
					if (ap != null &&  ap.analysisType() == AnalysisParameters.Type.WELL)
					{
						WellDetectionParameters wp = (WellDetectionParameters) ap;

						if (!mergeWin)
							wellsW = dcntrl.wells().get(wp.algoName()).get(wp);
						else
						{
							wellsW = new PotWellsWindows();
							wellsW.wins.add(new PotWells ());
							for (final PotWells wells: dcntrl.wells().get(wp.algoName()).get(wp).wins)
								wellsW.wins.get(0).wells.addAll(wells.wells);
						}
					}
					overlayCats.add("well");
				}
				if (SVGPanels.get("GRAPH").isSelected())
				{
					AnalysisParameters ap = gcntrl.curAnalysisParameters();
					if (ap != null &&  ap.analysisType() == AnalysisParameters.Type.GRAPH)
					{
						GraphConstructionParameters gp = (GraphConstructionParameters) ap;
						graphsW = dcntrl.graphs().get(gp.algoName()).get(gp);
					}
					overlayCats.add("graph");
				}

				double zoomFactor = 100;
				ArrayList<SVGWriter> writers = new ArrayList<> ();

				int maxFrame = mergeWin ? 1 : trajsW.wins.size();
				for (int i = 0; i < maxFrame; ++i)
				{
					if (SVGPanels.get("DENS").isSelected())
						writers.add(new SVGScalarMapWriter(densW.wins.get(i), zoomFactor,
								Utils.squaresInReg(densW.wins.get(i).grid(), selection)));
					if (SVGPanels.get("DIFF").isSelected())
						writers.add(new SVGScalarMapWriter(diffW.wins.get(i), zoomFactor,
								Utils.squaresInReg(diffW.wins.get(i).grid(), selection)));
					if (SVGPanels.get("TRAJ").isSelected())
					{
						TrajectoryEnsemble te = trajsW.wins.get(i);
						TrajectoriesColorScheme coloring = gcntrl.trajsOverlay().colorScheme().wins.get(i);
						if (regBox.isSelected())
						{
							te = Utils.trajsInShape(te, selection);
							coloring = new TrajectoriesColorScheme(te, coloring.coloringType());
						}
						writers.add(new SVGTrajectoriesWriter(te, 100, 1, coloring));
					}
					if (SVGPanels.get("DRIFT").isSelected())
						writers.add(new SVGVectorMapWriter(driftW.wins.get(i), dxDrift / 2, zoomFactor,
								Utils.squaresInReg(driftW.wins.get(i).grid(), selection)));
					if (wellsW != null && SVGPanels.get("WELL").isSelected())
					{
						ArrayList<PotWell> tmp1 = Utils.wellsInReg(wellsW.wins.get(i).wells, selection);
						PotWell[] tmp = new PotWell[tmp1.size()];
						tmp1.toArray(tmp);
						writers.add(new SVGWellsWriter(tmp, zoomFactor));
					}
					if (graphsW != null && SVGPanels.get("GRAPH").isSelected())
						writers.add(new SVGGraphWriter(Graph.GraphInRegion(graphsW.wins.get(i), selection), zoomFactor));
					writers.add(new SVGScaleBarWriter(zoomFactor, 1, Color.BLACK));

					SVGWriter[] tmp = new SVGWriter[writers.size()];
					writers.toArray(tmp);
					try
					{
						SVGWriter.saveSVG(selDir.getAbsolutePath() + String.format("/overlay_" + String.join("_", overlayCats) + "_%d.svg", i),
								tmp, tmp[0].minp());
					}
					catch (IOException e1)
					{
						e1.printStackTrace();
					}
					writers.clear();
				}
			}
		});

		this.regBox = new JCheckBox("Restrict to region", false);

		initGUI();
	}

	public void initGUI()
	{
		setLayout(new GridBagLayout());


		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0, 10, 10, 0);

		c.gridy = 0;
		c.gridx = 0;
		this.add(new JLabel("Trajs."), c);
		c.gridx = 1;
		this.add(this.SVGPanels.get("TRAJ"), c);

		c.insets = new Insets(0, 10, 10, 0);
		c.gridy = 1;
		c.gridx = 0;
		this.add(new JLabel("Dens."), c);
		c.gridx = 1;
		this.add(this.SVGPanels.get("DENS"), c);

		c.gridy = 2;
		c.gridx = 0;
		this.add(new JLabel("Diff."), c);
		c.gridx = 1;
		this.add(this.SVGPanels.get("DIFF"), c);

		c.gridy = 3;
		c.gridx = 0;
		this.add(new JLabel("Drift"), c);
		c.gridx = 1;
		this.add(this.SVGPanels.get("DRIFT"), c);

		c.gridy = 4;
		c.gridx = 0;
		this.add(new JLabel("Wells"), c);
		c.gridx = 1;
		this.add(this.SVGPanels.get("WELL"), c);

		c.gridy = 5;
		c.gridx = 0;
		this.add(new JLabel("Graph"), c);
		c.gridx = 1;
		this.add(this.SVGPanels.get("GRAPH"), c);


		c.gridx = 0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 10, 5, 10);
		c.gridy = 6;
		c.gridwidth = 2;
		this.add(this.SVGSaveIndividualButton, c);

		c.gridy = 7;
		c.gridwidth = 2;
		this.add(this.SVGSaveOverlayButton, c);
		
		c.gridy = 8;
		c.gridwidth = 2;
		this.add(this.regBox, c);
	}
}
