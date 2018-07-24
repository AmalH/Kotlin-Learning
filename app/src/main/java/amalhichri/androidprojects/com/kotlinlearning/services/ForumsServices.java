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

import amalhichri.androidprojects.com.kotlinlearning.models.ForumAnswer;
import amalhichri.androidprojects.com.kotlinlearning.models.ForumQuestion;
import amalhichri.androidprojects.com.kotlinlearning.utils.AppSingleton;

/**
 * Created by Amal on 26/11/2017.
 */

public class ForumsServices {


    private static ForumsServices this_ = new ForumsServices();


    public static synchronized ForumsServices getInstance()
    {
        if (this_ ==null) this_ =new ForumsServices();
        return this_;
    }

    public void getTopForumPosts(String id, Context context, int start, String search, int orderby , final ServerCallbacks serverCallbacks){
        final JSONObject jsonBody = new JSONObject();
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, "http://192.168.43.166/ikotlinBackEnd/web/forums/getAllQuestions?id="+id+"&start="+start+"&orderby="+orderby+"&keysearch="+search, jsonBody, new Response.Listener<JSONObject>() {

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
                    5000,
                    3,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "getForums");
    }

    public void addForumPost(Context context, ForumQuestion forumQuestion, String id, final ServerCallbacks serverCallbacks){
        Map<String, String> m = new HashMap<String, String>();
        m.put("id", id);
        m.put("subject", forumQuestion.getSubject());
        m.put("content", forumQuestion.getContent());
        m.put("tags", forumQuestion.getTags());
        if(forumQuestion.getCode()!=null) m.put("code", forumQuestion.getCode());
        final JSONObject jsonBody = new JSONObject(m);
        Log.d("body",jsonBody.toString());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, "http://192.168.43.166/ikotlinBackEnd/web/forums/addQuestion", jsonBody, new Response.Listener<JSONObject>() {

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

    public void editForumPost(Context context, ForumQuestion forumQuestion, String id, final ServerCallbacks serverCallbacks){
        Map<String, String> m = new HashMap<String, String>();
        m.put("id", id);
        m.put("questionId",String.valueOf(forumQuestion.getId()));
        m.put("subject", forumQuestion.getSubject());
        m.put("content", forumQuestion.getContent());
        m.put("tags", forumQuestion.getTags());
        if(forumQuestion.getCode()!=null)
            m.put("code", forumQuestion.getCode());
        final JSONObject jsonBody = new JSONObject(m);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, "http://192.168.43.166/ikotlinBackEnd/web/forums/editQuestion", jsonBody, new Response.Listener<JSONObject>() {

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
                5000,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "AddForumFragment");
    }

    public void getForumPostComments(String id, Context context, int start, int questionId, final ServerCallbacks serverCallbacks){
        final JSONObject jsonBody = new JSONObject();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, "http://192.168.43.166/ikotlinBackEnd/web/forums/getCommments?id="+id+"&questionId="+questionId+"&start="+start, jsonBody, new Response.Listener<JSONObject>() {

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

    public void addForumPostAnswer(Context context, String content, int questionId, String id, final ServerCallbacks serverCallbacks){
        Map<String, String> m = new HashMap<String, String>();
        m.put("id", id);
        m.put("questionId", String.valueOf(questionId));
        m.put("commentcontent", content);
        final JSONObject jsonBody = new JSONObject(m);
        Log.d("body",jsonBody.toString());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, "http://192.168.43.166/ikotlinBackEnd/web/forums/addComment", jsonBody, new Response.Listener<JSONObject>() {

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

    public void upvoteForumPost(String id, Context context, int questionId, final ServerCallbacks serverCallbacks){
        final JSONObject jsonBody = new JSONObject();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, "http://192.168.43.166/ikotlinBackEnd/web/forums/questionUpvotes?id="+id+"&questionId="+questionId, jsonBody, new Response.Listener<JSONObject>() {

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

    public void downvoteForumPost(String id, Context context, int questionId, final ServerCallbacks serverCallbacks){
        final JSONObject jsonBody = new JSONObject();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, "http://192.168.43.166/ikotlinBackEnd/web/forums/questionDownvotes?id="+id+"&questionId="+questionId, jsonBody, new Response.Listener<JSONObject>() {

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


    public void upvoteForumPostComment(String id, Context context, int commentid, final ServerCallbacks serverCallbacks){
        final JSONObject jsonBody = new JSONObject();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, "http://192.168.43.166/ikotlinBackEnd/web/forums/getCommentUpvotes?id="+id+"&commentid="+commentid, jsonBody, new Response.Listener<JSONObject>() {

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

    public void downvoteForumPostComment(String id, Context context, int commentid, final ServerCallbacks serverCallbacks){
        final JSONObject jsonBody = new JSONObject();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, "http://192.168.43.166/ikotlinBackEnd/web/forums/getCommentDownvotes?id="+id+"&commentid="+commentid, jsonBody, new Response.Listener<JSONObject>() {

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

    public void markSeenForum(String id, Context context, int questionId, final ServerCallbacks serverCallbacks){
        final JSONObject jsonBody = new JSONObject();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, "http://192.168.43.166/ikotlinBackEnd/web/forums/markQuestionAsSeen?id="+id+"&questionId="+questionId, jsonBody, new Response.Listener<JSONObject>() {

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

    public void getForumPost(String id, Context context, int questionId, final ServerCallbacks serverCallbacks){
        final JSONObject jsonBody = new JSONObject();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, "http://192.168.43.166/ikotlinBackEnd/web/forums/getSingleQuestion?id="+id+"&questionId="+questionId, jsonBody, new Response.Listener<JSONObject>() {

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

        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "getForumPost");
    }

    public void deleteForumPost(String id, Context context, int questionId, final ServerCallbacks serverCallbacks){
        final JSONObject jsonBody = new JSONObject();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, "http://192.168.43.166/ikotlinBackEnd/web/forums/deleteQuestion?id="+id+"&questionId="+questionId, jsonBody, new Response.Listener<JSONObject>() {

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

    public void deleteComment(String id, Context context, int commentid, final ServerCallbacks serverCallbacks){
        final JSONObject jsonBody = new JSONObject();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, "http://192.168.43.166/ikotlinBackEnd/web/forums/deleteComment?id="+id+"&commentid="+commentid, jsonBody, new Response.Listener<JSONObject>() {

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

    public static ForumQuestion jsonToForumQuestion(JSONObject o){
        ForumQuestion f = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
        Calendar cal = Calendar.getInstance()   ;
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

    public static ForumAnswer jsonToForumAnswer(JSONObject o){
        ForumAnswer a = null;
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
                a = new ForumAnswer(o.getInt("id"),o.getString("content"),cal,o.getLong("rating"),o.getString("user_id"),o.getString("user_name"),o.getString("user_picture"));
            else
                a = new ForumAnswer(o.getInt("id"),o.getString("content"),cal,o.getLong("rating"),o.getString("user_id"),o.getString("user_name"));
            //Log.d("parsing_forum",f.getId()+"");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("parsing_forum","Problem parsing data from server, please report");
        }
        return a;
    }


    /**public void getusersForums(String id, Context context, int start, final ServerCallbacks serverCallbacks){
        final JSONObject jsonBody = new JSONObject();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, IP + getCurrentUserQuestions +"?id="+id+"&start="+start, jsonBody, new Response.Listener<JSONObject>() {

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
    }*/

}
