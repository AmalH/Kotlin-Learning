package amalhichri.androidprojects.com.kotlinlearning.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
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
import amalhichri.androidprojects.com.kotlinlearning.models.Answer;
import amalhichri.androidprojects.com.kotlinlearning.services.ForumServices;
import amalhichri.androidprojects.com.kotlinlearning.services.ServerCallbacks;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Amal on 21/12/2017.
 */

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentItem_ViewHolder> {

    ArrayList<Answer> forumAnswersList= new ArrayList<>();
    Context context;
    String userid;
    ProgressDialog progressDialog;

    public CommentsAdapter(ArrayList<Answer> forum_list, Context context){
        this.forumAnswersList=forum_list;
        this.context=context;
    }

    @Override
    public CommentsAdapter.CommentItem_ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.share_comment_item,parent,false);
        CommentsAdapter.CommentItem_ViewHolder shareItem_viewHolder = new CommentsAdapter.CommentItem_ViewHolder(view);
        userid ="ljDQhORWgjaxrZcHZbpLR1vyfaF2";
//        userid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        progressDialog=new ProgressDialog(context);
        return shareItem_viewHolder;
    }

    @Override
    public void onBindViewHolder(final CommentItem_ViewHolder holder, final int position) {

        holder.content.setText(forumAnswersList.get(position).getContent());
        holder.user_name.setText(forumAnswersList.get(position).getUser_name_captalized());
        holder.rating.setText(forumAnswersList.get(position).getRatingString());
        holder.createed.setText(forumAnswersList.get(position).getCreated_string());

        if(forumAnswersList.get(position).getUserpicture()!=null)
            Picasso.with(context).load(Uri.parse(forumAnswersList.get(position).getUserpicture())).into(holder.user_picture);
        else
        {
            String item=forumAnswersList.get(position).getUsername();
           // holder.user_picture.setImageDrawable(UserServices.getInstance().getEmptyProfimePicture(item));
        }

        //check to add delete button
        if(userid.equals(forumAnswersList.get(position).getUserid())){
            holder.delete.setVisibility(View.VISIBLE);
        }

        //view content
      /**  holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity=(AppCompatActivity) context;
                Toast.makeText(context,forumAnswersList.get(position).toString(),Toast.LENGTH_LONG).show();
            }
        });*/

        holder.upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //FirebaseAuth.getInstance().getCurrentUser().getUid()
                ForumServices.getInstance().upvoteComment("dZb3TxK1x5dqQJkq7ve0d683VoA3",
                        context, forumAnswersList.get(position).getId(), new ServerCallbacks() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                try {
                                    holder.rating.setText(
                                            (result.getInt("resp")>0?"+"+result.getInt("resp"):result.getInt("resp")+"")
                                    );
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(VolleyError result) {
                                Toast.makeText(context,"Server Problem", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onWrong(JSONObject result) {
                                try {
                                    holder.rating.setText(
                                            (result.getInt("resp")>0?"+"+result.getInt("resp"):result.getInt("resp")+"")
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

                //FirebaseAuth.getInstance().getCurrentUser().getUid()
                ForumServices.getInstance().downvoteComment("ljDQhORWgjaxrZcHZbpLR1vyfaF2",
                        context, forumAnswersList.get(position).getId(), new ServerCallbacks() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                try {
                                    holder.rating.setText(
                                            (result.getInt("resp")>0?"+"+result.getInt("resp"):result.getInt("resp")+"")
                                    );
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(VolleyError result) {
                                Toast.makeText(context,"Server Problem", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onWrong(JSONObject result) {
                                try {
                                    holder.rating.setText(
                                            (result.getInt("resp")>0?"+"+result.getInt("resp"):result.getInt("resp")+"")
                                    );
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            }
        });

       /* holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                new AlertDialog.Builder(context)
                        //set message, title, and icon
                        .setTitle("Delete")
                        .setMessage("Do you want to delete this comment")
                        .setIcon(R.drawable.ic_action_delete)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int whichButton) {
                                progressDialog.setMessage("Deleting");
                                progressDialog.show();

                                ForumServices.getInstance().delComment(userid, context, forumAnswersList.get(position).getId(), new ServerCallbacks() {
                                    @Override
                                    public void onSuccess(JSONObject result) {
                                        if(progressDialog.isShowing())
                                        progressDialog.dismiss();
                                        holder.itemView.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError(VolleyError result) {
                                        if(progressDialog.isShowing())
                                        progressDialog.dismiss();
                                        //Toast.makeText(context,result.toString(),Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onWrong(JSONObject result) {
                                        if(progressDialog.isShowing())
                                        progressDialog.dismiss();
                                        //Toast.makeText(context,result.toString(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return forumAnswersList.size();
    }

    public static class CommentItem_ViewHolder extends RecyclerView.ViewHolder  {
        CircleImageView user_picture;
        TextView content,user_name,rating,createed;
        ImageButton upvote,downvote,delete;

        public CommentItem_ViewHolder(View itemView) {
            super(itemView);
            user_picture= itemView.findViewById(R.id.comment_postedBy_img);
            content= itemView.findViewById(R.id.commentContent);
            user_name=itemView.findViewById(R.id.comment_postedBy_name);
            rating=itemView.findViewById(R.id.comment_rating);
            createed=itemView.findViewById(R.id.comment_created);
            upvote=itemView.findViewById(R.id.comment_up_arrow);
            downvote=itemView.findViewById(R.id.comment_down_arrow);
           // delete=itemView.findViewById(R.id.deleComment);
        }

    }
}