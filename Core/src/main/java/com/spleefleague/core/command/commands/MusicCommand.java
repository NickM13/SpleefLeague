package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.annotation.OptionArg;
import com.spleefleague.core.command.error.CoreError;
import com.spleefleague.core.music.NoteBlockMusic;
import com.spleefleague.core.music.NoteBlockPlayer;
import com.spleefleague.core.music.NoteBlockSong;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;

/**
 * @author NickM13
 * @since 5/12/2020
 */
public class MusicCommand extends CoreCommand {

    public MusicCommand() {
        super("music", CoreRank.DEVELOPER);
        setOptions("songNames", cp -> NoteBlockMusic.getSongs());
    }

    @CommandAnnotation
    public void musicPlay(CorePlayer sender,
                          @LiteralArg("play") String l) {
        if (NoteBlockMusic.playSong(sender)) {
            success(sender, "Resuming play: " + NoteBlockMusic.getPlayer(sender).getSong().getName());
        } else {
            error(sender, "There is no paused song!");
        }
    }

    @CommandAnnotation
    public void musicPlay(CorePlayer sender,
                          @LiteralArg("play") String l,
                          @OptionArg(listName = "songNames", force = false) String songName) {
        NoteBlockSong song = NoteBlockMusic.getSong(songName);
        if (NoteBlockMusic.playSong(sender, song, 1)) {
            success(sender, "Now playing: " + song.getName() + " (" + song.getLengthSeconds() + ")");
        } else {
            error(sender, "Song file not found!");
        }
    }

    @CommandAnnotation
    public void musicPause(CorePlayer sender,
                           @LiteralArg("pause") String l) {
        if (NoteBlockMusic.pauseSong(sender)) {
            success(sender, "Song paused");
        } else {
            error(sender, "Song file not found!");
        }
    }

    @CommandAnnotation
    public void musicStop(CorePlayer sender,
                          @LiteralArg("stop") String l) {
        if (NoteBlockMusic.stopSong(sender)) {
            success(sender, "Song stopped");
        } else {
            error(sender, "Song file not found!");
        }
    }

    @CommandAnnotation
    public void musicInfo(CorePlayer sender,
                          @LiteralArg("info") String l) {
        error(sender, CoreError.SETUP);
        NoteBlockPlayer player = NoteBlockMusic.getPlayer(sender);
        if (player != null) {

        }
    }

}
