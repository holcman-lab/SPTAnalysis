package fiji.plugin.SPTAnalysis.struct;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;


import Jama.Matrix;
import Jama.SingularValueDecomposition;
import fiji.plugin.SPTAnalysis.Utils;

@XmlRootElement(name = "Ellipse")
@XmlAccessorType(XmlAccessType.FIELD)
public class Ellipse extends Shape
{
	public static final Ellipse nullVal = new Ellipse(new double[] {Double.NaN, Double.NaN},
			new double[] {Double.NaN, Double.NaN}, Double.NaN);

	@XmlList
	protected double[] mu;
	@XmlList
	protected double[] rad;
	protected double phi;

	public Ellipse()
	{
		this.mu = null;
		this.rad = null;
		this.phi = Double.NaN;
	}

	public Ellipse(double[] mu, double[] rad, double phi)
	{
		this.mu = mu;
		this.rad = rad;
		this.phi = phi;
	}

	public double[] mu()
	{
		return this.mu;
	}

	public double[] rad()
	{
		return this.rad;
	}

	public double phi()
	{
		return this.phi;
	}

	public String toStr()
	{
		return String.format("%g %g %g %g %g", this.mu[0], this.mu[1],
				this.rad[0], this.rad[1], this.phi);
	}

	public static EllipseFit ellipse_from_pca(Matrix pts, int confPerc)
	{
		double[] cent = Utils.centerOfMass(pts);

		SingularValueDecomposition svd =
				new SingularValueDecomposition(Utils.covarianceMatrix(pts, cent));

		Matrix cov = Utils.covarianceMatrix(pts, cent);
		//double covRat = Math.max(cov.get(1,1), cov.get(0,0)) /
		//		Math.min(cov.get(1,1), cov.get(0,0));

		Matrix U = svd.getU();
		double[] S = svd.getSingularValues();

		int first = 0;
		if (S[0] < S[1])
		{
			double tmp = S[0];
			S[0] = S[1];
			S[1] = tmp;
			first = 1;
		}

		if (U.get(0, first) < 0)
			U = U.uminus();
		double phi = Math.atan2(U.get(1, first), U.get(0, first));

		//double[][] rotPts = new double[pts.getRowDimension()][2];
		//for (int i = 0; i < pts.getRowDimension(); ++i)
		//	rotPts[i] = Utils.rot_point(new double[] {pts.get(i, 0), pts.get(i, 1)}, -phi);

		//Matrix rotCov = Utils.covarianceMatrix(new Matrix(rotPts), cent);
		//double covRatRot = rotCov.get(1,1) / rotCov.get(0,0);

		//chi-square distribution with dF = 2
		double chiSqTh = Double.NaN;
		if (confPerc == 99)
			chiSqTh = 9.210;
		else if (confPerc == 95)
			chiSqTh = 5.991;
		else if (confPerc == 90)
			chiSqTh = 4.605;
		else
			assert(false);

		//9.210 for 99% conf ellipse
		//5.991 for 95% conf ellipse
		//4.605 for 90% conf ellipse
//		Ellipse ell = new Ellipse(cent,
//								  new double[] {Math.sqrt(chiSqTh2 * S[0]), Math.sqrt(chiSqTh1 * S[1])},
//								  Math.atan2(U.get(1, first), U.get(0, first)));

		double a = Math.sqrt(chiSqTh * S[0]);
		double b = Math.sqrt(chiSqTh * S[1]);
		//double b = Math.sqrt(chiSqTh * S[0] * covRat);


		if (b > a)
		{
			double tmp = a;
			a = b;
			b = tmp;
			phi = (phi + (Math.PI / 2)) % Math.PI;
		}

		Ellipse ell = new Ellipse(cent, new double[] {a, b}, phi);
		return new EllipseFit(ell, new double[] {cov.get(0,0), cov.get(1,1)}, confPerc);
	}

	public double[][] covarianceInEllRot(final TrajectoryEnsemble trajs)
	{
		ArrayList<double[]> tmp = new ArrayList<> ();
		for (Trajectory tr: trajs.trajs())
			for (Point p: tr.points())
				if (this.inside(p.vec()))
					tmp.add(Utils.rot_point(p.vec(), this.phi));

		Matrix pts = new Matrix(tmp.toArray(new double[tmp.size()][2]));
		Matrix C = Utils.covarianceMatrix(pts, Utils.centerOfMass(pts));

		return new double[][] {new double[] {C.get(0, 0), C.get(0, 1)},
							   new double[] {C.get(1, 0), C.get(1, 1)}};
	}

	@Override
	public boolean inside(double[] p)
	{
		double cp = Math.cos(this.phi);
		double sp = Math.sin(this.phi);

		return Math.pow(cp * (p[0] - this.mu[0]) + sp * (p[1] - this.mu[1]), 2) / Math.pow(this.rad[0], 2) +
			   Math.pow(sp * (p[0] - this.mu[0]) - cp * (p[1] - this.mu[1]), 2) / Math.pow(this.rad[1], 2) <= 1;
	}

	public double[][] to_polygon(double eps)
	{
		double[][] res = new double[(int) Math.ceil(2*Math.PI/eps) + 1][2];

		int i = 0;
		for (double theta = 0; theta <= 2*Math.PI; theta += eps)
		{
			res[i][0] = this.mu[0] + this.rad[0] * Math.cos(theta) * Math.cos(this.phi) -
									 this.rad[1] * Math.sin(theta) * Math.sin(this.phi); 
			res[i][1] = this.mu[1] + this.rad[0] * Math.cos(theta) * Math.sin(this.phi) +
					 				 this.rad[1] * Math.sin(theta) * Math.cos(this.phi);
			++i;
		}

		//in order to close the polygon, the first and last points are the same
		res[i][0] = res[0][0];
		res[i][1] = res[0][1];

		return res;
	}

	public boolean intersect(final Shape s)
	{
		assert(s instanceof Ellipse);
		Ellipse e2 = (Ellipse) s;

		//As converting ellipses to polygon may be costly, do a quick test first
		double dist = Utils.squaredDist(this.mu, e2.mu());
		if (dist <= Math.pow(Math.min(this.rad[1], e2.rad[1]), 2))
			return true;
		//Pierre: That's a bit hacky but should be a good heuristic to speed up
		// overlap detection
		if (dist >= Math.pow(10 * Math.max(this.rad[0], e2.rad[0]), 2))
			return false;

		GeometryFactory geo_facto = new GeometryFactory();

		double[][] pe1 = this.to_polygon(0.05);
		double[][] pe2 = e2.to_polygon(0.05);

		/*
		 * Pierre: This seems overly complicated, there should be a better way.
		 * Like direct array copy or something.
		 * But this will do for the moment.
		 */
		CoordinateSequence cse1 = geo_facto.getCoordinateSequenceFactory().create(pe1.length, 2);
		for (int i = 0; i < pe1.length; ++i)
		{
			cse1.setOrdinate(i, 0, pe1[i][0]);
			cse1.setOrdinate(i, 1, pe1[i][1]);
		}
		CoordinateSequence cse2 = geo_facto.getCoordinateSequenceFactory().create(pe2.length, 2);
		for (int i = 0; i < pe2.length; ++i)
		{
			cse2.setOrdinate(i, 0, pe2[i][0]);
			cse2.setOrdinate(i, 1, pe2[i][1]);
		}

		Polygon p1 = geo_facto.createPolygon(cse1);
		Polygon p2 = geo_facto.createPolygon(cse2);
		return p1.intersects(p2) || p2.intersects(p1) || p2.contains(p1) || p1.contains(p2);
	}

	@Override
	public double[] minPt()
	{
		double[][] poly = this.to_polygon(0.2);
		
		if (poly.length == 0)
			return null;

		double[] minP = poly[0];
		for (int i = 1; i < poly.length; ++i)
		{
			minP[0] = poly[i][0] < minP[0] ? poly[i][0]: minP[0];
			minP[1] = poly[i][1] < minP[1] ? poly[i][1]: minP[1];
		}

		return minP;
	}

	@Override
	public double[] maxPt()
	{
		double[][] poly = this.to_polygon(0.2);

		if (poly.length == 0)
			return null;

		double[] maxP = poly[0];
		for (int i = 1; i < poly.length; ++i)
		{
			maxP[0] = poly[i][0] > maxP[0] ? poly[i][0]: maxP[0];
			maxP[1] = poly[i][1] > maxP[1] ? poly[i][1]: maxP[1];
		}

		return maxP;
	}

	private static double P(double r0, double theta0, double a, double b,
						double phi, double theta)
	{
		return r0 * ((b*b - a*a) * Math.cos(theta + theta0 - 2*phi) + 
				(a*a + b*b) * Math.cos(theta - theta0));
	}

	private static double R(double r0, double theta0, double a, double b,
						double phi, double theta)
	{
		return (b*b - a*a) * Math.cos(2*theta - 2*phi) + a*a + b*b;
	}

	private static double Q(double r0, double theta0, double a, double b,
						double phi, double theta)
	{
		return Math.sqrt(2) * a * b * Math.sqrt(R(r0, theta0, a, b, phi, theta) -
				2 * r0*r0 * Math.pow(Math.sin(theta - theta0), 2));
	}

	public double[] centerToCenterIntersect(final Ellipse e2)
	{
		double[] er = new double[] {0, Math.atan2(this.mu[1], this.mu[0])};

		double norm = Math.sqrt(Math.pow(e2.mu()[0] - this.mu[0] , 2) + Math.pow(e2.mu()[1] - this.mu[1] , 2));
		double[] N = new double[] {(e2.mu()[0] - this.mu[0]) / norm, (e2.mu()[1] - this.mu[1]) / norm};

		double theta = Math.atan2(N[1], N[0]) % (2*Math.PI);

		double re = (P(er[0], er[1], this.rad[0], this.rad[1], this.phi, theta) + Q(er[0], er[1], this.rad[0], this.rad[1], this.phi, theta)) /
				R(er[0], er[1], this.rad[0], this.rad[1], this.phi, theta);

		return new double[] {this.mu[0] + re * Math.cos(theta), this.mu[1] + re * Math.sin(theta)};
	}

	@Override
	public double[] center()
	{
		return this.mu;
	}

	@Override
	public double area()
	{
		return Math.PI * this.rad[0] * this.rad[1];
	}
}
