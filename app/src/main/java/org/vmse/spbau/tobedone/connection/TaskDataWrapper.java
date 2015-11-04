package org.vmse.spbau.tobedone.connection;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author antonpp
 * @since 04/11/15
 */
public class TaskDataWrapper {

    private static final String TAG = TaskDataWrapper.class.getName();

    private static final String DUMP_FILE = "dump.txt";

    private static TaskDataWrapper instance;

    private final Context context;
    private final String username;

    private boolean isSyncing = false;
    private List<TaskEntity> taskEntityData = new ArrayList<>();
    private Map<TaskEntity, List<String>> tags = new HashMap<>();

    private TaskDataWrapper(Context context, String username) {
        this.context = context;
        this.username = username;
    }

    public static TaskDataWrapper getInstance(Context context, String username) {
        if (instance == null) {
            instance = new TaskDataWrapper(context, username);
        }
        return instance;
    }

    public List<TaskEntity> getTaskEntityData() {
        return taskEntityData;
    }

    public void addTask(TaskEntity taskEntity, List<String> tags) throws SyncException {
        if (isSyncing) {
            throw new SyncException("Cannot add task while syncing");
        }
        if (Util.isConnected(context)) {
            new AddTaskEntityTask(taskEntity, tags).execute();
        } else {
            this.tags.put(taskEntity, tags);
        }
        taskEntityData.add(taskEntity);
    }

    public List<String> getTagsForTaskCached(TaskEntity taskEntity) {
        if (tags.containsKey(taskEntity)) {
            return tags.get(taskEntity);
        }
        return null;
    }

    public void getTagsForTask(TaskEntity taskEntity, TagsListReceiver receiver) {
        new GetTagsTask(taskEntity, receiver).execute();
    }

    /**
     * @param taskEntity    updated version of task
     * @param oldTaskEntity leave it as Null if task was created online and provide it otherwise
     */
    public void updateTask(TaskEntity taskEntity, TaskEntity oldTaskEntity) throws SyncException {
        if (isSyncing) {
            throw new SyncException("Cannot update while syncing");
        }
        if (oldTaskEntity == null) {
            oldTaskEntity = findTaskById(taskEntity.getId());
        }
        taskEntityData.remove(oldTaskEntity);
        taskEntityData.add(taskEntity);
        if (taskEntity.getId() != TaskEntity.CREATED_OFFLINE && Util.isConnected(context)) {
            new UpdateTaskEntityTask(taskEntity).execute();
        }
    }

    public void updateTaskTags(TaskEntity taskEntity, List<String> tags) throws SyncException {
        if (isSyncing) {
            throw new SyncException("Cannot update while syncing");
        }
        this.tags.remove(taskEntity);
        this.tags.put(taskEntity, tags);
        if (taskEntity.getId() != TaskEntity.CREATED_OFFLINE && Util.isConnected(context)) {
            new UpdateTagsTask(taskEntity, tags).execute();
        }
    }

    private TaskEntity findTaskById(long id) {
        for (TaskEntity t : taskEntityData) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public void addUser(String userName) {
        if (Util.isConnected(context)) {
            new AddUserTask(userName);
        }
    }

    public void syncDataSync() {
        if (Util.isConnected(context)) {
            isSyncing = true;

            List<TaskEntity> newTaskEntityData = new ArrayList<>(taskEntityData);
            Map<TaskEntity, List<String>> newTags = new HashMap<>();

            Util.addUser(username);

            for (TaskEntity taskEntity : newTaskEntityData) {
                try {
                    Util.updateTask(taskEntity);
                    newTags.put(taskEntity, Util.getAllTagsForTask(taskEntity.getId()));
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
            try {
                newTaskEntityData = Util.getAllTasksForUser(username);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }

            if (newTaskEntityData != null) {
                taskEntityData = newTaskEntityData;
                tags = newTags;
            }

            isSyncing = false;
        }
    }

    public void syncDataAsync() {
        syncDataAsync(null);
    }

    public void syncDataAsync(@Nullable OnSyncFinishedListener listener) {
        if (Util.isConnected(context)) {
            new SyncDataTask(username, listener).execute();
        }
    }

    public void saveState() throws JSONException {
        syncDataAsync();

        final JSONArray jsonArray = new JSONArray();
        for (TaskEntity taskEntity : taskEntityData) {
            final JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", taskEntity.getName());
                jsonObject.put("description", taskEntity.getDescription());
                jsonObject.put("priority", taskEntity.getPriority());
                jsonObject.put("deadline", taskEntity.getDeadline());
                jsonObject.put("breakTime", taskEntity.getBreakTime());
                jsonObject.put("elapsedTime", taskEntity.getElapsedTime());
                jsonArray.put(jsonObject);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
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
                result.add(Util.taskFromJson(jsonObject));
            }
            taskEntityData = result;
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }

        syncDataAsync();
    }

    public interface OnSyncFinishedListener {
        void onSyncFinished();
    }

    public interface TagsListReceiver {
        void onTagsListReceived(List<String> tags);
    }

    public static class SyncException extends Exception {
        public SyncException(String detailMessage) {
            super(detailMessage);
        }
    }

    private abstract class VoidAsyncTask extends AsyncTask<Void, Void, Void> {
    }

    private class UpdateTaskEntityTask extends VoidAsyncTask {

        private final TaskEntity taskEntity;

        private UpdateTaskEntityTask(TaskEntity taskEntity) {
            this.taskEntity = taskEntity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Util.updateTask(taskEntity);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }
    }

    private class UpdateTagsTask extends VoidAsyncTask {

        private final TaskEntity taskEntity;
        private final List<String> tags;

        private UpdateTagsTask(TaskEntity taskEntity, List<String> tags) {
            this.taskEntity = taskEntity;
            this.tags = tags;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Util.updateTags(taskEntity.getId(), tags);
            return null;
        }
    }

    private class GetTagsTask extends AsyncTask<Void, Void, List<String>> {

        private final TaskEntity taskEntity;
        private final TagsListReceiver receiver;

        private GetTagsTask(TaskEntity taskEntity, TagsListReceiver receiver) {
            this.taskEntity = taskEntity;
            this.receiver = receiver;
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            try {
                return Util.getAllTagsForTask(taskEntity.getId());
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<String> tags) {
            receiver.onTagsListReceived(tags);
        }
    }

    private class AddTaskEntityTask extends VoidAsyncTask {

        private final TaskEntity taskEntity;
        private final List<String> tags;

        private AddTaskEntityTask(TaskEntity taskEntity, List<String> tags) {
            this.taskEntity = taskEntity;
            this.tags = tags;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            isSyncing = true;
            try {
                JSONObject jsonObject = new JSONObject(Util.addTask(taskEntity, tags));
                taskEntity.setId(jsonObject.getLong("id"));
                TaskDataWrapper.this.tags.put(taskEntity, Util.getAllTagsForTask(taskEntity.getId()));
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
            isSyncing = false;
            return null;
        }
    }

    private class AddUserTask extends VoidAsyncTask {

        private final String userName;

        private AddUserTask(String userName) {
            this.userName = userName;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Util.addUser(userName);
            return null;
        }
    }

    private class SyncDataTask extends VoidAsyncTask {

        private final String userName;
        private final OnSyncFinishedListener listener;

        private SyncDataTask(String userName, OnSyncFinishedListener listener) {
            this.userName = userName;
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            isSyncing = true;


            List<TaskEntity> newTaskEntityData = new ArrayList<>(taskEntityData);
            Map<TaskEntity, List<String>> newTags = new HashMap<>();

            Util.addUser(userName);

            for (TaskEntity taskEntity : newTaskEntityData) {
                try {
                    Util.updateTask(taskEntity);
                    newTags.put(taskEntity, Util.getAllTagsForTask(taskEntity.getId()));
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
            try {
                newTaskEntityData = Util.getAllTasksForUser(userName);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }

            if (newTaskEntityData != null) {
                taskEntityData = newTaskEntityData;
                tags = newTags;
            }

            isSyncing = false;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (listener != null) {
                listener.onSyncFinished();
            }
        }
    }
}
