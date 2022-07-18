package fiji.plugin.SPTAnalysis.struct;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "Trajectory")
@XmlAccessorType(XmlAccessType.FIELD)
public class Point
{
	public double t;
	@XmlTransient
	public Integer frame;

	public double x;
	public double y;
	public double z;

	public Point()
	{
		this.t = Double.NaN;
		this.frame = null;
		this.x = Double.NaN;
		this.y = Double.NaN;
		this.z = Double.NaN;
	}

	public Point(double x, double y)
	{
		this.t = Double.NaN;
		this.frame = null;
		this.x = x;
		this.y = y;
		this.z = Double.NaN;
	}

	public Point(double t, double x, double y)
	{
		this.t = t;
		this.frame = null;
		this.x = x;
		this.y = y;
		this.z = Double.NaN;
	}

	public Point(double t, double x, double y, double z)
	{
		this.t = t;
		this.frame = null;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double[] vec()
	{
		return new double[] {this.x, this.y};
	}

	public void set(int dim, double v)
	{
		if (dim == 0)
			this.x = v;
		else if (dim == 1)
			this.y = v;
		else if (dim == 2)
			this.z = v;
	}
}