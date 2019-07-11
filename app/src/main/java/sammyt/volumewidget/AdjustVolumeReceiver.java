package sammyt.volumewidget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.util.Log;

import sammyt.volumewidget.volumewidget_experimental.VolumeExpWidget;

public class AdjustVolumeReceiver extends BroadcastReceiver {

    private final String LOG_TAG = this.getClass().getSimpleName();

    public static final String VOLUME_TYPE_EXTRA = "volume_type_extra";

    public static final String MEDIA_VOLUME = "media_volume";
    public static final String RING_VOLUME = "ring_volume";
    public static final String ALARM_VOLUME = "alarm_volume";
    public static final String NOTIFICATION_VOLUME = "notification_volume";
    public static final String SYSTEM_VOLUME = "system_volume";

    public static final String VOLUME_DIRECTION = "volume_direction";

    public static final String VOLUME_INCREASE = "volume_increase";
    public static final String VOLUME_DECREASE = "volume_decrease";
    public static final String VOLUME_TOGGLE = "volume_toggle";

    private static final String MEDIA_VOL_STORE = "media_vol_store";
    public static final String RING_VOLUME_STORE = "ring_volume_store";
    public static final String ALARM_VOLUME_STORE = "alarm_volume_store";
    public static final String NOTIFICATION_VOLUME_STORE = "notification_volume_store";
    public static final String SYSTEM_VOLUME_STORE = "system_volume_store";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(LOG_TAG, "vol receiver");

        String volumeType = intent.getStringExtra(VOLUME_TYPE_EXTRA);
        String volumeDirection = intent.getStringExtra(VOLUME_DIRECTION);

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        int audioStream = 3;

        switch(volumeType){
            case MEDIA_VOLUME:
                audioStream = AudioManager.STREAM_MUSIC;
                break;
            case RING_VOLUME:
                audioStream = AudioManager.STREAM_RING;
                break;
            case ALARM_VOLUME:
                audioStream = AudioManager.STREAM_ALARM;
                break;
            case NOTIFICATION_VOLUME:
                audioStream = AudioManager.STREAM_NOTIFICATION;
                break;
            case SYSTEM_VOLUME:
                audioStream = AudioManager.STREAM_SYSTEM;
                break;
        }

        int maxVolume = audioManager.getStreamMaxVolume(audioStream);
        int currentVolume = audioManager.getStreamVolume(audioStream);

        int setVolume = currentVolume;

        switch(volumeDirection){
            case VOLUME_INCREASE:
                if(currentVolume < maxVolume){
                    setVolume = currentVolume + 1;
                }else{
                    Log.w(LOG_TAG, "Unable to increase volume");
                }
                break;

            case VOLUME_DECREASE:
                if(currentVolume > 0){
                    setVolume = currentVolume - 1;
                }else{
                    Log.w(LOG_TAG, "Unable to decrease volume");
                }
                break;

            case VOLUME_TOGGLE:
                setVolume = toggleVolume(context, volumeType, maxVolume, currentVolume);
                break;
        }

        audioManager.setStreamVolume(audioStream, setVolume, AudioManager.FLAG_PLAY_SOUND);

        // Request an update to the App Widget
        Intent widgetIntent = new Intent(context, VolumeWidget.class);
        widgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(new ComponentName(context, VolumeWidget.class));
        widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(widgetIntent);

        // Request an update to the App Widget (ListView Collection version)
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] expIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, VolumeExpWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(expIds, R.id.widget_listview);
    }

    private int toggleVolume(Context context, String volumeType, int maxVolume, int currentVolume){

        int toggledVolume;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String volumeStoreKey = MEDIA_VOL_STORE;

        switch(volumeType){
            case MEDIA_VOLUME:
                volumeStoreKey = MEDIA_VOL_STORE;
                break;

            case RING_VOLUME:
                volumeStoreKey = RING_VOLUME_STORE;
                break;

            case ALARM_VOLUME:
                volumeStoreKey = ALARM_VOLUME_STORE;
                break;

            case NOTIFICATION_VOLUME:
                volumeStoreKey = NOTIFICATION_VOLUME_STORE;
                break;

            case SYSTEM_VOLUME:
                volumeStoreKey = SYSTEM_VOLUME_STORE;
                break;
        }

        if(currentVolume == 0){
            // Restore the volume
            toggledVolume = prefs.getInt(volumeStoreKey, maxVolume / 2);

        }else{
            // Store the volume
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(volumeStoreKey, currentVolume);
            editor.apply();

            // Then mute
            toggledVolume = 0;
        }

        return toggledVolume;
    }
}