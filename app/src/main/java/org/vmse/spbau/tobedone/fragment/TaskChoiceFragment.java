package org.vmse.spbau.tobedone.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.vmse.spbau.tobedone.MainApplication;
import org.vmse.spbau.tobedone.R;
import org.vmse.spbau.tobedone.TimerService;
import org.vmse.spbau.tobedone.algorithm.TaskUtils;
import org.vmse.spbau.tobedone.connection.TaskDataWrapper;
import org.vmse.spbau.tobedone.connection.model.TaskEntity;
import org.vmse.spbau.tobedone.view.TaskEntityView;

import java.util.Formatter;
import java.util.Iterator;
import java.util.SortedSet;

/**
 * Created by egorbunov on 03.11.15.
 * Email: egor-mailbox@ya.ru
 */
public class TaskChoiceFragment extends Fragment implements TaskDataWrapper.OnSyncFinishedListener {
    Button btnStart;
    Button btnStop;
    Button btnSkip;
    CountDownTimer timer;
    TaskEntityView taskEntityView;
    TaskEntity taskEntity;
    TextView timeText;
    SortedSet<TaskEntity> sortedSet;
    Iterator<TaskEntity> it;
    Formatter f = new Formatter();
    private boolean isStart = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.task_choose_fragment, container, false);
        btnStart = (Button) view.findViewById(R.id.taskChooseFragment_startButton);
        btnStop = (Button) view.findViewById(R.id.taskChooseFragment_stopButton);
        btnSkip = (Button) view.findViewById(R.id.taskChooseFragment_skipButton);
        taskEntityView = (TaskEntityView) view.findViewById(R.id.taskChooseFragment_view);
        timeText = (TextView) view.findViewById(R.id.time_text);
        timer = null;

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
        startRefreshing();

        return view;
    }

    public void startRefreshing() {
        MainApplication.getTaskDataWrapper().syncDataAsync(this);
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
                        timer.cancel();
                        btnStart.setText("Start");
                        btnStop.setEnabled(true);
                        btnSkip.setEnabled(true);

                        TaskUtils.pause(taskEntity, getActivity());
                        getActivity().startService(new Intent(getActivity(), TimerService.class)
                                .putExtra("interval", 0));
                    } else {
                        timer = new CountDownTimer(taskEntity.getElapsedTime(), 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                long hours = millisUntilFinished / 3600000;
                                long minutes = millisUntilFinished / 60000 - hours * 60;
                                long seconds = millisUntilFinished / 1000 - hours * 3600 - minutes * 60;
                                timeText.setText(String.format("%02d:%02d:%02d",
                                        hours, minutes, seconds));
                                taskEntity.setElapsedTime(millisUntilFinished);
                            }

                            @Override
                            public void onFinish() {

                            }
                        }.start();
                        btnStart.setText("Pause");
                        btnStop.setEnabled(false);
                        btnSkip.setEnabled(false);

                        TaskUtils.start(getActivity());
                        final long millisInFuture = taskEntity.getElapsedTime() * 60;
                        long countDownInterval = 1000;


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
    public void onSyncFinished() {
        refresh();
    }
}