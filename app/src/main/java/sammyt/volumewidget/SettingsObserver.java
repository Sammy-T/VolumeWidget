package sammyt.volumewidget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

public class SettingsObserver extends ContentObserver {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private Context mContext;

    public SettingsObserver(Handler handler, Context context){
        super(handler);
        mContext = context;
    }

    @Override
    public boolean deliverSelfNotifications(){
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange, Uri uri){
        super.onChange(selfChange);
        Log.d(LOG_TAG, "A Setting has changed");
        Log.d(LOG_TAG, uri.toString());

        Intent intent = new Intent(mContext, VolumeWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int ids[] = AppWidgetManager.getInstance(mContext)
                .getAppWidgetIds(new ComponentName(mContext, VolumeWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        mContext.sendBroadcast(intent);
    }
}
