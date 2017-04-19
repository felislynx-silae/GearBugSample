package eu.lynxit.sampleapp;

import android.animation.ValueAnimator;
import android.view.View;

import org.gearvrf.GVRActivity;
import org.gearvrf.GVRContext;

/**
 * Created by Bartosz Wyspiański on 04/03/2017.
 */

public class TextButtonGVRSceneObject extends BaseGVRSceneObject {
    private String mText = "";
    private float alpha = 0;

    public TextButtonGVRSceneObject(GVRContext gvrContext, float width, float height, String text) {
        super(gvrContext, width, height);
        mText = text;
        ((ButtonView) mView).setText(mText);
        mIsChanged = true;
        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setDuration(500);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                alpha = (float) animation.getAnimatedValue();
                mView.setAlpha(alpha);
                mIsChanged = true;
            }
        });
    }

    @Override
    public View createView(GVRActivity activity) {
        return new ButtonView(activity, mText);
    }

    public void hover(boolean isHovered) {
        if (isHovered != mView.isHovered()) {
            ((ButtonView) mView).setHover(isHovered);
            mIsChanged = true;
            if (mAnimator.isRunning() && !isHovered) {
                mAnimator.reverse();
            } else {
                mAnimator.start();
            }
        }
    }
}

