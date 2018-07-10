package amalhichri.androidprojects.com.kotlinlearning.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.rey.material.app.Dialog;

import java.util.ArrayList;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.adapters.CurrentUserCoursesList_Adapter;
import amalhichri.androidprojects.com.kotlinlearning.models.Course;
import amalhichri.androidprojects.com.kotlinlearning.utils.AllCourses;
import amalhichri.androidprojects.com.kotlinlearning.utils.Statics;


public class LearnFragment_currentUserCourses extends Fragment {


    public static ArrayList<Course> currentUserCourses = new ArrayList<>();
    public static CurrentUserCoursesList_Adapter listAdapter;
    private Dialog coursesListDialog;
    static private FragmentActivity activity;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        activity=getActivity();
    }

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
                /* saving from where the list was clicked to diffrentiate in adapter **/
                SharedPreferences listShownFromPrefs = getContext().getSharedPreferences("listShownFromPrefs", 0);
                SharedPreferences.Editor coursesPrefsEditor = listShownFromPrefs.edit();
                coursesPrefsEditor.clear();
                coursesPrefsEditor.putString("fromCurrentUserCourses","fromCurrentUserCourses");
                coursesPrefsEditor.commit();
                coursesListDialog.show();
            }
        });

        return view;
    }

    public static void switchFragments(int courseNb){ /** to be called when user clicks " Add to my courses " on courses list**/
      //  LearnFragment_currentUserCourses currentUserCourses = new LearnFragment_currentUserCourses();

        /** the call to switchFragment(int) in CoursesListAdapter
         passes the clicked item position to be used in calling addCourse method
         **/
       currentUserCourses.add(AllCourses.getCourse(courseNb));
       listAdapter.notifyDataSetChanged();
       // activity.getSupportFragmentManager().beginTransaction().replace(R.id.root_learFragment,currentUserCourses).addToBackStack(null).commit();
    }
}
