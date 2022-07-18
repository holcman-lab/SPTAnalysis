package fiji.plugin.SPTAnalysis;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

import fiji.plugin.SPTAnalysis.graphConstruction.GraphConstructionDBScan;
import fiji.plugin.SPTAnalysis.graphConstruction.GraphConstructionDBScanParameters;
import fiji.plugin.SPTAnalysis.graphConstruction.GraphConstructionDBScanRecursive;
import fiji.plugin.SPTAnalysis.graphConstruction.GraphConstructionDBScanRecursiveParameters;
import fiji.plugin.SPTAnalysis.graphConstruction.GraphConstructionParameters;
import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.GraphWindows;
import fiji.plugin.SPTAnalysis.struct.MyPolygon;
import fiji.plugin.SPTAnalysis.struct.Point;
import fiji.plugin.SPTAnalysis.struct.PotWellsWindows;
import fiji.plugin.SPTAnalysis.struct.Shape;
import fiji.plugin.SPTAnalysis.wellDetection.HybridWellDetection;
import fiji.plugin.SPTAnalysis.wellDetection.HybridWellDetectionMultiscale;
import fiji.plugin.SPTAnalysis.wellDetection.IterationChooser;
import fiji.plugin.SPTAnalysis.wellDetection.WellDetectionParameters;
import fiji.plugin.SPTAnalysis.wellLinker.WellLinker;
import fiji.plugin.SPTAnalysis.wellLinker.WellLinker.WindowIndex;
import fiji.plugin.SPTAnalysis.estimators.DensityEstimator;
import fiji.plugin.SPTAnalysis.estimators.DensityEstimatorParameters;
import fiji.plugin.SPTAnalysis.estimators.GridEstimatorParameters;
import fiji.plugin.SPTAnalysis.estimators.LeastSquareEstimatorCircle;
import fiji.plugin.SPTAnalysis.estimators.LeastSquareEstimatorEllipse;
import fiji.plugin.SPTAnalysis.estimators.LeastSquareEstimatorEllipseFit;
import fiji.plugin.SPTAnalysis.estimators.LeastSquareEstimatorEllipseNorm;
import fiji.plugin.SPTAnalysis.estimators.LeastSquareEstimatorEllipseNormFit;
import fiji.plugin.SPTAnalysis.estimators.MLEEstimatorParameters;
import fiji.plugin.SPTAnalysis.estimators.MaxLikelihoodEstimator;
import fiji.plugin.SPTAnalysis.estimators.WellEstimator;
import fiji.plugin.SPTAnalysis.estimators.WellEstimatorParameters;
import fiji.plugin.SPTAnalysis.estimators.WellScore;

public class XMLAdapters
{
	public static class TrajectoryAdapter extends XmlAdapter<String, ArrayList<Point>>
	{
		@Override
		public String marshal(ArrayList<Point> pts)
		{
			StringBuilder sb = new StringBuilder ();
			for (Point p: pts)
				sb.append(String.format("%g %g %g %g\n", p.t, p.x, p.y, p.z));
			return sb.toString();
		}

		@Override
		public ArrayList<Point> unmarshal(String v) throws Exception
		{
			ArrayList<Point> res = new ArrayList<> ();
			for (String tmp: v.split("\n"))
			{
				String[] elts = tmp.split(" ");
				res.add(new Point(Double.valueOf(elts[0]), Double.valueOf(elts[1]),
								  Double.valueOf(elts[2]), Double.valueOf(elts[3])));
			}
			return res;
		}
	}

	public static class Wells extends XmlAdapter<String, HashMap<String, HashMap<WellDetectionParameters, PotWellsWindows>>>
	{
		@Override
		public String marshal(HashMap<String, HashMap<WellDetectionParameters, PotWellsWindows>> wells) throws Exception
		{
			HashMap<String, Marshaller> jaxbMarhallers = new HashMap<> ();
			jaxbMarhallers.put(HybridWellDetection.name,
					JAXBContext.newInstance(HybridWellDetection.Parameters.class).createMarshaller());
			jaxbMarhallers.get(HybridWellDetection.name).setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			jaxbMarhallers.put(HybridWellDetectionMultiscale.name,
					JAXBContext.newInstance(HybridWellDetectionMultiscale.Parameters.class).createMarshaller());
			jaxbMarhallers.get(HybridWellDetectionMultiscale.name).setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			Marshaller wellsMarshaller = JAXBContext.newInstance(PotWellsWindows.class).createMarshaller();
			wellsMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.newDocument();
			DocumentFragment fdoc = doc.createDocumentFragment();

			Element root = doc.createElement("root");
			for (String algoName: wells.keySet())
			{
				Element e = doc.createElement("entry");
				Element f = doc.createElement("key");
				f.setTextContent(algoName);
				e.appendChild(f);
				f = doc.createElement("value");
				for (WellDetectionParameters ps: wells.get(algoName).keySet())
				{
					Element g = doc.createElement("entry");
					Element h = doc.createElement("key");

					if (ps.algoName().equals(HybridWellDetection.name))
						jaxbMarhallers.get(HybridWellDetection.name).marshal((HybridWellDetection.Parameters) ps, h);
					else if (ps.algoName().equals(HybridWellDetectionMultiscale.name))
						jaxbMarhallers.get(HybridWellDetectionMultiscale.name).marshal((HybridWellDetectionMultiscale.Parameters) ps, h);

					g.appendChild(h);

					h = doc.createElement("value");
					wellsMarshaller.marshal(wells.get(algoName).get(ps), h);
					g.appendChild(h);
					f.appendChild(g);
				}

				e.appendChild(f);
				root.appendChild(e);
			}
			fdoc.appendChild(root);

			StringWriter writer = new StringWriter();
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
			transformer.transform(new DOMSource(fdoc), new StreamResult(writer));

			return writer.getBuffer().toString();
		}

		@Override
		public HashMap<String, HashMap<WellDetectionParameters, PotWellsWindows>> unmarshal(String v) throws Exception
		{
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			StringReader sr = new StringReader(v);
			Document doc = db.parse(new InputSource(sr));

			HashMap<String, Unmarshaller> paramsUnmarshallers = new HashMap<> ();
			paramsUnmarshallers.put(HybridWellDetection.name,
					JAXBContext.newInstance(HybridWellDetection.Parameters.class).createUnmarshaller());
			paramsUnmarshallers.put(HybridWellDetectionMultiscale.name,
					JAXBContext.newInstance(HybridWellDetectionMultiscale.Parameters.class).createUnmarshaller());
			Unmarshaller wellsUnmarshaller = JAXBContext.newInstance(PotWellsWindows.class).createUnmarshaller();

			HashMap<String, HashMap<WellDetectionParameters, PotWellsWindows>> res = new HashMap<> ();

			Node n = doc.getFirstChild();
			n = n.getFirstChild();
			while (n != null)
			{
				if (n.getNodeName().equals("#text"))
				{
					n = n.getNextSibling();
					continue;
				}

				assert(n.getNodeName().equals("entry"));
				Node nn = n.getFirstChild();
				while (nn != null)
				{
					if (nn.getNodeName().equals("#text"))
					{
						nn = nn.getNextSibling();
						continue;
					}
					assert(nn.getNodeName().equals("key"));

					String algoName = nn.getTextContent();
					if (!res.containsKey(algoName))
						res.put(algoName, new HashMap<> ());

					nn = nn.getNextSibling();
					while (nn != null && nn.getNodeName().equals("#text"))
						nn = nn.getNextSibling();
					assert(nn.getNodeName().equals("value"));

					Node nnn = nn.getFirstChild();
					while (nnn != null)
					{
						if (nnn.getNodeName().equals("#text"))
						{
							nnn = nnn.getNextSibling();
							continue;
						}
						assert(nnn.getNodeName().equals("entry"));

						Node nnnn = nnn.getFirstChild();
						while (nnnn != null && nnnn.getNodeName().equals("#text"))
							nnnn = nnnn.getNextSibling();
						assert(nnnn.getNodeName().equals("key"));

						Node nnnnn = nnnn.getFirstChild();
						while (nnnnn != null && nnnnn.getNodeName().equals("#text"))
							nnnnn = nnnnn.getNextSibling();
						assert(nnnnn.getNodeName().contains("Parameters"));

						WellDetectionParameters ps = null;
						if (algoName.equals(HybridWellDetection.name))
							ps = (HybridWellDetection.Parameters) paramsUnmarshallers.get(algoName).unmarshal(nnnnn);
						else if (algoName.equals(HybridWellDetectionMultiscale.name))
							ps = (HybridWellDetectionMultiscale.Parameters) paramsUnmarshallers.get(algoName).unmarshal(nnnnn);
						assert(ps != null);

						nnnn = nnnn.getNextSibling();
						while (nnnn != null && nnnn.getNodeName().equals("#text"))
							nnnn = nnnn.getNextSibling();
						assert(nnnn.getNodeName().equals("value"));

						nnnnn = nnnn.getFirstChild();
						while (nnnnn != null && nnnnn.getNodeName().equals("#text"))
							nnnnn = nnnnn.getNextSibling();

						res.get(algoName).put(ps, (PotWellsWindows) wellsUnmarshaller.unmarshal(nnnnn));

						nnn = nnn.getNextSibling();
					}
					nn = nn.getNextSibling();
				}
				n = n.getNextSibling();
			}

			return res;
		}
	}

	public static class Graphs extends XmlAdapter<String, HashMap<String, HashMap<GraphConstructionParameters, GraphWindows>>>
	{
		@Override
		public String marshal(HashMap<String, HashMap<GraphConstructionParameters, GraphWindows>> graphs) throws Exception
		{
			HashMap<String, Marshaller> jaxbMarhallers = new HashMap<> ();
			jaxbMarhallers.put(GraphConstructionDBScan.name,
					JAXBContext.newInstance(GraphConstructionDBScanParameters.class).createMarshaller());
			jaxbMarhallers.get(GraphConstructionDBScan.name).setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			jaxbMarhallers.put(GraphConstructionDBScanRecursive.name,
					JAXBContext.newInstance(GraphConstructionDBScanRecursiveParameters.class).createMarshaller());
			jaxbMarhallers.get(GraphConstructionDBScanRecursive.name).setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			Marshaller graphsMarshaller = JAXBContext.newInstance(GraphWindows.class).createMarshaller();
			graphsMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.newDocument();
			DocumentFragment fdoc = doc.createDocumentFragment();

			Element root = doc.createElement("root");
			for (String algoName: graphs.keySet())
			{
				Element e = doc.createElement("entry");
				Element f = doc.createElement("key");
				f.setTextContent(algoName);
				e.appendChild(f);
				f = doc.createElement("value");
				for (GraphConstructionParameters ps: graphs.get(algoName).keySet())
				{
					Element g = doc.createElement("entry");
					Element h = doc.createElement("key");

					if (ps.algoName().equals(GraphConstructionDBScan.name))
						jaxbMarhallers.get(GraphConstructionDBScan.name).marshal((GraphConstructionDBScanParameters) ps, h);
					else if (ps.algoName().equals(GraphConstructionDBScanRecursive.name))
						jaxbMarhallers.get(GraphConstructionDBScanRecursive.name).marshal((GraphConstructionDBScanRecursiveParameters) ps, h);
					else
						assert(false);

					g.appendChild(h);

					h = doc.createElement("value");
					graphsMarshaller.marshal(graphs.get(algoName).get(ps), h);
					g.appendChild(h);
					f.appendChild(g);
				}

				e.appendChild(f);
				root.appendChild(e);
			}
			fdoc.appendChild(root);

			StringWriter writer = new StringWriter();
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
			transformer.transform(new DOMSource(fdoc), new StreamResult(writer));

			return writer.getBuffer().toString();
		}

		@Override
		public HashMap<String, HashMap<GraphConstructionParameters, GraphWindows>> unmarshal(String v) throws Exception
		{
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			StringReader sr = new StringReader(v);
			Document doc = db.parse(new InputSource(sr));

			HashMap<String, Unmarshaller> paramsUnmarshallers = new HashMap<> ();
			paramsUnmarshallers.put(GraphConstructionDBScan.name,
					JAXBContext.newInstance(GraphConstructionDBScanParameters.class).createUnmarshaller());
			paramsUnmarshallers.put(GraphConstructionDBScanRecursive.name,
					JAXBContext.newInstance(GraphConstructionDBScanRecursiveParameters.class).createUnmarshaller());
			Unmarshaller graphsUnmarshaller = JAXBContext.newInstance(GraphWindows.class).createUnmarshaller();

			HashMap<String, HashMap<GraphConstructionParameters, GraphWindows>> res = new HashMap<> ();

			Node n = doc.getFirstChild();
			n = n.getFirstChild();
			while (n != null)
			{
				if (n.getNodeName().equals("#text"))
				{
					n = n.getNextSibling();
					continue;
				}

				assert(n.getNodeName().equals("entry"));
				Node nn = n.getFirstChild();
				while (nn != null)
				{
					if (nn.getNodeName().equals("#text"))
					{
						nn = nn.getNextSibling();
						continue;
					}
					assert(nn.getNodeName().equals("key"));

					String algoName = nn.getTextContent();
					if (!res.containsKey(algoName))
						res.put(algoName, new HashMap<> ());

					nn = nn.getNextSibling();
					while (nn != null && nn.getNodeName().equals("#text"))
						nn = nn.getNextSibling();
					assert(nn.getNodeName().equals("value"));

					Node nnn = nn.getFirstChild();
					while (nnn != null)
					{
						if (nnn.getNodeName().equals("#text"))
						{
							nnn = nnn.getNextSibling();
							continue;
						}
						assert(nnn.getNodeName().equals("entry"));

						Node nnnn = nnn.getFirstChild();
						while (nnnn != null && nnnn.getNodeName().equals("#text"))
							nnnn = nnnn.getNextSibling();
						assert(nnnn.getNodeName().equals("key"));

						Node nnnnn = nnnn.getFirstChild();
						while (nnnnn != null && nnnnn.getNodeName().equals("#text"))
							nnnnn = nnnnn.getNextSibling();
						assert(nnnnn.getNodeName().contains("Parameters"));

						GraphConstructionParameters ps = null;
						if (algoName.equals(GraphConstructionDBScan.name))
							ps = (GraphConstructionDBScanParameters) paramsUnmarshallers.get(algoName).unmarshal(nnnnn);
						else if (algoName.equals(GraphConstructionDBScanRecursive.name))
							ps = (GraphConstructionDBScanRecursiveParameters) paramsUnmarshallers.get(algoName).unmarshal(nnnnn);
						assert(ps != null);

						nnnn = nnnn.getNextSibling();
						while (nnnn != null && nnnn.getNodeName().equals("#text"))
							nnnn = nnnn.getNextSibling();
						assert(nnnn.getNodeName().equals("value"));

						nnnnn = nnnn.getFirstChild();
						while (nnnnn != null && nnnnn.getNodeName().equals("#text"))
							nnnnn = nnnnn.getNextSibling();

						res.get(algoName).put(ps, (GraphWindows) graphsUnmarshaller.unmarshal(nnnnn));

						nnn = nnn.getNextSibling();
					}
					nn = nn.getNextSibling();
				}
				n = n.getNextSibling();
			}

			return res;
		}
	}

	public static class WellLinkWindows extends XmlAdapter<String, ArrayList<ArrayList<WindowIndex>>>
	{
		@Override
		public String marshal(ArrayList<ArrayList<WindowIndex>> v)
		{
			StringBuilder sb = new StringBuilder ();
			for (int i = 0; i < v.size(); ++i)
				for (int j = 0; j < v.get(i).size(); ++j)
					sb.append(String.format("%d %d %d\n", i, v.get(i).get(j).winIdx, v.get(i).get(j).wellIdx));
			return sb.toString();
		}

		@Override
		public ArrayList<ArrayList<WindowIndex>> unmarshal(String v) throws Exception
		{
			ArrayList<ArrayList<WindowIndex>> res = new ArrayList<>();
			for (String tmp: v.split("\n"))
			{
				String[] elts = tmp.split(" ");

				Integer i = Integer.valueOf(elts[0]);
				Integer winIdx = Integer.valueOf(elts[1]);
				Integer wellIdx = Integer.valueOf(elts[2]);

				if (i >= res.size())
					res.add(new ArrayList<>());
				res.get(i).add(new WellLinker.WindowIndex(winIdx, wellIdx));
			}

			return res;
		}
	}

	public static class TrajIds extends XmlAdapter<String, ArrayList<Set<Integer>>>
	{
		@Override
		public String marshal(ArrayList<Set<Integer>> v)
		{
			StringBuilder sb = new StringBuilder ();
			for (int i = 0; i < v.size(); ++i)
			{
				for (Integer j: v.get(i))
					sb.append(String.format("%d ", j));
				sb.append("\n");
			}
			return sb.toString();
		}

		@Override
		public ArrayList<Set<Integer>> unmarshal(String v) throws Exception
		{
			ArrayList<Set<Integer>> res = new ArrayList<>();
			for (String tmp: v.split("\n"))
			{
				res.add(new HashSet<Integer> ());
				if (tmp.isEmpty())
					continue;

				for (String e: tmp.split(" "))
					res.get(res.size()-1).add(Integer.parseInt(e));
			}

			return res;
		}
	}

	public static class WellScoreAdapter extends XmlAdapter<String, WellScore>
	{
		@Override
		public String marshal(WellScore v) throws JAXBException
		{
			JAXBContext jaxbContext = null;
			if (v instanceof WellScore.Empty)
				jaxbContext = JAXBContext.newInstance(WellScore.Empty.class);
			else if (v instanceof WellScore.Parabolic)
				jaxbContext = JAXBContext.newInstance(WellScore.Parabolic.class);
			else if (v instanceof WellScore.Likelihood)
				jaxbContext = JAXBContext.newInstance(WellScore.Likelihood.class);
			else if (v instanceof WellScore.Density)
				jaxbContext = JAXBContext.newInstance(WellScore.Density.class);
			else
				assert(false);

			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

			StringWriter sw = new StringWriter();
			jaxbMarshaller.marshal(v, sw);
			return sw.toString();
		}

		@Override
		public WellScore unmarshal(String v) throws Exception
		{
			JAXBContext jaxbContext = null;

			if (v.contains("EmpyWellScore"))
				return WellScore.empty;
			else if (v.contains("ParabolicWellScore"))
				jaxbContext = JAXBContext.newInstance(WellScore.Parabolic.class);
			else if (v.contains("LikelihoodWellScore"))
				jaxbContext = JAXBContext.newInstance(WellScore.Likelihood.class);
			else if (v.contains("DensityWellScore"))
				jaxbContext = JAXBContext.newInstance(WellScore.Density.class);
			else
				assert(false);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			StringReader sr = new StringReader(v);
			return (WellScore) jaxbUnmarshaller.unmarshal(sr);
		}
	}

	public static class WellEstimatorAdapter extends XmlAdapter<String, WellEstimator>
	{
		@Override
		public String marshal(WellEstimator v) throws JAXBException
		{
			JAXBContext jaxbContext = null;
			if (v instanceof WellEstimator.NullWellEstimator)
				jaxbContext = JAXBContext.newInstance(WellEstimator.NullWellEstimator.class);
			else if (v instanceof LeastSquareEstimatorCircle)
				jaxbContext = JAXBContext.newInstance(LeastSquareEstimatorCircle.class);
			else if (v instanceof LeastSquareEstimatorEllipse)
				jaxbContext = JAXBContext.newInstance(LeastSquareEstimatorEllipse.class);
			else if (v instanceof LeastSquareEstimatorEllipseFit)
				jaxbContext = JAXBContext.newInstance(LeastSquareEstimatorEllipseFit.class);
			else if (v instanceof LeastSquareEstimatorEllipseNorm)
				jaxbContext = JAXBContext.newInstance(LeastSquareEstimatorEllipseNorm.class);
			else if (v instanceof LeastSquareEstimatorEllipseNormFit)
				jaxbContext = JAXBContext.newInstance(LeastSquareEstimatorEllipseNormFit.class);
			else if (v instanceof MaxLikelihoodEstimator)
				jaxbContext = JAXBContext.newInstance(MaxLikelihoodEstimator.class);
			else if (v instanceof DensityEstimator)
				jaxbContext = JAXBContext.newInstance(DensityEstimator.class);
			else
				assert(false);

			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

			StringWriter sw = new StringWriter();
			jaxbMarshaller.marshal(v, sw);
			return sw.toString();
		}

		@Override
		public WellEstimator unmarshal(String v) throws Exception
		{
			JAXBContext jaxbContext = null;

			if (v.contains("NullWellEstimator"))
				return WellEstimator.empty;
			else if (v.contains("LeastSquareEstimatorCircle"))
				jaxbContext = JAXBContext.newInstance(LeastSquareEstimatorCircle.class);
			else if (v.contains("LeastSquareEstimatorEllipse"))
				jaxbContext = JAXBContext.newInstance(LeastSquareEstimatorEllipse.class);
			else if (v.contains("LeastSquareEstimatorEllipseFit"))
				jaxbContext = JAXBContext.newInstance(LeastSquareEstimatorEllipseFit.class);
			else if (v.contains("LeastSquareEstimatorEllipseNorm"))
				jaxbContext = JAXBContext.newInstance(LeastSquareEstimatorEllipseNorm.class);
			else if (v.contains("LeastSquareEstimatorEllipseNormFit"))
				jaxbContext = JAXBContext.newInstance(LeastSquareEstimatorEllipseNormFit.class);
			else if (v.contains("MaxLikelihoodEstimator"))
				jaxbContext = JAXBContext.newInstance(MaxLikelihoodEstimator.class);
			else if (v.contains("DensityEstimator"))
				jaxbContext = JAXBContext.newInstance(DensityEstimator.class);
			else
				assert(false);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			StringReader sr = new StringReader(v);
			return (WellEstimator) jaxbUnmarshaller.unmarshal(sr);
		}
	}

	public static class WellEstimatorParametersAdapter extends XmlAdapter<String, WellEstimatorParameters>
	{
		@Override
		public String marshal(WellEstimatorParameters v) throws JAXBException
		{
			JAXBContext jaxbContext = null;
			if (v instanceof GridEstimatorParameters)
				jaxbContext = JAXBContext.newInstance(GridEstimatorParameters.class);
			else if (v instanceof MLEEstimatorParameters)
				jaxbContext = JAXBContext.newInstance(MLEEstimatorParameters.class);
			else if (v instanceof DensityEstimatorParameters)
				jaxbContext = JAXBContext.newInstance(DensityEstimatorParameters.class);
			else
				assert(false);

			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

			StringWriter sw = new StringWriter();
			jaxbMarshaller.marshal(v, sw);
			return sw.toString();
		}

		@Override
		public WellEstimatorParameters unmarshal(String v) throws Exception
		{
			JAXBContext jaxbContext = null;

			if (v.contains("GridEstimatorParameters"))
				jaxbContext = JAXBContext.newInstance(GridEstimatorParameters.class);
			else if (v.contains("MLEEstimatorParameters"))
				jaxbContext = JAXBContext.newInstance(MLEEstimatorParameters.class);
			else if (v.contains("DensityEstimatorParameters"))
				jaxbContext = JAXBContext.newInstance(DensityEstimatorParameters.class);
			else
				assert(false);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			StringReader sr = new StringReader(v);
			return (WellEstimatorParameters) jaxbUnmarshaller.unmarshal(sr);
		}
	}

	public static class IterationChooserAdapter extends XmlAdapter<String, IterationChooser>
	{
		@Override
		public String marshal(IterationChooser v) throws JAXBException
		{
			JAXBContext jaxbContext = null;
			if (v instanceof IterationChooser.BestParabScore)
				jaxbContext = JAXBContext.newInstance(IterationChooser.BestParabScore.class);
			else if (v instanceof IterationChooser.BestMLEScore)
				jaxbContext = JAXBContext.newInstance(IterationChooser.BestMLEScore.class);
			else if (v instanceof IterationChooser.BestMLERatioScore)
				jaxbContext = JAXBContext.newInstance(IterationChooser.BestMLERatioScore.class);
			else if (v instanceof IterationChooser.BestMLEDeltaScore)
				jaxbContext = JAXBContext.newInstance(IterationChooser.BestMLEDeltaScore.class);
			else if (v instanceof IterationChooser.MaxDensPerc)
				jaxbContext = JAXBContext.newInstance(IterationChooser.MaxDensPerc.class);
			else
				assert(false);

			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

			StringWriter sw = new StringWriter();
			jaxbMarshaller.marshal(v, sw);
			return sw.toString();
		}

		@Override
		public IterationChooser unmarshal(String v) throws Exception
		{
			JAXBContext jaxbContext = null;

			if (v.equals("null"))
				return null;

			if (v.contains("IterationChooserBestParabScore"))
				jaxbContext = JAXBContext.newInstance(IterationChooser.BestParabScore.class);
			else if (v.contains("IterationChooserBestMLEScore"))
				jaxbContext = JAXBContext.newInstance(IterationChooser.BestMLEScore.class);
			else if (v.contains("IterationChooserBestMLERatioScore"))
				jaxbContext = JAXBContext.newInstance(IterationChooser.BestMLERatioScore.class);
			else if (v.contains("IterationChooserBestMLEDeltaScore"))
				jaxbContext = JAXBContext.newInstance(IterationChooser.BestMLEDeltaScore.class);
			else if (v.contains("IterationChooserMaxDensPerc"))
				jaxbContext = JAXBContext.newInstance(IterationChooser.MaxDensPerc.class);
			else
				assert(false);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			StringReader sr = new StringReader(v);
			return (IterationChooser) jaxbUnmarshaller.unmarshal(sr);
		}
	}

	public static class IterationChooserParametersAdapter extends XmlAdapter<String, IterationChooser.Parameters>
	{
		@Override
		public String marshal(IterationChooser.Parameters v) throws JAXBException
		{
			JAXBContext jaxbContext = null;
			if (v instanceof IterationChooser.BestParabScore.Parameters)
				jaxbContext = JAXBContext.newInstance(IterationChooser.BestParabScore.Parameters.class);
			else if (v instanceof IterationChooser.BestMLEDeltaScore.Parameters)
				jaxbContext = JAXBContext.newInstance(IterationChooser.BestMLEDeltaScore.Parameters.class);
			else if (v instanceof IterationChooser.MaxDensPerc.Parameters)
				jaxbContext = JAXBContext.newInstance(IterationChooser.MaxDensPerc.Parameters.class);
			else if (v instanceof IterationChooser.MLE.Parameters)
				jaxbContext = JAXBContext.newInstance(IterationChooser.MLE.Parameters.class);
			else
				assert(false);

			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

			StringWriter sw = new StringWriter();
			jaxbMarshaller.marshal(v, sw);
			return sw.toString();
		}

		@Override
		public IterationChooser.Parameters unmarshal(String v) throws Exception
		{
			JAXBContext jaxbContext = null;

			if (v.contains("IterationChooserBestParabScore"))
				jaxbContext = JAXBContext.newInstance(IterationChooser.BestParabScore.Parameters.class);
			else if (v.contains("IterationChooserMLEParameters"))
				jaxbContext = JAXBContext.newInstance(IterationChooser.MLE.Parameters.class);
			else if (v.contains("IterationChooserBestMLEDeltaScoreParameters"))
				jaxbContext = JAXBContext.newInstance(IterationChooser.BestMLEDeltaScore.Parameters.class);
			else if (v.contains("IterationChooserMaxDensPercParameters"))
				jaxbContext = JAXBContext.newInstance(IterationChooser.MaxDensPerc.Parameters.class);
			else
				assert(false);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			StringReader sr = new StringReader(v);
			return (IterationChooser.Parameters) jaxbUnmarshaller.unmarshal(sr);
		}
	}

	public static class PolygonAdapter extends XmlAdapter<String, Polygon>
	{
		@Override
		public String marshal(Polygon poly)
		{
			StringBuilder sb = new StringBuilder ();
			sb.append(String.format("%d\n", poly.getNumPoints()));
			for (final Coordinate coord: poly.getCoordinates())
				sb.append(String.format("%g %g\n",
						coord.getOrdinate(0), coord.getOrdinate(1)));
			return sb.toString();
		}

		@Override
		public Polygon unmarshal(String v) throws Exception
		{
			String[] lines = v.split("\n");
			int N = Integer.parseInt(lines[0]);

			GeometryFactory geo_facto = new GeometryFactory();
			CoordinateSequence cse = geo_facto.getCoordinateSequenceFactory().create(N, 2);
			for (int i = 1; i < lines.length; ++i)
			{
				String[] elts = lines[i].split(" ");
				cse.setOrdinate(i-1, 0, Double.parseDouble(elts[0]));
				cse.setOrdinate(i-1, 1, Double.parseDouble(elts[1]));
			}

			return geo_facto.createPolygon(cse);
		}
	}

	public static class NodeAdapter extends XmlAdapter<String, HashMap<Integer, Shape>>
	{
		@Override
		public String marshal(HashMap<Integer, Shape> nodes) throws Exception
		{
			if (nodes.isEmpty())
				return "";

			Shape elt = nodes.values().iterator().next();

			JAXBContext jaxbContext = null;
			if (elt instanceof Ellipse)
				jaxbContext = JAXBContext.newInstance(Ellipse.class);
			else if (elt instanceof MyPolygon)
				jaxbContext = JAXBContext.newInstance(MyPolygon.class);
			else
				assert(false);

			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.newDocument();
			DocumentFragment fdoc = doc.createDocumentFragment();

			Element root = doc.createElement("root");
			for (Integer nodeId: nodes.keySet())
			{
				Element e = doc.createElement("entry");
				Element f = doc.createElement("key");
				f.setTextContent(String.valueOf(nodeId));
				e.appendChild(f);
				f = doc.createElement("value");
				jaxbMarshaller.marshal(nodes.get(nodeId), f);
				e.appendChild(f);
				root.appendChild(e);
			}
			fdoc.appendChild(root);

			StringWriter writer = new StringWriter();
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
			transformer.transform(new DOMSource(fdoc), new StreamResult(writer));

			return writer.getBuffer().toString();
		}

		@Override
		public HashMap<Integer, Shape> unmarshal(String v) throws Exception
		{
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			StringReader sr = new StringReader(v);
			Document doc = db.parse(new InputSource(sr));

			Unmarshaller nodeUnmarshaller = null;
			if (v.contains("Ellipse"))
				nodeUnmarshaller = JAXBContext.newInstance(Ellipse.class).createUnmarshaller();
			else
				nodeUnmarshaller = JAXBContext.newInstance(MyPolygon.class).createUnmarshaller();

			HashMap<Integer, Shape> res = new HashMap<> ();

			Node n = doc.getFirstChild();
			n = n.getFirstChild();
			while (n != null)
			{
				if (n.getNodeName().equals("#text"))
				{
					n = n.getNextSibling();
					continue;
				}

				assert(n.getNodeName().equals("entry"));
				Node nn = n.getFirstChild();
				while (nn != null)
				{
					if (nn.getNodeName().equals("#text"))
					{
						nn = nn.getNextSibling();
						continue;
					}
					assert(nn.getNodeName().equals("key"));
					Integer nodeId = Integer.valueOf(nn.getTextContent());

					nn = nn.getNextSibling();
					while (nn != null && nn.getNodeName().equals("#text"))
						nn = nn.getNextSibling();
					assert(nn.getNodeName().equals("value"));

					Node nnn = nn.getFirstChild();
					while (nnn != null && nnn.getNodeName().equals("#text"))
						nnn = nnn.getNextSibling();

					res.put(nodeId, (Shape) nodeUnmarshaller.unmarshal(nnn));

					nn = nn.getNextSibling();
				}
				n = n.getNextSibling();
			}

			return res;
		}
	}

	public static class ConnectivityMatrixAdapter extends XmlAdapter<String, HashMap<Integer, HashMap<Integer, Double>>>
	{
		@Override
		public String marshal(HashMap<Integer, HashMap<Integer, Double>> C)
		{
			StringBuilder sb = new StringBuilder ();
			for (Integer i: C.keySet())
				for (Integer j: C.get(i).keySet())
					sb.append(String.format("%d %d %g\n", i, j, C.get(i).get(j)));
			return sb.toString();
		}

		@Override
		public HashMap<Integer, HashMap<Integer, Double>> unmarshal(String v) throws Exception
		{
			HashMap<Integer, HashMap<Integer, Double>> res = new HashMap<Integer, HashMap<Integer, Double>> ();
			for (String tmp: v.split("\n"))
			{
				String[] elts = tmp.split(" ");

				Integer i = Integer.valueOf(elts[0]);
				Integer j = Integer.valueOf(elts[1]);
				Double val = Double.valueOf(elts[2]);

				if (!res.containsKey(i))
					res.put(i, new HashMap<Integer, Double> ());
				res.get(i).put(j, val);
			}

			return res;
		}
	}
}
