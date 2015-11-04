package org.vmse.spbau.tobedone;

import org.vmse.spbau.tobedone.connection.model.TaskEntity;
import org.vmse.spbau.tobedone.task.Task;

/**
 * Created by Egor Gorbunov on 04.11.15.
 * Email: egor-mailbox@ya.ru
 */
public interface ToBeDoneActivity {
    void taskChooseFromList(TaskEntity task);
}
