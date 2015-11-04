package org.vmse.spbau.tobedone.connection;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.vmse.spbau.tobedone.connection.model.Task;

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

    private final Object syncMonitor = new Object();

    private final Context context;

    private boolean isSyncing = false;
    private List<Task> taskData;

    private TaskDataWrapper(Context context) {
        this.context = context;
    }

    public List<Task> getTaskData() {
        return taskData;
    }

    public TaskDataWrapper newInstance(Context context) {
        if (instance == null) {
            instance = new TaskDataWrapper(context);
        }
        return instance;
    }

    public void addTask(Task task) throws SyncException {
        if (isSyncing) {
            throw new SyncException("Cannot add task while syncing");
        }
        if (Util.isConnected(context)) {
            new AddTaskTask(task).execute();
        }
        taskData.add(task);
    }

    public void getTagsForTask(Task task, TagsListReceiver receiver) {
        new GetTagsTask(task, receiver).execute();
    }

    /**
     * @param task    updated version of task
     * @param oldTask leave it as Null if task was created online and provide it otherwise
     */
    public void updateTask(Task task, Task oldTask) throws SyncException {
        if (isSyncing) {
            throw new SyncException("Cannot update while syncing");
        }
        if (oldTask == null) {
            oldTask = findTaskById(task.getId());
        }
        taskData.remove(oldTask);
        taskData.add(task);
        if (task.getId() != Task.CREATED_OFFLINE && Util.isConnected(context)) {
            new UpdataTaskTask(task).execute();
        }
    }

    private Task findTaskById(long id) {
        for (Task t : taskData) {
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
        for (Task task : taskData) {
            final JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", task.getName());
                jsonObject.put("description", task.getDescription());
                jsonObject.put("priority", task.getPriority());
                jsonObject.put("deadline", task.getDeadline());
                jsonObject.put("breakTime", task.getBreakTime());
                jsonObject.put("elapsedTime", task.getElapsedTime());
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
        final List<Task> result = new ArrayList<>(n);
        for (int i = 0; i < n; ++i) {
            final JSONObject jsonObject = jsonArray.getJSONObject(i);
            result.add(Util.taskFromJson(jsonObject));
        }
        taskData = result;
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

    private class UpdataTaskTask extends VoidAsyncTask {

        private final Task task;

        private UpdataTaskTask(Task task) {
            this.task = task;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Util.updateTask(task);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }
    }

    private class GetTagsTask extends AsyncTask<Void, Void, List<String>> {

        private final Task task;
        private final TagsListReceiver receiver;

        private GetTagsTask(Task task, TagsListReceiver receiver) {
            this.task = task;
            this.receiver = receiver;
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            try {
                return Util.getAllTagsForTask(task.getId());
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

    private class AddTaskTask extends VoidAsyncTask {

        private final Task task;

        private AddTaskTask(Task task) {
            this.task = task;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Util.addTask(task);
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
            synchronized (syncMonitor) {
                isSyncing = true;

                List<Task> newTaskData = new ArrayList<>(taskData);

                for (Task task : newTaskData) {
                    try {
                        Util.updateTask(task);
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
                try {
                    newTaskData = Util.getAllTasksForUser(userName);
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }

                if (newTaskData != null) {
                    taskData = newTaskData;
                }

                isSyncing = false;
            }
            return null;
        }
    }
}
