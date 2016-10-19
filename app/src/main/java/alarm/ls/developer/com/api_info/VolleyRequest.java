package alarm.ls.developer.com.api_info;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

/**
 * Created by Lakshmisagar on 9/26/2016.
 * <p>
 * VolleyRequest is made singleton class , so that only one queue exists for the whole application.
 * We pass the Application context to get the queue.
 * It is initialized with application context, so that the queue exists only till the application exists.
 */

public class VolleyRequest {

    private static Context mContext;
    private RequestQueue mRequestQueue;
    private static VolleyRequest vrInstance;

    //Creates a request Queue with the specified application Context
    public VolleyRequest(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    //Since singleton class only once instance of this class (vrInstance) can exists.
    //getInstance returns the instace else create once and then returns.
    public static synchronized VolleyRequest getInstance(Context context) {
        if (vrInstance == null) {
            vrInstance = new VolleyRequest(context);
        }
        return vrInstance;
    }

    //Build the URL to request from
    public Uri getGeoPositionUri_Accu(String latitude, String longitude) {
        Uri.Builder builder = new Uri.Builder();
        return builder.scheme("http")
                .authority(GlobalConstants.BASE_URL)
                .appendPath(GlobalConstants.URL_PATH_LOCATION)
                .appendPath(GlobalConstants.URL_PATH_VERSION)
                .appendPath(GlobalConstants.URL_PATH_CITIES)
                .appendPath(GlobalConstants.URL_PATH_GEOPOSITION)
                .appendPath(GlobalConstants.URL_PATH_SEARCH)
                .appendQueryParameter(GlobalConstants.QUERY_PARAM_APIKEY, GlobalConstants.API_KEY)
                .appendQueryParameter(GlobalConstants.QUERY_PARAM_LAT_LON, latitude + "," + longitude).build();
    }

    //Build the TemperatureReqURL to request from
    public Uri getCurConditionUri_Accu(String cityCode) {
        Uri.Builder builder = new Uri.Builder();
        return builder.scheme("http")
                .authority(GlobalConstants.BASE_URL)
                .appendPath(GlobalConstants.URL_CURRENT_CONDITION)
                .appendPath(GlobalConstants.URL_PATH_VERSION)
                .appendPath(cityCode)
                .appendQueryParameter(GlobalConstants.QUERY_PARAM_APIKEY, GlobalConstants.API_KEY).build();
    }

    //Get the Request Queue
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            //Create a Cache for the application
            Cache cache = new DiskBasedCache(mContext.getCacheDir(), 1024 * 1024);//1MB space for cache
            //Set up the network to use HttpURLConnection
            Network network = new BasicNetwork(new HurlStack());
            //Request queue needs cache and network to be set.
            mRequestQueue = new RequestQueue(cache, network);
            //Start the queue to use
            mRequestQueue.start();
        }
        return mRequestQueue;
    }
}
