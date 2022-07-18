package fiji.plugin.SPTAnalysis.struct;

public abstract class Shape 
{
	public abstract boolean inside(double[] p);
	public abstract boolean intersect(final Shape s);
	public abstract double[] center();
	public abstract double area();

	public abstract double[] minPt();
	public abstract double[] maxPt();
}
