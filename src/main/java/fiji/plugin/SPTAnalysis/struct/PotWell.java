package fiji.plugin.SPTAnalysis.struct;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import fiji.plugin.SPTAnalysis.XMLAdapters;
import fiji.plugin.SPTAnalysis.estimators.WellScore;
import fiji.plugin.SPTAnalysis.wellDetection.FitResult;

@XmlRootElement(name = "PotWell")
@XmlAccessorType(XmlAccessType.FIELD)
public class PotWell
{
	protected Ellipse ell;
	protected double A;
	protected double D;
	@XmlJavaTypeAdapter(XMLAdapters.WellScoreAdapter.class)
	protected WellScore score;
	protected FitResult fitRes;

	boolean correctedD;

	public PotWell()
	{
		this.ell = null;
		this.A = Double.NaN;
		this.D = Double.NaN;
		this.score = WellScore.empty;
		this.fitRes = null;

		this.correctedD = false;
	}

	public PotWell(Ellipse ell, double A, double D, WellScore score)
	{
		this.ell = ell;
		this.A = A;
		this.D = D;
		this.score = score;
		this.fitRes = null;
	}

	public void attachFitResult(FitResult fr)
	{
		this.fitRes = fr;
	}

	public Ellipse ell()
	{
		return this.ell;
	}

	public WellScore score()
	{
		return this.score;
	}

	public double A()
	{
		return this.A;
	}

	public double D()
	{
		return this.D;
	}

	public FitResult fitResult()
	{
		return this.fitRes;
	}

	public double DCorrected(double dt)
	{
		double a = this.ell.rad()[0];
		double b = this.ell.rad()[1];
		double lambda = (2 * this.A / (a*a) + 2 * this.A / (b*b)) / 2;

		return this.D * (1 + lambda * dt);
	}

	public void correctD(double dt)
	{
		assert(!this.correctedD);
		this.D = this.DCorrected(dt);
		this.correctedD = true;
	}
	
	public String toStr()
	{
		return String.format("mu=[%g,%g], rad=[%g,%g], phi=%g, A=%g, D=%g, score=%g",
							 this.ell.mu[0], this.ell.mu[1], this.ell.rad[0], this.ell.rad[1],
							 this.ell.phi, this.A, this.D, this.score.value());
	}
}
