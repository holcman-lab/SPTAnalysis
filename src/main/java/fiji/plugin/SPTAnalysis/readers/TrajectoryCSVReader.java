package fiji.plugin.SPTAnalysis.readers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import fiji.plugin.SPTAnalysis.struct.Point;
import fiji.plugin.SPTAnalysis.struct.Trajectory;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;

public class TrajectoryCSVReader extends TrajectoryReader
{
	private static final Logger logger = Logger.getLogger(TrajectoryCSVReader.class.getName());
	static
	{
		logger.setUseParentHandlers(false);
		logger.addHandler(new ConsoleHandler());
		logger.getHandlers()[0].setFormatter(new SimpleFormatter() {
			@Override
			public synchronized String format(LogRecord lr)
			{
				return lr.getMessage() + System.lineSeparator();
			}
		});
	}

	private String fname;
	private CSVReaderOptions csvo;

	public TrajectoryCSVReader(String fname, CSVReaderOptions csvOpts)
	{
		this.fname = fname;
		this.csvo = csvOpts;
	}

	@Override
	public TrajectoryEnsemble read() throws Exception
	{
		logger.info("Loading trajectories from: " + this.fname);
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader(new File(this.fname)));
		}
		catch (FileNotFoundException e1)
		{
			e1.printStackTrace();
		}

		TrajectoryEnsemble res = new TrajectoryEnsemble();

		int cntHead = 0;
		Trajectory tr = new Trajectory();
		String st;
		while ((st = br.readLine()) != null)
		{
			if (cntHead < this.csvo.skipHeadLines())
			{
				cntHead += 1;
				continue;
			}

			String[] vals = st.split(this.csvo.delim());

			Integer cur_id = Integer.valueOf(vals[this.csvo.idPos()].trim());
			if (!cur_id.equals(tr.id()))
			{
				if (!tr.isEmpty())
					res.trajs().add(tr);
				tr = new Trajectory(cur_id);
			}

			Double t = Double.valueOf(vals[this.csvo.tPos()].trim());

			if (this.csvo.unitIsFrame())
				t = t * this.csvo.dt();

			double x = Double.valueOf(vals[this.csvo.xPos()].trim());
			double y = Double.valueOf(vals[this.csvo.yPos()].trim());
			double z = Double.NaN;
			if (this.csvo.hasZ())
				z = Double.valueOf(vals[this.csvo.zPos()].trim());

			if (this.csvo.unitIsPx())
			{
				x = x * this.csvo.pxSize();
				y = y * this.csvo.pxSize();
				if (this.csvo.hasZ())
					z = z * this.csvo.pxSize();
			}

			tr.points().add(new Point(t, x, y, z));
		}

		if (!tr.isEmpty())
			res.trajs().add(tr);



		logger.info(String.valueOf("  " + res.trajs().size()) + " trajectories");
		return res;
	}
}
