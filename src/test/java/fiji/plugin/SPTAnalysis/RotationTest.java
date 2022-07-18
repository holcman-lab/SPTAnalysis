package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.*;

import org.junit.Test;


public class RotationTest
{
	@Test
	public void test()
	{
		double[] pt = new double[] {10, 10};

		assertArrayEquals(pt, Utils.rot_point(pt, 0.0), 1e-5);

		assertArrayEquals(new double[] {-pt[0], pt[0]}, Utils.rot_point(pt, -Math.PI/2), 1e-5);
		assertArrayEquals(new double[] {-pt[0], -pt[0]}, Utils.rot_point(pt, -Math.PI), 1e-5);
		assertArrayEquals(new double[] {pt[0], -pt[0]}, Utils.rot_point(pt, -3*Math.PI/2), 1e-5);
		assertArrayEquals(pt, Utils.rot_point(pt, -2*Math.PI), 1e-5);

		assertArrayEquals(new double[] {pt[0], -pt[0]}, Utils.rot_point(pt, Math.PI/2), 1e-5);
		assertArrayEquals(new double[] {-pt[0], -pt[0]}, Utils.rot_point(pt, Math.PI), 1e-5);
		assertArrayEquals(new double[] {-pt[0], pt[0]}, Utils.rot_point(pt, 3*Math.PI/2), 1e-5);
		assertArrayEquals(pt, Utils.rot_point(pt, 2*Math.PI), 1e-5);
	}

}
