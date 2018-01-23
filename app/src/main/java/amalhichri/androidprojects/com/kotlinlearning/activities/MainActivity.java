package amalhichri.androidprojects.com.kotlinlearning.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.adapters.MainUiPagerAdapter;
import amalhichri.androidprojects.com.kotlinlearning.utils.Statics;
import me.relex.circleindicator.CircleIndicator;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends Activity implements View.OnClickListener   {


    private ViewPager vp;
    private CircleIndicator circlePageIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // firebase offline data
       // FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        /** if user is logged in --> homeActivity**/
        if(Statics.currentUser !=null){
          startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }

        /*** setting the page indicator ***/
        vp = findViewById(R.id.vwpMain);
        circlePageIndicator = findViewById(R.id.circlePageIndicator);
        vp.setAdapter(new MainUiPagerAdapter(getApplicationContext()));
        vp.setCurrentItem(0);
        circlePageIndicator.setViewPager(vp);

    }

    /*** login and signup buttons clicks ***/
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case(R.id.loginBtn):
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                break;
            case (R.id.signupBtn):
                startActivity(new Intent(getApplicationContext(),SignupActivity.class));
        }
    }


    /** for calligraphy lib usage **/
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
