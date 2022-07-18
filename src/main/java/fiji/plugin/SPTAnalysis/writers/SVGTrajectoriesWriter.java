package fiji.plugin.SPTAnalysis.writers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import fiji.plugin.SPTAnalysis.struct.Point;
import fiji.plugin.SPTAnalysis.struct.TrajectoriesColorScheme;
import fiji.plugin.SPTAnalysis.struct.Trajectory;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;

public class SVGTrajectoriesWriter extends SVGWriter
{
	protected final TrajectoryEnsemble trajs;

	protected double[] minp;
	protected final double[] maxp;
	protected double lineWidth;
	final protected TrajectoriesColorScheme trajCol;

	final protected Random r;

	public SVGTrajectoriesWriter(TrajectoryEnsemble trajs, double zoomFactor, double lineWidth,
			final TrajectoriesColorScheme trajCol)
	{
		super(zoomFactor);

		this.trajs = trajs;
		this.minp = trajs.min();
		this.maxp = trajs.max();

		this.lineWidth = lineWidth;
		this.trajCol = trajCol;

		this.r = new Random();
	}

	@Override
	public void setMinp(double[] minpt)
	{
		this.minp = minpt;
	}

	private void writeTraj(final StringBuilder sb, final Color col, ArrayList<Point> pts)
	{
		sb.append(String.format("<path style=\"fill:none;stroke:rgb(%d,%d,%d);stroke-width:%gpx\" d=\"M ",
				col.getRed(), col.getGreen(), col.getBlue(), this.lineWidth));

		for (int i = 0; i < pts.size() - 1; ++i)
			sb.append(String.format("%.3f,%.3f ", (pts.get(i).x - this.minp[0]) * this.zoomFactor,
												   (pts.get(i).y - this.minp[1]) * this.zoomFactor));
		sb.append(String.format("%.3f,%.3f", (pts.get((pts.size()-1)).x - this.minp[0]) * this.zoomFactor,
											  (pts.get((pts.size()-1)).y - this.minp[1]) * this.zoomFactor));
		sb.append("\"/>\n");
	}

	@Override
	public String generate()
	{
		Color col = new Color(0, 0, 0);

		StringBuilder res = new StringBuilder ();

		if (trajCol.coloringType() == TrajectoriesColorScheme.ColoringType.Uniform)
			col = TrajectoriesColorScheme.uniformColor;

		res.append("<g>\n");
		for (int i = 0; i < trajs.trajs().size(); ++i)
		{
			Trajectory tr = trajs.trajs().get(i);

			if (trajCol.coloringType() == TrajectoriesColorScheme.ColoringType.Random)
				col = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));

			if (trajCol.coloringType() == TrajectoriesColorScheme.ColoringType.InstVel)
			{
				res.append(String.format("<path style=\"fill:none;stroke:rgb(%d,%d,%d);stroke-width:%gpx\" d=\"M ",
						col.getRed(), col.getGreen(), col.getBlue(), this.lineWidth));

				for (int j = 0; j < tr.points().size() - 1; ++j)
				{
					ArrayList<Point> disps = new ArrayList<> ();
					disps.add(tr.points().get(j));
					disps.add(tr.points().get(j+1));
					this.writeTraj(res, this.trajCol.dispsCols().get(i).get(j), disps);
				}
			}
			else
				this.writeTraj(res, col, tr.points());
		}
		res.append("</g>\n");

		return res.toString();
	}

	@Override
	public double[] minp()
	{
		return new double[] {this.minp[0], this.minp[1]};
	}

	@Override
	public double[] maxp()
	{
		return new double[] {this.maxp[0], this.maxp[1]};
	}
}
