package org.vmse.spbau.tobedone.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.vmse.spbau.tobedone.tmp.Task;

import java.util.List;

/**
 * Created by Egor Gorbunov on 11/3/15.
 * email: egor-mailbox@ya.ru
 */
public class TaskAdapter extends ArrayAdapter<Task> {

    public TaskAdapter(Context c, List<Task> tasks) {
        super(c, 0, tasks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TaskView itemView = (TaskView) convertView;
        if (null == itemView)
            itemView = TaskView.inflate(parent);
        itemView.setTask(getItem(position));
        return itemView;
    }
}
