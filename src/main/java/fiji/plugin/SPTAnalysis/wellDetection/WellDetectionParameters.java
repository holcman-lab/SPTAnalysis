package fiji.plugin.SPTAnalysis.wellDetection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import fiji.plugin.SPTAnalysis.estimators.WellEstimatorParameters;
import fiji.plugin.SPTAnalysis.struct.AnalysisParameters;

@XmlRootElement(name = "WellDetectionParameters")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class WellDetectionParameters extends AnalysisParameters
{
	public final WellEstimatorParameters estPs;
	public final int bestItShift;

	public WellDetectionParameters()
	{
		this.estPs = null;
		this.bestItShift = 0;
	}

	public WellDetectionParameters(String expname, final WellEstimatorParameters estPs, int bestItShift)
	{
		super(expname);
		this.estPs = estPs;
		this.bestItShift = bestItShift;
	}

	@Override
	public AnalysisParameters.Type analysisType()
	{
		return AnalysisParameters.Type.WELL;
	}
}
