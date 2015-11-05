package org.vmse.spbau.tobedone.connection;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.vmse.spbau.tobedone.connection.model.TaskEntity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author antonpp
 * @since 04/11/15
 */
public class TaskDataWrapper {

    private static final String TAG = TaskDataWrapper.class.getName();

    private static final String DUMP_FILE = "dump.txt";

    private static TaskDataWrapper instance;

    private final String username;
    private final Context context;

    private List<TaskEntity> taskEntityData = new ArrayList<>();

    private TaskDataWrapper(String username, Context context) {
        this.username = username;
        this.context = context;
    }

    public static TaskDataWrapper getInstance(String username, Context context) {
        if (instance == null) {
            instance = new TaskDataWrapper(username, context);
        }
        return instance;
    }

    public List<TaskEntity> getTaskEntityData() {
        return new ArrayList<>(taskEntityData);
    }

    public void addTask(TaskEntity taskEntity) {
        taskEntityData.add(taskEntity);
    }

    public void updateTask(TaskEntity taskEntity) {
        final TaskEntity oldTaskEntity = findTaskByName(taskEntity.getTaskname());
        taskEntityData.remove(oldTaskEntity);
        taskEntityData.add(taskEntity);
    }

    private TaskEntity findTaskByName(String name) {
        for (TaskEntity t : taskEntityData) {
            if (t.getTaskname().equals(name)) {
                return t;
            }
        }
        return null;
    }

    public void saveState() throws JSONException {
        final JSONArray jsonArray = new JSONArray();
        for (TaskEntity taskEntity : taskEntityData) {
            final JSONObject jsonObject = taskEntity.toJsonObject();
        }
        final String jsonString = jsonArray.toString();
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(DUMP_FILE, Context.MODE_PRIVATE);
            outputStream.write(jsonString.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void loadState() throws JSONException {
        try {
            FileInputStream inputStream = context.openFileInput(DUMP_FILE);
            BufferedReader bufferedReader = null;
            StringBuilder stringBuilder = new StringBuilder();
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append('\n');
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } finally {
                try {
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                } catch (IOException ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
            final JSONArray jsonArray = new JSONArray(stringBuilder.toString());
            final int n = jsonArray.length();
            final List<TaskEntity> result = new ArrayList<>(n);
            for (int i = 0; i < n; ++i) {
                final JSONObject jsonObject = jsonArray.getJSONObject(i);
                result.add(TaskEntity.taskFromJson(jsonObject));
            }
            taskEntityData = result;
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void updateSync() throws JSONException {
        Util.sendTasks(taskEntityData);
        taskEntityData = Util.getAllTasksForUser(username);
    }

    public void updateASync(OnSyncFinishedListener listener) {
        new UpdateTask(taskEntityData, listener).execute();
    }

    public interface OnSyncFinishedListener {
        void onSyncFinished();
    }

    public static class SyncException extends Exception {
        public SyncException(String detailMessage) {
            super(detailMessage);
        }
    }

    private abstract class VoidAsyncTask extends AsyncTask<Void, Void, List<TaskEntity>> {
    }

    private class UpdateTask extends VoidAsyncTask {

        private final List<TaskEntity> taskEntities;
        private final OnSyncFinishedListener listener;

        private UpdateTask(List<TaskEntity> taskEntities, OnSyncFinishedListener listener) {
            this.taskEntities = taskEntities;
            this.listener = listener;
        }

        @Override
        protected List<TaskEntity> doInBackground(Void... voids) {
            try {
                Util.sendTasks(taskEntities);
                return Util.getAllTasksForUser(username);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<TaskEntity> result) {
            super.onPostExecute(result);
            listener.onSyncFinished();
            taskEntityData = result;
        }
    }
}
