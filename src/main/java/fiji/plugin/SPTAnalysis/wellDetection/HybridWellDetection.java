package fiji.plugin.SPTAnalysis.wellDetection;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import Jama.Matrix;
import fiji.plugin.SPTAnalysis.Utils;
import fiji.plugin.SPTAnalysis.estimators.GridEstimatorParameters;
import fiji.plugin.SPTAnalysis.estimators.WellEstimator;
import fiji.plugin.SPTAnalysis.estimators.WellEstimatorFactory;
import fiji.plugin.SPTAnalysis.estimators.WellEstimatorParameters;
import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.EllipseFit;
import fiji.plugin.SPTAnalysis.struct.MapParameters;
import fiji.plugin.SPTAnalysis.struct.PlugLogger;
import fiji.plugin.SPTAnalysis.struct.PotWell;
import fiji.plugin.SPTAnalysis.struct.PotWells;
import fiji.plugin.SPTAnalysis.struct.Rectangle;
import fiji.plugin.SPTAnalysis.struct.ScalarMap;
import fiji.plugin.SPTAnalysis.struct.SquareGrid;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;

public class HybridWellDetection extends WellDetection
{
	public static final String name = "Hybrid";

	@XmlRootElement(name = "HybridWellDetectionParameters")
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(namespace="HybridWellDetection")
	public static class Parameters extends WellDetectionParameters
	{
		public final double dxSeeds;
		public final int filtSeeds;
		public final double densityTh;
		public final int seedDist;
		public final double dx;
		public final double maxSize;
		public final int minPtsTh;
		public final int confEllPerc;
		public final IterationChooser.Parameters itChooserPs;

		public Parameters()
		{
			super();
			this.dxSeeds = Double.NaN;
			this.filtSeeds = 1;
			this.densityTh = Double.NaN;
			this.seedDist = -1;
			this.dx = Double.NaN;
			this.maxSize = Double.NaN;
			this.minPtsTh = -1;
			this.confEllPerc = -1;
			this.itChooserPs = null;
		}

		public Parameters(String expName, double dxSeeds, int filtSeeds, double densityTh, int seedDist, double dx,
				double maxSize, int minPtsTh, int confEllPerc, final WellEstimatorParameters estPs,
				int bestItShift, final IterationChooser.Parameters itChooserPs)
		{
			super(expName, estPs, bestItShift);
			this.dxSeeds = dxSeeds;
			this.filtSeeds = filtSeeds;
			this.densityTh = densityTh;
			this.seedDist = seedDist;
			this.dx = dx;
			this.maxSize = maxSize;
			this.minPtsTh = minPtsTh;
			this.confEllPerc = confEllPerc;
			this.itChooserPs = itChooserPs;
		}

		@Override
		public String algoName()
		{
			return HybridWellDetection.name;
		}

		@Override
		public String toString()
		{
			return "dxSeeds:" + Utils.rmTrail0(String.format("%g", this.dxSeeds)) +
				   "_filtSeeds:" + String.format("%d", this.filtSeeds) +
				   "_densityTh:" + Utils.rmTrail0(String.format("%g", this.densityTh)) +
				   "_seedDist:" + String.format("%d", this.seedDist) +
				   "_dx:" + Utils.rmTrail0(String.format("%g", this.dx)) +
				   "_maxSize:" + Utils.rmTrail0(String.format("%g", this.maxSize)) +
				   "_confEllPerc:" + String.format("%d", this.confEllPerc) +
				   "_minPtsTh:" + String.format("%d", this.minPtsTh) +
				   "_bestItShift:" + String.format("%d", this.bestItShift) +
				   "_" + this.estPs.toString() +
				   "_" + this.itChooserPs.toString();
		}
	}

	final protected Parameters ps;
	protected SquareGrid gDens;
	protected ScalarMap dens;

	public HybridWellDetection(WellDetectionParameters params)
	{
		this.ps = (HybridWellDetection.Parameters) params;
		this.dens = null;
	}

	@Override
	public String name()
	{
		return HybridWellDetection.name;
	}

	protected FitResult WellFromSeedDx(TrajectoryEnsemble trajs, double[] seedPoint, double dx)
	{
		double[] c = seedPoint;

		FitResult res = new FitResult();

		int maxIt = (int) Math.ceil(this.ps.maxSize / dx);

		int k = 1;
		while (k <= maxIt)
		{
			Rectangle rect = new Rectangle(c, (2 * k + 1) * dx);
			ArrayList<double[]> pts = Utils.pointsInReg(trajs, rect);
			if (pts.size() < this.ps.minPtsTh)
			{
				res.addEmptyIteration();
				++k;
				continue;
			}

			double[][] tmp = pts.toArray(new double[pts.size()][2]);
			EllipseFit ellFit = Ellipse.ellipse_from_pca(new Matrix(tmp), this.ps.confEllPerc);
			Ellipse ell = ellFit.ell();
			c = ell.mu();

			TrajectoryEnsemble trajs_in = Utils.trajsInShape(trajs, ell);

			//In the multiscale well detection
			WellEstimatorParameters estPs = this.ps.estPs;
			if (this.ps.estPs instanceof GridEstimatorParameters)
				estPs = ((GridEstimatorParameters) this.ps.estPs).copyChangeDx(dx);

			WellEstimator est = new WellEstimatorFactory(trajs, ell, estPs).getEst();
			if (est == null)
			{
				res.addEmptyIteration();
				++k;
				continue;
			}

			res.addIteration(ell, trajs_in.get_from_ids(), ellFit.S(), est);

			++k;
		}

		return res;
	}

	public FitResult fitWellFromSeed(TrajectoryEnsemble trajs, double[] seedPoint)
	{
		return WellFromSeedDx(trajs, seedPoint, this.ps.dx);
	}

	public ArrayList<double[]> computeSeedsCenter(TrajectoryEnsemble trajs)
	{
		this.gDens = new SquareGrid(trajs, this.ps.dxSeeds);
		this.dens = ScalarMap.genDensityMap(this.gDens, trajs,
				new MapParameters.DensityParameters(this.ps.dxSeeds, ScalarMap.DensityOption.DENS, this.ps.filtSeeds));

		ArrayList<int[]> seeds = Utils.highest_density_cells(this.dens, this.ps.densityTh);
		seeds = Utils.remove_closeby_cells(this.dens, seeds, this.ps.seedDist);

		ArrayList<double[]> res = new ArrayList<> ();
		for (int[] seed: seeds)
			res.add(this.gDens.get(seed[0],  seed[1]));

		return res;
	}

	@Override
	public PotWells detectWells(TrajectoryEnsemble trajs, PlugLogger log)
	{
		ArrayList<double[]> seedsPt = this.computeSeedsCenter(trajs);

		IterationChooser itChooser = IterationChooser.get(this.ps.itChooserPs, trajs);
		assert(itChooser != null);

		ArrayList<PotWell> detectedWells = new ArrayList<> ();
		for (double[] seedPt: seedsPt)
		{
			if (log != null)
				log.update(1.0 / seedsPt.size());
			FitResult fitRes = fitWellFromSeed(trajs, seedPt);

			if (fitRes != null)
			{
				fitRes.setBestIt(itChooser);

				PotWell tmpWell = fitRes.bestWell(trajs, ps);
				if (tmpWell != null && !Double.isNaN(tmpWell.A()) && !Double.isNaN(tmpWell.D())
						&& !Double.isNaN(tmpWell.score().value()))
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
		return (HybridWellDetection.Parameters) this.ps;
	}
}
