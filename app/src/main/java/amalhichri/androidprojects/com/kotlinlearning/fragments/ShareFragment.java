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
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.adapters.ShareListAdapter;
import amalhichri.androidprojects.com.kotlinlearning.models.ForumQuestion;
import amalhichri.androidprojects.com.kotlinlearning.services.ForumServices;
import amalhichri.androidprojects.com.kotlinlearning.services.ServerCallbacks;
import amalhichri.androidprojects.com.kotlinlearning.utils.Configuration;


public class ShareFragment extends Fragment {

    private RecyclerView forumQustsRecyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ForumQuestion> forumQuestionsList;
    private static int nbOfLoadedQuestions;
    private boolean loading = true, loadWhileEmpty =true;
    private int pastVisiblesItems, visibleItemCount,orderby, totalItemCount;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nbOfLoadedQuestions =0;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        forumQustsRecyclerView = getActivity().findViewById(R.id.recyclerViewShare);
        layoutManager = new LinearLayoutManager(getContext());
        forumQustsRecyclerView.setLayoutManager(layoutManager);
        forumQuestionsList =new ArrayList<>();
        nbOfLoadedQuestions =0;
        DefaultItemAnimator animator = new DefaultItemAnimator() {
            @Override
            public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder) {
                return true;
            }
        };
        forumQustsRecyclerView.setItemAnimator(animator);
        adapter = new ShareListAdapter(new ArrayList<ForumQuestion>(),getActivity());
        forumQustsRecyclerView.setAdapter(adapter);


       /** adding a forum question **/
        getActivity().findViewById(R.id.addFormQuestionBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.root_share_fragment,new AddForumFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        /** ordering forum questions **/
        ((Spinner)getActivity().findViewById(R.id.orderbySpinnerShare)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                orderby=i+1;
                //load forum
                if(Configuration.isOnline(getContext()))
                {
                    loadForumQuestions(0,((SearchView)getActivity().findViewById(R.id.searchViewShare)).getQuery().toString());
                }
                else
                    forumQustsRecyclerView.setVisibility(View.GONE);
                getActivity().findViewById(R.id.noConnectionTextView).setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /** searchview **/
        ((SearchView)getActivity().findViewById(R.id.searchViewShare)).setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(Configuration.isOnline(getContext())){

                    loadForumQuestions(0,s.trim().toLowerCase());

                    loadWhileEmpty =true;
                }

                else
                    Toast.makeText(getContext(),"No connection",Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(Configuration.isOnline(getContext())&& !s.trim().isEmpty() && loadWhileEmpty){
                    forumQuestionsList.clear();
                    nbOfLoadedQuestions =0;
                    forumQustsRecyclerView.removeAllViews();
                    loadForumQuestions(0,s.trim().toLowerCase());
                    loadWhileEmpty =false;
                }
                else{
                    loading=false;
                }
                return true;
            }
        });

        /** swipe refresh **/
        ((SwipeRefreshLayout)getActivity().findViewById(R.id.swipeRefreshShare)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Configuration.isOnline(getContext())){
                    nbOfLoadedQuestions =0;
                    loadForumQuestions(0,((SearchView)getActivity().findViewById(R.id.searchViewShare)).getQuery().toString());
                }
                else
                {
                    ((SwipeRefreshLayout)getActivity().findViewById(R.id.swipeRefreshShare)).setRefreshing(false);
                    loading=false;
                }
            }
        });
        ((SwipeRefreshLayout)getActivity().findViewById(R.id.swipeRefreshShare)).setColorSchemeColors(
                getContext().getResources().getColor(R.color.base_color_2),
                getContext().getResources().getColor(R.color.base_color_1));

        /** scroll listener for the recyclerView **/
        loading =false;
        forumQustsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0)
                {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();

                    if (Configuration.isOnline(getContext())&& !loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount-4)
                        {
                            loading=true;
                            if(adapter!=null) {
                                loadForumQuestions(nbOfLoadedQuestions, ((SearchView)getActivity().findViewById(R.id.searchViewShare)).getQuery().toString());
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_share, container, false);
        nbOfLoadedQuestions =0;
        return v;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if(Configuration.isOnline(getContext()) && forumQustsRecyclerView.getChildCount()==0) {
                nbOfLoadedQuestions = 0;
                getActivity().findViewById(R.id.noConnectionTextView).setVisibility(View.GONE);
                loadForumQuestions(0, ((SearchView)getActivity().findViewById(R.id.searchViewShare)).getQuery().toString());

            }
        }
    }


    /** this method load forum items and push it to showList **/
    public synchronized void loadForumQuestions(final int start_at, String query){
        if(start_at==0)
        {
            nbOfLoadedQuestions =0;
            forumQustsRecyclerView.removeAllViews();
            forumQuestionsList.clear();
        }
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            ((SwipeRefreshLayout)getActivity().findViewById(R.id.swipeRefreshShare)).setRefreshing(true);
            ForumServices.getInstance().getTopForums(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    getActivity().getApplicationContext(),start_at,query,orderby,
                    new ServerCallbacks() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            boolean showLoadedData=true;
                            JSONArray array = new JSONArray();
                            try {
                                array = result.getJSONArray("forum");
                            } catch (JSONException e) {
                                Toast.makeText(getContext(),"Server error while loading forum , please report",Toast.LENGTH_SHORT).show();
                                showLoadedData=false;
                            }
                            for(int i = 0 ; i < array.length() ; i++){
                                try {
                                    /** parse forum and add it to the arraylist**/
                                    forumQuestionsList.add(ForumServices.parse_(array.getJSONObject(i)));
                                } catch (JSONException e) {
                                    Toast.makeText(getContext(),"Application error while loading forum , please report",Toast.LENGTH_SHORT).show();
                                    showLoadedData=false;
                                }
                            }
                            /** if there is data to display **/
                            if(showLoadedData) {
                                getActivity().findViewById(R.id.noConnectionTextView).setVisibility(View.GONE);
                                if(nbOfLoadedQuestions ==0){
                                    adapter=new ShareListAdapter(forumQuestionsList,getContext());
                                    forumQustsRecyclerView.setAdapter(adapter);
                                } else{
                                    if(adapter==null){
                                        adapter=new ShareListAdapter(forumQuestionsList,getContext());
                                        forumQustsRecyclerView.setAdapter(adapter);
                                    }
                                    else
                                    { adapter.notifyDataSetChanged();}
                                }
                                if(nbOfLoadedQuestions ==0)
                                    nbOfLoadedQuestions +=array.length();
                                else
                                    nbOfLoadedQuestions +=10;
                            }
                            else{
                                getActivity().findViewById(R.id.noConnectionTextView).setVisibility(View.VISIBLE);
                                forumQustsRecyclerView.setVisibility(View.GONE);
                            }

                            /** if there is no data to display **/
                            if(forumQuestionsList.size()==0){
                                getActivity().findViewById(R.id.noConnectionTextView).setVisibility(View.VISIBLE);
                                forumQustsRecyclerView.setVisibility(View.GONE);
                            }
                            ((SwipeRefreshLayout)getActivity().findViewById(R.id.swipeRefreshShare)).setRefreshing(false);
                            loading=false;
                        }

                        @Override
                        public void onError(VolleyError result) {
                            getActivity().findViewById(R.id.noConnectionTextView).setVisibility(View.VISIBLE);
                            forumQustsRecyclerView.setVisibility(View.GONE);
                            ((SwipeRefreshLayout)getActivity().findViewById(R.id.swipeRefreshShare)).setRefreshing(false);
                            loading=false;
                        }

                        @Override
                        public void onWrong(JSONObject result) {
                            getActivity().findViewById(R.id.noConnectionTextView).setVisibility(View.VISIBLE);
                            forumQustsRecyclerView.setVisibility(View.GONE);
                            ((SwipeRefreshLayout)getActivity().findViewById(R.id.swipeRefreshShare)).setRefreshing(false);
                            loading=false;
                        }
                    });
        }

    }

}
