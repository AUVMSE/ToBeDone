package org.vmse.spbau.tobedone.task;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.List;
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
//        if (sPref.getString("START_TIME", "") != "") {
//            return false;
//        }
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
//        SharedPreferences.Editor ed = sPref.edit();
        try {
            gc.setTime(df.parse(sPref.getString("START_TIME", "")));
        } catch (Exception e) {e.printStackTrace();}
        long time = ((new GregorianCalendar()).getTimeInMillis() - gc.getTimeInMillis()) / 60 / 1000;
        Log.e("MY_TAG", "" + time);
        return time;
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
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //  HH:mm:ss
//        taskEntity.setLastStop(df.format(new GregorianCalendar()));
        taskEntity.setLastStop(task.getLastStop());
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
        taskEntity.setElapsedTime(task.getElapsedTime() + stopTime(activity));
        try {
            TaskDataWrapper.getInstance(activity).updateTask(taskEntity, task);
        } catch (Exception e) {e.printStackTrace();}

    }

    private static SortedSet<TaskEntity> prevList = null;
    public static SortedSet<TaskEntity> getSortedTaskList(Context context) {

        SortedSet<TaskEntity> sortedTasks = new TreeSet<TaskEntity>(getComparator());

        List<TaskEntity> list = TaskDataWrapper.getInstance(context).getTaskEntityData();

        list = new LinkedList<TaskEntity>();
//        if (prevList == null) {
//            for(int i = 0; i < 10; ++i) {
//                TaskEntity te = new TaskEntity();
//                te.setName("" + i + " task");
//                te.setPriority(2);
//                te.setDeadline("1995-10-01 11:11:11");
////                Log.e("MY_TAG", te.getName());
//                list.add(te);
//            }
//        } else {
//            for (TaskEntity it : prevList) {
//                Log.e("MY_TAG", it.getName() + " " + it.isSolved());
//                list.add(it);
//            }
//        }
        for(Iterator<TaskEntity> it = list.iterator(); it.hasNext();) {
            TaskEntity te = it.next();
            if(!te.isSolved()) {
                sortedTasks.add(te);
            }
        }
        prevList = sortedTasks;
        return sortedTasks;
    }

    public static Comparator<TaskEntity> getComparator() {
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
                GregorianCalendar gc = new GregorianCalendar();
                try {
                    gc.setTime(df.parse(s));
                } catch (Exception e) {e.printStackTrace();}

                long handicap1 = gc.getTimeInMillis() - currentDate.getTimeInMillis();
                if (handicap1 < 1 * 24 * 60 * 60 * 1000)
                    priority1 += 3;
                else if(handicap1 < 2 * 24 * 60 * 60 * 1000)
                    priority1 += 2;
                else if(handicap1 < 3 * 24 * 60 * 60 * 1000)
                    priority1 += 1;

                s = t2.getDeadline();
                df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                gc = new GregorianCalendar();
                try {
                    gc.setTime(df.parse(s));
                } catch (Exception e) {e.printStackTrace();}

                long handicap2 = gc.getTimeInMillis() - currentDate.getTimeInMillis();
                if (handicap2 < 1 * 24 * 60 * 60 * 1000)
                    priority2 += 3;
                else if(handicap2 < 2 * 24 * 60 * 60 * 1000)
                    priority2 += 2;
                else if(handicap2 < 3 * 24 * 60 * 60 * 1000)
                    priority2 += 1;

                if (priority1 == priority2)
                    return -1;
                return (int)(priority2 - priority1);
            }
        };
    }

}