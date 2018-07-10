package amalhichri.androidprojects.com.kotlinlearning.fragments;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eftimoff.viewpagertransformers.RotateUpTransformer;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.adapters.ConnectTabsAdapter;
import amalhichri.androidprojects.com.kotlinlearning.utils.CustomVerticalViewPager;
import q.rorbin.verticaltablayout.VerticalTabLayout;
import q.rorbin.verticaltablayout.widget.TabView;


public class ConnectFragment extends Fragment {


    private static ColorMatrixColorFilter filter;
    private ColorMatrix matrix;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_connect, container, false);

        /** load first icon's code **/
        getActivity().getFragmentManager().beginTransaction().replace(R.id.faqsRootElement,new ShareAppFragment()).addToBackStack(null).commitAllowingStateLoss();

        /** will be used to change tab icons colors on select/deselect */
        matrix = new ColorMatrix();
        matrix.setSaturation(0);
        filter = new ColorMatrixColorFilter(matrix);

        /** setting the tabLayout ***/
        final CustomVerticalViewPager viewPager_connectTabs = v.findViewById(R.id.viewpager_connectTabs);
        final VerticalTabLayout connectTabLayout = v.findViewById(R.id.vertical_nvgTab_connect);
        viewPager_connectTabs.setAdapter(new ConnectTabsAdapter(getContext()));
        viewPager_connectTabs.setPagingEnabled(false);
        viewPager_connectTabs.setPageTransformer(true, new RotateUpTransformer());
        connectTabLayout.setupWithViewPager(viewPager_connectTabs);
        connectTabLayout.setOnTabSelectedListener(new VerticalTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabView tab, int position) {
                switch (position){
                    case 0:
                        getActivity().getFragmentManager().beginTransaction().replace(R.id.faqsRootElement,new ShareAppFragment()).commit();
                        break;
                    case 1:
                        getActivity().getFragmentManager().beginTransaction().replace(R.id.faqsRootElement,new FeedbackFragment()).commit();
                        break;
                    case 2:
                        getActivity().getFragmentManager().beginTransaction().replace(R.id.faqsRootElement,new RateUsFragment()).commit();
                        break;
                    case 3:
                        getActivity().getFragmentManager().beginTransaction().replace(R.id.faqsRootElement,new AboutUsFragment()).commit();
                        break;
                    case 4:
                        getActivity().getFragmentManager().beginTransaction().replace(R.id.faqsRootElement,new FaqsFragment()).commit();
                        break;
                }
            }

            @Override
            public void onTabReselected(TabView tab, int position) {

            }
        });
        return v;
    }
}
