package amalhichri.androidprojects.com.kotlinlearning.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lucasurbas.listitemview.ListItemView;

import java.util.HashMap;
import java.util.List;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.fragments.LearnFragment_currentUserCourses;
import amalhichri.androidprojects.com.kotlinlearning.utils.AllCourses;

public class CoursesListAdapter extends BaseExpandableListAdapter {

    private Context context;

    private List listTitles;
    private HashMap<String, List> listData;
    private int[] icons;


    public CoursesListAdapter(Context context, List listTitles, HashMap<String, List> listData, int[]icons) {
        this.context = context;
        this.listTitles = listTitles;
        this.listData= listData;
        this.icons=icons;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listData.get(this.listTitles.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);
        if (convertView == null)
            convertView = ((LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.courseslist_item_expanded, null);
        ((TextView) convertView.findViewById(R.id.listItem_text)).setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listData.get(this.listTitles.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listTitles.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listTitles.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String title = (String) getGroup(groupPosition);
        final int  courseIcon = icons[groupPosition];

        if (convertView == null)
            convertView = ((LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.courseslist_item, null);
        ((TextView) convertView.findViewById(R.id.listTitle_text)).setText(title);
        ((ImageView)convertView.findViewById(R.id.courseIcon)).setImageResource(courseIcon);
        /** if a courses list item is clicked
         * switch LearnFragment_noCourses with LearnFragment_currentUserCourse
         * and update the takenCoursesPrefs
         * **/

        ((ListItemView) convertView. findViewById(R.id.coursesListItem )).setOnMenuItemClickListener(new ListItemView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(final MenuItem item) {
                if(item.getItemId()== R.id.action_startcourse){

                    //if(!DataBaseHandler.getInstance(context).courseExist("dZb3TxK1x5dqQJkq7ve0d683VoA3",groupPosition)){
                        //DataBaseHandler.getInstance(context).addCourse("dZb3TxK1x5dqQJkq7ve0d683VoA3",groupPosition);
                        /** switch fragments to display courses list **/
                        AppCompatActivity activity=(AppCompatActivity) context;
                        LearnFragment_currentUserCourses currentUserCourses = new LearnFragment_currentUserCourses();
                        currentUserCourses.currentUserCourses.add(AllCourses.getCourse(groupPosition));
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.root_learFragment,currentUserCourses).addToBackStack(null).commit();
                   /* }else{
                        /** will change later on to snackbar or smthin gpresentable
                        Toast toast = Toast.makeText( context, "You already started this course.", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }*/
                }
                if(item.getItemId()== R.id.action_share){
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT,courseLinkFromCourseNb(groupPosition));
                    context.startActivity(Intent.createChooser(shareIntent, " share "));
                }
            }
        });
        return convertView;
    }


    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    private String courseLinkFromCourseNb(int courseNb){
        String courseLink = "https://kotlinlang.org/docs/reference/";
        switch (courseNb){
            case 0:
                courseLink+="server-overview.html";
                break;
            case 1:
                courseLink+="basic-syntax.html";
                break;
            case 2:
                courseLink+="basic-types.html";
                break;
            case 3:
                courseLink+="classes.html";
                break;
            case 4:
                courseLink+="functions.html";
                break;
            case 5:
                courseLink+="multi-declarations.html";
                break;
            case 6:
                courseLink+="java-interop.html";
                break;
            case 7:
                courseLink+="dynamic-type.html";
                break;
        }
        return courseLink;
    }
}