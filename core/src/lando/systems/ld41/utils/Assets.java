package lando.systems.ld41.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.assets.loaders.ShaderProgramLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import lando.systems.ld41.LudumDare41;

import java.util.HashMap;

public class Assets implements Disposable {

    // Initialize descriptors for all assets
    private final AssetDescriptor<TextureAtlas> atlasAsset = new AssetDescriptor<TextureAtlas>("images/sprites.atlas", TextureAtlas.class);
    private final AssetDescriptor<BitmapFont> distanceFieldFontAsset = new AssetDescriptor<BitmapFont>("fonts/ubuntu.fnt", BitmapFont.class,
            new BitmapFontLoader.BitmapFontParameter() {{
                genMipMaps = true;
                minFilter = Texture.TextureFilter.MipMapLinearLinear;
                magFilter = Texture.TextureFilter.Linear;
            }}
    );
    private final AssetDescriptor<ShaderProgram> distanceFieldShaderAsset = new AssetDescriptor<ShaderProgram>("shaders/dist.frag", ShaderProgram.class);

    private final ShaderProgramLoader.ShaderProgramParameter defaultVertParam = new ShaderProgramLoader.ShaderProgramParameter() {{ vertexFile = "shaders/default.vert"; }};
    private final AssetDescriptor<ShaderProgram> shaderBlindsAsset     = new AssetDescriptor<ShaderProgram>("shaders/blinds.frag",     ShaderProgram.class, defaultVertParam);
    private final AssetDescriptor<ShaderProgram> shaderFadeAsset       = new AssetDescriptor<ShaderProgram>("shaders/dissolve.frag",   ShaderProgram.class, defaultVertParam);
    private final AssetDescriptor<ShaderProgram> shaderRadialAsset     = new AssetDescriptor<ShaderProgram>("shaders/radial.frag",     ShaderProgram.class, defaultVertParam);
    private final AssetDescriptor<ShaderProgram> shaderDoomAsset       = new AssetDescriptor<ShaderProgram>("shaders/doomdrip.frag",   ShaderProgram.class, defaultVertParam);
    private final AssetDescriptor<ShaderProgram> shaderPixelizeAsset   = new AssetDescriptor<ShaderProgram>("shaders/pixelize.frag",   ShaderProgram.class, defaultVertParam);
    private final AssetDescriptor<ShaderProgram> shaderDoorwayAsset    = new AssetDescriptor<ShaderProgram>("shaders/doorway.frag",    ShaderProgram.class, defaultVertParam);
    private final AssetDescriptor<ShaderProgram> shaderCrosshatchAsset = new AssetDescriptor<ShaderProgram>("shaders/crosshatch.frag", ShaderProgram.class, defaultVertParam);
    private final AssetDescriptor<ShaderProgram> shaderRippleAsset     = new AssetDescriptor<ShaderProgram>("shaders/ripple.frag",     ShaderProgram.class, defaultVertParam);
    private final AssetDescriptor<ShaderProgram> shaderHeartAsset      = new AssetDescriptor<ShaderProgram>("shaders/heart.frag",      ShaderProgram.class, defaultVertParam);
    private final AssetDescriptor<ShaderProgram> shaderCircleCropAsset = new AssetDescriptor<ShaderProgram>("shaders/circlecrop.frag", ShaderProgram.class, defaultVertParam);

    public enum Loading { SYNC, ASYNC }

    public SpriteBatch batch;
    public ShapeRenderer shapes;
    public GlyphLayout layout;

    public AssetManager mgr;

    public TextureAtlas atlas;
    public TextureRegion whitePixel;
    public TextureRegion whiteCircle;
    public TextureRegion testTexture;
    public TextureRegion ballBrown;
    public TextureRegion ballOrange;
    public TextureRegion hole;
    public TextureRegion smoke;

    public TextureRegion thumbnailBg;
    public TextureRegion thumbnailBoundries;

    public NinePatch defaultNinePatch;
    public NinePatch transparentNinePatch;
    public NinePatch backplateNinePatch;

    public BitmapFont font;
    public ShaderProgram fontShader;

    public Array<ShaderProgram> randomTransitions;
    public ShaderProgram blindsShader;
    public ShaderProgram fadeShader;
    public ShaderProgram radialShader;
    public ShaderProgram doomShader;
    public ShaderProgram crosshatchShader;
    public ShaderProgram pizelizeShader;
    public ShaderProgram doorwayShader;
    public ShaderProgram rippleShader;
    public ShaderProgram heartShader;
    public ShaderProgram circleCropShader;

    public HashMap<String, Animation<TextureRegion>> tankAnimations = new HashMap<String, Animation<TextureRegion>>();
    public HashMap<String, TextureRegion> tanks = new HashMap<String, TextureRegion>();
    public Animation<TextureRegion> catapultAnimation;

    public IntMap<String> levelNumberToFileNameMap;

    public boolean initialized;

    public Assets() {
        this(Loading.SYNC);
    }

    public Assets(Loading loading) {
        // Let us write shitty shader programs
        ShaderProgram.pedantic = false;

        batch = new SpriteBatch();
        shapes = new ShapeRenderer();
        layout = new GlyphLayout();

        initialized = false;

        mgr = new AssetManager();
        mgr.load(atlasAsset);
        mgr.load(distanceFieldFontAsset);
        mgr.load(distanceFieldShaderAsset);
        mgr.load(shaderBlindsAsset);
        mgr.load(shaderFadeAsset);
        mgr.load(shaderRadialAsset);
        mgr.load(shaderDoomAsset);
        mgr.load(shaderPixelizeAsset);
        mgr.load(shaderDoorwayAsset);
        mgr.load(shaderCrosshatchAsset);
        mgr.load(shaderRippleAsset);
        mgr.load(shaderHeartAsset);
        mgr.load(shaderCircleCropAsset);
        // ...

        levelNumberToFileNameMap = new IntMap<String>();
        levelNumberToFileNameMap.put(0, "maps/test.tmx");
        levelNumberToFileNameMap.put(1, "maps/test2.tmx");
        // TODO: add other maps here

        if (loading == Loading.SYNC) {
            mgr.finishLoading();
            updateLoading();
        }
    }

    public float updateLoading() {
        if (!mgr.update()) return mgr.getProgress();
        if (initialized) return 1f;
        initialized = true;

        // Cache TextureRegions from TextureAtlas in fields for quicker access
        atlas = mgr.get(atlasAsset);
        whitePixel = atlas.findRegion("white-pixel");
        whiteCircle = atlas.findRegion("white-circle");
        testTexture = atlas.findRegion("badlogic");
        ballBrown = atlas.findRegion("ballbrown");
        ballOrange = atlas.findRegion("ballorange");
        hole = atlas.findRegion("hole");
        smoke = atlas.findRegion("barrelsmoke");

        Pixmap pixGreen = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixGreen.setColor(Color.GREEN);
        pixGreen.fill();
        thumbnailBg = new TextureRegion(new Texture(pixGreen));
        Pixmap pixRed = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixRed.setColor(Color.RED);
        pixRed.fill();
        thumbnailBoundries = new TextureRegion(new Texture(pixRed));

        defaultNinePatch = new NinePatch(atlas.findRegion("ninepatch"), 6, 6, 6, 6);
        transparentNinePatch = new NinePatch(atlas.findRegion("transparent-ninepatch"), 10, 10, 10, 10);
        backplateNinePatch = new NinePatch(atlas.findRegion("backplate"), 10, 10, 10, 10);
        Array catapult = atlas.findRegions("catapulttower");
        catapultAnimation = new Animation<TextureRegion>(1, catapult, Animation.PlayMode.LOOP);

        loadTankAssets();

        // Initialize distance field font
        font = mgr.get(distanceFieldFontAsset);
        font.getData().setScale(.3f);
        font.setUseIntegerPositions(false);
        fontShader = mgr.get(distanceFieldShaderAsset);

        blindsShader     = mgr.get(shaderBlindsAsset);
        fadeShader       = mgr.get(shaderFadeAsset);
        radialShader     = mgr.get(shaderRadialAsset);
        doomShader       = mgr.get(shaderDoomAsset);
        pizelizeShader   = mgr.get(shaderPixelizeAsset);
        doorwayShader    = mgr.get(shaderDoorwayAsset);
        crosshatchShader = mgr.get(shaderCrosshatchAsset);
        rippleShader     = mgr.get(shaderRippleAsset);
        heartShader      = mgr.get(shaderHeartAsset);
        circleCropShader = mgr.get(shaderCircleCropAsset);

        randomTransitions = new Array<ShaderProgram>();
        randomTransitions.addAll(
//                blindsShader,
//                fadeShader,
                radialShader,
//                doomShader,
                pizelizeShader,
//                doorwayShader,
//                crosshatchShader,
//                rippleShader,
//                heartShader,
                circleCropShader
        );

        return 1f;
    }

    private void loadTankAssets() {
        addTreads("brown", 0.15f);
        addTreads("green", 0.15f);
        addTreads("orange", 0.15f);
        addTreads("pink", 0.15f);
        addTreads("greenpontoon", 0.15f);
        addTreads("brownpontoon", 0.15f);
        addTreads("orangepontoon", 0.15f);
        addTreads("pinkpontoon", 0.15f);

        addTank("greentank");
        addTank("browntank");
        addTank("orangetank");
        addTank("pinktank");

        addAnimation("smoke", 0.15f, Animation.PlayMode.LOOP);
        addAnimation("forceshield", 0.1f, Animation.PlayMode.LOOP_PINGPONG);
    }

    private void addTreads(String treadImage, float duration) {
        addAnimation(treadImage + "lefttread", duration, Animation.PlayMode.LOOP);
        addAnimation(treadImage + "righttread", duration, Animation.PlayMode.LOOP);
    }

    private void addAnimation(String animImage, float duration, Animation.PlayMode mode) {
        Array anim = atlas.findRegions(animImage);
        tankAnimations.put(animImage,
                new Animation<TextureRegion>(duration, anim, mode));
    }

    private void addTank(String tankName) {
        tanks.put(tankName, atlas.findRegion(tankName + "body"));
        String turret = tankName + "turret";
        tanks.put(turret, atlas.findRegion(turret));
        String dead = tankName + "broken";
        tanks.put(dead, atlas.findRegion(dead));
        String deadTurret = tankName + "turretbroken";
        tanks.put(deadTurret, atlas.findRegion(deadTurret));
        String recoil = tankName + "turretrecoil";
        tanks.put(recoil, atlas.findRegion(recoil));
    }

    @Override
    public void dispose() {
        mgr.clear();
        font.dispose();
        shapes.dispose();
        batch.dispose();
    }

    // ------------------------------------------------------------------------
    // Static helpers methods
    // ------------------------------------------------------------------------

    private static ShaderProgram loadShader(String vertSourcePath, String fragSourcePath) {
        ShaderProgram.pedantic = false;
        ShaderProgram shaderProgram = new ShaderProgram(
                Gdx.files.internal(vertSourcePath),
                Gdx.files.internal(fragSourcePath));
        ShaderProgram.pedantic = true;

        if (!shaderProgram.isCompiled()) {
            Gdx.app.error("LoadShader", "compilation failed:\n" + shaderProgram.getLog());
            throw new GdxRuntimeException("LoadShader: compilation failed:\n" + shaderProgram.getLog());
        } else {
            Gdx.app.debug("LoadShader", "ShaderProgram compilation log:\n" + shaderProgram.getLog());
        }

        return shaderProgram;
    }

    public static void drawString(SpriteBatch batch, String text,
                                  float x, float y, Color c, float scale, BitmapFont font) {
        batch.setShader(LudumDare41.game.assets.fontShader);
        LudumDare41.game.assets.fontShader.setUniformf("u_scale", scale);
        font.getData().setScale(scale);
        font.setColor(c);
        font.draw(batch, text, x, y);
        font.getData().setScale(1f);
        LudumDare41.game.assets.fontShader.setUniformf("u_scale", 1f);
        font.getData().setScale(scale);
        batch.setShader(null);
    }

    public static void drawString(SpriteBatch batch, String text,
                                  float x, float y, Color c, float scale,
                                  BitmapFont font, float targetWidth, int halign) {
        batch.setShader(LudumDare41.game.assets.fontShader);
        LudumDare41.game.assets.fontShader.setUniformf("u_scale", scale);
        font.getData().setScale(scale);
        font.setColor(c);
        font.draw(batch, text, x, y, targetWidth, halign, true);
        font.getData().setScale(1f);
        LudumDare41.game.assets.fontShader.setUniformf("u_scale", 1f);
        font.getData().setScale(scale);
        batch.setShader(null);
    }

}
