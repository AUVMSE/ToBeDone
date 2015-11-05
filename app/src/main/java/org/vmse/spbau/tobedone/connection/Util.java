package org.vmse.spbau.tobedone.connection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.vmse.spbau.tobedone.connection.model.TaskEntity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author antonpp
 * @since 04/11/15
 */
public class Util {

    public static final String TAG = Util.class.getName();
    private static final String IP = "192.168.65.143";
    private static final String SERVER_ADDRESS = "http://" + IP + ":8080/";
    private static final String TAGS_API_ADDRESS = SERVER_ADDRESS + "api/tags";
    private static final String USERS_API_ADDRESS = SERVER_ADDRESS + "api/users";
    private static final String TASKS_API_ADDRESS = SERVER_ADDRESS + "api/tasks";
    private static final String TASKTAGS_API_ADDRESS = SERVER_ADDRESS + "api/tasktags";

    private Util() {
        throw new UnsupportedOperationException();
    }

    public static String getResponse(HttpURLConnection httpURLConnection) {
        final StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
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
        return stringBuilder.toString();
    }

    private static String getJSONStringFromUrl(String url) {
        HttpURLConnection httpURLConnection = null;
        String jsonString = null;

        try {
            final URL u = new URL(url);
            httpURLConnection = (HttpURLConnection) u.openConnection();
            httpURLConnection.setRequestMethod("GET");
            jsonString = getResponse(httpURLConnection);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return jsonString;
    }

    public static List<String> getAllTags() throws JSONException {
        final JSONArray jsonArray = new JSONArray(getJSONStringFromUrl(TAGS_API_ADDRESS));
        final int n = jsonArray.length();
        final List<String> result = new ArrayList<>(n);
        for (int i = 0; i < n; ++i) {
            result.add(jsonArray.getJSONObject(i).getString("name"));
        }
        return result;
    }

    public static List<String> getAllTagsForTask(long taskId) throws JSONException {
        final JSONArray jsonArray = new JSONArray(getJSONStringFromUrl(TAGS_API_ADDRESS + "?id_task=" + taskId));
        final int n = jsonArray.length();
        final List<String> result = new ArrayList<>(n);
        for (int i = 0; i < n; ++i) {
            result.add(jsonArray.getJSONObject(i).getString("name"));
        }
        return result;
    }

    private static String sendPOST(String url, List<NameValuePair> params) {
        HttpURLConnection httpURLConnection = null;
        String result = null;

        try {
            final URL u = new URL(url);
            httpURLConnection = (HttpURLConnection) u.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            final OutputStream os = httpURLConnection.getOutputStream();
            final BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();

            result = getResponse(httpURLConnection);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return result;
    }

    private static String sendDELETE(String url, List<NameValuePair> params) {
        HttpURLConnection httpURLConnection = null;
        String result = null;

        try {
            final URL u = new URL(url);
            httpURLConnection = (HttpURLConnection) u.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("DELETE");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            final OutputStream os = httpURLConnection.getOutputStream();
            final BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();

            result = getResponse(httpURLConnection);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return result;
    }

    private static String sendPUT(String url, List<NameValuePair> params) {
        HttpURLConnection httpURLConnection = null;
        String jsonString = null;

        try {
            final URL u = new URL(url);
            httpURLConnection = (HttpURLConnection) u.openConnection();
            httpURLConnection.setRequestMethod("PUT");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            final OutputStream os = httpURLConnection.getOutputStream();
            final BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();

            jsonString = getResponse(httpURLConnection);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return jsonString;
    }

    /**
     * @return user id
     */
    public static Long addUser(String name)  {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new NameValuePair("name", name));
        String jsonUserId = sendPOST(USERS_API_ADDRESS, params);

        JSONObject jsonObject = null;
        Long userId = null;
        /*temporary*/
        if (jsonUserId == null)
            return -1l;
        try {
            jsonObject = new JSONObject(jsonUserId);
            userId = jsonObject.getLong("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userId;
    }

    private static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
        final StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public static List<String> getAllUsers() throws JSONException {
        final JSONArray jsonArray = new JSONArray(getJSONStringFromUrl(USERS_API_ADDRESS));
        final int n = jsonArray.length();
        final List<String> result = new ArrayList<>(n);
        for (int i = 0; i < n; ++i) {
            result.add(jsonArray.getJSONObject(i).getString("name"));
        }
        return result;
    }

    public static List<TaskEntity> getAllTasksForUser(String name) throws JSONException {
        final JSONArray jsonArray = new JSONArray(getJSONStringFromUrl(TASKS_API_ADDRESS + "?name=" + name));
        final int n = jsonArray.length();
        final List<TaskEntity> result = new ArrayList<>(n);
        for (int i = 0; i < n; ++i) {
            final JSONObject jsonObject = jsonArray.getJSONObject(i);
            result.add(taskFromJson(jsonObject));
        }
        return result;
    }

    public static String addTask(TaskEntity taskEntity, List<String> tags) {
        List<NameValuePair> params = new ArrayList<>();
        addParam(params, "id_user", taskEntity.getIdUser());
        addParam(params, "name", taskEntity.getName());
        addParam(params, "description", taskEntity.getDescription());
        addParam(params, "priority", taskEntity.getPriority());
        addParam(params, "deadline", taskEntity.getDeadline());
        addParam(params, "isSolved", taskEntity.isSolved());
        addParam(params, "breakTime", taskEntity.getBreakTime());
        addParam(params, "elapsedTime", taskEntity.getElapsedTime());
        addParam(params, "lastStop", taskEntity.getLastStop());
        for (String tag : tags) {
            addParam(params, "tags", tag);
        }
        Log.d("TEST", "UTIL::addTask");
        return sendPOST(TASKS_API_ADDRESS, params);
    }

    private static void addParam(List<NameValuePair> params, String key, Object value) {
        if (value != null) {
            params.add(new NameValuePair(key, value.toString()));
        }
    }

    public static void updateTask(TaskEntity taskEntity) throws JSONException {
        List<NameValuePair> params = new ArrayList<>();
        addParam(params, "id", taskEntity.getId());
        addParam(params, "id_user", taskEntity.getIdUser());
        addParam(params, "name", taskEntity.getName());
        addParam(params, "description", taskEntity.getDescription());
        addParam(params, "priority", taskEntity.getPriority());
        addParam(params, "deadline", taskEntity.getDeadline());
        addParam(params, "breakTime", taskEntity.getBreakTime());
        addParam(params, "isSolved", taskEntity.isSolved());
        addParam(params, "elapsedTime", taskEntity.getElapsedTime());
        addParam(params, "lastStop", taskEntity.getLastStop());
        final JSONObject jsonObject = new JSONObject(sendPUT(TASKS_API_ADDRESS, params));
        taskEntity.setId(jsonObject.getLong("id"));
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static TaskEntity taskFromJson(JSONObject jsonObject) throws JSONException {
        final TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(jsonObject.getLong("id"));
        taskEntity.setIdUser(jsonObject.getLong("id_user"));
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

    public static void removeTags(long taskId) {
        List<NameValuePair> params = new ArrayList<>();
        addParam(params, "id_task", taskId);
        sendDELETE(TASKTAGS_API_ADDRESS, params);
    }

    public static void updateTags(long taskId, List<String> tags) {
        removeTags(taskId);
        for (String tag : tags) {
            List<NameValuePair> params = new ArrayList<>();
            addParam(params, "id_task", taskId);
            addParam(params, "tag", tag);
            sendPOST(TASKTAGS_API_ADDRESS, params);
        }
    }

    private static class NameValuePair {
        private final String name;
        private final String value;

        public NameValuePair(String name, String value) {

            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }
}
