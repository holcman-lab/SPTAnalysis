package fiji.plugin.SPTAnalysis.graphConstruction;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import fiji.plugin.SPTAnalysis.struct.AnalysisParameters;

@XmlRootElement(name = "GraphConstructionDBScanParameters")
@XmlAccessorType(XmlAccessType.FIELD)
public class GraphConstructionDBScanParameters extends GraphConstructionParameters
{
	final public double R;
	final public int N;

	public GraphConstructionDBScanParameters()
	{
		super();
		this.R = Double.NaN;
		this.N = 0;
	}

	public GraphConstructionDBScanParameters(double R, int N)
	{
		super("", Double.NaN, Double.NaN, Double.NaN, Double.NaN, GraphConstructionParameters.NodeType.POLY);
		this.R = R;
		this.N = N;
	}

	public GraphConstructionDBScanParameters(String expName, double lowVelTh,
			double R, int N, double minArea, double maxArea,
			double minVolEllEps, GraphConstructionParameters.NodeType nodeT)
	{
		super(expName, lowVelTh, minArea, maxArea, minVolEllEps, nodeT);
		this.R = R;
		this.N = N;
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
		return GraphConstructionDBScan.name;
	}
}