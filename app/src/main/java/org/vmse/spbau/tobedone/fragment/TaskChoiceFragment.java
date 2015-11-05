package org.vmse.spbau.tobedone.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.vmse.spbau.tobedone.R;
import org.vmse.spbau.tobedone.algorithm.TaskUtils;
import org.vmse.spbau.tobedone.connection.TaskDataWrapper;
import org.vmse.spbau.tobedone.connection.model.TaskEntity;
import org.vmse.spbau.tobedone.view.TaskEntityView;

import java.util.Iterator;
import java.util.SortedSet;

/**
 * Created by egorbunov on 03.11.15.
 * Email: egor-mailbox@ya.ru
 */
public class TaskChoiceFragment extends Fragment implements TaskDataWrapper.OnSyncFinishedListener {
    Button btnStart;
    Button btnSkip;
    CountDownTimer timer;
    TaskEntityView taskEntityView;
    TaskEntity taskEntity;
    TextView textView;
    SortedSet<TaskEntity> sortedSet;
    Iterator<TaskEntity> it;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.task_choose_fragment, container, false);
        btnStart = (Button) view.findViewById(R.id.taskChooseFragment_startButton);
        btnSkip = (Button) view.findViewById(R.id.taskChooseFragment_skipButton);
        taskEntityView = (TaskEntityView) view.findViewById(R.id.taskChooseFragment_view);
        textView = (TextView) view.findViewById(R.id.taskChooseFragment_textView);
        timer = null;

        taskEntity = null;
        sortedSet = null;
        btnStart.setOnClickListener(null);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taskEntity == null)
                    refresh();
                else
                    next();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskInProgressFragment taskInProgressFragment = new TaskInProgressFragment();
                taskInProgressFragment.setTaskEntity(taskEntity);

                // changing fragment
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.contents_fragment_container, taskInProgressFragment, "TASK_IN_PROGRESS_FRAGMENT");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        refresh();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();

    }

    public void refresh() {
        sortedSet = TaskUtils.getSortedTaskList(getActivity());
        taskEntityView.setVisibility(View.VISIBLE);
        it = sortedSet.iterator();
        if (it.hasNext())
            next();
        else {
            taskEntityView.setVisibility(View.INVISIBLE);
            textView.setText("No active tasks in list");
            btnStart.setEnabled(false);
        }
    }

    private void next() {

        taskEntity = it.hasNext() ? it.next() : null;
        if (taskEntity != null) {
            taskEntityView.setTaskEntity(taskEntity);
            btnStart.setEnabled(true);
            textView.setText("");
        } else {
            btnStart.setEnabled(false);
            refresh();
        }

        if (!it.hasNext()) {
            textView.setText("No more tasks left");
        }
    }

    @Override
    public void onSyncFinished() {
        refresh();
    }
}