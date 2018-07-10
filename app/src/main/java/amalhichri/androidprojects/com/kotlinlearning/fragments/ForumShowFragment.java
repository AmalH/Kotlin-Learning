package amalhichri.androidprojects.com.kotlinlearning.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.adapters.CommentsAdapter;
import amalhichri.androidprojects.com.kotlinlearning.models.Answer;
import amalhichri.androidprojects.com.kotlinlearning.models.ForumQuestion;
import amalhichri.androidprojects.com.kotlinlearning.services.ForumServices;
import amalhichri.androidprojects.com.kotlinlearning.services.ServerCallbacks;
import amalhichri.androidprojects.com.kotlinlearning.services.UserProfileServices;
import amalhichri.androidprojects.com.kotlinlearning.utils.Configuration;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.kbiakov.codeview.CodeView;
import io.github.kbiakov.codeview.adapters.Options;
import io.github.kbiakov.codeview.highlight.ColorTheme;
import me.originqiu.library.FlowLayout;

public class ForumShowFragment extends Fragment {
    private ForumQuestion f;

    ImageButton ratingUp;
    ImageButton ratingDown;
    ImageButton backtoForum;
    TextView rating_txt;
    TextView subject;
    TextView content;
    TextView views;
    FlowLayout tags;
    TextView username;
    TextView creationDate;
    CircleImageView picture;
    FloatingActionButton addCommentButton;
    CodeView codeView;
    TextView editedText;

    /** forum edit*/
    ImageButton forumEditButton;

    /** comments list*/
    public RecyclerView commentsRececyclerView;
    public SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView.Adapter adapter;
    public RecyclerView.LayoutManager layoutManager;
    public ArrayList<Answer> listComments;
    public TextView noComments_textView;


    private static int loaded_length;
    private boolean mine;

    private boolean loading = true,reloadAfterEdit=false;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    /** Adding a comment vars*/
    private TextView content_toAdd;
    private View addCommentCostumView;


    /** check upvoted */
    int selfVote=2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //reload user to check validation
        FirebaseAuth.getInstance().getCurrentUser().reload();
        mine=false;
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forum_show, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("activity","activiy createed");
        //assign widgets
        ratingUp=getActivity().findViewById(R.id.forum_up_arrow);
        ratingDown=getActivity().findViewById(R.id.forum_down_arrow);
        rating_txt=getActivity().findViewById(R.id.forum_rating_show);
        subject=getActivity().findViewById(R.id.forum_subject);
        content=getActivity().findViewById(R.id.forum_content);
        views=getActivity().findViewById(R.id.forum_views_txt);
        tags=getActivity().findViewById(R.id.forum_tags);
        username=getActivity().findViewById(R.id.forum_username);
        creationDate=getActivity().findViewById(R.id.forum_created);
        picture=getActivity().findViewById(R.id.forum_user_picture);
        addCommentButton=getActivity().findViewById(R.id.add_Comment);
        backtoForum=getActivity().findViewById(R.id.backtoForumFromComment);
        codeView=getActivity().findViewById(R.id.code_view);
        forumEditButton=getActivity().findViewById(R.id.forum_edit_button);
        editedText=getActivity().findViewById(R.id.forum_show_edited);

        /** comment elements*/
        commentsRececyclerView= getActivity().findViewById(R.id.comments_container_list);
        /** temp */
        adapter = new CommentsAdapter(new ArrayList<Answer>(),getActivity());
        commentsRececyclerView.setAdapter(adapter);

        //listeners
        ratingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ratingUp.setEnabled(false);
                ForumServices.getInstance().upvoteForum(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        getContext(), f.getId(), new ServerCallbacks() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                try {
                                    rating_txt.setText((result.getInt("resp")>0?"+"+result.getInt("resp"):result.getInt("resp")+"")
                                    );
                                    selfVote=1;
                                    color_voted();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                ratingUp.setEnabled(true);
                            }

                            @Override
                            public void onError(VolleyError result) {
                                Toast.makeText(getContext(),"Server problem ",Toast.LENGTH_SHORT).show();
                                ratingUp.setEnabled(true);
                            }

                            @Override
                            public void onWrong(JSONObject result) {
                                load_forum();
                                ratingUp.setEnabled(true);
                            }
                        });
            }
        });
        ratingDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ratingDown.setEnabled(false);
                ForumServices.getInstance().downvoteForum(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        getContext(), f.getId(), new ServerCallbacks() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                try {
                                    rating_txt.setText( (result.getInt("resp")>0?"+"+result.getInt("resp"):result.getInt("resp")+"")
                                    );
                                    selfVote=-1;
                                    color_voted();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ratingDown.setEnabled(true);
                            }

                            @Override
                            public void onError(VolleyError result) {
                                Toast.makeText(getContext(),"Server problem ",Toast.LENGTH_SHORT).show();
                                ratingDown.setEnabled(true);
                            }

                            @Override
                            public void onWrong(JSONObject result) {
                                load_forum();
                                ratingDown.setEnabled(true);
                            }
                        });
            }
        });
        backtoForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });



        swipeRefreshLayout=getActivity().findViewById(R.id.comment_refresh_layout);
        listComments=new ArrayList<>();
        noComments_textView=getActivity().findViewById(R.id.no_comments_txt);

        layoutManager = new LinearLayoutManager(getContext());
        commentsRececyclerView.setLayoutManager(layoutManager);

        /** swipe to refresh */
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Configuration.isOnline(getContext())){
                    FirebaseAuth.getInstance().getCurrentUser().reload();
                    loaded_length=0;
                    load_comments(0);
                    load_forum();
                }
            }
        });

        loaded_length=0;

        setActionListenerToAdd();
        load_comments(0);
        attach_scrollListener();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        load_forum();
    }

    public void setF(ForumQuestion f) {
        this.f = f;
    }

    public void load_comments(int start_at){
        if(start_at==0)
        {
            loaded_length=0;
            commentsRececyclerView.removeAllViews();
            listComments.clear();
        }

        swipeRefreshLayout.setRefreshing(true);
        if(Configuration.isOnline(getContext()))
            ForumServices.getInstance().getComments(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    getContext(),start_at,f.getId(), new ServerCallbacks() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            /**
                             * do show
                             */
                            boolean goShow=true;
                            JSONArray array = new JSONArray();
                            try {
                                array = result.getJSONArray("comments");
                            } catch (JSONException e) {
                                Toast.makeText(getContext(),"Server error while loading forum , please report",Toast.LENGTH_SHORT).show();
                                goShow=false;
                            }
                            for(int i = 0 ; i < array.length() ; i++){
                                try {
                                    /** parse forum and add it to the arraylist**/
                                    listComments.add(ForumServices.Answerparse_(array.getJSONObject(i)));
                                } catch (JSONException e) {
                                    Toast.makeText(getContext(),"Application error while loading forum , please report",Toast.LENGTH_SHORT).show();
                                    goShow=false;
                                }
                            }
                            /** All the work will be here **/
                            if(goShow) {
                                noComments_textView.setVisibility(View.GONE);
                                commentsRececyclerView.setVisibility(View.VISIBLE);
                                if(loaded_length==0){
                                    adapter=new CommentsAdapter(listComments,getContext());
                                    commentsRececyclerView.setAdapter(adapter);
                                }
                                else{
                                    adapter.notifyItemChanged(layoutManager.getChildCount());
                                }

                                //addCalculated
                                if(loaded_length==0) loaded_length+=10;
                                else
                                    loaded_length+=8;

                            }
                            else {
                                noComments_textView.setVisibility(View.VISIBLE);
                                commentsRececyclerView.setVisibility(View.GONE);
                            }
                            if(listComments.size()==0){
                                noComments_textView.setVisibility(View.VISIBLE);
                                commentsRececyclerView.setVisibility(View.GONE);
                            }
                            swipeRefreshLayout.setRefreshing(false);
                            loading=false;

                        }

                        @Override
                        public void onError(VolleyError result) {
                            noComments_textView.setVisibility(View.VISIBLE);
                            commentsRececyclerView.setVisibility(View.GONE);
                            swipeRefreshLayout.setRefreshing(false);
                            loading=false;
                        }

                        @Override
                        public void onWrong(JSONObject result) {
                            noComments_textView.setVisibility(View.VISIBLE);
                            commentsRececyclerView.setVisibility(View.GONE);
                            Toast.makeText(getContext(),"Problem , please report!",Toast.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false);
                            loading=false;
                        }
                    });
        else{
            noComments_textView.setVisibility(View.VISIBLE);
            commentsRececyclerView.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            loading=false;
        }

    }

    public void  setActionListenerToAdd(){
        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (UserProfileServices.getInstance().is_verified(getContext())) {
                    /**
                     * go add a comment
                     */
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
                    addCommentCostumView=inflater.inflate(R.layout.comment_add_view,null);

                    content_toAdd=addCommentCostumView.findViewById(R.id.comment_post_content);

                    /** show add dialog */

                    new MaterialStyledDialog.Builder(getActivity())
                            .setTitle("Post a new comment")
                            .setStyle(Style.HEADER_WITH_ICON)
                            .withIconAnimation(false)
                            .setCancelable(true)
                            .withDialogAnimation(true)
                            .setHeaderColor(R.color.base_color_1)
                            .setHeaderScaleType(ImageView.ScaleType.CENTER_CROP)
                            .setCustomView(addCommentCostumView,10,30,10,10)
                            .setIcon(R.drawable.ic_action_add_comment)
                            .setPositiveText("Comment")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    final String ct=content_toAdd.getText().toString().trim();
                                    if(!ct.isEmpty() && Configuration.isOnline(getContext()) && UserProfileServices.getInstance().is_verified(getContext())){
                                        ForumServices.getInstance().addAnswer(getContext(), ct, f.getId(), FirebaseAuth.getInstance().getCurrentUser().getUid(), new ServerCallbacks() {
                                            @Override
                                            public void onSuccess(JSONObject result) {
                                                load_comments(0);
                                            }

                                            @Override
                                            public void onError(VolleyError result) {
                                                Toast.makeText(getContext(),"Server problem",Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onWrong(JSONObject result) {
                                                Toast.makeText(getContext(),"Problem while posting, please retry",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    else {
                                        Toast.makeText(getContext(),"Nothing to post",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .show();

                }
            }
        });
    }



    public void attach_scrollListener(){
        loading =false;
        commentsRececyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();
                    if (Configuration.isOnline(getContext())&& !loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount-3)
                        {
                            loading=true;
                            load_comments(loaded_length);

                            // Log.d("loading","loading_more");
                        }
                    }
                }
            }
        });
    }

    public void load_forum(){
        ForumServices.getInstance().getForum(FirebaseAuth.getInstance().getCurrentUser().getUid(), getContext(), f.getId(),
                new ServerCallbacks() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                            f=ForumServices.parse_(result.getJSONArray("forum").getJSONObject(0));
                            if(result.has("selfvote")){
                                selfVote=result.getInt("selfvote");
                            }
                            else
                                selfVote=0;
                            if(getContext()!=null)
                                fillForumContent();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(VolleyError result) {
                        Toast.makeText(getContext(),"error loading", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onWrong(JSONObject result) {
                        Toast.makeText(getContext(),"wrong loading", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void fillForumContent(){

        if(f.getId_User().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
            mine=true;

        rating_txt.setText((f.getRating()>0?"+"+f.getRating():f.getRating()+""));
        views.setText(f.getViews()+"");
        subject.setText(f.getSubject());
        content.setText(f.getContent());

        if(!mine) username.setText(f.getUser_name());
        else username.setText("By me ");

        creationDate.setText(f.getCreated_string());

        if(f.getUser_picture_url()!=null)
            Picasso.with(getContext()).load(Uri.parse(f.getUser_picture_url())).into(picture);
        else{
            String item=f.getUser_name();
            picture.setImageDrawable(UserProfileServices.getInstance().getEmptyProfimePicture(item));
        }

        //split tags
        String[] array = f.getTags().split(",");
        //Clear just in case
        tags.removeAllViews();
        //fill tags
        for(String s : array){
            TextView t = new TextView(getActivity());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(5,5,5,5);
            t.setLayoutParams(lp);
            t.setText(s);
            t.setBackgroundColor(getActivity().getResources().getColor(R.color.material_deep_teal_50));
            t.setTextColor(getActivity().getResources().getColor(R.color.cardview_light_background));
            t.setGravity(View.TEXT_ALIGNMENT_CENTER);
            t.setPaddingRelative(5,3,5,3);
            t.setTextSize(TypedValue.COMPLEX_UNIT_DIP ,13);

            tags.addView(t);
        }

        /** test if voted*/
        color_voted();

        /** a hack if internet is low */
        if(f.getViews()==0)
            views.setText("1");

        /** fill code viewer*/
        if(f.getCode()!=null) {
            codeView.setVisibility(View.VISIBLE);
            codeView.setOptions(Options.Default.get(getContext())
                    .withLanguage("java")
                    .withCode(f.getCode())
                    .withTheme(ColorTheme.DEFAULT));
        }
        else
            codeView.setVisibility(View.GONE);

        if(mine) forumEditButton.setVisibility(View.VISIBLE);
        attachEditForumListener();

        if(f.getEdited()!=null){
            editedText.setText("Last Edited: "+f.getEditedString());
            editedText.setVisibility(View.VISIBLE);
        }

    }

    public void color_voted(){
        if(selfVote==1){
            ratingUp.setBackgroundColor(getActivity().getResources().getColor(R.color.base_color_2));
            ratingDown.setBackgroundColor(getActivity().getResources().getColor(R.color.float_transparent));
        }
        else if(selfVote==-1){
            ratingDown.setBackgroundColor(getActivity().getResources().getColor(R.color.base_color_2));
            ratingUp.setBackgroundColor(getContext().getResources().getColor(R.color.float_transparent));
        }
        else{
            ratingUp.setBackgroundColor(getActivity().getResources().getColor(R.color.float_transparent));
            ratingDown.setBackgroundColor(getActivity().getResources().getColor(R.color.float_transparent));
        }
    }

    public void attachEditForumListener(){
        forumEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditForumFragment ef= new EditForumFragment();
                ef.setForum(f);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.root_share_fragment,ef)
                        .addToBackStack(null)
                        .commit();
                reloadAfterEdit=true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(reloadAfterEdit){
            load_forum();
            reloadAfterEdit=false;
        }
    }
}
