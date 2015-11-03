package org.vmse.spbau.tobedone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class StatisticsActivity extends AppCompatActivity implements View.OnClickListener {
    static final String FragmentNameExtra = "FRAGMENT_NAME";

    ImageView statsTimePerTag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        statsTimePerTag =  (ImageView)findViewById(R.id.stats_spent_per_tag);

        statsTimePerTag.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.stats_spent_per_tag:
                intent = new Intent(this, ChartActivity.class);
                intent.putExtra(FragmentNameExtra, TimeDifferentTagsFragment.class.getName());
                startActivity(intent);
                break;
        }
    }
}
