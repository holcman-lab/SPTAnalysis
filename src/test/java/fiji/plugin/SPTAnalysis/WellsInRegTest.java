package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.*;

import java.util.ArrayList;
import org.junit.Test;

import fiji.plugin.SPTAnalysis.estimators.WellScore;
import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.PotWell;
import fiji.plugin.SPTAnalysis.struct.Rectangle;

public class WellsInRegTest
{
	@Test
	public void test()
	{
		Rectangle reg = new Rectangle(new double[] {0.0, 0.0}, new double[] {5.0, 5.0});

		ArrayList<PotWell> wells = new ArrayList<>();
		wells.add(new PotWell(new Ellipse(new double[] {0.1, 0.1}, new double[] {1, 1}, 0),
				  0.1, 0.05, new WellScore.Parabolic(0.2)));
		wells.add(new PotWell(new Ellipse(new double[] {1.1, 1.1}, new double[] {1, 1}, 0),
				  0.1, 0.05, new WellScore.Parabolic(0.2)));
		wells.add(new PotWell(new Ellipse(new double[] {6, 5.1}, new double[] {1, 1}, 0),
				  0.1, 0.05, new WellScore.Parabolic(0.2)));
		wells.add(new PotWell(new Ellipse(new double[] {0.0, 0.0}, new double[] {1, 1}, 0),
				  0.1, 0.05, new WellScore.Parabolic(0.2)));

		assertEquals(3, Utils.wellsInReg(wells, reg).size());
	}
}
