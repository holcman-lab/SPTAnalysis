package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fiji.plugin.SPTAnalysis.readers.CSVReaderOptions;
import fiji.plugin.SPTAnalysis.readers.TrajectoryCSVReader;
import fiji.plugin.SPTAnalysis.struct.MapParameters;
import fiji.plugin.SPTAnalysis.struct.Rectangle;
import fiji.plugin.SPTAnalysis.struct.ScalarMap;
import fiji.plugin.SPTAnalysis.struct.SquareGrid;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsembleWindows;
import fiji.plugin.SPTAnalysis.writers.SVGScalarMapWriter;

public class SVGScalarMapTest
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

		SquareGrid g = new SquareGrid(0.1, new double[] {10.0, 10.0}, 6);
		ScalarMap dens = ScalarMap.genDensityMap(g, trajs,
				new MapParameters.DensityParameters(g.dx(), ScalarMap.DensityOption.NPTS, 0));

		String res = (new SVGScalarMapWriter(dens, 100)).generate();
		assertEquals("<g>\n" +
				"<!--\n" +
				"max: 283.000, dx=0.100_type=NPTS_meanNhSize=0\n" +
				"-->\n" +
				"<rect x=\"10.0000\" y=\"80.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,2,255)\"/>\n" + 
				"<rect x=\"20.0000\" y=\"80.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,8,255)\"/>\n" + 
				"<rect x=\"30.0000\" y=\"70.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,8,255)\"/>\n" + 
				"<rect x=\"30.0000\" y=\"80.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,8,255)\"/>\n" + 
				"<rect x=\"40.0000\" y=\"40.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,2,255)\"/>\n" + 
				"<rect x=\"40.0000\" y=\"50.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,5,255)\"/>\n" + 
				"<rect x=\"40.0000\" y=\"60.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,33,255)\"/>\n" + 
				"<rect x=\"40.0000\" y=\"70.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,31,255)\"/>\n" + 
				"<rect x=\"40.0000\" y=\"80.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,5,255)\"/>\n" + 
				"<rect x=\"50.0000\" y=\"30.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,2,255)\"/>\n" + 
				"<rect x=\"50.0000\" y=\"40.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,19,255)\"/>\n" + 
				"<rect x=\"50.0000\" y=\"50.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,195,255)\"/>\n" + 
				"<rect x=\"50.0000\" y=\"60.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(89,255,166)\"/>\n" + 
				"<rect x=\"50.0000\" y=\"70.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,143,255)\"/>\n" + 
				"<rect x=\"50.0000\" y=\"80.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,14,255)\"/>\n" + 
				"<rect x=\"50.0000\" y=\"90.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,2,255)\"/>\n" + 
				"<rect x=\"60.0000\" y=\"30.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,2,255)\"/>\n" + 
				"<rect x=\"60.0000\" y=\"40.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,36,255)\"/>\n" + 
				"<rect x=\"60.0000\" y=\"50.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(81,255,174)\"/>\n" + 
				"<rect x=\"60.0000\" y=\"60.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(255,0,0)\"/>\n" + 
				"<rect x=\"60.0000\" y=\"70.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(137,255,118)\"/>\n" + 
				"<rect x=\"60.0000\" y=\"80.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,42,255)\"/>\n" + 
				"<rect x=\"70.0000\" y=\"40.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,14,255)\"/>\n" + 
				"<rect x=\"70.0000\" y=\"50.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,174,255)\"/>\n" + 
				"<rect x=\"70.0000\" y=\"60.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,232,255)\"/>\n" + 
				"<rect x=\"70.0000\" y=\"70.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,145,255)\"/>\n" + 
				"<rect x=\"70.0000\" y=\"80.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,25,255)\"/>\n" + 
				"<rect x=\"80.0000\" y=\"40.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,2,255)\"/>\n" + 
				"<rect x=\"80.0000\" y=\"50.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,28,255)\"/>\n" + 
				"<rect x=\"80.0000\" y=\"60.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,22,254)\"/>\n" + 
				"<rect x=\"80.0000\" y=\"70.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,14,255)\"/>\n" + 
				"<rect x=\"80.0000\" y=\"80.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,14,255)\"/>\n" + 
				"<rect x=\"90.0000\" y=\"60.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,2,255)\"/>\n" + 
				"<rect x=\"90.0000\" y=\"70.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,2,255)\"/>\n" + 
				"<rect x=\"90.0000\" y=\"80.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,2,255)\"/>\n" + 
				"<rect x=\"100.000\" y=\"80.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,22,254)\"/>\n" + 
				"<rect x=\"100.000\" y=\"90.0000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,5,255)\"/>\n" + 
				"<rect x=\"100.000\" y=\"100.000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,2,255)\"/>\n" + 
				"<rect x=\"110.000\" y=\"100.000\" width=\"10.0000\" height=\"10.0000\" style=\"fill:rgb(0,2,255)\"/>\n" + 
				"</g>\n", res);
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

		TrajectoryEnsembleWindows trajsw = new TrajectoryEnsembleWindows();
		trajsw.wins.add(trajs);

		SquareGrid g = new SquareGrid(0.1, new double[] {10.0, 10.0}, 6);
		ScalarMap dens = ScalarMap.genDensityMap(g, trajs,
				new MapParameters.DensityParameters(g.dx(), ScalarMap.DensityOption.NPTS, 0));

		Rectangle bnds = new Rectangle(new double[] {0.0, 0.0}, trajsw.maxCoords());

		String res1 = (new SVGScalarMapWriter(dens, 100)).generate();
		String res2 = (new SVGScalarMapWriter(dens, 100, Utils.squaresInReg(g, bnds))).generate();
		assertEquals(res1, res2);
	}
}
