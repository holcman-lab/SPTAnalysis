package fiji.plugin.SPTAnalysis.struct;

public abstract class PlugLogger
{
	protected double maxWins;
	protected int curWin;

	public void maxWindows(int mw)
	{
		this.maxWins = (double) mw;
	}

	public void curWindow(int cw)
	{
		this.curWin = cw;
	}

	public abstract void update(double amount);
}
