package fiji.plugin.SPTAnalysis.estimators;

import java.util.ArrayList;

import fiji.plugin.SPTAnalysis.Utils;
import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.MapParameters;
import fiji.plugin.SPTAnalysis.struct.SquareGrid;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;
import fiji.plugin.SPTAnalysis.struct.VectorMap;

public class WellEstimatorFactory
{
	final public TrajectoryEnsemble trajs;
	final public Ellipse ell;
	final public WellEstimatorParameters ps;
	
	public WellEstimatorFactory(final TrajectoryEnsemble trajs,
								final Ellipse ell, final WellEstimatorParameters ps)
	{
		this.trajs = trajs;
		this.ell = ell;
		this.ps = ps;
	}

	public static WellEstimator getGridEst(final WellEstimator.type estType, final TrajectoryEnsemble trajs,
			final Ellipse ell, final VectorMap drift, final ArrayList<int[]> nh, boolean diffInWell)
	{
		if (estType == WellEstimator.type.LSQELL)
			return new LeastSquareEstimatorEllipse(trajs, drift, nh,
							ell, diffInWell);
		else if (estType == WellEstimator.type.LSQELLNORM)
			return new LeastSquareEstimatorEllipseNorm(trajs, drift, nh,
							ell, diffInWell);
		else if (estType == WellEstimator.type.LSQCIRC)
			return new LeastSquareEstimatorCircle(trajs, drift, nh,
							ell, diffInWell);
		else if (estType == WellEstimator.type.LSQELLNORMFIT)
			return new LeastSquareEstimatorEllipseNormFit(trajs, drift, nh,
							ell, diffInWell);
		else
			throw(new UnsupportedOperationException());
	}
	
	public WellEstimator getEst()
	{
		if (this.ps.estType == WellEstimator.type.LSQELL || this.ps.estType == WellEstimator.type.LSQELLNORM ||
			this.ps.estType == WellEstimator.type.LSQCIRC || this.ps.estType == WellEstimator.type.LSQELLNORMFIT)
		{
			assert (ps instanceof GridEstimatorParameters);
			GridEstimatorParameters psCur = (GridEstimatorParameters) this.ps;

			//TODO change 50 it is ugly
			SquareGrid locG = new SquareGrid(psCur.dx, this.ell.mu(), 50);
			ArrayList<int[]> nh = Utils.squaresInReg(locG, ell);

			VectorMap drift = VectorMap.genDriftMap(locG, this.trajs, 
					new MapParameters.DriftParameters(locG.dx(), psCur.driftNptsTh, false, 0));

			nh = Utils.filter_empty_squares(drift, nh);
			if (nh.size() < psCur.minCellsTh)
				return null;

			WellEstimator res = WellEstimatorFactory.getGridEst(psCur.estType, this.trajs, this.ell, drift,
					nh, psCur.diffInWell);
			if (psCur.correctField)
			{
				double A = res.estimateA();
				double lambda = A / Math.pow(ell.rad()[0],2) + A / Math.pow(ell.rad()[1], 2);
				res = WellEstimatorFactory.getGridEst(psCur.estType, this.trajs, this.ell,
						VectorMap.applyFactor(drift, 1 + lambda * trajs.acqDT()/2),
						nh, psCur.diffInWell);
			}

			return res;
		}
		else if (this.ps.estType == WellEstimator.type.DENS || this.ps.estType == WellEstimator.type.DENSMLE)
		{
			assert (ps instanceof DensityEstimatorParameters);
			DensityEstimatorParameters psCur = (DensityEstimatorParameters) this.ps;

			double[][] CInEll = ell.covarianceInEllRot(this.trajs);
			if (psCur.estType == WellEstimator.type.DENS)
				return new DensityEstimator(this.trajs, this.ell, new double[] {CInEll[0][0], CInEll[1][1]},
											psCur.diffInWell);
			else if (psCur.estType == WellEstimator.type.DENSMLE)
				return new DensityEstimatorMLE(this.trajs, this.ell, new double[] {CInEll[0][0], CInEll[1][1]});
			else
				throw(new UnsupportedOperationException());
		}
		else if (this.ps.estType == WellEstimator.type.MLE)
			return new MaxLikelihoodEstimator(Utils.trajsInShape(this.trajs, this.ell), this.trajs.acqDT(), this.ell);
		else
			throw(new UnsupportedOperationException());
	}
}
