package fiji.plugin.SPTAnalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.junit.Test;

import fiji.plugin.SPTAnalysis.readers.CSVReaderOptions;
import fiji.plugin.SPTAnalysis.readers.TrajectoryCSVReader;
import fiji.plugin.SPTAnalysis.struct.MapParameters;
import fiji.plugin.SPTAnalysis.struct.Point;
import fiji.plugin.SPTAnalysis.struct.ScalarMap;
import fiji.plugin.SPTAnalysis.struct.ScalarMapWindows;
import fiji.plugin.SPTAnalysis.struct.SquareGrid;
import fiji.plugin.SPTAnalysis.struct.TimeWindows;
import fiji.plugin.SPTAnalysis.struct.Trajectory;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsembleWindows;


public class AnomalousDiffusionTest
{
//	@Test
//	public void test()
//	{
//		String fname = ClassLoader.getSystemResource("trajectories/trajs_H:0.5_17012023@131727.csv").getFile();
//
//		CSVReaderOptions csvOpts = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 1, false, 0.0, false, 0.0);
//		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvOpts);
//		TrajectoryEnsemble trajs = null;
//		try {
//			trajs = reader.read();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		TimeWindows tw = TimeWindows.singleWindow(trajs);
//		TrajectoryEnsembleWindows trajsw = new TrajectoryEnsembleWindows(trajs, tw);
//
//		SquareGrid g = new SquareGrid(trajs, 0.2);
//
//		int nPtsFit = 5;
//
//		HashMap<Integer, HashMap<Integer, List<Trajectory>>> trajsMap = new HashMap<Integer, HashMap<Integer, List<Trajectory>>>();
//		HashMap<Integer, HashMap<Integer, List<Integer>>> trajsIdMap = new HashMap<Integer, HashMap<Integer, List<Integer>>>();
//
//		for (Trajectory tr: trajs.trajs())
//		{
//			for (int i = 0; i < tr.points().size() - 1; ++i)
//			{
//				Point p1 = tr.points().get(i);
//
//				if (p1.x < g.Xmin()[0] || p1.x > g.Xmax()[0] || p1.y < g.Xmin()[1] || p1.y > g.Xmax()[1])
//					continue;
//
//				int[] gpos = g.pos_to_gpos(new double[] {p1.x, p1.y});
//
//				if (!trajsMap.containsKey(gpos[0]))
//					trajsMap.put(gpos[0], new HashMap<Integer, List<Trajectory>>());
//				if (!trajsMap.get(gpos[0]).containsKey(gpos[1]))
//					trajsMap.get(gpos[0]).put(gpos[1], new ArrayList<Trajectory>());
//
//				if (tr.points().size() - i > nPtsFit)
//					trajsMap.get(gpos[0]).get(gpos[1]).add(Trajectory.subTrajStartingAt(tr, i));
//
//				if (!trajsIdMap.containsKey(gpos[0]))
//					trajsIdMap.put(gpos[0], new HashMap<Integer, List<Integer>>());
//				if (!trajsIdMap.get(gpos[0]).containsKey(gpos[1]))
//					trajsIdMap.get(gpos[0]).put(gpos[1], new ArrayList<Integer>());
//
//				if (tr.points().size() - i > nPtsFit)
//					trajsIdMap.get(gpos[0]).get(gpos[1]).add(tr.id());
//			}
//		}
//
//		for (Integer i: trajsMap.keySet())
//		{
//			for (Integer j: trajsMap.get(i).keySet())
//			{
//				double alphas = 0.0;
//				double ds = 0.0;
//				ArrayList<Double> alphass = new ArrayList<> ();
//				ArrayList<Double> dss = new ArrayList<> ();
//				ArrayList<Double> rs = new ArrayList<> ();
//				double N = trajsMap.get(i).get(j).size();
//	
//				for (Trajectory tr: trajsMap.get(i).get(j))
//				{
//					double[][] xs = new double[nPtsFit][2];
//					final Point p0 = tr.points().get(0);
//					for (int k = 0; k < nPtsFit; ++k)
//					{
//						final Point p = tr.points().get(k+1);
//						xs[k][0] = Math.log(p.t - p0.t);
//						xs[k][1] = Math.log((p.x - p0.x) * (p.x - p0.x) + (p.y - p0.y) * (p.y - p0.y));
//					}
//	
//					SimpleRegression sr = new SimpleRegression(true);
//					sr.addData(xs);
//					alphas += sr.getSlope();
//					alphass.add(sr.getSlope());
//					ds += Math.exp(sr.getIntercept());
//					dss.add(Math.exp(sr.getIntercept()));
//					rs.add(sr.getRSquare());
//				}
//
//				ds = ds / N;
//				if (ds > 5)
//				{
//					System.out.println(String.format("%g %g", ds, N));
//					for (int k = 0; k < trajsIdMap.get(i).get(j).size(); ++k)
//						System.out.println(String.format("%d %g %g | %g", trajsIdMap.get(i).get(j).get(k), dss.get(k), alphass.get(k), rs.get(k)));
//				}
//			}
//		}
//	}

	@Test
	public void test()
	{
		String fname = ClassLoader.getSystemResource("trajectories/trajs_H:0.5_17012023@131727.csv").getFile();

		CSVReaderOptions csvOpts = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 1, false, 0.0, false, 0.0);
		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvOpts);
		TrajectoryEnsemble trajs = null;
		try {
			trajs = reader.read();
		} catch (Exception e) {
			e.printStackTrace();
		}

		TimeWindows tw = TimeWindows.singleWindow(trajs);
		TrajectoryEnsembleWindows trajsw = new TrajectoryEnsembleWindows(trajs, tw);

		double dx = 0.2;
		SquareGrid g = new SquareGrid(trajs, dx);

		MapParameters.AnomalousDiffusionParameters ps = new MapParameters.AnomalousDiffusionParameters(g.dx(), 2, 4, false, 0);
		HashMap<String, ScalarMap> anoDiff = ScalarMap.genAnomalousDiffusionMap(g, trajs, ps);

//			ScalarMap ad = anoDiff.get("d");
//			Iterator<double[]> it = ad.iterator();
//			while (it.hasNext())
//			{
//				double[] elts = it.next();
//				System.out.println(String.format("%g %g %g", elts[0], elts[1], elts[2]));
//			}
//			ScalarMap alpha = anoDiff.get("d");
//			it = alpha.iterator();
//			while (it.hasNext())
//			{
//				double[] elts = it.next();
//				System.out.println(String.format("%g %g %g", elts[0], elts[1], elts[2]));
//			}
		HashMap<String,ScalarMapWindows> mapsw = ScalarMapWindows.genAnomalousDiffusionMaps(trajsw, ps);
		//System.out.println(mapsw.get("d").wins.get(0).dump());
		//System.out.println(mapsw.get("alpha").wins.get(0).dump());
	}
}
