package fiji.plugin.SPTAnalysis.visualization;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Iterator;

import fiji.plugin.SPTAnalysis.struct.VectorMapWindows;
import fiji.plugin.trackmate.util.TMUtils;
import ij.ImagePlus;
import ij.gui.Roi;

public class VectorMapOverlay extends Roi
{
	private static final long serialVersionUID = 1L;

	protected final double[] calib;
	protected final VectorMapWindows maps;
	protected double[] maps_max;
	protected double sizeMult;
	protected final boolean showAllWins;

	public VectorMapOverlay(final ImagePlus imp, final VectorMapWindows maps, double sizeMult,  boolean showAllWins)
	{
		super(0, 0, imp);
		this.calib = TMUtils.getSpatialCalibration(imp);
		this.maps = maps;
		this.maps_max = new double[maps.wins.size()];
		for (int i = 0; i < maps.wins.size(); ++i)
			this.maps_max[i] = maps.wins.get(i).max();
		this.sizeMult = sizeMult;
		this.showAllWins = showAllWins;
	}

	@Override
	public void drawOverlay(final Graphics g)
	{
		final Graphics2D g2d = (Graphics2D) g;

		int frame = 0;
		if (!showAllWins)
			frame = this.imp.getFrame() - 1;

		final double magn = getMagnification();
		final int xcorn = ic.offScreenX(0);
		final int ycorn = ic.offScreenY(0);

		double alpha = 0.33; //Size of arrow head relative to the length of the vector
		double beta = 0.33;  //Width of the base of the arrow head relative to the
		double eps = 0.0001;

		Iterator<double[]> it = this.maps.wins.get(frame).iterator();
		while (it.hasNext())
		{
			double[] v = it.next();
			double[] pos = this.maps.wins.get(frame).grid().get((int) v[0], (int) v[1]);
			double[] dpos = new double[] {v[2], v[3]};

			double[] dposNorm = new double[] {dpos[0] / Math.sqrt(dpos[0]*dpos[0] + dpos[1]*dpos[1]),
											  dpos[1] / Math.sqrt(dpos[0]*dpos[0] + dpos[1]*dpos[1])};

			int px = (int) Math.floor((pos[0] / this.calib[0] - xcorn) * magn);
			int py = (int) Math.floor((pos[1] / this.calib[1] - ycorn) * magn);

			int px2 = (int) Math.floor(((pos[0] + dpos[0] * this.sizeMult) / this.calib[0] - xcorn) * magn);
			int py2 = (int) Math.floor(((pos[1] + dpos[1] * this.sizeMult) / this.calib[1] - ycorn) * magn);

			double ang = Math.atan2(dposNorm[1], dposNorm[0]) + Math.PI;

			Color col = null;
			if (ang >= 7*Math.PI/4 || ang < Math.PI/4)
				col = new Color(255, 0, 255);
			else if (ang >= Math.PI/4 && ang < 3*Math.PI/4)
				col = new Color(255, 0, 0);
			else if (ang >= 3*Math.PI/4 && ang < 5*Math.PI/4)
				col = new Color(0, 255, 0);
			else
				col = new Color(0, 255, 255);

			int darr1x = (int) Math.floor((alpha * (dpos[0] + beta*(dpos[1]+eps)) * this.sizeMult / this.calib[0]) * magn);
			int darr1y = (int) Math.floor((alpha * (dpos[1] - beta*(dpos[0]+eps)) * this.sizeMult / this.calib[1]) * magn);

			int darr2x = (int) Math.floor((alpha * (dpos[0] - beta*(dpos[1]+eps)) * this.sizeMult / this.calib[0]) * magn);
			int darr2y = (int) Math.floor((alpha * (dpos[1] + beta*(dpos[0]+eps)) * this.sizeMult / this.calib[1]) * magn);

			g2d.setColor(col);
			g2d.setStroke(new BasicStroke(3));
			g2d.drawLine(px, py, px2, py2);
			g2d.drawLine(px2, py2, px2 - darr1x, py2 - darr1y);
			g2d.drawLine(px2, py2, px2 - darr2x, py2 - darr2y);
		}
	}
}
