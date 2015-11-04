package org.vmse.spbau.tobedone.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.vmse.spbau.tobedone.R;
import org.vmse.spbau.tobedone.tmp.Task;
import org.vmse.spbau.tobedone.view.TaskView;

/**
 * Created by Egor Gorbunov on 11/4/15.
 * email: egor-mailbox@ya.ru
 */
public class EditTaskFragment extends Fragment {
    private Task task;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.settings_fragment, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        TaskView taskView = (TaskView) view.findViewById(R.id.taskEditFragment_taskView);
        taskView.setTask(task);
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
