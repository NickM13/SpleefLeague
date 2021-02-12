package com.spleefleague.core.chat;

import com.google.common.collect.Lists;
import joptsimple.internal.Strings;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author NickM13
 * @since 4/17/2020
 */
public class ChatUtils {

    public static String centerText(String message, int centerPos) {
        StringBuilder centered = new StringBuilder();

        int msgPxSize = getPixelCount(message);

        int whitePxSize = (centerPos * 2 - msgPxSize);
        int spaceCount = whitePxSize / 2 / (DefaultFontInfo.SPACE.getLength() + 1);

        centered.append(Strings.repeat(' ', spaceCount));
        centered.append(message);

        return centered.toString();
    }

    public static String centerTitle(String message) {
        return centerText(message, DefaultFontInfo.SPACE.getLength() * 27);
    }

    /**
     * Add spaces to string until desired pixel count is reached
     *
     * @param strBuilder String Builder
     * @param toPixel    Pixel to Reach
     */
    public static void appendSpacesTo(StringBuilder strBuilder, int toPixel) {
        int pixelCount = getPixelCount(strBuilder.toString());
        int spaceCount = (toPixel - pixelCount) / (DefaultFontInfo.SPACE.getLength() + 1);
        strBuilder.append(Strings.repeat(' ', spaceCount));
    }

    /**
     * Returns total pixel count of a message horizontally
     *
     * @param message Message
     * @return Pixel Count
     */
    public static int getPixelCount(String message) {
        int pixelCount = 0;
        boolean prevCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                prevCode = true;
            } else if (prevCode) {
                prevCode = false;
                isBold = (c == 'l' || c == 'L');
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                int change = (isBold ? dFI.getBoldLength() : dFI.getLength()) + 1;
                pixelCount += change;
            }
        }
        return pixelCount;
    }

    private static final int DESC_WIDTH = 190;

    private static ArrayList<String> wrapDesc(String message) {
        ArrayList<String> msgs = new ArrayList<>();
        StringBuilder line = new StringBuilder(Chat.DEFAULT);
        StringBuilder word = new StringBuilder();

        int msgPxSize = 0;
        boolean prevCode = false;
        boolean isBold = false;
        List<String> prevColors = new ArrayList<>();
        prevColors.add(Chat.DEFAULT);

        for (char c : message.toCharArray()) {
            if (c == '§') {
                prevCode = true;
            } else if (prevCode) {
                prevCode = false;
                isBold = (c == 'l' || c == 'L');
                word.append("§").append(c);
                prevColors.add(ChatColor.getByChar(c) + "");
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                int change = isBold ? dFI.getBoldLength() : dFI.getLength() + 1;
                if (msgPxSize + change > DESC_WIDTH) {
                    msgPxSize = 0;
                    if (line.length() < 2) {
                        line.append(word);
                        word = new StringBuilder();
                    }
                    msgs.add(line.toString());
                    line = new StringBuilder();
                    for (String s : prevColors) {
                        line.append(s);
                    }
                }
                msgPxSize += change;
                if (c == ' ') {
                    line.append(word).append(" ");
                    word = new StringBuilder();
                } else {
                    word.append(c);
                }
            }
        }
        if (word.length() > 0) {
            line.append(word);
        }
        if (line.length() > 0) {
            msgs.add(line.toString());
        }
        if (msgs.isEmpty()) {
            msgs.add("");
        }
        return msgs;
    }

    public static List<String> wrapDescription(String message) {
        if (message == null || message.equals("")) return Lists.newArrayList("");

        List<String> messageSplit = Lists.newArrayList(message.split("\n"));

        List<String> msgs = new ArrayList<>();

        for (String m : messageSplit) {
            msgs.addAll(wrapDesc(m));
        }

        return msgs;
    }

    public static TextComponent centerChat(String msg) {
        return new TextComponent(centerText(msg, 160));
    }

}
