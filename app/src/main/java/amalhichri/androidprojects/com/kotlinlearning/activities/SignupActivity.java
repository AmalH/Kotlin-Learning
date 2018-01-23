package amalhichri.androidprojects.com.kotlinlearning.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.gson.Gson;
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
import amalhichri.androidprojects.com.kotlinlearning.models.User;
import amalhichri.androidprojects.com.kotlinlearning.utils.Statics;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignupActivity extends Activity {

    static boolean isFacebook= false;
    private LoginManager mLoginManager;
    private AccessTokenTracker mAccessTokenTracker;
    private CallbackManager mFacebookCallbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        //initializing facebook api
        facebookApiInit();

    }


    /** Sign up  **/
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
        if (((EditText) findViewById(R.id.fullNameTxt)).getText().toString().isEmpty()) {
            ((EditText) findViewById(R.id.fullNameTxt)).setError("Full name missing");
            return;
        }
        if (!(((EditText) findViewById(R.id.fullNameTxt)).getText().toString().isEmpty())
                && !(isFullName(((EditText) findViewById(R.id.fullNameTxt)).getText().toString()))) {
            ((EditText) findViewById(R.id.fullNameTxt)).setError("Please provide your full name");
            return;
        }
        //authenticate user + add it to firebase DB ..
        Statics.signUp(((EditText) findViewById(R.id.emailSignupTxt)).getText().toString(),
                ((EditText) findViewById(R.id.pswSignupTxt)).getText().toString(),
                ((EditText) findViewById(R.id.fullNameTxt)).getText().toString(),
                SignupActivity.this);
    }


    /** Sign up with linkedin **/
    public void signUpWithLinkedin(View v){
        // initializing connection to linkedin api
        LISessionManager.getInstance(getApplicationContext()).init(this, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                //Toast.makeText(getApplicationContext(), "auth succeded ", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAuthError(LIAuthError error) {
                //Toast.makeText(getApplicationContext(), "failed " + error.toString(), Toast.LENGTH_LONG).show();
            }
        }, true);

        // managing api responses
        String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,picture-url,email-address)";
        final APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(this, url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                /**  create User object from the linkedin profile **/
                Gson gson = new Gson();
                final User userFromLinkedIn = gson.fromJson(apiResponse.getResponseDataAsJson().toString(),User.class);
                /** signup to firebase with that user **/
                Statics.signUp(userFromLinkedIn.getEmailAddress(),userFromLinkedIn.getId(),userFromLinkedIn.getFirstName()+" "+userFromLinkedIn.getLastName(),SignupActivity.this);
            }

            @Override
            public void onApiError(LIApiError liApiError) {
                Toast.makeText(getApplicationContext(), "cant connect to Linkedin", Toast.LENGTH_LONG).show();
            }
        });
    }
    // asking for linkedin account info retreive permission
    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

    /** sign up with facebook **/
    public void signUpWithFacebook(View v) {
        isFacebook = true;
        if (AccessToken.getCurrentAccessToken() != null) {
            mLoginManager.logOut();
        } else {
            mAccessTokenTracker.startTracking();
            mLoginManager.logInWithReadPermissions(SignupActivity.this, Arrays.asList("public_profile"));
        }
    }
    // to initialize facebook api + retrieve user info
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
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                try {
                                    //if(object.getString("email").isEmpty())
                                        //Toast.makeText(getApplicationContext(), "No email adress linked to this facebook account !\nPlease use an account with email info.", Toast.LENGTH_LONG).show();
                                   // else
                                        // we would signup with object.getString("email") as an emaill,
                                        // but I added "test.email@gmail.com" for test ( as am using a phone number base fb account for tests )
                                        Statics.signUp("test.email@gmail.com",String.valueOf(object.getInt("id")),object.getString("first_name")+" "+object.getString("last_name"), SignupActivity.this);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,email,picture");
                request.setParameters(parameters);
                request.executeAsync();
            }
            @Override
            public void onCancel() {
            }
            @Override
            public void onError(FacebookException error) {
                Log.d("ERROR",error.toString());
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
        startActivity(new Intent(SignupActivity.this, HomeActivity.class));
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
