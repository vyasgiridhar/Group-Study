package com.example.vyas.groupstudy;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import me.panavtec.drawableview.DrawableView;
import me.panavtec.drawableview.DrawableViewConfig;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by vyas on 11/22/16.
 */

public class DrawingActivity extends AppCompatActivity {

    DrawableViewConfig config;
    DrawableView drawableView;
    String From, To;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_view);

        From = getIntent().getStringExtra("From");
        To = getIntent().getStringExtra("To");
        config = new DrawableViewConfig();
        config.setStrokeColor(getResources().getColor(android.R.color.black));
        config.setStrokeWidth(10f);
        config.setShowCanvasBounds(true);
        config.setMinZoom(1.0f);
        config.setMaxZoom(3.0f);
        config.setCanvasHeight(1000);
        config.setCanvasWidth(1920);
        drawableView = (DrawableView) findViewById(R.id.paintView);
        drawableView.setConfig(config);

        (findViewById(R.id.send)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                drawableView.obtainBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
                String outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Message.png";
                try {
                    stream.writeTo(new FileOutputStream(outputFile));
                } catch (Exception e) {
                    Log.d("Ho", e.toString());
                }
                new SendMessage(outputFile, DrawingActivity.this).execute();
            }
        });
    }

    public class SendMessage extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private ProgressDialog progressDialog;
        private String FileName;

        SendMessage(String fileName, Context c) {
            context = c;
            FileName = fileName;

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
                OkHttpClient client = new OkHttpClient.Builder()
                        .retryOnConnectionFailure(true)
                        .build();

                RequestBody body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("Image", FileName, RequestBody.create(MediaType.parse("image/jpeg"), new File(FileName)))
                        .build();
                Request request = new Request.Builder()
                        .url("http://192.168.43.58:9090/Send")
                        .addHeader("To_Name", To)
                        .addHeader("From_Name", From)
                        .post(body)
                        .build();
                Log.d("Request", "doInBackground: " + request.toString());
                Response response = client.newCall(request).execute();
                byte[] result = response.body().bytes();
                Log.d("Result : ", "doInBackground: " + result);
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
            } else {
                Toast.makeText(context, "Network Error", Toast.LENGTH_LONG).show();
            }
        }


    }

}
