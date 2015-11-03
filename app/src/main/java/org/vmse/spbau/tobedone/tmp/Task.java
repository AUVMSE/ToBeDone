package org.vmse.spbau.tobedone.tmp;

/**
 * Created by Egor Gorbunov on 11/3/15.
 * email: egor-mailbox@ya.ru
 */
public class Task {
    private String name;
    private String description;
    private String priority;
    private String deadline;

    public Task(String name, String description, String priority, String deadline) {
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.deadline = deadline;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPriority() {
        return priority;
    }

    public String getDeadline() {
        return deadline;
    }
}
