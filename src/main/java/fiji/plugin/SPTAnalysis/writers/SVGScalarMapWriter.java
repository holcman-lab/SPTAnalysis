package fiji.plugin.SPTAnalysis.writers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import org.jfree.chart.renderer.InterpolatePaintScale;

import fiji.plugin.SPTAnalysis.struct.ScalarMap;

public class SVGScalarMapWriter extends SVGWriter
{
	protected final ScalarMap map;
	protected final ArrayList<int[]> nhs;
	protected double[] minp;
	protected final double[] maxp;

	public SVGScalarMapWriter(ScalarMap map, double zoomFactor)
	{
		super(zoomFactor);

		this.map = map;
		this.nhs = null;
		this.minp = map.grid().Xmin();
		this.maxp = map.grid().Xmax();
	}

	public SVGScalarMapWriter(ScalarMap map, double zoomFactor, ArrayList<int[]> nhs)
	{
		super(zoomFactor);

		this.map = map;
		this.nhs = nhs;
		this.minp = map.grid().Xmin();
		this.maxp = map.grid().Xmax();
	}

	@Override
	public void setMinp(double[] minpt)
	{
		this.minp = minpt;
	}

	@Override
	public String generate()
	{
		InterpolatePaintScale cm = InterpolatePaintScale.Jet;

		double dx = map.grid().dx();

		StringBuilder res = new StringBuilder ();
		res.append("<g>\n");
		if (this.nhs == null)
		{
			Iterator<double[]> it = map.iterator();

			double mapMax =  map.max();
			res.append(String.format("<!--\nmax: %.3f, %s\n-->\n", mapMax, map.params().toString()));
			while (it.hasNext())
			{
				double[] v = it.next();

				if (v[2] == 0.0)
					continue;
				double[] pos = map.grid().get((int) v[0], (int) v[1]);

				Color col = cm.getPaint(v[2] / mapMax);

				res.append(String.format("<rect x=\"%g\" y=\"%g\" width=\"%g\" height=\"%g\" " +
						"style=\"fill:rgb(%d,%d,%d)\"/>\n",
						((pos[0] - dx/2) - this.minp[0]) * this.zoomFactor,
						((pos[1] - dx/2)  - this.minp[1]) * this.zoomFactor,
						dx * this.zoomFactor, dx * this.zoomFactor,
						col.getRed(), col.getGreen(), col.getBlue()));
			}
		}
		else
		{
			double mapMax =  map.max(nhs);
			res.append(String.format("<!--\nmax: %.3f, %s\n-->\n", mapMax, map.params().toString()));
			for (final int[] nh: nhs)
			{
				if (!this.map.isSet(nh[0], nh[1]))
					continue;

				double v = this.map.get(nh[0], nh[1]);
				if (v == 0.0)
					continue;

				double[] pos = map.grid().get(nh[0], nh[1]);

				Color col = cm.getPaint(v / mapMax);

				res.append(String.format("<rect x=\"%g\" y=\"%g\" width=\"%g\" height=\"%g\" " +
						"style=\"fill:rgb(%d,%d,%d)\"/>\n",
						((pos[0] - dx/2) - this.minp[0]) * this.zoomFactor,
						((pos[1] - dx/2) - this.minp[1]) * this.zoomFactor,
						dx * this.zoomFactor, dx * this.zoomFactor,
						col.getRed(), col.getGreen(), col.getBlue()));
			}
		}
		res.append("</g>\n");

		return res.toString();
	}

	@Override
	public double[] minp()
	{
		return new double[] {this.minp[0], this.minp[1]};
	}

	@Override
	public double[] maxp()
	{
		return new double[] {this.maxp[0], this.maxp[1]};
	}
}
