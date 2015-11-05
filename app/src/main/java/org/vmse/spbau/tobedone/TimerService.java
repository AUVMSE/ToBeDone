package org.vmse.spbau.tobedone;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by roman on 04.11.15.
 */
public class TimerService extends Service {

    private String LOG_TAG = "MY_TAG";
    private long interval = 1000; // 1 second
    private long timeBeforeBreak;
    private long timeElapsed;
    private boolean isFirstTime;

    private Handler mHandler = new Handler();
    private Timer mTimer = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mTimer.cancel();
        mTimer = new Timer();
        interval = intent.getIntExtra("interval", 0) * 1000;
        timeBeforeBreak = intent.getIntExtra("timeBeforeBreak", 0) * 1000;

        if (interval == 0)
            return START_NOT_STICKY;

        isFirstTime = true;
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, interval);
        return START_NOT_STICKY;
    }

    class TimeDisplayTimerTask extends TimerTask {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (isFirstTime) {
                        isFirstTime = false;
                        return;
                    }
                    // display toast
                    Toast.makeText(getApplicationContext(), "now every 5 seconds",
                            Toast.LENGTH_SHORT).show();
                }

            });
        }
    }
}
