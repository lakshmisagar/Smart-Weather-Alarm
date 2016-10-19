package alarm.ls.developer.com.ls_alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import java.util.Calendar;

/**
 * Created by Lakshmisagar on 10/15/2016.
 */

public class AlarmAlertActivity extends Activity {
    private static final String TAG = AlarmAlertActivity.class.getName();
    MediaPlayer mMediaPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        displayAlert();
    }

    public void displayAlert() {

        try {
            SharedPreferences mToneprefs = getSharedPreferences("TonePickerValue", MODE_PRIVATE);
            String selectedtone = mToneprefs.getString("SelectedURI", "");
            Log.d(TAG, "selectedtone :" + selectedtone);
            Uri alert = Uri.parse(selectedtone);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(this, alert);
            final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (Exception e) {
        }

        final AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        final Calendar calendar = Calendar.getInstance();
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alarm").setMessage("Its time , pull up your socks").setCancelable(
                false).setPositiveButton("Snooze",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 1000 * 60 * 29, pendingIntent);
                        mMediaPlayer.stop();
                        mMediaPlayer.release();
                        mMediaPlayer = null;
                        Log.d(TAG, "Snooze touch :" + mMediaPlayer);
                        SharedPreferences alarmTimeTextprefs = getSharedPreferences("alarmTimeTextprefs", MODE_PRIVATE);
                        SharedPreferences.Editor alarmeditor = alarmTimeTextprefs.edit();
                        alarmeditor.putString("alarmtimeTxt", "Snooze for 30 mins");
                        alarmeditor.commit();
                        dialog.cancel();
                        finish();
                    }
                }).setNegativeButton("Dismiss",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        manager.cancel(pendingIntent);
                        mMediaPlayer.stop();
                        mMediaPlayer.release();
                        mMediaPlayer = null;
                        Log.d(TAG, "Dismiss touch :" + mMediaPlayer);
                        SharedPreferences alarmTimeTextprefs = getSharedPreferences("alarmTimeTextprefs", MODE_PRIVATE);
                        SharedPreferences.Editor alarmeditor = alarmTimeTextprefs.edit();
                        alarmeditor.putString("alarmtimeTxt", "No alarm");
                        alarmeditor.commit();
                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}