package org.vmse.spbau.tobedone;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.util.Log;

import org.json.JSONException;
import org.vmse.spbau.tobedone.connection.TaskDataWrapper;

/**
 * @author antonpp
 * @since 04/11/15
 */
public class MainApplication extends Application {

    private static final String TAG = MainApplication.class.getName();
    private static TaskDataWrapper taskDataWrapper;

    public static TaskDataWrapper getTaskDataWrapper() {
        return taskDataWrapper;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final Account[] accounts = AccountManager.get(getApplicationContext()).
                getAccountsByType("com.google");
        if (accounts.length != 0) {
            taskDataWrapper = TaskDataWrapper.getInstance(accounts[0].name, this);
        } else {
            taskDataWrapper = TaskDataWrapper.getInstance("emulator", this);
        }
        try {
            taskDataWrapper.loadState();
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
