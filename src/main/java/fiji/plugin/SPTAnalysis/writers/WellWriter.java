package fiji.plugin.SPTAnalysis.writers;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import fiji.plugin.SPTAnalysis.Utils;
import fiji.plugin.SPTAnalysis.estimators.GridEstimator;
import fiji.plugin.SPTAnalysis.struct.Ellipse;
import fiji.plugin.SPTAnalysis.struct.MapParameters;
import fiji.plugin.SPTAnalysis.struct.PotWell;
import fiji.plugin.SPTAnalysis.struct.Rectangle;
import fiji.plugin.SPTAnalysis.struct.ScalarMap;
import fiji.plugin.SPTAnalysis.struct.SquareGrid;
import fiji.plugin.SPTAnalysis.struct.TrajectoriesColorScheme;
import fiji.plugin.SPTAnalysis.struct.TrajectoryEnsemble;
import fiji.plugin.SPTAnalysis.struct.VectorMap;

public class WellWriter
{
	protected final PotWell well;
	protected final TrajectoryEnsemble trajs;
	protected final TrajectoriesColorScheme coloring;
	protected final double regExtent;
	protected final String baseFolder;

	public WellWriter(final PotWell well, final TrajectoryEnsemble trajs, final TrajectoriesColorScheme coloring, 
			double regExtent, final String baseFolder)
	{
		this.well = well;
		this.trajs = trajs;
		this.coloring = coloring;
		this.regExtent = regExtent;
		this.baseFolder = baseFolder;
	}

	public void generate() throws IOException
	{
		final Ellipse e = this.well.ell();

		double dx = Double.NaN;
		GridEstimator ge = null;
		if (this.well.fitResult().ests().get(this.well.fitResult().bestIt()) instanceof GridEstimator)
			ge = (GridEstimator) this.well.fitResult().ests().get(this.well.fitResult().bestIt());

		if (ge == null)
			dx = 0.1;
		else
			dx = ge.drift().grid().dx();

		final SquareGrid grid = new SquareGrid(dx, e);

		final Rectangle reg = new Rectangle(new double[] {e.mu()[0] - e.rad()[0] * this.regExtent,
														  e.mu()[1] - e.rad()[1] * this.regExtent},
											new double[] {e.mu()[0] + e.rad()[0] * this.regExtent,
														  e.mu()[1] + e.rad()[1] * this.regExtent});


		final TrajectoryEnsemble trajsInReg = Utils.trajsInShape(this.trajs, reg);
		final TrajectoryEnsemble trajsInEll = Utils.trajsInShape(this.trajs, this.well.ell());


		//TODO: find way to fix parameter
		final ScalarMap dens = ScalarMap.genDensityMap(grid, trajs,
				new MapParameters.DensityParameters(grid.dx(), ScalarMap.DensityOption.DENS, 0));
		final ScalarMap diff = ScalarMap.genDiffusionMap(grid, trajs,
				new MapParameters.DiffusionParameters(grid.dx(), 10, false, 0));
		final VectorMap drift = VectorMap.genDriftMap(grid, trajs,
					new MapParameters.DriftParameters(grid.dx(), 5, false, 0));

		BufferedWriter writer = new BufferedWriter(new FileWriter(baseFolder + String.format("/well.csv")));
		writer.write("mux,muy,a,b,phi,A,D,score\n");
		writer.write(String.format("%g,%g,%g,%g,%g,%g,%g,%g\n", e.mu()[0], e.mu()[1],
				e.rad()[0], e.rad()[1], e.phi(), this.well.A(), this.well.D(), this.well.score().value()));
		writer.close();

		ArrayList<int[]> nhs = Utils.squaresInReg(grid, reg);

		CSVWriter.saveCSV(baseFolder + String.format("/trajsInReg.csv"),
						  new CSVTrajectoriesWriter(trajsInReg));
		CSVWriter.saveCSV(baseFolder + String.format("/trajsInWell.csv"),
						  new CSVTrajectoriesWriter(trajsInEll));
		CSVWriter.saveCSV(baseFolder + String.format("/density.csv"),
						  new CSVScalarMapWriter(dens, nhs));
		CSVWriter.saveCSV(baseFolder + String.format("/diffusion.csv"),
						  new CSVScalarMapWriter(diff, nhs));
		CSVWriter.saveCSV(baseFolder + String.format("/drift.csv"),
						  new CSVVectorMapWriter(drift, nhs));

		double zf = 100;
		double[] minP = new double[] {grid.Xmin()[0], grid.Xmin()[1]};

		SVGWellsWriter ellWriter = new SVGWellsWriter(new PotWell[] {this.well}, zf);
		SVGCanvasWriter canvWriter = new SVGCanvasWriter(grid.Xmin(), grid.Xmax(), zf);

		SVGWriter.saveSVG(baseFolder + String.format("/trajectories.svg"),
				new SVGWriter[] {new SVGTrajectoriesWriter(trajsInReg, zf, 1, coloring),
								 ellWriter,
								 new SVGScaleBarWriter(zf, 1, Color.BLACK),
								 canvWriter}, minP);
		SVGWriter.saveSVG(baseFolder + String.format("/density.svg"),
				new SVGWriter[] {new SVGScalarMapWriter(dens, zf, nhs),
								 ellWriter,
								 new SVGScaleBarWriter(zf, 1, Color.BLACK),
								 canvWriter}, minP);
		SVGWriter.saveSVG(baseFolder + String.format("/diffusion.svg"),
				new SVGWriter[] {new SVGScalarMapWriter(diff, zf, nhs),
								 ellWriter,
								 new SVGScaleBarWriter(zf, 1, Color.BLACK),
								 canvWriter}, minP);
		SVGWriter.saveSVG(baseFolder + String.format("/drift.svg"),
				new SVGWriter[] {new SVGVectorMapWriter(drift, dx/8, zf, nhs),
								 ellWriter,
								 new SVGScaleBarWriter(zf, 1, Color.BLACK),
								 canvWriter}, minP);
	}
}
