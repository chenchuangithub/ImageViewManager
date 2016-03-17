package utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by chenchuan on 2016/3/16.
 */
public class ImageUtils {
    private static final int DISPLAY_IMAGE = 1;
    private static final int LOAD_FAIL = -1;
    public static ImageViewSize getLayoutImageSize(final ImageView reImageView) {
            if(reImageView.getMeasuredWidth() == 0 || reImageView.getMeasuredHeight() == 0){
                Log.i("error","image can't ready");
                return null;
            }
        return new ImageViewSize(reImageView.getMeasuredWidth(),reImageView.getMeasuredHeight());
    }

    /**
     * 从网上加载
     *
     * @return
     */
    public static ImageViewSize getLoadImageSize(String url) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            URL path = new URL(url);
            connection = (HttpURLConnection) path.openConnection();
            inputStream = connection.getInputStream();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            return new ImageViewSize(options.outWidth, options.outHeight);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 从资源文件中加载
     *
     * @param resources
     * @param resId
     * @return
     */
    public static ImageViewSize getLoadImageSize(Resources resources, int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resId, options);
        return new ImageViewSize(options.outWidth, options.outHeight);
    }

    /**
     * 从磁盘文件中加载
     *
     * @return
     */
    public static ImageViewSize getLoadImageSize(File path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path.getName(), options);
        return new ImageViewSize(options.outWidth, options.outHeight);
    }

    /**
     * 计算采样率
     *
     * @param reImageView
     * @param url
     * @return
     */
    public static int cacluateInSimpleSize(ImageView reImageView, String url) {
        ImageViewSize reSize = getLayoutImageSize(reImageView);
        if(reSize == null){
            Log.d("boom","the view can't ready");
            return LOAD_FAIL;
        }
        ImageViewSize noSize = getLoadImageSize(url);
        int inSimpleSize = 1;
        while(noSize == null){

        }
        Log.e("infoinfoinfo",reSize.width+"height"+noSize.width);
        cacluateSimpleSize(reSize, noSize, inSimpleSize);
        return inSimpleSize;
    }

    public static int cacluateInSimpleSize(ImageView reImageView, Resources resources, int resId) {
        ImageViewSize reSize = getLayoutImageSize(reImageView);
        ImageViewSize noSize = getLoadImageSize(resources, resId);
        int inSimpleSize = 1;
        cacluateSimpleSize(reSize, noSize, inSimpleSize);
        return inSimpleSize;
    }

    public static int cacluateInSimpleSize(ImageView reImageView, File path) {
        ImageViewSize reSize = getLayoutImageSize(reImageView);
        ImageViewSize noSize = getLoadImageSize(path);
        int inSimpleSize = 1;
        cacluateSimpleSize(reSize, noSize, inSimpleSize);
        return inSimpleSize;
    }

    private static void cacluateSimpleSize(ImageViewSize reSize, ImageViewSize noSize, int simpleSize) {
        if (reSize.width == 0 || reSize.height == 0) {
            simpleSize = 1;
        }
        if (noSize.width > reSize.width && noSize.height > reSize.height) {
            final int halfWidth = noSize.width / 2;
            final int halfHeight = noSize.height / 2;
            while ((halfWidth / simpleSize) > reSize.width && (halfHeight / simpleSize) > reSize.height) {
                simpleSize *= 2;
            }
        }
    }
    public static void disPlayImage(final ImageView imageView, final String url){
        final Handler handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case DISPLAY_IMAGE:
                        if(msg.getData().get("bitmap") != null) {
                            imageView.setImageBitmap((Bitmap) msg.getData().get("bitmap"));
                        }else {
                            Log.d("tips","empty");
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                int inSimpleSize = cacluateInSimpleSize(imageView, url);
                if(inSimpleSize == LOAD_FAIL){
                    throw new RuntimeException("view have't init");
                }
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = inSimpleSize;
                options.inJustDecodeBounds = false;
                InputStream inputStream = getInByUrl(url);
                if(inputStream == null){
                    Log.d("hehe","tell me why");
                }
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream,null,options);
                Message message = handler.obtainMessage(DISPLAY_IMAGE);
                Log.d("this is test",inSimpleSize+"");
                Bundle bundle = new Bundle();
                bundle.putParcelable("bitmap",bitmap);
                message.setData(bundle);
                handler.sendMessage(message);
            }
        }).start();

    }

    public static InputStream getInByUrl(String url){
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        try {
            URL path = new URL(url);
            conn = (HttpURLConnection)path.openConnection();
            inputStream = conn.getInputStream();
            return inputStream;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }
    public static class ImageViewSize implements Serializable{
        public int width;
        public int height;

        public ImageViewSize(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }
    public static int px2dp(int px,Context context){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(px/scale + 0.5f);
    }
    public static int dp2px(int dp,Context context){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp*scale + 0.5f);
    }
}
