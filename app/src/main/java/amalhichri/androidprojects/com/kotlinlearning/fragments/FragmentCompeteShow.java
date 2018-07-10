package amalhichri.androidprojects.com.kotlinlearning.fragments;


import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.models.Competition;
import amalhichri.androidprojects.com.kotlinlearning.models.CompetitionAnswer;
import amalhichri.androidprojects.com.kotlinlearning.services.CompetitionServices;
import amalhichri.androidprojects.com.kotlinlearning.services.ServerCallbacks;
import amalhichri.androidprojects.com.kotlinlearning.services.StringCallbacks;
import amalhichri.androidprojects.com.kotlinlearning.services.UserProfileServices;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.kbiakov.codeview.CodeView;
import io.github.kbiakov.codeview.adapters.Options;
import io.github.kbiakov.codeview.highlight.ColorTheme;


public class FragmentCompeteShow extends Fragment {

    private Competition competition;
    private CompetitionAnswer answer;
    ImageButton backtoCompete;
    TextView subject, solved, content, username, created;
    CircleImageView profilepic;
    CardView cardViewAnswer;
    TextView textDateAnswer, addedcontent;
    CodeView answerCodeView;
    Button RespButton, run;
    LinearLayout answerdlayout;
    ImageButton editAnswer;
    ProgressDialog progressDialog;
    TextView argsText;

    TextView outputText;

    private int answerid;
    private boolean editnow = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FirebaseAuth.getInstance().getCurrentUser().reload();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_compete_show, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        progressDialog = new ProgressDialog(getActivity());

        backtoCompete = getActivity().findViewById(R.id.backtocompeteFromCompete);
        subject = getActivity().findViewById(R.id.compete_subject);
        solved = getActivity().findViewById(R.id.compete_solved_txt);
        content = getActivity().findViewById(R.id.compete_show_content);
        username = getActivity().findViewById(R.id.compete_show_username);
        created = getActivity().findViewById(R.id.compete_show_created);
        profilepic = getActivity().findViewById(R.id.compete_show_user_picture);
        cardViewAnswer = getActivity().findViewById(R.id.compete_myanswer);
        textDateAnswer = getActivity().findViewById(R.id.answer_date);
        answerCodeView = getActivity().findViewById(R.id.compete_answer_code_view);
        RespButton = getActivity().findViewById(R.id.compete_add_answer);
        addedcontent = getActivity().findViewById(R.id.compete_add_content);
        answerdlayout = getActivity().findViewById(R.id.compete_layout_answerd);
        editAnswer = getActivity().findViewById(R.id.compete_edit_answer);
        argsText=getActivity().findViewById(R.id.compete_args);
        run = getActivity().findViewById(R.id.compete_run);
        outputText=getActivity().findViewById(R.id.compete_response);
        loadCompetition();
        loadAnswer();
        attachAddListener();

    }


    public void loadCompetition() {
        CompetitionServices.getInstance().getCompetition(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                getContext(), competition.getId(), new ServerCallbacks() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                            competition = CompetitionServices.parse_(result.getJSONArray("resp").getJSONObject(0));
                            fillCompetition();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(VolleyError result) {
                        Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onWrong(JSONObject result) {
                        Toast.makeText(getContext(), "wrong", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void fillCompetition() {

        subject.setText(competition.getTitle());
        solved.setText(competition.getSolvedString());
        content.setText(competition.getContent());
        username.setText(competition.getUsername());
        created.setText(competition.getCreated_string());

        if (competition.getProfile_picture() != null)
            Picasso.with(getContext()).load(Uri.parse(competition.getProfile_picture())).into(profilepic);
        else {
            profilepic.setImageDrawable(UserProfileServices.getInstance().getEmptyProfimePicture(competition.getUsername()));
        }

        backtoCompete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    public void loadAnswer() {
        progressDialog.setMessage("Loading your answer, please wait.");
        progressDialog.show();
        CompetitionServices.getInstance().getCompetitionAnswer(FirebaseAuth.getInstance().getCurrentUser().getUid(), getContext(), competition.getId(), new ServerCallbacks() {
            @Override
            public void onSuccess(JSONObject result) {
                answerCodeView.setVisibility(View.VISIBLE);
                answerdlayout.setVisibility(View.VISIBLE);
                RespButton.setVisibility(View.GONE);
                addedcontent.setVisibility(View.GONE);
                try {
                    if (result.getJSONArray("resp").length() > 0) {
                        answer = CompetitionServices.parseAnswer_(result.getJSONArray("resp").getJSONObject(0));
                        //Toast.makeText(getContext(),answer.toString(),Toast.LENGTH_LONG).show();
                        fillanswer();
                    } else {
                        answerCodeView.setVisibility(View.GONE);
                        answerdlayout.setVisibility(View.GONE);
                        RespButton.setVisibility(View.VISIBLE);
                        addedcontent.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Not solved yet", Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onError(VolleyError result) {
                answerCodeView.setVisibility(View.GONE);
                answerdlayout.setVisibility(View.GONE);
                RespButton.setVisibility(View.GONE);
                addedcontent.setVisibility(View.GONE);
                Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onWrong(JSONObject result) {
                answerCodeView.setVisibility(View.GONE);
                answerdlayout.setVisibility(View.GONE);
                RespButton.setVisibility(View.VISIBLE);
                addedcontent.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "wrong", Toast.LENGTH_LONG).show();
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    public void fillanswer() {
        answer.setCreated(Calendar.getInstance());
        textDateAnswer.setText(answer.getCreated_string());
        addedcontent.setText(answer.getContent());
        //Toast.makeText(getContext(),answer.getContent(),Toast.LENGTH_LONG).show();
        if (answer.getContent() != null) {
            answerCodeView.setOptions(Options.Default.get(getContext())
                    .withLanguage("java")
                    .withCode(answer.getContent())
                    .withTheme(ColorTheme.DEFAULT));
        }
    }

    public void attachAddListener() {
        RespButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Saving your answer, please wait.");
                progressDialog.show();
                //add
                if (addedcontent.getText() != null)
                    if (addedcontent.getText().toString().trim().length() > 0) {
                        if (!editnow) {
                            answer = new CompetitionAnswer();
                            answer.setId_competition(competition.getId());
                            answer.setContent(addedcontent.getText().toString().replaceAll("[\r\n]", "\n"));
                            CompetitionServices.getInstance().addAnswer(getContext(), answer, FirebaseAuth.getInstance().getCurrentUser().getUid(), new ServerCallbacks() {
                                @Override
                                public void onSuccess(JSONObject result) {
                                    loadAnswer();
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                }

                                @Override
                                public void onError(VolleyError result) {
                                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                }

                                @Override
                                public void onWrong(JSONObject result) {
                                    Toast.makeText(getContext(), "Wrong", Toast.LENGTH_SHORT).show();
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        } else {
                            editnow = false;
                            answer.setContent(addedcontent.getText().toString().replaceAll("[\r\n]", "\n"));
                            CompetitionServices.getInstance().editAnswer(getContext(), answer, FirebaseAuth.getInstance().getCurrentUser().getUid(), new ServerCallbacks() {
                                @Override
                                public void onSuccess(JSONObject result) {
                                    answerCodeView.setVisibility(View.GONE);
                                    answerdlayout.setVisibility(View.GONE);
                                    RespButton.setVisibility(View.VISIBLE);
                                    addedcontent.setVisibility(View.VISIBLE);
                                    answer.setCreated(Calendar.getInstance());
                                    loadAnswer();
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                }

                                @Override
                                public void onError(VolleyError result) {
                                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                }

                                @Override
                                public void onWrong(JSONObject result) {
                                    Toast.makeText(getContext(), "Wrong", Toast.LENGTH_SHORT).show();
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        }
                    }
            }
        });

        editAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerCodeView.setVisibility(View.GONE);
                answerdlayout.setVisibility(View.GONE);
                RespButton.setVisibility(View.VISIBLE);
                addedcontent.setVisibility(View.VISIBLE);
                addedcontent.setText(answer.getContent());
                editnow = true;
            }
        });

        run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                run.setEnabled(false);
                progressDialog.setMessage("Running code, please wait.");
                progressDialog.show();
                final String s=addedcontent.getText().toString().replaceAll("[\r\n]", "\n");
               // s=s.replaceAll("\\s+","+");
                Map<String, String> m = new HashMap<String, String>();
                m.put("text", s);
                m.put("name", "ikotlinrun.kt");
                final JSONObject jsonBody = new JSONObject(m);
                m.clear();
                final JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonBody);
                JSONObject FullBody = new JSONObject();
                try {
                    FullBody.put("files", jsonArray);
                    FullBody.put("args", argsText.getText().toString());
                    CompetitionServices.getInstance().tryCode(getContext(), FullBody, new StringCallbacks() {
                        @Override
                        public void onSuccess(String result) {
                            run.setEnabled(true);
                            outputText.setVisibility(View.VISIBLE);
                            //Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                            JSONObject jsOResponse,subs;
                            JSONArray subsar;
                            String err="";
                            try {
                               jsOResponse =new JSONObject(result);
                                //Log.d("tryko",jsOResponse.toString());
                               if(jsOResponse.has("text")){
                                   outputText.setText(jsOResponse.get("text").toString().replaceAll("<[^>]+>", ""));
                                   outputText.setBackgroundColor(getActivity().getResources().getColor(R.color.success_background));
                               }
                                else {
                                   //Log.d("tryko",jsOResponse.toString());
                                   if(jsOResponse.has("errors")) {
                                       subs= (JSONObject) jsOResponse.get("errors");
                                       if (subs.has("ikotlinrun.kt")) {
                                            String line="",chr="",severity="",msg="";
                                            JSONObject ln;
                                           subsar= subs.getJSONArray("ikotlinrun.kt");
                                           subs=subsar.getJSONObject(0);
                                           if(subs.has("interval")) {
                                               JSONObject subsinterval= (JSONObject) subs.get("interval");
                                               if(subsinterval.has("start")) {
                                                   ln= (JSONObject) subsinterval.get("start");
                                                   line=ln.getString("line");
                                                   chr=ln.getString("ch");
                                               }
                                           }

                                           if(subs.has("message")) {
                                               msg=subs.getString("message");
                                           }

                                           if(subs.has("severity")){
                                               severity=subs.getString("severity");
                                           }

                                           //read intervals
                                           if(severity.length()>0) err = severity+" : \n";
                                           err = "Error : \n";

                                           if(msg.length()>0) err+="\n\tMessage  : "+msg;
                                           if(line.length()>0) err+="\n\tIn line : "+line;
                                           if(chr.length()>0) err+="\n\tCharacter: "+chr;

                                           outputText.setText(err);
                                       } else
                                           outputText.setText("Compilation error !");
                                       outputText.setBackgroundColor(getActivity().getResources().getColor(R.color.error_background));
                                   }
                               }
                            //Log.d("tryko",jsOResponse.toString());
                               if(jsOResponse.has("exception")){
                                   if(!jsOResponse.getString("exception").equals("null")) {
                                       String name="",method="",line="",cause="";
                                       subs= (JSONObject) jsOResponse.get("exception");
                                       if(subs.has("fullName")) name=subs.getString("fullName");
                                       if(subs.has("cause")) name=subs.getString("cause");
                                       if(subs.has("stackTrace")){
                                           subsar=subs.getJSONArray("stackTrace");
                                           subs=subsar.getJSONObject(0);
                                           if(subs.has("methodName")) method=subs.getString("methodName");
                                           if(subs.has("lineNumber")) line=subs.getString("lineNumber");
                                       }


                                       if(err.length()>0) err+="\n";

                                       err+="Exception : \n";
                                       if(name.length()>0) err+="\n\tReason(s) : "+name;
                                       if(method.length()>0)err+="\n\tMethod : "+method;
                                       if(line.length()>0)err+="\n\tIn line  : "+line;
                                       if(cause.length()>0)err+="\n\tCause   : "+cause;

                                       outputText.setText(err);
                                       outputText.setBackgroundColor(getActivity().getResources().getColor(R.color.error_background));
                                   }
                               }

                                Log.d("kotlinResponse",jsOResponse.toString());
                            } catch (JSONException e) {
                                outputText.setText("Parsing error !");
                                outputText.setBackgroundColor(getActivity().getResources().getColor(R.color.paper_background));
                            }



                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onError(VolleyError result) {
                            run.setEnabled(true);
                            outputText.setText("Please retry...");
                            outputText.setBackgroundColor(getActivity().getResources().getColor(R.color.paper_background));
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    });
                } catch (JSONException e) {
                    run.setEnabled(true);
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error while running on server\n Please report", Toast.LENGTH_SHORT).show();
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }

            }
        });

    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

}

