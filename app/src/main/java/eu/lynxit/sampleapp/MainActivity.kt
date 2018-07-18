package eu.lynxit.sampleapp

import android.os.Bundle

import org.gearvrf.GVRActivity

class MainActivity : GVRActivity() {
    private var main: ApplicationMain? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        main = ApplicationMain(this)
        setMain(main, "gvr.xml")
    }
}
