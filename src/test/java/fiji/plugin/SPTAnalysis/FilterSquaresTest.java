package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import fiji.plugin.SPTAnalysis.readers.CSVReaderOptions;
import fiji.plugin.SPTAnalysis.readers.TrajectoryCSVReader;
import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.MapParameters;
import fiji.plugin.SPTAnalysis.struct.SquareGrid;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;
import fiji.plugin.SPTAnalysis.struct.VectorMap;


public class FilterSquaresTest
{
	@Test
	public void test()
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

		SquareGrid g = new SquareGrid(0.2, new double[] {10.0, 10.0}, 6);

		VectorMap drift = VectorMap.genDriftMap(g, trajs,
				new MapParameters.DriftParameters(g.dx(), 5, false, 0));

		Ellipse ell = new Ellipse(new double[] {9.9979, 10.0039}, new double[] {0.2302, 0.2037}, 0.4481);

		ArrayList<int[]> nh = Utils.squaresInReg(g, ell);
		nh = Utils.filter_empty_squares(drift, nh);

		assertEquals(5, nh.size());
		assertTrue(TestUtils.pointInList(nh, new int[] {5, 6}));
		assertTrue(TestUtils.pointInList(nh, new int[] {6, 5}));
		assertTrue(TestUtils.pointInList(nh, new int[] {6, 6}));
		assertTrue(TestUtils.pointInList(nh, new int[] {7, 6}));
		assertTrue(TestUtils.pointInList(nh, new int[] {6, 7}));
	}
}