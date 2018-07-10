package amalhichri.androidprojects.com.kotlinlearning.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;

import java.util.ArrayList;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.models.Course;


/**
 * Created by Amal on 29/11/2017.
 */

public class CurrentUserCoursesList_Adapter extends BaseAdapter {

    /** adapter for the the list to be shown if user is enrolled to courses **/

    private Context context;
    private ArrayList<Course> currentUserCourses;

    public CurrentUserCoursesList_Adapter(Context context, ArrayList<Course> currentUserCourses){
        this.context=context;
        this.currentUserCourses=currentUserCourses;
    }


    @Override
    public int getCount() {
        return currentUserCourses.size();
    }

    @Override
    public Object getItem(int position) {
        return currentUserCourses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    /*** if reset list is clicked : remove listItem
     * + update Statics.loggedUser_startedLearning in case all items are removed !
     * + perform course rest in DB
     * ***/
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View rowView= ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.currentusercourses_list_item,parent, false);
        ((ImageView)rowView.findViewById(R.id.userCourseIcon)).setImageResource(currentUserCourses.get(position).getIconId());
        ((TextView)rowView.findViewById(R.id.userCourseTitle)).setText(currentUserCourses.get(position).getTitle());
        ((NumberProgressBar)rowView.findViewById(R.id.userCourseProgress)).setProgress(currentUserCourses.get(position).getAdvancement());
        return rowView;
    }
}
