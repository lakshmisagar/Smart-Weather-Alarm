package alarm.ls.developer.com.ls_alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import alarm.ls.developer.com.api_info.VolleyRequest;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Lakshmisagar on 9/28/2016.
 */

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = AlarmReceiver.class.getName();
    private String weather_text;
    Context mContext;

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        mContext = context;
        SharedPreferences Togglepref = mContext.getSharedPreferences("ToggleButtonStatus", MODE_PRIVATE);
        final Boolean sunny_btn = Togglepref.getBoolean("sunny_btn", false);
        final Boolean rainy_btn = Togglepref.getBoolean("rainy_btn", false);
        final Boolean cold_btn = Togglepref.getBoolean("cold_btn", false);
        final Boolean snow_btn = Togglepref.getBoolean("snow_btn", false);
        if (sunny_btn || rainy_btn || cold_btn || snow_btn) {
            SharedPreferences mToneprefs = mContext.getSharedPreferences("citycode", mContext.MODE_PRIVATE);
            String cityCode = mToneprefs.getString("cityCode", "");
            if(cityCode!= "") {
                String url = VolleyRequest.getInstance(context.getApplicationContext()).getCurConditionUri_Accu(cityCode).toString();
                Log.d(TAG, "URL :" + url);
                updateCurConditionFromJsonData(url);
            }
        }

        Timer mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "weather_text :" + weather_text);
                AlarmManager manager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                Calendar calendar = Calendar.getInstance();
                Intent alarmIntent = new Intent(mContext, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, alarmIntent, 0);
                if(weather_text!= null) {
                    if (sunny_btn && weather_text.equalsIgnoreCase("Sunny")) {
                        manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 1000 * 60 * 29, pendingIntent);
                        Toast.makeText(context, "Its sunny outside,Snoozing for 30 Minutes", Toast.LENGTH_LONG).show();
                    } else if (rainy_btn && (weather_text.equalsIgnoreCase("Rain") || weather_text.equalsIgnoreCase("T-Storms"))) {
                        manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 1000 * 60 * 29, pendingIntent);
                        Toast.makeText(context, "Its raining outside,Snoozing for 30 Minutes", Toast.LENGTH_LONG).show();
                    } else if (cold_btn && (weather_text.equalsIgnoreCase("Cold") || weather_text.equalsIgnoreCase("Ice"))) {
                        manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 1000 * 60 * 29, pendingIntent);
                        Toast.makeText(context, "Its cold outside,Snoozing for 30 Minutes", Toast.LENGTH_LONG).show();
                    } else if (snow_btn && (weather_text.equalsIgnoreCase("snow") || weather_text.equalsIgnoreCase("Rain and Snow"))) {
                        manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 1000 * 60 * 29, pendingIntent);
                        Toast.makeText(context, "Its snowing,Snoozing for 30 Minutes", Toast.LENGTH_LONG).show();
                    }else {
                        Intent alertIntent = new Intent(context, AlarmAlertActivity.class);
                        alertIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        context.startActivity(alertIntent);
                    }
                }else {
                    Intent alertIntent = new Intent(context, AlarmAlertActivity.class);
                    alertIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(alertIntent);
                }
            }
        }, 1000 * 10 * 1);

    }

    private void updateCurConditionFromJsonData(String url) {

        //JSON REQUEST USING VOLLEY RequestQueue to get the json Object.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, new JSONArray(), new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, " onResponse: " + response);
                try {
                    JSONObject jo = response.getJSONObject(0);
                    weather_text = jo.getString("WeatherText");
                    Log.d(TAG, "updateCurConditionFromJsonData  weather_text: " + weather_text);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, " onErrResp " + error.getMessage());
            }
        }
        );
        VolleyRequest.getInstance(mContext.getApplicationContext()).getRequestQueue().add(jsonArrayRequest);
    }
}
