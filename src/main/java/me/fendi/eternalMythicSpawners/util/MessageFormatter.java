package me.fendi.eternalMythicSpawners.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MessageFormatter {

    private static final Pattern HEX_PATTERN = Pattern.compile("<#([a-fA-F0-9]{6})>");

    private MessageFormatter() {
    }

    public static String renderMobTypeLine(String template, String mobTypeName) {
        return colorize(nullToEmpty(template).replace("%mt%", nullToEmpty(mobTypeName)));
    }

    public static String renderTimerLine(String template, int seconds) {
        int safeSeconds = Math.max(0, seconds);
        return colorize(nullToEmpty(template)
                .replace("%sec%", String.valueOf(safeSeconds))
                .replace("%formatted_sec%", formatSeconds(safeSeconds)));
    }

    public static String colorize(String message) {
        Matcher matcher = HEX_PATTERN.matcher(nullToEmpty(message));
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(toLegacyHex(matcher.group(1))));
        }
        matcher.appendTail(buffer);
        return translateLegacyCodes(buffer.toString());
    }

    public static String formatSeconds(int seconds) {
        int safeSeconds = Math.max(0, seconds);
        int hours = safeSeconds / 3600;
        int minutes = (safeSeconds % 3600) / 60;
        int remainingSeconds = safeSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }

    private static String toLegacyHex(String hex) {
        StringBuilder builder = new StringBuilder("§x");
        for (char character : hex.toCharArray()) {
            builder.append('§').append(Character.toLowerCase(character));
        }
        return builder.toString();
    }

    private static String translateLegacyCodes(String message) {
        char[] characters = message.toCharArray();
        for (int index = 0; index < characters.length - 1; index++) {
            if (characters[index] == '&' && isLegacyColorCode(characters[index + 1])) {
                characters[index] = '§';
                characters[index + 1] = Character.toLowerCase(characters[index + 1]);
            }
        }
        return new String(characters);
    }

    private static boolean isLegacyColorCode(char character) {
        return (character >= '0' && character <= '9')
                || (character >= 'a' && character <= 'f')
                || (character >= 'A' && character <= 'F')
                || (character >= 'k' && character <= 'o')
                || (character >= 'K' && character <= 'O')
                || character == 'r'
                || character == 'R';
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
