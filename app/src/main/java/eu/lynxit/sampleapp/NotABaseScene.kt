package eu.lynxit.sampleapp

import org.gearvrf.GVRContext

class NotABaseScene(gvrContext: GVRContext?, par:Int):BaseScene(gvrContext){
    init {
        addButton(8f,-4f+par)
        addButton(6f,-4f+par)
        addButton(4f,-4f+par)
        addButton(2f,-4f+par)
        addButton(0f,-4f+par)
        addButton(-10f,-4f+par)
        addButton(-15f,-4f+par)
        addButton(-20f,-4f+par)
        addButton(-25f,-4f+par)
        addButton(-30f,-4f+par)
        addButton(8f,-6f+par)
        addButton(6f,-6f+par)
        addButton(4f,-6f+par)
        addButton(2f,-6f+par)
        addButton(0f,-6f+par)
        addButton(-10f,-6f+par)
        addButton(-15f,-6f+par)
        addButton(-20f,-6f+par)
        addButton(-25f,-6f+par)
        addButton(-30f,-6f+par)
        initializeSpheres()
    }
}