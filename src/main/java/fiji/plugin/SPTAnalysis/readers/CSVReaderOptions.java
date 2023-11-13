package fiji.plugin.SPTAnalysis.readers;

public class CSVReaderOptions
{
	private String delim;

	private int idPos;
	private int tPos;
	private int xPos;
	private int yPos;

	boolean hasZ = false;
	private Integer zPos;

	private int skipHeadLines;

	private boolean unitIsPx = false;
	private Double pxSize;

	private boolean unitIsFrame = false;
	private Double dt;


	public CSVReaderOptions()
	{
	}

	public CSVReaderOptions(String delimiter, int idPos, int tPos, int xPos, int yPos,
				boolean hasZ, int zPos, int skipHeadLines, boolean unitIsPx, double pxSize,
				boolean unitIsFrame, double dt)
	{
		this.delim = delimiter;
		this.idPos = idPos;
		this.tPos = tPos;
		this.xPos = xPos;
		this.yPos = yPos;
		this.hasZ = hasZ;
		this.zPos = zPos;
		this.skipHeadLines = skipHeadLines;
		this.unitIsPx = unitIsPx;
		this.pxSize = pxSize;
		this.unitIsFrame = unitIsFrame;
		this.dt = dt;
	}

	public static CSVReaderOptions trackmateOptions()
	{
		return new CSVReaderOptions(",", 2, 7, 4, 5, false, 0, 1, false, Double.NaN, false, 0);
	}

	public String delim()
	{
		return this.delim;
	}

	public int idPos()
	{
		return this.idPos;
	}

	public int tPos()
	{
		return this.tPos;
	}

	public int xPos()
	{
		return this.xPos;
	}

	public int yPos()
	{
		return this.yPos;
	}

	public boolean hasZ()
	{
		return this.hasZ;
	}

	public int zPos()
	{
		return this.zPos;
	}

	public int skipHeadLines()
	{
		return this.skipHeadLines;
	}

	public boolean unitIsPx()
	{
		return this.unitIsPx;
	}

	public double pxSize()
	{
		return this.pxSize;
	}

	public boolean unitIsFrame()
	{
		return this.unitIsFrame;
	}

	public double dt()
	{
		return this.dt;
	}
}
