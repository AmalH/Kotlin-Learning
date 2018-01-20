package amalhichri.androidprojects.com.kotlinlearning.activities;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.adapters.SettingsListAdapter;
import amalhichri.androidprojects.com.kotlinlearning.utils.Statics;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SettingsActivity extends ListActivity {

    private static final String[] settingsContent = {
            "Sep_ACCOUNT",
            "Edit profile",
            "Change password",
            "Connected accounts",
            "Sep_SETTINGS",
            "Activity Feed",
            "Push notifications",
            "Language",
            "Sep_",
            "Sign out"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        /** setting the actionBar **/
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeAsUpIndicator(R.drawable.backarrow);
        final View v = LayoutInflater.from(this).inflate(R.layout.actionbartitle_view, null);
        ((TextView)v.findViewById(R.id.actionBarTitle)).setText("   Settings");
        getActionBar().setCustomView(v);
        setListAdapter(new SettingsListAdapter(this,settingsContent));
    }

    /** sign out **/
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id){
        if(position==9){
            Statics.auth.signOut();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    /** options menu **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /** for calligraphy lib usage **/
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
