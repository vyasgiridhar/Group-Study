package com.example.vyas.groupstudy;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by vyas on 11/22/16.
 */

public class AddFriendActivity extends AppCompatActivity {
    String Name2;
    String Name1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friend_activity);

        Name1 = getIntent().getStringExtra("Name");
        Button b = (Button) findViewById(R.id.add_friend_button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Name2 = ((EditText) findViewById(R.id.friend_name)).getText().toString();
                new AddFriend(Name1, Name2, AddFriendActivity.this).execute();

            }
        });
    }

    public class AddFriend extends AsyncTask<Void, Void, Boolean> {

        private String Name1;
        private String Name2;
        private Context context;
        private ProgressDialog progressDialog;

        AddFriend(String name1, String name2, Context context) {
            Name1 = name1;
            Name2 = name2;
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
                RequestBody body = RequestBody.create(JSON, "{\"Name1\":\"" + this.Name1 + "\",\"Name2\":\"" + this.Name2 + "\"}");
                Request request = new Request.Builder()
                        .url("http://192.168.43.58:9090/AddFriends")
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();
                String result = response.body().string();
                Log.d("Result : ", "doInBackground: " + result);
                if (result == null) {
                    return false;
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
            } else {
                Toast.makeText(context, "Network Error", Toast.LENGTH_LONG).show();
            }
        }

    }

}
