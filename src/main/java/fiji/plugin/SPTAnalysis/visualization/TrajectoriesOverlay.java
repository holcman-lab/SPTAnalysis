package fiji.plugin.SPTAnalysis.visualization;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import fiji.plugin.SPTAnalysis.struct.TrajectoriesColorScheme;
import fiji.plugin.SPTAnalysis.struct.TrajectoriesColorScheme.ColoringType;
import fiji.plugin.SPTAnalysis.struct.TrajectoriesColorSchemeWindows;
import fiji.plugin.SPTAnalysis.struct.Trajectory;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsembleWindows;
import fiji.plugin.trackmate.util.TMUtils;
import ij.ImagePlus;
import ij.gui.Roi;

public class TrajectoriesOverlay extends Roi
{
	private static final long serialVersionUID = 1L;

	protected final double[] calibration;
	protected final TrajectoryEnsembleWindows trs;
	protected final TrajectoriesColorSchemeWindows colors;
	protected boolean showAllWindows;
	protected double maxGUIInstVel;
	protected double maxInstVel;

	public TrajectoriesOverlay(final ImagePlus imp, final TrajectoryEnsembleWindows trajs,
							   final TrajectoriesColorSchemeWindows colors, boolean showAllWins,
							   double maxGUIInstVel)
	{
		super(0, 0, imp);
		this.calibration = TMUtils.getSpatialCalibration(imp);
		this.trs = trajs;
		this.colors = colors;
		this.showAllWindows = showAllWins;
		this.maxGUIInstVel = maxGUIInstVel;
	}

	public TrajectoriesColorSchemeWindows colorScheme()
	{
		return this.colors;
	}

	public double maxIvels()
	{
		return this.maxInstVel;
	}

	@Override
	public void drawOverlay(final Graphics g)
	{
		final Graphics2D g2d = (Graphics2D) g;
		int frame = this.imp.getFrame() - 1;

		final double magn = getMagnification();
		final int xcorn = ic.offScreenX(0);
		final int ycorn = ic.offScreenY(0);

		g2d.setStroke(new BasicStroke(1.0f));
		if (this.colors.coloringType() == ColoringType.Uniform)
			g2d.setColor(TrajectoriesColorScheme.uniformColor);

		ArrayList<Integer> todo = new ArrayList<> ();
		if (this.showAllWindows)
			for (int k = 0; k < this.trs.wins.size(); ++k)
				todo.add(k);
		else
			todo.add(frame);

		for (int curFrame: todo)
		{
			int k = 0;
			for (Trajectory tr: this.trs.wins.get(curFrame).trajs())
			{
				int[] xps = new int[tr.points().size()];
				int[] yps = new int[tr.points().size()];
				for (int i = 0; i < tr.points().size(); ++i)
				{
					xps[i] = (int) Math.floor((tr.points().get(i).x / calibration[0] - xcorn) * magn);
					yps[i] = (int) Math.floor((tr.points().get(i).y / calibration[1] - ycorn) * magn);
				}

				if (this.colors.coloringType() == ColoringType.InstVel)
				{
					for (int u = 0; u < xps.length - 1; ++u)
					{
						if (this.colors.wins.get(curFrame).instVels().get(k)[u] < this.maxGUIInstVel)
						{
							g2d.setColor(this.colors.wins.get(curFrame).dispsCols().get(k).get(u));
							g2d.drawLine(xps[u], yps[u], xps[u+1], yps[u+1]);
						}
					}
				}
				else
				{
					if (this.colors.coloringType() == ColoringType.Random)
						g2d.setColor(this.colors.wins.get(curFrame).trajsCols().get(k));
					g2d.drawPolyline(xps, yps, tr.points().size());
				}
				++k;
			}
		}
	}
}
