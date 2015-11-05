package org.vmse.spbau.tobedone.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.vmse.spbau.tobedone.R;
import org.vmse.spbau.tobedone.algorithm.TaskUtils;
import org.vmse.spbau.tobedone.connection.model.TaskEntity;
import org.vmse.spbau.tobedone.service.RestTimerService;
import org.vmse.spbau.tobedone.view.TaskEntityView;

/**
 * Created by Egor Gorbunov on 11/3/15.
 * email: egor-mailbox@ya.ru
 */
public class TaskInProgressFragment extends Fragment {
    public static final String SEC_INTERVAL_PARAM = "sec_interval";
    public static final String SEC_BREAK_TIME_PARAM = "timeBeforeBreaks";


    Button btnStop;
    Button btnPause;
    Button btnResume;
    Button btnComplete;
    TextView timerText;
    TaskEntity taskEntity;
    private Intent serviceIntent;
    boolean isRunning;
    long currentElapsedTime = 0;

    public void setTaskEntity(TaskEntity taskEntity) {
        this.taskEntity = taskEntity;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (taskEntity == null) {
            throw new NullPointerException();
        }

        View view = inflater.inflate(R.layout.task_in_progress_fragment, container, false);

        btnResume = (Button) view.findViewById(R.id.taskInProgress_resumeButton);
        btnPause = (Button) view.findViewById(R.id.taskInProgress_pauseButton);
        btnStop = (Button) view.findViewById(R.id.taskInProgress_stopButton);
        btnComplete = (Button) view.findViewById(R.id.taskInProgress_completeButton);
        timerText = (TextView) view.findViewById(R.id.time_text);

        TaskEntityView taskEntityView = (TaskEntityView) view.findViewById(R.id.taskInProgress_runningTaskView);
        taskEntityView.setTaskEntity(taskEntity);

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause();
            }
        });

        btnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }

        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: onBackPressed
                pause();
                getActivity().onBackPressed();
            }
        });

        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pause();
                TaskUtils.stop(taskEntity, getActivity());

                new AlertDialog.Builder(getContext())
                        .setTitle("Task solved!")
                        .setMessage("Congratulations with one more solved task!")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().onBackPressed();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }
        });

        isRunning = false;
        serviceIntent = new Intent(getActivity(), RestTimerService.class);

        return view;
    }

    private void start() {
        if (isRunning)
            return;

        btnResume.setVisibility(View.GONE);
        btnPause.setVisibility(View.VISIBLE);
        isRunning = true;
        TaskUtils.start(getActivity());

        getContext().registerReceiver(broadcastReceiver,
                new IntentFilter(RestTimerService.BROADCAST_ACTION));
        getContext().startService(serviceIntent.putExtra("elapsed", currentElapsedTime).
                putExtra(SEC_BREAK_TIME_PARAM, 60));
    }

    private void pause() {
        if (!isRunning)
            return;

        btnResume.setVisibility(View.VISIBLE);
        btnPause.setVisibility(View.GONE);
        isRunning = false;
        TaskUtils.pause(taskEntity, getActivity());

        getActivity().unregisterReceiver(broadcastReceiver);
        getActivity().stopService(serviceIntent);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            currentElapsedTime =
                    Long.valueOf(intent.getStringExtra(RestTimerService.SECONDS_ELAPSED_PARAM));
            timerText.setText(timeConversion(currentElapsedTime));
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        start();

    }

    // TODO
    @Override
    public void onDestroy() {
        pause();
        super.onDestroy();
    }

    private static String dummyFormat(long x) {
        String xs = Long.toString(x);
        if (xs.length() == 1) {
            xs = "0" + xs;
        }
        return xs;
    }
    private static String timeConversion(long totalSeconds) {
        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        long seconds = totalSeconds % SECONDS_IN_A_MINUTE;
        long totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE;
        long minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        long hours = totalMinutes / MINUTES_IN_AN_HOUR;

        return dummyFormat(hours) + ":" + dummyFormat(minutes) + ":" + dummyFormat(seconds);
    }


}
