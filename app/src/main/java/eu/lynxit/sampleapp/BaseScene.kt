package eu.lynxit.sampleapp

import android.graphics.Color
import android.view.View
import org.gearvrf.scene_objects.GVRSphereSceneObject
import org.gearvrf.scene_objects.GVRViewSceneObject
import android.widget.FrameLayout
import org.gearvrf.*
import java.util.*


open class BaseScene(gvrContext: GVRContext?) : GVRScene(gvrContext) {
    var mLeftPhotosphere: GVRSceneObject? = null
    var mRightPhotosphere: GVRSceneObject? = null

    var rn: Random?=null

    init {
        rn = Random()
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
        addButton(8f,-2f)
        addButton(6f,-2f)
        addButton(4f,-2f)
        addButton(2f,-2f)
        addButton(0f,-2f)
        addButton(-10f,-2f)
        addButton(-15f,-2f)
        addButton(-20f,-2f)
        addButton(-25f,-2f)
        addButton(-30f,-2f)
        initializeSpheres()
    }

    fun addButton(posx:Float, posY:Float=-1f) {
        gvrContext.activity.runOnUiThread {
            val frameLayout = FrameLayout(gvrContext.activity)
            (gvrContext.activity as GVRActivity).registerView(frameLayout)
            View.inflate(gvrContext.activity, R.layout.activity_main, frameLayout)
            frameLayout.setBackgroundColor(Color.WHITE)
            frameLayout.layoutParams.width = 750
            frameLayout.layoutParams.height = 300
            (gvrContext.activity as GVRActivity).unregisterView(frameLayout)

            val sceneObject =
                GVRViewSceneObject(gvrContext, frameLayout, gvrContext.createQuad(1.5f, 1f))
            sceneObject.transform.positionZ = -2.5f
            sceneObject.transform.positionX = posx
            sceneObject.transform.positionY = posY

            gvrContext.mainScene.addSceneObject(sceneObject)
        }
    }

    var variant = 0
    fun change() {
        variant = when (variant) {
            0 -> 1
            1 -> 2
            2 -> 3
            3 -> 4
            else -> 0
        }
        val textures = getTexture(variant)
        mLeftPhotosphere?.renderData?.material?.mainTexture = textures.first
        mRightPhotosphere?.renderData?.material?.mainTexture = textures.second
    }

    fun initializeSpheres() {
        mLeftPhotosphere = createPhotosphere(Eye.LEFT)
        mRightPhotosphere = createPhotosphere(Eye.RIGHT)

        val textures = getTexture(rn?.nextInt(4)?:0)
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
    fun getTexture(variant: Int): Pair<GVRTexture?, GVRTexture?> {
        return when (variant) {
            0 -> {
                val tmp = gvrContext?.assetLoader?.loadTexture(
                    GVRAndroidResource(
                        gvrContext.activity,
                        "phl.astc"
                    )
                )
                val tmp2 = gvrContext?.assetLoader?.loadTexture(
                    GVRAndroidResource(
                        gvrContext.activity,
                        "phr.astc"
                    )
                )
                Pair(tmp, tmp2)
            }
            1 -> {
                val tmp = gvrContext?.assetLoader?.loadTexture(
                    GVRAndroidResource(
                        gvrContext.activity,
                        "p1.JPG"
                    )
                )

                val tmp2 = gvrContext?.assetLoader?.loadTexture(
                    GVRAndroidResource(
                        gvrContext.activity,
                        "p2.JPG"
                    )
                )
                Pair(tmp, tmp2)
            }
            2 -> {
                val tmp = gvrContext?.assetLoader?.loadTexture(
                    GVRAndroidResource(
                        gvrContext.activity,
                        "3.JPG"
                    )
                )

                val tmp2 = gvrContext?.assetLoader?.loadTexture(
                    GVRAndroidResource(
                        gvrContext.activity,
                        "p4.JPG"
                    )
                )
                Pair(tmp, tmp2)
            }
            3 -> {
                val tmp = gvrContext?.assetLoader?.loadTexture(
                    GVRAndroidResource(
                        gvrContext.activity,
                        "p5.JPG"
                    )
                )

                val tmp2 = gvrContext?.assetLoader?.loadTexture(
                    GVRAndroidResource(
                        gvrContext.activity,
                        "p6.JPG"
                    )
                )
                Pair(tmp, tmp2)
            }
            else -> {
                val tmp = gvrContext?.assetLoader?.loadTexture(
                    GVRAndroidResource(
                        gvrContext.activity,
                        "phl.astc"
                    )
                )
                val tmp2 = gvrContext?.assetLoader?.loadTexture(
                    GVRAndroidResource(
                        gvrContext.activity,
                        "phr.astc"
                    )
                )
                Pair(tmp, tmp2)
            }
        }
    }
}
