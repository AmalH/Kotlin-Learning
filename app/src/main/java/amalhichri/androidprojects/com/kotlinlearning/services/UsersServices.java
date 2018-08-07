package amalhichri.androidprojects.com.kotlinlearning.services;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import amalhichri.androidprojects.com.kotlinlearning.utils.AppSingleton;

/**
 * Created by Amal on 19/11/2017.
 */

public class UsersServices {

    private static UsersServices this_ = new UsersServices();

    public static synchronized UsersServices getInstance()
    {	return this_;
    }

    public void registerUser(String id, String username, String email, String pictureUrl, Context context , final ServerCallbacks serverCallbacks){
        Map<String, String> m = new HashMap<String, String>();
        m.put("id", id);
        m.put("username", username);
        m.put("email", email);
        m.put("pictureUrl", pictureUrl);
        final JSONObject jsonBody = new JSONObject(m);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, "http://ikotlin.pragmatictheories.tech/web/users/register", jsonBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        if (!response.has("Error")){
                            serverCallbacks.onSuccess(response);
                        }
                        else{
                            serverCallbacks.onWrong(response);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        serverCallbacks.onError(error);
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "Register");
    }

    public void getUserById(String id, Context context, final ServerCallbacks serverCallbacks){
        final JSONObject jsonBody = new JSONObject();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, "http://ikotlin.pragmatictheories.tech/web/users/getUser?id="+id, jsonBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        if (!response.has("Error")){
                            serverCallbacks.onSuccess(response);
                        }
                        else{
                            serverCallbacks.onWrong(response);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        serverCallbacks.onError(error);
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "GetUser");
    }

    public void assignBadge(String id,String badgeindic, Context context, final ServerCallbacks serverCallbacks){
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, "http://ikotlin.pragmatictheories.tech/web/users/addBadge?id="+id+"&badgeindic="+badgeindic, new JSONObject(), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        if (!response.has("Error")){
                            serverCallbacks.onSuccess(response);
                        }
                        else{
                            serverCallbacks.onWrong(response);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        serverCallbacks.onError(error);
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "AssignBadge");
    }

    public void isHasBadge(String id,String badgeindic,Context context, final ServerCallbacks serverCallbacks){
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, "http://ikotlin.pragmatictheories.tech/web/users/hasBadge?id="+id+"&badgeindic="+badgeindic, new JSONObject(), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        if (!response.has("Error")){
                            serverCallbacks.onSuccess(response);
                        }
                        else{
                            serverCallbacks.onWrong(response);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        serverCallbacks.onError(error);
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "AssignBadge");
    }

}
