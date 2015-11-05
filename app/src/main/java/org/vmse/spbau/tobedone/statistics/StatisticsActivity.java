package org.vmse.spbau.tobedone.statistics;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import org.vmse.spbau.tobedone.R;

public class StatisticsActivity extends AppCompatActivity implements View.OnClickListener {
    static final String FragmentNameExtra = "FRAGMENT_NAME";

    ImageView statsTimePerTag;
    ImageView statsTimePerWeekDay;
    ImageView statsLastLongestTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        statsTimePerTag = (ImageView) findViewById(R.id.stats_spent_per_tag);
        statsTimePerWeekDay = (ImageView) findViewById(R.id.stats_utility_per_week_day);
        statsLastLongestTasks = (ImageView) findViewById(R.id.stats_last_longest_tasks);

        statsTimePerTag.setOnClickListener(this);
        statsTimePerWeekDay.setOnClickListener(this);
        statsLastLongestTasks.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, ChartActivity.class);
        switch (v.getId()) {
            case R.id.stats_spent_per_tag:
                intent.putExtra(FragmentNameExtra, TimeDifferentTagsFragment.class.getName());
                break;
            case R.id.stats_utility_per_week_day:
                intent.putExtra(FragmentNameExtra, TimeDifferentWeekDaysFragment.class.getName());
                break;
            case R.id.stats_last_longest_tasks:
                intent.putExtra(FragmentNameExtra, LastLongestTasksFragment.class.getName());
                break;
        }
        startActivity(intent);
    }
}
