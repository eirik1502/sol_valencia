package game.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eirik on 26.11.2018.
 */
public class StaticDataset implements Dataset{


    JFreeChart chart;
    DefaultXYDataset dataset;
    private String name;
//    private List<Float> xvalues = new ArrayList<>();
//    private List<Float> yvalues = new ArrayList<>();


    public StaticDataset(String seriesName) {
        this(seriesName, null);
    }
    public StaticDataset(String seriesName, List<Float> yvalues) {
        this.name = seriesName;
        dataset = new DefaultXYDataset();

        chart = ChartFactory.createXYLineChart(
                name,
                "X",
                "Y",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        if (yvalues != null)
            addSeries(seriesName, yvalues);
    }

    public void addIntSeries(String name, List<Integer> yvalues) {
        List<Float> yvalues_f = new ArrayList<>(yvalues.size());
        yvalues.forEach(v -> yvalues_f.add((float)v));
        addSeries(name, yvalues_f);
    }
    public void addSeries(String name, List<Float> yvalues) {
        double[] yvals = new double[yvalues.size()];
        for (int i = 0; i < yvals.length; i++) {
            yvals[i] = yvalues.get(i);
        }
        double[] xvals = new double[yvals.length];
        for (int i = 0; i < xvals.length; i++) {
            xvals[i] = i;
        }
        double[][] series = {xvals, yvals};

        dataset.addSeries(name, series);
    }

//    private XYDataset createDataset(String name, List<Float> yvalues) {
//        double[] yvals = new double[yvalues.size()];
//        for (int i = 0; i < yvals.length; i++) {
//            yvals[i] = yvalues.get(i);
//        }
//        double[] xvals = new double[yvals.length];
//        for (int i = 0; i < xvals.length; i++) {
//            xvals[i] = i;
//        }
//
//        double[][] vals = {xvals, yvals};
//
//        DefaultXYDataset dataset = new DefaultXYDataset();
//        dataset.addSeries(name, vals);
//        return dataset;
//    }

    public String getName() {
        return name;
    }

    public JFreeChart getChart() {
        return chart;
    }

}
