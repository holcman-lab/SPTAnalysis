package fiji.plugin.SPTAnalysis.struct;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "AnalysisParameters")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AnalysisParameters
{
	public enum Type {GRAPH, WELL};

	protected String expName;

	public AnalysisParameters()
	{
		this.expName = null;
	}

	public AnalysisParameters(String expName)
	{
		this.expName = expName;
	}

	public String expName()
	{
		return this.expName;
	}

	public void expName(String n)
	{
		this.expName = n;
	}

	public abstract String algoName();
	public abstract Type analysisType();

	public abstract String toString();
}
