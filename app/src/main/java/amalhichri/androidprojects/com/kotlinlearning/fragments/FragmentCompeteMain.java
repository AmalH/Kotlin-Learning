package amalhichri.androidprojects.com.kotlinlearning.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.firebase.auth.FirebaseAuth;

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
import amalhichri.androidprojects.com.kotlinlearning.services.CompetitionServices;
import amalhichri.androidprojects.com.kotlinlearning.services.ServerCallbacks;
import amalhichri.androidprojects.com.kotlinlearning.utils.Configuration;

public class FragmentCompeteMain extends Fragment {

    MultiStateToggleButton toggle_view;
    Spinner orderSpinner;
    Button bL1,bL2,bL3,bL4,bL5,bL6;
    RecyclerView competitionsRecyclerView,answersRecyclerView;
    SwipeRefreshLayout competeSwipeRefresh,competeAnswerSwipeRefresh;

    FloatingActionButton addButton;

    static int level=1;
    int order=1;
    int toggle=0;
    int lastToggle = -1;
    int lastOrder = -1;

    int loaded_length_competition=0,loaded_length_answers=0;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    boolean loading;

    RecyclerView.LayoutManager layoutCompetition,layoutAnswer;
    CompetitionAdapter competitionAdapter;
    CompetitionAnswerAdapter answersAdapter;

    ArrayList<Competition> competitionList;
    ArrayList<CompetitionAnswer> answersList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_compete_main, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        toggle_view = getActivity().findViewById(R.id.compete_toggle);
        orderSpinner=getActivity().findViewById(R.id.compete_orderby);
        bL1=getActivity().findViewById(R.id.compete_level_1);
        bL2=getActivity().findViewById(R.id.compete_level_2);
        bL3=getActivity().findViewById(R.id.compete_level_3);
        bL4=getActivity().findViewById(R.id.compete_level_4);
        bL5=getActivity().findViewById(R.id.compete_level_5);
        bL6=getActivity().findViewById(R.id.compete_level_6);
        competitionsRecyclerView = getActivity().findViewById(R.id.compete_competitions);
        answersRecyclerView = getActivity().findViewById(R.id.compete_answers);
        competeSwipeRefresh=getActivity().findViewById(R.id.compete_swipeRefresh);
        competeAnswerSwipeRefresh=getActivity().findViewById(R.id.competeanswer_swipeRefresh);
        addButton=getActivity().findViewById(R.id.compete_add);

        toggle_view.enableMultipleChoice(false);
        toggle_view.setValue(0);
        bL1.setBackgroundColor(getContext().getResources().getColor(R.color.base_color_2));

        DefaultItemAnimator animator = new DefaultItemAnimator() {
            @Override
            public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder) {
                return true;
            }
        };
        competitionsRecyclerView.setItemAnimator(animator);
        //answersRecyclerView.setItemAnimator(animator);

        layoutCompetition = new LinearLayoutManager(getContext());
        layoutAnswer = new LinearLayoutManager(getContext());
        competitionsRecyclerView.setLayoutManager(layoutCompetition);
        answersRecyclerView.setLayoutManager(layoutAnswer);
        competitionList=new ArrayList<>();
        answersList=new ArrayList<>();

        /** temp */
        competitionAdapter = new CompetitionAdapter(new ArrayList<Competition>(),getActivity());
        answersAdapter = new CompetitionAnswerAdapter(new ArrayList<CompetitionAnswer>(),getActivity());
        competitionsRecyclerView.setAdapter(competitionAdapter);
        answersRecyclerView.setAdapter(answersAdapter);

        attachSwipeRefreshListener();
        attachLevelButtonsListeners();
        attachToggleListener();
        attachspinnerListener();
        attachaddListener();
        attachcompetitionScrollListener();
        attachAnswerScrollListener();

    }

    public synchronized void LoadList(){
        if (Configuration.isOnline(getContext())){

            switch (toggle){
                case 0:
                    competitionsRecyclerView.setVisibility(View.VISIBLE);
                    answersRecyclerView.setVisibility(View.GONE);
                    competeSwipeRefresh.setRefreshing(true);
                    if(loaded_length_competition==0){
                        competitionsRecyclerView.removeAllViews();
                        competitionList.clear();
                    }
                    CompetitionServices.getInstance().getCompetitions(FirebaseAuth.getInstance().getCurrentUser().getUid(), getContext(), loaded_length_competition, level, order, new ServerCallbacks() {
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
                                    //Log.d("compet",CompetitionServices.parse_(array.getJSONObject(i)).toString());
                                    competitionList.add(CompetitionServices.parse_(array.getJSONObject(i)));
                                } catch (JSONException e) {
                                    Toast.makeText(getContext(),"Application error while loading competition , please report", Toast.LENGTH_SHORT).show();
                                    goShow=false;
                                }
                            }
                            /** All the work will be here **/
                            if(goShow) {
                                competeAnswerSwipeRefresh.setVisibility(View.GONE);
                                competeSwipeRefresh.setVisibility(View.VISIBLE);
                                if(loaded_length_competition==0){
                                    //Log.d("loaded",loaded_length_competition+"new load");
                                    competitionAdapter=new CompetitionAdapter(competitionList,getContext());
                                    competitionsRecyclerView.setAdapter(competitionAdapter);
                                }
                                else{
                                    //Log.d("loaded",loaded_length_competition+"old load : "+loaded_length_competition+"     "+forumRececyclerView+"   "+adapter);
                                    if(competitionAdapter==null){
                                        competitionAdapter=new CompetitionAdapter(competitionList,getContext());
                                        competitionsRecyclerView.setAdapter(competitionAdapter);
                                    }
                                    else
                                        competitionAdapter.notifyDataSetChanged();
                                }
                                //addCalculated
                                //Log.d("restult",array.length()+" #  "+loaded_length_competition);
                                if(loaded_length_competition==0) loaded_length_competition+=array.length();
                                else
                                    loaded_length_competition+=10;
                            }
                            competeAnswerSwipeRefresh.setRefreshing(false);
                            competeSwipeRefresh.setRefreshing(false);
                            loading=false;
                            enableLevels();
                        }

                        @Override
                        public void onError(VolleyError result) {
                            loading=false;
                            competeAnswerSwipeRefresh.setRefreshing(false);
                            competeSwipeRefresh.setRefreshing(false);
                            enableLevels();
                        }

                        @Override
                        public void onWrong(JSONObject result) {
                            loading=false;
                            competeAnswerSwipeRefresh.setRefreshing(false);
                            competeSwipeRefresh.setRefreshing(false);
                            enableLevels();
                        }
                    });

                    break;
                case 1:
                    competitionsRecyclerView.setVisibility(View.GONE);
                    answersRecyclerView.setVisibility(View.VISIBLE);
                    competeAnswerSwipeRefresh.setRefreshing(true);
                   if(loaded_length_answers==0){
                       answersRecyclerView.removeAllViews();
                       answersList.clear();
                    }
                    CompetitionServices.getInstance().getCompetitionsAnswers(FirebaseAuth.getInstance().getCurrentUser().getUid(), getContext(), loaded_length_answers, level, new ServerCallbacks() {
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
                           // Log.d("loaded",result+"");Log.d("loaded",""+loaded_length_answers);
                            //Log.d("loaded",array.toString());
                            if(array.length()==0) goShow=false;
                            //Log.d("loaded",goShow+"");
                            for(int i = 0 ; i < array.length() ; i++){
                                try {
                                    /** parse forum and add it to the arraylist**/
                                    //Log.d("compet",CompetitionServices.parseAnswer_(array.getJSONObject(i)).toString());
                                    answersList.add(CompetitionServices.parseAnswer_(array.getJSONObject(i)));
                                } catch (JSONException e) {
                                    Toast.makeText(getContext(),"Application error while loading competition , please report", Toast.LENGTH_SHORT).show();
                                    goShow=false;
                                }
                            }
                            //Log.d("loaded",goShow+"");
                            /** All the work will be here **/
                            if(goShow) {
                                competeAnswerSwipeRefresh.setVisibility(View.VISIBLE);
                                competeSwipeRefresh.setVisibility(View.GONE);
                                if(loaded_length_answers==0){
                                    //Log.d("loaded",answersList.toString());
                                    answersAdapter=new CompetitionAnswerAdapter(answersList,getContext());
                                    answersRecyclerView.setAdapter(answersAdapter);
                                }
                                else{
                                    //Log.d("loaded",loaded_length_competition+"old load : "+loaded_length_competition+"     "+forumRececyclerView+"   "+adapter);
                                    if(answersAdapter==null){
                                        answersAdapter=new CompetitionAnswerAdapter(answersList,getContext());
                                        answersRecyclerView.setAdapter(answersAdapter);
                                    }
                                    else
                                        answersAdapter.notifyDataSetChanged();
                                }
                                //addCalculated
                                if(loaded_length_answers==0) loaded_length_answers+=array.length();
                                else
                                    loaded_length_answers+=10;
                            }
                            competeAnswerSwipeRefresh.setRefreshing(false);
                            competeSwipeRefresh.setRefreshing(false);
                            loading=false;
                            enableLevels();
                        }

                        @Override
                        public void onError(VolleyError result) {
                            loading=false;
                            competeAnswerSwipeRefresh.setRefreshing(false);
                            competeSwipeRefresh.setRefreshing(false);
                            enableLevels();
                        }

                        @Override
                        public void onWrong(JSONObject result) {
                            loading=false;
                            competeAnswerSwipeRefresh.setRefreshing(false);
                            competeSwipeRefresh.setRefreshing(false);
                            enableLevels();
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

    public void attachSwipeRefreshListener(){
        competeSwipeRefresh.setColorSchemeColors(
                getContext().getResources().getColor(R.color.md_material_blue_600),
                getContext().getResources().getColor(R.color.refresh_progress_2),
                getContext().getResources().getColor(R.color.refresh_progress_3));
        competeAnswerSwipeRefresh.setColorSchemeColors(
                getContext().getResources().getColor(R.color.md_material_blue_600),
                getContext().getResources().getColor(R.color.refresh_progress_2),
                getContext().getResources().getColor(R.color.refresh_progress_3));

        competeSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Configuration.isOnline(getContext())){
                    loaded_length_competition=0;
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
                    loaded_length_answers=0;
                    LoadList();
                }
                else
                {
                    competeAnswerSwipeRefresh.setRefreshing(false);
                    loading=false;
                }
            }
        });

    }


    public void attachspinnerListener(){
        orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                order=i+1;
                if(toggle==0 && lastOrder!= order){
                    loaded_length_competition=0;
                    loaded_length_answers=0;
                    LoadList();
                    lastOrder = order;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void attachToggleListener(){
        toggle_view.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                toggle=value;
                loaded_length_competition=0;
                loaded_length_answers=0;
                if(value==1)
                    orderSpinner.setVisibility(View.GONE);
                else
                    orderSpinner.setVisibility(View.VISIBLE);
                disabbleLevels();
                if(lastToggle!=value) {
                    LoadList();
                    lastToggle= value;
                }
            }
        });
    }

    public void attachLevelButtonsListeners(){
        bL1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disabbleLevels();
                level=1;

                bL2.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                bL3.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                bL4.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                bL5.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                bL6.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));

                bL1.setBackgroundColor(getContext().getResources().getColor(R.color.base_color_2));
                loaded_length_competition=0;
                loaded_length_answers=0;
                LoadList();
            }
        });

        bL2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disabbleLevels();
                level=2;

                bL1.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                bL3.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                bL4.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                bL5.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                bL6.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));

                bL2.setBackgroundColor(getContext().getResources().getColor(R.color.base_color_2));
                loaded_length_competition=0;
                loaded_length_answers=0;
                LoadList();
            }
        });

        bL3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disabbleLevels();
                level=3;

                bL2.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                bL1.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                bL4.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                bL5.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                bL6.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));

                bL3.setBackgroundColor(getContext().getResources().getColor(R.color.base_color_2));
                loaded_length_competition=0;
                loaded_length_answers=0;
                LoadList();
            }
        });

        bL4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disabbleLevels();
                level=4;

                bL2.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                bL3.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                bL1.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                bL5.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                bL6.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));

                bL4.setBackgroundColor(getContext().getResources().getColor(R.color.base_color_2));
                loaded_length_competition=0;
                loaded_length_answers=0;
                LoadList();
            }
        });

        bL5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disabbleLevels();
                level=5;

                bL2.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                bL3.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                bL4.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                bL1.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                bL6.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));

                bL5.setBackgroundColor(getContext().getResources().getColor(R.color.base_color_2));
                loaded_length_competition=0;
                loaded_length_answers=0;
                LoadList();
            }
        });

        bL6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disabbleLevels();
                level=6;

                bL2.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                bL3.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                bL4.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                bL5.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                bL1.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));

                bL6.setBackgroundColor(getContext().getResources().getColor(R.color.base_color_2));
                loaded_length_competition=0;
                loaded_length_answers=0;
                LoadList();
            }
        });
    }

    public void attachcompetitionScrollListener(){
            loading =false;
            competitionsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if(dy > 0) //check for scroll down
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
                                    //Log.d("loaded",loaded_length+"by search");
                                    LoadList();
                                // //Log.d("loading","loading_more");
                            }
                        }
                    }
                }
            });
    }

    public void attachAnswerScrollListener(){
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

    public void attachaddListener(){
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.root_compete,new FragmentCompeteAdd())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }


    public void disabbleLevels(){
        bL1.setEnabled(false);
        bL2.setEnabled(false);
        bL3.setEnabled(false);
        bL4.setEnabled(false);
        bL5.setEnabled(false);
        bL6.setEnabled(false);
    }

    public void enableLevels(){
        bL1.setEnabled(true);
        bL2.setEnabled(true);
        bL3.setEnabled(true);
        bL4.setEnabled(true);
        bL5.setEnabled(true);
        bL6.setEnabled(true);
    }


    @Override
    public void onResume() {
        super.onResume();
        loaded_length_answers=0;
        loaded_length_competition=0;
        LoadList();
        colorcurrentLevel();

    }

    public void colorcurrentLevel(){
        switch (level){
            case 1:

                    bL2.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                    bL3.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                    bL4.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                    bL5.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                    bL6.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));

                    bL1.setBackgroundColor(getContext().getResources().getColor(R.color.base_color_2));
            break;

            case 2:

                    bL1.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                    bL3.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                    bL4.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                    bL5.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                    bL6.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));

                    bL2.setBackgroundColor(getContext().getResources().getColor(R.color.base_color_2));
                    loaded_length_competition=0;
                    loaded_length_answers=0;
             break;
            case 3:

                    bL2.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                    bL1.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                    bL4.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                    bL5.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                    bL6.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));

                    bL3.setBackgroundColor(getContext().getResources().getColor(R.color.base_color_2));
                    loaded_length_competition=0;
                    loaded_length_answers=0;
             break;
            case 4:
                    bL2.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                    bL3.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                    bL1.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                    bL5.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                    bL6.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));

                    bL4.setBackgroundColor(getContext().getResources().getColor(R.color.base_color_2));
                    loaded_length_competition=0;
                    loaded_length_answers=0;
            break;
            case 5:

                    bL2.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                    bL3.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                    bL4.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                    bL1.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                    bL6.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));

                    bL5.setBackgroundColor(getContext().getResources().getColor(R.color.base_color_2));
                    loaded_length_competition=0;
                    loaded_length_answers=0;
             break;

            case 6:
                    bL2.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                    bL3.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                    bL4.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                    bL5.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));
                    bL1.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_80));

                    bL6.setBackgroundColor(getContext().getResources().getColor(R.color.base_color_2));
                    loaded_length_competition=0;
                    loaded_length_answers=0;
             break;
        }
    }
}
