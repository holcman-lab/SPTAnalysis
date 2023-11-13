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


public class DriftTest
{
	@Test
	public void test()
	{
		String fname = ClassLoader.getSystemResource("trajectories/small_dataset_1.csv").getFile();

		CSVReaderOptions csvOpts = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 0, false, 0.0, false, 0.0);
		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvOpts);
		TrajectoryEnsemble trajs = null;
		try {
			trajs = reader.read();
		} catch (Exception e) {
			e.printStackTrace();
		}

		double dx = 0.2;
		SquareGrid g = new SquareGrid(trajs, dx);

		VectorMap drift = VectorMap.genDriftMap(g, trajs,
				new MapParameters.DriftParameters(g.dx(), 5, false, 0));

		Iterator<double[]> it = drift.iterator();
		double[] v = it.next();
		assertArrayEquals(v, new double[] {0.0, 2.0, 0.1095, -0.1581}, 1e-4);

		v = it.next();
		assertArrayEquals(v, new double[] {1, 0, -0.0950, 0.1830}, 1e-4);
		v = it.next();
		assertArrayEquals(v, new double[] {1, 1, -0.1786, -0.2451}, 1e-4);
		v = it.next();
		assertArrayEquals(v, new double[] {1, 2, 0.2102, -0.0434}, 1e-4);
		v = it.next();
		assertArrayEquals(v, new double[] {1, 3, 0.6566, -0.3944}, 1e-4);

		v = it.next();
		assertArrayEquals(v, new double[] {2, 0, 0.3784, -0.0436}, 1e-4);
		v = it.next();
		assertArrayEquals(v, new double[] {2, 1, -0.4518, -0.3506}, 1e-4);
		v = it.next();
		assertArrayEquals(v, new double[] {2, 2, 0.0249, 0.1552}, 1e-4);
		v = it.next();
		assertArrayEquals(v, new double[] {2, 3, -0.0766, 0.8917}, 1e-4);
		v = it.next();
		assertArrayEquals(v, new double[] {2, 4, 0.6163, 0.4760}, 1e-4);

		v = it.next();
		assertArrayEquals(v, new double[] {3, 0, 0.2138, 0.3742}, 1e-4);
		v = it.next();
		assertArrayEquals(v, new double[] {3, 1, -0.2957, 0.7997}, 1e-4);
		v = it.next();
		assertArrayEquals(v, new double[] {3, 2, -0.8325, -0.5784}, 1e-4);
		v = it.next();
		assertArrayEquals(v, new double[] {3, 3, 0.1832, 1.4473}, 1e-4);
		v = it.next();
		assertArrayEquals(v, new double[] {3, 4, 0.4608, 0.5215}, 1e-4);
		v = it.next();
		assertArrayEquals(v, new double[] {3, 5, 0.7891, 0.2221}, 1e-4);

		v = it.next();
		assertArrayEquals(v, new double[] {4, 4, -0.5379, 0.9871}, 1e-4);
		v = it.next();
		assertArrayEquals(v, new double[] {4, 5, -0.6487, -0.2738}, 1e-4);

		assertEquals(it.hasNext(), false);
	}

	@Test
	public void testDriftWell()
	{
		String fname = ClassLoader.getSystemResource("trajectories/trajs_well.csv").getFile();

		CSVReaderOptions csvOpts = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 0, false, 0.0, false, 0.0);
		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvOpts);
		TrajectoryEnsemble trajs = null;
		try {
			trajs = reader.read();
		} catch (Exception e) {
			e.printStackTrace();
		}

		SquareGrid g = new SquareGrid(0.2, new double[] {10.0, 10.0}, 2);

		VectorMap drift = VectorMap.genDriftMap(g, trajs,
				new MapParameters.DriftParameters(g.dx(), 5, false, 0));

		assertArrayEquals(new double[] {0.4108, 0.0417}, new double[] {drift.get(0, 3)[0], drift.get(0, 3)[1]}, 1e-3);
		assertArrayEquals(new double[] {1.7155, 0.6905}, new double[] {drift.get(1, 1)[0], drift.get(1, 1)[1]}, 1e-3);
		assertArrayEquals(new double[] {1.4936, 0.1095}, new double[] {drift.get(1, 2)[0], drift.get(1, 2)[1]}, 1e-3);
		assertArrayEquals(new double[] {-0.2272, -1.1222}, new double[] {drift.get(1, 3)[0], drift.get(1, 3)[1]}, 1e-3);
		assertArrayEquals(new double[] {-0.3044, 1.2570}, new double[] {drift.get(2, 1)[0], drift.get(2, 1)[1]}, 1e-3);
		assertArrayEquals(new double[] {-0.0899, -0.0586}, new double[] {drift.get(2, 2)[0], drift.get(2, 2)[1]}, 1e-3);
		assertArrayEquals(new double[] {-0.2431, -1.3409}, new double[] {drift.get(2, 3)[0], drift.get(2, 3)[1]}, 1e-3);
		assertArrayEquals(new double[] {-0.5450, 3.1225}, new double[] {drift.get(3, 1)[0], drift.get(3, 1)[1]}, 1e-3);
		assertArrayEquals(new double[] {-1.0755, 0.0201}, new double[] {drift.get(3, 2)[0], drift.get(3, 2)[1]}, 1e-3);
		assertArrayEquals(new double[] {-0.0500, -0.1300}, new double[] {drift.get(3, 3)[0], drift.get(3, 3)[1]}, 1e-3);
		assertArrayEquals(new double[] {0.1062, 0.5938}, new double[] {drift.get(4, 3)[0], drift.get(4, 3)[1]}, 1e-3);
	}
}
