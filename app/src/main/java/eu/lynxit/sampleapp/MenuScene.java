package eu.lynxit.sampleapp;

import android.graphics.Color;
import android.view.View;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRExternalTexture;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMaterialShaderManager;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRShaderTemplate;
import org.gearvrf.io.cursor3d.CursorEvent;
import org.gearvrf.io.cursor3d.CursorManager;
import org.gearvrf.io.cursor3d.SelectableBehavior;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.scene_objects.GVRViewSceneObject;
import org.gearvrf.scene_objects.view.GVRFrameLayout;
import org.gearvrf.utility.Log;


public class MenuScene extends BaseScene {
    public GVRSphereSceneObject mainPhotosphere;

    private int frameWidth;
    private int frameHeight;

    public MenuScene(final GVRContext gvrContext, CursorManager cursorManager, ApplicationMain applicationMain, MainActivity activity) {
        super(gvrContext, cursorManager, applicationMain, activity);
        //createPhotosphere();
//        for (int row = 0; row < 3; row++) {
//            for (int col = 0; col < 3; col++) {
//                final int finalCol = col;
//                final int finalRow = row;
//                gvrActivity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        createCustomThumbnail(finalCol, finalRow);
//                    }
//                });
//            }
//        }
        try {
            gvrActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextButtonGVRSceneObject textButtonGVRSceneObject = new TextButtonGVRSceneObject(gvrContext, 2f, 1f, "TEST");
                    //textButtonGVRSceneObject.attachComponent(new SelectableBehavior(cursorManager));
                    textButtonGVRSceneObject.getTransform().setPosition(0, -375, -700);
                    textButtonGVRSceneObject.getRenderData().setRenderingOrder(GVRRenderData.GVRRenderingOrder.TRANSPARENT + 15);
                    //textButtonGVRSceneObject.setRefreshFrequency(IntervalFrequency.REALTIME);
                    textButtonGVRSceneObject.getTransform().setScale(120F, 120F, 0);
                    frameWidth = 800;//frameLayout.getWidth();
                    frameHeight = 200;//frameLayout.getHeight();

                    Log.d("gvrf", "wxh: " + frameWidth + "x" + frameHeight);
                    float texelWidth = 1.0f / (float) frameWidth;
                    float texelHeight = 1.0f / (float) frameHeight;
                    float[] convolutionMatrix = {
                            1.0f, 1.0f, 1.0f, 1.0f,
                            1.0f, 1.0f, 1.0f, 1.0f,
                            1.0f, 1.0f, 1.0f, 1.0f,
                            1.0f, 1.0f, 1.0f, 1.0f
                    };

                    GVRRenderData renderData = textButtonGVRSceneObject.getRenderData();
                    GVRMaterial material = renderData.getMaterial();
                    GVRExternalTexture externalTexture = (GVRExternalTexture) material.getMainTexture();

                    GVRMaterialShaderManager shaderManager = gvrContext.getMaterialShaderManager();

                    GVRShaderTemplate convolutionShader = shaderManager.retrieveShaderTemplate(ConvolutionShader.class);
                    material.setFloat("texelWidth", texelWidth);
                    material.setFloat("texelHeight", texelHeight);
                    material.setMat4("convolutionMatrix",
                            convolutionMatrix[0], convolutionMatrix[1], convolutionMatrix[2], convolutionMatrix[3],
                            convolutionMatrix[4], convolutionMatrix[5], convolutionMatrix[6], convolutionMatrix[7],
                            convolutionMatrix[8], convolutionMatrix[9], convolutionMatrix[10], convolutionMatrix[11],
                            convolutionMatrix[12], convolutionMatrix[13], convolutionMatrix[14], convolutionMatrix[15]
                    );
                    material.setTexture("main_texture", externalTexture);
                    renderData.setShaderTemplate(ConvolutionShader.class);
                    convolutionShader.bindShader(gvrContext, renderData, MenuScene.this);

                    GVRSceneObject wrapper = new GVRSceneObject(gvrContext);
                    //wrapper.getTransform().setPosition(0.0f, -375f, -700);
                    //wrapper.addChildObject(textButtonGVRSceneObject);
                    addSceneObject(textButtonGVRSceneObject);

                }
            });
        } catch (Exception e) {
            Log.d("Lynx", "errr " + e.getMessage());
        }
    }

    @Override
    public void onEvent(CursorEvent event) {
        if (event.getObject() instanceof PresentationThumbnailGVRSceneObject) {
            ((PresentationThumbnailGVRSceneObject) event.getObject()).setHover(event.isOver());
        } else if (event.getObject() instanceof TextButtonGVRSceneObject) {
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
        PresentationThumbnailGVRSceneObject thumbnailGVRSceneObject = new PresentationThumbnailGVRSceneObject(gvrContext, 2f, 1f, ("C" + col + ",R" + row));
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
