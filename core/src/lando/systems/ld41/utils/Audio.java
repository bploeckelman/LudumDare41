package lando.systems.ld41.utils;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Sine;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import lando.systems.ld41.LudumDare41;

import java.util.HashMap;

public class Audio implements Disposable {

    public static final float MUSIC_VOLUME = 0.15f;

    public enum Sounds {
        explosion, good_job, lose_level, transition, shot, enemy_shot
    }

    public enum Musics {
        music1, music2
    }

    public HashMap<Sounds, Sound> sounds = new HashMap<Sounds, Sound>();
    public HashMap<Musics, Music> musics = new HashMap<Musics, Music>();

    public Music currentMusic;
    public MutableFloat musicVolume;

    public Audio() {
        this(true);
    }

    public Audio(boolean playMusic) {
//        sounds.put(Sounds.sound1, Gdx.audio.newSound(Gdx.files.internal("sounds/sound1.wav")));

        sounds.put(Sounds.explosion, Gdx.audio.newSound(Gdx.files.internal("audio/explosion1.wav")));
        sounds.put(Sounds.good_job, Gdx.audio.newSound(Gdx.files.internal("audio/good_job.wav")));
        sounds.put(Sounds.lose_level, Gdx.audio.newSound(Gdx.files.internal("audio/lose_level.wav")));
        sounds.put(Sounds.shot, Gdx.audio.newSound(Gdx.files.internal("audio/shot.wav")));
        sounds.put(Sounds.enemy_shot, Gdx.audio.newSound(Gdx.files.internal("audio/shot2.wav")));
        sounds.put(Sounds.transition, Gdx.audio.newSound(Gdx.files.internal("audio/transition.wav")));

        musics.put(Musics.music1, Gdx.audio.newMusic(Gdx.files.internal("audio/song1.wav")));
        musics.put(Musics.music2, Gdx.audio.newMusic(Gdx.files.internal("audio/song2.wav")));

        currentMusic = MathUtils.randomBoolean() ? musics.get(Musics.music1) : musics.get(Musics.music2);
        currentMusic.setLooping(false);
        currentMusic.setVolume(MUSIC_VOLUME);
        musicVolume = new MutableFloat(MUSIC_VOLUME);
        if (playMusic) {
            currentMusic.play();
            setMusicVolume(MUSIC_VOLUME, 2f);
        }
        currentMusic.setOnCompletionListener(nextSong);
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


    public Music.OnCompletionListener nextSong = new Music.OnCompletionListener() {
        @Override
        public void onCompletion(Music music) {
            if (currentMusic == musics.get(Musics.music1)){
                currentMusic = musics.get(Musics.music2);
            } else {
                currentMusic = musics.get(Musics.music1);
            }
            currentMusic.setVolume(MUSIC_VOLUME);
            currentMusic.play();
            currentMusic.setOnCompletionListener(nextSong);
        }
    };
}
