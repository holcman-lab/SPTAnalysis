package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.*;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.Test;

import fiji.plugin.SPTAnalysis.estimators.GridEstimatorParameters;
import fiji.plugin.SPTAnalysis.estimators.LeastSquareEstimatorEllipse;
import fiji.plugin.SPTAnalysis.estimators.MLEEstimatorParameters;
import fiji.plugin.SPTAnalysis.estimators.WellEstimator;
import fiji.plugin.SPTAnalysis.estimators.WellEstimatorParameters;
import fiji.plugin.SPTAnalysis.estimators.WellScore;
import fiji.plugin.SPTAnalysis.readers.CSVReaderOptions;
import fiji.plugin.SPTAnalysis.readers.TrajectoryCSVReader;
import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.PotWell;
import fiji.plugin.SPTAnalysis.struct.PotWells;
import fiji.plugin.SPTAnalysis.struct.PotWellsWindows;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;
import fiji.plugin.SPTAnalysis.wellDetection.FitResult;
import fiji.plugin.SPTAnalysis.wellDetection.HybridWellDetection;
import fiji.plugin.SPTAnalysis.wellDetection.HybridWellDetectionMultiscale;
import fiji.plugin.SPTAnalysis.wellDetection.IterationChooser;
import fiji.plugin.SPTAnalysis.wellDetection.WellDetectionParameters;
import fiji.plugin.SPTAnalysis.wellLinker.DistanceWellLinker;
import fiji.plugin.SPTAnalysis.wellLinker.WellLinker.WindowIndex;

public class MarshallingTest
{
	@Test public void test0()
	{
		PotWell pw = new PotWell(new Ellipse(new double[] {10.0, 11.0}, new double[] {12.0, 13.0}, 14.0),
								 0.7, 0.07, new WellScore.Parabolic(0.13));

		StringWriter sw = new StringWriter();

		try
		{
			JAXBContext jaxbContext = JAXBContext.newInstance(PotWell.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

			jaxbMarshaller.marshal(pw, sw);
		}
		catch (JAXBException e)
		{
		}

		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
				"<PotWell>\n" + 
				"    <ell>\n" + 
				"        <mu>10.0 11.0</mu>\n" + 
				"        <rad>12.0 13.0</rad>\n" + 
				"        <phi>14.0</phi>\n" + 
				"    </ell>\n" + 
				"    <A>0.7</A>\n" + 
				"    <D>0.07</D>\n" + 
				"    <score>&lt;ParabolicWellScore&gt;\n" + 
				"    &lt;value&gt;0.13&lt;/value&gt;\n" + 
				"&lt;/ParabolicWellScore&gt;</score>\n" + 
				"    <correctedD>false</correctedD>\n" + 
				"</PotWell>\n", sw.toString());
	}

	@Test
	public void testFitResult()
	{
		FitResult fr = new FitResult();

		HashSet<Integer> s1 = new HashSet<> ();
		s1.add(5); s1.add(10);

		HashSet<Integer> s2 = new HashSet<> ();

		HashSet<Integer> s3 = new HashSet<> ();
		s3.add(1); s3.add(3);

		{
			Ellipse ell = new Ellipse(new double[] {10.0, 11.0}, new double[] {0.15, 0.2}, 0.01);
			LeastSquareEstimatorEllipse est = new LeastSquareEstimatorEllipse();
			fr.addIteration(ell, s1, null, est);
			fr.setScore(0, new WellScore.Parabolic(0.3));
		}

		{
			Ellipse ell = new Ellipse(new double[] {10.5, 10.1}, new double[] {0.1, 0.1}, 0.01);
			LeastSquareEstimatorEllipse est = new LeastSquareEstimatorEllipse();
			fr.addIteration(ell, s2, null, est);
			fr.setScore(1, new WellScore.Parabolic(0.2));
		}

		{
			Ellipse ell = new Ellipse(new double[] {10.4, 10.0}, new double[] {0.1, 0.1}, 0.01);
			LeastSquareEstimatorEllipse est = new LeastSquareEstimatorEllipse();
			fr.addIteration(ell, s3, null, est);
			fr.setScore(2, new WellScore.Parabolic(0.2));
		}

		StringWriter sw = new StringWriter();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(FitResult.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

			jaxbMarshaller.marshal(fr, sw);
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}

		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
				"<FitResult>\n" +
				"    <ells>\n" +
				"        <mu>10.0 11.0</mu>\n" +
				"        <rad>0.15 0.2</rad>\n" +
				"        <phi>0.01</phi>\n" +
				"    </ells>\n" +
				"    <ells>\n" +
				"        <mu>10.5 10.1</mu>\n" +
				"        <rad>0.1 0.1</rad>\n" +
				"        <phi>0.01</phi>\n" +
				"    </ells>\n" +
				"    <ells>\n" +
				"        <mu>10.4 10.0</mu>\n" +
				"        <rad>0.1 0.1</rad>\n" +
				"        <phi>0.01</phi>\n" +
				"    </ells>\n" +
				"    <pcaSs xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>\n" +
				"    <pcaSs xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>\n" +
				"    <pcaSs xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>\n" +
				"    <traj_ids>5 10 \n" +
				"\n" +
				"1 3 \n" +
				"</traj_ids>\n" +
				"    <ests>&lt;LeastSquareEstimatorEllipse&gt;\n" +
				"    &lt;DinEllipseOnly&gt;false&lt;/DinEllipseOnly&gt;\n" +
				"&lt;/LeastSquareEstimatorEllipse&gt;</ests>\n" +
				"    <ests>&lt;LeastSquareEstimatorEllipse&gt;\n" +
				"    &lt;DinEllipseOnly&gt;false&lt;/DinEllipseOnly&gt;\n" +
				"&lt;/LeastSquareEstimatorEllipse&gt;</ests>\n" +
				"    <ests>&lt;LeastSquareEstimatorEllipse&gt;\n" +
				"    &lt;DinEllipseOnly&gt;false&lt;/DinEllipseOnly&gt;\n" +
				"&lt;/LeastSquareEstimatorEllipse&gt;</ests>\n" +
				"    <scores>&lt;ParabolicWellScore&gt;\n" +
				"    &lt;value&gt;0.3&lt;/value&gt;\n" +
				"&lt;/ParabolicWellScore&gt;</scores>\n" +
				"    <scores>&lt;ParabolicWellScore&gt;\n" +
				"    &lt;value&gt;0.2&lt;/value&gt;\n" +
				"&lt;/ParabolicWellScore&gt;</scores>\n" +
				"    <scores>&lt;ParabolicWellScore&gt;\n" +
				"    &lt;value&gt;0.2&lt;/value&gt;\n" +
				"&lt;/ParabolicWellScore&gt;</scores>\n" +
				"    <empty>false</empty>\n" +
				"    <empty>false</empty>\n" +
				"    <empty>false</empty>\n" +
				"    <numIt>3</numIt>\n" +
				"    <bestIt>-1</bestIt>\n" + 
				"</FitResult>\n", sw.toString());
	}

	@Test
	public void testWell()
	{
		XMLAdapters.Wells wAdapt = new XMLAdapters.Wells();

		HashMap<String, HashMap<WellDetectionParameters, PotWellsWindows>> dat = new HashMap<> ();
		dat.put(HybridWellDetectionMultiscale.name, new HashMap<> ());
		dat.put(HybridWellDetection.name, new HashMap<> ());

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.2,
					10, 5, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);

			HybridWellDetection.Parameters psMult =
					new HybridWellDetection.Parameters("Ana", 0.2, 1, 5.0, 3, 0.2, 1.0, 20, 95, estPs, 0, itChoosePs);
			PotWells pws = new PotWells();
			pws.wells.add(new PotWell(new Ellipse(new double[] {10.0, 11.0}, new double[] {12.0, 13.0}, 14.0),
									  0.7, 0.07, new WellScore.Parabolic(0.13)));
			pws.wells.add(new PotWell(new Ellipse(new double[] {15.0, 16.0}, new double[] {17.0, 18.0}, 19.0),
									  0.8, 0.08, new WellScore.Parabolic(0.14)));
			PotWellsWindows pwws = new PotWellsWindows();
			pwws.wins.add(pws);
			dat.get(HybridWellDetection.name).put(psMult, pwws);
		}

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, Double.NaN,
					10, 5, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);
			HybridWellDetectionMultiscale.Parameters psMult =
					new HybridWellDetectionMultiscale.Parameters("Ana", 0.2, 1, 0.1, 0.2, 0.02, 5.0, 3, 1.0, 20, 95, estPs, 0, itChoosePs);

			PotWells pws = new PotWells();
			pws.wells.add(new PotWell(new Ellipse(new double[] {0.0, 1.0}, new double[] {2.0, 3.0}, 4.0),
									  0.5, 0.05, new WellScore.Parabolic(0.11)));
			pws.wells.add(new PotWell(new Ellipse(new double[] {5.0, 6.0}, new double[] {7.0, 8.0}, 9.0),
									  0.6, 0.06, new WellScore.Parabolic(0.12)));
			PotWellsWindows pwws = new PotWellsWindows();
			pwws.wins.add(pws);
			dat.get(HybridWellDetectionMultiscale.name).put(psMult, pwws);
		}

		HashMap<String, HashMap<WellDetectionParameters, PotWellsWindows>> res = null;
		try {
			String v = wAdapt.marshal(dat);
			res = wAdapt.unmarshal(v);
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertEquals(2, res.keySet().size());
		assertTrue(res.keySet().contains(HybridWellDetectionMultiscale.name));
		assertTrue(res.keySet().contains(HybridWellDetection.name));

		assertEquals(1, res.get(HybridWellDetectionMultiscale.name).keySet().size());
		assertEquals(1, res.get(HybridWellDetection.name).keySet().size());

		HybridWellDetectionMultiscale.Parameters psMult =
				(HybridWellDetectionMultiscale.Parameters) res.get(HybridWellDetectionMultiscale.name).keySet().iterator().next();

		assertEquals(0.2, psMult.dxSeeds, 1e-5);
		assertEquals(5.0, psMult.densityTh, 1e-5);
		assertEquals(3, psMult.seedDist);
		assertEquals(1.0, psMult.maxSize, 1e-5);
		assertEquals(20, psMult.minPtsTh);
		assertTrue(psMult.estPs instanceof GridEstimatorParameters);
		assertEquals(10, ((GridEstimatorParameters) psMult.estPs).driftNptsTh);
		assertEquals(5, ((GridEstimatorParameters) psMult.estPs).minCellsTh);
		assertFalse(((GridEstimatorParameters) psMult.estPs).diffInWell);
		assertTrue(psMult.itChooserPs instanceof IterationChooser.BestParabScore.Parameters);
		assertEquals(0.7, ((IterationChooser.BestParabScore.Parameters) psMult.itChooserPs).angSimTh, 1e-5);
		assertEquals(0.5, ((IterationChooser.BestParabScore.Parameters) psMult.itChooserPs).sampledRatioTh, 1e-5);
		assertEquals(0.1, psMult.dxMin, 1e-5);
		assertEquals(0.2, psMult.dxMax, 1e-5);
		assertEquals(0.02, psMult.dxStep, 1e-5);

		HybridWellDetection.Parameters psHyb =
				(HybridWellDetection.Parameters) res.get(HybridWellDetection.name).keySet().iterator().next();

		assertEquals(0.2, psHyb.dx, 1e-5);
		assertEquals(5.0, psHyb.densityTh, 1e-5);
		assertEquals(3, psHyb.seedDist);
		assertEquals(1.0, psHyb.maxSize, 1e-5);
		assertEquals(20, psHyb.minPtsTh);
		assertTrue(psHyb.estPs instanceof GridEstimatorParameters);
		assertEquals(10, ((GridEstimatorParameters) psHyb.estPs).driftNptsTh);
		assertEquals(5, ((GridEstimatorParameters) psHyb.estPs).minCellsTh);
		assertFalse(((GridEstimatorParameters) psHyb.estPs).diffInWell);
		assertTrue(psHyb.itChooserPs instanceof IterationChooser.BestParabScore.Parameters);
		assertEquals(0.7, ((IterationChooser.BestParabScore.Parameters) psHyb.itChooserPs).angSimTh, 1e-5);
		assertEquals(0.5, ((IterationChooser.BestParabScore.Parameters) psHyb.itChooserPs).sampledRatioTh, 1e-5);

		{
			PotWellsWindows pwws = res.get(HybridWellDetectionMultiscale.name).get(psMult);
			assertEquals(1, pwws.wins.size());
			assertEquals(2, pwws.wins.get(0).wells.size());
			assertEquals(0.11, pwws.wins.get(0).wells.get(0).score().value(), 1e-5);
			assertEquals(0.12, pwws.wins.get(0).wells.get(1).score().value(), 1e-5);
		}

		{
			PotWellsWindows pwws = res.get(HybridWellDetection.name).get(psHyb);
			assertEquals(1, pwws.wins.size());
			assertEquals(2, pwws.wins.get(0).wells.size());
			assertEquals(0.13, pwws.wins.get(0).wells.get(0).score().value(), 1e-5);
			assertEquals(0.14, pwws.wins.get(0).wells.get(1).score().value(), 1e-5);
		}
	}

	@Test
	public void testWell2()
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

		GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.2,
				5, 4, false, false);
		IterationChooser.Parameters itChoosePs =
				new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);
		HybridWellDetection.Parameters ps =
				new HybridWellDetection.Parameters("Ana", 0.2, 1, 5, 3, 0.1, 1, 10, 95, estPs, 0, itChoosePs);

		HybridWellDetection hwd = new HybridWellDetection(ps);

		ArrayList<PotWell> res = hwd.detectWells(trajs, null).wells;
		assertEquals(1, res.size());
		PotWell w = res.get(0);

		StringWriter sw = new StringWriter();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(PotWell.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

			jaxbMarshaller.marshal(w, sw);
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}

		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
				"<PotWell>\n" + 
				"    <ell>\n" + 
				"        <mu>9.997047715289991 10.003292970123034</mu>\n" + 
				"        <rad>0.22204632059585702 0.20333840849426138</rad>\n" + 
				"        <phi>0.35162540379601953</phi>\n" + 
				"    </ell>\n" + 
				"    <A>0.12443459244772391</A>\n" + 
				"    <D>0.045559517978620026</D>\n" + 
				"    <score>&lt;ParabolicWellScore&gt;\n" + 
				"    &lt;value&gt;0.05181633102800742&lt;/value&gt;\n" + 
				"&lt;/ParabolicWellScore&gt;</score>\n" + 
				"    <fitRes>\n" + 
				"        <ells>\n" + 
				"            <mu>9.968863246554385 10.033523583460942</mu>\n" + 
				"            <rad>0.13771004038312684 0.13327719998866197</rad>\n" + 
				"            <phi>0.4063527721980411</phi>\n" + 
				"        </ells>\n" + 
				"        <ells>\n" + 
				"            <mu>9.995580961015422 10.001713780598383</mu>\n" + 
				"            <rad>0.1890293285683359 0.18044689976821313</rad>\n" + 
				"            <phi>-1.570042917024921</phi>\n" + 
				"        </ells>\n" + 
				"        <ells>\n" + 
				"            <mu>9.995076067615667 10.000677402135247</mu>\n" + 
				"            <rad>0.1983873012463851 0.19235917411511602</rad>\n" + 
				"            <phi>-1.3212650567752728</phi>\n" + 
				"        </ells>\n" + 
				"        <ells>\n" + 
				"            <mu>9.997047715289991 10.003292970123034</mu>\n" + 
				"            <rad>0.22204632059585702 0.20333840849426138</rad>\n" + 
				"            <phi>0.35162540379601953</phi>\n" + 
				"        </ells>\n" + 
				"        <ells>\n" + 
				"            <mu>9.997914298245623 10.003936315789486</mu>\n" + 
				"            <rad>0.23025113401804198 0.20371524221793388</rad>\n" + 
				"            <phi>0.4478363144107288</phi>\n" + 
				"        </ells>\n" + 
				"        <ells>\n" + 
				"            <mu>9.997914298245623 10.003936315789486</mu>\n" + 
				"            <rad>0.23025113401804198 0.20371524221793388</rad>\n" + 
				"            <phi>0.4478363144107288</phi>\n" + 
				"        </ells>\n" + 
				"        <ells>\n" + 
				"            <mu>9.997914298245623 10.003936315789486</mu>\n" + 
				"            <rad>0.23025113401804198 0.20371524221793388</rad>\n" + 
				"            <phi>0.4478363144107288</phi>\n" + 
				"        </ells>\n" + 
				"        <ells>\n" + 
				"            <mu>9.997914298245623 10.003936315789486</mu>\n" + 
				"            <rad>0.23025113401804198 0.20371524221793388</rad>\n" + 
				"            <phi>0.4478363144107288</phi>\n" + 
				"        </ells>\n" + 
				"        <ells>\n" + 
				"            <mu>9.997914298245623 10.003936315789486</mu>\n" + 
				"            <rad>0.23025113401804198 0.20371524221793388</rad>\n" + 
				"            <phi>0.4478363144107288</phi>\n" + 
				"        </ells>\n" + 
				"        <ells>\n" + 
				"            <mu>9.997914298245623 10.003936315789486</mu>\n" + 
				"            <rad>0.23025113401804198 0.20371524221793388</rad>\n" + 
				"            <phi>0.4478363144107288</phi>\n" + 
				"        </ells>\n" + 
				"        <pcaSs>\n" + 
				"            <item>0.003134098279014267</item>\n" + 
				"            <item>0.0029962417742556762</item>\n" + 
				"        </pcaSs>\n" + 
				"        <pcaSs>\n" + 
				"            <item>0.005435000072759824</item>\n" + 
				"            <item>0.005964293984151452</item>\n" + 
				"        </pcaSs>\n" + 
				"        <pcaSs>\n" + 
				"            <item>0.006200250085680197</item>\n" + 
				"            <item>0.0065454640124792875</item>\n" + 
				"        </pcaSs>\n" + 
				"        <pcaSs>\n" + 
				"            <item>0.00807219486284938</item>\n" + 
				"            <item>0.007059014761443265</item>\n" + 
				"        </pcaSs>\n" + 
				"        <pcaSs>\n" + 
				"            <item>0.008488792956047916</item>\n" + 
				"            <item>0.007287452183078408</item>\n" + 
				"        </pcaSs>\n" + 
				"        <pcaSs>\n" + 
				"            <item>0.008488792956047916</item>\n" + 
				"            <item>0.007287452183078408</item>\n" + 
				"        </pcaSs>\n" + 
				"        <pcaSs>\n" + 
				"            <item>0.008488792956047916</item>\n" + 
				"            <item>0.007287452183078408</item>\n" + 
				"        </pcaSs>\n" + 
				"        <pcaSs>\n" + 
				"            <item>0.008488792956047916</item>\n" + 
				"            <item>0.007287452183078408</item>\n" + 
				"        </pcaSs>\n" + 
				"        <pcaSs>\n" + 
				"            <item>0.008488792956047916</item>\n" + 
				"            <item>0.007287452183078408</item>\n" + 
				"        </pcaSs>\n" + 
				"        <pcaSs>\n" + 
				"            <item>0.008488792956047916</item>\n" + 
				"            <item>0.007287452183078408</item>\n" + 
				"        </pcaSs>\n" + 
				"        <traj_ids>0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 44 45 46 47 48 49 50 51 52 53 54 55 56 \n" + 
				"0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 \n" + 
				"0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 \n" + 
				"0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 \n" + 
				"0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 \n" + 
				"0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 \n" + 
				"0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 \n" + 
				"0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 \n" + 
				"0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 \n" + 
				"0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 \n" + 
				"</traj_ids>\n" + 
				"        <ests>&lt;LeastSquareEstimatorEllipse&gt;\n" + 
				"    &lt;drift&gt;\n" + 
				"        &lt;g&gt;\n" + 
				"            &lt;dx&gt;0.1&lt;/dx&gt;\n" + 
				"            &lt;Xmin&gt;4.918863246554384&lt;/Xmin&gt;\n" + 
				"            &lt;Xmin&gt;4.983523583460942&lt;/Xmin&gt;\n" + 
				"            &lt;Xmax&gt;15.018863246554385&lt;/Xmax&gt;\n" + 
				"            &lt;Xmax&gt;15.083523583460943&lt;/Xmax&gt;\n" + 
				"        &lt;/g&gt;\n" + 
				"        &lt;dat&gt;48 50 -0.0536364 0.133182\n" + 
				"49 48 2.40188 0.630625\n" + 
				"49 49 1.08564 0.596489\n" + 
				"49 50 1.09352 0.00710938\n" + 
				"49 51 1.14958 -1.81667\n" + 
				"50 48 0.0233333 1.53187\n" + 
				"50 49 0.0225410 0.435683\n" + 
				"50 50 0.274265 -0.319370\n" + 
				"50 51 0.195556 -1.38198\n" + 
				"50 47 -0.238571 0.508571\n" + 
				"51 48 -0.921739 1.51717\n" + 
				"51 49 -0.726855 0.541613\n" + 
				"51 50 -0.838711 -0.222852\n" + 
				"51 51 -0.666121 -1.14483\n" + 
				"52 49 -0.609091 0.247727\n" + 
				"52 50 -0.735000 -0.687750\n" + 
				"52 51 -1.03750 -0.0437500\n" + 
				"54 52 0.370000 0.290000\n" + 
				"&lt;/dat&gt;\n" + 
				"        &lt;normalized&gt;false&lt;/normalized&gt;\n" + 
				"        &lt;ps&gt;\n" + 
				"            &lt;dx&gt;0.1&lt;/dx&gt;\n" + 
				"            &lt;nPts&gt;5&lt;/nPts&gt;\n" + 
				"            &lt;filter&gt;false&lt;/filter&gt;\n" + 
				"            &lt;filterSize&gt;0.0&lt;/filterSize&gt;\n" + 
				"        &lt;/ps&gt;\n" + 
				"    &lt;/drift&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;DinEllipseOnly&gt;false&lt;/DinEllipseOnly&gt;\n" + 
				"&lt;/LeastSquareEstimatorEllipse&gt;</ests>\n" + 
				"        <ests>&lt;LeastSquareEstimatorEllipse&gt;\n" + 
				"    &lt;drift&gt;\n" + 
				"        &lt;g&gt;\n" + 
				"            &lt;dx&gt;0.1&lt;/dx&gt;\n" + 
				"            &lt;Xmin&gt;4.9455809610154216&lt;/Xmin&gt;\n" + 
				"            &lt;Xmin&gt;4.951713780598382&lt;/Xmin&gt;\n" + 
				"            &lt;Xmax&gt;15.045580961015423&lt;/Xmax&gt;\n" + 
				"            &lt;Xmax&gt;15.051713780598384&lt;/Xmax&gt;\n" + 
				"        &lt;/g&gt;\n" + 
				"        &lt;dat&gt;48 50 0.115385 1.34654\n" + 
				"48 51 0.757778 -1.93611\n" + 
				"49 48 1.69667 1.82917\n" + 
				"49 49 0.796522 0.565507\n" + 
				"49 50 0.876798 -0.136228\n" + 
				"49 51 1.35488 -1.19714\n" + 
				"50 48 -1.09417 0.297917\n" + 
				"50 49 -0.134065 0.909634\n" + 
				"50 50 -0.100818 -0.133364\n" + 
				"50 51 -0.236200 -0.776280\n" + 
				"50 52 -0.414167 -2.11667\n" + 
				"51 48 0.308333 1.76250\n" + 
				"51 49 -1.00719 1.17641\n" + 
				"51 50 -0.869878 0.0194512\n" + 
				"51 51 -0.934727 -0.880364\n" + 
				"51 52 -1.23800 -0.770000\n" + 
				"52 49 -0.872727 0.545455\n" + 
				"52 50 0.411111 0.0355556\n" + 
				"52 51 -2.22000 -1.02600\n" + 
				"52 52 1.29000 0.230000\n" + 
				"54 52 0.106250 0.593750\n" + 
				"&lt;/dat&gt;\n" + 
				"        &lt;normalized&gt;false&lt;/normalized&gt;\n" + 
				"        &lt;ps&gt;\n" + 
				"            &lt;dx&gt;0.1&lt;/dx&gt;\n" + 
				"            &lt;nPts&gt;5&lt;/nPts&gt;\n" + 
				"            &lt;filter&gt;false&lt;/filter&gt;\n" + 
				"            &lt;filterSize&gt;0.0&lt;/filterSize&gt;\n" + 
				"        &lt;/ps&gt;\n" + 
				"    &lt;/drift&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;DinEllipseOnly&gt;false&lt;/DinEllipseOnly&gt;\n" + 
				"&lt;/LeastSquareEstimatorEllipse&gt;</ests>\n" + 
				"        <ests>&lt;LeastSquareEstimatorEllipse&gt;\n" + 
				"    &lt;drift&gt;\n" + 
				"        &lt;g&gt;\n" + 
				"            &lt;dx&gt;0.1&lt;/dx&gt;\n" + 
				"            &lt;Xmin&gt;4.945076067615666&lt;/Xmin&gt;\n" + 
				"            &lt;Xmin&gt;4.950677402135247&lt;/Xmin&gt;\n" + 
				"            &lt;Xmax&gt;15.045076067615668&lt;/Xmax&gt;\n" + 
				"            &lt;Xmax&gt;15.050677402135248&lt;/Xmax&gt;\n" + 
				"        &lt;/g&gt;\n" + 
				"        &lt;dat&gt;48 50 0.369167 1.35875\n" + 
				"48 51 0.389000 -1.62250\n" + 
				"49 48 1.69667 1.82917\n" + 
				"49 49 0.847385 0.634538\n" + 
				"49 50 0.843565 -0.113348\n" + 
				"49 51 1.35488 -1.19714\n" + 
				"50 48 -0.821538 0.415769\n" + 
				"50 49 -0.134083 0.894250\n" + 
				"50 50 -0.0787993 -0.118638\n" + 
				"50 51 -0.258254 -0.785357\n" + 
				"50 52 -0.405385 -2.09231\n" + 
				"51 48 0.308333 1.76250\n" + 
				"51 49 -1.06387 1.17169\n" + 
				"51 50 -0.835732 -0.00195122\n" + 
				"51 51 -0.988571 -0.748571\n" + 
				"51 52 -0.884545 -0.890909\n" + 
				"52 49 -0.720000 0.677000\n" + 
				"52 50 0.130000 -0.0450000\n" + 
				"52 51 -2.22000 -1.02600\n" + 
				"52 52 1.29000 0.230000\n" + 
				"54 52 0.106250 0.593750\n" + 
				"&lt;/dat&gt;\n" + 
				"        &lt;normalized&gt;false&lt;/normalized&gt;\n" + 
				"        &lt;ps&gt;\n" + 
				"            &lt;dx&gt;0.1&lt;/dx&gt;\n" + 
				"            &lt;nPts&gt;5&lt;/nPts&gt;\n" + 
				"            &lt;filter&gt;false&lt;/filter&gt;\n" + 
				"            &lt;filterSize&gt;0.0&lt;/filterSize&gt;\n" + 
				"        &lt;/ps&gt;\n" + 
				"    &lt;/drift&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;DinEllipseOnly&gt;false&lt;/DinEllipseOnly&gt;\n" + 
				"&lt;/LeastSquareEstimatorEllipse&gt;</ests>\n" + 
				"        <ests>&lt;LeastSquareEstimatorEllipse&gt;\n" + 
				"    &lt;drift&gt;\n" + 
				"        &lt;g&gt;\n" + 
				"            &lt;dx&gt;0.1&lt;/dx&gt;\n" + 
				"            &lt;Xmin&gt;4.94704771528999&lt;/Xmin&gt;\n" + 
				"            &lt;Xmin&gt;4.953292970123034&lt;/Xmin&gt;\n" + 
				"            &lt;Xmax&gt;15.047047715289992&lt;/Xmax&gt;\n" + 
				"            &lt;Xmax&gt;15.053292970123035&lt;/Xmax&gt;\n" + 
				"        &lt;/g&gt;\n" + 
				"        &lt;dat&gt;48 50 0.280357 1.33607\n" + 
				"48 51 0.757778 -1.93611\n" + 
				"49 48 0.967143 1.57214\n" + 
				"49 49 0.809714 0.573214\n" + 
				"49 50 0.839043 -0.185739\n" + 
				"49 51 1.28750 -1.07560\n" + 
				"50 48 -0.883636 0.322273\n" + 
				"50 49 -0.170078 0.901250\n" + 
				"50 50 -0.0795018 -0.102669\n" + 
				"50 51 -0.326322 -0.854008\n" + 
				"50 52 -0.409231 -2.18077\n" + 
				"51 48 0.308333 1.76250\n" + 
				"51 49 -1.00785 1.18638\n" + 
				"51 50 -0.889620 -0.0368354\n" + 
				"51 51 -0.845588 -1.04451\n" + 
				"51 52 -1.33667 -0.527778\n" + 
				"52 49 -0.872727 0.545455\n" + 
				"52 50 0.842857 0.310000\n" + 
				"52 52 1.29000 0.230000\n" + 
				"54 52 0.106250 0.593750\n" + 
				"&lt;/dat&gt;\n" + 
				"        &lt;normalized&gt;false&lt;/normalized&gt;\n" + 
				"        &lt;ps&gt;\n" + 
				"            &lt;dx&gt;0.1&lt;/dx&gt;\n" + 
				"            &lt;nPts&gt;5&lt;/nPts&gt;\n" + 
				"            &lt;filter&gt;false&lt;/filter&gt;\n" + 
				"            &lt;filterSize&gt;0.0&lt;/filterSize&gt;\n" + 
				"        &lt;/ps&gt;\n" + 
				"    &lt;/drift&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;48&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;48&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;52&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;52&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;DinEllipseOnly&gt;false&lt;/DinEllipseOnly&gt;\n" + 
				"&lt;/LeastSquareEstimatorEllipse&gt;</ests>\n" + 
				"        <ests>&lt;LeastSquareEstimatorEllipse&gt;\n" + 
				"    &lt;drift&gt;\n" + 
				"        &lt;g&gt;\n" + 
				"            &lt;dx&gt;0.1&lt;/dx&gt;\n" + 
				"            &lt;Xmin&gt;4.947914298245623&lt;/Xmin&gt;\n" + 
				"            &lt;Xmin&gt;4.953936315789486&lt;/Xmin&gt;\n" + 
				"            &lt;Xmax&gt;15.047914298245624&lt;/Xmax&gt;\n" + 
				"            &lt;Xmax&gt;15.053936315789487&lt;/Xmax&gt;\n" + 
				"        &lt;/g&gt;\n" + 
				"        &lt;dat&gt;48 50 0.280357 1.33607\n" + 
				"48 51 0.757778 -1.93611\n" + 
				"49 48 0.967143 1.57214\n" + 
				"49 49 0.812113 0.602958\n" + 
				"49 50 0.852669 -0.215381\n" + 
				"49 51 1.31581 -1.02267\n" + 
				"50 48 -0.825500 0.558000\n" + 
				"50 49 -0.177070 0.876484\n" + 
				"50 50 -0.0958484 -0.0877617\n" + 
				"50 51 -0.349917 -0.871125\n" + 
				"50 52 -0.409231 -2.18077\n" + 
				"51 48 0.308333 1.76250\n" + 
				"51 49 -0.989545 1.18258\n" + 
				"51 50 -0.903590 -0.0492949\n" + 
				"51 51 -0.845588 -1.04451\n" + 
				"51 52 -1.33667 -0.527778\n" + 
				"52 49 -0.872727 0.545455\n" + 
				"52 50 0.842857 0.310000\n" + 
				"52 52 1.29000 0.230000\n" + 
				"54 52 0.106250 0.593750\n" + 
				"&lt;/dat&gt;\n" + 
				"        &lt;normalized&gt;false&lt;/normalized&gt;\n" + 
				"        &lt;ps&gt;\n" + 
				"            &lt;dx&gt;0.1&lt;/dx&gt;\n" + 
				"            &lt;nPts&gt;5&lt;/nPts&gt;\n" + 
				"            &lt;filter&gt;false&lt;/filter&gt;\n" + 
				"            &lt;filterSize&gt;0.0&lt;/filterSize&gt;\n" + 
				"        &lt;/ps&gt;\n" + 
				"    &lt;/drift&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;48&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;48&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;52&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;52&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;DinEllipseOnly&gt;false&lt;/DinEllipseOnly&gt;\n" + 
				"&lt;/LeastSquareEstimatorEllipse&gt;</ests>\n" + 
				"        <ests>&lt;LeastSquareEstimatorEllipse&gt;\n" + 
				"    &lt;drift&gt;\n" + 
				"        &lt;g&gt;\n" + 
				"            &lt;dx&gt;0.1&lt;/dx&gt;\n" + 
				"            &lt;Xmin&gt;4.947914298245623&lt;/Xmin&gt;\n" + 
				"            &lt;Xmin&gt;4.953936315789486&lt;/Xmin&gt;\n" + 
				"            &lt;Xmax&gt;15.047914298245624&lt;/Xmax&gt;\n" + 
				"            &lt;Xmax&gt;15.053936315789487&lt;/Xmax&gt;\n" + 
				"        &lt;/g&gt;\n" + 
				"        &lt;dat&gt;48 50 0.280357 1.33607\n" + 
				"48 51 0.757778 -1.93611\n" + 
				"49 48 0.967143 1.57214\n" + 
				"49 49 0.812113 0.602958\n" + 
				"49 50 0.852669 -0.215381\n" + 
				"49 51 1.31581 -1.02267\n" + 
				"50 48 -0.825500 0.558000\n" + 
				"50 49 -0.177070 0.876484\n" + 
				"50 50 -0.0958484 -0.0877617\n" + 
				"50 51 -0.349917 -0.871125\n" + 
				"50 52 -0.409231 -2.18077\n" + 
				"51 48 0.308333 1.76250\n" + 
				"51 49 -0.989545 1.18258\n" + 
				"51 50 -0.903590 -0.0492949\n" + 
				"51 51 -0.845588 -1.04451\n" + 
				"51 52 -1.33667 -0.527778\n" + 
				"52 49 -0.872727 0.545455\n" + 
				"52 50 0.842857 0.310000\n" + 
				"52 52 1.29000 0.230000\n" + 
				"54 52 0.106250 0.593750\n" + 
				"&lt;/dat&gt;\n" + 
				"        &lt;normalized&gt;false&lt;/normalized&gt;\n" + 
				"        &lt;ps&gt;\n" + 
				"            &lt;dx&gt;0.1&lt;/dx&gt;\n" + 
				"            &lt;nPts&gt;5&lt;/nPts&gt;\n" + 
				"            &lt;filter&gt;false&lt;/filter&gt;\n" + 
				"            &lt;filterSize&gt;0.0&lt;/filterSize&gt;\n" + 
				"        &lt;/ps&gt;\n" + 
				"    &lt;/drift&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;48&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;48&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;52&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;52&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;DinEllipseOnly&gt;false&lt;/DinEllipseOnly&gt;\n" + 
				"&lt;/LeastSquareEstimatorEllipse&gt;</ests>\n" + 
				"        <ests>&lt;LeastSquareEstimatorEllipse&gt;\n" + 
				"    &lt;drift&gt;\n" + 
				"        &lt;g&gt;\n" + 
				"            &lt;dx&gt;0.1&lt;/dx&gt;\n" + 
				"            &lt;Xmin&gt;4.947914298245623&lt;/Xmin&gt;\n" + 
				"            &lt;Xmin&gt;4.953936315789486&lt;/Xmin&gt;\n" + 
				"            &lt;Xmax&gt;15.047914298245624&lt;/Xmax&gt;\n" + 
				"            &lt;Xmax&gt;15.053936315789487&lt;/Xmax&gt;\n" + 
				"        &lt;/g&gt;\n" + 
				"        &lt;dat&gt;48 50 0.280357 1.33607\n" + 
				"48 51 0.757778 -1.93611\n" + 
				"49 48 0.967143 1.57214\n" + 
				"49 49 0.812113 0.602958\n" + 
				"49 50 0.852669 -0.215381\n" + 
				"49 51 1.31581 -1.02267\n" + 
				"50 48 -0.825500 0.558000\n" + 
				"50 49 -0.177070 0.876484\n" + 
				"50 50 -0.0958484 -0.0877617\n" + 
				"50 51 -0.349917 -0.871125\n" + 
				"50 52 -0.409231 -2.18077\n" + 
				"51 48 0.308333 1.76250\n" + 
				"51 49 -0.989545 1.18258\n" + 
				"51 50 -0.903590 -0.0492949\n" + 
				"51 51 -0.845588 -1.04451\n" + 
				"51 52 -1.33667 -0.527778\n" + 
				"52 49 -0.872727 0.545455\n" + 
				"52 50 0.842857 0.310000\n" + 
				"52 52 1.29000 0.230000\n" + 
				"54 52 0.106250 0.593750\n" + 
				"&lt;/dat&gt;\n" + 
				"        &lt;normalized&gt;false&lt;/normalized&gt;\n" + 
				"        &lt;ps&gt;\n" + 
				"            &lt;dx&gt;0.1&lt;/dx&gt;\n" + 
				"            &lt;nPts&gt;5&lt;/nPts&gt;\n" + 
				"            &lt;filter&gt;false&lt;/filter&gt;\n" + 
				"            &lt;filterSize&gt;0.0&lt;/filterSize&gt;\n" + 
				"        &lt;/ps&gt;\n" + 
				"    &lt;/drift&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;48&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;48&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;52&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;52&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;DinEllipseOnly&gt;false&lt;/DinEllipseOnly&gt;\n" + 
				"&lt;/LeastSquareEstimatorEllipse&gt;</ests>\n" + 
				"        <ests>&lt;LeastSquareEstimatorEllipse&gt;\n" + 
				"    &lt;drift&gt;\n" + 
				"        &lt;g&gt;\n" + 
				"            &lt;dx&gt;0.1&lt;/dx&gt;\n" + 
				"            &lt;Xmin&gt;4.947914298245623&lt;/Xmin&gt;\n" + 
				"            &lt;Xmin&gt;4.953936315789486&lt;/Xmin&gt;\n" + 
				"            &lt;Xmax&gt;15.047914298245624&lt;/Xmax&gt;\n" + 
				"            &lt;Xmax&gt;15.053936315789487&lt;/Xmax&gt;\n" + 
				"        &lt;/g&gt;\n" + 
				"        &lt;dat&gt;48 50 0.280357 1.33607\n" + 
				"48 51 0.757778 -1.93611\n" + 
				"49 48 0.967143 1.57214\n" + 
				"49 49 0.812113 0.602958\n" + 
				"49 50 0.852669 -0.215381\n" + 
				"49 51 1.31581 -1.02267\n" + 
				"50 48 -0.825500 0.558000\n" + 
				"50 49 -0.177070 0.876484\n" + 
				"50 50 -0.0958484 -0.0877617\n" + 
				"50 51 -0.349917 -0.871125\n" + 
				"50 52 -0.409231 -2.18077\n" + 
				"51 48 0.308333 1.76250\n" + 
				"51 49 -0.989545 1.18258\n" + 
				"51 50 -0.903590 -0.0492949\n" + 
				"51 51 -0.845588 -1.04451\n" + 
				"51 52 -1.33667 -0.527778\n" + 
				"52 49 -0.872727 0.545455\n" + 
				"52 50 0.842857 0.310000\n" + 
				"52 52 1.29000 0.230000\n" + 
				"54 52 0.106250 0.593750\n" + 
				"&lt;/dat&gt;\n" + 
				"        &lt;normalized&gt;false&lt;/normalized&gt;\n" + 
				"        &lt;ps&gt;\n" + 
				"            &lt;dx&gt;0.1&lt;/dx&gt;\n" + 
				"            &lt;nPts&gt;5&lt;/nPts&gt;\n" + 
				"            &lt;filter&gt;false&lt;/filter&gt;\n" + 
				"            &lt;filterSize&gt;0.0&lt;/filterSize&gt;\n" + 
				"        &lt;/ps&gt;\n" + 
				"    &lt;/drift&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;48&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;48&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;52&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;52&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;DinEllipseOnly&gt;false&lt;/DinEllipseOnly&gt;\n" + 
				"&lt;/LeastSquareEstimatorEllipse&gt;</ests>\n" + 
				"        <ests>&lt;LeastSquareEstimatorEllipse&gt;\n" + 
				"    &lt;drift&gt;\n" + 
				"        &lt;g&gt;\n" + 
				"            &lt;dx&gt;0.1&lt;/dx&gt;\n" + 
				"            &lt;Xmin&gt;4.947914298245623&lt;/Xmin&gt;\n" + 
				"            &lt;Xmin&gt;4.953936315789486&lt;/Xmin&gt;\n" + 
				"            &lt;Xmax&gt;15.047914298245624&lt;/Xmax&gt;\n" + 
				"            &lt;Xmax&gt;15.053936315789487&lt;/Xmax&gt;\n" + 
				"        &lt;/g&gt;\n" + 
				"        &lt;dat&gt;48 50 0.280357 1.33607\n" + 
				"48 51 0.757778 -1.93611\n" + 
				"49 48 0.967143 1.57214\n" + 
				"49 49 0.812113 0.602958\n" + 
				"49 50 0.852669 -0.215381\n" + 
				"49 51 1.31581 -1.02267\n" + 
				"50 48 -0.825500 0.558000\n" + 
				"50 49 -0.177070 0.876484\n" + 
				"50 50 -0.0958484 -0.0877617\n" + 
				"50 51 -0.349917 -0.871125\n" + 
				"50 52 -0.409231 -2.18077\n" + 
				"51 48 0.308333 1.76250\n" + 
				"51 49 -0.989545 1.18258\n" + 
				"51 50 -0.903590 -0.0492949\n" + 
				"51 51 -0.845588 -1.04451\n" + 
				"51 52 -1.33667 -0.527778\n" + 
				"52 49 -0.872727 0.545455\n" + 
				"52 50 0.842857 0.310000\n" + 
				"52 52 1.29000 0.230000\n" + 
				"54 52 0.106250 0.593750\n" + 
				"&lt;/dat&gt;\n" + 
				"        &lt;normalized&gt;false&lt;/normalized&gt;\n" + 
				"        &lt;ps&gt;\n" + 
				"            &lt;dx&gt;0.1&lt;/dx&gt;\n" + 
				"            &lt;nPts&gt;5&lt;/nPts&gt;\n" + 
				"            &lt;filter&gt;false&lt;/filter&gt;\n" + 
				"            &lt;filterSize&gt;0.0&lt;/filterSize&gt;\n" + 
				"        &lt;/ps&gt;\n" + 
				"    &lt;/drift&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;48&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;48&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;52&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;52&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;DinEllipseOnly&gt;false&lt;/DinEllipseOnly&gt;\n" + 
				"&lt;/LeastSquareEstimatorEllipse&gt;</ests>\n" + 
				"        <ests>&lt;LeastSquareEstimatorEllipse&gt;\n" + 
				"    &lt;drift&gt;\n" + 
				"        &lt;g&gt;\n" + 
				"            &lt;dx&gt;0.1&lt;/dx&gt;\n" + 
				"            &lt;Xmin&gt;4.947914298245623&lt;/Xmin&gt;\n" + 
				"            &lt;Xmin&gt;4.953936315789486&lt;/Xmin&gt;\n" + 
				"            &lt;Xmax&gt;15.047914298245624&lt;/Xmax&gt;\n" + 
				"            &lt;Xmax&gt;15.053936315789487&lt;/Xmax&gt;\n" + 
				"        &lt;/g&gt;\n" + 
				"        &lt;dat&gt;48 50 0.280357 1.33607\n" + 
				"48 51 0.757778 -1.93611\n" + 
				"49 48 0.967143 1.57214\n" + 
				"49 49 0.812113 0.602958\n" + 
				"49 50 0.852669 -0.215381\n" + 
				"49 51 1.31581 -1.02267\n" + 
				"50 48 -0.825500 0.558000\n" + 
				"50 49 -0.177070 0.876484\n" + 
				"50 50 -0.0958484 -0.0877617\n" + 
				"50 51 -0.349917 -0.871125\n" + 
				"50 52 -0.409231 -2.18077\n" + 
				"51 48 0.308333 1.76250\n" + 
				"51 49 -0.989545 1.18258\n" + 
				"51 50 -0.903590 -0.0492949\n" + 
				"51 51 -0.845588 -1.04451\n" + 
				"51 52 -1.33667 -0.527778\n" + 
				"52 49 -0.872727 0.545455\n" + 
				"52 50 0.842857 0.310000\n" + 
				"52 52 1.29000 0.230000\n" + 
				"54 52 0.106250 0.593750\n" + 
				"&lt;/dat&gt;\n" + 
				"        &lt;normalized&gt;false&lt;/normalized&gt;\n" + 
				"        &lt;ps&gt;\n" + 
				"            &lt;dx&gt;0.1&lt;/dx&gt;\n" + 
				"            &lt;nPts&gt;5&lt;/nPts&gt;\n" + 
				"            &lt;filter&gt;false&lt;/filter&gt;\n" + 
				"            &lt;filterSize&gt;0.0&lt;/filterSize&gt;\n" + 
				"        &lt;/ps&gt;\n" + 
				"    &lt;/drift&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;48&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;48&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"        &lt;item&gt;52&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"        &lt;item&gt;49&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"        &lt;item&gt;51&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;nh&gt;\n" + 
				"        &lt;item&gt;52&lt;/item&gt;\n" + 
				"        &lt;item&gt;50&lt;/item&gt;\n" + 
				"    &lt;/nh&gt;\n" + 
				"    &lt;DinEllipseOnly&gt;false&lt;/DinEllipseOnly&gt;\n" + 
				"&lt;/LeastSquareEstimatorEllipse&gt;</ests>\n" + 
				"        <scores>&lt;EmpyWellScore&gt;\n" + 
				"    &lt;value&gt;NaN&lt;/value&gt;\n" + 
				"&lt;/EmpyWellScore&gt;</scores>\n" + 
				"        <scores>&lt;EmpyWellScore&gt;\n" + 
				"    &lt;value&gt;NaN&lt;/value&gt;\n" + 
				"&lt;/EmpyWellScore&gt;</scores>\n" + 
				"        <scores>&lt;EmpyWellScore&gt;\n" + 
				"    &lt;value&gt;NaN&lt;/value&gt;\n" + 
				"&lt;/EmpyWellScore&gt;</scores>\n" + 
				"        <scores>&lt;ParabolicWellScore&gt;\n" + 
				"    &lt;value&gt;0.05181633102800742&lt;/value&gt;\n" + 
				"&lt;/ParabolicWellScore&gt;</scores>\n" + 
				"        <scores>&lt;ParabolicWellScore&gt;\n" + 
				"    &lt;value&gt;0.0556261156585538&lt;/value&gt;\n" + 
				"&lt;/ParabolicWellScore&gt;</scores>\n" + 
				"        <scores>&lt;ParabolicWellScore&gt;\n" + 
				"    &lt;value&gt;0.0556261156585538&lt;/value&gt;\n" + 
				"&lt;/ParabolicWellScore&gt;</scores>\n" + 
				"        <scores>&lt;ParabolicWellScore&gt;\n" + 
				"    &lt;value&gt;0.0556261156585538&lt;/value&gt;\n" + 
				"&lt;/ParabolicWellScore&gt;</scores>\n" + 
				"        <scores>&lt;ParabolicWellScore&gt;\n" + 
				"    &lt;value&gt;0.0556261156585538&lt;/value&gt;\n" + 
				"&lt;/ParabolicWellScore&gt;</scores>\n" + 
				"        <scores>&lt;ParabolicWellScore&gt;\n" + 
				"    &lt;value&gt;0.0556261156585538&lt;/value&gt;\n" + 
				"&lt;/ParabolicWellScore&gt;</scores>\n" + 
				"        <scores>&lt;ParabolicWellScore&gt;\n" + 
				"    &lt;value&gt;0.0556261156585538&lt;/value&gt;\n" + 
				"&lt;/ParabolicWellScore&gt;</scores>\n" + 
				"        <empty>false</empty>\n" + 
				"        <empty>false</empty>\n" + 
				"        <empty>false</empty>\n" + 
				"        <empty>false</empty>\n" + 
				"        <empty>false</empty>\n" + 
				"        <empty>false</empty>\n" + 
				"        <empty>false</empty>\n" + 
				"        <empty>false</empty>\n" + 
				"        <empty>false</empty>\n" + 
				"        <empty>false</empty>\n" + 
				"        <numIt>10</numIt>\n" + 
				"        <itChooser>&lt;IterationChooserBestParabScore&gt;\n" + 
				"    &lt;ps&gt;&amp;lt;IterationChooserBestParabScoreParameters&amp;gt;\n" + 
				"    &amp;lt;chooser&amp;gt;BestParabScore&amp;lt;/chooser&amp;gt;\n" + 
				"    &amp;lt;estPs&amp;gt;&amp;amp;lt;GridEstimatorParameters&amp;amp;gt;\n" + 
				"    &amp;amp;lt;estType&amp;amp;gt;LSQELL&amp;amp;lt;/estType&amp;amp;gt;\n" + 
				"    &amp;amp;lt;dx&amp;amp;gt;0.2&amp;amp;lt;/dx&amp;amp;gt;\n" + 
				"    &amp;amp;lt;driftNptsTh&amp;amp;gt;5&amp;amp;lt;/driftNptsTh&amp;amp;gt;\n" + 
				"    &amp;amp;lt;minCellsTh&amp;amp;gt;4&amp;amp;lt;/minCellsTh&amp;amp;gt;\n" + 
				"    &amp;amp;lt;diffInWell&amp;amp;gt;false&amp;amp;lt;/diffInWell&amp;amp;gt;\n" + 
				"    &amp;amp;lt;correctField&amp;amp;gt;false&amp;amp;lt;/correctField&amp;amp;gt;\n" + 
				"&amp;amp;lt;/GridEstimatorParameters&amp;amp;gt;&amp;lt;/estPs&amp;gt;\n" + 
				"    &amp;lt;angSimTh&amp;gt;0.7&amp;lt;/angSimTh&amp;gt;\n" + 
				"    &amp;lt;sampledRatioTh&amp;gt;0.5&amp;lt;/sampledRatioTh&amp;gt;\n" + 
				"&amp;lt;/IterationChooserBestParabScoreParameters&amp;gt;&lt;/ps&gt;\n" + 
				"&lt;/IterationChooserBestParabScore&gt;</itChooser>\n" + 
				"        <bestIt>3</bestIt>\n" + 
				"    </fitRes>\n" + 
				"    <correctedD>false</correctedD>\n" + 
				"</PotWell>\n", sw.toString());
	}

	@Test
	public void testWell3()
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
		WellEstimatorParameters estPs = new MLEEstimatorParameters(WellEstimator.type.MLE);
		IterationChooser.Parameters itChoosePs =
				new IterationChooser.BestMLEDeltaScore.Parameters(IterationChooser.chooser.BestMLEDelta, estPs, 5);
		HybridWellDetection.Parameters ps =
				new HybridWellDetection.Parameters("Ana", dx, 1, 5, 3, 0.08, 1, 10, 95, estPs, 0, itChoosePs);

		HybridWellDetection hwd = new HybridWellDetection(ps);

		ArrayList<PotWell> res = hwd.detectWells(trajs, null).wells;
		assertEquals(1, res.size());
		PotWell w = res.get(0);

		StringWriter sw = new StringWriter();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(PotWell.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

			jaxbMarshaller.marshal(w, sw);
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}

		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
				"<PotWell>\n" + 
				"    <ell>\n" + 
				"        <mu>9.992215428033878 10.004010442144887</mu>\n" + 
				"        <rad>0.17855366206464351 0.17118505355606606</rad>\n" + 
				"        <phi>-1.3978313920757144</phi>\n" + 
				"    </ell>\n" + 
				"    <A>0.20547805700804866</A>\n" + 
				"    <D>0.04982631794823075</D>\n" + 
				"    <score>&lt;LikelihoodWellScore&gt;\n" + 
				"    &lt;value&gt;1890.1471010628256&lt;/value&gt;\n" + 
				"&lt;/LikelihoodWellScore&gt;</score>\n" + 
				"    <fitRes>\n" + 
				"        <ells>\n" + 
				"            <mu>9.954113689095133 10.046347331786531</mu>\n" + 
				"            <rad>0.12357647110298048 0.11644629539343866</rad>\n" + 
				"            <phi>0.1615747063336756</phi>\n" + 
				"        </ells>\n" + 
				"        <ells>\n" + 
				"            <mu>9.992215428033878 10.004010442144887</mu>\n" + 
				"            <rad>0.17855366206464351 0.17118505355606606</rad>\n" + 
				"            <phi>-1.3978313920757144</phi>\n" + 
				"        </ells>\n" + 
				"        <ells>\n" + 
				"            <mu>9.99547713004485 10.00042511210764</mu>\n" + 
				"            <rad>0.1945571740595408 0.18461077609829613</rad>\n" + 
				"            <phi>-1.5485390314482768</phi>\n" + 
				"        </ells>\n" + 
				"        <ells>\n" + 
				"            <mu>9.995076067615667 10.000677402135247</mu>\n" + 
				"            <rad>0.1983873012463851 0.19235917411511602</rad>\n" + 
				"            <phi>-1.3212650567752728</phi>\n" + 
				"        </ells>\n" + 
				"        <ells>\n" + 
				"            <mu>9.99744283201408 10.003142832014083</mu>\n" + 
				"            <rad>0.2206785695242062 0.2020215840925906</rad>\n" + 
				"            <phi>0.42845091228240656</phi>\n" + 
				"        </ells>\n" + 
				"        <ells>\n" + 
				"            <mu>9.997444512730475 10.003567515364367</mu>\n" + 
				"            <rad>0.22531278234905025 0.20345588337162274</rad>\n" + 
				"            <phi>0.39285105761473293</phi>\n" + 
				"        </ells>\n" + 
				"        <ells>\n" + 
				"            <mu>9.997914298245623 10.003936315789486</mu>\n" + 
				"            <rad>0.23025113401804198 0.20371524221793388</rad>\n" + 
				"            <phi>0.4478363144107288</phi>\n" + 
				"        </ells>\n" + 
				"        <ells>\n" + 
				"            <mu>9.997914298245623 10.003936315789486</mu>\n" + 
				"            <rad>0.23025113401804198 0.20371524221793388</rad>\n" + 
				"            <phi>0.4478363144107288</phi>\n" + 
				"        </ells>\n" + 
				"        <ells>\n" + 
				"            <mu>9.997914298245623 10.003936315789486</mu>\n" + 
				"            <rad>0.23025113401804198 0.20371524221793388</rad>\n" + 
				"            <phi>0.4478363144107288</phi>\n" + 
				"        </ells>\n" + 
				"        <ells>\n" + 
				"            <mu>9.997914298245623 10.003936315789486</mu>\n" + 
				"            <rad>0.23025113401804198 0.20371524221793388</rad>\n" + 
				"            <phi>0.4478363144107288</phi>\n" + 
				"        </ells>\n" + 
				"        <ells>\n" + 
				"            <mu>9.997914298245623 10.003936315789486</mu>\n" + 
				"            <rad>0.23025113401804198 0.20371524221793388</rad>\n" + 
				"            <phi>0.4478363144107288</phi>\n" + 
				"        </ells>\n" + 
				"        <ells>\n" + 
				"            <mu>9.997914298245623 10.003936315789486</mu>\n" + 
				"            <rad>0.23025113401804198 0.20371524221793388</rad>\n" + 
				"            <phi>0.4478363144107288</phi>\n" + 
				"        </ells>\n" + 
				"        <ells>\n" + 
				"            <mu>9.997914298245623 10.003936315789486</mu>\n" + 
				"            <rad>0.23025113401804198 0.20371524221793388</rad>\n" + 
				"            <phi>0.4478363144107288</phi>\n" + 
				"        </ells>\n" + 
				"        <pcaSs>\n" + 
				"            <item>0.002541621277289159</item>\n" + 
				"            <item>0.0022707445917012846</item>\n" + 
				"        </pcaSs>\n" + 
				"        <pcaSs>\n" + 
				"            <item>0.004904132059303424</item>\n" + 
				"            <item>0.005308809485962496</item>\n" + 
				"        </pcaSs>\n" + 
				"        <pcaSs>\n" + 
				"            <item>0.0056890346560288605</item>\n" + 
				"            <item>0.006317914539372519</item>\n" + 
				"        </pcaSs>\n" + 
				"        <pcaSs>\n" + 
				"            <item>0.006200250085680197</item>\n" + 
				"            <item>0.0065454640124792875</item>\n" + 
				"        </pcaSs>\n" + 
				"        <pcaSs>\n" + 
				"            <item>0.007901482873310665</item>\n" + 
				"            <item>0.007039553929648705</item>\n" + 
				"        </pcaSs>\n" + 
				"        <pcaSs>\n" + 
				"            <item>0.008244435055540032</item>\n" + 
				"            <item>0.007138663987757893</item>\n" + 
				"        </pcaSs>\n" + 
				"        <pcaSs>\n" + 
				"            <item>0.008488792956047916</item>\n" + 
				"            <item>0.007287452183078408</item>\n" + 
				"        </pcaSs>\n" + 
				"        <pcaSs>\n" + 
				"            <item>0.008488792956047916</item>\n" + 
				"            <item>0.007287452183078408</item>\n" + 
				"        </pcaSs>\n" + 
				"        <pcaSs>\n" + 
				"            <item>0.008488792956047916</item>\n" + 
				"            <item>0.007287452183078408</item>\n" + 
				"        </pcaSs>\n" + 
				"        <pcaSs>\n" + 
				"            <item>0.008488792956047916</item>\n" + 
				"            <item>0.007287452183078408</item>\n" + 
				"        </pcaSs>\n" + 
				"        <pcaSs>\n" + 
				"            <item>0.008488792956047916</item>\n" + 
				"            <item>0.007287452183078408</item>\n" + 
				"        </pcaSs>\n" + 
				"        <pcaSs>\n" + 
				"            <item>0.008488792956047916</item>\n" + 
				"            <item>0.007287452183078408</item>\n" + 
				"        </pcaSs>\n" + 
				"        <pcaSs>\n" + 
				"            <item>0.008488792956047916</item>\n" + 
				"            <item>0.007287452183078408</item>\n" + 
				"        </pcaSs>\n" + 
				"        <traj_ids>0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 44 45 46 48 49 50 51 52 53 54 55 56 \n" + 
				"0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 \n" + 
				"0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 \n" + 
				"0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 \n" + 
				"0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 \n" + 
				"0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 \n" + 
				"0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 \n" + 
				"0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 \n" + 
				"0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 \n" + 
				"0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 \n" + 
				"0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 \n" + 
				"0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 \n" + 
				"0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 \n" + 
				"</traj_ids>\n" + 
				"        <ests>&lt;MaxLikelihoodEstimator&gt;\n" + 
				"    &lt;Aest&gt;NaN&lt;/Aest&gt;\n" + 
				"    &lt;Dest&gt;NaN&lt;/Dest&gt;\n" + 
				"&lt;/MaxLikelihoodEstimator&gt;</ests>\n" + 
				"        <ests>&lt;MaxLikelihoodEstimator&gt;\n" + 
				"    &lt;lambdaEst&gt;14.669518137112863&lt;/lambdaEst&gt;\n" + 
				"    &lt;lambdaEst&gt;12.087885723718195&lt;/lambdaEst&gt;\n" + 
				"    &lt;DVecEst&gt;0.051144747177020035&lt;/DVecEst&gt;\n" + 
				"    &lt;DVecEst&gt;0.04850788871944148&lt;/DVecEst&gt;\n" + 
				"    &lt;Aest&gt;0.20547805700804866&lt;/Aest&gt;\n" + 
				"    &lt;Dest&gt;0.04982631794823075&lt;/Dest&gt;\n" + 
				"&lt;/MaxLikelihoodEstimator&gt;</ests>\n" + 
				"        <ests>&lt;MaxLikelihoodEstimator&gt;\n" + 
				"    &lt;lambdaEst&gt;12.920763759246048&lt;/lambdaEst&gt;\n" + 
				"    &lt;lambdaEst&gt;11.547141550218925&lt;/lambdaEst&gt;\n" + 
				"    &lt;DVecEst&gt;0.05056646450499877&lt;/DVecEst&gt;\n" + 
				"    &lt;DVecEst&gt;0.049101227084788514&lt;/DVecEst&gt;\n" + 
				"    &lt;Aest&gt;0.2206557161478263&lt;/Aest&gt;\n" + 
				"    &lt;Dest&gt;0.04983384579489364&lt;/Dest&gt;\n" + 
				"&lt;/MaxLikelihoodEstimator&gt;</ests>\n" + 
				"        <ests>&lt;MaxLikelihoodEstimator&gt;\n" + 
				"    &lt;lambdaEst&gt;12.620869421611104&lt;/lambdaEst&gt;\n" + 
				"    &lt;lambdaEst&gt;11.383608073602389&lt;/lambdaEst&gt;\n" + 
				"    &lt;DVecEst&gt;0.050646508016420996&lt;/DVecEst&gt;\n" + 
				"    &lt;DVecEst&gt;0.049276599089267105&lt;/DVecEst&gt;\n" + 
				"    &lt;Aest&gt;0.22948574834934118&lt;/Aest&gt;\n" + 
				"    &lt;Dest&gt;0.049961553552844054&lt;/Dest&gt;\n" + 
				"&lt;/MaxLikelihoodEstimator&gt;</ests>\n" + 
				"        <ests>&lt;MaxLikelihoodEstimator&gt;\n" + 
				"    &lt;lambdaEst&gt;11.598984151233044&lt;/lambdaEst&gt;\n" + 
				"    &lt;lambdaEst&gt;10.98693555533375&lt;/lambdaEst&gt;\n" + 
				"    &lt;DVecEst&gt;0.050457326034145315&lt;/DVecEst&gt;\n" + 
				"    &lt;DVecEst&gt;0.04908628358494465&lt;/DVecEst&gt;\n" + 
				"    &lt;Aest&gt;0.25331650465041894&lt;/Aest&gt;\n" + 
				"    &lt;Dest&gt;0.04977180480954498&lt;/Dest&gt;\n" + 
				"&lt;/MaxLikelihoodEstimator&gt;</ests>\n" + 
				"        <ests>&lt;MaxLikelihoodEstimator&gt;\n" + 
				"    &lt;Aest&gt;NaN&lt;/Aest&gt;\n" + 
				"    &lt;Dest&gt;NaN&lt;/Dest&gt;\n" + 
				"&lt;/MaxLikelihoodEstimator&gt;</ests>\n" + 
				"        <ests>&lt;MaxLikelihoodEstimator&gt;\n" + 
				"    &lt;Aest&gt;NaN&lt;/Aest&gt;\n" + 
				"    &lt;Dest&gt;NaN&lt;/Dest&gt;\n" + 
				"&lt;/MaxLikelihoodEstimator&gt;</ests>\n" + 
				"        <ests>&lt;MaxLikelihoodEstimator&gt;\n" + 
				"    &lt;Aest&gt;NaN&lt;/Aest&gt;\n" + 
				"    &lt;Dest&gt;NaN&lt;/Dest&gt;\n" + 
				"&lt;/MaxLikelihoodEstimator&gt;</ests>\n" + 
				"        <ests>&lt;MaxLikelihoodEstimator&gt;\n" + 
				"    &lt;Aest&gt;NaN&lt;/Aest&gt;\n" + 
				"    &lt;Dest&gt;NaN&lt;/Dest&gt;\n" + 
				"&lt;/MaxLikelihoodEstimator&gt;</ests>\n" + 
				"        <ests>&lt;MaxLikelihoodEstimator&gt;\n" + 
				"    &lt;Aest&gt;NaN&lt;/Aest&gt;\n" + 
				"    &lt;Dest&gt;NaN&lt;/Dest&gt;\n" + 
				"&lt;/MaxLikelihoodEstimator&gt;</ests>\n" + 
				"        <ests>&lt;MaxLikelihoodEstimator&gt;\n" + 
				"    &lt;Aest&gt;NaN&lt;/Aest&gt;\n" + 
				"    &lt;Dest&gt;NaN&lt;/Dest&gt;\n" + 
				"&lt;/MaxLikelihoodEstimator&gt;</ests>\n" + 
				"        <ests>&lt;MaxLikelihoodEstimator&gt;\n" + 
				"    &lt;Aest&gt;NaN&lt;/Aest&gt;\n" + 
				"    &lt;Dest&gt;NaN&lt;/Dest&gt;\n" + 
				"&lt;/MaxLikelihoodEstimator&gt;</ests>\n" + 
				"        <ests>&lt;MaxLikelihoodEstimator&gt;\n" + 
				"    &lt;Aest&gt;NaN&lt;/Aest&gt;\n" + 
				"    &lt;Dest&gt;NaN&lt;/Dest&gt;\n" + 
				"&lt;/MaxLikelihoodEstimator&gt;</ests>\n" + 
				"        <scores>&lt;EmpyWellScore&gt;\n" + 
				"    &lt;value&gt;NaN&lt;/value&gt;\n" + 
				"&lt;/EmpyWellScore&gt;</scores>\n" + 
				"        <scores>&lt;LikelihoodWellScore&gt;\n" + 
				"    &lt;value&gt;1890.1471010628256&lt;/value&gt;\n" + 
				"&lt;/LikelihoodWellScore&gt;</scores>\n" + 
				"        <scores>&lt;LikelihoodWellScore&gt;\n" + 
				"    &lt;value&gt;1977.0027314034521&lt;/value&gt;\n" + 
				"&lt;/LikelihoodWellScore&gt;</scores>\n" + 
				"        <scores>&lt;LikelihoodWellScore&gt;\n" + 
				"    &lt;value&gt;1995.0333646748466&lt;/value&gt;\n" + 
				"&lt;/LikelihoodWellScore&gt;</scores>\n" + 
				"        <scores>&lt;LikelihoodWellScore&gt;\n" + 
				"    &lt;value&gt;2034.0575833931703&lt;/value&gt;\n" + 
				"&lt;/LikelihoodWellScore&gt;</scores>\n" + 
				"        <scores>&lt;EmpyWellScore&gt;\n" + 
				"    &lt;value&gt;NaN&lt;/value&gt;\n" + 
				"&lt;/EmpyWellScore&gt;</scores>\n" + 
				"        <scores>&lt;EmpyWellScore&gt;\n" + 
				"    &lt;value&gt;NaN&lt;/value&gt;\n" + 
				"&lt;/EmpyWellScore&gt;</scores>\n" + 
				"        <scores>&lt;EmpyWellScore&gt;\n" + 
				"    &lt;value&gt;NaN&lt;/value&gt;\n" + 
				"&lt;/EmpyWellScore&gt;</scores>\n" + 
				"        <scores>&lt;EmpyWellScore&gt;\n" + 
				"    &lt;value&gt;NaN&lt;/value&gt;\n" + 
				"&lt;/EmpyWellScore&gt;</scores>\n" + 
				"        <scores>&lt;EmpyWellScore&gt;\n" + 
				"    &lt;value&gt;NaN&lt;/value&gt;\n" + 
				"&lt;/EmpyWellScore&gt;</scores>\n" + 
				"        <scores>&lt;EmpyWellScore&gt;\n" + 
				"    &lt;value&gt;NaN&lt;/value&gt;\n" + 
				"&lt;/EmpyWellScore&gt;</scores>\n" + 
				"        <scores>&lt;EmpyWellScore&gt;\n" + 
				"    &lt;value&gt;NaN&lt;/value&gt;\n" + 
				"&lt;/EmpyWellScore&gt;</scores>\n" + 
				"        <scores>&lt;EmpyWellScore&gt;\n" + 
				"    &lt;value&gt;NaN&lt;/value&gt;\n" + 
				"&lt;/EmpyWellScore&gt;</scores>\n" + 
				"        <empty>false</empty>\n" + 
				"        <empty>false</empty>\n" + 
				"        <empty>false</empty>\n" + 
				"        <empty>false</empty>\n" + 
				"        <empty>false</empty>\n" + 
				"        <empty>false</empty>\n" + 
				"        <empty>false</empty>\n" + 
				"        <empty>false</empty>\n" + 
				"        <empty>false</empty>\n" + 
				"        <empty>false</empty>\n" + 
				"        <empty>false</empty>\n" + 
				"        <empty>false</empty>\n" + 
				"        <empty>false</empty>\n" + 
				"        <numIt>13</numIt>\n" + 
				"        <itChooser>&lt;IterationChooserBestMLEDeltaScore&gt;\n" + 
				"    &lt;ps&gt;&amp;lt;IterationChooserBestMLEDeltaScoreParameters&amp;gt;\n" + 
				"    &amp;lt;chooser&amp;gt;BestMLEDelta&amp;lt;/chooser&amp;gt;\n" + 
				"    &amp;lt;estPs&amp;gt;&amp;amp;lt;MLEEstimatorParameters&amp;amp;gt;\n" + 
				"    &amp;amp;lt;estType&amp;amp;gt;MLE&amp;amp;lt;/estType&amp;amp;gt;\n" + 
				"&amp;amp;lt;/MLEEstimatorParameters&amp;amp;gt;&amp;lt;/estPs&amp;gt;\n" + 
				"    &amp;lt;minSlicePtsTh&amp;gt;5&amp;lt;/minSlicePtsTh&amp;gt;\n" + 
				"&amp;lt;/IterationChooserBestMLEDeltaScoreParameters&amp;gt;&lt;/ps&gt;\n" + 
				"&lt;/IterationChooserBestMLEDeltaScore&gt;</itChooser>\n" + 
				"        <bestIt>1</bestIt>\n" + 
				"    </fitRes>\n" + 
				"    <correctedD>false</correctedD>\n" + 
				"</PotWell>\n", sw.toString());
	}

	@Test
	public void testWellLinker()
	{
		XMLAdapters.Wells wAdapt = new XMLAdapters.Wells();

		HashMap<String, HashMap<WellDetectionParameters, PotWellsWindows>> dat = new HashMap<> ();
		dat.put(HybridWellDetection.name, new HashMap<> ());

		{
			GridEstimatorParameters estPs = new GridEstimatorParameters(WellEstimator.type.LSQELL, 0.2,
					10, 5, false, false);
			IterationChooser.BestParabScore.Parameters itChoosePs =
					new IterationChooser.BestParabScore.Parameters(IterationChooser.chooser.BestParabScore, estPs, 0.7, 0.5);
			HybridWellDetection.Parameters psMult =
					new HybridWellDetection.Parameters("Ana", 0.2, 1, 5.0, 3, 0.2, 1.0, 20, 95, estPs, 0, itChoosePs);

			PotWells pws1 = new PotWells();
			pws1.wells.add(new PotWell(new Ellipse(new double[] {10.0, 11.0}, new double[] {12.0, 13.0}, 14.0),
									   0.7, 0.07, new WellScore.Parabolic(0.13)));
			PotWells pws2 = new PotWells();
			pws2.wells.add(new PotWell(new Ellipse(new double[] {10.01, 11.05}, new double[] {17.0, 18.0}, 19.0),
									   0.8, 0.08, new WellScore.Parabolic(0.14)));
			PotWellsWindows pwws = new PotWellsWindows();
			pwws.wins.add(pws1);
			pwws.wins.add(pws2);
			pwws.linkWells(new DistanceWellLinker(1, 0.5));
			dat.get(HybridWellDetection.name).put(psMult, pwws);
		}

		HashMap<String, HashMap<WellDetectionParameters, PotWellsWindows>> res = null;
		try {
			String v = wAdapt.marshal(dat);
			res = wAdapt.unmarshal(v);
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertEquals(1, res.keySet().size());
		assertTrue(res.keySet().contains(HybridWellDetection.name));

		assertEquals(1, res.get(HybridWellDetection.name).keySet().size());


		HybridWellDetection.Parameters psHyb =
				(HybridWellDetection.Parameters) res.get(HybridWellDetection.name).keySet().iterator().next();

		{
			PotWellsWindows pwws = res.get(HybridWellDetection.name).get(psHyb);
			
			assertEquals(1, pwws.linker().maxFrameGap());
			assertEquals(0.5, pwws.linker().maxDist(), 1e-2);

			ArrayList<ArrayList<WindowIndex>> links = pwws.links();
			assertEquals(1, links.size());
			assertEquals(2, links.get(0).size());

			assertEquals(0, links.get(0).get(0).wellIdx);
			assertEquals(0, links.get(0).get(0).winIdx);

			assertEquals(0, links.get(0).get(1).wellIdx);
			assertEquals(1, links.get(0).get(1).winIdx);

			assertEquals(2, pwws.wins.size());
			assertEquals(1, pwws.wins.get(0).wells.size());
			assertEquals(0.13, pwws.wins.get(0).wells.get(0).score().value(), 1e-5);
			assertEquals(1, pwws.wins.get(1).wells.size());
			assertEquals(0.14, pwws.wins.get(1).wells.get(0).score().value(), 1e-5);
		}
	}
}
