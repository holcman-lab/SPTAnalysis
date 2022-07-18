package fiji.plugin.SPTAnalysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.MyPolygon;
import fiji.plugin.SPTAnalysis.struct.Point;
import fiji.plugin.SPTAnalysis.struct.PotWell;
import fiji.plugin.SPTAnalysis.struct.ScalarMap;
import fiji.plugin.SPTAnalysis.struct.Shape;
import fiji.plugin.SPTAnalysis.struct.SquareGrid;
import fiji.plugin.SPTAnalysis.struct.Trajectory;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;
import fiji.plugin.SPTAnalysis.struct.VectorMap;

public class Utils
{
	public static double PRECISION = 1e6;

	public static double vnorm (double[] v)
	{
		return Math.sqrt(v[0]*v[0] + v[1]*v[1]);
	}

	public static int argmax(ArrayList<Double> arr)
	{
		if (arr.isEmpty())
			return -1;

		int res = 0;
		for (int i = 0; i < arr.size(); ++i)
			res = arr.get(i) > arr.get(res) ? i : res;
		return res;
	}
	
	public static ArrayList<int[]> highest_density_cells(ScalarMap dens, double density_th)
	{
		ArrayList<double[]> tmp = new ArrayList<double[]>();
		Iterator<double[]> it = dens.iterator();

		while (it.hasNext())
		{
			double[] v = it.next();
			tmp.add(v);
		}

		Collections.sort(tmp, new ScalarMap.IteratorComparator());

		int num_cells = (int) Math.ceil(density_th / 100 * tmp.size());

		ArrayList<int[]> res = new ArrayList<int[]>();
		for (int i = 0; i < num_cells; ++i)
			res.add(new int[] {(int) tmp.get(tmp.size() - 1 - i)[0], (int) tmp.get(tmp.size() - 1 - i)[1]});

		return res;
	}

	public static ArrayList<int[]> remove_closeby_cells(ScalarMap dens, ArrayList<int[]> cells, int dist_th)
	{
		ArrayList<int[]> res = new ArrayList<int[]> ();

		ArrayList<Boolean> done = new ArrayList<Boolean> ();
		for (int i = 0; i < cells.size(); ++i)
			done.add(false);

		for (int i = 0; i < cells.size(); ++i)
		{
			if (done.get(i))
				continue;

			int[] gp1 = cells.get(i);

			ArrayList<Integer> nh = new ArrayList<Integer> ();
			ArrayList<Double> nh_dens = new ArrayList<Double> ();
			for (int j = 0; j < cells.size(); ++j)
			{
				if (done.get(j))
					continue;

				int[] gp2 = cells.get(j);
				if (Math.abs(gp1[0] - gp2[0]) <= dist_th &&
					Math.abs(gp1[1] - gp2[1]) <= dist_th)
				{
					nh.add(j);
					nh_dens.add(dens.get(gp2[0], gp2[1]));
					done.set(j, true);
				}
			}

			res.add(cells.get(nh.get(Utils.argmax(nh_dens))));
		}

		return res;
	}

	public static double[] rot_point(Double[] p, double phi)
	{
		return new double[] {Math.cos(-phi) * p[0] -Math.sin(-phi) * p[1],
							 Math.sin(-phi) * p[0] + Math.cos(-phi) * p[1]};
	}

	public static double[] rot_point(double[] p, double phi)
	{
		return new double[] {Math.cos(-phi) * p[0] - Math.sin(-phi) * p[1],
							 Math.sin(-phi) * p[0] + Math.cos(-phi) * p[1]};
	}

	public static ArrayList<double[]> pointsInReg(TrajectoryEnsemble trajs, Shape reg)
	{
		ArrayList<double[]> res = new ArrayList<double[]> ();
		for (Trajectory traj: trajs.trajs())
			for (Point p: traj.points())
				if (reg.inside(new double[] {p.x, p.y}))
					res.add(new double[] {p.x, p.y});

		return res;
	}

	public static ArrayList<double[]> pointsInReg(ArrayList<double[]> points, Shape reg)
	{
		ArrayList<double[]> res = new ArrayList<double[]> ();
		for (final double[] p: points)
			if (reg.inside(p))
				res.add(p);

		return res;
	}

	public static ArrayList<int[]> squaresInReg(SquareGrid g, Shape reg)
	{
		ArrayList<int[]> res = new ArrayList<int[]> ();

		Iterator<int[]> it = g.iterator();
		while (it.hasNext())
		{
			int[] gpos = it.next();
			if (reg.inside(g.get(gpos[0], gpos[1])))
				res.add(gpos);
		}

		return res;
	}

	public static ArrayList<int[]> filter_empty_squares(VectorMap drift, ArrayList<int[]> cells)
	{
		ArrayList<int[]> res = new ArrayList<int[]> ();

		for (int[] gpos: cells)
		{
			Double[] tmp = drift.get(gpos[0], gpos[1]);
			if (!Double.isNaN(tmp[0]) && !Double.isNaN(tmp[1]))
				res.add(gpos);
		}

		return res;
	}

	public static ArrayList<int[]> filter_center(SquareGrid g, ArrayList<int[]> cells)
	{
		ArrayList<int[]> res = new ArrayList<int[]> ();

		int[] maxIdxs = g.max_pos();

		//Center cell only exists for odd-sized grids
		if (maxIdxs[0] % 2 == 0 || maxIdxs[1] % 2 == 0)
			return cells;

		for (int[] gpos: cells)
		{
			if (gpos[0] != (maxIdxs[0] - 1) / 2 || gpos[1] != (maxIdxs[1] - 1) / 2)
				res.add(gpos);
		}

		return res;
	}

	public static TrajectoryEnsemble trajsInShape(TrajectoryEnsemble trajs, Shape reg)
	{
		TrajectoryEnsemble res = new TrajectoryEnsemble();

		int cpt = 1;
		for (Trajectory tr: trajs.trajs())
		{
			Trajectory curTr = new Trajectory(cpt, tr.id());
			for (Point p: tr.points())
			{
				if (!reg.inside(p.vec()))
				{
					if (!curTr.isEmpty())
					{
						res.trajs().add(curTr);
						++cpt;
						curTr = new Trajectory(cpt, tr.id());
					}
				}
				else
					curTr.points().add(p);
			}

			if (!curTr.isEmpty())
			{
				res.trajs().add(curTr);
				++cpt;
			}
		}

		return res;
	}

	public static ArrayList<PotWell> wellsInReg(final ArrayList<PotWell> wells, Shape reg)
	{
		ArrayList<PotWell> res = new ArrayList<>();
		for (PotWell w: wells)
			if (reg.inside(w.ell().mu()))
				res.add(w);
		return res;
	}

	public static ArrayList<Integer> shapesInRegIdx(ArrayList<? extends Shape> shs, Shape reg)
	{
		ArrayList<Integer> res = new ArrayList<>();
		for (int i = 0; i < shs.size(); ++i)
			if (reg.inside(shs.get(i).center()))
				res.add(i);
		return res;
	}

	
	public static double diffCoeffInEllipse(TrajectoryEnsemble trajs, Ellipse ell)
	{
		double[] D = new double[] {0.0, 0.0};
		int cpt = 0;

		for (Trajectory tr: trajs.trajs())
		{
			for (int i = 0; i < tr.points().size()-1; ++i)
			{
				Point p1 = tr.points().get(i);

				if (ell.inside(new double[] {p1.x, p1.y}))
				{
					Point p2 = tr.points().get(i+1);

					D[0] += Math.pow(p2.x - p1.x, 2) / (p2.t - p1.t);
					D[1] += Math.pow(p2.y - p1.y, 2) / (p2.t - p1.t);
					++cpt;
				}
			}
		}

		return (D[0] + D[1]) / (4 * cpt);
	}

	public static double diffCoeffInEllipseConstr(TrajectoryEnsemble trajs, Ellipse ell)
	{
		double[] D = new double[] {0.0, 0.0};
		int cpt = 0;

		for (Trajectory tr: trajs.trajs())
		{
			for (int i = 0; i < tr.points().size()-1; ++i)
			{
				Point p1 = tr.points().get(i);
				Point p2 = tr.points().get(i+1);

				if (ell.inside(new double[] {p1.x, p1.y}) && ell.inside(new double[] {p2.x, p2.y}))
				{
					D[0] += Math.pow(p2.x - p1.x, 2) / (p2.t - p1.t);
					D[1] += Math.pow(p2.y - p1.y, 2) / (p2.t - p1.t);
					++cpt;
				}
			}
		}

		return (D[0] + D[1]) / (4 * cpt);
	}

	public static double dist(double[] p1, double[] p2)
	{
		double d = Math.sqrt(Math.pow(p2[0] - p1[0], 2) + Math.pow(p2[1] - p1[1], 2));
		return Math.rint(d * Utils.PRECISION) / Utils.PRECISION;
	}

	public static double squaredDist(double[] p1, double[] p2)
	{
		double d = Math.pow(p2[0] - p1[0], 2) + Math.pow(p2[1] - p1[1], 2);
		return Math.rint(d * Utils.PRECISION) / Utils.PRECISION; //0.1 nm precision
	}
	
	public static double RatioSquaredDist(double[] p1, double[] p2, double yrat)
	{
		double d = Math.pow(p2[0] - p1[0], 2) + Math.pow(yrat * (p2[1] - p1[1]), 2);
		return Math.rint(d * Utils.PRECISION) / Utils.PRECISION; //0.1 nm precision
	}

	public static ArrayList<Integer> remove_overlapping_wells(ArrayList<PotWell> wells)
	{
		ArrayList<Boolean> to_rm = new ArrayList<Boolean> ();
		for (int i = 0; i < wells.size(); ++i)
			to_rm.add(false);

		for (int i = 0; i < wells.size(); ++i)
		{
			for (int j = 0; j < wells.size(); ++j)
			{
				if (i != j && !to_rm.get(i) && wells.get(i).ell().intersect(wells.get(j).ell()))
				{
					if (wells.get(i).score().betterThan(wells.get(j).score()))
						to_rm.set(j, true);
					else
						to_rm.set(i, true);
				}
			}
		}

		ArrayList<Integer> res = new ArrayList<Integer> ();
		for (int i = 0; i < to_rm.size(); ++i)
			if (!to_rm.get(i))
				res.add(i);
		return res;
	}

	public static void mergeEllipses(HashMap<Integer, Shape> ells,
			HashMap<Integer, ArrayList<Point>> labPts, double minVolEllEps)
	{
		boolean go = true;
		while (go)
		{
			go = false;

			for (final Integer k1: ells.keySet())
			{
				for (final Integer k2: ells.keySet())
				{
					final Ellipse e1 = (Ellipse) ells.get(k1);
					final Ellipse e2 = (Ellipse) ells.get(k2);
					if (k1 != k2 && e1.intersect(e2))
					{
						ArrayList<Point> newPts = labPts.get(k1);
						newPts.addAll(labPts.get(k2));
						ells.put(k1, Utils.minVolEllipse(newPts, minVolEllEps));

						labPts.remove(k2);
						ells.remove(k2);

						go = true;
						break;
					}
				}
				if (go)
					break;
			}
		}
	}

	public static void mergePolygons(HashMap<Integer, Shape> polys,
			HashMap<Integer, ArrayList<Point>> labPts)
	{
		boolean go = true;
		while (go)
		{
			go = false;

			for (final Integer k1: polys.keySet())
			{
				for (final Integer k2: polys.keySet())
				{
					MyPolygon p1 = (MyPolygon) polys.get(k1);
					MyPolygon p2 = (MyPolygon) polys.get(k2);
					if (k1 != k2 && p1.intersect(p2))
					{
						ArrayList<Point> newPts = labPts.get(k1);
						newPts.addAll(labPts.get(k2));
						polys.put(k1, MyPolygon.convexHull(newPts));

						labPts.remove(k2);
						polys.remove(k2);

						go = true;
						break;
					}
				}
				if (go)
					break;
			}
		}
	}

	public static double arrayAVG(ArrayList<Double> arr)
	{
		double res = 0.0;

		for (double v: arr)
			res += v;

		return res / arr.size();
	}

	public static double arraySD(ArrayList<Double> arr)
	{
		double avg = arrayAVG(arr);

		double res = 0.0;

		for (double v: arr)
			res += Math.pow(v - avg, 2);

		return Math.sqrt(res / (arr.size() - 1));
	}

	public static double arrayAVG(double[] arr)
	{
		double res = 0.0;

		for (double v: arr)
			res += v;

		return res / arr.length;
	}

	public static double arraySD(double[] arr)
	{
		double avg = arrayAVG(arr);

		double res = 0.0;

		for (double v: arr)
			res += Math.pow(v - avg, 2);

		return Math.sqrt(res / (arr.length - 1));
	}

	public static double[] centerOfMass(Matrix pts)
	{
		double[] muPts = new double[] {0.0, 0.0};

		for (int i = 0; i < pts.getRowDimension(); ++i)
		{
			muPts[0] += pts.get(i, 0);
			muPts[1] += pts.get(i, 1);
		}
		muPts[0] /= pts.getRowDimension();
		muPts[1] /= pts.getRowDimension();

		return muPts;
	}

	public static double[] centerOfMass(ArrayList<double[]> pts)
	{
		double[] muPts = new double[] {0.0, 0.0};

		for (final double[] pt: pts)
		{
			muPts[0] += pt[0];
			muPts[1] += pt[1];
		}
		muPts[0] /= pts.size();
		muPts[1] /= pts.size();

		return muPts;
	}

	public static double[] trajsDensFraction(TrajectoryEnsemble trajs, ScalarMap dens, double[] densGrid)
	{
		double[] res = new double[densGrid.length];
		for (int i = 0; i < res.length; ++i)
			res[i] = 0.0;

		return res;
	}

	public static double[] vecMaxPos(Matrix m)
	{
		double[] res = new double[] {m.get(0, 0), 0};
		for (int i = 0; i < m.getRowDimension(); ++i)
			if (m.get(i, 0) > res[0])
				res = new double[] {m.get(i, 0), i};
		return res;
	}

	public static Matrix matDiag(Matrix m)
	{
		assert(m.getRowDimension() == m.getColumnDimension());
		Matrix res = new Matrix(m.getRowDimension(), 1);
		for (int i = 0; i < m.getColumnDimension(); ++i)
			res.set(i, 0, m.get(i, i));

		return res;
	}

	public static Matrix vecDiag(Matrix v)
	{
		Matrix res = new Matrix(v.getRowDimension(), v.getRowDimension());
		for (int i = 0; i < v.getRowDimension(); ++i)
			res.set(i, i, v.get(i, 0));

		return res;
	}

	public static Ellipse minVolEllipse(ArrayList<Point> pts, double tol)
	{
		//There is a difference in the signs of the U,V SVD matrices produced
		//by JAMA compared to matlab, hence it can change the sign of the angle.
		//Is it important???
		int N = pts.size();

		Matrix Q = new Matrix(3, N);
		for (int i = 0; i < pts.size(); ++i)
		{
			Q.set(0, i, pts.get(i).x);	
			Q.set(1, i, pts.get(i).y);
			Q.set(2, i, 1);
		}

		double count = 1;
		double err = 1;
		Matrix u = new Matrix(N, 1, 1.0/N);

		while (err > tol)
		{
			Matrix X = Q.times(vecDiag(u).times(Q.transpose()));
			Matrix M = matDiag(Q.transpose().times(X.inverse().times(Q)));

			double[] maxPos = vecMaxPos(M);
			double step_size = (maxPos[0] - 2 - 1)/((2+1)*(maxPos[0]-1));

			Matrix new_u = u.times(1 - step_size);

			new_u.set((int) maxPos[1], 0, new_u.get((int) maxPos[1], 0) + step_size);

			count = count + 1;
			err = new_u.minus(u).norm2();
			u = new_u;
		}

		Matrix P = Q.getMatrix(0, Q.getRowDimension() - 2,
							   0, Q.getColumnDimension() - 1);
		Matrix c = P.times(u);
		Matrix U = vecDiag(u);

		Matrix A = ((P.times(U.times(P.transpose()))).minus(
				P.times(u).times((P.times(u)).transpose()))).inverse().times(0.5);
		SingularValueDecomposition svdd = new SingularValueDecomposition(A);

		double a = 1/ Math.sqrt(svdd.getS().get(0, 0));
		double b = 1/ Math.sqrt(svdd.getS().get(1, 1));
		double phi = Math.atan2(svdd.getU().get(1, 0), svdd.getU().get(0, 0));

		//make sure that a (resp. b) is the large (resp. small) semi-axis
		if (b > a)
		{
			double tmp = a;
			a = b;
			b = tmp;
			phi += (Math.PI / 2) % (2 * Math.PI);
		}

		return new Ellipse(new double[] {c.get(0, 0), c.get(1, 0)},
						   new double[] {Math.max(a, b), Math.min(a, b)},
						   phi);
	}

	public static Matrix covarianceMatrix(Matrix pts, double[] mu)
	{
		Matrix res = new Matrix(new double[][] {new double[] {0.0, 0.0},
												new double[] {0.0, 0.0}});

		int N = pts.getRowDimension();
		for (int i = 0; i < N; ++i)
		{
			res.set(0, 0, res.get(0, 0) + Math.pow(pts.get(i, 0) - mu[0], 2));
			res.set(0, 1, res.get(0, 1) + (pts.get(i, 0) - mu[0]) * (pts.get(i, 1) - mu[1]));
			res.set(1, 0, res.get(1, 0) + (pts.get(i, 1) - mu[1]) * (pts.get(i, 0) - mu[0]));
			res.set(1, 1, res.get(1, 1) + Math.pow(pts.get(i, 1) - mu[1], 2));
		}

		res.set(0, 0, res.get(0, 0) / (N-1));
		res.set(0, 1, res.get(0, 1) / (N-1));
		res.set(1, 0, res.get(1, 0) / (N-1));
		res.set(1, 1, res.get(1, 1) / (N-1));

		return res;
	}
	
	public static String rmTrail0(String f)
	{
		int idx = f.length() - 1;
		while (f.charAt(idx) == '0')
			--idx;
		if (idx < f.length() - 1 && f.charAt(idx) == '.')
			++idx;
		return f.substring(0, idx + 1);
	}
	
	public static ArrayList<Integer> findPeaks(ArrayList<Double> vals)
	{
		ArrayList<Integer> res = new ArrayList<> ();
		for (int i = 1; i < vals.size() - 1; ++i)
			if (vals.get(i) > vals.get(i-1) && vals.get(i) >= vals.get(i+1))
				res.add(i);
		return res;
	}
}
