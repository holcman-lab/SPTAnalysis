package fiji.plugin.SPTAnalysis.writers;


public class SVGCanvasWriter extends SVGWriter
{
	protected double[] minp;
	protected double[] maxp;

	public SVGCanvasWriter(final double[] minp, final double[] maxp, double zoomFactor)
	{
		super(zoomFactor);

		this.minp = minp;
		this.maxp = maxp;
	}

	@Override
	public void setMinp(double[] minpt)
	{
		this.minp = minpt;
	}

	@Override
	public String generate()
	{
		return "";
	}

	@Override
	public double[] minp()
	{
		return new double[] {this.minp[0],
							 this.minp[1]};
	}

	@Override
	public double[] maxp()
	{
		return new double[] {this.maxp[0],
							 this.maxp[1]};
	}
}
