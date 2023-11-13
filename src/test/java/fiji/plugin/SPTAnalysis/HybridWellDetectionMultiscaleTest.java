package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import fiji.plugin.SPTAnalysis.estimators.DensityEstimatorParameters;
import fiji.plugin.SPTAnalysis.estimators.GridEstimatorParameters;
import fiji.plugin.SPTAnalysis.estimators.WellEstimator;
import fiji.plugin.SPTAnalysis.readers.CSVReaderOptions;
import fiji.plugin.SPTAnalysis.readers.TrajectoryCSVReader;
import fiji.plugin.SPTAnalysis.struct.PotWell;
import fiji.plugin.SPTAnalysis.struct.SquareGrid;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;
import fiji.plugin.SPTAnalysis.wellDetection.FitResultMultiscale;
import fiji.plugin.SPTAnalysis.wellDetection.HybridWellDetectionMultiscale;
import fiji.plugin.SPTAnalysis.wellDetection.IterationChooser;


public class HybridWellDetectionMultiscaleTest
{
	@Test
	public void test1()
	{
		String fname = ClassLoader.getSystemResource("trajectories/sample_data_well.csv").getFile();

		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, CSVReaderOptions.trackmateOptions());
		TrajectoryEnsemble trajs = null;
		try {
			trajs = reader.read();
		} catch (Exception e) {
			e.printStackTrace();
		}

		GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, Double.NaN,
				5, 4, false, false);
		IterationChooser.BestParabScore.Parameters itChoosePs = 
				new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

		HybridWellDetectionMultiscale.Parameters ps =
				new HybridWellDetectionMultiscale.Parameters("Ana", 0.2, 1, 0.1, 0.2, 0.05, 5, 3, 1, 10, 95, estPs, 0, itChoosePs);

		HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

		SquareGrid g = new SquareGrid(trajs, 0.2);
		FitResultMultiscale res = hwd.fitWellFromSeed(trajs, g.get(37, 39));
		res.setBestIt(new IterationChooser.BestParabScore(itChoosePs, trajs));

		assertEquals(1, res.bestIt());
		PotWell w = res.bestWell(trajs, ps);

		assertArrayEquals(new double[] {7.38868, 8.00490}, w.ell().mu(), 1e-5);
		assertArrayEquals(new double[] {0.21465, 0.15182}, w.ell().rad(), 1e-5);
		assertEquals(-0.32541, w.ell().phi(), 1e-5);
		assertEquals(0.21257, w.score().value(), 1e-5);
		assertEquals(0.25809, w.D(), 1e-5);
		assertEquals(0.55244, w.A(), 1e-5);
	}

	@Test
	public void test1bis()
	{
		String fname = ClassLoader.getSystemResource("trajectories/sample_data_well.csv").getFile();

		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, CSVReaderOptions.trackmateOptions());
		TrajectoryEnsemble trajs = null;
		try {
			trajs = reader.read();
		} catch (Exception e) {
			e.printStackTrace();
		}

		GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, Double.NaN,
				5, 4, false, false);
		IterationChooser.BestParabScore.Parameters itChoosePs = 
				new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

		HybridWellDetectionMultiscale.Parameters ps =
				new HybridWellDetectionMultiscale.Parameters("Ana", 0.2, 1, 0.1, 0.2, 0.05, 5, 3, 1, 10, 90, estPs, 0, itChoosePs);

		HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

		SquareGrid g = new SquareGrid(trajs, 0.2);
		FitResultMultiscale res = hwd.fitWellFromSeed(trajs, g.get(37, 39));
		res.setBestIt(new IterationChooser.BestParabScore(itChoosePs, trajs));

		assertEquals(1, res.bestIt());

		PotWell w = res.bestWell(trajs, ps);
		assertArrayEquals(new double[] {7.38867, 8.00489}, w.ell().mu(), 1e-5);
		assertArrayEquals(new double[] {0.18819, 0.13310}, w.ell().rad(), 1e-5);
		assertEquals(-0.32541, w.ell().phi(), 1e-5);
		assertEquals(0.21257, w.score().value(), 1e-5);
		assertEquals(0.23802, w.D(), 1e-5);
		assertEquals(0.42463, w.A(), 1e-5);
	}

	@Test
	public void test1ter()
	{
		String fname = ClassLoader.getSystemResource("trajectories/sample_data_well.csv").getFile();

		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, CSVReaderOptions.trackmateOptions());
		TrajectoryEnsemble trajs = null;
		try {
			trajs = reader.read();
		} catch (Exception e) {
			e.printStackTrace();
		}

		GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, Double.NaN,
				5, 4, false, false);
		IterationChooser.BestParabScore.Parameters itChoosePs = 
				new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

		HybridWellDetectionMultiscale.Parameters ps =
				new HybridWellDetectionMultiscale.Parameters("Ana", 0.2, 1, 0.08, 0.15, 0.01, 5, 3, 1, 10, 99, estPs, 0, itChoosePs);

		HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

		SquareGrid g = new SquareGrid(trajs, 0.2);
		FitResultMultiscale res = hwd.fitWellFromSeed(trajs, g.get(37, 39));
		res.setBestIt(new IterationChooser.BestParabScore(itChoosePs, trajs));

		assertEquals(0, res.bestIt());

		PotWell w = res.bestWell(trajs, ps);
		assertArrayEquals(new double[] {7.41137, 7.99114}, w.ell().mu(), 1e-5);
		assertArrayEquals(new double[] {0.23019, 0.16072}, w.ell().rad(), 1e-5);
		assertEquals(-0.52887, w.ell().phi(), 1e-5);
		assertEquals(0.24495, w.score().value(), 1e-5);
		assertEquals(0.25823, w.D(), 1e-4);
		assertEquals(0.54404, w.A(), 1e-4);
	}

	@Test
	public void test2()
	{
		String fname = ClassLoader.getSystemResource("trajectories/sample_data_well.csv").getFile();

		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, CSVReaderOptions.trackmateOptions());
		TrajectoryEnsemble trajs = null;
		try {
			trajs = reader.read();
		} catch (Exception e) {
			e.printStackTrace();
		}

		GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, Double.NaN,
				5, 4, true, false);
		IterationChooser.BestParabScore.Parameters itChoosePs = 
				new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

		HybridWellDetectionMultiscale.Parameters ps =
				new HybridWellDetectionMultiscale.Parameters("Ana", 0.2, 1, 0.1, 0.2, 0.05, 5, 3, 1, 10, 95, estPs, 0, itChoosePs);

		HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

		SquareGrid g = new SquareGrid(trajs, 0.2);
		FitResultMultiscale res = hwd.fitWellFromSeed(trajs, g.get(37, 39));
		res.setBestIt(new IterationChooser.BestParabScore(itChoosePs, trajs));

		assertEquals(1, res.bestIt());

		PotWell w = res.bestWell(trajs, ps);

		assertArrayEquals(new double[] {7.38868, 8.00490}, w.ell().mu(), 1e-5);
		assertArrayEquals(new double[] {0.214657, 0.151822}, w.ell().rad(), 1e-5);
		assertEquals(-0.32541, w.ell().phi(), 1e-5);
		assertEquals(0.21257, w.score().value(), 1e-5);
		assertEquals(0.17099, w.D(), 1e-5);
		assertEquals(0.55244, w.A(), 1e-5);
	}

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

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, Double.NaN,
					5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs = 
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);
	
			HybridWellDetectionMultiscale.Parameters ps =
					new HybridWellDetectionMultiscale.Parameters("Ana", 0.1, 1, 0.02, 0.2, 0.05, 5, 3, 1, 10, 95, estPs, 0, itChoosePs);
	
			HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.9697, 9.9755}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.1286, 0.1093}, w.ell().rad(), 1e-4);
			assertEquals(-0.2747, w.ell().phi(), 1e-4);
			assertEquals(0.45305, w.score().value(), 1e-5);
			assertEquals(0.095987, w.A(), 1e-5);
			assertEquals(0.049112, w.D(), 1e-5);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, Double.NaN,
					5, 4, true, false);
			IterationChooser.BestParabScore.Parameters itChoosePs = 
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetectionMultiscale.Parameters ps =
					new HybridWellDetectionMultiscale.Parameters("Ana", 0.1, 1, 0.02, 0.2, 0.05, 5, 3, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.9697, 9.9755}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.1286, 0.1093}, w.ell().rad(), 1e-4);
			assertEquals(-0.27475, w.ell().phi(), 1e-4);
			assertEquals(0.45305, w.score().value(), 1e-5);
			assertEquals(0.095987, w.A(), 1e-4);
			assertEquals(0.048374, w.D(), 1e-4);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQCIRC, Double.NaN,
					5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs = 
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetectionMultiscale.Parameters ps =
					new HybridWellDetectionMultiscale.Parameters("Ana", 0.1, 1, 0.02, 0.2, 0.05, 5, 3, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.9697, 9.9755}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.1286, 0.1093}, w.ell().rad(), 1e-4);
			assertEquals(-0.27475, w.ell().phi(), 1e-4);
			assertEquals(0.50342, w.score().value(), 1e-5);
			assertEquals(0.11047, w.A(), 1e-5);
			assertEquals(0.04911, w.D(), 1e-5);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQCIRC, Double.NaN,
					5, 4, true, false);
			IterationChooser.BestParabScore.Parameters itChoosePs = 
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetectionMultiscale.Parameters ps =
					new HybridWellDetectionMultiscale.Parameters("Ana", 0.1, 1, 0.02, 0.2, 0.05, 5, 3, 1, 10, 95, estPs, 0, itChoosePs);
			HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.9697, 9.9755}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.1286, 0.10932}, w.ell().rad(), 1e-4);
			assertEquals(-0.2747, w.ell().phi(), 1e-4);
			assertEquals(0.50342, w.score().value(), 1e-5);
			assertEquals(0.11047, w.A(), 1e-5);
			assertEquals(0.04837, w.D(), 1e-5);
		}

		{
			DensityEstimatorParameters estPs = new DensityEstimatorParameters(WellEstimator.type.DENS, true);
			GridEstimatorParameters estGridPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, Double.NaN,
					5, 4, true, false);
			IterationChooser.BestParabScore.Parameters itChoosePs = 
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estGridPs, 0.7, 0.5);
	
			HybridWellDetectionMultiscale.Parameters ps =
					new HybridWellDetectionMultiscale.Parameters("Ana", 0.1, 1, 0.02, 0.2, 0.05, 5, 3, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.9697, 9.9755}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.1286, 0.1093}, w.ell().rad(), 1e-4);
			assertEquals(-0.2747, w.ell().phi(), 1e-4);
			assertEquals(0.45305, w.score().value(), 1e-5);
			assertEquals(0.16421, w.A(), 1e-5);
			assertEquals(0.04837, w.D(), 1e-5);
		}

		{
			DensityEstimatorParameters estPs = new DensityEstimatorParameters(WellEstimator.type.MLE, true);
			GridEstimatorParameters estGridPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, Double.NaN,
					5, 4, true, false);
			IterationChooser.BestParabScore.Parameters itChoosePs = 
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estGridPs, 0.7, 0.5);

			HybridWellDetectionMultiscale.Parameters ps =
					new HybridWellDetectionMultiscale.Parameters("Ana", 0.1, 1, 0.02, 0.2, 0.05, 5, 3, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.9697, 9.9755}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.1286, 0.1093}, w.ell().rad(), 1e-4);
			assertEquals(-0.2747, w.ell().phi(), 1e-4);
			assertEquals(0.45305, w.score().value(), 1e-5);
			assertEquals(0.17034, w.A(), 1e-5);
			assertEquals(0.05420, w.D(), 1e-5);
		}
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

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, Double.NaN, 5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs = 
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);
	
			HybridWellDetectionMultiscale.Parameters ps =
					new HybridWellDetectionMultiscale.Parameters("Ana", 0.1, 1, 0.02, 0.2, 0.05, 5, 3, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {10.0039, 9.9991}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.15029, 0.10797}, w.ell().rad(), 1e-4);
			assertEquals(-0.06686, w.ell().phi(), 1e-4);
			assertEquals(0.04510, w.score().value(), 1e-5);
			assertEquals(0.12267, w.A(), 1e-5);
			assertEquals(0.04147, w.D(), 1e-5);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, Double.NaN, 5, 4, true, false);
			IterationChooser.BestParabScore.Parameters itChoosePs = 
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetectionMultiscale.Parameters ps =
					new HybridWellDetectionMultiscale.Parameters("Ana", 0.1, 1, 0.02, 0.2, 0.05, 5, 3, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {10.0039, 9.9991}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.15029, 0.10797}, w.ell().rad(), 1e-4);
			assertEquals(-0.06686, w.ell().phi(), 1e-4);
			assertEquals(0.04510, w.score().value(), 1e-5);
			assertEquals(0.12267, w.A(), 1e-5);
			assertEquals(0.03934, w.D(), 1e-5);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQCIRC, Double.NaN, 5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs = 
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetectionMultiscale.Parameters ps =
					new HybridWellDetectionMultiscale.Parameters("Ana", 0.1, 1, 0.02, 0.2, 0.05, 5, 3, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {10.0038, 9.9991}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.15029, 0.1079}, w.ell().rad(), 1e-4);
			assertEquals(-0.0668, w.ell().phi(), 1e-4);
			assertEquals(0.14705, w.score().value(), 1e-5);
			assertEquals(0.15689, w.A(), 1e-5);
			assertEquals(0.04147, w.D(), 1e-5);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQCIRC, Double.NaN, 5, 4, true, false);
			IterationChooser.BestParabScore.Parameters itChoosePs = 
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetectionMultiscale.Parameters ps =
					new HybridWellDetectionMultiscale.Parameters("Ana", 0.1, 1, 0.02, 0.2, 0.05, 5, 3, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {10.0038, 9.9991}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.15029, 0.1079}, w.ell().rad(), 1e-4);
			assertEquals(-0.0668, w.ell().phi(), 1e-4);
			assertEquals(0.14705, w.score().value(), 1e-5);
			assertEquals(0.15689, w.A(), 1e-5);
			assertEquals(0.03934, w.D(), 1e-5);
		}

		{
			DensityEstimatorParameters estPs = new DensityEstimatorParameters(WellEstimator.type.DENS, true);
			GridEstimatorParameters estGridPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, Double.NaN,
					5, 4, true, false);
			IterationChooser.BestParabScore.Parameters itChoosePs = 
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estGridPs, 0.7, 0.5);
	
			HybridWellDetectionMultiscale.Parameters ps =
					new HybridWellDetectionMultiscale.Parameters("Ana", 0.1, 1, 0.02, 0.2, 0.05, 5, 3, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {10.0039, 9.9991}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.15029, 0.10797}, w.ell().rad(), 1e-4);
			assertEquals(-0.06686, w.ell().phi(), 1e-4);
			assertEquals(0.04510, w.score().value(), 1e-5);
			assertEquals(0.16701, w.A(), 1e-5);
			assertEquals(0.03934, w.D(), 1e-5);
		}

		{
			DensityEstimatorParameters estPs = new DensityEstimatorParameters(WellEstimator.type.MLE, true);
			GridEstimatorParameters estGridPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, Double.NaN,
					5, 4, true, false);
			IterationChooser.BestParabScore.Parameters itChoosePs = 
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estGridPs, 0.7, 0.5);

			HybridWellDetectionMultiscale.Parameters ps =
					new HybridWellDetectionMultiscale.Parameters("Ana", 0.1, 1, 0.02, 0.2, 0.05, 5, 3, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {10.0039, 9.9991}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.15029, 0.10797}, w.ell().rad(), 1e-4);
			assertEquals(-0.06686, w.ell().phi(), 1e-4);
			assertEquals(0.04510, w.score().value(), 1e-5);
			assertEquals(0.24751, w.A(), 1e-5);
			assertEquals(0.05184, w.D(), 1e-5);
		}

		{
			DensityEstimatorParameters estPs = new DensityEstimatorParameters(WellEstimator.type.MLE, true);
			GridEstimatorParameters estGridPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, Double.NaN,
					5, 4, true, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estGridPs, 0.7, 0.5);

			HybridWellDetectionMultiscale.Parameters ps =
					new HybridWellDetectionMultiscale.Parameters("Ana", 0.1, 1, 0.02, 0.2, 0.05, 5, 3, 1, 10, 95, estPs, 1, itChoosePs);

			HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {10.0039, 9.9991}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.15029, 0.10797}, w.ell().rad(), 1e-4);
			assertEquals(-0.06686, w.ell().phi(), 1e-4);
			assertEquals(0.04510, w.score().value(), 1e-5);
			assertEquals(0.32032, w.A(), 1e-5);
			assertEquals(0.05184, w.D(), 1e-5);
		}

		{
			DensityEstimatorParameters estPs = new DensityEstimatorParameters(WellEstimator.type.MLE, true);
			GridEstimatorParameters estGridPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, Double.NaN,
					5, 4, true, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estGridPs, 0.7, 0.5);

			HybridWellDetectionMultiscale.Parameters ps =
					new HybridWellDetectionMultiscale.Parameters("Ana", 0.1, 1, 0.02, 0.2, 0.05, 5, 3, 1, 10, 95, estPs, 2, itChoosePs);

			HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {10.0039, 9.9991}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.1502, 0.1079}, w.ell().rad(), 1e-4);
			assertEquals(-0.06686, w.ell().phi(), 1e-4);
			assertEquals(0.04510, w.score().value(), 1e-5);
			assertEquals(0.33974, w.A(), 1e-5);
			assertEquals(0.05184, w.D(), 1e-5);
		}
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

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, Double.NaN, 5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs = 
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);
	
			HybridWellDetectionMultiscale.Parameters ps =
					new HybridWellDetectionMultiscale.Parameters("Ana", 0.1, 1, 0.02, 0.2, 0.05, 5, 3, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.9992, 9.9972}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.14067, 0.10528}, w.ell().rad(), 1e-4);
			assertEquals(0.002344, w.ell().phi(), 1e-4);
			assertEquals(0.08717, w.score().value(), 1e-5);
			assertEquals(0.09003, w.A(), 1e-5);
			assertEquals(0.02491, w.D(), 1e-5);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, Double.NaN, 5, 4, true, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetectionMultiscale.Parameters ps =
					new HybridWellDetectionMultiscale.Parameters("Ana", 0.1, 1, 0.02, 0.2, 0.05, 5, 3, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.9992, 9.9972}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.14067, 0.10528}, w.ell().rad(), 1e-4);
			assertEquals(0.00234, w.ell().phi(), 1e-4);
			assertEquals(0.08717, w.score().value(), 1e-5);
			assertEquals(0.09003, w.A(), 1e-5);
			assertEquals(0.02372, w.D(), 1e-5);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQCIRC, Double.NaN, 5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetectionMultiscale.Parameters ps =
					new HybridWellDetectionMultiscale.Parameters("Ana", 0.1, 1, 0.02, 0.2, 0.05, 5, 3, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.9992, 9.9972}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.14067, 0.10528}, w.ell().rad(), 1e-4);
			assertEquals(0.00234, w.ell().phi(), 1e-4);
			assertEquals(0.01251, w.score().value(), 1e-5);
			assertEquals(0.12049, w.A(), 1e-5);
			assertEquals(0.02491, w.D(), 1e-5);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQCIRC, Double.NaN, 5, 4, true, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetectionMultiscale.Parameters ps =
					new HybridWellDetectionMultiscale.Parameters("Ana", 0.1, 1, 0.02, 0.2, 0.05, 5, 3, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.9992, 9.9972}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.14067, 0.10528}, w.ell().rad(), 1e-4);
			assertEquals(0.002344, w.ell().phi(), 1e-4);
			assertEquals(0.01251, w.score().value(), 1e-5);
			assertEquals(0.12049, w.A(), 1e-5);
			assertEquals(0.02372, w.D(), 1e-5);
		}

		{
			DensityEstimatorParameters estPs = new DensityEstimatorParameters(WellEstimator.type.DENS, true);
			GridEstimatorParameters estGridPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, Double.NaN,
					5, 4, true, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estGridPs, 0.7, 0.5);

			HybridWellDetectionMultiscale.Parameters ps =
					new HybridWellDetectionMultiscale.Parameters("Ana", 0.1, 1, 0.02, 0.2, 0.05, 5, 3, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.9992, 9.9972}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.14067, 0.10528}, w.ell().rad(), 1e-4);
			assertEquals(0.00234, w.ell().phi(), 1e-4);
			assertEquals(0.08717, w.score().value(), 1e-5);
			assertEquals(0.12331, w.A(), 1e-5);
			assertEquals(0.02372, w.D(), 1e-5);
		}

		{
			DensityEstimatorParameters estPs = new DensityEstimatorParameters(WellEstimator.type.MLE, true);
			GridEstimatorParameters estGridPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, Double.NaN,
					5, 4, true, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estGridPs, 0.7, 0.5);

			HybridWellDetectionMultiscale.Parameters ps =
					new HybridWellDetectionMultiscale.Parameters("Ana", 0.1, 1, 0.02, 0.2, 0.05, 5, 3, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.9992, 9.9972}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.14067, 0.10528}, w.ell().rad(), 1e-4);
			assertEquals(0.00234, w.ell().phi(), 1e-4);
			assertEquals(0.08717, w.score().value(), 1e-5);
			assertEquals(0.30438, w.A(), 1e-5);
			assertEquals(0.05296, w.D(), 1e-5);
		}
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

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, Double.NaN, 5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetectionMultiscale.Parameters ps =
					new HybridWellDetectionMultiscale.Parameters("Ana", 0.1, 1, 0.02, 0.2, 0.05, 5, 3, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {10.0031, 9.9996}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.12645, 0.11345}, w.ell().rad(), 1e-4);
			assertEquals(-0.60121, w.ell().phi(), 1e-4);
			assertEquals(0.03749, w.score().value(), 1e-5);
			assertEquals(0.08622, w.A(), 1e-5);
			assertEquals(0.02777, w.D(), 1e-5);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, Double.NaN, 5, 4, true, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetectionMultiscale.Parameters ps =
					new HybridWellDetectionMultiscale.Parameters("Ana", 0.1, 1, 0.02, 0.2, 0.05, 5, 3, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {10.0031, 9.9996}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.12645, 0.11345}, w.ell().rad(), 1e-4);
			assertEquals(-0.6012, w.ell().phi(), 1e-4);
			assertEquals(0.03749, w.score().value(), 1e-5);
			assertEquals(0.08622, w.A(), 1e-5);
			assertEquals(0.02613, w.D(), 1e-5);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQCIRC, Double.NaN, 5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetectionMultiscale.Parameters ps =
					new HybridWellDetectionMultiscale.Parameters("Ana", 0.1, 1, 0.02, 0.2, 0.05, 5, 3, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {10.0014, 10.0011}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.14489, 0.12041}, w.ell().rad(), 1e-4);
			assertEquals(-0.62965, w.ell().phi(), 1e-4);
			assertEquals(0.01379, w.score().value(), 1e-5);
			assertEquals(0.13156, w.A(), 1e-5);
			assertEquals(0.02839, w.D(), 1e-5);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQCIRC, Double.NaN, 5, 4, true, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetectionMultiscale.Parameters ps =
					new HybridWellDetectionMultiscale.Parameters("Ana", 0.1, 1, 0.02, 0.2, 0.05, 5, 3, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {10.0014, 10.0011}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.14489, 0.12041}, w.ell().rad(), 1e-4);
			assertEquals(-0.62965, w.ell().phi(), 1e-4);
			assertEquals(0.01379, w.score().value(), 1e-5);
			assertEquals(0.13156, w.A(), 1e-5);
			assertEquals(0.02726, w.D(), 1e-5);
		}

		{
			DensityEstimatorParameters estPs = new DensityEstimatorParameters(WellEstimator.type.DENS, true);
			GridEstimatorParameters estGridPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, Double.NaN,
					5, 4, true, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estGridPs, 0.7, 0.5);

			HybridWellDetectionMultiscale.Parameters ps =
					new HybridWellDetectionMultiscale.Parameters("Ana", 0.1, 1, 0.02, 0.2, 0.05, 5, 3, 1, 10, 95, estPs, 0, itChoosePs);
			HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {10.0031, 9.9996}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.12645, 0.11345}, w.ell().rad(), 1e-4);
			assertEquals(-0.60121, w.ell().phi(), 1e-4);
			assertEquals(0.03749, w.score().value(), 1e-5);
			assertEquals(0.10897, w.A(), 1e-5);
			assertEquals(0.02613, w.D(), 1e-5);
		}

		{
			DensityEstimatorParameters estPs = new DensityEstimatorParameters(WellEstimator.type.MLE, true);
			GridEstimatorParameters estGridPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, Double.NaN,
					5, 4, true, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estGridPs, 0.7, 0.5);

			HybridWellDetectionMultiscale.Parameters ps =
					new HybridWellDetectionMultiscale.Parameters("Ana", 0.1, 1, 0.02, 0.2, 0.05, 5, 3, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {10.0031, 9.9996}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.12645, 0.11345}, w.ell().rad(), 1e-4);
			assertEquals(-0.60121, w.ell().phi(), 1e-4);
			assertEquals(0.03749, w.score().value(), 1e-5);
			assertEquals(0.21529, w.A(), 1e-5);
			assertEquals(0.04945, w.D(), 1e-5);
		}
	}

	@Test
	public void testSimusVaryParams()
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

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, Double.NaN, 5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetectionMultiscale.Parameters ps =
					new HybridWellDetectionMultiscale.Parameters("Ana", 0.04, 1, 0.02, 0.2, 0.05, 5, 3, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {10.0072, 10.0006}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.1131, 0.08959}, w.ell().rad(), 1e-4);
			assertEquals(-0.0936, w.ell().phi(), 1e-4);
			assertEquals(0.03323, w.score().value(), 1e-5);
			assertEquals(0.08632, w.A(), 1e-5);
			assertEquals(0.03991, w.D(), 1e-5);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, Double.NaN, 5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetectionMultiscale.Parameters ps =
					new HybridWellDetectionMultiscale.Parameters("Ana", 0.1, 1, 0.03, 0.2, 0.05, 5, 3, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {10.0135, 10.0026}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.1165, 0.0870}, w.ell().rad(), 1e-4);
			assertEquals(-0.0525, w.ell().phi(), 1e-4);
			assertEquals(0.08124, w.score().value(), 1e-5);
			assertEquals(0.07256, w.A(), 1e-5);
			assertEquals(0.03970, w.D(), 1e-5);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, Double.NaN, 5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetectionMultiscale.Parameters ps =
					new HybridWellDetectionMultiscale.Parameters("Ana", 0.2, 1, 0.03, 0.3, 0.05, 5, 3, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetectionMultiscale hwd = new HybridWellDetectionMultiscale(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {10.00668, 9.99936}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.16486, 0.11632}, w.ell().rad(), 1e-4);
			assertEquals(-0.09179, w.ell().phi(), 1e-4);
			assertEquals(0.10272, w.score().value(), 1e-5);
			assertEquals(0.11707, w.A(), 1e-5);
			assertEquals(0.04197, w.D(), 1e-5);
		}
	}
}
