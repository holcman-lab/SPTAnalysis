package fiji.plugin.SPTAnalysis.readers;

import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;

public abstract class TrajectoryReader
{
	public abstract TrajectoryEnsemble read();
}