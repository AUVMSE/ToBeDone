package org.vmse.spbau.tobedone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;

import org.vmse.spbau.tobedone.connection.model.TaskEntity;
import org.vmse.spbau.tobedone.fragment.EditTaskFragment;
import org.vmse.spbau.tobedone.fragment.SettingsFragment;
import org.vmse.spbau.tobedone.fragment.TaskChoiceFragment;
import org.vmse.spbau.tobedone.fragment.TaskFragment;
import org.vmse.spbau.tobedone.fragment.TaskInProgressFragment;
import org.vmse.spbau.tobedone.fragment.TaskListFragment;
import org.vmse.spbau.tobedone.statistics.StatisticsActivity;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ToBeDoneActivity {

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


    private LineChart lineChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

//      TODO: delete this
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setupDrawer();

//      FRAGMENTS

        taskChoiceFragment = new TaskChoiceFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.contents_fragment_container,
                taskChoiceFragment,
                TASK_CHOICE_FRAGMENT_TAG).commit();

        taskListFragment = new TaskListFragment();

////        getSupportFragmentManager().beginTransaction().add(R.id.contents_fragment_container,
////                taskListFragment,
////                TASK_LIST_FRAGMENT_TAG).commit();
//
//
//         /*TEST*/
//        TaskList tl = new TaskList();
//
//        for (int i = 5; i < 15; ++i) {
//            tl.add(new Task(tl, "Task" + i, "", new GregorianCalendar(2015, 11 - 1, i)));
//        }
//
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//        Iterator<Task> it = tl.iterator();
//        Task t = it.next();
//        Tag tag = new Tag("2");
//
//        t.addTag(new Tag("1"));
//        t.addTag(tag);
//        t.addTag(new Tag("3"));
//        t.removeTag(tag);
//        t.start();
//        t.pause();
//        t = it.next();
//        t.start();
//        t.pause();
//        t.stop();
//
//
//        tl.refresh();
//
//        it = tl.iterator();
//
//        for(; it.hasNext();)
//            Log.d("MY_TAG", it.next().toJSONObject().toString());
////            Log.d("MY_TAG", df.format(it.next().getDeadline().getTime()));
//        Log.d("MY_TAG", tl.toJSONArray().toString());

        settingsFragment = new SettingsFragment();
        taskInProgressFragment = new TaskInProgressFragment();
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
     * @param view
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
     * @param view
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

        TaskFragment taskFragment = new TaskFragment();
        taskFragment.setTaskEntity(taskEntity);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contents_fragment_container, taskFragment,
                TASK_FRAGMENT);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void taskChooseForEdit(TaskEntity taskEntity) {
        final String TASK_EDIT_FRAGMENT = "TASK_EDIT_FRAGMENT";

        EditTaskFragment editTaskFragment = new EditTaskFragment();
        editTaskFragment.setTaskEntity(taskEntity);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contents_fragment_container, editTaskFragment,
                TASK_EDIT_FRAGMENT);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void finishTask(TaskEntity taskEntity) {

    }

    @Override
    public void removeTask(TaskEntity taskEntity) {

    }

    @Override
    public void updateTask(TaskEntity taskEntity) {

    }
}
