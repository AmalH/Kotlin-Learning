package amalhichri.androidprojects.com.kotlinlearning.fragments;

import android.graphics.drawable.ColorDrawable;
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
import com.google.firebase.auth.FirebaseAuth;
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
    private static int nbOfLoadedCourses;
    private static CurrentUserCoursesList_Adapter listAdapter;
    private Dialog coursesListDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nbOfLoadedCourses = 0;
        coursesListDialog = Statics.createCoursesListDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(getUserVisibleHint()){
            loadCurrentUserCourses();
        }
        return inflater.inflate(R.layout.fragment_learn_currentusercourses, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        nbOfLoadedCourses = 0;

        /** open course Learn_Fragment ui on list item click **/
        ((ListView) getActivity().findViewById(R.id.myCoursesLv)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.root_learFragment, LearnFragment_course.newInstance(courseTitleToCoursePosition(((TextView) view.findViewById(R.id.userCourseTitle)).getText().toString())))
                        .addToBackStack(null)
                        .commit();
            }
        });
        ((ListView) getActivity().findViewById(R.id.myCoursesLv)).setDivider(new ColorDrawable(0x545454));
        /** addCoursesBtn click**/
        getActivity().findViewById(R.id.moreCoursesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coursesListDialog.show();
            }
        });
    }


    private void loadCurrentUserCourses() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            CoursesServices.getInstance().getAllUserCourses(Statics.auth.getCurrentUser().getUid(), getContext(), new ServerCallbacks() {
                @Override
                public void onSuccess(JSONObject result) {
                    boolean showLoadedData = true;
                    try {
                        if (!(result.getJSONArray("courses").length() == 0))
                            currentUserCourses.clear();
                            for (int i = 0; i < result.getJSONArray("courses").length(); i++) {
                                currentUserCourses.add(AllCourses.getCourse(Integer.parseInt(((JSONObject) result.getJSONArray("courses").get(i)).getString("courseindic"))));
                            }

                        if (showLoadedData) {
                            if (nbOfLoadedCourses == 0) {
                                listAdapter = new CurrentUserCoursesList_Adapter(getContext(), currentUserCourses);
                                ((ListView) getActivity().findViewById(R.id.myCoursesLv)).setAdapter(listAdapter);
                            } else {
                                if (listAdapter == null) {
                                    listAdapter = new CurrentUserCoursesList_Adapter(getContext(), currentUserCourses);
                                    ((ListView) getActivity().findViewById(R.id.myCoursesLv)).setAdapter(listAdapter);
                                } else {
                                    listAdapter.notifyDataSetChanged();
                                }
                            }
                            if (nbOfLoadedCourses == 0)
                                nbOfLoadedCourses += (result.getJSONArray("courses").length());
                            else
                                nbOfLoadedCourses += 10;
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "Please check internet connection !", Toast.LENGTH_SHORT).show();
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
    }

    private int courseTitleToCoursePosition(String title) {
        int pos = -1;
        switch (title) {
            case "Overview":
                pos = 0;
                break;
            case "Getting started":
                pos = 1;
                break;
            case "Basics":
                pos = 2;
                break;
            case "Classes and objects":
                pos = 3;
                break;
            case "Functions and Lambdas":
                pos = 4;
                break;
            case "Others":
                pos = 5;
                break;
            case "Java Interop":
                pos = 6;
                break;
            case "Javascript":
                pos = 7;
                break;
        }
        return pos;

    }
}
