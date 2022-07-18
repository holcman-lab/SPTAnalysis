package fiji.plugin.SPTAnalysis;

import java.util.ArrayList;

public class TestUtils
{
	public static boolean pointInList(ArrayList<int[]> l, int[] val)
	{
		for (int[] v: l)
			if (v[0] == val[0] && v[1] == val[1])
				return true;
		return false;
	}

	public static boolean pointInList(ArrayList<double[]> l, double[] val)
	{
		for (double[] v: l)
			if (v[0] == val[0] && v[1] == val[1])
				return true;
		return false;
	}
}
