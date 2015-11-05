package org.vmse.spbau.tobedone.connection.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author antonpp
 * @since 04/11/15
 */
public class TaskEntity {

    public static final long CREATED_OFFLINE = -1;

    private String taskname;
    private String username;
    private String description;
    private Long priority;
    private String deadline;
    private Long breakTime = 0L;
    private Boolean isSolved = false;
    private Long elapsedTime = 0L; //seconds
    private String lastStop = new String();
    private List<String> tags = new ArrayList<>();

    public static long getCreatedOffline() {
        return CREATED_OFFLINE;
    }

    public static TaskEntity taskFromJson(JSONObject jsonObject) throws JSONException {
        final TaskEntity taskEntity = new TaskEntity();
        taskEntity.setUsername(jsonObject.getString("username"));
        taskEntity.setTaskname(jsonObject.getString("taskname"));
        taskEntity.setDescription(jsonObject.getString("description"));
        taskEntity.setPriority(jsonObject.getLong("priority"));
        taskEntity.setDeadline(jsonObject.getString("deadline"));
        taskEntity.setBreakTime(jsonObject.getLong("breakTime"));
        taskEntity.setIsSolved(jsonObject.getBoolean("isSolved"));
        taskEntity.setElapsedTime(jsonObject.getLong("elapsedTime"));
        taskEntity.setLastStop(jsonObject.getString("lastStop"));
        JSONArray jsonArray = jsonObject.getJSONArray("tags");
        List<String> tags = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            tags.add(jsonArray.getString(i));
        }
        taskEntity.setTags(tags);
        return taskEntity;
    }

    public String getTaskname() {
        return taskname;
    }

    public void setTaskname(String taskname) {
        this.taskname = taskname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public TaskEntity copy() {
        try {
            return taskFromJson(toJsonObject());
        } catch (JSONException e) {
            Log.e("WOW", "LAH: " + e.getMessage());
        }
        return null;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags == null ? new ArrayList<String>() : new ArrayList<>(tags);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public Long getBreakTime() {
        return breakTime;
    }

    public void setBreakTime(Long breakTime) {
        this.breakTime = breakTime;
    }

    public void setBreakTime(long breakTime) {
        this.breakTime = breakTime;
    }

    public Boolean isSolved() {
        return isSolved;
    }

    public void setIsSolved(Boolean isSolved) {
        this.isSolved = isSolved;
    }

    public void setIsSolved(boolean isSolved) {
        this.isSolved = isSolved;
    }

    public Long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(Long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public String getLastStop() {
        return lastStop;
    }

    public void setLastStop(String lastStop) {
        this.lastStop = lastStop;
    }

    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("taskname", taskname);
        jsonObject.put("username", username);
        jsonObject.put("description", description);
        jsonObject.put("priority", priority);
        jsonObject.put("deadline", deadline);
        jsonObject.put("breakTime", breakTime);
        jsonObject.put("isSolved", isSolved());
        jsonObject.put("elapsedTime", elapsedTime);
        jsonObject.put("lastStop", lastStop);
        JSONArray jsonArray = new JSONArray();
        for (String tag : tags) {
            jsonArray.put(tag);
        }
        jsonObject.put("tags", jsonArray);
        return jsonObject;
    }
}
