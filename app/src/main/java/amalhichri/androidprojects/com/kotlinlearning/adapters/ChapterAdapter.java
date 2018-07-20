package amalhichri.androidprojects.com.kotlinlearning.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.utils.AllCourses;

/**
 * Created by Amal on 26/11/2017.
 */

public class ChapterAdapter extends BaseAdapter {


    /** this adapter is to load data dynamiacally in the linearLayout @+id/courseHeaderLayout from fragment_learn_course.xml **/

    private Context context;
    private int coursePosition,chapterPosition;


    public ChapterAdapter(Context context, int coursePosition, int chapterPosition){
        this.context=context;
        this.coursePosition=coursePosition;
        this.chapterPosition=chapterPosition;
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

    @SuppressLint("NewApi")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final View rowView= ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.chapteradapter_item, parent, false);

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        final String chapterTitle = AllCourses.getCourse(coursePosition).getChaptersList().get(chapterPosition).getTitle();
        ((TextView) rowView.findViewById(R.id.oneChapter_title)).setText(chapterTitle);
        ((WebView)(rowView.findViewById(R.id.chapterContentWebView))).loadUrl(getChapterContentFilePath());

        /** when user starts reading chapter **/
        (( rowView.findViewById(R.id.chapterContentScrollView))).getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener(){
            @Override public void onScrollChanged() {
                if ((((ScrollView)(rowView.findViewById(R.id.chapterContentScrollView))).getChildAt(0).getBottom() >= (rowView.findViewById(R.id.chapterContentScrollView).getHeight() + rowView.findViewById(R.id.chapterContentScrollView).getScrollY()))) {
                    // icon's filter + change text
                    ( rowView.findViewById(R.id.chapterStartedIcon)).getBackground().clearColorFilter();
                    ((TextView) rowView.findViewById(R.id.chapterStartedText)).setText("finished");
                    ((TextView) rowView.findViewById(R.id.chapterStartedText)).setTextColor( Color.parseColor("#e99631"));
                    ((TextView) rowView.findViewById(R.id.chapterStartedText)).setText("started");
                }
            }
        });

        /** if user clicks 'finished reading' **/
        (rowView.findViewById(R.id.chapterDoneIcon)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // test
                Toast.makeText(context,"CLICKED ",Toast.LENGTH_SHORT);
                Log.d("MADE IT-----","---CLICKED");
                /** add badge to database
                UserServices.getInstance().assignBadge(Statics.auth.getCurrentUser().getUid(), String.valueOf(position), context, new ServerCallbacks() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        Log.d("------","--+ "+result.toString());
                        Toast.makeText(context,"SUCCESS "+result.toString(),Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onError(VolleyError result) {
                        Log.d("------","--+ "+result.getClass().getName());
                        Toast.makeText(context,"FAILURE 1 "+result.getClass().getName(),Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onWrong(JSONObject result) {
                        Log.d("------","--+ "+result.toString());
                        Toast.makeText(context,"FAILURE 2 "+result.toString(),Toast.LENGTH_SHORT);
                    }
                });
                **/
                /** icon filter
                ( rowView.findViewById(R.id.chapterDoneIcon)).getBackground().clearColorFilter();
                ((TextView) rowView.findViewById(R.id.chapterDoneTxt)).setText("finished");
                ((TextView) rowView.findViewById(R.id.chapterDoneTxt)).setTextColor( Color.parseColor("#e99631"));
                ( rowView.findViewById(R.id.chapterDoneIcon)).setEnabled(false);**/
            }
        });
        return rowView;
    }


    private String getChapterContentFilePath(){
        String chapterContentFilePath =null;
        /** course 1 **/
        if(coursePosition==0&&chapterPosition==0)
            chapterContentFilePath="file:///android_asset/Course1Content/C1_Chapter1Content.html";
        if(coursePosition==0&&chapterPosition==1)
            chapterContentFilePath="file:///android_asset/Course1Content/C1_Chapter2Content.html";
        if(coursePosition==0&&chapterPosition==2)
            chapterContentFilePath="file:///android_asset/Course1Content/C1_Chapter3Content.html";
        if(coursePosition==0&&chapterPosition==3)
            chapterContentFilePath="file:///android_asset/Course1Content/C1_Chapter4Content.html";
        /** course 2 **/
        if(coursePosition==1&&chapterPosition==0)
            chapterContentFilePath="file:///android_asset/Course2Content/C2_Chapter1Content.html";
        if(coursePosition==1&&chapterPosition==1)
            chapterContentFilePath="file:///android_asset/Course2Content/C2_Chapter2Content.html";
        if(coursePosition==1&&chapterPosition==2)
            chapterContentFilePath="file:///android_asset/Course2Content/C2_Chapter3Content.html";
        /** course 3 **/
        if(coursePosition==2&&chapterPosition==0)
            chapterContentFilePath="file:///android_asset/Course3Content/C3_Chapter1Content.html";
        if(coursePosition==2&&chapterPosition==1)
            chapterContentFilePath="file:///android_asset/Course3Content/C3_Chapter2Content.html";
        if(coursePosition==2&&chapterPosition==2)
            chapterContentFilePath="file:///android_asset/Course3Content/C3_Chapter3Content.html";
        if(coursePosition==2&&chapterPosition==3)
            chapterContentFilePath="file:///android_asset/Course3Content/C3_Chapter4Content.html";
        /** course 4 **/
        if(coursePosition==3&&chapterPosition==0)
            chapterContentFilePath="file:///android_asset/Course4Content/C4_Chapter1Content.html";
        if(coursePosition==3&&chapterPosition==1)
            chapterContentFilePath="file:///android_asset/Course4Content/C4_Chapter2Content.html";
        if(coursePosition==3&&chapterPosition==2)
            chapterContentFilePath="file:///android_asset/Course4Content/C4_Chapter3Content.html";
        if(coursePosition==3&&chapterPosition==3)
            chapterContentFilePath="file:///android_asset/Course4Content/C4_Chapter4Content.html";
        if(coursePosition==3&&chapterPosition==4)
            chapterContentFilePath="file:///android_asset/Course4Content/C4_Chapter5Content.html";
        if(coursePosition==3&&chapterPosition==5)
            chapterContentFilePath="file:///android_asset/Course4Content/C4_Chapter6Content.html";
        if(coursePosition==3&&chapterPosition==6)
            chapterContentFilePath="file:///android_asset/Course4Content/C4_Chapter7Content.html";
        if(coursePosition==3&&chapterPosition==7)
            chapterContentFilePath="file:///android_asset/Course4Content/C4_Chapter8Content.html";
        if(coursePosition==3&&chapterPosition==8)
            chapterContentFilePath="file:///android_asset/Course4Content/C4_Chapter9Content.html";
        if(coursePosition==3&&chapterPosition==9)
            chapterContentFilePath="file:///android_asset/Course4Content/C4_Chapter10Content.html";
        if(coursePosition==3&&chapterPosition==10)
            chapterContentFilePath="file:///android_asset/Course4Content/C4_Chapter11Content.html";
        if(coursePosition==3&&chapterPosition==11)
            chapterContentFilePath="file:///android_asset/Course4Content/C4_Chapter12Content.html";
        if(coursePosition==3&&chapterPosition==12)
            chapterContentFilePath="file:///android_asset/Course4Content/C4_Chapter13Content.html";
        /** chapter 5 **/
        if(coursePosition==4&&chapterPosition==0)
            chapterContentFilePath="file:///android_asset/Course5Content/C5_Chapter1Content.html";
        if(coursePosition==4&&chapterPosition==1)
            chapterContentFilePath="file:///android_asset/Course5Content/C5_Chapter2Content.html";
        if(coursePosition==4&&chapterPosition==2)
            chapterContentFilePath="file:///android_asset/Course5Content/C5_Chapter3Content.html";
        if(coursePosition==4&&chapterPosition==3)
            chapterContentFilePath="file:///android_asset/Course5Content/C5_Chapter4Content.html";
        /** course 6 **/
        if(coursePosition==5&&chapterPosition==0)
            chapterContentFilePath="file:///android_asset/Course6Content/C6_Chapter1Content.html";
        if(coursePosition==5&&chapterPosition==1)
            chapterContentFilePath="file:///android_asset/Course6Content/C6_Chapter2Content.html";
        if(coursePosition==5&&chapterPosition==2)
            chapterContentFilePath="file:///android_asset/Course6Content/C6_Chapter3Content.html";
        if(coursePosition==5&&chapterPosition==3)
            chapterContentFilePath="file:///android_asset/Course6Content/C6_Chapter4Content.html";
        if(coursePosition==5&&chapterPosition==4)
            chapterContentFilePath="file:///android_asset/Course6Content/C6_Chapter5Content.html";
        if(coursePosition==5&&chapterPosition==5)
            chapterContentFilePath="file:///android_asset/Course6Content/C6_Chapter6Content.html";
        if(coursePosition==5&&chapterPosition==6)
            chapterContentFilePath="file:///android_asset/Course6Content/C6_Chapter7Content.html";
        if(coursePosition==5&&chapterPosition==7)
            chapterContentFilePath="file:///android_asset/Course6Content/C6_Chapter8Content.html";
        if(coursePosition==5&&chapterPosition==8)
            chapterContentFilePath="file:///android_asset/Course6Content/C6_Chapter9Content.html";
        if(coursePosition==5&&chapterPosition==9)
            chapterContentFilePath="file:///android_asset/Course6Content/C6_Chapter10Content.html";
        if(coursePosition==5&&chapterPosition==10)
            chapterContentFilePath="file:///android_asset/Course6Content/C6_Chapter11Content.html";
        if(coursePosition==5&&chapterPosition==11)
            chapterContentFilePath="file:///android_asset/Course6Content/C6_Chapter12Content.html";
        /** chapter 7 **/
        if(coursePosition==6&&chapterPosition==0)
            chapterContentFilePath="file:///android_asset/Course7Content/C7_Chapter1Content.html";
        if(coursePosition==6&&chapterPosition==0)
            chapterContentFilePath="file:///android_asset/Course7Content/C7_Chapter2Content.html";
        /** chapter 8 **/
        if(coursePosition==7&&chapterPosition==0)
            chapterContentFilePath="file:///android_asset/Course8Content/C8_Chapter1Content.html";
        if(coursePosition==7&&chapterPosition==1)
            chapterContentFilePath="file:///android_asset/Course8Content/C8_Chapter2Content.html";
        if(coursePosition==7&&chapterPosition==2)
            chapterContentFilePath="file:///android_asset/Course8Content/C8_Chapter3Content.html";
        if(coursePosition==7&&chapterPosition==3)
            chapterContentFilePath="file:///android_asset/Course8Content/C8_Chapter4Content.html";
        if(coursePosition==7&&chapterPosition==4)
            chapterContentFilePath="file:///android_asset/Course8Content/C8_Chapter5Content.html";
        if(coursePosition==7&&chapterPosition==5)
            chapterContentFilePath="file:///android_asset/Course8Content/C8_Chapter6Content.html";
        return chapterContentFilePath;
    }
}