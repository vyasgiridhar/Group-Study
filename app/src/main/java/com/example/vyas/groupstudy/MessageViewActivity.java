package com.example.vyas.groupstudy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

/**
 * Created by vyas on 11/22/16.
 */

public class MessageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_view);
        byte[] Image = getIntent().getByteArrayExtra("Image");
        ImageView view = (ImageView) findViewById(R.id.message);
        Bitmap bitmap = BitmapFactory.decodeByteArray(Image, 0, Image.length);
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        bitmap = Bitmap.createScaledBitmap(bitmap,size.x,size.y,false);
        view.setImageBitmap(bitmap);

    }

}
