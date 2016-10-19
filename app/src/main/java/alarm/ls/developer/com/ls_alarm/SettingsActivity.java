package alarm.ls.developer.com.ls_alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Lakshmisagar on 9/4/2016.
 */
public class SettingsActivity extends AppCompatActivity implements AlarmTimePickerFragment.AlTimePkrFragInterface{
    private static final String TAG = SettingsActivity.class.getName();

    //For Alarm
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main_fragment);


         /* Retrieve a PendingIntent that will perform a broadcast */
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        initActionBar();

        Button btnCancel = (Button) findViewById(R.id.button_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancel(v);
            }
        });
        Button btnOK = (Button) findViewById(R.id.button_ok);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSave(v);
            }
        });
    }

    private void initActionBar() {
        final android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.save_cancel_actionbar, null);
        mActionBar.setCustomView(view);
        mActionBar.setDisplayShowCustomEnabled(true);
    }


    public void onCancel(View view) {
        Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);

        SharedPreferences alarmTimeTextprefs = getSharedPreferences("alarmTimeTextprefs", MODE_PRIVATE);
        SharedPreferences.Editor alarmeditor = alarmTimeTextprefs.edit();
        alarmeditor.putString("alarmtimeTxt", "No alarm");
        alarmeditor.commit();
        finish();
    }

    public void onSave(View view) {

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        SharedPreferences prefs = getSharedPreferences("TimePickerValue", MODE_PRIVATE);
        int hour = prefs.getInt("hour_value", -1);
        int minute = prefs.getInt("minute_value", -1);

        SharedPreferences alarmTimeTextprefs = getSharedPreferences("alarmTimeTextprefs", MODE_PRIVATE);
        SharedPreferences.Editor alarmeditor = alarmTimeTextprefs.edit();
        if(hour == -1){
            alarmeditor.putString("alarmtimeTxt","Set TIme");
        }else {
            if (hour > 12) {
                alarmeditor.putString("alarmtimeTxt", String.format("%02d", (hour - 12)) + ":" + String.format("%02d", (minute)) + " pm");
            } else {
                alarmeditor.putString("alarmtimeTxt", String.format("%02d", (hour)) + ":" + String.format("%02d", (minute)) + " am");
            }
        }
        alarmeditor.commit();

        /* Set the alarm to start at perticular time */
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        Log.d(TAG, "time set: " + (calendar.getTimeInMillis() - (1000 * 15 * 1)));
        manager.set(AlarmManager.RTC_WAKEUP, (calendar.getTimeInMillis() - (1000 * 15 * 1)), pendingIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void sendAlarmTIme(String al_time) {

        PreviewFragment previewFragment = (PreviewFragment) getSupportFragmentManager().findFragmentById(R.id.preview_fragment);
        previewFragment.updateAlTime(al_time);
    }
}
