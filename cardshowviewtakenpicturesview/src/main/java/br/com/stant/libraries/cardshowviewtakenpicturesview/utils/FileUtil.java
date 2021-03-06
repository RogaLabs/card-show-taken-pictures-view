package br.com.stant.libraries.cardshowviewtakenpicturesview.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by erivan on 17/11/16.
 */
public class FileUtil {
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private static final String TEMP_IMAGE_NAME = "image_temp_stant";
    private static String path = "/<br.com.stant>/temp";
    public static File getFile(){
        return new File(
            Environment.getExternalStorageDirectory(),
            path);
    }
        public static Bitmap decodeBitmapFromFile(String path) {
        Bitmap result;

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        BitmapFactory.decodeFile(path, options);
        try {
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inSampleSize = 2;
            result = BitmapFactory.decodeFile(path, options);

            ExifInterface ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    result = rotateBitmap(result, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    result = rotateBitmap(result, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    result = rotateBitmap(result, 270);
                    break;
            }

        } catch (OutOfMemoryError oe) {
            options.inPreferredConfig = Bitmap.Config.ARGB_4444;
            options.inSampleSize = 4;
            result = BitmapFactory.decodeFile(path, options);
        } catch (IOException io) {
            options.inPreferredConfig = Bitmap.Config.ARGB_4444;
            options.inSampleSize = 4;
            result = BitmapFactory.decodeFile(path, options);
        }

            return result;
    }
    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
    @NonNull
    public static File createTempImageFile(String imageFileName, File sdcardTempImagesDir) throws IOException {
        return File.createTempFile(imageFileName, /* prefix */
                ".jpg", /* suffix */
                sdcardTempImagesDir /* directory */
        );
    }

    public static Bitmap getBitMapFromFile(String fileName, File sdcard){
        File[] files = getFiles(fileName, sdcard);
        if(files != null && files.length > 0) {
            return  decodeBitmapFromFile(files[files.length -1].getAbsolutePath());
        }
        return null;
    }

    private static File[] getFiles(final String fileName, File sdcard) {
        return sdcard.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return  name.contains(fileName) && ((name.endsWith(".jpg")) || (name.endsWith(".png")));
                }
        });
    }

    public static void createTempDirectory(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }


    public static Bitmap createBitFromPath(String pathName, ImageView imageView){
        /* Get the size of the ImageView */
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        return BitmapFactory.decodeFile(pathName, bmOptions);
    }

    public static Bitmap createBitFromPath(String pathName){

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
//        int scaleFactor = 1;
//        if ((targetW > 0) || (targetH > 0)) {
//            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        return BitmapFactory.decodeFile(pathName, bmOptions);
    }

//    FIleUtil.loadImageFromPath(mCurrentPhotoPath,mTakenPicture,getActivity().getContentResolver(),getContext());
    public static void shouImageFromUrl(String imageUrl, ImageView target, Context context){
        Picasso.with(context)
                .load(imageUrl)
                .fit()
                .into(target);
    }

    private static File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF =getFile();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private static File setUpPhotoFile() throws IOException {
        File file = createImageFile();
        return file;
    }

    public static File prepareFile(Intent takePictureIntent) {
        File file = null;
        try {
            file = setUpPhotoFile();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        } catch (IOException e) {
            e.printStackTrace();
            file = null;
        }
        return file;
    }

    public static void saveImage(Bitmap bitmap, String imageFileName) {
        if(bitmap!= null){
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, bytearrayoutputstream);
            File file = new File(getFile(), imageFileName);
            try {
                file.createNewFile();
                FileOutputStream fileoutputstream = new FileOutputStream(file);
                fileoutputstream.write(bytearrayoutputstream.toByteArray());
                fileoutputstream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    public static void deleteFile(String localImage) {
        if(localImage != null) {
            File[] files = getFiles(localImage, getFile());
            for (File file : files) {
                file.delete();
            }
        }
    }
}
