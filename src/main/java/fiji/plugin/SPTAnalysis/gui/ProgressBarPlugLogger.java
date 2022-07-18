package fiji.plugin.SPTAnalysis.gui;

import javax.swing.JProgressBar;

import fiji.plugin.SPTAnalysis.struct.PlugLogger;


public class ProgressBarPlugLogger extends PlugLogger
{
	private final JProgressBar progBar;
	double cum;

	public ProgressBarPlugLogger(final JProgressBar progBar)
	{
		this.progBar = progBar;
		this.cum = 0;
	}

	@Override
	public void update(double amount)
	{
		amount = amount / this.maxWins * 100;
		cum += amount;
		if (cum >= 1)
			progBar.setValue(progBar.getValue() + (int) Math.floor(cum));
			cum -= Math.floor(cum);
	}
}
