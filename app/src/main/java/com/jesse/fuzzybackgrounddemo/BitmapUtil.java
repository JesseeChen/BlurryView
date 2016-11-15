package com.jesse.fuzzybackgrounddemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by Jesse on 2016/9/19.
 */
public class BitmapUtil {
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            return bitmapDrawable.getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        BitmapDrawable drawable = new BitmapDrawable(bitmap);
        return drawable;
    }

    public static byte[] bitmap2ByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap byteArray2Bitmap(byte[] array) {
        Bitmap bitmap = null;
        if (array.length > 0) {
            bitmap = BitmapFactory.decodeByteArray(array, 0, array.length);
        }
        return bitmap;
    }

    public static String bitmap2String(Bitmap bitmap) {
        byte[] bytes = bitmap2ByteArray(bitmap);
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static Bitmap string2Bitmap(String string) {
        byte[] bytes = Base64.decode(string, Base64.DEFAULT);
        return byteArray2Bitmap(bytes);
    }

    public static Bitmap file2Bitmap(File file, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        options.inJustDecodeBounds = false;

        int scaleH = Math.max(options.outHeight / height, 1);
        int scaleW = Math.max(options.outWidth / width, 1);

        options.inSampleSize = Math.max(scaleH, scaleW);

        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }
}
