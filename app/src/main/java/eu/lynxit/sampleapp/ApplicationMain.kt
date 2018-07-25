package eu.lynxit.sampleapp

import android.util.Log
import android.view.MotionEvent
import org.gearvrf.*
import org.gearvrf.io.GVRCursorController
import java.util.*

class ApplicationMain(val baseActivity: MainActivity) : GVRMain() {

    var cursor: GVRSceneObject? = null
    var controller: GVRCursorController? = null
    val mPickHandler = object : ITouchEvents {

        override fun onEnter(sceneObj: GVRSceneObject?, collision: GVRPicker.GVRPickedObject?) {
        }

        override fun onMotionOutside(picker: GVRPicker?, motionEvent: MotionEvent?) {
        }

        override fun onTouchStart(
            sceneObj: GVRSceneObject?,
            collision: GVRPicker.GVRPickedObject?
        ) {
        }

        var time = 0L
        override fun onTouchEnd(sceneObj: GVRSceneObject?, collision: GVRPicker.GVRPickedObject?) {
            val newTime = System.currentTimeMillis()
            if (newTime - time > 1000) {
                time = newTime
                currentScene?.change()
                //currentScene = BaseScene(gvrContext)
                //gvrContext?.mainScene = currentScene
            }
        }

        override fun onInside(sceneObj: GVRSceneObject?, collision: GVRPicker.GVRPickedObject?) {
        }

        override fun onExit(sceneObj: GVRSceneObject?, collision: GVRPicker.GVRPickedObject?) {
        }

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
                        "p2.JPG"
                    )
                )
                Pair(tmp, tmp)
            }
            else -> {
                val tmp = gvrContext?.assetLoader?.loadTexture(
                    GVRAndroidResource(
                        gvrContext.activity,
                        "p3.JPG"
                    )
                )
                Pair(tmp, tmp)
            }
        }
    }

    var currentScene: BaseScene? = null
    @Throws(Throwable::class)
    override fun onInit(gvrContext: GVRContext?) {
        gvrContext?.mainScene?.eventReceiver?.addListener(mPickHandler)
        val inputManager = gvrContext?.inputManager
        cursor = GVRSceneObject(
            gvrContext, gvrContext?.createQuad(1f, 1f),
            gvrContext?.assetLoader?.loadTexture(
                GVRAndroidResource(gvrContext, R.drawable.theme_red_dot)
            )
        )
        cursor?.transform?.positionZ = -8f
        cursor?.renderData?.depthTest = false
        cursor?.renderData?.renderingOrder = GVRRenderData.GVRRenderingOrder.OVERLAY
        val eventOptions = EnumSet.of(
            GVRPicker.EventOptions.SEND_TOUCH_EVENTS,
            GVRPicker.EventOptions.SEND_TO_LISTENERS
        )
        inputManager?.selectController { newController, oldController ->
            oldController?.removePickEventListener(mPickHandler)
            controller = newController
            newController.addPickEventListener(mPickHandler)
            newController.cursor = cursor
            newController.cursorDepth = -8f
            newController.cursorControl =
                    GVRCursorController.CursorControl.PROJECT_CURSOR_ON_SURFACE
            newController.picker.eventOptions = eventOptions
            newController.setScene(currentScene)
            newController.addControllerEventListener(object:GVRCursorController.IControllerEvent{
                var time = 0L
                override fun onEvent(controller: GVRCursorController?, isActive: Boolean) {
                    if(controller?.keyEvent?.keyCode == 29) {val newTime = System.currentTimeMillis()
                        if (newTime - time > 1000) {
                            time = newTime
                            currentScene?.change()
                        }
                    }
                }

            })
        }
        currentScene = NotABaseScene(gvrContext)
        gvrContext?.mainScene = currentScene
    }

    override fun onStep() {
        //nothign to do
    }

}
