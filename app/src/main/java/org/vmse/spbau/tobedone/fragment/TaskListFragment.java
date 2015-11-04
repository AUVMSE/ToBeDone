package org.vmse.spbau.tobedone.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.vmse.spbau.tobedone.MainApplication;
import org.vmse.spbau.tobedone.R;
import org.vmse.spbau.tobedone.ToBeDoneActivity;
import org.vmse.spbau.tobedone.connection.model.TaskEntity;
import org.vmse.spbau.tobedone.view.TaskEntityAdapter;
import org.vmse.spbau.tobedone.view.TaskEntityView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Egor Gorbunov on 11/3/15.
 * email: egor-mailbox@ya.ru
 */
public class TaskListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<TaskEntity>> {

    private TaskEntityAdapter adapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.addNewTaskButton);
        fab.setVisibility(View.VISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TaskEntity taskEntity = new TaskEntity();
                taskEntity.setPriority(5);
                taskEntity.setIdUser(1);
                taskEntity.setName("Dummy task" + new Random().nextInt());
                taskEntity.setDeadline("2015-07-07");
                taskEntity.setDescription("Task description");

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

            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.addNewTaskButton);
        fab.setVisibility(View.INVISIBLE);

        super.onDestroyView();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        TaskEntityView taskEntityView = (TaskEntityView) v;
        TaskEntity taskEntity = taskEntityView.getTaskEntity();

        ToBeDoneActivity toBeDoneActivity = (ToBeDoneActivity) getActivity();
        toBeDoneActivity.taskChooseFromList(taskEntity);
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
//            MainApplication.getTaskDataWrapper().syncDataSync("Gregori");
            return MainApplication.getTaskDataWrapper().getTaskEntityData();
        }
    }
}

