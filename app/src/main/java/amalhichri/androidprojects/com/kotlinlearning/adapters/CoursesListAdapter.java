package amalhichri.androidprojects.com.kotlinlearning.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lucasurbas.listitemview.ListItemView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.fragments.LearnFragment_noCourses;

public class CoursesListAdapter extends BaseExpandableListAdapter {

    private Context context;

    // could've used an ArrayList<Course> but this list's data isnt dynamic it wont change !
    private List listTitles;
    private HashMap<String, List> listData;
    private int[] icons;
    final ArrayList<Integer> takenCoursesNbs=new ArrayList<>();



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
    public boolean hasStableIds() {
        return false;
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
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
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
        final int courseIcon = icons[groupPosition];

        if (convertView == null)
            convertView = ((LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.courseslist_item, null);
        ((TextView) convertView.findViewById(R.id.listTitle_text)).setText(title);
        ((ImageView) convertView.findViewById(R.id.courseIcon)).setImageResource(courseIcon);
        /** if a courses list item is clicked
         * switch LearnFragment_noCourses with LearnFragment_currentUserCourse
         * **/
        ((ListItemView) convertView.findViewById(R.id.coursesListItem)).setOnMenuItemClickListener(new ListItemView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(final MenuItem item) {
                if (item.getItemId() == R.id.action_startcourse) {
                    /** store this course in sharedPref to display ot later on **/
                    SharedPreferences coursesPrefs = context.getSharedPreferences("takenCoursesPrefs", 0);
                    SharedPreferences.Editor coursesPrefsEditor = coursesPrefs.edit();
                    StringBuilder str = new StringBuilder();
                    if (!takenCoursesNbs.contains(groupPosition)) {
                        takenCoursesNbs.add(groupPosition);
                        for (int i = 0; i < takenCoursesNbs.size(); i++) {
                            str.append(takenCoursesNbs.get(i)).append(",");
                        }
                        coursesPrefsEditor.putString("takenCourses", str.toString());
                        coursesPrefsEditor.commit();
                        /** switch fragments to display courses list **/
                        LearnFragment_noCourses.switchFragments(groupPosition, context);
                    } else {
                        /** will change later on to snackbar or smthin gpresentable **/
                        Toast toast = Toast.makeText(context, "You are already took this course. You are at 40% advancement !", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                       /*LinearLayout linearLayout = (((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_learn_currentusercourses, null)).findViewById(R.id.currentCoursesContainer);
                        Snackbar snackbar = Snackbar.make(linearLayout, "You are already at 40% advancement in this course!", Snackbar.LENGTH_LONG);
                        snackbar.show();**/
                    }
                }
                if (item.getItemId() == R.id.action_share) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, courseLinkFromCourseNb(groupPosition));
                    context.startActivity(Intent.createChooser(shareIntent, " share "));
                }

            }
        });
        return convertView;
    }

    /** helper methods **/
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