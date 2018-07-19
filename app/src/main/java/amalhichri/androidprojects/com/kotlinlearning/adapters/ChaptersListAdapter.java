package amalhichri.androidprojects.com.kotlinlearning.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.utils.AllCourses;


/**
 * Created by Amal on 25/11/2017.
 */

public class ChaptersListAdapter extends BaseAdapter {

    private Context context;
    private int coursePosition;


    public ChaptersListAdapter(Context context, int coursePosition){
        this.context=context;
        this.coursePosition=coursePosition;
    }
    @Override
    public int getCount() {
        return AllCourses.getCourse(coursePosition).getChaptersList().size();
    }

    @Override
    public Object getItem(int position) {
        return AllCourses.getCourse(coursePosition).getChaptersList().get(coursePosition);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView= ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.chapterslist_item, parent, false);
        ((TextView) rowView.findViewById(R.id.chapterTitle)).setText("Chapter "+ String.valueOf(position+1)+" _ "+  AllCourses.getCourse(coursePosition).getChaptersList().get(position).getTitle());
        ((TextView) rowView.findViewById(R.id.chapterDescription)).setText( AllCourses.getCourse(coursePosition).getChaptersList().get(position).getDescription());
        ((TextView) rowView.findViewById(R.id.nbbadgesEarned_chapter)).setText(String.valueOf(AllCourses.getCourse(coursePosition).getChaptersList().get(position).getNbBadges()));
        ((TextView) rowView.findViewById(R.id.timeNeeded_chapter)).setText(String.valueOf(AllCourses.getCourse(coursePosition).getChaptersList().get(position).getTimeTocomplete()));
        return rowView;
    }
}
