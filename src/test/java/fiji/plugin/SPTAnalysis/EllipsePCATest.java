package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;


import Jama.Matrix;
import fiji.plugin.SPTAnalysis.readers.CSVReaderOptions;
import fiji.plugin.SPTAnalysis.readers.TrajectoryCSVReader;
import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.EllipseFit;
import fiji.plugin.SPTAnalysis.struct.Rectangle;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;

public class EllipsePCATest
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

		ArrayList<double[]> pts = Utils.pointsInReg(trajs, new Rectangle(new double[] {10.0, 10.0}, 10.0));
		double[][] tmp = pts.toArray(new double[pts.size()][2]);

		EllipseFit fit = Ellipse.ellipse_from_pca(new Matrix(tmp), 95);
		Ellipse e = fit.ell();

		assertEquals(e.mu()[0], 9.9979, 1e-3);
		assertEquals(e.mu()[1], 10.0039, 1e-3);
		assertEquals(e.rad()[0], 0.2302, 1e-3);
		assertEquals(e.rad()[1], 0.2037, 1e-3);
		assertEquals(e.phi(), 0.4481, 1e-3);
	}
}