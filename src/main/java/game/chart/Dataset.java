package game.chart;

import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;

/**
 * Created by eirik on 26.11.2018.
 */
public interface Dataset {
    String getName();
    JFreeChart getChart();
}
