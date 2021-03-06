package com.equipshare.concreteapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;


import com.equipshare.concreteapp.model.Issue;
import com.equipshare.concreteapp.model.Order;
import com.equipshare.concreteapp.network.RetrofitInterface;
import com.equipshare.concreteapp.utils.Constants;
import com.equipshare.concreteapp.utils.DirectingClass;
import com.equipshare.concreteapp.utils.SessionManagement;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Issues extends AppCompatActivity {
    private EditText issue_title,issue_desc;
    private Spinner issue_type;
    SessionManagement session;
    LinearLayout linearLayout;
    String token;
    ProgressDialog progressDialog;
    private static Retrofit.Builder builder=new Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());
    public static Retrofit retrofit=builder.build();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issues);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent i=getIntent();
        final Order order;
        order=i.getParcelableExtra("Order");
        session = new SessionManagement(Issues.this);
        HashMap<String, String> user = session.getUserDetails();
        token=user.get(SessionManagement.KEY_TOKEN);
        issue_title=(EditText)findViewById(R.id.IssueTitle);
        issue_desc=(EditText)findViewById(R.id.issue_description);
        issue_type=(Spinner)findViewById(R.id.issue_type);
        Button submit=(Button)findViewById(R.id.raise_issue);
        linearLayout=(LinearLayout)findViewById(R.id.issue);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startissue(order);
            }
        });
    }
    private void startissue(Order order1)
    {
        progressDialog=new ProgressDialog(Issues.this);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();
        submit_issue(issue_title.getText().toString(),issue_desc.getText().toString(),issue_type.getSelectedItem().toString(),order1);
    }

    private void submit_issue(String title, String desc, String type,Order o1)
    {
        RetrofitInterface retrofitInterface=retrofit.create(RetrofitInterface.class);
        Map<String,String> map=new HashMap<>();
        map.put("title",title);
        map.put("description",desc);
        map.put("orderId", String.valueOf(o1.getOrderId()));
        map.put("type",type);
        Call<ResponseBody> call=retrofitInterface.add_issue(token,map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
             progressDialog.cancel();
                Snackbar snackbar = Snackbar
                        .make(linearLayout, "Issue Reported Successfully", Snackbar.LENGTH_LONG);
                snackbar.setActionTextColor(Color.RED);
                snackbar.show();
                Log.e("TAG", "response 33: "+new Gson().toJson(response.body()));
                DirectingClass directingClass=new DirectingClass(getApplicationContext(),Issues.this);
                directingClass.performLogin();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
               progressDialog.cancel();
                Toast.makeText(Issues.this,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
