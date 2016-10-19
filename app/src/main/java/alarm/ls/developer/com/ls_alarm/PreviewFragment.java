package alarm.ls.developer.com.ls_alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by "Lakshmisagar" on 9/4/2016.
 */
public class PreviewFragment extends Fragment {

    private static final String TAG = PreviewFragment.class.getName();
    private Handler handler;
    private TextView currentHour;
    private TextView currentMins;
    private TextView mAM_PM;
    private String hour;
    private String mins;
    private int am_pm = 0;
    private IntentFilter filter = new IntentFilter();

    private TextView alarmTime;
    private ImageView weatherimage1;
    private ImageView weatherimage2;
    private ImageView weatherimage3;
    private ImageView weatherimage4;

    private TextView day;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_preview, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        filter.addAction(Intent.ACTION_TIME_CHANGED);

        getActivity().registerReceiver(preview_timeChangeReceiver, filter);

        //Set time
        alarmTime = (TextView) getActivity().findViewById(R.id.p_alarmtime);
        SharedPreferences alarmTimeTextprefs = getActivity().getSharedPreferences("alarmTimeTextprefs", getActivity().MODE_PRIVATE);
        String alarmTimetxt = alarmTimeTextprefs.getString("alarmtimeTxt", "");
        if (!alarmTimetxt.equals(""))
            alarmTime.setText(alarmTimetxt);

        weatherimage1 = (ImageView) getActivity().findViewById(R.id.p_weather1);
        weatherimage2 = (ImageView) getActivity().findViewById(R.id.p_weather2);
        weatherimage3 = (ImageView) getActivity().findViewById(R.id.p_weather3);
        weatherimage4 = (ImageView) getActivity().findViewById(R.id.p_weather4);

        day = (TextView) getActivity().findViewById(R.id.p_day);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume()");
        currentHour = (TextView) getActivity().findViewById(R.id.p_hr);
        currentMins = (TextView) getActivity().findViewById(R.id.p_min);
        mAM_PM = (TextView) getActivity().findViewById(R.id.p_am_pm);
        Calendar c = Calendar.getInstance();
        if (!DateFormat.is24HourFormat(getActivity())) {
            hour = String.format("%02d", c.get(Calendar.HOUR));
            am_pm = c.get(Calendar.AM_PM);
            mAM_PM.setText((am_pm == Calendar.AM) ? "am" : "pm");
            mAM_PM.setVisibility(View.VISIBLE);
        } else {
            mAM_PM.setVisibility(View.GONE);
            hour = String.format("%02d", c.get(Calendar.HOUR_OF_DAY));
        }
        mins = String.format("%02d", c.get(Calendar.MINUTE));
        Log.d(TAG, " hour:" + hour + " mins:" + mins);
        currentHour.setText(hour);
        currentMins.setText(mins);

        int dayval = c.get(Calendar.DAY_OF_WEEK);
        String today ="";
        switch (dayval) {
            case Calendar.SUNDAY:
                today = "Sunday";
                break;
            case Calendar.MONDAY:
                today = "Monday";
                break;
            case Calendar.TUESDAY:
                today = "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                today = "Wednesday";
                break;
            case Calendar.THURSDAY:
                today = "Thursday";
                break;
            case Calendar.FRIDAY:
                today = "Friday";
                break;
            case Calendar.SATURDAY:
                today = "Saturday";
                break;
        }
        day.setText(today);

        p_dotBlinker();

        SharedPreferences Togglepref = getActivity().getSharedPreferences("ToggleButtonStatus", getActivity().MODE_PRIVATE);
        Boolean sunny_btn = Togglepref.getBoolean("sunny_btn", false);
        Boolean rainy_btn = Togglepref.getBoolean("rainy_btn", false);
        Boolean cold_btn = Togglepref.getBoolean("cold_btn", false);
        Boolean snow_btn = Togglepref.getBoolean("snow_btn", false);
        if (sunny_btn) {
            weatherimage1.setVisibility(View.VISIBLE);
        }
        if (rainy_btn) {
            weatherimage2.setVisibility(View.VISIBLE);
        }
        if (snow_btn) {
            weatherimage3.setVisibility(View.VISIBLE);
        }
        if (cold_btn) {
            weatherimage4.setVisibility(View.VISIBLE);
        }
    }

    public void p_dotBlinker() {
        TextView myText = (TextView) getActivity().findViewById(R.id.p_dots);

        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(800);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        myText.startAnimation(anim);
    }

    private final BroadcastReceiver preview_timeChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d("onreceive", "action :" + action);
            if (action.equals(Intent.ACTION_TIME_CHANGED) ||
                    action.equals(Intent.ACTION_TIMEZONE_CHANGED) || action.equals(Intent.ACTION_TIME_TICK)) {
                Calendar c = Calendar.getInstance();
                if (!DateFormat.is24HourFormat(context)) {
                    hour = String.format("%02d", c.get(Calendar.HOUR));
                    am_pm = c.get(Calendar.AM_PM);
                    mAM_PM.setText((am_pm == Calendar.AM) ? "am" : "pm");
                    mAM_PM.setVisibility(View.VISIBLE);
                } else {
                    mAM_PM.setVisibility(View.GONE);
                    hour = String.format("%02d", c.get(Calendar.HOUR_OF_DAY));
                }
                mins = String.format("%02d", c.get(Calendar.MINUTE));
                Log.d(TAG, " hour:" + hour + "  mins:" + mins);
                currentHour.setText(hour);
                currentMins.setText(mins);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (preview_timeChangeReceiver != null)
            getActivity().unregisterReceiver(preview_timeChangeReceiver);
    }

    public void updateAlTime(String al_time) {
        alarmTime.setText(al_time);
    }
}
