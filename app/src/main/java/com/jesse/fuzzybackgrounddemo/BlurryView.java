package com.jesse.fuzzybackgrounddemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by Jesse on 2016/11/15.
 */
public class BlurryView extends RelativeLayout {
    private int mBitmapId;
    private int imageWidth;
    private int imageHeight;
    private ImageView mImageView;

    public BlurryView(Context context) {
        super(context);
    }

    public BlurryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initRes(context, attrs);
        mImageView = new ImageView(context);
        LayoutParams layoutParams = new LayoutParams(imageWidth, imageHeight);
        mImageView.setLayoutParams(layoutParams);
        mImageView.setImageResource(mBitmapId);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        addView(mImageView);
    }

    private void doBlurry(ImageView imageView) {
        Bitmap bitmap = BitmapUtil.drawable2Bitmap(imageView.getDrawable());
        Bitmap overlay = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.drawBitmap(bitmap, 0, 0, null);

        //创建对象
        RenderScript rs = RenderScript.create(getContext());
        Allocation allocation = Allocation.createFromBitmap(rs, overlay);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, allocation.getElement());

        //渲染
        blur.setInput(allocation);
        blur.setRadius(20);
        blur.forEach(allocation);
        allocation.copyTo(overlay);

        setBackground(new BitmapDrawable(getResources(), overlay));

        //回收
        rs.destroy();
    }

    private void initRes(Context context, AttributeSet attrs) {
        TypedArray typedArray = getResources().obtainAttributes(attrs, R.styleable.BlurryView);
        mBitmapId = typedArray.getResourceId(R.styleable.BlurryView_src, 0);
        imageWidth = (int) typedArray.getDimension(R.styleable.BlurryView_image_width, 0);
        imageHeight = (int) typedArray.getDimension(R.styleable.BlurryView_image_height, 0);

        if (imageWidth != WRAP_CONTENT && imageWidth != MATCH_PARENT) {
            imageWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, imageWidth, getResources().getDisplayMetrics());
        }
        if (imageHeight != WRAP_CONTENT && imageHeight != MATCH_PARENT) {
            imageHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, imageHeight, getResources().getDisplayMetrics());
        }
        typedArray.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        doBlurry(mImageView);
    }
}
