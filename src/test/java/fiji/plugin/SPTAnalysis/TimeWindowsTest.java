package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.*;

import org.junit.Test;

import fiji.plugin.SPTAnalysis.readers.CSVReaderOptions;
import fiji.plugin.SPTAnalysis.readers.TrajectoryCSVReader;
import fiji.plugin.SPTAnalysis.struct.TimeWindows;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;


public class TimeWindowsTest
{
	@Test
	public void test1()
	{
		TimeWindows tw = new TimeWindows(20, 0, 0, 100);

		assertEquals(4, tw.idxMax());
		for (int i = 0; i <= tw.idxMax(); ++i)
		{
			double[] curTw = tw.get(i);
			assertArrayEquals(new double[] {20.0 * i, 20.0 * (i+1)}, curTw, 1e-5);
		}

		assertEquals(4, tw.idxMax());
		assertEquals(0, tw.getIdx(0.0));
		assertEquals(0, tw.getIdx(19.0));
		assertEquals(0, tw.getIdx(19.9999));
		assertEquals(0, tw.getIdx(20.0));
		assertEquals(2, tw.getIdx(50.0));
		assertEquals(4, tw.getIdx(100.0));
	}

	@Test
	public void test2()
	{
		TimeWindows tw = new TimeWindows(20, 0.5, 0, 100);

		assertEquals(9, tw.idxMax());
		for (int i = 0; i <= tw.idxMax(); ++i)
		{
			double[] curTw = tw.get(i);
			assertArrayEquals(new double[] {10 * i, 10 * i + 20}, curTw, 1e-5);
		}
	}

	@Test
	public void test3()
	{
		TimeWindows tw = new TimeWindows(20, 0.75, 0, 100);

		assertEquals(19, tw.idxMax());
		for (int i = 0; i <= tw.idxMax(); ++i)
		{
			double[] curTw = tw.get(i);
			assertArrayEquals(new double[] {5 * i, 5 * i + 20}, curTw, 1e-5);
		}
	}

	@Test
	public void test4()
	{
		TimeWindows tw = new TimeWindows(20, 0, 7, 92);

		assertEquals(4, tw.idxMax());
		for (int i = 0; i <= tw.idxMax(); ++i)
		{
			double[] curTw = tw.get(i);
			assertArrayEquals(new double[] {7 + 20 * i, 7 + 20 * i + 20}, curTw, 1e-5);
		}

		assertEquals(0, tw.getIdx(7.0));
		assertEquals(0, tw.getIdx(27.0));
		assertEquals(2, tw.getIdx(50.0));
		assertEquals(4, tw.getIdx(92.0));
	}

	@Test
	public void test5()
	{
		TimeWindows tw = new TimeWindows(20, 0, 7, 82);

		assertEquals(3, tw.idxMax());
		for (int i = 0; i <= tw.idxMax(); ++i)
		{
			double[] curTw = tw.get(i);
			assertArrayEquals(new double[] {7 + 20 * i, 7 + 20 * i + 20}, curTw, 1e-5);
		}
	}

	@Test
	public void test6()
	{
		TimeWindows tw = new TimeWindows(20, 0.2, 7, 82);

		assertEquals(4, tw.idxMax());
		for (int i = 0; i <= tw.idxMax(); ++i)
		{
			double[] curTw = tw.get(i);
			assertArrayEquals(new double[] {7 + (20*0.8) * i, 7 + (20*0.8) * i + 20}, curTw, 1e-5);
		}
	}

	@Test
	public void test7()
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

		TimeWindows tw = new TimeWindows(trajs, 0.2, 0.1);

		assertEquals(5, tw.idxMax());
		for (int i = 0; i <= tw.idxMax(); ++i)
		{
			double[] curTw = tw.get(i);
			assertArrayEquals(new double[] {(0.2*0.9) * i, (0.2*0.9) * i + 0.2}, curTw, 1e-5);
		}
	}

	@Test
	public void test8()
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

		TimeWindows tw = new TimeWindows(trajs, 0.1, 0.5);

		assertEquals(7, tw.idxMax());
		for (int i = 0; i <= tw.idxMax(); ++i)
		{
			double[] curTw = tw.get(i);
			assertArrayEquals(new double[] {(0.1*0.5) * i, (0.1*0.5) * i + 0.1}, curTw, 1e-5);
		}
	}

	@Test
	public void test9()
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

		double[] ti = trajs.time_interval();

		TimeWindows tw = TimeWindows.singleWindow(trajs);

		assertEquals(0, tw.idxMax());
		assertEquals(0, tw.getIdx(ti[1]));
	}
}
