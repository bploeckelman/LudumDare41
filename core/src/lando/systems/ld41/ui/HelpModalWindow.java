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

    public HelpModalWindow(OrthographicCamera camera) {
        super(camera);
        this.helpRect = new Rectangle();

    }

    @Override
    protected void renderWindowContents(SpriteBatch batch) {
        if (!showText) return;
        batch.setColor(Color.WHITE);
        batch.setShader(LudumDare41.game.assets.fontShader);
        {
            final float title_text_scale = 0.5f;
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
    }
}
