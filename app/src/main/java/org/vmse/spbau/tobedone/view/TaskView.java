package org.vmse.spbau.tobedone.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.vmse.spbau.tobedone.R;
import org.vmse.spbau.tobedone.tmp.Task;

/**
 * Created by Egor Gorbunov on 11/3/15.
 * email: egor-mailbox@ya.ru
 */
public class TaskView extends RelativeLayout {

    /**
     * TaskView can be used in list view, in that case user probably want to
     * make TaskView smaller (don't show description and that kinds of stuff...)
     */
    private boolean isSmall;
    private TextView nameTextView;
    private TextView descriptionTextView;
    private TextView priorityTextView;
    private TextView deadlineTextView;

    public TaskView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TaskView,
                0, 0);

        try {
            isSmall = a.getBoolean(R.styleable.TaskView_isSmall, false);
        } finally {
            a.recycle();
        }

        LayoutInflater.from(context).inflate(R.layout.task_view_child, this, true);
        nameTextView = (TextView) findViewById(R.id.taskView_nameTextView);
        nameTextView.setText("Task header");
        descriptionTextView = (TextView) findViewById(R.id.taskView_descriptionTextView);
        descriptionTextView.setText("Task description");
        priorityTextView = (TextView) findViewById(R.id.taskView_priorityTextView);
        priorityTextView.setText("8");
        deadlineTextView = (TextView) findViewById(R.id.taskView_deadlineTextView);
        deadlineTextView.setText("10.09.2017");
    }

    public static TaskView inflate(ViewGroup parent) {
        TaskView taskView = (TaskView)LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_view, parent, false);
        return taskView;
    }

    public void setTask(Task task) {
        nameTextView.setText(task.getName());
        descriptionTextView.setText(task.getDescription());
        priorityTextView.setText(task.getPriority());
        deadlineTextView.setText(task.getDeadline());
    }
}
