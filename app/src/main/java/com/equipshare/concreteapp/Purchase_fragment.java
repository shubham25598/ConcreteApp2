package com.equipshare.concreteapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.equipshare.concreteapp.model.POget;
import com.equipshare.concreteapp.model.Result;
import com.equipshare.concreteapp.model.User_;
import com.equipshare.concreteapp.network.RetrofitInterface;
import com.equipshare.concreteapp.utils.Constants;
import com.equipshare.concreteapp.utils.SessionManagement;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.zip.Inflater;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jarvis on 21-12-2017.
 */

public class Purchase_fragment extends Fragment {

    private FragmentActivity myContext;
    TextView empty;
    Result r1;
    User_ user;
    SessionManagement session;
    String token;
    ProgressDialog progressDialog;
    Gson gson = new GsonBuilder().create();
    Retrofit.Builder builder=new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create(gson));
    Retrofit retrofit=builder.build();
    RetrofitInterface retrofitInterface=retrofit.create(RetrofitInterface.class);
    private RecyclerView recyclerView;
    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       final View view=inflater.inflate(R.layout.available_po,null);
        /*TabLayout tabLayout=(TabLayout) view.findViewById(R.id.purchase_tab);
        ViewPager viewPager=(ViewPager) view.findViewById(R.id.purchase_viewpager);


        PurchaseTabs purchaseTabs=new PurchaseTabs(myContext.getSupportFragmentManager());
        viewPager.setAdapter(purchaseTabs);
        tabLayout.setupWithViewPager(viewPager);*/
        session = new SessionManagement(getContext());
        HashMap<String, String> user1 = session.getUserDetails();
        token=user1.get(SessionManagement.KEY_TOKEN);
        recyclerView=(RecyclerView)view.findViewById(R.id.recyclerViewPO);
        final LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getContext());
        gridLayoutManager.setReverseLayout(true);
        gridLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        r1=((DashBoard)getActivity()).r;
        user=((DashBoard)getActivity()).u;
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();
        // Log.e("TAG", "response 33: " + r1.getUser().getId());
        Call<POget> call=retrofitInterface.getpo(token);
        call.enqueue(new Callback<POget>() {
            @Override
            public void onResponse(Call<POget> call, retrofit2.Response<POget> response) {

                POget pOget=response.body(); // have your all data

                Log.e("TAG", "response 33: " + new Gson().toJson(response.body()));
                Log.e("TAG", "response 33: " + response.body());
                PoAdapter poAdapter=new PoAdapter(pOget.getData(),user.getCustomerSite(),r1.getToken());
                recyclerView.setAdapter(poAdapter);
                progressDialog.cancel();
                if(gridLayoutManager.getItemCount()==0)
                {
                    empty=(TextView)view.findViewById(R.id.emptypo);
                    empty.setText("No Records Of PO");
                }
                else{
                    View b = view.findViewById(R.id.emptypo);
                    b.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<POget> call, Throwable t) {
                Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
            }

        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Purchase Orders");
    }
}
