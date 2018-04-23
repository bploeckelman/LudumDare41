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
    private String[] tutorial = {
            "Welcome to Putt Putt Boom!",
            "Explosive Tank Action Golf Game",
            "Left Click - Control power of putt",
            "WASD - Basic movement of tank",
            "Mouse Scroll- zoom map",
            "RTFG - Control movement by tank's tread",
            "- Each death including reset counts as 2 shots",
            "- Enemy turrets attack in a set direction",
            "- Catapults aim and attack you when in sight",
            "- Enemy Tank - Advanced AI"
    };

    public HelpModalWindow(OrthographicCamera camera) {
        super(camera);
        this.helpRect = new Rectangle();

    }

    @Override
    protected void renderWindowContents(SpriteBatch batch) {
        if (!showText) return;
        final float MARGIN_MULTIPLIER = 5f;
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
        for (int i=0;i<tutorial.length;i++) {
            batch.setShader(LudumDare41.game.assets.fontShader);
            {
                final float title_text_scale = 0.5f;
                final float target_width = modalRect.width;
                LudumDare41.game.assets.font.getData().setScale(title_text_scale);
                LudumDare41.game.assets.fontShader.setUniformf("u_scale", title_text_scale);
                LudumDare41.game.assets.layout.setText(LudumDare41.game.assets.font, tutorial[i],
                        Color.WHITE, target_width, Align.left, true);
                LudumDare41.game.assets.font.draw(batch, LudumDare41.game.assets.layout,
                        modalRect.x + margin_left,
                        modalRect.y + modalRect.height - margin_top * MARGIN_MULTIPLIER * (i+1));
                LudumDare41.game.assets.font.setColor(Color.WHITE);
                LudumDare41.game.assets.font.getData().setScale(1f);
                LudumDare41.game.assets.fontShader.setUniformf("u_scale", 1f);
            }
        }

    }
}
