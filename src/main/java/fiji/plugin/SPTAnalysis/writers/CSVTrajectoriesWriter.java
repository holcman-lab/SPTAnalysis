package fiji.plugin.SPTAnalysis.writers;

import fiji.plugin.SPTAnalysis.struct.Point;
import fiji.plugin.SPTAnalysis.struct.Trajectory;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;

public class CSVTrajectoriesWriter extends CSVWriter
{
	protected final TrajectoryEnsemble trajs;

	public CSVTrajectoriesWriter(final String delim, TrajectoryEnsemble trajs)
	{
		super(delim);

		this.trajs = trajs;
	}

	@Override
	public String generate()
	{
		StringBuilder sb = new StringBuilder ();
		for (Trajectory tr: this.trajs.trajs())
			for (Point p: tr.points())
				sb.append(String.format("%d%s%g%s%g%s%g\n", tr.id(), this.delim,
						p.t, this.delim, p.x, this.delim, p.y));
		return sb.toString();
	}
}
