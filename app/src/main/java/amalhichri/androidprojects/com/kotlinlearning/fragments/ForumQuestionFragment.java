package amalhichri.androidprojects.com.kotlinlearning.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
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
import amalhichri.androidprojects.com.kotlinlearning.services.UsersServices;
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
        if(isVisible()){
            getActivity().findViewById(R.id.forumQstUpVote).setBackgroundResource(R.drawable.ic_arrow_up);
            getActivity().findViewById(R.id.forumQstnDownVote).setBackgroundResource(R.drawable.ic_arrow_down);
            ((ImageView)getActivity().findViewById(R.id.addCommentBtn)).setImageResource(R.drawable.ic_send_comment);
            (getActivity().findViewById(R.id.addCommentBtn)).setEnabled(false);
        }
        return inflater.inflate(R.layout.fragment_forum_question, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        getActivity().findViewById(R.id.forumQstUpVote).setBackgroundResource(R.drawable.ic_arrow_up);
        getActivity().findViewById(R.id.forumQstnDownVote).setBackgroundResource(R.drawable.ic_arrow_down);
       ((ImageView)getActivity().findViewById(R.id.addCommentBtn)).setImageResource(R.drawable.ic_send_comment);
        (getActivity().findViewById(R.id.addCommentBtn)).setEnabled(false);

        adapter = new CommentsAdapter(new ArrayList<ForumAnswer>(), getContext());
        picture = getActivity().findViewById(R.id.forumQuestionUserPic);
        layoutManager = new LinearLayoutManager(getContext());


        ((RecyclerView) getActivity().findViewById(R.id.commentsContainer)).setAdapter(adapter);

        /** votes up/dpwn btns **/
        getActivity().findViewById(R.id.upVoteContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().findViewById(R.id.forumQstUpVote).setEnabled(false);
                ForumsServices.getInstance().upvoteForumPost(Statics.auth.getCurrentUser().getUid(),
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
        getActivity().findViewById(R.id.downVoteContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().findViewById(R.id.forumQstnDownVote).setEnabled(false);
                ForumsServices.getInstance().downvoteForumPost(Statics.auth.getCurrentUser().getUid(),
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
                                getActivity().findViewById(R.id.forumQstnDownVote).setEnabled(true);
                            }

                            @Override
                            public void onError(VolleyError result) {
                                Toast.makeText(getContext(), "Server problem ", Toast.LENGTH_SHORT).show();
                                getActivity().findViewById(R.id.forumQstnDownVote).setEnabled(true);
                            }

                            @Override
                            public void onWrong(JSONObject result) {
                                loadForum();
                                getActivity().findViewById(R.id.forumQstnDownVote).setEnabled(true);
                            }
                        });
            }
        });

        ((RecyclerView) getActivity().findViewById(R.id.commentsContainer)).setLayoutManager(layoutManager);

        /** swipe to refresh */
        ((SwipeRefreshLayout) getActivity().findViewById(R.id.commentsRefreshLayout)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if((((ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo())
                        != null && (((ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo()).isConnected()) {
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
                    if((((ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo())
                            != null && (((ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo()).isConnected() && !loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount - 3) {
                            loading = true;
                            loadComments(loaded_length);
                        }
                    }
                }
            }
        });

        /** add comment btn **/
       ((EditText)getActivity().findViewById(R.id.commentToForumContent)).addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
               ((ImageView)getActivity().findViewById(R.id.addCommentBtn)).setImageResource(R.drawable.ic_send_comment_selected);
               (getActivity().findViewById(R.id.addCommentBtn)).setEnabled(true);
           }

           @Override
           public void afterTextChanged(Editable s) {
           }
       });
        getActivity().findViewById(R.id.addCommentBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String answerContent = ((EditText)getActivity().findViewById(R.id.commentToForumContent)).getText().toString().trim();
                ForumsServices.getInstance().addForumPostAnswer(getContext(), answerContent, currentQuestion.getId(), Statics.auth.getCurrentUser().getUid(), new ServerCallbacks() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        loadComments(0);
                        ((EditText)getActivity().findViewById(R.id.commentToForumContent)).setText("");
                        (getActivity().findViewById(R.id.forumQstnScrollView)).post(new Runnable() {
                            @Override
                            public void run() {
                                ((ScrollView)getActivity().findViewById(R.id.forumQstnScrollView)).fullScroll(View.FOCUS_DOWN);
                            }
                        });
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
        });
    }

    private void loadComments(int start_at) {
        if (start_at == 0) {
            loaded_length = 0;
            ((RecyclerView) getActivity().findViewById(R.id.commentsContainer)).removeAllViews();
            listComments.clear();
        }

        ((SwipeRefreshLayout) getActivity().findViewById(R.id.commentsRefreshLayout)).setRefreshing(true);
        if((((ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo())
                != null && (((ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo()).isConnected())
            ForumsServices.getInstance().getForumPostComments(Statics.auth.getCurrentUser().getUid(),
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
                                getActivity().findViewById(R.id.commentsContainer).setVisibility(View.VISIBLE);
                                if (loaded_length == 0) {
                                    adapter = new CommentsAdapter(listComments, getContext());
                                    ((RecyclerView) getActivity().findViewById(R.id.commentsContainer)).setAdapter(adapter);
                                    if(listComments.size()==0)
                                        ((TextView) getActivity().findViewById(R.id.commentsNbTv)).setText("No answers/comments yet..");
                                    if(listComments.size()==1)
                                        ((TextView) getActivity().findViewById(R.id.commentsNbTv)).setText("1 comment");
                                    if(listComments.size()!=0)
                                        ((TextView) getActivity().findViewById(R.id.commentsNbTv)).setText(listComments.size()+" comments");
                                } else {
                                    adapter.notifyItemChanged(layoutManager.getChildCount());
                                }


                                if (loaded_length == 0) loaded_length += 10;
                                else
                                    loaded_length += 8;

                            } else {
                                getActivity().findViewById(R.id.commentsContainer).setVisibility(View.GONE);
                            }
                            if (listComments.size() == 0) {
                                getActivity().findViewById(R.id.commentsContainer).setVisibility(View.GONE);
                            }
                            ((SwipeRefreshLayout) getActivity().findViewById(R.id.commentsRefreshLayout)).setRefreshing(false);
                            loading = false;

                        }

                        @Override
                        public void onError(VolleyError result) {
                            getActivity().findViewById(R.id.commentsContainer).setVisibility(View.GONE);
                            ((SwipeRefreshLayout) getActivity().findViewById(R.id.commentsRefreshLayout)).setRefreshing(false);
                            loading = false;
                        }

                        @Override
                        public void onWrong(JSONObject result) {
                            getActivity().findViewById(R.id.commentsContainer).setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Problem , please report!", Toast.LENGTH_SHORT).show();
                            ((SwipeRefreshLayout) getActivity().findViewById(R.id.commentsRefreshLayout)).setRefreshing(false);
                            loading = false;
                        }
                    });
        else {
            getActivity().findViewById(R.id.commentsContainer).setVisibility(View.GONE);
            ((SwipeRefreshLayout) getActivity().findViewById(R.id.commentsRefreshLayout)).setRefreshing(false);
            loading = false;
        }

    }

    private void loadForum() {
        UsersServices.getInstance().getUserById(Statics.auth.getCurrentUser().getUid(), getActivity(), new ServerCallbacks() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    if (result.getString("picture") != null) {
                        Picasso.with(getActivity()).load(Uri.parse(result.getString("picture"))).into((ImageView) getActivity().findViewById(R.id.currentUserPic));
                    }
                    if (result.getString("picture").isEmpty())
                        ((ImageView) getActivity().findViewById(R.id.currentUserPic)).setImageDrawable(Statics.getPlaceholderProfilePic(result.getString("username")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError result) {
                Toast.makeText(getActivity(), "error class" + result.getClass().getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onWrong(JSONObject result) {
                Toast.makeText(getActivity(), "error----" + result.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        ForumsServices.getInstance().getForumPost(Statics.auth.getCurrentUser().getUid(), getContext(), currentQuestion.getId(),
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
                                if (currentQuestion.getUserId().equals(Statics.auth.getCurrentUser().getUid()))
                                    mine = true;
                                ((TextView) getActivity().findViewById(R.id.forumQstRatingShow)).setText((currentQuestion.getRating() > 0 ? "+" + currentQuestion.getRating() : currentQuestion.getRating() + ""));
                                ((TextView) getActivity().findViewById(R.id.forumQstViews)).setText(currentQuestion.getViews() + "");
                                ((TextView) getActivity().findViewById(R.id.forumQstSubject)).setText(currentQuestion.getSubject());
                                ((TextView) getActivity().findViewById(R.id.forumQstnContent)).setText(currentQuestion.getContent());

                                if (!mine)
                                    ((TextView) getActivity().findViewById(R.id.forumQstnUsername)).setText(currentQuestion.getUserName());
                                else
                                    ((TextView) getActivity().findViewById(R.id.forumQstnUsername)).setText("By me ");

                                ((TextView) (getActivity().findViewById(R.id.forumQstnCreated))).setText(currentQuestion.getCreated_string());

                                if (currentQuestion.getUserPictureUrl() != null)
                                    Picasso.with(getContext()).load(Uri.parse(currentQuestion.getUserPictureUrl())).into(picture);
                                else {
                                    String item = currentQuestion.getUserName();
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
                                    lp.setMargins(5,5,5,5);
                                    t.setLayoutParams(lp);
                                    t.setText(s);
                                    t.setBackgroundResource(R.drawable.button_background2);
                                    t.setTextColor(getActivity().getResources().getColor(R.color.cardview_light_background));
                                    t.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                    t.setPadding(12,5,12,5);
                                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP ,11);

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
            getActivity().findViewById(R.id.forumQstUpVote).setBackgroundResource(R.drawable.ic_arrow_up_selected);
            getActivity().findViewById(R.id.forumQstnDownVote).setBackgroundResource(R.drawable.ic_arrow_down);
        } else if (selfVote == -1) {
            getActivity().findViewById(R.id.forumQstnDownVote).setBackgroundResource(R.drawable.ic_arrow_down_selected);
            getActivity().findViewById(R.id.forumQstUpVote).setBackgroundResource(R.drawable.ic_arrow_up);
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