package swapOperator;

import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class XYPlot extends ApplicationFrame {

	/**
	 * A demonstration application showing an XY series containing a null value.
	 *
	 * @param title
	 *            the frame title.
	 */
	public XYPlot(final String title, final String leyenda, ArrayList<Double> costos) {

		super(title);
		final XYSeries series = new XYSeries(leyenda);
		for(int i=0;i<costos.size();i++) {
			series.add(i, costos.get(i));
		}
		/*series.add(1.0, 500.2);
		series.add(5.0, 694.1);
		series.add(4.0, 100.0);
		series.add(12.5, 734.4);
		series.add(17.3, 453.2);
		series.add(21.2, 500.2);
		series.add(21.9, null);
		series.add(25.6, 734.4);
		series.add(30.0, 453.2);*/
		final XYSeriesCollection data = new XYSeriesCollection(series);
		final JFreeChart chart = ChartFactory.createXYLineChart(title, "X", "Y", data,
				PlotOrientation.VERTICAL, true, true, false);

		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);
	}
	
	public XYPlot(final String title, final String leyenda1, String leyenda2, ArrayList<CostosSA> costosSA) {

		super(title);
		final XYSeries series1 = new XYSeries(leyenda1);
		final XYSeries series2 = new XYSeries(leyenda2);
		for(int i=0;i<costosSA.size();i++) {
			series1.add(i, costosSA.get(i).getCostoAnteriorSolucion());
			series2.add(i, costosSA.get(i).getCostoMejorSolucion());
		}
		final XYSeriesCollection data = new XYSeriesCollection();
		//data.addSeries(series1);
		data.addSeries(series2);
		final JFreeChart chart = ChartFactory.createXYLineChart(title, "X", "Y", data,
				PlotOrientation.VERTICAL, true, true, false);

		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);
	}
}
