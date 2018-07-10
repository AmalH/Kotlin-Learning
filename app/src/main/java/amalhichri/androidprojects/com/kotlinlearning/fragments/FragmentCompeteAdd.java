package amalhichri.androidprojects.com.kotlinlearning.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.models.Competition;
import amalhichri.androidprojects.com.kotlinlearning.services.CompetitionServices;
import amalhichri.androidprojects.com.kotlinlearning.services.ServerCallbacks;


public class FragmentCompeteAdd extends Fragment {


    TextView title,content;
    Button addButton;
    ImageButton backButton;
    ProgressDialog progressDialog;
    Spinner level;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_compete_add, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        title=getActivity().findViewById(R.id.compete_add_title);
        content=getActivity().findViewById(R.id.compete_add_content);
        addButton=getActivity().findViewById(R.id.compete_add_submit);
        backButton=getActivity().findViewById(R.id.add_compete_back);
        level=getActivity().findViewById(R.id.compete_add_level);

        progressDialog = new ProgressDialog(getActivity());

        attachAddListener();
        attachBackListener();
    }

    public void attachAddListener(){
        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(!title.getText().toString().isEmpty() && !content.getText().toString().isEmpty()){
                   String t=title.getText().toString().trim();
                   String p=content.getText().toString().trim();
                   if(!t.isEmpty() && !p.isEmpty()){

                       final Competition c=new Competition();
                       c.setTitle(title.getText().toString());
                       c.setContent(content.getText().toString());
                       c.setLevel(Integer.valueOf(level.getSelectedItem().toString()));

                       new AlertDialog.Builder(getActivity())
                               //set message, title, and icon
                               .setTitle("Post problemset")
                               .setMessage("this action could not be undone")
                               .setIcon(R.drawable.ic_action_add_forum)
                               .setPositiveButton("Confirm adding", new DialogInterface.OnClickListener() {
                                   public void onClick(final DialogInterface dialog, int whichButton) {
                                       progressDialog.setMessage("Posting...");
                                       progressDialog.show();
                                       CompetitionServices.getInstance().addCompetition(getContext(),c, FirebaseAuth.getInstance().getCurrentUser().getUid(), new ServerCallbacks() {
                                           @Override
                                           public void onSuccess(JSONObject result) {
                                               dialog.dismiss();
                                               if(progressDialog.isShowing())
                                                   progressDialog.dismiss();

                                               getActivity().getSupportFragmentManager().popBackStack();
                                               Toast.makeText(getActivity(),"Posted", Toast.LENGTH_SHORT).show();
                                           }

                                           @Override
                                           public void onError(VolleyError result) {
                                               if(progressDialog.isShowing())
                                                   progressDialog.dismiss();

                                               // Toast.makeText(getActivity(),result.toString(),Toast.LENGTH_SHORT).show();
                                           }

                                           @Override
                                           public void onWrong(JSONObject result) {
                                               if(progressDialog.isShowing())
                                                   progressDialog.dismiss();

                                               //  Toast.makeText(getActivity(),result.toString(),Toast.LENGTH_SHORT).show();
                                           }
                                       });
                                   }

                               })
                               .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                   public void onClick(DialogInterface dialog, int which) {

                                       dialog.dismiss();
                                   }
                               })
                               .show();

                   }
                   else Toast.makeText(getContext(),"Empty fields !", Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(getContext(),"Empty fields !", Toast.LENGTH_LONG).show();


            }
        });
    }

    public void attachBackListener(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

}
