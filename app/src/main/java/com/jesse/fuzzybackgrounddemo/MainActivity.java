package com.jesse.fuzzybackgrounddemo;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private int[] imageIds;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initRes();
    }

    private void initRes() {
        TypedArray typedArray = getResources().obtainTypedArray(R.array.images);
        imageIds = new int[typedArray.length()];
        for (int i = 0; i < typedArray.length(); i++) {
            int resourceId = typedArray.getResourceId(i, 0);
            imageIds[i] = resourceId;
        }
        typedArray.recycle();
        for (int i : imageIds) {
            Log.d(TAG, "initRes: i= " + i);
        }
    }

    public void nextPic(View view) {
        ImageView imageView = (ImageView) findViewById(R.id.image_id);
        imageView.setImageResource(imageIds[index]);
        index = (++index) % imageIds.length;
    }
}
