package game.chart;

import org.jfree.chart.*;
import org.jfree.chart.ui.ApplicationFrame;

/**
 * Created by eirik on 26.11.2018.
 */
public class Plotter {

    private PlotFrame plotFrame;
    private float width;
    private float height;
    //private List<PlotFrame> plotFrames = new ArrayList<>();


    public Plotter(String title) {
        this(title, 500, 400);
    }
    public Plotter(String title, float width, float height) {
        plotFrame = new Plotter.PlotFrame(title);
        this.width = width;
        this.height = height;
    }

    public void addDataset(Dataset dataset) {

        final JFreeChart chart = dataset.getChart();
//        plot.getPlot().setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
//        NumberAxis domain = (NumberAxis) plot.getPlot().getDomainAxis();/*
//    domain.setRange(0.00, 30);*/
//
//        domain.setTickUnit(new NumberTickUnit(0.5));
//        domain.setVerticalTickLabels(true);

        //FreeChart chart = new JFreeChart(plot.getName(), plot.getPlot());


//        final ChartPanel chartPanel = new ChartPanel(chart);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension((int)width, (int)height));
        plotFrame.setContentPane(chartPanel);
    }

    public void create() {
        plotFrame.pack();
        //RefineryUtils.centerFrameOnScreen(demo);
        plotFrame.setVisible(true);
    }

    class PlotFrame extends ApplicationFrame {

        /*
         * @param title  the frame title.
         */
        public PlotFrame(final String title) {
            super(title);
        }
    }
}
