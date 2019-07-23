package sammyt.volumewidget;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class VolumeReceiverTest {

    private final String LOG_TAG = this.getClass().getSimpleName();

    private Context appContext = InstrumentationRegistry.getTargetContext();

    @Test
    public void testMediaVolumeDecrease() {
        Intent testIntent = new Intent(appContext, AdjustVolumeReceiver.class);

        testIntent.putExtra(AdjustVolumeReceiver.VOLUME_TYPE_EXTRA, AdjustVolumeReceiver.MEDIA_VOLUME);
        testIntent.putExtra(AdjustVolumeReceiver.VOLUME_DIRECTION, AdjustVolumeReceiver.VOLUME_DECREASE);

        AudioManager audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);

        int beforeValue = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        appContext.sendBroadcast(testIntent);

        for(int i = 0; i < 20; i++){
            appContext.sendBroadcast(testIntent);
        }

        try{
            Thread.sleep(3000); // Delay
        }catch(InterruptedException e){
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        int afterValue = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        if(beforeValue > 0) {
            Log.i(LOG_TAG, "Decreased from " + beforeValue + " to " + afterValue + " MEDIA");
            assertTrue(beforeValue > afterValue);
        }else{
            Log.i(LOG_TAG, "Decreased from 0 volume. Value remains the same. MEDIA");
            assertEquals(beforeValue, afterValue);
        }
    }

    @Test
    public void testMediaVolumeIncrease() {
        Intent testIntent = new Intent(appContext, AdjustVolumeReceiver.class);

        testIntent.putExtra(AdjustVolumeReceiver.VOLUME_TYPE_EXTRA, AdjustVolumeReceiver.MEDIA_VOLUME);
        testIntent.putExtra(AdjustVolumeReceiver.VOLUME_DIRECTION, AdjustVolumeReceiver.VOLUME_INCREASE);

        AudioManager audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);

        int beforeValue = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        appContext.sendBroadcast(testIntent);

        for(int i = 0; i < 20; i++){
            appContext.sendBroadcast(testIntent);
        }

        try{
            Thread.sleep(3000); // Delay
        }catch(InterruptedException e){
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        int afterValue = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        if(beforeValue < audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
            Log.i(LOG_TAG, "Increased from " + beforeValue + " to " + afterValue + " MEDIA");
            assertTrue(beforeValue < afterValue);
        }else{
            Log.i(LOG_TAG, "Increased from max volume. Value remains the same. MEDIA");
            assertEquals(beforeValue, afterValue);
        }
    }

    @Test
    public void testRingerVolumeDecrease() {
        Intent testIntent = new Intent(appContext, AdjustVolumeReceiver.class);

        testIntent.putExtra(AdjustVolumeReceiver.VOLUME_TYPE_EXTRA, AdjustVolumeReceiver.RING_VOLUME);
        testIntent.putExtra(AdjustVolumeReceiver.VOLUME_DIRECTION, AdjustVolumeReceiver.VOLUME_DECREASE);

        AudioManager audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);

        int beforeValue = audioManager.getStreamVolume(AudioManager.STREAM_RING);

        appContext.sendBroadcast(testIntent);

        for(int i = 0; i < 20; i++){
            appContext.sendBroadcast(testIntent);
        }

        try{
            Thread.sleep(3000); // Delay
        }catch(InterruptedException e){
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        int afterValue = audioManager.getStreamVolume(AudioManager.STREAM_RING);

        if(beforeValue > 0) {
            Log.i(LOG_TAG, "Decreased from " + beforeValue + " to " + afterValue + " RINGER");
            assertTrue(beforeValue > afterValue);
        }else{
            Log.i(LOG_TAG, "Decreased from 0 volume. Value remains the same. RINGER");
            assertEquals(beforeValue, afterValue);
        }
    }

    @Test
    public void testRingerVolumeIncrease() {
        Intent testIntent = new Intent(appContext, AdjustVolumeReceiver.class);

        testIntent.putExtra(AdjustVolumeReceiver.VOLUME_TYPE_EXTRA, AdjustVolumeReceiver.RING_VOLUME);
        testIntent.putExtra(AdjustVolumeReceiver.VOLUME_DIRECTION, AdjustVolumeReceiver.VOLUME_INCREASE);

        AudioManager audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);

        int beforeValue = audioManager.getStreamVolume(AudioManager.STREAM_RING);

        appContext.sendBroadcast(testIntent);

        for(int i = 0; i < 20; i++){
            appContext.sendBroadcast(testIntent);
        }

        try{
            Thread.sleep(3000); // Delay
        }catch(InterruptedException e){
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        int afterValue = audioManager.getStreamVolume(AudioManager.STREAM_RING);

        if(beforeValue < audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)) {
            Log.i(LOG_TAG, "Increased from " + beforeValue + " to " + afterValue + " RINGER");
            assertTrue(beforeValue < afterValue);
        }else{
            Log.i(LOG_TAG, "Increased from max volume. Value remains the same. RINGER");
            assertEquals(beforeValue, afterValue);
        }
    }
}
