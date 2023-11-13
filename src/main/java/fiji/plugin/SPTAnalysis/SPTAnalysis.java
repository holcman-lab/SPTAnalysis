package fiji.plugin.SPTAnalysis;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.display.DisplayService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

import fiji.plugin.SPTAnalysis.graphConstruction.GraphConstruction;
import fiji.plugin.SPTAnalysis.gui.DisplayPanel;
import fiji.plugin.SPTAnalysis.gui.GraphResultPanel;
import fiji.plugin.SPTAnalysis.gui.InputPanel;
import fiji.plugin.SPTAnalysis.readers.CSVReaderOptions;
import fiji.plugin.SPTAnalysis.readers.TrajectoryCSVReader;
import fiji.plugin.SPTAnalysis.struct.GraphWindows;
import fiji.plugin.SPTAnalysis.struct.MapParameters;
import fiji.plugin.SPTAnalysis.struct.PlugLogger;
import fiji.plugin.SPTAnalysis.struct.PotWellsWindows;
import fiji.plugin.SPTAnalysis.struct.ScalarMap;
import fiji.plugin.SPTAnalysis.struct.ScalarMapWindows;
import fiji.plugin.SPTAnalysis.struct.SquareGrid;
import fiji.plugin.SPTAnalysis.struct.TimeWindows;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsembleWindows;
import fiji.plugin.SPTAnalysis.struct.VectorMap;
import fiji.plugin.SPTAnalysis.struct.VectorMapWindows;
import fiji.plugin.SPTAnalysis.wellDetection.WellDetection;
import fiji.plugin.SPTAnalysis.wellDetection.WellDetectionParameters;
import fiji.plugin.SPTAnalysis.wellLinker.DistanceWellLinker;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.Overlay;
import ij.measure.Calibration;
import net.imagej.Dataset;

@Plugin(type = Command.class, headless = true, menuPath = "Plugins>SPTAnalysis")
public class SPTAnalysis implements Command
{
	@Parameter
	private UIService UIServ;

	@Parameter
	private DisplayService dispServ;

	@Parameter(type = ItemIO.OUTPUT)
	Dataset img;

	private JFrame inFrame;
	private InputPanel inPanel;

	DataController dcntrl;
	GUIController pcntrl;

	public static void main(String[] args)
	{
		System.out.println( "Hello World!" );
	}

	private void launchApp()
	{
		this.inFrame.setVisible(false);
		this.inFrame.dispose();

		dcntrl.filterTrajs();

		double[] pmax = dcntrl.base_trajs().maxCoords();

		String fname = this.inPanel.fname();
		if (fname == null)
			fname = "Trajectories";

		ImagePlus imp = IJ.createHyperStack(fname,
				(int) Math.ceil(pmax[0] / this.inPanel.displayPxSize()),
				(int) Math.ceil(pmax[1] / this.inPanel.displayPxSize()),
				1, 1, this.dcntrl.timeWindows().idxMax() + 1, 8);

		if (imp.getOverlay() == null)
			imp.setOverlay(new Overlay());

		Calibration calib = new Calibration();
		calib.setXUnit("micron");
		calib.setYUnit("micron");
		calib.pixelWidth = this.inPanel.displayPxSize();
		calib.pixelHeight = this.inPanel.displayPxSize();
		imp.setCalibration(calib);
		imp.show();

		this.pcntrl = new GUIController(this.UIServ, this.dcntrl, imp);
		this.pcntrl.initPanels();

		ScalarMapWindows denss = new ScalarMapWindows();
		ScalarMapWindows diffs = new ScalarMapWindows();
		VectorMapWindows drifts = new VectorMapWindows();

		MapParameters.DensityParameters deps = new MapParameters.DensityParameters(
				DisplayPanel.DEFAULT_DENS_BIN_W, ScalarMap.DensityOption.DENS, 0);
		MapParameters.DiffusionParameters dips = new MapParameters.DiffusionParameters(
				DisplayPanel.DEFAULT_DIFF_BIN_W, DisplayPanel.DEFAULT_DIFF_MIN_NPTS,
				false, DisplayPanel.DEFAULT_DIFF_FILTER_SIZE);
		MapParameters.DriftParameters drps = new MapParameters.DriftParameters(
				DisplayPanel.DEFAULT_DRIFT_BIN_W, DisplayPanel.DEFAULT_DRIFT_MIN_NPTS,
				false, DisplayPanel.DEFAULT_DRIFT_FILTER_SIZE);

		for (TrajectoryEnsemble te: this.dcntrl.trajs().wins)
		{
			denss.wins.add(ScalarMap.genDensityMap(new SquareGrid(te, deps.dx), te, deps));
			diffs.wins.add(ScalarMap.genDiffusionMap(new SquareGrid(te, dips.dx), te, dips));
			drifts.wins.add(VectorMap.genDriftMap(new SquareGrid(te, drps.dx), te, drps));
		}

		this.dcntrl().attachDens(deps, denss, false);
		this.dcntrl().attachDiff(dips, diffs, false);
		this.dcntrl().attachDrift(drps, drifts, false);

		this.pcntrl.display();
	}

	@Override
	public void run()
	{
		this.inFrame = new JFrame();
		this.inFrame.setSize(500, 300);
		this.inFrame.setTitle("Input");

		this.inPanel = new InputPanel(this, this.UIServ, this.inFrame);

		this.inFrame.add(new JScrollPane(this.inPanel));
		this.inFrame.pack();
		this.inFrame.setVisible(true);
	}

	public void loadRawTrajectories(String path, CSVReaderOptions csvo)
	{
		TrajectoryCSVReader read = new TrajectoryCSVReader(path, csvo);
		TimeWindows tws = null;

		try
		{
			TrajectoryEnsemble trajs = read.read();
			if (this.inPanel.useTimeWindow())
				tws = new TimeWindows(trajs, this.inPanel.timeWindowDuration(),
						this.inPanel.timeWindowOverlap());
			else
				tws = TimeWindows.singleWindow(trajs);

			this.dcntrl().attachTrajs(path, new TrajectoryEnsembleWindows(trajs, tws), tws,
					this.inPanel.getCSVReaderOptions().pxSize());

			launchApp();
		}
		catch (Exception e)
		{
			System.out.println("Error loading file, please verify the format");
			e.printStackTrace();
		}
	}

	public DataController dcntrl()
	{
		if (this.dcntrl == null)
			this.dcntrl = new DataController();
		return this.dcntrl;
	}

	public void setDcntrl(DataController cntrl)
	{
		assert(this.dcntrl == null);
		this.dcntrl = cntrl;
		launchApp();
	}

	public static void detectWells(DataController dcntrl, GUIController pcntrl, WellDetection algo,
			DistanceWellLinker wlink, final PlugLogger log)
	{
		WellDetectionParameters ps = algo.getParameters();
		PotWellsWindows res = WellDetection.detectWellsTimeWindows(algo, dcntrl.trajs(), log);

		dcntrl.addWellDetection(ps, res);
		dcntrl.wells().get(algo.name()).get(ps).linkWells(wlink);

		pcntrl.analysisResultSelectionPanel().addNewResults(ps);
		pcntrl.addWellOverlay(ps, res);
		pcntrl.initResultPanels(ps);

		System.out.println("Well detection process completed");
	}

	public static void constructGraph(DataController dcntrl, GUIController pcntrl,
			GraphConstruction algo, final PlugLogger log)
	{
		GraphWindows res = GraphConstruction.detectGraphsTimeWindows(algo, dcntrl.trajs(), log);
		dcntrl.addGraph(algo.getParameters(), res);

		pcntrl.mainPan().analysisResultSelectionPanel().addNewResults(algo.getParameters());
		pcntrl.addOrModifyGraphOverlay(algo.getParameters(), res, GraphResultPanel.defMinLinkDisp);
		pcntrl.initResultPanels(algo.getParameters());

		System.out.println("Graph construction process completed");
	}
}