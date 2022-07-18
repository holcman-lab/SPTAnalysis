package fiji.plugin.SPTAnalysis.struct;

import java.util.ArrayList;

public class TrajectoryProxy extends Trajectory
{
	ArrayList<Integer> ptsIds;
	Trajectory traj;

	TrajectoryProxy(ArrayList<Integer> ptsIds, Trajectory traj)
	{
		this.ptsIds = ptsIds;
		this.traj = traj;
	}

	public ArrayList<Point> points()
	{
		ArrayList<Point> res = new ArrayList<> ();
		for (Integer i: this.ptsIds)
			res.add(this.traj.points().get(i));
		return res;
	}
}
