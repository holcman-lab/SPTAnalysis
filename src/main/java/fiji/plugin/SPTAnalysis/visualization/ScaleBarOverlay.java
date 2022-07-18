package fiji.plugin.SPTAnalysis.visualization;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import fiji.plugin.trackmate.util.TMUtils;
import ij.ImagePlus;
import ij.gui.Roi;

public class ScaleBarOverlay extends Roi
{
	private static final long serialVersionUID = 1L;

	private final double[] calib;
	private double length;

	public ScaleBarOverlay(final ImagePlus imp, double length)
	{
		super(0, 0, imp);
		this.calib = TMUtils.getSpatialCalibration(imp);
		this.length = length;
	}

	@Override
	public void drawOverlay(final Graphics g)
	{
		final Graphics2D g2d = (Graphics2D) g;
		final double magn = getMagnification();

		g2d.setStroke(new BasicStroke(2.0f));
		g2d.setColor(Color.white);

		int[] x1 = new int[] {10, 10+(int) Math.floor(this.length / calib[0] * magn)};
		int[] x2 = new int[] {10, 10};

		g2d.drawPolyline(x1, x2, 2);
		g2d.drawString(String.format("%.1f µm", this.length), x1[0], x1[0] + 12);
	}
}