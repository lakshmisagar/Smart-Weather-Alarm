package alarm.ls.developer.com.api_info;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lakshmisagar on 9/26/2016.
 */

public class CustomJsonRequest extends JsonObjectRequest {
    public CustomJsonRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    //Passing some request Headers to avoid adding headers in every request
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8"); // specifying that you need data in json format
        return headers;
    }

    //Custom retry policy can be written
    @Override
    public RetryPolicy getRetryPolicy() {
        return super.getRetryPolicy();
    }
}
