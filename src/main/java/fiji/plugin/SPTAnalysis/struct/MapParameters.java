package fiji.plugin.SPTAnalysis.struct;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import fiji.plugin.SPTAnalysis.struct.ScalarMap.DensityOption;

public abstract class MapParameters
{
	public double dx;

	public MapParameters()
	{
		this.dx = Double.NaN;
	}

	public MapParameters(double dx)
	{
		this.dx = dx;
	}

	public abstract String toString();

	@XmlRootElement(name = "DensityParameters")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class DensityParameters extends MapParameters
	{
		public DensityOption dopt;
		public int filtNhSize;

		public DensityParameters()
		{
			super();
		}

		public DensityParameters(double dx, DensityOption dopt, int filtNhSize)
		{
			this.dx = dx;
			this.dopt = dopt;
			this.filtNhSize = filtNhSize;
		}

		@Override
		public boolean equals(Object o)
		{
			if (o == this)
				return true;
			if (!(o instanceof DensityParameters))
				return false;

			DensityParameters p2 = (DensityParameters) o;
			return this.dx == p2.dx && this.dopt == p2.dopt
					&& this.filtNhSize == p2.filtNhSize;
		}

		@Override
		public int hashCode()
		{
			//this is awful but we only need the hash to be equal for
			//equal parameter values
			if (this.dopt == DensityOption.DENS)
				return 1;
			else if (this.dopt == DensityOption.LOGDENS)
				return 2;
			else if (this.dopt == DensityOption.NPTS)
				return 3;
			else
				return 4;
		}

		@Override
		public String toString()
		{
			return String.format("dx=%.3f_type=%s_meanNhSize=%d", this.dx,
					this.dopt.name(), this.filtNhSize);
		}
	}

	@XmlRootElement(name = "DiffusionParameters")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class DiffusionParameters extends MapParameters
	{
		public double dx;
		public int nPts;
		public boolean filter;
		public double filterSize;

		public DiffusionParameters()
		{
		}

		public DiffusionParameters(double dx, int numPts,
				boolean filter, double filterSize)
		{
			this.dx = dx;
			this.nPts = numPts;
			this.filter = filter;
			this.filterSize = filterSize;
		}

		@Override
		public boolean equals(Object o)
		{
			if (o == this)
				return true;
			if (!(o instanceof DiffusionParameters))
				return false;

			DiffusionParameters p2 = (DiffusionParameters) o;
			return this.dx == p2.dx && this.nPts == p2.nPts &&
					this.filter == p2.filter && this.filterSize == p2.filterSize;
		}

		@Override
		public int hashCode()
		{
			//this is awful but we only need the hash to be equal for
			//equal parameter values
			return this.nPts;
		}

		@Override
		public String toString()
		{
			if (!this.filter)
				return String.format("dx=%.3f_minNpts=%d", this.dx,
						this.nPts);
			else
				return String.format("dx=%.3f_minNpts=%d_filterSize=%g", this.dx,
						this.nPts, this.filterSize);
		}
	}

	@XmlRootElement(name = "DiffusionParameters")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class AnomalousDiffusionParameters extends MapParameters
	{
		public double dx;
		public int nPts;
		public int nPtsFit;
		public boolean filter;
		public double filterSize;

		public AnomalousDiffusionParameters()
		{
		}

		public AnomalousDiffusionParameters(double dx, int numPts, int numPtsFit,
				boolean filter, double filterSize)
		{
			this.dx = dx;
			this.nPts = numPts;
			this.nPtsFit = numPtsFit;
			this.filter = filter;
			this.filterSize = filterSize;
		}

		@Override
		public boolean equals(Object o)
		{
			if (o == this)
				return true;
			if (!(o instanceof AnomalousDiffusionParameters))
				return false;

			AnomalousDiffusionParameters p2 = (AnomalousDiffusionParameters) o;
			return this.dx == p2.dx && this.nPts == p2.nPts &&
					this.nPtsFit == p2.nPtsFit &&
					this.filter == p2.filter && this.filterSize == p2.filterSize;
		}

		@Override
		public int hashCode()
		{
			//this is awful but we only need the hash to be equal for
			//equal parameter values
			return this.nPts * this.nPtsFit;
		}

		@Override
		public String toString()
		{
			if (!this.filter)
				return String.format("dx=%.3f_minNpts=%d_nPtsFit=%d", this.dx,
						this.nPts, this.nPtsFit);
			else
				return String.format("dx=%.3f_minNpts=%d_nPtsFit=%d_filterSize=%g", this.dx,
						this.nPts, this.nPtsFit, this.filterSize);
		}
	}

	@XmlRootElement(name = "DriftParameters")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class DriftParameters extends MapParameters
	{
		public double dx;
		public int nPts;
		public boolean filter;
		public double filterSize;

		public DriftParameters()
		{
		}

		public DriftParameters(double dx, int numPts,
				boolean filter, double filterSize)
		{
			this.dx = dx;
			this.nPts = numPts;
			this.filter = filter;
			this.filterSize = filterSize;
		}

		@Override
		public boolean equals(Object o)
		{
			if (o == this)
				return true;
			if (!(o instanceof DriftParameters))
				return false;

			DriftParameters p2 = (DriftParameters) o;
			return (this.dx == p2.dx && this.nPts == p2.nPts &&
				this.filter == p2.filter && this.filterSize == p2.filterSize);
		}

		@Override
		public int hashCode()
		{
			//this is awful but we only need the hash to be equal for
			//equal parameter values
			return this.nPts;
		}

		@Override
		public String toString()
		{
			if (!this.filter)
				return String.format("dx=%.3f_minNpts=%d", this.dx,
						this.nPts);
			else
				return String.format("dx=%.3f_minNpts=%d_filterSize=%g", this.dx,
						this.nPts, this.filterSize);
		}
	}
}
