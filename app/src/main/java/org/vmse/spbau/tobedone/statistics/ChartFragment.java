package org.vmse.spbau.tobedone.statistics;

import android.support.v4.app.Fragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Andy on 04.11.2015.
 */
public abstract class ChartFragment extends Fragment {
    protected Date startPeriod;
    protected Date endPeriod;

    public void updatePeriod(Date startPeriod, Date endPeriod) {
        this.endPeriod = endPeriod;
        this.startPeriod = startPeriod;
        updateChart();
    }

    public boolean isDateInPeriod(String date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d = df.parse(date);
            return (!d.after(endPeriod) && !d.before(startPeriod));
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    protected abstract void updateChart();
}
