package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.*;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import fiji.plugin.SPTAnalysis.estimators.WellScore;
import fiji.plugin.SPTAnalysis.struct.PotWell;


public class UnMarshallingTest
{
	@Test public void test0()
	{
		String base = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
					"<PotWell>\n" + 
					"    <ell>\n" + 
					"        <mu>10.0 11.0</mu>\n" + 
					"        <rad>12.0 13.0</rad>\n" + 
					"        <phi>14.0</phi>\n" + 
					"    </ell>\n" + 
					"    <A>0.7</A>\n" + 
					"    <D>0.07</D>\n" + 
					"    <score>&lt;ParabolicWellScore&gt;\n" + 
					"    &lt;value&gt;0.13&lt;/value&gt;\n" + 
					"&lt;/ParabolicWellScore&gt;</score>\n" + 
					"    <correctedD>false</correctedD>\n" + 
					"</PotWell>\n";
		
		StringReader sr = new StringReader(base);

		PotWell w = null;
		try
		{
			JAXBContext jaxbContext = JAXBContext.newInstance(PotWell.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			w = (PotWell) jaxbUnmarshaller.unmarshal(sr);
		}
		catch (JAXBException e)
		{
		}


		assertArrayEquals(new double[] {10.0, 11.0}, w.ell().mu(), 1e-1);
		assertArrayEquals(new double[] {12.0, 13.0}, w.ell().rad(), 1e-1);
		assertEquals(14.0, w.ell().phi(), 1e-1);
		assertEquals(0.7, w.A(), 1e-1);
		assertEquals(0.07, w.D(), 1e-2);
		assertTrue(w.score() instanceof WellScore.Parabolic);
		assertEquals(0.13, w.score().value(), 1e-2);
	}
}
