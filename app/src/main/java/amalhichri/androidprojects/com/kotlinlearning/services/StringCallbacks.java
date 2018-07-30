package amalhichri.androidprojects.com.kotlinlearning.services;

import com.android.volley.VolleyError;

/**
 * Created by Amal on 18/01/2018.
 */

public interface StringCallbacks {
    void onSuccess(String result);
    void onError(VolleyError result);
}
