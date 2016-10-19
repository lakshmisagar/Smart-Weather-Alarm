package alarm.ls.developer.com.ls_alarm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import alarm.ls.developer.com.api_info.VolleyRequest;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by "Lakshmisagar" on 9/4/2016.
 */
public class ListSettingFragment extends Fragment {
    private static final String TAG = ListSettingFragment.class.getName();
    private static final int TONE_PICKER = 111;
    private int RESULT_OK = -1;
    private static String cityCode;
    private RequestQueue requestQueue;

    private ImageView weatherimage1;
    private ImageView weatherimage2;
    private ImageView weatherimage3;
    private ImageView weatherimage4;

    ToggleButton sunny_btn, rainy_btn, cold_btn, snow_btn;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_list, container, false);
        return layoutView;
    }

    private VolleyRequest get_vrInstance() {
        return VolleyRequest.getInstance(getActivity().getApplicationContext());
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SharedPreferences mToneprefs = getActivity().getSharedPreferences("TonePickerValue", getActivity().MODE_PRIVATE);
        String selectedtone = mToneprefs.getString("Selected", "");
        Log.d(TAG, selectedtone + "  selected");
        TextView tone = (TextView) getActivity().findViewById(R.id.alarmtone_tv);
        if (!selectedtone.equals("")) {
            tone.setText(selectedtone);
        } else {
            tone.setText("Set Tone");
        }

        SharedPreferences prefs = getActivity().getSharedPreferences("TimePickerValue", getActivity().MODE_PRIVATE);
        int hour = prefs.getInt("hour_value", -1);
        int minute = prefs.getInt("minute_value", -1);
        TextView tv = (TextView) getActivity().findViewById(R.id.alarmtime_tv);
        if (hour == -1) {
            tv.setText("Set Time");
        } else {
            if (hour > 12) {
                tv.setText(String.format("%02d", (hour - 12)) + ":" + String.format("%02d", (minute)) + " pm");
            } else {
                tv.setText(String.format("%02d", (hour)) + ":" + String.format("%02d", (minute)) + " am");
            }
        }

        weatherimage1 = (ImageView) getActivity().findViewById(R.id.p_weather1);
        weatherimage2 = (ImageView) getActivity().findViewById(R.id.p_weather2);
        weatherimage3 = (ImageView) getActivity().findViewById(R.id.p_weather3);
        weatherimage4 = (ImageView) getActivity().findViewById(R.id.p_weather4);

        sunny_btn = (ToggleButton) getActivity().findViewById(R.id.togglebutton);
        sunny_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sunny_btn.isChecked()){
                    weatherimage1.setVisibility(View.VISIBLE);
                }else{
                    weatherimage1.setVisibility(View.GONE);
                }
                if (cityCode == null)
                    updateCityCodeFromGoogleApi();
                Log.d(TAG, "cityCode: " + cityCode);
            }
        });
        rainy_btn = (ToggleButton) getActivity().findViewById(R.id.togglebutton2);
        rainy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rainy_btn.isChecked()){
                    weatherimage2.setVisibility(View.VISIBLE);
                }else{
                    weatherimage2.setVisibility(View.GONE);
                }
                if (cityCode == null)
                    updateCityCodeFromGoogleApi();
                Log.d(TAG, "cityCode: " + cityCode);
            }
        });
        cold_btn = (ToggleButton) getActivity().findViewById(R.id.togglebutton3);
        cold_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cold_btn.isChecked()){
                    weatherimage3.setVisibility(View.VISIBLE);
                }else{
                    weatherimage3.setVisibility(View.GONE);
                }
                if (cityCode == null)
                    updateCityCodeFromGoogleApi();
                Log.d(TAG, "cityCode: " + cityCode);
            }
        });
        snow_btn = (ToggleButton) getActivity().findViewById(R.id.togglebutton4);
        snow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(snow_btn.isChecked()){
                    weatherimage4.setVisibility(View.VISIBLE);
                }else{
                    weatherimage4.setVisibility(View.GONE);
                }
                if (cityCode == null)
                    updateCityCodeFromGoogleApi();
                Log.d(TAG, "cityCode: " + cityCode);
            }
        });


        ((RelativeLayout) getView().findViewById(R.id.alarmTimeLayout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new AlarmTimePickerFragment();
                newFragment.show(getFragmentManager(), "TimePicker");
            }
        });

        ((RelativeLayout) getView().findViewById(R.id.alarmToneLayout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Uri currentTone = RingtoneManager.getActualDefaultRingtoneUri(getContext(), RingtoneManager.TYPE_ALARM);
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
                SharedPreferences mToneprefs = getActivity().getSharedPreferences("TonePickerValue", getActivity().MODE_PRIVATE);
                String selectedtone = mToneprefs.getString("Selected", "");
                Log.d(TAG, selectedtone + "  selected");
                if (!selectedtone.equals("")) {
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, selectedtone);
                } else {
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentTone);
                }
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                startActivityForResult(intent, TONE_PICKER);
            }
        });

        SharedPreferences Togglepref = getActivity().getSharedPreferences("ToggleButtonStatus", MODE_PRIVATE);
        sunny_btn.setChecked(Togglepref.getBoolean("sunny_btn", false));
        rainy_btn.setChecked(Togglepref.getBoolean("rainy_btn", false));
        cold_btn.setChecked(Togglepref.getBoolean("cold_btn", false));
        snow_btn.setChecked(Togglepref.getBoolean("snow_btn", false));

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("ListSettingFragment", "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        SharedPreferences TogglePref = getActivity().getSharedPreferences("ToggleButtonStatus", MODE_PRIVATE);
        SharedPreferences.Editor Toggleeditor = TogglePref.edit();
        Toggleeditor.putBoolean("sunny_btn", sunny_btn.isChecked());
        Toggleeditor.putBoolean("rainy_btn", rainy_btn.isChecked());
        Toggleeditor.putBoolean("cold_btn", cold_btn.isChecked());
        Toggleeditor.putBoolean("snow_btn", snow_btn.isChecked());
        Toggleeditor.commit();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");


    }

    public static String getCityCode() {
        Log.d(TAG, "cityCode :" + cityCode);
        return cityCode;
    }

    private void updateCityCodeFromGoogleApi() {

        Log.d(TAG, "getCityFromGoogleApi()");
        String url = getCityURL();
        Log.d(TAG, "url" + url);
        if(url!= "") {
            updateCityCodeFromJsonData(url);
        }
    }

    private void updateCityCodeFromJsonData(String url) {

        //JSON REQUEST USING VOLLEY RequestQueue to get the json Object.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, " onResponse: " + response);
                try {
                    cityCode = (String) response.get("Key");
                    SharedPreferences mCityCodeprefs = getActivity().getSharedPreferences("citycode", getActivity().MODE_PRIVATE);
                    SharedPreferences.Editor editor = mCityCodeprefs.edit();
                    editor.putString("cityCode", cityCode);
                    editor.commit();
                    Log.d(TAG, "updateCityCodeFromJsonData  cityCode: " + cityCode);
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
        requestQueue.add(jsonObjectRequest);
    }

    private String getCityURL() {

        Log.d(TAG, "getCityURL()");
        String url = "";
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(WatchScreenActivity.getGoogleApiClient());
        Log.d(TAG, "lastLocation : " + lastLocation);
        if(lastLocation!= null){
            double lat = lastLocation.getLatitude(), lon = lastLocation.getLongitude();
            String Latitude = String.valueOf(lat);
            String Longitude = String.valueOf(lon);

            requestQueue = get_vrInstance().getRequestQueue();
            Log.d(TAG, "Lati:" + Latitude + "  Long:" + Longitude);
            url = get_vrInstance().getGeoPositionUri_Accu(Latitude, Longitude).toString();
        }
        return url;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult:" + requestCode);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TONE_PICKER:
                    Log.d(TAG, "onActivityResult case:" + 1);
                    final Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                    final Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), uri);
                    TextView tone = (TextView) getActivity().findViewById(R.id.alarmtone_tv);
                    tone.setText(ringtone.getTitle(getActivity()));

                    SharedPreferences mToneprefs = getActivity().getSharedPreferences("TonePickerValue", getActivity().MODE_PRIVATE);
                    SharedPreferences.Editor editor = mToneprefs.edit();
                    editor.putString("SelectedURI", String.valueOf(uri));
                    editor.putString("Selected", ringtone.getTitle(getActivity()));
                    editor.commit();
                    break;

                default:
                    break;
            }
        }
    }

}