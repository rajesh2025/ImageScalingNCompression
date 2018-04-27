package com.android.imagequality.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
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

    public static  File createImageFile(Activity activity) throws IOException {
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
        Bitmap bm = BitmapFactory.decodeFile(file_path, options);

        Bitmap bitmap = bm;
        int orientation;
        try {
        ExifInterface exif = new ExifInterface(file_path);

        orientation = exif
                .getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

        Log.e("ExifInteface .........", "rotation ="+orientation);

//          exif.setAttribute(ExifInterface.ORIENTATION_ROTATE_90, 90);

        Log.e("orientation", "" + orientation);
        Matrix m = new Matrix();

        if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
            m.postRotate(180);
//              m.postScale((float) bm.getWidth(), (float) bm.getHeight());
            // if(m.preRotate(90)){
            Log.e("in orientation", "" + orientation);
            bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                    bm.getHeight(), m, true);
            return bitmap;
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            m.postRotate(90);
            Log.e("in orientation", "" + orientation);
            bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                    bm.getHeight(), m, true);
            return bitmap;
        }
        else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            m.postRotate(270);
            Log.e("in orientation", "" + orientation);
            bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                    bm.getHeight(), m, true);
            return bitmap;
        }
        return bitmap;
    } catch (Exception e) {
        return null;
    }
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


    public static Bitmap correctOrientation(String path) {
        int orientation;
        try {
//            if (path == null) {
//                return null;
//            }
//            // decode image size
//            BitmapFactory.Options o = new BitmapFactory.Options();
//            o.inJustDecodeBounds = true;
//            // Find the correct scale value. It should be the power of 2.
//            final int REQUIRED_SIZE = 70;
//            int width_tmp = o.outWidth, height_tmp = o.outHeight;
//            int scale = 0;
//            while (true) {
//                if (width_tmp / 2 < REQUIRED_SIZE
//                        || height_tmp / 2 < REQUIRED_SIZE)
//                    break;
//                width_tmp /= 2;
//                height_tmp /= 2;
//                scale++;
//            }
            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
//            o2.inSampleSize = scale;
            Bitmap bm = BitmapFactory.decodeFile(path, o2);
            Bitmap bitmap = bm;

            ExifInterface exif = new ExifInterface(path);

            orientation = exif
                    .getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

            Log.e("ExifInteface .........", "rotation ="+orientation);

//          exif.setAttribute(ExifInterface.ORIENTATION_ROTATE_90, 90);

            Log.e("orientation", "" + orientation);
            Matrix m = new Matrix();

            if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
                m.postRotate(180);
//              m.postScale((float) bm.getWidth(), (float) bm.getHeight());
                // if(m.preRotate(90)){
                Log.e("in orientation", "" + orientation);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
                return bitmap;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                m.postRotate(90);
                Log.e("in orientation", "" + orientation);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
                return bitmap;
            }
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                m.postRotate(270);
                Log.e("in orientation", "" + orientation);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
                return bitmap;
            }
            return bitmap;
        } catch (Exception e) {
            return null;
        }

    }

}
