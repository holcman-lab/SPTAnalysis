package fiji.plugin.SPTAnalysis.visualization;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Iterator;

import fiji.plugin.SPTAnalysis.struct.ScalarMapWindows;
import fiji.plugin.trackmate.util.TMUtils;
import ij.ImagePlus;
import ij.gui.Roi;

public class ScalarMapOverlay extends Roi
{
	private static final long serialVersionUID = 1L;

	protected final double[] calib;
	protected final ScalarMapWindows maps;
	protected double[] maps_max;
	protected InterpolatePaintScale cm;
	protected final boolean showAllWins;

	public ScalarMapOverlay(final ImagePlus imp, final ScalarMapWindows maps, boolean showAllWins)
	{
		super(0, 0, imp);
		this.calib = TMUtils.getSpatialCalibration(imp);
		this.maps = maps;
		this.maps_max = new double[maps.wins.size()];
		for (int i = 0; i < maps.wins.size(); ++i)
			this.maps_max[i] = maps.wins.get(i).max();
		this.cm = InterpolatePaintScale.Jet;
		this.showAllWins = showAllWins;
	}

	@Override
	public void drawOverlay(final Graphics g)
	{
		final Graphics2D g2d = (Graphics2D) g;

		int frame = 0;
		if (!this.showAllWins)
			frame = this.imp.getFrame() - 1;

		final double magn = getMagnification();
		final int xcorn = ic.offScreenX(0);
		final int ycorn = ic.offScreenY(0);

		double dx = this.maps.wins.get(frame).grid().dx();

		Iterator<double[]> it = this.maps.wins.get(frame).iterator();
		while (it.hasNext())
		{
			double[] v = it.next();
			double[] pos = this.maps.wins.get(frame).grid().get((int) v[0], (int) v[1]);

			int px = (int) Math.floor(((pos[0] - dx/2) / this.calib[0] - xcorn) * magn);
			int py = (int) Math.floor(((pos[1] - dx/2) / this.calib[1] - ycorn) * magn);
			int w = (int) ((this.maps.wins.get(frame).grid().dx() / this.calib[1]) * magn);

			g2d.setColor(this.cm.getPaint(v[2] / this.maps_max[frame]));
			g2d.fillRect(px, py, w, w);
		}
	}
}
