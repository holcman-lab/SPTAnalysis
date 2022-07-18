package fiji.plugin.SPTAnalysis.estimators;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import fiji.plugin.SPTAnalysis.XMLAdapters;

@XmlRootElement(name = "WellEstimator")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlJavaTypeAdapter(XMLAdapters.WellEstimatorAdapter.class)
public abstract class WellEstimator
{
	public static enum type {LSQELL, LSQELLNORM, LSQCIRC, MLE, DENS, LSQELLNORMFIT, DENSMLE};

	@XmlType(namespace="WellEstimator")
	@XmlRootElement(name = "NullWellEstimator")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class NullWellEstimator extends WellEstimator
	{
		@Override
		public double estimateA()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public double estimateD()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public WellScore estimateScore()
		{
			throw new UnsupportedOperationException();
		}
	}

	public static NullWellEstimator empty = new NullWellEstimator();

	public WellEstimator()
	{
	}

	public abstract double estimateA();
	public abstract double estimateD();
	public abstract WellScore estimateScore();
}
