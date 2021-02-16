package com.spleefleague.core.music;

import com.spleefleague.core.Core;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.player.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class NoteBlockMusic {

    private static final Map<String, NoteBlockSong> songMap = new HashMap<>();
    private static final Map<CorePlayer, NoteBlockPlayer> playerSongMap = new HashMap<>();

    private static final Map<CorePlayer, List<NoteBlockPlayer>> asyncSongs = new HashMap<>();

    private static final String MUSIC_DIR = "../shared/music/";

    private static BukkitTask songTask;
    private static BukkitTask asyncTask;

    public static void init() {
        File file = new File(MUSIC_DIR);
        if (file.exists()) {
            String[] fileNames = file.list();
            if (fileNames != null) {
                for (String s : fileNames) {
                    getSong(s);
                }
            }
        }
        songTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Core.getInstance(), () -> playerSongMap.entrySet().removeIf(entry -> entry.getValue().tick()), 20L, 1L);
        asyncTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Core.getInstance(), () -> {
            for (Map.Entry<CorePlayer, List<NoteBlockPlayer>> corePlayerListEntry : asyncSongs.entrySet()) {
                List<NoteBlockPlayer> songs = corePlayerListEntry.getValue();
                songs.removeIf(NoteBlockPlayer::tick);
            }
        }, 20L, 1L);
    }

    public static void close() {
        songTask.cancel();
        asyncTask.cancel();
    }

    public static void onPlayerJoin(CorePlayer cp) {
        asyncSongs.put(cp, new ArrayList<>());
    }

    public static void onPlayerQuit(CorePlayer cp) {
        synchronized (playerSongMap) {
            playerSongMap.remove(cp);
        }
        synchronized (asyncSongs) {
            asyncSongs.remove(cp);
        }
    }

    public static NoteBlockPlayer getPlayer(CorePlayer listener) {
        return playerSongMap.get(listener);
    }

    public static void playSongAsync(CorePlayer listener, NoteBlockSong song, float volume) {
        if (song == null) return;
        asyncSongs.get(listener).add(new NoteBlockPlayer(listener, song, volume));
    }

    public static boolean playSong(CorePlayer listener) {
        if (playerSongMap.containsKey(listener) && !playerSongMap.get(listener).isPlaying()) {
            playerSongMap.get(listener).play();
            return true;
        }
        return false;
    }

    public static boolean playSong(CorePlayer listener, NoteBlockSong song, float volume) {
        if (song != null) {
            playerSongMap.put(listener, new NoteBlockPlayer(listener, song, volume));
            return true;
        }
        return false;
    }

    public static boolean pauseSong(CorePlayer listener) {
        if (playerSongMap.containsKey(listener)) {
            playerSongMap.get(listener).pause();
            return true;
        }
        return false;
    }

    public static boolean stopSong(CorePlayer listener) {
        return (playerSongMap.remove(listener) != null);
    }

    public static Set<String> getSongs() {
        return songMap.keySet();
    }

    public static NoteBlockSong getSong(String songName) {
        NoteBlockSong song = songMap.get(songName);
        if (song != null) return song;
        File file = new File(MUSIC_DIR + songName + (songName.endsWith(".nbs") ? "" : ".nbs"));
        if (!file.exists()) {
            return null;
        }
        try {
            Map<Short, NoteBlockSong.Layer> layerMap = new HashMap<>();
            FileInputStream fileInputStream = new FileInputStream(file);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);

            // 1: Header
            readShort(dataInputStream); // NULL
            dataInputStream.readByte(); // Version
            byte instrumentCount = dataInputStream.readByte();
            short length = readShort(dataInputStream);
            short layers = readShort(dataInputStream);
            String name = readString(dataInputStream);
            String author = readString(dataInputStream);
            readString(dataInputStream); // Original Author
            String description = readString(dataInputStream);
            float tempo = readShort(dataInputStream) / 100f;
            dataInputStream.readBoolean(); // Auto-save
            dataInputStream.readByte(); // Auto-save duration
            dataInputStream.readByte(); // Time signature
            readInt(dataInputStream); // Minutes spent
            readInt(dataInputStream); // Left clicks
            readInt(dataInputStream); // Right clicks
            readInt(dataInputStream); // Blocks added
            readInt(dataInputStream); // Blocks removed
            readString(dataInputStream); // Midi/schematic name
            boolean looping = dataInputStream.readByte() != 0; // Looping
            byte loops = dataInputStream.readByte(); // Max Loop
            short loopStart = readShort(dataInputStream); // Loop start tick

            // 2: Note Blocks
            short tick = -1;
            while (true) {
                short jumpTicks = readShort(dataInputStream);
                if (jumpTicks == 0) {
                    break;
                }
                tick += jumpTicks;
                short layer = -1;
                while (true) {
                    short jumpLayers = readShort(dataInputStream);
                    if (jumpLayers == 0) {
                        break;
                    }
                    layer += jumpLayers;
                    addNoteBlock(layer, (short) (tick * (20.f / tempo)), dataInputStream.readByte(), dataInputStream.readByte(), dataInputStream.readByte(), dataInputStream.readByte(), dataInputStream.readShort(), layerMap);
                }
            }

            // 3: Layers
            for (int i = 0; i < layers; i++) {
                NoteBlockSong.Layer layer = layerMap.get(i);
                if (layer != null) {
                    layer.setName(readString(dataInputStream));
                    layer.setLocked(dataInputStream.readByte() != 0);
                    layer.setVolume(dataInputStream.readByte());
                    layer.setStereo(dataInputStream.readByte());
                }
            }

            // 4: Custom Instruments
            /*
            byte customInstrumentCount = dataInputStream.readByte();

            for (int i = 0; i < customInstrumentCount; i++) {
                String instName = readString(dataInputStream);
                String soundName = readString(dataInputStream);
                byte soundPitch = dataInputStream.readByte();
                byte pressKey = dataInputStream.readByte();
            }
            */

            song = new NoteBlockSong(tempo, layerMap, layers, length, name, author, description, looping, loops, loopStart, file);
            songMap.put(songName, song);
        } catch (IOException exception) {
            CoreLogger.logError(exception);
        }
        return song;
    }

    private static short readShort(DataInputStream dis) throws IOException {
        int byte1 = dis.readUnsignedByte();
        int byte2 = dis.readUnsignedByte();
        return (short) (byte1 + (byte2 << 8));
    }

    private static int readInt(DataInputStream dis) throws IOException {
        int byte1 = dis.readUnsignedByte();
        int byte2 = dis.readUnsignedByte();
        int byte3 = dis.readUnsignedByte();
        int byte4 = dis.readUnsignedByte();
        return (byte1 + (byte2 << 8) + (byte3 << 16) + (byte4 << 24));
    }

    private static String readString(DataInputStream dis) throws IOException {
        int length = readInt(dis);
        StringBuilder sb = new StringBuilder(length);
        for (; length > 0; --length) {
            char c = (char) dis.readByte();
            if (c == (char) 0x0D) {
                c = ' ';
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private static void addNoteBlock(short layer, short tick, byte inst, byte key, byte velocity, byte panning, short pitch, Map<Short, NoteBlockSong.Layer> layerMap) {
        if (!layerMap.containsKey(layer)) {
            layerMap.put(layer, new NoteBlockSong.Layer());
        }
        layerMap.get(layer).setNote(tick, new NoteBlockSong.Layer.Note(inst, key, velocity, panning, pitch));
    }

}
