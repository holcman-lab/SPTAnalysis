package fiji.plugin.SPTAnalysis.graphConstruction;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import fiji.plugin.SPTAnalysis.struct.AnalysisParameters;

@XmlRootElement(name = "GraphConstructionDBScanRecursiveParameters")
@XmlAccessorType(XmlAccessType.FIELD)
public class GraphConstructionDBScanRecursiveParameters extends GraphConstructionParameters
{
	public final int maxClustNpts;

	public final double RMax;
	public final double RStep;
	public final double RMin;

	public final int NMin;
	public final int NStep;
	public final int NMax;

	public GraphConstructionDBScanRecursiveParameters()
	{
		super();
		this.maxClustNpts = 0;
		this.RMax = Double.NaN;
		this.RStep = Double.NaN;
		this.RMin = Double.NaN;
		this.NMin = 0;
		this.NStep = 0;
		this.NMax = 0;
	}

	public GraphConstructionDBScanRecursiveParameters(String expName, double lowVelTh,
			int maxClustNpts, double RMax, double RStep, double RMin, int NMin, int NStep, int NMax,
			 double minArea, double maxArea, double minVolEllEps,
			GraphConstructionParameters.NodeType nodeT)
	{
		super(expName, lowVelTh, minArea, maxArea, minVolEllEps, nodeT);
		this.maxClustNpts = maxClustNpts;
		this.RMax = RMax;
		this.RStep = RStep;
		this.RMin = RMin;
		this.NMin = NMin;
		this.NStep = NStep;
		this.NMax = NMax;
	}

	@Override
	public AnalysisParameters.Type analysisType()
	{
		return AnalysisParameters.Type.GRAPH;
	}

	@Override
	public String toString()
	{
		return null;
	}

	@Override
	public String algoName()
	{
		return GraphConstructionDBScanRecursive.name;
	}
}