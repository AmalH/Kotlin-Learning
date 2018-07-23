package amalhichri.androidprojects.com.kotlinlearning.services;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import amalhichri.androidprojects.com.kotlinlearning.models.Competition;
import amalhichri.androidprojects.com.kotlinlearning.models.CompetitionAnswer;
import amalhichri.androidprojects.com.kotlinlearning.utils.AppSingleton;

/**
 * Created by Odil on 12/01/2018.
 */

public class CompetitionsServices {


    private static CompetitionsServices this_ = new CompetitionsServices();

    public static synchronized CompetitionsServices getInstance()
    {
        if(this_ ==null) this_ =new CompetitionsServices();
        return this_;
    }

    public void getCompetitions(String id, Context context, int start, int level, int orderby , final ServerCallbacks serverCallbacks){
        final JSONObject jsonBody = new JSONObject();

        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, "http://192.168.1.3/ikotlinBackEnd/web/competitions/getCompetitions?id=" + id + "&start=" + start + "&order=" + orderby + "&level=" + level, jsonBody, new Response.Listener<JSONObject>() {

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
                5000,//timeout
                3,//retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            //Log.d("compet",jsObjRequest.toString());
        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "getCompetitions");
    }

    public void getCompetitionsAnswers(String id, Context context, int start, int level, final ServerCallbacks serverCallbacks){
        final JSONObject jsonBody = new JSONObject();

        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, "http://192.168.1.3/ikotlinBackEnd/web/competitions/getAnswers?id=" + id + "&start=" + start + "&level=" + level, jsonBody, new Response.Listener<JSONObject>() {

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
                5000,//timeout
                3,//retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Log.d("compet",jsObjRequest.toString());
        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "getCompetitionsAnswers");
    }

    public void getCompetition(String id, Context context, int idcompetition, final ServerCallbacks serverCallbacks){
        final JSONObject jsonBody = new JSONObject();

        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, "http://192.168.1.3/ikotlinBackEnd/web/competitions/getCompetition?id=" + id + "&idcompetition=" +idcompetition, jsonBody, new Response.Listener<JSONObject>() {

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
                5000,//timeout
                3,//retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Log.d("compet",jsObjRequest.toString());
        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "getCompetitionsAnswers");
    }

    public void getCompetitionAnswer(String id, Context context, int idanswer, final ServerCallbacks serverCallbacks){
        final JSONObject jsonBody = new JSONObject();

        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, "http://192.168.1.3/ikotlinBackEnd/web/competitions/getAnswers?id=" + id + "&idanswer=" +idanswer, jsonBody, new Response.Listener<JSONObject>() {

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
        //Log.d("request",jsObjRequest.toString());
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,//timeout
                3,//retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Log.d("compet",jsObjRequest.toString());
        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "getCompetitionsAnswers");
    }

    public void addAnswer(Context context, CompetitionAnswer answer, String id, final ServerCallbacks serverCallbacks){
        Map<String, String> m = new HashMap<String, String>();
        m.put("id", id);
        m.put("competitionid", answer.getId_competition()+"");
        m.put("content", answer.getContent());
        final JSONObject jsonBody = new JSONObject(m);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, "http://192.168.1.3/ikotlinBackEnd/web/competitions/addAnswer", jsonBody, new Response.Listener<JSONObject>() {

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
                5000,//timeout
                3,//retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "AddAnswer");
    }

    public void addCompetition(Context context, Competition competition, String id, final ServerCallbacks serverCallbacks){
        Map<String, String> m = new HashMap<String, String>();
        m.put("id", id);
        m.put("title", competition.getTitle());
        m.put("content", competition.getContent());
        m.put("level", competition.getLevel()+"");
        final JSONObject jsonBody = new JSONObject(m);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, "http://192.168.1.3/ikotlinBackEnd/web/competitions/addCompetition", jsonBody, new Response.Listener<JSONObject>() {

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
                5000,//timeout
                3,//retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "AddCompetition");
    }

    public void editAnswer (Context context, CompetitionAnswer answer, String id, final ServerCallbacks serverCallbacks){
        Map<String, String> m = new HashMap<String, String>();
        m.put("id", id);
        m.put("competitionid", answer.getId_competition()+"");
        m.put("content", answer.getContent());
        m.put("answerid", answer.getId()+"");
        final JSONObject jsonBody = new JSONObject(m);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, "http://192.168.1.3/ikotlinBackEnd/web/competitions/competitions/editAnswer", jsonBody, new Response.Listener<JSONObject>() {

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
        //Log.d("compet",jsonBody.toString());
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,//timeout
                3,//retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "AddCompetition");
    }

    public void compileCode(final Context context, final JSONObject jsb, final StringCallbacks stringCallbacks) throws JSONException {
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("project",jsb);
        jsonBody.put("filename","ikotlinrun.kt");

        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
              "https://try.kotlinlang.org/kotlinServer?type=run&runConf=java",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        stringCallbacks.onSuccess(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                stringCallbacks.onError(error);
            }
        }) {
            @Override
            public String getPostBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("project",jsb.toString().trim());
                params.put("filename","ikotlinrun.kt");
                return params;
            }

        };

        jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                20,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppSingleton.getInstance(context).addToRequestQueue(jsonObjRequest, "runCode");
    }

    public static Competition jsonToCompetition(JSONObject o){
        Competition competition = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(sdf.parse(o.getString("created")));
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String content="";

        try {

            if(o.has("content")) content=o.getString("content");

            competition = new Competition(o.getInt("id"),o.getString("user_id"),content,
                    cal,o.getInt("level"),o.getString("title"),o.getString("user_name"));

            if(o.has("user_picture")) competition.setProfile_picture(o.getString("user_picture"));
            if(o.has("solved")) competition.setSolved(o.getLong("solved")); else competition.setSolved(0);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("parsing_Compete_problem",o.toString());
        }
        return competition;
    }
    public static CompetitionAnswer jsonToCompetitionAnswer(JSONObject o){
        CompetitionAnswer competitionAnswer = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(sdf.parse(o.getString("created")));
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String content="";

        try {

            if(o.has("content")) content=o.getString("content");

            competitionAnswer = new CompetitionAnswer(o.getInt("id"),cal);
            if(o.has("idcompetition")) competitionAnswer.setId_competition(o.getInt("idcompetition"));
            if(o.has("competitiontitle")) competitionAnswer.setCompetition_title(o.getString("competitiontitle"));
            if(o.has("competitionlevel")) competitionAnswer.setCompetiton_level(o.getInt("competitionlevel"));
            if(o.has("user_id")) competitionAnswer.setId_user(o.getString("user_id"));
            if(o.has("user_name")) competitionAnswer.setUsername(o.getString("user_name"));
            if(o.has("user_picture")) competitionAnswer.setProfile_picture(o.getString("user_picture"));
            competitionAnswer.setContent(content);


        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("parsing_Compete_problem",o.toString());
        }
        return competitionAnswer;
    }
}
