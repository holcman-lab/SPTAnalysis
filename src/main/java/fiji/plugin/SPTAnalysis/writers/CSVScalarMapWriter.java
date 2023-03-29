package fiji.plugin.SPTAnalysis.writers;

import java.util.ArrayList;
import java.util.Iterator;

import fiji.plugin.SPTAnalysis.struct.ScalarMap;

public class CSVScalarMapWriter extends CSVWriter
{
	protected final ScalarMap scalMap;
	protected final ArrayList<int[]> nhs;

	public CSVScalarMapWriter(final String delim, ScalarMap scalMap)
	{
		super(delim);

		this.scalMap = scalMap;
		this.nhs = null;
	}
	
	public CSVScalarMapWriter(final String delim, ScalarMap scalMap, ArrayList<int[]> nhs)
	{
		super(delim);

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
				sb.append(String.format("%g%s%g%s%g\n", p[0], this.delim, p[1],
						this.delim, v[2]));
			}
		}
		else
		{
			for (final int[] nh: nhs)
			{
				double[] p = this.scalMap.grid().get(nh[0], nh[1]);
				if (this.scalMap.isSet(nh[0], nh[1]))
					sb.append(String.format("%g%s%g%s%g\n", p[0], this.delim,
							p[1], this.delim, this.scalMap.get(nh[0], nh[1])));
			}
		}

		return sb.toString();
	}
}
