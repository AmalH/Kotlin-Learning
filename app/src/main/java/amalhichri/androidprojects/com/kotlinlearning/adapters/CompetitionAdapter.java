package amalhichri.androidprojects.com.kotlinlearning.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.fragments.CompetitionFragment;
import amalhichri.androidprojects.com.kotlinlearning.models.Competition;
import amalhichri.androidprojects.com.kotlinlearning.utils.Statics;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Amal on 11/01/2018.
 */

public class CompetitionAdapter extends RecyclerView.Adapter<CompetitionAdapter.CompetitionItem_ViewHolder> {

    private ArrayList<Competition> CompetitionsList;
    private Context context;

    public CompetitionAdapter(ArrayList<Competition> c_list, Context context){
        this.CompetitionsList=c_list;
        this.context=context;
    }

    @Override
    public CompetitionAdapter.CompetitionItem_ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        CompetitionAdapter.CompetitionItem_ViewHolder shareItem_viewHolder =
                new CompetitionAdapter.CompetitionItem_ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.competition_item,parent,false));

        return shareItem_viewHolder;
    }

    @Override
    public void onBindViewHolder(final CompetitionAdapter.CompetitionItem_ViewHolder holder, final int position) {
        holder.title.setText(CompetitionsList.get(position).getTitle());
        holder.solved.setText(CompetitionsList.get(position).getSolvedString());
        holder.createed.setText(CompetitionsList.get(position).getCreated_string());

        if(CompetitionsList.get(position).getUserId().equals(Statics.auth.getCurrentUser().getUid()))
            holder.user_name.setText("me");
        else
            holder.user_name.setText(CompetitionsList.get(position).getUser_name_captalized());

        if(CompetitionsList.get(position).getProfilePicture()!=null)
            Picasso.with(context).load(Uri.parse(CompetitionsList.get(position).getProfilePicture())).into(holder.user_picture);
        else{
            String item=CompetitionsList.get(position).getUsername();
            holder.user_picture.setImageDrawable(Statics.getPlaceholderProfilePic(item));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity=(AppCompatActivity) context;

                CompetitionFragment competitionFragment = new CompetitionFragment();

                competitionFragment.setCompetition(CompetitionsList.get(position));

                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.root_compete, competitionFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return CompetitionsList.size();
    }

    public static class CompetitionItem_ViewHolder extends RecyclerView.ViewHolder  {
        CircleImageView user_picture;
        TextView title,user_name,solved,createed;

        public CompetitionItem_ViewHolder(View itemView) {
            super(itemView);
            user_picture= itemView.findViewById(R.id.competePostedByImg);
            title= itemView.findViewById(R.id.competePostTitle);
            user_name=itemView.findViewById(R.id.competePostedByName);
            solved=itemView.findViewById(R.id.competeNbSolve);
            createed=itemView.findViewById(R.id.competeCreated);
        }

    }
}
