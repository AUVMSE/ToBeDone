package org.vmse.spbau.tobedone.statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.vmse.spbau.tobedone.R;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Andy on 04.11.2015.
 */
public class TimeDifferentWeekDaysFragment extends ChartFragment {

    BarChart chart;
    private String[] dayNames;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.time_per_week_day, container, false);

        chart = (BarChart) v.findViewById(R.id.time_per_week_day_chart);
        chart.setDescription("");
        DateFormatSymbols symbols = new DateFormatSymbols();
        dayNames = symbols.getShortWeekdays();

        updateChart();
        return v;
    }

    @Override
    protected void updateChart() {
        if (null == chart)
            return;

        ArrayList<String> xVals = new ArrayList<String>(Arrays.asList(dayNames));
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < dayNames.length; i++) {
            float val = (float) (Math.random());
            yVals1.add(new BarEntry(val, i));
        }

        BarDataSet set1 = new BarDataSet(yVals1, "DataSet");
        set1.setBarSpacePercent(35f);

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);

        chart.setData(data);
        chart.invalidate();
    }
}
