package lando.systems.ld41.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.ui.ModalWindow;

public class HelpModalWindow extends ModalWindow {
    private Rectangle helpRect;
    private String[] introTutorial = {
            "Welcome to Putt Putt Boom! Explosive Tank Action 9-hole Mini Golf."
    };
    private String[] keyTutorial = {
            "Left Click - Control power of putt",
            "Mouse Scroll- zoom map",
            "WASD - Basic control of the tank",
            "RTFG - Advanced control of the tank using tread",
    };
    private String[] tutorial = {
            "- Each death, including reset, counts as 2 shots",
            "- Enemy turrets attack in a set direction",
            "- Catapults aim and attack you when in sight",
            "- Enemy Tanks follow and attack you",
            "- Beware of the boss!"
    };

    public HelpModalWindow(OrthographicCamera camera) {
        super(camera);
        this.helpRect = new Rectangle();

    }

    @Override
    protected void renderWindowContents(SpriteBatch batch) {
        if (!showText) return;
        final float MARGIN_MULTIPLIER = 4f;
        batch.setColor(Color.WHITE);
        batch.setShader(LudumDare41.game.assets.fontShader);
        {
            final float title_text_scale = 0.75f;
            final float target_width = modalRect.width;
            LudumDare41.game.assets.font.getData().setScale(title_text_scale);
            LudumDare41.game.assets.fontShader.setUniformf("u_scale", title_text_scale);
            LudumDare41.game.assets.layout.setText(LudumDare41.game.assets.font, "Tutorial",
                    Color.WHITE, target_width, Align.center, true);
            LudumDare41.game.assets.font.draw(batch, LudumDare41.game.assets.layout,
                    modalRect.x + margin_left,
                    modalRect.y + modalRect.height - margin_top);
            LudumDare41.game.assets.font.setColor(Color.WHITE);
            LudumDare41.game.assets.font.getData().setScale(1f);
            LudumDare41.game.assets.fontShader.setUniformf("u_scale", 1f);
        }

        for (int i=0;i<introTutorial.length;i++) {
            batch.setShader(LudumDare41.game.assets.fontShader);
            {
                final float title_text_scale = 0.35f;
                final float target_width = modalRect.width;
                LudumDare41.game.assets.font.getData().setScale(title_text_scale);
                LudumDare41.game.assets.fontShader.setUniformf("u_scale", title_text_scale);
                LudumDare41.game.assets.layout.setText(LudumDare41.game.assets.font, introTutorial[i],
                        Color.WHITE, target_width, Align.left, true);
                LudumDare41.game.assets.font.draw(batch, LudumDare41.game.assets.layout,
                        modalRect.x + margin_left,
                        modalRect.y + modalRect.height - margin_top * MARGIN_MULTIPLIER * (i + 2));
                LudumDare41.game.assets.font.setColor(Color.WHITE);
                LudumDare41.game.assets.font.getData().setScale(1f);
                LudumDare41.game.assets.fontShader.setUniformf("u_scale", 1f);
            }
        }

        for (int i=0;i<keyTutorial.length;i++) {
            batch.setShader(LudumDare41.game.assets.fontShader);
            {
                final float title_text_scale = 0.35f;
                final float target_width = modalRect.width;
                LudumDare41.game.assets.font.getData().setScale(title_text_scale);
                LudumDare41.game.assets.fontShader.setUniformf("u_scale", title_text_scale);
                LudumDare41.game.assets.layout.setText(LudumDare41.game.assets.font, keyTutorial[i],
                        Color.WHITE, target_width, Align.left, true);
                LudumDare41.game.assets.font.draw(batch, LudumDare41.game.assets.layout,
                        modalRect.x + margin_left * 6f,
                        modalRect.y + modalRect.height - margin_top * MARGIN_MULTIPLIER * (i+introTutorial.length+2));
                LudumDare41.game.assets.font.setColor(Color.WHITE);
                LudumDare41.game.assets.font.getData().setScale(1f);
                LudumDare41.game.assets.fontShader.setUniformf("u_scale", 1f);
            }
        }

        for (int i=0;i<tutorial.length;i++) {
            batch.setShader(LudumDare41.game.assets.fontShader);
            {
                final float title_text_scale = 0.35f;
                final float target_width = modalRect.width;
                LudumDare41.game.assets.font.getData().setScale(title_text_scale);
                LudumDare41.game.assets.fontShader.setUniformf("u_scale", title_text_scale);
                LudumDare41.game.assets.layout.setText(LudumDare41.game.assets.font, tutorial[i],
                        Color.WHITE, target_width, Align.left, true);
                LudumDare41.game.assets.font.draw(batch, LudumDare41.game.assets.layout,
                        modalRect.x + margin_left,
                        modalRect.y + modalRect.height - margin_top * MARGIN_MULTIPLIER * (i+introTutorial.length+keyTutorial.length+2));
                LudumDare41.game.assets.font.setColor(Color.WHITE);
                LudumDare41.game.assets.font.getData().setScale(1f);
                LudumDare41.game.assets.fontShader.setUniformf("u_scale", 1f);
            }
        }

        batch.draw(LudumDare41.game.assets.mouseLeft,
                modalRect.x + margin_left * 2f,
                modalRect.y + modalRect.height - margin_top * 2f - margin_top * MARGIN_MULTIPLIER * (introTutorial.length+2),
                LudumDare41.game.assets.mouseLeft.getRegionWidth() / 3f,
                LudumDare41.game.assets.mouseLeft.getRegionHeight() / 3f);

        batch.draw(LudumDare41.game.assets.mouseMiddle,
                modalRect.x + margin_left * 2f,
                modalRect.y + modalRect.height - margin_top * 2f - margin_top * MARGIN_MULTIPLIER * (introTutorial.length+3),
                LudumDare41.game.assets.mouseLeft.getRegionWidth() / 3f,
                LudumDare41.game.assets.mouseLeft.getRegionHeight() / 3f);

        batch.draw(LudumDare41.game.assets.keyWASD,
                modalRect.x + margin_left * 1f,
                modalRect.y + modalRect.height - margin_top * 2f - margin_top * MARGIN_MULTIPLIER * (introTutorial.length+4),
                LudumDare41.game.assets.keyWASD.getRegionWidth() / 9f,
                LudumDare41.game.assets.keyWASD.getRegionHeight() / 9f);

        batch.draw(LudumDare41.game.assets.keyRTFG,
                modalRect.x + margin_left * 1.5f,
                modalRect.y + modalRect.height - margin_top * 2f - margin_top * MARGIN_MULTIPLIER * (introTutorial.length+5),
                LudumDare41.game.assets.keyRTFG.getRegionWidth() / 9f,
                LudumDare41.game.assets.keyRTFG.getRegionHeight() / 9f);
    }

}
