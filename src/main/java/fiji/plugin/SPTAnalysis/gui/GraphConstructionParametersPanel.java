package fiji.plugin.SPTAnalysis.gui;

import fiji.plugin.SPTAnalysis.graphConstruction.GraphConstructionParameters;

public abstract class GraphConstructionParametersPanel extends AnalysisParametersPanel
{
	private static final long serialVersionUID = 1L;

	public abstract GraphConstructionParameters params(String expName, double instVelTh, double minAreaTh,
			double maxAreaTh, double ellEps, GraphConstructionParameters.NodeType nodeT);
}
