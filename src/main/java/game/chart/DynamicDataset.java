package game.chart;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.*;

/**
 * Created by eirik on 26.11.2018.
 */
public class DynamicDataset implements Dataset{

    private String name;
    private int itemCount;

    private Map<String, XYSeries> seriesByName = new HashMap<>(10);
    private XYSeriesCollection seriesCollection;
    private JFreeChart chart;

    float secondsPassed;

    public DynamicDataset(String seriesName, int itemCount) {
        this.name = seriesName;
        this.itemCount = itemCount;

        seriesCollection = new XYSeriesCollection();
        chart = ChartFactory.createXYLineChart(name, "frameTime", "timePerFrame", seriesCollection);
    }

    public void setSeries(String... seriesNames) {
        Arrays.stream(seriesNames).forEach(this::addSeries);
    }

    private void addSeries(String name) {
        XYSeries newSeries = new XYSeries(name);
        seriesByName.put(name, newSeries);
        seriesCollection.addSeries(newSeries);

        //newSeries.setMaximumItemCount(itemCount);

        for (int i = -itemCount; i < 0; i++) {
            newSeries.add(0.016f*i,0);
        }
    }

    public void timeInterval(float secondsPassed) {
        this.secondsPassed += secondsPassed;

        for (String seriesName : seriesByName.keySet()) {
            XYSeries series = seriesByName.get(seriesName);
            if (series.getItemCount() > 0)
                series.remove(0);
        }
//        System.out.println("element removed");
    }
    public void appendValue(String seriesName, float val) {
        XYSeries series = seriesByName.get(seriesName);

        series.add(secondsPassed, val);
//        if (series.getItemCount() > itemCount-1 && series.getItemCount() > 0) {
//            series.remove(1);
//        }
    }


    public String getName() {
        return name;
    }

    public JFreeChart getChart() {
        return chart;
    }

}
