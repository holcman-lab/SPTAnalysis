from fiji.plugin.SPTAnalysis.readers import TrajectoryCSVReader, CSVReaderOptions
from fiji.plugin.SPTAnalysis.graphConstruction import GraphConstructionDBScan, GraphConstructionDBScanRecursive, GraphConstructionDBScanParameters, GraphConstructionDBScanRecursiveParameters, GraphConstructionParameters
from fiji.plugin.SPTAnalysis.struct import GraphWindows, TimeWindows, TrajectoryEnsembleWindows
from fiji.plugin.SPTAnalysis import Utils, DataController

from datetime import datetime
import os
from os import path

base_dir = "/tmp/parutto_CRM22_data/raw/ER/graphs/dATL/2calrER_COSdATL_3dg_nan3_3h_r/subbckg_size:100_slidingparab_enhcont_satpxs:0.3_norm_spots_r:0.45_th:6500_tracks_dist:1_distgap:1_framegap:2"

outdir = "/tmp/graphs_CRM22/"

if not path.isdir(outdir):
	os.makedirs(outdir)

ps = {"v_th": 5.0, "maxClustNpts": 100,
     "Rmax": 0.21, "Rstep": 0.01 , "Rmin": 0.16,
     "Nmin": 15, "Nstep": 5, "Nmax": 75,
     "A_min": 0, "A_max": 999999999,
     "ell_eps": 0.1,
     "node_type": GraphConstructionParameters.NodeType.POLY}


		
infile = path.join(base_dir, "Spots in tracks statistics.csv")
treader = TrajectoryCSVReader(infile, CSVReaderOptions.trackmateOptions())
trajs = treader.read()

g_ps = GraphConstructionDBScanRecursiveParameters("", ps["v_th"], ps["maxClustNpts"],
													  ps["Rmax"], ps["Rstep"], ps["Rmin"],
												 	  ps["Nmin"], ps["Nstep"], ps["Nmax"],
											 		  ps["A_min"], ps["A_max"], ps["ell_eps"],
											 		  ps["node_type"])
g_algo = GraphConstructionDBScanRecursive(g_ps)
g = g_algo.constructGraph(trajs, None)
g.toFile(path.join(outdir, "network_recon.csv"))

gw = GraphWindows()
gw.wins.append(g)
dcntrl = DataController()
twins = TimeWindows.singleWindow(trajs)
trajsw = TrajectoryEnsembleWindows(trajs, twins)
dcntrl.attachTrajs("", trajsw, twins)
#dcntrl.addGraph(g_ps, gw)
dcntrl.marshall(path.join(outdir, "plugin_save_notw.xml"))


print("DONE")