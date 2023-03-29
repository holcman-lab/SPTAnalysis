package fiji.plugin.SPTAnalysis.struct;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import fiji.plugin.SPTAnalysis.XMLAdapters;
import fiji.plugin.SPTAnalysis.wellLinker.DistanceWellLinker;
import fiji.plugin.SPTAnalysis.wellLinker.WellLinker;
import fiji.plugin.SPTAnalysis.wellLinker.WellLinker.WindowIndex;

@XmlRootElement(name = "PotWellsWindows")
@XmlAccessorType(XmlAccessType.FIELD)
public class PotWellsWindows
{
	public ArrayList<PotWells> wins;

	@XmlJavaTypeAdapter(XMLAdapters.WellLinkWindows.class)
	protected ArrayList<ArrayList<WindowIndex>> links;
	protected DistanceWellLinker linker;

	public PotWellsWindows()
	{
		this.wins = new ArrayList<> ();
	}

	public PotWellsWindows(ArrayList<PotWells> ws)
	{
		this.wins = ws;
	}

	public void toFile(final String delim, String outPath, boolean header, Shape selection) throws IOException
	{
		int cpt = 0;
		BufferedWriter writer = new BufferedWriter(new FileWriter(outPath));
		if (header)
			writer.write(String.format("idx%swindow%smux(µm)%smuy(µm)%sa(µm)%sb(µm)%sphi%sA(µm²/s)%sD(µm²/s)%sscore%srestime(s)%sFamily\n",
					delim, delim, delim, delim, delim, delim, delim, delim, delim, delim, delim));

		for (int i = 0; i < this.wins.size(); ++i)
		{
			for (int j = 0; j < this.wins.get(i).wells.size(); ++j)
			{
				PotWell w = this.wins.get(i).wells.get(j);
				if (selection.inside(w.ell().mu()))
					writer.write(String.format("%d%s%d%s%g%s%g%s%g%s%g%s%g%s%g%s%g%s%g%s%g%s%d\n", cpt, delim, i,
							delim, w.ell().mu()[0], delim, w.ell().mu()[1], delim, w.ell().rad()[0], delim,
							w.ell().rad()[1], delim, w.ell().phi(), delim, w.A(), delim, w.D(), delim, w.score().value(),
							delim, w.residence_time(), delim, WellLinker.findFamily(new WellLinker.WindowIndex(i,j), this.links)));
				++cpt;
			}
		}
		writer.close();
	}

	public PotWell[] flatten()
	{
		ArrayList<PotWell> tmp = new ArrayList<> ();
		for (PotWells wellsWin: this.wins)
			tmp.addAll(wellsWin.wells);

		PotWell[] res = tmp.toArray(new PotWell[tmp.size()]);
		return res;
	}

	public ArrayList<PotWell> flattenArray()
	{
		ArrayList<PotWell> res = new ArrayList<> ();
		for (PotWells wellsWin: this.wins)
			res.addAll(wellsWin.wells);

		return res;
	}

	
	public void linkWells(DistanceWellLinker linker)
	{
		this.linker = linker;
		this.links = linker.link(this);
	}

	public ArrayList<ArrayList<WindowIndex>> links()
	{
		return this.links;
	}

	public DistanceWellLinker linker()
	{
		return this.linker;
	}
}
