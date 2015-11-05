package org.vmse.spbau.tobedone.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.vmse.spbau.tobedone.MainApplication;
import org.vmse.spbau.tobedone.R;

/**
 * Created by Egor Gorbunov on 11/4/15.
 * email: egor-mailbox@ya.ru
 */
public class SettingsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);

        EditText username = (EditText) view.findViewById(R.id.settings_username);
        username.setClickable(false);
        username.setFocusableInTouchMode(false);
        username.setFocusable(false);

        username.setText(MainApplication.getTaskDataWrapper().getUsername());

        return view;
    }
}