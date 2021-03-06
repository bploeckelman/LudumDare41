package lando.systems.ld41.utils;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.assets.loaders.ShaderProgramLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import lando.systems.ld41.LudumDare41;

import java.util.HashMap;

public class Assets implements Disposable {
    public class Balls {
        public static final String White = "ballwhite";
        public static final String Orange = "ballorange";
        public static final String Brown = "ballbrown";
        public static final String Blue = "ballblue";
        public static final String Pink = "ballpink";
        public static final String Purple = "ballpurple";
    }

    // Initialize descriptors for all assets
    private final AssetDescriptor<TextureAtlas> atlasAsset = new AssetDescriptor<TextureAtlas>("images/sprites.atlas", TextureAtlas.class);
    private final AssetDescriptor<Texture> titleTextureAsset = new AssetDescriptor<Texture>("images/title_bg.png", Texture.class);
    private final AssetDescriptor<Texture> titleBoomTextureAsset = new AssetDescriptor<Texture>("images/boom.png", Texture.class);
    private final AssetDescriptor<Texture> titlePutt1TextureAsset = new AssetDescriptor<Texture>("images/putt1.png", Texture.class);
    private final AssetDescriptor<Texture> titlePutt2TextureAsset = new AssetDescriptor<Texture>("images/putt2.png", Texture.class);
    private final AssetDescriptor<Texture> titlePutterTextureAsset = new AssetDescriptor<Texture>("images/putter.png", Texture.class);
    private final AssetDescriptor<Texture> waterTextureAsset = new AssetDescriptor<Texture>("images/water.png", Texture.class, new TextureLoader.TextureParameter() {{
        genMipMaps = true;
        minFilter = Texture.TextureFilter.MipMapLinearLinear;
        magFilter = Texture.TextureFilter.Linear;
    }});
    private final AssetDescriptor<Texture> sandTextureAsset = new AssetDescriptor<Texture>("images/sand.png", Texture.class, new TextureLoader.TextureParameter() {{
        genMipMaps = true;
        minFilter = Texture.TextureFilter.MipMapLinearLinear;
        magFilter = Texture.TextureFilter.Linear;
    }});
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

    private final AssetDescriptor<ShaderProgram> shaderWaterAsset = new AssetDescriptor<ShaderProgram>("shaders/water.frag", ShaderProgram.class, defaultVertParam);

    public enum Loading { SYNC, ASYNC }

    public SpriteBatch batch;
    public ShapeRenderer shapes;
    public PolygonSpriteBatch polys;
    public GlyphLayout layout;

    public AssetManager mgr;

    public TextureAtlas atlas;
    public TextureRegion whitePixel;
    public TextureRegion whiteCircle;
    public TextureRegion testTexture;
    public TextureRegion hole;
    public TextureRegion smoke;
    public TextureRegion thumbnailBg;
    public TextureRegion thumbnailBoundries;
    public TextureRegion pinballBumperOff;
    public TextureRegion pinballBumperOn;
    public TextureRegion arrow;
    public TextureRegion indicator;
    public TextureRegion enemyTurret;
    public TextureRegion enemyTurretRecoil;
    public TextureRegion flag;
    public TextureRegion refreshButton;
    public TextureRegion helpButton;
    public TextureRegion ballSign;
    public TextureRegion mouseMiddle;
    public TextureRegion mouseLeft;
    public TextureRegion keyWASD;
    public TextureRegion keyRTFG;
    public TextureRegion clownHead;


    public Texture titleTexture;
    public Texture titleBoom;
    public Texture titlePutt1;
    public Texture titlePutt2;
    public Texture titlePutter;

    // pickups
    public TextureRegion puCamo;
    public TextureRegion puShield;
    public TextureRegion puInvincible;
    public TextureRegion puPontoon;

    public Texture waterTexture;
    public Texture sandTexture;
    public TextureRegion waterTextureRegion;
    public TextureRegion sandTextureRegion;

    public NinePatch defaultNinePatch;
    public NinePatch transparentNinePatch;
    public NinePatch backplateNinePatch;
    public NinePatch boxNinePatch;

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

    public ShaderProgram waterShader;

    public HashMap<String, Animation<TextureRegion>> tankAnimations = new HashMap<String, Animation<TextureRegion>>();
    public HashMap<String, TextureRegion> tanks = new HashMap<String, TextureRegion>();
    public HashMap<String, TextureRegion> assetMap = new HashMap<String, TextureRegion>();
    public Animation<TextureRegion> catapultAnimation;
    public Animation<TextureRegion> smokeAnimation;
    public Animation<TextureRegion> explosionAnimation;

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
        polys = new PolygonSpriteBatch();
        layout = new GlyphLayout();

        initialized = false;

        mgr = new AssetManager();
        mgr.load(atlasAsset);
        mgr.load(titleTextureAsset);
        mgr.load(titleBoomTextureAsset);
        mgr.load(titlePutt1TextureAsset);
        mgr.load(titlePutt2TextureAsset);
        mgr.load(titlePutterTextureAsset);
        mgr.load(waterTextureAsset);
        mgr.load(sandTextureAsset);
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
        mgr.load(shaderWaterAsset);
        // ...

        levelNumberToFileNameMap = new IntMap<String>();
        levelNumberToFileNameMap.put(0, "maps/troysholeshole1.tmx");
        levelNumberToFileNameMap.put(1, "maps/troysholeshole2.tmx");
        levelNumberToFileNameMap.put(2, "maps/troysholeshole3.tmx");
        levelNumberToFileNameMap.put(3, "maps/troysholeshole4.tmx");
        levelNumberToFileNameMap.put(4, "maps/troysholeshole5.tmx");
        levelNumberToFileNameMap.put(5, "maps/troysholeshole6.tmx");
        levelNumberToFileNameMap.put(6, "maps/troysholeshole10.tmx");
        levelNumberToFileNameMap.put(7, "maps/troysholeshole11.tmx");
        levelNumberToFileNameMap.put(8, "maps/troysholeshole12.tmx");
//        levelNumberToFileNameMap.put(11, "maps/troysholeshole13.tmx");
//        levelNumberToFileNameMap.put(14, "maps/troysholeshole16.tmx");
        levelNumberToFileNameMap.put(9, "maps/clown_school.tmx");

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
        hole = atlas.findRegion("hole");
        smoke = atlas.findRegion("barrelsmoke");
        arrow = atlas.findRegion("arrow");
        indicator = atlas.findRegion("indicator");
        flag = atlas.findRegion("flag");
        refreshButton = atlas.findRegion("refresh");
        helpButton = atlas.findRegion("helpbutton");
        ballSign = atlas.findRegion("ballsign");
        enemyTurret = atlas.findRegion("greentankturret");
        enemyTurretRecoil = atlas.findRegion("greentankturretrecoil");
        pinballBumperOff = atlas.findRegion("pinballbumper-off");
        pinballBumperOn = atlas.findRegion("pinballbumper-on");
        mouseLeft = atlas.findRegion("mouse-left");
        mouseMiddle = atlas.findRegion("mouse-middle");
        keyWASD = atlas.findRegion("keyboard-wasd");
        keyRTFG = atlas.findRegion("keyboard-rtfg");
        clownHead = atlas.findRegion("clown_head");

        puCamo = atlas.findRegion("pu-camo");
        puInvincible = atlas.findRegion("pu-invincible");
        puPontoon = atlas.findRegion("pu-pontoon");
        puShield = atlas.findRegion("pu-shield");

        titleTexture = mgr.get(titleTextureAsset);
        titleBoom = mgr.get(titleBoomTextureAsset);
        titlePutter = mgr.get(titlePutterTextureAsset);
        titlePutt1 = mgr.get(titlePutt1TextureAsset);
        titlePutt2 = mgr.get(titlePutt2TextureAsset);

        waterTexture = mgr.get(waterTextureAsset);
        sandTexture = mgr.get(sandTextureAsset);
        waterTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        sandTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        waterTextureRegion = new TextureRegion(waterTexture);
        sandTextureRegion = new TextureRegion(sandTexture);

        defaultNinePatch = new NinePatch(atlas.findRegion("ninepatch"), 6, 6, 6, 6);
        transparentNinePatch = new NinePatch(atlas.findRegion("transparent-ninepatch"), 10, 10, 10, 10);
        backplateNinePatch = new NinePatch(atlas.findRegion("backplate"), 10, 10, 10, 10);
        boxNinePatch = new NinePatch(atlas.findRegion("box-outline"), 4, 4, 4, 4);

        Pixmap pixGreen = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixGreen.setColor(57f / 255f, 123f / 255f, 68f / 255f, 1f);
        pixGreen.fill();
        thumbnailBg = new TextureRegion(new Texture(pixGreen));
        Pixmap pixRed = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixRed.setColor(169f / 255f, 59f / 255f, 59f / 255f, 1f);
//        pixRed.setColor(230f / 255f, 72f / 255f, 46f / 255f, 1f);
        pixRed.fill();
        thumbnailBoundries = new TextureRegion(new Texture(pixRed));

        Array catapult = atlas.findRegions("catapulttower");
        Array smoke = atlas.findRegions("smoke");
        Array explosion = atlas.findRegions("explosion");
        catapultAnimation = new Animation<TextureRegion>(1, catapult, Animation.PlayMode.LOOP);
        smokeAnimation = new Animation<TextureRegion>(0.3f, smoke, Animation.PlayMode.LOOP);
        explosionAnimation = new Animation<TextureRegion>(0.15f, explosion, Animation.PlayMode.NORMAL);

        loadBalls();
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

        waterShader      = mgr.get(shaderWaterAsset);

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

    private void loadBalls() {
        assetMap.put(Balls.White, atlas.findRegion(Balls.White));
        assetMap.put(Balls.Orange, atlas.findRegion(Balls.Orange));
        assetMap.put(Balls.Blue, atlas.findRegion(Balls.Blue));
        assetMap.put(Balls.Purple, atlas.findRegion(Balls.Purple));
        assetMap.put(Balls.Brown, atlas.findRegion(Balls.Brown));
        assetMap.put(Balls.Pink, atlas.findRegion(Balls.Pink));
    }

    private void loadTankAssets() {
        addTreads("brown", 0.15f);
        addTreads("green", 0.15f);
        addTreads("orange", 0.15f);
        addTreads("pink", 0.15f);
        addTreads("blue", 0.15f);
        addTreads("greenpontoon", 0.15f);
        addTreads("brownpontoon", 0.15f);
        addTreads("orangepontoon", 0.15f);
        addTreads("pinkpontoon", 0.15f);
        addTreads("bluepontoon", 0.15f);

        addTank("greentank");
        addTank("browntank");
        addTank("orangetank");
        addTank("pinktank");
        addTank("bluetank");

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
        assetMap.clear();
        tanks.clear();
        tankAnimations.clear();
        mgr.clear();
        font.dispose();
        polys.dispose();
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

    public static TextureRegion getImage(String imageKey) {
        return LudumDare41.game.assets.assetMap.get(imageKey);
    }

}
