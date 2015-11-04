package org.vmse.spbau.tobedone.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.vmse.spbau.tobedone.R;
import org.vmse.spbau.tobedone.tmp.Task;
import org.vmse.spbau.tobedone.view.TaskAdapter;
import org.vmse.spbau.tobedone.view.TaskView;

import java.util.ArrayList;

/**
 * Created by Egor Gorbunov on 11/3/15.
 * email: egor-mailbox@ya.ru
 */
public class TaskListFragment extends ListFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);


        ArrayList<Task> tasks = new ArrayList<Task>();
        for (int i = 0; i < 100; i++) {
            Task task = new Task("Task " + Integer.toString(i), "Description", Integer.toString(i % 10), "10.10.2015");
            tasks.add(task);
        }

        setListAdapter(new TaskAdapter(getActivity(), tasks));

        return view;
    }
}
