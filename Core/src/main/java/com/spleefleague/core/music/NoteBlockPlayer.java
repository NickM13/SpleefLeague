package com.spleefleague.core.music;

import com.spleefleague.core.player.CorePlayer;
import org.bukkit.SoundCategory;

public class NoteBlockPlayer {

    private final CorePlayer listener;
    private final NoteBlockSong song;
    private float volume = 1;
    private short tickCursor = 0;
    private boolean playing = true;
    private byte loop = 0;

    public NoteBlockPlayer(CorePlayer listener, NoteBlockSong song, float volume) {
        this.listener = listener;
        this.song = song;
        this.volume = volume;
    }

    public NoteBlockSong getSong() {
        return song;
    }

    public short getTickCursor() {
        return tickCursor;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void play() {
        playing = true;
    }

    public void pause() {
        playing = false;
    }

    public void tick() {
        if (playing) {
            for (NoteBlockSong.Layer layer : song.getLayerMap().values()) {
                if (layer != null) {
                    NoteBlockSong.Layer.Note note = layer.getNote(tickCursor);
                    if (note != null) {
                        String instrument;
                        switch (note.instrument) {
                            case 0:  instrument = "minecraft:block.note_block.harp";           break;
                            case 1:  instrument = "minecraft:block.note_block.bass";           break;
                            case 2:  instrument = "minecraft:block.note_block.bassdrum";       break;
                            case 3:  instrument = "minecraft:block.note_block.snare";          break;
                            case 4:  instrument = "minecraft:block.note_block.hat";            break;
                            case 5:  instrument = "minecraft:block.note_block.guitar";         break;
                            case 6:  instrument = "minecraft:block.note_block.flute";          break;
                            case 7:  instrument = "minecraft:block.note_block.bell";           break;
                            case 8:  instrument = "minecraft:block.note_block.chime";          break;
                            case 9:  instrument = "minecraft:block.note_block.xylophone";      break;
                            case 10: instrument = "minecraft:block.note_block.iron_xylophone"; break;
                            case 11: instrument = "minecraft:block.note_block.cow_bell";       break;
                            case 12: instrument = "minecraft:block.note_block.didgeridoo";     break;
                            case 13: instrument = "minecraft:block.note_block.bit";            break;
                            case 14: instrument = "minecraft:block.note_block.banjo";          break;
                            case 15: default: instrument = "minecraft:block.note_block.pling"; break;
                        }
                        listener.getPlayer().playSound(listener.getPlayer().getLocation(),
                                instrument + (note.key < 33 ? "_-1" : note.key > 57 ? "_1" : ""),
                                SoundCategory.RECORDS,
                                (layer.getVolume() / 100.f) * (note.velocity / 100.f) * volume,
                                (float) Math.pow(2D, (((note.key - 9) % 24) - 12) / 12D));
                    }
                }
            }
            tickCursor++;
            if (tickCursor > song.getLengthSeconds() * 20 && song.isLooping()) {
                tickCursor = song.getLoopStartStretched();
            }
        }
    }

}
