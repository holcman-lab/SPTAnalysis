package fiji.plugin.SPTAnalysis.writers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import fiji.plugin.SPTAnalysis.struct.VectorMap;

public class SVGVectorMapWriter extends SVGWriter
{
	protected final VectorMap map;
	protected final ArrayList<int[]> nhs;

	protected double scaleFactor;

	protected double[] minp;
	protected final double[] maxp;

	public SVGVectorMapWriter(VectorMap map, double scaleFactor, double zoomFactor)
	{
		super(zoomFactor);

		this.map = map;
		this.nhs = null;

		this.scaleFactor = scaleFactor;

		this.minp = map.grid().Xmin();
		this.maxp = map.grid().Xmax();
	}

	public SVGVectorMapWriter(VectorMap map, double scaleFactor, double zoomFactor, ArrayList<int[]> nhs)
	{
		super(zoomFactor);

		this.map = map;
		this.nhs = nhs;

		this.scaleFactor = scaleFactor;

		this.minp = map.grid().Xmin();
		this.maxp = map.grid().Xmax();
	}

	@Override
	public void setMinp(double[] minpt)
	{
		this.minp = minpt;
	}

	protected Color selectColor(double ang)
	{
		if (ang >= 7*Math.PI/4 || ang < Math.PI/4)
			return new Color(255, 0, 255);
		else if (ang >= Math.PI/4 && ang < 3*Math.PI/4)
			return new Color(255, 0, 0);
		else if (ang >= 3*Math.PI/4 && ang < 5*Math.PI/4)
			return new Color(0, 255, 0);
		else
			return new Color(0, 255, 255);
	}

	protected String selectMarker(double ang)
	{
		if (ang >= 7*Math.PI/4 || ang < Math.PI/4)
			return "arrow1";
		else if (ang >= Math.PI/4 && ang < 3*Math.PI/4)
			return "arrow2";
		else if (ang >= 3*Math.PI/4 && ang < 5*Math.PI/4)
			return "arrow3";
		else
			return "arrow4";
	}

	@Override
	public String generate()
	{
		StringBuilder res = new StringBuilder ();

		res.append("<g>\n");
		res.append("<defs><marker id=\"arrow1\" markerWidth=\"0.025\" markerHeight=\"10\" refX=\"0\" refY=\"3\" " +
				"orient=\"auto\" markerUnits=\"strokeWidth\"><path d=\"M0,0 L0,6 L9,3 z\" fill=\"rgb(255,0,255)\"/></marker></defs>\n");
		res.append("<defs><marker id=\"arrow2\" markerWidth=\"0.025\" markerHeight=\"10\" refX=\"0\" refY=\"3\" " +
				"orient=\"auto\" markerUnits=\"strokeWidth\"><path d=\"M0,0 L0,6 L9,3 z\" fill=\"rgb(255,0,0)\"/></marker></defs>\n");
		res.append("<defs><marker id=\"arrow3\" markerWidth=\"0.025\" markerHeight=\"10\" refX=\"0\" refY=\"3\" " +
				"orient=\"auto\" markerUnits=\"strokeWidth\"><path d=\"M0,0 L0,6 L9,3 z\" fill=\"rgb(0,255,0)\"/></marker></defs>\n");
		res.append("<defs><marker id=\"arrow4\" markerWidth=\"0.025\" markerHeight=\"10\" refX=\"0\" refY=\"3\" " +
				"orient=\"auto\" markerUnits=\"strokeWidth\"><path d=\"M0,0 L0,6 L9,3 z\" fill=\"rgb(0,255,255)\"/></marker></defs>\n");

		res.append(String.format("<!--%s\n-->\n", map.params().toString()));
		if (this.nhs == null)
		{
			Iterator<double[]> it = map.iterator();
			while (it.hasNext())
			{
				double[] v = it.next();

				if (v[2] == 0.0 && v[3] == 0.0)
					continue;

				double[] pos = map.grid().get((int) v[0], (int) v[1]);

				double[] dr = new double[] {v[2] / Math.sqrt(v[2]*v[2] + v[3]*v[3]),
											v[3] / Math.sqrt(v[2]*v[2] + v[3]*v[3])};

				double ang = Math.atan2(dr[1], dr[0]) + Math.PI;
				Color col = this.selectColor(ang);
				String marker = this.selectMarker(ang);

				res.append(String.format("<line x1=\"%g\" y1=\"%g\" x2=\"%g\" y2=\"%g\" style=\"stroke:rgb(%d,%d,%d);stroke-width:1px;marker-end:url(#%s)\"/>\n",
						(pos[0] - this.minp[0]) * this.zoomFactor,
						(pos[1] - this.minp[1]) * this.zoomFactor,
						((pos[0] + v[2] * this.scaleFactor) - this.minp[0]) * this.zoomFactor,
						((pos[1] + v[3] * this.scaleFactor) - this.minp[1]) * this.zoomFactor,
						col.getRed(), col.getGreen(), col.getBlue(), marker));
			}
		}
		else
		{
			for (final int[] nh: nhs)
			{
				if (!this.map.isSet(nh[0], nh[1]))
					continue;

				Double[] v = this.map.get(nh[0], nh[1]);

				if (v[0] == 0.0 && v[1] == 0.0)
					continue;

				double[] pos = map.grid().get(nh[0], nh[1]);

				double[] dr = new double[] {v[0] / Math.sqrt(v[0]*v[0] + v[1]*v[1]),
											v[1] / Math.sqrt(v[0]*v[0] + v[1]*v[1])};

				double ang = Math.atan2(dr[1], dr[0]) + Math.PI;
				Color col = this.selectColor(ang);
				String marker = this.selectMarker(ang);

				res.append(String.format("<line x1=\"%g\" y1=\"%g\" x2=\"%g\" y2=\"%g\" style=\"stroke:rgb(%d,%d,%d);stroke-width:1px;marker-end:url(#%s)\"/>\n",
						(pos[0] - this.minp[0]) * this.zoomFactor,
						(pos[1] - this.minp[1]) * this.zoomFactor,
						((pos[0] + v[0] * this.scaleFactor) - this.minp[0]) * this.zoomFactor,
						((pos[1] + v[1] * this.scaleFactor) - this.minp[1]) * this.zoomFactor,
						col.getRed(), col.getGreen(), col.getBlue(), marker));
			}
		}
		res.append("</g>\n");

		return res.toString();
	}

	@Override
	public double[] minp()
	{
		return new double[] {this.minp[0], this.minp[1]};
	}

	@Override
	public double[] maxp()
	{
		return new double[] {this.maxp[0], this.maxp[1]};
	}
}
