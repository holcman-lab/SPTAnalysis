package fiji.plugin.SPTAnalysis.writers;

import java.awt.Color;

public class SVGScaleBarWriter extends SVGWriter
{
	protected double lineWidth;
	protected double[] minp;
	protected Color col;

	public SVGScaleBarWriter(double zoomFactor, double lineWidth, Color col)
	{
		super(zoomFactor);

		this.lineWidth = lineWidth;
		this.col = col;
	}

	@Override
	public void setMinp(double[] minpt)
	{
		this.minp = minpt;
	}

	@Override
	public String generate()
	{
		return String.format("<path style=\"fill:none;stroke:rgb(0,0,0);stroke-width:2px\" " +
				 "d=\"M %.3f,%.3f %.3f,%.3f\"/>\n", 1 * this.zoomFactor, 1 * this.zoomFactor,
				 2 * this.zoomFactor, 1 * this.zoomFactor);
	}

	@Override
	public double[] minp()
	{
		return new double[] {1, 1};
	}

	@Override
	public double[] maxp()
	{
		return new double[] {2, 1};
	}
}
