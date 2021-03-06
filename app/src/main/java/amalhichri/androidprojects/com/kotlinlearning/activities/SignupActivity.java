package amalhichri.androidprojects.com.kotlinlearning.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.FirebaseAuth;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.rey.material.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.utils.Statics;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignupActivity extends Activity {

    private static boolean isFacebook= false;
    private LoginManager mLoginManager;
    private AccessTokenTracker mAccessTokenTracker;
    private CallbackManager mFacebookCallbackManager;
    private static String userEmail,userId,username,pictureUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_signup);

        //initializing facebook api
        facebookApiInit();

        (findViewById(R.id.emailSignupTxt)).setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)
                    ((EditText) findViewById(R.id.emailSignupTxt)).setError("Password is incorrect.");
                return false;
            }
        });
        (findViewById(R.id.emailSignupTxt)).setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override public void onFocusChange(    View v,    boolean hasFocus){
                if (hasFocus) ((EditText) findViewById(R.id.emailSignupTxt)).setError(null);
            }
        }
        );

        isFacebook= false;
    }

    /** Firebase Sign up  **/
    public void signUp(View v) {


        /** simple data validation ... */
        if (((EditText) findViewById(R.id.emailSignupTxt)).getText().toString().isEmpty()) {
            ((EditText) findViewById(R.id.emailSignupTxt)).setError("Email missing !");
            return;
        }
        if (((EditText) findViewById(R.id.pswSignupTxt)).getText().toString().isEmpty()) {
            ((EditText) findViewById(R.id.pswSignupTxt)).setError("Password missing");
            return;
        }
        if (!(((EditText) findViewById(R.id.pswSignupTxt)).getText().toString().isEmpty())
                && (((EditText) findViewById(R.id.pswSignupTxt)).getText().toString().length() < 6)) {
            ((EditText) findViewById(R.id.pswSignupTxt)).setError("Password should be at least 6 characters");
            return;
        }
        if (!(((EditText) findViewById(R.id.fullNameTxt)).getText().toString().isEmpty())
                && !(isFullName(((EditText) findViewById(R.id.fullNameTxt)).getText().toString()))) {
            ((EditText) findViewById(R.id.fullNameTxt)).setError("Please provide a full name");
            return;
        }
        /**authenticate user + add it to DB **/
        Statics.signUp(((EditText) findViewById(R.id.emailSignupTxt)).getText().toString(),
                ((EditText) findViewById(R.id.pswSignupTxt)).getText().toString(),
                ((EditText) findViewById(R.id.fullNameTxt)).getText().toString(),
                "",
                SignupActivity.this);
    }

    /** Linkedin signup **/
    public void signUpWithLinkedin(View v){
        String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,picture-url,email-address)";
        final APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(this, url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                /**  create User object from the linkedin profile **/
                try {
                    userEmail = apiResponse.getResponseDataAsJson().getString("emailAddress").toString();
                    userId = apiResponse.getResponseDataAsJson().getString("id").toString();
                    username =  apiResponse.getResponseDataAsJson().getString("firstName").toString()
                            +" "+apiResponse.getResponseDataAsJson().getString("lastName").toString();
                    pictureUrl = apiResponse.getResponseDataAsJson().getString("pictureUrl").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                 Toast.makeText(getApplicationContext(), "Success 1 "+userId+" "+userEmail, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onApiError(LIApiError liApiError) {
                Toast.makeText(getApplicationContext(), "Error! "+liApiError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        LISessionManager.getInstance(getApplicationContext()).init(this, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                /** signup to firebase with that user **/
                Statics.signUp(userEmail,userId,username,pictureUrl,SignupActivity.this);
            }
            @Override
            public void onAuthError(LIAuthError error) {
                Toast.makeText(getApplicationContext(), "failed " + error.toString(), Toast.LENGTH_LONG).show();
            }
        }, true);

    }
    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

    /** Facebook signup **/
    public void signUpWithFacebook(View v) {
        isFacebook = true;
        if (AccessToken.getCurrentAccessToken() != null) {
            mLoginManager.logOut();
        } else {
            mAccessTokenTracker.startTracking();
            mLoginManager.logInWithReadPermissions(SignupActivity.this, Arrays.asList("public_profile"));
        }
    }

    private void facebookApiInit() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        mLoginManager = LoginManager.getInstance();
        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,AccessToken currentAccessToken) {
            }
        };

        final LoginButton loginButton = findViewById(R.id.facebookSignInBtn);
        mFacebookCallbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("email","public_profile");
        loginButton.registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                isFacebook=true;
                final GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    if(object.isNull("email")){
                                        Statics.signUp(null,String.valueOf(object.getInt("id")),
                                                object.getString("first_name")+" "+object.getString("last_name"),
                                                "https://graph.facebook.com/v3.0"+String.valueOf(object.getInt("id"))+"/picture?type=large"
                                                ,SignupActivity.this);
                                       SignupActivity.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(SignupActivity.this, "Sign up failed! \n Please provide a Facebook account with email!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }else
                                        Statics.signUp(object.getString("email"),String.valueOf(object.getInt("id")),
                                                object.getString("first_name")+" "+object.getString("last_name"),
                                                "https://graph.facebook.com/v3.0"+String.valueOf(object.getInt("id"))+"/picture?type=large",
                                                SignupActivity.this);
                                } catch (JSONException e) {
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,email,picture");
                request.setParameters(parameters);
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        GraphResponse gResponse =request.executeAndWait();
                    }
                });
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                }
            }
            @Override
            public void onCancel() {
            }
            @Override
            public void onError(FacebookException error) {
            }
        });
    }

    /** onActivityResult **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(!isFacebook){
            LISessionManager.getInstance(getApplicationContext())
                    .onActivityResult(this,
                            requestCode, resultCode, data);
            isFacebook=false;
        }
        else
            mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        if(!(FirebaseAuth.getInstance().getCurrentUser()==null))
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
    }

    /** for calligraphy lib usage **/
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /*** helper methods ***/
    private boolean isFullName(String s){
        int j=0;
        for(int i=0;i<s.length();i++){
            if(s.charAt(i)==' ')
                j++;
        }
        if (j==1)
           return true;
        else
            return false;
    }

}
