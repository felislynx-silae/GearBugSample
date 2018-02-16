package eu.lynxit.sampleapp;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRScene;
import org.gearvrf.io.cursor3d.CursorEventListener;
import org.gearvrf.io.cursor3d.CursorManager;

public abstract class BaseScene extends GVRScene implements CursorEventListener {
    protected final GVRContext gvrContext;
    protected final MainActivity gvrActivity;
    protected final CursorManager cursorManager;
    protected final ApplicationMain main;

    /**
     * Constructs a scene with a camera rig holding left & right cameras in it.
     *
     * @param gvrContext {@link GVRContext} the app is using.
     */
    public BaseScene(final GVRContext gvrContext, CursorManager cursorManager, final ApplicationMain applicationMain, MainActivity activity) {
        super(gvrContext);
        this.gvrContext = gvrContext;
        this.gvrActivity = activity;
        this.main = applicationMain;
        this.cursorManager = cursorManager;

    }

    public void onStart() {
        if (main.getCursor() != null)
            this.main.getCursor().addCursorEventListener(this);
    }

    public void onEnd() {
        if (main.getCursor() != null)
            this.main.getCursor().removeCursorEventListener(this);
    }
}
