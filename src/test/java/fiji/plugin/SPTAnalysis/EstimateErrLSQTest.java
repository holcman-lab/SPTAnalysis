package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.junit.Test;

import fiji.plugin.SPTAnalysis.estimators.WellEstimator;
import fiji.plugin.SPTAnalysis.estimators.WellEstimatorFactory;
import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.MapParameters;
import fiji.plugin.SPTAnalysis.struct.SquareGrid;
import fiji.plugin.SPTAnalysis.struct.VectorMap;

public class EstimateErrLSQTest
{
	@Test
	public void test()
	{
		double[] c = {10.0, 10.0};
		double dx = 0.2;
		double A = 0.4;

		SquareGrid g = new SquareGrid(dx, c, 3);

		HashMap<Integer, HashMap<Integer, Double[]>> tmp = new HashMap<Integer, HashMap<Integer, Double[]>> ();
		ArrayList<int[]> sqs = new ArrayList<int[]> ();

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

		{
			Ellipse ell = new Ellipse(c, new double[] {1.0, 1.0}, Math.PI/4);
			WellEstimator west = WellEstimatorFactory.getGridEst(WellEstimator.type.LSQELL, null, ell,
					drift, sqs, true);
			assertEquals(0.0, west.estimateScore().value(), 1e-5);
		}

		{
			Ellipse ell = new Ellipse(c, new double[] {1.0, 1.0}, Math.PI/2);
			WellEstimator west = WellEstimatorFactory.getGridEst(WellEstimator.type.LSQCIRC, null, ell,
					drift, sqs, true);
			assertEquals(0.0, west.estimateScore().value(), 1e-5);
		}

		{
			Ellipse ell = new Ellipse(c, new double[] {1.0, 1.0}, 0.0);
			WellEstimator west = WellEstimatorFactory.getGridEst(WellEstimator.type.LSQELLNORM, null, ell,
					drift, sqs, true);
			assertEquals(0.0, west.estimateScore().value(), 1e-5);
		}
	}
}
