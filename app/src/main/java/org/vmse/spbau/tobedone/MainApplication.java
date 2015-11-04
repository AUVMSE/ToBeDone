package org.vmse.spbau.tobedone;

import android.app.Application;
import android.util.Log;

import org.json.JSONException;
import org.vmse.spbau.tobedone.connection.TaskDataWrapper;

import java.io.FileNotFoundException;

/**
 * @author antonpp
 * @since 04/11/15
 */
public class MainApplication extends Application {

    private static final String TAG = MainApplication.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            TaskDataWrapper.getInstance(this).loadState();
        } catch (FileNotFoundException | JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onTerminate() {
        try {
            TaskDataWrapper.getInstance(this).saveState();
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        super.onTerminate();
    }
}
