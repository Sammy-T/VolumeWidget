package sammyt.volumewidget.volumewidget_experimental;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import sammyt.volumewidget.AdjustVolumeReceiver;
import sammyt.volumewidget.R;

public class VolumeWidgetService extends RemoteViewsService {

    private final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent){
        return new VolumeRemoteViewsFactory(this.getApplicationContext());
    }
}

class VolumeRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory{

    private final String LOG_TAG = this.getClass().getSimpleName();

    private Context mContext;
    private static AudioManager mAudioManager;
    private ArrayList<Integer> mVolumeItems = new ArrayList<>();

    private static final int MEDIA_VOLUME = 3;
    private static final int RING_VOLUME = 4;
    private static final int ALARM_VOLUME = 5;
    private static final int NOTIFICATION_VOLUME = 6;
    private static final int SYSTEM_VOLUME = 7;

    public VolumeRemoteViewsFactory(Context context){
        mContext = context;
    }

    public void onCreate(){
        // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.

        mVolumeItems.add(RING_VOLUME);
        mVolumeItems.add(MEDIA_VOLUME);
        mVolumeItems.add(NOTIFICATION_VOLUME);
    }

    public void onDestroy() {
        // In onDestroy() you should tear down anything that was setup for your data source,
        // eg. cursors, connections, etc.
        mVolumeItems.clear();
    }

    public int getCount() {
        return mVolumeItems.size();
    }

    public RemoteViews getViewAt(int position){

        int audioType = mVolumeItems.get(position);

        if(mAudioManager == null) {
            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        }

        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.volume_exp_widget_item);

        // Build the views appropriately here (change images, etc)
        rv.setImageViewResource(R.id.widget_item_toggle, getImageRes(audioType));
        rv.setProgressBar(R.id.widget_item_volume, getMaxStreamValue(audioType), getStreamValue(audioType),
                false);

        // Set the fill-in intents on the item
        Intent decreaseFillIntent = new Intent();
        decreaseFillIntent.putExtra(AdjustVolumeReceiver.VOLUME_TYPE_EXTRA, getVolumeType(audioType));
        decreaseFillIntent.putExtra(AdjustVolumeReceiver.VOLUME_DIRECTION, AdjustVolumeReceiver.VOLUME_DECREASE);

        Intent increaseFillIntent = new Intent();
        increaseFillIntent.putExtra(AdjustVolumeReceiver.VOLUME_TYPE_EXTRA, getVolumeType(audioType));
        increaseFillIntent.putExtra(AdjustVolumeReceiver.VOLUME_DIRECTION, AdjustVolumeReceiver.VOLUME_INCREASE);

        Intent toggleFillIntent = new Intent();
        toggleFillIntent.putExtra(AdjustVolumeReceiver.VOLUME_TYPE_EXTRA, getVolumeType(audioType));
        toggleFillIntent.putExtra(AdjustVolumeReceiver.VOLUME_DIRECTION, AdjustVolumeReceiver.VOLUME_TOGGLE);

        rv.setOnClickFillInIntent(R.id.widget_item_decrease, decreaseFillIntent);
        rv.setOnClickFillInIntent(R.id.widget_item_increase, increaseFillIntent);
        rv.setOnClickFillInIntent(R.id.widget_item_toggle, toggleFillIntent);

        return rv;
    }

    public RemoteViews getLoadingView() {
        // You can create a custom loading view (for instance when getViewAt() is slow.) If you
        // return null here, you will get the default loading view.
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
        // This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
        // on the collection view corresponding to this factory. You can do heaving lifting in
        // here, synchronously. For example, if you need to process an image, fetch something
        // from the network, etc., it is ok to do it here, synchronously. The widget will remain
        // in its current state while work is being done here, so you don't need to worry about
        // locking up the widget.
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

    private static String getVolumeType(int volumeType){
        String volumeStreamType = "";

        switch(volumeType){
            case MEDIA_VOLUME:
                volumeStreamType = AdjustVolumeReceiver.MEDIA_VOLUME;
                break;
            case RING_VOLUME:
                volumeStreamType = AdjustVolumeReceiver.RING_VOLUME;
                break;
            case NOTIFICATION_VOLUME:
                volumeStreamType = AdjustVolumeReceiver.NOTIFICATION_VOLUME;
                break;
        }

        return volumeStreamType;
    }
}