package com.jesse.fuzzybackgrounddemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.renderscript.Allocation;
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
        Bitmap overlay = Bitmap.createBitmap((int) (bitmap.getWidth() * ratio), (int) (bitmap.getHeight() * ratio), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.drawBitmap(bitmap, 0, 0, null);

        //创建对象
        RenderScript rs = RenderScript.create(getContext());
        Allocation allocation = Allocation.createFromBitmap(rs, overlay);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, allocation.getElement());
        //渲染
        blur.setInput(allocation);
        blur.setRadius(blurryRadius);
        blur.forEach(allocation);
        allocation.copyTo(overlay);
        //回收
        rs.destroy();
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
