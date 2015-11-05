package org.vmse.spbau.tobedone.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.vmse.spbau.tobedone.R;

/**
 * Created by Egor Gorbunov on 11/3/15.
 * email: egor-mailbox@ya.ru
 */
public class TaskInProgressFragment extends Fragment {

    Button btnStop;
    Button btnPause;
    Button btnComplete;
    TextView timerText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_in_progress_fragment, container, false);
        btnPause = (Button) view.findViewById(R.id.taskInProgress_pauseButton);
        btnStop = (Button) view.findViewById(R.id.taskChooseFragment_stopButton);
        btnComplete = (Button) view.findViewById(R.id.taskInProgress_completeButton);

        // TODO finish
        return view;
    }
}
