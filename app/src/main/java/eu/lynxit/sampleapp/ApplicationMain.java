package eu.lynxit.sampleapp;

import android.util.Log;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRTexture;
import org.gearvrf.io.cursor3d.Cursor;
import org.gearvrf.io.cursor3d.CursorEvent;
import org.gearvrf.io.cursor3d.CursorEventListener;
import org.gearvrf.io.cursor3d.CursorManager;

import java.util.concurrent.Future;

public class ApplicationMain extends GVRMain {

    protected Cursor cursor;
    protected CursorManager cursorManager;
    public Future<GVRTexture> mBackSphere;
    private CursorEventListener mListener = new CursorEventListener() {
        @Override
        public void onEvent(CursorEvent event) {
        }
    };

    private MainActivity baseActivity;

    public ApplicationMain(MainActivity activity) {
        baseActivity = activity;
    }

    @Override
    public void onInit(final GVRContext gvrContext) throws Throwable {
        cursorManager = new CursorManager(gvrContext);
        cursor = cursorManager.getActiveCursors().get(0);
        assert cursor != null : "No cursors found!";
        cursor.addCursorEventListener(mListener);
        mBackSphere = gvrContext.loadFutureTexture(new GVRAndroidResource(gvrContext, R.raw.sphere_placeholder));
        MenuScene scene = new MenuScene(gvrContext, cursorManager, this, baseActivity);
        cursorManager.setScene(scene);
        getGVRContext().setMainScene(scene);
        scene.onStart();
    }

    @Override
    public void onStep() {
        //nothign to do
    }

    public Cursor getCursor() {
        return cursor;
    }
}
