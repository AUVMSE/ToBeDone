package org.vmse.spbau.tobedone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class ChartActivity extends AppCompatActivity {

    final String[] periods = {"week", "month", "year", "custom"};
    Spinner periodSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        Intent intent = getIntent();
        String fragmentClassName = intent.getStringExtra(StatisticsActivity.FragmentNameExtra);
        Class<?> fragmentClass = null;
        Log.i("fragName", fragmentClassName);
        try { fragmentClass = Class.forName(fragmentClassName); } catch (Exception ignored){}

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, periods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        periodSpinner = (Spinner) findViewById(R.id.chart_time_limit_spinner);
        periodSpinner.setAdapter(adapter);

        periodSpinner.setPrompt("Period of interest");
        periodSpinner.setSelection(2);

        periodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // update fragments chart
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing?
            }
        });

        Log.i("CHART", "Constructor called");
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            try {
                Fragment fragment = (Fragment)fragmentClass.newInstance();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, fragment).commit();
            } catch (Exception e) {
                Log.e("ChartError", e.toString());
            }
        } else {
            Log.e("ChartError", "Fragment container not found.");
        }
    }
}
