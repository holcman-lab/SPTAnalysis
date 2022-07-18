package fiji.plugin.SPTAnalysis.wellDetection;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import fiji.plugin.SPTAnalysis.Utils;
import fiji.plugin.SPTAnalysis.XMLAdapters;
import fiji.plugin.SPTAnalysis.estimators.GridEstimator;
import fiji.plugin.SPTAnalysis.estimators.GridEstimatorParameters;
import fiji.plugin.SPTAnalysis.estimators.MLEEstimatorParameters;
import fiji.plugin.SPTAnalysis.estimators.MaxLikelihoodEstimator;
import fiji.plugin.SPTAnalysis.estimators.PureDiffusionEstimatorMLE;
import fiji.plugin.SPTAnalysis.estimators.WellEstimator;
import fiji.plugin.SPTAnalysis.estimators.WellEstimatorFactory;
import fiji.plugin.SPTAnalysis.estimators.WellEstimatorParameters;
import fiji.plugin.SPTAnalysis.estimators.WellScore;
import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.Point;
import fiji.plugin.SPTAnalysis.struct.Trajectory;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;
import fiji.plugin.SPTAnalysis.struct.VectorMap;

@XmlRootElement(name = "IterationChooser")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlJavaTypeAdapter(XMLAdapters.IterationChooserAdapter.class)
public abstract class IterationChooser
{
	public static enum chooser {BestParabScore, BestMLEScore, BestMLEDelta, BestMLERatio, MaxDensPerc, MinDens};

	@XmlType(namespace="IterationChooser")
	@XmlRootElement(name = "IterationChooserParameters")
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlJavaTypeAdapter(XMLAdapters.IterationChooserParametersAdapter.class)
	public static abstract class Parameters
	{
		public final IterationChooser.chooser chooser;
		public final WellEstimatorParameters estPs;

		Parameters()
		{
			this.chooser = null;
			this.estPs = null;
		}

		Parameters(final IterationChooser.chooser chooser, final WellEstimatorParameters estPs)
		{
			this.chooser = chooser;
			this.estPs = estPs;
		}

		public String toString()
		{
			return "itChooser:" + this.chooser.name() +
				   "_" + this.estPs.toString();
		}

		public abstract Parameters cloneUpdate(WellEstimatorParameters newEstPs);
	}

	public final Parameters ps;
	@XmlTransient
	public final TrajectoryEnsemble trajs;

	public IterationChooser()
	{
		this.ps = null;
		this.trajs = null;
	}

	public IterationChooser(final Parameters ps, final TrajectoryEnsemble trajs)
	{
		this.ps = ps;
		this.trajs = trajs;
	}

	public abstract IterationChooser cloneUpdate(final Parameters ps);
	public abstract int bestIteration(final FitResult fitRes);

	public IterationChooser cloneUpdateEstParams(final WellEstimatorParameters estPs)
	{
		return this.cloneUpdate(this.ps.cloneUpdate(estPs));
	}

	public static IterationChooser get(final Parameters ps,
									   final TrajectoryEnsemble trajs)
	{
		if (ps.chooser == IterationChooser.chooser.BestParabScore)
		{
			assert(ps instanceof BestParabScore.Parameters);
			assert(ps.estPs instanceof GridEstimatorParameters);
			return new BestParabScore((BestParabScore.Parameters) ps, trajs);
		}
		else if (ps.chooser == IterationChooser.chooser.BestMLEScore ||
				 ps.chooser == IterationChooser.chooser.BestMLERatio ||
				 ps.chooser == IterationChooser.chooser.BestMLEDelta)
		{
			assert(ps.estPs instanceof MLEEstimatorParameters);

			if (ps.chooser == IterationChooser.chooser.BestMLEScore ||
				ps.chooser == IterationChooser.chooser.BestMLERatio)
			{
				assert(ps instanceof IterationChooser.MLE.Parameters);
				if (ps.chooser == IterationChooser.chooser.BestMLEScore)
					return new BestMLEScore((MLE.Parameters) ps, trajs);
				else
					return new BestMLERatioScore((MLE.Parameters) ps, trajs);
			}
			else if (ps.chooser == IterationChooser.chooser.BestMLEDelta)
			{
				assert(ps instanceof IterationChooser.BestMLEDeltaScore.Parameters);
				return new BestMLEDeltaScore((BestMLEDeltaScore.Parameters) ps, trajs);
			}
		}
		else if (ps.chooser == IterationChooser.chooser.MaxDensPerc)
		{
			assert(ps instanceof IterationChooser.MaxDensPerc.Parameters);
			return new MaxDensPerc((MaxDensPerc.Parameters) ps, trajs);
		}
		else if (ps.chooser == IterationChooser.chooser.MinDens)
		{
			assert(ps instanceof IterationChooser.MinDens.Parameters);
			return new MinDens((MinDens.Parameters) ps, trajs);
		}


		System.out.println(String.format("Wrong iteration chooser: %s", ps.chooser.name()));
		return null;
	}

	@XmlType(namespace="IterationChooser")
	@XmlRootElement(name = "IterationChooserBestParabScore")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class BestParabScore extends IterationChooser
	{
		@XmlType(namespace="IterationChooserBestParabScore")
		@XmlRootElement(name = "IterationChooserBestParabScoreParameters")
		@XmlAccessorType(XmlAccessType.FIELD)
		public static class Parameters extends IterationChooser.Parameters
		{
			public final double angSimTh;
			public final double sampledRatioTh;

			public Parameters()
			{
				super();
				this.angSimTh = Double.NaN;
				this.sampledRatioTh = Double.NaN;
			}

			public Parameters(final IterationChooser.chooser chooser,
					final WellEstimatorParameters estPs, double angSimTh, double sampledRatioTh)
			{
				super(chooser, estPs);
				this.angSimTh = angSimTh;
				this.sampledRatioTh = sampledRatioTh;
			}

			@Override
			public String toString()
			{
				return super.toString() +
					   "_angSim:" + Utils.rmTrail0(String.format("%g", this.angSimTh)) +
					   "_sampRat:" + Utils.rmTrail0(String.format("%g", this.sampledRatioTh));
			}

			@Override
			public Parameters cloneUpdate(WellEstimatorParameters newEstPs)
			{
				return new Parameters(this.chooser, newEstPs, this.angSimTh, this.sampledRatioTh);
			}
		}

		public BestParabScore()
		{
			super();
		}

		public BestParabScore(final Parameters ps, final TrajectoryEnsemble trajs)
		{
			super(ps, trajs);
		}

		@Override
		public int bestIteration(FitResult fitRes)
		{
			Parameters curPs = (Parameters) this.ps;
			GridEstimatorParameters gEstPs = (GridEstimatorParameters) this.ps.estPs;
			int bestIt = -1;
			WellScore bestScore = null;
			for (int k = 0; k < fitRes.numIt(); ++k)
			{
				if (fitRes.empty().get(k))
					continue;

				final Ellipse ell = fitRes.ells.get(k);
				WellEstimator west = (new WellEstimatorFactory(this.trajs, ell, gEstPs)).getEst();

				if (west == null)
					continue;

				final WellScore score = west.estimateScore();
				fitRes.setScore(k, score);

				GridEstimator est = (GridEstimator) west;

				VectorMap locDriftNorm = VectorMap.normalized_drift(est.drift());
				ArrayList<int[]> nh2 = Utils.filter_center(est.drift().grid(), est.nh());
				double angSim = locDriftNorm.angular_similarity(nh2, ell);

				double sq_area = est.nh().size() * Math.pow(gEstPs.dx, 2);

				if ((bestScore == null || score.betterThan(bestScore)) &&
					 angSim > curPs.angSimTh && sq_area / ell.area() > curPs.sampledRatioTh)
				{
					bestScore = score;
					bestIt = k;
				}
			}
			return bestIt;
		}

		@Override
		public IterationChooser cloneUpdate(IterationChooser.Parameters ps)
		{
			assert(ps instanceof Parameters);
			return new BestParabScore((Parameters) ps, this.trajs);
		}
	}

	public static abstract class MLE extends IterationChooser
	{
		@XmlType(namespace="IterationChooserMLE")
		@XmlRootElement(name = "IterationChooserMLEParameters")
		@XmlAccessorType(XmlAccessType.FIELD)
		public static class Parameters extends IterationChooser.Parameters
		{
			public Parameters()
			{
				super();
			}

			public Parameters(IterationChooser.chooser chooser,
					final WellEstimatorParameters estPs)
			{
				super(chooser, estPs);
			}

			@Override
			public Parameters cloneUpdate(WellEstimatorParameters newEstPs)
			{
				return new Parameters(this.chooser, newEstPs);
			}
		}

		@XmlTransient
		public final double dt;

		public MLE()
		{
			super();
			this.dt = Double.NaN;
		}

		public MLE(final Parameters ps, final TrajectoryEnsemble trajs)
		{
			super(ps, trajs);
			this.dt = trajs.acqDT();
		}
	}

	@XmlType(namespace="IterationChooser")
	@XmlRootElement(name = "IterationChooserBestMLEScore")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class BestMLEScore extends IterationChooser.MLE
	{
		public BestMLEScore()
		{
			super();
		}

		public BestMLEScore(final MLE.Parameters ps,
				final TrajectoryEnsemble trajs)
		{
			super(ps, trajs);
		}

		@Override
		public int bestIteration(FitResult fitRes)
		{
			int bestIt = -1;
			double bestMLE = 0.0;
			for (int k = 0; k < fitRes.numIt(); ++k)
			{
				if (fitRes.empty().get(k))
					continue;

				final Ellipse ell = fitRes.ells.get(k);
				final TrajectoryEnsemble curTrajs = Utils.trajsInShape(this.trajs, ell);
				final WellEstimator est = fitRes.ests().get(k);

				double curA = est.estimateA();
				double curD = est.estimateD();

				double curMLE = MaxLikelihoodEstimator.wellLogLikelihoodXY(curTrajs,
						ell.mu(), new double[] {2 * curA / Math.pow(ell.rad()[0], 2),
												2 * curA / Math.pow(ell.rad()[1], 2)},
						new double[] {curD, curD}, this.dt);
				//curMLE /= curTrajs.numDisps();
				fitRes.setScore(k, new WellScore.Likelihood(curMLE));

				if (curMLE > bestMLE)
				{
					bestIt = k;
					bestMLE = curMLE;
				}
			}

			return bestIt;
		}

		@Override
		public IterationChooser cloneUpdate(IterationChooser.Parameters ps)
		{
			assert(ps instanceof MLE.Parameters);
			return new BestMLEScore((MLE.Parameters) ps, this.trajs);
		}
	}

	@XmlType(namespace="IterationChooser")
	@XmlRootElement(name = "IterationChooserBestMLERatioScore")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class BestMLERatioScore extends IterationChooser.MLE
	{
		public BestMLERatioScore(final MLE.Parameters ps,
				final TrajectoryEnsemble trajs)
		{
			super(ps, trajs);
		}

		@Override
		public int bestIteration(FitResult fitRes)
		{
			int bestIt = -1;
			double bestRatio = Double.NaN;
			for (int k = 0; k < fitRes.numIt(); ++k)
			{
				if (fitRes.empty().get(k))
					continue;

				final Ellipse ell = fitRes.ells.get(k);
				final TrajectoryEnsemble curTrajs = Utils.trajsInShape(trajs, ell);

				final WellEstimator est = fitRes.ests().get(k);

				double curA = est.estimateA();
				double curD = est.estimateD();

				double curWellMLE = MaxLikelihoodEstimator.wellLogLikelihoodXY(curTrajs,
						ell.mu(), new double[] {2 * curA / Math.pow(ell.rad()[0], 2),
												2 * curA / Math.pow(ell.rad()[1], 2)},
						new double[] {curD, curD}, this.dt);

				double pureDiffD = PureDiffusionEstimatorMLE.estimateD(curTrajs);
				double pureDiffMLE = PureDiffusionEstimatorMLE.logLikelihood(curTrajs, pureDiffD);

				double curRatio = curWellMLE / pureDiffMLE;
				fitRes.setScore(k, new WellScore.Likelihood(curRatio));

				if (Double.isNaN(bestRatio) || curRatio > bestRatio)
				{
					bestIt = k;
					bestRatio = curRatio;
				}
			}

			return bestIt;
		}

		@Override
		public IterationChooser cloneUpdate(IterationChooser.Parameters ps)
		{
			assert(ps instanceof MLE.Parameters);
			return new BestMLERatioScore((MLE.Parameters) ps, this.trajs);
		}
	}

	@XmlType(namespace="IterationChooser")
	@XmlRootElement(name = "IterationChooserBestMLEDeltaScore")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class BestMLEDeltaScore extends IterationChooser.MLE
	{
		@XmlType(namespace="IterationChooserBestMLEDeltaScore")
		@XmlRootElement(name = "IterationChooserBestMLEDeltaScoreParameters")
		@XmlAccessorType(XmlAccessType.FIELD)
		public static class Parameters extends IterationChooser.MLE.Parameters
		{
			public final int minSlicePtsTh;

			public Parameters()
			{
				super();
				this.minSlicePtsTh = -1;
			}

			public Parameters(IterationChooser.chooser chooser,
					final WellEstimatorParameters estPs, int minSlicePtsTh)
			{
				super(chooser, estPs);
				this.minSlicePtsTh = minSlicePtsTh;
			}

			@Override
			public String toString()
			{
				return super.toString() +
					   "_minSlicePtsTh:" + String.format("%d", this.minSlicePtsTh);
			}
		}

		public BestMLEDeltaScore()
		{
			super();
		}

		public BestMLEDeltaScore(final Parameters ps, final TrajectoryEnsemble trajs)
		{
			super(ps, trajs);
		}

		@Override
		public int bestIteration(FitResult fitRes)
		{
			Parameters curPs = (Parameters) this.ps;

			ArrayList<Double> scores = new ArrayList<> ();
			for (int k = 1; k < fitRes.numIt(); ++k)
			{
				if (fitRes.empty().get(k) || fitRes.empty().get(k-1))
					continue;

				final Ellipse ellLarge = fitRes.ells.get(k);
				final Ellipse ellSmall = fitRes.ells.get(k-1);

				TrajectoryEnsemble curTrajs = new TrajectoryEnsemble();
				for (final Trajectory traj: trajs.trajs())
				{
					for (int i = 0; i < traj.points().size() - 1; ++i)
					{
						final Point p = traj.points().get(i);
						if (ellLarge.inside(p.vec()) && ! ellSmall.inside(p.vec()))
						{
							Trajectory tr = new Trajectory();
							tr.points().add(p);
							tr.points().add(traj.points().get(i+1));
							curTrajs.trajs().add(tr);
						}
					}
				}

				if (curTrajs.trajs().size() < curPs.minSlicePtsTh)
					continue;

				final WellEstimator est = fitRes.ests().get(k);
				double curA = est.estimateA();
				double curD = est.estimateD();

				double curMLE = MaxLikelihoodEstimator.wellLogLikelihoodXY(curTrajs,
						ellLarge.mu(), new double[] {2 * curA / Math.pow(ellLarge.rad()[0], 2),
													 2 * curA / Math.pow(ellLarge.rad()[1], 2)},
						new double[] {curD, curD}, this.dt);
				curMLE /= curTrajs.numDisps();

				//fitRes.setScore(k-1, new WellScore.Likelihood(curMLE));
				fitRes.setScore(k, fitRes.ests().get(k).estimateScore());
				scores.add(curMLE);
			}

			ArrayList<Integer> pks = Utils.findPeaks(scores);

			//return location of 1st peak
			if (pks.isEmpty())
				return -1;
			else
				return pks.get(0);
		}

		@Override
		public IterationChooser cloneUpdate(IterationChooser.Parameters ps)
		{
			assert(ps instanceof Parameters);
			return new BestMLEDeltaScore((Parameters) ps, this.trajs);
		}
	}

	@XmlType(namespace="IterationChooser")
	@XmlRootElement(name = "IterationChooserMaxDensPerc")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class MaxDensPerc extends IterationChooser
	{
		@XmlType(namespace="IterationChooserMaxDensPerc")
		@XmlRootElement(name = "IterationChooserMaxDensPercParameters")
		@XmlAccessorType(XmlAccessType.FIELD)
		public static class Parameters extends IterationChooser.Parameters
		{
			public final double maxDensPerc;

			public Parameters()
			{
				super();
				this.maxDensPerc = Double.NaN;
			}

			public Parameters(IterationChooser.chooser chooser,
					final WellEstimatorParameters estPs, double maxDensPerc)
			{
				super(chooser, estPs);
				this.maxDensPerc = maxDensPerc;
			}

			@Override
			public String toString()
			{
				return super.toString() +
					   "_maxDensPerc:" + Utils.rmTrail0(String.format("%g", this.maxDensPerc));
			}

			@Override
			public Parameters cloneUpdate(WellEstimatorParameters newEstPs)
			{
				return new Parameters(this.chooser, newEstPs, this.maxDensPerc);
			}
		}

		public MaxDensPerc()
		{
			super();
		}

		public MaxDensPerc(final IterationChooser.Parameters ps, final TrajectoryEnsemble trajs)
		{
			super(ps, trajs);
		}

		@Override
		public int bestIteration(FitResult fitRes)
		{
			assert(fitRes instanceof FitResultDensity);
			FitResultDensity fitResdens = (FitResultDensity) fitRes;
			Parameters curPs = (Parameters) this.ps;

			double maxDens = fitResdens.densScores().get(0).value();
			int k = 1;
			while (k < fitRes.numIt() &&
				   fitResdens.densScores().get(k).value() > curPs.maxDensPerc * maxDens)
				++k;

			return k - 1;
		}

		@Override
		public IterationChooser cloneUpdate(IterationChooser.Parameters ps)
		{
			assert(ps instanceof Parameters);
			return new MaxDensPerc((Parameters) ps, this.trajs);
		}
	}

	@XmlType(namespace="IterationChooser")
	@XmlRootElement(name = "IterationChooserMinDens")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class MinDens extends IterationChooser
	{
		@XmlType(namespace="IterationChooserMinDens")
		@XmlRootElement(name = "IterationChooserMinDensParameters")
		@XmlAccessorType(XmlAccessType.FIELD)
		public static class Parameters extends IterationChooser.Parameters
		{
			public Parameters()
			{
				super();
			}

			public Parameters(IterationChooser.chooser chooser,
					final WellEstimatorParameters estPs)
			{
				super(chooser, estPs);
			}

			@Override
			public String toString()
			{
				return super.toString();
			}

			@Override
			public Parameters cloneUpdate(WellEstimatorParameters newEstPs)
			{
				return new Parameters(this.chooser, newEstPs);
			}
		}

		public MinDens()
		{
			super();
		}

		public MinDens(final IterationChooser.Parameters ps, final TrajectoryEnsemble trajs)
		{
			super(ps, trajs);
		}

		@Override
		public int bestIteration(FitResult fitRes)
		{
			assert(fitRes instanceof FitResultDensity);
			FitResultDensity fitResdens = (FitResultDensity) fitRes;
			//Parameters curPs = (Parameters) this.ps;

//			System.out.println(String.format("num it = %d", fitRes.numIt()));
//			int minPos = 0;
//			for (int k = 1; k < fitRes.numIt(); ++k)
//			{
//				if (fitResdens.densScores().get(k).value() < fitResdens.densScores().get(minPos).value())
//					minPos = k;
//			}
//
//			return minPos;
			
			for (int k = 1; k < fitRes.numIt(); ++k)
			{
				if (fitResdens.densScores().get(k).value() > fitResdens.densScores().get(k-1).value())
					return k;
			}

			return fitRes.numIt() - 1;
		}

		@Override
		public IterationChooser cloneUpdate(IterationChooser.Parameters ps)
		{
			assert(ps instanceof Parameters);
			return new MinDens((Parameters) ps, this.trajs);
		}
	}

	/*
	public static class MaxScore extends IterationChooser
	{
		@Override
		public int bestIteration(FitResult fitRes)
		{
			WellScore bestScore = null;
			int bestIt = -1;
			for (int k = 0; k < fitRes.numIt(); ++k)
			{
				final WellScore score = fitRes.ests().get(k).estimateScore();
				if (bestScore == null || score.betterThan(bestScore))
				{
					bestScore = score;
					bestIt = k;
				}
			}
			return bestIt;
		}
	}

	public static class MaxDeltaScore extends IterationChooser
	{
		@Override
		public int bestIteration(FitResult fitRes)
		{
			double bestDelta = 0;
			int bestIt = -1;
			for (int k = 1; k < fitRes.numIt(); ++k)
			{
				final WellScore est1 = fitRes.ests().get(k).estimateScore();
				final WellScore est2 = fitRes.ests().get(k-1).estimateScore();

				double delta = est1.value() - est2.value();

				if (delta  > bestDelta)
				{
					bestDelta = delta;
					if (k < fitRes.numIt()-1 && !fitRes.empty().get(k+1))
						bestIt = k+1;
					else
						bestIt = k;
				}
			}
			return bestIt;
		}
	}
	*/
}
