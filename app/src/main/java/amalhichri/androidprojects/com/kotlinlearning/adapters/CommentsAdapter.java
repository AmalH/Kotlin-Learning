package amalhichri.androidprojects.com.kotlinlearning.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.models.ForumAnswer;
import amalhichri.androidprojects.com.kotlinlearning.services.ForumsServices;
import amalhichri.androidprojects.com.kotlinlearning.services.ServerCallbacks;
import amalhichri.androidprojects.com.kotlinlearning.utils.Statics;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Amal on 21/12/2017.
 */

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentItem_ViewHolder> {

    private ArrayList<ForumAnswer> forumAnswersList;
    private Context context;
    private ProgressDialog progressDialog;

    public CommentsAdapter(ArrayList<ForumAnswer> forum_list, Context context) {
        this.forumAnswersList = forum_list;
        this.context = context;
    }

    @Override
    public CommentsAdapter.CommentItem_ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_comment_item, parent, false);
        CommentsAdapter.CommentItem_ViewHolder shareItem_viewHolder = new CommentsAdapter.CommentItem_ViewHolder(view);
        progressDialog = new ProgressDialog(context);
        return shareItem_viewHolder;
    }

    @Override
    public void onBindViewHolder(final CommentItem_ViewHolder holder, final int position) {

        holder.content.setText(forumAnswersList.get(position).getContent());
        holder.user_name.setText(forumAnswersList.get(position).getUser_name_captalized());
        holder.rating.setText(forumAnswersList.get(position).getRatingString());
        holder.createed.setText(forumAnswersList.get(position).getCreated_string());

        if (forumAnswersList.get(position).getUserpicture() != null)
            Picasso.with(context).load(Uri.parse(forumAnswersList.get(position).getUserpicture())).into(holder.user_picture);
        else {
            holder.user_picture.setImageDrawable(Statics.getPlaceholderProfilePic(forumAnswersList.get(position).getUser_name_captalized()));

        }

        if ((Statics.auth.getCurrentUser().getUid()).equals(forumAnswersList.get(position).getUserid())) {
            holder.delete.setVisibility(View.VISIBLE);
        }

        holder.upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ForumsServices.getInstance().upvoteForumPostComment(Statics.auth.getCurrentUser().getUid(),
                        context, forumAnswersList.get(position).getId(), new ServerCallbacks() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                try {
                                    holder.rating.setText(
                                            (result.getInt("resp") > 0 ? "+" + result.getInt("resp") : result.getInt("resp") + "")
                                    );
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(VolleyError result) {
                                Toast.makeText(context, "Cant update votes ! Please check internet connection !", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onWrong(JSONObject result) {
                                try {
                                    holder.rating.setText(
                                            (result.getInt("resp") > 0 ? "+" + result.getInt("resp") : result.getInt("resp") + "")
                                    );
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            }
        });

        holder.downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ForumsServices.getInstance().downvoteForumPostComment(Statics.auth.getCurrentUser().getUid(),
                        context, forumAnswersList.get(position).getId(), new ServerCallbacks() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                try {
                                    holder.rating.setText(
                                            (result.getInt("resp") > 0 ? "+" + result.getInt("resp") : result.getInt("resp") + "")
                                    );
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(VolleyError result) {
                                Toast.makeText(context,"Cant update votes ! Please check internet connection !", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onWrong(JSONObject result) {
                                try {
                                    holder.rating.setText(
                                            (result.getInt("resp") > 0 ? "+" + result.getInt("resp") : result.getInt("resp") + "")
                                    );
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Remove post")
                        .setMessage("Are you sure you want to remove this post ?")
                        .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int whichButton) {
                                progressDialog.setMessage("Removing...");
                                progressDialog.show();

                                ForumsServices.getInstance().deleteComment(
                                        Statics.auth.getCurrentUser().getUid(), context, forumAnswersList.get(position).getId(), new ServerCallbacks() {
                                            @Override
                                            public void onSuccess(JSONObject result) {
                                                if (progressDialog.isShowing())
                                                    progressDialog.dismiss();
                                                forumAnswersList.remove(position);
                                                notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onError(VolleyError result) {
                                                if (progressDialog.isShowing())
                                                    progressDialog.dismiss();
                                                Toast.makeText(context, "Please check internet connection !", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onWrong(JSONObject result) {
                                                if (progressDialog.isShowing())
                                                    progressDialog.dismiss();
                                                Toast.makeText(context, "Please check internet connection !", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return forumAnswersList.size();
    }

    public static class CommentItem_ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView user_picture;
        TextView content, user_name, rating, createed;
        ImageButton upvote, downvote, delete;

        public CommentItem_ViewHolder(View itemView) {
            super(itemView);
            user_picture = itemView.findViewById(R.id.commentPostedByImg);
            content = itemView.findViewById(R.id.commentContent);
            user_name = itemView.findViewById(R.id.commentPostedByName);
            rating = itemView.findViewById(R.id.commentRating);
            createed = itemView.findViewById(R.id.commentCreated);
            upvote = itemView.findViewById(R.id.commentUpVote);
            downvote = itemView.findViewById(R.id.commentDownVote);
            delete = itemView.findViewById(R.id.deleteCommentBtn);
        }

    }
}