package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import fiji.plugin.SPTAnalysis.readers.CSVReaderOptions;
import fiji.plugin.SPTAnalysis.readers.TrajectoryCSVReader;
import fiji.plugin.SPTAnalysis.struct.Rectangle;
import fiji.plugin.SPTAnalysis.struct.Trajectory;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;


public class PointsInSquareTest
{
	@Test
	public void test()
	{
		String fname = ClassLoader.getSystemResource("trajectories/trajs_well.csv").getFile();

		CSVReaderOptions csvOpts = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 0, false, 0.0, false, 0.0);
		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvOpts);
		TrajectoryEnsemble trajs = reader.read();

		int npts = 0;
		for (Trajectory traj: trajs.trajs())
			npts += traj.points().size();

		ArrayList<double[]> pts = Utils.pointsInReg(trajs, new Rectangle(new double[] {10.0, 10.0}, 10.0));
		assertEquals(npts, pts.size());

		pts = Utils.pointsInReg(trajs, new Rectangle(new double[] {10.0, 10.0}, 0.6));
		assertEquals(npts - 18, pts.size());
	}

}
