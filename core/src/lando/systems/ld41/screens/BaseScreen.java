package lando.systems.ld41.screens;

import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.utils.Config;

/**
 * Created by Brian on 7/25/2017
 */
public abstract class BaseScreen extends InputAdapter {

    public final LudumDare41 game;

    public boolean allowInput;
    public MutableFloat alpha;
    public OrthographicCamera worldCamera;
    public OrthographicCamera hudCamera;

    private static final float ZOOM_SCALE = 0.1f;
    private static final float MAX_ZOOM = 5f;
    private static final float MIN_ZOOM = 0.5f;
    private static final float DRAG_DELTA = 10f;
    private static final float ZOOM_LERP = .1f;
    private static final float PAN_LERP = .2f;
    protected boolean cancelTouchUp = false;
    protected Vector3 cameraTouchStart = new Vector3();
    protected Vector3 touchStart = new Vector3();

    public Vector3 cameraTargetPos = new Vector3();
    public MutableFloat targetZoom = new MutableFloat(1f);

    public BaseScreen () {
        super();
        this.game = LudumDare41.game;

        this.allowInput = false;
        this.alpha = new MutableFloat(0f);

        float aspect = (float) Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();
        this.worldCamera = new OrthographicCamera(Config.gameWidth, Config.gameWidth / aspect);
        this.worldCamera.translate(this.worldCamera.viewportWidth / 2f, this.worldCamera.viewportHeight / 2f, 0f);
        this.worldCamera.update();

        this.hudCamera = new OrthographicCamera(Config.gameWidth, Config.gameWidth / aspect);
        this.hudCamera.translate(this.hudCamera.viewportWidth / 2f, this.hudCamera.viewportHeight / 2f, 0f);
        this.hudCamera.update();
    }

    public abstract void update(float dt);
    public abstract void render(SpriteBatch batch);

    protected void updateCamera() {
        worldCamera.zoom = MathUtils.lerp(worldCamera.zoom, targetZoom.floatValue(), ZOOM_LERP);
        worldCamera.zoom = MathUtils.clamp(worldCamera.zoom, MIN_ZOOM, MAX_ZOOM);

        worldCamera.position.x = MathUtils.lerp(worldCamera.position.x, cameraTargetPos.x, PAN_LERP);
        worldCamera.position.y = MathUtils.lerp(worldCamera.position.y, cameraTargetPos.y, PAN_LERP);
        worldCamera.update();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        cameraTouchStart.set(worldCamera.position);
        touchStart.set(screenX, screenY, 0);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (cancelTouchUp) {
            cancelTouchUp = false;
            return false;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        cameraTargetPos.x = cameraTouchStart.x + (touchStart.x - screenX) * worldCamera.zoom;
        cameraTargetPos.y = cameraTouchStart.y + (screenY - touchStart.y) * worldCamera.zoom;
        if (cameraTouchStart.dst(cameraTargetPos) > DRAG_DELTA) {
            cancelTouchUp = true;
        }
        return true;
    }

    @Override
    public boolean scrolled (int change) {
        targetZoom.setValue(targetZoom.floatValue() + change * targetZoom.floatValue() * ZOOM_SCALE);
        targetZoom.setValue(MathUtils.clamp(targetZoom.floatValue(), MIN_ZOOM, MAX_ZOOM));
        return true;
    }

}
