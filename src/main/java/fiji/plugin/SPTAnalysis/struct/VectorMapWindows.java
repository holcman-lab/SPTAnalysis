package fiji.plugin.SPTAnalysis.struct;

import java.util.ArrayList;

public class VectorMapWindows
{
	public ArrayList<VectorMap> wins;

	public VectorMapWindows()
	{
		this.wins = new ArrayList<VectorMap> ();
	}

	public static VectorMapWindows gen_drift_maps(final TrajectoryEnsembleWindows trajsw,
			MapParameters.DriftParameters opts)
	{
		VectorMapWindows res = new VectorMapWindows();
		for (final TrajectoryEnsemble trajs: trajsw.wins)
		{
			if (opts.filter)
				res.wins.add(VectorMap.genDriftMapFiltered(new SquareGrid(trajs, opts.dx), trajs, opts));
			else
				res.wins.add(VectorMap.genDriftMap(new SquareGrid(trajs, opts.dx), trajs, opts));
		}
		return res;
	}
}
