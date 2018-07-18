package amalhichri.androidprojects.com.kotlinlearning.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.rey.material.app.Dialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.adapters.CurrentUserCoursesList_Adapter;
import amalhichri.androidprojects.com.kotlinlearning.models.Course;
import amalhichri.androidprojects.com.kotlinlearning.services.CoursesServices;
import amalhichri.androidprojects.com.kotlinlearning.services.ServerCallbacks;
import amalhichri.androidprojects.com.kotlinlearning.utils.AllCourses;
import amalhichri.androidprojects.com.kotlinlearning.utils.Statics;


public class LearnFragment_currentUserCourses extends Fragment {


    public static ArrayList<Course> currentUserCourses = new ArrayList<>();
    public static CurrentUserCoursesList_Adapter listAdapter;
    private Dialog coursesListDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        coursesListDialog= Statics.createCoursesListDialog(getContext());
        listAdapter= new CurrentUserCoursesList_Adapter(getContext(),currentUserCourses);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view= inflater.inflate(R.layout.fragment_learn_currentusercourses, container, false);
        ((ListView)view.findViewById(R.id.myCoursesLv)).setAdapter(listAdapter);

        /** open course Learn_Fragment ui on list item click **/
       ((ListView)view.findViewById(R.id.myCoursesLv)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.root_learFragment, LearnFragment_course.newInstance(courseTitleToCoursePosition(((TextView)view.findViewById(R.id.userCourseTitle)).getText().toString())))
                        .addToBackStack(null)
                        .commit();
            }
        });

        /** addCoursesBtn click**/
        view.findViewById(R.id.moreCoursesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coursesListDialog.show();
            }
        });

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /** get courses from database **/
        CoursesServices.getInstance().getAllUserCourses(Statics.auth.getCurrentUser().getUid(), getContext(), new ServerCallbacks() {
            @Override
            public void onSuccess(JSONObject result) {

                try {
                    if (!(result.getJSONArray("courses").length() == 0))
                        for (int i = 0; i < result.getJSONArray("courses").length(); i++) {
                            /** load courses **/
                            currentUserCourses.add(AllCourses.getCourse(Integer.parseInt(((JSONObject) result.getJSONArray("courses").get(i)).getString("courseindic"))));
                        }
                }catch (JSONException e) {
                    Toast.makeText(getContext(),"Server error "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError result) {

            }

            @Override
            public void onWrong(JSONObject result) {

            }
        });
    }

    /** used to get clicked course's position **/
    private int courseTitleToCoursePosition(String title){
        int pos=-1 ;
        switch(title){
            case "Overview":
                pos=0;
                break;
            case "Getting started":
                pos=1;
                break;
            case "Basics":
                pos=2;
                break;
            case "Classes and objects":
                pos=3;
                break;
            case "Functions and Lambdas":
                pos=4;
                break;
            case "Others":
                pos=5;
                break;
            case "Java Interop":
                pos=6;
                break;
            case "Javascript":
                pos=7;
                break;
        }
        return pos;

    }
}
