package amalhichri.androidprojects.com.kotlinlearning.fragments;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.utils.AllCourses;
import amalhichri.androidprojects.com.kotlinlearning.utils.Statics;


public class LearnFragment_noCourses extends Fragment {


    private static Dialog dialog;
    static private FragmentActivity activity;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
      activity=getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_learn_nocourses, container, false);
        this.dialog= Statics.createCoursesListDialog(getContext());   /** this holds the courses list,
         it's in Statics because we'll load it in other fragments not only this one ! **/
        v.findViewById(R.id.openCoursesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences listShownFromPrefs = getContext().getSharedPreferences("listShownFromPrefs", 0);
                SharedPreferences.Editor coursesPrefsEditor = listShownFromPrefs.edit();
                coursesPrefsEditor.clear();
                coursesPrefsEditor.putString("fromNoCourses","fromNoCourses");
                coursesPrefsEditor.commit();
                dialog.show();
            }

        });
        return v;
    }

    public static void switchFragments(int courseNb){ /** to be called when user clicks " Add to my courses " on courses list**/
        LearnFragment_currentUserCourses currentUserCourses = new LearnFragment_currentUserCourses();
        currentUserCourses.currentUserCourses.add(AllCourses.getCourse(courseNb));
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.root_learFragment,currentUserCourses).addToBackStack(null).commit();
    }
}
