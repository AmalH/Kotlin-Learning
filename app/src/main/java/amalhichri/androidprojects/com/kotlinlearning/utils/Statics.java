package amalhichri.androidprojects.com.kotlinlearning.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.rey.material.app.Dialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.activities.HomeActivity;
import amalhichri.androidprojects.com.kotlinlearning.adapters.CoursesListAdapter;
import amalhichri.androidprojects.com.kotlinlearning.models.User;
import amalhichri.androidprojects.com.kotlinlearning.models.UserDb;
import amalhichri.androidprojects.com.kotlinlearning.services.ServerCallbacks;
import amalhichri.androidprojects.com.kotlinlearning.services.UserProfileServices;

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
    public static DatabaseReference takenCoursesTable = FirebaseDatabase.getInstance().getReference().child("takenCourses");
    public static DatabaseReference startedChaptersTable = FirebaseDatabase.getInstance().getReference().child("startedChapters");
    static List<String> courses=new ArrayList<>();
    static HashMap<String,List> chapters= new HashMap<>();


    /** user signup **/
    public static void signUp(final String email, String password, final String fullName, final String pictureUrl, final Activity activity) {
        // we'll use a fullName in signup ui we're not providing firstName / lastName editTexts
        //authenticate user through firebase
        Statics.auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // add user to database
                        //Log.d("Test","Facebook to firebase success");
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
                        //
                        String idUser=auth.getCurrentUser().getUid();
                        // save user to mysql database and start the next activity
                        UserProfileServices.getInstance().registerUserWebService(idUser,fullName,email,activity,new ServerCallbacks(){

                            @Override
                            public void onSuccess(JSONObject result) {
                                //send email verification

                                //clear backstack
                                activity.finishAffinity();
                                //start intent
                                UserDb user;
                                user=UserProfileServices.getInstance().get_user_from_json(result);
                                DataBaseHandler.getInstance(activity).saveUser(user);
                                // FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                                Intent intent = new Intent(activity, HomeActivity.class);
                                activity.startActivity(intent);

                                activity.finish();

                            }

                            @Override
                            public void onError(VolleyError result) {
                                auth.getCurrentUser().delete();
                                Toast.makeText(activity,"Error while registring please retry",Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onWrong(JSONObject result) {
                                auth.getCurrentUser().delete();
                                Toast.makeText(activity,"Error while registring please retry",Toast.LENGTH_LONG).show();
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
    /** user signin **/
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
    /** for user session **/
    // this will be re-used alot in the whole project !
    public static User getLoggedUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("loggedUserPrefs", 0);
        User user = (new Gson()).fromJson(prefs.getString("user", null), User.class);
        return user;
    }




    public static Dialog createCoursesListDialog(Context context){   /** attached listcourses_view ui to the dialog (check CoursesListAdapter..)**/
        Dialog dialog = new Dialog(context);
        int[] icons = new int[]{R.drawable.ic_overview,R.drawable.ic_start,R.drawable.ic_basics,R.drawable.ic_classesobjects,
                R.drawable.ic_functions,R.drawable.ic_others,R.drawable.ic_java,R.drawable.ic_javascript};
        dialog.setContentView(R.layout.listcourses_view);
        ExpandableListView lvCourses=dialog.findViewById(R.id.expandableLvw);
        prepareCoursesListData();
        lvCourses.setAdapter(new CoursesListAdapter(context,courses,chapters,icons));
        return dialog;
    }

    public static void prepareCoursesListData(){
        courses = new ArrayList<String>();
        chapters = new HashMap<String, List>();

        courses.add("Overview");
        courses.add("Getting started");
        courses.add("Basics");
        courses.add("Classes and objects");
        courses.add("Functions and Lambdas");
        courses.add("Others");
        courses.add("Java Interop");
        courses.add("Javascript");

        List overviewCourse = new ArrayList();
        overviewCourse.add("Kotlin for Server side");
        overviewCourse.add("Kotlin for Android");
        overviewCourse.add("Kotlin for Javascript");
        overviewCourse.add("Kotlin/Native");

        List gettingStartedCourse = new ArrayList();
        gettingStartedCourse.add("Basic syntax");
        gettingStartedCourse.add("Idioms");
        gettingStartedCourse.add("Coding conventions");

        List basicsCourse = new ArrayList();
        basicsCourse.add("Basics Types");
        basicsCourse.add("Packages and imports");
        basicsCourse.add("Control flow");
        basicsCourse.add("Returns and jumps");

        List classesAndObjectsCourse = new ArrayList();
        classesAndObjectsCourse.add("Classes and inheritance");
        classesAndObjectsCourse.add("Properties and fields");
        classesAndObjectsCourse.add("Interfaces");
        classesAndObjectsCourse.add("Visibility modifiers");
        classesAndObjectsCourse.add("Extensions");
        classesAndObjectsCourse.add("Data classes");
        classesAndObjectsCourse.add("Sealed classes");
        classesAndObjectsCourse.add("Generics");
        classesAndObjectsCourse.add("Nested classes");
        classesAndObjectsCourse.add("Enum classes");
        classesAndObjectsCourse.add("Objects");
        classesAndObjectsCourse.add("Delegation");
        classesAndObjectsCourse.add("Delegated properties");

        List functionsAnLambdasCourse = new ArrayList();
        functionsAnLambdasCourse.add("Functions");
        functionsAnLambdasCourse.add("Lambdas");
        functionsAnLambdasCourse.add("Inline functions");
        functionsAnLambdasCourse.add("Coroutines");

        List othersCourse = new ArrayList();
        othersCourse.add("Destructuring declarations");othersCourse.add("Collections");othersCourse.add("Ranges");othersCourse.add("Type Checks and casts");othersCourse.add("This expressions");
        othersCourse.add("Equality"); othersCourse.add("Sealed classes");othersCourse.add("Operator overloading");othersCourse.add("Null safety");othersCourse.add("Exceptions");othersCourse.add("Annotations");
        othersCourse.add("Reflection");othersCourse.add("Type-safety builders");othersCourse.add("Types Aliases");othersCourse.add("Multiplatform projects");

        List javaInteropCourse = new ArrayList();
        javaInteropCourse.add("Calling Java from Kotlin");
        javaInteropCourse.add("Calling Kotlin frm Java");

        List javascriptCourse = new ArrayList();
        javascriptCourse.add("Dynamic Type");
        javascriptCourse.add("Calling JavaScript from Kotlin");
        javascriptCourse.add("Calling Kotlin from JavaScript");
        javascriptCourse.add("Javascript modules");
        javascriptCourse.add("Javascript reflection");
        javascriptCourse.add("Javascript DCE");
        chapters.put(courses.get(0), overviewCourse);
        chapters.put(courses.get(1), gettingStartedCourse);
        chapters.put(courses.get(2), basicsCourse);
        chapters.put(courses.get(3),classesAndObjectsCourse);
        chapters.put(courses.get(4),functionsAnLambdasCourse);
        chapters.put(courses.get(5),othersCourse);
        chapters.put(courses.get(6),javaInteropCourse);
        chapters.put(courses.get(7),javascriptCourse);

    }


}