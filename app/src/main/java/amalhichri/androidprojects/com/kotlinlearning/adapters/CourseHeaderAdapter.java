package amalhichri.androidprojects.com.kotlinlearning.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.utils.AllCourses;


/**
 * Created by Amal on 26/11/2017.
 */

public class CourseHeaderAdapter extends BaseAdapter {


    /** this adapter is to load data dynamiacally in the linearLayout @+id/courseHeaderLayout from fragment_learn_course.xml **/

    private Context context;
    private int coursePosition;
    private int nbOfChaptersFinished;

    public CourseHeaderAdapter(final Context context, final int coursePosition,int nbOfChaptersFinished,int nbOfBadgesEarned){
        this.context=context;
        this.coursePosition=coursePosition;
        this.nbOfChaptersFinished = nbOfChaptersFinished;
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
        final View rowView= ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.course_header_view, parent, false);

        ((TextView) rowView.findViewById(R.id.courseTitle)).setText("Course "+ String.valueOf(coursePosition+1)+": "+  AllCourses.getCourse(coursePosition).getTitle());
        ((TextView) rowView.findViewById(R.id.courseDescription)).setText( AllCourses.getCourse(coursePosition).getDescription());
        ((TextView) rowView.findViewById(R.id.nbbadgesEarnedCourse)).setText(String.valueOf(nbOfChaptersFinished));
        ((TextView) rowView.findViewById(R.id.nbChaptersFinished)).setText(String.valueOf(AllCourses.getCourse(coursePosition).getCompletedChaptersNb()));
        ((TextView) rowView.findViewById(R.id.timeNeededCourse)).setText(String.valueOf(AllCourses.getCourse(coursePosition).getTimeToFinish()));
        ((com.daimajia.numberprogressbar.NumberProgressBar) rowView.findViewById(R.id.courseProgress)).setProgress((AllCourses.getCourse(coursePosition).getAdvancement()));
        ((ImageView) rowView.findViewById(R.id.courseIcon)).setImageResource(AllCourses.getCourse(coursePosition).getIconId());
        return rowView;
    }
}