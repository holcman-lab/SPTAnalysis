package fiji.plugin.SPTAnalysis.struct;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.vividsolutions.jts.geom.Coordinate;

import fiji.plugin.SPTAnalysis.Utils;
import fiji.plugin.SPTAnalysis.XMLAdapters;
import fiji.plugin.SPTAnalysis.graphConstruction.GraphConstructionParameters;

@XmlRootElement(name = "Graph")
@XmlAccessorType(XmlAccessType.FIELD)
public class Graph
{
	@XmlJavaTypeAdapter(XMLAdapters.NodeAdapter.class)
	private HashMap<Integer, Shape> nodes;
	private GraphConstructionParameters.NodeType nodeT;

	@XmlJavaTypeAdapter(XMLAdapters.ConnectivityMatrixAdapter.class)
	private HashMap<Integer, HashMap<Integer, Double>> C;

	public Graph()
	{
		this.nodes = null;
		this.C = null;
		this.nodeT = null;
	}

	public Graph(HashMap<Integer, Shape> nodes,
				 GraphConstructionParameters.NodeType nodeT,
				 HashMap<Integer, HashMap<Integer, Double>> C)
	{
		this.nodes = nodes;
		this.nodeT = nodeT;
		this.C = C;
	}

	public HashMap<Integer, ? extends Shape> nodes()
	{
		return this.nodes;
	}
	
	public GraphConstructionParameters.NodeType nodeT()
	{
		return this.nodeT;
	}

	public HashMap<Integer, HashMap<Integer, Double>> conn()
	{
		return this.C;
	}

	public double connect(int i, int j)
	{
		if (!this.C.containsKey(i) || !this.C.get(i).containsKey(j))
			return 0.0;
		return this.C.get(i).get(j);
	}

	public int numLinks()
	{
		int res = 0;
		for (Integer I: this.C.keySet())
			for (Integer J: this.C.get(I).keySet())
				if (this.C.get(I).get(J) > 0)
					++res;
		return res;
	}

	public void toFile(String outPath) throws IOException
	{
		ArrayList<String> links = new ArrayList<>();
		for (int i: this.nodes.keySet())
			for (int j: this.nodes.keySet())
				if (this.connect(i,j) > 0)
					links.add(String.format("%d,%d,%g\n", i, j, this.C.get(i).get(j)));

		BufferedWriter writer = new BufferedWriter(new FileWriter(outPath));

		//Changed in v.1.1: added this first line giving the number of nodes and links
		writer.write(String.format("%d,%d\n", this.nodes.size(), links.size()));
		for (int k: this.nodes.keySet())
		{
			if (this.nodeT() == GraphConstructionParameters.NodeType.ELLIPSE)
			{
				Ellipse e = (Ellipse) this.nodes.get(k);
				writer.write(String.format("%d,%g,%g,%g,%g,%g\n", k, e.mu()[0], e.mu()[1],
								e.rad()[0], e.rad()[1], e.phi()));
			}
			else
			{
				MyPolygon p = (MyPolygon) this.nodes.get(k);

				writer.write(String.format("%d", k));
				for (final Coordinate coord: p.poly().getCoordinates())
				{
					writer.write(String.format(" %g,%g", coord.getOrdinate(0),
							coord.getOrdinate(1)));
				}
				writer.write("\n");
			}
		}
		for (String link_s: links)
			writer.write(link_s);

		writer.close();
	}

	public static Graph GraphInRegion(final Graph g, Shape reg)
	{
		ArrayList<Integer> keys = new ArrayList<>();
		ArrayList<Shape> vals = new ArrayList<>();
		for (Integer k: g.nodes().keySet())
		{
			keys.add(k);
			vals.add(g.nodes().get(k));
		}

		ArrayList<Integer> idxs = Utils.shapesInRegIdx(vals, reg);

		HashMap<Integer, Shape> newNodes = new HashMap<>();
		ArrayList<Integer> keptKeys = new ArrayList<>();
		for (int k: idxs)
		{
			newNodes.put(keys.get(k), vals.get(k));
			keptKeys.add(keys.get(k));
		}

		HashMap<Integer, HashMap<Integer, Double>> newC = new HashMap<Integer, HashMap<Integer, Double>>();
		for (int k: keptKeys)
		{
			for (int l: keptKeys)
			{
				if (g.conn().containsKey(k) && g.conn().get(k).containsKey(l))
				{
					if (!newC.containsKey(k))
						newC.put(k, new HashMap<>());
					newC.get(k).put(l, g.conn().get(k).get(l));
				}
			}
		}

		return new Graph(newNodes, g.nodeT(), newC);
	}
}