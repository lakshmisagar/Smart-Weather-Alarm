package alarm.ls.developer.com.ls_alarm;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

public class WatchScreenActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = WatchScreenActivity.class.getName();
    //For LOCATION FUSED API
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 0;
    private static GoogleApiClient googleApiClient;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    private RequestQueue requestQueue;

    private IntentFilter filter = new IntentFilter();
    private TextView day;
    private TextView currentHour;
    private TextView currentMins;
    private TextView mAM_PM;

    private ImageView alarmStop;
    private TextView alarmTime;
    private ImageView weatherimage1;
    private ImageView weatherimage2;
    private ImageView weatherimage3;
    private ImageView weatherimage4;

    private Bitmap tempbit;
    private String hour;
    private String mins;
    private int am_pm = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "create");

        //For FUSED LOCATION
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_watch_screen);

        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        filter.addAction(Intent.ACTION_TIME_CHANGED);

        initLocation();
        registerReceiver(mtimeChangeReceiver, filter);
    }

    private void initLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (googleApiClient != null) {
            Log.d(TAG, "onStart()");
            googleApiClient.connect();
        }

        checkGPS();

    }

    private void checkGPS() {
        boolean gps_enabled = false;
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!gps_enabled)
            statusCheck();
    }

    public static GoogleApiClient getGoogleApiClient() {
        Log.d(TAG, "getGoogleApiClient()");
        return googleApiClient;
    }

    //Fetch Location when connected to GoogleApiClient Service
    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            Log.d(TAG, "lastLocation :" + lastLocation);
            if (lastLocation != null) {
                LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                double lat = latLng.latitude;
                double lon = latLng.longitude;
                Log.d(TAG, "Latitude: " + lat + " Longitude: " + lon);
            }
        }
        startLocationUpdates();
    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates()");
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended()");
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed()");
        Toast.makeText(this, "onConnectionFailed.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged()");
// New location has now been determined
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.d(TAG, "onLocationChanged()  latLng: " + latLng.latitude + "," + latLng.longitude);
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, Please enable to proceed!")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        // Begin polling for new location updates.
                        startLocationUpdates();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    Log.d(TAG, "lastLocation Res :" + lastLocation);
                    if (lastLocation != null) {
                        LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                        double lat = latLng.latitude;
                        double lon = latLng.longitude;
                        Log.d(TAG, "Latitude: " + lat + " Longitude: " + lon);
                    }
                    // Begin polling for new location updates.
                    startLocationUpdates();
                } else {
                    Toast.makeText(this, "Need your location to enable snooze option!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }


    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        currentHour = (TextView) findViewById(R.id.hr);
        currentMins = (TextView) findViewById(R.id.min);
        mAM_PM = (TextView) findViewById(R.id.am_pm);
        Calendar c = Calendar.getInstance();
        if (!DateFormat.is24HourFormat(this)) {
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

        day = (TextView) findViewById(R.id.day);
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

        alarmTime = (TextView) findViewById(R.id.alarmtime);
        SharedPreferences alarmTimeTextprefs = getSharedPreferences("alarmTimeTextprefs", MODE_PRIVATE);
        String alarmTimetxt = alarmTimeTextprefs.getString("alarmtimeTxt", "");
        if (!alarmTimetxt.equals(""))
            alarmTime.setText(alarmTimetxt);
        Log.d(TAG, "OnResume alarmTimetxt:" + alarmTimetxt);
        //weather Image View
        alarmStop = (ImageView) findViewById(R.id.stopalarm);
        weatherimage1 = (ImageView) findViewById(R.id.weather1);
        weatherimage2 = (ImageView) findViewById(R.id.weather2);
        weatherimage3 = (ImageView) findViewById(R.id.weather3);
        weatherimage4 = (ImageView) findViewById(R.id.weather4);
        alarmStop.setVisibility(View.GONE);
        weatherimage1.setVisibility(View.GONE);
        weatherimage2.setVisibility(View.GONE);
        weatherimage3.setVisibility(View.GONE);
        weatherimage4.setVisibility(View.GONE);
        SharedPreferences Togglepref = getSharedPreferences("ToggleButtonStatus", MODE_PRIVATE);
        Boolean sunny_btn = Togglepref.getBoolean("sunny_btn", false);
        Boolean rainy_btn = Togglepref.getBoolean("rainy_btn", false);
        Boolean cold_btn = Togglepref.getBoolean("cold_btn", false);
        Boolean snow_btn = Togglepref.getBoolean("snow_btn", false);
        Log.d(TAG, "sunny_btn :" + sunny_btn + "rainy_btn:" + rainy_btn + " cold_btn:" + cold_btn + " snow_btn:" + snow_btn);
        if (sunny_btn || rainy_btn || cold_btn || snow_btn) {
            alarmStop.setVisibility(View.VISIBLE);
            int count = 1;
            if (sunny_btn) {
                setResourceID("Sunny", count);
                count++;
            }
            if (rainy_btn) {
                setResourceID("Rainy", count);
                count++;
            }
            if (snow_btn) {
                setResourceID("Snow", count);
                count++;
            }
            if (cold_btn) {
                setResourceID("Cold", count);
                count++;
            }

        }
        super.onResume();
    }

    private void setResourceID(String weather, int imagevalue) {
        switch (imagevalue) {
            case 1:
                if (weather.equalsIgnoreCase("Sunny")) {
                    weatherimage1.setImageResource(R.drawable.sunnyimage);
                } else if (weather.equalsIgnoreCase("Rainy")) {
                    weatherimage1.setImageResource(R.drawable.rainyimage);
                } else if (weather.equalsIgnoreCase("Snow")) {
                    weatherimage1.setImageResource(R.drawable.snowimage);
                } else if (weather.equalsIgnoreCase("Cold")) {
                    weatherimage1.setImageResource(R.drawable.coldimage);
                }
                weatherimage1.setVisibility(View.VISIBLE);
                break;
            case 2:
                if (weather.equalsIgnoreCase("Sunny")) {
                    weatherimage2.setImageResource(R.drawable.sunnyimage);
                } else if (weather.equalsIgnoreCase("Rainy")) {
                    weatherimage2.setImageResource(R.drawable.rainyimage);
                } else if (weather.equalsIgnoreCase("Snow")) {
                    weatherimage2.setImageResource(R.drawable.snowimage);
                } else if (weather.equalsIgnoreCase("Cold")) {
                    weatherimage2.setImageResource(R.drawable.coldimage);
                }
                weatherimage2.setVisibility(View.VISIBLE);
                break;
            case 3:
                if (weather.equalsIgnoreCase("Sunny")) {
                    weatherimage3.setImageResource(R.drawable.sunnyimage);
                } else if (weather.equalsIgnoreCase("Rainy")) {
                    weatherimage3.setImageResource(R.drawable.rainyimage);
                } else if (weather.equalsIgnoreCase("Snow")) {
                    weatherimage3.setImageResource(R.drawable.snowimage);
                } else if (weather.equalsIgnoreCase("Cold")) {
                    weatherimage3.setImageResource(R.drawable.coldimage);
                }
                weatherimage3.setVisibility(View.VISIBLE);
                break;
            case 4:
                if (weather.equalsIgnoreCase("Sunny")) {
                    weatherimage4.setImageResource(R.drawable.sunnyimage);
                } else if (weather.equalsIgnoreCase("Rainy")) {
                    weatherimage4.setImageResource(R.drawable.rainyimage);
                } else if (weather.equalsIgnoreCase("Snow")) {
                    weatherimage4.setImageResource(R.drawable.snowimage);
                } else if (weather.equalsIgnoreCase("Cold")) {
                    weatherimage4.setImageResource(R.drawable.coldimage);
                }
                weatherimage4.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void settingsButtonClick(View v) {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
    }

    public void p_dotBlinker() {
        TextView myText = (TextView) findViewById(R.id.dots);

        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(800);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        myText.startAnimation(anim);
    }

    private final BroadcastReceiver mtimeChangeReceiver = new BroadcastReceiver() {
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
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        // only stop if it's connected, otherwise we crash
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }

        if (mtimeChangeReceiver != null)
            unregisterReceiver(mtimeChangeReceiver);

        super.onDestroy();

    }

}
