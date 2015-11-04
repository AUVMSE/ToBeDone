package org.vmse.spbau.tobedone.task;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.TreeSet;

public class Task {

    private final static int DEFAULT_PRIORITY   = 2;
    private final static int DEFAULT_BREAK_TIME = 120;

    private String name;
    private String description;
    private int priority;
    private GregorianCalendar deadline;
    private int breakTime;
    private int elapsedTime;
    private TreeSet<String> tagList;
    private boolean isSolved;

    private int tagPriority;
    private WeakReference<TaskUtils> taskList;
    private GregorianCalendar startTime;

    public Task(TaskUtils tl, String name, String description, GregorianCalendar deadline) {
        this.name        = name;
        this.description = description;
        this.deadline    = deadline;
        this.priority    = DEFAULT_PRIORITY;
        this.breakTime   = DEFAULT_BREAK_TIME;
        this.elapsedTime = 0;
        tagList          = new TreeSet<String>();
        isSolved         = false;
        taskList         = new WeakReference<TaskUtils>(tl);
        tagPriority      = 0;
    }

//    public void start() {
//        TaskUtils tl = taskList.get();
//        if(tl.isTaskRunning())
//            return;
//        tl.touchTask(this);
//        tl.setIsTaskRunning(true);
//        startTime = new GregorianCalendar();
//    }
//
//    public void pause() {
//        TaskUtils tl = taskList.get();
//        tl.setIsTaskRunning(false);
//        long time = (new GregorianCalendar()).getTimeInMillis() - startTime.getTimeInMillis();
//        time /= 60 * 1000;
//        startTime = null;
//        elapsedTime += time;
//    }

    public void stop() {
        isSolved = true;
    }

    public boolean isSolved() {
        return isSolved;
    }

//    private void calcTagPriority() {
//        tagPriority = 0;
//        for(Tag t : tagList)
//            tagPriority = tagPriority < t.getPriority() ? t.getPriority() : tagPriority;
//    }

    public int getTagPriority() {
        return 0;
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("description", description);
            json.put("priority", priority);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            json.put("deadline", df.format(deadline.getTime()));
            json.put("breakTime", breakTime);
            json.put("elapsedTime", elapsedTime);
            JSONArray tagJson = new JSONArray();
            for(String tag : tagList)
                tagJson.put(tag);
            json.put("tags", tagJson);
        } catch (Exception e) {
            e.printStackTrace();
            json = null;
        }
        return json;
    }

    public void addTag(String tag) {
        tagList.add(tag);
    }

    public void removeTag(String tag) {
        tagList.remove(tag);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public GregorianCalendar getDeadline() {
        return deadline;
    }

    public void setDeadline(GregorianCalendar deadline) {
        this.deadline = deadline;
    }

    public int getBreakTime() {
        return breakTime;
    }

    public void setBreakTime(int breakTime) {
        this.breakTime = breakTime;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(int elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

}