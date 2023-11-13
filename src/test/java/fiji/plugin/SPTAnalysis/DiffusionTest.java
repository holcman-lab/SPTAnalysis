package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

import fiji.plugin.SPTAnalysis.readers.CSVReaderOptions;
import fiji.plugin.SPTAnalysis.readers.TrajectoryCSVReader;
import fiji.plugin.SPTAnalysis.struct.MapParameters;
import fiji.plugin.SPTAnalysis.struct.ScalarMap;
import fiji.plugin.SPTAnalysis.struct.SquareGrid;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;


public class DiffusionTest
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

		ScalarMap diff = ScalarMap.genDiffusionMap(g, trajs,
				new MapParameters.DiffusionParameters(g.dx(), 2, false, 0));

		assertEquals(0.0406, diff.get(0, 2), 1e-2);

		assertEquals(0.0477, diff.get(1, 0), 1e-2);
		assertEquals(0.0610, diff.get(1, 1), 1e-2);
		assertEquals(0.0536, diff.get(1, 2), 1e-2);
		assertEquals(0.0356, diff.get(1, 3), 1e-2);

		assertEquals(0.0410, diff.get(2, 0), 1e-2);
		assertEquals(0.0493, diff.get(2, 1), 1e-2);
		assertEquals(0.0570, diff.get(2, 2), 1e-2);
		assertEquals(0.0843, diff.get(2, 3), 1e-2);
		assertEquals(0.0729, diff.get(2, 4), 1e-2);
		assertEquals(0.0763, diff.get(2, 5), 1e-2);

		assertEquals(0.0469, diff.get(3, 0), 1e-2);
		assertEquals(0.0462, diff.get(3, 1), 1e-2);
		assertEquals(0.0495, diff.get(3, 2), 1e-2);
		assertEquals(0.0225, diff.get(3, 3), 1e-2);
		assertEquals(0.0487, diff.get(3, 4), 1e-2);
		assertEquals(0.0430, diff.get(3, 5), 1e-2);

		assertEquals(0.0914, diff.get(4, 1), 1e-2);
		assertEquals(0.0633, diff.get(4, 4), 1e-2);
		assertEquals(0.0410, diff.get(4, 5), 1e-2);

		int cpt = 0;
		Iterator<double[]> it = diff.iterator();
		while (it.hasNext())
		{
			++cpt;
			@SuppressWarnings("unused")
			double[] v = it.next();
		}
		assertEquals(20, cpt);
	}

	@Test
	public void test2()
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

		double dx = 0.1;
		SquareGrid g = new SquareGrid(dx, new double[] {0.5, 0.5}, 1);

		ScalarMap dens = ScalarMap.genDiffusionMap(g, trajs,
				new MapParameters.DiffusionParameters(g.dx(), 10, false, 0));

		int cpt = 0;
		Iterator<double[]> it = dens.iterator();
		while (it.hasNext())
		{
			++cpt;
			@SuppressWarnings("unused")
			double[] v = it.next();
		}
		assertEquals(cpt, 7);

		assertEquals(dens.get(0, 0), 0.04174, 1e-5);
		assertEquals(dens.get(0, 1), 0.06508, 1e-5);
		assertEquals(dens.get(0, 2), 0.05132, 1e-5);
		assertEquals(dens.get(1, 0), 0.05160, 1e-5);
		assertEquals(dens.get(1, 1), 0.05041, 1e-5);
		assertEquals(dens.get(1, 2), 0.05804, 1e-5);
		assertEquals(dens.get(2, 0), 0.04784, 1e-5);

	}
}
