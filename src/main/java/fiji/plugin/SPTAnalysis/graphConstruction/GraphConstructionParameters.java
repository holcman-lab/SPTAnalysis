package fiji.plugin.SPTAnalysis.graphConstruction;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import fiji.plugin.SPTAnalysis.struct.AnalysisParameters;

@XmlRootElement(name = "GraphConstructionParameters")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class GraphConstructionParameters extends AnalysisParameters
{
	public enum NodeType {ELLIPSE, POLY};

	final public double lowVelTh;
	public double minArea;
	public double maxArea;
	public double minVolEllEps;
	public NodeType nodeT;

	public GraphConstructionParameters()
	{
		super();
		this.lowVelTh = Double.NaN;
		this.minArea = Double.NaN;
		this.maxArea = Double.NaN;
		this.minVolEllEps = Double.NaN;
		this.nodeT = NodeType.ELLIPSE;
	}

	public GraphConstructionParameters(String expName, double lowVelTh,
			double minArea, double maxArea, double minVolEllEps, NodeType nodeT)
	{
		super(expName);
		this.lowVelTh = lowVelTh;
		this.minArea = minArea;
		this.maxArea = maxArea;
		this.minVolEllEps = minVolEllEps;
		this.nodeT = nodeT;
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
}