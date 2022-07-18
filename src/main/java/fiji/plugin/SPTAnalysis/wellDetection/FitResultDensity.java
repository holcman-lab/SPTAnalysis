package fiji.plugin.SPTAnalysis.wellDetection;

import java.util.ArrayList;
import java.util.Set;

import fiji.plugin.SPTAnalysis.estimators.WellEstimator;
import fiji.plugin.SPTAnalysis.estimators.WellScore;
import fiji.plugin.SPTAnalysis.struct.Ellipse;

public class FitResultDensity extends FitResult
{
	protected ArrayList<WellScore> densScores;

	public FitResultDensity()
	{
		super();
		this.densScores = new ArrayList<> ();
	}

	public void addIteration(Ellipse e, Set<Integer> trajs_ids, double[] pcaS, WellEstimator est,
					WellScore densScore)
	{
		super.addIteration(e, trajs_ids, pcaS, est);
		this.densScores.add(densScore);
	}

	@Override
	public void addEmptyIteration()
	{
		super.addEmptyIteration();
		this.densScores.add(WellScore.empty);
	}

	public ArrayList<WellScore> densScores()
	{
		return this.densScores;
	}
}
