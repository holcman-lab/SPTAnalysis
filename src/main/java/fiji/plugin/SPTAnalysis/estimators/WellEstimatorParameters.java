package fiji.plugin.SPTAnalysis.estimators;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import fiji.plugin.SPTAnalysis.XMLAdapters;

@XmlRootElement(name = "WellEstimatorParameters")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlJavaTypeAdapter(XMLAdapters.WellEstimatorParametersAdapter.class)
public abstract class WellEstimatorParameters
{
	final public WellEstimator.type estType;

	public WellEstimatorParameters()
	{
		this.estType = null;
	}

	public WellEstimatorParameters(final WellEstimator.type estType)
	{
		this.estType = estType;
	}

	public String toString()
	{
		return "estType:" + this.estType.name();
	}
}
