package org.vmse.spbau.tobedone.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.vmse.spbau.tobedone.connection.model.TaskEntity;
import org.vmse.spbau.tobedone.task.Task;

import java.util.List;

/**
 * Created by Egor Gorbunov on 11/3/15.
 * email: egor-mailbox@ya.ru
 */
public class TaskEntityAdapter extends ArrayAdapter<TaskEntity> {

    public TaskEntityAdapter(Context c, List<TaskEntity> tasks) {
        super(c, 0, tasks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TaskEntityView itemView = (TaskEntityView) convertView;
        if (null == itemView)
            itemView = TaskEntityView.inflate(parent);
        itemView.setTaskEntity(getItem(position));
        return itemView;
    }
}
