package org.vmse.spbau.tobedone.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.vmse.spbau.tobedone.MainApplication;
import org.vmse.spbau.tobedone.R;
import org.vmse.spbau.tobedone.activity.ToBeDoneActivity;
import org.vmse.spbau.tobedone.connection.TaskDataWrapper;
import org.vmse.spbau.tobedone.connection.model.TaskEntity;
import org.vmse.spbau.tobedone.view.TaskEntityAdapter;
import org.vmse.spbau.tobedone.view.TaskEntityView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Egor Gorbunov on 11/3/15.
 * email: egor-mailbox@ya.ru
 */
public class TaskListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<TaskEntity>> {

    private TaskEntityAdapter adapter;
    private Map<TaskEntity, List<String>> taskTagsMap = new HashMap<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setDefaultEmptyText();
        adapter = new TaskEntityAdapter(getActivity());
        setListAdapter(adapter);
        setListShown(false);
        getLoaderManager().initLoader(0, null, this);
    }

    private void setDefaultEmptyText() {
        setEmptyText("No tasks found");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.task_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_create_new_task:
                TaskEntity taskEntity = new TaskEntity();
                taskEntity.setPriority(5);
                taskEntity.setIdUser(1);
                taskEntity.setName("Dummy task" + new Random().nextInt());
                taskEntity.setDeadline("2015-07-07");
                taskEntity.setDescription("Task description");
                taskEntity.setIsSolved(false);

                List<String> tags = new ArrayList<String>() {{
                    add("university");
                    add("study");
                    add("ha");
                }};


                try {
                    MainApplication.getTaskDataWrapper().addTask(taskEntity, tags);
                } catch (Exception ignored) {
                }

                TaskListFragment.this.getLoaderManager().restartLoader(0, null, TaskListFragment.this);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        TaskEntityView taskEntityView = (TaskEntityView) v;
        TaskEntity taskEntity = taskEntityView.getTaskEntity();

        ToBeDoneActivity toBeDoneActivity = (ToBeDoneActivity) getActivity();

        toBeDoneActivity.taskChooseFromList(taskEntity, taskTagsMap.get(taskEntity));
    }

    @Override
    public Loader<List<TaskEntity>> onCreateLoader(int id, Bundle args) {
        return new TaskEntityLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<TaskEntity>> loader, List<TaskEntity> data) {
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
        if (data != null && !data.isEmpty()) {

            for (final TaskEntity taskEntity : data) {
                MainApplication.getTaskDataWrapper().getTagsForTask(taskEntity, new TaskDataWrapper.TagsListReceiver() {
                    @Override
                    public void onTagsListReceived(List<String> tags) {
                        taskTagsMap.put(taskEntity, tags);
                    }
                });
            }

            adapter.setData(data);
        } else {
            setDefaultEmptyText();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<TaskEntity>> loader) {
        adapter.setData(null);
    }

    private static class TaskEntityLoader extends AsyncTaskLoader<List<TaskEntity>> {

        public TaskEntityLoader(Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public List<TaskEntity> loadInBackground() {
            MainApplication.getTaskDataWrapper().syncDataSync();
            return MainApplication.getTaskDataWrapper().getTaskEntityData();
        }
    }
}
