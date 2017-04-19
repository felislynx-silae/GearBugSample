package eu.lynxit.sampleapp;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRRenderData;
import org.gearvrf.io.cursor3d.CursorEvent;
import org.gearvrf.io.cursor3d.CursorManager;
import org.gearvrf.io.cursor3d.SelectableBehavior;
import org.gearvrf.scene_objects.GVRSphereSceneObject;

public class MenuScene extends BaseScene {
    public GVRSphereSceneObject mainPhotosphere;

    public MenuScene(GVRContext gvrContext, CursorManager cursorManager, ApplicationMain applicationMain, MainActivity activity) {
        super(gvrContext, cursorManager, applicationMain, activity);
        createPhotosphere();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                final int finalCol = col;
                final int finalRow = row;
                gvrActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        createCustomThumbnail(finalCol, finalRow);
                    }
                });
            }
        }
        TextButtonGVRSceneObject textButtonGVRSceneObject = new TextButtonGVRSceneObject(gvrContext, 3f, 1f, "TEST");
        textButtonGVRSceneObject.attachComponent(new SelectableBehavior(cursorManager));
        textButtonGVRSceneObject.getTransform().setPosition(0, -375, -700);
        textButtonGVRSceneObject.getRenderData().setRenderingOrder(GVRRenderData.GVRRenderingOrder.TRANSPARENT + 15);
        textButtonGVRSceneObject.setRefreshFrequency(IntervalFrequency.REALTIME);
        textButtonGVRSceneObject.getTransform().setScale(80F, 80F, 0);
        addSceneObject(textButtonGVRSceneObject);
    }

    @Override
    public void onEvent(CursorEvent event) {
        if (event.getObject() instanceof PresentationThumbnailGVRSceneObject) {
            ((PresentationThumbnailGVRSceneObject) event.getObject()).setHover(event.isOver());
        } else if (event.getObject() instanceof TextButtonGVRSceneObject){
            ((TextButtonGVRSceneObject) event.getObject()).hover(event.isOver());
        }
    }

    private void createPhotosphere() {
        mainPhotosphere = new GVRSphereSceneObject(gvrContext, false);
        mainPhotosphere.getRenderData().getMaterial().setMainTexture(main.mBackSphere);
        mainPhotosphere.getTransform().setScale(1000.0f, 1000.0f, 1000.0f);
        mainPhotosphere.getRenderData().setRenderingOrder(GVRRenderData.GVRRenderingOrder.BACKGROUND);
        mainPhotosphere.getTransform().setRotation(1, 0, 0, 0);
        mainPhotosphere.getTransform().rotateByAxis(90, 0f, 1f, 0f);
        addSceneObject(mainPhotosphere);
    }

    private void createCustomThumbnail(int col, int row) {
        PresentationThumbnailGVRSceneObject thumbnailGVRSceneObject = new PresentationThumbnailGVRSceneObject(gvrContext, 2f, 1f, ("C"+col+",R"+row));
        thumbnailGVRSceneObject.addListener(new SceneObjectEventListener() {
            @Override
            public void onLoadingFinished(final PresentationThumbnailGVRSceneObject object) {
            }
        });
        thumbnailGVRSceneObject.getTransform().setScale(125.0F, 125.0F, 125.0F);
        thumbnailGVRSceneObject.getTransform().setPosition(convertPixelToVRFloatValue(
                (col - (3 - 1) / 2.0f) * (200 + 100)),
                convertPixelToVRFloatValue(-50 +
                        ((3 - 1) / 2.0f - row) * (100 + 50)), -700);
        thumbnailGVRSceneObject.getRenderData().setRenderingOrder(GVRRenderData.GVRRenderingOrder.TRANSPARENT + 30);
        thumbnailGVRSceneObject.attachComponent(new SelectableBehavior(cursorManager));
        addSceneObject(thumbnailGVRSceneObject);
        thumbnailGVRSceneObject.updatePresentation("http://lynxit.eu/images/logo-promo-big.png");
    }

    public static float convertPixelToVRFloatValue(float pixel) {
        return pixel * 0.934f;
    }
}
