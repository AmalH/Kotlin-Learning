package amalhichri.androidprojects.com.kotlinlearning.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
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

public class AddForumFragment extends Fragment {

    private ProgressDialog progressDialog;
    private boolean add_code_to_forum;
    private boolean isCodeEmpty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        add_code_to_forum = false;
        isCodeEmpty = false;
        progressDialog = new ProgressDialog(getActivity());
        View v = inflater.inflate(R.layout.fragment_add_forum, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().findViewById(R.id.addForumSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ForumQuestion f = new ForumQuestion();
                f.setSubject(((EditText) getActivity().findViewById(R.id.add_forum_subject)).getText().toString());
                f.setContent(((EditText) getActivity().findViewById(R.id.addForumContent)).getText().toString());
                f.setTags("");
                for (String s : ((EditTag) getActivity().findViewById(R.id.addQuestionTagsInput)).getTagList()) {
                    f.setTags(f.getTags() + s + ",");
                }
                progressDialog.setMessage("Posting, please wait.");
                progressDialog.show();
                if (add_code_to_forum) {
                    String code = ((EditText) getActivity().findViewById(R.id.forumAddCodeEditor)).getText().toString().replaceAll("[\r\n]+", "\n");
                    if (!code.trim().isEmpty()) {
                        f.setCode(code);
                        isCodeEmpty = false;
                    } else {
                        Toast.makeText(getContext(), "No code snippet added !", Toast.LENGTH_SHORT).show();
                        isCodeEmpty = true;
                    }
                }

                if (!isCodeEmpty && !f.getContent().isEmpty() && !f.getTags().isEmpty() && !f.getSubject().isEmpty()) {
                    ForumsServices.getInstance().addForumPost(getContext(), f, Statics.auth.getCurrentUser().getUid(), new ServerCallbacks() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            getFragmentManager().popBackStack();
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onError(VolleyError result) {
                            Toast.makeText(getContext(), "Please check internet connection !", Toast.LENGTH_SHORT).show();
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onWrong(JSONObject result) {
                            Toast.makeText(getContext(),"Please check internet connection !", Toast.LENGTH_SHORT).show();
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    });

                } else {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(getContext(), "Empty fields", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        ((ToggleButton) getActivity().findViewById(R.id.forumAddTooggleCode)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean ischecked) {
                if (ischecked) {
                    add_code_to_forum = true;
                    (getActivity().findViewById(R.id.forumAddCodeEditor)).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.forumAddCodeStatic).setVisibility(View.VISIBLE);
                } else {
                    add_code_to_forum = false;
                    (getActivity().findViewById(R.id.forumAddCodeEditor)).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.forumAddCodeStatic).setVisibility(View.GONE);
                }
            }
        });
    }
}
