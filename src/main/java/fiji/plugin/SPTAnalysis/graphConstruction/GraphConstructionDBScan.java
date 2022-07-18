package fiji.plugin.SPTAnalysis.graphConstruction;

import java.util.ArrayList;
import java.util.HashMap;
import fiji.plugin.SPTAnalysis.struct.Graph;
import fiji.plugin.SPTAnalysis.struct.LabeledPoint;
import fiji.plugin.SPTAnalysis.struct.PlugLogger;
import fiji.plugin.SPTAnalysis.struct.Point;
import fiji.plugin.SPTAnalysis.struct.Shape;
import fiji.plugin.SPTAnalysis.struct.Trajectory;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;

public class GraphConstructionDBScan extends GraphConstruction
{
	public static final String name = "DBScan";

	private GraphConstructionDBScanParameters ps;

	public GraphConstructionDBScan(GraphConstructionParameters ps)
	{
		this.ps = (GraphConstructionDBScanParameters) ps;
	}

	@Override
	public GraphConstructionParameters getParameters()
	{
		return this.ps;
	}

	@Override
	public Graph constructGraph(TrajectoryEnsemble trajs, final PlugLogger log)
	{
		ArrayList<Point> lowVelPts = GraphConstruction.extractLowVelPts(trajs, this.ps);
		System.out.println("Number of points: " + String.valueOf(lowVelPts.size()));
		HashMap<Integer, ArrayList<Point>> lowVelLabPts = this.computeClusters(lowVelPts);
		//Remove cluster with ID 0 which represents the noise
		lowVelLabPts.remove(0);

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

		HashMap<Integer, HashMap<Integer, Double>> conn =
				GraphConstruction.buildConnectivityMatrix(trajs, ptsNode);

		return new Graph(nodes, this.ps.nodeT, conn);
	}

	@Override
	public HashMap<Integer, ArrayList<Point>> computeClusters(final ArrayList<Point> pts)
	{
		ArrayList<Integer> labels = MyDbscan.cluster(pts, this.ps.R, this.ps.N);
		HashMap<Integer, ArrayList<Point>> lowVelLabPts = new HashMap<> ();
		for (int i = 0; i < labels.size(); ++i)
		{
			final Integer lab = labels.get(i);

			if (!lowVelLabPts.containsKey(lab))
				lowVelLabPts.put(lab, new ArrayList<> ());
			lowVelLabPts.get(lab).add(new Point(pts.get(i).x, pts.get(i).y));
		}

		HashMap<Integer, ArrayList<Point>> res = new HashMap<> ();
		for (Integer I: lowVelLabPts.keySet())
			if (lowVelLabPts.get(I).size() >= 5)
				res.put(I, lowVelLabPts.get(I));

		return res;
	}
}
