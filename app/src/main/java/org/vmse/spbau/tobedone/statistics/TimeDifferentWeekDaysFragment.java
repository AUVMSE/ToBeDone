package org.vmse.spbau.tobedone.statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.vmse.spbau.tobedone.MainApplication;
import org.vmse.spbau.tobedone.R;
import org.vmse.spbau.tobedone.connection.model.TaskEntity;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Andy on 04.11.2015.
 */
public class TimeDifferentWeekDaysFragment extends ChartFragment {

    BarChart chart;
    private String[] dayNames;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        ArrayList<String> xVals = new ArrayList<String>(7);
        for (int i = 2; i < 8; i++)
            xVals.add(dayNames[i]);
        xVals.add(dayNames[1]);

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        List<TaskEntity> l = MainApplication.getTaskDataWrapper().getTaskEntityData();

        DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        float[] tasks = new float[7];
        int totalTasksInPeriod = 0;

        int weeks = Weeks.weeksBetween(new DateTime(startPeriod), new DateTime(endPeriod)).getWeeks();
        for (TaskEntity ent : l) {
            if (!isDateInPeriod(ent.getLastStop()))
                continue;
            try {
                Date d = df.parse(ent.getDeadline());
                Calendar cal = Calendar.getInstance();
                cal.setTime(d);
                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                tasks[dayOfWeek - 1] += 1.0f;
                totalTasksInPeriod++;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        for (int i = 1; i < 7; i++) {
            yVals1.add(new BarEntry(tasks[i] / weeks, i - 1));
        }
        yVals1.add(new BarEntry(tasks[0] / weeks, 6));

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
