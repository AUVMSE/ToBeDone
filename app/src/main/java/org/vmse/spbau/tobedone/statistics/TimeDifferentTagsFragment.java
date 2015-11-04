package org.vmse.spbau.tobedone.statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.vmse.spbau.tobedone.R;

import java.util.ArrayList;

/**
 * Created by Andy on 04.11.2015.
 */
public class TimeDifferentTagsFragment extends ChartFragment {

    PieChart chart;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.time_per_dif_tags_layout, container, false);

        chart = (PieChart) v.findViewById(R.id.time_per_tag_chart);
        chart.setDescription("");
        updateChart();
        return v;
    }

    @Override
    protected void updateChart() {
        if (null == chart)
            return;

        ArrayList<Entry> valsComp1 = new ArrayList<Entry>();

        // Here we actually will iterate over our tasks
        // this is a dummy stub
        Entry c1e1 = new Entry(70.000f, 0); // 0 == quarter 1
        valsComp1.add(c1e1);
        Entry c1e2 = new Entry(30.000f, 1); // 1 == quarter 2 ...
        valsComp1.add(c1e2);

        PieDataSet setComp1 = new PieDataSet(valsComp1, "Your tags in %");
        setComp1.setColors(ColorTemplate.VORDIPLOM_COLORS);
        ArrayList<String> xVals = new ArrayList<>();
        xVals.add("tag1");
        xVals.add("tag2");

        PieData data = new PieData(xVals, setComp1);

        chart.setData(data);
        chart.invalidate();
    }
}
