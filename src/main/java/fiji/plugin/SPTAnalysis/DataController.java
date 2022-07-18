package fiji.plugin.SPTAnalysis;

import java.io.File;
import java.util.HashMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import fiji.plugin.SPTAnalysis.graphConstruction.GraphConstructionParameters;
import fiji.plugin.SPTAnalysis.struct.Graph;
import fiji.plugin.SPTAnalysis.struct.GraphWindows;
import fiji.plugin.SPTAnalysis.struct.MapParameters;
import fiji.plugin.SPTAnalysis.struct.MyPolygon;
import fiji.plugin.SPTAnalysis.struct.PotWells;
import fiji.plugin.SPTAnalysis.struct.PotWellsWindows;
import fiji.plugin.SPTAnalysis.struct.Rectangle;
import fiji.plugin.SPTAnalysis.struct.ScalarMapWindows;
import fiji.plugin.SPTAnalysis.struct.TimeWindows;
import fiji.plugin.SPTAnalysis.struct.Trajectory;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsembleWindows;
import fiji.plugin.SPTAnalysis.struct.VectorMapWindows;
import fiji.plugin.SPTAnalysis.wellDetection.WellDetectionParameters;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DataController")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataController
{
	public static enum overlays {TRAJECTORIES, DENSITY, DIFFUSION, DRIFT};

	@XmlTransient
	private HashMap<MapParameters.DensityParameters, ScalarMapWindows> densMaps;
	@XmlTransient
	private HashMap<MapParameters.DiffusionParameters, ScalarMapWindows> diffMaps;
	@XmlTransient
	private HashMap<MapParameters.DriftParameters, VectorMapWindows> driftMaps;

	@XmlTransient
	private HashMap<MapParameters.DensityParameters, ScalarMapWindows> densMapsFlat;
	@XmlTransient
	private HashMap<MapParameters.DiffusionParameters, ScalarMapWindows> diffMapsFlat;
	@XmlTransient
	private HashMap<MapParameters.DriftParameters, VectorMapWindows> driftMapsFlat;

	private String filename;
	private TimeWindows timeWins;
	private TrajectoryEnsembleWindows base_trajs;

	private int traj_min_size;
	private MyPolygon traj_region;

	@XmlTransient
	private TrajectoryEnsembleWindows trajs;

	@XmlTransient
	private TrajectoryEnsemble trajsFlat;

	@XmlJavaTypeAdapter(XMLAdapters.Wells.class)
	private HashMap<String, HashMap<WellDetectionParameters, PotWellsWindows>> wells;

	@XmlJavaTypeAdapter(XMLAdapters.Graphs.class)
	private HashMap<String, HashMap<GraphConstructionParameters, GraphWindows>> graphs;

	private int analysis_name_cnt;

	public static DataController controllerFromFile(String path)
	{
		DataController res = null;

		try
		{
			File inFile = new File(path);
			JAXBContext jaxbContext = JAXBContext.newInstance(DataController.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			res = (DataController) jaxbUnmarshaller.unmarshal(inFile);
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}

		return res;
	}

	public DataController()
	{
		this.densMaps = new HashMap<> ();
		this.diffMaps = new HashMap<> ();
		this.driftMaps = new HashMap<> ();

		this.densMapsFlat = new HashMap<> ();
		this.diffMapsFlat = new HashMap<> ();
		this.driftMapsFlat = new HashMap<> ();

		this.filename = "";

		this.base_trajs = null;
		this.traj_min_size = 0;
		this.traj_region = null;

		this.trajs = null;
		this.trajsFlat = null;
		this.wells = new HashMap<> ();
		this.graphs = new HashMap<> ();

		this.analysis_name_cnt = 0;
	}

	public void attachTrajs(String filename, TrajectoryEnsembleWindows te, TimeWindows tws)
	{
		this.filename = filename;
		this.base_trajs = te;
		this.trajs = this.base_trajs;
		this.trajsFlat = this.trajs.flatten();
		this.timeWins = tws;
	}

	protected static TrajectoryEnsembleWindows filterTrajsOnSize(
			final TrajectoryEnsembleWindows tew, int minPts)
	{
		if (minPts == 0)
			return tew;

		TrajectoryEnsembleWindows res = new TrajectoryEnsembleWindows();
		for (int k = 0; k < tew.wins.size(); ++k)
		{
			final TrajectoryEnsemble te = tew.wins.get(k);

			res.wins.add(new TrajectoryEnsemble());
			for (final Trajectory tr: te.trajs())
			{
				if (tr.points().size() >= minPts)
					res.wins.get(k).trajs().add(tr);
			}
		}

		return res;
	}

	protected static TrajectoryEnsembleWindows filterTrajsOnRegion(
			final TrajectoryEnsembleWindows tew, final MyPolygon poly)
	{
		if (poly == null)
			return tew;

		TrajectoryEnsembleWindows res = new TrajectoryEnsembleWindows();
		for (final TrajectoryEnsemble te: tew.wins)
			res.wins.add(Utils.trajsInShape(te, poly));

		return res;
	}

	public void filterTrajs()
	{
		this.trajs = filterTrajsOnSize(this.base_trajs, this.traj_min_size);
		this.trajs = filterTrajsOnRegion(this.trajs, this.traj_region);
		this.trajsFlat = this.trajs.flatten();
	}

	public void updateTrajFilteringSize(int min_size)
	{
		if (min_size != this.traj_min_size)
		{
			this.traj_min_size = min_size;
			this.filterTrajs();
		}
	}

	public int traj_min_size()
	{
		return this.traj_min_size;
	}

	public MyPolygon traj_region()
	{
		return this.traj_region;
	}

	public void updateTrajFilteringRegion(final MyPolygon poly)
	{
		this.traj_region = poly;
		this.filterTrajs();
	}

	public void attachDens(MapParameters.DensityParameters ps, ScalarMapWindows denss,
			boolean flattenTimeWindow)
	{
		if (flattenTimeWindow)
			this.densMapsFlat.put(ps, denss);
		else
			this.densMaps.put(ps, denss);
	}

	public void attachDiff(MapParameters.DiffusionParameters ps, ScalarMapWindows diffs,
			boolean flattenTimeWindow)
	{
		if (flattenTimeWindow)
			this.diffMapsFlat.put(ps, diffs);
		else
			this.diffMaps.put(ps, diffs);
	}

	public void attachDrift(MapParameters.DriftParameters ps, VectorMapWindows drifts,
			boolean flattenTimeWindow)
	{
		if (flattenTimeWindow)
			this.driftMapsFlat.put(ps, drifts);
		else
			this.driftMaps.put(ps, drifts);
	}

	public TrajectoryEnsembleWindows base_trajs()
	{
		return this.base_trajs;
	}

	public TrajectoryEnsemble trajsFlat()
	{
		return this.trajsFlat;
	}

	public String filename()
	{
		return this.filename;
	}

	public TrajectoryEnsembleWindows trajs()
	{
		if (trajs == null)
			this.trajs = this.base_trajs;
		return this.trajs;
	}

	public TimeWindows timeWindows()
	{
		return this.timeWins;
	}

	public HashMap<MapParameters.DensityParameters, ScalarMapWindows> densMaps(boolean flattenTimeWindow)
	{
		if (flattenTimeWindow)
			return this.densMapsFlat;
		return this.densMaps;
	}

	public ScalarMapWindows densMapWindows(final MapParameters.DensityParameters ps, boolean flattenTimeWindow)
	{
		if (!this.densMaps(flattenTimeWindow).containsKey(ps))
		{
			if (!flattenTimeWindow)
				this.densMaps(flattenTimeWindow).put(ps,
						ScalarMapWindows.gen_density_maps(this.trajs, ps));
			else
			{
				TrajectoryEnsembleWindows tmp = new TrajectoryEnsembleWindows();
				tmp.wins.add(this.trajsFlat);
				this.densMaps(flattenTimeWindow).put(ps,
						ScalarMapWindows.gen_density_maps(tmp, ps));
			}
		}

		return this.densMaps(flattenTimeWindow).get(ps);
	}

	public HashMap<MapParameters.DiffusionParameters, ScalarMapWindows> diffMaps(boolean flattenTimeWindow)
	{
		if (flattenTimeWindow)
			return this.diffMapsFlat;
		return this.diffMaps;
	}

	public ScalarMapWindows diffMapWindows(final MapParameters.DiffusionParameters ps, boolean flattenTimeWindow)
	{
		if (!this.diffMaps(flattenTimeWindow).containsKey(ps))
		{
			if (!flattenTimeWindow)
				this.diffMaps(flattenTimeWindow).put(ps,
						ScalarMapWindows.gen_diffusion_maps(this.trajs, ps));
			else
			{
				TrajectoryEnsembleWindows tmp = new TrajectoryEnsembleWindows();
				tmp.wins.add(this.trajsFlat);
				this.diffMaps(flattenTimeWindow).put(ps,
						ScalarMapWindows.gen_diffusion_maps(tmp, ps));
			}
		}

		return this.diffMaps(flattenTimeWindow).get(ps);
	}

	public HashMap<MapParameters.DriftParameters, VectorMapWindows> driftMaps(boolean flattenTimeWindow)
	{
		if (flattenTimeWindow)
			return this.driftMapsFlat;
		return this.driftMaps;
	}

	public VectorMapWindows driftMapWindows(final MapParameters.DriftParameters ps, boolean flattenTimeWindow)
	{
		if (!this.driftMaps(flattenTimeWindow).containsKey(ps))
		{
			if (!flattenTimeWindow)
				this.driftMaps(flattenTimeWindow).put(ps,
						VectorMapWindows.gen_drift_maps(this.trajs, ps));
			else
			{
				TrajectoryEnsembleWindows tmp = new TrajectoryEnsembleWindows();
				tmp.wins.add(this.trajsFlat);
				this.driftMaps(flattenTimeWindow).put(ps,
						VectorMapWindows.gen_drift_maps(tmp, ps));
			}
		}

		return this.driftMaps(flattenTimeWindow).get(ps);
	}

	public TimeWindows timeWins()
	{
		return this.timeWins;
	}

	public HashMap<String, HashMap<WellDetectionParameters, PotWellsWindows>> wells()
	{
		return this.wells;
	}

	public HashMap<String, HashMap<GraphConstructionParameters, GraphWindows>> graphs()
	{
		return this.graphs;
	}

	public int numberOfWells(WellDetectionParameters ps)
	{
		int cpt = 0;
		for (PotWells ws: this.wells.get(ps.algoName()).get(ps).wins)
			cpt += ws.wells.size();
		return cpt;
	}

	public int numberOfNodes(GraphConstructionParameters ps)
	{
		int cpt = 0;
		for (Graph gs: this.graphs.get(ps.algoName()).get(ps).wins)
			cpt += gs.nodes().size();
		return cpt;
	}

	public String nextAvailableAnalysisName()
	{
		return "Analysis" + String.valueOf(this.analysis_name_cnt);
	}

	public void incAvailableAnalysisName()
	{
		this.analysis_name_cnt += 1;
	}

	public void marshall(String fname)
	{
		try
		{
			JAXBContext jaxbContext = JAXBContext.newInstance(DataController.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			jaxbMarshaller.marshal(this, new File(fname));
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}
	}

	public void addWellDetection(WellDetectionParameters ps, PotWellsWindows res)
	{
		if (!this.wells.containsKey(ps.algoName()))
			this.wells.put(ps.algoName(), new HashMap<> ());
		this.wells.get(ps.algoName()).put(ps, res);
	}

	public void addGraph(GraphConstructionParameters ps, GraphWindows res)
	{
		if (!this.graphs.containsKey(ps.algoName()))
			this.graphs.put(ps.algoName(), new HashMap<> ());
		this.graphs.get(ps.algoName()).put(ps, res);
	}

	//ENTIRE experimental plane
	public Rectangle default_selection()
	{
		return new Rectangle(new double[] {0.0, 0.0}, this.trajs().maxCoords());
	}
}
