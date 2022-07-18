package fiji.plugin.SPTAnalysis.gui;


import fiji.plugin.SPTAnalysis.wellDetection.WellDetectionParameters;

public abstract class WellDetectionParametersPanel extends AnalysisParametersPanel
{
	private static final long serialVersionUID = 1L;

	public static final String[] estTypeCategories = new String[] {"Grid", "MLE", "Dens", "DensMLE"};

	public abstract WellDetectionParameters detectionParameters();
}
