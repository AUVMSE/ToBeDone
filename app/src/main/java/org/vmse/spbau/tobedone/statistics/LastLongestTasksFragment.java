package org.vmse.spbau.tobedone.statistics;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.vmse.spbau.tobedone.MainApplication;
import org.vmse.spbau.tobedone.R;
import org.vmse.spbau.tobedone.connection.model.TaskEntity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class LastLongestTasksFragment extends ChartFragment implements OnChartValueSelectedListener {

    private BarChart chart;
    public static final int LONGEST_TASKS_NUMBER = 10;
    private HashMap<Integer, String> entryNames;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_last_longest_tasks, container, false);

        chart = (BarChart) v.findViewById(R.id.longest_recent_tasks_chart);
        chart.setDescription("");

        updateChart();

        chart.setOnChartValueSelectedListener(this);
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
                return (int)(rhs.getElapsedTime() - lhs.getElapsedTime());
            }
        };

        Collections.sort(l, cmp);

        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        entryNames = new HashMap<>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < l.size() && yVals1.size() < LONGEST_TASKS_NUMBER; i++) {
            TaskEntity ent = l.get(i);

            try {
                Date d = df.parse(ent.getLastStop());
                if (d.before(startPeriod) || d.after(endPeriod))
                    continue;
            } catch (ParseException e) {
                e.printStackTrace();
                continue;
            }

            BarEntry entry = new BarEntry(ent.getElapsedTime(), i);
            yVals1.add(entry);
            entryNames.put(i, ent.getName());
            xVals.add(ent.getName().substring(0, 4));
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

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        String fullName = entryNames.get(e.getXIndex());
        Toast.makeText(getContext(), fullName, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected() {

    }
}
