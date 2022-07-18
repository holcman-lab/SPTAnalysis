package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.Rectangle;
import fiji.plugin.SPTAnalysis.struct.SquareGrid;


public class SquaresInEllipseTest
{
	@Test
	public void test()
	{
		SquareGrid g = new SquareGrid(0.2, new double[] {10.0, 10.0}, 7);

		Ellipse ell = new Ellipse(new double[] {10.0, 10.0}, new double[] {0.15, 0.15}, 0);

		ArrayList<int[]> nh = Utils.squaresInReg(g, ell);

		assertEquals(1, nh.size());
		assertArrayEquals(new int[] {7, 7}, nh.get(0));

		ell = new Ellipse(new double[] {10.0, 10.0}, new double[] {0.3, 0.21}, 0);
		nh = Utils.squaresInReg(g, ell);

		assertEquals(5, nh.size());
		assertTrue(TestUtils.pointInList(nh, new int[] {7,7}));
		assertTrue(TestUtils.pointInList(nh, new int[] {6,7}));
		assertTrue(TestUtils.pointInList(nh, new int[] {8,7}));
		assertTrue(TestUtils.pointInList(nh, new int[] {7,6}));
		assertTrue(TestUtils.pointInList(nh, new int[] {7,8}));
	}

	@Test
	public void testRotate()
	{
		SquareGrid g = new SquareGrid(0.2, new double[] {10.0, 10.0}, 7);

		Ellipse ell = new Ellipse(new double[] {10.0, 10.0}, new double[] {0.3, 0.21}, Math.PI/5);
		ArrayList<int[]> nh = Utils.squaresInReg(g, ell);

		assertEquals(7, nh.size());
		assertTrue(TestUtils.pointInList(nh, new int[] {7,7}));
		assertTrue(TestUtils.pointInList(nh, new int[] {6,7}));
		assertTrue(TestUtils.pointInList(nh, new int[] {8,7}));
		assertTrue(TestUtils.pointInList(nh, new int[] {7,6}));
		assertTrue(TestUtils.pointInList(nh, new int[] {7,8}));
		assertTrue(TestUtils.pointInList(nh, new int[] {6,6}));
		assertTrue(TestUtils.pointInList(nh, new int[] {8,8}));
	}

	@Test
	public void test2()
	{
		SquareGrid g = new SquareGrid(0.2, new double[] {10.0, 10.0}, 7);

		Rectangle reg = new Rectangle(new double[] {10.0, 10.0}, new double[] {12.0, 12.0});
		ArrayList<int[]> nh = Utils.squaresInReg(g, reg);
		assertEquals(64, nh.size());

		reg = new Rectangle(new double[] {10.0, 10.0}, new double[] {11.0, 11.0});
		nh = Utils.squaresInReg(g, reg);
		assertEquals(36, nh.size());

		reg = new Rectangle(new double[] {10.1, 10.1}, new double[] {10.9, 10.9});
		nh = Utils.squaresInReg(g, reg);
		assertEquals(16, nh.size());

	}
}
