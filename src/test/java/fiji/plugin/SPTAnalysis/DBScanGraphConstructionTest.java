package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.*;

import org.junit.Test;

import fiji.plugin.SPTAnalysis.graphConstruction.GraphConstructionDBScan;
import fiji.plugin.SPTAnalysis.graphConstruction.GraphConstructionDBScanParameters;
import fiji.plugin.SPTAnalysis.graphConstruction.GraphConstructionParameters;
import fiji.plugin.SPTAnalysis.readers.CSVReaderOptions;
import fiji.plugin.SPTAnalysis.readers.TrajectoryCSVReader;
import fiji.plugin.SPTAnalysis.struct.Graph;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;

public class DBScanGraphConstructionTest
{
	@Test
	public void test()
	{
		String fname = ClassLoader.getSystemResource("trajectories/trajs_graph_1.csv").getFile();

		CSVReaderOptions csvOpts = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 0, false, 0.0, false, 0.0);
		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvOpts);
		TrajectoryEnsemble trajs = null;
		try {
			trajs = reader.read();
		} catch (Exception e) {
			e.printStackTrace();
		}

		GraphConstructionDBScanParameters gparams = new GraphConstructionDBScanParameters("a", 1, 0.2, 10, 0, Double.MAX_VALUE,
				0.001, GraphConstructionParameters.NodeType.POLY);

		GraphConstructionDBScan galgo = new GraphConstructionDBScan(gparams);

		Graph g = galgo.constructGraph(trajs, null);

		assertEquals(8, g.conn().size());
		Integer[] keys = new Integer[g.conn().size()];
		g.conn().keySet().toArray(keys);
		assertArrayEquals(new Integer[] {1, 2, 3, 4, 5, 6, 7, 8}, keys);

		assertEquals(2, g.conn().get(1).size());
		assertEquals(4.0, g.conn().get(1).get(4), 1e-1);
		assertEquals(1.0, g.conn().get(1).get(7), 1e-1);

		assertEquals(2, g.conn().get(2).size());
		assertEquals(12.0, g.conn().get(2).get(4), 1e-1);
		assertEquals(1.0, g.conn().get(2).get(5), 1e-1);

		assertEquals(1, g.conn().get(3).size());
		assertEquals(6.0, g.conn().get(3).get(7), 1e-1);

		assertEquals(3, g.conn().get(4).size());
		assertEquals(4.0, g.conn().get(4).get(1), 1e-1);
		assertEquals(19.0, g.conn().get(4).get(2), 1e-1);
		assertEquals(10.0, g.conn().get(4).get(5), 1e-1);

		assertEquals(2, g.conn().get(5).size());
		assertEquals(1.0, g.conn().get(5).get(2), 1e-1);
		assertEquals(5.0, g.conn().get(5).get(4), 1e-1);

		assertEquals(2, g.conn().get(6).size());
		assertEquals(5.0, g.conn().get(6).get(7), 1e-1);
		assertEquals(7.0, g.conn().get(6).get(8), 1e-1);

		assertEquals(4, g.conn().get(7).size());
		assertEquals(1.0, g.conn().get(7).get(1), 1e-1);
		assertEquals(10.0, g.conn().get(7).get(3), 1e-1);
		assertEquals(3.0, g.conn().get(7).get(6), 1e-1);
		assertEquals(2.0, g.conn().get(7).get(8), 1e-1);

		assertEquals(2, g.conn().get(8).size());
		assertEquals(4.0, g.conn().get(8).get(6), 1e-1);
		assertEquals(3.0, g.conn().get(8).get(7), 1e-1);
	}
}
