package fiji.plugin.SPTAnalysis;

import java.awt.BorderLayout;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.xml.bind.annotation.XmlTransient;

import org.scijava.ui.UIService;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.GeometryFactory;

import fiji.plugin.SPTAnalysis.graphConstruction.GraphConstructionParameters;
import fiji.plugin.SPTAnalysis.gui.AnalysisResultSelectionPanel;
import fiji.plugin.SPTAnalysis.gui.DisplayPanel;
import fiji.plugin.SPTAnalysis.gui.GraphConstructionParametersSelectionPanel;
import fiji.plugin.SPTAnalysis.gui.GraphResultPanel;
import fiji.plugin.SPTAnalysis.gui.GraphResultParameterPanel;
import fiji.plugin.SPTAnalysis.gui.HistogramPanel;
import fiji.plugin.SPTAnalysis.gui.MainPanel;
import fiji.plugin.SPTAnalysis.gui.SavePanel;
import fiji.plugin.SPTAnalysis.gui.WellDetectionParametersSelectionPanel;
import fiji.plugin.SPTAnalysis.gui.WellResultPanel;
import fiji.plugin.SPTAnalysis.gui.WellResultParameterPanel;
import fiji.plugin.SPTAnalysis.struct.AnalysisParameters;
import fiji.plugin.SPTAnalysis.struct.GraphWindows;
import fiji.plugin.SPTAnalysis.struct.MapParameters;
import fiji.plugin.SPTAnalysis.struct.MyPolygon;
import fiji.plugin.SPTAnalysis.struct.PotWell;
import fiji.plugin.SPTAnalysis.struct.PotWellsWindows;
import fiji.plugin.SPTAnalysis.struct.Rectangle;
import fiji.plugin.SPTAnalysis.struct.Shape;
import fiji.plugin.SPTAnalysis.struct.TrajectoriesColorScheme;
import fiji.plugin.SPTAnalysis.struct.TrajectoriesColorSchemeWindows;
import fiji.plugin.SPTAnalysis.visualization.ColorBarOverlay;
import fiji.plugin.SPTAnalysis.visualization.GraphOverlay;
import fiji.plugin.SPTAnalysis.visualization.ScalarMapOverlay;
import fiji.plugin.SPTAnalysis.visualization.ScaleBarOverlay;
import fiji.plugin.SPTAnalysis.visualization.TrajectoriesOverlay;
import fiji.plugin.SPTAnalysis.visualization.VectorMapOverlay;
import fiji.plugin.SPTAnalysis.visualization.WellOverlay;
import fiji.plugin.SPTAnalysis.wellDetection.WellDetectionParameters;
import fiji.plugin.trackmate.util.TMUtils;
import ij.ImageListener;
import ij.ImagePlus;
import ij.gui.Roi;

public class GUIController
{
	private UIService UIServ;
	private DataController dcntrl;
	private ImagePlus imp;

	private HashMap<AnalysisParameters, JFrame> resultFrames;
	private HashMap<WellDetectionParameters, WellResultPanel> wellPanels;
	private HashMap<GraphConstructionParameters, GraphResultPanel> graphPanels;
	private HashMap<AnalysisParameters, JFrame> parameterFrames;

	private HashMap<MapParameters.DensityParameters, JFrame> densHistFrames;
	private HashMap<MapParameters.DensityParameters, HistogramPanel> densHistPanels;

	private HashMap<MapParameters.DiffusionParameters, JFrame> diffHistFrames;
	private HashMap<MapParameters.DiffusionParameters, HistogramPanel> diffHistPanels;

	private JFrame instVelHistFrame;
	private HistogramPanel instVelHistPanel;

	private HashMap<AnalysisParameters, Roi> overlays;

	private TrajectoriesOverlay trajsOverlay;
	private boolean regenerateTrajsOverlay;

	private JFrame frame;
	private MainPanel mainPan;


	@XmlTransient
	private int curTimeWin;

	@XmlTransient
	private AnalysisParameters curResults;

	public GUIController(UIService uiserv, DataController dcntrl, ImagePlus imp)
	{
		this.UIServ = uiserv;
		this.dcntrl = dcntrl;
		this.imp = imp;

		this.imp.getCanvas().addMouseListener(new MouseListener()
		{
			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (curResults == null)
					return;

				int frame = imp.getFrame() - 1;
				double magn = imp.getCanvas().getMagnification();
				double[] calib = TMUtils.getSpatialCalibration(imp);
				int xcorn = imp.getCanvas().offScreenX(0);
				int ycorn = imp.getCanvas().offScreenY(0);

				double[] p = new double[] {(e.getX() / magn + xcorn) * calib[0],
						   (e.getY() / magn + ycorn) * calib[0]};

				if (curResults.analysisType() == AnalysisParameters.Type.WELL)
				{
					WellDetectionParameters curRes = (WellDetectionParameters) curResults;

					PotWellsWindows wells = dcntrl.wells().get(curRes.algoName()).get(curRes);
	
					ArrayList<PotWell> curWells = null;
					if (displayPanel().mergeWindows())
						curWells = wells.flattenArray();
					else
						curWells = wells.wins.get(frame).wells;
	
					for (PotWell w: curWells)
					{
						if (w.ell().inside(p))
						{
							wellPanels.get(curResults).highlight(w);
							setSelectedWell(w);
						}
					}
				}
				else if (curResults.analysisType() == AnalysisParameters.Type.GRAPH)
				{
					GraphConstructionParameters curRes = (GraphConstructionParameters) curResults;

					for (Shape s: dcntrl.graphs().get(curRes.algoName()).get(curRes).wins.get(frame).nodes().values())
					{
						if (s.inside(p))
						{
							graphPanels.get(curResults).highlight(s);
							setSelectedNode(s);
						}
					}
				}
			}
		});

		ImagePlus.addImageListener(new ImageListener()
		{
			@Override
			public void imageOpened(ImagePlus imp) {}
			@Override
			public void imageClosed(ImagePlus imp) {}

			@Override
			public void imageUpdated(ImagePlus imp)
			{
				if (imp.getFrame() - 1 != curTimeWin)
				{
					curTimeWin = imp.getFrame() - 1;
					updateHists();
				}
			}
		});

		this.resultFrames = new HashMap<> ();
		this.wellPanels = new HashMap<> ();
		this.graphPanels = new HashMap<> ();
		this.parameterFrames = new HashMap<> ();

		this.densHistFrames = new HashMap<> ();
		this.densHistPanels = new HashMap<> ();

		this.diffHistFrames = new HashMap<> ();
		this.diffHistPanels = new HashMap<> ();

		this.instVelHistFrame = null;
		this.instVelHistPanel = null;

		this.overlays = new HashMap<> ();
		this.trajsOverlay = null;
		this.regenerateTrajsOverlay = true;

		this.curTimeWin = 1;
	}

	public void regenerateTrajsOverlay()
	{
		this.regenerateTrajsOverlay = true;
	}

	public void selectWellResultDisplay(AnalysisParameters ps)
	{
		if (this.curResults != null && this.curResults.equals(ps))
		{
			this.curResults = null;
			this.resultFrames.get(ps).setVisible(false);
		}
		else
		{
			this.curResults = ps;
			this.resultFrames.get(ps).setVisible(true);
		}

		this.display();
	}

	public void initPanels()
	{
		this.frame = new JFrame();
		this.frame.setSize(400, 400);
		this.frame.setTitle("SPT analysis");

		this.mainPan = new MainPanel(this.dcntrl, this, this.frame);

		this.frame.add(this.mainPan, BorderLayout.CENTER);
		this.frame.pack();
		this.frame.setVisible(true);

		for (String algoName: this.dcntrl.wells().keySet())
		{
			for (WellDetectionParameters ps: this.dcntrl.wells().get(algoName).keySet())
			{
				this.initResultPanels(ps);
				this.overlays.put(ps, new WellOverlay(this.imp,
						this.dcntrl.wells().get(algoName).get(ps),
						this.displayPanel().mergeWindows(),
						this.displayPanel().colorByFamily()));
			}
		}

		for (String algoName: this.dcntrl.graphs().keySet())
		{
			for (GraphConstructionParameters ps: this.dcntrl.graphs().get(algoName).keySet())
			{
				this.initResultPanels(ps);
				this.overlays.put(ps, new GraphOverlay(this.imp,
						this.dcntrl.graphs().get(algoName).get(ps),
						GraphResultPanel.defMinLinkDisp,
						this.displayPanel().mergeWindows()));
			}
		}
	}

	public void initResultPanels(AnalysisParameters ps)
	{
		JFrame fram = new JFrame();
		fram.setSize(500, 300);

		if (ps.analysisType() == AnalysisParameters.Type.WELL)
		{
			WellDetectionParameters wps = (WellDetectionParameters) ps;
			fram.setTitle("Detected wells");
			WellResultPanel wrp = new WellResultPanel(this.UIServ, this, this.dcntrl,
					this.dcntrl.wells().get(wps.algoName()).get(ps));
			fram.add(wrp);
			this.wellPanels.put(wps, wrp);
		}
		else if (ps.analysisType() == AnalysisParameters.Type.GRAPH)
		{
			GraphConstructionParameters gps = (GraphConstructionParameters) ps;
			fram.setTitle("Detected Graph");
			GraphResultPanel grp = new GraphResultPanel(this.UIServ, this,
					this.dcntrl.graphs().get(gps.algoName()).get(gps));
			fram.add(grp);
			this.graphPanels.put(gps, grp);
		}

		fram.setVisible(false);
		fram.pack();
		this.resultFrames.put(ps, fram);
	}

	public void AnalysisResultParameterPanels(AnalysisParameters ps)
	{
		if (!this.parameterFrames.containsKey(ps))
		{
			JFrame fram = new JFrame();
			fram.setSize(300, 300);
			fram.setTitle(String.format("%s parameters", ps.expName()));

			if (ps.analysisType() == AnalysisParameters.Type.WELL)
			{
				WellResultParameterPanel wrpp =
						new WellResultParameterPanel(this.UIServ,
								(WellDetectionParameters) ps);
				fram.add(new JScrollPane(wrpp));
			}
			else if (ps.analysisType() == AnalysisParameters.Type.GRAPH)
			{
				GraphResultParameterPanel grpp =
						new GraphResultParameterPanel(this.UIServ,
								(GraphConstructionParameters) ps);
				fram.add(new JScrollPane(grpp));
			}
			this.parameterFrames.put(ps, fram);
		}

		JFrame fram = this.parameterFrames.get(ps);
		fram.pack();
		fram.setVisible(!fram.isVisible());
	}

	public void displayDensHist(MapParameters.DensityParameters ps)
	{
		if (!this.densHistFrames.containsKey(ps))
		{
			this.densHistPanels.put(ps, new HistogramPanel(this, this.dcntrl, this.dcntrl.densMaps(false).get(ps).getValues(),
					this.dcntrl.densMaps(false).get(ps).getPositions(), this.imp.getFrame() - 1, "Density", "point/µm²"));
			JFrame frame = new JFrame();
			frame.setTitle("Density Histogram");
			frame.add(this.densHistPanels.get(ps));
			frame.pack();
			frame.setVisible(false);
			this.densHistFrames.put(ps, frame);
		}

		this.densHistFrames.get(ps).setVisible(!this.densHistFrames.get(ps).isVisible());
	}

	public void displayDiffHist(MapParameters.DiffusionParameters ps)
	{
		if (!this.diffHistFrames.containsKey(ps))
		{
			this.diffHistPanels.put(ps, new HistogramPanel(this, this.dcntrl, this.dcntrl.diffMaps(false).get(ps).getValues(),
					this.dcntrl.diffMaps(false).get(ps).getPositions(), this.imp.getFrame() - 1, "Diffusion coefficient", "µm²/s"));
			JFrame frame = new JFrame();
			frame.setTitle("Diffusion Histogram");
			frame.add(this.diffHistPanels.get(ps));
			frame.pack();
			frame.setVisible(false);
			this.diffHistFrames.put(ps, frame);
		}

		this.diffHistFrames.get(ps).setVisible(!this.diffHistFrames.get(ps).isVisible());
	}

	public void displayInstVelHist()
	{
		if (this.instVelHistFrame == null)
		{
			this.instVelHistPanel = new HistogramPanel(this, this.dcntrl, this.dcntrl.trajs().instantaneousVelocities(),
					this.dcntrl.trajs().instantaneousVelocitiesPositions(), this.imp.getFrame() - 1, "Instantaneous velocity", "µm/s");
			this.instVelHistFrame = new JFrame();
			this.instVelHistFrame.setTitle("Inst. Vel. Histogram");
			this.instVelHistFrame.add(this.instVelHistPanel);
			this.instVelHistFrame.pack();
			this.instVelHistFrame.setVisible(false);
		}

		this.instVelHistFrame.setVisible(!this.instVelHistFrame.isVisible());
	}

	private void updateHists()
	{
		for (MapParameters.DensityParameters ps: this.densHistPanels.keySet())
				this.densHistPanels.get(ps).updateCurTimeWindow(this.curTimeWin);
		for (MapParameters.DiffusionParameters ps: this.diffHistPanels.keySet())
				this.diffHistPanels.get(ps).updateCurTimeWindow(this.curTimeWin);
		if (this.instVelHistPanel != null)
			this.instVelHistPanel.updateCurTimeWindow(this.curTimeWin);
	}

	public void display()
	{
		this.imp.getOverlay().clear();

		boolean mergeWin = this.displayPanel().mergeWindows();
		//order matters here
		HashMap<String, Roi> todoOverlays = new HashMap<> ();
		for (String val: this.displayPanel().toDisplay())
		{
			if (val.equals("Trajectories"))
			{
				if (this.regenerateTrajsOverlay)
				{
					this.trajsOverlay = new TrajectoriesOverlay(this.imp, this.dcntrl.trajs(),
							TrajectoriesColorSchemeWindows.createColorSchemes(this.dcntrl.trajs(),
									this.displayPanel().trajectoryColor()),
							this.displayPanel().mergeWindows(), this.displayPanel().trajMaxInstVel());
					this.regenerateTrajsOverlay = false;
				}
				todoOverlays.put("Trajs", this.trajsOverlay);
				if (this.trajsOverlay.colorScheme().coloringType() == TrajectoriesColorScheme.ColoringType.InstVel)
					todoOverlays.put("ColorBar", new ColorBarOverlay(this.imp, "%.2f",
							this.trajsOverlay.colorScheme().maxIvelsWins(), this.displayPanel().mergeWindows()));
			}
			else if (val.equals("Density"))
			{
				MapParameters.DensityParameters densP = this.displayPanel().getDensityParams();

				todoOverlays.put("Density", new ScalarMapOverlay(this.imp, this.dcntrl.densMapWindows(densP, mergeWin),
						this.displayPanel().mergeWindows()));
				todoOverlays.put("ColorBar", new ColorBarOverlay(this.imp, "%.0f",
						this.dcntrl.densMapWindows(densP, mergeWin).maxs(), this.displayPanel().mergeWindows()));
			}
			else if (val.equals("Diffusion"))
			{
				MapParameters.DiffusionParameters diffP = this.displayPanel().getDiffusionParams();

				todoOverlays.put("Diffusion", new ScalarMapOverlay(this.imp, this.dcntrl.diffMapWindows(diffP, mergeWin),
						this.displayPanel().mergeWindows()));
				todoOverlays.put("ColorBar", new ColorBarOverlay(this.imp, "%.2f",
						this.dcntrl.diffMapWindows(diffP, mergeWin).maxs(), this.displayPanel().mergeWindows()));
			}
			else if (val.equals("Drift"))
			{
				MapParameters.DriftParameters driftP = this.displayPanel().getDriftParams();

				todoOverlays.put("Drift", new VectorMapOverlay(this.imp, this.dcntrl.driftMapWindows(driftP, mergeWin),
						this.displayPanel().driftSizeMult(), this.displayPanel().mergeWindows()));
			}
		}

		if (this.curResults != null)
		{
			if (curResults.analysisType() == AnalysisParameters.Type.WELL)
			{
				WellOverlay tmp = (WellOverlay) this.overlays.get(this.curResults);
				tmp.setShowAllWindows(this.displayPanel().mergeWindows());
				tmp.setColorByFamily(this.displayPanel().colorByFamily());
				todoOverlays.put("Wells", tmp);
			}
			else if (curResults.analysisType() == AnalysisParameters.Type.GRAPH)
				todoOverlays.put("Graph", this.overlays.get(this.curResults));
		}

		if (todoOverlays.containsKey("Density"))
			this.imp.getOverlay().add(todoOverlays.get("Density"));
		if (todoOverlays.containsKey("Diffusion"))
			this.imp.getOverlay().add(todoOverlays.get("Diffusion"));
		if (todoOverlays.containsKey("Drift"))
			this.imp.getOverlay().add(todoOverlays.get("Drift"));

		if (todoOverlays.containsKey("Trajs"))
			this.imp.getOverlay().add(todoOverlays.get("Trajs"));

		if (todoOverlays.containsKey("Graph"))
			this.imp.getOverlay().add(todoOverlays.get("Graph"));
		if (todoOverlays.containsKey("Wells"))
			this.imp.getOverlay().add(todoOverlays.get("Wells"));

		if (todoOverlays.containsKey("ColorBar"))
			this.imp.getOverlay().add(todoOverlays.get("ColorBar"));

		this.imp.getOverlay().add(new ScaleBarOverlay(this.imp,
				this.displayPanel().scaleBarLength()));

		this.imp.updateAndDraw();
	}

	public MainPanel mainPan()
	{
		return this.mainPan;
	}

	public void addWellOverlay(WellDetectionParameters ps, PotWellsWindows res)
	{
		this.overlays.put(ps, new WellOverlay(this.imp, res,
				this.displayPanel().mergeWindows(),
				this.displayPanel().colorByFamily()));
	}

	public void addOrModifyGraphOverlay(final GraphConstructionParameters ps,
										final GraphWindows grs, final int minDispLink)
	{
		this.overlays.put(ps, new GraphOverlay(this.imp, grs, minDispLink,
						  this.displayPanel().mergeWindows()));
	}

	public void modifyGraphOverlay(final GraphWindows grs, final int minDispLink)
	{
		this.overlays.put(this.curResults, new GraphOverlay(this.imp, grs, minDispLink,
						  this.displayPanel().mergeWindows()));
	}


	public AnalysisParameters curAnalysisParameters()
	{
		return this.curResults;
	}

	public void setSelectedWell(final PotWell w)
	{
		((WellOverlay) overlays.get(curResults)).setSelected(w);
		display();
	}

	public void setSelectedNode(final Shape s)
	{
		((GraphOverlay) overlays.get(curResults)).setSelected(s);
		display();
	}


	public WellDetectionParametersSelectionPanel wellDetectionPanel()
	{
		return this.mainPan.selectionPanel().wellDetectionParametersSelectionPanel();
	}

	public GraphConstructionParametersSelectionPanel graphConstructionPanel()
	{
		return this.mainPan.selectionPanel().graphConstructionParametersSelectionPanel();
	}

	public DisplayPanel displayPanel()
	{
		return this.mainPan.selectionPanel().displayPanel();
	}

	public GraphConstructionParametersSelectionPanel dbscanPanel()
	{
		return this.mainPan.selectionPanel().graphConstructionParametersSelectionPanel();
	}

	public SavePanel savePanel()
	{
		return this.mainPan.selectionPanel().savePanel();
	}
	
	public AnalysisResultSelectionPanel analysisResultSelectionPanel()
	{
		return this.mainPan.analysisResultSelectionPanel();
	}

	public UIService UIServ()
	{
		return this.UIServ;
	}

	public boolean hasSelection()
	{
		return this.imp.getRoi() != null;
	}

	public Rectangle selectedRegion()
	{
		Roi sel = this.imp.getRoi();

		if (sel == null)
			return this.dcntrl.default_selection();

		double pw = this.imp.getCalibration().pixelWidth;
		double ph = this.imp.getCalibration().pixelHeight;

		double[] p = new double[] {sel.getBounds().x * pw, sel.getBounds().y * ph};

		Rectangle reg = new Rectangle(p,
									  new double[] {p[0] + sel.getBounds().width * pw,
											  		p[1] + sel.getBounds().height * ph});

		return reg;
	}

	public MyPolygon getPolygon()
	{
		Roi sel = this.imp.getRoi();

		double pw = this.imp.getCalibration().pixelWidth;
		double ph = this.imp.getCalibration().pixelHeight;

		GeometryFactory geo_facto = new GeometryFactory();

		Polygon sel_poly = sel.getPolygon();
		CoordinateSequence cse =
				geo_facto.getCoordinateSequenceFactory().create(sel_poly.xpoints.length+1, 2);
		int i = 0;
		while (i < sel_poly.xpoints.length)
		{
			cse.setOrdinate(i, 0, sel_poly.xpoints[i] * pw);
			cse.setOrdinate(i, 1, sel_poly.ypoints[i] * ph);
			++i;
		}
		cse.setOrdinate(i, 0, sel_poly.xpoints[0] * pw);
		cse.setOrdinate(i, 1, sel_poly.ypoints[0] * ph);

		return new MyPolygon(geo_facto.createPolygon(cse));
	}
	
	public int curFrame()
	{
		return this.imp.getFrame() - 1;
	}
	
	public TrajectoriesOverlay trajsOverlay()
	{
		return this.trajsOverlay;
	}
}
