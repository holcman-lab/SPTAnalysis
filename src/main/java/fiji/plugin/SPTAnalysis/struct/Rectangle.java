package fiji.plugin.SPTAnalysis.struct;

public class Rectangle extends Shape
{
	protected double[] ll;
	protected double[] tr;

	public Rectangle(double[] lower_left, double[] top_right)
	{
		this.ll = lower_left;
		this.tr = top_right;
	}

	public double[] ll()
	{
		return this.ll;
	}

	public double[] tr()
	{
		return this.tr;
	}

	public Rectangle(double[] cent, double size)
	{
		this.ll = new double[] {cent[0] - size/2, cent[1] - size/2};
		this.tr = new double[] {cent[0] + size/2, cent[1] + size/2};
	}

	@Override
	public boolean inside(double[] pt)
	{
		return pt[0] >= this.ll[0] && pt[0] <= this.tr[0] &&
			   pt[1] >= this.ll[1] && pt[1] <= this.tr[1];
	}

	@Override
	public double[] center()
	{
		return new double[] {this.ll[0] + (this.tr[0] - this.ll[0]) / 2,
							 this.ll[1] + (this.tr[1] - this.ll[1]) / 2};
	}
	
	@Override
	public double area()
	{
		return (this.tr[0] - this.ll[0]) * (this.tr[1] - this.ll[1]);
	}

	@Override
	public boolean intersect(Shape s)
	{
		assert(false); //not implemented
		return false;
	}

	@Override
	public double[] minPt()
	{
		return this.ll;
	}

	@Override
	public double[] maxPt()
	{
		return this.tr;
	}
}