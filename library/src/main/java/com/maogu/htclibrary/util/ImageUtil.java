package com.maogu.htclibrary.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import com.maogu.htclibrary.app.HtcAppBase;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageUtil {

    public static boolean isSquare(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        return options.outHeight == options.outWidth;
    }

    /**
     * 判断是否有SD卡
     *
     * @return true为有SDcard，false则表示没有
     */
    public static boolean hasSdcard() {
        boolean hasCard = false;
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            hasCard = true;
        }
        return hasCard;
    }

    public static Bitmap convertViewToBitmap(View view) {
        if (null == view) {
            return null;
        }
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        return view.getDrawingCache();
    }

    /**
     * 及时扫描拍照后的照片，在相册就能看到
     *
     * @param context 上下文对象
     * @param path    照片的路径
     */
    public static void scanMedia(Context context, String path) {
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        Intent scanFileIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        context.sendBroadcast(scanFileIntent);
    }

    public static void saveBitmap2Jpg(Context context, Bitmap bitmap, File photoFile) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photoFile);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        stream.close();
        scanMedia(context, Uri.decode(photoFile.getAbsolutePath()));
    }

    public static Bitmap decodeLocalSampled(String filePath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        EvtLog.d("aaa", "options.inSampleSize:" + options.inSampleSize);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        int degree = readPictureDegree(filePath);
        if (degree != 0) {// 旋转照片角度
            bitmap = rotateBitmap(bitmap, degree);
        }
        return bitmap;
    }

    public static Bitmap decodeRes(int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(HtcAppBase.getInstance().getResources(), resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        EvtLog.d("aaa", "options.inSampleSize:" + options.inSampleSize);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeResource(HtcAppBase.getInstance().getResources(), resId, options);
        return bitmap;
    }

    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 计算图片的缩放值
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
        if (bitmap == null) {
            return null;

        }
        Matrix m = new Matrix();
        m.postRotate(degress);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }

    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Uri转bitmap
     *
     * @param uri          图像的uri对象
     * @param inSampleSize 图像质量因素
     * @return Bitmap 对象
     */
    public static Bitmap getBitmapFromUri(Uri uri, int inSampleSize) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = inSampleSize;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            InputStream inputStream = HtcAppBase.getInstance().getApplicationContext().getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            int degree = readPictureDegree(uri.getPath());
            if (degree != 0) {// 旋转照片角度
                bitmap = rotateBitmap(bitmap, degree);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static boolean saveImage(Bitmap photo, File file) {
        if (null == photo) {
            return false;
        }
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeStream(bos);
        }
        return false;
    }
}
