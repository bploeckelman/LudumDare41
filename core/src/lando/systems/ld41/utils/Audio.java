package lando.systems.ld41.utils;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Sine;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import lando.systems.ld41.LudumDare41;

import java.util.HashMap;

public class Audio implements Disposable {

    public static final float MUSIC_VOLUME = 0.25f;

    public enum Sounds {
        sound1, sound2
    }

    public enum Musics {
        music1, music2
    }

    public HashMap<Sounds, Sound> sounds = new HashMap<Sounds, Sound>();
    public HashMap<Musics, Music> musics = new HashMap<Musics, Music>();

    public Music currentMusic;
    public MutableFloat musicVolume;

    public Audio() {
        this(false);
    }

    public Audio(boolean playMusic) {
//        sounds.put(Sounds.sound1, Gdx.audio.newSound(Gdx.files.internal("sounds/sound1.wav")));
//        sounds.put(Sounds.sound2, Gdx.audio.newSound(Gdx.files.internal("sounds/sound2.wav")));
//
//        musics.put(Musics.music1, Gdx.audio.newMusic(Gdx.files.internal("sounds/music1.mp3")));
//        musics.put(Musics.music2, Gdx.audio.newMusic(Gdx.files.internal("sounds/music2.mp3")));
//        currentMusic = musics.get(Musics.music1);
//        currentMusic.setLooping(true);

        musicVolume = new MutableFloat(0);
        if (playMusic) {
            currentMusic.play();
            setMusicVolume(MUSIC_VOLUME, 2f);
        }
    }

    public void update(float dt){
        if (currentMusic != null) {
            currentMusic.setVolume(musicVolume.floatValue());
        }
    }

    @Override
    public void dispose() {
        Sounds[] allSounds = Sounds.values();
        for (Sounds sound : allSounds) {
            if (sounds.get(sound) != null) {
                sounds.get(sound).dispose();
            }
        }
        Musics[] allMusics = Musics.values();
        for (Musics music : allMusics) {
            if (musics.get(music) != null) {
                musics.get(music).dispose();
            }
        }
        currentMusic = null;
    }

    public long playSound(Sounds soundOption) {
        return sounds.get(soundOption).play(1f);
    }

    public void playMusic(Musics musicOption) {
        // Stop currently running music
        if (currentMusic != null) currentMusic.stop();

        // Set specified music track as current and play it
        currentMusic = musics.get(musicOption);
        currentMusic.setLooping(true);
        currentMusic.play();
    }

    public void stopSound(Sounds soundOption) {
        Sound sound = sounds.get(soundOption);
        if (sound != null) {
            sound.stop();
        }
    }

    public void stopAllSounds() {
        for (Sound sound : sounds.values()) {
            if (sound != null) {
                sound.stop();
            }
        }
    }

    public void setMusicVolume(float level, float duration) {
        LudumDare41.game.tween.killTarget(musicVolume);
        Tween.to(musicVolume, 1, duration)
                .target(level)
                .ease(Sine.IN)
                .start(LudumDare41.game.tween);
    }

}
