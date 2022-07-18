package fiji.plugin.SPTAnalysis.estimators;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import fiji.plugin.SPTAnalysis.Utils;
import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;

@XmlRootElement(name = "DensityEstimator")
@XmlAccessorType(XmlAccessType.FIELD)
public class DensityEstimator extends WellEstimator
{
	protected TrajectoryEnsemble trajs;
	protected Ellipse ell;
	protected boolean DInEllipseOnly;
	protected final double[] Cdiag;

	public DensityEstimator(TrajectoryEnsemble trajs, Ellipse ell, double[] Cdiag,
			boolean DInEllipseOnly)
	{
		this.trajs = trajs;
		this.ell = ell;
		this.DInEllipseOnly = DInEllipseOnly;
		this.Cdiag = Cdiag;
	}

	public static double Aformula(final double[] Cdiag, final double[] rads, final double D)
	{
		return (D * Math.pow(rads[0], 2) / Cdiag[0] +
				D * Math.pow(rads[1], 2) / Cdiag[1]) / 4;
	}

	@Override
	public double estimateA()
	{
		double D = this.estimateD();
		return DensityEstimator.Aformula(this.Cdiag, this.ell.rad(), D);
	}

	@Override
	public double estimateD()
	{
		if (this.DInEllipseOnly)
			return Utils.diffCoeffInEllipseConstr(this.trajs, this.ell);
		else
			return Utils.diffCoeffInEllipse(this.trajs, this.ell);
	}

	@Override
	public WellScore estimateScore()
	{
		throw new UnsupportedOperationException();
	}

}
