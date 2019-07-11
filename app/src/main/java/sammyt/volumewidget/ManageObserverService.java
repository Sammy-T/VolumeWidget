package sammyt.volumewidget;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class ManageObserverService extends Service {

    private final String LOG_TAG = this.getClass().getSimpleName();

    private Context mContext;
    private SettingsObserver mSettingsObserver;

    public ManageObserverService() {
    }

    @Override
    public int	onStartCommand(Intent intent, int flags, int startId){

        Log.d(LOG_TAG, "onStartCommand");

        mContext = getApplicationContext();

        // Register the observer
        mSettingsObserver = new SettingsObserver(new Handler(), mContext);
        mContext.getContentResolver()
                .registerContentObserver(Settings.System.CONTENT_URI, true,
                        mSettingsObserver);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){

        Log.d(LOG_TAG, "onDestroy");

        // Unregister the observer
        mContext.getContentResolver().unregisterContentObserver(mSettingsObserver);

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
