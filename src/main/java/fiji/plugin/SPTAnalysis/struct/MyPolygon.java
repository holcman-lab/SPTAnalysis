package fiji.plugin.SPTAnalysis.struct;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import fiji.plugin.SPTAnalysis.XMLAdapters;

@XmlRootElement(name = "MyPolygon")
@XmlAccessorType(XmlAccessType.FIELD)
public class MyPolygon extends Shape
{
	@XmlJavaTypeAdapter(XMLAdapters.PolygonAdapter.class)
	protected Polygon poly;

	public MyPolygon()
	{
		this.poly = null;
	}

	public MyPolygon(final Polygon poly)
	{
		this.poly = poly;
	}

	public Polygon poly()
	{
		return this.poly;
	}

	@Override
	public boolean inside(double[] p)
	{
		final GeometryFactory gf = new GeometryFactory();
		final Point point = gf.createPoint(new Coordinate(p[0], p[1]));
		return point.within(this.poly);
	}

	@Override
	public boolean intersect(final Shape s)
	{
		assert(s instanceof MyPolygon);
		final Polygon p2 = ((MyPolygon) s).poly();
		
		return (this.poly.intersects(p2) || p2.intersects(this.poly) ||
				this.poly.contains(p2) || p2.contains(this.poly));
	}

	@Override
	public double[] center()
	{
		double[] c = {0.0, 0.0};
		for (final Coordinate coord: this.poly.getCoordinates())
		{
			c[0] += coord.getOrdinate(0);
			c[1] += coord.getOrdinate(1);
		}

		return new double[] {c[0] / this.poly.getNumPoints(),
				c[1] / this.poly.getNumPoints()};
	}

	public ArrayList<fiji.plugin.SPTAnalysis.struct.Point> points()
	{
		ArrayList<fiji.plugin.SPTAnalysis.struct.Point> res = new ArrayList<> ();
		for (final Coordinate coord: this.poly.getCoordinates())
			res.add(new fiji.plugin.SPTAnalysis.struct.Point(coord.getOrdinate(0), coord.getOrdinate(1)));
		return res;
	}

	@Override
	public double area()
	{
		return this.poly.getArea();
	}

	public double[] centerToCenterIntersect(final MyPolygon poly2)
	{
		double[] c1 = this.center();
		double[] c2 = poly2.center();

		GeometryFactory geo_facto = new GeometryFactory();
		CoordinateSequence cse = geo_facto.getCoordinateSequenceFactory().create(2, 2);
		cse.setOrdinate(0, 0, c1[0]);
		cse.setOrdinate(0, 1, c1[1]);
		cse.setOrdinate(1, 0, c2[0]);
		cse.setOrdinate(1, 1, c2[1]);
		LineString line1 = geo_facto.createLineString(cse);

		//here we test in any line of the polygon intersects line1
		for (int i = 0; i < this.poly.getNumPoints() - 1; ++i)
		{
			cse = geo_facto.getCoordinateSequenceFactory().create(2, 2);
			cse.setOrdinate(0, 0, this.poly.getCoordinates()[i].getOrdinate(0));
			cse.setOrdinate(0, 1, this.poly.getCoordinates()[i].getOrdinate(1));
			cse.setOrdinate(1, 0, this.poly.getCoordinates()[i+1].getOrdinate(0));
			cse.setOrdinate(1, 1, this.poly.getCoordinates()[i+1].getOrdinate(1));
			LineString line2 = geo_facto.createLineString(cse);

			Geometry interPts = line1.intersection(line2);
			if (interPts.getNumPoints() == 1)
				return new double[] {interPts.getCoordinates()[0].getOrdinate(0),
						 			 interPts.getCoordinates()[0].getOrdinate(1)};
		}

		return null;
	}

	public static MyPolygon convexHull(final ArrayList<fiji.plugin.SPTAnalysis.struct.Point> pts)
	{
		GeometryFactory geo_facto = new GeometryFactory();
		CoordinateSequence cse = geo_facto.getCoordinateSequenceFactory().create(pts.size()+1, 2);
		int cpt = 0;
		for (final fiji.plugin.SPTAnalysis.struct.Point p: pts)
		{
			cse.setOrdinate(cpt, 0, p.x);
			cse.setOrdinate(cpt, 1, p.y);
			++cpt;
		}
		cse.setOrdinate(cpt, 0, pts.get(0).x);
		cse.setOrdinate(cpt, 1, pts.get(0).y);

		return new MyPolygon((Polygon) geo_facto.createPolygon(cse).convexHull());
	}

	@Override
	public double[] minPt()
	{
		Coordinate[] coords = this.poly.getCoordinates();
		double[] mPt = {coords[0].getOrdinate(0), coords[0].getOrdinate(1)};
		for (int i = 1; i < coords.length; ++i)
		{
			mPt[0] = mPt[0] < coords[i].getOrdinate(0) ? mPt[0] : coords[i].getOrdinate(0);
			mPt[1] = mPt[1] < coords[i].getOrdinate(1) ? mPt[1] : coords[i].getOrdinate(1);
		}

		return mPt;
	}

	@Override
	public double[] maxPt()
	{
		Coordinate[] coords = this.poly.getCoordinates();
		double[] MPt = {coords[0].getOrdinate(0), coords[0].getOrdinate(1)};
		for (int i = 1; i < coords.length; ++i)
		{
			MPt[0] = MPt[0] > coords[i].getOrdinate(0) ? MPt[0] : coords[i].getOrdinate(0);
			MPt[1] = MPt[1] > coords[i].getOrdinate(1) ? MPt[1] : coords[i].getOrdinate(1);
		}

		return MPt;
	}
}
