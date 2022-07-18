package fiji.plugin.SPTAnalysis.graphConstruction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.index.kdtree.KdNode;
import com.vividsolutions.jts.index.kdtree.KdTree;

import fiji.plugin.SPTAnalysis.Utils;
import fiji.plugin.SPTAnalysis.struct.Point;

public class MyDbscan
{
	@SuppressWarnings("unchecked")
	private static ArrayList<Integer> rangeQuery(final KdTree kdt, final double[] p, double radius)
	{
		Envelope evlp = new Envelope(p[0] - radius, p[0] + radius, p[1] - radius, p[1] + radius);

		ArrayList<Integer> res = new ArrayList<> ();
		List<KdNode> nhs = kdt.query(evlp);
		for (final KdNode kdn: nhs)
		{
			final Coordinate coords = kdn.getCoordinate();
			if (Utils.dist(new double[] {coords.x, coords.y}, p) < radius)
				res.add((Integer) kdn.getData());
		}

		return res;
	}

	public static ArrayList<Integer> cluster(final ArrayList<Point> pts, double eps, int N)
	{
		Integer UNDEFINED = -1;
		Integer NOISE = 0;

		KdTree kdt = new KdTree();
		ArrayList<Integer> labels = new ArrayList<> ();
		for (int i = 0; i < pts.size(); ++i)
		{
			labels.add(UNDEFINED);
			kdt.insert(new Coordinate(pts.get(i).x, pts.get(i).y), i);
		}

		int clab = 0;

		for (int i = 0; i < pts.size(); ++i)
		{
			if (labels.get(i) != UNDEFINED)
				continue;

			ArrayList<Integer> nhs = rangeQuery(kdt, pts.get(i).vec(), eps);
			if (nhs.size() < N)
			{
				labels.set(i, NOISE);
				continue;
			}

			++clab;
			labels.set(i, clab);

			LinkedList<Integer> todo = new LinkedList<> ();
			todo.addAll(nhs);
			while (!todo.isEmpty())
			{
				final Integer q = todo.poll();
				if (labels.get(q) == NOISE)
					labels.set(q, clab);
				if (labels.get(q) != UNDEFINED)
					continue;
				labels.set(q, clab);

				ArrayList<Integer> nhs_q = rangeQuery(kdt, pts.get(q).vec(), eps);
				if (nhs_q.size() >= N)
					todo.addAll(nhs_q);
			}
		}

		return labels;
	}
}
