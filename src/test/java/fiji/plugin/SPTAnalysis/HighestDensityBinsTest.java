package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.junit.Test;

import fiji.plugin.SPTAnalysis.readers.CSVReaderOptions;
import fiji.plugin.SPTAnalysis.readers.TrajectoryCSVReader;
import fiji.plugin.SPTAnalysis.struct.MapParameters;
import fiji.plugin.SPTAnalysis.struct.ScalarMap;
import fiji.plugin.SPTAnalysis.struct.SquareGrid;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;


public class HighestDensityBinsTest
{
	@Test
	public void test()
	{
		SquareGrid g = new SquareGrid(1, new double[] {10.0, 10.0}, new double[] {20.0, 20.0});

		ArrayList<Double> vals = new ArrayList<> ();
		for (int i = 1; i <= 100; ++i)
			vals.add((double) i);
		Collections.shuffle(vals);

		HashMap<Integer, HashMap<Integer, Double>> mH = new HashMap<Integer, HashMap<Integer, Double>>();
		Iterator<int[]> it = g.iterator();
		int cpt = 0;
		while (it.hasNext())
		{
			int[] p = it.next();
			if (!mH.containsKey(p[0]))
				mH.put(p[0], new HashMap<Integer, Double> ());
			mH.get(p[0]).put(p[1], vals.get(cpt));
			++cpt;
		}

		ScalarMap m = new ScalarMap(g, mH,
				new MapParameters.DensityParameters(g.dx(), ScalarMap.DensityOption.NPTS, 0));
		ArrayList<int[]> cells = Utils.highest_density_cells(m, 5.0);

		for (int i = 0; i < cells.size(); ++i)
			assertEquals(m.get(cells.get(i)[0], cells.get(i)[1]), 100-i, 1e-5);
	}

	@Test
	public void testSeed()
	{
		String fname = ClassLoader.getSystemResource("trajectories/trajs_well.csv").getFile();

		CSVReaderOptions csvOpts = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 0, false, 0.0, false, 0.0);
		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvOpts);
		TrajectoryEnsemble trajs = reader.read();

		SquareGrid g = new SquareGrid(trajs, 0.2);
		ScalarMap dens = ScalarMap.genDensityMap(g, trajs,
				new MapParameters.DensityParameters(g.dx(), ScalarMap.DensityOption.DENS, 0));

		ArrayList<int[]> hds = Utils.highest_density_cells(dens, 5);

		assertEquals(1, hds.size());
		assertArrayEquals(new int[] {49, 50}, hds.get(0));
	}
}
