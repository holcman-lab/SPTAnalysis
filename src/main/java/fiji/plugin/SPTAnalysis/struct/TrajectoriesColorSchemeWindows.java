package fiji.plugin.SPTAnalysis.struct;

import java.util.ArrayList;

public class TrajectoriesColorSchemeWindows
{
	public ArrayList<TrajectoriesColorScheme> wins;

	public static TrajectoriesColorSchemeWindows createColorSchemes(final TrajectoryEnsembleWindows tewins,
			final TrajectoriesColorScheme.ColoringType ctype)
	{
		ArrayList<TrajectoriesColorScheme> wins = new ArrayList<> ();

		for (int i = 0; i < tewins.wins.size(); ++i)
			wins.add(new TrajectoriesColorScheme(tewins.wins.get(i), ctype));

		return new TrajectoriesColorSchemeWindows(wins);
	}

	public TrajectoriesColorSchemeWindows (final ArrayList<TrajectoriesColorScheme> wins)
	{
		this.wins = wins;
	}

	public double[] maxIvelsWins()
	{
		double[] res = new double[this.wins.size()];
		for (int i = 0; i < this.wins.size(); ++i)
			res[i] = TrajectoriesColorScheme.computeMax(this.wins.get(i).instVels());
		return res;
	}

	public TrajectoriesColorScheme.ColoringType coloringType()
	{
		return wins.get(0).coloringType();
	}
}
