package fiji.plugin.SPTAnalysis.estimators;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import fiji.plugin.SPTAnalysis.Utils;
import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;
import fiji.plugin.SPTAnalysis.struct.VectorMap;

@XmlRootElement(name = "LeastSquareEstimatorCircle")
@XmlAccessorType(XmlAccessType.FIELD)
public class LeastSquareEstimatorCircle extends GridEstimator
{
	public LeastSquareEstimatorCircle()
	{
		super();
	}

	public LeastSquareEstimatorCircle(TrajectoryEnsemble trajs, VectorMap drift,
				ArrayList<int[]> nh, Ellipse ell, boolean DinEllipseOnly)
	{
		super(trajs, ell, drift, nh, DinEllipseOnly);
	}

	@Override
	public double estimateA()
	{
		double num = 0.0;
		double den = 0.0;
		for (int[] gpos: this.nh)
		{
			double pt[] = drift.grid().get(gpos[0], gpos[1]);
			double[] rotPt = Utils.rot_point(new double[] {pt[0] - ell.mu()[0], pt[1] - ell.mu()[1]}, ell.phi());
			double[] rotDr = Utils.rot_point(drift.get(gpos[0], gpos[1]), ell.phi());

			num += rotDr[0] * rotPt[0] + rotDr[1] * rotPt[1];
			den += Math.pow(rotPt[0], 2) + Math.pow(rotPt[1], 2);
		}

		return - Math.pow(this.ell.rad()[0], 2) * num / den / 2;
	}

	@Override
	public WellScore estimateScore()
	{
		double num = 0.0;
		double ptSum = 0.0;
		double fieldSum = 0.0;
		for (int[] gpos: this.nh)
		{
			double[] pt = new double[] {this.drift.grid().get(gpos[0], gpos[1])[0] - this.ell.mu()[0],
										this.drift.grid().get(gpos[0], gpos[1])[1] - this.ell.mu()[1]};

			double[] rotPt = Utils.rot_point(pt, this.ell.phi());
			double[] rotDr = Utils.rot_point(this.drift.get(gpos[0], gpos[1]), this.ell.phi());

			num += rotPt[0] * rotDr[0] + rotPt[1] * rotDr[1];
			ptSum += Math.pow(rotPt[0], 2) + Math.pow(rotPt[1], 2);
			fieldSum += Math.pow(rotDr[0], 2) + Math.pow(rotDr[1], 2);
		}

		return new WellScore.Parabolic(1 - Math.pow(num, 2) / (ptSum * fieldSum));
	}
}
