package fiji.plugin.SPTAnalysis.gui;

import javax.swing.JFormattedTextField;
import javax.swing.JPanel;

import fiji.plugin.SPTAnalysis.struct.Freezable;

public abstract class AnalysisParametersPanel extends JPanel implements Freezable
{
	private static final long serialVersionUID = 1L;

	protected JFormattedTextField analysisNamePanel;

	public AnalysisParametersPanel()
	{
		this.analysisNamePanel = new JFormattedTextField();
	}

	public void setAnalysisName(String name)
	{
		this.analysisNamePanel.setValue(name);
	}

	public void freeze()
	{
		this.analysisNamePanel.setEnabled(false);
	}

	public void reset()
	{
		this.analysisNamePanel.setEnabled(true);
	}
}
