package amalhichri.androidprojects.com.kotlinlearning.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Iterator;

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
    public  static DatabaseReference usersTable =  FirebaseDatabase.getInstance().getReference("users");


    public static void signUp(final String email, String password, final String fullName, final Activity activity){
        // we'll use a fullName in signup ui we're not providing firstName / lastName editTe
        //authenticate user through firebase
        Statics.auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful())
                            Toast.makeText(activity, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        else{
                            Toast.makeText(activity, "added to firebase ", Toast.LENGTH_LONG).show();
                            activity.startActivity(new Intent(activity, LoginActivity.class));
                            User userToAdd = new User();
                            userToAdd.setEmailAddress(email);
                            String[] splited =fullName.split("\\s+");
                            userToAdd.setFirstName(splited[0]);userToAdd.setLastName(splited[1]);
                            usersTable.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userToAdd);
                            Toast.makeText(activity, "added to database ", Toast.LENGTH_LONG).show();
                        }

                    }
                });


        // add user to firebase DB
    }
    public static void signIn(String email, String password, final Activity activity){
        Statics.auth.signInWithEmailAndPassword(email,password).addOnCompleteListener
                (activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(activity, "logged in", Toast.LENGTH_LONG).show();
                            activity.startActivity(new Intent(activity, HomeActivity.class));
                        }
                        else {
                            Toast.makeText(activity, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /** firebase methods **/
    public static void findUserByEmail(String email) {
        usersTable.orderByChild("emailAddress").equalTo(email).addChildEventListener(new ChildEventListener() {
            @
                    Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Iterable snapshotIterator = dataSnapshot.getChildren();
                Iterator iterator = snapshotIterator.iterator();
                while ((iterator.hasNext())) {
                    Log.d("Item found: ", iterator.next().toString() + "---");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    }
