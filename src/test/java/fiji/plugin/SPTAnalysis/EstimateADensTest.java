package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.*;

import org.junit.Test;

import fiji.plugin.SPTAnalysis.estimators.DensityEstimator;
import fiji.plugin.SPTAnalysis.estimators.DensityEstimatorParameters;
import fiji.plugin.SPTAnalysis.estimators.WellEstimator;
import fiji.plugin.SPTAnalysis.estimators.WellEstimatorFactory;
import fiji.plugin.SPTAnalysis.readers.CSVReaderOptions;
import fiji.plugin.SPTAnalysis.readers.TrajectoryCSVReader;
import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;


public class EstimateADensTest
{
	@Test
	public void test()
	{
		double[] c = {10.0, 10.0};
		double A = 0.4;
		double D = 0.05;

		Ellipse e = new Ellipse(c, new double[] {0.2, 0.2}, 0.0);
	
		double[] Cdiag = new double[] {D/2/A * e.rad()[0] * e.rad()[0],
									   D/2/A * e.rad()[1] * e.rad()[1]};
		assertEquals(A, DensityEstimator.Aformula(Cdiag, e.rad(), D), 1e-5);
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

		WellEstimatorFactory wef = new WellEstimatorFactory(trajs, ell,
				new DensityEstimatorParameters(WellEstimator.type.DENSMLE, false));
		assertEquals(0.19595, wef.getEst().estimateA(), 1e-5);
	}

	@Test
	public void test_simus1()
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
				new DensityEstimatorParameters(WellEstimator.type.DENSMLE, false));
		assertEquals(0.20460, wef.getEst().estimateA(), 1e-5);
	}

	@Test
	public void test_simus2()
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
				new DensityEstimatorParameters(WellEstimator.type.DENSMLE, false));
		assertEquals(0.20920, wef.getEst().estimateA(), 1e-5);
	}

	@Test
	public void test_simus3()
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
				new DensityEstimatorParameters(WellEstimator.type.DENSMLE, false));

		assertEquals(0.28188, wef.getEst().estimateA(), 1e-5);
	}

	@Test
	public void test_simus4()
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
				new DensityEstimatorParameters(WellEstimator.type.DENSMLE, false));

		assertEquals(0.28124, wef.getEst().estimateA(), 1e-5);
	}
}
