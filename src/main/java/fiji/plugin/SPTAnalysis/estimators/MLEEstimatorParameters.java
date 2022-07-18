package fiji.plugin.SPTAnalysis.estimators;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "MLEEstimatorParameters")
@XmlAccessorType(XmlAccessType.FIELD)
public class MLEEstimatorParameters extends WellEstimatorParameters
{
	public MLEEstimatorParameters()
	{
		super();
	}

	public MLEEstimatorParameters(final WellEstimator.type estType)
	{
		super(estType);
	}
}
