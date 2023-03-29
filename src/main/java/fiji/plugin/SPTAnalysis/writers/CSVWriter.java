package fiji.plugin.SPTAnalysis.writers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public abstract class CSVWriter
{
	protected String delim;

	public CSVWriter(final String delim)
	{
		this.delim = delim;
	}

	public abstract String generate();

	public static void saveCSV(String fname, CSVWriter csvw) throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(fname));
		writer.write(csvw.generate());
		writer.close();
	}
}
