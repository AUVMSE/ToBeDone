package org.vmse.spbau.tobedone.statistics;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.vmse.spbau.tobedone.MainApplication;
import org.vmse.spbau.tobedone.R;
import org.vmse.spbau.tobedone.connection.model.TaskEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LastLongestTasksFragment extends ChartFragment {

    private BarChart chart;
    public static final int LONGEST_TASKS_NUMBER = 10;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_last_longest_tasks, container, false);

        chart = (BarChart) v.findViewById(R.id.longest_recent_tasks_chart);
        chart.setDescription("");

        updateChart();
        return v;
    }

    @Override
    protected void updateChart() {
        if (null == chart)
            return;

        List<TaskEntity> l = new ArrayList<>(MainApplication.getTaskDataWrapper().getTaskEntityData());

        Log.i("SIZE", "Array size is " + Integer.toString(l.size()));
        Comparator<TaskEntity> cmp = new Comparator<TaskEntity>() {
            @Override
            public int compare(TaskEntity lhs, TaskEntity rhs) {
                return (int)(lhs.getElapsedTime() - rhs.getElapsedTime());
            }
        };

        Collections.sort(l, cmp);

        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < l.size() && i < LONGEST_TASKS_NUMBER; i++) {
            TaskEntity ent = l.get(i);
            yVals1.add(new BarEntry(ent.getElapsedTime(), i));
            xVals.add(ent.getName());
        }

        BarDataSet set1 = new BarDataSet(yVals1, "Longest tasks");
        set1.setBarSpacePercent(35f);

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);

        chart.setData(data);
        chart.invalidate();
    }
}
