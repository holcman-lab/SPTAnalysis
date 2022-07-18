package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

import fiji.plugin.SPTAnalysis.readers.CSVReaderOptions;
import fiji.plugin.SPTAnalysis.readers.TrajectoryCSVReader;
import fiji.plugin.SPTAnalysis.struct.MapParameters;
import fiji.plugin.SPTAnalysis.struct.SquareGrid;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;
import fiji.plugin.SPTAnalysis.struct.VectorMap;


public class NormalizeDriftTest
{
	@Test
	public void test()
	{
		String fname = ClassLoader.getSystemResource("trajectories/small_dataset_1.csv").getFile();

		CSVReaderOptions csvOpts = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 0, false, 0.0, false, 0.0);
		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvOpts);
		TrajectoryEnsemble trajs = reader.read();

		double dx = 0.2;
		SquareGrid g = new SquareGrid(trajs, dx);

		VectorMap drift = VectorMap.genDriftMap(g, trajs,
				new MapParameters.DriftParameters(g.dx(), 5, false, 0));
		VectorMap norm_drift = VectorMap.normalized_drift(drift);

		assertEquals(drift.normalized(), false);
		assertEquals(norm_drift.normalized(), true);

		Iterator<double[]> it = norm_drift.iterator();
		double[] v = it.next();
		assertArrayEquals(v, new double[] {0.0, 2.0, 0.5693, -0.8221}, 1e-4);

		v = it.next();
		assertArrayEquals(v, new double[] {1, 0, -0.4610, 0.8874}, 1e-4);
		v = it.next();
		assertArrayEquals(v, new double[] {1, 1, -0.5889, -0.8082}, 1e-4);
		v = it.next();
		assertArrayEquals(v, new double[] {1, 2, 0.9793, -0.2023}, 1e-4);
		v = it.next();
		assertArrayEquals(v, new double[] {1, 3, 0.8573, -0.5149}, 1e-4);

		v = it.next();
		assertArrayEquals(v, new double[] {2, 0, 0.9934, -0.1145}, 1e-4);
		v = it.next();
		assertArrayEquals(v, new double[] {2, 1, -0.7901, -0.6130}, 1e-4);
		v = it.next();
		assertArrayEquals(v, new double[] {2, 2, 0.1584, 0.9874}, 1e-4);
		v = it.next();
		assertArrayEquals(v, new double[] {2, 3, -0.0855, 0.9963}, 1e-4);
		v = it.next();
		assertArrayEquals(v, new double[] {2, 4, 0.7914, 0.6113}, 1e-4);

		v = it.next();
		assertArrayEquals(v, new double[] {3, 0, 0.4961, 0.8683}, 1e-4);
		v = it.next();
		assertArrayEquals(v, new double[] {3, 1, -0.3468, 0.9379}, 1e-4);
		v = it.next();
		assertArrayEquals(v, new double[] {3, 2, -0.8212, -0.5706}, 1e-4);
		v = it.next();
		assertArrayEquals(v, new double[] {3, 3, 0.1256, 0.9921}, 1e-4);
		v = it.next();
		assertArrayEquals(v, new double[] {3, 4, 0.6622, 0.7494}, 1e-4);
		v = it.next();
		assertArrayEquals(v, new double[] {3, 5, 0.9626, 0.2709}, 1e-4);

		v = it.next();
		assertArrayEquals(v, new double[] {4, 4, -0.4785, 0.8781}, 1e-4);
		v = it.next();
		assertArrayEquals(v, new double[] {4, 5, -0.9213, -0.3888}, 1e-4);

		assertEquals(it.hasNext(), false);
	}
}
