package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import fiji.plugin.SPTAnalysis.readers.CSVReaderOptions;
import fiji.plugin.SPTAnalysis.readers.TrajectoryCSVReader;
import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.Point;
import fiji.plugin.SPTAnalysis.struct.Rectangle;
import fiji.plugin.SPTAnalysis.struct.Trajectory;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsembleWindows;

public class TrajectoriesInRegTest
{
	@Test
	public void test()
	{
		String fname = ClassLoader.getSystemResource("trajectories/trajs_well.csv").getFile();

		CSVReaderOptions csvOpts = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 0, false, 0.0, false, 0.0);
		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvOpts);
		TrajectoryEnsemble trajs = reader.read();

		Ellipse ell = new Ellipse(new double[] {10.0, 10.0}, new double[] {0.2, 0.2}, 0);

		TrajectoryEnsemble trajsInEll = Utils.trajsInShape(trajs, ell);

		Set<Double> dtsAll = new TreeSet<> ();
		for (Trajectory tr: trajs.trajs())
			for (double[] disp: tr.displacements())
				dtsAll.add(disp[0]);

		Set<Double> dts = new TreeSet<> ();
		for (Trajectory tr: trajsInEll.trajs())
		{
			for (Point p: tr.points())
				assertTrue(ell.inside(p.vec()));

			for (double[] disp: tr.displacements())
				dts.add(disp[0]);
		}
		assertEquals(dtsAll.size(), dts.size());
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

		Rectangle reg = new Rectangle(new double[] {0.0, 0.0}, trajsw.maxCoords());

		TrajectoryEnsemble trajs2 = Utils.trajsInShape(trajs, reg);

		assertEquals(trajs.trajs().size(), trajs2.trajs().size());
	}

	@Test
	public void test3()
	{
		String fname = ClassLoader.getSystemResource("trajectories/trajs_well.csv").getFile();

		CSVReaderOptions csvOpts = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 0, false, 0.0, false, 0.0);
		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvOpts);
		TrajectoryEnsemble trajs = reader.read();

		ArrayList<TrajectoryEnsemble> trajsw = new ArrayList<>();
		trajsw.add(trajs);

		Rectangle reg = new Rectangle(new double[] {0.0, 0.0}, new double[] {10.0, 10.0});
		TrajectoryEnsemble trajs2 = Utils.trajsInShape(trajs, reg);

		int cntKept = 0;
		for (Trajectory tr: trajs2.trajs())
		{
			cntKept += tr.points().size();
			for (Point p: tr.points())
				assertTrue(reg.inside(p.vec()));
		}

		int cntIn = 0;
		for (Trajectory tr: trajs.trajs())
			for (Point p: tr.points())
				if (reg.inside(p.vec()))
					++cntIn;

		assertEquals(cntIn, cntKept);
	}

	@Test
	public void test4()
	{
		String fname = ClassLoader.getSystemResource("trajectories/trajs_well.csv").getFile();

		CSVReaderOptions csvOpts = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 0, false, 0.0, false, 0.0);
		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvOpts);
		TrajectoryEnsemble trajs = reader.read();

		ArrayList<TrajectoryEnsemble> trajsw = new ArrayList<>();
		trajsw.add(trajs);

		Rectangle reg = new Rectangle(new double[] {10.0, 10.0}, new double[] {11.0, 11.0});
		TrajectoryEnsemble trajs2 = Utils.trajsInShape(trajs, reg);

		int cntKept = 0;
		for (Trajectory tr: trajs2.trajs())
		{
			cntKept += tr.points().size();
			for (Point p: tr.points())
				assertTrue(reg.inside(p.vec()));
		}

		int cntIn = 0;
		for (Trajectory tr: trajs.trajs())
			for (Point p: tr.points())
				if (reg.inside(p.vec()))
					++cntIn;

		assertEquals(cntIn, cntKept);
	}
}
