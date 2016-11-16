package com.jesse.fuzzybackgrounddemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

/**
 * Created by Jesse on 2016/11/15.
 */
public class BlurryView extends RelativeLayout {
    private static final String TAG = BlurryView.class.getSimpleName();
    private View mBlurrySourceView;
    private Bitmap mBitmap;

    public BlurryView(Context context) {
        super(context);
    }

    public BlurryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private View findBlurrySourceView() {
        return findViewById(R.id.image_id);
    }

    private void addViewObserver() {
        mBlurrySourceView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (getVisibility() == VISIBLE) {
                    mBlurrySourceView.setDrawingCacheEnabled(true);
                    mBitmap = mBlurrySourceView.getDrawingCache();

                    Bitmap overlay = doBlurry(mBitmap, 10, 0.5f);
                    setBackground(new BitmapDrawable(getResources(), overlay));
                    mBlurrySourceView.setDrawingCacheEnabled(false);
                }
            }
        });
    }

    private Bitmap doBlurry(Bitmap bitmap, int blurryRadius, float ratio) {
        long startTime = System.currentTimeMillis();
        Bitmap overlay = Bitmap.createBitmap((bitmap.getWidth()), (bitmap.getHeight()), Bitmap.Config.ARGB_8888);

        //创建RenderScript对象
        RenderScript rs = RenderScript.create(getContext());
        //模糊对象
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        blur.setRadius(blurryRadius);
        //Allocation
        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        Allocation output = Allocation.createFromBitmap(rs, overlay);
        //渲染
        blur.setInput(input);
        blur.forEach(output);
        output.copyTo(overlay);
        //回收内存
        rs.destroy();
        bitmap.recycle();
        Log.d(TAG, "doBlurry: during " + (System.currentTimeMillis() - startTime) + " ms");
        return overlay;
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mBlurrySourceView = findBlurrySourceView();
        addViewObserver();
    }
}
