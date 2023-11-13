package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fiji.plugin.SPTAnalysis.readers.CSVReaderOptions;
import fiji.plugin.SPTAnalysis.readers.TrajectoryCSVReader;
import fiji.plugin.SPTAnalysis.struct.MapParameters;
import fiji.plugin.SPTAnalysis.struct.Rectangle;
import fiji.plugin.SPTAnalysis.struct.SquareGrid;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsembleWindows;
import fiji.plugin.SPTAnalysis.struct.VectorMap;
import fiji.plugin.SPTAnalysis.writers.SVGVectorMapWriter;

public class SVGVectorMapTest
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

		double dx = 0.1;
		SquareGrid g = new SquareGrid(0.1, new double[] {10.0, 10.0}, 6);
		VectorMap drift = VectorMap.genDriftMap(g, trajs,
				new MapParameters.DriftParameters(g.dx(), 3, false, 0));

		String res = (new SVGVectorMapWriter(drift, dx/2, 100)).generate();
		assertEquals("<g>\n" +
				"<defs><marker id=\"arrow1\" markerWidth=\"0.025\" markerHeight=\"10\" refX=\"0\" refY=\"3\" orient=\"auto\" markerUnits=\"strokeWidth\"><path d=\"M0,0 L0,6 L9,3 z\" fill=\"rgb(255,0,255)\"/></marker></defs>\n" + 
				"<defs><marker id=\"arrow2\" markerWidth=\"0.025\" markerHeight=\"10\" refX=\"0\" refY=\"3\" orient=\"auto\" markerUnits=\"strokeWidth\"><path d=\"M0,0 L0,6 L9,3 z\" fill=\"rgb(255,0,0)\"/></marker></defs>\n" + 
				"<defs><marker id=\"arrow3\" markerWidth=\"0.025\" markerHeight=\"10\" refX=\"0\" refY=\"3\" orient=\"auto\" markerUnits=\"strokeWidth\"><path d=\"M0,0 L0,6 L9,3 z\" fill=\"rgb(0,255,0)\"/></marker></defs>\n" + 
				"<defs><marker id=\"arrow4\" markerWidth=\"0.025\" markerHeight=\"10\" refX=\"0\" refY=\"3\" orient=\"auto\" markerUnits=\"strokeWidth\"><path d=\"M0,0 L0,6 L9,3 z\" fill=\"rgb(0,255,255)\"/></marker></defs>\n" + 
				"<!--dx=0.100_minNpts=3\n" +
				"-->\n" +
				"<line x1=\"25.0000\" y1=\"85.0000\" x2=\"27.6250\" y2=\"88.1667\" style=\"stroke:rgb(0,255,255);stroke-width:1px;marker-end:url(#arrow4)\"/>\n" + 
				"<line x1=\"35.0000\" y1=\"75.0000\" x2=\"35.2000\" y2=\"85.0000\" style=\"stroke:rgb(0,255,255);stroke-width:1px;marker-end:url(#arrow4)\"/>\n" + 
				"<line x1=\"45.0000\" y1=\"65.0000\" x2=\"48.3521\" y2=\"72.9604\" style=\"stroke:rgb(0,255,255);stroke-width:1px;marker-end:url(#arrow4)\"/>\n" + 
				"<line x1=\"45.0000\" y1=\"75.0000\" x2=\"45.1775\" y2=\"67.5625\" style=\"stroke:rgb(255,0,0);stroke-width:1px;marker-end:url(#arrow2)\"/>\n" + 
				"<line x1=\"55.0000\" y1=\"45.0000\" x2=\"59.8357\" y2=\"52.8607\" style=\"stroke:rgb(0,255,255);stroke-width:1px;marker-end:url(#arrow4)\"/>\n" + 
				"<line x1=\"55.0000\" y1=\"55.0000\" x2=\"59.2205\" y2=\"58.2896\" style=\"stroke:rgb(0,255,0);stroke-width:1px;marker-end:url(#arrow3)\"/>\n" + 
				"<line x1=\"55.0000\" y1=\"65.0000\" x2=\"59.0828\" y2=\"64.2889\" style=\"stroke:rgb(0,255,0);stroke-width:1px;marker-end:url(#arrow3)\"/>\n" + 
				"<line x1=\"55.0000\" y1=\"75.0000\" x2=\"60.9786\" y2=\"69.3204\" style=\"stroke:rgb(0,255,0);stroke-width:1px;marker-end:url(#arrow3)\"/>\n" + 
				"<line x1=\"55.0000\" y1=\"85.0000\" x2=\"60.3750\" y2=\"90.0625\" style=\"stroke:rgb(0,255,0);stroke-width:1px;marker-end:url(#arrow3)\"/>\n" + 
				"<line x1=\"65.0000\" y1=\"45.0000\" x2=\"61.9708\" y2=\"47.2396\" style=\"stroke:rgb(255,0,255);stroke-width:1px;marker-end:url(#arrow1)\"/>\n" + 
				"<line x1=\"65.0000\" y1=\"55.0000\" x2=\"64.3300\" y2=\"59.5137\" style=\"stroke:rgb(0,255,255);stroke-width:1px;marker-end:url(#arrow4)\"/>\n" + 
				"<line x1=\"65.0000\" y1=\"65.0000\" x2=\"64.1509\" y2=\"64.4070\" style=\"stroke:rgb(255,0,255);stroke-width:1px;marker-end:url(#arrow1)\"/>\n" + 
				"<line x1=\"65.0000\" y1=\"75.0000\" x2=\"63.9750\" y2=\"71.4439\" style=\"stroke:rgb(255,0,0);stroke-width:1px;marker-end:url(#arrow2)\"/>\n" + 
				"<line x1=\"65.0000\" y1=\"85.0000\" x2=\"62.8600\" y2=\"75.3833\" style=\"stroke:rgb(255,0,0);stroke-width:1px;marker-end:url(#arrow2)\"/>\n" + 
				"<line x1=\"75.0000\" y1=\"45.0000\" x2=\"74.7500\" y2=\"53.8700\" style=\"stroke:rgb(0,255,255);stroke-width:1px;marker-end:url(#arrow4)\"/>\n" + 
				"<line x1=\"75.0000\" y1=\"55.0000\" x2=\"69.7280\" y2=\"60.8059\" style=\"stroke:rgb(0,255,255);stroke-width:1px;marker-end:url(#arrow4)\"/>\n" + 
				"<line x1=\"75.0000\" y1=\"65.0000\" x2=\"71.1377\" y2=\"65.3318\" style=\"stroke:rgb(255,0,255);stroke-width:1px;marker-end:url(#arrow1)\"/>\n" + 
				"<line x1=\"75.0000\" y1=\"75.0000\" x2=\"69.6142\" y2=\"70.7892\" style=\"stroke:rgb(255,0,255);stroke-width:1px;marker-end:url(#arrow1)\"/>\n" + 
				"<line x1=\"75.0000\" y1=\"85.0000\" x2=\"70.2333\" y2=\"80.4722\" style=\"stroke:rgb(255,0,255);stroke-width:1px;marker-end:url(#arrow1)\"/>\n" + 
				"<line x1=\"85.0000\" y1=\"55.0000\" x2=\"81.4000\" y2=\"58.3850\" style=\"stroke:rgb(255,0,255);stroke-width:1px;marker-end:url(#arrow1)\"/>\n" + 
				"<line x1=\"85.0000\" y1=\"65.0000\" x2=\"87.1875\" y2=\"65.8750\" style=\"stroke:rgb(0,255,0);stroke-width:1px;marker-end:url(#arrow3)\"/>\n" + 
				"<line x1=\"85.0000\" y1=\"75.0000\" x2=\"71.0625\" y2=\"70.1500\" style=\"stroke:rgb(255,0,255);stroke-width:1px;marker-end:url(#arrow1)\"/>\n" + 
				"<line x1=\"85.0000\" y1=\"85.0000\" x2=\"91.4500\" y2=\"86.1500\" style=\"stroke:rgb(0,255,0);stroke-width:1px;marker-end:url(#arrow3)\"/>\n" + 
				"<line x1=\"105.000\" y1=\"85.0000\" x2=\"105.531\" y2=\"87.9688\" style=\"stroke:rgb(0,255,255);stroke-width:1px;marker-end:url(#arrow4)\"/>\n" + 
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

		double dx = 0.1;
		SquareGrid g = new SquareGrid(0.1, new double[] {10.0, 10.0}, 6);
		VectorMap drift = VectorMap.genDriftMap(g, trajs,
				new MapParameters.DriftParameters(g.dx(), 3, false, 0));

		Rectangle bnds = new Rectangle(new double[] {0.0, 0.0}, trajsw.maxCoords());

		String res1 = (new SVGVectorMapWriter(drift, dx / 2, 100)).generate();
		String res2 = (new SVGVectorMapWriter(drift, dx / 2, 100, Utils.squaresInReg(g, bnds))).generate();
		assertEquals(res1, res2);
	}
}
