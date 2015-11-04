package org.vmse.spbau.tobedone.activity;

import org.vmse.spbau.tobedone.connection.model.TaskEntity;

import java.util.List;

/**
 * Created by Egor Gorbunov on 04.11.15.
 * Email: egor-mailbox@ya.ru
 */
public interface ToBeDoneActivity {
    void taskChooseFromList(TaskEntity task, List<String> strings);

    void finishTask(TaskEntity taskEntity);

    void removeTask(TaskEntity taskEntity);

    void updateTask(TaskEntity taskEntity);
}
