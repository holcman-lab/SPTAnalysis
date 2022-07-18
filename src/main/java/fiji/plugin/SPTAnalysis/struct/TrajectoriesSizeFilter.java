package fiji.plugin.SPTAnalysis.struct;

import java.util.ArrayList;
import java.util.HashMap;

public class TrajectoriesSizeFilter extends TrajectoriesProcessor
{
	protected int minNPts;

	public TrajectoriesSizeFilter(int minNPts)
	{
		this.minNPts = minNPts;
	}

	@Override
	public TrajectoryEnsemble run(TrajectoryEnsemble trajs)
	{
		HashMap<Integer, ArrayList<Integer>> proxIds = new HashMap<> ();
		for (Trajectory tr: trajs.trajs())
		{
			if (tr.points().size() >= minNPts)
			{
				ArrayList<Integer> tmp = new ArrayList<Integer> ();
				for (int i = 0; i < tr.points().size(); ++i)
					tmp.add(i);

				assert(!tmp.isEmpty());
				proxIds.put(tr.id(), tmp);
			}
		}
		return new TrajectoryEnsembleProxy(proxIds, trajs);
	}
}
