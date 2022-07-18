package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.*;

import java.util.ArrayList;
import org.junit.Test;

import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.Rectangle;

public class ShapesInRegTest
{
	@Test
	public void test()
	{
		Rectangle reg = new Rectangle(new double[] {0.0, 0.0}, new double[] {5.0, 5.0});

		ArrayList<Ellipse> ells = new ArrayList<>();
		ells.add(new Ellipse(new double[] {0.1, 0.1}, new double[] {1, 1}, 0));
		ells.add(new Ellipse(new double[] {1.1, 1.1}, new double[] {1, 1}, 0));
		ells.add(new Ellipse(new double[] {6, 5.1}, new double[] {1, 1}, 0));
		ells.add(new Ellipse(new double[] {0.0, 0.0}, new double[] {1, 1}, 0));

		assertEquals(3, Utils.shapesInRegIdx(ells, reg).size());
	}

	@Test
	public void test2()
	{
		Rectangle reg = new Rectangle(new double[] {0.0, 0.0}, new double[] {5.0, 5.0});

		ArrayList<Rectangle> ells = new ArrayList<>();
		ells.add(new Rectangle(new double[] {0.0, 0.0}, new double[] {1, 1}));
		ells.add(new Rectangle(new double[] {1.1, 1.1}, new double[] {1, 1}));
		ells.add(new Rectangle(new double[] {6, 5.1}, new double[] {10, 10}));
		ells.add(new Rectangle(new double[] {0.0, 0.0}, new double[] {1, 1}));

		assertEquals(3, Utils.shapesInRegIdx(ells, reg).size());
	}
}
