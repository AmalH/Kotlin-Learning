package amalhichri.androidprojects.com.kotlinlearning.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.adapters.ChapterAdapter;


public class ChapterFragment extends Fragment {

    private ChapterAdapter chapter_adapter;
    //public static Alerter engagedInBadgeUnlockedAltert;

    public static ChapterFragment newInstance(int courseNb, int chapterNb) {

        Bundle args = new Bundle();
        args.putInt("courseNb",courseNb);
        args.putInt("chapterNb",chapterNb);
        ChapterFragment fragment = new ChapterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_chapter, container, false);

       /* engagedInBadgeUnlockedAltert = Alerter.create(getActivity())
                .setTitle("Engaged in !")
                .setTitleTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/graublau_slab_bold.ttf"))
                .enableSwipeToDismiss()
                .setText("Congrats! \n You unlocked your Engaged in badge \n for completing a chapter !                                                                                                                  " )
                .setTextTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/graublau_slab.ttf"))
                .setDuration(1000)
                .setIcon(R.drawable.ic_badge_engaged);*/
        chapter_adapter = new ChapterAdapter(getContext(),getArguments().getInt("courseNb"),getArguments().getInt("chapterNb"));
        /** filling the course header **/
        for (int i = 0; i < chapter_adapter.getCount(); i++) {
            View item = chapter_adapter.getView(i, null, null);
            ((FrameLayout)v.findViewById(R.id.fragmentChapterContainer)).addView(item);
        }
        return v;
    }
}
