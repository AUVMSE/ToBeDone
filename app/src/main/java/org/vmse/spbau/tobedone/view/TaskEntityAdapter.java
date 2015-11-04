package org.vmse.spbau.tobedone.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.vmse.spbau.tobedone.connection.model.TaskEntity;

import java.util.List;

/**
 * Created by Egor Gorbunov on 11/3/15.
 * email: egor-mailbox@ya.ru
 */
public class TaskEntityAdapter extends ArrayAdapter<TaskEntity> {

    public TaskEntityAdapter(Context ctx) {
        super(ctx, 0);
    }

    public void setData(List<TaskEntity> data) {
        clear();
        if (data != null) {
            addAll(data);
        }
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