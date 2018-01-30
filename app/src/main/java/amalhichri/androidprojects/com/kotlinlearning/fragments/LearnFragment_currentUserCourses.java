package amalhichri.androidprojects.com.kotlinlearning.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.rey.material.app.Dialog;

import java.util.ArrayList;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.adapters.CurrentUserCoursesList_Adapter;
import amalhichri.androidprojects.com.kotlinlearning.models.Course;
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

        /** addCoursesBtn click**/
        view.findViewById(R.id.moreCoursesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // new coursesPoisitions list ..
                coursesListDialog.show();
            }
        });

        return view;
    }
}
