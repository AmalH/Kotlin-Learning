package amalhichri.androidprojects.com.kotlinlearning.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.adapters.CommentsAdapter;
import amalhichri.androidprojects.com.kotlinlearning.models.ForumAnswer;
import amalhichri.androidprojects.com.kotlinlearning.models.ForumQuestion;
import amalhichri.androidprojects.com.kotlinlearning.services.ForumsServices;
import amalhichri.androidprojects.com.kotlinlearning.services.ServerCallbacks;
import amalhichri.androidprojects.com.kotlinlearning.utils.Configuration;
import amalhichri.androidprojects.com.kotlinlearning.utils.Statics;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.kbiakov.codeview.CodeView;
import io.github.kbiakov.codeview.adapters.Options;
import io.github.kbiakov.codeview.highlight.ColorTheme;
import me.originqiu.library.FlowLayout;

public class ForumQuestionFragment extends Fragment {


    private ForumQuestion currentQuestion;
    private CircleImageView picture;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ForumAnswer> listComments = new ArrayList<>();

    private static int loaded_length;
    private boolean mine;

    private boolean loading = true, reloadAfterEdit = false;
    private int pastVisiblesItems, visibleItemCount, totalItemCount, selfVote = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FirebaseAuth.getInstance().getCurrentUser().reload();
        mine = false;
        return inflater.inflate(R.layout.fragment_forum_question, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new CommentsAdapter(new ArrayList<ForumAnswer>(), getContext());
        picture = getActivity().findViewById(R.id.forumQuestionUserPic);
        layoutManager = new LinearLayoutManager(getContext());


        ((RecyclerView) getActivity().findViewById(R.id.commentsContainer)).setAdapter(adapter);

        ((TextView) getActivity().findViewById(R.id.commentsNbTv)).setText("11" + " comments");

        /** votes up/dpwn btns **/
        getActivity().findViewById(R.id.forumQstUpVote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().findViewById(R.id.forumQstUpVote).setEnabled(false);
                ForumsServices.getInstance().upvoteForumPost("dZb3TxK1x5dqQJkq7ve0d683VoA3",
                        getContext(), currentQuestion.getId(), new ServerCallbacks() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                try {
                                    ((TextView) getActivity().findViewById(R.id.forumQstRatingShow)).setText((result.getInt("resp") > 0 ? "+" + result.getInt("resp") : result.getInt("resp") + "")
                                    );
                                    selfVote = 1;
                                    setVotesBtnColor();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                getActivity().findViewById(R.id.forumQstUpVote).setEnabled(true);
                            }

                            @Override
                            public void onError(VolleyError result) {
                                Toast.makeText(getContext(), "Server problem ", Toast.LENGTH_SHORT).show();
                                getActivity().findViewById(R.id.forumQstUpVote).setEnabled(true);
                            }

                            @Override
                            public void onWrong(JSONObject result) {
                                loadForum();
                                getActivity().findViewById(R.id.forumQstUpVote).setEnabled(true);
                            }
                        });
            }
        });
        getActivity().findViewById(R.id.forumQstnDownArrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().findViewById(R.id.forumQstnDownArrow).setEnabled(false);
                ForumsServices.getInstance().downvoteForumPost("dZb3TxK1x5dqQJkq7ve0d683VoA3",
                        getContext(), currentQuestion.getId(), new ServerCallbacks() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                try {
                                    ((TextView) getActivity().findViewById(R.id.forumQstRatingShow)).setText((result.getInt("resp") > 0 ? "+" + result.getInt("resp") : result.getInt("resp") + "")
                                    );
                                    selfVote = -1;
                                    setVotesBtnColor();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                getActivity().findViewById(R.id.forumQstnDownArrow).setEnabled(true);
                            }

                            @Override
                            public void onError(VolleyError result) {
                                Toast.makeText(getContext(), "Server problem ", Toast.LENGTH_SHORT).show();
                                getActivity().findViewById(R.id.forumQstnDownArrow).setEnabled(true);
                            }

                            @Override
                            public void onWrong(JSONObject result) {
                                loadForum();
                                getActivity().findViewById(R.id.forumQstnDownArrow).setEnabled(true);
                            }
                        });
            }
        });

        ((RecyclerView) getActivity().findViewById(R.id.commentsContainer)).setLayoutManager(layoutManager);

        /** swipe to refresh */
        ((SwipeRefreshLayout) getActivity().findViewById(R.id.commentsRefreshLayout)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Configuration.isOnline(getContext())) {
                    FirebaseAuth.getInstance().getCurrentUser().reload();
                    loaded_length = 0;
                    loadComments(0);
                    loadForum();
                }
            }
        });

        loaded_length = 0;
        loadComments(0);


        /** attach scroll listener **/
        loading = false;
        ((RecyclerView) getActivity().findViewById(R.id.commentsContainer)).addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                    if (Configuration.isOnline(getContext()) && !loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount - 3) {
                            loading = true;
                            loadComments(loaded_length);
                        }
                    }
                }
            }
        });

        /** add comment btn **/
        getActivity().findViewById(R.id.addCommentBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View dialogView = ((LayoutInflater) getActivity().getSystemService(getContext().LAYOUT_INFLATER_SERVICE)).inflate(R.layout.comment_add_view,null);
                new MaterialStyledDialog.Builder(getActivity())
                        .setTitle("Drop your answer/comment here")
                        //.setStyle(Style.HEADER_WITH_ICON)
                       // .withIconAnimation(false)
                        .setCancelable(true)
                        .withDialogAnimation(true)
                        .setHeaderColor(R.color.float_transparent)
                        //.setHeaderColor(R.color.baseColor1)
                       // .setHeaderScaleType(ImageView.ScaleType.CENTER_CROP)
                        .setCustomView( dialogView, 10, 30, 10, 10)
                        //.setIcon(R.drawable.ic_action_add_comment)
                        .setPositiveText("Post")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                final String answerContent = ((TextView) dialogView.findViewById(R.id.answerToPostContent)).getText().toString().trim();
                                ForumsServices.getInstance().addForumPostAnswer(getContext(), answerContent, currentQuestion.getId(), "dZb3TxK1x5dqQJkq7ve0d683VoA3", new ServerCallbacks() {
                                    @Override
                                    public void onSuccess(JSONObject result) {
                                        loadComments(0);
                                    }

                                    @Override
                                    public void onError(VolleyError result) {
                                        Toast.makeText(getContext(), "Server problem+++" +result.getClass().getName(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onWrong(JSONObject result) {
                                        Toast.makeText(getContext(), "Problem while posting, please retry", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .show();
            }
        });
    }

    private void loadComments(int start_at) {
        if (start_at == 0) {
            loaded_length = 0;
            ((RecyclerView) getActivity().findViewById(R.id.commentsContainer)).removeAllViews();
            listComments.clear();
        }

        ((SwipeRefreshLayout) getActivity().findViewById(R.id.commentsRefreshLayout)).setRefreshing(true);
        if (Configuration.isOnline(getContext()))
            ForumsServices.getInstance().getForumPostComments("dZb3TxK1x5dqQJkq7ve0d683VoA3",
                    getContext(), start_at, currentQuestion.getId(), new ServerCallbacks() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            /**
                             * do show
                             */
                            boolean goShow = true;
                            JSONArray array = new JSONArray();
                            try {
                                array = result.getJSONArray("comments");
                            } catch (JSONException e) {
                                Toast.makeText(getContext(), "Server error while loading forum , please report", Toast.LENGTH_SHORT).show();
                                goShow = false;
                            }
                            for (int i = 0; i < array.length(); i++) {
                                try {
                                    /** parse forum and add it to the arraylist**/
                                    listComments.add(ForumsServices.jsonToForumAnswer(array.getJSONObject(i)));
                                } catch (JSONException e) {
                                    Toast.makeText(getContext(), "Application error while loading forum , please report", Toast.LENGTH_SHORT).show();
                                    goShow = false;
                                }
                            }
                            /** All the work will be here **/
                            if (goShow) {
                                ((TextView) getActivity().findViewById(R.id.commentsNbTv)).setText("No comments ...");
                                getActivity().findViewById(R.id.commentsContainer).setVisibility(View.VISIBLE);
                                if (loaded_length == 0) {
                                    adapter = new CommentsAdapter(listComments, getContext());
                                    ((RecyclerView) getActivity().findViewById(R.id.commentsContainer)).setAdapter(adapter);
                                } else {
                                    adapter.notifyItemChanged(layoutManager.getChildCount());
                                }

                                //addCalculated
                                if (loaded_length == 0) loaded_length += 10;
                                else
                                    loaded_length += 8;

                            } else {
                                getActivity().findViewById(R.id.commentsNbTv).setVisibility(View.VISIBLE);
                                getActivity().findViewById(R.id.commentsContainer).setVisibility(View.GONE);
                            }
                            if (listComments.size() == 0) {
                                getActivity().findViewById(R.id.commentsNbTv).setVisibility(View.VISIBLE);
                                getActivity().findViewById(R.id.commentsContainer).setVisibility(View.GONE);
                            }
                            ((SwipeRefreshLayout) getActivity().findViewById(R.id.commentsRefreshLayout)).setRefreshing(false);
                            loading = false;

                        }

                        @Override
                        public void onError(VolleyError result) {
                            getActivity().findViewById(R.id.commentsNbTv).setVisibility(View.VISIBLE);
                            getActivity().findViewById(R.id.commentsContainer).setVisibility(View.GONE);
                            ((SwipeRefreshLayout) getActivity().findViewById(R.id.commentsRefreshLayout)).setRefreshing(false);
                            loading = false;
                        }

                        @Override
                        public void onWrong(JSONObject result) {
                            getActivity().findViewById(R.id.commentsNbTv).setVisibility(View.VISIBLE);
                            getActivity().findViewById(R.id.commentsContainer).setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Problem , please report!", Toast.LENGTH_SHORT).show();
                            ((SwipeRefreshLayout) getActivity().findViewById(R.id.commentsRefreshLayout)).setRefreshing(false);
                            loading = false;
                        }
                    });
        else {
            getActivity().findViewById(R.id.commentsNbTv).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.commentsContainer).setVisibility(View.GONE);
            ((SwipeRefreshLayout) getActivity().findViewById(R.id.commentsRefreshLayout)).setRefreshing(false);
            loading = false;
        }

    }

    private void loadForum() {
        ForumsServices.getInstance().getForumPost("dZb3TxK1x5dqQJkq7ve0d683VoA3", getContext(), currentQuestion.getId(),
                new ServerCallbacks() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                            currentQuestion = ForumsServices.jsonToForumQuestion(result.getJSONArray("forum").getJSONObject(0));
                            if (result.has("selfvote")) {
                                selfVote = result.getInt("selfvote");
                            } else
                                selfVote = 0;
                            if (getContext() != null) {
                                if (currentQuestion.getId_User().equals("dZb3TxK1x5dqQJkq7ve0d683VoA3"))
                                    mine = true;
                                ((TextView) getActivity().findViewById(R.id.forumQstRatingShow)).setText((currentQuestion.getRating() > 0 ? "+" + currentQuestion.getRating() : currentQuestion.getRating() + ""));
                                ((TextView) getActivity().findViewById(R.id.forumQstViews)).setText(currentQuestion.getViews() + "");
                                ((TextView) getActivity().findViewById(R.id.forumQstSubject)).setText(currentQuestion.getSubject());
                                ((TextView) getActivity().findViewById(R.id.forumQstnContent)).setText(currentQuestion.getContent());

                                if (!mine)
                                    ((TextView) getActivity().findViewById(R.id.forumQstnUsername)).setText(currentQuestion.getUser_name());
                                else
                                    ((TextView) getActivity().findViewById(R.id.forumQstnUsername)).setText("By me ");

                                ((TextView) (getActivity().findViewById(R.id.forumQstnCreated))).setText(currentQuestion.getCreated_string());

                                if (currentQuestion.getUser_picture_url() != null)
                                    Picasso.with(getContext()).load(Uri.parse(currentQuestion.getUser_picture_url())).into(picture);
                                else {
                                    String item = currentQuestion.getUser_name();
                                    ((ImageView) getActivity().findViewById(R.id.forumQuestionUserPic)).setImageDrawable(Statics.getPlaceholderProfilePic(item));
                                }

                                //split tags
                                String[] array = currentQuestion.getTags().split(",");
                                //Clear just in case
                                ((FlowLayout) getActivity().findViewById(R.id.forumQstTags)).removeAllViews();
                                //fill tags
                                for (String s : array) {
                                    TextView t = new TextView(getActivity());
                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    lp.setMargins(5, 5, 5, 5);
                                    t.setLayoutParams(lp);
                                    t.setText(s);
                                    t.setBackgroundColor(getActivity().getResources().getColor(R.color.material_deep_teal_50));
                                    t.setTextColor(getActivity().getResources().getColor(R.color.cardview_light_background));
                                    t.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                    t.setPaddingRelative(5, 3, 5, 3);
                                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);

                                    ((FlowLayout) getActivity().findViewById(R.id.forumQstTags)).addView(t);
                                }

                                /** test if voted*/
                                setVotesBtnColor();

                                /**  if internet is low */
                                if (currentQuestion.getViews() == 0)
                                    ((TextView) getActivity().findViewById(R.id.forumQstViews)).setText("1");

                                /** set code viewer*/
                                if (currentQuestion.getCode() != null) {
                                    getActivity().findViewById(R.id.codeViewerInQstn).setVisibility(View.VISIBLE);
                                    ((CodeView) getActivity().findViewById(R.id.codeViewerInQstn)).setOptions(Options.Default.get(getContext())
                                            .withLanguage("java")
                                            .withCode(currentQuestion.getCode())
                                            .withTheme(ColorTheme.DEFAULT));
                                } else
                                    (getActivity().findViewById(R.id.codeViewerInQstn)).setVisibility(View.GONE);

                                if (mine)
                                    getActivity().findViewById(R.id.forumQstEditBtn).setVisibility(View.VISIBLE);
                                getActivity().findViewById(R.id.forumQstEditBtn).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        EditForumQuestionFragment ef = new EditForumQuestionFragment();
                                        ef.setForum(currentQuestion);
                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.root_share_fragment, ef)
                                                .addToBackStack(null)
                                                .commit();
                                        reloadAfterEdit = true;
                                    }
                                });

                                if (currentQuestion.getEdited() != null) {
                                    ((TextView) getActivity().findViewById(R.id.forumQstnShowEdited)).setText("Last Edited: " + currentQuestion.getEditedString());
                                    getActivity().findViewById(R.id.forumQstnShowEdited).setVisibility(View.VISIBLE);
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(VolleyError result) {
                        Toast.makeText(getContext(), "error loading", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onWrong(JSONObject result) {
                        Toast.makeText(getContext(), "wrong loading", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setVotesBtnColor() {
        if (selfVote == 1) {
            getActivity().findViewById(R.id.forumQstUpVote).setBackgroundColor(getActivity().getResources().getColor(R.color.baseColor2));
            getActivity().findViewById(R.id.forumQstnDownArrow).setBackgroundColor(getActivity().getResources().getColor(R.color.float_transparent));
        } else if (selfVote == -1) {
            getActivity().findViewById(R.id.forumQstnDownArrow).setBackgroundColor(getActivity().getResources().getColor(R.color.baseColor2));
            getActivity().findViewById(R.id.forumQstUpVote).setBackgroundColor(getContext().getResources().getColor(R.color.float_transparent));
        } else {
            getActivity().findViewById(R.id.forumQstUpVote).setBackgroundColor(getActivity().getResources().getColor(R.color.float_transparent));
            getActivity().findViewById(R.id.forumQstnDownArrow).setBackgroundColor(getActivity().getResources().getColor(R.color.float_transparent));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        loadForum();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (reloadAfterEdit) {
            loadForum();
            reloadAfterEdit = false;
        }
    }

    public void setCurrentQuestion(ForumQuestion currentQuestion) {
        this.currentQuestion = currentQuestion;
    }
}