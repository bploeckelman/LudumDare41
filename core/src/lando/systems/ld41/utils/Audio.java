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

    public static final float MUSIC_VOLUME = 0.4f;
    public static final boolean shutUpYourFace = false;
    public static final boolean shutUpYourTunes = false;

    public enum Sounds {
        explosion, explosion1, explosion2,
        good_job, lose_level, transition,
        bumper, sassy_boom,
        shot, shot1, shot2, shot3,
        pew,
        splash, splash1, splash2,
        pop, in_the_hole, noice, full_power
    }

    public enum Musics {
        music1, music2
    }

    public HashMap<Sounds, Sound> sounds = new HashMap<Sounds, Sound>();
    public HashMap<Musics, Music> musics = new HashMap<Musics, Music>();

    public Music currentMusic;
    public MutableFloat musicVolume;

    public Audio() {
        this(!shutUpYourTunes);
    }

    public Audio(boolean playMusic) {
        sounds.put(Sounds.lose_level, Gdx.audio.newSound(Gdx.files.internal("audio/awww.mp3")));
        sounds.put(Sounds.splash1, Gdx.audio.newSound(Gdx.files.internal("audio/splash-1.mp3")));
        sounds.put(Sounds.splash2, Gdx.audio.newSound(Gdx.files.internal("audio/splash-2.mp3")));
        sounds.put(Sounds.shot1, Gdx.audio.newSound(Gdx.files.internal("audio/shot-1.mp3")));
        sounds.put(Sounds.shot2, Gdx.audio.newSound(Gdx.files.internal("audio/shot-2.mp3")));
        sounds.put(Sounds.shot3, Gdx.audio.newSound(Gdx.files.internal("audio/shot-3.mp3")));
        sounds.put(Sounds.explosion1, Gdx.audio.newSound(Gdx.files.internal("audio/explosion-1.mp3")));
        sounds.put(Sounds.explosion2, Gdx.audio.newSound(Gdx.files.internal("audio/explosion-2.mp3")));
        sounds.put(Sounds.pop, Gdx.audio.newSound(Gdx.files.internal("audio/beer-bottle-pop.mp3")));
        sounds.put(Sounds.in_the_hole, Gdx.audio.newSound(Gdx.files.internal("audio/yay.mp3")));
        sounds.put(Sounds.transition, Gdx.audio.newSound(Gdx.files.internal("audio/transition.mp3")));
        sounds.put(Sounds.noice, Gdx.audio.newSound(Gdx.files.internal("audio/noice.mp3")));
        sounds.put(Sounds.bumper, Gdx.audio.newSound(Gdx.files.internal("audio/bumper.mp3")));
        sounds.put(Sounds.sassy_boom, Gdx.audio.newSound(Gdx.files.internal("audio/bewmmm.mp3")));
        sounds.put(Sounds.pew, Gdx.audio.newSound(Gdx.files.internal("audio/pew.mp3")));

        musics.put(Musics.music1, Gdx.audio.newMusic(Gdx.files.internal("audio/song1.mp3")));
        musics.put(Musics.music2, Gdx.audio.newMusic(Gdx.files.internal("audio/song2.mp3")));

        currentMusic = musics.get(Musics.music1);
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
        if (shutUpYourFace) return -1;
        if (soundOption == Sounds.splash) {
            soundOption = MathUtils.randomBoolean() ? Sounds.splash1 : Sounds.splash2;
        }
        if (soundOption == Sounds.full_power) {
            soundOption = MathUtils.randomBoolean() ? Sounds.sassy_boom : Sounds.noice;
        }
        if (soundOption == Sounds.shot) {
            int id = MathUtils.random(1, 3);
            switch (id) {
                default:
                case 1: soundOption = Sounds.shot1; break;
                case 2: soundOption = Sounds.shot2; break;
                case 3: soundOption = Sounds.shot3; break;
            }
        }
        if (soundOption == Sounds.explosion) {
            soundOption = MathUtils.randomBoolean() ? Sounds.explosion1 : Sounds.explosion2;
        }

        Sound s = sounds.get(soundOption);
        return (s != null) ? s.play(1f) : 0;
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
            currentMusic.setVolume(musicVolume.floatValue());
            currentMusic.play();
            currentMusic.setOnCompletionListener(nextSong);
        }
    };
}
