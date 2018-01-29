package amalhichri.androidprojects.com.kotlinlearning.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import amalhichri.androidprojects.com.kotlinlearning.R;


public class LearnFragment_noCourses extends Fragment {


    static private FragmentManager fgMgr;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.fgMgr=getActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_learn_nocourses, container, false);
        return v;
    }
}
