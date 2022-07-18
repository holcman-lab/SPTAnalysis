package fiji.plugin.SPTAnalysis.visualization;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;

import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.Graph;
import fiji.plugin.SPTAnalysis.struct.GraphWindows;
import fiji.plugin.SPTAnalysis.struct.MyPolygon;
import fiji.plugin.SPTAnalysis.struct.Shape;
import fiji.plugin.trackmate.util.TMUtils;
import fiji.plugin.SPTAnalysis.graphConstruction.GraphConstructionParameters;

import ij.ImagePlus;
import ij.gui.Roi;

public class GraphOverlay extends Roi
{
	private static final long serialVersionUID = 1L;

	protected final double[] calib;
	private final GraphWindows graphs;
	private final int minDispLink;
	private Shape selectedNode;
	private boolean showAllWindows;

	public GraphOverlay(final ImagePlus imp, final GraphWindows graphs, final int minDispLink,
						boolean showAllWins)
	{
		super(0, 0, imp);
		this.calib = TMUtils.getSpatialCalibration(imp);
		this.graphs = graphs;
		this.minDispLink = minDispLink;
		this.selectedNode = null;
		this.showAllWindows = showAllWins;
	}

	public void setSelected(final Shape val)
	{
		this.selectedNode = val;
	}

	private void drawEllipse(final Graphics2D g2d, final Ellipse e,
			double deps, int xcorn, int ycorn, double magn)
	{
		int N = (int) Math.ceil(2*Math.PI/deps) + 1;
		int[] xps = new int[N];
		int[] yps = new int[N];

		int i = 0;
		for (double theta = 0; theta <= 2*Math.PI; theta += deps)
		{
			double tmpx = e.mu()[0] + e.rad()[0] * Math.cos(theta) * Math.cos(e.phi())
									- e.rad()[1] * Math.sin(theta) * Math.sin(e.phi()); 
			double tmpy = e.mu()[1] + e.rad()[0] * Math.cos(theta) * Math.sin(e.phi())
									+ e.rad()[1] * Math.sin(theta) * Math.cos(e.phi());

			xps[i] = (int) Math.floor((tmpx / this.calib[0] - xcorn) * magn);
			yps[i] = (int) Math.floor((tmpy / this.calib[0] - ycorn) * magn);

			++i;
		}
		g2d.drawPolygon(xps, yps, N);
	}

	private void drawPolygon(final Graphics2D g2d, final MyPolygon mp,
			int xcorn, int ycorn, double magn)
	{
		final Polygon p = mp.poly();
		int N = p.getNumPoints();
		int[] xps = new int[N];
		int[] yps = new int[N];

		int i = 0;
		for (final Coordinate c: p.getCoordinates())
		{
			xps[i] = (int) Math.floor((c.x / this.calib[0] - xcorn) * magn);
			yps[i] = (int) Math.floor((c.y / this.calib[0] - ycorn) * magn);
			++i;
		}
		g2d.drawPolygon(xps, yps, N);
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

		double deps = Math.PI / 20;
		

		ArrayList<Integer> todo = new ArrayList<> ();
		if (this.showAllWindows)
			for (int k = 0; k < this.graphs.wins.size(); ++k)
				todo.add(k);
		else
			todo.add(frame);
		
		for (int curFrame: todo)
		{
			final Graph graph = this.graphs.wins.get(curFrame);
			for (Shape ee: graph.nodes().values())
			{
				if (ee == this.selectedNode)
				{
					g2d.setColor(Color.MAGENTA);
					g2d.setStroke(new BasicStroke(3.0f));
				}
				else
				{
					g2d.setStroke(new BasicStroke(2.0f));
					g2d.setColor(Color.red);
				}

				if (graph.nodeT() == GraphConstructionParameters.NodeType.ELLIPSE)
					drawEllipse(g2d, (Ellipse) ee, deps, xcorn, ycorn, magn);
				else if (graph.nodeT() == GraphConstructionParameters.NodeType.POLY)
					drawPolygon(g2d, (MyPolygon) ee, xcorn, ycorn, magn);
			}
	
	
			g2d.setColor(new Color(255, 69, 0));
			for (Integer i: graph.nodes().keySet())
			{
				for (Integer j: graph.nodes().keySet())
				{
					if (j == 0 || i <= j)
						continue;
	
					if (graph.connect(i, j) > this.minDispLink)
					{
						Shape n1 = graph.nodes().get(i);
						Shape n2 = graph.nodes().get(j);
	
						double[] p1 = null;
						double[] p2 = null;
						if (graph.nodeT() == GraphConstructionParameters.NodeType.ELLIPSE)
						{
							p1 = ((Ellipse) n1).centerToCenterIntersect((Ellipse) n2);
							p2 = ((Ellipse) n2).centerToCenterIntersect((Ellipse) n1);
						}
						else
						{
							p1 = ((MyPolygon) n1).centerToCenterIntersect((MyPolygon) n2);
							p2 = ((MyPolygon) n2).centerToCenterIntersect((MyPolygon) n1);
						}

						g2d.drawLine((int) Math.floor((p1[0] / this.calib[0] - xcorn) * magn),
									 (int) Math.floor((p1[1] / this.calib[0] - ycorn) * magn),
									 (int) Math.floor((p2[0] / this.calib[0] - xcorn) * magn),
									 (int) Math.floor((p2[1] / this.calib[0] - ycorn) * magn));
					}
				}
			}
		}
	}
}
