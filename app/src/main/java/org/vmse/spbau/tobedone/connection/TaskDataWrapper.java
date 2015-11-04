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

    private final Context context;

    private boolean isSyncing = false;
    private List<TaskEntity> taskEntityData;

    private TaskDataWrapper(Context context) {
        this.context = context;
    }

    public List<TaskEntity> getTaskEntityData() {
        return taskEntityData;
    }

    public static TaskDataWrapper getInstance(Context context) {
        if (instance == null) {
            instance = new TaskDataWrapper(context);
        }
        return instance;
    }

    public void addTask(TaskEntity taskEntity) throws SyncException {
        if (isSyncing) {
            throw new SyncException("Cannot add task while syncing");
        }
        if (Util.isConnected(context)) {
            new AddTaskEntityTask(taskEntity).execute();
        }
        taskEntityData.add(taskEntity);
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
            new UpdataTaskEntityTask(taskEntity).execute();
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

    public void syncData(String userName) throws JSONException {
        if (Util.isConnected(context)) {
            new SyncDataTask(userName).execute();
        }
    }

    public void saveState() {
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

    public void loadState() throws FileNotFoundException, JSONException {
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
    }

    public interface TagsListReceiver {
        void onTagsListReceived(List<String> tags);
    }

    private static class SyncException extends Exception {
        public SyncException() {
        }

        public SyncException(String detailMessage) {
            super(detailMessage);
        }
    }

    private abstract class VoidAsyncTask extends AsyncTask<Void, Void, Void> {
    }

    private class UpdataTaskEntityTask extends VoidAsyncTask {

        private final TaskEntity taskEntity;

        private UpdataTaskEntityTask(TaskEntity taskEntity) {
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

        private AddTaskEntityTask(TaskEntity taskEntity) {
            this.taskEntity = taskEntity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Util.addTask(taskEntity);
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

        private SyncDataTask(String userName) {
            this.userName = userName;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            isSyncing = true;

            List<TaskEntity> newTaskEntityData = new ArrayList<>(taskEntityData);

            for (TaskEntity taskEntity : newTaskEntityData) {
                try {
                    Util.updateTask(taskEntity);
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
            }

            isSyncing = false;
            return null;
        }
    }
}
