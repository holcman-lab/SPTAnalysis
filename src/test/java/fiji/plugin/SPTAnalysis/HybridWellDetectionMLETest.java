package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fiji.plugin.SPTAnalysis.estimators.MLEEstimatorParameters;
import fiji.plugin.SPTAnalysis.estimators.WellEstimator;
import fiji.plugin.SPTAnalysis.readers.CSVReaderOptions;
import fiji.plugin.SPTAnalysis.readers.TrajectoryCSVReader;
import fiji.plugin.SPTAnalysis.struct.PotWell;
import fiji.plugin.SPTAnalysis.struct.PotWells;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;
import fiji.plugin.SPTAnalysis.wellDetection.HybridWellDetection;
import fiji.plugin.SPTAnalysis.wellDetection.IterationChooser;

public class HybridWellDetectionMLETest
{
	@Test
	public void testSimus1()
	{
		String fname = ClassLoader.getSystemResource("trajectories/well_simu_1.csv").getFile();

		CSVReaderOptions csvo = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 0, false, 0, false, 0);
		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvo);
		TrajectoryEnsemble trajs = null;
		try {
			trajs = reader.read();
		} catch (Exception e) {
			e.printStackTrace();
		}

		MLEEstimatorParameters estPs = new MLEEstimatorParameters(WellEstimator.type.MLE);
		HybridWellDetection.Parameters ps =
				new HybridWellDetection.Parameters("Ana", 0.05, 1, 5, 3, 0.2, 1, 10, 95, estPs, 0,
						new IterationChooser.MLE.Parameters(IterationChooser.chooser.BestMLEScore, estPs));
		HybridWellDetection hwd = new HybridWellDetection(ps);

		PotWells wells = hwd.detectWells(trajs, null);
		assertEquals(1, wells.wells.size());
		PotWell w = wells.wells.get(0);

		assertArrayEquals(new double[] {9.9681, 9.9450}, w.ell().mu(), 1e-4);
		assertArrayEquals(new double[] {0.3616, 0.2599}, w.ell().rad(), 1e-4);
		assertEquals(1.0748, w.ell().phi(), 1e-4);
		assertEquals(546.83738, w.score().value(), 1e-5);
		assertEquals(0.23333, w.A(), 1e-5);
		assertEquals(0.05147, w.D(), 1e-5);
	}

	@Test
	public void testSimus2()
	{
		String fname = ClassLoader.getSystemResource("trajectories/well_simu_2.csv").getFile();

		CSVReaderOptions csvo = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 0, false, 0, false, 0);
		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvo);
		TrajectoryEnsemble trajs = null;
		try {
			trajs = reader.read();
		} catch (Exception e) {
			e.printStackTrace();
		}

		MLEEstimatorParameters estPs = new MLEEstimatorParameters(WellEstimator.type.MLE);
		HybridWellDetection.Parameters ps =
				new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 10, 95, estPs, 0,
						new IterationChooser.MLE.Parameters(IterationChooser.chooser.BestMLEScore, estPs));
		HybridWellDetection hwd = new HybridWellDetection(ps);

		PotWells wells = hwd.detectWells(trajs, null);
		assertEquals(1, wells.wells.size());
		PotWell w = wells.wells.get(0);

		assertArrayEquals(new double[] {10.0012, 10.0002}, w.ell().mu(), 1e-3);
		assertArrayEquals(new double[] {0.2302, 0.139535}, w.ell().rad(), 1e-3);
		assertEquals(-0.09752, w.ell().phi(), 1e-5);
		assertEquals(2224.23547, w.score().value(), 1e-5);
		assertEquals(0.32790, w.A(), 1e-5);
		assertEquals(0.05081, w.D(), 1e-5);
	}

	@Test
	public void testSimus3()
	{
		String fname = ClassLoader.getSystemResource("trajectories/well_simu_3.csv").getFile();

		CSVReaderOptions csvo = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 0, false, 0, false, 0);
		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvo);
		TrajectoryEnsemble trajs = null;
		try {
			trajs = reader.read();
		} catch (Exception e) {
			e.printStackTrace();
		}

		MLEEstimatorParameters estPs = new MLEEstimatorParameters(WellEstimator.type.MLE);
		HybridWellDetection.Parameters ps =
				new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 10, 95, estPs, 0,
						new IterationChooser.MLE.Parameters(IterationChooser.chooser.BestMLEScore, estPs));
		HybridWellDetection hwd = new HybridWellDetection(ps);

		PotWells wells = hwd.detectWells(trajs, null);
		assertEquals(1, wells.wells.size());
		PotWell w = wells.wells.get(0);

		assertArrayEquals(new double[] {10.0068, 9.9985}, w.ell().mu(), 1e-3);
		assertArrayEquals(new double[] {0.190668, 0.1803}, w.ell().rad(), 1e-3);
		assertEquals(-0.00659, w.ell().phi(), 1e-5);
		assertEquals(2197.99379, w.score().value(), 1e-5);
		assertEquals(0.52934, w.A(), 1e-5);
		assertEquals(0.04643, w.D(), 1e-5);
	}

	@Test
	public void testSimus4()
	{
		String fname = ClassLoader.getSystemResource("trajectories/well_simu_4.csv").getFile();

		CSVReaderOptions csvo = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 0, false, 0, false, 0);
		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvo);
		TrajectoryEnsemble trajs = null;
		try {
			trajs = reader.read();
		} catch (Exception e) {
			e.printStackTrace();
		}

		MLEEstimatorParameters estPs = new MLEEstimatorParameters(WellEstimator.type.MLE);
		HybridWellDetection.Parameters ps =
				new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 10, 95, estPs, 0,
						new IterationChooser.MLE.Parameters(IterationChooser.chooser.BestMLEScore, estPs));
		HybridWellDetection hwd = new HybridWellDetection(ps);

		PotWells wells = hwd.detectWells(trajs, null);
		assertEquals(1, wells.wells.size());
		PotWell w = wells.wells.get(0);

		assertArrayEquals(new double[] {9.9931, 10.0177}, w.ell().mu(), 1e-3);
		assertArrayEquals(new double[] {0.2688, 0.1921}, w.ell().rad(), 1e-3);
		assertEquals(-0.68826, w.ell().phi(), 1e-5);
		assertEquals(2543.75222, w.score().value(), 1e-5);
		assertEquals(0.61671, w.A(), 1e-5);
		assertEquals(0.04811, w.D(), 1e-5);
	}
}
