package fiji.plugin.SPTAnalysis.visualization;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import fiji.plugin.trackmate.util.TMUtils;
import ij.ImagePlus;
import ij.gui.Roi;

public class ColorBarOverlay extends Roi
{
	private static final long serialVersionUID = 1L;

	protected final double[] calib;
	protected final String fmt;
	protected final double[] maxs;
	protected InterpolatePaintScale cm;
	protected final boolean showAllWins;

	public ColorBarOverlay(final ImagePlus imp, String fmt, final double[] maxs, boolean showAllWins)
	{
		super(0, 0, imp);
		this.calib = TMUtils.getSpatialCalibration(imp);
		this.fmt = fmt;
		this.maxs = maxs;

		this.cm = InterpolatePaintScale.Jet;
		this.showAllWins = showAllWins;
	}

	@Override
	public void drawOverlay(final Graphics g)
	{
		final Graphics2D g2d = (Graphics2D) g;
		final Rectangle bnds = ic.getBounds();

		int width = 70;
		for ( int i = 0; i < width; i++ )
		{
			g.setColor(this.cm.getPaint(i / (width - 1.0)));
			g.drawLine(35 + i, bnds.height - 20, 35 + i, bnds.height - 40);
		}

		int frame = 0;
		if (!this.showAllWins)
			frame = this.imp.getFrame() - 1;

		g.setColor(Color.WHITE);
		int textWidth = g.getFontMetrics().stringWidth(String.format(this.fmt, 0.0));
		g2d.drawString(String.format(this.fmt, 0.0), 32 - textWidth, bnds.height - 24);
		g2d.drawString(String.format(this.fmt, this.maxs[frame]), 35 + width, bnds.height - 24);
	}
}
