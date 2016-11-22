package com.example.vyas.groupstudy;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

public class RegisterActivity extends AppCompatActivity {

    private EditText Email;
    private EditText Password;
    private EditText Name;
    private UserRegisterTask RegTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        Email = (EditText) findViewById(R.id.r_email);
        Password = (EditText) findViewById(R.id.r_password);
        Name = (EditText) findViewById(R.id.r_name);
        Button b = (Button) findViewById(R.id.register_button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
    }

    private void attemptRegister() {
        if (RegTask != null) {
            return;
        }

        // Reset errors.
        Email.setError(null);
        Password.setError(null);
        Name.setError(null);
        // Store values at the time of the login attempt.
        String email = Email.getText().toString();
        String password = Password.getText().toString();
        String name = Name.getText().toString();

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            Password.setError(getString(R.string.error_invalid_password));
        } else if (TextUtils.isEmpty(name)) {
            Name.setError("Name Required");
        }
        // Check for a valid email address.
        else if (TextUtils.isEmpty(email)) {
            Email.setError(getString(R.string.error_field_required));

        } else {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(Email.getWindowToken(), 0);
            RegTask = new UserRegisterTask(name, password, email, this);
            RegTask.execute((Void) null);
        }
    }

    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private String Name;
        private String Email;
        private String Password;
        private Context context;
        private ProgressDialog progressDialog;

        UserRegisterTask(String name, String password, String Email, Context context) {
            Name = name;
            this.Email = Email;
            this.Password = password;
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
                RequestBody body = RequestBody.create(JSON, "{\"name\":\"" + this.Name + "\",\"email\":\"" + this.Email + "\",\"password\":\"" + this.Password + "\"}");
                Request request = new Request.Builder()
                        .url("http://192.168.43.58:9090/Create")
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();
                String result = response.body().string();
                Log.d("Result : ", "doInBackground: " + result);
                return result.contains("Created");
            } catch (Exception e) {
                Log.d("Error", e.toString());
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            progressDialog.dismiss();
            if (success) {
                finish();
            } else {
                Toast.makeText(context, "Wrong credentials", Toast.LENGTH_LONG).show();
            }
        }

    }
}