package fiji.plugin.SPTAnalysis.struct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.index.kdtree.KdNode;
import com.vividsolutions.jts.index.kdtree.KdTree;

import fiji.plugin.SPTAnalysis.Utils;

@XmlRootElement(name = "VectorMap")
@XmlAccessorType(XmlAccessType.FIELD)
public class VectorMap implements Iterable<double[]>
{
	public static class XMLAdapter extends XmlAdapter<String, HashMap<Integer, HashMap<Integer, Double[]>>>
	{
		@Override
		public String marshal(HashMap<Integer, HashMap<Integer, Double[]>> v)
		{
			StringBuilder sb = new StringBuilder ();
			for (int i: v.keySet())
				for (int j: v.get(i).keySet())
					sb.append(String.format("%d %d %g %g\n", i, j,
							v.get(i).get(j)[0], v.get(i).get(j)[1]));

			return sb.toString();
		}

		@Override
		public HashMap<Integer, HashMap<Integer, Double[]>> unmarshal(String v) throws Exception
		{
			HashMap<Integer, HashMap<Integer, Double[]>> res = new HashMap<Integer, HashMap<Integer, Double[]>>();
			for (String tmp: v.split("\n"))
			{
				String[] elts = tmp.split(" ");
				int i = Integer.valueOf(elts[0]);
				int j = Integer.valueOf(elts[1]);

				if (!res.containsKey(i))
					res.put(i, new HashMap<Integer, Double[]>());
				res.get(i).put(j, new Double[] {Double.valueOf(elts[2]), Double.valueOf(elts[3])});
			}
			return res;
		}
	}

	public static final VectorMap nullVal = new VectorMap(SquareGrid.nullVal,
			new HashMap<Integer, HashMap<Integer, Double[]>> (), null);

	protected SquareGrid g;
	@XmlJavaTypeAdapter(VectorMap.XMLAdapter.class)
	protected HashMap<Integer, HashMap<Integer, Double[]>> dat;
	protected boolean normalized;
	protected MapParameters.DriftParameters ps;

	public VectorMap()
	{
	}

	public VectorMap(SquareGrid g, HashMap<Integer, HashMap<Integer, Double[]>> dat, MapParameters.DriftParameters ps)
	{
		this.g = g;
		this.dat = dat;
		this.normalized = false;
		this.ps = ps;
	}

	public VectorMap(SquareGrid g, HashMap<Integer, HashMap<Integer, Double[]>> dat, boolean norm)
	{
		this.g = g;
		this.dat = dat;
		this.normalized = norm;
	}

	public SquareGrid grid()
	{
		return this.g;
	}

	public boolean normalized()
	{
		return this.normalized;
	}

	public MapParameters.DriftParameters params()
	{
		return this.ps;
	}

	public Double[] get(Integer i, Integer j)
	{
		if (!this.dat.containsKey(i) || !this.dat.get(i).containsKey(j))
			return new Double[] {Double.NaN, Double.NaN};
		return this.dat.get(i).get(j);
	}

	public boolean isSet(int i, int j)
	{
		return this.dat.containsKey(i) && this.dat.get(i).containsKey(j);
	}

	public double angular_similarity(ArrayList<int[]> cells, Ellipse ell)
	{
		if (!this.normalized)
			return Double.NaN;

		double res = 0.0;
		for (int i = 0; i < cells.size(); ++i)
		{
			double[] p = this.g.get(cells.get(i)[0], cells.get(i)[1]);

			Double[] A = this.get(cells.get(i)[0], cells.get(i)[1]);
			double[] B = new double[] {ell.mu()[0] - p[0], ell.mu()[1] - p[1]};

			double Anorm = Math.sqrt(Math.pow(A[0], 2) + Math.pow(A[1], 2));
			double Bnorm = Math.sqrt(Math.pow(B[0], 2) + Math.pow(B[1], 2));

			double tmp = (A[0] * B[0] + A[1] * B[1]) / (Anorm * Bnorm);
			if (tmp > 1.0)
				tmp = 1.0;
			if (tmp < -1.0)
				tmp = -1.0;

			if (!Double.isNaN(Math.acos(tmp)))
				res += 1 - Math.acos(tmp) / Math.PI;
		}

		return res / cells.size();
	}

	public String dump()
	{
		Iterator<double[]> it = this.iterator();

		String res = "";
		while (it.hasNext())
		{
			double[] v = it.next();
			res += String.format("%d %d %g %g\n", (int) v[0], (int) v[1], v[2], v[3]);
		}

		return res;
	}

	public double max()
	{
		Iterator<double[]> it = this.iterator();
		if (!it.hasNext())
			return Double.NaN;
		double[] v = it.next();

		double res = v[2];
		while (it.hasNext())
		{
			v = it.next();
			if (v[2] > res)
				res = v[2];
		}

		return res;
	}

	public static VectorMap load_from_str(String val, SquareGrid g, MapParameters.DriftParameters ps)
	{
		HashMap<Integer, HashMap<Integer, Double[]>> res = new HashMap<Integer, HashMap<Integer, Double[]>>();
		String[] tmps = val.split("\n");
		for (int k = 0; k < tmps.length; ++k)
		{
			String[] tmp = tmps[k].split(",");
			int i = Integer.valueOf(tmp[0]);
			int j = Integer.valueOf(tmp[1]);

			if (!res.containsKey(i))
				res.put(i, new HashMap<Integer, Double[]> ());
			res.get(i).put(j, new Double[] {Double.valueOf(tmp[2]), Double.valueOf(tmp[3])});
		}

		return new VectorMap(g, res, ps);
	}

	public static VectorMap genDriftMap(SquareGrid g, final TrajectoryEnsemble trajs, MapParameters.DriftParameters ps)
	{
		HashMap<Integer, HashMap<Integer, Double[]>> tmp_drift = new HashMap<Integer, HashMap<Integer, Double[]>>();
		HashMap<Integer, HashMap<Integer, Integer>> npts = new HashMap<Integer, HashMap<Integer, Integer>>();

		for (Trajectory tr: trajs.trajs)
		{
			for (int i = 0; i < tr.points.size() - 1; ++i)
			{
				Point p1 = tr.points.get(i);
				Point p2 = tr.points.get(i+1);

				if (p1.x < g.Xmin()[0] || p1.x > g.Xmax()[0] || p1.y < g.Xmin()[1] || p1.y > g.Xmax()[1])
					continue;

				int[] gpos = g.pos_to_gpos(new double[] {p1.x, p1.y});

				if (!tmp_drift.containsKey(gpos[0]))
				{
					tmp_drift.put(gpos[0], new HashMap<Integer, Double[]>());
					npts.put(gpos[0], new HashMap<Integer, Integer>());
				}
				if (!tmp_drift.get(gpos[0]).containsKey(gpos[1]))
				{
					tmp_drift.get(gpos[0]).put(gpos[1], new Double[] {0.0, 0.0});
					npts.get(gpos[0]).put(gpos[1], 0);
				}

				tmp_drift.get(gpos[0]).put(gpos[1], new Double[] {tmp_drift.get(gpos[0]).get(gpos[1])[0] + (p2.x - p1.x) / (p2.t - p1.t),
																  tmp_drift.get(gpos[0]).get(gpos[1])[1] + (p2.y - p1.y) / (p2.t - p1.t)});
				npts.get(gpos[0]).put(gpos[1], npts.get(gpos[0]).get(gpos[1]) + 1);
			}
		}

		HashMap<Integer, HashMap<Integer, Double[]>> drift = new HashMap<Integer, HashMap<Integer, Double[]>>();
		for (Integer i: tmp_drift.keySet())
		{
			for (Integer j: tmp_drift.get(i).keySet())
			{
				if (npts.get(i).get(j) >= ps.nPts)
				{
					if (!drift.containsKey(i))
						drift.put(i, new HashMap<Integer, Double[]> ());
					drift.get(i).put(j, new Double[] {tmp_drift.get(i).get(j)[0] / npts.get(i).get(j),
													  tmp_drift.get(i).get(j)[1] / npts.get(i).get(j)});
				}
			}
		}

		return new VectorMap(g, drift, ps);
	}

	private static class KdVal
	{
		public final Trajectory tr;
		public final int idx;
		
		public KdVal(final Trajectory tr, int idx)
		{
			this.tr = tr;
			this.idx = idx;
		}
	}

	public static VectorMap genDriftMapFiltered(SquareGrid g, final TrajectoryEnsemble trajs, MapParameters.DriftParameters ps)
	{
		HashMap<Integer, HashMap<Integer, Double[]>> drift = new HashMap<Integer, HashMap<Integer, Double[]>>();

		KdTree kdt = new KdTree();
		for (Trajectory tr: trajs.trajs)
			for (int i = 0; i < tr.points.size() - 1; ++i)
				kdt.insert(new Coordinate(tr.points.get(i).x, tr.points.get(i).y), new KdVal(tr, i));

		Iterator<int[]> it = g.iterator();
		while (it.hasNext())
		{
			int[] gpos = it.next();
			double[] pos = g.get(gpos[0], gpos[1]);

			Envelope evlp = new Envelope(pos[0] - ps.filterSize, pos[0] + ps.filterSize,
										 pos[1] - ps.filterSize, pos[1] + ps.filterSize);

			@SuppressWarnings("unchecked")
			List<KdNode> nhs = kdt.query(evlp);

			double[] curDrift = new double[] {0.0, 0.0};
			double curSumW = 0.0;
			int npts = 0;
			for (final KdNode kdn: nhs)
			{
				KdVal kdv = (KdVal) kdn.getData();
				Point p1 = kdv.tr.points.get(kdv.idx);

				double d = Utils.dist(p1.vec(), pos);
				if (d < ps.filterSize)
				{
					Point p2 = kdv.tr.points.get(kdv.idx+1);

					double w = Math.cos(Math.PI / 2 * d / ps.filterSize);
					curDrift[0] = curDrift[0] + w * (p2.x - p1.x) / (p2.t - p1.t);
					curDrift[1] = curDrift[1] + w * (p2.y - p1.y) / (p2.t - p1.t);
					curSumW = curSumW + w;
					++npts;
				}
			}

			if (npts > ps.nPts)
			{
				if (!drift.containsKey(gpos[0]))
					drift.put(gpos[0], new HashMap<Integer, Double[]>());
				drift.get(gpos[0]).put(gpos[1], new Double[] {curDrift[0] / curSumW, curDrift[1] / curSumW});
			}
		}
		return new VectorMap(g, drift, ps);
	}

	public static VectorMap rotate_field(VectorMap drift, double phi)
	{
		double[][] rot = new double[][] {new double[] {Math.cos(phi), -Math.sin(phi)},
										 new double[] {Math.sin(-phi), Math.cos(-phi)}};

		HashMap<Integer, HashMap<Integer, Double[]>> res = new HashMap<Integer, HashMap<Integer, Double[]>>();
		Iterator<double[]> it = drift.iterator();

		while(it.hasNext())
		{
			double[] v = it.next();
			Integer i = (int) v[0];
			Integer j = (int) v[1];

			Double[] rv = new Double[] {rot[0][0] * v[2] + rot[0][1] * v[3],
										rot[1][0] * v[2] + rot[1][1] * v[3]};

			if (!res.containsKey(i))
				res.put(i, new HashMap<Integer, Double[]>());
			if (!res.get(i).containsKey(j))
				res.get(i).put(j, rv);
		}

		return new VectorMap(drift.grid(), res, drift.params());
	}

	public static VectorMap normalized_drift(VectorMap drift)
	{
		HashMap<Integer, HashMap<Integer, Double[]>> res = new HashMap<Integer, HashMap<Integer, Double[]>>();
		Iterator<double[]> it = drift.iterator();

		while(it.hasNext())
		{
			double[] v = it.next();
			Integer i = (int) v[0];
			Integer j = (int) v[1];

			double norm = Math.sqrt(v[2]*v[2] + v[3]*v[3]);
			Double tmp[] = new Double[] {v[2] / norm, v[3] / norm};

			if (Double.isNaN(tmp[0]) || Double.isNaN(tmp[1]))
				continue;

			if (!res.containsKey(i))
				res.put(i, new HashMap<Integer, Double[]>());
			if (!res.get(i).containsKey(j))
				res.get(i).put(j, tmp);
		}

		return new VectorMap(drift.grid(), res, true);
	}

	public static VectorMap applyFactor(VectorMap drift, double factor)
	{
		HashMap<Integer, HashMap<Integer, Double[]>> res = new HashMap<Integer, HashMap<Integer, Double[]>>();
		Iterator<double[]> it = drift.iterator();

		while(it.hasNext())
		{
			double[] v = it.next();
			Integer i = (int) v[0];
			Integer j = (int) v[1];

			if (!res.containsKey(i))
				res.put(i, new HashMap<Integer, Double[]>());
			if (!res.get(i).containsKey(j))
				res.get(i).put(j, new Double[] {factor * v[2], factor * v[3]});
		}

		return new VectorMap(drift.grid(), res, true);
	}

	@Override
	public java.util.Iterator<double[]> iterator()
	{
		Iterator<double[]> it = new Iterator<double[]>()
		{
			private Iterator<Integer> it1 = dat.keySet().iterator();
			private Integer i = null;
			private Iterator<Integer> it2 = null;

			@Override
			public boolean hasNext()
			{
				return it1.hasNext() || (it2 != null && it2.hasNext());
			}

			@Override
			public double[] next()
			{
				if (it2 == null || !it2.hasNext())
				{
					i = it1.next();
					it2 = dat.get(i).keySet().iterator();
				}
				int j = it2.next();
				return new double[] {i, j, dat.get(i).get(j)[0], dat.get(i).get(j)[1]};
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
		return it;
	}
}
