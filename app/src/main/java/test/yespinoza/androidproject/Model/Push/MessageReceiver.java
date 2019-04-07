package test.yespinoza.androidproject.Model.Push;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import test.yespinoza.androidproject.View.Activity.MainActivity;
import test.yespinoza.androidproject.R;

public class MessageReceiver extends FirebaseMessagingService {
    public static final String CHANNEL_REGULAR = "APP_REGULAR";
    public static final String CHANNEL_REGULAR_NAME = "Regular Channel";

    public MessageReceiver() {
        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        final String title = getString(R.string.app_name);
        final String description = remoteMessage.getNotification().getBody();

        showNotification(title, description);
    }

    private void showNotification(String title, String description) {
        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(mNotifyManager, description);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_REGULAR)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setContentTitle(title)
                .setContentText(description)
                .setAutoCancel(true)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setContentIntent(resultPendingIntent);
        mNotifyManager.notify(10111, mBuilder.build());

    }

    @TargetApi(26)
    private void createNotificationChannel(NotificationManager notificationManager, String description) {
        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel regularChannel = new NotificationChannel(CHANNEL_REGULAR, CHANNEL_REGULAR_NAME, importance);
        regularChannel.setDescription(description);
        regularChannel.enableLights(true);
        regularChannel.setShowBadge(true);
        regularChannel.setLightColor(R.color.colorPrimary);
        notificationManager.createNotificationChannel(regularChannel);
    }
}
