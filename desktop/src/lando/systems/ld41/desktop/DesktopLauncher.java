package lando.systems.ld41.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.utils.Config;

public class DesktopLauncher {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = Config.gameWidth;
        config.height = Config.gameHeight;
        config.resizable = Config.resizable;
        config.samples = 4;
        config.title = "Putt-Putt Boom";
        new LwjglApplication(new LudumDare41(), config);
    }
}
