package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fiji.plugin.SPTAnalysis.estimators.MLEEstimatorParameters;
import fiji.plugin.SPTAnalysis.estimators.MaxLikelihoodEstimator;
import fiji.plugin.SPTAnalysis.estimators.WellEstimator;
import fiji.plugin.SPTAnalysis.estimators.WellEstimatorFactory;
import fiji.plugin.SPTAnalysis.readers.CSVReaderOptions;
import fiji.plugin.SPTAnalysis.readers.TrajectoryCSVReader;
import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.Point;
import fiji.plugin.SPTAnalysis.struct.Trajectory;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;

public class EstimateAMLETest
{
	@Test
	public void test_simu()
	{
		double dt = 0.01;
		double A = 0.2;
		TrajectoryEnsemble trajs = new TrajectoryEnsemble();
		Ellipse ell = new Ellipse(new double[] {10.0, 10.0}, new double[] {0.2, 0.2}, 0);

		for (double dx = -0.2; dx < 0.2; dx += 0.001)
		{
			Trajectory tr = new Trajectory();
			tr.points().add(new Point(0, 10 + dx, 10 + dx ));
			tr.points().add(new Point(dt, 10 + dx - 2 * A * dx / Math.pow(ell.rad()[0], 2) * dt,
										  10 + dx - 2 * A * dx / Math.pow(ell.rad()[1], 2) * dt));

			trajs.trajs().add(tr);
		}

		MaxLikelihoodEstimator mle = new MaxLikelihoodEstimator(trajs, trajs.acqDT(), ell);
		assertEquals(0.18862, mle.estimateA(), 1e-5);
	}

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

		Ellipse ell = new Ellipse(new double[] {10.0, 10.0}, new double[] {0.2, 0.2}, 0);

		MaxLikelihoodEstimator mle = new MaxLikelihoodEstimator(trajs, trajs.acqDT(), ell);

		assertEquals(0.12887, mle.estimateA(), 1e-5);
	}

	@Test
	public void test2()
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

		Ellipse ell = new Ellipse(new double[] {10.0, 10.0}, new double[] {0.2, 0.2}, 0);

		MaxLikelihoodEstimator mle = new MaxLikelihoodEstimator(Utils.trajsInShape(trajs, ell), trajs.acqDT(), ell);

		assertEquals(0.23438, mle.estimateA(), 1e-5);
	}

	@Test
	public void test3()
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

		Ellipse ell = new Ellipse(new double[] {10.0, 10.0}, new double[] {0.2, 0.2}, 0);

		WellEstimatorFactory wef = new WellEstimatorFactory(trajs, ell,
				new MLEEstimatorParameters(WellEstimator.type.MLE));

		assertEquals(0.23438, wef.getEst().estimateA(), 1e-5);
	}

	@Test
	public void test_simus_1()
	{
		String fname = ClassLoader.getSystemResource("trajectories/well_simu_1.csv").getFile();

		CSVReaderOptions csvOpts = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 0, false, 0.0, false, 0.0);
		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvOpts);
		TrajectoryEnsemble trajs = null;
		try {
			trajs = reader.read();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Ellipse ell = new Ellipse(new double[] {10.0, 10.0}, new double[] {0.1, 0.1}, 0);

		WellEstimatorFactory wef = new WellEstimatorFactory(trajs, ell,
				new MLEEstimatorParameters(WellEstimator.type.MLE));

		assertEquals(0.21295, wef.getEst().estimateA(), 1e-5);
	}

	@Test
	public void test_simus_2()
	{
		String fname = ClassLoader.getSystemResource("trajectories/well_simu_2.csv").getFile();

		CSVReaderOptions csvOpts = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 0, false, 0.0, false, 0.0);
		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvOpts);
		TrajectoryEnsemble trajs = null;
		try {
			trajs = reader.read();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Ellipse ell = new Ellipse(new double[] {10.0, 10.0}, new double[] {0.15, 0.1}, 0);

		WellEstimatorFactory wef = new WellEstimatorFactory(trajs, ell,
				new MLEEstimatorParameters(WellEstimator.type.MLE));

		assertEquals(0.23608, wef.getEst().estimateA(), 1e-5);
	}

	@Test
	public void test_simus_3()
	{
		String fname = ClassLoader.getSystemResource("trajectories/well_simu_3.csv").getFile();

		CSVReaderOptions csvOpts = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 0, false, 0.0, false, 0.0);
		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvOpts);
		TrajectoryEnsemble trajs = null;
		try {
			trajs = reader.read();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Ellipse ell = new Ellipse(new double[] {10.0, 10.0}, new double[] {0.15, 0.1}, 0);

		WellEstimatorFactory wef = new WellEstimatorFactory(trajs, ell,
				new MLEEstimatorParameters(WellEstimator.type.MLE));

		assertEquals(0.31152, wef.getEst().estimateA(), 1e-5);
	}

	@Test
	public void test_simus_4()
	{
		String fname = ClassLoader.getSystemResource("trajectories/well_simu_4.csv").getFile();

		CSVReaderOptions csvOpts = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 0, false, 0.0, false, 0.0);
		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvOpts);
		TrajectoryEnsemble trajs = null;
		try {
			trajs = reader.read();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Ellipse ell = new Ellipse(new double[] {10.0, 10.0}, new double[] {0.15, 0.15}, 0);

		WellEstimatorFactory wef = new WellEstimatorFactory(trajs, ell,
				new MLEEstimatorParameters(WellEstimator.type.MLE));

		assertEquals(0.316963, wef.getEst().estimateA(), 1e-5);
	}
}
