package org.vmse.spbau.tobedone.activity;

import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONException;
import org.vmse.spbau.tobedone.MainApplication;
import org.vmse.spbau.tobedone.R;
import org.vmse.spbau.tobedone.connection.TaskDataWrapper;
import org.vmse.spbau.tobedone.connection.model.TaskEntity;
import org.vmse.spbau.tobedone.fragment.EditableTaskFragment;
import org.vmse.spbau.tobedone.fragment.SettingsFragment;
import org.vmse.spbau.tobedone.fragment.TaskChoiceFragment;
import org.vmse.spbau.tobedone.fragment.TaskInProgressFragment;
import org.vmse.spbau.tobedone.fragment.TaskListFragment;
import org.vmse.spbau.tobedone.statistics.StatisticsActivity;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ToBeDoneActivity, TaskDataWrapper.OnSyncFinishedListener {

    private static final String TAG = MainActivity.class.getName();

    private static final String TASK_IN_PROGRESS_FRAGMENT_TAG = "TASK_IN_PROGRESS_FRAGMENT";
    private static final String TASK_CHOICE_FRAGMENT_TAG = "TASK_CHOICE_FRAGMENT";
    private static final String TASK_LIST_FRAGMENT_TAG = "TASK_LIST_FRAGMENT";
    private static final String SETTINGS_FRAGMENT_TAG = "SETTINGS_FRAGMENT";

    private TaskChoiceFragment taskChoiceFragment;
    private TaskListFragment taskListFragment;
    private SettingsFragment settingsFragment;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private TaskInProgressFragment taskInProgressFragment;

    private AccountManager accountManager;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setupDrawer();

//      FRAGMENTS
        taskChoiceFragment = new TaskChoiceFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.contents_fragment_container,
                taskChoiceFragment,
                TASK_CHOICE_FRAGMENT_TAG).commit();

        taskListFragment = new TaskListFragment();
        settingsFragment = new SettingsFragment();
        taskInProgressFragment = new TaskInProgressFragment();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            MainApplication.getTaskDataWrapper().saveState();
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        item.setChecked(false);

        if (id == R.id.nav_show_task_list) {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contents_fragment_container, taskListFragment,
                    TASK_LIST_FRAGMENT_TAG);
            transaction.addToBackStack(null);
            transaction.commit();

            setupBackButton();

        } else if (id == R.id.nav_settings) {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contents_fragment_container, settingsFragment,
                    SETTINGS_FRAGMENT_TAG);
            transaction.addToBackStack(null);
            transaction.commit();

            setupBackButton();
        } else if (id == R.id.nav_statistics) {
            Intent intent = new Intent(this, StatisticsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_synchronize) {
            try {
                MainApplication.getTaskDataWrapper().updateASync(this);
                progressDialog = ProgressDialog.show(this, "Synchronization with server...", "Please wait", true);
            } catch (TaskDataWrapper.SyncException e) {
                Log.e(getClass().getCanonicalName(), e.getMessage());
                new AlertDialog.Builder(this)
                        .setTitle("Error...")
                        .setMessage(e.getMessage())
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    /**
     * Use it to setup that navigation bar, which slides from left side
     */
    private void setupDrawer() {
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Use it to change nav. bar with back button
     */
    private void setupBackButton() {
        drawer.setDrawerListener(null);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    setupDrawer();
                }
            }
        });
    }

    /**
     * Process click on START button in TaskChoiceFragment
     *
     * @param view the view
     */
    public void onClick_btnStartTask(View view) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contents_fragment_container, taskInProgressFragment,
                TASK_IN_PROGRESS_FRAGMENT_TAG);
//        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Process click on STOP button in TaskInProgressFragment
     *
     * @param view the view
     */
    public void onClick_btnStopTask(View view) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contents_fragment_container, taskChoiceFragment,
                TASK_CHOICE_FRAGMENT_TAG);
//        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void taskChooseFromList(TaskEntity taskEntity) {
        final String TASK_FRAGMENT = "TASK_FRAGMENT";

        EditableTaskFragment taskFragment = new EditableTaskFragment();
        taskFragment.setTaskEntity(taskEntity, true);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contents_fragment_container, taskFragment,
                TASK_FRAGMENT);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onSyncFinished() {
        progressDialog.dismiss();
    }
}
