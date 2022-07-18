 package fiji.plugin.SPTAnalysis.estimators;

import fiji.plugin.SPTAnalysis.Utils;
import fiji.plugin.SPTAnalysis.struct.Trajectory;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;

public class PureDiffusionEstimatorMLE
{
	public static double estimateD(final TrajectoryEnsemble trajs)
	{
		int cnt = 0;
		double res = 0.0;
		for (final Trajectory t: trajs.trajs())
		{
			for (int i = 1; i < t.points().size(); ++i)
			{
				res += Utils.squaredDist(t.points().get(i).vec(),
										 t.points().get(i-1).vec());
				++cnt;
			}
		}

		return 1 / (4 * trajs.acqDT() * cnt) * res;
	}

	public static double logLikelihood(final TrajectoryEnsemble trajs, double D)
	{
		//int cnt = 0;
		double res = 0.0;
		for (final Trajectory t: trajs.trajs())
		{
			for (int i = 0; i < t.points().size() - 1; ++i)
			{
				res += Utils.squaredDist(t.points().get(i+1).vec(),
										 t.points().get(i).vec());
				//++cnt;
			}
		}

		//- cnt * Math.log(D * trajs.acqDT()) 
		return res / (4 * D * trajs.acqDT());
	}
}
