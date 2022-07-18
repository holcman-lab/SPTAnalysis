package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fiji.plugin.SPTAnalysis.readers.CSVReaderOptions;
import fiji.plugin.SPTAnalysis.readers.TrajectoryCSVReader;
import fiji.plugin.SPTAnalysis.struct.MapParameters;
import fiji.plugin.SPTAnalysis.struct.Rectangle;
import fiji.plugin.SPTAnalysis.struct.SquareGrid;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsembleWindows;
import fiji.plugin.SPTAnalysis.struct.VectorMap;
import fiji.plugin.SPTAnalysis.writers.CSVVectorMapWriter;

public class CSVVectorMapTest
{
	@Test
	public void test()
	{
		String fname = ClassLoader.getSystemResource("trajectories/trajs_well.csv").getFile();

		CSVReaderOptions csvOpts = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 0, false, 0.0, false, 0.0);
		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvOpts);
		TrajectoryEnsemble trajs = reader.read();

		SquareGrid g = new SquareGrid(0.1, new double[] {10.0, 10.0}, 6);
		VectorMap drift = VectorMap.genDriftMap(g, trajs, new MapParameters.DriftParameters(g.dx(), 3, false, 0));

		String res = (new CSVVectorMapWriter(drift)).generate();
		assertEquals("10.2000 9.60000 0.525000 0.633333\n" + 
				"10.1000 9.70000 0.0400000 2.00000\n" + 
				"10.0000 9.80000 0.670417 1.59208\n" + 
				"10.1000 9.80000 0.0355000 -1.48750\n" + 
				"9.80000 9.90000 0.967143 1.57214\n" + 
				"9.90000 9.90000 0.844104 0.657910\n" + 
				"10.0000 9.90000 0.816557 -0.142213\n" + 
				"10.1000 9.90000 1.19571 -1.13592\n" + 
				"10.2000 9.90000 1.07500 1.01250\n" + 
				"9.80000 10.0000 -0.605833 0.447917\n" + 
				"9.90000 10.0000 -0.133992 0.902731\n" + 
				"10.0000 10.0000 -0.169812 -0.118609\n" + 
				"10.1000 10.0000 -0.205000 -0.711213\n" + 
				"10.2000 10.0000 -0.428000 -1.92333\n" + 
				"9.80000 10.1000 -0.0500000 1.77400\n" + 
				"9.90000 10.1000 -1.05441 1.16119\n" + 
				"10.0000 10.1000 -0.772469 0.0663580\n" + 
				"10.1000 10.1000 -1.07716 -0.842157\n" + 
				"10.2000 10.1000 -0.953333 -0.905556\n" + 
				"9.90000 10.2000 -0.720000 0.677000\n" + 
				"10.0000 10.2000 0.437500 0.175000\n" + 
				"10.1000 10.2000 -2.78750 -0.970000\n" + 
				"10.2000 10.2000 1.29000 0.230000\n" + 
				"10.2000 10.4000 0.106250 0.593750\n", res);
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
		VectorMap drift = VectorMap.genDriftMap(g, trajs,
				new MapParameters.DriftParameters(g.dx(), 3, false, 0));

		Rectangle bnds = new Rectangle(new double[] {0.0, 0.0}, trajsw.maxCoords());

		String res1 = (new CSVVectorMapWriter(drift)).generate();
		String res2 = (new CSVVectorMapWriter(drift, Utils.squaresInReg(g, bnds))).generate();
		assertEquals(res1, res2);
	}
}
