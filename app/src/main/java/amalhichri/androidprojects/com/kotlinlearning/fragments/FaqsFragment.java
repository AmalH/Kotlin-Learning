package amalhichri.androidprojects.com.kotlinlearning.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.adapters.FaqsListAdapter;


public class FaqsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_faqs, container, false);
        ((ExpandableListView)v.findViewById(R.id.faqsListView)).setAdapter(new FaqsListAdapter(getActivity()));
        return v;
    }

}
