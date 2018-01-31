package amalhichri.androidprojects.com.kotlinlearning.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.utils.AllCourses;


public class RootFragment_learn extends Fragment {

    /**
     * fragment added to load/switch fragments in the first tab of HomePage tabLayout
     * call  getFragmentManager.replace ( SOME_ROOT_LAYOUT, My new fragment to load ! )
     *
     * LearnFragment_Chapter / LearnFragment_course / LearnFragment_noCourses... are all being switched inside  R.layout.fragment_root_fragment_learn
     * **/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_root_learn, container, false);
        /**
         * if user has never added a course to his list he finds the ui LearnFragment_noCourses()
         * ( with the button Start new course in center ... )
         * else he find the ui  LearnFragment_currentUserCourses() with his courses list !
         */
        SharedPreferences coursesPrefs = getContext().getSharedPreferences("takenCoursesPrefs",0);
        if (coursesPrefs.contains("takenCourses")){
            /** get all current user's taken courses from sharedPrefs**/
            StringTokenizer st = new StringTokenizer(coursesPrefs.getString("takenCourses",null), ",");
            List<Integer> coursesTaken = new ArrayList<>();
            while(st.hasMoreElements()){
                coursesTaken.add(Integer.parseInt(st.nextToken()));
            }
            Log.d("test 9",String.valueOf(coursesTaken.size()));
            Log.d("test 9++",coursesTaken.toString());
            /** add them to currentUserCourses list and notify the adapter**/
            LearnFragment_currentUserCourses currentUserCoursesFragment = new LearnFragment_currentUserCourses();
            for(int i=0;i<coursesTaken.size();i++){
                Log.d("test 10",String.valueOf(coursesTaken.get(i)));
                currentUserCoursesFragment.currentUserCourses.add(AllCourses.getCourse(coursesTaken.get(i)));
            }
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.root_learFragment,currentUserCoursesFragment).commit();
        }
        else if (!coursesPrefs.contains("takenCourses")){
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.root_learFragment,new LearnFragment_noCourses()).commit();
        }
         return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
