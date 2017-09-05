package ve.com.abicelis.planetracker.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import ve.com.abicelis.planetracker.application.PlaneTrackerApplication;

/**
 * Created by abicelis on 8/7/2017.
 */

public class ImageUtil {

    /**
     * Returns a bitmap of the @DrawableRes
     * @param drawableRes @DrawableRes of the image
     */
    public static Bitmap getBitmap(@DrawableRes int drawableRes) {
        Drawable d = ContextCompat.getDrawable(PlaneTrackerApplication.getAppContext(), drawableRes);
        return getBitmap(d);
    }


    /**
     * Returns a bitmap of the image contained in the byte array
     * @param imgInBytes The image in a byte[]
     */
    public static Bitmap getBitmap(byte[] imgInBytes) {
        int length = imgInBytes.length;
        return BitmapFactory.decodeByteArray(imgInBytes, 0, length);
    }


    /**
     * Returns a bitmap given a drawable
     * @param drawable The drawable to convert
     */
    public static Bitmap getBitmap(Drawable drawable) {
        BitmapDrawable bdp = (BitmapDrawable)drawable;
        Bitmap bp = bdp.getBitmap();
        return bp;
    }

    /**
     * Returns a bitmap of the image given its directory and its filename
     * @param directory The directory where to look the image for
     * @param filename The name of the image file
     */
    public static Bitmap getBitmap(File directory, String filename) {
        File imageFile = new File(directory, filename);
        return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
    }

    /**
     * Returns a bitmap of the image at the specified path
     * @param file The file containing the path to the image
     */
    public static Bitmap getBitmap(File file) {
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    /**
     * Returns a bitmap of the image at the specified uri
     * @param uri The URI containing the path to the image
     */
    public static Bitmap getBitmap(Uri uri) throws IOException {
        return MediaStore.Images.Media.getBitmap(PlaneTrackerApplication.getAppContext().getContentResolver(), uri);
    }


    /**
     * Bitmap to byte[]
     *
     * @param bitmap Bitmap
     * @return byte array
     */
    public static byte[] toByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    /**
     * Returns a compressed JPEG byte[] representation of a Bitmap
     * @param bitmap  Bitmap
     * @param quality int
     * @return compressed JPEG byte array
     */
    public static byte[] toCompressedByteArray(Bitmap bitmap, int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }






    /**
     * Returns a scaled Bitmap with:
     *  - Its larger dimension = largerScaledDimension in px
     *  - Its smaller dimension scaled, according to the bitmap's original aspect ratio
     *
     *  Note: if the bitmap's dimensions are already smaller than largerScaledDimension
     *  then nothing will be done to the bitmap
     */
    public static Bitmap scaleBitmap(Bitmap image, int largerScaledDimension) {

        if (image == null || image.getWidth() == 0 || image.getHeight() == 0)
            return image;

        //if the image is already small, leave as is
        if (image.getHeight() <= largerScaledDimension && image.getWidth() <= largerScaledDimension)
            return image;

        // Resize the larger dimension of the image to largerScaledDimension and calculate other size
        // respecting the image's aspect ratio
        boolean heightLargerThanWidth = (image.getHeight() > image.getWidth());
        float aspectRatio = (heightLargerThanWidth ? (float)image.getHeight() / (float)image.getWidth() : (float)image.getWidth() / (float)image.getHeight());
        int smallerScaledDimension = (int) (largerScaledDimension / aspectRatio);
        int scaledWidth = (heightLargerThanWidth ? smallerScaledDimension : largerScaledDimension);
        int scaledHeight = (heightLargerThanWidth ? largerScaledDimension : smallerScaledDimension);

        return Bitmap.createScaledBitmap(image, scaledWidth, scaledHeight, true);
    }

}
