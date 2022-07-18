package fiji.plugin.SPTAnalysis.estimators;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import fiji.plugin.SPTAnalysis.Utils;
import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;

@XmlRootElement(name = "DensityEstimatorMLE")
@XmlAccessorType(XmlAccessType.FIELD)
public class DensityEstimatorMLE extends WellEstimator
{
	@XmlTransient
	protected TrajectoryEnsemble trajs;
	@XmlTransient
	protected Ellipse ell;
	protected final double[] Cdiag;

	protected final MaxLikelihoodEstimator mle;

	public DensityEstimatorMLE(TrajectoryEnsemble trajs, Ellipse ell, double[] Cdiag)
	{
		this.trajs = trajs;
		this.ell = ell;
		this.Cdiag = Cdiag;

		this.mle = new MaxLikelihoodEstimator(Utils.trajsInShape(this.trajs, this.ell),
				this.trajs.acqDT(), this.ell);
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
		return this.mle.estimateD();
	}

	@Override
	public WellScore estimateScore()
	{
		throw new UnsupportedOperationException();
	}

}
