package fiji.plugin.SPTAnalysis.estimators;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import fiji.plugin.SPTAnalysis.Utils;

@XmlRootElement(name = "DensityEstimatorParameters")
@XmlAccessorType(XmlAccessType.FIELD)
public class DensityEstimatorParameters extends WellEstimatorParameters
{
	final public boolean diffInWell;

	public DensityEstimatorParameters()
	{
		super();
		this.diffInWell = false;
	}

	public DensityEstimatorParameters(final WellEstimator.type estType, boolean diffInWell)
	{
		super(estType);
		this.diffInWell = diffInWell;
	}

	@Override
	public String toString()
	{
		return super.toString() +
			   "_diffInWell:" + Utils.rmTrail0(String.format("%b", this.diffInWell));
	}
}
