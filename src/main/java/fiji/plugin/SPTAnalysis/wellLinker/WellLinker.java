package fiji.plugin.SPTAnalysis.wellLinker;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import fiji.plugin.SPTAnalysis.struct.PotWellsWindows;

@XmlRootElement(name = "WellLinker")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({DistanceWellLinker.class})
public abstract class WellLinker
{
	@XmlRootElement(name = "WindowIndex")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class WindowIndex
	{
		public int winIdx;
		public int wellIdx;

		public WindowIndex()
		{
		}

		public WindowIndex(int wiIdx, int weIdx)
		{
			this.winIdx = wiIdx;
			this.wellIdx = weIdx;
		}
	}

	public WellLinker()
	{
	}

	public abstract ArrayList<ArrayList<WindowIndex>> link(PotWellsWindows wellsWins);

	public static int findFamily (WindowIndex widx, ArrayList<ArrayList<WindowIndex>> links)
	{
		if (links == null)
			return -1;

		for (int i = 0; i < links.size(); ++i)
			for (WindowIndex wi: links.get(i))
				if (wi.wellIdx == widx.wellIdx && wi.winIdx == widx.winIdx)
					return i;
		return -1;
	}
}
