package amalhichri.androidprojects.com.kotlinlearning.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import amalhichri.androidprojects.com.kotlinlearning.activities.HomeActivity;
import amalhichri.androidprojects.com.kotlinlearning.activities.LoginActivity;
import amalhichri.androidprojects.com.kotlinlearning.models.User;

/**
 * Created by Amal on 30/11/2017.
 */

public class Statics {

    /**
     *  to keep all static references
     *  to avoid instanciating them 2+ times in diffrents activitis/fragments
     */

    public static FirebaseAuth auth = FirebaseAuth.getInstance();
    public static FirebaseUser currentUser = auth.getCurrentUser();
    public  static DatabaseReference usersTable =  FirebaseDatabase.getInstance().getReference("users");


    public static void signUp(String email,String password,String fullName, final Activity activity){
        //authenticate user through firebase
        Statics.auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful())
                            Toast.makeText(activity, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        Toast.makeText(activity, "added to firebase ", Toast.LENGTH_LONG).show();
                        activity.startActivity(new Intent(activity, LoginActivity.class));
                    }
                });
        User userToAdd = new User();
        userToAdd.setEmailAddress(email);
        String[] splited =fullName.split("\\s+");
        userToAdd.setFirstName(splited[0]);userToAdd.setLastName(splited[1]);
        usersTable.child(usersTable.push().getKey()).setValue(userToAdd);
        Toast.makeText(activity, "added to database ", Toast.LENGTH_LONG).show();

        // add user to firebase DB
    }
    public static void signIn(String email, String password, final Activity activity){
        Statics.auth.signInWithEmailAndPassword(email,password).addOnCompleteListener
                (activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful())
                            Toast.makeText(activity, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        Toast.makeText(activity, "logged in", Toast.LENGTH_LONG).show();
                        activity.startActivity(new Intent(activity, HomeActivity.class));
                    }
                });
    }
}
