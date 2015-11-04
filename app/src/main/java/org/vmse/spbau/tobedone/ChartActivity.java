package org.vmse.spbau.tobedone;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

public class ChartActivity extends AppCompatActivity implements View.OnClickListener {

    final String[] periods = {"week", "month", "year", "custom"};
    TextView startDate;
    TextView endDate;
    Spinner periodSpinner;
    Dialog startDateDialog;
    Dialog endDateDialog;
    private final int START_DATE_DIALOG_ID = 1;
    private final int END_DATE_DIALOG_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        Intent intent = getIntent();
        String fragmentClassName = intent.getStringExtra(StatisticsActivity.FragmentNameExtra);
        Class<?> fragmentClass = null;
        Log.i("fragName", fragmentClassName);
        try { fragmentClass = Class.forName(fragmentClassName); } catch (Exception ignored){}

        startDate = (TextView) findViewById(R.id.start_date_text);
        endDate = (TextView) findViewById(R.id.end_date_text);
        startDate.setOnClickListener(this);
        endDate.setOnClickListener(this);

        startDate.setEnabled(false);
        endDate.setEnabled(false);

        DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                // arg1 = year
                // arg2 = month
                // arg3 = day
                Log.i("DATE", "Start Date set");
            }
        };

        DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                // arg1 = year
                // arg2 = month
                // arg3 = day
            }
        };

        startDateDialog = new DatePickerDialog(this, startDateListener, 1, 1, 1900);
        endDateDialog = new DatePickerDialog(this, endDateListener, 1, 1, 1900);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, periods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        periodSpinner = (Spinner) findViewById(R.id.chart_time_limit_spinner);
        periodSpinner.setAdapter(adapter);

        periodSpinner.setPrompt("Period of interest");
        periodSpinner.setSelection(2);

        periodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                startDate.setEnabled(false);
                endDate.setEnabled(false);
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        startDate.setEnabled(true);
                        endDate.setEnabled(true);
                        break;
                }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_date_text:
                startDateDialog.show();
                break;
            case R.id.end_date_text:
                endDateDialog.show();
                break;
        }
    }
}
