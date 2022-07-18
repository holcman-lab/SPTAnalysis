package fiji.plugin.SPTAnalysis.struct;

public class EllipseFit
{
	protected final Ellipse ell;
	protected final double[] S;
	protected final double confPerc;

	public EllipseFit(final Ellipse ell, final double[] S, double confPerc)
	{
		this.ell = ell;
		this.S = S;
		this.confPerc = confPerc;
	}

	public Ellipse ell()
	{
		return ell;
	}

	public double[] S()
	{
		return S;
	}
}
