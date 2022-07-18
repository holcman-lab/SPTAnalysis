package fiji.plugin.SPTAnalysis.estimators;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import fiji.plugin.SPTAnalysis.Utils;

@XmlRootElement(name = "GridEstimatorParameters")
@XmlAccessorType(XmlAccessType.FIELD)
public class GridEstimatorParameters extends WellEstimatorParameters
{
	final public double dx;
	final public int driftNptsTh;
	final public int minCellsTh;
	final public boolean diffInWell;
	final public boolean correctField;

	public GridEstimatorParameters()
	{
		super();

		this.dx = Double.NaN;
		this.driftNptsTh = -1;
		this.minCellsTh = -1;
		this.diffInWell = false;
		this.correctField = false;
	}

	public GridEstimatorParameters(final WellEstimator.type estType, 
								   double dx, int driftNptsTh, int minCellsTh,
								   boolean diffInWell, boolean correctField)
	{
		super(estType);

		this.dx = dx;
		this.driftNptsTh = driftNptsTh;
		this.minCellsTh = minCellsTh;
		this.diffInWell = diffInWell;
		this.correctField = correctField;
	}

	public  GridEstimatorParameters copyChangeDx(double dx)
	{
		return new GridEstimatorParameters(this.estType, dx, this.driftNptsTh, this.minCellsTh,
				this.diffInWell, this.correctField);
	}

	@Override
	public String toString()
	{
		return super.toString() +
			   "_dxDrift:" + Utils.rmTrail0(String.format("%g", this.dx)) +
			   "_driftNpts:" + Utils.rmTrail0(String.format("%d", this.driftNptsTh)) +
			   "_minCells:" + String.format("%d", this.minCellsTh) +
			   "_DIn:" + String.format("%d", this.diffInWell? 1: 0) +
			   "_corrDrift:" + String.format("%d", this.correctField? 1: 0);
	}
}
