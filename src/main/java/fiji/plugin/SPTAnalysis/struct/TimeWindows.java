package fiji.plugin.SPTAnalysis.struct;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "TimeWindows")
@XmlAccessorType(XmlAccessType.FIELD)
public class TimeWindows
{
	protected double dur;
	protected double ovlp;

	protected double tMin;
	protected double tMax;

	public TimeWindows()
	{
	}

	//overlap in [0, 1] % of overlap (0 -> back to back windows; 1 -> identical windows)
	public TimeWindows(double duration, double overlap, double timeMin, double timeMax)
	{
		this.dur = duration;
		this.ovlp = overlap;
		this.tMin = timeMin;
		this.tMax = timeMax;
	}

	public double duration()
	{
		return this.dur;
	}

	public double overlap()
	{
		return this.ovlp;
	}

	public static TimeWindows singleWindow(TrajectoryEnsemble trajs)
	{
		double[] ti = trajs.time_interval();
		double dur = ti[1] - ti[0] + 1;
		double over = 0.0;

		return new TimeWindows(trajs, dur, over);
	}

	//overlap in [0, 1]
	public TimeWindows(TrajectoryEnsemble trajs, double duration, double overlap)
	{
		this.dur = duration;
		this.ovlp = overlap;

		double[] ti = trajs.time_interval();
		this.tMin = ti[0];
		this.tMax = ti[1];
	}

	public int idxMax()
	{
		return (int) Math.ceil((this.tMax - this.tMin) / (this.dur * (1 - this.ovlp))) - 1;
	}

	public double[] get(int k)
	{
		return new double[] {this.tMin + k * this.dur * (1 - this.ovlp),
							 this.tMin + k * this.dur * (1 - this.ovlp) + this.dur};
	}

	public int getIdx(double t)
	{
		int a = (int) Math.floor((t - 1e-6 - this.tMin) / (this.dur * (1 - this.ovlp)));
		return a < 0 ? 0 : a;
	}
}