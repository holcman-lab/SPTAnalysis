package fiji.plugin.SPTAnalysis.struct;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.index.kdtree.KdNode;
import com.vividsolutions.jts.index.kdtree.KdTree;

import fiji.plugin.SPTAnalysis.Utils;

@XmlRootElement(name = "ScalarMap")
@XmlAccessorType(XmlAccessType.FIELD)
public class ScalarMap implements Iterable<double[]>
{
	public static class XMLAdapter extends XmlAdapter<String, HashMap<Integer, HashMap<Integer, Double>>>
	{
		@Override
		public String marshal(HashMap<Integer, HashMap<Integer, Double>> v)
		{
			StringBuilder sb = new StringBuilder ();
			for (int i: v.keySet())
				for (int j: v.get(i).keySet())
					sb.append(String.format("%d %d %g\n", i, j, v.get(i).get(j)));

			return sb.toString();
		}

		@Override
		public HashMap<Integer, HashMap<Integer, Double>> unmarshal(String v) throws Exception
		{
			HashMap<Integer, HashMap<Integer, Double>> res = new HashMap<Integer, HashMap<Integer, Double>>();
			for (String tmp: v.split("\n"))
			{
				String[] elts = tmp.split(" ");
				int i = Integer.valueOf(elts[0]);
				int j = Integer.valueOf(elts[1]);

				if (!res.containsKey(i))
					res.put(i, new HashMap<Integer, Double>());
				res.get(i).put(j, Double.valueOf(elts[2]));
			}
			return res;
		}
	}

	protected SquareGrid g;
	protected HashMap<Integer, HashMap<Integer, Double>> dat;
	protected MapParameters ps;

	public enum DensityOption {NPTS, DENS, LOGDENS};

	public ScalarMap(SquareGrid g, HashMap<Integer, HashMap<Integer, Double>> dat, MapParameters ps)
	{
		this.g = g;
		this.dat = dat;
		this.ps = ps;
	}

	public SquareGrid grid()
	{
		return g;
	}

	public MapParameters params()
	{
		return this.ps;
	}

	public double get(Integer i, Integer j)
	{
		return this.dat.get(i).get(j);
	}

	public boolean isSet(Integer i, Integer j)
	{
		return this.dat.containsKey(i) && this.dat.get(i).containsKey(j);
	}

	public String dump()
	{
		Iterator<double[]> it = this.iterator();

		String res = "";
		while (it.hasNext())
		{
			double[] v = it.next();
			res += String.valueOf((int) v[0]) + "," + String.valueOf((int) v[1]) + "," + String.valueOf(v[2]) + "\n";
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

	public double max(ArrayList<int[]> nhs)
	{
		if (nhs.isEmpty())
			return Double.NaN;

		int i = 0;
		while (i < nhs.size() && (!this.isSet(nhs.get(i)[0], nhs.get(i)[1]) ||
			   Double.isNaN(this.get(nhs.get(i)[0], nhs.get(i)[1]))))
			++i;

		if (i == nhs.size())
			return Double.NaN;

		double res = this.get(nhs.get(i)[0], nhs.get(i)[1]);
		++i;
		while (i < nhs.size())
		{
			if (!this.isSet(nhs.get(i)[0], nhs.get(i)[1]))
			{
				++i;
				continue;
			}

			double v = this.get(nhs.get(i)[0], nhs.get(i)[1]);
			if (!Double.isNaN(v) && v > res)
				res = v;
			++i;
		}

		return res;
	}

	public ArrayList<Double> getValues()
	{
		Iterator<double[]> it = this.iterator();

		ArrayList<Double> res = new ArrayList<> ();
		while (it.hasNext())
		{
			double[] v = it.next();
			res.add(v[2]);
		}

		return res;
	}
	
	public ArrayList<double[]> getPositions()
	{
		Iterator<double[]> it = this.iterator();

		ArrayList<double[]> res = new ArrayList<> ();
		while (it.hasNext())
		{
			double[] v = it.next();
			res.add(this.grid().get((int) v[0], (int) v[1]));
		}

		return res;
	}


	public static ScalarMap load_from_str(String val, SquareGrid g, MapParameters ps)
	{
		HashMap<Integer, HashMap<Integer, Double>> res = new HashMap<Integer, HashMap<Integer, Double>>();
		String[] tmps = val.split("\n");
		for (int k = 0; k < tmps.length; ++k)
		{
			String[] tmp = tmps[k].split(",");
			int i = Integer.valueOf(tmp[0]);
			int j = Integer.valueOf(tmp[1]);

			if (!res.containsKey(i))
				res.put(i, new HashMap<Integer, Double> ());
			res.get(i).put(j, Double.valueOf(tmp[2]));
		}

		return new ScalarMap(g, res, ps);
	}

	public static ScalarMap genDensityMap(SquareGrid g, TrajectoryEnsemble trajs, MapParameters.DensityParameters ps)
	{
		HashMap<Integer, HashMap<Integer, Double>> dens = new HashMap<Integer, HashMap<Integer, Double>>();

		for (Trajectory tr: trajs.trajs)
		{
			for (Point p: tr.points)
			{
				if (p.x < g.Xmin()[0] || p.x > g.Xmax()[0] || p.y < g.Xmin()[1] || p.y > g.Xmax()[1])
					continue;

				int[] gpos = g.pos_to_gpos(new double[] {p.x, p.y});

				if (!dens.containsKey(gpos[0]))
					dens.put(gpos[0], new HashMap<Integer, Double>());
				if (!dens.get(gpos[0]).containsKey(gpos[1]))
					dens.get(gpos[0]).put(gpos[1], 0.0);
				dens.get(gpos[0]).put(gpos[1], dens.get(gpos[0]).get(gpos[1]) + 1);
			}
		}

		for (Integer i: dens.keySet())
		{
			for (Integer j: dens.get(i).keySet())
			{
				if (ps.dopt == DensityOption.DENS)
					dens.get(i).put(j, dens.get(i).get(j) / (g.dx * g.dx));
				else if (ps.dopt == DensityOption.LOGDENS && dens.get(i).get(j) > 0.0)
					dens.get(i).put(j, Math.log(dens.get(i).get(j) / (g.dx * g.dx)));
				else if (ps.dopt == DensityOption.NPTS)
					continue;
				else
					assert(false);
			}
		}

		ScalarMap res = new ScalarMap(g, dens, ps);
		if (ps.filtNhSize > 1)
			return genDensityMapFiltered(res, ps);
		return res;
	}

	public static ScalarMap genDiffusionMap(SquareGrid g, final TrajectoryEnsemble trajs, MapParameters.DiffusionParameters ps)
	{
		HashMap<Integer, HashMap<Integer, Double>> diffx = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, HashMap<Integer, Double>> diffy = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, HashMap<Integer, Integer>> npts = new HashMap<Integer, HashMap<Integer, Integer>>();

		for (Trajectory tr: trajs.trajs)
		{
			for (int i = 0; i < tr.points.size() - 1; ++i)
			{
				Point p1 = tr.points.get(i);

				if (p1.x < g.Xmin()[0] || p1.x > g.Xmax()[0] || p1.y < g.Xmin()[1] || p1.y > g.Xmax()[1])
					continue;

				Point p2 = tr.points.get(i+1);
				int[] gpos = g.pos_to_gpos(new double[] {p1.x, p1.y});

				if (!diffx.containsKey(gpos[0]))
				{
					diffx.put(gpos[0], new HashMap<Integer, Double>());
					diffy.put(gpos[0], new HashMap<Integer, Double>());
					npts.put(gpos[0], new HashMap<Integer, Integer>());
				}
				if (!diffx.get(gpos[0]).containsKey(gpos[1]))
				{
					diffx.get(gpos[0]).put(gpos[1], 0.0);
					diffy.get(gpos[0]).put(gpos[1], 0.0);
					npts.get(gpos[0]).put(gpos[1], 0);
				}

				diffx.get(gpos[0]).put(gpos[1], diffx.get(gpos[0]).get(gpos[1]) + Math.pow(p2.x - p1.x, 2) / (p2.t - p1.t));
				diffy.get(gpos[0]).put(gpos[1], diffy.get(gpos[0]).get(gpos[1]) + Math.pow(p2.y - p1.y, 2) / (p2.t - p1.t));
				npts.get(gpos[0]).put(gpos[1], npts.get(gpos[0]).get(gpos[1]) + 1);
			}
		}

		HashMap<Integer, HashMap<Integer, Double>> diff = new HashMap<Integer, HashMap<Integer, Double>>();
		for (Integer i: diffx.keySet())
		{
			if (!diff.containsKey(i))
				diff.put(i, new HashMap<Integer, Double>());
			for (Integer j: diffx.get(i).keySet())
			{
				if (npts.get(i).get(j) >= ps.nPts)
					diff.get(i).put(j, (diffx.get(i).get(j) + diffy.get(i).get(j)) / (4 * npts.get(i).get(j)));
			}
			if (diff.get(i).isEmpty())
				diff.remove(i);
		}

		return new ScalarMap(g, diff, ps);
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

	public static ScalarMap genDiffusionMapFiltered(SquareGrid g, final TrajectoryEnsemble trajs, MapParameters.DiffusionParameters ps)
	{
		HashMap<Integer, HashMap<Integer, Double>> diff = new HashMap<Integer, HashMap<Integer, Double>>();

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

			double curD = 0.0;
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
					curD = curD + w * (Math.pow(p2.x - p1.x, 2) / (2 * ((p2.t - p1.t))) + Math.pow(p2.y - p1.y, 2) / (2 * ((p2.t - p1.t)))) / 2;
					curSumW = curSumW + w;
					++npts;
				}
			}

			if (npts > ps.nPts)
			{
				if (!diff.containsKey(gpos[0]))
					diff.put(gpos[0], new HashMap<Integer, Double>());
				diff.get(gpos[0]).put(gpos[1], curD / curSumW);
			}
		}
		return new ScalarMap(g, diff, ps);
	}


	public static ScalarMap genDensityMapFiltered(final ScalarMap map, MapParameters.DensityParameters ps)
	{
		//nhSize must be odd
		assert(ps.filtNhSize % 2 == 1);
		int extent = (ps.filtNhSize - 1) / 2;

		HashMap<Integer, HashMap<Integer, Double>> res = new HashMap<> ();

		int[] minPos = map.grid().pos_to_gpos(map.grid().Xmin());
		int[] maxPos = map.grid().pos_to_gpos(map.grid().Xmax());

		for (int i = minPos[0]; i <= maxPos[0]; ++i)
		{
			for (int j = minPos[1]; j < maxPos[1]; ++j)
			{
				double val = 0.0;
				int nhCnt = 0;
				for (int k = i - extent; k <= i + extent; ++k)
				{
					for (int l = j - extent; l <= j + extent; ++l)
					{
						if (k < minPos[0] || k > maxPos[0] || l < minPos[1] || l > maxPos[1])
							continue;

						if (map.isSet(k, l))
							val = val + map.get(k, l);
						else
							val = val + 0;
						nhCnt = nhCnt + 1;
					}

					if (nhCnt > 0)
					{
						if (!res.containsKey(i))
							res.put(i, new HashMap<> ());
						res.get(i).put(j, val / nhCnt);
					}
				}
			}
		}

		return new ScalarMap(map.grid(), res, ps);
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
				return (it1 != null && it1.hasNext()) || (it2 != null && it2.hasNext());
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
				return new double[] {i, j, dat.get(i).get(j)};
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
		return it;
	}

	public static class IteratorComparator implements Comparator<double[]>
	{
		@Override
		public int compare(double[] o1, double[] o2)
		{
			if (o1[2] < o2[2])
				return -1;
			else if (o1[2] > o2[2])
				return 1;
			else
				return 0;
		}
	}
}
