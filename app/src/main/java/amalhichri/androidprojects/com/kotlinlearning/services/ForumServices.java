package amalhichri.androidprojects.com.kotlinlearning.services;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import amalhichri.androidprojects.com.kotlinlearning.models.Answer;
import amalhichri.androidprojects.com.kotlinlearning.models.ForumQuestion;
import amalhichri.androidprojects.com.kotlinlearning.utils.AppSingleton;

/**
 * Created by Amal on 26/11/2017.
 */

public class ForumServices {
    public static String IP;
    public static final String URL_FORUM_HOME= "forum/getforums";
    public static final String URL_FORUM_ADD= "forum/addforum";
    public static final String URL_COMMENTS_GET= "forum/getforumcommments";
    public static final String URL_COMMENT_ADD= "forum/addcomment";
    public static final String URL_UPVOTE_FORUM="forum/forum/upvote";
    public static final String URL_DOWNVOTE_FORUM="forum/forum/downvote";
    public static final String URL_UPVOTE_COMMENT="forum/comment/upvote";
    public static final String URL_DOWNVOTE_COMMENT="forum/comment/downvote";
    public static final String URL_MARKVIEW_FORUM="forum/markview";
    public static final String URL_GET_SINGLE_FORUM="forum/getsignleforum";
    public static final String URL_SAVE_FORUM="forum/edit";
    public static final String URL_DELETE_FORUM="forum/delete";
    public static final String URL_DELETE_COMMENT="comment/delete";
    public static final String URL_USERS_FORUMS="forum/getmine";

    /** Constructeur privé */

    //http://ikotlin.000webhostapp.com/web/
    private ForumServices()
    {
        IP= "http://41.226.11.243:10080/ikotlin/public_html/web/app.php/";
    }

    /** Instance unique pré-initialisée */
    private static ForumServices INSTANCE = new ForumServices();

    /** Point d'accès pour l'instance unique du singleton */
    public static synchronized ForumServices getInstance()
    {
        if (INSTANCE==null) INSTANCE=new ForumServices();
        return INSTANCE;
    }

    public void getTopForums(String id, Context context, int start, String search, int orderby , final ServerCallbacks serverCallbacks){
        final JSONObject jsonBody = new JSONObject();

            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, IP + URL_FORUM_HOME+"?id="+id+"&start="+start+"&orderby="+orderby+"&keysearch="+search, jsonBody, new Response.Listener<JSONObject>() {

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

            AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "getForums");
    }

    public static ForumQuestion parse_(JSONObject o){
        ForumQuestion f = null;
        //datetimeparser
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
            ///Log.d("forum",o.toString());
            if(o.has("content")) content=o.getString("content");

                if(o.has("user_picture"))
                f = new ForumQuestion(o.getInt("id"),o.getString("subject"),
                        o.getInt("rating"),o.getString("tags"),cal,content,
                        o.getInt("views"),o.getString("user_id"),o.getString("user_name"),
                        o.getString("user_picture"));
                else
                    f = new ForumQuestion(o.getInt("id"),o.getString("subject"),
                            o.getInt("rating"),o.getString("tags"),cal,content,
                            o.getInt("views"),o.getString("user_id"),o.getString("user_name"),
                            null);

            if(o.has("code")) f.setCode(o.getString("code"));

            if(o.has("edited")) {
                Calendar edited = Calendar.getInstance();
                try {
                    edited.setTime(sdf.parse(o.getString("edited")));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                f.setEdited(edited);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("parsing_forum","Problem parsing data from server, please report");
        }
        return f;
    }

    public void addForum(Context context, ForumQuestion f, String id, final ServerCallbacks serverCallbacks){
        Map<String, String> m = new HashMap<String, String>();
        m.put("id", id);
        m.put("subject", f.getSubject());
        m.put("content", f.getContent());
        m.put("tags", f.getTags());
        if(f.getCode()!=null) m.put("code", f.getCode());
        final JSONObject jsonBody = new JSONObject(m);
        Log.d("body",jsonBody.toString());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, IP + URL_FORUM_ADD, jsonBody, new Response.Listener<JSONObject>() {

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

        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "AddForumFragment");
    }

    public void editForum(Context context, ForumQuestion f, String id, final ServerCallbacks serverCallbacks){
        Map<String, String> m = new HashMap<String, String>();
        m.put("id", id);
        m.put("idforum", f.getId()+"");
        Log.e("idf",f.getId()+"");
        m.put("subject", f.getSubject());
        m.put("content", f.getContent());
        m.put("tags", f.getTags());
        if(f.getCode()!=null) m.put("code", f.getCode());
        final JSONObject jsonBody = new JSONObject(m);
        Log.d("body",jsonBody.toString());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, IP + URL_SAVE_FORUM, jsonBody, new Response.Listener<JSONObject>() {

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

        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "AddForumFragment");
    }

    public void getComments(String id, Context context, int start, int forumId, final ServerCallbacks serverCallbacks){
        final JSONObject jsonBody = new JSONObject();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, IP + URL_COMMENTS_GET+"?id="+id+"&forumid="+forumId+"&start="+start, jsonBody, new Response.Listener<JSONObject>() {

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

        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "getForums");
    }


    public static Answer Answerparse_(JSONObject o){
        Answer a = null;
        //datetimeparser
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(sdf.parse(o.getString("created")));
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            if(o.has("user_picture"))
                a = new Answer(o.getInt("id"),o.getString("content"),cal,o.getLong("rating"),o.getString("user_id"),o.getString("user_name"),o.getString("user_picture"));
            else
                a = new Answer(o.getInt("id"),o.getString("content"),cal,o.getLong("rating"),o.getString("user_id"),o.getString("user_name"));
            //Log.d("parsing_forum",f.getId()+"");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("parsing_forum","Problem parsing data from server, please report");
        }
        return a;
    }


    public void addAnswer(Context context, String content, int idforum, String id, final ServerCallbacks serverCallbacks){
        Map<String, String> m = new HashMap<String, String>();
        m.put("id", id);
        m.put("forumid", String.valueOf(idforum));
        m.put("commentcontent", content);
        final JSONObject jsonBody = new JSONObject(m);
        Log.d("body",jsonBody.toString());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, IP + URL_COMMENT_ADD, jsonBody, new Response.Listener<JSONObject>() {

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

        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "AddAnswerFragment");
    }

    public void upvoteForum (String id, Context context, int forumId, final ServerCallbacks serverCallbacks){
        final JSONObject jsonBody = new JSONObject();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, IP + URL_UPVOTE_FORUM+"?id="+id+"&forumid="+forumId, jsonBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        if (!response.has("Error")){
                            //ok
                            try {
                                if(!response.getString("resp").equals("no"))
                                serverCallbacks.onSuccess(response);
                                else  serverCallbacks.onWrong(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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

        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "getForums");
    }

    public void downvoteForum (String id, Context context, int forumId, final ServerCallbacks serverCallbacks){
        final JSONObject jsonBody = new JSONObject();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, IP + URL_DOWNVOTE_FORUM+"?id="+id+"&forumid="+forumId, jsonBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        if (!response.has("Error")){
                            //ok
                            try {
                                if(!response.getString("resp").equals("no"))
                                    serverCallbacks.onSuccess(response);
                                else  serverCallbacks.onWrong(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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

        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "getForums");
    }


    public void upvoteComment (String id, Context context, int commentid, final ServerCallbacks serverCallbacks){
        final JSONObject jsonBody = new JSONObject();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, IP + URL_UPVOTE_COMMENT+"?id="+id+"&commentid="+commentid, jsonBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        if (!response.has("Error")){
                            //ok
                            try {
                                if(!response.getString("resp").equals("no"))
                                    serverCallbacks.onSuccess(response);
                                else  serverCallbacks.onWrong(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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

        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "getForums");
    }

    public void downvoteComment (String id, Context context, int commentid, final ServerCallbacks serverCallbacks){
        final JSONObject jsonBody = new JSONObject();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, IP + URL_DOWNVOTE_COMMENT+"?id="+id+"&commentid="+commentid, jsonBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        if (!response.has("Error")){
                            //ok
                            try {
                                if(!response.getString("resp").equals("no"))
                                    serverCallbacks.onSuccess(response);
                                else  serverCallbacks.onWrong(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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

        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "getForums");
    }

    public void markViewForum (String id, Context context, int forumid, final ServerCallbacks serverCallbacks){
        final JSONObject jsonBody = new JSONObject();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, IP + URL_MARKVIEW_FORUM+"?id="+id+"&forumid="+forumid, jsonBody, new Response.Listener<JSONObject>() {

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

        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "markforumView");
    }

    public void getForum (String id, Context context, int forumid, final ServerCallbacks serverCallbacks){
        final JSONObject jsonBody = new JSONObject();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, IP + URL_GET_SINGLE_FORUM+"?id="+id+"&forumid="+forumid, jsonBody, new Response.Listener<JSONObject>() {

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

        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "getForum");
    }

    public void delForum (String id, Context context, int forumid, final ServerCallbacks serverCallbacks){
        final JSONObject jsonBody = new JSONObject();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, IP + URL_DELETE_FORUM+"?id="+id+"&forumid="+forumid, jsonBody, new Response.Listener<JSONObject>() {

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

        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "deleteForum");
    }

    public void delComment(String id, Context context, int commentid, final ServerCallbacks serverCallbacks){
        final JSONObject jsonBody = new JSONObject();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, IP + URL_DELETE_COMMENT+"?id="+id+"&commentid="+commentid, jsonBody, new Response.Listener<JSONObject>() {

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

        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "deleteComment");
    }

    public void getusersForums(String id, Context context, int start, final ServerCallbacks serverCallbacks){
        final JSONObject jsonBody = new JSONObject();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, IP + URL_USERS_FORUMS+"?id="+id+"&start="+start, jsonBody, new Response.Listener<JSONObject>() {

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

        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "getUsersForums");
    }

}
