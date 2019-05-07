package eu.lynxit.sampleapp

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

        override fun onTouchEnd(sceneObj: GVRSceneObject?, collision: GVRPicker.GVRPickedObject?) {
        }

        override fun onInside(sceneObj: GVRSceneObject?, collision: GVRPicker.GVRPickedObject?) {
        }

        override fun onExit(sceneObj: GVRSceneObject?, collision: GVRPicker.GVRPickedObject?) {
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
            newController.addControllerEventListener { controller, isActive -> }
        }
        currentScene = NotABaseScene(gvrContext)
        gvrContext?.mainScene = currentScene
    }

    override fun onStep() {
        //nothign to do
    }

}
