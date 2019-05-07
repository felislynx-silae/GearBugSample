package eu.lynxit.sampleapp

import android.view.MotionEvent
import android.view.View
import org.gearvrf.*
import org.gearvrf.scene_objects.GVRViewSceneObject
import android.widget.FrameLayout

/**
 * Created by paulinakornatowicz on 7/13/17.
 */
class AndroidView(
    val mContext: GVRContext,
    val mLayoutId: Int,
    val mWidth: Int,
    val mHeight: Int,
    val mQuadX: Float,
    val mQuadY: Float,
    val mPositionZ: Float,
    val color: Int,
    val useCollider: Boolean = true
) : GVRViewSceneObject(
    mContext,
    initLayout(mContext.activity as MainActivity, mWidth, mHeight, mLayoutId, color),
    mContext.createQuad(mQuadX, mQuadY)
) {
    init {
        if(!useCollider){
            this.detachCollider()
        }
        initializeView()
    }

    fun setViewListener(viewId: Int, listener: View.OnClickListener) {
        view?.findViewById<View>(viewId)?.setOnClickListener(listener)
    }

    private fun initializeView() {
        transform.positionZ = mPositionZ
        val propertiesCenter = MotionEvent.PointerProperties()
        propertiesCenter.id = 0
        propertiesCenter.toolType = MotionEvent.TOOL_TYPE_MOUSE
    }

    fun setBufferScale(scale: Int) {
        setTextureBufferSize(Math.min(4096, scale * Math.max(mWidth, mHeight)))
    }
    fun clear(){
        (view as FrameLayout?)?.removeAllViews()
    }
}


fun initLayout(
    activity: MainActivity,
    width: Int,
    height: Int,
    layoutId: Int,
    color: Int
): FrameLayout {
    val frameLayout = FrameLayout(activity)
    View.inflate(activity, layoutId, frameLayout)
    activity.runOnUiThread {
        activity.registerView(frameLayout)
        frameLayout.setBackgroundColor(color)
        frameLayout.layoutParams = FrameLayout.LayoutParams(width, height)
        activity.unregisterView(frameLayout)
    }
    return frameLayout
}
