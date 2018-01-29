package amalhichri.androidprojects.com.kotlinlearning.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import amalhichri.androidprojects.com.kotlinlearning.R;


public class RootFragment_learn extends Fragment {

    /**
     * fragment added to load/switch fragments in the first tab of HomePage tabLayout
     * call  getFragmentManager.replace ( SOME_ROOT_LAYOUT, My new fragment to load ! )
     *
     * LearnFragment_Chapter / LearnFragment_course / LearnFragment_noCourses... are all being switched inside  R.layout.fragment_root_fragment_learn
     * **/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_root_learn, container, false);
        getFragmentManager().beginTransaction().replace(R.id.root_learFragment,new LearnFragment_noCourses()).addToBackStack(null).commit();
         return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
