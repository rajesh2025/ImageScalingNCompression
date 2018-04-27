package com.android.imagequality.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {

    public static final int TAKE_PICTURE = 419;
    private static final DecimalFormat format = new DecimalFormat("#.##");
    private static final long MiB = 1024 * 1024;
    private static final long KiB = 1024;
    public static String captureImage(Activity context,int requestCode){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(context);
            } catch (IOException ex) {
                // Error occurred while creating the File
                    ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(context,
                        "com.android.imagequality.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                context.startActivityForResult(takePictureIntent, requestCode);

                return photoFile.getAbsolutePath();
            }

            return  null;
    }

    private static  File createImageFile(Activity activity) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }



//    public static int calculateInSampleSize(
//            BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        // Raw height and width of image
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//
//        if (height > reqHeight || width > reqWidth) {
//
//            final int halfHeight = height / 2;
//            final int halfWidth = width / 2;
//
//            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
//            // height and width larger than the requested height and width.
//            while ((halfHeight / inSampleSize) >= reqHeight
//                    && (halfWidth / inSampleSize) >= reqWidth) {
//                inSampleSize *= 2;
//            }
//        }
//
//        return inSampleSize;
//    }


public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {
        final int heightRatio = Math.round((float) height / (float) reqHeight);
        final int widthRatio = Math.round((float) width / (float) reqWidth);
        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    }
    final float totalPixels = width * height;
    final float totalReqPixelsCap = reqWidth * reqHeight * 2;
    while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
        inSampleSize++;
    }

    return inSampleSize;
}


    public static Bitmap decodeSampledBitmapFromResource(String file_path,
                                                         int reqWidth, int reqHeight) {
        if(reqWidth == 0 && reqHeight== 0)
            return null;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file_path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        return BitmapFactory.decodeFile(file_path, options);
    }



    public  static long getBitmapDetail(Bitmap bitmap,int quality){
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bytestream );
        byte[] imageByte = bytestream .toByteArray();
//        Log.i("Bitmap Size",imageByte.length+" ");
//        return imageByte.length / (1024 * 1024);
        return imageByte.length;
    }


    public static String storeImage(Bitmap imageData, Activity activity,int compressQuality) {
        String filePath;
        File realpath = null;
        try {
             realpath = createImageFile(activity);

            filePath = realpath.toString();
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);

            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

            //choose another format if PNG doesn't suit you
            imageData.compress(Bitmap.CompressFormat.JPEG, compressQuality, bos);

            bos.flush();
            bos.close();
        }
        catch (FileNotFoundException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return "Error";
        } catch (IOException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return "Error";
        }

        return filePath;
    }

    public static String getFileSize(long length) {
        double deividend = 0.000;

        if (length > MiB) {
            deividend = (length / MiB);
            return deividend + " MiB";
        }
        if (length > KiB) {
            deividend = (length / KiB);
            return deividend + " KiB";
        }
        return (length) + " B";
    }

}
