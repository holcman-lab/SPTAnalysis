package fiji.plugin.SPTAnalysis.writers;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.MyPolygon;
import fiji.plugin.SPTAnalysis.struct.Point;

public abstract class SVGWriter
{
	public static final Color blackColor = new Color(0, 0, 0);

	protected double zoomFactor;

	public SVGWriter(double zoomFactor)
	{
		this.zoomFactor = zoomFactor;
	}

	public abstract String generate();

	public abstract void setMinp(double[] minpt);
	public abstract double[] minp();
	public abstract double[] maxp();

	public double zoomFactor()
	{
		return this.zoomFactor;
	}

	public static String svgHeader(double width, double height)
	{
		return "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n" + 
			   String.format("<svg width=\"%gpx\" height=\"%gpx\">\n",
				width, height);
	}

	public static String writeEllipse(final Ellipse e, final double zoomFactor, final double[] minp, final Color col)
	{
		return String.format("<ellipse transform=\"rotate(%g %g %g)\" cx=\"%g\" cy=\"%g\" rx=\"%g\" ry=\"%g\" " +
							 "style=\"fill:none;stroke:rgb(%d, %d, %d);stroke-width:1px\" />\n",
				Math.toDegrees(e.phi()), (e.mu()[0]  - minp[0]) * zoomFactor, (e.mu()[1] - minp[1]) * zoomFactor,
				(e.mu()[0] - minp[0]) * zoomFactor, (e.mu()[1]  - minp[1]) * zoomFactor,
				e.rad()[0] * zoomFactor, e.rad()[1] * zoomFactor,
				col.getRed(), col.getGreen(), col.getBlue());
	}

	public static String writePolygon(final MyPolygon poly, final double zoomFactor, final double[] minp, final Color col)
	{
		StringBuilder res = new StringBuilder ();
		res.append(String.format("<path style=\"fill:none;stroke:rgb(%d,%d,%d);stroke-width:1px\" d=\"M ",
				col.getRed(), col.getGreen(), col.getBlue()));

			for (final Point p: poly.points())
				res.append(String.format("%.3f,%.3f ", (p.x - minp[0]) * zoomFactor,
													   (p.y - minp[1]) * zoomFactor));
			res.append("\"/>\n");
		return res.toString();
	}

	public static String svgFooter()
	{
		return "</svg>\n";
	}

	public static void saveSVG(String fname, SVGWriter[] writers, double[] minP) throws IOException
	{
		if (writers.length == 0)
			return;

		double[] maxSize = new double[] {0.0, 0.0};
		for (int i = 0; i < writers.length; ++i)
		{
			double[] mip = writers[i].minp();
			double[] map = writers[i].maxp();

			maxSize[0] = map[0] - mip[0] > maxSize[0] ? map[0] - mip[0] : maxSize[0];
			maxSize[1] = map[1] - mip[1] > maxSize[1] ? map[1] - mip[1] : maxSize[1];
		}

		BufferedWriter writer = new BufferedWriter(new FileWriter(fname));
		writer.write(svgHeader(maxSize[0] * writers[0].zoomFactor(), maxSize[1] * writers[0].zoomFactor()));

		for (SVGWriter wr: writers)
		{
			wr.setMinp(minP);
			writer.write(wr.generate());
		}

		writer.write(svgFooter());
		writer.close();
	}
}
