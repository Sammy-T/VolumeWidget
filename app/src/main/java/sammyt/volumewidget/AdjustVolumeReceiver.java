package sammyt.volumewidget;

import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

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

    public static final String MEDIA_VOL_STORE = "media_vol_store";
    public static final String RING_VOLUME_STORE = "ring_volume_store";
    public static final String ALARM_VOLUME_STORE = "alarm_volume_store";
    public static final String NOTIFICATION_VOLUME_STORE = "notification_volume_store";
    public static final String SYSTEM_VOLUME_STORE = "system_volume_store";

    @Override
    public void onReceive(Context context, Intent intent) {

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

        boolean changeVolume = true; // Default to true for non-ringer changes

        // If we're muting the ringer, check if we have the appropriate permission
        if(audioStream == AudioManager.STREAM_RING && setVolume == 0){

            changeVolume = hasDoNotDisturb(context);

            if(!changeVolume){
                String muteWarning = context.getString(R.string.mute_permission_warning);

                Toast.makeText(context, muteWarning, Toast.LENGTH_SHORT).show();
                Log.w(LOG_TAG, muteWarning);

                requestDoNotDisturb(context);
            }
        }

        if(changeVolume){
            audioManager.setStreamVolume(audioStream, setVolume, AudioManager.FLAG_PLAY_SOUND);
            Log.d(LOG_TAG, "Volume Type: " + audioStream + " Level: " + setVolume);
        }

        // Request an update to the App Widget (ListView Collection version)
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] expIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, VolumeWidget.class));
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

    // Returns whether or not we have permission to mute the ringer
    private boolean hasDoNotDisturb(Context context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            NotificationManager notifyManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            return notifyManager.isNotificationPolicyAccessGranted();
        }else{
            return true; // default to true if the API level is too low for this permission request
        }
    }

    private void requestDoNotDisturb(Context context){
        Intent requestIntent = new Intent();
        requestIntent.setAction(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

        context.startActivity(requestIntent);
    }
}
