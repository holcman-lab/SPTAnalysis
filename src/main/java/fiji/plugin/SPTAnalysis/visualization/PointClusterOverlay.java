package fiji.plugin.SPTAnalysis.visualization;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import fiji.plugin.SPTAnalysis.struct.Point;
import fiji.plugin.trackmate.util.TMUtils;
import ij.ImagePlus;
import ij.gui.Roi;

public class PointClusterOverlay extends Roi
{
	private static final long serialVersionUID = 1L;
	final protected HashMap<Integer, ArrayList<Point>> clustPts;
	final protected HashMap<Integer, Color> clustCols;

	public PointClusterOverlay(final ImagePlus imp, final HashMap<Integer, ArrayList<Point>> clustPts)
	{
		super(0, 0, imp);
		this.clustPts = clustPts;
		this.clustCols = new HashMap<Integer, Color> ();

		Random rnd = new Random();
		for (final Integer i: clustPts.keySet())
			this.clustCols.put(i, new Color(rnd.nextInt(256),
					rnd.nextInt(256), rnd.nextInt(256)));
		
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter("/tmp/pts.csv"));
			for (ArrayList<Point> pts: clustPts.values())
				for (Point p: pts)
					writer.write(String.format("%.5f %.5f\n", p.x, p.y));
			writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void drawOverlay(final Graphics g)
	{
		final Graphics2D g2d = (Graphics2D) g;

		double[] calib = TMUtils.getSpatialCalibration(imp);
		final double magn = getMagnification();
		final int xcorn = ic.offScreenX(0);
		final int ycorn = ic.offScreenY(0);

		//int frame = this.imp.getFrame() - 1;

		for (final Integer I: clustPts.keySet())
		{
			final ArrayList<Point> pts = this.clustPts.get(I);

			g2d.setColor(this.clustCols.get(I));
			for (final Point p: pts)
			{
				int xp = (int) Math.floor((p.x / calib[0] - xcorn) * magn);
				int yp = (int) Math.floor((p.y / calib[1] - ycorn) * magn);
				g2d.drawLine(xp, yp, xp, yp);
			}
		}
	}
}
