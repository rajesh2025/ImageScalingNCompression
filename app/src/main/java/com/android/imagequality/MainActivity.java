package com.android.imagequality;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.imagequality.utility.Utility;

public class MainActivity extends AppCompatActivity {
Button btn_normal_quality,btn_image_load;
ImageView iv_original_image,iv_modified_image;

EditText et_image_width,et_image_height,et_compression_quality;
    private  Bitmap originalBitmap,modifiedBitmap;
    String  mImagePath,modifiedImagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_normal_quality = (Button)findViewById(R.id.btn_normal_quality);
        btn_image_load = (Button)findViewById(R.id.btn_image_load);
        btn_image_load.setVisibility(View.GONE);
        iv_original_image = (ImageView)findViewById(R.id.iv_original_image);
        iv_modified_image = (ImageView)findViewById(R.id.iv_modified_image);
        et_image_height = (EditText)findViewById(R.id.et_image_height);
        et_image_width = (EditText)findViewById(R.id.et_image_width);
        et_compression_quality = (EditText)findViewById(R.id.et_compression_quality);

        iv_original_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mImagePath !=null){
                    Intent fullScreenIntent = new Intent(MainActivity.this,FullScreenImageActivity.class);
                    fullScreenIntent.putExtra("image_path",mImagePath);
                    startActivity(fullScreenIntent);
                }

            }
        });

        iv_modified_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(modifiedImagePath != null){
                    Intent fullScreenIntent = new Intent(MainActivity.this,FullScreenImageActivity.class);
                    fullScreenIntent.putExtra("image_path",modifiedImagePath);
                    startActivity(fullScreenIntent);
                }

            }
        });


        btn_normal_quality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  mImagePath = Utility.captureImage(MainActivity.this,Utility.TAKE_PICTURE);
            }
        });

        btn_image_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImage();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Utility.TAKE_PICTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
             originalBitmap = (Bitmap) extras.get("data");
                btn_image_load.setVisibility(View.VISIBLE);
                        iv_original_image.setImageBitmap(originalBitmap);
        }
    }

    private void loadImage(){
        String quality_input =  et_compression_quality.getText().toString();
        String width_input =  et_image_width.getText().toString();
        String height_input =  et_image_height.getText().toString();
        if(!width_input.isEmpty() && !height_input.isEmpty() ) {
            modifiedBitmap = Utility.decodeSampledBitmapFromResource(mImagePath,
                    Integer.valueOf(et_image_width.getText().toString()),
                    Integer.valueOf(et_image_height.getText().toString()));
            int compress_quality = quality_input.isEmpty() ? 100: Integer.valueOf(quality_input);
            if(modifiedBitmap != null){
                modifiedImagePath = Utility.storeImage(modifiedBitmap, MainActivity.this, compress_quality);
                iv_modified_image.setImageBitmap(modifiedBitmap);
            }

        }

    }


}
