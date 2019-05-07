package eu.lynxit.sampleapp

import org.gearvrf.scene_objects.GVRSphereSceneObject
import org.gearvrf.*
import java.util.*


open class BaseScene(gvrContext: GVRContext?) : GVRScene(gvrContext) {
    var mLeftPhotosphere: GVRSceneObject? = null
    var mRightPhotosphere: GVRSceneObject? = null

    init {
        addButton(8f)
        addButton(6f)
        addButton(4f)
        addButton(2f)
        addButton(0f)
        addButton(-10f)
        addButton(-15f)
        addButton(-20f)
        addButton(-25f)
        addButton(-30f)
        addButton(8f, -2f)
        addButton(6f, -2f)
        addButton(4f, -2f)
        addButton(2f, -2f)
        addButton(0f, -2f)
        addButton(-10f, -2f)
        addButton(-15f, -2f)
        addButton(-20f, -2f)
        addButton(-25f, -2f)
        addButton(-30f, -2f)
        initializeSpheres()
    }

    fun addButton(posx: Float, posY: Float = -1f) {
        this.addSceneObject(AndroidView(
            gvrContext,
            R.layout.activity_main,
            750,
            300,
            0.65f,
            1.0f,
            -2.6f,
            gvrContext.activity.resources.getColor(R.color.colorNavyBlue)
        ).apply {
            this.transform.positionZ = -2.5f
            this.transform.positionX = posx
            this.transform.positionY = posY
        })
    }

    fun initializeSpheres() {
        mLeftPhotosphere = createPhotosphere(Eye.LEFT)
        mRightPhotosphere = createPhotosphere(Eye.RIGHT)

        val textures =
            Pair(
                gvrContext?.assetLoader?.loadTexture(
                    GVRAndroidResource(
                        gvrContext.activity,
                        "phl.astc"
                    )
                ), gvrContext?.assetLoader?.loadTexture(
                    GVRAndroidResource(
                        gvrContext.activity,
                        "phr.astc"
                    )
                )
            )
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
