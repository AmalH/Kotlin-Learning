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
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.lucasurbas.listitemview.ListItemView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.fragments.LearnFragment_currentUserCourses;
import amalhichri.androidprojects.com.kotlinlearning.services.CoursesServices;
import amalhichri.androidprojects.com.kotlinlearning.services.ServerCallbacks;
import amalhichri.androidprojects.com.kotlinlearning.utils.AllCourses;
import amalhichri.androidprojects.com.kotlinlearning.utils.Statics;

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
        ((TextView) convertView.findViewById(R.id.listItemTxt)).setText(childText);
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
        ((TextView) convertView.findViewById(R.id.listTitle)).setText(title);
        ((ImageView)convertView.findViewById(R.id.courseIcon)).setImageResource(courseIcon);

        ((ListItemView) convertView. findViewById(R.id.coursesListItem )).setOnMenuItemClickListener(new ListItemView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(final MenuItem item) {
                if(item.getItemId()== R.id.action_startcourse){

                    CoursesServices.getInstance().hasStartedAcourse(Statics.auth.getCurrentUser().getUid(), String.valueOf(groupPosition), context, new ServerCallbacks() {
                                @Override
                                public void onSuccess(JSONObject result) {
                                    try {
                                        if(result.getJSONArray("courses").length()==0){
                                            CoursesServices.getInstance().addCourseToUser(Statics.auth.getCurrentUser().getUid(), String.valueOf(groupPosition), context, new ServerCallbacks() {
                                                @Override
                                                public void onSuccess(JSONObject result) {
                                                    // update ui
                                                    LearnFragment_currentUserCourses currentUserCourses = new LearnFragment_currentUserCourses();
                                                    currentUserCourses.currentUserCourses.add(AllCourses.getCourse(groupPosition));
                                                    ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.root_learFragment,currentUserCourses).addToBackStack(null).commit();
                                                }

                                                @Override
                                                public void onError(VolleyError result) {
                                                    Toast.makeText(context,"Error "+result.getClass().getName(),Toast.LENGTH_SHORT).show();
                                                }

                                                @Override
                                                public void onWrong(JSONObject result) {
                                                    Toast.makeText(context,"Error "+result.toString(),Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }
                                        else
                                            Toast.makeText( context, "You already started this course.", Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        Toast.makeText(context,"Server error "+e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onError(VolleyError result) {
                                    Toast.makeText(context,"Error "+result.getMessage(),Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onWrong(JSONObject result) {
                                    Toast.makeText(context,"Error "+result.toString(),Toast.LENGTH_SHORT).show();
                                }
                            });
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