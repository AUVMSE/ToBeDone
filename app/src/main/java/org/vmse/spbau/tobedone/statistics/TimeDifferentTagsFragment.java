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

import org.vmse.spbau.tobedone.MainApplication;
import org.vmse.spbau.tobedone.R;
import org.vmse.spbau.tobedone.connection.model.TaskEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

        HashMap<String, Float> timePerTag = new HashMap<>();

        List<TaskEntity> l = MainApplication.getTaskDataWrapper().getTaskEntityData();
        for (TaskEntity ent : l) {
            List<String> tags = MainApplication.getTaskDataWrapper().getTagsForTaskCached(ent);
            if (null == tags)
                continue;
            for (String s : tags) {
                if (timePerTag.containsKey(s))
                    timePerTag.put(s, timePerTag.get(s) + ent.getElapsedTime());
                else
                    timePerTag.put(s, (float) ent.getElapsedTime());
            }
        }
        ArrayList<Entry> tagVals = new ArrayList<>();
        ArrayList<String> tagNames = new ArrayList<>();

        int idx = 0;
        for (String k : timePerTag.keySet()) {
            tagNames.add(k);
            Entry c1e = new Entry(timePerTag.get(k), idx++); // 1 == quarter 2 ...
            tagVals.add(c1e);
        }

        if (tagNames.size() > 0) {
            PieDataSet setComp1 = new PieDataSet(tagVals, "Your tags in %");
            setComp1.setColors(ColorTemplate.VORDIPLOM_COLORS);

            PieData data = new PieData(tagNames, setComp1);

            chart.setData(data);
            chart.invalidate();
        } else {
            chart.setNoDataTextDescription("There are no tasks with tags :(");
        }
    }
}
