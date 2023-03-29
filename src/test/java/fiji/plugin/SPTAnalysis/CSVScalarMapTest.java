package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fiji.plugin.SPTAnalysis.readers.CSVReaderOptions;
import fiji.plugin.SPTAnalysis.readers.TrajectoryCSVReader;
import fiji.plugin.SPTAnalysis.struct.MapParameters;
import fiji.plugin.SPTAnalysis.struct.Rectangle;
import fiji.plugin.SPTAnalysis.struct.ScalarMap;
import fiji.plugin.SPTAnalysis.struct.SquareGrid;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsembleWindows;
import fiji.plugin.SPTAnalysis.writers.CSVScalarMapWriter;

public class CSVScalarMapTest
{
	@Test
	public void test()
	{
		String fname = ClassLoader.getSystemResource("trajectories/trajs_well.csv").getFile();

		CSVReaderOptions csvOpts = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 0, false, 0.0, false, 0.0);
		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvOpts);
		TrajectoryEnsemble trajs = reader.read();

		SquareGrid g = new SquareGrid(0.1, new double[] {10.0, 10.0}, 6);
		ScalarMap dens = ScalarMap.genDensityMap(g, trajs,
				new MapParameters.DensityParameters(g.dx(), ScalarMap.DensityOption.NPTS, 0));

		String res = (new CSVScalarMapWriter(" ", dens)).generate();
		assertEquals("9.50000 10.2000 1.00000\n" + 
				"9.60000 10.2000 3.00000\n" + 
				"9.70000 10.1000 3.00000\n" + 
				"9.70000 10.2000 3.00000\n" + 
				"9.80000 9.80000 1.00000\n" + 
				"9.80000 9.90000 2.00000\n" + 
				"9.80000 10.0000 12.0000\n" + 
				"9.80000 10.1000 11.0000\n" + 
				"9.80000 10.2000 2.00000\n" + 
				"9.90000 9.70000 1.00000\n" + 
				"9.90000 9.80000 7.00000\n" + 
				"9.90000 9.90000 71.0000\n" + 
				"9.90000 10.0000 127.000\n" + 
				"9.90000 10.1000 51.0000\n" + 
				"9.90000 10.2000 5.00000\n" + 
				"9.90000 10.3000 1.00000\n" + 
				"10.0000 9.70000 1.00000\n" + 
				"10.0000 9.80000 13.0000\n" + 
				"10.0000 9.90000 124.000\n" + 
				"10.0000 10.0000 283.000\n" + 
				"10.0000 10.1000 145.000\n" + 
				"10.0000 10.2000 15.0000\n" + 
				"10.1000 9.80000 5.00000\n" + 
				"10.1000 9.90000 63.0000\n" + 
				"10.1000 10.0000 85.0000\n" + 
				"10.1000 10.1000 52.0000\n" + 
				"10.1000 10.2000 9.00000\n" + 
				"10.2000 9.80000 1.00000\n" + 
				"10.2000 9.90000 10.0000\n" + 
				"10.2000 10.0000 8.00000\n" + 
				"10.2000 10.1000 5.00000\n" + 
				"10.2000 10.2000 5.00000\n" + 
				"10.3000 10.0000 1.00000\n" + 
				"10.3000 10.1000 1.00000\n" + 
				"10.3000 10.2000 1.00000\n" + 
				"10.4000 10.2000 8.00000\n" + 
				"10.4000 10.3000 2.00000\n" + 
				"10.4000 10.4000 1.00000\n" + 
				"10.5000 10.4000 1.00000\n", res);
	}

	@Test
	public void test2()
	{
		String fname = ClassLoader.getSystemResource("trajectories/trajs_well.csv").getFile();

		CSVReaderOptions csvOpts = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 0, false, 0.0, false, 0.0);
		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvOpts);
		TrajectoryEnsemble trajs = reader.read();
		TrajectoryEnsembleWindows trajsw = new TrajectoryEnsembleWindows();
		trajsw.wins.add(trajs);

		SquareGrid g = new SquareGrid(0.1, new double[] {10.0, 10.0}, 6);
		ScalarMap dens = ScalarMap.genDensityMap(g, trajs,
				new MapParameters.DensityParameters(g.dx(), ScalarMap.DensityOption.NPTS, 0));

		Rectangle bnds = new Rectangle(new double[] {0.0, 0.0}, trajsw.maxCoords());

		String res1 = (new CSVScalarMapWriter(" ", dens)).generate();
		String res2 = (new CSVScalarMapWriter(" ", dens, Utils.squaresInReg(g, bnds))).generate();
		assertEquals(res1, res2);
	}
}
