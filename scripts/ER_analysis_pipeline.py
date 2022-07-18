from fiji.plugin.SPTAnalysis.readers import TrajectoryCSVReader, CSVReaderOptions
from fiji.plugin.SPTAnalysis.wellDetection import HybridWellDetectionMultiscale, WellDetection, HybridWellDetection, IterationChooser
from fiji.plugin.SPTAnalysis.wellLinker import DistanceWellLinker
from fiji.plugin.SPTAnalysis.struct import TimeWindows, PotWells, ScalarMap, VectorMap, SquareGrid, TrajectoryEnsembleWindows
from fiji.plugin.SPTAnalysis.writers import SVGTrajectoriesWriter, SVGWellsWriter, SVGWriter, SVGScaleBarWriter
from fiji.plugin.SPTAnalysis.writers import SVGScalarMapWriter, SVGVectorMapWriter
from fiji.plugin.SPTAnalysis.estimators import WellEstimator, MLEEstimatorParameters
from fiji.plugin.SPTAnalysis import DataController



from os import walk, path, makedirs

def find_categories(based, root):
	categories = []
	while root != "/" and root != based:
		tmp = path.split(root)
		categories = [tmp[1]] + categories
		root = tmp[0]
	return categories

force = False

estParams = MLEEstimatorParameters(WellEstimator.type.MLE)
itChooseParams = IterationChooser.BestMLEDeltaScore.Parameters(IterationChooser.chooser.BestMLEDelta, estParams, 5)
wparams = HybridWellDetectionMultiscale.Parameters("", 0.05, 3, 0.02, 0.04, 0.002, 0.4, 3, 0.3, 20, 95, estParams, 0, itChooseParams)
wd = HybridWellDetectionMultiscale(wparams)

wdur = 20
wover = 0

link_gap = 1
link_dist = 0.2


base_dir = "/tmp/parutto_CRM22_data/raw/ER/wells"
out_dir = "/tmp/wells_CRM22/ER"

cpt = 0
for root, cdir, files in walk(base_dir):
	for fname in files:
		if fname == "Spots in tracks statistics.csv":
			fpath = path.join(root, fname)
			cats = find_categories(base_dir, root)
			cpt += 1

			outd = out_dir
			for cat in cats:
				outd = path.join(outd, cat)
				if not path.isdir(outd):
					makedirs(outd)

			if path.isfile(path.join(outd, "plugin_save.xml")) and not force:
				print("[{}] Skipped: {}".format(cpt, outd))
				continue
			print("[{}] Processing: {}".format(cpt, outd))

			out_exp_dir = path.join(outd, wparams.toString())
			if (not path.isdir(out_exp_dir)):
				makedirs(out_exp_dir)

			treader = TrajectoryCSVReader(fpath, CSVReaderOptions.trackmateOptions())
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
				dcntrl.attachTrajs(fpath, trajsw, twins)

			wparams.expName(dcntrl.nextAvailableAnalysisName())

			wd = HybridWellDetectionMultiscale(wparams)
			wells = WellDetection.detectWellsTimeWindows(wd, trajsw, None)
			wells.linkWells(DistanceWellLinker(link_gap, link_dist))
			dcntrl.addWellDetection(wparams, wells)
			dcntrl.incAvailableAnalysisName()


			dcntrl.marshall(path.join(outd, "plugin_save.xml"))
			wells.toFile(path.join(out_exp_dir, "wells.csv"), True, dcntrl.default_selection())
print("DONE")