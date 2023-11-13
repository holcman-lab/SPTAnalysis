package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.*;

import org.junit.Test;

import fiji.plugin.SPTAnalysis.readers.CSVReaderOptions;
import fiji.plugin.SPTAnalysis.readers.TrajectoryCSVReader;
import fiji.plugin.SPTAnalysis.struct.Trajectory;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;



public class ReaderTest
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

		assertEquals(trajs.trajs().size(), 10);

		for (Trajectory tr: trajs.trajs())
			assertEquals(tr.points().size(), 50);

		assertEquals(trajs.trajs().get(0).points().get(0).x, 0.49191, 1e-5);
		assertEquals(trajs.trajs().get(0).points().get(0).y, 0.46844, 1e-5);
		assertEquals(trajs.trajs().get(0).points().get(1).x, 0.46324, 1e-5);
		assertEquals(trajs.trajs().get(0).points().get(1).y, 0.5253, 1e-5);
		assertEquals(trajs.trajs().get(0).points().get(48).x, 0.18452, 1e-5);
		assertEquals(trajs.trajs().get(0).points().get(48).y, 0.035585, 1e-5);
		assertEquals(trajs.trajs().get(0).points().get(49).x, 0.18577, 1e-5);
		assertEquals(trajs.trajs().get(0).points().get(49).y, 0.093128, 1e-5);

		assertEquals(trajs.trajs().get(1).points().get(0).x, 0.48787, 1e-5);
		assertEquals(trajs.trajs().get(1).points().get(0).y, 0.47729, 1e-5);
		assertEquals(trajs.trajs().get(1).points().get(1).x, 0.50806, 1e-5);
		assertEquals(trajs.trajs().get(1).points().get(1).y, 0.5009, 1e-5);
		assertEquals(trajs.trajs().get(1).points().get(48).x, 0.92387, 1e-5);
		assertEquals(trajs.trajs().get(1).points().get(48).y, 0.34075, 1e-5);
		assertEquals(trajs.trajs().get(1).points().get(49).x, 0.8818, 1e-5);
		assertEquals(trajs.trajs().get(1).points().get(49).y, 0.36739, 1e-5);

		assertEquals(trajs.trajs().get(8).points().get(0).x, 0.50629, 1e-5);
		assertEquals(trajs.trajs().get(8).points().get(0).y, 0.45394, 1e-5);
		assertEquals(trajs.trajs().get(8).points().get(1).x, 0.44332, 1e-5);
		assertEquals(trajs.trajs().get(8).points().get(1).y, 0.39024, 1e-5);
		assertEquals(trajs.trajs().get(8).points().get(48).x, 0.65239, 1e-5);
		assertEquals(trajs.trajs().get(8).points().get(48).y, 0.4277, 1e-5);
		assertEquals(trajs.trajs().get(8).points().get(49).x, 0.69168, 1e-5);
		assertEquals(trajs.trajs().get(8).points().get(49).y, 0.38628, 1e-5);

		assertEquals(trajs.trajs().get(9).points().get(0).x, 0.51998, 1e-5);
		assertEquals(trajs.trajs().get(9).points().get(0).y, 0.46584, 1e-5);
		assertEquals(trajs.trajs().get(9).points().get(1).x, 0.53386, 1e-5);
		assertEquals(trajs.trajs().get(9).points().get(1).y, 0.45993, 1e-5);
		assertEquals(trajs.trajs().get(9).points().get(48).x, 0.3664, 1e-5);
		assertEquals(trajs.trajs().get(9).points().get(48).y, 0.43412, 1e-5);
		assertEquals(trajs.trajs().get(9).points().get(49).x, 0.31277, 1e-5);
		assertEquals(trajs.trajs().get(9).points().get(49).y, 0.43912, 1e-5);
	}

	@Test
	public void test2()
	{
		String fname = ClassLoader.getSystemResource("trajectories/small_dataset_1_2headlines.csv").getFile();

		CSVReaderOptions csvOpts = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 2, false, 0.0, false, 0.0);
		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvOpts);
		TrajectoryEnsemble trajs = null;
		try {
			trajs = reader.read();
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertEquals(trajs.trajs().size(), 10);

		for (Trajectory tr: trajs.trajs())
			assertEquals(tr.points().size(), 50);

		assertEquals(trajs.trajs().get(0).points().get(0).x, 0.49191, 1e-5);
		assertEquals(trajs.trajs().get(0).points().get(0).y, 0.46844, 1e-5);
		assertEquals(trajs.trajs().get(0).points().get(1).x, 0.46324, 1e-5);
		assertEquals(trajs.trajs().get(0).points().get(1).y, 0.5253, 1e-5);
		assertEquals(trajs.trajs().get(0).points().get(48).x, 0.18452, 1e-5);
		assertEquals(trajs.trajs().get(0).points().get(48).y, 0.035585, 1e-5);
		assertEquals(trajs.trajs().get(0).points().get(49).x, 0.18577, 1e-5);
		assertEquals(trajs.trajs().get(0).points().get(49).y, 0.093128, 1e-5);

		assertEquals(trajs.trajs().get(1).points().get(0).x, 0.48787, 1e-5);
		assertEquals(trajs.trajs().get(1).points().get(0).y, 0.47729, 1e-5);
		assertEquals(trajs.trajs().get(1).points().get(1).x, 0.50806, 1e-5);
		assertEquals(trajs.trajs().get(1).points().get(1).y, 0.5009, 1e-5);
		assertEquals(trajs.trajs().get(1).points().get(48).x, 0.92387, 1e-5);
		assertEquals(trajs.trajs().get(1).points().get(48).y, 0.34075, 1e-5);
		assertEquals(trajs.trajs().get(1).points().get(49).x, 0.8818, 1e-5);
		assertEquals(trajs.trajs().get(1).points().get(49).y, 0.36739, 1e-5);

		assertEquals(trajs.trajs().get(8).points().get(0).x, 0.50629, 1e-5);
		assertEquals(trajs.trajs().get(8).points().get(0).y, 0.45394, 1e-5);
		assertEquals(trajs.trajs().get(8).points().get(1).x, 0.44332, 1e-5);
		assertEquals(trajs.trajs().get(8).points().get(1).y, 0.39024, 1e-5);
		assertEquals(trajs.trajs().get(8).points().get(48).x, 0.65239, 1e-5);
		assertEquals(trajs.trajs().get(8).points().get(48).y, 0.4277, 1e-5);
		assertEquals(trajs.trajs().get(8).points().get(49).x, 0.69168, 1e-5);
		assertEquals(trajs.trajs().get(8).points().get(49).y, 0.38628, 1e-5);

		assertEquals(trajs.trajs().get(9).points().get(0).x, 0.51998, 1e-5);
		assertEquals(trajs.trajs().get(9).points().get(0).y, 0.46584, 1e-5);
		assertEquals(trajs.trajs().get(9).points().get(1).x, 0.53386, 1e-5);
		assertEquals(trajs.trajs().get(9).points().get(1).y, 0.45993, 1e-5);
		assertEquals(trajs.trajs().get(9).points().get(48).x, 0.3664, 1e-5);
		assertEquals(trajs.trajs().get(9).points().get(48).y, 0.43412, 1e-5);
		assertEquals(trajs.trajs().get(9).points().get(49).x, 0.31277, 1e-5);
		assertEquals(trajs.trajs().get(9).points().get(49).y, 0.43912, 1e-5);
	}
}
