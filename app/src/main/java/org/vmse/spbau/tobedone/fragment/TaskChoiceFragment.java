package org.vmse.spbau.tobedone.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.vmse.spbau.tobedone.R;

/**
 * Created by egorbunov on 03.11.15.
 * Email: egor-mailbox@ya.ru
 */
public class TaskChoiceFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_choose_fragment, container, false);

        return view;
    }
}
