package com.example.mobileticketapp.utils;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.mobileticketapp.R;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "movie_ticket_channel";
    public static final String EXTRA_MOVIE_TITLE = "movie_title";
    public static final String EXTRA_THEATER_NAME = "theater_name";
    public static final String EXTRA_START_TIME = "start_time";

    @Override
    public void onReceive(Context context, Intent intent) {
        String movieTitle = intent.getStringExtra(EXTRA_MOVIE_TITLE);
        String theaterName = intent.getStringExtra(EXTRA_THEATER_NAME);
        String startTime = intent.getStringExtra(EXTRA_START_TIME);

        createNotificationChannel(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("🎬 Sắp đến giờ chiếu!")
                .setContentText(movieTitle + " lúc " + startTime + " tại " + theaterName)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Phim \"" + movieTitle + "\" sẽ chiếu lúc " + startTime
                                + " tại " + theaterName + ". Đừng quên đến đúng giờ nhé!"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Movie Ticket Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Thông báo nhắc nhở giờ chiếu phim");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    /**
     * Thông báo nhắc giờ chiếu - luôn bắn sau 5 giây kể từ khi đặt vé xong
     */
    public static void scheduleNotification(Context context, String movieTitle,
                                             String theaterName, String startTime,
                                             long showtimeMillis) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(EXTRA_MOVIE_TITLE, movieTitle);
        intent.putExtra(EXTRA_THEATER_NAME, theaterName);
        intent.putExtra(EXTRA_START_TIME, startTime);

        int requestCode = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Luôn bắn thông báo sau 5 giây kể từ lúc đặt vé
        long triggerAtMillis = System.currentTimeMillis() + 5000;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        }
    }
}
