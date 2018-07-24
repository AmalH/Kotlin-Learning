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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.models.ForumQuestion;
import amalhichri.androidprojects.com.kotlinlearning.services.ForumsServices;
import amalhichri.androidprojects.com.kotlinlearning.services.ServerCallbacks;
import amalhichri.androidprojects.com.kotlinlearning.utils.Statics;
import me.originqiu.library.EditTag;


public class EditForumQuestionFragment extends Fragment {
    
    ProgressDialog progressDialog;
    boolean addCodeToForum,isCodeEmpty=false;

    private ForumQuestion question;

    public void setForum(ForumQuestion question){
        this.question =question;
        if(this.question.getCode()!=null){
            addCodeToForum =true;
        }
        else {
            addCodeToForum = false;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        progressDialog=new ProgressDialog(getActivity());
        return inflater.inflate(R.layout.fragment_edit_forum, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

       /** uploading data **/
        ((EditTag) getActivity().findViewById(R.id.editForumQuestionTags)).clearDisappearingChildren();
        getActivity().findViewById(R.id.editForumQuestionTags).clearAnimation();
        ((EditTag) getActivity().findViewById(R.id.editForumQuestionTags)).setEditable(true);
        for(String s :  question.getTags().split(",")){
            ((EditTag) getActivity().findViewById(R.id.editForumQuestionTags)).addTag(s);
        }
        ((TextView)getActivity().findViewById(R.id.editQuestionTitle)).setText(question.getSubject());
        ((EditText)getActivity().findViewById(R.id.editForumQstContent)).setText(question.getContent());
        if(addCodeToForum){
            ((ToggleButton)getActivity().findViewById(R.id.editForumCodeToggle)).setChecked(true);
            getActivity().findViewById(R.id.editForumCodeEditor).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.editForumCodeStatic).setVisibility(View.VISIBLE);
            ((EditText)getActivity().findViewById(R.id.editForumCodeEditor)).setText(question.getCode());
        }


        /** edit question btn click **/
        getActivity().findViewById(R.id.editForumSaveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                question.setSubject(((TextView)getActivity().findViewById(R.id.editQuestionTitle)).getText().toString());
                question.setContent(((TextView)getActivity().findViewById(R.id.editForumQstContent)).getText().toString());
                question.setTags("");
                for(String s : ((EditTag)getActivity().findViewById(R.id.editForumQuestionTags)).getTagList()){
                    question.setTags(question.getTags()+s+",");
                }

                progressDialog.setMessage("Saving, please wait.");
                progressDialog.show();

                if(addCodeToForum){
                    String code=((EditText)getActivity().findViewById(R.id.editForumCodeEditor)).getText().toString().replaceAll("[\r\n]+", "\n");
                    if(!code.trim().isEmpty()){
                        question.setCode(code);
                        isCodeEmpty=false;
                    }
                    else {
                        Toast.makeText(getContext(),"Please add code snippet !", Toast.LENGTH_SHORT).show();
                        isCodeEmpty=true;
                    }
                }
                else question.setCode(null);

                if(!isCodeEmpty && !question.getContent().isEmpty() && !question.getTags().isEmpty()){
                    ForumsServices.getInstance().editForumPost(getContext(), question, Statics.auth.getCurrentUser().getUid(), new ServerCallbacks() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            getFragmentManager().popBackStack();
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onError(VolleyError result) {
                            Toast.makeText(getContext(),result.getClass().getName(), Toast.LENGTH_SHORT).show();
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onWrong(JSONObject result) {
                            Toast.makeText(getContext(),result.toString(), Toast.LENGTH_SHORT).show();
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
                else {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(getContext(),"Please fill all fields", Toast.LENGTH_LONG).show();
                }
            }
        });

      /** delete question btn click */
        getActivity().findViewById(R.id.deleteForumQuestion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Delete")
                        .setMessage("Do you want to Delete")
                        .setIcon(R.drawable.ic_delete_post)
                        .setPositiveButton("Delete now", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int whichButton) {
                                progressDialog.setMessage("Deleting");
                                progressDialog.show();
                                ForumsServices.getInstance().deleteForumPost(Statics.auth.getCurrentUser().getUid(), getContext(), question.getId(), new ServerCallbacks() {
                                    @Override
                                    public void onSuccess(JSONObject result) {
                                        dialog.dismiss();
                                        if(progressDialog.isShowing())
                                            progressDialog.dismiss();
                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.root_share_fragment,new ShareFragment()).commit();
                                        Toast.makeText(getActivity(),"Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                    @Override
                                    public void onError(VolleyError result) {
                                        Toast.makeText(getActivity(),result.getClass().getName(), Toast.LENGTH_SHORT).show();
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
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        ((ToggleButton)getActivity().findViewById(R.id.editForumCodeToggle)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean ischecked) {
                if(ischecked){
                    addCodeToForum =true;
                    getActivity().findViewById(R.id.editForumCodeEditor).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.editForumCodeStatic).setVisibility(View.VISIBLE);
                }
                else {
                    addCodeToForum =false;
                    getActivity().findViewById(R.id.editForumCodeEditor).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.editForumCodeStatic).setVisibility(View.GONE);
                }
            }
        });
    }

}
