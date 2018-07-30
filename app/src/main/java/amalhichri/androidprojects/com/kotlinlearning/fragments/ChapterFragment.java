package amalhichri.androidprojects.com.kotlinlearning.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.adapters.ChapterAdapter;
import amalhichri.androidprojects.com.kotlinlearning.services.ServerCallbacks;
import amalhichri.androidprojects.com.kotlinlearning.services.UsersServices;
import amalhichri.androidprojects.com.kotlinlearning.utils.Statics;


public class ChapterFragment extends Fragment {

    private ChapterAdapter chapter_adapter;

    public static ChapterFragment newInstance(int courseNb, int chapterNb) {

        Bundle args = new Bundle();
        args.putInt("courseNb",courseNb);
        args.putInt("chapterNb",chapterNb);
        ChapterFragment fragment = new ChapterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /** will change this **/
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** assign engagedIn badge**/
        UsersServices.getInstance().assignBadge(Statics.auth.getCurrentUser().getUid(), String.valueOf(1),getActivity(), new ServerCallbacks() {
            @Override
            public void onSuccess(JSONObject result) {
                Toast.makeText(getActivity(),"SUCCESS "+result.toString(),Toast.LENGTH_SHORT);
            }

            @Override
            public void onError(VolleyError result) {
                Toast.makeText(getActivity(),"FAILURE 1 "+result.getClass().getName(),Toast.LENGTH_SHORT);
            }

            @Override
            public void onWrong(JSONObject result) {
                Toast.makeText(getActivity(),"FAILURE 2 "+result.toString(),Toast.LENGTH_SHORT);
            }
        });

    }

    /** will change this **/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_chapter, container, false);

        chapter_adapter = new ChapterAdapter(getContext(),getArguments().getInt("courseNb"),getArguments().getInt("chapterNb"));
        /** filling the course header **/
        for (int i = 0; i < chapter_adapter.getCount(); i++) {
            View item = chapter_adapter.getView(i, null, null);
            ((FrameLayout)v.findViewById(R.id.fragmentChapterContainer)).addView(item);
        }
        return v;
    }
}
