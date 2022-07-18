package fiji.plugin.SPTAnalysis.struct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "TrajectoryEnsemble")
@XmlAccessorType(XmlAccessType.FIELD)
public class TrajectoryEnsemble
{
	public static final TrajectoryEnsemble nullVal = new TrajectoryEnsemble();

	protected ArrayList<Trajectory> trajs;
	protected double minAcqDT;

	public TrajectoryEnsemble()
	{
		this.trajs = new ArrayList<Trajectory> ();
		this.minAcqDT = Double.NaN;
	}

	private void findMinDT()
	{
		Set<Double> dts = new TreeSet<> ();
		for (final Trajectory tr: this.trajs)
			for (final double[] disp: tr.displacements())
				dts.add(disp[0]);
		this.minAcqDT = Collections.min(dts);
	}

	public double acqDT()
	{
		if (Double.isNaN(this.minAcqDT))
			findMinDT();
		return this.minAcqDT;
	}

	public Trajectory findId(Integer id)
	{
		for (final Trajectory tr: this.trajs)
			if (tr.id() == id)
				return tr;

		return null;
	}

	public double[] min()
	{
		double[] res = {0.0, 0.0, 0.0};

		if (!this.trajs().isEmpty())
			res = this.trajs().get(0).min();

		for (final Trajectory tr: this.trajs())
		{
			double[] tmin = tr.min();
			res[0] = Math.min(res[0], tmin[0]);
			res[1] = Math.min(res[1], tmin[1]);
			res[2] = Math.min(res[2], tmin[2]);
		}

		return res;
	}

	public double[] max()
	{
		double[] res = {0.0, 0.0, 0.0};

		if (!this.trajs().isEmpty())
			res = this.trajs().get(0).max();

		for (final Trajectory tr: this.trajs())
		{
			double[] tmax = tr.max();
			res[0] = Math.max(res[0], tmax[0]);
			res[1] = Math.max(res[1], tmax[1]);
			res[2] = Math.max(res[2], tmax[2]);
		}

		return res;
	}

	public double[] time_interval()
	{
		double[] res = new double[] {99999999.0, 0.0};

		for (final Trajectory tr: this.trajs())
		{
			for (final Point p: tr.points())
			{
				if (p.t < res[0])
					res[0] = p.t;
				else if (p.t > res[1])
					res[1] = p.t;
			}
		}
		return res;
	}

	public ArrayList<Trajectory> trajs()
	{
		return this.trajs;
	}

	public Rectangle boundingRect()
	{
		double[] minPt = new double[] {Double.NaN, Double.NaN};
		double[] maxPt = new double[] {Double.NaN, Double.NaN};

		if (this.trajs.isEmpty())
			return null;
		assert(!this.trajs.get(0).points.isEmpty());

		minPt[0] = this.trajs.get(0).points().get(0).x;
		minPt[1] = this.trajs.get(0).points().get(0).y;
		maxPt[0] = this.trajs.get(0).points().get(0).x;
		maxPt[1] = this.trajs.get(0).points().get(0).y;

		for (final Trajectory traj: this.trajs)
		{
			for (final Point p: traj.points())
			{
				minPt[0] = p.x < minPt[0] ? p.x : minPt[0];
				minPt[1] = p.y < minPt[1] ? p.y : minPt[1];
				maxPt[0] = p.x > maxPt[0] ? p.x : maxPt[0];
				maxPt[1] = p.y > maxPt[1] ? p.y : maxPt[1];
			}
		}

		return new Rectangle(minPt, maxPt);
	}

	public Set<Integer> get_ids()
	{
		HashSet<Integer> res = new HashSet<>();
		for (final Trajectory traj: this.trajs)
			res.add(traj.id);
		return res;
	}

	public Set<Integer> get_from_ids()
	{
		HashSet<Integer> res = new HashSet<>();
		for (final Trajectory traj: this.trajs)
			res.add(traj.from);
		return res;
	}

	public ArrayList<Double> instantaneousVelocities()
	{
		ArrayList<Double> res = new ArrayList<> ();

		for (final Trajectory tr: this.trajs)
			for (double d: tr.instantaneousVelocities())
				res.add(d);

		return res;
	}

	public int numDisps()
	{
		int res = 0;
		for (final Trajectory tr: this.trajs)
			res += tr.points().size() - 1;
		return res;
	}
}
