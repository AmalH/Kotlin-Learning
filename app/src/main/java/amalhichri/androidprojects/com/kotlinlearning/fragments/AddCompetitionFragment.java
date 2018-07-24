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
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.models.Competition;
import amalhichri.androidprojects.com.kotlinlearning.services.CompetitionsServices;
import amalhichri.androidprojects.com.kotlinlearning.services.ServerCallbacks;
import amalhichri.androidprojects.com.kotlinlearning.utils.Statics;


public class AddCompetitionFragment extends Fragment {


    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_competition, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        progressDialog = new ProgressDialog(getActivity());

        getActivity().findViewById(R.id.competeAddSubmit).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(!((EditText)getActivity().findViewById(R.id.compete_add_title)).getText().toString().isEmpty() && !((EditText)getActivity().findViewById(R.id.competeAddContent)).getText().toString().isEmpty()){
                    String t=((EditText)getActivity().findViewById(R.id.compete_add_title)).getText().toString().trim();
                    String p=((EditText)getActivity().findViewById(R.id.competeAddContent)).getText().toString().trim();
                    if(!t.isEmpty() && !p.isEmpty()){

                        final Competition c=new Competition();
                        c.setTitle(((EditText)getActivity().findViewById(R.id.compete_add_title)).getText().toString());
                        c.setContent(((EditText)getActivity().findViewById(R.id.competeAddContent)).getText().toString());

                        new AlertDialog.Builder(getActivity())
                                .setTitle("New contest")
                                .setMessage("this action could not be undone")
                                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, int whichButton) {
                                        progressDialog.setMessage("Adding...");
                                        progressDialog.show();
                                        CompetitionsServices.getInstance().addCompetition(getContext(),c, Statics.auth.getCurrentUser().getUid(), new ServerCallbacks() {
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
                                            }

                                            @Override
                                            public void onWrong(JSONObject result) {
                                                if(progressDialog.isShowing())
                                                    progressDialog.dismiss();
                                                }
                                        });
                                    }

                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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



}
