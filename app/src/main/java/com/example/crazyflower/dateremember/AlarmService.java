package com.example.crazyflower.dateremember;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class AlarmService extends Service {

    public static boolean isRunning = false;

    private static final String TAG = "AlarmService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onStart: ");
        isRunning = true;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flag, int startId) {
        Log.d(TAG, "onStartCommand: ");

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                List<FutureEvent> list = DataInMemory.getInstance().getFutureEvents();
//                long now = System.currentTimeMillis() / 86400000;
//                for (FutureEvent futureEvent : list) {
//                    if (futureEvent.getCalendar().getTimeInMillis() / 86400000 == now) {
//                        NotificationCompat.Builder builder = new NotificationCompat.Builder(AlarmService.this);
//                        builder.setSmallIcon(R.mipmap.ic_launcher);
//                        builder.setContentTitle(futureEvent.getNote());
//                        builder.setContentText("还有" + RemindDaysList.getItemByIndex(futureEvent.getRemindDayIndex()).getRemindDays() + "天");
//                        Intent shareActivityIntent = new Intent(AlarmService.this, ShareActivity.class);
//                        shareActivityIntent.putExtra("event_id", futureEvent.getId());
//                        PendingIntent pendingIntent = PendingIntent.getActivity(AlarmService.this, 0, shareActivityIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//                        builder.setContentIntent(pendingIntent);
//                        builder.setAutoCancel(true);
//                        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                        manager.notify((int) futureEvent.getId(), builder.build());
//                    }
//                }
//            }
//        }).start();

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmReceiver = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmReceiver, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis() / 86400000 + 1) * 86400000, pendingIntent);

        return START_STICKY;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

}
