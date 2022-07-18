package fiji.plugin.SPTAnalysis.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.scijava.widget.FileWidget;

import fiji.plugin.SPTAnalysis.DataController;
import fiji.plugin.SPTAnalysis.GUIController;
import fiji.plugin.SPTAnalysis.Utils;
import fiji.plugin.SPTAnalysis.struct.Rectangle;

public class HistogramPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private final GUIController gctrl;
	private final DataController dctrl;

	protected ArrayList<ArrayList<Double>> timedData;
	protected ArrayList<ArrayList<double[]>> timedPos;

	protected String xlabel;
	private final String unit;

	protected JFreeChart chart;
	protected XYPlot histPlot;
	protected ChartPanel chartPanel;

	protected int curTimeWindow;

	private final JButton optsBut;
	private final JButton saveBut;

	private final JFrame optsFrame;
	private final HistOptionsPanel optsPanel;

	protected double[] dat;

	public HistogramPanel(final GUIController gctrl, final DataController dctrl, ArrayList<ArrayList<Double>> timedData,
			ArrayList<ArrayList<double[]>> timedPos, int timeWindow, String xlabel, String unit)
	{
		this.gctrl = gctrl;
		this.dctrl = dctrl;
		this.timedData = timedData;
		this.timedPos = timedPos;
		this.xlabel = xlabel;
		this.unit = unit;
		this.chart = null;
		this.histPlot = null;
		this.chartPanel = null;
		this.curTimeWindow = timeWindow;

		this.optsFrame = new JFrame();
		this.optsPanel = new HistOptionsPanel(this);
		this.optsFrame.add(this.optsPanel);
		this.optsFrame.setTitle("Histogram Options");
		this.optsFrame.pack();
		this.optsFrame.setVisible(false);

		this.optsBut = new JButton("Options");
		this.optsBut.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				optsFrame.setVisible(!optsFrame.isVisible());
			}
		});

		this.saveBut = new JButton("Save as png");
		this.saveBut.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					File saveFile = gctrl.UIServ().chooseFile(null, FileWidget.SAVE_STYLE);
					if (saveFile != null)
						ChartUtils.saveChartAsPNG(saveFile, chart, 600, 600);
				}
				catch (IOException exc)
				{
					exc.printStackTrace();
				}
			}
		});

		this.dat = new double[timedData.get(timeWindow).size()];
		for (int i = 0; i < this.dat.length; ++i)
			this.dat[i] = timedData.get(timeWindow).get(i);

		this.initGui();
	}

	protected void initGui()
	{
		this.setLayout(new GridBagLayout());

		HistogramDataset dataset = new HistogramDataset();
		dataset.setType(HistogramType.RELATIVE_FREQUENCY);
		dataset.addSeries("no", this.dat, (int) Math.ceil(Math.sqrt(dat.length)));


		String title = String.format("AVG=%.4f±%.4f %s (n=%d)", Utils.arrayAVG(this.dat),
				Utils.arraySD(this.dat), this.unit, this.dat.length);
		this.chart = ChartFactory.createHistogram(title, String.format("%s (%s)", this.xlabel, this.unit),
				"Frequency", dataset, PlotOrientation.VERTICAL, false, false, false);

		this.histPlot = this.chart.getXYPlot();
		final XYBarRenderer renderer = (XYBarRenderer) this.histPlot.getRenderer();
		renderer.setShadowVisible(false);
		renderer.setMargin(0);
		renderer.setBarPainter(new StandardXYBarPainter());
		renderer.setDrawBarOutline(true);
		renderer.setSeriesOutlinePaint(0, Color.BLACK);
		renderer.setSeriesPaint(0, new Color(100, 100, 100, 0));

		this.histPlot.setBackgroundPaint(new Color(1, 1, 1, 0 ));
		this.histPlot.setOutlineVisible(false);
		this.histPlot.setDomainCrosshairVisible(false);
		this.histPlot.setDomainGridlinesVisible(false);
		this.histPlot.setRangeCrosshairVisible(false);
		this.histPlot.setRangeGridlinesVisible(false);

		this.chartPanel = new ChartPanel(this.chart);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(10, 10, 0, 0);
		this.add(this.chartPanel, c);

		c.gridy = 1;
		c.gridwidth = 1;
		c.insets = new Insets(10, 10, 10, 0);
		c.gridx = 0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		this.add(this.optsBut, c);

		c.gridx = 1;
		this.add(this.saveBut, c);
	}

	protected void updateHist(final HistOptionsPanel.HistOptions opts)
	{
		Rectangle selection = this.dctrl.default_selection();
		if (opts.restrictToReg)
			selection = this.gctrl.selectedRegion();

		ArrayList<Double> tmp = new ArrayList<> ();
		if (opts.mergeWins)
		{
			for (int i = 0; i < this.timedData.size(); ++i)
				for (int j = 0; j < this.timedData.get(i).size(); ++j)
					if (selection.inside(this.timedPos.get(i).get(j)))
						tmp.add(this.timedData.get(i).get(j));
		}
		else
		{
			for (int i = 0; i < this.timedData.get(this.curTimeWindow).size(); ++i)
				if (selection.inside(this.timedPos.get(this.curTimeWindow).get(i)))
					tmp.add(this.timedData.get(this.curTimeWindow).get(i));
		}

		this.dat = new double[tmp.size()];
		for (int i = 0; i < tmp.size(); ++i)
			this.dat[i] = tmp.get(i);

		HistogramDataset dataset = new HistogramDataset();
		dataset.setType(HistogramType.RELATIVE_FREQUENCY);

		if (opts.autoBins)
			dataset.addSeries("no", this.dat, (int) Math.ceil(Math.sqrt(dat.length)));
		else if (opts.fixedBins)
			dataset.addSeries("no", this.dat, opts.nBins);
		else
		{
			dataset.addSeries("no", this.dat,
					(int) Math.ceil((opts.maxBin - opts.minBin) / opts.stepBin),
					opts.minBin, opts.maxBin);
		}
		String title = String.format("AVG=%.4f±%.4f (n=%d)", Utils.arrayAVG(this.dat),
				Utils.arraySD(this.dat), this.dat.length);
		this.chart.setTitle(title);
		this.histPlot.setDataset(dataset);
		this.chartPanel.repaint();
	}

	public void updateCurTimeWindow(int curTimeWindow)
	{
		this.curTimeWindow = curTimeWindow;
		this.updateHist(this.optsPanel.generateOpts());
	}
}
