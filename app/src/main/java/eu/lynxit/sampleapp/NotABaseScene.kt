package eu.lynxit.sampleapp

import org.gearvrf.GVRContext

class NotABaseScene(gvrContext: GVRContext?):BaseScene(gvrContext){
    init {
        addButton(8f,-4f)
        addButton(6f,-4f)
        addButton(4f,-4f)
        addButton(2f,-4f)
        addButton(0f,-4f)
        addButton(-10f,-4f)
        addButton(-15f,-4f)
        addButton(-20f,-4f)
        addButton(-25f,-4f)
        addButton(-30f,-4f)
        addButton(8f,-6f)
        addButton(6f,-6f)
        addButton(4f,-6f)
        addButton(2f,-6f)
        addButton(0f,-6f)
        addButton(-10f,-6f)
        addButton(-15f,-6f)
        addButton(-20f,-6f)
        addButton(-25f,-6f)
        addButton(-30f,-6f)
        initializeSpheres()
    }
}