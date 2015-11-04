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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ChartActivity extends AppCompatActivity implements View.OnClickListener {

    final String[] periods = {"week", "month", "year", "custom"};
    TextView startDate;
    TextView endDate;
    Spinner periodSpinner;
    Dialog startDateDialog;
    Dialog endDateDialog;
    Fragment fragment;
    Date startDateDate;
    Date endDateDate;

    private void updateDatesTexts() {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        startDate.setText(getString(R.string.start_stat_date_str) + df.format(startDateDate));
        endDate.setText(getString(R.string.end_stat_date_str) + df.format(endDateDate));
    }

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

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        startDateDate = cal.getTime();
        cal.add(Calendar.MONTH, 1);
        endDateDate = cal.getTime();

        updateDatesTexts();

        DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                ChartFragment f = (ChartFragment) fragment;
                Calendar cal = Calendar.getInstance();
                cal.set(arg1, arg2, arg3);
                startDateDate = cal.getTime();
                updateDatesTexts();

                Log.i("CHECK", endDate.getText().toString());
                f.updateChart(startDateDate, endDateDate);
            }
        };

        DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                ChartFragment f = (ChartFragment) fragment;
                Calendar cal = Calendar.getInstance();
                cal.set(arg1, arg2, arg3);
                startDateDate = cal.getTime();
                updateDatesTexts();

                Log.i("CHECK", endDate.getText().toString());
                f.updateChart(startDateDate, endDateDate);
            }
        };

        startDateDialog = new DatePickerDialog(this, startDateListener, 1, 1, 1900);
        endDateDialog = new DatePickerDialog(this, endDateListener, 1, 1, 1900);
        Log.i("LOG", "Here is ok");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, periods);
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
                fragment = (Fragment)fragmentClass.newInstance();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, fragment).commit();
                ((ChartFragment) fragment).updateChart(startDateDate, endDateDate);
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
