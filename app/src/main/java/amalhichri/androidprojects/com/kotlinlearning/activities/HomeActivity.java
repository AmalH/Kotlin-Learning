package amalhichri.androidprojects.com.kotlinlearning.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.adapters.HomePageTabsAdapter;
import amalhichri.androidprojects.com.kotlinlearning.models.User;
import amalhichri.androidprojects.com.kotlinlearning.utils.Statics;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends AppCompatActivity {

    private static TabLayout tablayout;
    private static ViewPager vpPager;
    private HomePageTabsAdapter adapter;
    private static ColorMatrixColorFilter filter;
    private ColorMatrix matrix;
    private FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        fragmentManager =getSupportFragmentManager();

        /** adding current user to sharedPreferences **/
        Statics.usersTable.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SharedPreferences loggedUserPrefs = getSharedPreferences("loggedUserPrefs",0);
                String loggedUser = (new Gson()).toJson(dataSnapshot.getValue(User.class));
                SharedPreferences.Editor e=loggedUserPrefs.edit();
                e.putString("user",loggedUser);
                e.commit();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });


        /** will be used to change tab icons colors on select/deselect */
        matrix = new ColorMatrix();
        matrix.setSaturation(0);
        filter = new ColorMatrixColorFilter(matrix);

        /** setting the actionBar **/
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final View v = LayoutInflater.from(this).inflate(R.layout.actionbartitle_view, null);
        ((TextView)v.findViewById(R.id.actionBarTitle)).setText("Learn");
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setElevation(0);

        /*** setting the tabsLayout ***/
        adapter = new HomePageTabsAdapter(getSupportFragmentManager());
        vpPager = findViewById(R.id.viewpager);
        vpPager.setAdapter(adapter);
        vpPager.setCurrentItem(0);
        vpPager.setOffscreenPageLimit(3);
        tablayout=findViewById(R.id.tabsLayout);
        tablayout.setupWithViewPager(vpPager);
        setUpTabIcons(tablayout);


        /** change title in actionBar depending on tabSelected **/
        tablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch(tablayout.getSelectedTabPosition()) {
                    case 0:
                        tab.getIcon().clearColorFilter();
                        ((TextView)v.findViewById(R.id.actionBarTitle)).setText("Learn");
                        break;
                    case 1:
                        tab.getIcon().clearColorFilter();
                        ((TextView)v.findViewById(R.id.actionBarTitle)).setText("Share");
                        break;
                    case 2:
                        tab.getIcon().clearColorFilter();
                        ((TextView)v.findViewById(R.id.actionBarTitle)).setText("Compete");
                        break;
                    case 3:
                        tab.getIcon().clearColorFilter();
                        ((TextView)v.findViewById(R.id.actionBarTitle)).setText("Connect");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(filter);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }


    /** options menu **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_profile){
            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        }
        if(item.getItemId()==R.id.action_settings){
            startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }




    /*** helper methods ***/
    private static void allTabIconsToDeselected(TabLayout tablayout){
        for(int i=1;i<4;i++){
            tablayout.getTabAt(i).getIcon().setColorFilter(filter);
        }
    }
    private static void setUpTabIcons(TabLayout tbs){
        tbs.getTabAt(0).setIcon(R.drawable.ic_learn_icon_tab0);
        tbs.getTabAt(1).setIcon(R.drawable.ic_share_icon_tab1);
        tbs.getTabAt(2).setIcon(R.drawable.ic_compete_icon_tab2);
        tbs.getTabAt(3).setIcon(R.drawable.ic_connect_icon_tab3);
        allTabIconsToDeselected(tbs);
    }
    /** to prevent back to loginActivity **/
    @Override
    public void onBackPressed() {
           fragmentManager.popBackStack();
    }

    /** for calligraphy lib usage **/
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}