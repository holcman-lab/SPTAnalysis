package fiji.plugin.SPTAnalysis.wellDetection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import fiji.plugin.SPTAnalysis.Utils;
import fiji.plugin.SPTAnalysis.estimators.WellEstimatorParameters;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;

@XmlRootElement(name = "HybridWellDetectionMultiscale")
@XmlAccessorType(XmlAccessType.FIELD)
public class HybridWellDetectionMultiscale extends HybridWellDetection
{
	public static final String name = "HybridMultiscale";

	@XmlRootElement(name = "HybridWellDetectionMultiscaleParameters")
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(namespace="HybridWellDetectionMultiscale")
	public static class Parameters extends HybridWellDetection.Parameters
	{
		public final double dxMin;
		public final double dxMax;
		public final double dxStep;

		public Parameters()
		{
			super();
			this.dxMin = Double.NaN;
			this.dxMax = Double.NaN;
			this.dxStep = Double.NaN;
		}

		public Parameters(String expName, double dxSeeds, int filtSeeds, double dxMin, double dxMax, double dxStep,
				double densityTh, int seedDist, double maxSize, int minPtsTh, int confEllPerc, final WellEstimatorParameters estPs,
				int bestItShift, final IterationChooser.Parameters itChooserPs)
		{
			super(expName, dxSeeds, filtSeeds, densityTh, seedDist, Double.NaN, maxSize, minPtsTh,
					confEllPerc, estPs, bestItShift, itChooserPs);
			this.dxMin = dxMin;
			this.dxMax = dxMax;
			this.dxStep = dxStep;
		}

		@Override
		public String algoName()
		{
			return HybridWellDetectionMultiscale.name;
		}

		@Override
		public String toString()
		{
			return "dxDens:" + Utils.rmTrail0(String.format("%g", this.dxSeeds)) +
				   "_filtSeeds:" + String.format("%d", this.filtSeeds) +
				   "_dxMin:" + Utils.rmTrail0(String.format("%g", this.dxMin)) +
				   "_dxMax:" + Utils.rmTrail0(String.format("%g", this.dxMax)) +
				   "_dxStep:" + Utils.rmTrail0(String.format("%g", this.dxStep)) +
				   "_densityTh:" + Utils.rmTrail0(String.format("%g", this.densityTh)) +
				   "_seedDist:" + String.format("%d", this.seedDist) +
				   "_maxSize:" + Utils.rmTrail0(String.format("%g", this.maxSize)) +
				   "_confEllPerc:" + String.format("%d", this.confEllPerc) +
				   "_minPtsTh:" + String.format("%d", this.minPtsTh) +
				   "_bestItShift:" + String.format("%d", this.bestItShift) +
				   "_" + this.estPs.toString() +
				   "_" + this.itChooserPs.toString();
		}
	}

	public HybridWellDetectionMultiscale(WellDetectionParameters params)
	{
		super(params);
	}

	@Override
	public String name()
	{
		return HybridWellDetectionMultiscale.name;
	}

	@Override
	public FitResultMultiscale fitWellFromSeed(TrajectoryEnsemble trajs, double[] seedPoint)
	{
		FitResultMultiscale res = new FitResultMultiscale();

		Parameters curPs = (HybridWellDetectionMultiscale.Parameters) this.ps;
		for (double dxCur = curPs.dxMin; dxCur <= curPs.dxMax; dxCur += curPs.dxStep)
			res.addFitResult(dxCur, WellFromSeedDx(trajs, seedPoint, dxCur));

		return res;
	}

	@Override
	public WellDetectionParameters getParameters()
	{
		return (HybridWellDetectionMultiscale.Parameters) this.ps;
	}
}