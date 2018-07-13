package amalhichri.androidprojects.com.kotlinlearning.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.adapters.ProfileTabsAdapter;
import amalhichri.androidprojects.com.kotlinlearning.services.UserServices;
import amalhichri.androidprojects.com.kotlinlearning.utils.Statics;
import de.hdodenhof.circleimageview.CircleImageView;
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

        /** populating ui with user data **/
        /** fields data **/
        Log.d("picture url",Statics.getLoggedUser(getApplicationContext()).getPictureUrl().toString());
        ((TextView) findViewById(R.id.fullNameInProfile)).setText(Statics.getLoggedUser(getApplicationContext()).getFirstName()+" "+
        Statics.getLoggedUser(getApplicationContext()).getLastName());

        if(FirebaseAuth.getInstance().getCurrentUser().toString()!=null){
            Picasso.with(getApplicationContext()).load(Uri.parse(Statics.getLoggedUser(getApplicationContext()).getPictureUrl())).into((CircleImageView)findViewById(R.id.userImgProfile));
        }
        else
            ((ImageView) findViewById(R.id.userImgProfile)).setImageDrawable(UserServices.getInstance().getEmptyProfimePicture(Statics.getLoggedUser(getApplicationContext()).getFirstName()+" "+
                    Statics.getLoggedUser(getApplicationContext()).getLastName()));


        (findViewById(R.id.logoutBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statics.auth.signOut();
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });
    }

    /** for calligraphy lib usage **/
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
