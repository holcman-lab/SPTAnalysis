package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.*;

import org.junit.Test;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;


public class IntersectionTest
{
	@Test
	public void test1()
	{
		GeometryFactory geo_facto = new GeometryFactory();

		CoordinateSequence cse = geo_facto.getCoordinateSequenceFactory().create(2, 2);
		cse.setOrdinate(0, 0, 1);
		cse.setOrdinate(0, 1, 3);
		cse.setOrdinate(1, 0, 3);
		cse.setOrdinate(1, 1, 3);
		LineString ls1 = geo_facto.createLineString(cse);

		cse = geo_facto.getCoordinateSequenceFactory().create(2, 2);
		cse.setOrdinate(0, 0, 2);
		cse.setOrdinate(0, 1, 1);
		cse.setOrdinate(1, 0, 2);
		cse.setOrdinate(1, 1, 4);
		LineString ls2 = geo_facto.createLineString(cse);

		Geometry interPts = ls1.intersection(ls2);
		assertEquals(1, interPts.getNumPoints());
		assertEquals(2.0, interPts.getCoordinates()[0].getOrdinate(0), 1e-3);
		assertEquals(3.0, interPts.getCoordinates()[0].getOrdinate(1), 1e-3);
	}
}