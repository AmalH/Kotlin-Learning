package amalhichri.androidprojects.com.kotlinlearning.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.adapters.ProfileTabsAdapter;
import amalhichri.androidprojects.com.kotlinlearning.services.ServerCallbacks;
import amalhichri.androidprojects.com.kotlinlearning.services.UsersServices;
import amalhichri.androidprojects.com.kotlinlearning.utils.Statics;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class ProfileActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ProfileTabsAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        /*** disabling actionBar ****/
        getSupportActionBar().hide();

        /** setting the tabLayout **/
        tabLayout = findViewById(R.id.profileTabs);
        viewPager = findViewById(R.id.viewpager_profileTabs);
        adapter = new ProfileTabsAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                Statics.setTabIndicator(tabLayout,30,30);
            }
        });
        tabLayout.getTabAt(1).select();
        LinearLayout tabStrip = ((LinearLayout)tabLayout.getChildAt(0));
        for(int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }



        (findViewById(R.id.logoutBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statics.auth.signOut();
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        UsersServices.getInstance().getUserById(Statics.auth.getCurrentUser().getUid(), this, new ServerCallbacks() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    ((TextView) findViewById(R.id.fullNameInProfile)).setText((result.getString("username")));
                    if (result.getString("picture") != null) {
                        Picasso.with(getApplicationContext()).load(Uri.parse(result.getString("picture"))).into((ImageView) findViewById(R.id.userImgProfile));
                    }
                    if (result.getString("picture").isEmpty())
                        ((ImageView) findViewById(R.id.userImgProfile)).setImageDrawable(Statics.getPlaceholderProfilePic(result.getString("username")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError result) {
                Toast.makeText(getApplicationContext(), "error class" + result.getClass().getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onWrong(JSONObject result) {
                Toast.makeText(getApplicationContext(), "error----" + result.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * for calligraphy lib usage
     **/
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
