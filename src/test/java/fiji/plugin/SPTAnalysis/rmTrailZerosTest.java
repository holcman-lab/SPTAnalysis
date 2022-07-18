package fiji.plugin.SPTAnalysis;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class rmTrailZerosTest
{
	@Test
	public void test()
	{
		assertEquals("0.1", Utils.rmTrail0(String.format("%g", 0.1000000)));
		assertEquals("0.12508", Utils.rmTrail0(String.format("%g", 0.1250800)));
		assertEquals("0.005", Utils.rmTrail0(String.format("%g", 0.0050000)));
		assertEquals("0.0", Utils.rmTrail0(String.format("%g", 0.0000000)));
	}
}
