package fiji.plugin.SPTAnalysis.writers;

import java.awt.Color;
import java.util.Iterator;
import java.util.Set;

import fiji.plugin.SPTAnalysis.graphConstruction.GraphConstructionParameters;
import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.Graph;
import fiji.plugin.SPTAnalysis.struct.MyPolygon;
import fiji.plugin.SPTAnalysis.struct.Shape;

public class SVGGraphWriter extends SVGWriter
{
	private final Graph g;
	protected double[] minp;

	public SVGGraphWriter(final Graph g, double zoomFactor)
	{
		super(zoomFactor);
		this.g = g;
	}

	@Override
	public String generate()
	{
		Color col = new Color(0, 0, 0);
		Color nodeCol = Color.RED;

		StringBuilder res = new StringBuilder ();
		if (g.nodeT() == GraphConstructionParameters.NodeType.ELLIPSE)
		{
			for (int k: this.g.nodes().keySet())
				res.append(SVGWriter.writeEllipse((Ellipse) this.g.nodes().get(k),
						this.zoomFactor, this.minp, nodeCol));
		}
		else
		{
			for (int k: this.g.nodes().keySet())
				res.append(SVGWriter.writePolygon((MyPolygon) this.g.nodes().get(k),
						this.zoomFactor, this.minp, nodeCol));
		}

		for (int i: this.g.nodes().keySet())
		{
			for (int j: this.g.nodes().keySet())
			{
				if (j == 0 || i <= j || this.g.connect(i, j) == 0)
					continue;

				double[] p1 = null;
				double[] p2 = null;
				Shape n1 = this.g.nodes().get(i);
				Shape n2 = this.g.nodes().get(j);
				if (g.nodeT() == GraphConstructionParameters.NodeType.ELLIPSE)
				{
					p1 = ((Ellipse) n1).centerToCenterIntersect((Ellipse) n2);
					p2 = ((Ellipse) n2).centerToCenterIntersect((Ellipse) n1);
				}
				else
				{
					p1 = ((MyPolygon) n1).centerToCenterIntersect((MyPolygon) n2);
					p2 = ((MyPolygon) n2).centerToCenterIntersect((MyPolygon) n1);
				}

				res.append(String.format("<path style=\"fill:none;stroke:rgb(%d,%d,%d);stroke-width:%gpx\" d=\"M %.3f,%.3f %.3f,%.3f\"/>\n",
						col.getRed(), col.getGreen(), col.getBlue(), 1.0,
						(p1[0] - this.minp[0]) * this.zoomFactor,
						(p1[1] - this.minp[1]) * this.zoomFactor,
						(p2[0] - this.minp[0]) * this.zoomFactor,
						(p2[1] - this.minp[1]) * this.zoomFactor));
			}
		}

		return res.toString();
	}

	@Override
	public void setMinp(double[] minpt)
	{
		this.minp = minpt;
	}

	@Override
	public double[] minp()
	{
		double[] minV = new double[] {Double.NaN, Double.NaN};
		if (this.g.nodes().isEmpty())
			return minV;

		Set<Integer> keys = g.nodes().keySet();
		Iterator<Integer> it = keys.iterator();

		Shape s = g.nodes().get(it.next());
		double[] mPt = s.minPt();
		minV = new double[] {mPt[0] * this.zoomFactor, mPt[1] * this.zoomFactor};
		while (it.hasNext())
		{
			s = g.nodes().get(it.next());
			mPt = s.minPt();
			double[] tmp = new double[] {mPt[0] * this.zoomFactor, mPt[1] * this.zoomFactor};
			minV[0] = tmp[0] < minV[0] ? tmp[0] : minV[0];
			minV[1] = tmp[1] < minV[1] ? tmp[1] : minV[1];
		}
		return minV;
	}

	@Override
	public double[] maxp()
	{
		double[] maxV = new double[] {Double.NaN, Double.NaN};
		if (this.g.nodes().isEmpty())
			return maxV;

		Set<Integer> keys = g.nodes().keySet();
		Iterator<Integer> it = keys.iterator();

		Shape s = g.nodes().get(it.next());
		double[] MPt = s.maxPt();
		maxV = new double[] {MPt[0], MPt[1]};
		while (it.hasNext())
		{
			s = g.nodes().get(it.next());
			MPt = s.maxPt();
			double[] tmp = new double[] {MPt[0], MPt[1]};
			maxV[0] = tmp[0] > maxV[0] ? tmp[0] : maxV[0];
			maxV[1] = tmp[1] > maxV[1] ? tmp[1] : maxV[1];
		}
		return maxV;
	}
}
