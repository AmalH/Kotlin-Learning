package amalhichri.androidprojects.com.kotlinlearning.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.models.ForumQuestion;
import amalhichri.androidprojects.com.kotlinlearning.services.ForumsServices;
import amalhichri.androidprojects.com.kotlinlearning.services.ServerCallbacks;
import me.originqiu.library.EditTag;

public class AddForumFragment extends Fragment {

    TextView subject;
    TextView content;
    EditTag tags;
    Button submitButton;
    ToggleButton toggleCode;
    TextView code_content;
    TextView static_code_contetnt;

    ProgressDialog progressDialog;
    boolean add_code_to_forum;
    boolean isCodeEmpty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        add_code_to_forum=false;
        isCodeEmpty=false;
//        FirebaseAuth.getInstance().getCurrentUser().reload();
        progressDialog=new ProgressDialog(getActivity());
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_add_forum, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        subject=getActivity().findViewById(R.id.add_forum_subject);
        content=getActivity().findViewById(R.id.addForumContent);
        tags=getActivity().findViewById(R.id.addQuestionTagsInput);
        submitButton=getActivity().findViewById(R.id.addForumSubmit);
        toggleCode=getActivity().findViewById(R.id.forumAddTooggleCode);
        code_content=getActivity().findViewById(R.id.forumAddCodeEditor);
        static_code_contetnt=getActivity().findViewById(R.id.forumAddCodeStatic);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ForumQuestion f= new ForumQuestion();
                f.setSubject(subject.getText().toString());
                f.setContent(content.getText().toString());
                f.setTags("");
                for(String s : tags.getTagList()){
                    f.setTags(f.getTags()+s+",");
                }
                progressDialog.setMessage("Posting, please wait.");
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

               if(!isCodeEmpty && !f.getContent().isEmpty() && !f.getTags().isEmpty() && !f.getSubject().isEmpty()){
                   // if(Configuration.isOnline(getContext()) && UsersServices.getInstance().is_verified(getContext()))
                        ForumsServices.getInstance().addForumPost(getContext(), f, "dZb3TxK1x5dqQJkq7ve0d683VoA3", new ServerCallbacks() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                Toast.makeText(getContext(),"Submitted...", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getContext(),"Oups...", Toast.LENGTH_SHORT).show();
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    /*else{
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        if(!Configuration.isOnline(getContext())) Toast.makeText(getContext(),"No connection", Toast.LENGTH_SHORT).show();
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
}
