package fiji.plugin.SPTAnalysis.writers;

import java.util.ArrayList;
import java.util.Iterator;

import fiji.plugin.SPTAnalysis.struct.VectorMap;

public class CSVVectorMapWriter extends CSVWriter
{
	protected final VectorMap vecMap;
	protected final ArrayList<int[]> nhs;
	protected boolean writeCoord;

	public CSVVectorMapWriter(final String delim, VectorMap vecMap)
	{
		super(delim);

		this.vecMap = vecMap;
		this.nhs = null;
		this.writeCoord = true;
	}

	public CSVVectorMapWriter(final String delim, VectorMap vecMap,  ArrayList<int[]> nhs)
	{
		super(delim);

		this.vecMap = vecMap;
		this.nhs = nhs;
		this.writeCoord = true;
	}

	public void writeCoord(boolean v)
	{
		this.writeCoord = v;
	}

	@Override
	public String generate()
	{
		StringBuilder sb = new StringBuilder ();

		if (this.nhs == null)
		{
			Iterator<double[]> it = this.vecMap.iterator();
			while (it.hasNext())
			{
				double[] v = it.next();
	
				if (this.writeCoord)
				{
					double[] p = this.vecMap.grid().get((int) v[0], (int) v[1]);
					sb.append(String.format("%g%s%g%s%g%s%g\n", p[1], this.delim, p[0],
							this.delim, v[2], this.delim, v[3]));
				}
				else
					sb.append(String.format("%d%s%d%s%g%s%g\n", (int) v[1], this.delim,
							(int) v[0], this.delim, v[2], this.delim, v[3]));
			}
		}
		else
		{
			for (final int[] nh: nhs)
			{
				if (!this.vecMap.isSet(nh[0], nh[1]))
					continue;

				Double[] v = this.vecMap.get(nh[0], nh[1]);
				if (this.writeCoord)
				{
					double[] p = this.vecMap.grid().get(nh[0], nh[1]);
					sb.append(String.format("%g%s%g%s%g%s%g\n", p[1], this.delim,
							p[0], this.delim, v[0], this.delim, v[1]));
				}
				else
					sb.append(String.format("%d%s%d%s%g%s%g\n", nh[1], this.delim, nh[0],
							this.delim, v[0], this.delim, v[1]));
			}
		}

		return sb.toString();
	}
}
