package amalhichri.androidprojects.com.kotlinlearning.services;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by Amal on 19/11/2017.
 */

public interface ServerCallbacks {
    void onSuccess(JSONObject result);
    void onError(VolleyError result);
    void onWrong(JSONObject result);
}
