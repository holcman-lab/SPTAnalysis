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
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;

public class EstimateDMLETest
{
	@Test
	public void test()
	{
		String fname = ClassLoader.getSystemResource("trajectories/trajs_well.csv").getFile();

		CSVReaderOptions csvOpts = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 0, false, 0.0, false, 0.0);
		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvOpts);
		TrajectoryEnsemble trajs = reader.read();

		Ellipse ell = new Ellipse(new double[] {10.0, 10.0}, new double[] {0.2, 0.2}, 0);

		MaxLikelihoodEstimator mle = new MaxLikelihoodEstimator(trajs, trajs.acqDT(), ell);

		assertEquals(0.048986, mle.estimateD(), 1e-5);
	}

	@Test
	public void test2()
	{
		String fname = ClassLoader.getSystemResource("trajectories/trajs_well.csv").getFile();

		CSVReaderOptions csvOpts = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 0, false, 0.0, false, 0.0);
		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvOpts);
		TrajectoryEnsemble trajs = reader.read();

		Ellipse ell = new Ellipse(new double[] {10.0, 10.0}, new double[] {0.2, 0.2}, 0);

		MaxLikelihoodEstimator mle = new MaxLikelihoodEstimator(Utils.trajsInShape(trajs, ell), trajs.acqDT(), ell);

		assertEquals(0.049877, mle.estimateD(), 1e-5);
	}

	@Test
	public void test3()
	{
		String fname = ClassLoader.getSystemResource("trajectories/trajs_well.csv").getFile();

		CSVReaderOptions csvOpts = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 0, false, 0.0, false, 0.0);
		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvOpts);
		TrajectoryEnsemble trajs = reader.read();

		Ellipse ell = new Ellipse(new double[] {10.0, 10.0}, new double[] {0.2, 0.2}, 0);

		WellEstimatorFactory wef = new WellEstimatorFactory(trajs, ell,
				new MLEEstimatorParameters(WellEstimator.type.MLE));

		assertEquals(0.049877, wef.getEst().estimateD(), 1e-5);
	}
}
