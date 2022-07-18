package fiji.plugin.SPTAnalysis.struct;

import fiji.plugin.SPTAnalysis.Utils;

public class Ring extends Shape
{
	final protected double[] mu;

	//rads[0] -> inner radius; rads[1] -> outter
	final protected double[] rads;
	final protected double[] radsSq;

	public Ring(double[] mu, double[] rads)
	{
		this.mu = mu;
		this.rads = rads;
		this.radsSq = new double[] {Math.rint(Math.pow(rads[0], 2) * Utils.PRECISION) / Utils.PRECISION,
									Math.rint(Math.pow(rads[1], 2) * Utils.PRECISION) / Utils.PRECISION};
	}

	@Override
	public boolean inside(double[] p)
	{
		double d = Utils.squaredDist(p, this.mu);
		return d >= this.radsSq[0] && d <= this.radsSq[1];
	}

	@Override
	public boolean intersect(Shape s)
	{
		assert(false); //not implemented
		return false;
	}

	public boolean insideRatio(double[] p, double yratio)
	{
		double d = Utils.RatioSquaredDist(p, this.mu, yratio);
		return d >= this.radsSq[0] && d <= this.radsSq[1];
	}

	@Override
	public double[] center()
	{
		return this.mu;
	}

	public double[] rads()
	{
		return this.rads;
	}

	public double dr()
	{
		return this.rads[1] - this.rads[0];
	}

	@Override
	public double area()
	{
		return Math.PI * Math.pow(this.radsSq[1] - this.radsSq[0], 2);
	}

	public String toStr()
	{
		return String.format("mu=[%g,%g], rads=[%g %g]", this.mu[0], this.mu[1],
				this.rads[0], this.rads[1]);
	}

	@Override
	public double[] minPt()
	{
		return new double[] {this.mu[0] - this.rads[1],
							 this.mu[1] - this.rads[1]};
	}

	@Override
	public double[] maxPt()
	{
		return new double[] {this.mu[0] + this.rads[1],
				 			 this.mu[1] + this.rads[1]};
	}
}
