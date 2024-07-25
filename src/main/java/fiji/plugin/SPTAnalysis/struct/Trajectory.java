package fiji.plugin.SPTAnalysis.struct;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import fiji.plugin.SPTAnalysis.XMLAdapters;


@XmlRootElement(name = "Trajectory")
@XmlAccessorType(XmlAccessType.FIELD)

public class Trajectory
{
	@XmlAttribute
	protected Integer id;

	@XmlAttribute
	protected Integer from;

	@XmlJavaTypeAdapter(XMLAdapters.TrajectoryAdapter.class)
	protected ArrayList<Point> points;

	public static Trajectory subTrajStartingAt(final Trajectory traj, int first)
	{
		Trajectory newT = new Trajectory(-1, traj.id());
		for (int i = first; i < traj.points().size(); ++i)
			newT.points().add(traj.points().get(i));
		return newT;
	}

	public Trajectory()
	{
		this.id = null;
		this.from = null;
		this.points = new ArrayList<Point> ();
	}

	public Trajectory(Integer id)
	{
		this.id = id;
		this.from = null;
		this.points = new ArrayList<Point> ();
	}

	public Trajectory(Integer id, Integer from)
	{
		this.id = id;
		this.from = from;
		this.points = new ArrayList<Point> ();
	}

	public boolean isEmpty()
	{
		return this.points.isEmpty();
	}

	public ArrayList<double[]> displacements()
	{
		ArrayList<double[]> res = new ArrayList<double[]> ();

		for (int i = 0; i < this.points.size() - 1; ++i)
			res.add(new double[] {this.points.get(i+1).t - this.points.get(i).t,
								  this.points.get(i+1).x - this.points.get(i).x,
								  this.points.get(i+1).y - this.points.get(i).y});

		return res;
	}

	public double[] instantaneousVelocities()
	{
		double[] res = new double[this.points.size() - 1];

		for (int i = 0; i < this.points.size() - 1; ++i)
			res[i] = Math.sqrt(Math.pow(this.points.get(i+1).x - this.points.get(i).x, 2) + 
							   Math.pow(this.points.get(i+1).y - this.points.get(i).y, 2))/
								(this.points.get(i+1).t - this.points.get(i).t);

		return res;
	}

	public double[] center_of_mass()
	{
		double[] res = new double[] {0.0, 0.0};

		for (Point p: this.points())
		{
			res[0] += p.x;
			res[1] += p.y;
		}
		res[0] /= this.points.size();
		res[1] /= this.points.size();

		return res;
	}

	public double[] min()
	{
		double[] res = {0.0, 0.0, 0.0};

		if (!points.isEmpty())
			res = new double[] {this.points().get(0).x, this.points().get(0).y,
								this.points().get(0).z};

		for (Point p: this.points())
		{
			res[0] = Math.min(res[0], p.x);
			res[1] = Math.min(res[1], p.y);
			res[2] = Math.min(res[2], p.z);
		}

		return res;
	}

	public double[] max()
	{
		double[] res = {0.0, 0.0, 0.0};

		if (!points.isEmpty())
			res = new double[] {this.points().get(0).x, this.points().get(0).y,
								this.points().get(0).z};

		for (Point p: this.points())
		{
			res[0] = Math.max(res[0], p.x);
			res[1] = Math.max(res[1], p.y);
			res[2] = Math.max(res[2], p.z);
		}

		return res;
	}

	public Integer id()
	{
		return this.id;
	}

	public ArrayList<Point> points()
	{
		return this.points;
	}
}
