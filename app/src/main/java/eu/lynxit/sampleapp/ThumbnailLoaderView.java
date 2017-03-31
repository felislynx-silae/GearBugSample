package eu.lynxit.sampleapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.widget.FrameLayout;

import org.gearvrf.GVRActivity;

public class ThumbnailLoaderView extends View {
    private Bitmap mDefaultBitmap;
    private int width;
    private int height;
    private Paint backgroundPaint;
    private RectF mBounds;
    private RectF mBounds2;
    private Bitmap mBitmap;
    private Paint mImagePaint = new Paint();
    private Paint progressPaint;
    private Rect mDefaultImageRect;
    private float progress = 0;
    private int startAngle = -90;

    public ThumbnailLoaderView(GVRActivity context) {
        super(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(900, 1900);
        setLayoutParams(params);
        context.registerView(this);
        setBackgroundColor(getResources().getColor(android.R.color.transparent));
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.WHITE);
        progressPaint = new Paint();
        progressPaint.setColor(getResources().getColor(R.color.colorAccent));
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawArc(mBounds, startAngle, 360 * progress, true, progressPaint);
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, mDefaultImageRect, mBounds2, mImagePaint);
        } else if (mDefaultBitmap != null) {
            canvas.drawBitmap(mDefaultBitmap, mDefaultImageRect, mBounds2, mImagePaint);
        } else {
            canvas.drawRect(mBounds2, backgroundPaint);
       }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        mBounds = new RectF(-100, -100, width+100, height+100);
        mBounds2 = new RectF(height*0.05F, height*0.05F, width -height*0.05F, height - height*0.05F);
        mDefaultImageRect = new Rect((int)(height*0.05F), (int)(height*0.05F), (int)(width -height*0.05F), (int)(height - height*0.05F));
    }

    public void updateThumbnail(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public void setDefaultBitmap(Bitmap bitmap) {
        mDefaultBitmap = bitmap;
    }

    public void setProgress(float newProgress) {
        progress = newProgress;
    }
}