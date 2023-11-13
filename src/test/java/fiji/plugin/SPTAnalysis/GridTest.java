package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

import fiji.plugin.SPTAnalysis.readers.CSVReaderOptions;
import fiji.plugin.SPTAnalysis.readers.TrajectoryCSVReader;
import fiji.plugin.SPTAnalysis.struct.SquareGrid;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;



public class GridTest
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

		assertArrayEquals(new double[] {0.0, 0.0}, g.Xmin(), 1.e-5);
		assertArrayEquals(new double[] {1.0, 1.2}, g.Xmax(), 1.e-5);

		java.util.Iterator<int[]> it = g.iterator();

		int[] v = it.next();
		assertArrayEquals(new int[] {0, 0}, v);
		assertArrayEquals(new double[] {0.1, 0.1}, g.get(v[0], v[1]), 1e-5);

		v = it.next();
		assertArrayEquals(new int[] {0, 1}, v);
		assertArrayEquals(new double[] {0.1, 0.3}, g.get(v[0], v[1]), 1e-5);

		v = it.next();
		assertArrayEquals(new int[] {0, 2}, v);
		assertArrayEquals(new double[] {0.1, 0.5}, g.get(v[0], v[1]), 1e-5);

		v = it.next();
		assertArrayEquals(new int[] {0, 3}, v);
		assertArrayEquals(new double[] {0.1, 0.7}, g.get(v[0], v[1]), 1e-5);

		v = it.next();
		assertArrayEquals(new int[] {0, 4}, v);
		assertArrayEquals(new double[] {0.1, 0.9}, g.get(v[0], v[1]), 1e-5);

		v = it.next();
		assertArrayEquals(new int[] {0, 5}, v);
		assertArrayEquals(new double[] {0.1, 1.1}, g.get(v[0], v[1]), 1e-5);


		v = it.next();
		assertArrayEquals(new int[] {1, 0}, v);
		assertArrayEquals(new double[] {0.3, 0.1}, g.get(v[0], v[1]), 1e-5);

		v = it.next();
		assertArrayEquals(new int[] {1, 1}, v);
		assertArrayEquals(new double[] {0.3, 0.3}, g.get(v[0], v[1]), 1e-5);

		v = it.next();
		assertArrayEquals(new int[] {1, 2}, v);
		assertArrayEquals(new double[] {0.3, 0.5}, g.get(v[0], v[1]), 1e-5);

		v = it.next();
		assertArrayEquals(new int[] {1, 3}, v);
		assertArrayEquals(new double[] {0.3, 0.7}, g.get(v[0], v[1]), 1e-5);

		v = it.next();
		assertArrayEquals(new int[] {1, 4}, v);
		assertArrayEquals(new double[] {0.3, 0.9}, g.get(v[0], v[1]), 1e-5);

		v = it.next();
		assertArrayEquals(new int[] {1, 5}, v);
		assertArrayEquals(new double[] {0.3, 1.1}, g.get(v[0], v[1]), 1e-5);


		v = it.next();
		assertArrayEquals(new int[] {2, 0}, v);
		assertArrayEquals(new double[] {0.5, 0.1}, g.get(v[0], v[1]), 1e-5);

		v = it.next();
		assertArrayEquals(new int[] {2, 1}, v);
		assertArrayEquals(new double[] {0.5, 0.3}, g.get(v[0], v[1]), 1e-5);

		v = it.next();
		assertArrayEquals(new int[] {2, 2}, v);
		assertArrayEquals(new double[] {0.5, 0.5}, g.get(v[0], v[1]), 1e-5);

		v = it.next();
		assertArrayEquals(new int[] {2, 3}, v);
		assertArrayEquals(new double[] {0.5, 0.7}, g.get(v[0], v[1]), 1e-5);

		v = it.next();
		assertArrayEquals(new int[] {2, 4}, v);
		assertArrayEquals(new double[] {0.5, 0.9}, g.get(v[0], v[1]), 1e-5);

		v = it.next();
		assertArrayEquals(new int[] {2, 5}, v);
		assertArrayEquals(new double[] {0.5, 1.1}, g.get(v[0], v[1]), 1e-5);


		v = it.next();
		assertArrayEquals(new int[] {3, 0}, v);
		assertArrayEquals(new double[] {0.7, 0.1}, g.get(v[0], v[1]), 1e-5);

		v = it.next();
		assertArrayEquals(new int[] {3, 1}, v);
		assertArrayEquals(new double[] {0.7, 0.3}, g.get(v[0], v[1]), 1e-5);

		v = it.next();
		assertArrayEquals(new int[] {3, 2}, v);
		assertArrayEquals(new double[] {0.7, 0.5}, g.get(v[0], v[1]), 1e-5);

		v = it.next();
		assertArrayEquals(new int[] {3, 3}, v);
		assertArrayEquals(new double[] {0.7, 0.7}, g.get(v[0], v[1]), 1e-5);

		v = it.next();
		assertArrayEquals(new int[] {3, 4}, v);
		assertArrayEquals(new double[] {0.7, 0.9}, g.get(v[0], v[1]), 1e-5);

		v = it.next();
		assertArrayEquals(new int[] {3, 5}, v);
		assertArrayEquals(new double[] {0.7, 1.1}, g.get(v[0], v[1]), 1e-5);


		v = it.next();
		assertArrayEquals(new int[] {4, 0}, v);
		assertArrayEquals(new double[] {0.9, 0.1}, g.get(v[0], v[1]), 1e-5);

		v = it.next();
		assertArrayEquals(new int[] {4, 1}, v);
		assertArrayEquals(new double[] {0.9, 0.3}, g.get(v[0], v[1]), 1e-5);

		v = it.next();
		assertArrayEquals(new int[] {4, 2}, v);
		assertArrayEquals(new double[] {0.9, 0.5}, g.get(v[0], v[1]), 1e-5);

		v = it.next();
		assertArrayEquals(new int[] {4, 3}, v);
		assertArrayEquals(new double[] {0.9, 0.7}, g.get(v[0], v[1]), 1e-5);

		v = it.next();
		assertArrayEquals(new int[] {4, 4}, v);
		assertArrayEquals(new double[] {0.9, 0.9}, g.get(v[0], v[1]), 1e-5);

		v = it.next();
		assertArrayEquals(new int[] {4, 5}, v);
		assertArrayEquals(new double[] {0.9, 1.1}, g.get(v[0], v[1]), 1e-5);


		v = it.next();
		assertArrayEquals(new int[] {5, 0}, v);
		assertArrayEquals(new double[] {1.1, 0.1}, g.get(v[0], v[1]), 1e-5);

		v = it.next();
		assertArrayEquals(new int[] {5, 1}, v);
		assertArrayEquals(new double[] {1.1, 0.3}, g.get(v[0], v[1]), 1e-5);

		v = it.next();
		assertArrayEquals(new int[] {5, 2}, v);
		assertArrayEquals(new double[] {1.1, 0.5}, g.get(v[0], v[1]), 1e-5);

		v = it.next();
		assertArrayEquals(new int[] {5, 3}, v);
		assertArrayEquals(new double[] {1.1, 0.7}, g.get(v[0], v[1]), 1e-5);

		v = it.next();
		assertArrayEquals(new int[] {5, 4}, v);
		assertArrayEquals(new double[] {1.1, 0.9}, g.get(v[0], v[1]), 1e-5);

		v = it.next();
		assertArrayEquals(new int[] {5, 5}, v);
		assertArrayEquals(new double[] {1.1, 1.1}, g.get(v[0], v[1]), 1e-5);

		assertFalse(it.hasNext());
	}

	@Test
	public void test2()
	{
		double dx = 0.2;
		SquareGrid g = new SquareGrid(dx, new double[] {10.0, 10.0}, 3);

		int cpt = 0;
		Iterator<int[]> it = g.iterator();
		while (it.hasNext())
		{
			int[] v = it.next();
			assertArrayEquals(new double[] {9.4 + v[0]*dx, 9.4 + v[1]*dx}, g.get(v[0], v[1]), 1e-5);
			++cpt;
		}
		assertEquals(49, cpt);
	}

	@Test
	public void testGridSizes()
	{
		double dx = 0.2;

		for (int k=1; k < 15; ++k)
		{
			SquareGrid g = new SquareGrid(dx, new double[] {10.0, 10.0}, k);

			int cpt = 0;
			Iterator<int[]> it = g.iterator();
			while (it.hasNext())
			{
				it.next();
				++cpt;
			}

			assertEquals((int) Math.pow(2*k+1, 2), cpt);
		}
	}

}
