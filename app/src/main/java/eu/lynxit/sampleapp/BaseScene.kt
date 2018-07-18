package eu.lynxit.sampleapp

import android.graphics.Color
import android.view.View
import org.gearvrf.GVRContext
import org.gearvrf.GVRRenderData
import org.gearvrf.GVRScene
import org.gearvrf.GVRSceneObject
import org.gearvrf.scene_objects.GVRSphereSceneObject
import org.gearvrf.scene_objects.GVRViewSceneObject
import android.widget.FrameLayout


class BaseScene(gvrContext: GVRContext?) : GVRScene(gvrContext) {
    var mLeftPhotosphere: GVRSceneObject? = null
    var mRightPhotosphere: GVRSceneObject? = null

    init {
        addButton(-2f)
        addButton(2f)
        addButton(0f)
        addButton(-10f)
        addButton(-15f)
        initializeSpheres()
    }

    fun addButton(posx:Float) {
        gvrContext.activity.runOnUiThread {
            val frameLayout = FrameLayout(gvrContext.activity)
            gvrContext.activity.registerView(frameLayout)
            View.inflate(gvrContext.activity, R.layout.activity_main, frameLayout)
            frameLayout.setBackgroundColor(Color.WHITE)
            frameLayout.layoutParams.width = 750
            frameLayout.layoutParams.height = 300
            gvrContext.activity.unregisterView(frameLayout)

            val sceneObject =
                GVRViewSceneObject(gvrContext, frameLayout, gvrContext.createQuad(1.5f, 1f))
            sceneObject.transform.positionZ = -2.5f
            sceneObject.transform.positionX = posx
            sceneObject.transform.positionY = -1f

            gvrContext.mainScene.addSceneObject(sceneObject)
        }
    }

    var variant = 0
    fun change() {
        variant = when (variant) {
            0 -> 1
            1 -> 2
            else -> 0
        }
        val textures = (gvrContext.activity.main as ApplicationMain).getTexture(variant)
        mLeftPhotosphere?.renderData?.material?.mainTexture = textures.first
        mRightPhotosphere?.renderData?.material?.mainTexture = textures.second
    }

    fun initializeSpheres() {
        mLeftPhotosphere = createPhotosphere(Eye.LEFT)
        mRightPhotosphere = createPhotosphere(Eye.RIGHT)

        val textures = (gvrContext.activity.main as ApplicationMain).getTexture(2)
        mLeftPhotosphere?.renderData?.material?.mainTexture = textures.first
        mRightPhotosphere?.renderData?.material?.mainTexture = textures.second
        this.addSceneObject(mLeftPhotosphere)
        this.addSceneObject(mRightPhotosphere)
    }

    fun createPhotosphere(eye: Eye): GVRSphereSceneObject {
        val photosphere = GVRSphereSceneObject(gvrContext, 90, 60, false)
        val scale = 1000.0f
        photosphere.transform.setScale(scale, scale, scale)
        photosphere.renderData.renderingOrder = GVRRenderData.GVRRenderingOrder.BACKGROUND
        photosphere.renderData.renderMask =
                if (eye == Eye.LEFT) GVRRenderData.GVRRenderMaskBit.Left else GVRRenderData.GVRRenderMaskBit.Right
        photosphere.transform.setRotation(1f, 0f, 0f, 0f)
        photosphere.transform.rotateByAxis(90f, 0f, 1f, 0f)
        return photosphere
    }

    enum class Eye {
        LEFT, RIGHT
    }
}
