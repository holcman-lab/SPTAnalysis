package fiji.plugin.SPTAnalysis.estimators;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import fiji.plugin.SPTAnalysis.Utils;
import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;
import fiji.plugin.SPTAnalysis.struct.VectorMap;

@XmlRootElement(name = "LeastSquareEstimatorEllipseNormFit")
@XmlAccessorType(XmlAccessType.FIELD)
public class LeastSquareEstimatorEllipseNormFit extends GridEstimator
{
	public LeastSquareEstimatorEllipseNormFit()
	{
		super();
	}

	public LeastSquareEstimatorEllipseNormFit(TrajectoryEnsemble trajs, VectorMap drift, ArrayList<int[]> nh,
								Ellipse ell, boolean inEllipseOnly)
	{
		super(trajs, ell, drift, nh, inEllipseOnly);
	}

	//FROM: https://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/LinearRegression.java.html
	protected static double[] linearRegression(double[] x, double[] y)
	{
		int n = x.length;

		double sumx = 0.0;
		double sumy = 0.0;
		for (int i = 0; i < n; i++)
		{
			sumx  += x[i];
			sumy  += y[i];
		}
		double xbar = sumx / n;
		double ybar = sumy / n;

		double xxbar = 0.0;
		double xybar = 0.0;
		for (int i = 0; i < n; i++)
		{
			xxbar += (x[i] - xbar) * (x[i] - xbar);
			xybar += (x[i] - xbar) * (y[i] - ybar);
		}
		double slope  = xybar / xxbar;
		double intercept = ybar - slope * xbar;

		return new double[] {slope, intercept};
	}

	@Override
	public double estimateA()
	{
		double[] xs = new double[this.nh.size()];
		double[] ys = new double[this.nh.size()];
		for (int k = 0; k < this.nh.size(); ++ k)
		{
			int[] gpos = this.nh.get(k);
			double[] pos = this.drift.grid().get(gpos[0], gpos[1]);
			pos[0] -= this.ell.mu()[0];
			pos[1] -= this.ell.mu()[1];
			xs[k] = Utils.vnorm(pos);
			ys[k] = Utils.vnorm(new double[] {this.drift.get(gpos[0], gpos[1])[0],
											  this.drift.get(gpos[0], gpos[1])[1]});
		}

		double[] fit = linearRegression(xs, ys);

		return fit[0] * Math.pow(this.ell.rad()[0], 2) / 2;
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
