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

    private RecyclerView forumRececyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ForumQuestion> listForum;

    private static int loaded_length;
    private boolean loading = true,loadwhile_empty=true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount,orderby;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaded_length=0;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

       // ((SearchView)getActivity().findViewById(R.id.searchKey_forum)).onActionViewExpanded();
        /*((SearchView)getActivity().findViewById(R.id.searchKey_forum)).setIconifiedByDefault(true);
        (getActivity().findViewById(R.id.searchKey_forum)).setFocusable(true);
        ((SearchView)getActivity().findViewById(R.id.searchKey_forum)).setIconified(false);
        ((SearchView)getActivity().findViewById(R.id.searchKey_forum)).clearFocus();
        (getActivity().findViewById(R.id.searchKey_forum)).requestFocusFromTouch();*/

        /** list **/
        forumRececyclerView = getActivity().findViewById(R.id.forum_recycler_view_share);
        layoutManager = new LinearLayoutManager(getContext());
        forumRececyclerView.setLayoutManager(layoutManager);
        listForum=new ArrayList<>();
        loaded_length=0;
        DefaultItemAnimator animator = new DefaultItemAnimator() {
            @Override
            public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder) {
                return true;
            }
        };
        forumRececyclerView.setItemAnimator(animator);
        adapter = new ShareListAdapter(new ArrayList<ForumQuestion>(),getActivity());
        forumRececyclerView.setAdapter(adapter);
        setActionListenerToAdd();

        //forum loads in first time in here
        setActionListenerToOrderBySpinner();

        //setSubmitListener fo search
        setOnsearchListener();

        ((SwipeRefreshLayout)getActivity().findViewById(R.id.share_refresh)).setColorSchemeColors(
                getContext().getResources().getColor(R.color.refresh_progress_1),
                getContext().getResources().getColor(R.color.refresh_progress_2),
                getContext().getResources().getColor(R.color.refresh_progress_3));


        ((SwipeRefreshLayout)getActivity().findViewById(R.id.share_refresh)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Configuration.isOnline(getContext())){
                    loaded_length=0;
                    //Log.d("loaded",loaded_length+"by refresh");
                    load_forum(0,((SearchView)getActivity().findViewById(R.id.searchKey_forum)).getQuery().toString());
                }
                else
                {
                    getActivity().findViewById(R.id.no_connection_shareFragment).setVisibility(View.VISIBLE);
                    forumRececyclerView.setVisibility(View.GONE);
                    ((SwipeRefreshLayout)getActivity().findViewById(R.id.share_refresh)).setRefreshing(false);
                    loading=false;
                }
            }
        });
        attach_scrollListener();

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_share, container, false);
        loaded_length=0;
        return v;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if(Configuration.isOnline(getContext()) && forumRececyclerView.getChildCount()==0) {
                loaded_length = 0;
                getActivity().findViewById(R.id.no_connection_shareFragment).setVisibility(View.GONE);
                load_forum(0, ((SearchView)getActivity().findViewById(R.id.searchKey_forum)).getQuery().toString());
            }
        }
    }


    public synchronized void  load_forum(final int start_at, String query){
        if(start_at==0)
        {
            loaded_length=0;
            forumRececyclerView.removeAllViews();
            listForum.clear();
        }
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            ((SwipeRefreshLayout)getActivity().findViewById(R.id.share_refresh)).setRefreshing(true);
            ForumServices.getInstance().getTopForums("dZb3TxK1x5dqQJkq7ve0d683VoA3",
                    getActivity().getApplicationContext(),start_at,query,orderby,
                    new ServerCallbacks() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            boolean goShow=true;
                            JSONArray array = new JSONArray();
                            try {
                                array = result.getJSONArray("forum");
                            } catch (JSONException e) {
                                Toast.makeText(getContext(),"Server error while loading forum , please report",Toast.LENGTH_SHORT).show();
                                goShow=false;
                            }
                            for(int i = 0 ; i < array.length() ; i++){
                                try {
                                    /** parse forum and add it to the arraylist**/
                                    listForum.add(ForumServices.parse_(array.getJSONObject(i)));
                                } catch (JSONException e) {
                                    Toast.makeText(getContext(),"Application error while loading forum , please report",Toast.LENGTH_SHORT).show();
                                    goShow=false;
                                }
                            }
                            /** All the work will be here **/
                            if(goShow) {
                                getActivity().findViewById(R.id.no_connection_shareFragment).setVisibility(View.GONE);
                                forumRececyclerView.setVisibility(View.VISIBLE);
                                if(loaded_length==0){
                                    //Log.d("loaded",loaded_length+"new load");
                                    adapter=new ShareListAdapter(listForum,getContext());
                                    forumRececyclerView.setAdapter(adapter);
                                }
                                else{
                                    //Log.d("loaded",loaded_length+"old load : "+loaded_length+"     "+forumRececyclerView+"   "+adapter);
                                    if(adapter==null){
                                        adapter=new ShareListAdapter(listForum,getContext());
                                        forumRececyclerView.setAdapter(adapter);
                                    }
                                    else
                                    { adapter.notifyDataSetChanged();}
                                }
                                //addCalculated
                                if(loaded_length==0) loaded_length+=array.length();
                                else
                                    loaded_length+=10;
                            }
                            else{
                                getActivity().findViewById(R.id.no_connection_shareFragment).setVisibility(View.VISIBLE);
                                forumRececyclerView.setVisibility(View.GONE);
                            }
                            if(listForum.size()==0){
                                getActivity().findViewById(R.id.no_connection_shareFragment).setVisibility(View.VISIBLE);
                                forumRececyclerView.setVisibility(View.GONE);
                            }
                            ((SwipeRefreshLayout)getActivity().findViewById(R.id.share_refresh)).setRefreshing(false);
                            loading=false;
                        }

                        @Override
                        public void onError(VolleyError result) {
                            if (result.networkResponse == null) {
                                Toast.makeText(getContext(),"error class"+result.getClass().getName(),Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(getContext(),"----"+result.getClass(),Toast.LENGTH_SHORT).show();
                            getActivity().findViewById(R.id.no_connection_shareFragment).setVisibility(View.VISIBLE);
                            forumRececyclerView.setVisibility(View.GONE);
                            ((SwipeRefreshLayout)getActivity().findViewById(R.id.share_refresh)).setRefreshing(false);
                            loading=false;
                        }

                        @Override
                        public void onWrong(JSONObject result) {
                            Toast.makeText(getContext(),"error----"+result.toString(),Toast.LENGTH_SHORT).show();
                            getActivity().findViewById(R.id.no_connection_shareFragment).setVisibility(View.VISIBLE);
                            forumRececyclerView.setVisibility(View.GONE);
                            ((SwipeRefreshLayout)getActivity().findViewById(R.id.share_refresh)).setRefreshing(false);
                            loading=false;
                        }
                    });
        }

    }

    public void attach_scrollListener(){
        loading =false;
        forumRececyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0) //check for scroll down
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
                                //Log.d("loaded",loaded_length+"by search");
                                load_forum(loaded_length, ((SearchView)getActivity().findViewById(R.id.searchKey_forum)).getQuery().toString());
                                // //Log.d("loading","loading_more");
                            }
                        }
                    }
                }
            }
        });
    }
    public void  setActionListenerToAdd(){
        getActivity().findViewById(R.id.add_forum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //loaded_length=0;
                AddForumFragment af= new AddForumFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.root_share_fragment,af)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    public void setActionListenerToOrderBySpinner(){
        ((Spinner)getActivity().findViewById(R.id.orderby_forum)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                orderby=i+1;
                //load forum
                if(Configuration.isOnline(getContext()))
                {
                    load_forum(0,((SearchView)getActivity().findViewById(R.id.searchKey_forum)).getQuery().toString());
                }
                else
                    forumRececyclerView.setVisibility(View.GONE);
                getActivity().findViewById(R.id.no_connection_shareFragment).setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void setOnsearchListener(){
        ((SearchView)getActivity().findViewById(R.id.searchKey_forum)).setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(Configuration.isOnline(getContext())){

                    load_forum(0,s.trim().toLowerCase());

                    loadwhile_empty=true;
                }

                else
                    Toast.makeText(getContext(),"No connection",Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(Configuration.isOnline(getContext())&& !s.trim().isEmpty() && loadwhile_empty){
                    listForum.clear();
                    loaded_length=0;
                    forumRececyclerView.removeAllViews();
                    //Log.d("loaded",loaded_length+"by search query");
                    load_forum(0,s.trim().toLowerCase());
                    loadwhile_empty=false;
                }
                else{
                    loading=false;
                }
                return true;
            }
        });
    }


}
