package amalhichri.androidprojects.com.kotlinlearning.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import android.widget.GridView;
import android.widget.ListView;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.adapters.BadgesAdapter;
import amalhichri.androidprojects.com.kotlinlearning.services.ServerCallbacks;
import amalhichri.androidprojects.com.kotlinlearning.services.UsersServices;
import amalhichri.androidprojects.com.kotlinlearning.utils.Statics;
import cn.pedant.SweetAlert.Rotate3dAnimation;


public class BadgesFragment extends Fragment {

    private ArrayList<Integer> deactivatedBadgesIcons,activatedBadgesIcons;
    private String[] badgesNames,badgesDescriptions_Grid,badgesDescriptions;
    private  ListView badgesLv;
    private GridView badgesGridView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** settings badges listView / gridView **/
        badgesNames = new String[] {
                "Verified Account", "Engaged in!", "Achiever","Just getting started","Course master",
                "Asker", "Good question", "Master","Top question","Good answer",
                "Guru", "Question Master", "Self Learner","Answerer","Developer",
                "Contributor", "Teacher", "Coder","Popular answer","Code ninja"};
        badgesDescriptions = new String[] {
                "Verify your account's email address", "Complete a chapter", "Complete a course","Win a contest","Complete 5 courses",
                "Post a question and get an upvote", "Get 5 upvotes on your question", "Win 5 contests","Get 20 upvotes on your question","Get 5 upvotes on your answer",
                "Win 10 contests", "Post 5 questions with at least 5 upvotes each", "ForumAnswer your own question and get 5 upvotes","Post an answer and get an upvote","Get 10 upvotes on your code",
                "Leave a comment with 5 upvotes", "Post 10 answers with at least 5 upvotes each", "Post 5 codes with at least 5 upvotes each","Get 100 upvotes on your answer","Post 15 codes with at least 3 upvotes each"};
        badgesDescriptions_Grid = new String[]{"","","","","","","","","","","","","","","","","","","",""};
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(getUserVisibleHint()){
            activateBadges(1);
        }

        return  inflater.inflate(R.layout.fragment_badges, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        badgesLv = getActivity().findViewById(R.id.badgesListView);
        badgesGridView = getActivity().findViewById(R.id.badgesGridView);

        activatedBadgesIcons = new ArrayList<Integer>() {{
            add(R.drawable.ic_badge_accountverified);add(R.drawable.ic_badge_engaged);add(R.drawable.ic_badge_acheiver);add(R.drawable.ic_badge_gettingstarted);
            add(R.drawable.ic_badge_course_master);add(R.drawable.ic_badge_coder);add(R.drawable.ic_badge_good_question);add(R.drawable.ic_badge_master);
            add(R.drawable.ic_badge_top_question);add(R.drawable.ic_badge_coder);add(R.drawable.ic_badge_guru);add(R.drawable.ic_badge_question_master);
            add(R.drawable.ic_badge_self_learner);add(R.drawable.ic_badge_coder);add(R.drawable.ic_badge_developer);add(R.drawable.ic_badge_contributor);
            add(R.drawable.ic_badge_teacher);add(R.drawable.ic_badge_coder);add(R.drawable.ic_badge_popular_answer);add(R.drawable.ic_badge_code_ninja);
        }};

        deactivatedBadgesIcons = new ArrayList<Integer>() {{
            add(R.drawable.ic_badge_accountverified_unlocked);add(R.drawable.ic_badge_engaged);add(R.drawable.ic_badge_acheiver);add(R.drawable.ic_badge_gettingstarted_unlocked);
            add(R.drawable.ic_badge_course_master_unlocked);add(R.drawable.ic_badge_coder);add(R.drawable.ic_badge_good_question_unlocked);add(R.drawable.ic_badge_master_unlocked);
            add(R.drawable.ic_badge_top_question_unlocked);add(R.drawable.ic_badge_coder_unlocked);add(R.drawable.ic_badge_guru_unlocked);add(R.drawable.ic_badge_question_master_unlocked);
            add(R.drawable.ic_badge_self_learne_unlockedr);add(R.drawable.ic_badge_coder_unlocked);add(R.drawable.ic_badge_developer_unlocked);add(R.drawable.ic_badge_contributor_unlocked);
            add(R.drawable.ic_badge_teacher_unlocked);add(R.drawable.ic_badge_coder_unlocked);add(R.drawable.ic_badge_popular_answer_unlocked);add(R.drawable.ic_badge_code_ninja_unlocked);

        }};

        badgesGridView.setLayoutAnimation(getgridlayoutAnim());
        badgesGridView.setAdapter(new BadgesAdapter(getContext(),badgesDescriptions_Grid,badgesDescriptions_Grid, deactivatedBadgesIcons));
        badgesLv.setAdapter(new BadgesAdapter(getContext(),badgesDescriptions,badgesNames, deactivatedBadgesIcons));
        /** switching between listView and gridView button click **/
        (getActivity().findViewById(R.id.gridListSwitchBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (getActivity().findViewById(R.id.badgesGridView).getVisibility()){
                    case View.VISIBLE:
                        v.setBackgroundResource(R.drawable.ic_grid);
                        badgesGridView.setVisibility(View.GONE);
                        badgesLv.setVisibility(View.VISIBLE);
                        break;
                    case View.GONE:
                        v.setBackgroundResource(R.drawable.ic_list);
                        badgesGridView.setVisibility(View.VISIBLE);
                        badgesLv.setVisibility(View.GONE);
                }

            }
        });

    }

    private void activateBadges(final int badgeIndic){
        UsersServices.getInstance().isHasBadge(Statics.auth.getCurrentUser().getUid(), String.valueOf(badgeIndic), getActivity(), new ServerCallbacks() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    Log.d("SUCCESS ","-------"+result.getJSONArray("badges").length());
                    if(!(result.getJSONArray("badges").length()==0)){
                        deactivatedBadgesIcons.remove(badgeIndic);
                        deactivatedBadgesIcons.add(badgeIndic,activatedBadgesIcons.get(badgeIndic));
                        badgesLv.deferNotifyDataSetChanged();
                        badgesGridView.deferNotifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(VolleyError result) {
                Log.d("ERROR ","----"+result.getClass().getName());
            }

            @Override
            public void onWrong(JSONObject result) {
                Log.d("SUCCESS ","-----"+result.toString());
            }
        });
    }

    private static LayoutAnimationController getgridlayoutAnim()
    {
        LayoutAnimationController controller;
        Animation anim=new Rotate3dAnimation(1,0f,0.5f,0.5f,0.5f);
        anim.setDuration(500);
        controller=new LayoutAnimationController(anim,0.1f);
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        return controller;
    }

/*private ImageView generateImageView(int icon){
        ImageView iv = new ImageView(getContext());
        iv.setTag(icon);
       //iv.setImageResource(icon);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
       iv.setLayoutParams(lp);
    ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        iv.setColorFilter(new ColorMatrixColorFilter(matrix));
        return iv;
    }*/
}
