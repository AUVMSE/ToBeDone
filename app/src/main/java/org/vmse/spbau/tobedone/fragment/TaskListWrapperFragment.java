package org.vmse.spbau.tobedone.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.vmse.spbau.tobedone.R;

/**
 * Created by Andy on 05.11.2015.
 */
public class TaskListWrapperFragment extends Fragment {
    private static final String TASK_LIST_FRAGMENT_TAG = "Task list";

    TaskListFragment listFragment;
    EditText filterEdit;
    Spinner filterSpinner;
    FilterType filterType;

    enum FilterType {
        BY_NAME,
        BY_PRIORITY,
        BY_TAGS
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        View myFragmentView = inflater.inflate(R.layout.task_list_wrapper_fragment, container, false);

        filterEdit = (EditText) myFragmentView.findViewById(R.id.task_filter_edit);
        filterSpinner = (Spinner) myFragmentView.findViewById(R.id.chart_type_spinner);
        filterType = FilterType.BY_NAME;

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        filterType = FilterType.BY_NAME;
                        break;
                    case 1:
                        filterType = FilterType.BY_PRIORITY;
                        break;
                    case 2:
                        filterType = FilterType.BY_TAGS;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        filterEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        listFragment.
                        break;
                }
                return false;
            }
        });

        listFragment = new TaskListFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.contents_list_tasks_frame, listFragment,
                TASK_LIST_FRAGMENT_TAG).commit();

        return myFragmentView;
    }

}
