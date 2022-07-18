package fiji.plugin.SPTAnalysis.wellDetection;

import java.util.ArrayList;

import fiji.plugin.SPTAnalysis.struct.PlugLogger;
import fiji.plugin.SPTAnalysis.struct.PotWells;
import fiji.plugin.SPTAnalysis.struct.PotWellsWindows;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsembleWindows;

public abstract class WellDetection
{
	protected ArrayList<FitResult> lastFitResults;

	public WellDetection()
	{
		this.lastFitResults = null;
	}

	public abstract PotWells detectWells (TrajectoryEnsemble trajs, PlugLogger log);

	public abstract WellDetectionParameters getParameters();
	public abstract String name();

	public static PotWellsWindows detectWellsTimeWindows(WellDetection algo, TrajectoryEnsembleWindows trajs, PlugLogger log)
	{
		PotWellsWindows res = new PotWellsWindows();
		if (log != null)
			log.maxWindows(trajs.wins.size());
		for (int i = 0; i < trajs.wins.size(); ++i)
		{
			if (log != null)
				log.curWindow(i);
			if (trajs.wins.get(i).trajs().isEmpty())
				res.wins.add(new PotWells());
			else
				res.wins.add(algo.detectWells(trajs.wins.get(i), log));

			//ArrayList<PotWell> filt_wells = new ArrayList<> ();
			//for (PotWell w: wells)
			//	if (w.score() < 0.5)
			//		filt_wells.add(w);
			
			System.out.println("Detected: " + res.wins.get(i).wells.size() + " wells.");
		}

		return res;
	}

	public ArrayList<FitResult> lastFitResults()
	{
		return this.lastFitResults;
	}
}