package eu.lynxit.sampleapp

import android.util.Log
import android.view.MotionEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import org.gearvrf.*
import org.gearvrf.io.GVRCursorController
import java.util.*

class ApplicationMain(val baseActivity: MainActivity) : GVRMain() {
    var counter = 0
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
        currentScene = NotABaseScene(gvrContext,0)
        gvrContext?.mainScene = currentScene
        changeScene()
    }

    fun changeScene() {
        GlobalScope.async {
            gvrContext?.runOnGlThread {
                try {
                    gvrContext?.activity?.runOnUiThread {
                        (currentScene as NotABaseScene).sceneObjects?.filter { it is AndroidView }
                            ?.forEach {
                                (it as AndroidView).clear()
                            }
                        (currentScene as NotABaseScene).sceneObjects?.forEach {
                            currentScene?.removeSceneObject(it)
                        }
                    }
                } catch (e:Exception){
                    Log.d("DEBUG","ERROR: ${e.message}")
                }
                counter += 1
                Log.d("DEBUG", "Scene changed: $counter times")
                currentScene = NotABaseScene(gvrContext, counter)
                gvrContext?.mainScene = currentScene
            }
            delay(3000)
            changeScene()
        }
    }

    override fun onStep() {
        //nothing to do
    }

}
