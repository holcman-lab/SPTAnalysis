package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.*;

import org.junit.Test;

import fiji.plugin.SPTAnalysis.estimators.LeastSquareEstimatorCircle;
import fiji.plugin.SPTAnalysis.estimators.LeastSquareEstimatorEllipse;
import fiji.plugin.SPTAnalysis.readers.CSVReaderOptions;
import fiji.plugin.SPTAnalysis.readers.TrajectoryCSVReader;
import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;


public class DiffCoeffInEllipseTest
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

		Ellipse ell = new Ellipse(new double[] {0.6, 0.6}, new double[] {0.3, 0.3}, 0);

		LeastSquareEstimatorCircle lsqcirc = new LeastSquareEstimatorCircle(trajs, null, null, ell, false);
		assertEquals(0.0536, lsqcirc.estimateD(), 1e-4);

		LeastSquareEstimatorEllipse lsqell = new LeastSquareEstimatorEllipse(trajs, null, null, ell, false);
		assertEquals(0.0536, lsqell.estimateD(), 1e-4);
	}

	@Test
	public void test2()
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

		Ellipse ell = new Ellipse(new double[] {0.6, 0.6}, new double[] {0.3, 0.3}, 0);

		LeastSquareEstimatorCircle lsqcirc = new LeastSquareEstimatorCircle(trajs, null, null, ell, true);
		assertEquals(0.0505, lsqcirc.estimateD(), 1e-4);

		LeastSquareEstimatorEllipse lsqell = new LeastSquareEstimatorEllipse(trajs, null, null, ell, true);
		assertEquals(0.0505, lsqell.estimateD(), 1e-4);
	}
}