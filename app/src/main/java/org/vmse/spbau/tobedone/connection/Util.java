package org.vmse.spbau.tobedone.connection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.vmse.spbau.tobedone.connection.model.Task;

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

    private static final String SERVER_ADDRESS = "localhost:8080/";
    private static final String TAGS_API_ADDRESS = SERVER_ADDRESS + "api/tags";
    private static final String USERS_API_ADDRESS = SERVER_ADDRESS + "api/users";
    private static final String TASKS_API_ADDRESS = SERVER_ADDRESS + "api/tasks";

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
        final JSONArray jsonArray = new JSONArray(getJSONStringFromUrl(TAGS_API_ADDRESS + "?id=" + taskId));
        final int n = jsonArray.length();
        final List<String> result = new ArrayList<>(n);
        for (int i = 0; i < n; ++i) {
            result.add(jsonArray.getJSONObject(i).getString("name"));
        }
        return result;
    }

    private static void sendPOST(String url, List<NameValuePair> params) {
        HttpURLConnection httpURLConnection = null;

        try {
            final URL u = new URL(USERS_API_ADDRESS);
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

            httpURLConnection.connect();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }

    private static String sendPUT(String url, List<NameValuePair> params) {
        HttpURLConnection httpURLConnection = null;
        String jsonString = null;

        try {
            final URL u = new URL(USERS_API_ADDRESS);
            httpURLConnection = (HttpURLConnection) u.openConnection();
            httpURLConnection.setRequestMethod("PUT");
            httpURLConnection.setDoOutput(true);

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

    public static void addUser(String name) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new NameValuePair("name", name));
        sendPOST(USERS_API_ADDRESS, params);
    }

    private static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

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

    public static List<Task> getAllTasksForUser(String name) throws JSONException {
        final JSONArray jsonArray = new JSONArray(getJSONStringFromUrl(TASKS_API_ADDRESS + "?name=" + name));
        final int n = jsonArray.length();
        final List<Task> result = new ArrayList<>(n);
        for (int i = 0; i < n; ++i) {
            final JSONObject jsonObject = jsonArray.getJSONObject(i);
            result.add(taskFromJson(jsonObject));
        }
        return result;
    }

    public static void addTask(Task task) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new NameValuePair("id_user", Long.toString(task.getIdUser())));
        params.add(new NameValuePair("name", task.getName()));
        params.add(new NameValuePair("description", task.getDescription()));
        params.add(new NameValuePair("priority", Long.toString(task.getPriority())));
        params.add(new NameValuePair("deadline", task.getDeadline()));
        params.add(new NameValuePair("breakTime", Long.toString(task.getBreakTime())));
        params.add(new NameValuePair("isSolved", Boolean.toString(task.isSolved())));
        params.add(new NameValuePair("elapsedTime", Long.toString(task.getElapsedTime())));
        sendPOST(USERS_API_ADDRESS, params);
    }

    public static void updateTask(Task task) throws JSONException {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new NameValuePair("id", Long.toString(task.getId())));
        params.add(new NameValuePair("id_user", Long.toString(task.getIdUser())));
        params.add(new NameValuePair("name", task.getName()));
        params.add(new NameValuePair("description", task.getDescription()));
        params.add(new NameValuePair("priority", Long.toString(task.getPriority())));
        params.add(new NameValuePair("deadline", task.getDeadline()));
        params.add(new NameValuePair("breakTime", Long.toString(task.getBreakTime())));
        params.add(new NameValuePair("isSolved", Boolean.toString(task.isSolved())));
        params.add(new NameValuePair("elapsedTime", Long.toString(task.getElapsedTime())));
        final JSONObject jsonObject = new JSONObject(sendPUT(USERS_API_ADDRESS, params));
        task.setId(jsonObject.getLong("id"));
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static Task taskFromJson(JSONObject jsonObject) throws JSONException {
        final Task task = new Task();
        task.setId(jsonObject.getLong("id"));
        task.setIdUser(jsonObject.getLong("id_user"));
        task.setName(jsonObject.getString("name"));
        task.setDescription(jsonObject.getString("description"));
        task.setPriority(jsonObject.getLong("priority"));
        task.setDeadline(jsonObject.getString("deadline"));
        task.setBreakTime(jsonObject.getLong("breakTime"));
        task.setIsSolved(jsonObject.getBoolean("isSolved"));
        task.setElapsedTime(jsonObject.getLong("elapsedTime"));
        return task;
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
