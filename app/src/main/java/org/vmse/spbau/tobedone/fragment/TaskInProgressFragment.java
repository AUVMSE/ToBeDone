package org.vmse.spbau.tobedone.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
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

import org.vmse.spbau.tobedone.MainApplication;
import org.vmse.spbau.tobedone.R;
import org.vmse.spbau.tobedone.activity.MainActivity;
import org.vmse.spbau.tobedone.algorithm.TaskUtils;
import org.vmse.spbau.tobedone.connection.TaskDataWrapper;
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

    /**
     * You must set TaskEntity, work on that was started
     */
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
                btnResume.setVisibility(View.VISIBLE);
                btnPause.setVisibility(View.GONE);

                //TODO: ?
                getActivity().unregisterReceiver(broadcastReceiver);
                getActivity().stopService(serviceIntent);

                TaskUtils.stop(taskEntity, getActivity());
            }
        });

        btnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnResume.setVisibility(View.GONE);
                btnPause.setVisibility(View.VISIBLE);

                // TODO: ???
                getContext().registerReceiver(broadcastReceiver,
                        new IntentFilter(RestTimerService.BROADCAST_ACTION));
                getContext().startService(serviceIntent
                        .putExtra(SEC_INTERVAL_PARAM, 1)
                        .putExtra(SEC_BREAK_TIME_PARAM, 5));

                TaskUtils.start(getActivity());
            }

        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPause.callOnClick();

                //TODO: ?
                getActivity().unregisterReceiver(broadcastReceiver);
                getActivity().stopService(serviceIntent);

                TaskUtils.stop(taskEntity, getActivity());

                getActivity().onBackPressed(); // go to home screen (it can only be prev. in stack)
            }
        });

        // ...................................................

        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: FIX
//                getActivity().unregisterReceiver(broadcastReceiver);
//                getActivity().stopService(serviceIntent);

                TaskUtils.stop(taskEntity, getActivity());

                TaskEntity newTaskEntity = taskEntity.copy();
                newTaskEntity.setIsSolved(true);
                try {
                    MainApplication.getTaskDataWrapper().updateTask(newTaskEntity, taskEntity);
                    taskEntity = newTaskEntity;
                } catch (TaskDataWrapper.SyncException e) {
                    e.printStackTrace();
                }

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

        TaskUtils.start(getActivity());
        serviceIntent = new Intent(getActivity(), RestTimerService.class);

        return view;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Long elapsedTime =
                    Long.valueOf(intent.getStringExtra(RestTimerService.SECONDS_ELAPSED_PARAM));
            timerText.setText(timeConversion(elapsedTime));
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(broadcastReceiver,
                new IntentFilter(RestTimerService.BROADCAST_ACTION));
        getContext().startService(serviceIntent
                .putExtra(SEC_INTERVAL_PARAM, 1)
                .putExtra(SEC_BREAK_TIME_PARAM, 5));

    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
        getActivity().stopService(serviceIntent);
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
