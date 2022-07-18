package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import fiji.plugin.SPTAnalysis.struct.Ring;

public class RingTest
{
	@Test
	public void test()
	{
		ArrayList<double[]> pts = new ArrayList<> ();
		pts.add(new double[] {10.0, 10.0});
		pts.add(new double[] {10.19, 10.0});
		pts.add(new double[] {10.20, 10.0});
		pts.add(new double[] {10.21, 10.0});
		pts.add(new double[] {10.22, 10.0});
		pts.add(new double[] {10.23, 10.0});

		Ring ring = new Ring(new double[] {10.0, 10.0}, new double[] {0.20, 0.22});

		assertFalse(ring.inside(pts.get(0)));
		assertFalse(ring.inside(pts.get(1)));
		assertTrue(ring.inside(pts.get(2)));
		assertTrue(ring.inside(pts.get(3)));
		assertTrue(ring.inside(pts.get(4)));
		assertFalse(ring.inside(pts.get(5)));
	}
}
