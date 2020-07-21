package com.spleefleague.core.music;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class NoteBlockSong {

    static class Layer {

        static class Note {
            byte instrument, key, velocity, panning;
            short pitch;

            public Note(byte instrument, byte key, byte velocity, byte panning, short pitch) {
                this.instrument = instrument;
                this.key = key;
                this.velocity = velocity;
                this.panning = panning;
                this.pitch = pitch;
            }

        }

        String name = "";
        boolean locked = false;
        byte volume = 100;
        float stereo = 0;
        Map<Integer, Note> noteMap = new HashMap<>();

        public Layer() {

        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setLocked(boolean locked) {
            this.locked = locked;
        }

        public void setVolume(byte volume) {
            this.volume = volume;
        }

        public void setStereo(byte stereo) {
            this.stereo = (stereo - 100.f) / 50;
        }

        public byte getVolume() {
            return volume;
        }

        public void setNote(int tick, Note note) {
            noteMap.put(tick, note);
        }

        public Note getNote(int tick) {
            return noteMap.get(tick);
        }

    }

    private final float tempo;
    private final Map<Short, Layer> layerMap;
    private final short layers;
    private final short length;
    private final String name;
    private final String author;
    private final String description;
    private final File decodeFile;
    private final boolean looping;
    private final byte loops;
    private final short loopStart;

    NoteBlockSong(float tempo, Map<Short, Layer> layerMap, short layers, short length, String name, String author, String description, boolean looping, byte loops, short loopStart, File decodeFile) {
        this.tempo = tempo;
        this.layerMap = layerMap;
        this.layers = layers;
        this.length = length;
        this.name = name;
        this.author = author;
        this.description = description;
        this.decodeFile = decodeFile;
        this.looping = looping;
        this.loops = loops;
        this.loopStart = loopStart;
    }

    public float getTempo() {
        return tempo;
    }

    public Map<Short, Layer> getLayerMap() {
        return layerMap;
    }

    public short getLayers() {
        return layers;
    }

    public short getLength() {
        return length;
    }

    public float getLengthSeconds() {
        return length / tempo;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public File getDecodeFile() {
        return decodeFile;
    }

    public boolean isLooping() {
        return looping;
    }

    public byte getLoops() {
        return loops;
    }

    public short getLoopStartStretched() {
        return (short) (loopStart / tempo * 20);
    }

}
