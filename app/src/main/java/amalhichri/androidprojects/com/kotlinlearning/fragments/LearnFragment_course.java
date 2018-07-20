package amalhichri.androidprojects.com.kotlinlearning.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.tn.amalhichri.library.Parallaxor;

import org.json.JSONException;
import org.json.JSONObject;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.adapters.ChaptersListAdapter;
import amalhichri.androidprojects.com.kotlinlearning.adapters.CourseHeaderAdapter;
import amalhichri.androidprojects.com.kotlinlearning.services.CoursesServices;
import amalhichri.androidprojects.com.kotlinlearning.services.ServerCallbacks;
import amalhichri.androidprojects.com.kotlinlearning.utils.Statics;


public class LearnFragment_course extends Fragment {

    private static int coursePosition;
    public static CourseHeaderAdapter headerAdapter;
    private int nbOfChaptersCompleted=1;

    public static LearnFragment_course newInstance(int coursePosition) {  /** got the clicked course position for previous ui (LearnFragment_CurrentUserCourses) **/
        Bundle args = new Bundle();
        args.putInt("coursePosition",coursePosition);
        LearnFragment_course fragment = new LearnFragment_course();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /** get chapters nb + badge nb **/
        CoursesServices.getInstance().getAllUserCourses(Statics.auth.getCurrentUser().getUid(), getContext(), new ServerCallbacks() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    if (!(result.getJSONArray("courses").length() == 0))
                        for(int i=0;i<result.getJSONArray("courses").length();i++){
                            /** find current course **/
                            if (Integer.parseInt(((JSONObject) result.getJSONArray("courses").get(i)).getString("courseindic"))
                                    == coursePosition) {
                                /** update data **/
                                nbOfChaptersCompleted = Integer.parseInt(((JSONObject) result.getJSONArray("courses").get(coursePosition)).getString("finishedchapter"));
                              //  Toast.makeText(getActivity(),String.valueOf(coursePosition),Toast.LENGTH_SHORT).show();

                                /** if nbOf ChaptersCompleted is 0 **/
                                if (Integer.parseInt(((JSONObject) result.getJSONArray("courses").get(i)).getString("finishedchapter")) == 100) {
                                }
                            }
                        }

                }catch (JSONException e) {
                   // Toast.makeText(getActivity(),"Server error "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError result) {

            }

            @Override
            public void onWrong(JSONObject result) {

            }
        });
        /** **/
        ChaptersListAdapter scrollViewAdapter = new ChaptersListAdapter(getContext(),this.coursePosition);
        headerAdapter = new CourseHeaderAdapter(getContext(),this.coursePosition, nbOfChaptersCompleted);

        /** filling the course header **/
        for (int i = 0; i < headerAdapter.getCount(); i++) {
            View item = headerAdapter.getView(i, null, null);
            ((LinearLayout)getActivity().findViewById(R.id.courseHeaderLayout)).addView(item);
        }

        /** loading  data **/
        for (int i = 0; i < scrollViewAdapter.getCount(); i++) {
            final View item = scrollViewAdapter.getView(i, null, null);
            ((LinearLayout)getActivity().findViewById(R.id.courseChaptersLayout)).addView(item);
            final int finalI = i;
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /** **/
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.root_learFragment, ChapterFragment.newInstance(coursePosition,finalI)).addToBackStack(null).commit();
                }
            });
        }

        /** parallax effect **/
        if (getActivity().findViewById(R.id.scroll_view) instanceof Parallaxor) {
            ((Parallaxor) getActivity().findViewById(R.id.scroll_view)).parallaxViewBy(getActivity().findViewById(R.id.courseHeaderLayout), 0.5f);
        }

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.coursePosition= getArguments().getInt("coursePosition");
        final View view = inflater.inflate(R.layout.fragment_learn_course, container, false);
        return view;
    }

}
