package fiji.plugin.SPTAnalysis.struct;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "PotWells")
@XmlAccessorType(XmlAccessType.FIELD)
public class PotWells
{
	public ArrayList<PotWell> wells;

	public PotWells()
	{
		this.wells = new ArrayList<> ();
	}

	public PotWells(ArrayList<PotWell> ws)
	{
		this.wells = ws;
	}

	public PotWell[] toArray()
	{
		return this.wells.toArray(new PotWell[this.wells.size()]);
	}
}