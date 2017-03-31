package eu.lynxit.sampleapp;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.gearvrf.GVRActivity;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRDrawFrameListener;
import org.gearvrf.GVRExternalTexture;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;

import java.lang.ref.WeakReference;

public class PresentationThumbnailGVRSceneObject extends GVRSceneObject {
    private static final int REALTIME_REFRESH_INTERVAL = 1;
    private static final int HIGH_REFRESH_INTERVAL = 10; // frames
    private static final int MEDIUM_REFRESH_INTERVAL = 20;
    private static final int LOW_REFRESH_INTERVAL = 30;
    private static final int NONE_REFRESH_INTERVAL = 0;

    private static final float DEFAULT_QUAD_WIDTH = 2.0f;
    private static final float DEFAULT_QUAD_HEIGHT = 1.0f;
    private static final String DEFAULT_TEXT = "";
    private static final int FACTOR_IMAGE_SIZE = 128;
    private static final int MAX_IMAGE_SIZE = 4 * FACTOR_IMAGE_SIZE;

    private static int sReferenceCounter = 0;// This is for load balancing.
    private boolean mFirstFrame;
    private boolean mIsChanged;
    private volatile int mRefreshInterval = REALTIME_REFRESH_INTERVAL;

    private final Surface mSurface;
    private final SurfaceTexture mSurfaceTexture;
    private final LinearLayout mTextViewContainer;
    private final ThumbnailLoaderView mButtonView;

    private int mCount;
    private final PresentationThumbnailGVRSceneObject.GVRDrawFrameListenerImpl mFrameListener;
    public String objectName;
    private ValueAnimator mAnimator;
    int imageWidth;
    int imageHeight;
    boolean isFinished = false;
    private boolean isStopped = true;
    private float progress = 0;
    final GVRActivity activity;
    private SceneObjectEventListener mListener;
    private String mText;

    public PresentationThumbnailGVRSceneObject(GVRContext gvrContext, float width, float height, String text) {
        super(gvrContext, gvrContext.createQuad(width, height));
        mText = text;
        //cap the canvas dimensions
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
        mButtonView = new ThumbnailLoaderView(activity);
        //mButtonView.setBackgroundColor(Color.TRANSPARENT);
        mButtonView.setVisibility(View.VISIBLE);
        mButtonView.setLayoutParams(new ViewGroup.LayoutParams(canvasWidth, canvasHeight));
        if (mButtonView.getParent() != null) {
            ViewGroup parent = (ViewGroup) mButtonView.getParent();
            parent.removeView(mButtonView);
            parent = null;
        }
        mTextViewContainer = new LinearLayout(activity);
        mTextViewContainer.addView(mButtonView);
        mTextViewContainer.setVisibility(View.VISIBLE);

        mTextViewContainer.measure(canvasWidth, canvasHeight);
        mTextViewContainer.layout(0, 0, canvasWidth, canvasHeight);
        imageWidth = mTextViewContainer.getMeasuredWidth() > 0 ? (int) mTextViewContainer.getMeasuredWidth() : 960;
        imageHeight = mTextViewContainer.getMeasuredHeight() > 0 ? (int) mTextViewContainer.getMeasuredHeight() : 1920;
        Glide.with(activity.getApplicationContext()).load(Uri.parse("file:///android_asset/sphere-placeholder.png")).asBitmap().into(
                new SimpleTarget<Bitmap>(imageWidth, imageHeight) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mButtonView.setDefaultBitmap(resource);
                        mIsChanged = true;
                    }
                });
        mFrameListener = new PresentationThumbnailGVRSceneObject.GVRDrawFrameListenerImpl(this);
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
        mAnimator = ValueAnimator.ofFloat(0.0F, 1.0F);
        mAnimator.setDuration(3000);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                float fraction = ((ValueAnimator) animation).getAnimatedFraction();
                if (fraction == 1 && mListener != null) {
                    //isFinished = true; //TODO DISABLED BECAUSE THERE IS NO ACTION AFTER FINISHED LOADING IN THIS SAMPLE
                    mListener.onLoadingFinished(PresentationThumbnailGVRSceneObject.this);
                    removePresentation();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (float) animation.getAnimatedValue();
                Log.d("Rychu", "Progress "+progress+", "+mText);
                mButtonView.setProgress(progress);
                mIsChanged = true;
            }
        });
    }

    public PresentationThumbnailGVRSceneObject(String name, GVRContext gvrContext, float width, float height, String text) {
        this(gvrContext, width, height, text);
        objectName = name;
    }

    public PresentationThumbnailGVRSceneObject(GVRContext gvrContext, String text) {
        this(gvrContext, DEFAULT_QUAD_WIDTH, DEFAULT_QUAD_HEIGHT, text);
    }

    public PresentationThumbnailGVRSceneObject(GVRContext gvrContext) {
        this(gvrContext, DEFAULT_TEXT);
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

    public String getName() {
        return objectName;
    }

    private static final class GVRDrawFrameListenerImpl implements GVRDrawFrameListener {
        GVRDrawFrameListenerImpl(final PresentationThumbnailGVRSceneObject sceneObject) {
            mRef = new WeakReference<PresentationThumbnailGVRSceneObject>(sceneObject);
            mContext = sceneObject.getGVRContext();
        }

        @Override
        public void onDrawFrame(float frameTime) {
            final PresentationThumbnailGVRSceneObject sceneObject = mRef.get();
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

        private final WeakReference<PresentationThumbnailGVRSceneObject> mRef;
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
            if (isPlaying() && !isStopped) {
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

    public void removePresentation() {
        isStopped = true;
        isFinished = false;
        progress = 0;
        //mButtonView.updateThumbnail(null);
        mButtonView.setProgress(progress);
        mIsChanged = true;
    }

    public void updatePresentation(final String url) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(activity.getApplicationContext()).load(url).asBitmap().into(
                        new SimpleTarget<Bitmap>(imageWidth, imageHeight) {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                mButtonView.updateThumbnail(resource);
                                mIsChanged = true;
                            }
                        });
            }
        });
    }

    public void addListener(SceneObjectEventListener listener) {
        mListener = listener;
    }
}
