package fiji.plugin.SPTAnalysis.writers;

import fiji.plugin.SPTAnalysis.struct.Point;
import fiji.plugin.SPTAnalysis.struct.Trajectory;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;

public class CSVTrajectoriesWriter extends CSVWriter
{
	protected final TrajectoryEnsemble trajs;

	public CSVTrajectoriesWriter(TrajectoryEnsemble trajs)
	{
		this.trajs = trajs;
	}

	@Override
	public String generate()
	{
		StringBuilder sb = new StringBuilder ();
		for (Trajectory tr: this.trajs.trajs())
			for (Point p: tr.points())
				sb.append(String.format("%d %g %g %g\n", tr.id(), p.t, p.x, p.y));
		return sb.toString();
	}

}
