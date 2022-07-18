package fiji.plugin.SPTAnalysis.wellDetection;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import fiji.plugin.SPTAnalysis.estimators.GridEstimatorParameters;
import fiji.plugin.SPTAnalysis.estimators.WellScore;
import fiji.plugin.SPTAnalysis.struct.PotWell;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;

@XmlRootElement(name = "FitResultMultiscale")
@XmlAccessorType(XmlAccessType.FIELD)
public class FitResultMultiscale extends FitResult
{
	protected HashMap<Double, FitResult> results;
	protected double dxBest;
	protected IterationChooser itChoose;

	public FitResultMultiscale()
	{
		super();
		this.results = new HashMap<> ();
		this.dxBest = Double.NaN;
		this.itChoose = null;
	}

	public HashMap<Double, FitResult> results()
	{
		return this.results;
	}

	public double dxBest()
	{
		return this.dxBest;
	}

	public void addFitResult(double dx, FitResult fr)
	{
		results.put(dx, fr);
	}

	@Override
	public void setBestIt(final IterationChooser choose)
	{
		this.itChoose = choose;
		WellScore bestScore = null;
		for (double dx: this.results.keySet())
		{
			if (this.results.get(dx) == null)
				continue;

			if (choose.ps.estPs instanceof GridEstimatorParameters)
				itChoose = choose.cloneUpdateEstParams(((GridEstimatorParameters) choose.ps.estPs).copyChangeDx(dx));

			this.results.get(dx).setBestIt(itChoose);

			if (bestScore == null || this.results.get(dx).bestScore().betterThan(bestScore))
			{
				bestScore = this.results.get(dx).bestScore();
				this.dxBest = dx;
			}
		}

	}

	@Override
	public int bestIt()
	{
		return this.results.get(this.dxBest).bestIt();
	}

	@Override
	public PotWell bestWell(TrajectoryEnsemble trajs, final WellDetectionParameters ps)
	{
		if (Double.isNaN(this.dxBest))
			return null;

		return this.results.get(this.dxBest).bestWell(trajs, ps);
	}

	@Override
	public ArrayList<WellScore> scores()
	{
		return this.results.get(this.dxBest).scores();
	}

	public void computeBestDx()
	{
		WellScore bestScore = null;
		for (double dx: results.keySet())
		{
			if (results.get(dx) == null || results.get(dx).bestScore() == WellScore.empty)
				continue;

			if (bestScore == null || results.get(dx).bestScore().betterThan(bestScore))
			{
				bestScore = results.get(dx).bestScore();
				this.dxBest = dx; 
			}
		}
	}
}