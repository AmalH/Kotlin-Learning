package amalhichri.androidprojects.com.kotlinlearning.services;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import amalhichri.androidprojects.com.kotlinlearning.utils.AppSingleton;

public class CoursesServices {


    private static CoursesServices this_ = new CoursesServices();

    public static synchronized CoursesServices getInstance()
    {
        if (this_==null) this_=new CoursesServices();
        return this_;
    }


    public void getAllUserCourses(String id, Context context, final ServerCallbacks serverCallbacks){
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, "http://192.168.1.3/ikotlinBackEnd/web/courses/getAllCourses?id="+id,  new JSONObject(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (!response.has("Error")){
                            //
                            serverCallbacks.onSuccess(response);
                        }
                        else{
                            //
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
                    5000,
                    3,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "getUserCourses");
    }

    public void hasStartedAcourse(String id,String courseIndic, Context context, final ServerCallbacks serverCallbacks){
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, "http://192.168.1.3/iKotlinbackend/Web/courses/courseStarted?id="+id+"&courseindic="+courseIndic,  new JSONObject(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (!response.has("Error")){
                            //
                            serverCallbacks.onSuccess(response);
                        }
                        else{
                            //
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
                5000,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "getUserCourses");
    }

    public void addCourseToUser(String id,String courseIndic, Context context, final ServerCallbacks serverCallbacks){
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, "http://192.168.1.3/iKotlinbackend/Web/courses/addCourse?id="+id+"&courseindic="+courseIndic,  new JSONObject(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (!response.has("Error")){
                            //
                            serverCallbacks.onSuccess(response);
                        }
                        else{
                            //
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
                5000,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "getUserCourses");
    }

    public void incrementEarnedBadgesNumber(String id,String courseindic, Context context,final ServerCallbacks serverCallbacks){
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, "http://192.168.1.3:80/iKotlinbackend/Web/courses/earnedBadges?id="+id+"&courseindic="+courseindic, new JSONObject(), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        if (!response.has("Error")){
                            //ok
                            serverCallbacks.onSuccess(response);
                        }
                        else{
                            //wrong entries
                            serverCallbacks.onWrong(response);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //connection problem
                        serverCallbacks.onError(error);
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "AssignBadge");
    }

    public static void incrementFinishedChaptersNumber(String id, String courseindic, Context context, final ServerCallbacks serverCallbacks){
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, "http://192.168.1.3:80/iKotlinbackend/Web/courses/finishedChapter?id="+id+"&courseindic="+courseindic, new JSONObject(), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        if (!response.has("Error")){
                            //ok
                            serverCallbacks.onSuccess(response);
                        }
                        else{
                            //wrong entries
                            serverCallbacks.onWrong(response);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //connection problem
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
