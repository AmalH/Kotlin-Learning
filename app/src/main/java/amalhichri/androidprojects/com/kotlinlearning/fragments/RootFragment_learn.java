package amalhichri.androidprojects.com.kotlinlearning.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.services.CoursesServices;
import amalhichri.androidprojects.com.kotlinlearning.services.ServerCallbacks;
import amalhichri.androidprojects.com.kotlinlearning.utils.Statics;


public class RootFragment_learn extends Fragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CoursesServices.getInstance().getAllUserCourses(Statics.auth.getCurrentUser().getUid(), getContext(), new ServerCallbacks() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    if(result.getJSONArray("courses").length()==0)
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.root_learFragment,new LearnFragment_nocourses()).commitAllowingStateLoss();
                    else
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.root_learFragment,new LearnFragment_currentUserCourses()).commitAllowingStateLoss();
                } catch (JSONException e) {
                    Toast.makeText(getContext(),"Server error "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError result) {
                Toast.makeText(getContext(),"ERROR ! Please check you internet connection."+result.getMessage(),Toast.LENGTH_SHORT);
            }

            @Override
            public void onWrong(JSONObject result) {
                Toast.makeText(getContext(),"ERROR ! Please try again later ."+result.toString(),Toast.LENGTH_SHORT);
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         return inflater.inflate(R.layout.fragment_root_learn, container, false);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
