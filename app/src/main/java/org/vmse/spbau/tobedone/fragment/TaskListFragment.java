package org.vmse.spbau.tobedone.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.vmse.spbau.tobedone.ToBeDoneActivity;
import org.vmse.spbau.tobedone.connection.TaskDataWrapper;
import org.vmse.spbau.tobedone.connection.model.TaskEntity;
import org.vmse.spbau.tobedone.view.TaskEntityAdapter;
import org.vmse.spbau.tobedone.view.TaskEntityView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Egor Gorbunov on 11/3/15.
 * email: egor-mailbox@ya.ru
 */
public class TaskListFragment extends ListFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

//        List<TaskEntity> tasks = new ArrayList<>();
        List<TaskEntity> tasks = TaskDataWrapper.getInstance(getActivity()).getTaskEntityData();

        if (tasks == null) {
            tasks = new ArrayList<>();
            for (int i = 0; i < 10; ++i) {
                TaskEntity taskEntity = new TaskEntity();

                taskEntity.setName("SHITTY TASK " + Long.toString(i));
                taskEntity.setDescription("What a description!");
                taskEntity.setDeadline("10.10.2015");
                taskEntity.setPriority(10);

                tasks.add(taskEntity);
            }
        }



        setListAdapter(new TaskEntityAdapter(getActivity(), tasks));

        return view;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        TaskEntityView taskEntityView = (TaskEntityView) v;
        TaskEntity taskEntity = taskEntityView.getTaskEntity();

        ToBeDoneActivity toBeDoneActivity = (ToBeDoneActivity) getActivity();
        toBeDoneActivity.taskChooseFromList(taskEntity);
    }
}

