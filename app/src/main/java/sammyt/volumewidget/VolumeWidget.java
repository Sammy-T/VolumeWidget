package sammyt.volumewidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class VolumeWidget extends AppWidgetProvider {

    private static final String LOG_TAG = "VolumeWidget";

    private static AudioManager mAudioManager;
    //private static Intent mIntent;

    private static final int VOLUME_UP = 0;
    private static final int VOLUME_DOWN = 1;
    private static final int VOLUME_TOGGLE = 2;

    private static final int MEDIA_VOLUME = 3;
    private static final int RING_VOLUME = 4;
    private static final int ALARM_VOLUME = 5;
    private static final int NOTIFICATION_VOLUME = 6;
    private static final int SYSTEM_VOLUME = 7;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.volume_widget);
        //views.setTextViewText(R.id.appwidget_text, widgetText);

        if(mAudioManager == null) {
            mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }

        // Update the Ringer volume UI
        views.setImageViewResource(R.id.ring_toggle, getImageRes(RING_VOLUME));
        views.setOnClickPendingIntent(R.id.ring_toggle, createVolumePendingIntent(context,
                RING_VOLUME, VOLUME_TOGGLE));
        views.setProgressBar(R.id.ring_volume, getMaxStreamValue(RING_VOLUME), getStreamValue(RING_VOLUME),
                false);
        views.setOnClickPendingIntent(R.id.ring_increase, createVolumePendingIntent(context,
                RING_VOLUME, VOLUME_UP));
        views.setOnClickPendingIntent(R.id.ring_decrease, createVolumePendingIntent(context,
                RING_VOLUME, VOLUME_DOWN));

        // Update the Media volume UI
        views.setImageViewResource(R.id.media_toggle, getImageRes(MEDIA_VOLUME));
        views.setOnClickPendingIntent(R.id.media_toggle, createVolumePendingIntent(context,
                MEDIA_VOLUME, VOLUME_TOGGLE));
        views.setProgressBar(R.id.media_volume, getMaxStreamValue(MEDIA_VOLUME), getStreamValue(MEDIA_VOLUME),
                false);
        views.setOnClickPendingIntent(R.id.media_increase, createVolumePendingIntent(context,
                MEDIA_VOLUME, VOLUME_UP));
        views.setOnClickPendingIntent(R.id.media_decrease, createVolumePendingIntent(context,
                MEDIA_VOLUME, VOLUME_DOWN));

        // Update the Notification volume UI
        views.setImageViewResource(R.id.notification_toggle, getImageRes(NOTIFICATION_VOLUME));
        views.setOnClickPendingIntent(R.id.notification_toggle, createVolumePendingIntent(context,
                NOTIFICATION_VOLUME, VOLUME_TOGGLE));
        views.setProgressBar(R.id.notification_volume, getMaxStreamValue(NOTIFICATION_VOLUME),
                getStreamValue(NOTIFICATION_VOLUME), false);
        views.setOnClickPendingIntent(R.id.notification_increase, createVolumePendingIntent(context,
                NOTIFICATION_VOLUME, VOLUME_UP));
        views.setOnClickPendingIntent(R.id.notification_decrease, createVolumePendingIntent(context,
                NOTIFICATION_VOLUME, VOLUME_DOWN));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Log.d(LOG_TAG, "onUpdate");

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

        // Start the observer service
//        if(mIntent == null) {
//            mIntent = new Intent(context, ManageObserverService.class);
//            context.startService(mIntent);
//        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        // Start the observer service
//        if(mIntent == null) {
//            mIntent = new Intent(context, ManageObserverService.class);
//            context.startService(mIntent);
//        }
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        // Stop the observer service
//        if(mIntent != null) {
//            context.stopService(mIntent);
//            mIntent = null;
//        }
    }

    private static int getImageRes(int volumeType){

        int currentVolume = getStreamValue(volumeType);
        int res = R.drawable.baseline_keyboard_arrow_down_white_24;

        switch(volumeType){
            case MEDIA_VOLUME:
                if(currentVolume == 0) {
                    res = R.drawable.baseline_music_off_white_24;
                }else{
                    res = R.drawable.baseline_audiotrack_white_24;
                }
                break;
            case RING_VOLUME:
                if(currentVolume == 0) {
                    res = R.drawable.baseline_volume_off_white_24;
                }else{
                    res = R.drawable.baseline_volume_up_white_24;
                }
                break;
            case NOTIFICATION_VOLUME:
                if(currentVolume == 0) {
                    res = R.drawable.ic_bell_off_white_24dp;
                }else{
                    res = R.drawable.ic_bell_white_24dp;
                }
                break;
        }

        return res;
    }

    private static int getMaxStreamValue(int volumeType){
        int stream = AudioManager.STREAM_MUSIC;

        switch(volumeType){
            case MEDIA_VOLUME:
                stream = AudioManager.STREAM_MUSIC;
                break;
            case RING_VOLUME:
                stream = AudioManager.STREAM_RING;
                break;
            case NOTIFICATION_VOLUME:
                stream = AudioManager.STREAM_NOTIFICATION;
                break;
            case ALARM_VOLUME:
                stream = AudioManager.STREAM_ALARM;
                break;
            case SYSTEM_VOLUME:
                stream = AudioManager.STREAM_SYSTEM;
                break;
        }
        return mAudioManager.getStreamMaxVolume(stream);
    }

    private static int getStreamValue(int volumeType){
        int stream = AudioManager.STREAM_MUSIC;

        switch(volumeType){
            case MEDIA_VOLUME:
                stream = AudioManager.STREAM_MUSIC;
                break;
            case RING_VOLUME:
                stream = AudioManager.STREAM_RING;
                break;
            case NOTIFICATION_VOLUME:
                stream = AudioManager.STREAM_NOTIFICATION;
                break;
            case ALARM_VOLUME:
                stream = AudioManager.STREAM_ALARM;
                break;
            case SYSTEM_VOLUME:
                stream = AudioManager.STREAM_SYSTEM;
                break;
        }
        return mAudioManager.getStreamVolume(stream);
    }

    private static PendingIntent createVolumePendingIntent(Context context, int volumeType,
                                                           int volumeDirection){

        Log.d(LOG_TAG, "vol pending intent");

        String volumeStreamType = "";
        String volumeStreamDirection = "";

        // Check which volume stream is being adjusted
        switch(volumeType){
            case MEDIA_VOLUME:
                volumeStreamType = AdjustVolumeReceiver.MEDIA_VOLUME;
                break;

            case RING_VOLUME:
                volumeStreamType = AdjustVolumeReceiver.RING_VOLUME;
                break;

            case NOTIFICATION_VOLUME:
                volumeStreamType = AdjustVolumeReceiver.NOTIFICATION_VOLUME;
        }

        // Check which direction the volume is being adjusted
        switch(volumeDirection){
            case VOLUME_UP:
                volumeStreamDirection = AdjustVolumeReceiver.VOLUME_INCREASE;
                break;
            case VOLUME_DOWN:
                volumeStreamDirection = AdjustVolumeReceiver.VOLUME_DECREASE;
                break;
            case VOLUME_TOGGLE:
                volumeStreamDirection = AdjustVolumeReceiver.VOLUME_TOGGLE;
                break;
        }

        // Build request code
        int[] code = {volumeType, volumeDirection};
        StringBuilder builder = new StringBuilder();

        for (int digit: code){
            builder.append(digit);
        }

        int requestCode = Integer.parseInt(builder.toString());

        // Create intent to adjust volume
        Intent intent = new Intent(context, AdjustVolumeReceiver.class);
        intent.putExtra(AdjustVolumeReceiver.VOLUME_TYPE_EXTRA, volumeStreamType);
        intent.putExtra(AdjustVolumeReceiver.VOLUME_DIRECTION, volumeStreamDirection);

        return PendingIntent.getBroadcast(context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}

