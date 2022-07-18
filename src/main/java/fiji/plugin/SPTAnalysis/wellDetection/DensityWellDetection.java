package fiji.plugin.SPTAnalysis.wellDetection;

import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


import Jama.Matrix;
import Jama.SingularValueDecomposition;
import fiji.plugin.SPTAnalysis.Utils;
import fiji.plugin.SPTAnalysis.estimators.WellEstimator;
import fiji.plugin.SPTAnalysis.estimators.WellEstimatorFactory;
import fiji.plugin.SPTAnalysis.estimators.WellEstimatorParameters;
import fiji.plugin.SPTAnalysis.estimators.WellScore;
import fiji.plugin.SPTAnalysis.struct.PotWell;
import fiji.plugin.SPTAnalysis.struct.PotWells;
import fiji.plugin.SPTAnalysis.struct.Rectangle;
import fiji.plugin.SPTAnalysis.struct.Ring;
import fiji.plugin.SPTAnalysis.struct.ScalarMap;
import fiji.plugin.SPTAnalysis.struct.SquareGrid;
import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.MapParameters;
import fiji.plugin.SPTAnalysis.struct.PlugLogger;
import fiji.plugin.SPTAnalysis.struct.Point;
import fiji.plugin.SPTAnalysis.struct.Trajectory;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;

public class DensityWellDetection extends WellDetection
{
	public static final String name = "DensityMLE";

	@XmlRootElement(name = "DensityWellDetectionParameters")
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(namespace="DensityWellDetection")
	public static class Parameters extends WellDetectionParameters
	{
		public final double dx;
		public final double densityTh;
		public final int seedDist;
		public final double dr;
		public final double localGridDx;
		public final int localGridSize;
		public final double rMin;
		public final double rMax;
		public final double ratMaxDist;
		public final IterationChooser.Parameters itChooserPs;

		public Parameters(String expName, double dx, double densityTh, int seedDist, double localGridDx, int localGridSize,
						  double dr, double rMin, double rMax, double ratMaxDist,
						  final WellEstimatorParameters estPs, final IterationChooser.Parameters itChooserPs)
		{
			super(expName, estPs, 0);
			this.dx = dx;
			this.densityTh = densityTh;
			this.seedDist = seedDist;
			this.localGridDx = localGridDx;
			this.localGridSize = localGridSize;
			this.dr = dr;
			this.rMin = rMin;
			this.rMax = rMax;
			this.ratMaxDist = ratMaxDist;
			this.itChooserPs = itChooserPs;
		}

		@Override
		public String algoName()
		{
			return DensityWellDetection.name;
		}

		@Override
		public String toString()
		{
			return "dx:" + Utils.rmTrail0(String.format("%g", this.dx)) +
					   "_densityTh:" + Utils.rmTrail0(String.format("%g", this.densityTh)) +
					   "_seedDist:" + String.format("%d", this.seedDist) +
					   "_locGridDx:" + Utils.rmTrail0(String.format("%g", this.localGridDx)) +
					   "_locGridSize:" + Utils.rmTrail0(String.format("%d", this.localGridSize)) +
					   "_dr:" + Utils.rmTrail0(String.format("%g", this.dr)) +
					   "_rMin:" + Utils.rmTrail0(String.format("%g", this.rMin)) +
					   "_rMax:" + Utils.rmTrail0(String.format("%g", this.rMax)) +
					   "_ratMaxDist:" + Utils.rmTrail0(String.format("%g", this.ratMaxDist)) +
					   "_bestItShift:" + String.format("%d", this.bestItShift) +
					   "_" + this.estPs.toString() +
					   "_" + this.itChooserPs.toString();
		}

	}

	final DensityWellDetection.Parameters ps;

	public DensityWellDetection(final WellDetectionParameters ps)
	{
		this.ps = (DensityWellDetection.Parameters) ps;
	}

	public double[] RatioAndAngleFromCov(final Matrix cov)
	{
		SingularValueDecomposition svd = new SingularValueDecomposition(cov);

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

		return new double[] {S[1] / S[0],
							 Math.atan2(U.get(1, first), U.get(0, first))};
	}

	public FitResult fitWellFromSeed(TrajectoryEnsemble trajs, double[] muEst)
	{
		int gridSize = (int) Math.floor((this.ps.rMax - this.ps.rMin) / this.ps.dr) - 1;
		Ellipse[] circs = new Ellipse[gridSize];
		for (int k = 0; k < gridSize; ++k)
			circs[k] = new Ellipse(muEst,
								   new double[] {this.ps.rMin + this.ps.dr * (k+1),
										   		 this.ps.rMin + this.ps.dr * (k+1)}, 0);


		int maxRatIt = -1;
		double maxRat = Double.NaN;
		double maxAng = Double.NaN;
		double[] covRatios = new double[gridSize];
		for (int k = 0; k < gridSize; ++k)
		{
			ArrayList<double[]> pts = Utils.pointsInReg(trajs, circs[k]);

			if (pts.size() < 5)
			{
				covRatios[k] = Double.NaN;
				continue;
			}


			Matrix cov = Utils.covarianceMatrix(new Matrix(pts.toArray(new double[pts.size()][2])), muEst);
			//covRatios[k] = Math.max(cov.get(0, 0), cov.get(1, 1)) / Math.min(cov.get(0, 0), cov.get(1, 1));
			double[] tmp = this.RatioAndAngleFromCov(cov);
			covRatios[k] = tmp[0];

			if (maxRatIt == -1 ||
				(this.ps.rMin + this.ps.dr * k + this.ps.dr / 2) < this.ps.ratMaxDist && covRatios[k] > maxRat)
			{
				maxRatIt = k;
				maxRat = covRatios[k];
				//maxAng = this.RatioAndAngleFromCov(cov);
				maxAng = tmp[1];
			}
		}

		if (maxRatIt == -1)
			return null;

		Ring[] rings = new Ring[gridSize];
		for (int k = 0; k < gridSize; ++k)
			rings[k] = new Ring(muEst,
								new double[] {this.ps.rMin + this.ps.dr * k,
											  this.ps.rMin + this.ps.dr * (k+1)});

		double[] densRingRat = new double[gridSize];
		for (final Trajectory tr: trajs.trajs())
		{
			for (final Point pt: tr.points())
			{
				for (int k = 0; k < gridSize; ++k)
				{
					if (rings[k].insideRatio(pt.vec(), maxRat))
					{
						++densRingRat[k];
						break;
					}
				}
			}
		}
		for (int k = 0; k < gridSize; ++k)
			densRingRat[k] /= rings[k].area();

		FitResultDensity res = new FitResultDensity();
		for (int k = 0; k < gridSize; ++k)
		{
			double rest = rings[k].rads()[0] + rings[k].dr();
			Ellipse ell = new Ellipse(muEst, new double[] {rest, maxRat * rest}, maxAng);
			TrajectoryEnsemble trajs_in = Utils.trajsInShape(trajs, ell);

			WellEstimator est = new WellEstimatorFactory(trajs, ell, this.ps.estPs).getEst();
			if (est == null)
			{
				res.addEmptyIteration();
				continue;
			}

			res.addIteration(ell, trajs_in.get_ids(), null, est, new WellScore.Density(densRingRat[k]));
		}

		return res;
	}

	public ArrayList<double[]> computeSeedsCenter(TrajectoryEnsemble trajs)
	{
		SquareGrid grid = new SquareGrid(trajs, this.ps.dx);
		ScalarMap dens = ScalarMap.genDensityMap(grid, trajs,
				new MapParameters.DensityParameters(grid.dx(), ScalarMap.DensityOption.DENS, 0));

		ArrayList<int[]> seeds = Utils.highest_density_cells(dens, this.ps.densityTh);
		seeds = Utils.remove_closeby_cells(dens, seeds, this.ps.seedDist);

		ArrayList<double[]> res = new ArrayList<> ();
		for (int[] seed: seeds)
			res.add(grid.get(seed[0],  seed[1]));

		return res;
	}

	protected double[] computeCenterAlphas(ArrayList<double[]> points, final SquareGrid grid,
										   final ScalarMap dens, final double[] alphas)
	{
		double maxVal = dens.max();

		ArrayList<double[]> cents = new ArrayList<> ();
		for (int k = 0; k < alphas.length; ++k)
		{
			ArrayList<int[]> squaresToKeep = new ArrayList<> ();
			Iterator<double[]> it = dens.iterator();

			while (it.hasNext())
			{
				double[] vals = it.next();
				if (vals[2] > alphas[k] * maxVal)
					squaresToKeep.add(new int[] {(int) vals[0], (int) vals[1]});
			}

			ArrayList<double[]> pointsToKeep = new ArrayList<> ();
			for (final int[] sq: squaresToKeep)
			{
				double[] c = grid.get(sq[0], sq[1]);
				Rectangle reg = new Rectangle(new double[] {c[0] - grid.dx() / 2, c[1] - grid.dx() / 2},
											  new double[] {c[0] + grid.dx() / 2, c[1] + grid.dx() / 2});
				pointsToKeep.addAll(Utils.pointsInReg(points, reg));
			}

			cents.add(Utils.centerOfMass(pointsToKeep));
		}

		return Utils.centerOfMass(cents);
	}

	@Override
	public PotWells detectWells(TrajectoryEnsemble trajs, PlugLogger log)
	{
		double[] alphas = new double[] {0, 0.05, 0.1, 0.15, 0.2, 0.25, 0.3,
				0.35, 0.4, 0.45, 0.5};

		ArrayList<double[]> seedsPt = this.computeSeedsCenter(trajs);

		IterationChooser itChoose = IterationChooser.get(this.ps.itChooserPs, trajs);
		assert(itChoose != null);

		ArrayList<PotWell> detectedWells = new ArrayList<PotWell> ();
		for (double[] seedPt: seedsPt)
		{
			if (log != null)
				log.update(1.0 / seedsPt.size());
			SquareGrid locGrid = new SquareGrid(this.ps.localGridDx, seedPt,
												this.ps.localGridSize);
			ScalarMap locDens = ScalarMap.genDensityMap(locGrid, trajs,
					new MapParameters.DensityParameters(locGrid.dx(), ScalarMap.DensityOption.NPTS, 0));

			ArrayList<double[]> locPoints = Utils.pointsInReg(trajs, locGrid.boundary());

			double[] muEst = this.computeCenterAlphas(locPoints, locGrid, locDens, alphas);

			FitResult fitRes = this.fitWellFromSeed(trajs, muEst);
			if (fitRes != null)
			{
				fitRes.setBestIt(itChoose);

				PotWell tmpWell = fitRes.bestWell(trajs, ps);
				if (tmpWell != null)
					detectedWells.add(tmpWell);
			}
		}

		ArrayList<Integer> toKeep = Utils.remove_overlapping_wells(detectedWells);
		ArrayList<PotWell> mergedWells = new ArrayList<> ();
		this.lastFitResults = new ArrayList<> ();
		for (int k: toKeep)
		{
			mergedWells.add(detectedWells.get(k));
			this.lastFitResults.add(detectedWells.get(k).fitResult());
		}

		return new PotWells(mergedWells);
	}

	@Override
	public WellDetectionParameters getParameters()
	{
		return this.ps;
	}

	@Override
	public String name()
	{
		return DensityWellDetection.name;
	}

}
