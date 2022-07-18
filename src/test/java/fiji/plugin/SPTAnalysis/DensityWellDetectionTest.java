package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import fiji.plugin.SPTAnalysis.estimators.DensityEstimatorParameters;
import fiji.plugin.SPTAnalysis.estimators.WellEstimator;
import fiji.plugin.SPTAnalysis.readers.CSVReaderOptions;
import fiji.plugin.SPTAnalysis.readers.TrajectoryCSVReader;
import fiji.plugin.SPTAnalysis.struct.PotWell;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;
import fiji.plugin.SPTAnalysis.wellDetection.DensityWellDetection;
import fiji.plugin.SPTAnalysis.wellDetection.IterationChooser;

public class DensityWellDetectionTest
{
	@Test
	public void test1()
	{
		String fname = ClassLoader.getSystemResource("trajectories/trajs_well.csv").getFile();

		CSVReaderOptions csvOpts = new CSVReaderOptions(",", 0, 1, 2, 3, false, 0, 0, false, 0.0, false, 0.0);
		TrajectoryCSVReader reader = new TrajectoryCSVReader(fname, csvOpts);
		TrajectoryEnsemble trajs = reader.read();

		DensityEstimatorParameters estPs = new DensityEstimatorParameters(WellEstimator.type.DENSMLE, false);
		DensityWellDetection.Parameters ps =
				new DensityWellDetection.Parameters("Ana", 0.2, 5, 3, 0.05, 5, 0.03, 0.01, 0.6, 0.5, estPs,
						new IterationChooser.MaxDensPerc.Parameters(IterationChooser.chooser.MaxDensPerc, estPs, 0.3));

		DensityWellDetection wd = new DensityWellDetection(ps);

		ArrayList<PotWell> res = wd.detectWells(trajs, null).wells;
		assertEquals(1, res.size());
		PotWell w = res.get(0);

		assertArrayEquals(new double[] {9.9940,9.9996}, w.ell().mu(), 1e-3);
		assertArrayEquals(new double[] {0.0700,0.0682}, w.ell().rad(), 1e-3);
		assertEquals(1.4099, w.ell().phi(), 1e-3);
		assertEquals(0.0865, w.A(), 1e-3);
		assertEquals(0.0388, w.D(), 1e-3);
		assertEquals(7248930.374, w.score().value(), 1e-3);
	}
}
