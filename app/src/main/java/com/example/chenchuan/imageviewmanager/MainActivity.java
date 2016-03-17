package com.example.chenchuan.imageviewmanager;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import utils.ImageUtils;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.image);
        final String path = "https://www.baidu.com/img/bd_logo1.png";
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(6000);
//                Log.d("infoinfoinfo",imageView.getWidth()+"");
                ImageUtils.disPlayImage(imageView,path);
            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
