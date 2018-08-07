package amalhichri.androidprojects.com.kotlinlearning.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.fragments.ForumQuestionFragment;
import amalhichri.androidprojects.com.kotlinlearning.models.ForumQuestion;
import amalhichri.androidprojects.com.kotlinlearning.services.ForumsServices;
import amalhichri.androidprojects.com.kotlinlearning.services.ServerCallbacks;
import amalhichri.androidprojects.com.kotlinlearning.utils.Statics;
import de.hdodenhof.circleimageview.CircleImageView;
import me.originqiu.library.FlowLayout;

/**
 * Created by Amal on 04/12/2017.
 */

public class ShareListAdapter extends RecyclerView.Adapter<ShareListAdapter.ShareItem_ViewHolder> {

    private ArrayList<ForumQuestion> forumQuestionsList;
    private Context context;
    private String userid;

    public ShareListAdapter(ArrayList<ForumQuestion> forum_list, Context context){
        this.forumQuestionsList=forum_list;
        this.context=context;
        userid= FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public ShareItem_ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        ShareItem_ViewHolder shareItem_viewHolder = new ShareItem_ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.share_list_item,parent,false));
        return shareItem_viewHolder;
    }


    @Override
    public void onBindViewHolder(final ShareItem_ViewHolder holder, final int position) {

        /** post data **/
        holder.title.setText(forumQuestionsList.get(position).getSubject());
        holder.nbviews.setText(forumQuestionsList.get(position).getViews_string());
        holder.rating.setText(forumQuestionsList.get(position).getRatingString());
        holder.createed.setText(forumQuestionsList.get(position).getCreated_string());

        /** user data **/
        if(forumQuestionsList.get(position).getUserId().equals(userid))
            holder.user_name.setText("me");
        else
            holder.user_name.setText(forumQuestionsList.get(position).getUser_name_captalized());

        if(forumQuestionsList.get(position).getUserPictureUrl()!=null)
            Picasso.with(context).load(Uri.parse(forumQuestionsList.get(position).getUserPictureUrl())).into(holder.user_picture);
        else{
            String item=forumQuestionsList.get(position).getUserName();
            holder.user_picture.setImageDrawable(Statics.getPlaceholderProfilePic(item));
        }
        /** tags **/
        String[] array = forumQuestionsList.get(position).getTags().split(",");
        holder.tags_layout.removeAllViews();
        for(String s : array){
            TextView t = new TextView(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(5,5,5,5);
            t.setLayoutParams(lp);
            t.setText(s);
            t.setBackgroundResource(R.drawable.button_background2);
            t.setTextColor(context.getResources().getColor(R.color.cardview_light_background));
            t.setGravity(View.TEXT_ALIGNMENT_CENTER);
            t.setPadding(12,5,12,5);
            t.setTextSize(TypedValue.COMPLEX_UNIT_DIP ,11);
            holder.tags_layout.addView(t);
        }


        /** itemview click **/
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity=(AppCompatActivity) context;
                final ForumQuestionFragment forumQuestionFragment =new ForumQuestionFragment();
                forumQuestionFragment.setCurrentQuestion(forumQuestionsList.get(position));
                ForumsServices.getInstance().markSeenForum(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        context, forumQuestionsList.get(position).getId(), new ServerCallbacks() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                try {
                                    if(result.getInt("resp")>0){
                                        holder.nbviews.setText(String.valueOf(result.getInt("resp")));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(VolleyError result) {
                                  Toast.makeText(context,"Error ! Please check your internet connection.",Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onWrong(JSONObject result) {
                                Toast.makeText(context,"Error ! Please try again later !",Toast.LENGTH_LONG).show();
                            }
                        });
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.root_share_fragment, forumQuestionFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return forumQuestionsList.size();
    }

    public static class ShareItem_ViewHolder extends RecyclerView.ViewHolder  {
        CircleImageView user_picture;
        TextView title,user_name,nbviews,rating,createed;
        FlowLayout tags_layout;
        public ShareItem_ViewHolder(View itemView) {
            super(itemView);
            user_picture= itemView.findViewById(R.id.forum_postedBy_img);
            title= itemView.findViewById(R.id.postTile);
            user_name=itemView.findViewById(R.id.forum_postedBy_name);
            nbviews=itemView.findViewById(R.id.nbViews);
            rating=itemView.findViewById(R.id.nbVotes);
            createed=itemView.findViewById(R.id.forumQstnCreated);
            tags_layout=itemView.findViewById(R.id.tagsLayout);

        }

    }
}
