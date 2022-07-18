package fiji.plugin.SPTAnalysis.struct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class LabeledPoint extends Point
{
	public int label;

	public LabeledPoint(int lab, double x, double y)
	{
		this.label = lab;
		this.t = Double.NaN;
		this.frame = null;
		this.x = x;
		this.y = y;
	}

	public static ArrayList<Integer> findLabedPtsIdxs(ArrayList<LabeledPoint> lpts,
			int lab)
	{
		ArrayList<Integer> res = new ArrayList<> ();
		for (int i = 0; i < lpts.size(); ++i)
			if (lpts.get(i).label == lab)
				res.add(i);
		return res;
	}

	public static ArrayList<Point> findLabedPts(ArrayList<LabeledPoint> lpts,
			int lab)
	{
		ArrayList<Point> res = new ArrayList<> ();
		for (LabeledPoint lpt: lpts)
			if (lpt.label == lab)
				res.add(lpt);
		return res;
	}

	public static Set<Integer> allUniqueLabels(ArrayList<LabeledPoint> lpts)
	{
		HashSet<Integer> res = new HashSet<> ();
		for (LabeledPoint lpt: lpts)
			res.add(lpt.label);
		return res;
	}

	public static int assignLabel(Point pt, HashMap<Integer, Shape> shapes)
	{
		for (int i: shapes.keySet())
			if (shapes.get(i).inside(pt.vec()))
				return i;
		return 0;
	}

	public static int countLabelPts(ArrayList<LabeledPoint> lpts, int lab)
	{
		int res = 0;
		for (int i = 0; i < lpts.size(); ++i)
			if (lpts.get(i).label == lab)
				++res;
		return res;
	}
}
