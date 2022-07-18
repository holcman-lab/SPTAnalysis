package fiji.plugin.SPTAnalysis.wellDetection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import fiji.plugin.SPTAnalysis.Utils;
import fiji.plugin.SPTAnalysis.XMLAdapters;
import fiji.plugin.SPTAnalysis.estimators.WellEstimator;
import fiji.plugin.SPTAnalysis.estimators.WellScore;
import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.PotWell;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;


@XmlRootElement(name = "FitResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class FitResult
{
	protected ArrayList<Ellipse> ells;
	protected ArrayList<double[]> pcaSs;

	@XmlJavaTypeAdapter(XMLAdapters.TrajIds.class)
	protected ArrayList<Set<Integer>> traj_ids;
	protected ArrayList<WellEstimator> ests;

	protected ArrayList<WellScore> scores;

	protected ArrayList<Boolean> empty;

	protected int numIt;

	protected IterationChooser itChooser;
	protected int bestIt;

	public FitResult()
	{
		this.ells = new ArrayList<> ();
		this.pcaSs = new ArrayList<> ();

		this.traj_ids = new ArrayList<> ();
		this.ests = new ArrayList<> ();
		this.scores = new ArrayList<> ();

		this.empty = new ArrayList<>();

		this.numIt = 0;
		this.bestIt = -1;
	}

	public ArrayList<Ellipse> ells()
	{
		return this.ells;
	}

	public ArrayList<Set<Integer>> traj_ids()
	{
		return this.traj_ids;
	}

	public ArrayList<WellEstimator> ests()
	{
		return this.ests;
	}

	public ArrayList<double[]> pcaSs()
	{
		return this.pcaSs;
	}

	public ArrayList<Boolean> empty()
	{
		return this.empty;
	}

	public int numIt()
	{
		return this.numIt;
	}

	public int bestIt()
	{
		return this.bestIt;
	}

	public void setBestIt(final IterationChooser choose)
	{
		this.itChooser = choose;
		this.bestIt = choose.bestIteration(this);
	}

	public void setScore(int it, WellScore val)
	{
		this.scores.set(it, val);
	}
	
	public ArrayList<WellScore> scores()
	{
		return this.scores;
	}

	public WellScore bestScore()
	{
		if (this.bestIt == -1)
			return WellScore.empty;

		return this.scores.get(this.bestIt);
	}

	public void addIteration(Ellipse e, Set<Integer> trajs_ids, double[] pcaS, WellEstimator est)
	{
		this.ells.add(e);
		this.pcaSs.add(pcaS);
		this.traj_ids.add(trajs_ids);
		this.ests.add(est);
		this.scores.add(WellScore.empty);
		this.empty.add(false);
		++this.numIt;
	}

	public void addEmptyIteration()
	{
		this.ells.add(null);
		this.pcaSs.add(null);
		this.traj_ids.add(new HashSet<> ());
		this.ests.add(WellEstimator.empty);
		this.scores.add(WellScore.empty);
		this.empty.add(true);
		++this.numIt;
	}

	public PotWell bestWell(TrajectoryEnsemble trajs, WellDetectionParameters ps)
	{
		if (this.bestIt < 0)
			return null;

		double A = Double.NaN;
		boolean computed = false;
		for (int it = this.bestIt + ps.bestItShift; it >= this.bestIt && Double.isNaN(A); --it)
		{
			if (it >= this.ells.size() || this.empty.get(it))
				continue;

			ArrayList<double[]> pts = Utils.pointsInReg(trajs, this.ells.get(this.bestIt));
			if (pts.size() <= 5)
				continue;

			computed = true;
			A = this.ests.get(it).estimateA();
		}

		if (!computed)
			return null;

		double D = this.ests.get(bestIt).estimateD();

		PotWell res = null;
		if (ps.algoName().equals("DensityMLE"))
			res = new PotWell(this.ells.get(this.bestIt), A, D,
					((FitResultDensity) this).densScores().get(bestIt));
		else
			res = new PotWell(this.ells.get(this.bestIt), A, D, this.scores.get(bestIt));
		res.attachFitResult(this);

		return res;
	}
}
