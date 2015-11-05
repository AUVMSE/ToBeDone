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
    private static final String IP = "192.168.65.245";
    private static final String SERVER_ADDRESS = "http://" + IP + ":8080/";
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
        Log.d("RESPONSE", stringBuilder.toString());
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

    public static List<TaskEntity> getAllTasksForUser(String username) throws JSONException {
        final JSONArray jsonArray = new JSONArray(getJSONStringFromUrl(TASKS_API_ADDRESS + "?username=" + username));
        final int n = jsonArray.length();
        final List<TaskEntity> result = new ArrayList<>(n);
        for (int i = 0; i < n; ++i) {
            final JSONObject jsonObject = jsonArray.getJSONObject(i);
            result.add(TaskEntity.taskFromJson(jsonObject));
        }
        return result;
    }

    public static String addTask(TaskEntity taskEntity) {
        List<NameValuePair> params = new ArrayList<>();
        addParam(params, "taskname", taskEntity.getTaskname());
        addParam(params, "username", taskEntity.getUsername());
        addParam(params, "description", taskEntity.getDescription());
        addParam(params, "priority", taskEntity.getPriority());
        addParam(params, "deadline", taskEntity.getDeadline());
        addParam(params, "isSolved", taskEntity.isSolved());
        addParam(params, "breakTime", taskEntity.getBreakTime());
        addParam(params, "elapsedTime", taskEntity.getElapsedTime());
        addParam(params, "lastStop", taskEntity.getLastStop());
        for (String tag : taskEntity.getTags()) {
            addParam(params, "tags", tag);
        }
        return sendPOST(TASKS_API_ADDRESS, params);
    }

    private static void addParam(List<NameValuePair> params, String key, Object value) {
        if (value != null) {
            params.add(new NameValuePair(key, value.toString()));
        }
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void sendTasks(List<TaskEntity> taskEntityData) {
        for (TaskEntity taskEntity : taskEntityData) {
            Util.addTask(taskEntity);
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
