package com.android.imagequality;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.imagequality.utility.Utility;

import java.io.File;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullScreenImageActivity extends AppCompatActivity {
    Button btn_close;
    ImageView iv_full_image;
    String image_path;
    TextView tv_image_type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        btn_close = (Button) findViewById(R.id.btn_close);
        iv_full_image = (ImageView) findViewById(R.id.iv_full_image);
        tv_image_type = (TextView) findViewById(R.id.tv_image_type);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (getIntent().getExtras() != null) {
            image_path = getIntent().getExtras().getString("image_path");
            if (image_path != null) {
                loadImage(image_path);
            }
        }

    }

    private void loadImage(String image_path) {
        File imgFile = new File(image_path);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            long size = Utility.getBitmapDetail(myBitmap,100);

            String image_details = " Height: "+myBitmap.getHeight()+
                    " Width: "+myBitmap.getWidth()+
                    "  Size : "+  Utility.getFileSize(imgFile.length());

        Log.i("im memory size",myBitmap.getByteCount()+"");
        Log.i("im disk size",size+"");
        Log.i("file length ",imgFile.length()+"");
            iv_full_image.setImageBitmap(myBitmap);
            tv_image_type.setText(image_details);
        }
    }


}
