package org.vmse.spbau.tobedone.task;

import org.json.JSONObject;

public class Tag implements Comparable {

    private final static int DEFAULT_PRIORITY = 1;

    private String name;
    private int priority;

    public Tag(String name) {
        this.name = name;
        this.priority = DEFAULT_PRIORITY;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("priority", priority);
        } catch (Exception e) {
            e.printStackTrace();
            json = null;
        }
        return json;
    }

    public int compareTo(Object o) {
        return name.compareTo(((Tag) o).getName());
    }


}