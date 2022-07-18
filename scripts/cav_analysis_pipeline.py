from fiji.plugin.SPTAnalysis.readers import TrajectoryCSVReader, CSVReaderOptions
from fiji.plugin.SPTAnalysis.wellDetection import HybridWellDetectionMultiscale, WellDetection, HybridWellDetection, IterationChooser
from fiji.plugin.SPTAnalysis.wellLinker import DistanceWellLinker
from fiji.plugin.SPTAnalysis.struct import TimeWindows, PotWells, ScalarMap, VectorMap, SquareGrid, TrajectoryEnsembleWindows, TrajectoriesColorSchemeWindows, TrajectoriesColorScheme 
from fiji.plugin.SPTAnalysis.writers import SVGTrajectoriesWriter, SVGWellsWriter, SVGWriter, SVGScaleBarWriter
from fiji.plugin.SPTAnalysis.writers import SVGScalarMapWriter, SVGVectorMapWriter
from fiji.plugin.SPTAnalysis.estimators import WellEstimator, MLEEstimatorParameters
from fiji.plugin.SPTAnalysis import DataController

from os import walk, path, makedirs

def find_categories(based, root):
	categories = []
	while root != "/" and root != based:
		tmp = path.split(root)
		categories = [tmp[1].replace(' ', '_')] + categories
		root = tmp[0]
	return categories

def microscope_params_from_file(d):
	dx = None
	dt = None
	fname = None
	if path.isfile(path.join(d, "PALMprofile-Tracking.log")):
		fname = path.join(d, "PALMprofile-Tracking.log")
	elif path.isfile(path.join(d, "PALMprofile.log")):
		fname = path.join(d, "PALMprofile.log")
	else:
		assert(False)

	with open(fname, 'r') as f:
		for line in f:
			if line.startswith("[Calibration_XY_Txt]"):
				dx = float(line.rstrip("\n").split(" ")[1])
			elif line.startswith("[Calibration_T_Txt]"):
				dt = float(line.rstrip("\n").split(" ")[1])
	return (dx,dt)

force = False
compute_maps = False

estParams = MLEEstimatorParameters(WellEstimator.type.MLE)
itChooseParams = IterationChooser.BestMLEDeltaScore.Parameters(IterationChooser.chooser.BestMLEDelta, estParams, 3)


wparams = {"endo": HybridWellDetectionMultiscale.Parameters("", 0.1, 1, 0.01, 0.025, 0.001, 10, 3, 0.3, 10, 95, estParams, 0, itChooseParams), #endo
           "47": HybridWellDetectionMultiscale.Parameters("", 0.1, 1, 0.01, 0.025, 0.001, 5, 3, 0.3, 20, 99, estParams, 0, itChooseParams)} #P/M 47

link_gap = 1
link_dist = 0.2

wdur = 20
wover = 0
dx_diff = 0.1
dx = 0.05


base_dir = "/tmp/parutto_CRM22_data/raw/cav"

out_dir = "/tmp/wells_CRM22/cav"

cpt = 0
for root, cdir, files in walk(base_dir):
	for fname in files:
		if fname == "trcPALMTracer.txt" or fname.endswith("_MIA-Filter.trc"):
			fpath = path.join(root, fname)
			cats = find_categories(base_dir, root)
			cpt += 1

			outd = out_dir
			for cat in cats:
				outd = path.join(outd, cat.replace(' ', '_'))
				if not path.isdir(outd):
					makedirs(outd)

			if path.isfile(path.join(outd, "plugin_save.xml")) and not force:
				print("[{}] Skipped: {}".format(cpt, outd))
				continue
			print("[{}] Processing: {}".format(cpt, outd))

			if "endogenous" in fpath:
				cur_wparams = wparams["endo"]
				print("Selecting endogenous parameters")
			else:
				cur_wparams = wparams["47"]
				print("Selecting P/M-47 parameters")

			out_exp_dir = path.join(outd, cur_wparams.toString())
			if (not path.isdir(out_exp_dir)):
				makedirs(out_exp_dir)

			dx,dt = microscope_params_from_file(root)
			csvo = CSVReaderOptions("\t", 0, 1, 2, 3, False, 0, 3, True, dx, True, dt)

			treader = TrajectoryCSVReader(fpath, csvo)
			trajs = treader.read()

			dcntrl = None
			trajsw = None
			if path.isfile(path.join(outd, "plugin_save.xml")):
				dcntrl = DataController.controllerFromFile(path.join(outd, "plugin_save.xml"))
				trajsw = dcntrl.trajs()
			else:
				dcntrl = DataController()
				twins = TimeWindows(trajs, wdur, wover)
				trajsw = TrajectoryEnsembleWindows(trajs, twins)
				dcntrl.attachTrajs("", trajsw, twins)

			if compute_maps:
				grid = SquareGrid(dx, trajs.min(), trajs.max())
				grid_diff = SquareGrid(dx_diff, trajs.min(), trajs.max())

				if not path.isfile(path.join(outd, "density.svg")):
					SVGWriter.saveSVG(path.join(outd, "density.svg"),
						[SVGScalarMapWriter(ScalarMap.gen_density_map(grid, trajs, ScalarMap.DensityOption.LOGDENS), 100)],
						trajs.min())
				if not path.isfile(path.join(outd, "diffusion.svg")):
					SVGWriter.saveSVG(path.join(outd, "diffusion.svg"),
						[SVGScalarMapWriter(ScalarMap.gen_diffusion_map(grid_diff, trajs, 20), 100)],
						trajs.min())
				if not path.isfile(path.join(outd, "drift.svg")):
					SVGWriter.saveSVG(path.join(outd, "drift.svg"),
						[SVGVectorMapWriter(VectorMap.gen_drift_map(grid, trajs, 10), dx/2, 100)],
						trajs.min())

				for i, w_trajs in enumerate(trajsw.wins):
					if not path.isfile(path.join(outd, "density_w={}.svg".format(i))):
						SVGWriter.saveSVG(path.join(outd, "density_w={}.svg".format(i)),
							[SVGScalarMapWriter(ScalarMap.gen_density_map(grid, w_trajs, ScalarMap.DensityOption.LOGDENS), 100)],
							w_trajs.min())
					if not path.isfile(path.join(outd, "diffusion_w={}.svg".format(i))):
						SVGWriter.saveSVG(path.join(outd, "diffusion_w={}.svg".format(i)),
							[SVGScalarMapWriter(ScalarMap.gen_diffusion_map(grid_diff, w_trajs, 20), 100)],
							w_trajs.min())
					if not path.isfile(path.join(outd, "drift_w={}.svg".format(i))):
						SVGWriter.saveSVG(path.join(outd, "drift_w={}.svg".format(i)),
							[SVGVectorMapWriter(VectorMap.gen_drift_map(grid, w_trajs, 10), dx/2, 100)],
							w_trajs.min())

			cur_wparams.expName(dcntrl.nextAvailableAnalysisName())

			wd = HybridWellDetectionMultiscale(cur_wparams)

			if dcntrl.wells().containsKey(cur_wparams.algoName()):
				print("Loading wells")
				for k in dcntrl.wells().get(cur_wparams.algoName()).keySet():
					wells = dcntrl.wells().get(cur_wparams.algoName()).get(k)
					break
			else:
				print("Detecting wells")
				wells = WellDetection.detectWellsTimeWindows(wd, trajsw, None)
				wells.linkWells(DistanceWellLinker(link_gap, link_dist))
				dcntrl.addWellDetection(cur_wparams, wells)
				dcntrl.incAvailableAnalysisName()

			dcntrl.marshall(path.join(outd, "plugin_save.xml"))

			wells.toFile(path.join(out_exp_dir, "wells.csv"), True, dcntrl.default_selection())

			trajs_cols = TrajectoriesColorScheme(trajs, TrajectoriesColorScheme.ColoringType.Random)
			trajs_w_cols = TrajectoriesColorSchemeWindows.createColorSchemes(trajsw, TrajectoriesColorScheme.ColoringType.Random)

			SVGWriter.saveSVG(path.join(out_exp_dir, "trajs_wells.svg"),
				[SVGTrajectoriesWriter(trajs, 100, 1, trajs_cols),
				 SVGWellsWriter(wells.flatten(), 100), SVGScaleBarWriter(100, 2, SVGWriter.blackColor)],
				 trajs.min())

			for i, w_trajs in enumerate(trajsw.wins):
				SVGWriter.saveSVG(path.join(out_exp_dir, "trajs_wells_win{}.svg".format(i)),
					[SVGTrajectoriesWriter(w_trajs, 100, 1, trajs_w_cols.wins.get(i)),
				 	SVGWellsWriter(wells.wins.get(i).toArray(), 100), SVGScaleBarWriter(100, 2, SVGWriter.blackColor)],
				 	w_trajs.min())

print("DONE")