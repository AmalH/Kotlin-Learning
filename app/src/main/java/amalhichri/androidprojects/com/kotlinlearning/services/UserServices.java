package amalhichri.androidprojects.com.kotlinlearning.services;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import amalhichri.androidprojects.com.kotlinlearning.models.UserDb;
import amalhichri.androidprojects.com.kotlinlearning.utils.AppSingleton;
import amalhichri.androidprojects.com.kotlinlearning.utils.DataBaseHandler;

/**
 * Created by Amal on 19/11/2017.
 */

public class UserServices {

    public static String IP;
    public static final String URL_RGISTER= "user/register";
    public static final String URL_LOGIN= "user/authentification";
    public static final String URL_SET_PROFILE_PICTURE="user/setprofilepicture";
    public static final String URL_SET_USERNAME="user/setusername";

    /** Constructeur privé */
    private UserServices()
    {
        IP= "http://41.226.11.243:10080/ikotlin/public_html/web/app.php/";
    }

    /** Instance unique pré-initialisée */
    private static UserServices INSTANCE = new UserServices();

    /** Point d'accès pour l'instance unique du singleton */
    public static synchronized UserServices getInstance()
    {	return INSTANCE;
    }

    public void registerUserWebService(String id, String username, String email, Context context , final ServerCallbacks serverCallbacks){
        Map<String, String> m = new HashMap<String, String>();
        m.put("id", id);
        m.put("username", username);
        m.put("email", email);
        final JSONObject jsonBody = new JSONObject(m);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, IP + URL_RGISTER, jsonBody, new Response.Listener<JSONObject>() {

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
                10000,//timeout
                3,//retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "Register");
    }

    public void markLoggedUserWebService(String id, Context context , final ServerCallbacks serverCallbacks){
        final JSONObject jsonBody = new JSONObject();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, IP + URL_LOGIN+"?id="+id, jsonBody, new Response.Listener<JSONObject>() {

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
                10000,//timeout
                3,//retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "logging");
    }

    public void logout(Context context){
        DataBaseHandler.getInstance(context).deleteUser();
    }

    public UserDb get_user_from_json(JSONObject json){
        //datetimeparser
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
        Calendar cal = Calendar.getInstance();

        UserDb user = new UserDb();
        try {
            JSONObject uj=json.getJSONObject("user");
            user.setId(uj.getString("id"));
            user.setUsername(uj.getString("username"));
            user.setEmail(uj.getString("email"));
            try {
                cal.setTime(sdf.parse(uj.getString("created")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            user.setCreated(cal);

            try {
                cal.setTime(sdf.parse(uj.getString("lastlogged")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            user.setLast_loggued(cal);
            user.setSkill_learner(uj.getInt("skill_learner"));
            user.setSkill_challenger(uj.getInt("skill_challenger"));
            user.setSkill_coder(uj.getInt("skill_coder"));

            user.setPictureUrl(uj.getString("picture"));
            user.setConfirmed(uj.getInt("confirmed")!=0);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    public Boolean isFacebooklogged(Context context){
        for (UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (user.getProviderId().equals("facebook.com")) {
                return true;
            }
        }
        return false;
    }

    public Boolean is_verified(final Context context){
        if(DataBaseHandler.getInstance(context).getUser().isConfirmed()) return true;
        else{
            for (UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                if (user.getProviderId().equals("facebook.com")) {
                    DataBaseHandler.getInstance(context).setUserConfirmed(true);
                    return true;
                }
            }
            if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                DataBaseHandler.getInstance(context).setUserConfirmed(true);
                return true;
            }
            else {
                //Toast.makeText(context, "Verifing", Toast.LENGTH_LONG).show();
                FirebaseAuth.getInstance().getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
                            Toast.makeText(context, "Please confirm your email before commenting !", Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                            Toast.makeText(context, "A confirmation link has been sent to your account !\nPlease refresh and try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        return false;
    }

    public Drawable getEmptyProfimePicture(String item){
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        int color = generator.getColor(item);
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .width(60)  // width in px
                .height(60) // height in px
                .endConfig()
                .buildRect(String.valueOf(item.charAt(0)).toUpperCase()+ String.valueOf(item.charAt(1)), color);
        return drawable;
    }

    public Drawable getRatingINPicture(String item){
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        int color = generator.getColor(item);
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .width(60)  // width in px
                .height(60) // height in px
                .endConfig()
                .buildRect(item, color);
        return drawable;
    }


    public void changeProfilePicture(String id, String picture, Context context , final ServerCallbacks serverCallbacks){
        Map<String, String> m = new HashMap<String, String>();
        m.put("id", id);
        m.put("profile_picture", picture);
        final JSONObject jsonBody = new JSONObject(m);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, IP + URL_SET_PROFILE_PICTURE, jsonBody, new Response.Listener<JSONObject>() {

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
                10000,//timeout
                3,//retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "pic");
    }

    public void changeUsername(String id, String username, Context context , final ServerCallbacks serverCallbacks){
        Map<String, String> m = new HashMap<String, String>();
        m.put("id", id);
        m.put("username", username);
        final JSONObject jsonBody = new JSONObject(m);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, IP + URL_SET_USERNAME, jsonBody, new Response.Listener<JSONObject>() {

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
                10000,//timeout
                3,//retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppSingleton.getInstance(context).addToRequestQueue(jsObjRequest, "name");
    }

}
