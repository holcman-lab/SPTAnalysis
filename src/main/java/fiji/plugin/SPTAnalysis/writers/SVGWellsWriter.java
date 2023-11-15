package fiji.plugin.SPTAnalysis.writers;

import java.awt.Color;

import fiji.plugin.SPTAnalysis.struct.PotWell;

public class SVGWellsWriter extends SVGWriter
{
	protected final PotWell[] wells;
	protected double[] minp;
	protected Color col;

	public SVGWellsWriter(PotWell[] wells, double zoomFactor)
	{
		super(zoomFactor);
		this.wells = wells;
		this.col = Color.RED;
	}

	public void color(Color col)
	{
		this.col = col;
	}

	@Override
	public void setMinp(double[] minpt)
	{
		this.minp = minpt;
	}

	@Override
	public String generate()
	{
		StringBuilder res = new StringBuilder ();
		res.append("<g>\n");
		for (PotWell w: this.wells)
			res.append(writeEllipse(w.ell(), this.zoomFactor, this.minp, this.col));
		res.append("</g>\n");
		return res.toString();
	}

	@Override
	public double[] minp()
	{
		double[] minEll = new double[] {Double.NaN, Double.NaN};
		if (this.wells.length == 0)
			return minEll;
		minEll = new double[] {this.wells[0].ell().minPt()[0],
							   this.wells[0].ell().minPt()[1]};
		for (int i = 1; i < this.wells.length; ++i)
		{
			double[] tmp = new double[] {this.wells[i].ell().minPt()[0],
										 this.wells[i].ell().minPt()[1]};
			minEll[0] = tmp[0] < minEll[0] ? tmp[0] : minEll[0];
			minEll[1] = tmp[1] < minEll[1] ? tmp[1] : minEll[1];
		}
		return minEll;
	}

	@Override
	public double[] maxp()
	{
		double[] maxEll = new double[] {Double.NaN, Double.NaN};
		if (this.wells.length == 0)
			return maxEll;
		maxEll = new double[] {this.wells[0].ell().maxPt()[0],
							   this.wells[0].ell().maxPt()[1]};
		for (int i = 1; i < this.wells.length; ++i)
		{
			double[] tmp = new double[] {this.wells[i].ell().maxPt()[0],
										 this.wells[i].ell().maxPt()[1]};
			maxEll[0] = tmp[0] > maxEll[0] ? tmp[0] : maxEll[0];
			maxEll[1] = tmp[1] > maxEll[1] ? tmp[1] : maxEll[1];
		}
		return maxEll;
	}
}
