package fiji.plugin.SPTAnalysis.estimators;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import fiji.plugin.SPTAnalysis.Utils;
import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;
import fiji.plugin.SPTAnalysis.struct.VectorMap;

@XmlRootElement(name = "GridEstimator")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class GridEstimator extends WellEstimator
{
	@XmlTransient
	protected final TrajectoryEnsemble trajs;
	@XmlTransient
	protected final Ellipse ell;
	protected final VectorMap drift;
	protected final ArrayList<int[]> nh;
	protected final boolean DinEllipseOnly;

	public GridEstimator()
	{
		this.trajs = null;
		this.ell = null;
		this.drift = null;
		this.nh = null;
		this.DinEllipseOnly = false;
	}

	public GridEstimator(TrajectoryEnsemble trajs, Ellipse ell, final VectorMap drift, final ArrayList<int[]> nh, boolean DinEllipseOnly)
	{
		this.trajs = trajs;
		this.ell = ell;
		this.drift = drift;
		this.nh = nh;
		this.DinEllipseOnly = DinEllipseOnly;
	}

	@Override
	public double estimateD()
	{
		if (this.DinEllipseOnly)
			return Utils.diffCoeffInEllipseConstr(this.trajs, this.ell);
		else
			return Utils.diffCoeffInEllipse(this.trajs, this.ell);
	}

	public VectorMap drift()
	{
		return this.drift;
	}

	public ArrayList<int[]> nh()
	{
		return this.nh;
	}
}
