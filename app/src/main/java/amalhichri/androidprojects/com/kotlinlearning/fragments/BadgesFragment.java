package amalhichri.androidprojects.com.kotlinlearning.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import android.widget.GridView;
import android.widget.ListView;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.adapters.BadgesAdapter;
import cn.pedant.SweetAlert.Rotate3dAnimation;


public class BadgesFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_badges, container, false);
        final ListView badgesLv = view.findViewById(R.id.badgesListView);
        final GridView badgesGridView = view.findViewById(R.id.badgesGridView);

        /** test of badge value has passes **/
        SharedPreferences badgesPrefs = getContext().getSharedPreferences("badgesTestPrefs",0);
        int iconPos = badgesPrefs.getInt("engagedInBadge",0);

        Log.d("badgeToActivate", String.valueOf(iconPos));


                /** settings badges listView / gridView **/
        String[] badgesNames = new String[] {
                "Verified Account", "Engaged in!", "Achiever","Just getting started","Course master",
                "Asker", "Good question", "Master","Top question","Good answer",
                "Guru", "Question Master", "Self Learner","Answerer","Developer",
                "Contributor", "Teacher", "Coder","Popular answer","Code ninja"};
        String[] badgesDescriptions = new String[] {
                "Verify your account's email address", "Complete a chapter", "Complete a course","Win a challenge","Complete 10 courses",
                "Post a question and get an upvote", "Get 5 upvotes on your question", "Win 5 challenges","Get 20 upvotes on your question","Get 5 upvotes on your answer",
                "Win 10 challenges", "Post 5 questions with at least 5 upvotes each", "Answer your own question and get 5 upvotes","Post an answer and get an upvote","Get 10 upvotes on your code",
                "Leave a comment with 5 upvotes", "Post 10 answers with at least 5 upvotes each", "Post 5 codes with at least 5 upvotes each","Get 100 upvotes on your answer","Post 15 codes with at least 3 upvotes each"};
        String[] badgesDescriptions_Grid = new String[]{"","","","","","","","","","","","","","","","","","","",""};
        /*ImageView[] badgesIcons = new ImageView[]{ generateImageView(R.drawable.ic_badge_accountverified),generateImageView(R.drawable.ic_badge_engaged_locked),generateImageView(R.drawable.ic_badge_acheiver),generateImageView(R.drawable.ic_badge_gettingstarted)
                ,generateImageView(R.drawable.ic_badge_course_master),generateImageView(R.drawable.ic_badge_asker),generateImageView(R.drawable.ic_badge_good_question),generateImageView(R.drawable.ic_badge_master),
                generateImageView(R.drawable.ic_badge_top_question),generateImageView(R.drawable.ic_badge_good_answer),generateImageView(R.drawable.ic_badge_guru),generateImageView(R.drawable.ic_badge_question_master),
                generateImageView(R.drawable.ic_badge_self_learner),generateImageView(R.drawable.ic_badge_answerer),generateImageView(R.drawable.ic_badge_developer),generateImageView(R.drawable.ic_badge_contributor),
                generateImageView(R.drawable.ic_badge_teacher),generateImageView(R.drawable.ic_badge_coder),generateImageView(R.drawable.ic_badge_popular_answer),generateImageView(R.drawable.ic_badge_code_ninja),
        };*/
         int[] badgesIcons= new int[]{
                R.drawable.ic_badge_accountverified_unlocked,R.drawable.ic_badge_engaged_unlocked,R.drawable.ic_badge_acheiver_unlocked,R.drawable.ic_badge_gettingstarted_unlocked,
                R.drawable.ic_badge_course_master_unlocked,R.drawable.ic_badge_asker_unlocked,R.drawable.ic_badge_good_question_unlocked,R.drawable.ic_badge_master_unlocked,
                R.drawable.ic_badge_top_question_unlocked,R.drawable.ic_badge_good_answer_unlocked,R.drawable.ic_badge_guru_unlocked,R.drawable.ic_badge_question_master_unlocked,
                R.drawable.ic_badge_self_learne_unlockedr,R.drawable.ic_badge_answerer_unlocked,R.drawable.ic_badge_developer_unlocked,R.drawable.ic_badge_contributor_unlocked,
                R.drawable.ic_badge_teacher_unlocked,R.drawable.ic_badge_coder_unlocked,R.drawable.ic_badge_popular_answer_unlocked,R.drawable.ic_badge_code_ninja_unlocked};

      badgesGridView.setLayoutAnimation(getgridlayoutAnim());
        badgesGridView.setAdapter(new BadgesAdapter(getContext(),badgesDescriptions_Grid,badgesDescriptions_Grid,badgesIcons));
        badgesLv.setAdapter(new BadgesAdapter(getContext(),badgesDescriptions,badgesNames,badgesIcons));
        /** switching between listView and gridView button click **/
        (view.findViewById(R.id.gridListSwitchBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (view.findViewById(R.id.badgesGridView).getVisibility()){
                    case View.VISIBLE:
                        v.setBackgroundResource(R.drawable.ic_list);
                        badgesGridView.setVisibility(View.GONE);
                        badgesLv.setVisibility(View.VISIBLE);
                        break;
                    case View.GONE:
                        v.setBackgroundResource(R.drawable.ic_grid);
                        badgesGridView.setVisibility(View.VISIBLE);
                        badgesLv.setVisibility(View.GONE);
                }

            }
        });
        /** if user has badge, activate the icon **/
        if(badgesPrefs.getInt("engagedInBadge",0)==1){
            badgesIcons[1]=R.drawable.ic_badge_engaged;
            badgesLv.deferNotifyDataSetChanged();
        }
        return  view;
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
