package org.vmse.spbau.tobedone.task;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.SortedSet;

import org.json.JSONArray;
import org.json.JSONObject;
import org.vmse.spbau.tobedone.connection.TaskDataWrapper;
import org.vmse.spbau.tobedone.connection.model.TaskEntity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.GregorianCalendar;


public class TaskUtils {


    private final static int PREV_TASKS_MAX_SIZE = 2;



    private static boolean startTime(Activity act) {
        SharedPreferences sPref = act.getPreferences(Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        ed.putString("START_TIME", df.format(new Date()));
        ed.commit();
        return true;
    }

    private static long stopTime(Activity act) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        GregorianCalendar gc = new GregorianCalendar();
        SharedPreferences sPref = act.getPreferences(Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        try {
            gc.setTime(df.parse(sPref.getString("START_TIME", "")));
        } catch (Exception e) {e.printStackTrace();}
        return (new GregorianCalendar()).getTimeInMillis() - gc.getTimeInMillis();
    }

    public static void start(Activity activity) {
        startTime(activity);
    }

    public static void pause(TaskEntity task, Activity activity) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setBreakTime(task.getBreakTime());
        taskEntity.setDeadline(task.getDeadline());
        taskEntity.setDescription(task.getDescription());
        taskEntity.setName(task.getName());
        taskEntity.setId(task.getId());
        taskEntity.setIdUser(task.getIdUser());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        taskEntity.setLastStop(df.format(new GregorianCalendar()));
        taskEntity.setIsSolved(task.isSolved());
        taskEntity.setElapsedTime(task.getElapsedTime() + stopTime(activity));
        try {
            TaskDataWrapper.getInstance(activity).updateTask(taskEntity, task);
        } catch (Exception e) {e.printStackTrace();}
    }

    public static void stop(TaskEntity task, Activity activity) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setBreakTime(task.getBreakTime());
        taskEntity.setDeadline(task.getDeadline());
        taskEntity.setDescription(task.getDescription());
        taskEntity.setName(task.getName());
        taskEntity.setId(task.getId());
        taskEntity.setIdUser(task.getIdUser());
        taskEntity.setLastStop(task.getLastStop());
        taskEntity.setIsSolved(true);
        taskEntity.setElapsedTime((task.getElapsedTime() + stopTime(activity)) / 60 / 1000);
        try {
            TaskDataWrapper.getInstance(activity).updateTask(taskEntity, task);
        } catch (Exception e) {e.printStackTrace();}

    }

    public static SortedSet<TaskEntity> getSortedTaskList(Context context) {
        SortedSet<TaskEntity> sortedTasks = new TreeSet<TaskEntity>(getComparator());
        for(Iterator<TaskEntity> it = TaskDataWrapper.getInstance(context).getTaskEntityData().iterator(); it.hasNext();) {
            TaskEntity te = it.next();
            if(!te.isSolved())
                sortedTasks.add(it.next());
        }
        return sortedTasks;
    }

    private static Comparator<TaskEntity> getComparator() {
        return new Comparator<TaskEntity>() {

            private GregorianCalendar currentDate = new GregorianCalendar();

            @Override
            public int compare(TaskEntity t1, TaskEntity t2) {
                if(t1.isSolved())
                    return 1;
                if(t2.isSolved())
                    return -1;

                long priority1 = t1.getPriority();
//                if(prevTasks.contains(t1))
//                    priority1 = 1;
                long priority2 = t2.getPriority();
//                if(prevTasks.contains(t2))
//                    priority2 = 1;

                String s = t1.getDeadline();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                GregorianCalendar gc.
//                long handicap1 = t1.getDeadline().getTimeInMillis() - currentDate.getTimeInMillis();
//                if (handicap1 < 1 * 24 * 60 * 60 * 1000)
//                    priority1 += 5;
//                else if(handicap1 < 2 * 24 * 60 * 60 * 1000)
//                    priority1 += 4;
//                else if(handicap1 < 3 * 24 * 60 * 60 * 1000)
//                    priority1 += 3;
//                else if(handicap1 < 4 * 24 * 60 * 60 * 1000)
//                    priority1 += 2;
//                else if(handicap1 < 5 * 24 * 60 * 60 * 1000)
//                    priority1 += 1;
//
//                long handicap2 = t2.getDeadline().getTimeInMillis() - currentDate.getTimeInMillis();
//                if (handicap2 < 1 * 24 * 60 * 60 * 1000)
//                    priority2 += 5;
//                else if(handicap2 < 2 * 24 * 60 * 60 * 1000)
//                    priority2 += 4;
//                else if(handicap2 < 3 * 24 * 60 * 60 * 1000)
//                    priority2 += 3;
//                else if(handicap2 < 4 * 24 * 60 * 60 * 1000)
//                    priority2 += 2;
//                else if(handicap2 < 5 * 24 * 60 * 60 * 1000)
//                    priority2 += 1;
//
//                if (priority1 == priority2)
//                    return -1;
                return (int)(priority2 - priority1);
            }
        };
    }

}