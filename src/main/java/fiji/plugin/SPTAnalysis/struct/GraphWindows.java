package fiji.plugin.SPTAnalysis.struct;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GraphWindows")
@XmlAccessorType(XmlAccessType.FIELD)
public class GraphWindows
{
	public ArrayList<Graph> wins;

	public GraphWindows()
	{
		this.wins = new ArrayList<> ();
	}
}
