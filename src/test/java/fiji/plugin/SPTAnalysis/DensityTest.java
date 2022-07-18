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

public class DensityTest
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

		ScalarMap dens = ScalarMap.genDensityMap(g, trajs,
				new MapParameters.DensityParameters(g.dx(), ScalarMap.DensityOption.NPTS, 0));

		assertEquals(dens.get(0, 0), 2.0, 1e-5);
		assertEquals(dens.get(0, 1), 1.0, 1e-5);
		assertEquals(dens.get(0, 2), 9.0, 1e-5);
		assertEquals(dens.get(0, 3), 1.0, 1e-5);

		assertEquals(dens.get(1, 0), 22.0, 1e-5);
		assertEquals(dens.get(1, 1), 23.0, 1e-5);
		assertEquals(dens.get(1, 2), 32.0, 1e-5);
		assertEquals(dens.get(1, 3), 14.0, 1e-5);

		assertEquals(dens.get(2, 0), 49.0, 1e-5);
		assertEquals(dens.get(2, 1), 81.0, 1e-5);
		assertEquals(dens.get(2, 2), 83.0, 1e-5);
		assertEquals(dens.get(2, 3), 24.0, 1e-5);
		assertEquals(dens.get(2, 4), 14.0, 1e-5);
		assertEquals(dens.get(2, 5), 3.0, 1e-5);

		assertEquals(dens.get(3, 0), 25.0, 1e-5);
		assertEquals(dens.get(3, 1), 13.0, 1e-5);
		assertEquals(dens.get(3, 2), 11.0, 1e-5);
		assertEquals(dens.get(3, 3), 6.0, 1e-5);
		assertEquals(dens.get(3, 4), 32.0, 1e-5);
		assertEquals(dens.get(3, 5), 22.0, 1e-5);

		assertEquals(dens.get(4, 0), 1.0, 1e-5);
		assertEquals(dens.get(4, 1), 4.0, 1e-5);
		assertEquals(dens.get(4, 4), 13.0, 1e-5);
		assertEquals(dens.get(4, 5), 15.0, 1e-5);

		int cpt = 0;
		Iterator<double[]> it = dens.iterator();
		while (it.hasNext())
		{
			++cpt;
			@SuppressWarnings("unused")
			double[] v = it.next();
		}
		assertEquals(cpt, 24);
	}

	@Test
	public void test2()
	{
		String fname = ClassLoader.getSystemResource("trajectories/small_dataset_1.csv").getFile();

		CSVReaderOptions csvOpts = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 0, false, 0.0, false, 0.0);
		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvOpts);
		TrajectoryEnsemble trajs = reader.read();

		double dx = 0.1;
		SquareGrid g = new SquareGrid(dx, new double[] {0.5, 0.5}, 1);

		ScalarMap dens = ScalarMap.genDensityMap(g, trajs,
				new MapParameters.DensityParameters(g.dx(), ScalarMap.DensityOption.NPTS, 0));

		int cpt = 0;
		Iterator<double[]> it = dens.iterator();
		while (it.hasNext())
		{
			++cpt;
			@SuppressWarnings("unused")
			double[] v = it.next();
		}
		assertEquals(cpt, 9);

		assertEquals(dens.get(0, 0), 20.0000, 1e-5);
		assertEquals(dens.get(0, 1), 20.0000, 1e-5);
		assertEquals(dens.get(0, 2), 16.0000, 1e-5);
		assertEquals(dens.get(1, 0), 32.0000, 1e-5);
		assertEquals(dens.get(1, 1), 34.0000, 1e-5);
		assertEquals(dens.get(1, 2), 11.0000, 1e-5);
		assertEquals(dens.get(2, 0), 15.0000, 1e-5);
		assertEquals(dens.get(2, 1), 5.00000, 1e-5);
		assertEquals(dens.get(2, 2), 2.00000, 1e-5);
	}
}
