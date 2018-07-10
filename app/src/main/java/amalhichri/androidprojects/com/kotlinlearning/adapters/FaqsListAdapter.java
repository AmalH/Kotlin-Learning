package amalhichri.androidprojects.com.kotlinlearning.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import amalhichri.androidprojects.com.kotlinlearning.R;


/**
 * Created by Amal on 08/12/2017.
 */

public class FaqsListAdapter extends BaseExpandableListAdapter {

    /** expandable listView for QA fragment **/

    private Context context;

    public FaqsListAdapter(Context context){
        this.context=context;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null)convertView = ((LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.faqslist_item_expended, null);
        ((TextView) convertView.findViewById(R.id.faqsListAnswer)).setText(context.getString(getAnswerNb(groupPosition)));
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public int getGroupCount() {
        return 7;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = ((LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.faqslist_item, null);
        ((TextView)convertView.findViewById(R.id.faqsListQuestion)).setText(context.getString(getQuestionNb(groupPosition)));
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

    private int getQuestionNb(int indc){
        int questionNb=0;
        switch (indc){
            case 0:questionNb=R.string.question1;break;
            case 1:questionNb=R.string.question2;break;
            case 2:questionNb=R.string.question3;break;
            case 3:questionNb=R.string.question4;break;
            case 4:questionNb=R.string.question5;break;
            case 5:questionNb=R.string.question6;break;
            case 6:questionNb=R.string.question7;break;
        }
        return questionNb;
    }

    private int getAnswerNb(int indc){
        int answerNb=0;
        switch (indc){
            case 0: answerNb=R.string.answer1;break;
            case 1:answerNb=R.string.answer2;break;
            case 2:answerNb=R.string.answer3;break;
            case 3:answerNb=R.string.answer4;break;
            case 4:answerNb=R.string.answer5;break;
            case 5:answerNb=R.string.answer6;break;
            case 6:answerNb=R.string.answer7;break;
        }
        return answerNb;
    }




}
