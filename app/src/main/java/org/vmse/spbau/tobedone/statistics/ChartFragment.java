package org.vmse.spbau.tobedone.statistics;

import android.support.v4.app.Fragment;

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

    protected abstract void updateChart();
}
