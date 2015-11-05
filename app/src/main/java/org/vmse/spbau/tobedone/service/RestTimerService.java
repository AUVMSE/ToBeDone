package org.vmse.spbau.tobedone.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import org.vmse.spbau.tobedone.R;
import org.vmse.spbau.tobedone.activity.MainActivity;
import org.vmse.spbau.tobedone.fragment.TaskInProgressFragment;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Egor Gorbunov on 11/5/15.
 * email: egor-mailbox@ya.ru
 */
public class RestTimerService extends Service {
    private static final String TAG = "RestTimerService";
    public static final String BROADCAST_ACTION = "org.vmse.spbau.tobedone.service";
    public static final int SECOND = 1000;

    public static final String SECONDS_ELAPSED_PARAM = "elapsed_seconds";

    private final Handler handler = new Handler();
    private long elapsedSeconds = 0;
    private long timeBeforeBreak = SECOND * 60;
    private long interval = SECOND;
    Intent intent;

    private final Handler mHandler = new Handler();
    private Timer mTimer = null;

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
        if (mTimer != null)
            mTimer.cancel();
        mTimer = new Timer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        interval = intent.getIntExtra(TaskInProgressFragment.SEC_INTERVAL_PARAM, 0) * SECOND;
        timeBeforeBreak = intent.getIntExtra(TaskInProgressFragment.SEC_BREAK_TIME_PARAM, 0) * SECOND;
        elapsedSeconds = intent.getLongExtra("elapsed", 0);

        handler.removeCallbacks(notificationSender);
        handler.postDelayed(notificationSender, timeBeforeBreak);
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, SECOND);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
        mTimer.cancel();
        handler.removeCallbacks(notificationSender);
        super.onDestroy();
    }

    private Runnable sendSecondsToUi = new Runnable() {
        @Override
        public void run() {
            broadcastSecondsElapsed();
            handler.postDelayed(this, SECOND);
        }
    };

    private Runnable notificationSender = new Runnable() {
        @Override
        public void run() {
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
            sendNotification();
            handler.postDelayed(this, 15 * 1000);
        }
    };

    private void sendNotification() {
        int notificationId = 001;
        // Build intent for notification content
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.edit_tile)
                        .setContentTitle("...")
                        .setContentText("Do not work so much on one task!")
                        .setContentIntent(viewPendingIntent);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    private void broadcastSecondsElapsed() {
        intent.putExtra(SECONDS_ELAPSED_PARAM, String.valueOf(elapsedSeconds++));
        sendBroadcast(intent);
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
////        handler.removeCallbacks(sendSecondsToUi);
//
//    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class TimeDisplayTimerTask extends TimerTask {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    broadcastSecondsElapsed();

                }

            });
        }
    }
    class TimeDisplayNotificationTask extends TimerTask {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    sendNotification();

                }

            });
        }
    }
}
