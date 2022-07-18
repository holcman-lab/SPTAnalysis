package fiji.plugin.SPTAnalysis.struct;

import java.util.ArrayList;
import java.util.HashMap;

public class TrajectoryEnsembleProxy extends TrajectoryEnsemble
{
	HashMap<Integer, ArrayList<Integer>> trajsId;
	TrajectoryEnsemble trajs;

	public TrajectoryEnsembleProxy(HashMap<Integer, ArrayList<Integer>> trajsId, TrajectoryEnsemble trajs)
	{
		this.trajsId = trajsId;
		this.trajs = trajs;
	}

	public ArrayList<Trajectory> trajs()
	{
		ArrayList<Trajectory> res = new ArrayList<> ();
		for (Integer tid: trajsId.keySet())
		{
			assert(this.trajs.findId(tid) != null);
			res.add(new TrajectoryProxy(this.trajsId.get(tid), this.trajs.findId(tid)));
		}

		return res;
	}
}
