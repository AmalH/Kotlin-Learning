package amalhichri.androidprojects.com.kotlinlearning.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import amalhichri.androidprojects.com.kotlinlearning.R;

/**
 * Created by Amal on 12/12/2017.
 */

public class BadgesAdapter extends ArrayAdapter {

    private final Context context;
    private final String[] badgesNames;
    private final String[] badgesDescriptions;
    private final ArrayList<Integer> badgesIcons;

    public BadgesAdapter(@NonNull Context context, String[] badgesDescriptions, String[]badgesNames, @NonNull ArrayList<Integer> badgesIcons) {
        super(context, -1, badgesDescriptions);
        this.context = context;
        this.badgesDescriptions = badgesDescriptions;
        this.badgesNames=badgesNames;
        this.badgesIcons = badgesIcons;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View rowView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.badgeslist_row, parent, false);
        ((ImageView)rowView.findViewById(R.id.badgeIcon)).setImageResource(badgesIcons.get(position));
        ((TextView)rowView.findViewById(R.id.badgeDescription)).setText(badgesDescriptions[position]);
        ((TextView)rowView.findViewById(R.id.badgeName)).setText(badgesNames[position]);
        return rowView;
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }
}
