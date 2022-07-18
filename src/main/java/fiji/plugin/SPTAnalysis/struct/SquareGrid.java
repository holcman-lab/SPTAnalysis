package fiji.plugin.SPTAnalysis.struct;

import java.util.Iterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SquareGrid")
@XmlAccessorType(XmlAccessType.FIELD)
public class SquareGrid implements Iterable<int[]>
{
	public static final SquareGrid nullVal = new SquareGrid(TrajectoryEnsemble.nullVal,
			Double.NaN);

	protected double dx;
	protected double[] Xmin;
	protected double[] Xmax;

	public SquareGrid()
	{
	}

	public SquareGrid(double dx, double[] minx, double[] maxx)
	{
		this.dx = dx;
		this.Xmin = minx;
		this.Xmax = maxx;
	}

	public SquareGrid(TrajectoryEnsemble trajs, double dx)
	{
		this.dx = dx;
		this.Xmin = new double[] {0.0, 0.0};

		double[] max = new double[] {0.0, 0.0};
		for (Trajectory tr: trajs.trajs)
		{
			for (Point p: tr.points)
			{
				max[0] = p.x > max[0] ? p.x : max[0];
				max[1] = p.y > max[1] ? p.y : max[1];
			}
		}

		int i = 0;
		while (i * dx < max[0])
			++i;
		int j = 0;
		while (j * dx < max[1])
			++j;

		this.Xmax = new double[] {i * dx, j * dx};
	}

	public SquareGrid(double dx, double[] C, long ncells)
	// Generate a grid with bin width dx, centered at C and possessing
	// (ncells+1) * (ncells+1) bins
	{
		this.dx = dx;
		this.Xmin = new double[] {C[0] - dx/2 - ncells * dx,
								  C[1] - dx/2 - ncells * dx};
		this.Xmax = new double[] {C[0] + dx/2 + ncells * dx,
								  C[1] + dx/2 + ncells * dx};
	}

	public SquareGrid(double dx, final Ellipse ell)
	{
		double[] c = ell.mu();

		boolean go = true;
		int k = 1;
		while (go)
		{
			go = false;
			for (int i = -k; i <= k; ++i)
			{
				if (ell.inside(new double[] {c[0] + i * dx, c[1] - k * dx}) ||
					ell.inside(new double[] {c[0] + i * dx, c[1] + k * dx}) ||
					ell.inside(new double[] {c[0] - k * dx, c[1] + i * dx}) ||
					ell.inside(new double[] {c[0] + k * dx, c[1] + i * dx}))
				{
					go = true;
					break;
				}
			}
			k = k + 1;
		}

		this.dx = dx;
		this.Xmin = new double[] {c[0] - dx/2 - k * dx,
								  c[1] - dx/2 - k * dx};
		this.Xmax = new double[] {c[0] + dx/2 + k * dx,
								  c[1] + dx/2 + k * dx};
	}

	public double[] Xmin()
	{
		return this.Xmin;
	}

	public double[] Xmax()
	{
		return this.Xmax;
	}

	public Rectangle boundary()
	{
		return new Rectangle(this.Xmin, this.Xmax);
	}

	public String dump()
	{
		Iterator<int[]> it = this.iterator();

		String res = "";
		while (it.hasNext())
		{
			int[] gpos = it.next();
			double[] pos = this.get(gpos[0], gpos[1]);
			res += String.valueOf(pos[0]) + "," + String.valueOf(pos[1]) + "\n";
		}

		return res;
	}

	public String dumpAlongDimension()
	{
		String res = String.valueOf(this.Xmin[0]);
		for (double x = this.Xmin[0] + dx; x <= this.Xmax[0]; x += this.dx)
			res += "," + String.valueOf(x);
		res += "\n" + String.valueOf(this.Xmin[1]);
		for (double y = this.Xmin[1] + dx; y <= this.Xmax[1]; y += this.dx)
			res += "," + String.valueOf(y);
		res += "\n";

		return res;
	}

	public int[] pos_to_gpos(double[] p)
	{
		return new int[] {(int) Math.floor(Math.round(((p[0] - this.Xmin[0]) / this.dx) * 1e6) / 1e6), 
						  (int) Math.floor(Math.round(((p[1] - this.Xmin[1]) / this.dx) * 1e6) / 1e6)};
	}

	public int[] max_pos()
	{
		return pos_to_gpos(this.Xmax);
	}

	public double dx()
	{
		return this.dx;
	}

	public double[] get(Integer i, Integer j)
	{
		return new double[] {this.Xmin[0] + this.dx * i + this.dx/2,
							 this.Xmin[1] + this.dx * j + this.dx/2};
	}

	@Override
	public java.util.Iterator<int[]> iterator()
	{
		Iterator<int[]> it = new Iterator<int[]>()
		{
			private int i = 0;
			private int j = -1;

			@Override
			public boolean hasNext()
			{
				if (Xmin[1] + (j+1) * dx + dx/2 > Xmax[1])
					return Xmin[0] + (i+1) * dx + dx/2 < Xmax[0];
				return Xmin[0] + i * dx + dx/2 < Xmax[0];
			}

			@Override
			public int[] next()
			{
				if (j == -1 || Xmin[1] + (j + 1) * dx + dx/2 < Xmax[1])
					++j;
				else
				{
					++i;
					j = 0;
				}

				return new int[] {i, j};
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
		return it;
	}
}