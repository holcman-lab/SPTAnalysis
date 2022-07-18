package fiji.plugin.SPTAnalysis.graphConstruction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import fiji.plugin.SPTAnalysis.struct.Graph;
import fiji.plugin.SPTAnalysis.struct.LabeledPoint;
import fiji.plugin.SPTAnalysis.struct.PlugLogger;
import fiji.plugin.SPTAnalysis.struct.Point;
import fiji.plugin.SPTAnalysis.struct.Shape;
import fiji.plugin.SPTAnalysis.struct.Trajectory;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;

public class GraphConstructionDBScanRecursive extends GraphConstruction
{
	public static final String name = "DBScanRecursive";

	private GraphConstructionDBScanRecursiveParameters ps;

	public GraphConstructionDBScanRecursive(GraphConstructionParameters ps)
	{
		this.ps = (GraphConstructionDBScanRecursiveParameters) ps;
	}

	@Override
	public GraphConstructionParameters getParameters()
	{
		return this.ps;
	}

	public int maxVal(final Set<Integer> s)
	{
		int res = 0;
		for (final Integer v: s)
			if (v > res)
				res = v;
		return res;
	}

	//TODO: get rid of Instance
	@Override
	public Graph constructGraph(TrajectoryEnsemble trajs, final PlugLogger log)
	{
		ArrayList<Point> lowVelPts = GraphConstruction.extractLowVelPts(trajs, this.ps);

		HashMap<Integer, ArrayList<Point>> lowVelLabPts = this.computeClusters(lowVelPts);

		HashMap<Integer, Shape> nodes = GraphConstruction.buildNodes(lowVelLabPts, this.ps, log);

		//consolidate clusters
		{
			int cCpt = 1;
			HashMap<Integer, Shape> tmp = new HashMap<>();
			for (final Integer k: nodes.keySet())
			{
				tmp.put(cCpt, nodes.get(k));
				++cCpt;
			}
			nodes = tmp;
		}

		//Assign a cluster to ALL points (not only low vel)
		// >= 1 if point belong to a cluster, 0 otherwise
		HashMap<Integer, Integer> ptsNode = new HashMap<> ();
		int pCpt = 0;
		for (Trajectory tr: trajs.trajs())
		{
			for (Point pt: tr.points())
			{
				ptsNode.put(pCpt, LabeledPoint.assignLabel(pt, nodes));
				++pCpt;
			}
		}

		System.out.println("Final number of nodes: " + String.valueOf(ptsNode.size()));

		HashMap<Integer, HashMap<Integer, Double>> conn =
				GraphConstruction.buildConnectivityMatrix(trajs, ptsNode);

		return new Graph(nodes, this.ps.nodeT, conn);
	}

	@Override
	public HashMap<Integer, ArrayList<Point>> computeClusters(final ArrayList<Point> pts)
	{
		double curR = this.ps.RMax;
		int curN = this.ps.NMin;

		 HashMap<Integer, ArrayList<Point>> clusts =
				 new GraphConstructionDBScan(
						 new GraphConstructionDBScanParameters(curR, curN)).computeClusters(pts);

		//Remove cluster with ID 0 which represents the noise
		 clusts.remove(0);

		//Split nodes composed of too many points
		boolean go = true;
		while (go && curR >= this.ps.RMin && curN <= this.ps.NMax)
		{
			go = false;

			ArrayList<ArrayList<Point>> toAdd = new ArrayList<>();
			HashSet<Integer> toDel = new HashSet<>();
			for (final Integer i: clusts.keySet())
			{
				if (clusts.get(i).size() > this.ps.maxClustNpts)
				{
					go = true;
					HashMap<Integer, ArrayList<Point>> tmp =
							new GraphConstructionDBScan(
									new GraphConstructionDBScanParameters(curR, curN)).computeClusters(clusts.get(i));
					tmp.remove(0); //noise

					toDel.add(i);
					for (final Integer k: tmp.keySet())
						toAdd.add(tmp.get(k));
				}
			}

			for (final Integer i: toDel)
				clusts.remove(i);
			int max = maxVal(clusts.keySet()) + 1;
			for (int i = 0; i < toAdd.size(); ++i)
				clusts.put(max + i, toAdd.get(i));

			curR -= this.ps.RStep;
			if (curR < this.ps.RMin)
			{
				curR = this.ps.RMax;
				curN += this.ps.NStep;
			}
		}

		return clusts;
	}
}
