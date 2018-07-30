package amalhichri.androidprojects.com.kotlinlearning.fragments;


import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import amalhichri.androidprojects.com.kotlinlearning.services.CompetitionsServices;
import amalhichri.androidprojects.com.kotlinlearning.services.ServerCallbacks;
import amalhichri.androidprojects.com.kotlinlearning.services.StringCallbacks;
import amalhichri.androidprojects.com.kotlinlearning.utils.Statics;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.kbiakov.codeview.CodeView;
import io.github.kbiakov.codeview.adapters.Options;
import io.github.kbiakov.codeview.highlight.ColorTheme;


public class CompetitionFragment extends Fragment {

    private Competition competition;
    private CompetitionAnswer answer;
    private ProgressDialog progressDialog;
    private boolean editnow = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FirebaseAuth.getInstance().getCurrentUser().reload();
        return inflater.inflate(R.layout.fragment_competition, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        progressDialog = new ProgressDialog(getActivity());
        loadCompetition();
        loadAnswer();

        /** add answer btn **/
        getActivity().findViewById(R.id.competitionAddAnswer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Saving your answer, please wait.");
                progressDialog.show();
                if (((TextView) getActivity().findViewById(R.id.compete_add_content)).getText() != null)
                    if (((TextView) getActivity().findViewById(R.id.compete_add_content)).getText().toString().trim().length() > 0) {
                        if (!editnow) {
                            answer = new CompetitionAnswer();
                            answer.setCompetitionId(competition.getId());
                            answer.setContent(((TextView) getActivity().findViewById(R.id.compete_add_content)).getText().toString().replaceAll("[\r\n]", "\n"));
                            CompetitionsServices.getInstance().addAnswer(getContext(), answer, Statics.auth.getCurrentUser().getUid(), new ServerCallbacks() {
                                @Override
                                public void onSuccess(JSONObject result) {
                                    loadAnswer();
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                }

                                @Override
                                public void onError(VolleyError result) {
                                    Toast.makeText(getContext(), result.getClass().getName(), Toast.LENGTH_SHORT).show();
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                }

                                @Override
                                public void onWrong(JSONObject result) {
                                    Toast.makeText(getContext(), result.toString(), Toast.LENGTH_SHORT).show();
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        } else {
                            editnow = false;
                            answer.setContent(((TextView) getActivity().findViewById(R.id.compete_add_content)).getText().toString().replaceAll("[\r\n]", "\n"));
                            CompetitionsServices.getInstance().editAnswer(getContext(), answer, Statics.auth.getCurrentUser().getUid(), new ServerCallbacks() {
                                @Override
                                public void onSuccess(JSONObject result) {
                                    getActivity().findViewById(R.id.competitionAnswerCodeView).setVisibility(View.GONE);
                                    getActivity().findViewById(R.id.competitionAnswered).setVisibility(View.GONE);
                                    getActivity().findViewById(R.id.competitionAddAnswer).setVisibility(View.VISIBLE);
                                    getActivity().findViewById(R.id.compete_add_content).setVisibility(View.VISIBLE);
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

        /** edit answer btn **/
        getActivity().findViewById(R.id.competitionEditAnswer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().findViewById(R.id.competitionAnswerCodeView).setVisibility(View.GONE);
                getActivity().findViewById(R.id.competitionAnswered).setVisibility(View.GONE);
                getActivity().findViewById(R.id.competitionAddAnswer).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.compete_add_content).setVisibility(View.VISIBLE);
                ((TextView) getActivity().findViewById(R.id.compete_add_content)).setText(answer.getContent());
                editnow = true;
            }
        });

        /** compile code btn **/
        getActivity().findViewById(R.id.competitionRun).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().findViewById(R.id.competitionRun).setEnabled(false);
                progressDialog.setMessage("Running code, please wait.");
                progressDialog.show();
                Map<String, String> m = new HashMap<String, String>();
                m.put("text", ((TextView) getActivity().findViewById(R.id.compete_add_content)).getText().toString().replaceAll("[\r\n]", "\n"));
                m.put("name", "ikotlinrun.kt");
                final JSONArray jsonArray = new JSONArray();
                jsonArray.put(new JSONObject(m));
                JSONObject FullBody = new JSONObject();
                try {
                    FullBody.put("files", jsonArray);
                    FullBody.put("args", ((TextView) getActivity().findViewById(R.id.competitionArgs)).getText().toString());
                    CompetitionsServices.getInstance().compileCode(getContext(), FullBody, new StringCallbacks() {
                        @Override
                        public void onSuccess(String result) {
                            getActivity().findViewById(R.id.competitionRun).setEnabled(true);
                            getActivity().findViewById(R.id.competitionResponse).setVisibility(View.VISIBLE);
                            JSONObject jsOResponse, subs;
                            JSONArray subsar;
                            String err = "";
                            try {
                                jsOResponse = new JSONObject(result);
                                if (jsOResponse.has("text")) {
                                    ((TextView) getActivity().findViewById(R.id.competitionResponse)).setText(jsOResponse.get("text").toString().replaceAll("<[^>]+>", ""));
                                    getActivity().findViewById(R.id.competitionResponse).setBackgroundColor(getActivity().getResources().getColor(R.color.successBackground));
                                } else {
                                    if (jsOResponse.has("errors")) {
                                        subs = (JSONObject) jsOResponse.get("errors");
                                        if (subs.has("ikotlinrun.kt")) {
                                            String line = "", chr = "", severity = "", msg = "";
                                            JSONObject ln;
                                            subsar = subs.getJSONArray("ikotlinrun.kt");
                                            subs = subsar.getJSONObject(0);
                                            if (subs.has("interval")) {
                                                JSONObject subsinterval = (JSONObject) subs.get("interval");
                                                if (subsinterval.has("start")) {
                                                    ln = (JSONObject) subsinterval.get("start");
                                                    line = ln.getString("line");
                                                    chr = ln.getString("ch");
                                                }
                                            }

                                            if (subs.has("message")) {
                                                msg = subs.getString("message");
                                            }

                                            if (subs.has("severity")) {
                                                severity = subs.getString("severity");
                                            }

                                            //read intervals
                                            if (severity.length() > 0) err = severity + " : \n";
                                            err = "Error : \n";

                                            if (msg.length() > 0) err += "\n\tMessage  : " + msg;
                                            if (line.length() > 0) err += "\n\tIn line : " + line;
                                            if (chr.length() > 0) err += "\n\tCharacter: " + chr;

                                            ((TextView) getActivity().findViewById(R.id.competitionResponse)).setText(err);
                                        } else
                                            ((TextView) getActivity().findViewById(R.id.competitionResponse)).setText("Compilation error !");
                                        getActivity().findViewById(R.id.competitionResponse).setBackgroundColor(getActivity().getResources().getColor(R.color.errorBackground));
                                    }
                                }
                                if (jsOResponse.has("exception")) {
                                    if (!jsOResponse.getString("exception").equals("null")) {
                                        String name = "", method = "", line = "", cause = "";
                                        subs = (JSONObject) jsOResponse.get("exception");
                                        if (subs.has("fullName")) name = subs.getString("fullName");
                                        if (subs.has("cause")) name = subs.getString("cause");
                                        if (subs.has("stackTrace")) {
                                            subsar = subs.getJSONArray("stackTrace");
                                            subs = subsar.getJSONObject(0);
                                            if (subs.has("methodName"))
                                                method = subs.getString("methodName");
                                            if (subs.has("lineNumber"))
                                                line = subs.getString("lineNumber");
                                        }


                                        if (err.length() > 0) err += "\n";

                                        err += "Exception : \n";
                                        if (name.length() > 0) err += "\n\tReason(s) : " + name;
                                        if (method.length() > 0) err += "\n\tMethod : " + method;
                                        if (line.length() > 0) err += "\n\tIn line  : " + line;
                                        if (cause.length() > 0) err += "\n\tCause   : " + cause;

                                        ((TextView) getActivity().findViewById(R.id.competitionResponse)).setText(err);
                                        (getActivity().findViewById(R.id.competitionResponse)).setBackgroundColor(getActivity().getResources().getColor(R.color.errorBackground));
                                    }
                                }
                                } catch (JSONException e) {
                                ((TextView) getActivity().findViewById(R.id.competitionResponse)).setText("Parsing error !");
                                getActivity().findViewById(R.id.competitionResponse).setBackgroundColor(getActivity().getResources().getColor(R.color.paperBackground));
                            }


                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onError(VolleyError result) {
                            getActivity().findViewById(R.id.competitionRun).setEnabled(true);
                            ((TextView) getActivity().findViewById(R.id.competitionResponse)).setText("Please retry...");
                            getActivity().findViewById(R.id.competitionResponse).setBackgroundColor(getActivity().getResources().getColor(R.color.paperBackground));
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    });
                } catch (JSONException e) {
                    getActivity().findViewById(R.id.competitionRun).setEnabled(true);
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error while running on server\n Please report", Toast.LENGTH_SHORT).show();
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }

            }
        });

    }


    private void loadCompetition() {
        CompetitionsServices.getInstance().getCompetition(Statics.auth.getCurrentUser().getUid(),
                getContext(), competition.getId(), new ServerCallbacks() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                            competition = CompetitionsServices.jsonToCompetition(result.getJSONArray("resp").getJSONObject(0));

                            ((TextView) getActivity().findViewById(R.id.competitionSubject)).setText(competition.getTitle());
                            ((TextView) getActivity().findViewById(R.id.competeSolvedTxt)).setText(competition.getSolvedString());
                            ((TextView) getActivity().findViewById(R.id.competitionContent)).setText(competition.getContent());
                            ((TextView) getActivity().findViewById(R.id.competitionUsername)).setText(competition.getUsername());
                            ((TextView) getActivity().findViewById(R.id.competitionCreated)).setText(competition.getCreated_string());

                            if (competition.getProfilePicture() != null)
                                Picasso.with(getContext()).load(Uri.parse(competition.getProfilePicture())).into((CircleImageView) getActivity().findViewById(R.id.competitionUserPicture));
                            else {
                                ((ImageView) getActivity().findViewById(R.id.competitionUserPicture)).setImageDrawable(Statics.getPlaceholderProfilePic(competition.getUsername()));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(VolleyError result) {
                        Toast.makeText(getContext(), "COMPETITION ERROR " + result.toString(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onWrong(JSONObject result) {
                        Toast.makeText(getContext(), "wrong", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loadAnswer() {
        progressDialog.setMessage("Loading your answer, please wait.");
        progressDialog.show();
        CompetitionsServices.getInstance().getCompetitionAnswer(Statics.auth.getCurrentUser().getUid(), getContext(), competition.getId(), new ServerCallbacks() {
            @Override
            public void onSuccess(JSONObject result) {
                getActivity().findViewById(R.id.competitionAnswerCodeView).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.competitionAnswered).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.competitionAddAnswer).setVisibility(View.GONE);
                getActivity().findViewById(R.id.compete_add_content).setVisibility(View.GONE);
                try {
                    if (result.getJSONArray("resp").length() > 0) {
                        answer = CompetitionsServices.jsonToAnswer(result.getJSONArray("resp").getJSONObject(0));
                        answer.setCreated(Calendar.getInstance());
                        ((TextView) getActivity().findViewById(R.id.competitionAnswerDate)).setText(answer.getCreated_string());
                        ((TextView) getActivity().findViewById(R.id.compete_add_content)).setText(answer.getContent());
                        if (answer.getContent() != null) {
                            ((CodeView) getActivity().findViewById(R.id.competitionAnswerCodeView)).setOptions(Options.Default.get(getContext())
                                    .withLanguage("java")
                                    .withCode(answer.getContent())
                                    .withTheme(ColorTheme.DEFAULT));
                        }
                    } else {
                        getActivity().findViewById(R.id.competitionAnswerCodeView).setVisibility(View.GONE);
                        getActivity().findViewById(R.id.competitionAnswered).setVisibility(View.GONE);
                        getActivity().findViewById(R.id.competitionAddAnswer).setVisibility(View.VISIBLE);
                        getActivity().findViewById(R.id.compete_add_content).setVisibility(View.VISIBLE);
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
                getActivity().findViewById(R.id.competitionAnswerCodeView).setVisibility(View.GONE);
                getActivity().findViewById(R.id.competitionAnswered).setVisibility(View.GONE);
                getActivity().findViewById(R.id.competitionAddAnswer).setVisibility(View.GONE);
                getActivity().findViewById(R.id.compete_add_content).setVisibility(View.GONE);
                Toast.makeText(getContext(), "ANSWSER ERROR " + competition.getId(), Toast.LENGTH_LONG).show();
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onWrong(JSONObject result) {
                getActivity().findViewById(R.id.competitionAnswerCodeView).setVisibility(View.GONE);
                getActivity().findViewById(R.id.competitionAnswered).setVisibility(View.GONE);
                getActivity().findViewById(R.id.competitionAddAnswer).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.compete_add_content).setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "wrong", Toast.LENGTH_LONG).show();
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

}
