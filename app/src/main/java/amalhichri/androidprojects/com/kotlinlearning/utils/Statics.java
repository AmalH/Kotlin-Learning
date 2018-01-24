package amalhichri.androidprojects.com.kotlinlearning.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.rey.material.widget.EditText;

import amalhichri.androidprojects.com.kotlinlearning.activities.HomeActivity;
import amalhichri.androidprojects.com.kotlinlearning.models.User;

/**
 * Created by Amal on 30/11/2017.
 */

public class Statics {

    /**
     * to keep all static references
     * to avoid instanciating them 2+ times in diffrents activitis/fragments
     */

    public static FirebaseAuth auth = FirebaseAuth.getInstance();
    public static DatabaseReference usersTable = FirebaseDatabase.getInstance().getReference("users");


    public static void signUp(final String email, String password, final String fullName, final String pictureUrl, final Activity activity) {
        // we'll use a fullName in signup ui we're not providing firstName / lastName editTe
        //authenticate user through firebase
        Statics.auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // add user to database
                        Log.d("Test","Facebook to firebase success");
                        User userToAdd = new User();
                        userToAdd.setEmailAddress(email);
                        String[] splited = fullName.split("\\s+");
                        userToAdd.setFirstName(splited[0]);
                        userToAdd.setLastName(splited[1]);
                        userToAdd.setPictureUrl(pictureUrl);
                        usersTable.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userToAdd).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Failure",e.getMessage());
                            }
                        });
                        Toast.makeText(activity, "added to database ", Toast.LENGTH_LONG).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("Test","Facebook ato firebase success");

            }
        });
    }

    public static void signIn(String email, String password, final Activity activity) {
        Statics.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener
                (activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(activity, "logged in", Toast.LENGTH_LONG).show();
                            activity.startActivity(new Intent(activity, HomeActivity.class));
                        } else {
                            Toast.makeText(activity, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // this will be re-used alot in the whole project !
    public static User getLoggedUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("loggedUserPrefs", 0);
        User user = (new Gson()).fromJson(prefs.getString("user", null), User.class);
        return user;
    }

    // to set material editTexts focus listners / error messages
    public static void setErrorText(final EditText editText, final String errorMsg){
        editText.setOnKeyListener(new View.OnKeyListener(){

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)
                    editText.setError(errorMsg);
                return false;
            }
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override public void onFocusChange(    View v,    boolean hasFocus){
                if (hasFocus) editText.setError(null);
            }
        }
        );
    }
}