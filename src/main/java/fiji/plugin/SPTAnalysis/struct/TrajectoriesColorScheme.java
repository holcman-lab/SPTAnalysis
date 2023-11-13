package fiji.plugin.SPTAnalysis.struct;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import fiji.plugin.SPTAnalysis.external.InterpolatePaintScale;

public class TrajectoriesColorScheme
{
	public static enum ColoringType {Uniform, Random, InstVel};
	public static Color uniformColor = Color.yellow;

	final protected Random rand;
	protected ColoringType coloringType;
	protected ArrayList<Color> trajsCols;
	protected ArrayList<ArrayList<Color>> dispsCols;
	protected ArrayList<double[]> instVels;
	protected double maxInstVel;

	public TrajectoriesColorScheme(final TrajectoryEnsemble te , final ColoringType coloringType)
	{
		this.rand = new Random();
		this.coloringType = coloringType;

		this.trajsCols = new ArrayList<> ();
		for (int j = 0; j < te.trajs().size(); ++j)
			this.trajsCols.add(new Color(this.rand.nextInt(256),
					this.rand.nextInt(256), this.rand.nextInt(256)));


		InterpolatePaintScale cm = InterpolatePaintScale.Jet;
		this.instVels = new ArrayList<>();
		for (final Trajectory tr: te.trajs())
			this.instVels.add(tr.instantaneousVelocities());
		this.maxInstVel = computeMax(this.instVels);


		this.dispsCols = new ArrayList<>();
		for (int j = 0; j < this.instVels.size(); ++j)
		{
			this.dispsCols.add(new ArrayList<> ());
			for (int k = 0; k < this.instVels.get(j).length; ++k)
				this.dispsCols.get(j).add(cm.getPaint(this.instVels.get(j)[k] / this.maxInstVel));
		}
	}

	public ColoringType coloringType()
	{
		return this.coloringType;
	}

	static public double computeMax(final ArrayList<double[]> vels)
	{
		double res = 0.0;
		for (final double[] vs: vels)
			for (double d: vs)
				if (d > res)
					res = d;
		return res;
	}


	public ArrayList<Color> trajsCols()
	{
		return this.trajsCols;
	}

	public ArrayList<ArrayList<Color>> dispsCols()
	{
		return this.dispsCols;
	}

	public ArrayList<double[]> instVels()
	{
		return this.instVels;
	}
	
	public double maxInstVel()
	{
		return this.maxInstVel;
	}
}
