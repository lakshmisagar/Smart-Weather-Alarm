package alarm.ls.developer.com.api_info;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Lakshmisagar on 9/26/2016.
 */

public class LocationKey {


    @SerializedName("key")
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
