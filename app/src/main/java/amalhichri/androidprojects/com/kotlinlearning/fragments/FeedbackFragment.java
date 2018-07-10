package amalhichri.androidprojects.com.kotlinlearning.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.rey.material.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import amalhichri.androidprojects.com.kotlinlearning.R;


public class FeedbackFragment extends Fragment {

    private ArrayAdapter<String> spinnerAdapter;
    private List<String> feedBackTypes= new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_feedback, container, false);

        /** settings feedback type spinner **/
        feedBackTypes.add("General feedback");feedBackTypes.add("Bug report");feedBackTypes.add("Suggestion");feedBackTypes.add("Other");
        spinnerAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(),R.layout.customspinner_view, feedBackTypes);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner)v.findViewById(R.id.feedback_type)).setAdapter(spinnerAdapter);
        (v.findViewById(R.id.sendFeedbackBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setType("plain/text");
                sendIntent.setData(Uri.parse("ikotlin.team@gmail.com"));
                sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "ikotlin.team@gmail.com" });
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "test");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "hello. this is a message sent from my demo app :-)");
                startActivity(sendIntent);*/
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
