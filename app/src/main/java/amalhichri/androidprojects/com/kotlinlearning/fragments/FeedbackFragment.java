package amalhichri.androidprojects.com.kotlinlearning.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.rey.material.widget.EditText;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.services.ServerCallbacks;
import amalhichri.androidprojects.com.kotlinlearning.services.UserServices;
import amalhichri.androidprojects.com.kotlinlearning.utils.Statics;


public class FeedbackFragment extends Fragment {

    private ArrayAdapter<String> spinnerAdapter;
    private List<String> feedBackTypes= new ArrayList<>();
    private static String imgUrl;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_feedback, container, false);


        UserServices.getInstance().getUserById(Statics.auth.getCurrentUser().getUid(),getActivity(),new ServerCallbacks() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    ((TextView) v.findViewById(R.id.userName_Feedback)).setText((result.getString("username")));
                    if (result.getString("picture") != null)
                        Picasso.with(getActivity()).load(result.getString("picture")).into((ImageView)v.findViewById(R.id.userImg_Feedback));
                    if (result.getString("picture").isEmpty())
                        ((ImageView) v.findViewById(R.id.userImg_Feedback)).setImageDrawable(UserServices.getInstance().getPlaceholderProfilePic(result.getString("username")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(VolleyError result) {
                Toast.makeText(container.getContext(),"error class"+result.getClass().getName(),Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onWrong(JSONObject result) {
                Toast.makeText(getActivity(),"error----"+result.toString(),Toast.LENGTH_SHORT).show();
            }
        });


        /** settings feedback type spinner **/
        feedBackTypes.add("General feedback");feedBackTypes.add("Bug report");feedBackTypes.add("Suggestion");feedBackTypes.add("Other");
        spinnerAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(),R.layout.customspinner_view, feedBackTypes);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner)v.findViewById(R.id.feedback_type)).setAdapter(spinnerAdapter);
        (v.findViewById(R.id.sendFeedbackBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Email = new Intent(Intent.ACTION_SEND);
                Email.setType("text/email");
                Email.putExtra(Intent.EXTRA_EMAIL, new String[] { "ikotlin.team@gmail.com" });
                Email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                Email.putExtra(Intent.EXTRA_TEXT, "Feedback Type: " + ((Spinner)v.findViewById(R.id.feedback_type)).getSelectedItem().toString()
                +"\nContent: "+((EditText)v.findViewById(R.id.feedback_content)).getText().toString());
                startActivity(Intent.createChooser(Email, "Send Feedback:"));
            }
        });
        return v;
    }
}
