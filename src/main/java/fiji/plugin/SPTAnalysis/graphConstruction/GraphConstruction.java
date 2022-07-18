package fiji.plugin.SPTAnalysis.graphConstruction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import fiji.plugin.SPTAnalysis.Utils;
import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.MyPolygon;
import fiji.plugin.SPTAnalysis.struct.PlugLogger;
import fiji.plugin.SPTAnalysis.struct.Graph;
import fiji.plugin.SPTAnalysis.struct.GraphWindows;
import fiji.plugin.SPTAnalysis.struct.Point;
import fiji.plugin.SPTAnalysis.struct.Shape;
import fiji.plugin.SPTAnalysis.struct.Trajectory;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsembleWindows;

public abstract class GraphConstruction
{
	public abstract GraphConstructionParameters getParameters();
	public abstract HashMap<Integer, ArrayList<Point>> computeClusters(final ArrayList<Point> pts);
	public abstract Graph constructGraph(TrajectoryEnsemble trajs, final PlugLogger log);

	public static ArrayList<Point> extractLowVelPts(final TrajectoryEnsemble trajs,
			final GraphConstructionParameters ps)
	{
		ArrayList<Point> res = new ArrayList<> ();
		for (Trajectory tr: trajs.trajs())
		{
			double[] ivels = tr.instantaneousVelocities();
			for (int i = 0; i < ivels.length; ++i)
			{
				if (ivels[i] < ps.lowVelTh)
				{
					res.add(tr.points().get(i));
					res.add(tr.points().get(i+1));
				}
			}
		}

		return res;
	}

	protected static HashMap<Integer, Ellipse> fitEllipses(final HashMap<Integer, ArrayList<Point>> lowVelLabPts,
			double minVolEllEps)
	{
		HashMap<Integer, Ellipse> res = new HashMap<> ();

		for (Integer lid: lowVelLabPts.keySet())
		{
			if (lowVelLabPts.get(lid).size() > 5)
			{
				try
				{
					res.put(lid, Utils.minVolEllipse(lowVelLabPts.get(lid), minVolEllEps));
				}
				catch (Exception e)
				{
				}
			}
		}

		return res;
	}

	protected static HashMap<Integer, Shape> buildNodes(final HashMap<Integer, ArrayList<Point>> lowVelLabPts,
			final GraphConstructionParameters ps, final PlugLogger log)
	{
		HashMap<Integer, Shape> res = new HashMap<> ();
		if (ps.nodeT == GraphConstructionParameters.NodeType.ELLIPSE)
		{
			for (final Integer lid: lowVelLabPts.keySet())
			{
				if (log != null)
					log.update(1.0 / lowVelLabPts.keySet().size());
				if (lowVelLabPts.get(lid).size() > 5)
				{
					try
					{
						res.put(lid, (Shape) Utils.minVolEllipse(lowVelLabPts.get(lid), ps.minVolEllEps));
					}
					catch (Exception e)
					{
					}
				}
			}
		}
		else if (ps.nodeT == GraphConstructionParameters.NodeType.POLY)
		{
			for (final Integer lid: lowVelLabPts.keySet())
			{
				if (log != null)
					log.update(1.0 / lowVelLabPts.keySet().size());
				if (lowVelLabPts.get(lid).size() > 3)
					res.put(lid, MyPolygon.convexHull(lowVelLabPts.get(lid)));
			}
		}
		else
			assert(false);

		//Filter nodes on areas: remove large nodes
		Iterator<HashMap.Entry<Integer, Shape> > it = res.entrySet().iterator(); 
		while (it.hasNext())
			if (it.next().getValue().area() > ps.maxArea)
				it.remove(); 

		if (ps.nodeT == GraphConstructionParameters.NodeType.ELLIPSE)
			Utils.mergeEllipses(res, lowVelLabPts, ps.minVolEllEps);
		else
			Utils.mergePolygons(res, lowVelLabPts);

		//Filter nodes on areas: remove both large and small nodes
		it = res.entrySet().iterator(); 
		while (it.hasNext())
		{ 
			double ar = it.next().getValue().area();
			if (ar < ps.minArea || ar > ps.maxArea)
				it.remove(); 
		}

		return res;
	}

	protected static void add_connection(HashMap<Integer, HashMap<Integer, Double>> C, int i, int j)
	{
		if (!C.containsKey(i))
			C.put(i, new HashMap<> ());
		if (!C.get(i).containsKey(j))
			C.get(i).put(j, 0.0);
		C.get(i).put(j, C.get(i).get(j) + 1);
	}

	protected static HashMap<Integer, HashMap<Integer, Double>> buildConnectivityMatrix(
			final TrajectoryEnsemble trajs,
			final HashMap<Integer, Integer> ptsNode)
	{
		HashMap<Integer, HashMap<Integer, Double>> res = new HashMap<> ();

		int pCpt = 0;
		for (Trajectory tr: trajs.trajs())
		{
			int j = 0;
			while (j < tr.points().size()-1)
			{
				int c1 = ptsNode.get(pCpt);
				if (c1 > 0)
				{
					int c2 = ptsNode.get(pCpt+1);
					if (c2 > 0 && c2 != c1) //Single-jump
						add_connection(res, c1, c2);
					else if (c2 == 0 && j < tr.points().size() - 2) //Double jump
					{
						int c3 = ptsNode.get(pCpt+2);
						if (c3 > 0 && c3 != c1)
						{
							add_connection(res, c1, c3);
							++j;
							++pCpt;
						}
					}
				}

				++pCpt;
				++j;
			}
			++pCpt; //as we skip the last traj pt.
		}

		return res;
	}

	public static GraphWindows detectGraphsTimeWindows(final GraphConstruction algo,
			final TrajectoryEnsembleWindows trajs, final PlugLogger log)
	{
		GraphWindows res = new GraphWindows();
		if (log != null)
			log.maxWindows(trajs.wins.size());
		for (int i = 0; i < trajs.wins.size(); ++i)
		{
			if (log != null)
				log.curWindow(i);
			res.wins.add(algo.constructGraph(trajs.wins.get(i), log));
		}

		return res;
	}
}
