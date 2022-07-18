package fiji.plugin.SPTAnalysis.struct;

import fiji.plugin.SPTAnalysis.estimators.WellScore;

public class PotWellWindow extends PotWell
{
	protected int timeWin;

	public PotWellWindow(Ellipse ell, double A, double D, WellScore score, int timeWin)
	{
		super(ell, A, D, score);
		this.timeWin = timeWin;
	}

	public PotWellWindow(PotWell w, int timeWin)
	{
		super(w.ell(), w.A(), w.D(), w.score());
		this.timeWin = timeWin;
	}

	public int timeWin()
	{
		return this.timeWin;
	}
}
