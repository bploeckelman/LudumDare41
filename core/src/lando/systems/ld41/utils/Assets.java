package lando.systems.ld41.utils;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
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

    // NOTE: using a vertex shader with a different name than the fragment shader requires an extra parameter, like so...
//    final AssetDescriptor<ShaderProgram> shaderAsset = new AssetDescriptor<ShaderProgram>("shaders/frag.frag", ShaderProgram.class,
//            new ShaderProgramLoader.ShaderProgramParameter() {{
//                vertexFile = "shaders/common.vert";
//            }}
//    );

    public enum Loading { SYNC, ASYNC }

    public SpriteBatch batch;
    public ShapeRenderer shapes;
    public GlyphLayout layout;

    public AssetManager mgr;

    public TextureAtlas atlas;
    public TextureRegion whitePixel;
    public TextureRegion whiteCircle;
    public TextureRegion testTexture;

    public NinePatch defaultNinePatch;
    public NinePatch transparentNinePatch;

    public BitmapFont font;
    public ShaderProgram fontShader;

    public HashMap<String, Animation<TextureRegion>> tankAnimations = new HashMap<String, Animation<TextureRegion>>();
    public HashMap<String, TextureRegion> tanks = new HashMap<String, TextureRegion>();

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
        // ...

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
        defaultNinePatch = new NinePatch(atlas.findRegion("ninepatch"), 6, 6, 6, 6);
        transparentNinePatch = new NinePatch(atlas.findRegion("transparent-ninepatch"), 10, 10, 10, 10);

        loadTankAssets();

        // Initialize distance field font
        font = mgr.get(distanceFieldFontAsset);
        font.getData().setScale(.3f);
        font.setUseIntegerPositions(false);
        fontShader = mgr.get(distanceFieldShaderAsset);

        return 1f;
    }

    private void loadTankAssets() {
        addTreads("lefttread");
        addTreads("righttread");

        addTank("browntank");
    }

    private void addTreads(String treadImage) {
        Array treads = atlas.findRegions(treadImage);
        tankAnimations.put(treadImage,
                new Animation<TextureRegion>(0.15f, treads, Animation.PlayMode.LOOP));
    }

    private void addTank(String tankName) {
        tanks.put(tankName, atlas.findRegion(tankName + "body"));
        String turret = tankName + "turret";
        tanks.put(turret, atlas.findRegion(turret));
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
