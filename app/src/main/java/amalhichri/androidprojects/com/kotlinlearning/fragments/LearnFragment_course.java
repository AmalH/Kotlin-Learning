package amalhichri.androidprojects.com.kotlinlearning.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tn.amalhichri.library.Parallaxor;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.adapters.ChaptersListAdapter;
import amalhichri.androidprojects.com.kotlinlearning.adapters.CourseHeaderAdapter;


public class LearnFragment_course extends Fragment {

    private static int coursePosition;
    public static CourseHeaderAdapter headerAdapter;

    public static LearnFragment_course newInstance(int coursePosition) {  /** got the clicked course position for previous ui (LearnFragment_CurrentUserCourses) **/
        Bundle args = new Bundle();
        args.putInt("coursePosition",coursePosition);
        LearnFragment_course fragment = new LearnFragment_course();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.coursePosition= getArguments().getInt("coursePosition");
        final View view = inflater.inflate(R.layout.fragment_learn_course, container, false);
        ChaptersListAdapter scrollViewAdapter = new ChaptersListAdapter(getContext(),this.coursePosition);
        headerAdapter = new CourseHeaderAdapter(getContext(),this.coursePosition);

       /** filling the course header **/
        for (int i = 0; i < headerAdapter.getCount(); i++) {
            View item = headerAdapter.getView(i, null, null);
            ((LinearLayout)view.findViewById(R.id.courseHeaderLayout)).addView(item);
        }

        /** loading  data **/
        for (int i = 0; i < scrollViewAdapter.getCount(); i++) {
            final View item = scrollViewAdapter.getView(i, null, null);
            ((LinearLayout)view.findViewById(R.id.courseChaptersLayout)).addView(item);
            final int finalI = i;
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
               //loadSingleChapterContentData(view,finalI);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.root_learFragment, ChapterFragment.newInstance(coursePosition,finalI)).addToBackStack(null).commit();
                }
            });
        }

        /** parallax effect **/
        if (view.findViewById(R.id.scroll_view) instanceof Parallaxor) {
            ((Parallaxor) view.findViewById(R.id.scroll_view)).parallaxViewBy(view.findViewById(R.id.courseHeaderLayout), 0.5f);
        }
        return view;
    }
}
