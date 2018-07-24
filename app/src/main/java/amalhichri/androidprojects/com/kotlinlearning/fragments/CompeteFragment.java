package amalhichri.androidprojects.com.kotlinlearning.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.adapters.CompetitionAdapter;
import amalhichri.androidprojects.com.kotlinlearning.adapters.CompetitionAnswerAdapter;
import amalhichri.androidprojects.com.kotlinlearning.models.Competition;
import amalhichri.androidprojects.com.kotlinlearning.models.CompetitionAnswer;
import amalhichri.androidprojects.com.kotlinlearning.services.CompetitionsServices;
import amalhichri.androidprojects.com.kotlinlearning.services.ServerCallbacks;
import amalhichri.androidprojects.com.kotlinlearning.utils.Configuration;
import amalhichri.androidprojects.com.kotlinlearning.utils.Statics;

public class CompeteFragment extends Fragment {


    private RecyclerView competitionsRecyclerView,answersRecyclerView;
    private SwipeRefreshLayout competeSwipeRefresh,competeAnswerSwipeRefresh;
    private RecyclerView.LayoutManager layoutCompetition,layoutAnswer;
    private CompetitionAdapter competitionAdapter;
    private CompetitionAnswerAdapter answersAdapter;

    private int order=1,toggle=0,lastToggle = -1,lastOrder = -1, loadedLengthCompetition =0, loadedLengthAnswers =0,pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading;

    private ArrayList<Competition> competitionList;
    private ArrayList<CompetitionAnswer> answersList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compete, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        competitionsRecyclerView = getActivity().findViewById(R.id.competeCompetitions);
        answersRecyclerView = getActivity().findViewById(R.id.competeAnswers);
        competeSwipeRefresh=getActivity().findViewById(R.id.competeSwipeRefresh);
        competeAnswerSwipeRefresh=getActivity().findViewById(R.id.competeAnswerSwipeRefresh);

        ((MultiStateToggleButton)getActivity().findViewById(R.id.competeToggleBtn)).enableMultipleChoice(false);
        ((MultiStateToggleButton)getActivity().findViewById(R.id.competeToggleBtn)).setValue(0);

        DefaultItemAnimator animator = new DefaultItemAnimator() {
            @Override
            public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder) {
                return true;
            }
        };
        competitionsRecyclerView.setItemAnimator(animator);
        layoutCompetition = new LinearLayoutManager(getContext());
        layoutAnswer = new LinearLayoutManager(getContext());
        competitionsRecyclerView.setLayoutManager(layoutCompetition);
        answersRecyclerView.setLayoutManager(layoutAnswer);
        competitionList=new ArrayList<>();
        answersList=new ArrayList<>();

        competitionAdapter = new CompetitionAdapter(new ArrayList<Competition>(),getActivity());
        answersAdapter = new CompetitionAnswerAdapter(new ArrayList<CompetitionAnswer>(),getActivity());
        competitionsRecyclerView.setAdapter(competitionAdapter);
        answersRecyclerView.setAdapter(answersAdapter);

        /** swipe refresh layout **/
        competeSwipeRefresh.setColorSchemeColors(
                getContext().getResources().getColor(R.color.baseColor1),
                getContext().getResources().getColor(R.color.baseColor2));
        competeSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Configuration.isOnline(getContext())){
                    loadedLengthCompetition =0;
                    LoadList();
                }
                else
                {
                    competeSwipeRefresh.setRefreshing(false);
                    loading=false;
                }
            }
        });
        competeAnswerSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Configuration.isOnline(getContext())){
                    loadedLengthAnswers =0;
                    LoadList();
                }
                else
                {
                    competeAnswerSwipeRefresh.setRefreshing(false);
                    loading=false;
                }
            }
        });

        /** compete answer toggle **/
        ((MultiStateToggleButton)getActivity().findViewById(R.id.competeToggleBtn)).setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                toggle=value;
                loadedLengthCompetition =0;
                loadedLengthAnswers =0;
                if(value==1)
                    getActivity().findViewById(R.id.competeOrderbySpinner).setVisibility(View.GONE);
                else
                    getActivity().findViewById(R.id.competeOrderbySpinner).setVisibility(View.VISIBLE);
                if(lastToggle!=value) {
                    LoadList();
                    lastToggle= value;
                }
            }
        });
       /** orderby spinner **/
        ((Spinner)getActivity().findViewById(R.id.competeOrderbySpinner)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                order=i+1;
                if(toggle==0 && lastOrder!= order){
                    loadedLengthCompetition =0;
                    loadedLengthAnswers =0;
                    LoadList();
                    lastOrder = order;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /** add contest **/
        getActivity().findViewById(R.id.competeAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.root_compete,new AddCompetitionFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        /** recyclerView onScroll **/
        loading =false;
        competitionsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0)
                {
                    visibleItemCount = layoutCompetition.getChildCount();
                    totalItemCount = layoutCompetition.getItemCount();
                    pastVisiblesItems = ((LinearLayoutManager)layoutCompetition).findFirstVisibleItemPosition();

                    if (Configuration.isOnline(getContext())&& !loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount-4)
                        {
                            loading=true;
                            if(competitionAdapter!=null)

                                LoadList();

                        }
                    }
                }
            }
        });


        /** answer recyclerView scroll listener **/
        loading =false;
        answersRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = layoutAnswer.getChildCount();
                    totalItemCount = layoutAnswer.getItemCount();
                    pastVisiblesItems = ((LinearLayoutManager)layoutAnswer).findFirstVisibleItemPosition();

                    if (Configuration.isOnline(getContext())&& !loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount-4)
                        {
                            loading=true;
                            if(answersAdapter!=null)
                                //Log.d("loaded",loaded_length+"by search");
                                LoadList();
                            // //Log.d("loading","loading_more");
                        }
                    }
                }
            }
        });

    }

    private synchronized void LoadList(){
        if (Configuration.isOnline(getContext())){

            switch (toggle){
                case 0:
                    competitionsRecyclerView.setVisibility(View.VISIBLE);
                    answersRecyclerView.setVisibility(View.GONE);
                    competeSwipeRefresh.setRefreshing(true);
                    if(loadedLengthCompetition ==0){
                        competitionsRecyclerView.removeAllViews();
                        competitionList.clear();
                    }
                    CompetitionsServices.getInstance().getCompetitions(Statics.auth.getCurrentUser().getUid(), getContext(), loadedLengthCompetition, 1, order, new ServerCallbacks() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            boolean showLoadedData =true;
                            try {
                            if((result.getJSONArray("competitions")).length()==0) showLoadedData =false;
                            for(int i = 0 ; i < (result.getJSONArray("competitions")).length() ; i++){
                                try {
                                    competitionList.add(CompetitionsServices.jsonToCompetition((result.getJSONArray("competitions")).getJSONObject(i)));
                                } catch (JSONException e) {
                                    Toast.makeText(getContext(),"Application error while loading competition , please report", Toast.LENGTH_SHORT).show();
                                    showLoadedData =false;
                                }
                            }
                            if(showLoadedData) {
                                competeAnswerSwipeRefresh.setVisibility(View.GONE);
                                competeSwipeRefresh.setVisibility(View.VISIBLE);
                                if(loadedLengthCompetition ==0){
                                    competitionAdapter=new CompetitionAdapter(competitionList,getContext());
                                    competitionsRecyclerView.setAdapter(competitionAdapter);
                                }
                                else{
                                    if(competitionAdapter==null){
                                        competitionAdapter=new CompetitionAdapter(competitionList,getContext());
                                        competitionsRecyclerView.setAdapter(competitionAdapter);
                                    }
                                    else
                                        competitionAdapter.notifyDataSetChanged();
                                }
                                if(loadedLengthCompetition ==0) loadedLengthCompetition +=(result.getJSONArray("competitions")).length();
                                else
                                    loadedLengthCompetition +=10;
                            }
                            competeAnswerSwipeRefresh.setRefreshing(false);
                            competeSwipeRefresh.setRefreshing(false);
                            loading=false;
                            } catch (JSONException e) {
                                Toast.makeText(getContext(),"Server error while loading competitions , please report", Toast.LENGTH_SHORT).show();
                                //showLoadedData =false;
                            }
                        }

                        @Override
                        public void onError(VolleyError result) {
                            loading=false;
                            competeAnswerSwipeRefresh.setRefreshing(false);
                            competeSwipeRefresh.setRefreshing(false);
                        }

                        @Override
                        public void onWrong(JSONObject result) {
                            loading=false;
                            competeAnswerSwipeRefresh.setRefreshing(false);
                            competeSwipeRefresh.setRefreshing(false);
                        }
                    });

                    break;
                case 1:
                    competitionsRecyclerView.setVisibility(View.GONE);
                    answersRecyclerView.setVisibility(View.VISIBLE);
                    competeAnswerSwipeRefresh.setRefreshing(true);
                   if(loadedLengthAnswers ==0){
                       answersRecyclerView.removeAllViews();
                       answersList.clear();
                    }
                    CompetitionsServices.getInstance().getCompetitionsAnswers(Statics.auth.getCurrentUser().getUid(), getContext(), loadedLengthAnswers,0, new ServerCallbacks() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            boolean goShow=true;
                            JSONArray array = new JSONArray();
                            try {
                                array = result.getJSONArray("competitions");
                            } catch (JSONException e) {
                                Toast.makeText(getContext(),"Server error while loading competitions , please report", Toast.LENGTH_SHORT).show();
                                goShow=false;
                            }

                            if(array.length()==0) goShow=false;

                            for(int i = 0 ; i < array.length() ; i++){
                                try {
                                    /** parse forum and add it to the arraylist**/

                                    answersList.add(CompetitionsServices.jsonToAnswer(array.getJSONObject(i)));
                                } catch (JSONException e) {
                                    Toast.makeText(getContext(),"Application error while loading competition , please report", Toast.LENGTH_SHORT).show();
                                    goShow=false;
                                }
                            }

                            /** All the work will be here **/
                            if(goShow) {
                                competeAnswerSwipeRefresh.setVisibility(View.VISIBLE);
                                competeSwipeRefresh.setVisibility(View.GONE);
                                if(loadedLengthAnswers ==0){
                                    answersAdapter=new CompetitionAnswerAdapter(answersList,getContext());
                                    answersRecyclerView.setAdapter(answersAdapter);
                                }
                                else{
                                    if(answersAdapter==null){
                                        answersAdapter=new CompetitionAnswerAdapter(answersList,getContext());
                                        answersRecyclerView.setAdapter(answersAdapter);
                                    }
                                    else
                                        answersAdapter.notifyDataSetChanged();
                                }


                                if(loadedLengthAnswers ==0) loadedLengthAnswers +=array.length();
                                else
                                    loadedLengthAnswers +=10;
                            }
                            competeAnswerSwipeRefresh.setRefreshing(false);
                            competeSwipeRefresh.setRefreshing(false);
                            loading=false;
                        }

                        @Override
                        public void onError(VolleyError result) {
                            loading=false;
                            competeAnswerSwipeRefresh.setRefreshing(false);
                            competeSwipeRefresh.setRefreshing(false);
                        }

                        @Override
                        public void onWrong(JSONObject result) {
                            loading=false;
                            competeAnswerSwipeRefresh.setRefreshing(false);
                            competeSwipeRefresh.setRefreshing(false);
                        }
                    });
                    break;
            }
        }
        else{
            loading=false;
            competeAnswerSwipeRefresh.setRefreshing(false);
            competeSwipeRefresh.setRefreshing(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadedLengthAnswers =0;
        loadedLengthCompetition =0;
        LoadList();
    }

}
