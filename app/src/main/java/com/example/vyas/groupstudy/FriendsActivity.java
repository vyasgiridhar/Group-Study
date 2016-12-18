package com.example.vyas.groupstudy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FriendsActivity extends AppCompatActivity {

    ArrayList<String> Friendlist;
    ArrayList<byte[]> Message;
    ArrayList<Boolean> hasMessage;
    String name;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        Message = new ArrayList<>();
        hasMessage = new ArrayList<>();
        listView = (ListView) findViewById(R.id.friendslist);
        name = getIntent().getStringExtra("Name");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (hasMessage.size() > 0) {
                    if (hasMessage.get(i) == Boolean.TRUE) {
                        Intent intent = new Intent(FriendsActivity.this, MessageViewActivity.class);
                        byte[] m = Message.get(i);
                        hasMessage.set(i, Boolean.FALSE);
                        Message.set(i, null);
                        intent.putExtra("Image", m);
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(FriendsActivity.this, DrawingActivity.class);
                    intent.putExtra("From", name);
                    intent.putExtra("To", Friendlist.get(i));
                    startActivity(intent);
                }
            }
        });
        findViewById(R.id.addFriend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddFriendFragment.class);
                i.putExtra("Name", name);
                startActivity(i);
            }
        });

        new GetUserDetails(name, this).execute();
        new GetUserMessage(name, this).execute();
    }

    public class GetUserMessage extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private ProgressDialog progressDialog;

        GetUserMessage(String name, Context c) {
            context = c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loading");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            try {
                for (String Name : Friendlist) {
                    Log.d("Here ", "OK");
                    OkHttpClient client = new OkHttpClient();
                    RequestBody body = RequestBody.create(JSON, "{\"To_Name\":\"" + name + "\",\"From_Name\":\"" + Name + "\"}");
                    Request request = new Request.Builder()
                            .url("http://192.168.43.58:9090/Recieve")
                            .post(body)
                            .build();

                    Response response = client.newCall(request).execute();
                    byte[] result = response.body().bytes();
                    Log.d("Result : ", "doInBackground: " + result);
                    Message.add(result);
                    hasMessage.add(result.length != 0);
                }
            } catch (Exception e) {
                Log.d("Error", e.toString());
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            progressDialog.dismiss();
            if (success) {
                Toast.makeText(context, "Fetched Messages", Toast.LENGTH_LONG).show();
            }
        }


    }

    public class GetUserDetails extends AsyncTask<Void, Void, Boolean> {

        private String Name;
        private Context context;
        private ProgressDialog progressDialog;

        GetUserDetails(String name, Context context) {
            Name = name;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loading");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");

            try {
                Log.d("Here ", "OK");
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON, "{\"To_Name\":\"" + this.Name + "\"}");
                Request request = new Request.Builder()
                        .url("http://192.168.43.58:9090/GetFriends")
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();
                String result = response.body().string();
                Log.d("Result : ", "doInBackground: " + result);
                try {
                    Friendlist = new ArrayList<>();
                    JSONArray json = new JSONArray(result);
                    for (int i = 0; i < json.length(); i++) {
                        JSONObject Friends = json.getJSONObject(i);
                        Friendlist.add(Friends.getString("Name2"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            } catch (Exception e) {
                Log.d("Error", e.toString());
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            progressDialog.dismiss();
            if (success) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, Friendlist);
                listView.setAdapter(adapter);
            } else {
                Toast.makeText(context, "Network Error", Toast.LENGTH_LONG).show();
            }
        }

    }

}
