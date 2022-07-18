package fiji.plugin.SPTAnalysis.estimators;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import fiji.plugin.SPTAnalysis.Utils;
import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;
import fiji.plugin.SPTAnalysis.struct.VectorMap;

@XmlRootElement(name = "LeastSquareEstimatorEllipse")
@XmlAccessorType(XmlAccessType.FIELD)
public class LeastSquareEstimatorEllipse extends GridEstimator
{
	public LeastSquareEstimatorEllipse()
	{
		super();
	}

	public LeastSquareEstimatorEllipse(TrajectoryEnsemble trajs, VectorMap drift, ArrayList<int[]> nh,
								Ellipse ell, boolean inEllipseOnly)
	{
		super(trajs, ell, drift, nh, inEllipseOnly);
	}

	@Override
	public double estimateA()
	{
		double num = 0.0;
		double den = 0.0;
		for (int[] gpos: this.nh)
		{
			double pt[] = drift.grid().get(gpos[0], gpos[1]);
			double[] rot_pt = Utils.rot_point(new double[] {pt[0] - ell.mu()[0], pt[1] - ell.mu()[1]}, ell.phi());
			double[] rot_dr = Utils.rot_point(drift.get(gpos[0], gpos[1]), ell.phi());

			num += rot_dr[0] * rot_pt[0] / Math.pow(ell.rad()[0], 2) +
				   rot_dr[1] * rot_pt[1] / Math.pow(ell.rad()[1], 2);
			den += Math.pow(rot_pt[0], 2) / Math.pow(ell.rad()[0], 4) +
				   Math.pow(rot_pt[1], 2) / Math.pow(ell.rad()[1], 4);
		}

		return - num / den / 2;
	}

	public double estimateANorm()
	{
		double num = 0.0;
		double den = 0.0;
		for (int[] gpos: this.nh)
		{
			double pt[] = drift.grid().get(gpos[0], gpos[1]);
			double[] rot_pt = Utils.rot_point(new double[] {pt[0] - ell.mu()[0], pt[1] - ell.mu()[1]}, ell.phi());
			double[] rot_dr = Utils.rot_point(drift.get(gpos[0], gpos[1]), ell.phi());

			num += Utils.vnorm(new double[] {rot_pt[0] / Math.pow(ell.rad()[0], 2),
											 rot_pt[1] / Math.pow(ell.rad()[1], 2)}) *
					Utils.vnorm(rot_dr);
			den += Math.pow(rot_pt[0], 2) / Math.pow(ell.rad()[0], 4) +
				   Math.pow(rot_pt[1], 2) / Math.pow(ell.rad()[1], 4);
		}

		return num / den / 2;
	}

	@Override
	public WellScore estimateScore()
	{
		double fnorm = 0.0;
		double num = 0.0;
		double den = 0.0;
		for (int[] gpos: this.nh)
		{
			double[] pt = new double[] {this.drift.grid().get(gpos[0], gpos[1])[0] - this.ell.mu()[0],
										this.drift.grid().get(gpos[0], gpos[1])[1] - this.ell.mu()[1]};

			double[] rot_pt = Utils.rot_point(pt, this.ell.phi());
			double[] rot_dr = Utils.rot_point(this.drift.get(gpos[0], gpos[1]), this.ell.phi());

			fnorm += Math.pow(rot_dr[0], 2) + Math.pow(rot_dr[1], 2);
			num += rot_pt[0] * rot_dr[0] / Math.pow(this.ell.rad()[0], 2) +
				   rot_pt[1] * rot_dr[1] / Math.pow(this.ell.rad()[1], 2);
			den += Math.pow(rot_pt[0], 2) / Math.pow(this.ell.rad()[0], 4) +
				   Math.pow(rot_pt[1], 2) / Math.pow(this.ell.rad()[1], 4);
		}

		return new WellScore.Parabolic(1 - Math.pow(num, 2) / den / fnorm);
	}
}
