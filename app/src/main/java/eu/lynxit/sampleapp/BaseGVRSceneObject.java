package eu.lynxit.sampleapp;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.gearvrf.GVRActivity;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRDrawFrameListener;
import org.gearvrf.GVRExternalTexture;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;

import java.lang.ref.WeakReference;

/**
 * Created by Bartosz Wyspiański on 08/04/2017.
 */

public abstract class BaseGVRSceneObject extends GVRSceneObject {
    protected static final int REALTIME_REFRESH_INTERVAL = 1;
    protected static final int HIGH_REFRESH_INTERVAL = 10; // frames
    protected static final int MEDIUM_REFRESH_INTERVAL = 20;
    protected static final int LOW_REFRESH_INTERVAL = 30;
    protected static final int NONE_REFRESH_INTERVAL = 0;

    protected static final int FACTOR_IMAGE_SIZE = 128;
    protected static final int MAX_IMAGE_SIZE = 4 * FACTOR_IMAGE_SIZE;

    protected static int sReferenceCounter = 0;// This is for load balancing.
    protected boolean mFirstFrame;
    protected boolean mIsChanged;
    protected volatile int mRefreshInterval = REALTIME_REFRESH_INTERVAL;

    protected final Surface mSurface;
    protected final SurfaceTexture mSurfaceTexture;
    protected final LinearLayout mTextViewContainer;
    protected final View mView;

    protected int mCount;
    protected final BaseGVRSceneObject.GVRDrawFrameListenerImpl mFrameListener;
    protected ValueAnimator mAnimator;
    protected boolean isFinished = false;
    protected boolean isStopped = true;
    protected final GVRActivity activity;

    public abstract View createView(GVRActivity activity);

    public BaseGVRSceneObject(GVRContext gvrContext, float width, float height) {
        super(gvrContext, gvrContext.createQuad(width, height));
        final float factor = width / height;
        int canvasWidth = (int) (width * FACTOR_IMAGE_SIZE);
        int canvasHeight = (int) (height * FACTOR_IMAGE_SIZE);

        if (canvasWidth > canvasHeight && canvasWidth > MAX_IMAGE_SIZE) {
            canvasWidth = MAX_IMAGE_SIZE;
            canvasHeight = (int) (MAX_IMAGE_SIZE / factor);
        } else if (canvasHeight > canvasWidth && canvasHeight > MAX_IMAGE_SIZE) {
            canvasWidth = (int) (MAX_IMAGE_SIZE * factor);
            canvasHeight = MAX_IMAGE_SIZE;
        }

        activity = gvrContext.getActivity();
        mView = createView(activity);
        mView.setVisibility(View.VISIBLE);
        mView.setLayoutParams(new ViewGroup.LayoutParams(canvasWidth, canvasHeight));
        if (mView.getParent() != null) {
            ViewGroup parent = (ViewGroup) mView.getParent();
            parent.removeView(mView);
        }
        mTextViewContainer = new LinearLayout(activity);
        mTextViewContainer.addView(mView);
        mTextViewContainer.setVisibility(View.VISIBLE);

        mTextViewContainer.measure(canvasWidth, canvasHeight);
        mTextViewContainer.layout(0, 0, canvasWidth, canvasHeight);
        mFrameListener = new BaseGVRSceneObject.GVRDrawFrameListenerImpl(this);
        gvrContext.registerDrawFrameListener(mFrameListener);

        GVRTexture texture = new GVRExternalTexture(gvrContext);
        GVRMaterial material = new GVRMaterial(gvrContext, GVRMaterial.GVRShaderType.OES.ID);
        material.setMainTexture(texture);
        getRenderData().setMaterial(material);

        mSurfaceTexture = new SurfaceTexture(texture.getId());
        mSurfaceTexture.setDefaultBufferSize(canvasWidth, canvasHeight);
        mSurface = new Surface(mSurfaceTexture);

        sReferenceCounter++;
        mCount = sReferenceCounter;
        mFirstFrame = true;
    }

    public void setRefreshFrequency(IntervalFrequency frequency) {
        if (NONE_REFRESH_INTERVAL == mRefreshInterval && IntervalFrequency.NONE != frequency) {
            // Install draw-frame listener if frequency is no longer NONE
            getGVRContext().unregisterDrawFrameListener(mFrameListener);
            getGVRContext().registerDrawFrameListener(mFrameListener);
        }
        switch (frequency) {
            case REALTIME:
                mRefreshInterval = REALTIME_REFRESH_INTERVAL;
                break;
            case HIGH:
                mRefreshInterval = HIGH_REFRESH_INTERVAL;
                break;
            case MEDIUM:
                mRefreshInterval = MEDIUM_REFRESH_INTERVAL;
                break;
            case LOW:
                mRefreshInterval = LOW_REFRESH_INTERVAL;
                break;
            case NONE:
                mRefreshInterval = NONE_REFRESH_INTERVAL;
                break;
            default:
                break;
        }
    }

    public IntervalFrequency getRefreshFrequency() {
        switch (mRefreshInterval) {
            case REALTIME_REFRESH_INTERVAL:
                return IntervalFrequency.REALTIME;
            case HIGH_REFRESH_INTERVAL:
                return IntervalFrequency.HIGH;
            case LOW_REFRESH_INTERVAL:
                return IntervalFrequency.LOW;
            case MEDIUM_REFRESH_INTERVAL:
                return IntervalFrequency.MEDIUM;
            default:
                return IntervalFrequency.NONE;
        }
    }

    private static final class GVRDrawFrameListenerImpl implements GVRDrawFrameListener {
        GVRDrawFrameListenerImpl(final BaseGVRSceneObject sceneObject) {
            mRef = new WeakReference<BaseGVRSceneObject>(sceneObject);
            mContext = sceneObject.getGVRContext();
        }

        @Override
        public void onDrawFrame(float frameTime) {
            final BaseGVRSceneObject sceneObject = mRef.get();
            if (null != sceneObject) {
                int refreshInterval = sceneObject.mRefreshInterval;
                if ((sceneObject.mFirstFrame || sceneObject.mIsChanged) &&
                        (REALTIME_REFRESH_INTERVAL == refreshInterval ||
                                (NONE_REFRESH_INTERVAL != refreshInterval
                                        && (++sceneObject.mCount % refreshInterval == 0)))) {

                    sceneObject.refresh();
                    if (!sceneObject.mFirstFrame) {
                        sceneObject.mCount = 0;
                    } else {
                        sceneObject.mFirstFrame = false;
                    }
                    sceneObject.mIsChanged = false;
                }
                if (NONE_REFRESH_INTERVAL == refreshInterval) {
                    mContext.unregisterDrawFrameListener(this);
                }
            } else {
                mContext.unregisterDrawFrameListener(this);
            }
        }

        private final WeakReference<BaseGVRSceneObject> mRef;
        private final GVRContext mContext;
    }

    private void refresh() {
        try {
            Canvas canvas = mSurface.lockCanvas(null);
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mTextViewContainer.draw(canvas);
            mSurface.unlockCanvasAndPost(canvas);
        } catch (Surface.OutOfResourcesException t) {
        }
        mSurfaceTexture.updateTexImage();
    }

    public void setHover(boolean hover) {
        if (hover) {
            if (!isPlaying() && !isFinished && isStopped) {
                isStopped = false;
                mAnimator.start();
            }
        } else {
            if ((isPlaying() || isFinished) && !isStopped) {
                isStopped = true;
                mAnimator.reverse();
            }
        }
    }

    public boolean isPlaying() {
        if (mAnimator != null) {
            return mAnimator.isRunning();
        } else {
            return false;
        }
    }

}
