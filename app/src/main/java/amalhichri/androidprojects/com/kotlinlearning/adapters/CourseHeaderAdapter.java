package amalhichri.androidprojects.com.kotlinlearning.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.services.CoursesServices;
import amalhichri.androidprojects.com.kotlinlearning.services.ServerCallbacks;
import amalhichri.androidprojects.com.kotlinlearning.utils.AllCourses;
import amalhichri.androidprojects.com.kotlinlearning.utils.Statics;


/**
 * Created by Amal on 26/11/2017.
 */

public class CourseHeaderAdapter extends BaseAdapter {


    /** this adapter is to load data dynamiacally in the linearLayout @+id/courseHeaderLayout from fragment_learn_course.xml **/

    private Context context;
    private int coursePosition;

    public CourseHeaderAdapter(final Context context, final int coursePosition){
        this.context=context;
        this.coursePosition=coursePosition;
        CoursesServices.getInstance().getAllUserCourses(Statics.auth.getCurrentUser().getUid(), context, new ServerCallbacks() {
            @Override
            public void onSuccess(JSONObject result) {

                try {
                    if (!(result.getJSONArray("courses").length() == 0))
                        for (int i = 0; i < result.getJSONArray("courses").length(); i++) {
                            /** set chapters nb + badge nb **/
                            if (Integer.parseInt(((JSONObject) result.getJSONArray("courses").get(i)).getString("finishedchapter")) == 100) {
                            }else{
                                /** updates course */
                                AllCourses.getCourse(Integer.parseInt(((JSONObject)result.getJSONArray("courses").get(i)).getString("courseindic")))
                                        .setCompletedChaptersNb(Integer.parseInt(((JSONObject)result.getJSONArray("courses").get(i)).getString("finishedchapter")));
                                Toast.makeText(context, "All | ---------" + AllCourses.getCourse(coursePosition).toString(), Toast.LENGTH_SHORT).show();


                                /** update ui */
                               // ((TextView) rowView.findViewById(R.id.nbChaptersFinished)).setText(AllCourses.getCourse(Integer.parseInt(((JSONObject)result.getJSONArray("courses").get(i)).getString("finishedchapter"))).getCompletedChaptersNb());
                               // ((TextView) rowView.findViewById(R.id.nbbadgesEarned_course)).setText(AllCourses.getCourse(Integer.parseInt(((JSONObject)result.getJSONArray("courses").get(i)).getString("earnedbadge"))).getCompletedChaptersNb());

                                /** set badges nb **/
                            }
                        }
                }catch (JSONException e) {
                    Toast.makeText(context,"Server error "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError result) {

            }

            @Override
            public void onWrong(JSONObject result) {

            }
        });

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
        ((TextView) rowView.findViewById(R.id.timeNeeded_course)).setText(String.valueOf(AllCourses.getCourse(coursePosition).getTimeToFinish()));
        ((com.daimajia.numberprogressbar.NumberProgressBar) rowView.findViewById(R.id.courseProgress)).setProgress((AllCourses.getCourse(coursePosition).getAdvancement()));
        ((ImageView) rowView.findViewById(R.id.courseIcon)).setImageResource(AllCourses.getCourse(coursePosition).getIconId());
        return rowView;
    }
}