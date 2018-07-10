package amalhichri.androidprojects.com.kotlinlearning.fragments;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.VolleyError;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.models.ForumQuestion;
import amalhichri.androidprojects.com.kotlinlearning.services.ForumServices;
import amalhichri.androidprojects.com.kotlinlearning.services.ServerCallbacks;
import me.originqiu.library.EditTag;


public class EditForumFragment extends Fragment {

    TextView subject;
    TextView content;
    EditTag tags;
    Button submitButton;
    ImageButton backButton;
    ToggleButton toggleCode;
    TextView code_content;
    TextView static_code_contetnt;
    ImageButton deleteForum;

    ProgressDialog progressDialog;
    boolean add_code_to_forum;
    boolean isCodeEmpty=false;

    private ForumQuestion f;

    public void setForum(ForumQuestion forum){
        f=forum;
        if(f.getCode()!=null){
            add_code_to_forum=true;
        }
        else {
            add_code_to_forum = false;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        progressDialog=new ProgressDialog(getActivity());
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_forum, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        subject=getActivity().findViewById(R.id.edit_forum_subject);
        content=getActivity().findViewById(R.id.edit_forum_content);
        tags=getActivity().findViewById(R.id.edit_forum_tags_input);
        submitButton=getActivity().findViewById(R.id.edit_forum_submit);
        backButton=getActivity().findViewById(R.id.edit_forum_back);
        toggleCode=getActivity().findViewById(R.id.forum_edit_tooggle_code);
        code_content=getActivity().findViewById(R.id.forum_edit_code_editor);
        static_code_contetnt=getActivity().findViewById(R.id.forum_edit_code_static);
        deleteForum=getActivity().findViewById(R.id.edit_forum_delete);

        fillComponents();
        attachOnClickListener();
        attachDeleteListener();
    }

    public void fillComponents(){
        //split tags
        String[] array = f.getTags().split(",");
        tags.clearDisappearingChildren();
        tags.clearAnimation();
        tags.setEditable(true);
        //fill tags
        for(String s : array){
            tags.addTag(s);
        }
        subject.setText(f.getSubject());
        content.setText(f.getContent());
        if(add_code_to_forum){
            toggleCode.setChecked(true);
            code_content.setVisibility(View.VISIBLE);
            static_code_contetnt.setVisibility(View.VISIBLE);
            code_content.setText(f.getCode());
        }
    }



    @Override
    public void onStart() {
        super.onStart();
        toggleCode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean ischecked) {
                if(ischecked){
                    add_code_to_forum=true;
                    code_content.setVisibility(View.VISIBLE);
                    static_code_contetnt.setVisibility(View.VISIBLE);
                }
                else {
                    add_code_to_forum=false;
                    code_content.setVisibility(View.GONE);
                    static_code_contetnt.setVisibility(View.GONE);
                }
            }
        });
    }

    public void  attachOnClickListener(){
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                f.setSubject(subject.getText().toString());
                f.setContent(content.getText().toString());
                f.setTags("");
                for(String s : tags.getTagList()){
                    f.setTags(f.getTags()+s+",");
                }
                progressDialog.setMessage("Saving, please wait.");
                progressDialog.show();
                //adding
                if(add_code_to_forum){
                    String code=code_content.getText().toString().replaceAll("[\r\n]+", "\n");
                    if(!code.trim().isEmpty()){
                        f.setCode(code);
                        isCodeEmpty=false;
                    }
                    else {
                        Toast.makeText(getContext(),"Empty code", Toast.LENGTH_SHORT).show();
                        isCodeEmpty=true;
                    }
                }
                else f.setCode(null);

                if(!isCodeEmpty && !f.getContent().isEmpty() && !f.getTags().isEmpty()){
                  //  if(Configuration.isOnline(getContext()))
                        ForumServices.getInstance().editForum(getContext(), f, FirebaseAuth.getInstance().getCurrentUser().getUid(), new ServerCallbacks() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                Toast.makeText(getContext(),"Upated", Toast.LENGTH_SHORT).show();
                                getFragmentManager().popBackStack();
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                            }

                            @Override
                            public void onError(VolleyError result) {
                                Toast.makeText(getContext(),"Server problem...", Toast.LENGTH_SHORT).show();
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
                    /*else {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        //if (!Configuration.isOnline(getContext()))
                            Toast.makeText(getContext(), "No connection", Toast.LENGTH_SHORT).show();
                    }*/
                }
                else {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(getContext(),"Empty fields", Toast.LENGTH_LONG).show();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = getActivity().getCurrentFocus();
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                getFragmentManager().popBackStack();
            }
        });
    }

    public void attachDeleteListener(){
        deleteForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               new AlertDialog.Builder(getActivity())
                        //set message, title, and icon
                        .setTitle("Delete")
                        .setMessage("Do you want to Delete")
                        .setIcon(R.drawable.ic_action_delete)
                        .setPositiveButton("Delete now", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int whichButton) {
                                progressDialog.setMessage("Deleting");
                                progressDialog.show();
                                ForumServices.getInstance().delForum(FirebaseAuth.getInstance().getCurrentUser().getUid(), getContext(), f.getId(), new ServerCallbacks() {
                                    @Override
                                    public void onSuccess(JSONObject result) {
                                        dialog.dismiss();
                                        if(progressDialog.isShowing())
                                            progressDialog.dismiss();

                                        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.root_share_fragment,new ShareFragment()).commit();
                                        Toast.makeText(getActivity(),"Deleted", Toast.LENGTH_SHORT).show();
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
        });
    }

}
