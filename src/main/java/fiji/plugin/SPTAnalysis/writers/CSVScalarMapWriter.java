package fiji.plugin.SPTAnalysis.writers;

import java.util.ArrayList;
import java.util.Iterator;

import fiji.plugin.SPTAnalysis.struct.ScalarMap;

public class CSVScalarMapWriter extends CSVWriter
{
	protected final ScalarMap scalMap;
	protected final ArrayList<int[]> nhs;

	public CSVScalarMapWriter(ScalarMap scalMap)
	{
		this.scalMap = scalMap;
		this.nhs = null;
	}
	
	public CSVScalarMapWriter(ScalarMap scalMap,  ArrayList<int[]> nhs)
	{
		this.scalMap = scalMap;
		this.nhs = nhs;
	}

	@Override
	public String generate()
	{
		StringBuilder sb = new StringBuilder ();

		if (this.nhs == null)
		{
			Iterator<double[]> it = this.scalMap.iterator();
			while (it.hasNext())
			{
				double[] v = it.next();
				double[] p = this.scalMap.grid().get((int) v[0], (int) v[1]);
				sb.append(String.format("%g %g %g\n", p[0], p[1], v[2]));
			}
		}
		else
		{
			for (final int[] nh: nhs)
			{
				double[] p = this.scalMap.grid().get(nh[0], nh[1]);
				if (this.scalMap.isSet(nh[0], nh[1]))
					sb.append(String.format("%g %g %g\n", p[0], p[1], this.scalMap.get(nh[0], nh[1])));
			}
		}

		return sb.toString();
	}
}
