package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import fiji.plugin.SPTAnalysis.struct.MapParameters;
import fiji.plugin.SPTAnalysis.struct.ScalarMap;
import fiji.plugin.SPTAnalysis.struct.SquareGrid;



public class FilterSeedsTest
{
	@Test
	public void test1()
	{
		SquareGrid g = new SquareGrid(0.2, new double[] {0.0, 0.0}, new double[] {1.0, 1.0});
		HashMap<Integer, HashMap<Integer, Double>> tmp = new HashMap<> ();
		tmp.put(1, new HashMap<> ());
		tmp.put(2, new HashMap<> ());
		tmp.put(3, new HashMap<> ());

		tmp.get(1).put(1, 10.0);
		tmp.get(1).put(2, 14.0);
		tmp.get(1).put(3, 6.0);
		tmp.get(2).put(1, 13.0);
		tmp.get(2).put(2, 12.0);
		tmp.get(2).put(3, 7.0);
		tmp.get(3).put(1, 11.0);
		tmp.get(3).put(2, 9.0);
		tmp.get(3).put(3, 5.0);

		ScalarMap dens = new ScalarMap(g, tmp,
				new MapParameters.DensityParameters(g.dx(), ScalarMap.DensityOption.NPTS, 0));

		ArrayList<int[]> cells = new ArrayList<> ();
		cells.add(new int[] {1,1});
		cells.add(new int[] {1,2});
		cells.add(new int[] {1,3});
		cells.add(new int[] {2,1});
		cells.add(new int[] {2,2});
		cells.add(new int[] {2,3});
		cells.add(new int[] {3,1});
		cells.add(new int[] {3,2});
		cells.add(new int[] {3,3});

		ArrayList<int[]> res = Utils.remove_closeby_cells(dens, cells, 3);

		assertEquals(1, res.size());
		assertArrayEquals(new int[] {1, 2}, res.get(0));
	}

	@Test
	public void test2()
	{
		SquareGrid g = new SquareGrid(0.2, new double[] {0.0, 0.0}, new double[] {1.0, 1.0});
		HashMap<Integer, HashMap<Integer, Double>> tmp = new HashMap<> ();
		tmp.put(1, new HashMap<> ());
		tmp.put(2, new HashMap<> ());
		tmp.put(3, new HashMap<> ());

		tmp.get(1).put(1, 10.0);
		tmp.get(1).put(2, 14.0);
		tmp.get(1).put(3, 6.0);
		tmp.get(2).put(1, 16.0);
		tmp.get(2).put(2, 12.0);
		tmp.get(2).put(3, 7.0);
		tmp.get(3).put(1, 11.0);
		tmp.get(3).put(2, 9.0);
		tmp.get(3).put(3, 5.0);

		ScalarMap dens = new ScalarMap(g, tmp,
				new MapParameters.DensityParameters(g.dx(), ScalarMap.DensityOption.NPTS, 0));

		ArrayList<int[]> cells = new ArrayList<> ();
		cells.add(new int[] {1,1});
		cells.add(new int[] {1,2});
		cells.add(new int[] {1,3});
		cells.add(new int[] {2,1});
		cells.add(new int[] {2,2});
		cells.add(new int[] {2,3});
		cells.add(new int[] {3,1});
		cells.add(new int[] {3,2});
		cells.add(new int[] {3,3});

		ArrayList<int[]> res = Utils.remove_closeby_cells(dens, cells, 3);

		assertEquals(1, res.size());
		assertArrayEquals(new int[] {2, 1}, res.get(0));
	}

	@Test
	public void test3()
	{
		SquareGrid g = new SquareGrid(0.2, new double[] {0.0, 0.0}, new double[] {1.0, 1.0});
		HashMap<Integer, HashMap<Integer, Double>> tmp = new HashMap<> ();
		tmp.put(1, new HashMap<> ());
		tmp.put(2, new HashMap<> ());
		tmp.put(3, new HashMap<> ());

		tmp.get(1).put(1, 10.0);
		tmp.get(1).put(2, 14.0);
		tmp.get(1).put(3, 6.0);
		tmp.get(2).put(1, 16.0);
		tmp.get(2).put(2, 12.0);
		tmp.get(2).put(3, 7.0);
		tmp.get(3).put(1, 11.0);
		tmp.get(3).put(2, 9.0);
		tmp.get(3).put(3, 25.0);

		ScalarMap dens = new ScalarMap(g, tmp,
				new MapParameters.DensityParameters(g.dx(), ScalarMap.DensityOption.NPTS, 0));

		ArrayList<int[]> cells = new ArrayList<> ();
		cells.add(new int[] {1,1});
		cells.add(new int[] {1,2});
		cells.add(new int[] {1,3});
		cells.add(new int[] {2,1});
		cells.add(new int[] {2,2});
		cells.add(new int[] {2,3});
		cells.add(new int[] {3,1});
		cells.add(new int[] {3,2});
		cells.add(new int[] {3,3});

		ArrayList<int[]> res = Utils.remove_closeby_cells(dens, cells, 3);

		assertEquals(1, res.size());
		assertArrayEquals(new int[] {3, 3}, res.get(0));
	}

	@Test
	public void test4()
	{
		String fname = ClassLoader.getSystemResource("maps/dens_map.csv").getFile();

		SquareGrid g = new SquareGrid(0.2, new double[] {0.0, 0.0}, new double[] {17.2, 17.2});

		ScalarMap dens = null;
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(fname));
			String tmp = "";
			String line = br.readLine();
			while (line != null)
			{
				tmp += line + "\n";
				line = br.readLine();
			}
			br.close();

			dens = ScalarMap.load_from_str(tmp, g,
					new MapParameters.DensityParameters(g.dx(), ScalarMap.DensityOption.DENS, 0));
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}

		try {
			BufferedWriter bfw = new BufferedWriter(new FileWriter("/tmp/dens_map.csv"));
			bfw.write(dens.dump());
			bfw.newLine();
			bfw.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}

		ArrayList<int[]> seeds = Utils.highest_density_cells(dens, 5);
		ArrayList<int[]> res = Utils.remove_closeby_cells(dens, seeds, 3);

		assertEquals(64, res.size());
		assertArrayEquals(new int[] {13, 32}, res.get(0));
		assertArrayEquals(new int[] {9, 18}, res.get(res.size()-1));
	}

}
