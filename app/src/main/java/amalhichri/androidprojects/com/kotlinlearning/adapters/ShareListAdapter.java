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

import com.android.volley.VolleyError;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.fragments.ForumShowFragment;
import amalhichri.androidprojects.com.kotlinlearning.models.ForumQuestion;
import amalhichri.androidprojects.com.kotlinlearning.services.ForumServices;
import amalhichri.androidprojects.com.kotlinlearning.services.ServerCallbacks;
import amalhichri.androidprojects.com.kotlinlearning.services.UserServices;
import de.hdodenhof.circleimageview.CircleImageView;
import me.originqiu.library.FlowLayout;

/**
 * Created by Amal on 04/12/2017.
 */

public class ShareListAdapter extends RecyclerView.Adapter<ShareListAdapter.ShareItem_ViewHolder> {

    ArrayList<ForumQuestion> forumQuestionsList;
    Context context;
    String userid;

    public ShareListAdapter(ArrayList<ForumQuestion> forum_list, Context context){
        this.forumQuestionsList=forum_list;
        this.context=context;
        userid= FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public ShareItem_ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.share_list_item,parent,false);
        ShareItem_ViewHolder shareItem_viewHolder = new ShareItem_ViewHolder(view);
        return shareItem_viewHolder;
    }


    @Override
    public void onBindViewHolder(final ShareItem_ViewHolder holder, final int position) {
        holder.title.setText(forumQuestionsList.get(position).getSubject());
        holder.nbviews.setText(forumQuestionsList.get(position).getViews_string());
        holder.rating.setText(forumQuestionsList.get(position).getRatingString());
        holder.createed.setText(forumQuestionsList.get(position).getCreated_string());

        if(forumQuestionsList.get(position).getId_User().equals(userid))
            holder.user_name.setText("me");
        else
            holder.user_name.setText(forumQuestionsList.get(position).getUser_name_captalized());

        if(forumQuestionsList.get(position).getUser_picture_url()!=null)
            Picasso.with(context).load(Uri.parse(forumQuestionsList.get(position).getUser_picture_url())).into(holder.user_picture);
        else{
            String item=forumQuestionsList.get(position).getUser_name();
            holder.user_picture.setImageDrawable(UserServices.getInstance().getEmptyProfimePicture(item));
        }
        //split tags
        String[] array = forumQuestionsList.get(position).getTags().split(",");

        //init tags layout
        holder.tags_layout.removeAllViews();
        //fill tags
        for(String s : array){
            TextView t = new TextView(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(5,5,5,5);
            t.setLayoutParams(lp);
            t.setText(s);
            t.setBackgroundColor(context.getResources().getColor(R.color.material_deep_teal_50));
            t.setTextColor(context.getResources().getColor(R.color.cardview_light_background));
            t.setGravity(View.TEXT_ALIGNMENT_CENTER);
            t.setPaddingRelative(5,3,5,3);
            t.setTextSize(TypedValue.COMPLEX_UNIT_DIP ,13);
            holder.tags_layout.addView(t);
        }
        //view content
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity=(AppCompatActivity) context;

                final ForumShowFragment fs =new ForumShowFragment();
                fs.setF(forumQuestionsList.get(position));
                ForumServices.getInstance().markViewForum(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        context, forumQuestionsList.get(position).getId(), new ServerCallbacks() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                // Toast.makeText(context,"marked ",Toast.LENGTH_LONG).show();
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
                                //  Toast.makeText(context,"not marked",Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onWrong(JSONObject result) {
                                //  Toast.makeText(context,"not marked",Toast.LENGTH_LONG).show();
                            }
                        });


                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.root_share_fragment,fs)
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
            createed=itemView.findViewById(R.id.forum_created);
            tags_layout=itemView.findViewById(R.id.tags_layout);

        }

    }
}
