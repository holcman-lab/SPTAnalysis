package fiji.plugin.SPTAnalysis.visualization;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.PotWell;
import fiji.plugin.SPTAnalysis.struct.PotWellsWindows;
import fiji.plugin.SPTAnalysis.wellLinker.WellLinker;
import fiji.plugin.trackmate.util.TMUtils;
import ij.ImagePlus;
import ij.gui.Roi;

public class WellOverlay extends Roi
{
	private static final long serialVersionUID = 1L;

	protected final double[] calib;
	protected PotWellsWindows wells;
	protected boolean showAllWindows;
	protected boolean colorByFamily;
	protected PotWell selectedWell;
	protected ArrayList<Color> colors;

	public WellOverlay(final ImagePlus imp, PotWellsWindows wells,
			boolean showAllWins, boolean colorByFamily)
	{
		super(0, 0, imp);
		this.calib = TMUtils.getSpatialCalibration(imp);
		this.wells = wells;
		this.showAllWindows = showAllWins;
		this.colorByFamily = colorByFamily;

		this.colors = null;
		if (wells.links() != null)
		{
			Random rand = new Random();
			this.colors = new ArrayList<> ();
			for (int i = 0; i < wells.links().size(); ++i)
				this.colors.add(new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
		}
	}

	public void setShowAllWindows(boolean val)
	{
		this.showAllWindows = val;
	}

	public void setColorByFamily(boolean val)
	{
		this.colorByFamily = val;
	}

	public void setSelected(final PotWell val)
	{
		this.selectedWell = val;
	}

	@Override
	public void drawOverlay(final Graphics g)
	{
		final Graphics2D g2d = (Graphics2D) g;
		int frame = this.imp.getFrame() - 1;

		final double magn = getMagnification();
		final int xcorn = ic.offScreenX(0);
		final int ycorn = ic.offScreenY(0);

		g2d.setStroke(new BasicStroke(2.0f));
		g2d.setColor(Color.red);

		ArrayList<Integer> todo = new ArrayList<> ();
		if (this.showAllWindows)
			for (int k = 0; k < this.wells.wins.size(); ++k)
				todo.add(k);
		else
			todo.add(frame);

		for (int curFrame: todo)
		{
			for (int k = 0; k < this.wells.wins.get(curFrame).wells.size(); ++k)
			{
				PotWell w = this.wells.wins.get(curFrame).wells.get(k);
				Ellipse e = w.ell();


				if (w == this.selectedWell)
				{
					g2d.setColor(Color.MAGENTA);
					g2d.setStroke(new BasicStroke(3.0f));
				}

				else if (this.colorByFamily && this.colors != null)
				{
					g2d.setColor(this.colors.get(WellLinker.findFamily(
							new WellLinker.WindowIndex(curFrame, k), this.wells.links())));
				}
				else
				{
					g2d.setColor(Color.red);
					g2d.setStroke(new BasicStroke(2.0f));
				}

				double deps = Math.PI / 20;
				int N = (int) Math.ceil(2*Math.PI/deps) + 1;
				int[] xps = new int[N];
				int[] yps = new int[N];

				int i = 0;
				for (double theta = 0; theta <= 2*Math.PI; theta += deps)
				{
					double tmpx = e.mu()[0] + e.rad()[0] * Math.cos(theta) * Math.cos(e.phi()) -
											  e.rad()[1] * Math.sin(theta) * Math.sin(e.phi()); 
					double tmpy = e.mu()[1] + e.rad()[0] * Math.cos(theta) * Math.sin(e.phi()) +
							 				  e.rad()[1] * Math.sin(theta) * Math.cos(e.phi());

					xps[i] = (int) Math.floor((tmpx / this.calib[0] - xcorn) * magn);
					yps[i] = (int) Math.floor((tmpy / this.calib[0] - ycorn) * magn);

					++i;
				}

				g2d.drawPolygon(xps, yps, N);
			}
		}
	}
}