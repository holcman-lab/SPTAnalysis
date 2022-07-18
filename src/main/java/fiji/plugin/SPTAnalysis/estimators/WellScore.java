package fiji.plugin.SPTAnalysis.estimators;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.NotImplementedException;
import fiji.plugin.SPTAnalysis.XMLAdapters;

@XmlRootElement(name = "WellScore")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlJavaTypeAdapter(XMLAdapters.WellScoreAdapter.class)
public abstract class WellScore
{
	protected double value;

	public abstract boolean betterThan(final WellScore other);
	public abstract double worstValue();

	public static final Empty empty = new Empty();

	
	public double value()
	{
		return this.value;
	}

	public WellScore(double value)
	{
		this.value = value;
	}

	public WellScore()
	{
	}

	@XmlRootElement(name = "EmpyWellScore")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Empty extends WellScore
	{
		public Empty()
		{
			this.value = Double.NaN;
		}

		@Override
		public boolean betterThan(final WellScore other)
		{
			return false;
		}

		@Override
		public double worstValue()
		{
			throw new NotImplementedException("");
		}
	}

	@XmlRootElement(name = "ParabolicWellScore")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Parabolic extends WellScore
	{
		public Parabolic()
		{
			super();
		}

		public Parabolic(double value)
		{
			super(value);
		}

		@Override
		public boolean betterThan(final WellScore other)
		{
			if (other == WellScore.empty)
				return true;

			assert(other instanceof Parabolic);
			return this.value < other.value();
		}

		@Override
		public double worstValue()
		{
			return 1.0;
		}
	}

	@XmlRootElement(name = "LikelihoodWellScore")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Likelihood extends WellScore
	{
		public Likelihood()
		{
			super();
		}

		public Likelihood(double value)
		{
			super(value);
		}

		@Override
		public boolean betterThan(final WellScore other)
		{
			if (other == WellScore.empty)
				return true;

			assert(other instanceof Likelihood);
			return this.value > other.value();
		}

		@Override
		public double worstValue()
		{
			return Double.NEGATIVE_INFINITY;
		}
	}

	@XmlRootElement(name = "DensityWellScore")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Density extends WellScore
	{
		public Density()
		{
			super();
		}

		public Density(double value)
		{
			super(value);
		}

		@Override
		public boolean betterThan(final WellScore other)
		{
			if (other == WellScore.empty)
				return true;

			assert(other instanceof Density);
			return this.value > other.value();
		}

		@Override
		public double worstValue()
		{
			return Double.NEGATIVE_INFINITY;
		}
	}
}
