package org.vmse.spbau.tobedone.task;

import org.json.JSONArray;

import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.GregorianCalendar;


public class TaskList {

    private final static int PREV_TASKS_MAX_SIZE = 2;

    private TreeSet<Task> taskQueue;
    private LinkedList<Task> prevTasks;
    private boolean isTaskRunning;

    public TaskList() {
        taskQueue = new TreeSet<Task>(getComparator());
        isTaskRunning = false;
        prevTasks = new LinkedList<Task>();
    }

    public void touchTask(Task task) {
        prevTasks.add(task);
        while (prevTasks.size() > PREV_TASKS_MAX_SIZE)
            prevTasks.remove();
    }

    public void setIsTaskRunning(boolean isRunning) {
        this.isTaskRunning = isTaskRunning;
    }

    public boolean isTaskRunning() {
        return isTaskRunning;
    }

    public void add(Task task) {
        taskQueue.add(task);
    }

    public void remove(Task task) {
        taskQueue.remove(task);
    }

    public Iterator<Task> iterator() {
        return new Iterator<Task>() {
            private Iterator<Task> it = taskQueue.iterator();
            Task next = it.hasNext() ? it.next() : null;

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public Task next() {
                Task tmp = next;
                next = it.hasNext() ? it.next() : null;
                next = next != null && next.isSolved() ? null : next;
                return tmp;
            }

            @Override
            public void remove() {
                return;
            }

        };
    }

    public void refresh() {
        TreeSet<Task> newTaskQueue = new TreeSet<Task>(getComparator());
        for(Iterator<Task> it = taskQueue.iterator(); it.hasNext();)
            newTaskQueue.add(it.next());
        taskQueue = newTaskQueue;
    }

    private Comparator<Task> getComparator() {
        return new Comparator<Task>() {

            private GregorianCalendar currentDate = new GregorianCalendar();

            @Override
            public int compare(Task t1, Task t2) {
                if(t1.isSolved())
                    return 1;
                if(t2.isSolved())
                    return -1;

                int priority1 = t1.getPriority() + t1.getTagPriority();
                if(prevTasks.contains(t1))
                    priority1 = 1;
                int priority2 = t2.getPriority() + t2.getTagPriority();
                if(prevTasks.contains(t2))
                    priority2 = 1;

                long handicap1 = t1.getDeadline().getTimeInMillis() - currentDate.getTimeInMillis();
                if (handicap1 < 1 * 24 * 60 * 60 * 1000)
                    priority1 += 5;
                else if(handicap1 < 2 * 24 * 60 * 60 * 1000)
                    priority1 += 4;
                else if(handicap1 < 3 * 24 * 60 * 60 * 1000)
                    priority1 += 3;
                else if(handicap1 < 4 * 24 * 60 * 60 * 1000)
                    priority1 += 2;
                else if(handicap1 < 5 * 24 * 60 * 60 * 1000)
                    priority1 += 1;

                long handicap2 = t2.getDeadline().getTimeInMillis() - currentDate.getTimeInMillis();
                if (handicap2 < 1 * 24 * 60 * 60 * 1000)
                    priority2 += 5;
                else if(handicap2 < 2 * 24 * 60 * 60 * 1000)
                    priority2 += 4;
                else if(handicap2 < 3 * 24 * 60 * 60 * 1000)
                    priority2 += 3;
                else if(handicap2 < 4 * 24 * 60 * 60 * 1000)
                    priority2 += 2;
                else if(handicap2 < 5 * 24 * 60 * 60 * 1000)
                    priority2 += 1;

                if (priority1 == priority2)
                    return -1;
                return priority2 - priority1;
            }
        };
    }

    public JSONArray toJSONArray() {
        JSONArray json = new JSONArray();
        try {
            for(Task t : taskQueue)
                json.put(t.toJSONObject());
        } catch (Exception e) {
            e.printStackTrace();
            json = null;
        }

        return  json;
    }


}