package amalhichri.androidprojects.com.kotlinlearning.adapters;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.fragments.CompetitionFragment;
import amalhichri.androidprojects.com.kotlinlearning.models.Competition;
import amalhichri.androidprojects.com.kotlinlearning.models.CompetitionAnswer;

/**
 * Created by Amal on 12/01/2018.
 */

public class CompetitionAnswerAdapter extends RecyclerView.Adapter<CompetitionAnswerAdapter.CompetitionAnswerItem_ViewHolder> {
    ArrayList<CompetitionAnswer> CompetitionsList;
    Context context;
    String userid;

    public CompetitionAnswerAdapter(ArrayList<CompetitionAnswer> c_list, Context context) {
        this.CompetitionsList = c_list;
        this.context = context;
        userid = "dZb3TxK1x5dqQJkq7ve0d683VoA3";
                //FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public CompetitionAnswerAdapter.CompetitionAnswerItem_ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.competition_answer_item, parent, false);
        CompetitionAnswerAdapter.CompetitionAnswerItem_ViewHolder shareItem_viewHolder = new CompetitionAnswerAdapter.CompetitionAnswerItem_ViewHolder(view);

        return shareItem_viewHolder;
    }

    @Override
    public void onBindViewHolder(final CompetitionAnswerAdapter.CompetitionAnswerItem_ViewHolder holder, final int position) {
        holder.title.setText(CompetitionsList.get(position).getCompetition_title());
        holder.createed.setText(CompetitionsList.get(position).getCreated_string());
        holder.level.setText(CompetitionsList.get(position).getCompetiton_level() + "");


        //view content
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) context;

                Competition c = new Competition();
                c.setId(CompetitionsList.get(position).getId_competition());
                CompetitionFragment competeShow = new CompetitionFragment();
                competeShow.setCompetition(c);

                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.root_compete, competeShow)
                        .addToBackStack(null)
                        .commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return CompetitionsList.size();
    }

    public static class CompetitionAnswerItem_ViewHolder extends RecyclerView.ViewHolder {
        TextView title, level, createed;

        public CompetitionAnswerItem_ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.competeanswer_postTile);
            level = itemView.findViewById(R.id.competeanswer_level);
            createed = itemView.findViewById(R.id.competeanswer_created);
        }

    }
}
