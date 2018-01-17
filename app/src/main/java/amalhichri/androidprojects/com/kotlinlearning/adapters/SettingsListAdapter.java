package amalhichri.androidprojects.com.kotlinlearning.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import amalhichri.androidprojects.com.kotlinlearning.R;


public class SettingsListAdapter extends BaseAdapter {


    /** adapter for settings list , with a seperation item [ ACCOUNT | SETTINGS | SIGN OUT ]
     * between each set of items **/

    private static final int ITEM_VIEW_TYPE_CONTENT = 0;
    private static final int ITEM_VIEW_TYPE_SEPARATOR = 1;
    private static final int ITEM_VIEW_TYPE_COUNT = 2;
    private String[] content;
    private Activity adaptTo;

    public SettingsListAdapter(Activity adaptTo, String[]content){
        this.adaptTo=adaptTo;
        this.content=content;
    }

    @Override
    public int getCount() {
        return content.length;
    }

    @Override
    public Object getItem(int i) {
        return content[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public int getViewTypeCount() {
        return ITEM_VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return (content[position].startsWith("Sep_")) ? ITEM_VIEW_TYPE_SEPARATOR : ITEM_VIEW_TYPE_CONTENT;
    }
    @Override
    public boolean isEnabled(int position) {
        // A separator cannot be clicked !
        return getItemViewType(position) != ITEM_VIEW_TYPE_SEPARATOR;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int type = getItemViewType(i);

        /** instead of just loading a view for the list row
         * we will test on the ITEM_VIEW_TYPE_SEPARATOR | ITEM_VIEW_TYPE_CONTENT int value
         * and load a different layout each time !
         */
        if (view == null) {
            /**
             * if it's  ITEM_VIEW_TYPE_SEPARATOR load  R.layout.settingslist_rowseperator_view
             * else load R.layout.settingslist_item
             */
            view = LayoutInflater.from(adaptTo.getBaseContext()).inflate(
                    type == ITEM_VIEW_TYPE_SEPARATOR ? R.layout.settingslist_rowseperator_view : R.layout.settingslist_item, viewGroup, false);
        }

        /**
         * then we just fill each item layout with apprpriate data from private String[] content attribute !
         */
        if (type == ITEM_VIEW_TYPE_SEPARATOR) {
            ((TextView) view.findViewById(R.id.separatorTxt)).setText(getItem(i).toString().substring(4,getItem(i).toString().length())); // remove the "_Sep" from the string !
        } else {
            ((TextView) view.findViewById(R.id.itemContent)).setText((String) getItem(i));
        }
        return view;
    }
}
