package eu.lynxit.sampleapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.view.View;

/**
 * Created by Bartosz Wyspiański on 04/03/2017.
 */

public class ButtonView extends View {
    private TextPaint mNotSelectedTextPaint;
    private TextPaint mSelectedTextPaint;
    private Paint mNotSelectedBackgroundPaint;
    private Paint mSelectedBackgroundPaint;
    private Paint mBoundsPaint;
    private int width;
    private int height;
    private RectF mBounds;
    private RectF mBoundsT;
    private Rect mTextRect = new Rect();
    private String mText = "";
    private boolean hover = false;
    private Paint cutPaint;
    private Paint fillPaint;
    private float alpha = 0;

    public ButtonView(Context context, String text) {
        super(context);
        mText = text;
        float density = context.getResources().getDisplayMetrics().density;
        mNotSelectedTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mNotSelectedTextPaint.setColor(Color.RED);
        mNotSelectedTextPaint.setTextSize(75F);
        mNotSelectedTextPaint.density = density;
        mNotSelectedTextPaint.setAntiAlias(true);
        mNotSelectedTextPaint.setFilterBitmap(true);
        mSelectedTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mSelectedTextPaint.setColor(Color.BLACK);
        mSelectedTextPaint.setTextSize(75F);
        mSelectedTextPaint.density = density;
        mSelectedTextPaint.setAntiAlias(true);
        mSelectedTextPaint.setFilterBitmap(true);
        mNotSelectedBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNotSelectedBackgroundPaint.setColor(Color.TRANSPARENT);
        mSelectedBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSelectedBackgroundPaint.setColor(Color.RED);
        mBoundsPaint = new Paint();
        mBoundsPaint.setAntiAlias(true);
        mBoundsPaint.setFilterBitmap(true);
        mBoundsPaint.setColor(Color.RED);
        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(Color.RED);
        fillPaint.setAntiAlias(true);
        fillPaint.setFilterBitmap(true);
        cutPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cutPaint.setColor(getResources().getColor(android.R.color.transparent));
        cutPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        cutPaint.setAntiAlias(true);
        cutPaint.setFilterBitmap(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mNotSelectedTextPaint.getTextBounds(mText, 0, mText.length(), mTextRect);
        float textX = (width / 2) - (mNotSelectedTextPaint.measureText(mText) / 2);
        float textY = (height / 2) - ((mNotSelectedTextPaint.ascent() + mNotSelectedTextPaint.descent()) / 2);
        if (hover) {
            fillPaint.setAlpha(((int) (255 * alpha)));
            canvas.drawRoundRect(mBounds, 90, 90, mBoundsPaint);
            canvas.drawRoundRect(mBoundsT, 90, 90, cutPaint);
            canvas.drawRoundRect(mBoundsT, 90, 90, fillPaint);
            canvas.drawText(mText, textX, textY, mSelectedTextPaint);
        } else {
            canvas.drawRoundRect(mBounds, 90, 90, mBoundsPaint);
            canvas.drawRoundRect(mBoundsT, 90, 90, cutPaint);
            canvas.drawText(mText, textX, textY, mNotSelectedTextPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        mBounds = new RectF(0, 0, width, height);
        mBoundsT = new RectF(4, 4, width - 4, height - 4);
    }

    public void setHover(boolean isHover) {
        if (isHover != hover) {
            hover = isHover;
        }
    }

    public boolean isHovered() {
        return hover;
    }

    public void setAlpha(float newAlpha) {
        alpha = newAlpha;
    }

    public void click() {
        hover = false;
    }

    public void setText(String text){
        mText = text;
    }
}
