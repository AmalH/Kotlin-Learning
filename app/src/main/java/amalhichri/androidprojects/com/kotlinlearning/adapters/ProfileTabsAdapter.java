package amalhichri.androidprojects.com.kotlinlearning.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import amalhichri.androidprojects.com.kotlinlearning.fragments.BadgesFragment;
import amalhichri.androidprojects.com.kotlinlearning.fragments.SkillsFragment;

/**
 * Created by Amal on 11/11/2017.
 */

public class ProfileTabsAdapter extends FragmentPagerAdapter {

    private int NUM_ITEMS = 2;
    private String[] titles= new String[]{"3\nSkills","5\nBadges"};

    public ProfileTabsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return  NUM_ITEMS ;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SkillsFragment();
            case 1:
                return new BadgesFragment();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return  titles[position];
    }
}
