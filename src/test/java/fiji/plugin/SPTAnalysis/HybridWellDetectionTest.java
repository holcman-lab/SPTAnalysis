package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import fiji.plugin.SPTAnalysis.estimators.DensityEstimatorParameters;
import fiji.plugin.SPTAnalysis.estimators.GridEstimatorParameters;
import fiji.plugin.SPTAnalysis.estimators.MLEEstimatorParameters;
import fiji.plugin.SPTAnalysis.estimators.WellEstimator;
import fiji.plugin.SPTAnalysis.estimators.WellEstimatorParameters;
import fiji.plugin.SPTAnalysis.readers.CSVReaderOptions;
import fiji.plugin.SPTAnalysis.readers.TrajectoryCSVReader;
import fiji.plugin.SPTAnalysis.struct.PotWell;
import fiji.plugin.SPTAnalysis.struct.SquareGrid;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;
import fiji.plugin.SPTAnalysis.wellDetection.FitResult;
import fiji.plugin.SPTAnalysis.wellDetection.HybridWellDetection;
import fiji.plugin.SPTAnalysis.wellDetection.IterationChooser;

public class HybridWellDetectionTest
{
	@Test
	public void test1()
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

		double dx = 0.2;
		GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL,
				dx, 4, 5, false, false);
		HybridWellDetection.Parameters ps =
				new HybridWellDetection.Parameters("Ana", dx, 1, 5, 3, 0.18, 1, 10, 95, estPs, 0,
						new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, -1, -1));

		HybridWellDetection hwd = new HybridWellDetection(ps);

		ArrayList<PotWell> res = hwd.detectWells(trajs, null).wells;
		assertEquals(1, res.size());
		PotWell w = res.get(0);

		assertArrayEquals(new double[] {9.99705, 10.0033}, w.ell().mu(), 1e-3);
		assertArrayEquals(new double[] {0.22204, 0.20333}, w.ell().rad(), 1e-3);
		assertEquals(0.35162, w.ell().phi(), 1e-3);
		assertEquals(0.05182, w.score().value(), 1e-3);
		assertEquals(0.158042, w.A(), 1e-3);
		assertEquals(0.0455595, w.D(), 1e-3);
	}

	@Test
	public void test1bis()
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

		double dx = 0.1;
		GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, dx,
				5, 4, false, false);
		HybridWellDetection.Parameters ps =
				new HybridWellDetection.Parameters("Ana", dx, 1, 5, 3, 0.1, 1, 10, 90, estPs, 0,
						new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, -1, -1));

		HybridWellDetection hwd = new HybridWellDetection(ps);

		ArrayList<PotWell> res = hwd.detectWells(trajs, null).wells;
		assertEquals(1, res.size());
		PotWell w = res.get(0);

		assertArrayEquals(new double[] {9.9970, 10.0024}, w.ell().mu(), 1e-3);
		assertArrayEquals(new double[] {0.19467, 0.17827}, w.ell().rad(), 1e-3);
		assertEquals(0.3516, w.ell().phi(), 1e-3);
		assertEquals(0.0476, w.score().value(), 1e-3);
		assertEquals(0.1627, w.A(), 1e-3);
		assertEquals(0.0455158, w.D(), 1e-3);
	}

	@Test
	public void test1ter()
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

		double dx = 0.1;
		GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, dx,
				5, 4, false, false);
		HybridWellDetection.Parameters ps =
				new HybridWellDetection.Parameters("Ana", dx, 1, 5, 3, 0.1, 1, 10, 90, estPs, 1,
						new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, -1, -1));

		HybridWellDetection hwd = new HybridWellDetection(ps);

		ArrayList<PotWell> res = hwd.detectWells(trajs, null).wells;
		assertEquals(1, res.size());
		PotWell w = res.get(0);

		assertArrayEquals(new double[] {9.9970, 10.0024}, w.ell().mu(), 1e-3);
		assertArrayEquals(new double[] {0.1946, 0.1782}, w.ell().rad(), 1e-3);
		assertEquals(0.3516, w.ell().phi(), 1e-3);
		assertEquals(0.0476, w.score().value(), 1e-3);
		assertEquals(0.167856, w.A(), 1e-3);
		assertEquals(0.0455158, w.D(), 1e-3);

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

		GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.3,
				5, 4, false, false);
		IterationChooser.BestParabScore.Parameters itChoosePs =
				new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);
		HybridWellDetection.Parameters ps =
				new HybridWellDetection.Parameters("Ana", 0.2, 1, 5, 3, 0.3, 1, 10, 95, estPs, 0, itChoosePs);

		HybridWellDetection hwd = new HybridWellDetection(ps);

		SquareGrid g = new SquareGrid(trajs, 0.2);
		FitResult res = hwd.fitWellFromSeed(trajs, g.get(25, 41));
		res.setBestIt(new IterationChooser.BestParabScore(itChoosePs, trajs));

		assertEquals(0, res.bestIt());
		PotWell w = res.bestWell(trajs, ps);

		assertArrayEquals(new double[] {5.02457, 8.22208}, w.ell().mu(), 1e-4);
		assertArrayEquals(new double[] {0.50089, 0.24260}, w.ell().rad(), 1e-4);
		assertEquals(1.0467, w.ell().phi(), 1e-4);
		assertEquals(0.51389, w.score().value(), 1e-5);
		assertEquals(0.423615, w.A(), 1e-5);
		assertEquals(0.354111, w.D(), 1e-5);
	}

	@Test
	public void test3()
	{
		String fname = ClassLoader.getSystemResource("trajectories/sample_data_well.csv").getFile();

		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, CSVReaderOptions.trackmateOptions());
		TrajectoryEnsemble trajs = null;
		try {
			trajs = reader.read();
		} catch (Exception e) {
			e.printStackTrace();
		}

		GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.25,
				5, 4, false, false);
		IterationChooser.BestParabScore.Parameters itChoosePs =
				new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

		HybridWellDetection.Parameters ps =
				new HybridWellDetection.Parameters("Ana", 0.2, 1, 5, 3, 0.25, 1, 10, 95, estPs, 0, itChoosePs);

		HybridWellDetection hwd = new HybridWellDetection(ps);

		SquareGrid g = new SquareGrid(trajs, 0.2);
		FitResult res = hwd.fitWellFromSeed(trajs, g.get(37, 39));
		res.setBestIt(new IterationChooser.BestParabScore(itChoosePs, trajs));

		assertEquals(-1, res.bestIt());
	}

	@Test
	public void test4()
	{
		String fname = ClassLoader.getSystemResource("trajectories/sample_data_well.csv").getFile();

		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, CSVReaderOptions.trackmateOptions());
		TrajectoryEnsemble trajs = null;
		try {
			trajs = reader.read();
		} catch (Exception e) {
			e.printStackTrace();
		}

		GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.15,
				5, 4, true, false);
		IterationChooser.BestParabScore.Parameters itChoosePs =
				new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

		HybridWellDetection.Parameters ps =
				new HybridWellDetection.Parameters("Ana", 0.2, 1, 5, 3, 0.12, 1, 10, 95, estPs, 0, itChoosePs);

		HybridWellDetection hwd = new HybridWellDetection(ps);

		SquareGrid g = new SquareGrid(trajs, 0.2);
		FitResult res = hwd.fitWellFromSeed(trajs, g.get(37, 39));
		res.setBestIt(new IterationChooser.BestParabScore(itChoosePs, trajs));

		assertEquals(1, res.bestIt());

		PotWell w = res.bestWell(trajs, ps);

		assertArrayEquals(new double[] {7.38649, 8.00658}, w.ell().mu(), 1e-4);
		assertArrayEquals(new double[] {0.243358, 0.175455}, w.ell().rad(), 1e-4);
		assertEquals(-0.273563, w.ell().phi(), 1e-4);
		assertEquals(0.19345, w.score().value(), 1e-5);
		assertEquals(0.61347, w.A(), 1e-5);
		assertEquals(0.17615, w.D(), 1e-5);
	}

	@Test
	public void test5()
	{
		String fname = ClassLoader.getSystemResource("trajectories/sample_data_well.csv").getFile();

		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, CSVReaderOptions.trackmateOptions());
		TrajectoryEnsemble trajs = null;
		try {
			trajs = reader.read();
		} catch (Exception e) {
			e.printStackTrace();
		}

		GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.1,
				5, 4, false, false);
		IterationChooser.BestParabScore.Parameters itChoosePs =
				new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

		HybridWellDetection.Parameters ps =
				new HybridWellDetection.Parameters("Ana", 0.2, 1, 5, 3, 0.1, 1, 10, 95, estPs, 0, itChoosePs);

		HybridWellDetection hwd = new HybridWellDetection(ps);

		SquareGrid g = new SquareGrid(trajs, 0.2);
		FitResult res = hwd.fitWellFromSeed(trajs, g.get(37, 39));
		res.setBestIt(new IterationChooser.BestParabScore(itChoosePs, trajs));

		assertEquals(1, res.bestIt());

		PotWell w = res.bestWell(trajs, ps);

		assertArrayEquals(new double[] {7.38868, 8.00490}, w.ell().mu(), 1e-4);
		assertArrayEquals(new double[] {0.21465, 0.151822}, w.ell().rad(), 1e-4);
		assertEquals(-0.32541, w.ell().phi(), 1e-4);
		assertEquals(0.21257, w.score().value(), 1e-5);
		assertEquals(0.552441, w.A(), 1e-4);
		assertEquals(0.258095, w.D(), 1e-4);
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
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.1,
					5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.9703, 9.9806}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.1506, 0.1257}, w.ell().rad(), 1e-4);
			assertEquals(-0.3289, w.ell().phi(), 1e-4);
			assertEquals(0.558240, w.score().value(), 1e-5);
			assertEquals(0.066450, w.A(), 1e-5);
			assertEquals(0.049215, w.D(), 1e-5);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.1,
					5, 4, true, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 10, 95, estPs, 0, itChoosePs);
			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.9703, 9.9806}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.1506, 0.1257}, w.ell().rad(), 1e-4);
			assertEquals(-0.3289, w.ell().phi(), 1e-4);
			assertEquals(0.558240, w.score().value(), 1e-5);
			assertEquals(0.06645, w.A(), 1e-5);
			assertEquals(0.04916, w.D(), 1e-5);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQCIRC, 0.1,
					5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.9703, 9.9806}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.1506, 0.1257}, w.ell().rad(), 1e-4);
			assertEquals(-0.3289, w.ell().phi(), 1e-4);
			assertEquals(0.55917, w.score().value(), 1e-5);
			assertEquals(0.07761, w.A(), 1e-5);
			assertEquals(0.04921, w.D(), 1e-5);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQCIRC, 0.1,
					5, 4, true, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.9703, 9.9806}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.1506, 0.1257}, w.ell().rad(), 1e-4);
			assertEquals(-0.3289, w.ell().phi(), 1e-4);
			assertEquals(0.55917, w.score().value(), 1e-5);
			assertEquals(0.07761, w.A(), 1e-5);
			assertEquals(0.04916, w.D(), 1e-5);
		}

		{
			DensityEstimatorParameters estPs = new DensityEstimatorParameters(WellEstimator.type.DENS, true);
			GridEstimatorParameters estGridPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.1,
					5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estGridPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps = new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.9703, 9.9806}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.1506, 0.1257}, w.ell().rad(), 1e-4);
			assertEquals(-0.3289, w.ell().phi(), 1e-4);
			assertEquals(0.55824, w.score().value(), 1e-5);
			assertEquals(0.19523, w.A(), 1e-5);
			assertEquals(0.04916, w.D(), 1e-5);
		}

		{
			DensityEstimatorParameters estPs = new DensityEstimatorParameters(WellEstimator.type.MLE, true);
			GridEstimatorParameters estGridPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.1,
					5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estGridPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.9703, 9.9806}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.1506, 0.1257}, w.ell().rad(), 1e-4);
			assertEquals(-0.3289, w.ell().phi(), 1e-4);
			assertEquals(0.55824, w.score().value(), 1e-5);
			assertEquals(0.19117, w.A(), 1e-5);
			assertEquals(0.05443, w.D(), 1e-5);
		}

		{
			DensityEstimatorParameters estPs = new DensityEstimatorParameters(WellEstimator.type.MLE, true);
			GridEstimatorParameters estGridPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.1,
					5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estGridPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 10, 95, estPs, 1, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.9703, 9.9806}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.1506, 0.1257}, w.ell().rad(), 1e-4);
			assertEquals(-0.3289, w.ell().phi(), 1e-4);
			assertEquals(0.55824, w.score().value(), 1e-5);
			assertEquals(0.18409, w.A(), 1e-5);
			assertEquals(0.05443, w.D(), 1e-5);
		}

		{
			DensityEstimatorParameters estPs = new DensityEstimatorParameters(WellEstimator.type.MLE, true);
			GridEstimatorParameters estGridPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.1,
					5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estGridPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps = new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 10, 95, estPs, 2, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);
			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.9703, 9.9806}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.1506, 0.1257}, w.ell().rad(), 1e-4);
			assertEquals(-0.3289, w.ell().phi(), 1e-4);
			assertEquals(0.55824, w.score().value(), 1e-5);
			assertEquals(0.21220, w.A(), 1e-5);
			assertEquals(0.05443, w.D(), 1e-5);
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
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.1,
					5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 10, 95, estPs, 0, itChoosePs);
			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.9987, 9.9996}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.1964, 0.12974}, w.ell().rad(), 1e-4);
			assertEquals(-0.1639, w.ell().phi(), 1e-4);
			assertEquals(0.16739, w.score().value(), 1e-5);
			assertEquals(0.13076, w.A(), 1e-5);
			assertEquals(0.04213, w.D(), 1e-5);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.1,
					5, 4, true, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.9987, 9.9996}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.1964, 0.12974}, w.ell().rad(), 1e-4);
			assertEquals(-0.1639, w.ell().phi(), 1e-4);
			assertEquals(0.16739, w.score().value(), 1e-5);
			assertEquals(0.13076, w.A(), 1e-5);
			assertEquals(0.04073, w.D(), 1e-5);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQCIRC, 0.1,
					5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps = new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.99871, 9.99966}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.19640, 0.12974}, w.ell().rad(), 1e-4);
			assertEquals(-0.163955, w.ell().phi(), 1e-4);
			assertEquals(0.11496, w.score().value(), 1e-5);
			assertEquals(0.23837, w.A(), 1e-5);
			assertEquals(0.04213, w.D(), 1e-5);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQCIRC, 0.1,
					5, 4, true, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.99871, 9.99966}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.19640, 0.12974}, w.ell().rad(), 1e-4);
			assertEquals(-0.16395, w.ell().phi(), 1e-4);
			assertEquals(0.11496, w.score().value(), 1e-5);
			assertEquals(0.23837, w.A(), 1e-5);
			assertEquals(0.04073, w.D(), 1e-5);
		}

		{
			DensityEstimatorParameters estPs = new DensityEstimatorParameters(WellEstimator.type.DENS, true);
			GridEstimatorParameters estGridPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.1,
					5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estGridPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.9987, 9.9996}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.1964, 0.12974}, w.ell().rad(), 1e-4);
			assertEquals(-0.1639, w.ell().phi(), 1e-4);
			assertEquals(0.16739, w.score().value(), 1e-5);
			assertEquals(0.21604, w.A(), 1e-5);
			assertEquals(0.04073, w.D(), 1e-5);
		}

		{
			DensityEstimatorParameters estPs = new DensityEstimatorParameters(WellEstimator.type.MLE, true);
			GridEstimatorParameters estGridPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.1,
					5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estGridPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.9987, 9.9996}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.1964, 0.12974}, w.ell().rad(), 1e-4);
			assertEquals(-0.1639, w.ell().phi(), 1e-4);
			assertEquals(0.16739, w.score().value(), 1e-5);
			assertEquals(0.32388, w.A(), 1e-5);
			assertEquals(0.05171, w.D(), 1e-5);
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
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.1,
					5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {10.0068, 9.9985}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.1901, 0.1803}, w.ell().rad(), 1e-4);
			assertEquals(-0.0065, w.ell().phi(), 1e-4);
			assertEquals(0.05513, w.score().value(), 1e-5);
			assertEquals(0.15532, w.A(), 1e-5);
			assertEquals(0.02643, w.D(), 1e-5);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.1,
					5, 4, true, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {10.0068, 9.9985}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.1901, 0.1803}, w.ell().rad(), 1e-4);
			assertEquals(-0.0065, w.ell().phi(), 1e-4);
			assertEquals(0.05513, w.score().value(), 1e-5);
			assertEquals(0.15532, w.A(), 1e-5);
			assertEquals(0.02526, w.D(), 1e-5);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQCIRC, 0.1,
					5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {10.0002, 9.99693}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.14682, 0.10956}, w.ell().rad(), 1e-4);
			assertEquals(-0.05351, w.ell().phi(), 1e-4);
			assertEquals(0.01799, w.score().value(), 1e-5);
			assertEquals(0.10405, w.A(), 1e-5);
			assertEquals(0.02505, w.D(), 1e-5);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQCIRC, 0.1,
					5, 4, true, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {10.0002, 9.9969}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.14682, 0.10956}, w.ell().rad(), 1e-4);
			assertEquals(-0.05351, w.ell().phi(), 1e-4);
			assertEquals(0.01799, w.score().value(), 1e-5);
			assertEquals(0.10405, w.A(), 1e-5);
			assertEquals(0.02389, w.D(), 1e-5);
		}

		{
			DensityEstimatorParameters estPs = new DensityEstimatorParameters(WellEstimator.type.DENS, true);
			GridEstimatorParameters estGridPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.1,
					5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estGridPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {10.0068, 9.9985}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.1901, 0.1803}, w.ell().rad(), 1e-4);
			assertEquals(-0.0065, w.ell().phi(), 1e-4);
			assertEquals(0.05513, w.score().value(), 1e-5);
			assertEquals(0.25405, w.A(), 1e-5);
			assertEquals(0.02526, w.D(), 1e-5);
		}

		{
			DensityEstimatorParameters estPs = new DensityEstimatorParameters(WellEstimator.type.MLE, true);
			GridEstimatorParameters estGridPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.1,
					5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estGridPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps = new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {10.0068, 9.9985}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.1901, 0.1803}, w.ell().rad(), 1e-4);
			assertEquals(-0.0065, w.ell().phi(), 1e-4);
			assertEquals(0.05513, w.score().value(), 1e-5);
			assertEquals(0.52934, w.A(), 1e-5);
			assertEquals(0.04643, w.D(), 1e-5);
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
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.1,
					5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 10, 95, estPs, 0, itChoosePs);
			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {10.0062, 9.99657}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.11681, 0.10358}, w.ell().rad(), 1e-4);
			assertEquals(-0.585241, w.ell().phi(), 1e-4);
			assertEquals(0.03421, w.score().value(), 1e-5);
			assertEquals(0.063958, w.A(), 1e-5);
			assertEquals(0.027209, w.D(), 1e-5);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.1,
					5, 4, true, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {10.0062, 9.99657}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.116816, 0.103586}, w.ell().rad(), 1e-4);
			assertEquals(-0.585241, w.ell().phi(), 1e-4);
			assertEquals(0.03421, w.score().value(), 1e-5);
			assertEquals(0.0639580, w.A(), 1e-5);
			assertEquals(0.0243588, w.D(), 1e-5);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQCIRC, 0.1,
					5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {10.0062, 9.99657}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.116816, 0.103586}, w.ell().rad(), 1e-4);
			assertEquals(-0.585241, w.ell().phi(), 1e-4);
			assertEquals(0.01230, w.score().value(), 1e-5);
			assertEquals(0.073991, w.A(), 1e-5);
			assertEquals(0.027209, w.D(), 1e-5);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQCIRC, 0.1,
					5, 4, true, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {10.0062, 9.99657}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.116816, 0.103586}, w.ell().rad(), 1e-4);
			assertEquals(-0.58524, w.ell().phi(), 1e-4);
			assertEquals(0.01230, w.score().value(), 1e-5);
			assertEquals(0.0739916, w.A(), 1e-5);
			assertEquals(0.0243588, w.D(), 1e-5);
		}

		{
			DensityEstimatorParameters estPs = new DensityEstimatorParameters(WellEstimator.type.DENS, true);
			GridEstimatorParameters estGridPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.1,
					5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estGridPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {10.0062, 9.99657}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.116816, 0.103586}, w.ell().rad(), 1e-4);
			assertEquals(-0.58524, w.ell().phi(), 1e-4);
			assertEquals(0.03421, w.score().value(), 1e-5);
			assertEquals(0.092875, w.A(), 1e-5);
			assertEquals(0.024358, w.D(), 1e-5);
		}

		{
			DensityEstimatorParameters estPs = new DensityEstimatorParameters(WellEstimator.type.MLE, true);
			GridEstimatorParameters estGridPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.1,
					5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estGridPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {10.0062, 9.99657}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.116816, 0.103586}, w.ell().rad(), 1e-4);
			assertEquals(-0.58524, w.ell().phi(), 1e-4);
			assertEquals(0.03421, w.score().value(), 1e-5);
			assertEquals(0.186132, w.A(), 1e-5);
			assertEquals(0.046929, w.D(), 1e-5);
		}
	}

	@Test
	public void testSimusVaryParams()
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
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.15,
					5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

		HybridWellDetection.Parameters ps =
				new HybridWellDetection.Parameters("Ana", 0.15, 1, 5, 3, 0.15, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.9776, 9.9559}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.30536, 0.25100}, w.ell().rad(), 1e-4);
			assertEquals(1.44555, w.ell().phi(), 1e-4);
			assertEquals(0.51604, w.score().value(), 1e-5);
			assertEquals(0.22093, w.A(), 1e-5);
			assertEquals(0.05005, w.D(), 1e-5);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.2,
					5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.2, 1, 5, 3, 0.2, 1, 10, 95, estPs, 0, itChoosePs);
			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.9664, 9.9557}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.30132, 0.20922}, w.ell().rad(), 1e-4);
			assertEquals(1.2907, w.ell().phi(), 1e-4);
			assertEquals(0.68743, w.score().value(), 1e-5);
			assertEquals(0.11368, w.A(), 1e-5);
			assertEquals(0.04972, w.D(), 1e-5);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.05,
					5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.05, 1, 20, 3, 0.05, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.9819, 9.9790}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.0938, 0.0776}, w.ell().rad(), 1e-4);
			assertEquals(-0.1537, w.ell().phi(), 1e-4);
			assertEquals(0.24888, w.score().value(), 1e-5);
			assertEquals(0.09059, w.A(), 1e-5);
			assertEquals(0.04736, w.D(), 1e-5);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.1,
					5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.1, 0, 20, 0, 0.25, 1, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(0, wells.size());
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.1,
					5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 0.2, 10, 95, estPs, 0, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.9703, 9.9806}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.1506, 0.1257}, w.ell().rad(), 1e-4);
			assertEquals(-0.3289, w.ell().phi(), 1e-4);
			assertEquals(0.55824, w.score().value(), 1e-5);
			assertEquals(0.06645, w.A(), 1e-5);
			assertEquals(0.04921, w.D(), 1e-5);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.1,
					5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 500000, 95, estPs, 0, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(0, wells.size());
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.1,
					2, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.1, 0, 5, 3, 0.1, 1, 20, 95, estPs, 0, itChoosePs);
			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.1,
					8, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps = new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 20, 95, estPs, 0, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
			PotWell w = wells.get(0);

			assertArrayEquals(new double[] {9.9703, 9.9806}, w.ell().mu(), 1e-4);
			assertArrayEquals(new double[] {0.1506, 0.1257}, w.ell().rad(), 1e-4);
			assertEquals(-0.3289, w.ell().phi(), 1e-4);
			assertEquals(0.537765, w.score().value(), 1e-5);
			assertEquals(0.07446, w.A(), 1e-5);
			assertEquals(0.04921, w.D(), 1e-5);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.1,
					5, 20, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 20, 95, estPs, 0, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(0, wells.size());
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.1,
					5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.9, 0.5);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.1, 0, 5, 3, 0.1, 1, 20, 95, estPs, 0, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(0, wells.size());
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.1,
					5, 4, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.9);

			HybridWellDetection.Parameters ps =
					new HybridWellDetection.Parameters("Ana", 0.1, 1, 5, 3, 0.1, 1, 20, 95, estPs, 0, itChoosePs);

			HybridWellDetection hwd = new HybridWellDetection(ps);

			ArrayList<PotWell> wells = hwd.detectWells(trajs, null).wells;
			assertEquals(1, wells.size());
		}
	}

	@Test
	public void testWellMLEDelta()
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

		WellEstimatorParameters estPs = new MLEEstimatorParameters(WellEstimator.type.MLE);
		IterationChooser.Parameters itChoosePs =
				new IterationChooser.BestMLEDeltaScore.Parameters(IterationChooser.chooser.BestMLEDelta, estPs, 5);
		HybridWellDetection.Parameters ps =
				new HybridWellDetection.Parameters("Ana", 0.2, 1, 5, 3, 0.05, 1, 10, 95, estPs, 0, itChoosePs);

		HybridWellDetection hwd = new HybridWellDetection(ps);

		ArrayList<PotWell> res = hwd.detectWells(trajs, null).wells;
		assertEquals(1, res.size());
		PotWell w = res.get(0);

		assertArrayEquals(new double[] {9.9960, 10.0009}, w.ell().mu(), 1e-4);
		assertArrayEquals(new double[] {0.18689, 0.17722}, w.ell().rad(), 1e-4);
		assertEquals(-1.5407, w.ell().phi(), 1e-4);
		assertEquals(0.21559, w.A(), 1e-5);
		assertEquals(0.05024, w.D(), 1e-5);
		assertEquals(1934.54399, w.score().value(), 1e-5);
	}
}
