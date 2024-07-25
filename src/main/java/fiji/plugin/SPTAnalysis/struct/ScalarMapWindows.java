package fiji.plugin.SPTAnalysis.struct;

import java.util.ArrayList;
import java.util.HashMap;

public class ScalarMapWindows
{
	public ArrayList<ScalarMap> wins;

	public ScalarMapWindows()
	{
		this.wins = new ArrayList<ScalarMap> ();
	}

	public ArrayList<ArrayList<Double>> getValues()
	{
		ArrayList<ArrayList<Double>> res = new ArrayList<>();
		for (final ScalarMap m: this.wins)
			res.add(m.getValues());
		return res;
	}

	public ArrayList<ArrayList<double[]>> getPositions()
	{
		ArrayList<ArrayList<double[]>> res = new ArrayList<>();
		for (final ScalarMap m: this.wins)
			res.add(m.getPositions());
		return res;
	}

	public double[] maxs()
	{
		double[] maxs = new double[this.wins.size()];
		for (int i = 0; i < this.wins.size(); ++i)
			maxs[i] = this.wins.get(i).max();
		return maxs;
	}

	public static ScalarMapWindows gen_density_maps(final TrajectoryEnsembleWindows trajsw,
			MapParameters.DensityParameters opts)
	{
		ScalarMapWindows res = new ScalarMapWindows();
		for (final TrajectoryEnsemble trajs: trajsw.wins)
			res.wins.add(ScalarMap.genDensityMap(new SquareGrid(trajs, opts.dx), trajs, opts));
		return res;
	}

	public static ScalarMapWindows gen_diffusion_maps(final TrajectoryEnsembleWindows trajsw,
			MapParameters.DiffusionParameters opts)
	{
		ScalarMapWindows res = new ScalarMapWindows();
		for (final TrajectoryEnsemble trajs: trajsw.wins)
		{
			if (opts.filter)
				res.wins.add(ScalarMap.genDiffusionMapFiltered(new SquareGrid(trajs, opts.dx), trajs, opts));
			else
				res.wins.add(ScalarMap.genDiffusionMap(new SquareGrid(trajs, opts.dx), trajs, opts));
		}
		return res;
	}

	public static HashMap<String,ScalarMapWindows> genAnomalousDiffusionMaps(final TrajectoryEnsembleWindows trajsw,
			MapParameters.AnomalousDiffusionParameters opts)
	{
		HashMap<String,ScalarMapWindows> res = new HashMap<> ();
		res.put("alpha", new ScalarMapWindows());
		res.put("d", new ScalarMapWindows());

		for (final TrajectoryEnsemble trajs: trajsw.wins)
		{
			HashMap<String, ScalarMap> maps = null;
			if (opts.filter)
				maps = ScalarMap.genAnomalousDiffusionMapFiltered(new SquareGrid(trajs, opts.dx), trajs, opts);
			else
				maps = ScalarMap.genAnomalousDiffusionMap(new SquareGrid(trajs, opts.dx), trajs, opts);

			res.get("alpha").wins.add(maps.get("alpha"));
			res.get("d").wins.add(maps.get("d"));
		}
		return res;
	}
}
