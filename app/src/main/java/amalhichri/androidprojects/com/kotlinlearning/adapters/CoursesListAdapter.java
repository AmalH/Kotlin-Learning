package amalhichri.androidprojects.com.kotlinlearning.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.lucasurbas.listitemview.ListItemView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.fragments.LearnFragment_currentUserCourses;
import amalhichri.androidprojects.com.kotlinlearning.utils.AllCourses;
import amalhichri.androidprojects.com.kotlinlearning.utils.DataBaseHandler;

public class CoursesListAdapter extends BaseExpandableListAdapter {

    private Context context;

    // could've user an ArrayList<Course> but this list's data isnt dynamic it wont change !
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

        final StringBuilder str = new StringBuilder();
        final ArrayList<String>  takenCourses = new ArrayList<>();
        ((ListItemView) convertView. findViewById(R.id.coursesListItem )).setOnMenuItemClickListener(new ListItemView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(final MenuItem item) {
                if(item.getItemId()== R.id.action_startcourse){

                    if(!DataBaseHandler.getInstance(context).courseExist(FirebaseAuth.getInstance().getCurrentUser().getUid(),groupPosition)){
                        DataBaseHandler.getInstance(context).addCourse(FirebaseAuth.getInstance().getCurrentUser().getUid(),groupPosition);
                        /** switch fragments to display courses list **/
                        AppCompatActivity activity=(AppCompatActivity) context;
                        LearnFragment_currentUserCourses currentUserCourses = new LearnFragment_currentUserCourses();
                        currentUserCourses.currentUserCourses.add(AllCourses.getCourse(groupPosition));
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.root_learFragment,currentUserCourses).addToBackStack(null).commit();
                    }else{
                        //DataBaseHandler.getInstance(context).deleteCourse(FirebaseAuth.getInstance().getCurrentUser().getUid(),groupPosition);
                        /** will change later on to snackbar or smthin gpresentable **/
                        Toast toast = Toast.makeText( context, "You already started this course.", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }

                    /** search for course in coursesTable **/
                    /*Statics.takenCoursesTable.orderByChild("userId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                           .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            //Log.d("COUNT++","bbb "+snapshot.getChildren().toString());
                           //Log.d("COUNT",String.valueOf(snapshot.getChildrenCount()));

                            // if user has courses already
                            if(snapshot.getChildrenCount()>0){
                                for (DataSnapshot course: snapshot.getChildren()) {
                                    Log.d("FCourse", course.toString());
                                    Log.d("FCourse nb", course.child("courseNb").getValue().toString());
                                    takenCourses.add(course.child("courseNb").getValue().toString());
                                }
                                Toast toast = Toast.makeText(context, "Taken Courses are: "+takenCourses.size(), Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                if(!takenCourses.contains(String.valueOf(groupPosition))){
                                    String courseId = Statics.takenCoursesTable.push().getKey();
                                    Statics.takenCoursesTable.child(courseId).child("userId").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    Statics.takenCoursesTable.child(courseId).child("courseNb").setValue(String.valueOf(groupPosition));
                                    LearnFragment_noCourses.switchFragments(groupPosition,context);
                                }
                            }
                            else {
                                String courseId = Statics.takenCoursesTable.push().getKey();
                                Statics.takenCoursesTable.child(courseId).child("userId").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                Statics.takenCoursesTable.child(courseId).child("courseNb").setValue(String.valueOf(groupPosition));
                                LearnFragment_noCourses.switchFragments(groupPosition,context);
                            }

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("FOUND 1", "getUser:onCancelled", databaseError.toException());
                        }
                    });*/
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