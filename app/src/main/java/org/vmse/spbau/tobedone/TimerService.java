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
    // constant
    private long interval = 10 * 1000; // 10 seconds
    private boolean isFirstTime;

    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
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
        if (interval == 0)
            return START_NOT_STICKY;
        isFirstTime = true;
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, interval);
        return START_NOT_STICKY;
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {

                    if (isFirstTime) {
                        isFirstTime = false;
                        return;
                    }

                    if (interval != 3 * 1000) {
                        Toast.makeText(getApplicationContext(), "" + interval / 1000 + " seconds past",
                                Toast.LENGTH_SHORT).show();

                        getApplicationContext().startService(new Intent(getApplication(),
                                TimerService.class).putExtra("interval", 5));

                    }

                    // display toast
                    Toast.makeText(getApplicationContext(), "now every 5 seconds",
                            Toast.LENGTH_SHORT).show();
                }

            });
        }

        private String getDateTime() {
            // get date time in custom format
            SimpleDateFormat sdf = new SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss]");
            return sdf.format(new Date());
        }
    }
}
