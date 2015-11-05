package org.vmse.spbau.tobedone.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.vmse.spbau.tobedone.R;
import org.vmse.spbau.tobedone.TimerService;
import org.vmse.spbau.tobedone.connection.model.TaskEntity;
import org.vmse.spbau.tobedone.task.TaskUtils;
import org.vmse.spbau.tobedone.view.TaskEntityView;

import java.util.Iterator;
import java.util.SortedSet;

/**
 * Created by egorbunov on 03.11.15.
 * Email: egor-mailbox@ya.ru
 */
public class TaskChoiceFragment extends Fragment {
    private boolean isStart = false;
    Button btnStart;
    Button btnStop;
    Button btnSkip;
    TaskEntityView taskEntityView;
    TaskEntity taskEntity;
    SortedSet sortedSet;
    Iterator<TaskEntity> it;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.task_choose_fragment, container, false);
        btnStart = (Button)view.findViewById(R.id.taskChooseFragment_startButton);
        btnStop = (Button)view.findViewById(R.id.taskChooseFragment_stopButton);
        btnSkip = (Button)view.findViewById(R.id.taskChooseFragment_skipButton);
        taskEntityView = (TaskEntityView)view.findViewById(R.id.taskChooseFragment_view);
        taskEntity = null;
        sortedSet = null;
        btnStart.setOnClickListener(null);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taskEntity != null)
                    TaskUtils.stop(taskEntity, getActivity());
                refresh();
            }
        });
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taskEntity == null)
                    refresh();
                else
                    next();
            }
        });
        refresh();

        return view;
    }

    public void refresh() {
        sortedSet = TaskUtils.getSortedTaskList(getActivity());
        taskEntityView.setVisibility(View.VISIBLE);
        it = sortedSet.iterator();
        next();
    }

    private void next() {
        if (!it.hasNext())
            it = sortedSet.iterator();
        
        taskEntity = it.hasNext() ? it.next() : null;
        if (taskEntity != null) {
            taskEntityView.setTaskEntity(taskEntity);
            btnStart.setEnabled(true);
            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isStart) {
                        btnStart.setText("Start");
                        btnStop.setEnabled(true);
                        btnSkip.setEnabled(true);

                        TaskUtils.pause(taskEntity, getActivity());
                        getActivity().startService(new Intent(getActivity(), TimerService.class)
                                .putExtra("interval", 0));
                    } else {
                        btnStart.setText("Pause");
                        btnStop.setEnabled(false);
                        btnSkip.setEnabled(false);

                        TaskUtils.start(getActivity());
                        getActivity().startService(new Intent(getActivity(), TimerService.class)
                                .putExtra("interval", 10));
                    }
                    isStart = !isStart;
                }
            });
        } else {
            taskEntityView.setVisibility(View.INVISIBLE);
            btnStart.setEnabled(false);
            btnStart.setOnClickListener(null);
//            taskEntityView.setTaskEntity(new TaskEntity());
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}