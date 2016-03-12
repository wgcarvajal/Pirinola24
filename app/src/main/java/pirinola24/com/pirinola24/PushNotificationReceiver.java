package pirinola24.com.pirinola24;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.backendless.messaging.PublishOptions;
import com.backendless.push.BackendlessBroadcastReceiver;

/**
 * Created by geovanny on 10/03/16.
 */
public class PushNotificationReceiver extends BackendlessBroadcastReceiver
{
    private static final int NOTIFICATION_ID = 1;

    @Override
    public boolean onMessage(Context context, Intent intent)
    {

        String message = intent.getStringExtra(PublishOptions.MESSAGE_TAG);
        String tickertext = intent.getStringExtra(PublishOptions.ANDROID_TICKER_TEXT_TAG);
        String contentitulo=intent.getStringExtra(PublishOptions.ANDROID_CONTENT_TITLE_TAG );
        String contentext= intent.getStringExtra(PublishOptions.ANDROID_CONTENT_TEXT_TAG);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

//Here you put the Activity that you want to open when you click the Notification

//(and you can pass some Bundle/Extra if you want to add information about the Notification)

        Intent notificationIntent = new Intent(context, NotificacionActivity.class);
        notificationIntent.putExtra("message",message);
        notificationIntent.putExtra("contentitulo",contentitulo);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

//Custom notification

        long[] vibrate = { 0, 100, 200, 300 };
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
                .setTicker(tickertext)
                .setContentTitle(contentitulo)
                .setContentText(contentext)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(vibrate)
                .setSmallIcon(R.mipmap.ic_gorrita);

        notification.setContentIntent(contentIntent);

//Dismiss notification when click on it

        notification.setAutoCancel(true);

        mNotificationManager.notify(NOTIFICATION_ID, notification.build());

// super.onMessage(context, intent);
        return false;
    }
}
