package amalhichri.androidprojects.com.kotlinlearning.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.utils.AllCourses;
import amalhichri.androidprojects.com.kotlinlearning.utils.Statics;


/**
 * Created by Amal on 26/11/2017.
 */

public class CourseHeaderAdapter extends BaseAdapter {


    /** this adapter is to load data dynamiacally in the linearLayout @+id/courseHeaderLayout from fragment_learn_course.xml **/

    private Context context;
    private int coursePosition;
    private final ArrayList<String> courseChapters = new ArrayList<>();


    public CourseHeaderAdapter(Context context, int coursePosition){
        this.context=context;
        this.coursePosition=coursePosition;
    }

    @Override
    public int getCount() {
       return AllCourses.getCourse(coursePosition).getChaptersList().size();
    }

    @Override
    public Object getItem(int position) {
        return AllCourses.getCourse(coursePosition).getChaptersList().get(coursePosition+1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView= ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.course_header_view, parent, false);
        ((TextView) rowView.findViewById(R.id.courseTitle)).setText("Course "+ String.valueOf(coursePosition+1)+": "+  AllCourses.getCourse(coursePosition).getTitle());
        ((TextView) rowView.findViewById(R.id.courseDescription)).setText( AllCourses.getCourse(coursePosition).getDescription());
        ((TextView) rowView.findViewById(R.id.nbChaptersFinished)).setText(String.valueOf(getNbChaptersFinished()));
        ((TextView) rowView.findViewById(R.id.nbbadgesEarned_course)).setText(String.valueOf(getNbBadgesEarned()));
        ((TextView) rowView.findViewById(R.id.timeNeeded_course)).setText(String.valueOf(AllCourses.getCourse(coursePosition).getTimeToFinish()));
        ((com.daimajia.numberprogressbar.NumberProgressBar) rowView.findViewById(R.id.courseProgress)).setProgress((AllCourses.getCourse(coursePosition).getAdvancement()));
        ((ImageView) rowView.findViewById(R.id.courseIcon)).setImageResource(AllCourses.getCourse(coursePosition).getIconId());
        return rowView;
    }

    private int getNbChaptersFinished(){
        final int[] nbChpts = {0};
        // if chapter has already been started
        Statics.startedChaptersTable.orderByChild("userId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                //.orderByChild("courseNb").equalTo(String.valueOf(coursePosition))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        // Log.d("COUNT++","bbb "+snapshot.getChildren().toString());
                        // Log.d("COUNT",String.valueOf(snapshot.getChildrenCount()));
                        if(snapshot.getChildrenCount()>0){
                            for (DataSnapshot chapter: snapshot.getChildren()) {
                                Log.d("FChapter nb", chapter.child("chapterNb").getValue().toString());
                                Log.d("FChapter nb", chapter.child("courseNb").getValue().toString());
                                courseChapters.add(chapter.child("chapterNb").getValue().toString());
                            }
                            nbChpts[0] = courseChapters.size();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("FOUND 1", "getUser:onCancelled", databaseError.toException());
                    }
                });
        return nbChpts[0];
    }

    private int getNbBadgesEarned(){
        return 1;
    }
}