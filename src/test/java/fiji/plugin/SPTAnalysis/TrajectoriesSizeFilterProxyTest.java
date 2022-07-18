package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.*;

import org.junit.Test;

import fiji.plugin.SPTAnalysis.readers.CSVReaderOptions;
import fiji.plugin.SPTAnalysis.readers.TrajectoryCSVReader;
import fiji.plugin.SPTAnalysis.struct.TrajectoriesSizeFilter;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;

public class TrajectoriesSizeFilterProxyTest
{
	@Test
	public void test()
	{
		String fname = ClassLoader.getSystemResource("trajectories/small_dataset_1.csv").getFile();

		CSVReaderOptions csvOpts = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 0, false, 0.0, false, 0.0);
		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvOpts);
		TrajectoryEnsemble trajs = reader.read();

		TrajectoryEnsemble trajsFilt = new TrajectoriesSizeFilter(3).run(trajs);

		assertEquals(trajs.trajs().size(), trajsFilt.trajs().size());
		assertArrayEquals(trajs.max(), trajsFilt.max(), 1e-5);
		assertArrayEquals(trajs.min(), trajsFilt.min(), 1e-5);
	}
}
