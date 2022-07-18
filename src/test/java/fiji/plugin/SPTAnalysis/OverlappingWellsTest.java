package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.*;

import org.junit.Test;

import fiji.plugin.SPTAnalysis.struct.Ellipse;


public class OverlappingWellsTest
{
	@Test
	public void test1()
	{
		Ellipse e1 = new Ellipse(new double[] {0.0, 0.0}, new double[] {1.0, 1.0}, 0.0);
		Ellipse e2 = new Ellipse(new double[] {10.0, 10.0}, new double[] {1.0, 1.0}, 0.0);
		assertTrue(!e1.intersect(e2));
	}

	@Test
	public void test2()
	{
		Ellipse e1 = new Ellipse(new double[] {0.0, 0.0}, new double[] {1.0, 1.0}, 0.0);
		Ellipse e2 = new Ellipse(new double[] {0.2, 0.2}, new double[] {1.0, 1.0}, 0.0);
		assertTrue(e1.intersect(e2));
	}

	@Test
	public void test3()
	{
		Ellipse e1 = new Ellipse(new double[] {0.0, 0.0}, new double[] {1.0, 1.0}, 0.0);
		Ellipse e2 = new Ellipse(new double[] {2.0, 2.0}, new double[] {1.0, 1.0}, 0.0);
		assertTrue(!e1.intersect(e2));
	}

	@Test
	public void test4()
	{
		Ellipse e1 = new Ellipse(new double[] {0.0, 0.0}, new double[] {1.0, 1.0}, 0.0);
		Ellipse e2 = new Ellipse(new double[] {1.9, 1.9}, new double[] {1.0, 1.0}, 0.0);
		assertTrue(!e1.intersect(e2));
	}

}
