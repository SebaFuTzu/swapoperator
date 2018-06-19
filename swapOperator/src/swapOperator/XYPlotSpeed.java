package swapOperator;

import java.io.File;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class XYPlotSpeed {// extends ApplicationFrame {

	/**
	 * A demonstration application showing an XY series containing a null value.
	 *
	 * @param title
	 *            the frame title.
	 */
	
	public  XYPlotSpeed ( ) { 
		
	}
	
	
	
	public XYPlotSpeed(final String title, final String leyenda, ArrayList<Double> costos) {

		//super(title);
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
		//setContentPane(chartPanel);
	}
	
	public void Plot(final String title, final String leyenda, ArrayList<Double> costos, double bkv, String fileName)  {

		//super(title);
		final XYSeries series = new XYSeries(leyenda);
		double temp=0;
		for(int i=0;i<costos.size();i++) {
			temp = (costos.get(i)-bkv)/bkv;
			series.add(i, temp);
		}
		// Create chart
		final XYSeriesCollection data = new XYSeriesCollection(series);
		final JFreeChart chart = ChartFactory.createXYLineChart(title, "Iteraciones", "Cercania al optimo", data,
				PlotOrientation.VERTICAL, true, true, false);
		//deshabilitado mostrar el grafico en ventana
		//final ChartPanel chartPanel = new ChartPanel(chart);
		//chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		//setContentPane(chartPanel);
		
		// Draw png
		try {
			//File archivo = new File(title+".png");
			ChartUtilities.saveChartAsPNG(new File("img/"+fileName+".png"), chart, 1000, 540);
			//System.out.println("Guardado en:"+ archivo.getAbsolutePath());
		} catch (Exception e) {
			System.out.println("error guardar chart");
	    } finally {
	        //..
	    }
		
		//ChartUtilities.saveChartAsJPEG("outputFile", chartPanel, 1000, 400);
	}
	
	public XYPlotSpeed(final String title, final String leyenda1, String leyenda2, ArrayList<Costos> costosSA) {

		//super(title);
		final XYSeries series1 = new XYSeries(leyenda1);
		final XYSeries series2 = new XYSeries(leyenda2);
		for(int i=0;i<costosSA.size();i++) {
			series1.add(i, costosSA.get(i).getCostoActualSolucion());
			series2.add(i, costosSA.get(i).getCostoMejorSolucion());
		}
		final XYSeriesCollection data = new XYSeriesCollection();
		data.addSeries(series1);
		data.addSeries(series2);
		final JFreeChart chart = ChartFactory.createXYLineChart(title, "X", "Y", data,
				PlotOrientation.VERTICAL, true, true, false);

		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		//setContentPane(chartPanel);
	}
	
	public XYPlotSpeed(final String title, final String leyenda1, String leyenda2, ArrayList<Costos> costosSA, double bkv, String fileName) {

		//super(title);
		final XYSeries series1 = new XYSeries(leyenda1);
		final XYSeries series2 = new XYSeries(leyenda2);
		for(int i=0;i<costosSA.size();i++) {
			series1.add(i, (costosSA.get(i).getCostoActualSolucion()-bkv)/bkv);
			series2.add(i, (costosSA.get(i).getCostoMejorSolucion()-bkv)/bkv);
		}
		final XYSeriesCollection data = new XYSeriesCollection();
		data.addSeries(series1);
		data.addSeries(series2);
		final JFreeChart chart = ChartFactory.createXYLineChart(title, "X", "Y", data,
				PlotOrientation.VERTICAL, true, true, false);

		//final ChartPanel chartPanel = new ChartPanel(chart);
		//chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		try {
			//File archivo = new File(title+".png");
			ChartUtilities.saveChartAsPNG(new File("img/"+fileName+".png"), chart, 1000, 540);
			//System.out.println("Guardado en:"+ archivo.getAbsolutePath());
		} catch (Exception e) {
			System.out.println("error guardar chart");
	    } finally {
	        //..
	    }
		//setContentPane(chartPanel);
	}
}
