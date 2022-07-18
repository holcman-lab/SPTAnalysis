package fiji.plugin.SPTAnalysis.estimators;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.Point;
import fiji.plugin.SPTAnalysis.struct.Trajectory;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;

@XmlRootElement(name = "MaxLikelihoodEstimator")
@XmlAccessorType(XmlAccessType.FIELD)
public class MaxLikelihoodEstimator extends WellEstimator
{
	@XmlTransient
	protected final TrajectoryEnsemble trajs;
	@XmlTransient
	protected final double dt;

	protected double[] lambdaEst;
	protected double[] DVecEst;
	protected double Aest;
	protected double Dest;
	@XmlTransient
	protected Ellipse ell;

	public MaxLikelihoodEstimator()
	{
		this.trajs = null;
		this.dt = Double.NaN;
		this.ell = null;

		this.lambdaEst = null;
		this.DVecEst = null;
		this.Aest = Double.NaN;
		this.Dest = Double.NaN;
	}

	public MaxLikelihoodEstimator(TrajectoryEnsemble trajs, double dt, Ellipse ell)
	{
		this.trajs = trajs;
		this.dt = dt;
		this.ell = ell;

		this.lambdaEst = null;
		this.DVecEst = null;
		this.Aest = Double.NaN;
		this.Dest = Double.NaN;
	}

	private void estimateMLE()
	{
		double[] p1 = new double[] {0.0, 0.0};
		double[] p2 = new double[] {0.0, 0.0};
		double[] p3 = new double[] {0.0, 0.0};
		double[] p4 = new double[] {0.0, 0.0};

		double n = 0;
		for (Trajectory tr: this.trajs.trajs())
		{
			for (int i = 1; i < tr.points().size(); ++i)
			{
				p1[0] += tr.points().get(i).x * tr.points().get(i-1).x;
				p1[1] += tr.points().get(i).y * tr.points().get(i-1).y;

				p2[0] += tr.points().get(i).x;
				p2[1] += tr.points().get(i).y;

				p3[0] += tr.points().get(i-1).x;
				p3[1] += tr.points().get(i-1).y;

				p4[0] += Math.pow(tr.points().get(i-1).x, 2);
				p4[1] += Math.pow(tr.points().get(i-1).y, 2);

				n += 1;
			}
		}
		double[] beta1 = new double[] {Double.NaN, Double.NaN};
		beta1[0] = (p1[0] / n - (p2[0] * p3[0]) / (n*n)) /
				(p4[0] / n - Math.pow(p3[0], 2) / (n*n)) + 4/n;
		beta1[1] = (p1[1] / n - (p2[1] * p3[1]) / (n*n)) /
				(p4[1] / n - Math.pow(p3[1], 2) / (n*n)) + 4/n;

		p1 = new double[] {0.0, 0.0};
		for (Trajectory tr: this.trajs.trajs())
		{
			for (int i = 1; i < tr.points().size(); ++i)
			{
				p1[0] += tr.points().get(i).x - beta1[0] * tr.points().get(i-1).x;
				p1[1] += tr.points().get(i).y - beta1[1] * tr.points().get(i-1).y;
			}
		}
		double[] beta2 = new double[] {p1[0] / n / (1 - beta1[0]),
									   p1[1] / n / (1 - beta1[1])};

		p1 = new double[] {0.0, 0.0};
		for (Trajectory tr: this.trajs.trajs())
		{
			for (int i = 1; i < tr.points().size(); ++i)
			{
				p1[0] += Math.pow(tr.points().get(i).x - beta1[0] * tr.points().get(i-1).x
						- beta2[0] * (1 - beta1[0]), 2);
				p1[1] += Math.pow(tr.points().get(i).y - beta1[1] * tr.points().get(i-1).y
						- beta2[1] * (1 - beta1[1]), 2);
			}
		}
		double[] beta3 = new double[] {p1[0] / n, p1[1] / n};
		this.lambdaEst = new double[] {-Math.log(beta1[0]) / this.dt,
									   -Math.log(beta1[1]) / this.dt};

		this.Aest = (this.lambdaEst[0] * Math.pow(ell.rad()[0], 2) / 2 +
					 this.lambdaEst[1] * Math.pow(ell.rad()[1], 2) / 2) / 2;

		this.DVecEst = new double[] {(this.lambdaEst[0] * beta3[0]) / (1 - Math.pow(beta1[0], 2)),
									 (this.lambdaEst[1] * beta3[1]) / (1 - Math.pow(beta1[1], 2))};
		this.Dest = (this.DVecEst[0] + this.DVecEst[1]) / 2;
	}

	@Override
	public double estimateA()
	{
		if (Double.isNaN(this.Aest))
			this.estimateMLE();
		return this.Aest;
	}

	@Override
	public double estimateD()
	{
		if (Double.isNaN(this.Dest))
			this.estimateMLE();
		return this.Dest;
	}

	@Override
	public WellScore estimateScore()
	{
		return new WellScore.Likelihood(wellLogLikelihoodXY(this.trajs, this.ell.mu(),
										this.lambdaEst, this.DVecEst, this.dt));
	}

	public double[] estimateLambda()
	{
		if (this.lambdaEst == null)
			this.estimateMLE();
		return this.lambdaEst;
	}

	public double[] estimateDVec()
	{
		if (this.DVecEst == null)
			this.estimateMLE();
		return this.DVecEst;
	}

	public static double wellLogLikelihood(TrajectoryEnsemble trajs, double[] muEst,
										   double lambdaEst, double Dest, double dt)
	{
		long N = 0;
		double res = 0.0;
		for (Trajectory tr: trajs.trajs())
		{
			for (int i = 0; i <  tr.points().size()-1; ++i)
			{
				Point p1 = tr.points().get(i);
				Point p2 = tr.points().get(i+1);

				double[] tmp = new double[]{p2.x - muEst[0] - (p1.x - muEst[0])
									* Math.exp(-lambdaEst * dt),
								p2.y - muEst[1] - (p1.y - muEst[1])
									* Math.exp(-lambdaEst * dt)};
				res += (tmp[0] * tmp[0] + tmp[1] * tmp[1]) * lambdaEst /
				(Dest * (1 - Math.exp(-2 * lambdaEst * dt)));
				++N;
			}
		}

		return res / N;
	}

	public static double wellLogLikelihoodXY(TrajectoryEnsemble trajs, double[] muEst,
											 double[] lambdaEst, double[] Dest, double dt)
	{
		//long N = 0;
		double res = 0.0;
		for (Trajectory tr: trajs.trajs())
		{
			for (int i = 0; i <  tr.points().size()-1; ++i)
			{
				Point p1 = tr.points().get(i);
				Point p2 = tr.points().get(i+1);

				double[] tmp = new double[]{p2.x - muEst[0] - (p1.x - muEst[0])
									* Math.exp(-lambdaEst[0] * dt),
											p2.y - muEst[1] - (p1.y - muEst[1])
									* Math.exp(-lambdaEst[1] * dt)};

				res += (tmp[0] * tmp[0]) * lambdaEst[0] /
				(Dest[0] * (1 - Math.exp(-2 * lambdaEst[0] * dt))) + 
				(tmp[1] * tmp[1]) * lambdaEst[1] /
				(Dest[1] * (1 - Math.exp(-2 * lambdaEst[1] * dt)));
				//++N;
			}
		}

		return res;// / N;
	}
}
