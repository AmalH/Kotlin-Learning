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
import amalhichri.androidprojects.com.kotlinlearning.services.UserServices;
import amalhichri.androidprojects.com.kotlinlearning.utils.Configuration;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.kbiakov.codeview.CodeView;
import io.github.kbiakov.codeview.adapters.Options;
import io.github.kbiakov.codeview.highlight.ColorTheme;
import me.originqiu.library.FlowLayout;

public class ForumShowFragment extends Fragment {


    private ForumQuestion f;
    private CircleImageView picture;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Answer> listComments = new ArrayList<>();

    private static int loaded_length;
    private boolean mine;

    private boolean loading = true, reloadAfterEdit = false;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    /**
     * to check upvotes
     */
    int selfVote = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FirebaseAuth.getInstance().getCurrentUser().reload();
        mine = false;
        return inflater.inflate(R.layout.fragment_forum_show, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new CommentsAdapter(new ArrayList<Answer>(), getContext());
        picture = getActivity().findViewById(R.id.forum_user_picture);
        layoutManager = new LinearLayoutManager(getContext());


        ((RecyclerView) getActivity().findViewById(R.id.comments_container_list)).setAdapter(adapter);

        /** vote btns listeners **/
        getActivity().findViewById(R.id.forum_up_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().findViewById(R.id.forum_up_arrow).setEnabled(false);
                ForumServices.getInstance().upvoteForum("dZb3TxK1x5dqQJkq7ve0d683VoA3",
                        getContext(), f.getId(), new ServerCallbacks() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                try {
                                    ((TextView) getActivity().findViewById(R.id.forum_rating_show)).setText((result.getInt("resp") > 0 ? "+" + result.getInt("resp") : result.getInt("resp") + "")
                                    );
                                    selfVote = 1;
                                    color_voted();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                getActivity().findViewById(R.id.forum_up_arrow).setEnabled(true);
                            }

                            @Override
                            public void onError(VolleyError result) {
                                Toast.makeText(getContext(), "Server problem ", Toast.LENGTH_SHORT).show();
                                getActivity().findViewById(R.id.forum_up_arrow).setEnabled(true);
                            }

                            @Override
                            public void onWrong(JSONObject result) {
                                load_forum();
                                getActivity().findViewById(R.id.forum_up_arrow).setEnabled(true);
                            }
                        });
            }
        });
        getActivity().findViewById(R.id.forum_down_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().findViewById(R.id.forum_down_arrow).setEnabled(false);
                ForumServices.getInstance().downvoteForum("dZb3TxK1x5dqQJkq7ve0d683VoA3",
                        getContext(), f.getId(), new ServerCallbacks() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                try {
                                    ((TextView) getActivity().findViewById(R.id.forum_rating_show)).setText((result.getInt("resp") > 0 ? "+" + result.getInt("resp") : result.getInt("resp") + "")
                                    );
                                    selfVote = -1;
                                    color_voted();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                getActivity().findViewById(R.id.forum_down_arrow).setEnabled(true);
                            }

                            @Override
                            public void onError(VolleyError result) {
                                Toast.makeText(getContext(), "Server problem ", Toast.LENGTH_SHORT).show();
                                getActivity().findViewById(R.id.forum_down_arrow).setEnabled(true);
                            }

                            @Override
                            public void onWrong(JSONObject result) {
                                load_forum();
                                getActivity().findViewById(R.id.forum_down_arrow).setEnabled(true);
                            }
                        });
            }
        });

        ((RecyclerView) getActivity().findViewById(R.id.comments_container_list)).setLayoutManager(layoutManager);

        /** swipe to refresh */
        ((SwipeRefreshLayout) getActivity().findViewById(R.id.comment_refresh_layout)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Configuration.isOnline(getContext())) {
                    FirebaseAuth.getInstance().getCurrentUser().reload();
                    loaded_length = 0;
                    load_comments(0);
                    load_forum();
                }
            }
        });

        loaded_length = 0;
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

    public void load_comments(int start_at) {
        if (start_at == 0) {
            loaded_length = 0;
            ((RecyclerView) getActivity().findViewById(R.id.comments_container_list)).removeAllViews();
            listComments.clear();
        }

        ((SwipeRefreshLayout) getActivity().findViewById(R.id.comment_refresh_layout)).setRefreshing(true);
        if (Configuration.isOnline(getContext()))
            ForumServices.getInstance().getComments("dZb3TxK1x5dqQJkq7ve0d683VoA3",
                    getContext(), start_at, f.getId(), new ServerCallbacks() {
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
                                    listComments.add(ForumServices.Answerparse_(array.getJSONObject(i)));
                                } catch (JSONException e) {
                                    Toast.makeText(getContext(), "Application error while loading forum , please report", Toast.LENGTH_SHORT).show();
                                    goShow = false;
                                }
                            }
                            /** All the work will be here **/
                            if (goShow) {
                                getActivity().findViewById(R.id.no_comments_txt).setVisibility(View.GONE);
                                getActivity().findViewById(R.id.comments_container_list).setVisibility(View.VISIBLE);
                                if (loaded_length == 0) {
                                    adapter = new CommentsAdapter(listComments, getContext());
                                    ((RecyclerView) getActivity().findViewById(R.id.comments_container_list)).setAdapter(adapter);
                                } else {
                                    adapter.notifyItemChanged(layoutManager.getChildCount());
                                }

                                //addCalculated
                                if (loaded_length == 0) loaded_length += 10;
                                else
                                    loaded_length += 8;

                            } else {
                                getActivity().findViewById(R.id.no_comments_txt).setVisibility(View.VISIBLE);
                                getActivity().findViewById(R.id.comments_container_list).setVisibility(View.GONE);
                            }
                            if (listComments.size() == 0) {
                                getActivity().findViewById(R.id.no_comments_txt).setVisibility(View.VISIBLE);
                                getActivity().findViewById(R.id.comments_container_list).setVisibility(View.GONE);
                            }
                            ((SwipeRefreshLayout) getActivity().findViewById(R.id.comment_refresh_layout)).setRefreshing(false);
                            loading = false;

                        }

                        @Override
                        public void onError(VolleyError result) {
                            getActivity().findViewById(R.id.no_comments_txt).setVisibility(View.VISIBLE);
                            getActivity().findViewById(R.id.comments_container_list).setVisibility(View.GONE);
                            ((SwipeRefreshLayout) getActivity().findViewById(R.id.comment_refresh_layout)).setRefreshing(false);
                            loading = false;
                        }

                        @Override
                        public void onWrong(JSONObject result) {
                            getActivity().findViewById(R.id.no_comments_txt).setVisibility(View.VISIBLE);
                            getActivity().findViewById(R.id.comments_container_list).setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Problem , please report!", Toast.LENGTH_SHORT).show();
                            ((SwipeRefreshLayout) getActivity().findViewById(R.id.comment_refresh_layout)).setRefreshing(false);
                            loading = false;
                        }
                    });
        else {
            getActivity().findViewById(R.id.no_comments_txt).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.comments_container_list).setVisibility(View.GONE);
            ((SwipeRefreshLayout) getActivity().findViewById(R.id.comment_refresh_layout)).setRefreshing(false);
            loading = false;
        }

    }

    public void setActionListenerToAdd() {
        getActivity().findViewById(R.id.add_Comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // if (UserServices.getInstance().is_verified(getContext())) {
                    /**
                     * add a comment
                     */
                    final LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
                    /** show add dialog */

                    new MaterialStyledDialog.Builder(getActivity())
                            .setTitle("Post a new comment")
                            .setStyle(Style.HEADER_WITH_ICON)
                            .withIconAnimation(false)
                            .setCancelable(true)
                            .withDialogAnimation(true)
                            .setHeaderColor(R.color.base_color_1)
                            .setHeaderScaleType(ImageView.ScaleType.CENTER_CROP)
                            .setCustomView(inflater.inflate(R.layout.comment_add_view, null), 10, 30, 10, 10)
                            .setIcon(R.drawable.ic_action_add_comment)
                            .setPositiveText("Comment")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    final String ct = ((TextView) (inflater.inflate(R.layout.comment_add_view, null)).findViewById(R.id.comment_post_content)).getText().toString().trim();
                                   // if (!ct.isEmpty() && Configuration.isOnline(getContext()) && UserServices.getInstance().is_verified(getContext())) {
                                        ForumServices.getInstance().addAnswer(getContext(), ct, f.getId(), "dZb3TxK1x5dqQJkq7ve0d683VoA3", new ServerCallbacks() {
                                            @Override
                                            public void onSuccess(JSONObject result) {
                                                load_comments(0);
                                            }

                                            @Override
                                            public void onError(VolleyError result) {
                                                Toast.makeText(getContext(), "Server problem", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onWrong(JSONObject result) {
                                                Toast.makeText(getContext(), "Problem while posting, please retry", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    //} else {
                                      //  Toast.makeText(getContext(), "Nothing to post", Toast.LENGTH_SHORT).show();
                                    //}
                                }
                            })
                            .show();

               // }
            }
        });
    }


    private void attach_scrollListener() {
        loading = false;
        ((RecyclerView) getActivity().findViewById(R.id.comments_container_list)).addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                    if (Configuration.isOnline(getContext()) && !loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount - 3) {
                            loading = true;
                            load_comments(loaded_length);
                        }
                    }
                }
            }
        });
    }

    private void load_forum() {
        ForumServices.getInstance().getForum("dZb3TxK1x5dqQJkq7ve0d683VoA3", getContext(), f.getId(),
                new ServerCallbacks() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                            f = ForumServices.parse_(result.getJSONArray("forum").getJSONObject(0));
                            if (result.has("selfvote")) {
                                selfVote = result.getInt("selfvote");
                            } else
                                selfVote = 0;
                            if (getContext() != null)
                                setForumContent();
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

    private void setForumContent() {

        if (f.getId_User().equals("dZb3TxK1x5dqQJkq7ve0d683VoA3"))
            mine = true;
        ((TextView) getActivity().findViewById(R.id.forum_rating_show)).setText((f.getRating() > 0 ? "+" + f.getRating() : f.getRating() + ""));
        ((TextView) getActivity().findViewById(R.id.forum_views_txt)).setText(f.getViews() + "");
        ((TextView) getActivity().findViewById(R.id.forum_subject)).setText(f.getSubject());
        ((TextView) getActivity().findViewById(R.id.forum_content)).setText(f.getContent());

        if (!mine)
            ((TextView) getActivity().findViewById(R.id.forum_username)).setText(f.getUser_name());
        else ((TextView) getActivity().findViewById(R.id.forum_username)).setText("By me ");

        ((TextView) (getActivity().findViewById(R.id.forum_created))).setText(f.getCreated_string());

        if (f.getUser_picture_url() != null)
            Picasso.with(getContext()).load(Uri.parse(f.getUser_picture_url())).into(picture);
        else {
            String item = f.getUser_name();
            ((ImageView) getActivity().findViewById(R.id.forum_user_picture)).setImageDrawable(UserServices.getInstance().getEmptyProfimePicture(item));
        }

        //split tags
        String[] array = f.getTags().split(",");
        //Clear just in case
        ((FlowLayout) getActivity().findViewById(R.id.forum_tags)).removeAllViews();
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

            ((FlowLayout) getActivity().findViewById(R.id.forum_tags)).addView(t);
        }

        /** test if voted*/
        color_voted();

        /**  if internet is low */
        if (f.getViews() == 0)
            ((TextView) getActivity().findViewById(R.id.forum_views_txt)).setText("1");

        /** set code viewer*/
        if (f.getCode() != null) {
            getActivity().findViewById(R.id.code_view).setVisibility(View.VISIBLE);
            ((CodeView) getActivity().findViewById(R.id.code_view)).setOptions(Options.Default.get(getContext())
                    .withLanguage("java")
                    .withCode(f.getCode())
                    .withTheme(ColorTheme.DEFAULT));
        } else
            (getActivity().findViewById(R.id.code_view)).setVisibility(View.GONE);

        if (mine) getActivity().findViewById(R.id.forum_edit_button).setVisibility(View.VISIBLE);
        attachEditForumListener();

        if (f.getEdited() != null) {
            ((TextView) getActivity().findViewById(R.id.forum_show_edited)).setText("Last Edited: " + f.getEditedString());
            getActivity().findViewById(R.id.forum_show_edited).setVisibility(View.VISIBLE);
        }

    }

    private void color_voted() {
        if (selfVote == 1) {
            getActivity().findViewById(R.id.forum_up_arrow).setBackgroundColor(getActivity().getResources().getColor(R.color.base_color_2));
            getActivity().findViewById(R.id.forum_down_arrow).setBackgroundColor(getActivity().getResources().getColor(R.color.float_transparent));
        } else if (selfVote == -1) {
            getActivity().findViewById(R.id.forum_down_arrow).setBackgroundColor(getActivity().getResources().getColor(R.color.base_color_2));
            getActivity().findViewById(R.id.forum_up_arrow).setBackgroundColor(getContext().getResources().getColor(R.color.float_transparent));
        } else {
            getActivity().findViewById(R.id.forum_up_arrow).setBackgroundColor(getActivity().getResources().getColor(R.color.float_transparent));
            getActivity().findViewById(R.id.forum_down_arrow).setBackgroundColor(getActivity().getResources().getColor(R.color.float_transparent));
        }
    }

    private void attachEditForumListener() {
        getActivity().findViewById(R.id.forum_edit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditForumFragment ef = new EditForumFragment();
                ef.setForum(f);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.root_share_fragment, ef)
                        .addToBackStack(null)
                        .commit();
                reloadAfterEdit = true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (reloadAfterEdit) {
            load_forum();
            reloadAfterEdit = false;
        }
    }
}
