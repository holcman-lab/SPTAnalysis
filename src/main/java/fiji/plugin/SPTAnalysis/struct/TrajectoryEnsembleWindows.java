package fiji.plugin.SPTAnalysis.struct;

import java.util.ArrayList;

public class TrajectoryEnsembleWindows
{
	public ArrayList<TrajectoryEnsemble> wins;

	public TrajectoryEnsembleWindows()
	{
		this.wins = new ArrayList<TrajectoryEnsemble> ();
	}

	public TrajectoryEnsembleWindows(TrajectoryEnsemble trajs, TimeWindows tws)
	{
		this.wins = new ArrayList<TrajectoryEnsemble> ();

		for (int i = 0; i <= tws.idxMax(); ++i)
			this.wins.add(new TrajectoryEnsemble());

		int cpt = 0;
		for (Trajectory tr: trajs.trajs)
		{
			Integer prevIdx = null;
			for (Point p: tr.points)
			{
				int idx = tws.getIdx(p.t);

				if (prevIdx == null || !prevIdx.equals(idx))
				{
					prevIdx = idx;
					this.wins.get(idx).trajs.add(new Trajectory(cpt, tr.id));
					cpt = cpt + 1;
				}
				this.wins.get(idx).trajs.get(this.wins.get(idx).trajs.size()-1).points.add(p);
			}
		}
	}

	public double[] maxCoords()
	{
		double[] res = {0.0, 0.0, 0.0};

		if (!this.wins.isEmpty() && !this.wins.get(0).trajs().isEmpty())
			res = this.wins.get(0).trajs().get(0).max();

		for (TrajectoryEnsemble te: this.wins)
		{
			for (Trajectory tr: te.trajs())
			{
				double[] tmax = tr.max();
				res[0] = Math.max(res[0], tmax[0]);
				res[1] = Math.max(res[1], tmax[1]);
				res[2] = Math.max(res[2], tmax[2]);
			}
		}

		return res;
	}

	public ArrayList<ArrayList<Double>> instantaneousVelocities()
	{
		ArrayList<ArrayList<Double>> res = new ArrayList<>();

		for (final TrajectoryEnsemble te: this.wins)
			res.add(te.instantaneousVelocities());

		return res;
	}

	public ArrayList<ArrayList<double[]>> instantaneousVelocitiesPositions()
	{
		ArrayList<ArrayList<double[]>> res = new ArrayList<>();

		for (final TrajectoryEnsemble te: this.wins)
		{
			res.add(new ArrayList<> ());
			for (Trajectory tr: te.trajs())
				for (int i = 0; i < tr.points().size(); ++i)
					res.get(res.size() - 1).add(tr.points().get(i).vec());
		}

		return res;
	}

	public TrajectoryEnsemble flatten()
	{
		TrajectoryEnsemble res = new TrajectoryEnsemble();
		for (final TrajectoryEnsemble te: this.wins)
			res.trajs.addAll(te.trajs);
		return res;
	}
}
