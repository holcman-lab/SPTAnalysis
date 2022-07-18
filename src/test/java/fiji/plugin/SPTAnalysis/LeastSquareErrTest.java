package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.junit.Test;

import fiji.plugin.SPTAnalysis.estimators.GridEstimatorParameters;
import fiji.plugin.SPTAnalysis.estimators.WellEstimator;
import fiji.plugin.SPTAnalysis.estimators.WellEstimatorFactory;
import fiji.plugin.SPTAnalysis.readers.CSVReaderOptions;
import fiji.plugin.SPTAnalysis.readers.TrajectoryCSVReader;
import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.MapParameters;
import fiji.plugin.SPTAnalysis.struct.SquareGrid;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;
import fiji.plugin.SPTAnalysis.struct.VectorMap;

public class LeastSquareErrTest
{
	@Test
	public void testPerfectDrift()
	{
		double[] c = {10.0, 10.0}; double dx = 0.2; double
		A = 0.4;

		SquareGrid g = new SquareGrid(dx, c, 3);

		HashMap<Integer, HashMap<Integer, Double[]>> tmp = new HashMap<Integer,
				HashMap<Integer, Double[]>> (); ArrayList<int[]> sqs = new ArrayList<int[]>();

		Iterator<int[]> it = g.iterator();
		while (it.hasNext())
		{
			int[] p = it.next();
			double[] pos = g.get(p[0], p[1]);
			sqs.add(p);
			if (!tmp.containsKey(p[0]))
				tmp.put(p[0], new HashMap<Integer, Double[]> ());
			tmp.get(p[0]).put(p[1], new Double[] {-2*A * (pos[0] - c[0]), -2*A * (pos[1] - c[1])});
		}

		VectorMap drift = new VectorMap(g, tmp,
				new MapParameters.DriftParameters(g.dx(), 0, false, 0));

		WellEstimator west = WellEstimatorFactory.getGridEst(WellEstimator.type.LSQELL, null,
				new Ellipse(c, new double[] {1.0, 1.0}, 0.0), drift, sqs, true);

		assertEquals(0.0, west.estimateScore().value() , 1e-5);
	}

	@Test
	public void testWell()
	{
		String fname = ClassLoader.getSystemResource("trajectories/trajs_well.csv").getFile();

		CSVReaderOptions csvOpts = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 0, false, 0.0, false, 0.0);
		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvOpts);
		TrajectoryEnsemble trajs = reader.read();

		Ellipse ell = new Ellipse(new double[] {10, 10}, new double[] {0.21, 0.21}, 0);

		double dx = 0.2;
		SquareGrid g = new SquareGrid(dx, ell.mu(), 5);

		VectorMap drift = VectorMap.genDriftMap(g, trajs,
				new MapParameters.DriftParameters(g.dx(), 5, false, 0));

		ArrayList<int[]> sqs = Utils.squaresInReg(g, ell);
		sqs = Utils.filter_empty_squares(drift, sqs);

		WellEstimator west = WellEstimatorFactory.getGridEst(WellEstimator.type.LSQELL, null,
				ell, drift, sqs, true);

		assertEquals(0.0384, west.estimateScore().value(), 1e-3);
	}

	@Test
	public void testWell2()
	{
		String fname = ClassLoader.getSystemResource("trajectories/trajs_well.csv").getFile();

		CSVReaderOptions csvOpts = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 0, false, 0.0, false, 0.0);
		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvOpts);
		TrajectoryEnsemble trajs = reader.read();

		Ellipse ell = new Ellipse(new double[] {10, 10}, new double[] {0.21, 0.21}, 0);

		GridEstimatorParameters ps = new GridEstimatorParameters(WellEstimator.type.LSQELL, 
				0.2, 5, 0, true, false);

		WellEstimatorFactory wef = new WellEstimatorFactory(trajs, ell, ps);

		assertEquals(0.0384, wef.getEst().estimateScore().value(), 1e-3);
	}
}
