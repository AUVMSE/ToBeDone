package org.vmse.spbau.tobedone.connection.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * @author antonpp
 * @since 04/11/15
 */
public class TaskEntity {

    public static final long CREATED_OFFLINE = -1;

    private String name;
    private String description;
    private Long priority;
    private String deadline;
    private Long breakTime;
    private Boolean isSolved;
    private Long elapsedTime; //seconds
    private String lastStop;
    private List<String> tags;

    public static long getCreatedOffline() {
        return CREATED_OFFLINE;
    }

    public static TaskEntity taskFromJson(JSONObject jsonObject) throws JSONException {
        final TaskEntity taskEntity = new TaskEntity();
        taskEntity.setName(jsonObject.getString("name"));
        taskEntity.setDescription(jsonObject.getString("description"));
        taskEntity.setPriority(jsonObject.getLong("priority"));
        taskEntity.setDeadline(jsonObject.getString("deadline"));
        taskEntity.setBreakTime(jsonObject.getLong("breakTime"));
        taskEntity.setIsSolved(jsonObject.getBoolean("isSolved"));
        taskEntity.setElapsedTime(jsonObject.getLong("elapsedTime"));
        taskEntity.setLastStop(jsonObject.getString("lastStop"));
        return taskEntity;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setPriority(long priority) {
        this.priority = priority;
    }

    public void setPriority(Long priority) {
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

    public void setBreakTime(long breakTime) {
        this.breakTime = breakTime;
    }

    public void setBreakTime(Long breakTime) {
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

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public void setElapsedTime(Long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public String getLastStop() {
        return lastStop;
    }

    public void setLastStop(String lastStop) {
        this.lastStop = lastStop;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskEntity that = (TaskEntity) o;

        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null)
            return false;
        if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null)
            return false;
        if (getPriority() != null ? !getPriority().equals(that.getPriority()) : that.getPriority() != null)
            return false;
        if (getDeadline() != null ? !getDeadline().equals(that.getDeadline()) : that.getDeadline() != null)
            return false;
        if (getBreakTime() != null ? !getBreakTime().equals(that.getBreakTime()) : that.getBreakTime() != null)
            return false;
        if (isSolved != null ? !isSolved.equals(that.isSolved) : that.isSolved != null)
            return false;
        if (getElapsedTime() != null ? !getElapsedTime().equals(that.getElapsedTime()) : that.getElapsedTime() != null)
            return false;
        if (getLastStop() != null ? !getLastStop().equals(that.getLastStop()) : that.getLastStop() != null)
            return false;
        return !(getTags() != null ? !getTags().equals(that.getTags()) : that.getTags() != null);

    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getPriority() != null ? getPriority().hashCode() : 0);
        result = 31 * result + (getDeadline() != null ? getDeadline().hashCode() : 0);
        result = 31 * result + (getBreakTime() != null ? getBreakTime().hashCode() : 0);
        result = 31 * result + (isSolved != null ? isSolved.hashCode() : 0);
        result = 31 * result + (getElapsedTime() != null ? getElapsedTime().hashCode() : 0);
        result = 31 * result + (getLastStop() != null ? getLastStop().hashCode() : 0);
        result = 31 * result + (getTags() != null ? getTags().hashCode() : 0);
        return result;
    }

    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
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
