package fiji.plugin.SPTAnalysis.wellLinker;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import fiji.plugin.SPTAnalysis.Utils;
import fiji.plugin.SPTAnalysis.struct.PotWell;
import fiji.plugin.SPTAnalysis.struct.PotWellsWindows;

@XmlRootElement(name = "DistanceWellLinker")
@XmlAccessorType(XmlAccessType.FIELD)
public class DistanceWellLinker extends WellLinker
{
	protected int maxFrameGap;
	protected double maxDist;

	public DistanceWellLinker()
	{
	}

	public DistanceWellLinker(int maxFrameGap, double maxDist)
	{
		this.maxFrameGap = maxFrameGap;
		this.maxDist = maxDist;
	}

	@Override
	public ArrayList<ArrayList<WellLinker.WindowIndex>> link(PotWellsWindows wellsWins)
	{
		ArrayList<ArrayList<WellLinker.WindowIndex>> res = new ArrayList<> ();

		ArrayList<PotWell> curWin = wellsWins.wins.get(wellsWins.wins.size() - 1).wells;
		for (int i = 0; i < curWin.size(); ++i)
		{
			res.add(new ArrayList<> ());
			res.get(res.size()-1).add(new WellLinker.WindowIndex(wellsWins.wins.size() - 1, i));
		}

		for (int i = wellsWins.wins.size() - 2; i >= 0; --i)
		{
			for (int k = 0; k < wellsWins.wins.get(i).wells.size(); ++k)
			{
				PotWell w = wellsWins.wins.get(i).wells.get(k);
				boolean added = false;
				for (int j = 0; j < res.size(); ++j)
				{
					WellLinker.WindowIndex idx = res.get(j).get(0);
					if (idx.winIdx - i <= this.maxFrameGap &&
						Utils.squaredDist(wellsWins.wins.get(idx.winIdx).wells.get(idx.wellIdx).ell().mu(),
										   w.ell().mu()) < Math.pow(this.maxDist, 2))
					{
						res.get(j).add(0, new WellLinker.WindowIndex(i, k));
						added = true;
						break;
					}
				}

				if (!added)
				{
					res.add(new ArrayList<> ());
					res.get(res.size() - 1).add(new WellLinker.WindowIndex(i, k));
				}
			}
		}

		return res;
	}

	public int maxFrameGap()
	{
		return this.maxFrameGap;
	}

	public double maxDist()
	{
		return this.maxDist;
	}
}
