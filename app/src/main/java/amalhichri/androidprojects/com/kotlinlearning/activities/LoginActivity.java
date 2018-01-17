package amalhichri.androidprojects.com.kotlinlearning.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;

import java.util.Arrays;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.models.User;
import amalhichri.androidprojects.com.kotlinlearning.utils.Statics;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class LoginActivity extends Activity {

    private static boolean isFacebook= false;
    private CallbackManager mFacebookCallbackManager;
    private LoginManager mLoginManager;
    private AccessTokenTracker mAccessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        facebookApiInit();
        setContentView(R.layout.activity_login);

    }

    /** Firebase login **/
  public void login(View view) {

      /** simple data validation ... **/
      if (((EditText) findViewById(R.id.emailLoginTxt)).getText().toString().trim().isEmpty()) {
          ((EditText) findViewById(R.id.emailLoginTxt)).setError("email missing");
          (findViewById(R.id.emailLoginTxt)).requestFocus();
          return;
      }
      if(((ShowHidePasswordEditText) findViewById(R.id.pswLoginTxt)).getText().toString().isEmpty()){
          ((ShowHidePasswordEditText)findViewById(R.id.pswLoginTxt)).setError("password missing");
          (findViewById(R.id.pswLoginTxt)).requestFocus();
          return;
      }
      if (!(((ShowHidePasswordEditText) findViewById(R.id.pswLoginTxt)).getText().toString().isEmpty())&&(((ShowHidePasswordEditText)findViewById(R.id.pswLoginTxt)).getText().toString().length()<6)) {
          ((ShowHidePasswordEditText)findViewById(R.id.pswLoginTxt)).setError("Password should be at least 6 characters");
          (findViewById(R.id.pswLoginTxt)).requestFocus();
          return;
      }
      Statics.signIn(((EditText) findViewById(R.id.emailLoginTxt)).getText().toString(), ((ShowHidePasswordEditText) findViewById(R.id.pswLoginTxt)).getText().toString(),LoginActivity.this);
  }


    /** password recovery **/
    public void resetPassword(View v) {
        if (((EditText)findViewById(R.id.emailLoginTxt)).getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please provide your email\nto send you password recovery info.", Toast.LENGTH_LONG).show();
        } else {
            FirebaseAuth.getInstance().sendPasswordResetEmail(((EditText)findViewById(R.id.emailLoginTxt)).getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "A password recovery email has been sent to "+((EditText)findViewById(R.id.emailLoginTxt)).getText().toString(), Toast.LENGTH_LONG).show();
                            } else
                                Toast.makeText(getApplicationContext(), "No such email KotlinLearn database, please provide your email!", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    /** Facebook login **/
    public void loginWithFacebook(View v) {
        isFacebook = true;
        if (AccessToken.getCurrentAccessToken() != null) {
            mLoginManager.logOut();
        } else {
            mAccessTokenTracker.startTracking();
            mLoginManager.logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile"));
        }
    }
    private void facebookApiInit() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,AccessToken currentAccessToken) {
            }
        };
        mLoginManager = LoginManager.getInstance();
        mFacebookCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }


    /** Linkedin login **/
    public void loginWithLinkedin(View v){
        // initializing connection to linkedin api
        LISessionManager.getInstance(getApplicationContext()).init(this, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
               // Toast.makeText(getApplicationContext(), "auth succes", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAuthError(LIAuthError error) {
                //Toast.makeText(getApplicationContext(), " auth failure" + error.toString(), Toast.LENGTH_LONG).show();
            }
        }, true);

        // managing api responses
        String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,picture-url,email-address)";
        final APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(this, url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                // create User object from the linkedin profile
                Gson gson = new Gson();
                final User userFromLinkedIn = gson.fromJson(apiResponse.getResponseDataAsJson().toString(),User.class);
                // login to firebase with that user
                Statics.signIn(userFromLinkedIn.getEmailAddress(), userFromLinkedIn.getId(),LoginActivity.this);
            }

            @Override
            public void onApiError(LIApiError liApiError) {
                // Toast.makeText(getApplicationContext(), "cant connect to linkedin" + error.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // asking for linkedin account info retreive permission
    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
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
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
    }

    /** for calligraphy lib usage **/
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}