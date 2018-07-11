package amalhichri.androidprojects.com.kotlinlearning.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.utils.AllCourses;
import amalhichri.androidprojects.com.kotlinlearning.utils.Statics;


public class LearnFragment_noCourses extends Fragment {


    private static Dialog dialog;
    static private FragmentManager fgMgr;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.fgMgr=getActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_learn_nocourses, container, false);
      /*  ArrayList<Integer> coursesPositions = new ArrayList<>();
         coursesPositions.add(0);coursesPositions.add(1);coursesPositions.add(2);*/
        this.dialog= Statics.createCoursesListDialog(getContext());   /** this holds the courses list,
         it's in Statics because we'll load it in other fragments not only this one ! **/
        v.findViewById(R.id.openCoursesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        return v;
    }

    public static void switchFragments(int courseNb,Context context){ /** to be called when user clicks " Add to my courses " on courses list**/
        LearnFragment_currentUserCourses currentUserCourses = new LearnFragment_currentUserCourses();

        /** the call to switchFragment(int) in CoursesListAdapter
             passes the clicked item position to be used in calling addCourse method
             **/
        currentUserCourses.currentUserCourses.add(AllCourses.getCourse(courseNb));
        ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.root_learFragment,currentUserCourses).addToBackStack(null).commit();
    }
}